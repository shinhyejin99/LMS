package kr.or.jsu.mybatis.mapper.classroom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.EnrollGPAInfo;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.SbjRvwQuestionInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.info.StuReviewLctInfo;
import kr.or.jsu.classroom.dto.response.ender.AttendanceStatusResp;
import kr.or.jsu.classroom.dto.response.ender.EnrollingStudentsAndScoreResp;
import kr.or.jsu.classroom.dto.response.ender.ExamWeightAndStatusResp;
import kr.or.jsu.classroom.dto.response.ender.LectureProgressResp;
import kr.or.jsu.classroom.dto.response.ender.TaskWeightAndStatusResp;

@Mapper
public interface ClassroomEnderMapper {
	/**
	 * 강의 진행 현황(주차/주당 블록/기록된 출석회차)을 조회합니다.
	 *
	 * <p>집계 기준</p>
	 * <ul>
	 *   <li>주차 수: <code>LCT_WEEKBY</code> 에서 강의별 COUNT</li>
	 *   <li>주당 출석 블록 수: <code>LCT_ROOM_SCHEDULE</code> 의 TIMEBLOCK_CD를 요일+분 단위로 파싱 후,
	 *       같은 요일 내 30분 간격이 연속되면 하나의 블록으로 간주하여 블록 시작 개수를 합산</li>
	 *   <li>기록된 출석 회차 수: <code>LCT_ATTROUND</code> 의 강의별 COUNT</li>
	 * </ul>
	 *
	 * @param lectureId 강의 ID (예: LECT00000000001)
	 * @return 주차 수, 주당 출석 블록 수, 기록된 출석 회차 수를 담은 응답 DTO
	 * @throws org.apache.ibatis.exceptions.PersistenceException 매퍼 실행 중 SQL 에러가 발생한 경우
	 */
	public LectureProgressResp getProgress(
		String lectureId
	);

	/**
	 * 강의 내 모든 과제(개인/조별)의 가중치와 마감 여부를 조회합니다.
	 *
	 * <p>규칙</p>
	 * <ul>
	 *   <li>대상 테이블: 개인(<code>LCT_INDIVTASK</code>), 조별(<code>LCT_GROUPTASK</code>)</li>
	 *   <li>삭제 제외: <code>DELETE_YN = 'N'</code> 만 포함</li>
	 *   <li>가중치: <code>LCT_TASK_WEIGHT</code> 와 LEFT JOIN, 없으면 0으로 처리</li>
	 *   <li>마감 여부: <code>END_AT &le; SYSDATE</code> 이면 <code>closedYn = 'Y'</code>, 그 외 <code>'N'</code></li>
	 * </ul>
	 *
	 * @param lectureId 강의 ID
	 * @return 과제별(INDIV/GROUP) 가중치, 기간, 마감 여부 목록
	 * @throws org.apache.ibatis.exceptions.PersistenceException 매퍼 실행 중 SQL 에러가 발생한 경우
	 */
	public List<TaskWeightAndStatusResp> getTaskStatus(
		String lectureId
	);

	/**
	 * 강의 내 시험의 가중치와 마감 여부를 조회합니다.
	 *
	 * <p>규칙</p>
	 * <ul>
	 *   <li>대상 테이블: <code>LCT_EXAM</code></li>
	 *   <li>삭제 제외: <code>DELETE_YN = 'N'</code> 만 포함</li>
	 *   <li>가중치: <code>WEIGHT_VALUE</code> (NULL이면 0으로 매핑)</li>
	 *   <li>마감 여부: <code>END_AT &le; SYSDATE</code> 이면 <code>closedYn = 'Y'</code>, 그 외 <code>'N'</code></li>
	 *   <li>시험 타입: <code>EXAM_TYPE</code> 은 ON/OFF(온라인/오프라인)</li>
	 * </ul>
	 *
	 * @param lectureId 강의 ID
	 * @return 시험별 가중치, 기간, 마감 여부 목록
	 * @throws org.apache.ibatis.exceptions.PersistenceException 매퍼 실행 중 SQL 에러가 발생한 경우
	 */
	public List<ExamWeightAndStatusResp> getExamStatus(
		String lectureId
	);
	
	/**
	 * 강의 내 학생별 출석 내역을 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<AttendanceStatusResp> getAttendanceStatus(
		String lectureId
	);
	
	/**
	 * 수강중에는 존재하지 않았던, ENROLL_GPA 테이블의 레코드를 생성합니다.
	 * 
	 * @param gpas
	 * @return
	 */
	public int insertAllStudentGPA(
		List<EnrollGPAInfo> gpas
	);
	
	public String selectStudentGPA(
		String enrollId
	);
	
	/**
	 * 수강중에는 null이었던 AUTO_SCORE를 채워줍니다.
	 * 
	 * @param scores
	 * @return
	 */
	public int updateAllStudentScore(
		List<StuEnrollLctInfo> scores
	);
	
	/**
	 * 강의를 종강 상태로 변경합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public int updateLectureStatusToDone(
		String lectureId	
	);
	
	/**
	 * 수강완료했지만 강의평가를 작성하지 않은 강의ID List를 반환합니다.
	 * 
	 * @param studentNo
	 * @return
	 */
	public List<LectureInfo> selectReviewNeededLectureList(
		String studentNo
	);
	
	/**
	 * 해당 강의 과목에 대한 강의평가 질문들을 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<SbjRvwQuestionInfo> selectReviewQuestionList(
		String lectureId
	);
	
	/**
	 * 리뷰 테이블에 JSON 데이터를 저장합니다.
	 * 
	 * @param enrollId 수강ID
	 * @param reviewJson questionNo, answer 키:맵 형태의 Json
	 * @return
	 */
	public int insertReviewJson(
		String enrollId
		, String resultJson
	);
	
	/**
	 * 특정 강의에 대해 작성된 모든 강의평가를 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<StuReviewLctInfo> selectReviews(
		String lectureId
	);

	/**
	 * 특정 강의에 대해 수강생들의 평점과 성적산출항목별 성적을 가져옵니다. <br>
	 * (성적 확정용)
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EnrollingStudentsAndScoreResp> selectStudentAndScoreList(
		String lectureId	
	);
	
	/**
	 * 특정 학생의 최종 평점(숫자)을 변경합니다.
	 * (성적 확정용)
	 * 
	 * @param request
	 * @return
	 */
	public int updateEnrollmentFinalScore(
		StuEnrollLctInfo enrollment
	);
	
	/**
	 * 특정 학생의 최종 평점(영어)을 변경합니다.
	 * (성적 확정용)
	 * 
	 * @param request
	 * @return
	 */
	public int updateEnrollmentFinalGPA(
		EnrollGPAInfo gpa
	);
	
	
	/**
	 * 특정 강의의 수강중인 모든 학생의 최종 성적을 확정하고,
	 * 확정한 성적에 따라 수강 상태를 변경합니다.
	 * (F, NP일 시 낙제, 그 이외의 경우 수강완료)
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	public int updateToFinalizeAllScore(
		String lectureId
	);
	
	/**
	 * 특정 강의의 성적확정여부를 변경합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public int updateToFinalizeLecture(
		String lectureId
	);
}