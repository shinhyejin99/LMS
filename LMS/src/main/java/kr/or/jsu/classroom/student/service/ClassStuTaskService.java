package kr.or.jsu.classroom.student.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.task.TaskSubmitReq;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.NotSubmittedIndivtaskResp;
import kr.or.jsu.vo.UsersVO;

/**
 * 클래스룸에서 학생의 과제에 대한 활동을 처리합니다.
 * 
 * @author 송태호
 * @since 2025. 10. 8.
 */
public interface ClassStuTaskService {
	
	/**
	 * 특정 강의에 대한 개인과제 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다. <br>
	 * 해당 과제에 대해 자신의 제출 기록이 있는지 확인하여 추가 기록합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<IndivtaskLabelResp_STU> readIndivtaskList(
		UsersVO user
		, String lectureId
	);
		
	/**
	 * 특정 강의의 특정 개인과제 상세조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 정보를 반환합니다. <br>
	 * (교수가 출제한 과제에 대한 내용 + 자신의 제출 기록)
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param indivtaskId 개인과제ID
	 * @return
	 */
	public LctIndivtaskDetailResp readIndivtask(
		UsersVO user
		, String lectureId
		, String indivtaskId
	);

	/**
	 * 특정 개인과제에 대한 제출 요청을 받아, <br>
	 * 제출 권한을 확인한 뒤 제출을 기록합니다. <br>
	 * 제출 권한 : <br>
	 * 1. 강의를 수강중이며 <br>
	 * 2. 제출 대상으로 등록되었는가 <br>
	 * 3. 제출 기간인가
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param indivtaskId 개인과제ID
	 * @param request 제출 내용 + 제출 파일
	 */
	public void submitIndivtask(
		UsersVO user
		, String lectureId
		, String indivtaskId
		, TaskSubmitReq request
	);
	
	/**
	 * 특정 강의에 대한 조별과제 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다. <br>
	 * 해당 과제에 대해 자신이 소속된 조의 제출 기록이 있는지 확인하여 추가 기록합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<GrouptaskLabelResp_STU> readGrouptaskList(
		UsersVO user
		, String lectureId
	);
	
	
	/**
	 * 학생의 조별과제 상세 조회 1 <br>
	 * 조별과제 단건 내용만 반환합니다. 
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @return 조별과제에 대한 단건 정보
	 */
	public LctGrouptaskDetailResp readGrouptask(
		UsersVO user
		, String lectureId
		, String grouptaskId
	);
	
	/**
	 * 학생의 조별과제 상세 조회 2 <br>
	 * 자신이 속한 조, 제출, 조별 구성원 등을 반환합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @return 
	 */
	public GrouptaskJoResp readGrouptaskJo(
		UsersVO user
		, String lectureId
		, String grouptaskId
	);

	/**
	 * 특정 조별과제에 대한 제출 요청을 받아, <br>
	 * 제출 권한을 확인한 뒤 제출을 기록합니다. <br>
	 * 제출 권한 : <br>
	 * 1. 강의를 수강중이며 <br>
	 * 2. 제출 대상으로 등록되었는가 <br>
	 * 3. 해당 조의 조장이며 <br>
	 * 4. 제출 기간인가
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @param request 제출 내용 + 제출 파일
	 */
	public void submitGrouptask(
		UsersVO user
		, String lectureId
		, String grouptaskId
		, TaskSubmitReq request
	);
	
	/**
	 * 1. 자신이 "수강 중"인 강의의,
	 * 2. 삭제되지 않은 "개인과제"의
	 * 3. "제출 기간이면서" "제출하지 않은"
	 * 과제의 정보를 반환합니다.
	 * 
	 * @param user
	 * @return
	 */
	public List<NotSubmittedIndivtaskResp> readNotSubmittedTaskList(
		UsersVO user
	);
}