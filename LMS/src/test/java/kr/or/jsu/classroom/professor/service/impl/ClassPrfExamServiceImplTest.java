package kr.or.jsu.classroom.professor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.or.jsu.classroom.dto.request.ExamWeightValueModReq;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.professor.service.ClassPrfExamService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class ClassPrfExamServiceImplTest {

	@Autowired
	ClassPrfExamService service;

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
	void 가중치일괄수정_성공버전() {
		String lectureId = "LECT00000000001";

		List<ExamWeightValueModReq> wVs = new ArrayList<>();

		ExamWeightValueModReq a = new ExamWeightValueModReq();
		a.setLctExamId("LCTEXM000000001");
		a.setWeightValue(0);

		ExamWeightValueModReq b = new ExamWeightValueModReq();
		b.setLctExamId("LCTEXM000000002");
		b.setWeightValue(10);

		ExamWeightValueModReq c = new ExamWeightValueModReq();
		c.setLctExamId("LCTEXM000000003");
		c.setWeightValue(20);

		ExamWeightValueModReq d = new ExamWeightValueModReq();
		d.setLctExamId("LCTEXM000000004");
		d.setWeightValue(30);

		ExamWeightValueModReq e = new ExamWeightValueModReq();
		e.setLctExamId("LCTEXM000000005");
		e.setWeightValue(40);

		wVs.add(a);
		wVs.add(b);
		wVs.add(c);
		wVs.add(d);
		wVs.add(e);

		service.modifyAllWeightValues(lectureId, wVs);
	}

	@Test
	void 가중치일괄수정_일부만입력_성공버전() {
		String lectureId = "LECT00000000001";

		List<ExamWeightValueModReq> wVs = new ArrayList<>();

		ExamWeightValueModReq d = new ExamWeightValueModReq();
		d.setLctExamId("LCTEXM000000004");
		d.setWeightValue(1);

		ExamWeightValueModReq e = new ExamWeightValueModReq();
		e.setLctExamId("LCTEXM000000005");
		e.setWeightValue(69);

		wVs.add(d);
		wVs.add(e);

		service.modifyAllWeightValues(lectureId, wVs);
	}

	@Test
	void 가중치일괄수정_다른시험넣어보기() {
		String lectureId = "LECT00000000001";

		List<ExamWeightValueModReq> wVs = new ArrayList<>();

		ExamWeightValueModReq a = new ExamWeightValueModReq();
		a.setLctExamId("LCTEXM000000011");
		a.setWeightValue(0);

		wVs.add(a);

		service.modifyAllWeightValues(lectureId, wVs);
	}

	@Test
	void 가중치일괄수정_null넣어보기() {
		String lectureId = "LECT00000000001";

		List<ExamWeightValueModReq> wVs = new ArrayList<>();

		ExamWeightValueModReq a = new ExamWeightValueModReq();
		a.setLctExamId("LCTEXM000000001");
		a.setWeightValue(null);

		ExamWeightValueModReq b = new ExamWeightValueModReq();
		b.setLctExamId("LCTEXM000000002");
		b.setWeightValue(null);

		wVs.add(a);
		wVs.add(b);

		service.modifyAllWeightValues(lectureId, wVs);
	}

	@Test
	void 수강자전원과특정시험에대한제출() {
		String lectureId = "LECT00000000001";
		String lctExamId = "LCTEXM000000004";

		ExamAndEachStudentsSubmitResp result = service.readExamDetail(lectureId, lctExamId);

		log.info("결과 : {}", PrettyPrint.pretty(result));
	}
}