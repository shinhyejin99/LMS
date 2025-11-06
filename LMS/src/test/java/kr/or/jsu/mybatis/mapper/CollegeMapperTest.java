package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class CollegeMapperTest {

	@Autowired
	CollegeMapper mapper;
	
	@Test
	void testInsertCollege() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectCollegeList() {
	}

	@Test
	void testUpdateCollege() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteCollege() {
		fail("Not yet implemented");
	}

}
