package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class DeptConditionMapperTest {

	@Autowired
	DeptConditionMapper mapper;

//	@Test
//	void testInsertDeptCondition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testSelectDeptConditionList() {
//		assertNotNull(mapper.selectDeptConditionList());
//		log.info("====> 조회결과 {}", mapper.selectDeptConditionList());
//		assertDoesNotThrow(() -> mapper.selectDeptConditionList());
//		assertEquals(0, (mapper.selectDeptConditionList()).size());
//	}
//
//	@Test
//	void testUpdateDeptCondition() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDeleteDeptCondition() {
//		fail("Not yet implemented");
//	}

	/**
	 * 학과별 조건 값 조회 (NEEDED_VALUE)
	 */
	@Test
	void testSelectConditionValue() {
		String univDeptCd = "DEP-ENGI-CSE"; // 컴퓨터공학과
        String conditionCd = "MJ_DBL_CREDIT"; // 컴퓨터공학과를 복수전공 시 -> 최소 졸업이수학점
        assertEquals("36", mapper.selectConditionValue(univDeptCd, conditionCd));
	}

}
