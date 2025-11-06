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
class StuApplyLctMapperTest {

	@Autowired // Mapper 주입
	StuApplyLctMapper mapper;

//	@Test
	void testSelectStuApplyLct() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("학생 수강 신청 전체 목록 조회 테스트")
	void testSelectStuApplyLctList() {
        // 1. 쿼리 실행 시 예외가 발생하지 않는지 검증 (실행 성공 여부 확인)
        assertDoesNotThrow(() -> mapper.selectStuApplyLctList(), 
                           "쿼리 실행 중 예외가 발생했습니다 (SQL, 매핑 등 확인 필요).");

        // 2. 쿼리를 다시 실행하여 반환된 List 객체 자체가 null이 아닌지 검증 (객체 유효성 확인)
        assertNotNull(mapper.selectStuApplyLctList(), "조회된 List 객체는 null이 아니어야 합니다.");

        // 3. 로그 출력 (쿼리가 세 번째 실행되지만 코드가 간결함)
        log.info("===> 조회 결과 크기: {}", mapper.selectStuApplyLctList().size());
	}

//	@Test
	void testInsertStuApplyLct() {
		fail("Not yet implemented");
	}

//	@Test
	void testUpdateStuApplyLct() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteStuApplyLct() {
		fail("Not yet implemented");
	}
}