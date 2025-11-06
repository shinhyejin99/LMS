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
class ExamSubmitMapperTest {

	@Autowired
	ExamSubmitMapper mapper;
	
//	@Test
	void testInsertExamSubmit() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectExamSubmitList() {
//		assertNotNull(mapper.selectExamQuestionList());
//		log.info("====> 조회결과 {}", mapper.selectExamQuestionList());
		assertDoesNotThrow(() -> mapper.selectExamSubmitList());
		assertEquals(0, (mapper.selectExamSubmitList()).size());
	}

//	@Test
	void testUpdateExamSubmit() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteExamSubmit() {
		fail("Not yet implemented");
	}

}
