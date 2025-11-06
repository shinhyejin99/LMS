package kr.or.jsu.mybatis.mapper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.LectureSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.db.StudentAndEnrollDTO;
import kr.or.jsu.classroom.dto.db.LectureWithWeekbyDTO;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LectureMapperTest {

	@Autowired
	LectureMapper mapper;
	
	@Autowired
	DatabaseCache cache;
	
	@Test
	void testNewPrfLct() {
		String professorNo = "20218020";
		
		List<LectureSimpleDTO> list = mapper.selectTeachingLectureList(professorNo);
		assertTrue(list.size() != 0);
		
		log.info("{} 교수가 담당중인 강의 목록 : {}", professorNo, PrettyPrint.pretty(list));
	}
	
	@Test
	void testLct() {
		String studentNo = "202500001";
		
		List<LectureSimpleDTO> list = mapper.selectEnrolledLectureList(studentNo);
		assertTrue(list.size() != 0);
		
		list.stream().forEach(a -> {
			LectureInfo lecture = a.getLectureInfo();
			lecture.setProfessorName(cache.getUserName(lecture.getProfessorNo()));
			SubjectInfo subject = a.getSubjectInfo();
			subject.setCompletionName(cache.getCodeName(subject.getCompletionCd()));
		});
		
		log.info("{} 학생이 수강중인 강의 건수 :{}\n"
				+ "목록 : {}", studentNo, list.size(), PrettyPrint.pretty(list));
	}
	
	@Test
	void testLctSchedule() {
		String studentNo = "202500001";
		
		List<LectureSimpleDTO> list = mapper.selectEnrolledLectureList(studentNo);
		assertTrue(list.size() != 0);
		
		list.stream().forEach(a -> {
			LectureInfo lecture = a.getLectureInfo();
			lecture.setProfessorName(cache.getUserName(lecture.getProfessorNo()));
			SubjectInfo subject = a.getSubjectInfo();
			subject.setCompletionName(cache.getCodeName(subject.getCompletionCd()));
		});
		
		Collection<String> ids = list.stream()
		        .map(d -> d.getLectureInfo().getLectureId())
		        .filter(Objects::nonNull)
		        .collect(Collectors.toCollection(LinkedHashSet::new));
		
		List<LectureWithScheduleDTO> schedules = mapper.selectScheduleListJson(ids);
		
		log.info("강의 스케쥴들 : {}", PrettyPrint.pretty(schedules));
		log.info("강의 스케쥴 리스트 크기 : {}", schedules.size());
	}
	
	@Test
	void 강의주차계획목록조회() {
		String lectureId = "LECT00000000001";
		
		LectureWithWeekbyDTO list = mapper.selectLectureWithWeekby(lectureId);
		log.info("강의 주차 수 : {},\n 주차 계획들 : {}", list.getWeekbyList().size(), PrettyPrint.pretty(list));
	}
	
	@Test
	void 강의주차계획이없는거갖고와보기() {
		String lectureId = "LECT90000000240";
		
		LectureWithWeekbyDTO list = mapper.selectLectureWithWeekby(lectureId);
		log.info("강의 주차 수 : {},\n 주차 계획들 : {}", list.getWeekbyList().size(), PrettyPrint.pretty(list));
	}
	
	@Test
	void 강의수강중인수강생만가져와보기() {
		String lectureId = "LECT00000000001";
		
		 List<StudentAndEnrollDTO> list = mapper.selectLectureWithStudent(lectureId, true);
		log.info("강의 수강생들 : {}", PrettyPrint.pretty(list));
		log.info("강의 수강생 수 : {}", list.size());
	}
	
	@Test
	void 강의수강하고취소한수강생만가져와보기() {
		String lectureId = "LECT00000000001";
		
		List<StudentAndEnrollDTO> list = mapper.selectLectureWithStudent(lectureId, false);
		log.info("강의 수강생들 : {}", PrettyPrint.pretty(list));
		log.info("강의 수강생 수 : {}", list.size());
	}
	
	@Test
	void testSelectOne() {
		String lectureId = "LECT00000000001";
		LectureSimpleDTO lct = mapper.selectLecture(lectureId);
		
		log.info("강의정보 : {}", PrettyPrint.pretty(lct));
	}
	
	@Test
	void 없는강의가져와보기() {
		String lectureId = "DONOTEXIST";
		LectureSimpleDTO lct = mapper.selectLecture(lectureId);
		assertNull(lct);		
	}
	
	@Test
	void 교수의강의시간표갖고오기() {
		String yeartermCd = "2026_REG1";
		String professorNo = "20230001";
		List<LectureScheduleResp> list = mapper.selectPrfLectureSchedule(yeartermCd, professorNo);
		log.info("{}", PrettyPrint.pretty(list));
	}
	
	@Test
	void 강의신청서대로강의개설해보기() {
		String approveId = "APPR00000000713";
		int maxCap = 10000;
		var lct = new LectureInfo();
		lct.setMaxCap(maxCap);
		
		int cnt = mapper.insertLectureWithApply(approveId, lct);
		
		
		
		assertTrue(cnt == 1);
		
		String newLectureId = lct.getLectureId();
		
		log.info("만들어진 강의 기본키 : {}", newLectureId);
		
		mapper.insertLctWeekbyWithApply(approveId, newLectureId);
		
		mapper.insertLctGraderatioWithApply(approveId, newLectureId);
		
		var lecture = mapper.selectLecture(newLectureId);
		
		log.info("만들어진 강의 : {}", PrettyPrint.pretty(lecture));
		
		var withWeekby = mapper.selectLectureWithWeekby(newLectureId);
		var graderatio = mapper.selectGraderatioList(newLectureId);
		
		log.info("만들어진 강의주차 : {}", PrettyPrint.pretty(withWeekby));
		log.info("만들어진 성적산출비 : {}", PrettyPrint.pretty(graderatio));
	}
	
	@Test
	void 시수대로시간표배정안된강의가져와봐() {
		String yeartermCd = "2026_REG1";
		
		var result = mapper.selectUnassignedLectureList(yeartermCd);
		
		log.info("result : {}", PrettyPrint.pretty(result));	
	}
	
	@Test
	void 특정강의들의시간표가져와볼래() {
		var list = List.of(
			"LECT00000000001"
			, "LECT00000000002"
			, "LECT00000000003"
			, "LECT00000000004"
		);
				
		var result = mapper.selectScheduleListJson(list);
		
		log.info("result : {}", PrettyPrint.pretty(result));
	}
	
	
}
