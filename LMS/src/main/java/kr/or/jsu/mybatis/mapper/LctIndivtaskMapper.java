package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.db.task.NotEvaluatedIndivtaskDTO;
import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;

@Mapper
public interface LctIndivtaskMapper {
	
	/**
	 * 개인 과제 단건을 생성합니다. <br>
	 * 삭제 상태는 'N', 생성일시는 SYSDATE 고정입니다.
	 * 
	 * @param newTask 생성할 과제 정보
	 * @return 생성된 레코드 수
	 */
	public int insertIndivtask(
		LctIndivtaskInfo newTask
	);
	
	/**
	 * 수강ID별로, 개인 과제에 대한 제출을 생성합니다. <br>
	 * 개인 과제가 생성될 때 일괄적으로 만들어집니다. 
	 * 
	 * @param enrollIds 현재 강의를 "수강중"인 학생들의 수강ID 
	 * @param indivtaskId 대상 개인과제ID
	 * @return 생성된 레코드 수
	 */
	public int insertIndivtaskSubmit(
		List<String> enrollIds
		, String indivtaskId	
	);
	
	/**
	 * 개인 과제 단건을 선택합니다. <br>
	 * 소프트 삭제되지 않은 과제만 선택할 수 있습니다. <br>
	 * 존재하지 않거나 삭제된 과제ID로 실행할 경우 결과는 null입니다.
	 * 
	 * @param indivtaskId 선택할 과제ID
	 * @return 과제 정보, 존재하지 않거나 삭제되었으면 null
	 */
	public LctIndivtaskInfo selectIndivtask(
		String indivtaskId
	);
	
	/**
	 * 개인 과제에 대한 제출을 선택합니다. <br>
	 * 1. stuNo를 null로 넣을 경우, 교수 입장에서 <br>
	 * 해당 과제에 대한 모든 학생들의 제출 정보를 가져옵니다. <br>
	 * 2. stuNo를 입력한 경우, 해당 학생 입장에서 <br>
	 * 해당 과제에 대한 자신의 제출 정보만을 가져옵니다.
	 * 
	 * @param indivtaskId 대상 개인과제ID
	 * @param enrollId 자신의 수강ID <br>
	 * 검색하는 사용자가 학생일 경우만 입력
	 * @return
	 */
	public List<IndivtaskSubmitInfo> selectIndivtaskSubmit(
		String indivtaskId
		, String enrollId
	);
	
	/**
	 * 특정 강의에서 출제한 개인 과제 목록을 선택합니다. <br>
	 * 개인과제 Entity의 모든 컬럼과 더불어, <br>
	 * 1. 출제 당시 과제가 몇 명에게 할당되었고, <br>
	 * 2. 그 중 몇 명이 제출했는지 <br>
	 * 계산된 정보를 추가하여 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 해당 강의에서 출제한 개인 과제 목록, 제출현황
	 */
	public List<LctIndivtaskInfo> selectIndivtaskList_PRF(
		String lectureId
	);
	
	/**
	 * 특정 강의에서 출제한 개인 과제 목록을 선택합니다. <br>
	 * 개인과제 Entity의 모든 컬럼과 더불어, <br>
	 * 1. 이 과제에 대한 나의 제출 기록이 있는지 <br>
	 * 계산된 정보를 추가하여 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param enrollId 요청자 학생의 해당 강의에서의 수강ID
	 * @return 해당 강의에서 출제한 개인 과제 목록, 제출현황
	 */
	public List<LctIndivtaskInfo> selectIndivtaskList_STU(
		String lectureId
		, String enrollId
	);
	
	/**
	 * 출제한 개인과제의 삭제 상태를 변경합니다. <br>
	 * 변경한 삭제 상태는 되돌릴 수 없습니다.
	 * 
	 * @param indivtaskId 삭제 대상 개인과제ID
	 * @return 삭제된 과제 수
	 */
	public int deleteIndivtaskSoftly(
		String indivtaskId
	);
	
	/**
	 * 출제한 개인과제의 상세 내용을 변경합니다.
	 * 
	 * @param changedTask
	 * @return
	 */
	public int updateIndivtask(
		LctIndivtaskInfo changedTask
	);
	
	/**
	 * 개인 과제의 제출을 변경하거나 (학생) <br>
	 * 제출에 대한 평가를 기입합니다. (교수)
	 *
	 * @param submit 변경하지 않을 부분은 null로 채운 객체
	 * @return
	 */
	public int updateIndivtaskSubmit(
		IndivtaskSubmitInfo submit
	);
	
	/**
	 * 해당 학생이 제출해야 할 과제 목록을 반환합니다.
	 * 
	 * @param studentNo 학번
	 * @return
	 */
	public List<LctIndivtaskInfo> notSubmittedTaskList(
		String studentNo
	);
	
	/**
	 * 교수 사용자가 평가해야 할 과제 목록을 반환합니다.
	 * 
	 * @param professorNo 교번
	 * @return
	 */
	public List<NotEvaluatedIndivtaskDTO> notEvaluatedTaskList(
		String professorNo
	);
}
