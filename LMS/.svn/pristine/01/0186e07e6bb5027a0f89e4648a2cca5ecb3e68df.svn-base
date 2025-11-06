package kr.or.jsu.aiDummyDataGenerator.step5_student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.StuEntranceVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class StudentDummyGeneratorTest {

	private static final int[] TARGET_YEARS = { 25, 24, 23, 22, 21, 20, 19 };
	private static final int STUDENTS_PER_YEAR = 2000;
	private static final int PROFESSORS_PER_YEAR = 500;

	private static final List<String> PROFESSOR_DEPARTMENTS = Arrays.asList(
			"DEPT-GENR",
			"DEPT-HUMN-COM", "DEPT-SOCS-COM", "DEPT-NATS-COM", "DEPT-ENGR-COM", "DEPT-EDUC-COM", "DEPT-ARTS-COM",
			"DEPT-KORE", "DEPT-ENGL", "DEPT-HIST",
			"DEPT-ECON", "DEPT-POLS", "DEPT-PSYC",
			"DEPT-MATH", "DEPT-PHYS", "DEPT-CHEM", "DEPT-BIOS",
			"DEPT-CIVL", "DEPT-MECH", "DEPT-ELIT", "DEPT-COMP",
			"DEPT-EDUC", "DEPT-CHED", "DEPT-SPEC",
			"DEPT-MUSI", "DEPT-FINE", "DEPT-PEPE");

	private static final List<String> STUDENT_DEPARTMENTS = Arrays.asList(
			"DEPT-KORE", "DEPT-ENGL", "DEPT-HIST",
			"DEPT-ECON", "DEPT-POLS", "DEPT-PSYC",
			"DEPT-MATH", "DEPT-PHYS", "DEPT-CHEM", "DEPT-BIOS",
			"DEPT-CIVL", "DEPT-MECH", "DEPT-ELIT", "DEPT-COMP",
			"DEPT-EDUC", "DEPT-CHED", "DEPT-SPEC",
			"DEPT-MUSI", "DEPT-FINE", "DEPT-PEPE",
			"DEPT-GENR");

	private static final String[] ENGLISH_SURNAMES = {
			"KIM", "LEE", "PARK", "CHOI", "JUNG", "KANG", "CHO", "YOON", "JANG", "LIM",
			"HAN", "OH", "SEO", "SHIN", "KWON", "HWANG", "YANG", "JEON", "HONG", "NA"
	};

	private static final String[] ENGLISH_GIVEN_NAMES = {
			"JIWON", "SEOUNGYEON", "MINJAE", "JIMIN", "HAYEON", "SOYOUNG", "YERIN", "JUNSEO", "SIWOO", "HANEUL",
			"DOYUN", "JISOO", "GARAM", "JIHO", "HANA", "YUNA", "SEOHYUN", "JUNGHOO", "MINSEO", "SEUNGAH"
	};

	private static final String[] GUARDIAN_FAMILY_NAMES = { "김", "이", "박", "최", "정", "조", "윤", "장", "임", "강" };
	private static final String[] GUARDIAN_GIVEN_NAMES = { "철수", "영희", "미영", "성호", "진수", "선영", "명자", "동수", "정자", "상철" };
	private static final String[] GUARDIAN_RELATIONS = { "부", "모", "형", "누나", "오빠", "언니" };

	private static final String[] ENTRANCE_TYPES = { "SU-GC", "SU-GJ", "SU-NS", "JE-SN", "JE-SK", "SU-RB", "SP-EQ" };
	private static final String[] HIGH_SCHOOLS = {
			"서울고등학교", "한빛고등학교", "다온여자고등학교", "푸른과학고등학교", "정화예술고등학교", "누리국제고등학교"
	};

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertStudents() {
		Map<String, List<String>> professorsByDept = buildProfessorDirectory();
		Map<String, Integer> professorCursor = new HashMap<>();

		int totalStudents = 0;
		int deptCursor = 0;

		for (int year : TARGET_YEARS) {
			for (int seq = 0; seq < STUDENTS_PER_YEAR; seq++) {
				String userId = String.format("USER1%02d%08d", year, seq + 1);
				String studentNo = String.format("%04d7%04d", 2000 + year, seq + 1);
				String deptCode = STUDENT_DEPARTMENTS.get(deptCursor % STUDENT_DEPARTMENTS.size());
				deptCursor++;

				String gradeCd = gradeCodeForYear(year);
				String statusCd = statusCodeForYear(year, seq);

				int nameIndex = totalStudents;
				String engLast = ENGLISH_SURNAMES[nameIndex % ENGLISH_SURNAMES.length];
				String engFirst = ENGLISH_GIVEN_NAMES[nameIndex % ENGLISH_GIVEN_NAMES.length];

				ThreadLocalRandom random = ThreadLocalRandom.current();
				String guardName = GUARDIAN_FAMILY_NAMES[random.nextInt(GUARDIAN_FAMILY_NAMES.length)]
						+ GUARDIAN_GIVEN_NAMES[random.nextInt(GUARDIAN_GIVEN_NAMES.length)];
				String guardRelation = GUARDIAN_RELATIONS[random.nextInt(GUARDIAN_RELATIONS.length)];
				String guardPhone = String.format("010-%04d-%04d", random.nextInt(1000, 10000),
						random.nextInt(1000, 10000));

				String professorId = nextAdvisorForDepartment(professorsByDept, professorCursor, deptCode);

				StudentVO student = new StudentVO();
				student.setStudentNo(studentNo);
				student.setUserId(userId);
				student.setUnivDeptCd(deptCode);
				student.setGradeCd(gradeCd);
				student.setStuStatusCd(statusCd);
				student.setEngLname(engLast.toUpperCase(Locale.ROOT));
				student.setEngFname(engFirst.toUpperCase(Locale.ROOT));
				student.setGuardName(guardName);
				student.setGuardRelation(guardRelation);
				student.setGuardPhone(guardPhone);
				student.setProfessorId(professorId);

				dummyDataMapper.insertOneDummyStudent(student);

				StuEntranceVO entrance = buildEntranceInfo(studentNo, year, deptCode, random);
				dummyDataMapper.insertOneDummyStuEntrance(entrance);

				totalStudents++;
			}
			log.info("Inserted students for 20{} cohort: cumulative {}", year, totalStudents);
		}

		log.info("Total student rows inserted: {}", totalStudents);
	}

	private StuEntranceVO buildEntranceInfo(String studentNo, int intakeYear, String deptCode,
			ThreadLocalRandom random) {
		int realYear = 2000 + intakeYear;
		String entranceType = ENTRANCE_TYPES[random.nextInt(ENTRANCE_TYPES.length)];
		String gradYear = String.format("%04d", realYear - 1);
		String gradHighschool = HIGH_SCHOOLS[random.nextInt(HIGH_SCHOOLS.length)];

		String examYn = switch (entranceType) {
			case "JE-SN", "JE-SK", "SU-NS" -> "Y";
			default -> "N";
		};

		StuEntranceVO vo = new StuEntranceVO();
		vo.setStudentNo(studentNo);
		vo.setEntranceTypeCd(entranceType);
		vo.setGradHighschool(gradHighschool);
		vo.setGradYear(gradYear);
		vo.setGradExamYn(examYn);
		vo.setTargetDept(deptCode);
		return vo;
	}

	private Map<String, List<String>> buildProfessorDirectory() {
		Map<String, List<String>> directory = new HashMap<>();
		int deptCursor = 0;

		for (int year : TARGET_YEARS) {
			for (int seq = 1; seq <= PROFESSORS_PER_YEAR; seq++) {
				String deptCode = PROFESSOR_DEPARTMENTS.get(deptCursor % PROFESSOR_DEPARTMENTS.size());
				deptCursor++;

				String professorNo = String.format("%04d7%03d", 2000 + year, seq);
				directory.computeIfAbsent(deptCode, key -> new ArrayList<>()).add(professorNo);
			}
		}

		return directory;
	}

	private String nextAdvisorForDepartment(Map<String, List<String>> professorsByDept,
			Map<String, Integer> professorCursor, String deptCode) {
		List<String> list = professorsByDept.get(deptCode);
		if (list == null || list.isEmpty()) {
			list = professorsByDept.get("DEPT-GENR");
		}

		int index = professorCursor.merge(deptCode, 1, (oldVal, one) -> oldVal + 1);
		return list.get((index - 1) % list.size());
	}

	private String gradeCodeForYear(int year) {
		return switch (year) {
			case 25 -> "1ST";
			case 24 -> "2ND";
			case 23 -> "3RD";
			default -> "4TH";
		};
	}

	private String statusCodeForYear(int year, int sequence) {
		return switch (year) {
			case 25, 24, 23, 22 -> "ENROLLED";
			case 21 -> sequence % 9 == 0 ? "ON_LEAVE" : "ENROLLED";
			case 20 -> sequence % 7 == 0 ? "DEFERRED" : "ENROLLED";
			default -> "GRAD";
		};
	}
}
