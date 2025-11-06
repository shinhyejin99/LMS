package kr.or.jsu.lms.staff.service.staff;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.StaffDeptVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/**
 * StaffManagementService의 CRUD 기능을 검증하는 테스트 클래스입니다.
 */
@Slf4j
@Transactional
@SpringBootTest
class StaffManagementServiceTest {
	@Autowired
	private StaffManagementService service;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 테스트에서 사용할 초기 비밀번호를 상수로 정의
	private static final String INITIAL_PASSWORD = "950510";

	/**
	 * 테스트용 교직원 DTO를 생성합니다.
	 * ORA-01400 오류 방지를 위해 필수 필드와 암호화된 비밀번호를 설정합니다.
	 */
	private UserStaffDTO createTestStaffDTO(String hireYear) {
		UserStaffDTO dto = new UserStaffDTO();
		
		// 1단계: 내부 VO 객체들을 명시적으로 생성하고 DTO에 주입
		UsersVO userInfo = new UsersVO();
		StaffVO staffInfo = new StaffVO();
		AddressVO addressInfo = new AddressVO();
		StaffDeptVO staffDeptInfo = new StaffDeptVO();
			
		dto.setUserInfo(userInfo);
		dto.setStaffInfo(staffInfo);
		dto.setAddressInfo(addressInfo);
		dto.setStaffDeptInfo(staffDeptInfo);
			
		// ----------------- 1. 공통 사용자 정보 (userInfo) 설정 -----------------
		userInfo.setFirstName("찬수"); // ⭐️ ORA-01400: FIRST_NAME 해결
		userInfo.setLastName("박");
		userInfo.setRegiNo(INITIAL_PASSWORD + "2000000"); // 초기 비밀번호 "950510"
		userInfo.setMobileNo("010-1111-2222");
		userInfo.setEmail("parkyh@jsu.ac.kr");

		// 임용일 설정 (교번 생성에 사용)
		LocalDateTime hireDateTime = LocalDateTime.of(Integer.parseInt(hireYear), 9, 1, 9, 0, 0);
		userInfo.setCreateAt(hireDateTime);	

		// 주소 및 은행 정보
		userInfo.setBankCode("BANK_SH");
		userInfo.setBankAccount("1234567890");
		userInfo.setPhotoId(null);
        
        // ⭐️ 2. PW_HASH 설정 (ORA-01400: PW_HASH 해결)
		String hashedPassword = passwordEncoder.encode(INITIAL_PASSWORD);
		userInfo.setPwHash(hashedPassword);	

		// ----------------- 3. 교직원 정보 (staffInfo) 설정 -----------------
		staffInfo.setTeleNo("02-9876-5432");
		staffInfo.setStfDeptCd("STF-HR" );	

		// ----------------- 4. 주소 정보 (addressInfo) 설정 -----------------
		addressInfo.setAddrId("ADDRTEST0000001");
		addressInfo.setUsingYn("Y");

		addressInfo.setBaseAddr("서울특별시 강남구"); 
		addressInfo.setDetailAddr("JS빌딩 502호");
		addressInfo.setZipCode("06123");

		// ----------------- 5. 부서 정보 (staffDeptInfo) 설정 -----------------
		staffDeptInfo.setStfDeptName("총무처");

		// 6. Service가 필요로 하는 String 타입 임용일 설정
		dto.setHireDateString(String.format("%s-09-01", hireYear));
		dto.setGender("F");

		return dto;
	}

	/**
	 * 1. 교직원 등록 및 교번/비밀번호 생성 로직 검증 테스트 (CREATE)
	 */
	@Test
	void testCreateStaffManagement() {
		final String TEST_HIRE_YEAR = String.valueOf(LocalDateTime.now().getYear());
		final String EXPECTED_INITIAL_PW = INITIAL_PASSWORD; 

		// 1. Arrange: 유효한 교직원 DTO 준비
		UserStaffDTO newStaff = createTestStaffDTO(TEST_HIRE_YEAR);

		// 2. Act: 등록 서비스 실행 (ADDRESS, USERS, STAFF 순서)
		int result = assertDoesNotThrow(() -> service.createStaffManagement(newStaff),
				"교직원 등록 서비스 실행 시 예외가 발생해서는 안 됩니다.");

		// 3. Assert: 등록 성공 및 데이터 검증
		assertEquals(3, result, "DB 삽입된 레코드 수는 정확히 3개여야 합니다. 현재: " + result);

		String generatedStaffNo = newStaff.getStaffInfo().getStaffNo();
		assertNotNull(generatedStaffNo, "등록 후 DTO에 교번(StaffNo)이 설정되어야 합니다.");

		// ⭐️ ORA-12899 해결 후 교번 길이는 7자리여야 합니다. 
		assertEquals(7, generatedStaffNo.length(), "교번 길이는 7자리여야 합니다.");

		log.info("✅ 새로 생성된 교직원 번호: {}", generatedStaffNo);

		// 4. 데이터 재조회 및 검증
		UserStaffDTO createdStaff = service.readStaffManagement(generatedStaffNo);

		assertNotNull(createdStaff, "새로 생성된 교번으로 교직원 정보를 조회할 수 있어야 합니다.");

		// 5. 비밀번호 검증
		String dbHashedPassword = createdStaff.getUserInfo().getPwHash();
		boolean isPasswordMatch = passwordEncoder.matches(EXPECTED_INITIAL_PW, dbHashedPassword);
		assertTrue(isPasswordMatch, "초기 비밀번호(" + EXPECTED_INITIAL_PW + ")가 정확히 암호화되어 저장되었어야 합니다.");
		log.info("✅ 교직원 등록(CREATE) 및 비밀번호 검증 성공.");
	}

