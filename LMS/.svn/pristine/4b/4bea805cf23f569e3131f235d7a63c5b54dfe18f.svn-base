package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.db.GrouptaskGroupWithCrewDTO;
import kr.or.jsu.classroom.dto.db.task.NotEvaluatedGrouptaskDTO;
import kr.or.jsu.classroom.dto.info.GrouptaskGroupInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      	수정자				수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.10. 	송태호	          조별과제 생성, 조회 쿼리
 *
 * </pre>
 */
@Mapper
public interface LctGrouptaskMapper {
	
	/**
	 * 조별과제 내용을 등록합니다. <br>
	 * 조는 이 레코드가 생성된 후 트랜잭션으로 생성합니다.
	 * 
	 * @param newTask 조별과제 단건 생성 요청
	 * @return 생성된 레코드 수 + 셀렉트키로 생성한 조별과제ID
	 */
	public int insertGrouptask(
		LctGrouptaskInfo newTask
	);
	
	/**
	 * 조별과제의 조를 등록합니다. <br>
	 * 조별과제가 생성된 후 트랜잭션으로 생성해야 합니다.
	 * 
	 * @param newGroup 조 단건 생성 요청
	 * @return 생성된 레코드 수 + 셀렉트키로 생성한 조ID
	 */
	public int insertGroups(
		GrouptaskGroupInfo newGroup
	);
	
	/**
	 * 조별과제의 빈 제출 레코드를 미리 생성합니다. <br>
	 * 조별과제가 생성된 후 트랜잭션으로 생성해야 합니다.
	 * 
	 * @param blankSubmit 조별과제ID, 조ID를 채운 요청
	 * @return 생성된 레코드 수
	 */
	public int insertGrouptaskSubmit(
		GrouptaskSubmitInfo blankSubmit
	);
	
	/**
	 * 조별로 조원의 수강ID를 삽입하여 조를 구성합니다.
	 * 
	 * @param groupId 조 ID
	 * @param crewsEnrollIdList 조원의 수강ID
	 * @return 한 조에 삽입한 조원의 수
	 */
	public int insertGrouptaskCrew(
		String groupId
		, List<String> crewsEnrollIdList
	);
	
	/**
	 * 강의별로 출제된 조별과제 목록을 조회합니다. <br>
	 * 조별과제 Entity의 모든 컬럼과 더불어, <br>
	 * 1. 출제 당시 과제가 몇 개의 조에게 할당되었고, <br>
	 * 2. 그 중 몇 개의 조가 제출했는지 <br>
	 * 계산된 정보를 추가하여 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 삭제되지 않은 조별과제 목록
	 */
	public List<LctGrouptaskInfo> selectGrouptaskList_PRF(
		String lectureId
	);
	
	/**
	 * 특정 강의에서 출제한 조별과제 목록을 선택합니다. <br>
	 * 조별과제 Entity의 모든 컬럼과 더불어, <br>
	 * 1. 이 과제에 대한 내가 속한 조의 제출 기록이 있는지 <br>
	 * 계산된 정보를 추가하여 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param enrollId 요청자 학생의 해당 강의에서의 수강ID
	 * @return 해당 강의에서 출제한 조별과제 목록, 제출현황
	 */
	public List<LctGrouptaskInfo> selectGrouptaskList_STU(
		String lectureId
		, String enrollId
	);
	
	/**
	 * 조별과제 단건을 조회합니다.
	 * 
	 * @param grouptaskId 조별과제ID
	 * @return 조별과제 단건 정보
	 */
	public LctGrouptaskInfo selectGrouptask(
		String grouptaskId
	);
	
	/**
	 * 조별과제에 대해 생성된 조, 제출과 조 구성원 리스트를 조회합니다.
	 * 
	 * @param grouptaskId 조별과제ID
	 * @param stuNo null일 경우 모든 조, null이 아닐 경우 특정 학생과 관련된
	 * @return 조&제출 리스트 + 조별 구성원 리스트
	 */
	public List<GrouptaskGroupWithCrewDTO> selectGroupList(
		String grouptaskId
		, String stuNo
	);
	
	/**
	 * 조별과제의 제출을 변경합니다. (조장 한정)
	 *
	 * @param submit
	 * @return
	 */
	public int updateGrouptaskSubmit(
		GrouptaskSubmitInfo submit
	);
	
	public int deleteGrouptaskSoftly(
		String grouptaskId
	);
	
	/**
	 * 출제한 조별과제의 상세 내용을 변경합니다.
	 * 
	 * @param changedTask
	 * @return
	 */
	public int updateGrouptask(
		LctGrouptaskInfo changedTask
	);
	
	/**
	 * 교수 사용자가 평가해야 할 과제 목록을 반환합니다.
	 * 
	 * @param professorNo 교번
	 * @return
	 */
	public List<NotEvaluatedGrouptaskDTO> notEvaluatedTaskList(
		String professorNo
	);
}
