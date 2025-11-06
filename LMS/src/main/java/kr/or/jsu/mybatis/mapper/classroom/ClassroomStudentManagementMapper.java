package kr.or.jsu.mybatis.mapper.classroom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.db.studentManage.GrouptaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.IndivtaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctAttRoundAndStuAttendacneDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctExamAndStuSubmitDTO;

@Mapper
public interface ClassroomStudentManagementMapper {

	/**
	 * 강의에서 생성된 모든 출석회차와, <br>
	 * 그 출석회차에 대한 학생의 출석기록을 가져옵니다. <br>
	 * 출석회차에 대한 출석기록이 존재하지 않을 수 있습니다. (LEFT JOIN)
	 *
	 * @param lectureId 강의ID
	 * @param enrollId 학생별 수강ID
	 * @return 출석회차 정보 + 학생의 출석기록(Optional)
	 */
	public List<LctAttRoundAndStuAttendacneDTO> selectStuAttendance(
		String lectureId
		, String enrollId
	);

	/**
	 * 강의에서 출제된 모든 개인과제와, <br>
	 * 그 과제들에 대한 특정 학생의 제출기록을 조회합니다 <br>
	 * 개인과제에 대한 제출기록이 존재하지 않을 수 있습니다. (LEFT JOIN)
	 *
	 * @param lectureId 강의ID
	 * @param enrollId 학생의 수강ID
	 * @return 시험 정보 + 학생의 응시기록
	 */
	public List<IndivtaskAndStuSubmitDTO> selectIndivtaskWithSubmitByEnrollId(
		String lectureId
		, String enrollId
	);
	
	/**
	 * 강의에서 출제된 모든 조별과제와, <br>
	 * 그 과제들에 대한 특정 학생이 속한 조의 제출기록을 조회합니다 <br>
	 * 조별과제에 대한 제출기록이 존재하지 않을 수 있습니다. (LEFT JOIN)
	 *
	 * @param lectureId 강의ID
	 * @param enrollId 학생의 수강ID
	 * @return 시험 정보 + 학생의 응시기록
	 */
	public List<GrouptaskAndStuSubmitDTO> selectGrouptaskWithSubmitByEnrollId(
		String lectureId
		, String enrollId
	);
	
	/**
	 * 강의에서 출제된 모든 시험과, <br>
	 * 그 시험들에 대한 특정 학생의 응시기록을 조회합니다 <br>
	 * 시험에 대한 응시기록이 존재하지 않을 수 있습니다. (LEFT JOIN)
	 *
	 * @param lectureId 강의ID
	 * @param enrollId 학생의 수강ID
	 * @return 시험 정보 + 학생의 응시기록
	 */
	public List<LctExamAndStuSubmitDTO> selectExamWithSubmitByStudent(
		String lectureId
		, String enrollId
	);


}
