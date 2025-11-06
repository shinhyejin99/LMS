package kr.or.jsu.lms.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.dto.request.lms.lecture.apply.LctApprovalExecuteReq;
import kr.or.jsu.dto.request.lms.lecture.apply.LctOpenApplyReq;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyDetailResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyLabelResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApprovalLabelResp;
import kr.or.jsu.lms.professor.service.lecture.ProfessorLectureApplyService;
import kr.or.jsu.vo.UsersVO;
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
@RequestMapping("/lms/api/v1/professor")
@RequiredArgsConstructor
public class LMSProfessorRESTController {
	
	private final ProfessorLectureApplyService lctApplyService;
	
	/**
	 * 현재 강의개설신청 대상 학기를 반환합니다. (하드코딩)
	 * 
	 * @return
	 */
	@GetMapping("/lecture/apply/semester")
	public Map<String, String> currentLctOpenSemester() {
		return Map.of("yearTermCd", "2026_REG1", "yearTermName", "2026년도 1학기");
	}
	
	/**
	 * 강의 신청서를 작성하여, <br>
	 * 승인 / 강의신청 / 강의신청_주차별계획 / 강의신청_성적산출기준 테이블에 데이터를 입력합니다.
	 * 
	 * @param request
	 * @param loginUser
	 * @return
	 */
	@PostMapping("/lecture/apply")
	public ResponseEntity<?> createLctOpenApply(
		@RequestBody LctOpenApplyReq request
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		log.info("강의 신청 내용 : {}", PrettyPrint.pretty(request));
		
		String lectureApplyId = lctApplyService.createLectureApply(loginUser.getRealUser(), request);
		
		// Location 헤더 (게시글 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/lms/professor/lecture/apply/%s", lectureApplyId
	    ));

	    return ResponseEntity
	            .created(location) // 201 Created
	            .body(Map.of("success", "신청 성공", "postId", lectureApplyId));
	}
	
	/**
	 * 본인이 신청한 "강의신청" 목록을 요청합니다. 
	 * 
	 * @param loginUser
	 * @return
	 */
	@GetMapping("/lecture/apply")
	public List<LectureOpenApplyLabelResp> readMyLctApply(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		UsersVO realUser = loginUser.getRealUser();
		
		return lctApplyService.readMyLectureApplyList(realUser);
	}
	
	/**
	 * 본인이 신청했거나 승인할 "강의신청" 상세를 요청합니다.
	 * 
	 * @param lctAppyId
	 * @param loginUser
	 * @return
	 */
	@GetMapping("/lecture/apply/{lctAppyId}")
	public LectureOpenApplyDetailResp readLctApplyDetail(
		@PathVariable String lctAppyId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return lctApplyService.readLectureApplyDetail(loginUser.getRealUser(), lctAppyId);
	}
	
	/**
	 * 본인이 승인할 "강의신청" 목록을 요청합니다.
	 * 
	 * @param loginUser
	 * @return
	 */
	@GetMapping("/lecture/approval")
	public List<LectureOpenApprovalLabelResp> readMyLctApproval(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return lctApplyService.readMyLectureApprovalList(loginUser.getRealUser());
	}
	
	/**
	 * 강의 신청을 일괄 승인/반려합니다.
	 * 
	 * @param request
	 * @param loginUser
	 * @return
	 */
	@PutMapping("/lecture/approval")
	public ResponseEntity<Void> executeLctApply(
		@RequestBody LctApprovalExecuteReq request
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		log.info("일괄처리 중 요청 : {}", PrettyPrint.pretty(request));
		
		if(request.isApproved()) lctApplyService.approveLctApply(loginUser.getRealUser(), request);
		else lctApplyService.rejectLctApply(loginUser.getRealUser(), request);
		
		return ResponseEntity.noContent().build();
	}
}


//-- 1. 교수가, 강의개설기간에 강의를 신청한다. (강의신청 폼 페이지로 이동)
//
//-- "학년도" "학기"는 실제로는 "직원이 선택한 시점" 고정이지만, 시연용으로 년도는 2026, 학기는 선택 가능.
//
//
//
//-- 2. 과목을 선택하기 위해, 해당 과목의 상위 "학과" 선택.
//-- "학과"는, 자신이 속한 학과가 기본적으로 선택되어 있다.
//-- "학과" 선택은 "UNIV_DEPT_CD"가 BASIC 으로 끝나는 것의 이름을 선택하고, 그 안에서 학과를 선택한다.
//SELECT ud.* 
//FROM UNIV_DEPT ud
//ORDER BY ud.COLLEGE_CD , ud.UNIV_DEPT_CD;
//
//-- 3. "학과"를 선택했으면, 해당 "학과"에서 이루어지는 "과목" 목록이 출력된다.
//
//-- 4. "과목명"으로 과목을 선택했으면, 해당 과목의 "이수구분", "평가방식", "학점", "시수" 가 출력된다.
//
//-- 선수학습과목, 강의개요, 강의목표, 예상정원을 입력한다.
//-- 강의실, 시간대, 기타 등등 희망사항을 입력한다.
