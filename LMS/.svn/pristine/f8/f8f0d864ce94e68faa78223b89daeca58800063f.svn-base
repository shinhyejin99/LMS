package kr.or.jsu.lms.staff.service.student;

// JUnit 5 (Jupiter) Assertions으로 통일
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; // DI 해결을 위해 추가
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.StudentDetailDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class StaffStudentInfoServiceTest {

	@Autowired
	private StaffStudentInfoService service;

	@Autowired // 💡 비밀번호 검증을 위해 주입
	private PasswordEncoder passwordEncoder;

	private StudentDetailDTO createNewStudentDTO(String gradYear) {
		StudentDetailDTO dto = new StudentDetailDTO();

		dto.setFirstName("길동");
		dto.setLastName("홍");
		dto.setRegiNo("9512121000000"); // 초기 비밀번호 "951212"
		dto.setMobileNo("010-1111-2222");
		dto.setEmail("honggd@test.com");

		// **STUDENT 테이블 및 USERS 테이블 공통 필드**
		dto.setUnivDeptCd("DEP-ENGI-CSE");
		dto.setGradeCd("1ST");
		dto.setStuStatusCd("ENROLLED");
		dto.setProfessorId("20220001");
		dto.setEngLname("HONG");
		dto.setEngFname("GILDONG");

		// 보호자 정보
		dto.setGuardName("홍아버지");
		dto.setGuardRelation("REL01");
		dto.setGuardPhone("010-3333-4444");

		// **STU_ENTRANCE 테이블**
		dto.setEntranceTypeCd("ENTR01");
		dto.setGradHighschool("JS고등학교");
		dto.setGradYear(gradYear); // 졸업 연도 (사용되지 않을 수도 있지만 일단 유지)
		dto.setGradExamYn("N");
		dto.setTargetDept("DEP-ENGI-CSE");

		// ⭐️ 핵심 수정 1: 입학일(entranceDate) 정보 추가 (이전 오류 해결) ⭐️
		dto.setEntranceDate(gradYear + "-03-02");

		// ⭐️ 핵심 수정 2: 주소 정보 추가 (ORA-01400 오류 해결) ⭐️
		// Service 로직이 Address 테이블에 INSERT를 시도할 때 BASE_ADDR은 NOT NULL이므로 반드시 필요합니다.
		dto.setBaseAddr("대전광역시 유성구");
		dto.setDetailAddr("관평동 한밭대로 500");
		dto.setZipCode("34000");

		// 기타 외래 키 필드
		dto.setCollegeCd("COL01");
		dto.setPhotoId("PHO001");
		dto.setAddrId("ADDR001"); // AddressMapper가 새 주소를 삽입하는 경우 이 값은 무시될 수 있습니다.
		dto.setBankCode("BANK_NH");
		dto.setBankAccount("1234567890");

		return dto;
	}

	@Test
	void testCreateStaffStudentInfo() {
	    final String TEST_ENTRANCE_YEAR = "2025";
	    final String EXPECTED_INITIAL_PW = "951212";

	    // 1. Arrange: 유효한 학생 DTO 준비
	    StudentDetailDTO newStudent = createNewStudentDTO(TEST_ENTRANCE_YEAR);

	    // 2. Act: 등록 서비스 실행
	    // 예외 발생하지 않아야 함 (리턴값 없음)
	    assertDoesNotThrow(() -> service.createStaffStudentInfo(newStudent),
	        "학생 등록 서비스 실행 시 외래 키 데이터 문제로 예외가 발생해서는 안 됩니다.");

	    // 3. Assert: 등록 성공 및 데이터 검증
	    String generatedStudentNo = newStudent.getStudentNo();
	    assertNotNull(generatedStudentNo, "등록 후 DTO에 학번이 설정되어야 합니다.");
	    assertEquals(9, generatedStudentNo.length(), "학번 길이는 9자리여야 합니다.");
	    assertTrue(generatedStudentNo.startsWith(TEST_ENTRANCE_YEAR),
	            "학번이 입학 연도(" + TEST_ENTRANCE_YEAR + ")로 시작해야 합니다.");

	    log.info("✅ 새로 생성된 학번: {}", generatedStudentNo);

	    StudentDetailDTO createdStudent = service.readStaffStudentInfo(generatedStudentNo);

	    assertNotNull(createdStudent, "새로 생성된 학번으로 학생 정보를 조회할 수 있어야 합니다.");
	    assertEquals("남자", createdStudent.getGender(), "주민번호에 따라 성별이 'M'으로 설정되어야 합니다.");

	    String dbHashedPassword = createdStudent.getPwHash();
	    boolean isPasswordMatch = passwordEncoder.matches(EXPECTED_INITIAL_PW, dbHashedPassword);
	    assertTrue(isPasswordMatch, "초기 비밀번호(" + EXPECTED_INITIAL_PW + ")가 정확히 암호화되어 저장되었어야 합니다.");
	}
	@Test
	void testReadStaffStudentInfo() {
		String studentNo = "202591000";

		StudentDetailDTO studentDTO = assertDoesNotThrow(() -> service.readStaffStudentInfo(studentNo),
				"유효한 학번 조회 시 예외가 발생해서는 안 됩니다.");

		// 2. 결과 검증
		assertNotNull(studentDTO, "조회된 학생 정보 DTO는 NULL이 아니어야 합니다.");

		// 3. 전체 정보 로그 출력 🚀
		log.info("=========================================================");
		log.info("✅ 조회된 학생 전체 상세 정보 (학번: {}):", studentNo);
		log.info(">> StudentDetailDTO 전체: {}", studentDTO);

		assertEquals(studentNo, studentDTO.getStudentNo(), "조회된 학생의 학번이 요청한 학번과 일치해야 합니다.");
	}

	@Test
	void testModifyStaffStudentInfo() {
		final String studentNo = "202500001";
		final String newMobile = "01099999999";

		// 1. Arrange: 기존 데이터 조회
		StudentDetailDTO originalDTO = service.readStaffStudentInfo(studentNo);
		assertNotNull(originalDTO, "수정 전 학생 DTO는 존재해야 합니다.");

		// 2. Arrange: 수정할 DTO 생성
		StudentDetailDTO updateDTO = new StudentDetailDTO();
		updateDTO.setStudentNo(originalDTO.getStudentNo());
		updateDTO.setMobileNo(newMobile);

		// 필수적으로 null이 아니어야 할 필드들 설정 (예시)
		updateDTO.setGuardName(originalDTO.getGuardName());
		updateDTO.setGuardRelation(originalDTO.getGuardRelation());
		updateDTO.setGuardPhone(originalDTO.getGuardPhone());
		updateDTO.setGradeCd(originalDTO.getGradeCd());
		updateDTO.setStuStatusCd(originalDTO.getStuStatusCd());
		updateDTO.setUnivDeptCd(originalDTO.getUnivDeptCd());
		updateDTO.setProfessorId(originalDTO.getProfessorId());
		updateDTO.setCollegeCd(originalDTO.getCollegeCd());
		updateDTO.setCollegeName(originalDTO.getCollegeName());
		updateDTO.setPhotoId(originalDTO.getPhotoId());
		updateDTO.setBaseAddr(originalDTO.getBaseAddr());
		updateDTO.setDetailAddr(originalDTO.getDetailAddr());
		updateDTO.setZipCode(originalDTO.getZipCode());
		updateDTO.setEmail(originalDTO.getEmail());
		updateDTO.setBankCode(originalDTO.getBankCode());
		updateDTO.setBankName(originalDTO.getBankName());
		updateDTO.setBankAccount(originalDTO.getBankAccount());



		StudentDetailDTO afterDTO = service.readStaffStudentInfo(studentNo);
		assertNotNull(afterDTO, "수정 후 재조회된 DTO는 null이 아니어야 합니다.");
		assertEquals(newMobile, afterDTO.getMobileNo(), "휴대전화 번호가 정확히 변경되어야 합니다.");
	}
}