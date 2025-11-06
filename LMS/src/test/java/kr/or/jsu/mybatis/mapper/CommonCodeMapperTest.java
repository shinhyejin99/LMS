package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.jsu.vo.CommonCodeVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class CommonCodeMapperTest {

	@Autowired
	CommonCodeMapper ccMapper;

	@Test
	void testGetBankCodeList() throws Exception {
		List<CommonCodeVO> bankCodeList = ccMapper.selectCommonCodeList("BANK_CODE");
		assertTrue(bankCodeList.size() != 0);

		ObjectMapper mapper = new ObjectMapper();
		String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bankCodeList);

		log.info("은행코드와 그 이름 : \n{}", pretty);
	}

}
