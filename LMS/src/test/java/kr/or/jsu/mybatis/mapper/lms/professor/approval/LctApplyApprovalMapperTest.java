package kr.or.jsu.mybatis.mapper.lms.professor.approval;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Transactional
class LctApplyApprovalMapperTest {

	@Autowired
	LctApplyApprovalMapper mapper;

	@Test
	void 신청목록들가져오기() {
		List<String> request = List.of(
			"APPR00000000378"
			, "APPR00000000400"
			, "APPR00000000401"
			, "APPR00000000406"
			, "APPR00000000407"
			, "APPR00000000409"
		);
		
		var result = mapper.selectApprovalList(request);
		
		log.info("{}", PrettyPrint.pretty(result));
	}


}
