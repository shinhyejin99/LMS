package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.ender.LctGPARecordReq;
import kr.or.jsu.classroom.dto.request.ender.LctSectionScoreUpsertReq;
import kr.or.jsu.classroom.dto.request.finalize.ChangeScoreReq;
import kr.or.jsu.classroom.dto.response.ender.AttendanceStatusResp;
import kr.or.jsu.classroom.dto.response.ender.EnrollingStudentsAndScoreResp;
import kr.or.jsu.classroom.dto.response.ender.ExamWeightAndStatusResp;
import kr.or.jsu.classroom.dto.response.ender.LctSectionScoreResp;
import kr.or.jsu.classroom.dto.response.ender.LectureProgressResp;
import kr.or.jsu.classroom.dto.response.ender.TaskWeightAndStatusResp;

public interface ClassPrfEnderService {
	
	/**
	 * 종강 조건 1, 강의의 진행률을 확인합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public LectureProgressResp readProgress(
		String lectureId
	);
	
	/**
	 * 종강 조건 2, 과제별 가중치 등 정보를 확인합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<TaskWeightAndStatusResp> readTaskStatus(
		String lectureId
	);
	
	/**
	 * 종강 조건 3, 강의의 시험 가중치를 확인합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<ExamWeightAndStatusResp> readExamStatus(
		String lectureId	
	);
	
	/**
	 * 종강 조건 4, 출석 미정 상태가 있는지 확인합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<AttendanceStatusResp> readAttendanceStatus(
		String lectureId
	);
	
	/**
	 * 종강 절차 1, 학생별로 산출 항목별 점수를 저장합니다.
	 * 
	 * @param lectureId
	 * @param request
	 */
	public void mergeEachSectionScore(
		String lectureId
		, List<LctSectionScoreUpsertReq> request
	);
	
	/**
	 * 종강 절차 2, 저장한 학생별, 산출 항목별 점수 목록을 가져옵니다. 
	 * 
	 * @param lectureId
	 * @param gradeCriteriaCd
	 * @return
	 */
	public List<LctSectionScoreResp> readEachSectionScore(
		String lectureId
		, String gradeCriteriaCd
	);
	
	/**
	 * 종강 절차 3, 순위별로 나온 학생 목록에 평점구간을 설정하여 저장합니다. <br>
	 * STU_ENROLL_LCT에는 AUTO_SCORE(숫자 점수), ENROLL_GPA에는 평점이 저장됩니다. <br>
	 * P(pass), NP(not pass)일 경우는 숫자 점수는 0점입니다.
	 * 
	 * @param lectureId
	 * @param request
	 */
	public void recordGPAAndScore(
		String lectureId
		, List<LctGPARecordReq> request
	);
	
	/**
	 * 성적 확정절차 1, 모든 수강생의 성적 정보를 가져옵니다. <br>
	 * 학생별 수강ID, 산출평점, 수정평점+이유, 각 성적 산출 항목별 점수를 반환합니다. 
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EnrollingStudentsAndScoreResp> readStudentAndScoreList(
		String lectureId
	);
	
	/**
	 * 성적 확정 절차 2, 특정 학생의 평점을 수정하고 이유를 기록 <br>
	 * lecture의 finalScore & changeReason, enroll_gpa의 gpa_cd 변경
	 * 
	 * @param lectureId 강의ID
	 * @param request 수강ID, 평점(영어), 변경이유 
	 */
	public void modifyStudentGrade(
		String lectureId
		, ChangeScoreReq request
	);
	
	/**
	 * 성적 확정 절차 3 <br>
	 * 1. 모든 학생들의 평점을 확정합니다. (finalGrade가 null인 경우, autoGrade를 넣음) <br>
	 * 2. 모든 학생들의 수강 상태를 변경합니다. (default 수강완료, F학점일 경우 낙제) 
	 * 3. 해당 강의의 성적확정여부를 Y로 변경합니다.
	 */
	public void finalizeLecture(
		String lectureId
	);
	
}
