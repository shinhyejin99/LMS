package kr.or.jsu.aiDummyDataGenerator.step7_lecture.lecture2026;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ApprovalVO;
import kr.or.jsu.vo.LctApplyGraderatioVO;
import kr.or.jsu.vo.LctApplyWeekbyVO;
import kr.or.jsu.vo.LctOpenApplyVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step7_5_1_LectureOpenApply2026DummyGeneratorTest {

    private static final String TEMPLATE_TERM = "2025_REG1";
    private static final String TARGET_TERM = "2026_REG1";
    private static final String APPLY_TYPE_LCT_OPEN = "LCT_OPEN";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void cloneApprovedApplicationsFrom2025For2026() {
        List<ApplyTemplate> templates = loadTemplates();
        if (templates.isEmpty()) {
            log.warn("{} 학기에 사용할 원본 강의개설신청 데이터가 없습니다.", TEMPLATE_TERM);
            return;
        }

        purgeExistingTargetApplications();

        AtomicLong applySeq = new AtomicLong(fetchMaxSequence("LCT_OPEN_APPLY", "LCT_APPLY_ID", "LCTAPLY"));
        AtomicLong approvalSeq = new AtomicLong(fetchMaxSequence("APPROVAL", "APPROVE_ID", "APPR"));

        int approvalCount = 0;
        int applyCount = 0;
        int weekCount = 0;
        int ratioCount = 0;

        for (ApplyTemplate template : templates) {
            String newApplyId = nextId("LCTAPLY", applySeq, 7);
            LocalDateTime newApplyAt = adjustApplyAt(template.apply().getApplyAt());

            ApprovalVO approvalCopy = null;
            String newApproveId = null;
            if (template.approval() != null) {
                newApproveId = nextId("APPR", approvalSeq, 10);
                approvalCopy = cloneApproval(template.approval(), newApproveId, newApplyAt);
                approvalCount += dummyDataMapper.insertDummyApproval(approvalCopy);
            }

            LctOpenApplyVO applyCopy = cloneApply(template.apply(), newApplyId, newApproveId, newApplyAt);
            applyCount += dummyDataMapper.insertDummyLctOpenApply(applyCopy);

            List<LctApplyWeekbyVO> weeks = cloneWeeks(template.weeks(), newApplyId);
            if (!weeks.isEmpty()) {
                weekCount += dummyDataMapper.insertDummyLctApplyWeekby(weeks);
            }

            List<LctApplyGraderatioVO> ratios = cloneRatios(template.ratios(), newApplyId);
            if (!ratios.isEmpty()) {
                ratioCount += dummyDataMapper.insertDummyLctApplyGraderatio(ratios);
            }
        }

        log.info("2026_REG1 강의개설신청 복제 완료 - Approval {}, Apply {}, Week {}, Ratio {}",
                approvalCount, applyCount, weekCount, ratioCount);
    }

    private List<ApplyTemplate> loadTemplates() {
        List<LctOpenApplyVO> applies = jdbcTemplate.query("""
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
                TEMPLATE_TERM);
        List<ApplyTemplate> templates = new ArrayList<>();
        for (LctOpenApplyVO apply : applies) {
            ApprovalVO approval = jdbcTemplate.query("""
                            SELECT
                                APPROVE_ID,
                                PREV_APPROVE_ID,
                                USER_ID,
                                APPROVE_YNNULL,
                                APPLY_TYPE_CD,
                                APPLICANT_USER_ID,
                                APPROVE_AT,
                                COMMENTS,
                                ATTACH_FILE_ID
                            FROM APPROVAL
                            WHERE APPROVE_ID = ?
                            """,
                    new BeanPropertyRowMapper<>(ApprovalVO.class),
                    apply.getApproveId())
                    .stream()
                    .findFirst()
                    .orElse(null);
            List<LctApplyWeekbyVO> weeks = jdbcTemplate.query("""
                            SELECT LECTURE_WEEK, LCT_APPLY_ID, WEEK_GOAL, WEEK_DESC
                            FROM LCT_APPLY_WEEKBY
                            WHERE LCT_APPLY_ID = ?
                            ORDER BY LECTURE_WEEK
                            """,
                    (rs, rowNum) -> {
                        LctApplyWeekbyVO vo = new LctApplyWeekbyVO();
                        vo.setLectureWeek(rs.getInt("LECTURE_WEEK"));
                        vo.setLctApplyId(rs.getString("LCT_APPLY_ID"));
                        vo.setWeekGoal(rs.getString("WEEK_GOAL"));
                        vo.setWeekDesc(rs.getString("WEEK_DESC"));
                        return vo;
                    },
                    apply.getLctApplyId());
            List<LctApplyGraderatioVO> ratios = jdbcTemplate.query("""
                            SELECT LCT_APPLY_ID, GRADE_CRITEIRA_CD, RATIO
                            FROM LCT_APPLY_GRADERATIO
                            WHERE LCT_APPLY_ID = ?
                            ORDER BY GRADE_CRITEIRA_CD
                            """,
                    (rs, rowNum) -> {
                        LctApplyGraderatioVO vo = new LctApplyGraderatioVO();
                        vo.setLctApplyId(rs.getString("LCT_APPLY_ID"));
                        vo.setGradeCriteiraCd(rs.getString("GRADE_CRITEIRA_CD"));
                        vo.setRatio(rs.getInt("RATIO"));
                        return vo;
                    },
                    apply.getLctApplyId());
            templates.add(new ApplyTemplate(apply, approval, weeks, ratios));
        }
        return templates;
    }

    private void purgeExistingTargetApplications() {
        List<ApplyIdPair> pairs = jdbcTemplate.query("""
                        SELECT LCT_APPLY_ID, APPROVE_ID
                        FROM LCT_OPEN_APPLY
                        WHERE YEARTERM_CD = ?
                        """,
                (rs, rowNum) -> new ApplyIdPair(rs.getString("LCT_APPLY_ID"), rs.getString("APPROVE_ID")),
                TARGET_TERM);
        for (ApplyIdPair pair : pairs) {
            jdbcTemplate.update("DELETE FROM LCT_APPLY_WEEKBY WHERE LCT_APPLY_ID = ?", pair.applyId());
            jdbcTemplate.update("DELETE FROM LCT_APPLY_GRADERATIO WHERE LCT_APPLY_ID = ?", pair.applyId());
            jdbcTemplate.update("DELETE FROM LCT_OPEN_APPLY WHERE LCT_APPLY_ID = ?", pair.applyId());
        }
        for (ApplyIdPair pair : pairs) {
            if (pair.approveId() != null && !pair.approveId().isBlank()) {
                jdbcTemplate.update("DELETE FROM APPROVAL WHERE APPROVE_ID = ?", pair.approveId());
            }
        }
    }

    private ApprovalVO cloneApproval(ApprovalVO original, String newApproveId, LocalDateTime applyAt) {
        ApprovalVO copy = new ApprovalVO();
        if (original != null) {
            BeanUtils.copyProperties(original, copy);
        }
        copy.setApproveId(newApproveId);
        copy.setPrevApproveId(null);
        copy.setApplyTypeCd(APPLY_TYPE_LCT_OPEN);
        copy.setApproveYnnull("Y");
        if (copy.getUserId() == null || copy.getUserId().isBlank()) {
            copy.setUserId(copy.getApplicantUserId());
        }
        LocalDateTime approvalAt = original != null ? original.getApproveAt() : null;
        copy.setApproveAt(adjustApprovalAt(approvalAt, applyAt));
        return copy;
    }

    private LctOpenApplyVO cloneApply(LctOpenApplyVO original, String newApplyId,
            String newApproveId, LocalDateTime newApplyAt) {
        LctOpenApplyVO copy = new LctOpenApplyVO();
        BeanUtils.copyProperties(original, copy);
        copy.setLctApplyId(newApplyId);
        copy.setYeartermCd(TARGET_TERM);
        copy.setApproveId(newApproveId);
        copy.setApplyAt(newApplyAt);
        copy.setCancelYn("N");
        return copy;
    }

    private List<LctApplyWeekbyVO> cloneWeeks(List<LctApplyWeekbyVO> templates, String newApplyId) {
        List<LctApplyWeekbyVO> result = new ArrayList<>(templates.size());
        for (LctApplyWeekbyVO template : templates) {
            LctApplyWeekbyVO copy = new LctApplyWeekbyVO();
            copy.setLectureWeek(template.getLectureWeek());
            copy.setLctApplyId(newApplyId);
            copy.setWeekGoal(template.getWeekGoal());
            copy.setWeekDesc(template.getWeekDesc());
            result.add(copy);
        }
        return result;
    }

    private List<LctApplyGraderatioVO> cloneRatios(List<LctApplyGraderatioVO> templates, String newApplyId) {
        List<LctApplyGraderatioVO> result = new ArrayList<>(templates.size());
        for (LctApplyGraderatioVO template : templates) {
            LctApplyGraderatioVO copy = new LctApplyGraderatioVO();
            copy.setGradeCriteiraCd(template.getGradeCriteiraCd());
            copy.setLctApplyId(newApplyId);
            copy.setRatio(template.getRatio());
            result.add(copy);
        }
        return result;
    }

    private LocalDateTime adjustApplyAt(LocalDateTime original) {
        if (original == null) {
            return LocalDateTime.of(2025, Month.NOVEMBER, 12, 9, 0);
        }
        return original.plusYears(1);
    }

    private LocalDateTime adjustApprovalAt(LocalDateTime original, LocalDateTime applyAt) {
        if (original == null) {
            return applyAt.plusDays(5);
        }
        return original.plusYears(1);
    }

    private long fetchMaxSequence(String table, String column, String prefix) {
        int start = prefix.length() + 2;
        String sql = String.format(Locale.ROOT,
                "SELECT NVL(MAX(TO_NUMBER(SUBSTR(%s, %d))), 0) FROM %s WHERE %s LIKE ?",
                column, start, table, column);
        Long max = jdbcTemplate.queryForObject(sql, Long.class, prefix + "7%");
        return max == null ? 0L : max;
    }

    private String nextId(String prefix, AtomicLong seq, int numericLength) {
        long next = seq.incrementAndGet();
        String number = String.format(Locale.ROOT, "%0" + numericLength + "d", next);
        return prefix + "7" + number;
    }

    private record ApplyTemplate(
            LctOpenApplyVO apply,
            ApprovalVO approval,
            List<LctApplyWeekbyVO> weeks,
            List<LctApplyGraderatioVO> ratios) {
    }

    private record ApplyIdPair(String applyId, String approveId) {
    }
}
