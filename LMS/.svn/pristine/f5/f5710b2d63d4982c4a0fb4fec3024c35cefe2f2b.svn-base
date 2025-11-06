package kr.or.jsu.mybatis.mapper;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.info.LctAttRoundInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LctAttendanceMapperTest {

	@Autowired
	LctAttendanceMapper attMapper;
	
	@Autowired // Mapper 주입
	StuEnrollLctMapper enrollMapper;


	@Test
	void 출석회차생성후기본데이터넣어보기() {
		String lectureId = "LECT00000000001";
		
		LctAttRoundInfo attRound = new LctAttRoundInfo();
		attRound.setLectureId(lectureId);
		
		int rowCnt1 = attMapper.insertLctAttround(attRound);
		assertTrue(rowCnt1 != 0);
		
		int newRound = attRound.getLctRound();
		log.info("생성된 회차 : {}", newRound);
		
		List<StuEnrollLctInfo> list = enrollMapper.selectAllStudentList(lectureId, true);
		assertTrue(list.size() == 40); // 수강생은 40명임
				
		List<String> enrollIds = list.stream().map(stu -> {
			return stu.getEnrollId();
		}).collect(Collectors.toList());
		
		int rowCnt2 = attMapper.insertDefaultAttendanceRecords(lectureId, newRound, enrollIds, "ATTD_TBD");
		assertTrue(rowCnt2 == 40); // 40데이터 넣었어야 함
		
		List<EnrollSimpleDTO> students = enrollMapper.selectAttendanceRecords(lectureId, newRound);
				
		
		assertTrue(students.size() == 40); // 수강생은 40명임
		
		log.info("40번째 수강생 정보 : {}", PrettyPrint.pretty(students.get(39)));
		
	}

}
