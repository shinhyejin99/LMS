package kr.or.jsu.mybatis.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@SpringBootTest
class LctTaskMapperTest {

	@Autowired
	LctTaskMapper mapper;
	
	@Test
	void 개인과제제출과점수() {
		String lectureId = "LECT00000000001";
		
		var result = mapper.selectIndivtaskAndEachStudentScore(lectureId);
		
		log.info("{}", PrettyPrint.pretty(result));
	}
	
	@Test
	void 조별과제제출과점수() {
		String lectureId = "LECT00000000001";
		
		var result = mapper.selectGrouptaskAndEachStudentScore(lectureId);
		
		log.info("{}", PrettyPrint.pretty(result));
	}

}
