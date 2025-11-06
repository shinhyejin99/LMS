package kr.or.jsu.lms.student.controller.dashboard;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.SemesterGradeDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.vo.PortalRecruitVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학생 대시보드 컨트롤러
 * @author 김수현
 * @since 2025. 10. 24.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 24.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StuDashBoardViewController {

	private final StuInfoService service;
	private final DatabaseCache databaseCache;
	private final PortalJobService portalJobService;

	public static final String MODELNAME = "student";

	/**
	 * 로그인에서 정보 가져오기
	 *
	 * @return
	 */
	private String getLoggedInStudentNo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof CustomUserDetails) {
			return ((CustomUserDetails) principal).getRealUser().getUserNo();
		}

		return null;
	}


	/**
	 * 학생 인적 & 학적 정보 조회
	 * @param model
	 * @return
	 */
	@GetMapping
	public String getStuMyInfo(
		Model model
	) {
		String studentNo = getLoggedInStudentNo();
		if (studentNo == null) {
			model.addAttribute("error", "로그인이 필요합니다.");
			return "dashboard/studentdashboard";
		}
		StudentDetailDTO student = service.readStuMyInfo(studentNo);
		String colleagName = databaseCache.getCollegeName(student.getCollegeCd());
		log.info("===> 단과대명 : {}", colleagName);
		student.setCollegeName(colleagName);

		List<SemesterGradeDTO> grades = service.readSemesterGrades(studentNo);
		Double gpa = service.readStudentGPA(studentNo);


		// 2-3. 채용정보 최신 5건
		PaginationInfo<PortalRecruitVO> jobPaging = new PaginationInfo<>();
		jobPaging.setCurrentPage(1);
		jobPaging.setScreenSize(7);

		List<PortalRecruitVO> jobNotices = portalJobService.readSchRecruitList(jobPaging);
		model.addAttribute("jobNotices", jobNotices);

		model.addAttribute("student", student);
		model.addAttribute("grades", grades);
		model.addAttribute("totalGPA", String.format("%.2f", gpa));
		return "dashboard/studentdashboard";
	}

	/**
	 * 증명사진 불러오기
	 */
	@GetMapping("/photo")
	public ResponseEntity<Resource> getMyIdPhoto() {
	    String studentNo = getLoggedInStudentNo();

	    if (studentNo == null) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
	    }

	    try {
	        FileDetailInfo fileInfo = service.getStudentIdPhoto(studentNo);

	        Resource file = fileInfo.getRealFile();

	        String ext = fileInfo.getExtension() == null ? "" : fileInfo.getExtension().toLowerCase();

	        MediaType mediaType = switch (ext) {
	            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
	            case "png" -> MediaType.IMAGE_PNG;
	            case "gif" -> MediaType.IMAGE_GIF;
	            case "webp" -> MediaType.parseMediaType("image/webp");
	            case "bmp" -> MediaType.parseMediaType("image/bmp");
	            default -> MediaType.APPLICATION_OCTET_STREAM;
	        };

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(mediaType);

	        return new ResponseEntity<>(file, headers, HttpStatus.OK);

	    } catch (RuntimeException e) {
	        log.error("증명사진 조회 실패: {}", e.getMessage());
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 또는 500
	    }
	}
}
