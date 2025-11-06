package kr.or.jsu.core.common.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.vo.CommonCodeVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class CommonCodeServiceImplTest {
	
	@Autowired
	private CommonCodeService service;
	
	@Test
	void test() {
		
		List<CommonCodeVO> list = service.readCommonCodeList(CommonCodeSort.SCHOLARSHIP_CD);
		
		
		list.forEach(row -> log.info("장학코드 : {}", row));
		
	}

}
