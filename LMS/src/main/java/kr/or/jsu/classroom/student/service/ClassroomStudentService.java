package kr.or.jsu.classroom.student.service;

import java.util.List;

import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyEnrollInfoResp;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyInfoResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.classroom.dto.response.student.ClassmateResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.vo.UsersVO;

/**
 * 사용자 학생과 관련된 강의에 대한 정보를 가공하여 컨트롤러에 전달합니다.
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 */
public interface ClassroomStudentService {
	// TODO 예외처리
	
	/**
	 * 내 개인정보 일부를 가져옵니다.
	 * 
	 * @param user 접속한 사용자
	 * @return
	 */
	public StudentMyInfoResp getMyInfo(
		UsersVO user
	);
	
	/**
	 * 학생이 수강한 모든 강의를 가져옵니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @return 학생이 수강한 (폐강되지 않은) 모든 강의
	 */
	public List<LectureLabelResp> readMyLectureList(
		UsersVO user
	);
	
	/**
	 * 특정 학년도학기의 사용자 학생에 대한 강의 시간표를 반환합니다.
	 * 
	 * @param yeartermCd 학년도학기코드
	 * @return
	 */
	public List<LectureScheduleResp> readMySchedule(
		UsersVO user
		, String yeartermCd
	);
	
	/**
	 * 특정 강의에서 사용되는 내 수강정보를 가져옵니다. <br>
	 * 해당 강의를 수강한 기록이 없는 경우 예외가 발생합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return 해당 강의에 대한 자신의 수강정보
	 * @throws 수강한 적 없는 강의에 대한 수강정보를 요청한 경우
	 */
	public StuEnrollLctInfo checkRelevantAndGetMyEnrollInfo(
		UsersVO user
		, String lectureId
	) throws RuntimeException;
	
	/**
	 * 내 개인정보 + 특정 강의에서 사용되는 내 수강정보를 가져옵니다. <br>
	 * 해당 강의를 수강한 기록이 없는 경우 예외가 발생합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return 개인정보 + 해당 강의에 대한 자신의 수강정보
	 * @throws 수강한 적 없는 강의에 대한 수강정보를 요청한 경우
	 */
	public StudentMyEnrollInfoResp getMyInfoAndEnrollment(
		UsersVO user
		, String lectureId
	) throws RuntimeException;
	
	/**
	 * 동료 수강생 정보를 가져옵니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 대상 강의
	 */
	public List<ClassmateResp> readClassmates(
		UsersVO user
		, String lectureId
	);
	
	/**
	 * 강의의 모든 출석회차와 그에 대한 자신의 출석정보를 가져옵니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	public List<StudentsAllAttendanceResp> readMyAttendacneList(
		UsersVO user
		, String lectureId
	);
}