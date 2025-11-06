package kr.or.jsu.aiDummyDataGenerator.lecture.lecture2026;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
class Lecture2026DummyGeneratorTest {

    private static final String TARGET_TERM = "2026_REG1";
    private static final AtomicLong LECTURE_SEQ = new AtomicLong(-1L);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void cloneApprovedApplicationsIntoLecturesFor2026() {
        purgeExistingLectures();

        List<LctOpenApplyVO> applications = loadApprovedApplications();
        if (applications.isEmpty()) {
            log.warn("{} 학기에 승인된 강의신청이 없습니다.", TARGET_TERM);
            return;
        }

        int lectureRows = 0;
        int weekRows = 0;
        int ratioRows = 0;

        for (LctOpenApplyVO apply : applications) {
            ensureRatioSumIsValid(apply.getLctApplyId());

            String lectureId = nextLectureId();
            LectureVO lecture = buildLecture(apply, lectureId);
            lectureRows += dummyDataMapper.insertOneDummyLecture(lecture);

            List<LctWeekbyVO> weeks = loadWeekTemplates(apply.getLctApplyId(), lectureId);
            if (!weeks.isEmpty()) {
                weekRows += dummyDataMapper.insertDummyLctWeekby(weeks);
            }

            List<LctGraderatioVO> ratios = loadRatioTemplates(apply.getLctApplyId(), lectureId);
            if (!ratios.isEmpty()) {
                ratioRows += dummyDataMapper.insertDummyLctGraderatio(ratios);
            }
        }

        log.info("2026_REG1 강의 생성 완료 - Lecture {}, Week {}, Ratio {}", lectureRows, weekRows, ratioRows);
    }

    private void purgeExistingLectures() {
        List<String> lectureIds = jdbcTemplate.queryForList("""
                        SELECT LECTURE_ID
                        FROM LECTURE
                        WHERE YEARTERM_CD = ?
                        """,
                String.class,
                TARGET_TERM);
        for (String lectureId : lectureIds) {
            jdbcTemplate.update("DELETE FROM LCT_ROOM_SCHEDULE WHERE LECTURE_ID = ?", lectureId);
            jdbcTemplate.update("DELETE FROM LCT_GRADERATIO WHERE LECTURE_ID = ?", lectureId);
            jdbcTemplate.update("DELETE FROM LCT_WEEKBY WHERE LECTURE_ID = ?", lectureId);
            jdbcTemplate.update("DELETE FROM LECTURE WHERE LECTURE_ID = ?", lectureId);
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
                          AND YEARTERM_CD = ?
                        ORDER BY LCT_APPLY_ID
                        """,
                new BeanPropertyRowMapper<>(LctOpenApplyVO.class),
                TARGET_TERM);
    }

    private void ensureRatioSumIsValid(String lctApplyId) {
        List<Integer> ratios = jdbcTemplate.query(
                "SELECT RATIO FROM LCT_APPLY_GRADERATIO WHERE LCT_APPLY_ID = ?",
                (rs, rowNum) -> rs.getInt(1),
                lctApplyId);
        if (ratios.isEmpty()) {
            return;
        }
        int sum = ratios.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (sum != 100) {
            throw new IllegalStateException("평가 비율 합이 100이 아닙니다. applyId=" + lctApplyId + ", sum=" + sum);
        }
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

    private boolean shouldFinalize(String yeartermCd) {
        ParsedYearTerm term = parseYearTerm(yeartermCd);
        if (term == null) {
            return false;
        }
        if (term.year() < 2025) {
            return true;
        }
        if (term.year() == 2025) {
            return !"REG2".equals(term.termSuffix());
        }
        return false;
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

    private String nextLectureId() {
        ensureLectureSeed();
        long next = LECTURE_SEQ.incrementAndGet();
        return String.format(Locale.ROOT, "LECT7%010d", next);
    }

    private void ensureLectureSeed() {
        while (true) {
            long seed = LECTURE_SEQ.get();
            if (seed >= 0) {
                return;
            }
            long maxExisting = fetchMaxLectureSeed();
            if (LECTURE_SEQ.compareAndSet(seed, maxExisting)) {
                return;
            }
        }
    }

    private long fetchMaxLectureSeed() {
        Long max = jdbcTemplate.queryForObject(
                "SELECT NVL(MAX(TO_NUMBER(SUBSTR(LECTURE_ID, 6))), 0) FROM LECTURE WHERE LECTURE_ID LIKE 'LECT7%'",
                Long.class);
        return max == null ? 0L : max;
    }

    private record ParsedYearTerm(int year, String termSuffix) {
    }
}

