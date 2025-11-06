package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Transactional // 테스트 후 자동 롤백
@SpringBootTest // 스프링 컨텍스트 로드
@Slf4j
class StuEnrollLctMapperTest {

	@Autowired // Mapper 주입
	StuEnrollLctMapper mapper;
	
	@Test
	void 강의수강중인학생목록_모두() {
		List<StuEnrollLctInfo> list = mapper.selectAllStudentList("LECT00000000001", null);
		
		log.info("수강생 수 : {}", list.size());
		
		list.forEach(l -> log.info("학생 : {}", l)); 
		
		System.out.println(list.get(39).getEnrollStatusCd());
		
	}
	
	@Test
	void 강의수강중인학생목록_수강중수강완료만() {
		List<StuEnrollLctInfo> list = mapper.selectAllStudentList("LECT00000000001", true);
		
		log.info("수강생 수 : {}", list.size());

		list.forEach(l -> log.info("학생 : {}", l));
	}
	
	@Test
	void 강의수강중인학생목록_포기한놈만() {
		List<StuEnrollLctInfo> list = mapper.selectAllStudentList("LECT00000000001", false);
		
		log.info("수강생 수 : {}", list.size());
		
		list.forEach(l -> log.info("학생 : {}", l));
	}
	
	@Test
	void 이사람의수강정보는() {
		String lectureId = "LECT00000000001";
		String studentNo = "202500001";
		EnrollSimpleDTO stu = mapper.selectEnrollingStudent(lectureId, studentNo);
		
		log.info("수강생 : {}", PrettyPrint.pretty(stu));
	}
	
	@Test
	void 강의출석회차에누가출석했니() {
		String lectureId = "LECT00000000001";
		int round = 1;
		List<EnrollSimpleDTO> result = mapper.selectAttendanceRecords(lectureId, round);
		
		log.info("수강생 수 : {}", result.size());
		
		result.forEach(l -> log.info("{}", PrettyPrint.pretty(l)));
	}
	
	
}