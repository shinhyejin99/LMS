package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.response.classroom.ProfessorMyInfoResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;

/**
 * 교수의 강의에 대한 정보를 가공하여 컨트롤러에 전달합니다.
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 */
public interface ClassroomProfessorService {
	
	/**
	 * 사용자 교수의 개인정보를 조회합니다.
	 * 
	 * @return 개인정보(간략)
	 */
	public ProfessorMyInfoResp getMyInfo();
	
	/**
	 * 사용자 교수가 담당한 강의 목록을 반환합니다.
	 * 
	 * @return 응답용 강의목록 정보 리스트
	 */
	public List<LectureLabelResp> readMyLectureList();
	
	/**
	 * 특정 학년도학기의 사용자 교수에 대한 강의 시간표를 반환합니다.
	 * 
	 * @param yeartermCd 학년도학기코드
	 * @return
	 */
	public List<LectureScheduleResp> readMySchedule(
		String yeartermCd
	);
}
