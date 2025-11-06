package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.AttendanceRecordReq;
import kr.or.jsu.classroom.dto.response.attandance.LctAttRoundLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentAttLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentForAttendanceResp;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 6.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 6.     	송태호	          최초 생성
 *
 * </pre>
 */
public interface ClassPrfAttendanceService {
	
	/**
	 * 요청한 강의의 모든 출석회차 목록을 표시하기 위한 정보를 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 출석회차 목록 표시용 정보
	 */
	public List<LctAttRoundLabelResp> readAttRoundLabel(
		String lectureId
	);
	
	/**
	 * 요청한 강의에 새 출석 회차를 생성하고 <br>
	 * 요청한 상태를 기본값으로 수강중인 학생들의 출석을 생성한 후 <br>
	 * 생성된 회차를 반환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param defaultStatus 출석회차 생성 시 기본값으로 요청한 상태 <br>
	 * TBD = 미정, OK = 출석, NO = 결석
	 * 
	 * @return
	 */
	public int createManualAttRound(
		String lectureId
		, String defaultStatus
	);
	
	
	/**
	 * 특정 강의의 회차 출결기록에 기록된 학생들의 데이터 출력
	 * 
	 * @param lectureId 강의ID
	 * @param lctRound 출석회차
	 * @return
	 */
	public List<StudentForAttendanceResp> getStudentListForAtt(
		String lectureId
		, int lctRound
	);
	
	/**
	 * 특정 출석회차를 삭제합니다. <br>
	 * 
	 * @param lectureId 강의ID
	 * @param lctRound 삭제할 출석회차 수
	 */
	public void removeAttRound(
		String lectureId
		, int lctRound
	);
	
	/**
	 * 특정 강의 출석회차에 대해 학생들의 세부 출석 변경사항을 기록합니다.
	 * 
	 * @param lectureId
	 * @param attRound
	 * @param items
	 */
	public void modifyAttendanceRecord(
		String lectureId
		, int attRound
		, List<AttendanceRecordReq> items
	);
	
	/**
	 * 특정 강의에 대한 수강생들의 출석 요약을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<StudentAttLabelResp> getStudentAttendanceSummary(
		String lectureId
	);
	
}
