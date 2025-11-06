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
class GrouptaskGroupMapperTest {

	@Autowired
	GrouptaskGroupMapper mapper;
	
//	@Test
	void testInsertGroup() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectGroupList() {
//		assertNotNull(mapper.selectExamQuestionList());
//		log.info("====> 조회결과 {}", mapper.selectExamQuestionList());
		assertDoesNotThrow(() -> mapper.selectGroupList());
		assertEquals(0, (mapper.selectGroupList()).size());
	}

//	@Test
	void testSelectGroup() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdateGroup() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteGroupSoft() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteGroupHard() {
		fail("Not yet implemented");
	}

}
