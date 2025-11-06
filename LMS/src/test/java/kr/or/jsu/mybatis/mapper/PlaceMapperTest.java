package kr.or.jsu.mybatis.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class PlaceMapperTest {

	@Autowired
	PlaceMapper mapper;

	@Test
	void 강의실있는건물만가져온다음가진강의실리스트도가져오기() {
		var result = mapper.selectBuildingAndChildPlace("2025_REG2", "CLASSROOM");
		
		log.info("결과 : {}", PrettyPrint.pretty(result));
	}

}
