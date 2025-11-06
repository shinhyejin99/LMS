package kr.or.jsu.mybatis.mapper.classroom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class ClassroomEnderMapperTest {
	
	@Autowired
	ClassroomEnderMapper mapper;

	@Test
	void 강의진행도_과제가중치_시험가중치확인() {
		String lectureId = "LECT00000000001";
		
		var a = mapper.getProgress(lectureId);
		var b = mapper.getExamStatus(lectureId);
		var c = mapper.getTaskStatus(lectureId);
		
		log.info("{}", PrettyPrint.pretty(a));
		log.info("{}", PrettyPrint.pretty(b));
		log.info("{}", PrettyPrint.pretty(c));
	}
	
	@Test
	void 난학생인데내가강의평가해야할강의() {
		String studentNo = "202500001";
		var result = mapper.selectReviewNeededLectureList(studentNo);
		
		log.info("{}", PrettyPrint.pretty(result));
	}
	
	@Test
	void 교수가자기학생들성적과항목별성적조회() {
		String lectureId = "LECT00000000001";
		
		var a = mapper.selectStudentAndScoreList(lectureId);
		
		log.info("{}", PrettyPrint.pretty(a));
	}

}
