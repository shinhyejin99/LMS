package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class PrfCareerMapperTest {

	@Autowired
	PrfCareerMapper mapper;

//	@Test
	void testInsertPrfCareer() {
		fail("Not yet implemented");
	}

//	@Test
//	void testSelectPrfCareerList() {
//		assertNotNull(mapper.selectPrfCareerList());
//		log.info("===> 조회 결과 : {}", mapper.selectPrfCareerList());
//		assertDoesNotThrow(() -> mapper.selectPrfCareerList());
//
//	}
	
	@Test
	void testSelectPrfCareerList() {
	    // 1. 쿼리 실행 시 예외가 발생하지 않는지 가장 먼저 검증합니다.
	    assertDoesNotThrow(() -> mapper.selectPrfCareerList(), 
	                       "쿼리 실행 중 예외가 발생했습니다 (SQL, 매핑 등 확인 필요).");

	    // 2. 쿼리를 다시 실행하여 반환된 List 객체가 null이 아닌지 검증합니다.
	    assertNotNull(mapper.selectPrfCareerList(), "조회된 List 객체는 null이 아니어야 합니다.");

	    // 3. 로그 출력 (선택 사항)
	    log.info("===> 조회 결과 크기: {}", mapper.selectPrfCareerList().size());
	}
	

//	@Test
	void testSelectPrfCareer() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdatePrfCareer() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeletePrfCareer() {
		fail("Not yet implemented");
	}

}
