package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.ScholarshipSimpleDTO;
import kr.or.jsu.dto.TuitionDetailDTO;
import kr.or.jsu.dto.TuitionSimpleDTO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class TuitionMapperTest {

    @Autowired
    TuitionMapper mapper;

    private static final String TEST_STUDENT_NO = "202590994";
    private static final String TEST_YEARTERM_CD = "2025_REG2";
    
    @Test
    void testSelectAllTuitions() {
        assertDoesNotThrow(() -> mapper.selectAllTuitions());
    }
    
    // 교직원) 등록금 납부요청 프로시저 실행
    @Test
    void testCallTuitionRequestProcedure() {
        // 프로시저 호출
        // 호출 자체에 오류가 없는지 확인
        assertThatNoException().isThrownBy(() -> {
        	mapper.callTuitionRequestProcedure();
        });
    }
    
    // 학생)
    // 1. 등록금 고지서 메인 정보 조회 테스트
    @Test
    void testSelectTuitionNoticeMain() {
        String studentNo = TEST_STUDENT_NO;
        String yeartermCd = TEST_YEARTERM_CD;
        
        // mapper 메소드 호출
        TuitionDetailDTO result = mapper.selectTuitionNoticeMain(studentNo, yeartermCd);
        
        // 검증
        assertThat(result).isNotNull();
        assertThat(result.getStudentNo()).isEqualTo(studentNo);
        assertThat(result.getFinalAmount()).isGreaterThanOrEqualTo(0);
        
        log.info("메인 정보 조회 성공. 최종 금액: {}", result.getFinalAmount());
    }

    // 2. 등록금 상세 내역 조회 테스트
    @Test
    void testSelectTuitionDetails() {
        String studentNo = TEST_STUDENT_NO;
        String yeartermCd = TEST_YEARTERM_CD;

        // mapper 메소드 호출
        List<TuitionSimpleDTO> details = mapper.selectTuitionDetails(studentNo, yeartermCd);

        // 검증
        assertThat(details).isNotNull().isNotEmpty();
        assertThat(details.get(0).getAmount()).isGreaterThan(0);
            
        log.info("등록금 상세 내역 조회 성공. 항목 수: {}", details.size());
    }
    
    // 3. 장학금 상세 내역 조회 테스트
    @Test
    void testSelectScholarshipDetails() {
        String studentNo = TEST_STUDENT_NO;
        String yeartermCd = TEST_YEARTERM_CD;

        // mapper 메소드 호출 
        List<ScholarshipSimpleDTO> details = mapper.selectScholarshipDetails(studentNo, yeartermCd);

        // 검증
        assertThat(details).isNotNull();
        
        if (!details.isEmpty()) {
            assertThat(details.get(0).getAmount()).isGreaterThan(0);
        }
        log.info("장학금 상세 내역 조회 성공. 항목 수: {}", details.size());
    }
}
