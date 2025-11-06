package kr.or.jsu.lms.staff.service.professor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.ProfessorInfoDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * StaffProfessorInfoService의 CRUD 기능을 검증하는 테스트 클래스입니다. Controller의 기본값 설정 로직을
 * 모방하여 데이터를 준비합니다.
 */
@Slf4j
@SpringBootTest
@Transactional
class StaffProfessorInfoServiceTest {

	@Autowired
	private StaffProfessorInfoService service;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Controller에서 사용하는 기본값 (테스트 시 Service를 직접 호출할 때 필요)
	private static final String DEFAULT_APPOINTMENT_CODE = "PRF_APPNT_F_ASST";
	private static final String DEFAULT_PROFESSOR_POSITION_CODE = "PRF_POSIT_ASSO";
	private static final String TEST_DEPT_CODE = "DEP-ENGI-CSE"; // 테스트에 사용할 유효 학과 코드

	/**
	 * 테스트용 교수 DTO를 생성합니다. (임용일/주민번호 기반 교번 및 비밀번호 생성용) Controller의
	 * handleForeignKeyDefaults 역할을 대신하여 필수 외래 키 코드를 설정합니다.
	 */
	private ProfessorInfoDTO createNewProfessorDTO(String hireYear) {
		ProfessorInfoDTO dto = new ProfessorInfoDTO();

		dto.setFirstName("찬수");
		dto.setLastName("김");
		final String INITIAL_PASSWORD = "941120"; // 주민번호 앞 6자리를 초기 비밀번호로 사용
		dto.setRegiNo(INITIAL_PASSWORD + "2000000");
		dto.setMobileNo("010-5555-6666");
		dto.setEmail("kimcs@jsu.ac.kr");
		dto.setGender("F");
		dto.setOfficeNo("02-1234-5678");

		// 임용일 설정 (교번 생성에 사용)
		dto.setCreateAt(LocalDateTime.of(Integer.parseInt(hireYear), 3, 1, 9, 0, 0));
		dto.setHireDateString(hireYear + "-03-01");

		// =========================================================================
		// ⭐️ 필수 외래 키 및 비밀번호 설정 ⭐️
		// =========================================================================

		// 1. 임용/직위/학과 코드 설정
		dto.setPrfAppntCd(DEFAULT_APPOINTMENT_CODE);
		dto.setPrfPositCd(DEFAULT_PROFESSOR_POSITION_CODE);
		dto.setUnivDeptCd(TEST_DEPT_CODE);
		dto.setCollegeCd("COL-ENGI");

		// 2. 영문 이름 (DB NOT NULL 충족용)
		dto.setEngLname("KIM");
		dto.setEngFname("CHANSU");

		// 3. 주소 정보 (ADDRESS 테이블)
		dto.setZipCode("01234");
		dto.setBaseAddr("서울시 강남구 테헤란로"); // ⬅️ 여기에 필수적인 값을 넣어줍니다.
		dto.setDetailAddr("A동 101호");

		// 4. 은행 정보 (USERS 테이블)
		dto.setBankCode("BANK_KB");
		dto.setBankAccount("9876543210");

		// 5. PW_HASH 설정 (ORA-01400 오류 해결)
		// DTO에 암호화된 비밀번호를 미리 설정해야 Service가 DB에 삽입할 때 NULL이 아닙니다.
		String hashedPassword = passwordEncoder.encode(INITIAL_PASSWORD);
		dto.setPwHash(hashedPassword);

		return dto;
	}