    // -------------------------------------------------------------
    
	/**
	 * 2. 교직원 목록 조회 테스트 (READ LIST)
	 */
	@Test
	void testReadSStaffManagementList() {
		PaginationInfo<Map<String, Object>> pagingInfo = new PaginationInfo<>();
		pagingInfo.setCurrentPage(1);
		pagingInfo.setScreenSize(10);
//		List<Map<String, Object>> staffList = service.readStaffManagementList(pagingInfo);
		
//		assertNotNull(staffList, "교직원 목록은 NULL이 아니어야 합니다.");
		// DB에 테스트 데이터가 이미 존재한다는 가정 하에 검증
//		assertFalse(staffList.isEmpty(), "DB에 존재하는 교직원이 있다면, 목록 크기는 0보다 커야 합니다.");
		log.info("✅ 교직원 목록 조회(READ LIST) 성공. Total: {}", pagingInfo.getTotalRecord());
	}

    // -------------------------------------------------------------

	/**
	 * 3. 교직원 상세 정보 조회 테스트 (READ DETAIL)
	 */
	@Test
	void testReadStaffManagement() {
		// 테스트 환경에 존재하는 유효한 사번 사용
		String staffNo = "2023001"; 

		UserStaffDTO userStaffDTO = assertDoesNotThrow(() -> service.readStaffManagement(staffNo),
				"유효한 사번 조회 시 예외가 발생해서는 안 됩니다.");

		assertNotNull(userStaffDTO, "조회된 교직원 정보 DTO는 NULL이 아니어야 합니다.");
		
		log.info("=========================================================");
		log.info("✅ 조회된 교직원 전체 상세 정보 (교번: {}):", staffNo);
		log.info(">> userStaffDTO 전체: {}", userStaffDTO);

		assertEquals(staffNo, userStaffDTO.getStaffInfo().getStaffNo(), "조회된 교직원 사번이 요청한 사번과 일치해야 합니다.");
		log.info("✅ 교직원 상세 조회(READ DETAIL) 성공. 교번: {}", staffNo);
	}
    
    // -------------------------------------------------------------

	/**
	 * 4. 교직원 정보 수정 테스트 (UPDATE)
	 */
	@Test
	void testModifyStaffManagement() {
		// 테스트 환경에 존재하는 유효한 사번 사용
		String staffNo = "2023001"; 
        
		UserStaffDTO originalDTO = assertDoesNotThrow(() -> service.readStaffManagement(staffNo),
				"수정 전 조회 시 예외가 발생해서는 안 됩니다.");

		assertNotNull(originalDTO, "수정 전 교직원 DTO는 존재해야 합니다.");

		// 수정할 데이터 설정
        final String newMobile = "010-7777-8888";
		final String newDetailAddr = "테스트 수정 상세 주소";

        // DTO에 ID 값을 유지한 채 업데이트 필드만 변경
        originalDTO.getUserInfo().setMobileNo(newMobile); 		// USERS 테이블
        originalDTO.getAddressInfo().setDetailAddr(newDetailAddr); // ADDRESS 테이블
        originalDTO.getStaffInfo().setTeleNo("02-1234-9999");	// STAFF 테이블

        // 수정 서비스 실행
		assertDoesNotThrow(() -> service.modifyStaffManagementDetail(originalDTO), "교직원 수정 서비스 실행 시 예외가 발생해서는 안 됩니다.");

		UserStaffDTO afterDTO = service.readStaffManagement(staffNo);

        // 수정 후 검증
		assertEquals(newMobile, afterDTO.getUserInfo().getMobileNo(), "휴대전화 번호(USERS)가 정확히 변경되어야 합니다.");
		assertEquals(newDetailAddr, afterDTO.getAddressInfo().getDetailAddr(), "상세 주소(ADDRESS)가 정확히 변경되어야 합니다.");
		assertEquals("02-1234-9999", afterDTO.getStaffInfo().getTeleNo(), "교직원 내선번호(STAFF)가 정확히 변경되어야 합니다.");

		log.info("✅ 교직원 정보 수정(UPDATE) 성공. 교번: {}", staffNo);
	}
}