package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.GroupTaskCreateReq;
import kr.or.jsu.classroom.dto.request.IndivTaskCreateReq;
import kr.or.jsu.classroom.dto.request.task.GrouptaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskEvaluateReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.TaskWeightValueReq;
import kr.or.jsu.classroom.dto.response.task.EachStudentGrouptaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentIndivtaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedGrouptaskResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedIndivtaskResp;
import kr.or.jsu.classroom.dto.response.task.TaskWeightValueResp;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 8.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 8.     	송태호	          최초 생성	
 *
 * </pre>
 */
public interface ClassPrfTaskService {
	
	// 개인과제를 활성화된 학생들에게 출제 후, 과제ID 반환하여 리다이렉트 URI 구성
	public String createIndivtask(
		String lectureId
		, IndivTaskCreateReq taskCreateReq
	);
	
	// 개인과제 목록(제출율 포함) 조회
	public List<IndivtaskLabelResp_PRF> readIndivtaskList(
		String lectureId
	);
	
	// 개인과제 상세 + 학생별 제출 조회
	public LctIndivtaskDetailResp readIndivtask(
		String lectureId
		, String indivtaskId
	);
	
	// 개인과제 삭제
	public void removeIndivtask(
		String lectureId
		, String indivtaskId
	);
	
	// 개인과제 수정
	public void modifyIndivtask(
		String lectureId
		, IndivtaskModifyReq taskModifyReq
	);
	
	/**
	 * 학생의 개인 과제 제출에 대한 평가를 작성합니다. 
	 * 
	 * @param lectureId 경로변수 : 강의ID
	 * @param indivtaskId 경로변수 : 개인과제ID
	 * @param request "학생의 수강ID", "점수", "코멘트" 포함한 객체
	 */
	public void evaluateIndivtaskSubmit(
		String lectureId
		, String indivtaskId
		, IndivtaskEvaluateReq request
	);
	
	// 조 지정하여 조별과제 생성 후 ID 반환하여 리다이렉트 URI 구성
	public String createGrouptask(
		String lectureId
		, GroupTaskCreateReq groupTaskAndGroups
	);
	
	// 조별과제 목록(제출율 포함) 조회
	public List<GrouptaskLabelResp_PRF> readGrouptaskList(
		String lectureId
	);
	
	/**
	 * 교수의 조별과제 상세 조회 1 <br>
	 * 조별과제 단건 내용만 반환합니다. 
	 * 
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @return 조별과제에 대한 단건 정보
	 */
	public LctGrouptaskDetailResp readGrouptask(
		String lectureId
		, String grouptaskId
	);
	
	/**
	 * 교수의 조별과제 상세 조회 2 <br>
	 * 조별과제에 구성된 조, 조별 제출, 조별 구성원 등을 반환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @return 조별과제에 대한 각 조별 정보
	 */
	public List<GrouptaskJoResp> readGrouptaskJo(
		String lectureId
		, String grouptaskId
	);
	
	// 조별과제 수정
	public void modifyGrouptask(
		String lectureId
		, GrouptaskModifyReq taskModifyReq
	);
	
	/**
	 * 요청 사용자 유효성 검사 후, <br>
	 * 특정 강의의 특정 조별과제를 삭제 상태로 전환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 */
	public void removeGrouptask(
		String lectureId
    	, String grouptaskId
	);
	
	/**
	 * 특정 강의에서 출제된 삭제되지 않은 과제들의 가중치를 요청합니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<TaskWeightValueResp> readTaskWeightValues(
		String lectureId
	);
	
	/**
	 * 특정 강의에서 출제된 삭제되지 않은 과제들의 가중치를 일괄 수정합니다.
	 * 
	 * @param values
	 */
	public void modifyAllTaskWeightValues(
		String lectureId
		, List<TaskWeightValueReq> values
	);
	
	/**
	 * 강의의 개인과제 중 <br>
	 * 1. 진행중인 강의의 <br>
	 * 2. 개인과제가 삭제되지 않았고 <br>
	 * 3. 개인과제에 대해 아직 평가받지 않은 제출이 존재하는 <br>
	 * 강의 & 개인과제와 과제별 채점할 제출물 수를 반환합니다.
	 */
	public List<NotEvaluatedIndivtaskResp> readNotEvaluatedIndivtaskList();
	
	/**
	 * 강의의 조별과제 중 <br>
	 * 1. 진행중인 강의의 <br>
	 * 2. 조별과제가 삭제되지 않았고 <br>
	 * 3. 조별과제에 대해 아직 평가받지 않은 제출이 존재하는 <br>
	 * 강의 & 조별과제와 과제별 채점할 제출물 수를 반환합니다.
	 */
	public List<NotEvaluatedGrouptaskResp> readNotEvaluatedGrouptaskList();
	
	/**
	 * 특정 강의의 (삭제되지 않은) 개인 과제와, 모든 과제별 학생의 점수를 가져옵니다. <br>
	 * 제출하지 않은 경우 0점, 제출했으나 교수가 채점하지 않은 경우 null
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentIndivtaskScoreResp> readAllIndivtaskAndEachStudentScore(
		String lectureId
	);
	
	/**
	 * 특정 강의의 (삭제되지 않은) 조별 과제와, 모든 과제별 학생의 점수를 가져옵니다. <br>
	 * 제출하지 않은 경우 0점, 제출했으나 교수가 채점하지 않은 경우 null
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentGrouptaskScoreResp> readAllGrouptaskAndEachStudentScore(
		String lectureId
	);
}
