package kr.or.jsu.aiDummyDataGenerator.faculty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ProfessorVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ProfessorDummyGeneratorTest {

	private static final int[] TARGET_YEARS = { 25, 24, 23, 22, 21, 20, 19 };
	private static final int PROFESSORS_PER_YEAR = 500;

	private static final List<String> DEPARTMENTS = Arrays.asList(
			"DEPT-GENR",
			"DEPT-HUMN-COM", "DEPT-SOCS-COM", "DEPT-NATS-COM", "DEPT-ENGR-COM", "DEPT-EDUC-COM", "DEPT-ARTS-COM",
			"DEPT-KORE", "DEPT-ENGL", "DEPT-HIST",
			"DEPT-ECON", "DEPT-POLS", "DEPT-PSYC",
			"DEPT-MATH", "DEPT-PHYS", "DEPT-CHEM", "DEPT-BIOS",
			"DEPT-CIVL", "DEPT-MECH", "DEPT-ELIT", "DEPT-COMP",
			"DEPT-EDUC", "DEPT-CHED", "DEPT-SPEC",
			"DEPT-MUSI", "DEPT-FINE", "DEPT-PEPE");

	private static final String[] ENGLISH_SURNAMES = {
			"KIM", "LEE", "PARK", "CHOI", "JUNG", "KANG", "CHO", "YOON", "JANG", "LIM",
			"HAN", "OH", "SEO", "SHIN", "KWON", "HWANG", "YANG", "JEON", "HONG", "NA"
	};

	private static final String[] ENGLISH_GIVEN_NAMES = {
			"YOUNGHO", "JAEWON", "SEUNGMIN", "DONGHYUN", "SANGHOON", "JONGSU", "JINWOO", "SUNGJIN", "JUNHO",
			"SEONGIL", "MYUNGSOO", "HYEONSU", "INPYO", "SANGMIN", "HYUNSEOK", "JINSEOK", "YONGMIN", "JAESEOK",
			"SEUNGJAE", "HOKYUN"
	};

	private static final String[] APPOINT_CODES = {
			"PRF_APPNT_F_PROF", "PRF_APPNT_F_ASSO", "PRF_APPNT_F_ASST", "PRF_APPNT_P_ADJ", "PRF_APPNT_P_LECT"
	};

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertProfessors() {
		int totalInserted = 0;
		int departmentCursor = 0;
		Map<String, Integer> perDepartmentCount = new HashMap<>();
		Map<String, Integer> positionTracker = new HashMap<>();

		for (int year : TARGET_YEARS) {
			for (int seq = 0; seq < PROFESSORS_PER_YEAR; seq++) {
				String userId = String.format("USER2%02d%08d", year, seq + 1);
				String professorNo = String.format("%04d7%03d", 2000 + year, seq + 1);

				String deptCode = DEPARTMENTS.get(departmentCursor % DEPARTMENTS.size());
				departmentCursor++;

				String engLast = ENGLISH_SURNAMES[totalInserted % ENGLISH_SURNAMES.length];
				String engFirst = ENGLISH_GIVEN_NAMES[totalInserted % ENGLISH_GIVEN_NAMES.length];
				String appointCd = APPOINT_CODES[totalInserted % APPOINT_CODES.length];
				int positionCount = positionTracker.merge(deptCode, 1, Integer::sum);
				String positionCd = switch (positionCount) {
					case 1 -> "PRF_POSIT_HEAD";
					case 2 -> "PRF_POSIT_ASSO";
					default -> "PRF_POSIT_PROF";
				};

				ProfessorVO professor = new ProfessorVO();
				professor.setProfessorNo(professorNo);
				professor.setUserId(userId);
				professor.setUnivDeptCd(deptCode);
				professor.setEngLname(engLast.toUpperCase(Locale.ROOT));
				professor.setEngFname(engFirst.toUpperCase(Locale.ROOT));
				professor.setPrfStatusCd("PRF_STATUS_ACTV");
				professor.setPrfAppntCd(appointCd);
				professor.setPrfPositCd(positionCd);

				dummyDataMapper.insertOneDummyProfessor(professor);

				perDepartmentCount.merge(deptCode, 1, Integer::sum);
				totalInserted++;
			}
		}

		log.info("Inserted professor rows: {}", totalInserted);
		perDepartmentCount.forEach(
				(dept, count) -> log.info("  - {} : {}", dept, count));
	}
}
