package kr.or.jsu.lms.staff.service.staffLectureRoom;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.dto.info.place.PlaceInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class StaffLectureRoomServiceImplTest {

	@Autowired
	StaffLectureRoomService service;
	
	@Test
	void 강의실가져와보기() {
		
		List<PlaceInfo> result = service.readAllRooms();
		
		log.info("결과 : {}", PrettyPrint.pretty(result));
	}

	@Test
	void 강의실배정작업해야할강의가져와보기() {
		String yeartermCd = "2026_REG1";
		var result = service.unAssignedLectureList(yeartermCd);
		
		log.info("결과 : {}", PrettyPrint.pretty(result));
	}
	
}
