package kr.or.jsu.classroom.professor.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class ClassPrfTaskServiceImplTest {
	
	@BeforeEach
	void 가짜교수넣기() {
		CustomUserDetails fakeUser = new CustomUserDetails(
				new UsersVO("USER00000000008", "20230001", "ROLE_PROFESSOR", false, "", "", "", "", "", "", "", "", "", "", null)
				);

		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
