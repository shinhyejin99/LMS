package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 1: 교수 ?�용??USERS)?� 교수(PROFESSOR) ?�코?��? 1건씩 ?�성?�다.
 * ?�요??맞게 ?�수 값을 ?�정?????�행?�세??
 */
@SpringBootTest
@Slf4j
class Step1_ProfessorUserDummyGeneratorTest {

	// === 직접 ?�력???�요??�?===
	
	private static final String USER_PREFIX = "218";
	
	private static final String USER_ID = "USER" + USER_PREFIX + "99999999";
	private static final String PROFESSOR_NO = "2020" + USER_PREFIX + "1";
	private static final String FIRST_NAME = "도윤";
	private static final String LAST_NAME = "이";
	private static final String REGI_NO_PREFIX = "8210101";
	private static final String ENG_LNAME = "Lee";
	private static final String ENG_FNAME = "DoYoon";

	// === 고정??�?===
	private static final String ADDR_ID = "ADDR00000000001";
	private static final String BANK_CODE = "BANK_DG";
	private static final String BANK_ACCOUNT = "111111-3333333";
	private static final String UNIV_DEPT_CD = "DEP-ENGI-CSE";
	private static final String PRF_STATUS_CD = "PRF_STATUS_ACTV";
	private static final String PRF_APPNT_CD = "PRF_APPNT_F_PROF";
	private static final String PRF_POSIT_CD = null;
	private static final String OFFICE_NO = "042-7312-5432";
	private static final String PW_HASH = "{bcrypt}$2a$10$1vrhrg0sCry19KPz/pyJ5OmwYQZNw805uL9lYF8dFiVMWgCbodQwi";
	private static final LocalDateTime CREATE_AT = LocalDateTime.of(2020, 2, 5, 0, 0);

	@Autowired
	private DummyDataMapper dummyDataMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Test
	void insertProfessorUserAndProfessor() {
		insertProfessorUser();
		insertProfessor();
	}
	
	void insertProfessorUser() {
		UsersVO user = new UsersVO();
		user.setUserId(USER_ID);
		user.setPwHash(PW_HASH);
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setRegiNo(generateRegiNo(REGI_NO_PREFIX));
		user.setPhotoId("FILE99900000099");
		user.setAddrId(ADDR_ID);
		user.setMobileNo(generateMobile());
		user.setEmail(buildEmail(USER_ID));
		user.setBankCode(BANK_CODE);
		user.setBankAccount(BANK_ACCOUNT);
		user.setCreateAt(CREATE_AT);

		int inserted = dummyDataMapper.insertOneDummyUser(user);
		assertEquals(1, inserted, "?�용??1건이 ?�력?�어???�니??");
		log.info("USERS ?�코???�력 ?�료: {} (mobile={}, email={})", user.getUserId(), user.getMobileNo(),
				user.getEmail());
	}

	void insertProfessor() {
		ProfessorVO professor = new ProfessorVO();
		professor.setProfessorNo(PROFESSOR_NO);
		professor.setUserId(USER_ID);
		professor.setUnivDeptCd(UNIV_DEPT_CD);
		professor.setEngLname(ENG_LNAME);
		professor.setEngFname(ENG_FNAME);
		professor.setPrfStatusCd(PRF_STATUS_CD);
		professor.setPrfAppntCd(PRF_APPNT_CD);
		professor.setPrfPositCd(PRF_POSIT_CD);
		professor.setOfficeNo(OFFICE_NO);

		int inserted = dummyDataMapper.insertOneDummyProfessor(professor);
		assertEquals(1, inserted, "���� 1���� ??��??��????��??");
		log.info("PROFESSOR ??��????�� ??��: {} -> {}", professor.getProfessorNo(), professor.getUserId());

		assignProfessorAsDepartmentHead(professor.getProfessorNo());
	}

		

	private void assignProfessorAsDepartmentHead(String professorNo) {
		int cleared = jdbcTemplate.update(
			"UPDATE PROFESSOR SET PRF_POSIT_CD = NULL WHERE PRF_POSIT_CD = ? AND UNIV_DEPT_CD = ?",
			"PRF_POSIT_HEAD", UNIV_DEPT_CD);
		int assigned = jdbcTemplate.update(
			"UPDATE PROFESSOR SET PRF_POSIT_CD = ? WHERE PROFESSOR_NO = ?",
			"PRF_POSIT_HEAD", professorNo);
		assertEquals(1, assigned, "�а��� ������ ���� UPDATE�� 1���� ??��??");
		log.info("�а��� ���� ??��: cleared={}, assigned={}, professorNo={}", cleared, assigned, professorNo);
	}

	private String generateRegiNo(String prefix7Digits) {
		if (prefix7Digits == null || prefix7Digits.length() != 7 || !prefix7Digits.matches("\\d{7}")) {
			throw new IllegalArgumentException("REGI_NO ??7??��????�� 7??��??�� ??��??");
		}
		int suffix = ThreadLocalRandom.current().nextInt(0, 1_000_000);
		return prefix7Digits + String.format("%06d", suffix);
	}

	private String generateMobile() {
		int suffix = ThreadLocalRandom.current().nextInt(0, 100_000_000);
		return "010" + String.format("%08d", suffix);
	}

	private String buildEmail(String userId) {
		return userId.toLowerCase(Locale.ROOT) + "@professor.lms.ac.kr";
	}
}


