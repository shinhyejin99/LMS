package kr.or.jsu.aiDummyDataGenerator.lecture;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LctGraderatioVO;
import kr.or.jsu.vo.LctOpenApplyVO;
import kr.or.jsu.vo.LctWeekbyVO;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class LectureDummyGeneratorTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    private static final AtomicLong LECTURE_SEQ = new AtomicLong(-1L);
    private static final String LECTURE_ID_PATTERN = "^LECT7\\d{10}$";

    @Test
    void cloneApprovedApplicationsIntoLectures() {
        List<LctOpenApplyVO> applications = loadApprovedApplications();
        if (applications.isEmpty()) {
            log.warn("강의개설신청 데이터가 없어 12.x 강의 확정 더미를 생성하지 않습니다.");
            return;
        }

        Map<String, String> applyToLecture = new LinkedHashMap<>();
        int lectureRows = 0;
        int weekRows = 0;
        int ratioRows = 0;

        for (LctOpenApplyVO apply : applications) {
            String applyId = apply.getLctApplyId();
            String approveId = apply.getApproveId();

            if (approveId == null || approveId.isBlank()) {
                log.warn("승인ID가 없는 신청 {} 은 강의로 이관하지 않습니다.", applyId);
                continue;
            }

            ensureRatioSumIsValid(applyId);

            Optional<String> existingLectureId = findExistingLecture(apply);
            existingLectureId.ifPresent(this::purgeLectureHierarchy);

            String lectureId = existingLectureId
                    .filter(this::matchesDummyKey)
                    .orElseGet(this::nextLectureId);

            LectureVO lecture = buildLecture(apply, lectureId);
            int inserted = dummyDataMapper.insertOneDummyLecture(lecture);
            if (inserted == 0) {
                log.warn("강의 생성 실패: 신청 {} (approveId: {})", applyId, approveId);
                continue;
            }

            lectureRows += inserted;
            List<LctWeekbyVO> weekTemplates = loadWeekTemplates(applyId, lectureId);
            if (!weekTemplates.isEmpty()) {
                weekRows += dummyDataMapper.insertDummyLctWeekby(weekTemplates);
            }
            List<LctGraderatioVO> ratioTemplates = loadRatioTemplates(applyId, lectureId);
            if (!ratioTemplates.isEmpty()) {
                ratioRows += dummyDataMapper.insertDummyLctGraderatio(ratioTemplates);
            }
            applyToLecture.put(applyId, lectureId);
        }

        log.info("강의 확정 데이터 생성 완료 → 강의 {}건, 주차 {}건, 성적비율 {}건 복제", lectureRows, weekRows, ratioRows);
        if (!applyToLecture.isEmpty()) {
            log.info("신청→강의 매핑 샘플: {}", applyToLecture.entrySet().stream()
                    .limit(10)
                    .map(e -> String.format(Locale.ROOT, "%s→%s", e.getKey(), e.getValue()))
                    .toList());
        }
    }

    private List<LctOpenApplyVO> loadApprovedApplications() {
        return jdbcTemplate.query("""
                        SELECT
                            LCT_APPLY_ID,
                            SUBJECT_CD,
                            PROFESSOR_NO,
                            YEARTERM_CD,
                            LECTURE_INDEX,
                            LECTURE_GOAL,
                            PREREQ_SUBJECT,
                            EXPECT_CAP,
                            DESIRE_OPTION,
                            CANCEL_YN,
                            APPROVE_ID,
                            APPLY_AT
                        FROM LCT_OPEN_APPLY
                        WHERE CANCEL_YN = 'N'
                          AND APPROVE_ID IS NOT NULL
                        ORDER BY LCT_APPLY_ID
                        """,
                new BeanPropertyRowMapper<>(LctOpenApplyVO.class));
    }

    private void ensureRatioSumIsValid(String lctApplyId) {
        List<Integer> ratios = jdbcTemplate.query(
                "SELECT RATIO FROM LCT_APPLY_GRADERATIO WHERE LCT_APPLY_ID = ?",
                (rs, rowNum) -> rs.getInt(1),
                lctApplyId);
        if (ratios.isEmpty()) {
            log.warn("신청 {} 에 등록된 성적비율 항목이 없어 0%로 간주합니다.", lctApplyId);
            return;
        }
        int sum = ratios.stream().mapToInt(Integer::intValue).sum();
        if (sum != 100) {
            throw new IllegalStateException(
                    String.format(Locale.ROOT, "신청 %s 의 성적비율 총합이 %d%% 입니다. (기대값=100%%)", lctApplyId, sum));
        }
    }

    private Optional<String> findExistingLecture(LctOpenApplyVO apply) {
        List<String> lectureIds = jdbcTemplate.queryForList("""
                        SELECT LECTURE_ID
                        FROM LECTURE
                        WHERE SUBJECT_CD = ?
                          AND PROFESSOR_NO = ?
                          AND YEARTERM_CD = ?
                          AND LECTURE_INDEX = ?
                        """,
                String.class,
                apply.getSubjectCd(),
                apply.getProfessorNo(),
                apply.getYeartermCd(),
                apply.getLectureIndex());
        if (lectureIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lectureIds.get(0));
    }

    private String nextLectureId() {
        long seed = LECTURE_SEQ.get();
        if (seed < 0) {
            long maxExisting = fetchMaxLectureSeed();
            if (LECTURE_SEQ.compareAndSet(seed, maxExisting)) {
                seed = maxExisting;
            } else {
                seed = LECTURE_SEQ.get();
            }
        }
        long next = LECTURE_SEQ.incrementAndGet();
        return String.format(Locale.ROOT, "LECT7%010d", next);
    }

    private long fetchMaxLectureSeed() {
        Long max = jdbcTemplate.queryForObject(
                "SELECT MAX(TO_NUMBER(SUBSTR(LECTURE_ID, 6))) FROM LECTURE WHERE LECTURE_ID LIKE 'LECT7%'",
                Long.class);
        return max == null ? 0L : max;
    }

    private boolean matchesDummyKey(String lectureId) {
        return lectureId != null && lectureId.matches(LECTURE_ID_PATTERN);
    }

    private void purgeLectureHierarchy(String lectureId) {
        jdbcTemplate.update("DELETE FROM LCT_ROOM_SCHEDULE WHERE LECTURE_ID = ?", lectureId);
        jdbcTemplate.update("DELETE FROM LCT_GRADERATIO WHERE LECTURE_ID = ?", lectureId);
        jdbcTemplate.update("DELETE FROM LCT_WEEKBY WHERE LECTURE_ID = ?", lectureId);
        int deleted = jdbcTemplate.update("DELETE FROM LECTURE WHERE LECTURE_ID = ?", lectureId);
        if (deleted == 0) {
            log.warn("강의 {} 삭제에 실패했습니다. 기존 데이터가 남아 있을 수 있습니다.", lectureId);
        }
    }

    private LectureVO buildLecture(LctOpenApplyVO apply, String lectureId) {
        LectureVO lecture = new LectureVO();
        lecture.setLectureId(lectureId);
        lecture.setSubjectCd(apply.getSubjectCd());
        lecture.setProfessorNo(apply.getProfessorNo());
        lecture.setYeartermCd(apply.getYeartermCd());
        lecture.setMaxCap(apply.getExpectCap());
        lecture.setLectureIndex(apply.getLectureIndex());
        lecture.setLectureGoal(apply.getLectureGoal());
        lecture.setPrereqSubject(apply.getPrereqSubject());
        lecture.setCancelYn("N");
        boolean finalized = shouldFinalize(apply.getYeartermCd());
        lecture.setEndAt(finalized ? resolveEndAt(apply.getYeartermCd()) : null);
        lecture.setScoreFinalizeYn(finalized ? "Y" : "N");
        return lecture;
    }

    private List<LctWeekbyVO> loadWeekTemplates(String applyId, String lectureId) {
        return jdbcTemplate.query("""
                        SELECT LECTURE_WEEK, WEEK_GOAL, WEEK_DESC
                        FROM LCT_APPLY_WEEKBY
                        WHERE LCT_APPLY_ID = ?
                        ORDER BY LECTURE_WEEK
                        """,
                (rs, rowNum) -> {
                    LctWeekbyVO vo = new LctWeekbyVO();
                    vo.setLectureWeek(rs.getInt("LECTURE_WEEK"));
                    vo.setLectureId(lectureId);
                    vo.setWeekGoal(rs.getString("WEEK_GOAL"));
                    vo.setWeekDesc(rs.getString("WEEK_DESC"));
                    return vo;
                },
                applyId);
    }

    private List<LctGraderatioVO> loadRatioTemplates(String applyId, String lectureId) {
        return jdbcTemplate.query("""
                        SELECT GRADE_CRITEIRA_CD, RATIO
                        FROM LCT_APPLY_GRADERATIO
                        WHERE LCT_APPLY_ID = ?
                        ORDER BY GRADE_CRITEIRA_CD
                        """,
                (rs, rowNum) -> {
                    LctGraderatioVO vo = new LctGraderatioVO();
                    vo.setGradeCriteiraCd(rs.getString("GRADE_CRITEIRA_CD"));
                    vo.setLectureId(lectureId);
                    vo.setRatio(rs.getInt("RATIO"));
                    return vo;
                },
                applyId);
    }

    private boolean shouldFinalize(String yeartermCd) {
        ParsedYearTerm term = parseYearTerm(yeartermCd);
        if (term == null) {
            return false;
        }
        if (term.year() < 2025) {
            return true;
        }
        return term.year() == 2025 && !"REG2".equals(term.termSuffix());
    }

    private LocalDateTime resolveEndAt(String yeartermCd) {
        ParsedYearTerm term = parseYearTerm(yeartermCd);
        if (term == null) {
            return null;
        }
        int month = "REG1".equals(term.termSuffix()) ? 6 : 12;
        int day = "REG1".equals(term.termSuffix()) ? 20 : 18;
        return LocalDateTime.of(term.year(), month, day, 18, 0);
    }

    private ParsedYearTerm parseYearTerm(String yeartermCd) {
        if (yeartermCd == null) {
            return null;
        }
        String[] parts = yeartermCd.split("_", 2);
        if (parts.length != 2) {
            return null;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            return new ParsedYearTerm(year, parts[1]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private record ParsedYearTerm(int year, String termSuffix) {
    }
}
