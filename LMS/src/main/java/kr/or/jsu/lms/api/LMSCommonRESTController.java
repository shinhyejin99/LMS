package kr.or.jsu.lms.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.dto.response.lms.lecture.apply.SubjectWithCollegeAndDeptResp;
import kr.or.jsu.lms.professor.service.lecture.ProfessorLectureApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 28.     	송태호	          최초 생성, 강의개설신청 로직 일부 분리하여 가져옴
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/api/v1/common")
@RequiredArgsConstructor
public class LMSCommonRESTController {
	
	private final ProfessorLectureApplyService lctApplyService;
	
	/**
	 * 학교 내의 모든 과목을 반환합니다. <br>
	 * "단과대학"과 "학과"는 1:N 관계, <br>
	 * "학과" "과목"은 1:N 관계이므로 <br>
	 * 해당 구조에 맞춰 단과대 - 자식 학과 - 자식 과목 형태로 반환합니다.
	 * @return 
	 */
	@GetMapping("/subject")
	public List<SubjectWithCollegeAndDeptResp> availableSubjectList() {
		return lctApplyService.readAllSubject();
	}
	
	/**
	 * 현재 강의 성적산출에 사용하는 기준들을 반환합니다.
	 * 
	 * @return
	 */
	@GetMapping("/lecture/criteria")
	public Map<String, String> lectureCriteriaList() {
		return lctApplyService.readLectureEvaluateCriteria();
	}
		
	@GetMapping("/lecture/apply/yearterm")
	public Map<String, String> currentApplyYearterm() {
		return Map.of("yeartermCd", "2026_REG1", "yeartermName", "2026년 1학기");
	}
	
}