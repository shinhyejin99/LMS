package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.response.ender.EnrollingStudentsAndScoreResp;

@Mapper
public interface StuEnrollLctMapper {
	
	/**
	 * 강의ID와 학번으로 수강정보를 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 * @return
	 */
	public StuEnrollLctInfo selectEnrollRecord(
		String lectureId
		, String studentNo
	);
	
	/**
	 * 강의ID와 학번으로 개인정보 + 수강정보를 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 * @return
	 */
	public EnrollSimpleDTO selectEnrollingStudent(
		String lectureId
		, String studentNo
	);
	
	/**
	 * 특정 강의를 수강한 학생들의 수강정보를 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param isNotCancel 취소 여부 <br>
	 * null = 모든, true = 수강중/수강완료만, false = 수강철회/포기
	 * @return
	 */
	public List<StuEnrollLctInfo> selectAllStudentList(
		String lectureId
		, Boolean isNotCancel
	);
	
	/**
	 * 강의ID, 출석 회차를 입력하여, <br>
	 * 기록이 존재하는 학생들의 개인정보를 가져옵니다. <br>
	 * (출석 정보가 아닌, 개인정보 + 수강정보를 가져옴) <br>
	 * (출석체크할 화면에 띄우기 위함)
	 * 
	 * @param lectureId 강의ID
	 * @param lctRound 출석 회차
	 * @return
	 */
	public List<EnrollSimpleDTO> selectAttendanceRecords(
		String lectureId
		, int lctRound
	);
	
}
