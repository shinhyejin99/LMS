package kr.or.jsu.classroom.professor.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class ClassPrfAttendanceServiceImplTest {
	
	@Autowired
	ClassPrfAttendanceService service;
	
	@Test
	void 수동출석생성하기() {
		
		String lectureId = "LECT00000000001";
		String status1 = "TBD";
//		String status2 = "OK";
//		String status3 = "NO";
		
		int result = service.createManualAttRound(lectureId, status1);
		
		log.info("생성된 주차 : {}", PrettyPrint.pretty(result));
	}


}
