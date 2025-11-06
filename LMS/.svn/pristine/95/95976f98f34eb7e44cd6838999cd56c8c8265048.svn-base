package kr.or.jsu.lms.staff.service.mypage;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.UserStaffDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class StaffMyPageServiceTest {

	@Autowired
	StaffMyPageService service;
	UserStaffDTO userStaffDto;

	@Test
	void testReadStaffDetail() {
		UserStaffDTO detailResult = assertDoesNotThrow(
	            () -> service.readStaffDetail("2024001"), 
	            "readStaffDetail 호출 중 예외가 발생했습니다." // 예외 발생 시 메시지
	        );
		log.info("조회된 직원 상세 정보: {}", detailResult);
	}

//	@Test
//	void testModifyStaffDetail() {
//	
//		String StaffNo = "2023001";
//
//	    UserStaffDTO userStaff = service.readStaffDetail(StaffNo);
//	    assertNotNull(userStaff, "수정 대상 userStaff가 null이면 안 됩니다.");
//	    assertNotNull(userStaff.getStaffDeptInfo(), "staffDeptInfo가 null이면 안 됩니다.");
//
//	    // 여기서 stfDeptCd가 null 또는 빈 문자열이면 임시로 넣어주기
//	    if (userStaff.getStaffDeptInfo().getStfDeptCd() == null || userStaff.getStaffDeptInfo().getStfDeptCd().trim().isEmpty()) {
//	    	userStaff.getStaffDeptInfo().setStfDeptCd("STF-HR");
//	    }
//
//	    // 수정할 내용 세팅
//	    userStaff.getUserInfo().setFirstName("수정된이름");
//	    userStaff.getUserInfo().setEmail("updated_email@example.com");
//	    userStaff.getStaffInfo().setTeleNo("010-1234-5678");
//
//	    // 부서코드는 원본 유지
//	    String originalDeptCd = userStaff.getStaffInfo().getStfDeptCd();
//	    userStaff.getStaffInfo().setStfDeptCd(originalDeptCd);
//
//	    // 서비스 메서드 호출
//	    assertDoesNotThrow(() -> service.modifyStaffDetail(userStaff));
//
//	    UserStaffDTO updated = service.readStaffDetail(StaffNo);
//
//	    assertEquals("수정된이름", updated.getUserInfo().getFirstName());
//	    assertEquals("updated_email@example.com", updated.getUserInfo().getEmail());
//	    assertEquals("010-1234-5678", updated.getStaffInfo().getTeleNo());
//	    assertEquals(originalDeptCd, updated.getStaffInfo().getStfDeptCd());
//
//	    System.out.println("수정 후 DTO: " + updated);
//	}
}
