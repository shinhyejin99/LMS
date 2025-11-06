package kr.or.jsu.mybatis.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.classroom.dto.db.LectureSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
// import kr.or.jsu.dto.ApprovalLineRequestDetailDTO; // Map으로 대체하여 사용하지 않음
import kr.or.jsu.classroom.dto.db.LectureWithWeekbyDTO;
import kr.or.jsu.classroom.dto.db.StudentAndEnrollDTO;
import kr.or.jsu.classroom.dto.db.UnassignedLectureDTO;
import kr.or.jsu.classroom.dto.info.LctGraderatioInfo;
import kr.or.jsu.classroom.dto.info.LctRoomScheduleInfo;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;

/**
 * 수강신청, 수강중인 강의 목록 등 강의 자체를 검색할 상황에 사용합니다.
 *
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성
 * 2025. 11. 10.       Gemini          insertLecture 메서드 시그니처를 Map으로 수정 (서비스 로직 일치)
 * </pre>
 */
@Mapper
public interface LectureMapper {

	/**
	 * 강의 한 건에 대한 기본 정보와 수강생 수를 가져옵니다.
	 * @param lectureId 강의ID
	 * @return 강의 엔터티 + 과목 엔터티 조인 + 수강생 수
	 */
	public LectureSimpleDTO selectLecture(String lectureId);

	/**
	 * 특정 교수와 관련있는 강의(모든/진행했던/진행중인)를 가져옵니다.
	 *
	 * @param professorNo 강의 담당 교수의 교번
	 * @param ongoing null = 모든 강의, true = 진행중인, false = 종강된
	 * @return
	 */
	public List<LectureSimpleDTO> selectTeachingLectureList(String professorNo);

	/**
	 * 특정 학생과 관련있는 강의(모든/수강중인/수강완료한)를 가져옵니다.
	 *
	 * @param studentNo 학생의 학번
	 * @param ongoing null = 모든 강의, true = 수강중인, false = 수강완료한
	 * @return
	 */
	public List<LectureSimpleDTO> selectEnrolledLectureList(String studentNo);

	/**
	 * 특정 강의들에 대한 시간표를 Json 형식으로 가져옵니다.
	 *
	 * @param lectureIds 시간표를 원하는 강의들의 기본키를 모은 Collection
	 * @return 맵에 옮겨 담을 수 있는 lectureId 1 : 1 scheduleJson 객체 리스트
	 */
	public List<LectureWithScheduleDTO> selectScheduleListJson(Collection<String> lectureIds);

	/**
	 * 특정 강의의 주차별 계획들을 가져옵니다.
	 * @param lectureId
	 * @return
	 */
	public LectureWithWeekbyDTO selectLectureWithWeekby(String lectureId);
	
	/**
	 * 특정 강의의 성적산출비율을 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<LctGraderatioInfo> selectGraderatioList(
		String lectureId
	);
	
	/**
	 * 특정 강의를 수강한 학생들의 인적 정보 + 수강 정보를 가져옵니다. <br>
	 *
	 * @param lectureId 강의ID
	 * @param isNotCancel 취소 여부 <br>
	 * null = 모든, true = 수강중/수강완료만, false = 수강철회/포기
	 * @return 학생 List
	 */
	public List<StudentAndEnrollDTO> selectLectureWithStudent(
		String lectureId
		, Boolean isNotCancel
	);
	
	/**
	 * 특정 학기, 특정 교수의 시간표를 반환합니다.
	 * 
	 * @param yearTermCd 학년도학기코드
	 * @param professorNo 교번
	 * @return
	 */
	public List<LectureScheduleResp> selectPrfLectureSchedule(
		String yearTermCd
		, String professorNo
	);
	
	/**
	 * 특정 학기, 특정 학생의 시간표를 반환합니다.
	 * 
	 * @param yearTermCd 학년도학기코드
	 * @param studentNo 학번
	 * @return
	 */
	public List<LectureScheduleResp> selectStuLectureSchedule(
		String yearTermCd
		, String studentNo
	);
	
	/**
	 * 특정 학기, 특정 강의실의 시간표를 반환합니다.
	 * 
	 * @param yearTermCd 학년도학기코드
	 * @param placeCd 건물ID
	 * @return
	 */
	public List<LectureScheduleResp> selectRoomLectureSchedule(
		String yearTermCd
		, String placeCd
	);
	
	/**
	 * 강의를 특정 강의실의 특정 시간에 배정합니다.
	 * 
	 * @param schedules
	 * @return
	 */
	public int insertClassroomSchedule(
		List<LctRoomScheduleInfo> schedules 
	);
	
	/**
	 * 특정 승인ID로 신청한 강의신청서대로 강의를 개설합니다. <br>
	 * maxCap은 따로 설정할 수 있으나, null로 넣을 경우 신청의 예상정원을 그대로 사용합니다.
	 * 
	 * @param approveId
	 * @param maxCap
	 * @param lectureId 여기에 셀렉트키가 채워짐, null을 넣어두기.
	 * @return
	 */
	public int insertLectureWithApply(
	    @Param("approveId") String approveId,
	    @Param("lecture") LectureInfo lecture
	);

	/**
	 * 강의주차별계획 강의신청시 작성한대로 넣기
	 * 
	 * @param approveId
	 * @param lectureId
	 * @return
	 */
	public int insertLctWeekbyWithApply(
		String approveId
		, String lectureId
	);
	
	/**
	 * 강의 성적산출항목&비율 강의신청시 작성한대로 넣기
	 * 
	 * @param approveId
	 * @param lectureId
	 * @return
	 */
	public int insertLctGraderatioWithApply(
		String approveId
		, String lectureId
	);
	
	/**
	 * 특정 학기에 대해, <br>
	 * 1. 시간표가 아예 없거나 <br>
	 * 2. 강의 시수가 부족한 <br>
	 * 3. 강의 시수가 초과된 <br>
	 * 강의에 대한 정보 리스트를 반환합니다.
	 * 
	 * @param yeartermCd 특정 학기
	 * @return
	 */
	public List<UnassignedLectureDTO> selectUnassignedLectureList(
		String yeartermCd
	);
	
	
	
	// 혜진님 이거 xml쪽에는 구현이 안되있는건데요?
	// 근데 이걸 StaffApprovalServiceImpl 에서 사용하고있던데 뭐죠
	// 149, 165, 185, 221 line
	public int insertLecture(Map<String, Object> paramMap);
	public int updateLectureOpenApplyStatus(Map<String, Object> lectureApplyUpdateParam);
	public Map<String, Object> selectLectureAssignmentDetails(String lctApplyId);

}