package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.LctAttRoundInfo;
import kr.or.jsu.classroom.dto.info.LctStuAttInfo;
import kr.or.jsu.classroom.dto.request.AttendanceRecordReq;
import kr.or.jsu.classroom.dto.response.attandance.LctAttRoundLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentAttLabelResp;

@Mapper
public interface LctAttendanceMapper {
	
	/**
	 * 새 강의 회차를 생성합니다.
	 * 셀렉트키로 생성된 회차는 파라미터에 들어있습니다.
	 * 
	 * @param newAttRound 강의 번호, (필요시) QR코드 파일ID를 기록한 객체
	 * @return 생성 성공 시 1
	 */
	public int insertLctAttround(
		LctAttRoundInfo newAttRound
	);
	
	/**
	 * 새 강의 회차가 생성된 후, 요청한 상태대로 <br>
	 * 수강 학생들의 출석 데이터를 생성합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctRound 대상 출석회차
	 * @param enrollIds 학생들의 수강ID 리스트
	 * @param attStatusCd 생성될 출석데이터의 기본 상태
	 * @return 생성된 레코드 수
	 */
	public int insertDefaultAttendanceRecords(
		String lectureId
		, int lctRound
		, List<String> enrollIds
		, String attStatusCd
	);
	
	/**
	 * 강의의 모든 회차에 대한 간략한 출석 정보를 가져옵니다. <br>
	 * (강의주차, 강의일, 총인원, 출석인수, 결석인수...)
	 * 
	 * @param lectureId 강의ID
	 * @return 출석회차 목록 화면에 필요한 정보들 
	 */
	public List<LctAttRoundLabelResp> selectAttRoundLabelList(
		String lectureId
	);
	
	/**
	 * 강의의 특정 회차에 대한 수강생들의 출결기록을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctRound 대상 출석회차
	 * @return
	 */
	public List<LctStuAttInfo> selectAttendanceRecordList(
		String lectureId
		, int lctRound
	);
	
	/**
	 * 강의의 특정 출석회차를 삭제합니다. <br>
	 * 그 출석 회차에 대한 학생들의 출석 기록이 먼저 삭제되어야 합니다.
	 * 
	 * @param lectureId
	 * @param lctRound
	 * @return
	 */
	public int deleteLctAttRound(
		String lectureId
		, int lctRound 
	);
	
	/**
	 * 강의의 특정 출석회차에 대한 학생의 출결기록을 일괄 삭제합니다.
	 * 
	 * @param lectureId
	 * @param lctRound
	 * @return
	 */
	public int deleteLctAttRecord(
		String lectureId
		, int lctRound
	);
	
	/**
	 * 학생의 출결 기록 일괄 변경 요청을 처리합니다.
	 * 
	 * @param lectureId 대상 강의ID
	 * @param attRound 대상 출석회차
	 * @param items 어떤 학생을 어떤 상태로 변경할지에 대한 리스트
	 * @return 변경된 레코드 수
	 */
	public int updateAttendanceRecordList(
		String lectureId
		, int attRound
		, List<AttendanceRecordReq> items
	);
	
	/**
	 * 특정 강의에 대한 수강생들의 간단한 출결기록을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	public List<StudentAttLabelResp> selectStudentAttendanceLabel(
		String lectureId
	);
}
