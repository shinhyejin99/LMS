package kr.or.jsu.classroom.student.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.student.service.ClassStuTaskService;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@SpringBootTest
class ClassStuTaskServiceImplTest {

	@Autowired
	ClassStuTaskService service;
	
	@Test
	void 조별과제와그조별과제의내조가져와보기() {
		
		UsersVO user = new UsersVO();
		user.setUserNo("202500001");
		String lectureId = "LECT00000000001";
		String grouptaskId = "TSKGRUP00000006";
		
		LctGrouptaskDetailResp result = service.readGrouptask(user, lectureId, grouptaskId);
		
		log.info("결과 : {}", PrettyPrint.pretty(result));
		
	}

}
