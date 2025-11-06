package kr.or.jsu.mybatis.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class AuthAccountMapperTest {

	@Autowired
	AuthAccountMapper mapper;

	@Test
	void testSelectStudentUserForAuth() {
		UsersVO student = mapper.selectUserForAuth("202500001");
		log.info("{}", student);

	}

	@Test
	void testSelectProfessorUserForAuth() {
		UsersVO professor = mapper.selectUserForAuth("20240005");
		log.info("{}", professor);
	}

	@Test
	void testSelectStaffUserForAuth() {
		UsersVO staff = mapper.selectUserForAuth("2024003");
		log.info("{}", staff);
	}

}