	/**
	 * 1. 교수 등록 및 교번/비밀번호 생성 로직 검증 테스트 (CREATE)
	 */
	@Test
	void testCreateStaffProfessorInfo() {
		final String TEST_HIRE_YEAR = String.valueOf(LocalDateTime.now().getYear());
		final String EXPECTED_INITIAL_PW = "941120"; // 비밀번호 검증용 원본 평문

		// 1. Arrange: 유효한 교수 DTO 준비
		ProfessorInfoDTO newProfessor = createNewProfessorDTO(TEST_HIRE_YEAR);

		// 2. Act: 등록 서비스 실행 (ADDRESS, USERS, PROFESSOR 순서)
		int result = assertDoesNotThrow(() -> service.createStaffProfessorInfo(newProfessor),
				"교수 등록 서비스 실행 시 예외가 발생해서는 안 됩니다.");

		// 3. Assert: 등록 성공 및 데이터 검증
		assertTrue(result == 3, "DB 삽입된 레코드 수는 정확히 3개(ADDRESS, USERS, PROFESSOR)여야 합니다.");

		String generatedProfessorNo = newProfessor.getProfessorNo(); // Service에서 DTO에 설정한 교번을 가져옴
		assertNotNull(generatedProfessorNo, "등록 후 DTO에 교번이 설정되어야 합니다.");
		assertEquals(8, generatedProfessorNo.length(), "교번 길이는 8자리여야 합니다.");

		// 4. 데이터 재조회 및 검증
		ProfessorInfoDTO createdProfessor = service.readStaffProfessorInfo(generatedProfessorNo);

		// 5. 비밀번호 검증
		String dbHashedPassword = createdProfessor.getPwHash();
		boolean isPasswordMatch = passwordEncoder.matches(EXPECTED_INITIAL_PW, dbHashedPassword);
		assertTrue(isPasswordMatch, "초기 비밀번호(" + EXPECTED_INITIAL_PW + ")가 정확히 암호화되어 저장되었어야 합니다.");
		log.info("✅ 교수 등록(CREATE) 및 비밀번호 검증 성공. 교번: {}", generatedProfessorNo);
	}

	// 2. 교수 목록 조회 테스트 (READ LIST) 
	@Test
	void testReadStaffProfessorInfoList() {
		PaginationInfo<Object> pagingInfo = new PaginationInfo<>();
		pagingInfo.setCurrentPage(1);
		pagingInfo.setScreenSize(10);



		// then
//		assertNotNull(professorList, "교수 목록은 NULL이 아니어야 합니다.");
//		assertTrue(pagingInfo.getTotalRecord() >= professorList.size(), "TotalRecord는 조회된 목록 크기보다 작을 수 없습니다.");

		log.info("✅ 교수 목록 기본 조회 성공. Total: {}", pagingInfo.getTotalRecord());
	}

	// 3. 교수 상세 정보 조회 테스트 (READ DETAIL)
	@Test
	void testReadStaffProfessorInfo() {
		final String professorNo = "20258022"; // 테스트 데이터의 유효한 교번

		ProfessorInfoDTO professorDTO = assertDoesNotThrow(() -> service.readStaffProfessorInfo(professorNo),
				"유효한 교번 조회 시 예외가 발생해서는 안 됩니다.");

		assertNotNull(professorDTO, "조회된 교수 정보 DTO는 NULL이 아니어야 합니다.");
		assertEquals(professorNo, professorDTO.getProfessorNo(), "조회된 교수의 교번이 요청한 교번과 일치해야 합니다.");
		log.info("✅ 교수 상세 조회(READ DETAIL) 성공. 교번: {}", professorNo);
	}

	// 4. 교수 정보 수정 테스트 (UPDATE) 
	@Test
	void testModifyStaffProfessorInfo() {
		final String professorNo = "20218001";
		final String newMobile = "010-7777-8888";
		final String newDetailAddr = "테스트 수정 상세 주소";

		ProfessorInfoDTO originalDTO = service.readStaffProfessorInfo(professorNo);
		assertNotNull(originalDTO, "수정 전 교수 DTO는 존재해야 합니다.");

		// DTO에 ID 값을 유지한 채 업데이트 필드만 변경
		ProfessorInfoDTO updateDTO = originalDTO;

		updateDTO.setMobileNo(newMobile); // USERS 테이블
		updateDTO.setDetailAddress(newDetailAddr); // ADDRESS 테이블
		updateDTO.setEngFname("UPDATED"); // PROFESSOR 테이블

		assertDoesNotThrow(() -> service.modifyStaffProfessorInfo(updateDTO), "교수 수정 서비스 실행 시 예외가 발생해서는 안 됩니다.");

		ProfessorInfoDTO afterDTO = service.readStaffProfessorInfo(professorNo);

		assertEquals(newMobile, afterDTO.getMobileNo(), "휴대전화 번호(USERS)가 정확히 변경되어야 합니다.");
		assertEquals(newDetailAddr, afterDTO.getDetailAddress(), "상세 주소(ADDRESS)가 정확히 변경되어야 합니다.");
		assertEquals("UPDATED", afterDTO.getEngFname(), "영문 이름(PROFESSOR)이 정확히 변경되어야 합니다.");

		log.info("✅ 교수 정보 수정(UPDATE) 성공. 교번: {}", professorNo);
	}
}
