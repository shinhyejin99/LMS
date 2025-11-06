package kr.or.jsu.lms.professor.controller.lecture;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Added missing import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author 김수현
 * @since 2025. 10. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 28.     	김수현	        컨트롤러 수정
 *
 * </pre>
 */
@Controller
@RequestMapping("/lms/professor/lecture")
@RequiredArgsConstructor
public class ProfLectureViewController {

//	@Resource(name = "profLectureService")
//	private ProfLectureService profLectureService;

	// 교수 - 강의 목록 조회 및 상세 페이지

	/**
	 * 교수) 강의목록 조회
	 * @return
	 */
	@GetMapping("/list")
	public String list() {
		return "professor/lecture/profLectureList";
	}


	/**
	 * 교수) 강의목록 상세보기
	 * @param lectureId
	 * @param model
	 * @return
	 */
	@GetMapping("/detail/{lectureId}")
	public String detail(@PathVariable String lectureId, Model model) {
	    model.addAttribute("lectureId", lectureId);
	    return "professor/lecture/profLectureDetail";
	}

//
//	@GetMapping("/detail")
//	public String detail(@RequestParam("lectureId") String lectureId, Model model) {
//		Map<String, Object> lectureDetails = profLectureService.selectLectureDetails(lectureId);
//		model.addAttribute("lectureDetails", lectureDetails);
//		return "professor/lecture/profLectureDetail";
//	}
//
//	@GetMapping("/getLectureDetails")
//	@ResponseBody
//	public Map<String, Object> getLectureDetails(@RequestParam("lectureId") String lectureId) {
//		Map<String, Object> lectureDetails = profLectureService.selectLectureDetails(lectureId);
//		return lectureDetails;
//	}
//
//	/**
//	 * 교수 번호, 학년도, 학기를 기준으로 강의 목록을 조회하여 JSON 형태로 반환합니다.
//	 *
//	 * @param academicYear 학년도
//	 * @param semester     학기
//	 * @return 강의 목록 (List<Map<String, Object>>)
//	 */
//	@GetMapping("/getLectures")
//	@ResponseBody
//	public List<Map<String, Object>> getLectures(
//			@RequestParam(value = "academicYear", required = false) String academicYear) {
//
//		System.out.println("Received academicYear: " + academicYear);
//
//		String professorNo = null;
//// 로그인한 사용자 정보 가져오기 (표준 Spring Security 사용)
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if (authentication != null && authentication.isAuthenticated()
//				&& authentication.getPrincipal() instanceof CustomUserDetails) {
//			CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//			if (customUserDetails.getRealUser() instanceof ProfessorVO) {
//				ProfessorVO professorVO = (ProfessorVO) customUserDetails.getRealUser();
//				professorNo = professorVO.getProfessorNo();
//			} else {
//				// Handle cases where the logged-in user is not a ProfessorVO
//				throw new IllegalStateException("Logged-in user is not a professor.");
//			}
//		} else {
//			throw new IllegalStateException("Logged-in user details could not be retrieved.");
//		}
//
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("professorNo", professorNo);
//
//		System.out.println("paramMap: " + paramMap);
//
//		System.out.println("paramMap: " + paramMap);
//
//		return profLectureService.selectProfLectures(paramMap);
//	}

// 강의 상세 - 강의 계획서 조회 및 수정

// 강의 상세 - 출석률 조회 탭

// 강의 상세 - 평가 조회/입력 및 수정 탭

// 강의 상세 - 강의평가 탭

// 강의 상세 - 통계 탭

}