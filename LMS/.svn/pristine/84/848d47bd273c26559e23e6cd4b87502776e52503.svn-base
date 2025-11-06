package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional // 테스트 후 자동 롤백
@SpringBootTest // 스프링 컨텍스트 로드
@Slf4j
class PushNoticeTargetMapperTest {

	@Autowired // Mapper 주입
	PushNoticeTargetMapper mapper;

//	@Test
	void testInsertPushNoticeTarget() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("푸시 알림 대상 전체 목록 조회 테스트")
	void testSelectPushNoticeTargetList() {
        
        // 1. 쿼리 실행 시 예외가 발생하지 않는지 검증 (실행 성공 여부 확인)
        // ⭐️ 람다식 내에서 mapper.selectPushNoticeTargetList()를 호출합니다.
        assertDoesNotThrow(() -> mapper.selectPushNoticeTargetList(), 
                           "쿼리 실행 중 예외가 발생했습니다 (SQL, 매핑 등 확인 필요).");

        // 2. 쿼리를 다시 실행하여 반환된 List 객체 자체가 null이 아닌지 검증 (객체 유효성 확인)
        assertNotNull(mapper.selectPushNoticeTargetList(), "조회된 List 객체는 null이 아니어야 합니다.");

        // 3. 로그 출력 (쿼리가 세 번째 실행되지만 코드가 간결함)
        log.info("===> 조회 결과 크기: {}", mapper.selectPushNoticeTargetList().size());
        
        
	}
    
//	@Test
	void testSelectPushNoticeTarget() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdatePushNoticeTarget() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeletePushNoticeTarget() {
		fail("Not yet implemented");
	}
}