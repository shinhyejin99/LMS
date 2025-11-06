package kr.or.jsu.lms.professor.service.lecture;

import java.util.List;
import java.util.Map;

import kr.or.jsu.dto.request.lms.lecture.apply.LctApprovalExecuteReq;
import kr.or.jsu.dto.request.lms.lecture.apply.LctOpenApplyReq;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyDetailResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyLabelResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApprovalLabelResp;
import kr.or.jsu.dto.response.lms.lecture.apply.SubjectWithCollegeAndDeptResp;
import kr.or.jsu.vo.UsersVO;

public interface ProfessorLectureApplyService {
	
	/**
	 * 모든 과목을 가져와서, 단과대학 / 학과별 소속으로 구분합니다.
	 * 
	 * @return
	 */
	public List<SubjectWithCollegeAndDeptResp> readAllSubject();
	
	/**
	 * 현재 사용하는 강의 성적 평가 기준을 가져옵니다.
	 * 
	 * @return
	 */
	public Map<String, String> readLectureEvaluateCriteria();
	
	/**
	 * 강의를 신청하는 교수 정보, 강의 신청과 관련된 요청 객체를 받아 <br>
	 * 1. 해당 과목이 소속된 학과의 학과장을 대상으로 하는 승인 요청 생성 <br>
	 * 2. 해당 과목 신청서 생성 <br>
	 * 3. 해당 과목의 주차계획 생성 <br>
	 * 4. 해당 과목의 성적산출항목&비율 생성 <br>
	 * 모든 과정이 완료되었을 경우에, 리다이렉트용 URL 완성을 위한 강의신청ID를 반환합니다.
	 * 
	 * @param realUser 로그인한, 강의 신청하는 교수 정보
	 * @param request 강의 신청서 내용
	 * @return 생성된 강의신청ID
	 */
	public String createLectureApply(
		UsersVO realUser
		, LctOpenApplyReq request
	);
	
	/**
	 * 교수 자신이 신청한 강의신청 목록을 반환합니다.
	 * 
	 * @param realUser 로그인한, 강의 신청하는 교수 정보
	 * @return
	 */
	public List<LectureOpenApplyLabelResp> readMyLectureApplyList(
		UsersVO realUser	
	);
	
	/**
	 * 열람 권한이 있는 강의신청 상세를 조회합니다. <br>
	 * "작성자 본인"과 "승인자" 에게만 열람 권한이 있습니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param lctOpenApplyId 열람할 강의신청ID
	 * @return
	 */
	public LectureOpenApplyDetailResp readLectureApplyDetail(
		UsersVO realUser
		, String lctOpenApplyId
	);
	
	/**
	 * 사용자 교수가 처리할 강의 신청 목록을 반환합니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @return
	 */
	public List<LectureOpenApprovalLabelResp> readMyLectureApprovalList(
		UsersVO realUser
	);
	
	/**
	 * 1~n개의 강의개설신청 승인에 대한 처리 요청을 받아, <br>
	 * 사용자 교수가 승인자로 등록되었는지 확인하고, <br>
	 * 모든 승인을 요청에 따라 승인하거나 반려합니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param request 일괄 처리할 승인ID와 처리할 액션(승인or반려)
	 */
	public void approveLctApply(
		UsersVO realUser
		, LctApprovalExecuteReq request
	);
	
	/**
	 * 1~n개의 강의개설신청 승인에 대한 처리 요청, <br>
	 * 사용자 교수가 승인자로 등록되었는지 확인하고, <br>
	 * 모든 승인을 일괄 "반려" 합니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param request 일괄 처리할 승인ID와 처리할 액션(승인or반려)
	 */
	public void rejectLctApply(
		UsersVO realUser
		, LctApprovalExecuteReq request
	);
	
}
