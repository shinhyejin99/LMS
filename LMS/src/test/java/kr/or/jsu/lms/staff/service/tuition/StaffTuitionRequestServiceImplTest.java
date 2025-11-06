package kr.or.jsu.lms.staff.service.tuition;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class StaffTuitionRequestServiceImplTest {

	@Autowired
	private StaffTuitionRequestService service;
    
	@Test
	void testExecuteTuitionRequest() {
		log.info("등록금 납부 요청 프로시저 실행 (호출 성공 여부 검증)");
		assertDoesNotThrow(() -> {
            service.executeTuitionRequest();
        }, "등록금 납부 요청 프로시저 호출 중 DB 오류 발생");
        
        log.info("검증 성공: Service 호출 및 프로시저 실행 완료");
	}
}
