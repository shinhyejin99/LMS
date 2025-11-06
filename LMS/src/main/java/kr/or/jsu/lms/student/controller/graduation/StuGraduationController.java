package kr.or.jsu.lms.student.controller.graduation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.mybatis.mapper.StuGraduReqMapper;
import kr.or.jsu.vo.StuGraduReqVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25. 		김수현			파일 이름 수정
 *  2025. 10. 20.       Gemini            학생 정보 조회 및 모델 추가
 *  2025. 10. 28.       Gemini            서버 오류 로깅 강화
 * </pre>
 */
@Controller
@RequestMapping("/lms/student/graduation")
public class StuGraduationController {

    private static final Logger log = LoggerFactory.getLogger(StuGraduationController.class);

	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private LMSFilesService lmsFilesService;

	@Autowired
	private StuGraduReqMapper stuGraduReqMapper;

	// 학생 졸업요건 조회 및 졸업 과제 제출 페이지
	@GetMapping
	public String getList(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
		String studentNo = customUserDetails.getRealUser().getUserNo();
		StudentDetailDTO studentInfo = studentMapper.selectStudentDetailInfo(studentNo);
		model.addAttribute("studentInfo", studentInfo);

		// Check for existing graduation assignment submission
		Map<String, Object> params = new HashMap<>();
		params.put("studentNo", studentNo);
		params.put("professorNo", studentInfo.getProfessorId()); // 지도교수 번호 추가
		// graduReqCd 필터링 제거, 최신 제출을 가져오도록 매퍼 수정됨
		List<StuGraduReqVO> submittedAssignments = stuGraduReqMapper.selectGraduationAssignmentByStudentNo(params);
		
		boolean hasSubmitted = !submittedAssignments.isEmpty();
		model.addAttribute("hasSubmittedGraduationAssignment", hasSubmitted);

		if (hasSubmitted) {
		    StuGraduReqVO latestSubmission = submittedAssignments.get(0);
		    model.addAttribute("graduationAssignmentStatus", latestSubmission.getGraduReqCd());
		    model.addAttribute("graduationAssignmentEvaluation", latestSubmission.getEvaluation());
		} else {
		    model.addAttribute("graduationAssignmentStatus", "NOT_SUBMITTED");
		    model.addAttribute("graduationAssignmentEvaluation", null);
		}
		
		return "student/graduation/studentGraduation";
	}

	@PostMapping("/submitAssignment")
	@ResponseBody
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<Map<String, Object>> submitAssignment(
			@RequestParam("studentNo") String studentNo,
			@RequestParam("attachFiles") List<MultipartFile> attachFiles,
			@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		Map<String, Object> response = new HashMap<>();
		String uploaderUserId = customUserDetails.getRealUser().getUserId();

		try {
			// 1. Get professorNo (advisor professor of the student)
			StudentDetailDTO studentInfo = studentMapper.selectStudentDetailInfo(studentNo);
			String professorNo = studentInfo.getProfessorId(); // Assuming professorId is available in StudentDetailDTO

			if (professorNo == null) {
				response.put("success", false);
				response.put("message", "지도교수 정보가 없습니다. 관리자에게 문의하세요.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// 2. Save files to disk and get file metadata
			List<FileDetailInfo> fileMetaDatas = lmsFilesService.saveAtDirectory(attachFiles, FileUploadDirectory.CLASSROOM);

			String fileId = null;
			if (!fileMetaDatas.isEmpty()) {
				// 3. Save file metadata to DB and get fileId
				fileId = lmsFilesService.saveAtDB(fileMetaDatas, uploaderUserId, false); // false for not public
			}

			// 4. Check if a submission already exists
			Map<String, Object> params = new HashMap<>();
			params.put("studentNo", studentNo);
			params.put("professorNo", professorNo);
			params.put("graduReqCd", "GRAD_ASSIGNMENT");
			List<StuGraduReqVO> existingSubmissions = stuGraduReqMapper.selectGraduationAssignmentByStudentNo(params);

			int result;
			if (!existingSubmissions.isEmpty()) {
				// Update existing submission
				StuGraduReqVO existingSubmission = existingSubmissions.get(0);
				existingSubmission.setFileId(fileId);
				existingSubmission.setSubmitAt(java.time.LocalDateTime.now());
				existingSubmission.setGraduReqCd("GRAD_ASSIGNMENT"); // 재제출 시 상태를 'GRAD_ASSIGNMENT'로 변경
				existingSubmission.setEvaluateAt(null); // 재제출 시 평가일 초기화
				                        existingSubmission.setEvaluation(null); // 재제출 시 평가 내용 초기화
				                        log.info("Attempting to update graduation assignment with ID: {}", existingSubmission.getGraduReqSubmitId()); // 추가할 로그
				                        result = stuGraduReqMapper.updateStuGraduReq(existingSubmission);
				                        log.info("Updated existing graduation assignment. Result: {}", result);				        } else {
				            // Insert new submission
				            StuGraduReqVO stuGraduReq = new StuGraduReqVO();
				            String graduReqSubmitId = stuGraduReqMapper.getNextGraduReqSubmitId(); // Generate unique ID
				            stuGraduReq.setGraduReqSubmitId(graduReqSubmitId);
				            stuGraduReq.setStudentNo(studentNo);
				            stuGraduReq.setProfessorNo(professorNo);
				            stuGraduReq.setGraduReqCd("GRAD_ASSIGNMENT"); // Assuming a common code for graduation assignment
				            stuGraduReq.setFileId(fileId);
				            stuGraduReq.setSubmitAt(java.time.LocalDateTime.now());
				            stuGraduReq.setDeleteYn("N");
				            result = stuGraduReqMapper.insertStuGraduReq(stuGraduReq);
				            log.info("Inserted new graduation assignment. Result: {}", result);
				        }
				
				        if (result > 0) {
				            response.put("success", true);
				            response.put("message", "졸업 과제가 성공적으로 제출되었습니다.");
				            log.info("Returning success response for studentNo: {}", studentNo);
				            return ResponseEntity.ok().body(response);
				        } else {
				            response.put("success", false);
				            response.put("message", "졸업 과제 제출에 실패했습니다. 데이터베이스 저장 중 오류가 발생했습니다.");
				            log.error("Returning failure response due to DB save error for studentNo: {}", studentNo);
				            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				        }
		} catch (Exception e) {
			log.error("Error submitting graduation assignment: studentNo={}, uploaderUserId={}", studentNo, uploaderUserId, e);
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
}
