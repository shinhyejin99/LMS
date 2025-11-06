package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class ExamQuestionMapperTest {

	@Autowired
	ExamQuestionMapper mapper;
	
//	@Test
	void testInsertExamQuestion() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectExamQuestionList() {
//		assertNotNull(mapper.selectExamQuestionList());
//		log.info("====> 조회결과 {}", mapper.selectExamQuestionList());
		assertDoesNotThrow(() -> mapper.selectExamQuestionList());
		assertEquals(0, (mapper.selectExamQuestionList()).size());
	}

//	@Test
	void testUpdateExamQuestion() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteExamQuestion() {
		fail("Not yet implemented");
	}

}
