package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional // 테스트 후 자동 롤백
@SpringBootTest // 스프링 컨텍스트 로드
@Slf4j
class PushNoticeMapperTest {

	@Autowired // Mapper 주입
	PushNoticeMapper mapper;

//	@Test
	void testInsertPushNotice() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("푸시 알림 전체 목록 조회 테스트")
	void testSelectPushNoticeList() {
		// 1. 쿼리를 한 번만 실행하고 결과를 저장
		 List<?> resultList = null;
	    
	    // 예외가 발생하면 JUnit이 자동으로 테스트 실패로 처리합니다.
	    resultList = mapper.selectPushNoticeListByTargetId(null); 
	    
	    // 2. 반환된 List 객체 자체가 null이 아닌지 검증
	    assertNotNull(resultList, "조회된 List 객체는 null이 아니어야 합니다.");

	    // 3. 로그 출력
	    log.info("===> 조회 결과 크기: {}", resultList.size());
	    
	    // 5. 필요하다면 데이터 건수 검증 추가
	    // assertTrue(resultList.size() >= 0, "조회된 항목 수는 0 이상이어야 합니다.");
	}

//	@Test
	void testSelectPushNotice() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdatePushNotice() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeletePushNotice() {
		fail("Not yet implemented");
	}
}