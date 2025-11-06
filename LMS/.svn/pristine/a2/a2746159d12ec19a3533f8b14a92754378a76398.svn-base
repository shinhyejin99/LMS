package kr.or.jsu.mybatis.mapper.classroom;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.studentManage.GrouptaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.IndivtaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctAttRoundAndStuAttendacneDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctExamAndStuSubmitDTO;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class ClassroomStudentManagementMapperTest {

	@Autowired
	ClassroomStudentManagementMapper mapper;

	@Test
	void 모든출석회차랑그거에대한특정학생의출석기록() {
		String lectureId = "LECT00000000001";
		String enrollId = "STENRL000000001";

		List<LctAttRoundAndStuAttendacneDTO> list =
			mapper.selectStuAttendance(lectureId, enrollId);

		log.info("결과 : {}", PrettyPrint.pretty(list));
	}
	
	@Test
	void 모든제출가능한개인과제와그거에대한특정학생의제출기록() {
		String lectureId = "LECT00000000001";
		String enrollId = "STENRL000000001";
		
		List<IndivtaskAndStuSubmitDTO> list = 
			mapper.selectIndivtaskWithSubmitByEnrollId(lectureId, enrollId);
		
		log.info("결과 : {}", PrettyPrint.pretty(list));
	}
	
	@Test
	void 모든제출가능한조별과제와그거에대한특정학생의제출기록() {
		String lectureId = "LECT00000000001";
		String enrollId = "STENRL000000001";
		
		List<GrouptaskAndStuSubmitDTO> list = 
			mapper.selectGrouptaskWithSubmitByEnrollId(lectureId, enrollId);
		
		log.info("결과 : {}", PrettyPrint.pretty(list));
		log.info("과제수 : {}", list.size());
	}

	@Test
	void 모든완료된시험과그거에대한특정학생의응시기록() {
		String lectureId = "LECT00000000001";
		String enrollId = "STENRL000000001";

		List<LctExamAndStuSubmitDTO> list = mapper.selectExamWithSubmitByStudent(lectureId, enrollId);

		log.info("결과 : {}", PrettyPrint.pretty(list));
	}

}
