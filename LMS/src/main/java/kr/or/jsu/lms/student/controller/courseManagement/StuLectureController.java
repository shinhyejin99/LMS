package kr.or.jsu.lms.student.controller.courseManagement;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam; // Import RequestParam
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.LectureDetailDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.dto.StudentLectureResponseDTO;
import kr.or.jsu.dto.PaginatedResponseDTO; // Import PaginatedResponseDTO
import kr.or.jsu.lms.student.service.courseManagement.StuLectureService;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *  2025. 9. 25.     	정태일            최초 생성
 *	2025. 9. 25.		김수현			파일 이름 변경
 *  2025. 10. 17.       최건우            코드 추가 
 *  2025. 10. 31.       최건우            페이지네이션을 위한 컨트롤러 수정
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/lecture")
@RequiredArgsConstructor
public class StuLectureController {

    private final StuLectureService stuLectureService;
    private final StuInfoService stuInfoService;

	// 수강내역 조회 페이지 - 컨트롤러
	
	@GetMapping
	public String getList(
        Model model,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "1") int page, // Add page parameter
        @RequestParam(defaultValue = "10") int size // Add size parameter
    ) {
        // 현재 로그인한 학생의 학번 가져오기
        String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();

        // 학생 상세 정보 조회
        StudentDetailDTO studentInfo = stuInfoService.readStuMyInfo(studentNo);
        model.addAttribute("studentInfo", studentInfo);
        model.addAttribute("studentName", studentInfo.getLastName() + studentInfo.getFirstName());

        // 학생의 수강 내역 조회 (페이지네이션 적용)
        PaginatedResponseDTO<StudentLectureResponseDTO> paginatedLectureList = stuLectureService.getStudentLectures(studentNo, page, size);
        model.addAttribute("paginatedLectureList", paginatedLectureList);
        model.addAttribute("lectureList", paginatedLectureList.getContent()); // For compatibility with existing view

        log.debug("학생 수강 내역 조회 (페이지네이션) - studentNo: {}, currentPage: {}, pageSize: {}, totalElements: {}",
            studentNo, paginatedLectureList.getCurrentPage(), paginatedLectureList.getPageSize(), paginatedLectureList.getTotalElements());

		return "student/courseManagement/studentLecture";
	}

	@GetMapping("/{lectureId}")
    @ResponseBody
    public LectureDetailDTO getLectureDetail(
        @PathVariable String lectureId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();
        return stuLectureService.getLectureDetail(lectureId, studentNo);
    }

    @PostMapping("/drop")
    @ResponseBody
    public ResponseEntity<?> dropLecture(
        @RequestBody Map<String, String> payload,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();
        String lectureId = payload.get("lectureId");

        if (lectureId == null || lectureId.isEmpty()) {
            return ResponseEntity.badRequest().body("강의 ID가 필요합니다.");
        }

        try {
            stuLectureService.dropLecture(studentNo, lectureId);
            return ResponseEntity.ok().body("수강 포기가 성공적으로 처리되었습니다.");
        } catch (IllegalStateException e) {
            log.error("수강 포기 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("수강 포기 중 서버 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수강 포기 중 오류가 발생했습니다.");
        }
    }
}