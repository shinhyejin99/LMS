package kr.or.jsu.aiDummyDataGenerator.step4_professor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
		int departmentCursor = 0;
		int generatedCount = 0;

		Map<String, List<ProfessorCandidate>> candidatesByDept = new HashMap<>();

		for (int year : TARGET_YEARS) {
			for (int seq = 0; seq < PROFESSORS_PER_YEAR; seq++) {
				String userId = String.format("USER2%02d%08d", year, seq + 1);
				String professorNo = String.format("%04d7%03d", 2000 + year, seq + 1);

				String deptCode = DEPARTMENTS.get(departmentCursor % DEPARTMENTS.size());
				departmentCursor++;

				String engLast = ENGLISH_SURNAMES[generatedCount % ENGLISH_SURNAMES.length];
				String engFirst = ENGLISH_GIVEN_NAMES[generatedCount % ENGLISH_GIVEN_NAMES.length];
				String appointCd = APPOINT_CODES[generatedCount % APPOINT_CODES.length];
				int appointmentYear = 2000 + year;

				ProfessorVO professor = new ProfessorVO();
				professor.setProfessorNo(professorNo);
				professor.setUserId(userId);
				professor.setUnivDeptCd(deptCode);
				professor.setEngLname(engLast.toUpperCase(Locale.ROOT));
				professor.setEngFname(engFirst.toUpperCase(Locale.ROOT));
				professor.setPrfStatusCd("PRF_STATUS_ACTV");
				professor.setPrfAppntCd(appointCd);
				professor.setPrfPositCd("PRF_POSIT_PROF");

				candidatesByDept.computeIfAbsent(deptCode, key -> new ArrayList<>())
						.add(new ProfessorCandidate(professor, appointmentYear, professorNo));
				generatedCount++;
			}
		}

		int totalInserted = 0;
		Map<String, Integer> perDepartmentCount = new HashMap<>();

		for (String deptCode : DEPARTMENTS) {
			List<ProfessorCandidate> candidates = candidatesByDept.get(deptCode);
			if (candidates == null || candidates.isEmpty()) {
				continue;
			}

			candidates.sort(Comparator
					.comparingInt(ProfessorCandidate::appointmentYear)
					.thenComparing(ProfessorCandidate::professorNo));

			for (int i = 0; i < candidates.size(); i++) {
				ProfessorVO professor = candidates.get(i).professor();
				if (i == 0) {
					professor.setPrfPositCd("PRF_POSIT_HEAD");
				} else if (i == 1) {
					professor.setPrfPositCd("PRF_POSIT_ASSO");
				} else {
					professor.setPrfPositCd("PRF_POSIT_PROF");
				}

				dummyDataMapper.insertOneDummyProfessor(professor);
				perDepartmentCount.merge(deptCode, 1, Integer::sum);
				totalInserted++;
			}
		}

		log.info("Inserted professor rows: {}", totalInserted);
		perDepartmentCount.forEach((dept, count) -> log.info("  - {} : {}", dept, count));
	}

	private record ProfessorCandidate(ProfessorVO professor, int appointmentYear, String professorNo) {
	}
}
