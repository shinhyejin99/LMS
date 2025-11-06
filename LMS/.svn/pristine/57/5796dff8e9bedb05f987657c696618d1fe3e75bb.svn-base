package kr.or.jsu.classroom.professor.service;

import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class ClassroomProfessorServiceImplTest {

	@Autowired
	ClassroomProfessorService cpService;

	@Autowired
	ClassPrfBoardService boardService;

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
		List<LectureLabelResp> list = cpService.readMyLectureList();
		log.info("값 : {}", PrettyPrint.pretty(list));
	}

	@Test
	void 교수가강의게시글열람하는테스트() {
		List<LctPostLabelResp_PRF> list = boardService.readPostList("LECT00000000001", "20230001", null);
		log.info("{}", PrettyPrint.pretty(list));
	}

	@Test
	void 교수가강의게시글열람하는테스트_다른교수가시도() {
		assertThrows(RuntimeException.class, () -> {
			List<LctPostLabelResp_PRF> list = boardService.readPostList("LECT00000000001", "20230002", null);
			log.info("{}", PrettyPrint.pretty(list));
		});
	}
}