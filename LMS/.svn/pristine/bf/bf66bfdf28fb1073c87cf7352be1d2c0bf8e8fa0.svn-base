package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.ScholarshipDistributionDTO;
import kr.or.jsu.dto.ScholarshipHistoryDTO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class ScholarshipMapperTest {

	@Autowired
	ScholarshipMapper mapper;
	
	private static final String TEST_STUDENT_NO = "202590994";

	// 1. 학생) 총 수혜 금액 테스트
    @Test
    void selectTotalScholarshipTest() {
        log.info("selectTotalScholarshipTest 시작 - 학번: {}", TEST_STUDENT_NO);

        Integer totalAmount = mapper.selectTotalScholarship(TEST_STUDENT_NO);
        
        assertNotNull(totalAmount, "총 금액은 null이 아님(0 or 금액)");
        assertTrue(totalAmount >= 0, "총 금액은 0 이상");
        
        log.info("총 수혜 금액: {}원", totalAmount);
    }

    // 2. 학생) 종류별 분포 테스트
    @Test
    void selectScholarshipDistributionTest() {
        log.info("selectScholarshipDistributionTest 시작 - 학번: {}", TEST_STUDENT_NO);
        
        // 테스트 실행
        List<ScholarshipDistributionDTO> list = mapper.selectScholarshipDistribution(TEST_STUDENT_NO);

        assertNotNull(list, "결과 리스트는 null이 아님");
        
        if (list.isEmpty()) {
            log.warn("해당 학번({})에 대한 장학금 분포 데이터가 없음", TEST_STUDENT_NO);
        } else {
            log.info("조회된 장학금 종류 수: {}", list.size());
            
            // 3. 로그 출력 (데이터 확인)
            list.forEach(dto -> log.info("분포 항목: {} - 횟수: {}, 총액: {}", 
                                         dto.getScholarshipName(), dto.getCount(), dto.getTotalAmount()));
        }
    }

    // 3. 학생) 수혜 내역 테스트
    @Test
    void selectScholarshipHistoryTest() {
        log.info("selectScholarshipHistoryTest 시작 - 학번: {}", TEST_STUDENT_NO);

        // 테스트 실행
        List<ScholarshipHistoryDTO> list = mapper.selectScholarshipHistory(TEST_STUDENT_NO);
        
        assertNotNull(list, "결과 리스트는 null이 아님");
        
        if (list.isEmpty()) {
            log.warn("해당 학번({})에 대한 장학금 수혜 내역 데이터가 없음", TEST_STUDENT_NO);
        } else {
            log.info("조회된 수혜 내역 건수: {}", list.size());

            // 3. 로그 출력 (데이터 확인)
            list.forEach(dto -> log.info("내역: {} ({}) - 금액: {}원, 상태: {}", 
                                         dto.getSemesterName(), dto.getScholarshipName(), dto.getAmount(), dto.getStatus()));
        }
    }
}