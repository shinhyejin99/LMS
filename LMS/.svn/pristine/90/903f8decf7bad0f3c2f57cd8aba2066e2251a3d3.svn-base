package kr.or.jsu.mybatis.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.mybatis.mapper.lms.professor.lctapply.LctOpenApplyMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LctOpenApplyMapperTest {

	@Autowired
	LctOpenApplyMapper mapper;

	@Test
	void 특정교수의강의신청목록에쓸데이터() {
		String professorNo = "20230001";
		
		var a = mapper.selectLctApplyList(professorNo);
		
		log.info("result : {}", PrettyPrint.pretty(a));
		
		log.info("결과 수 : {}", a.size());
	}
	
	@Test
	void 특정강의의상세내용에쓸데이터() {
		String lctApplyId = "LCTAPLY00000085";
		
		var res = mapper.selectLctApplyDetail(lctApplyId);
		
		log.info("result : {}", PrettyPrint.pretty(res));
	}
	
	@Test
	void 신청받아서강의만들어주기위해가져오기() {
		String approveId = "APPR00000000713";
		
		var res = mapper.selectLctApplyByApproveId(approveId);
		
		log.info("result : {}", PrettyPrint.pretty(res));
	}
}
