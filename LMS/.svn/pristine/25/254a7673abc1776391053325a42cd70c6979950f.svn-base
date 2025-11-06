package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.dto.StudentSimpleDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 1.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 1.     	김수현	          학생 인적&학적 조회 테스트 추가
 *
 * </pre>
 */
@Transactional
@SpringBootTest
@Slf4j
class StudentMapperTest {

	@Autowired
	StudentMapper mapper;
	@Autowired
	CommonCodeMapper cMapper;

	@Test
	@DisplayName("학생 전체 목록 조회 테스트")
	void testSelectStudentList() {
        // 1. 쿼리 실행 시 예외가 발생하지 않는지 검증 (실행 성공 여부 확인)
        assertDoesNotThrow(() -> mapper.selectStudentList(), 
                           "쿼리 실행 중 예외가 발생했습니다 (SQL, 매핑 등 확인 필요).");

        // 2. 쿼리를 다시 실행하여 반환된 List 객체 자체가 null이 아닌지 검증 (객체 유효성 확인)
        assertNotNull(mapper.selectStudentList(), "조회된 List 객체는 null이 아니어야 합니다.");

        // 3. 로그 출력 (쿼리가 세 번째 실행되지만 코드가 간결함)
        log.info("===> 조회 결과 크기: {}", mapper.selectStudentList().size());
	}
	
	@Test
	void testDTO() {
		String sNo = "202500001";
		StudentSimpleDTO dto = mapper.selectBasicStudentInfo(sNo);
		assertNotNull(dto);
		log.info("dto : {}\n"
				+ "dto 속의 유저정보 : {}\n"
				+ "dto 속의 학생정보 : {}\n"
				+ "dto 속의 학과정보 : {}\n"
				+ "dto 속의 단과대정보 : {}\n"
				, dto, dto.getUserInfo(), dto.getStudentInfo(), dto.getMajorDeptInfo(), dto.getMajorCollegeInfo());
	}
	
	@Test
	void testDTO2() {
//		String sNo = "202500001";
//		StudentDetailDTO dto = mapper.selectStudentDetailInfo(sNo);
//		assertNotNull(dto);
//		log.info("dto : {}\n"
//				+ "dto 속의 유저정보 : {}\n"
//				+ "dto 속의 학생정보 : {}\n"
//				+ "dto 속의 학생입학정보 : {}\n"
//				+ "dto 속의 병역정보 : {}\n"
//				, dto, dto.getUserInfo(), dto.getStudentInfo(), dto.getEntranceInfo(), dto.getMilitaryInfo());
	}
	
	@Test
	void testSelectStudentDetailInfo() {
		String studentNo = "202500001";
		StudentDetailDTO dto = mapper.selectStudentDetailInfo(studentNo);
		assertDoesNotThrow(() -> dto);
		assertNotNull(dto);
		log.info("===> 조회된 학생 정보 : {}", dto);
	}

	@Test
	void testUpdateUserDetailInfo() {
	    String studentNo = "202500001"; // 김새봄 학생
	    String userId = "USER00000000016";

	    StudentDetailDTO newDto = new StudentDetailDTO();
	    newDto.setUserId(userId);
	    newDto.setMobileNo("010-9999-8888"); // 새로운 휴대폰 번호
	    newDto.setEmail("new_email@test.com");   // 새로운 이메일

	    newDto.setBankCode("BANK_HN"); // 새로운 은행 코드 (하나은행)
	    newDto.setBankAccount("1234567890");

	    int result = mapper.updateUserDetailInfo(newDto);

	    assertEquals(1, result); // 1개의 행이 업데이트되었는지 확인

	    StudentDetailDTO updatedDto = mapper.selectStudentDetailInfo(studentNo);
	    assertEquals("010-9999-8888", updatedDto.getMobileNo());
	    log.info("===> 업데이트 후 휴대폰 번호: {}", updatedDto.getMobileNo());
	}
	
	@Test
	void testUpdateAddressInfo() {
	    String studentNo = "202500001";
	    String addrId = "ADDR00000000016"; 

	    StudentDetailDTO newAddressDto = new StudentDetailDTO();
	    newAddressDto.setAddrId(addrId); 
	    newAddressDto.setZipCode("01234");       // 새로운 우편번호
	    newAddressDto.setBaseAddr("서울특별시 강남구 테헤란로 100"); // 새로운 기본 주소
	    newAddressDto.setDetailAddr("2층");      // 새로운 상세 주소

	    int result = mapper.updateAddressInfo(newAddressDto);
	    assertEquals(1, result);

	    StudentDetailDTO updatedDto = mapper.selectStudentDetailInfo(studentNo);
	    assertEquals("01234", updatedDto.getZipCode());
	    assertEquals("서울특별시 강남구 테헤란로 100", updatedDto.getBaseAddr());
	    log.info("===> 업데이트 후 우편번호: {}", updatedDto.getZipCode());
	}
	@Test
	void testUpdateStudentPersonalInfo() {
	    String studentNo = "202500001";

	    StudentDetailDTO newStudentDto = new StudentDetailDTO();
	    newStudentDto.setStudentNo(studentNo);
	    newStudentDto.setEngLname("NewKim");        // 새로운 영문 성
	    //newStudentDto.setEngFname("TestSaebom");    // 새로운 영문 이름
	    newStudentDto.setGuardName("홍길동");       // 새로운 비상 연락처 이름
	    newStudentDto.setGuardPhone("010-1234-5678"); // 새로운 비상 연락처 번호

	    int stuResult = mapper.updateStudentPersonalInfo(newStudentDto); 

	    assertEquals(1, stuResult); // STUDENT 테이블 업데이트 확인

	     StudentDetailDTO updatedDto = mapper.selectStudentDetailInfo(studentNo);
	     assertEquals("NewKim", updatedDto.getEngLname());
	     assertEquals("홍길동", updatedDto.getGuardName());
	     log.info("===> 업데이트 후 영문 성: {}", updatedDto.getEngLname());
	}
	
	

//	@Test
	void testInsertStudent() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdateStudent() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteStudent() {
		fail("Not yet implemented");
	}
}