package kr.or.jsu.lms.student.service.info;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.StudentDetailDTO;
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
 *  2025. 10. 1.     	김수현	          최초 생성
 *
 * </pre>
 */
@Transactional
@SpringBootTest
@Slf4j
class StuInfoServiceImplTest {

	@Autowired
	StuInfoService service;
	// 김새봄 학생
	private final String TEST_STUDENT_NO = "202500001"; 
    private final String TEST_USER_ID = "USER00000000016";
    private final String TEST_ADDR_ID = "ADDR00000000016";


	@Test
	// 조회: 학생 인적/학적 정보 조회 성공
	void testReadStuMyInfo() {
		String studentNo = TEST_STUDENT_NO;
		StudentDetailDTO dto = service.readStuMyInfo(studentNo);
		assertDoesNotThrow(() -> dto);
		assertNotNull(dto);
		log.info("===> 조회된 학생 정보 : {}", dto);
	}
	
	@Test
	// 업데이트: 인적 정보 수정 통합 테스트 성공
	void testUpdateStuMyInfo_Success() {
		// 1. 테스트 준비: 기존 학번 및 변경할 데이터 설정
		final String STUDENT_NO = TEST_STUDENT_NO; // 수정할 학생의 학번
		
		// 2. 초기 데이터 조회 (업데이트 전 상태 확인)
		StudentDetailDTO originalDto = service.readStuMyInfo(STUDENT_NO);
		assertNotNull(originalDto, "테스트를 위해 학생 정보가 존재해야 합니다.");
		log.info("Original Mobile No: {}", originalDto.getMobileNo());
		
		// 3. 업데이트 요청 DTO 및 nameValue 생성
		StudentDetailDTO updateDto = new StudentDetailDTO();
		
		// Service 내부에서 ID를 SecurityContext로 설정한다는 가정 하에,
		updateDto.setStudentNo(STUDENT_NO); 
		updateDto.setUserId(TEST_USER_ID); 
		updateDto.setAddrId(TEST_ADDR_ID); 
		
		// USERS 테이블 수정 필드
		updateDto.setMobileNo("010-9999-8888");
		updateDto.setEmail("new.contact@jsu.or.kr");
		updateDto.setBankName("농협은행"); // Service에서 코드로 변환됨
		updateDto.setBankAccount("9876543210");
		
		// ADDRESS 테이블 수정 필드
		updateDto.setZipCode("05111");
		updateDto.setBaseAddr("서울시 광진구 능동로 120");
		updateDto.setDetailAddr("정수관 501호");
		
		// STUDENT 테이블 수정 필드
		updateDto.setGuardName("박지수");
		updateDto.setGuardPhone("010-1111-0000");
		
		// 합쳐진 이름 문자열 (Service의 processNameSplit 로직을 통과할 값)
		final String NEW_NAME_VALUE = "이 봄이 / Lee Bomi";

		// 4. Service 메서드 실행
		log.info("===> 인적 정보 업데이트 시작");
		int result = service.updateStuMyInfo(updateDto, NEW_NAME_VALUE);
		
		// 5. 실행 결과 검증
		assertEquals(1, result);

		// 6. 업데이트된 데이터 재조회 및 상세 검증
		StudentDetailDTO updatedDto = service.readStuMyInfo(STUDENT_NO);
		
		// USERS 테이블 (한글 이름, 연락처, 은행) 검증
		assertEquals("이", updatedDto.getLastName(), "한글 성이 '이'로 업데이트되어야 함");
		assertEquals("봄이", updatedDto.getFirstName(), "한글 이름이 '봄이'로 업데이트되어야 함");
		assertEquals("010-9999-8888", updatedDto.getMobileNo(), "휴대폰 번호가 업데이트되어야 함");
		// DB에 농협은행 코드가 '011'이라고 가정하고 검증 (실제 DB 코드에 맞게 수정 필요)
		// assertEquals("011", updatedDto.getBankCode(), "은행 코드가 업데이트되어야 함"); 
		
		// ADDRESS 테이블 검증
		assertEquals("05111", updatedDto.getZipCode(), "우편번호가 업데이트되어야 함");
		assertEquals("서울시 광진구 능동로 120", updatedDto.getBaseAddr(), "기본 주소가 업데이트되어야 함");
		
		// STUDENT 테이블 (영문 이름, 비상연락처) 검증
		assertEquals("Lee", updatedDto.getEngLname(), "영문 성이 'Lee'로 업데이트되어야 함");
		assertEquals("Bomi", updatedDto.getEngFname(), "영문 이름이 'Bomi'로 업데이트되어야 함");
		assertEquals("박지수", updatedDto.getGuardName(), "비상 연락망 이름이 업데이트되어야 함");
		
		log.info("===> 업데이트 후 모바일 번호: {}", updatedDto.getMobileNo());
	}

}
