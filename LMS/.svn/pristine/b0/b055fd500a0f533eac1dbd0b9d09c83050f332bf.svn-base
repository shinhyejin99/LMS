package kr.or.jsu.lms.staff.service.subject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.vo.SubjectVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class StaffSubjectServiceTest {

	@Autowired
	StaffSubjectService service;

//	@Test
	void testCreateStaffSubject() {
		fail("Not yet implemented");
	}

	@Test
	void testReadStaffSubjectList() {
		List<Map<String, Object>> subjectList = service.readStaffSubjectList(null, null, null);
		log.info("교과목 전체: {}", subjectList);
	}

	@Test
	void testReadStaffSubject() {
		assertDoesNotThrow(() -> service.readStaffSubject("SUBJ00000000001"));
	}

	@Test
	void testModifyStaffSubject() {
		String subjectCd = "SUBJ00000000001";
		SubjectInfoDetailDTO subject = service.readStaffSubject(subjectCd);
		assertNotNull(subject);
		log.info("교과목 정보: {}", subject);

		subject.setSubjectName("컴퓨터네트워크");
		service.modifyStaffSubject(subject);

		SubjectInfoDetailDTO updatedStudent = service.readStaffSubject(subjectCd);
		assertNotNull(updatedStudent);
		log.info("교과목 정보: {}", updatedStudent);
	}

}
