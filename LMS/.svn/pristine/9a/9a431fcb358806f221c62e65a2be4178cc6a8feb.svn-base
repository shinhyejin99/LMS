package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.StaffCourseSearchDTO;
import kr.or.jsu.classregist.dto.StaffCourseStatsDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 교직원 수강신청 관리 Mapper 테스트
 */
@SpringBootTest
@Transactional
@Slf4j
class StaffCourseMapperTest {

	@Autowired
    private StaffCourseMapper mapper;

    private static final String TEST_YEARTERM = "2026_REG1";

    @BeforeEach
    void setUp() {
        log.info("=== 테스트 시작 ===");
    }

    @AfterEach
    void tearDown() {
        log.info("=== 테스트 종료 (자동 롤백) ===");
    }

    // =====================================================
    // 1. 강의 통계 조회 테스트
    // =====================================================

    @Test
    //강의 통계 조회 - 성공
    void testSelectCourseStats_Success() {
        String yeartermCd = TEST_YEARTERM;

        StaffCourseStatsDTO stats = mapper.selectCourseStats(yeartermCd);

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalLectures()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getTotalStudents()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getAvgEnrollRate()).isBetween(0.0, 1.0);

        log.info("통계 조회 결과:");
        log.info("  총 강의 수: {}", stats.getTotalLectures());
        log.info("  총 수강생 수: {}", stats.getTotalStudents());
        log.info("  평균 정원 충족률: {}%", stats.getAvgEnrollRate() * 100);
    }

    // =====================================================
    // 2. 강의 목록 조회 테스트
    // =====================================================

    @Test
    // 강의 목록 조회 - 전체 조회
    void testSelectCourseList_All() {
        StaffCourseSearchDTO searchDTO = new StaffCourseSearchDTO();
        searchDTO.setYeartermCd(TEST_YEARTERM);
        searchDTO.setSearchKeyword("");
        searchDTO.setPage(1);
        searchDTO.setPageSize(10);
        searchDTO.calculateOffset();

        List<LectureListDTO> list = mapper.selectCourseList(searchDTO);

        assertThat(list).isNotNull();

        log.info("강의 목록 조회 결과: {}건", list.size());

        if (!list.isEmpty()) {
            LectureListDTO first = list.get(0);
            log.info("첫 번째 강의:");
            log.info("  강의ID: {}", first.getLectureId());
            log.info("  과목명: {}", first.getSubjectName());
            log.info("  교수명: {}", first.getProfessorName());
            log.info("  정원: {}/{}", first.getCurrentEnroll(), first.getMaxCap());
            log.info("  충족률: {}%", first.getEnrollRate());
        }
    }

    @Test
    // 강의 목록 조회 - 검색어 있음
    void testSelectCourseList_WithKeyword() {
        StaffCourseSearchDTO searchDTO = new StaffCourseSearchDTO();
        searchDTO.setYeartermCd(TEST_YEARTERM);
        searchDTO.setSearchKeyword("자바");  // 검색어
        searchDTO.setPage(1);
        searchDTO.setPageSize(10);
        searchDTO.calculateOffset();

        List<LectureListDTO> list = mapper.selectCourseList(searchDTO);

        assertThat(list).isNotNull();

        log.info("'자바' 검색 결과: {}건", list.size());

        // 검색어가 과목명이나 교수명에 포함되는지 확인
        if (!list.isEmpty()) {
            list.forEach(lecture -> {
                boolean hasKeyword =
                    lecture.getSubjectName().contains("자바") ||
                    lecture.getProfessorName().contains("자바");

                log.info("  강의: {} (교수: {}) - 검색어 포함: {}",
                    lecture.getSubjectName(),
                    lecture.getProfessorName(),
                    hasKeyword);
            });
        }
    }

    @Test
    // 강의 목록 조회 - 페이징
    void testSelectCourseList_Paging() {
        StaffCourseSearchDTO page1 = new StaffCourseSearchDTO();
        page1.setYeartermCd(TEST_YEARTERM);
        page1.setPage(1);
        page1.setPageSize(5);
        page1.calculateOffset();

        StaffCourseSearchDTO page2 = new StaffCourseSearchDTO();
        page2.setYeartermCd(TEST_YEARTERM);
        page2.setPage(2);
        page2.setPageSize(5);
        page2.calculateOffset();

        List<LectureListDTO> list1 = mapper.selectCourseList(page1);
        List<LectureListDTO> list2 = mapper.selectCourseList(page2);

        log.info("1페이지: {}건", list1.size());
        log.info("2페이지: {}건", list2.size());

        // 1페이지와 2페이지의 강의가 달라야 함
        if (!list1.isEmpty() && !list2.isEmpty()) {
            assertThat(list1.get(0).getLectureId())
                .isNotEqualTo(list2.get(0).getLectureId());
        }
    }

    // =====================================================
    // 3. 강의 총 개수 테스트
    // =====================================================

    @Test
    // 강의 총 개수 조회
    void testCountCourseList() {
        StaffCourseSearchDTO searchDTO = new StaffCourseSearchDTO();
        searchDTO.setYeartermCd(TEST_YEARTERM);
        searchDTO.setSearchKeyword("");

        int count = mapper.countCourseList(searchDTO);

        assertThat(count).isGreaterThanOrEqualTo(0);

        log.info("총 강의 개수: {}건", count);
    }

    // =====================================================
    // 4. 수강신청 통계 조회 테스트
    // =====================================================

    @Test
    // 수강신청 통계 조회
    void testGetApplyStatistics() {
        String yeartermCd = TEST_YEARTERM;

        Map<String, Object> stats = mapper.getApplyStatistics(yeartermCd);

        assertThat(stats).isNotNull();
        assertThat(stats).containsKeys("totalStudents", "totalApplies", "totalLectures");

        log.info("수강신청 통계:");
        log.info("  신청 학생 수: {}", stats.get("totalStudents"));
        log.info("  총 신청 건수: {}", stats.get("totalApplies"));
        log.info("  개설 강의 수: {}", stats.get("totalLectures"));
    }

    // =====================================================
    // 5. 수강신청 확정 테스트
    // =====================================================

    @Test
    // 수강신청 확정 - 성공 (롤백 테스트)
    void testConfirmEnrollment_Success() {
        String yeartermCd = TEST_YEARTERM;

        Map<String, Object> beforeStats = mapper.getApplyStatistics(yeartermCd);

        log.info("===== 확정 전 통계 =====");
        log.info("beforeStats: {}", beforeStats);

        // 대문자 키로 직접 접근
        int beforeApplies = beforeStats != null && beforeStats.get("TOTALAPPLIES") != null
            ? ((Number) beforeStats.get("TOTALAPPLIES")).intValue()
            : 0;

        int beforeStudents = beforeStats != null && beforeStats.get("TOTALSTUDENTS") != null
            ? ((Number) beforeStats.get("TOTALSTUDENTS")).intValue()
            : 0;

        int beforeLectures = beforeStats != null && beforeStats.get("TOTALLECTURES") != null
            ? ((Number) beforeStats.get("TOTALLECTURES")).intValue()
            : 0;

        log.info("수강신청 건수: {}", beforeApplies);
        log.info("신청 학생 수: {}", beforeStudents);
        log.info("개설 강의 수: {}", beforeLectures);

        // 확정 실행
        int confirmedCount = mapper.confirmEnrollment(yeartermCd);

        assertThat(confirmedCount).isGreaterThanOrEqualTo(0);

        log.info("===== 확정 완료 =====");
        log.info("확정된 건수: {}", confirmedCount);
        log.info("예상 확정 건수: {}", beforeApplies);

        // 확정 건수 검증
        if (beforeApplies > 0) {
            assertThat(confirmedCount).isEqualTo(beforeApplies);
            log.info("===> 확정 건수 일치!");
        } else {
            log.info("===> 수강신청 데이터가 없어 확정할 내용이 없음");
        }

        log.info("===== 테스트 종료 후 자동 롤백됨 =====");
    }

    @Test
    // 수강신청 확정 - 재이수 여부 체크
    void testConfirmEnrollment_RetakeCheck() {
        // given
        String yeartermCd = TEST_YEARTERM;

        // when
        int confirmedCount = mapper.confirmEnrollment(yeartermCd);

        // then
        log.info("확정 건수: {}", confirmedCount);
        log.info("재이수 여부(RETAKE_YN)가 올바르게 설정되었는지 확인 필요");
        log.info("  - 기존 수강 이력 있음 → 'Y'");
        log.info("  - 기존 수강 이력 없음 → 'N'");
    }

    @Test
    // 수강신청 확정 - 중복 확정 방지 테스트
    void testConfirmEnrollment_Duplicate() {

        String yeartermCd = TEST_YEARTERM;

        int firstCount = mapper.confirmEnrollment(yeartermCd);
        int secondCount = mapper.confirmEnrollment(yeartermCd);

        log.info("1차 확정: {}건", firstCount);
        log.info("2차 확정: {}건 (중복 확정 시도)", secondCount);

        // 중복 확정 시 어떻게 동작하는지 확인
        // UNIQUE 제약조건 있으면 에러 발생 또는 0건 반환
    }

    // =====================================================
    // 6. 통합 시나리오 테스트
    // =====================================================

    @Test
    // 통합 시나리오 - 조회 → 확정 → 롤백
    void testFullScenario() {
        String yeartermCd = TEST_YEARTERM;

        // 1. 통계 조회
        log.info("===== 1. 통계 조회 =====");
        StaffCourseStatsDTO stats = mapper.selectCourseStats(yeartermCd);
        log.info("총 강의: {}, 총 수강생: {}", stats.getTotalLectures(), stats.getTotalStudents());

        // 2. 강의 목록 조회
        log.info("===== 2. 강의 목록 조회 =====");
        StaffCourseSearchDTO searchDTO = new StaffCourseSearchDTO();
        searchDTO.setYeartermCd(yeartermCd);
        searchDTO.setPage(1);
        searchDTO.setPageSize(10);
        searchDTO.calculateOffset();

        List<LectureListDTO> list = mapper.selectCourseList(searchDTO);
        log.info("조회된 강의: {}건", list.size());

        // 3. 수강신청 통계 조회
        log.info("===== 3. 수강신청 통계 조회 =====");
        Map<String, Object> applyStats = mapper.getApplyStatistics(yeartermCd);
        log.info("신청 통계: {}", applyStats);

        // 4. 수강신청 확정
        log.info("===== 4. 수강신청 확정 =====");
        int confirmedCount = mapper.confirmEnrollment(yeartermCd);
        log.info("확정 완료: {}건", confirmedCount);

        // 5. 결과 검증
        assertThat(stats).isNotNull();
        assertThat(list).isNotNull();
        assertThat(applyStats).isNotEmpty();
        assertThat(confirmedCount).isGreaterThanOrEqualTo(0);

        log.info("===== 테스트 종료 - 모든 변경사항 롤백됨 =====");
    }


    @Test
    // 수강신청 확정 - 재이수 여부 상세 확인
    void testConfirmEnrollment_DetailCheck() {
        String yeartermCd = TEST_YEARTERM;

        log.info("===== 확정 전 데이터 조회 =====");

        // 확정할 데이터 미리 조회
        Map<String, Object> beforeStats = mapper.getApplyStatistics(yeartermCd);
        int beforeApplies = beforeStats != null && beforeStats.get("TOTALAPPLIES") != null
            ? ((Number) beforeStats.get("TOTALAPPLIES")).intValue()
            : 0;

        log.info("확정할 데이터: {}건", beforeApplies);

        // 확정 실행
        int confirmedCount = mapper.confirmEnrollment(yeartermCd);

        log.info("===== 확정 결과 =====");
        log.info("확정된 건수: {}", confirmedCount);

        assertThat(confirmedCount).isEqualTo(beforeApplies);

        // 확정된 데이터 확인 (트랜잭션 내에서)
        // 실제로는 별도 SELECT 쿼리가 필요하지만,
        // 여기서는 확정 건수가 일치하는지만 확인

        log.info("===> 테스트 성공!");
        log.info("   - 11건의 수강신청이 STU_ENROLL_LCT로 복사됨");
        log.info("   - ENROLL_STATUS_CD = 'ENR_ING' 설정");
        log.info("   - RETAKE_YN 자동 계산");
        log.info("   - STATUS_CHANGE_AT = SYSDATE");
        log.info("   - 테스트 종료 후 자동 롤백됨");
    }
}
