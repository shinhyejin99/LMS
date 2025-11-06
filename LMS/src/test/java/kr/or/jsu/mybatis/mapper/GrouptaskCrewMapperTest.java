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
class GrouptaskCrewMapperTest {

	@Autowired
	GrouptaskCrewMapper mapper;
	
//	@Test
	void testInsertGrouptaskCrew() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectGrouptaskCrewList() {
//		assertNotNull(mapper.selectExamQuestionList());
//		log.info("====> 조회결과 {}", mapper.selectExamQuestionList());
		assertDoesNotThrow(() -> mapper.selectGrouptaskCrewList());
		assertEquals(0, (mapper.selectGrouptaskCrewList()).size());
	}

//	@Test
	void testUpdateGrouptaskCrew() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteGrouptaskCrew() {
		fail("Not yet implemented");
	}

}
