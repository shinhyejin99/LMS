package kr.or.jsu.aiDummyDataGenerator.lecture.lecture2026notapproved;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ApprovalVO;
import kr.or.jsu.vo.LctApplyGraderatioVO;
import kr.or.jsu.vo.LctApplyWeekbyVO;
import kr.or.jsu.vo.LctOpenApplyVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class LectureOpenApply2026NotApprovedDummyGeneratorTest {

    private static final String TARGET_TERM = "2026_REG1";
    private static final String TARGET_DEPT = "DEPT-COMP";
    private static final int TARGET_COUNT = 10;
    private static final String APPLY_TYPE_LCT_OPEN = "LCT_OPEN";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertUnapprovedComputerEngineeringApplications() {
        purgeExistingUnapprovedApplications();

        List<SubjectSlice> subjects = loadSubjects();
        if (subjects.isEmpty()) {
            log.warn("컴퓨터공학부 과목 정보를 찾을 수 없습니다.");
            return;
        }

        List<ProfessorSlice> professors = loadProfessors();
        if (professors.isEmpty()) {
            log.warn("컴퓨터공학부 소속 교수 정보를 찾을 수 없습니다.");
            return;
        }

        int applyTarget = Math.min(TARGET_COUNT, subjects.size());
        if (applyTarget < TARGET_COUNT) {
            log.info("과목 수가 부족하여 {}건만 생성합니다.", applyTarget);
        }

        AtomicLong applySeq = new AtomicLong(fetchMaxSequence("LCT_OPEN_APPLY", "LCT_APPLY_ID", "LCTAPLY"));
        AtomicLong approvalSeq = new AtomicLong(fetchMaxSequence("APPROVAL", "APPROVE_ID", "APPR"));

        int approvalRows = 0;
        int applyRows = 0;
        int weekRows = 0;
        int ratioRows = 0;

        for (int i = 0; i < applyTarget; i++) {
            SubjectSlice subject = subjects.get(i);
            ProfessorSlice professor = professors.get(i % professors.size());

            String applyId = nextId("LCTAPLY", applySeq, 7);
            String approveId = nextId("APPR", approvalSeq, 10);
            LocalDateTime applyAt = buildApplyAt(i);

            ApprovalVO approval = buildPendingApproval(professor, approveId);
            approvalRows += dummyDataMapper.insertDummyApproval(approval);

            LctOpenApplyVO apply = new LctOpenApplyVO();
            apply.setLctApplyId(applyId);
            apply.setSubjectCd(subject.subjectCd());
            apply.setProfessorNo(professor.professorNo());
            apply.setYeartermCd(TARGET_TERM);
            apply.setLectureIndex(buildLectureIndex(subject.subjectName(), i));
            apply.setLectureGoal(buildLectureGoal(subject.subjectName()));
            apply.setPrereqSubject("컴퓨터공학 핵심 역량 강화 과목");
            apply.setExpectCap(determineCapacity(subject));
            apply.setDesireOption("공과대 실습실 우선 배정 희망");
            apply.setCancelYn("N");
            apply.setApproveId(approveId);
            apply.setApplyAt(applyAt);

            applyRows += dummyDataMapper.insertDummyLctOpenApply(apply);

            List<LctApplyWeekbyVO> weeks = buildWeeks(applyId, subject.subjectName());
            weekRows += dummyDataMapper.insertDummyLctApplyWeekby(weeks);

            List<LctApplyGraderatioVO> ratios = buildRatios(applyId, subject);
            ratioRows += dummyDataMapper.insertDummyLctApplyGraderatio(ratios);
        }

        log.info("승인 대기 강의신청 생성 완료 - Approval {}, Apply {}, Week {}, Ratio {}",
                approvalRows, applyRows, weekRows, ratioRows);
    }

    private void purgeExistingUnapprovedApplications() {
        List<ApplyApprovalPair> pairs = jdbcTemplate.query("""
                        SELECT A.LCT_APPLY_ID, A.APPROVE_ID
                        FROM LCT_OPEN_APPLY A
                        JOIN SUBJECT S ON A.SUBJECT_CD = S.SUBJECT_CD
                        LEFT JOIN APPROVAL AP ON A.APPROVE_ID = AP.APPROVE_ID
                        WHERE A.YEARTERM_CD = ?
                          AND S.UNIV_DEPT_CD = ?
                          AND (AP.APPROVE_YNNULL IS NULL OR AP.APPROVE_YNNULL = 'N')
                        """,
                (rs, rowNum) -> new ApplyApprovalPair(rs.getString("LCT_APPLY_ID"), rs.getString("APPROVE_ID")),
                TARGET_TERM, TARGET_DEPT);
        for (ApplyApprovalPair pair : pairs) {
            jdbcTemplate.update("DELETE FROM LCT_APPLY_WEEKBY WHERE LCT_APPLY_ID = ?", pair.applyId());
            jdbcTemplate.update("DELETE FROM LCT_APPLY_GRADERATIO WHERE LCT_APPLY_ID = ?", pair.applyId());
            jdbcTemplate.update("DELETE FROM LCT_OPEN_APPLY WHERE LCT_APPLY_ID = ?", pair.applyId());
        }
        for (ApplyApprovalPair pair : pairs) {
            if (pair.approveId() != null && !pair.approveId().isBlank()) {
                jdbcTemplate.update("DELETE FROM APPROVAL WHERE APPROVE_ID = ?", pair.approveId());
            }
        }
    }

    private List<SubjectSlice> loadSubjects() {
        return jdbcTemplate.query("""
                        SELECT SUBJECT_CD, SUBJECT_NAME, COMPLETION_CD, SUBJECT_TYPE_CD, NVL(CREDIT, 3) AS CREDIT, NVL(HOUR, 3) AS HOUR
                        FROM SUBJECT
                        WHERE UNIV_DEPT_CD = ?
                        ORDER BY SUBJECT_CD
                        """,
                (rs, rowNum) -> new SubjectSlice(
                        rs.getString("SUBJECT_CD"),
                        rs.getString("SUBJECT_NAME"),
                        rs.getString("COMPLETION_CD"),
                        rs.getString("SUBJECT_TYPE_CD"),
                        rs.getInt("CREDIT"),
                        rs.getInt("HOUR")),
                TARGET_DEPT);
    }

    private List<ProfessorSlice> loadProfessors() {
        return jdbcTemplate.query("""
                        SELECT PROFESSOR_NO, USER_ID
                        FROM PROFESSOR
                        WHERE UNIV_DEPT_CD = ?
                          AND PRF_STATUS_CD = 'PRF_STATUS_ACTV'
                        ORDER BY PROFESSOR_NO
                        """,
                (rs, rowNum) -> new ProfessorSlice(
                        rs.getString("PROFESSOR_NO"),
                        rs.getString("USER_ID")),
                TARGET_DEPT);
    }

    private LocalDateTime buildApplyAt(int offset) {
        return LocalDateTime.of(2025, Month.NOVEMBER, 18, 10, 0).plusDays(offset * 2L);
    }

    private String buildLectureIndex(String subjectName, int order) {
        return String.format(Locale.ROOT, "%s 실습 중심 설계-%02d", subjectName, order + 1);
    }

    private String buildLectureGoal(String subjectName) {
        return subjectName + " 심화 학습을 통해 프로젝트 수행 능력 향상";
    }

    private int determineCapacity(SubjectSlice subject) {
        int base = switch (subject.completionCd()) {
            case "MAJ_CORE" -> 45;
            case "MAJ_BASIC" -> 50;
            default -> 40;
        };
        return Math.max(base, subject.hour() * 12);
    }

    private List<LctApplyWeekbyVO> buildWeeks(String applyId, String subjectName) {
        List<LctApplyWeekbyVO> weeks = new ArrayList<>(15);
        for (int week = 1; week <= 15; week++) {
            LctApplyWeekbyVO vo = new LctApplyWeekbyVO();
            vo.setLectureWeek(week);
            vo.setLctApplyId(applyId);
            vo.setWeekGoal(String.format(Locale.ROOT, "%s 주차 심화 학습 및 팀 프로젝트 진행", week));
            vo.setWeekDesc(String.format(Locale.ROOT, "%s 주차에는 %s 관련 사례 분석과 실습을 수행합니다.", week, subjectName));
            weeks.add(vo);
        }
        return weeks;
    }

    private List<LctApplyGraderatioVO> buildRatios(String applyId, SubjectSlice subject) {
        List<LctApplyGraderatioVO> ratios = new ArrayList<>();
        if ("SUBJ_PASSFAIL".equals(subject.subjectTypeCd())) {
            ratios.add(new LctApplyGraderatioVO("PRAC", applyId, 70));
            ratios.add(new LctApplyGraderatioVO("ATTD", applyId, 30));
            return ratios;
        }
        ratios.add(new LctApplyGraderatioVO("EXAM", applyId, 40));
        ratios.add(new LctApplyGraderatioVO("TASK", applyId, 30));
        ratios.add(new LctApplyGraderatioVO("ATTD", applyId, 20));
        ratios.add(new LctApplyGraderatioVO("ETC", applyId, 10));
        return ratios;
    }

    private ApprovalVO buildPendingApproval(ProfessorSlice professor, String approveId) {
        String userId = professor.userId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("교수 사용자 ID를 찾을 수 없습니다: " + professor.professorNo());
        }
        ApprovalVO approval = new ApprovalVO();
        approval.setApproveId(approveId);
        approval.setPrevApproveId(null);
        approval.setApplyTypeCd(APPLY_TYPE_LCT_OPEN);
        approval.setApplicantUserId(userId);
        approval.setUserId(userId);
        approval.setApproveYnnull(null);
        approval.setApproveAt(null);
        approval.setComments("승인 대기");
        approval.setAttachFileId(null);
        return approval;
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

    private record SubjectSlice(
            String subjectCd,
            String subjectName,
            String completionCd,
            String subjectTypeCd,
            int credit,
            int hour) {
    }

    private record ProfessorSlice(String professorNo, String userId) {
    }

    private record ApplyApprovalPair(String applyId, String approveId) {
    }
}
