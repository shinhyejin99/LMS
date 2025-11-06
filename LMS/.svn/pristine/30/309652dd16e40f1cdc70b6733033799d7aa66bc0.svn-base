package kr.or.jsu.aiDummyDataGenerator.step7_lecture;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.EnrollGpaVO;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step7_4_StudentEnrollmentDummyGeneratorTest {

    private static final Set<String> ACTIVE_STATUS = Set.of("ENROLLED", "DEFERRED");
    private static final List<String> TARGET_TERMS = List.of("2024_REG1", "2024_REG2", "2025_REG1", "2025_REG2");
    private static final Map<String, Double> GRADE_SCORE = Map.ofEntries(
            Map.entry("A+", 4.5), Map.entry("A0", 4.0),
            Map.entry("B+", 3.5), Map.entry("B0", 3.0),
            Map.entry("C+", 2.5), Map.entry("C0", 2.0),
            Map.entry("D+", 1.5), Map.entry("D0", 1.0),
            Map.entry("F", 0.0),
            Map.entry("P", 0.0), Map.entry("NP", 0.0)
    );
    private static final double FAILURE_RATIO = 0.10;
    private static final double A_RATIO = 0.30;
    private static final double B_RATIO = 0.30;
    private static final Random RANDOM = new Random(2025_11_06_0900L);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertStudentEnrollments() {
        List<StudentInfo> students = loadStudents();
        if (students.isEmpty()) {
            log.warn("학생 데이터가 없어 수강 정보를 생성하지 않습니다.");
            return;
        }

        List<LectureInfo> lectures = loadLectures();
        if (lectures.isEmpty()) {
            log.warn("강의 데이터가 없어 수강 정보를 생성하지 않습니다.");
            return;
        }

        Map<String, List<StudentInfo>> studentsByTerm = buildStudentsByTerm(students);
        if (studentsByTerm.isEmpty()) {
            log.warn("학기별 배정 가능한 학생 목록이 없습니다.");
            return;
        }

        long maxSeq = Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT NVL(MAX(TO_NUMBER(SUBSTR(ENROLL_ID,7))),0) FROM STU_ENROLL_LCT", Long.class)).orElse(0L);
        AtomicLong enrollSeq = new AtomicLong(maxSeq);

        jdbcTemplate.update("DELETE FROM ENROLL_GPA");
        jdbcTemplate.update("DELETE FROM STU_ENROLL_LCT");

        Map<String, Set<String>> subjectHistory = new HashMap<>();

        int enrollments = 0;
        int gpaCount = 0;

        List<LectureInfo> orderedLectures = new ArrayList<>(lectures);
        orderedLectures.sort(this::compareTerms);

        for (LectureInfo lecture : orderedLectures) {
            List<StudentInfo> candidates = new ArrayList<>(studentsByTerm.getOrDefault(lecture.yearTerm(), List.of()));
            if (candidates.isEmpty()) {
                continue;
            }
            candidates.removeIf(student -> !matchesEnrollmentRule(lecture, student));
            if (candidates.isEmpty()) {
                continue;
            }
            Collections.shuffle(candidates, RANDOM);

            int targetSize = (int) Math.ceil(lecture.maxCapacity() * 2 / 3.0);
            targetSize = Math.max(5, targetSize);
            targetSize = Math.min(targetSize, candidates.size());
            if (targetSize <= 0) {
                continue;
            }

            List<StudentInfo> selected = candidates.subList(0, targetSize);
            EnrollmentPlan plan = buildPlan(lecture, targetSize);

            for (int i = 0; i < selected.size(); i++) {
                StudentInfo student = selected.get(i);
                boolean retake = subjectHistory
                        .computeIfAbsent(student.getStudentNo(), ignore -> new LinkedHashSet<>())
                        .contains(lecture.subjectCd());

                EnrollmentResult result = determineResult(lecture, i, plan);
                String enrollId = nextEnrollId(enrollSeq);

                StuEnrollLctVO enroll = new StuEnrollLctVO();
                enroll.setEnrollId(enrollId);
                enroll.setStudentNo(student.getStudentNo());
                enroll.setLectureId(lecture.getLectureId());
                enroll.setEnrollStatusCd(result.status());
                enroll.setRetakeYn(retake ? "Y" : "N");
                enroll.setStatusChangeAt(result.changeAt());
                enroll.setAutoGrade(result.autoGrade());
                enroll.setFinalGrade(result.finalGrade());
                enroll.setChangeReason(result.changeReason());

                                dummyDataMapper.insertOneDummyEnrollment(enroll);
                enrollments++;

                if (result.gpaCd() != null) {
                    dummyDataMapper.insertOneEnrollGpa(new EnrollGpaVO(enrollId, result.gpaCd(), result.changeAt(), result.changeAt()));
                    gpaCount++;
                }

                subjectHistory.get(student.getStudentNo()).add(lecture.subjectCd());
            }
        }

        log.info("학생 수강 데이터 생성 완료 - 수강 {}건, 성적 {}건", enrollments, gpaCount);
    }

    private Map<String, List<StudentInfo>> buildStudentsByTerm(List<StudentInfo> students) {
        Map<String, List<StudentInfo>> map = new LinkedHashMap<>();
        for (String term : TARGET_TERMS) {
            int year = parseYear(term);
            int startYear = year - 3;
            Set<Integer> activeYears = new LinkedHashSet<>();
            for (int y = startYear; y <= year; y++) {
                activeYears.add(y);
            }
            List<StudentInfo> eligible = new ArrayList<>();
            for (StudentInfo student : students) {
                if (activeYears.contains(student.entryYear())) {
                    eligible.add(student);
                }
            }
            map.put(term, eligible);
        }
        return map;
    }

    private EnrollmentPlan buildPlan(LectureInfo lecture, int totalStudents) {
        if (lecture.termSuffix().equals("REG2") && lecture.year() == 2025) {
            return EnrollmentPlan.ongoingPlan();
        }
        int failCount = (int) Math.round(totalStudents * FAILURE_RATIO);
        if (failCount == 0 && totalStudents >= 5) {
            failCount = 1;
        }
        failCount = Math.min(failCount, totalStudents);
        int doneCount = totalStudents - failCount;
        List<String> grades = "SUBJ_PASSFAIL".equals(lecture.subjectTypeCd())
                ? allocatePassFailGrades(doneCount)
                : allocateLetterGrades(doneCount);
        return EnrollmentPlan.completedPlan(failCount, grades);
    }

    private boolean matchesEnrollmentRule(LectureInfo lecture, StudentInfo student) {
        String completion = lecture.completionCdOrDefault();
        if ("MAJ_BASIC".equals(completion)) {
            String lectureCollege = Optional.ofNullable(lecture.getCollegeCd()).orElse("COL-GENR");
            return Objects.equals(lectureCollege, student.collegeCdOrDefault());
        }
        if ("MAJ_CORE".equals(completion) || "MAJ_ELEC".equals(completion)) {
            return Objects.equals(lecture.getUnivDeptCd(), student.getUnivDeptCd());
        }
        return true;
    }

    private EnrollmentResult determineResult(LectureInfo lecture, int index, EnrollmentPlan plan) {
        boolean isPassFail = "SUBJ_PASSFAIL".equals(lecture.subjectTypeCd());

        if (plan.inProgress()) {
            return EnrollmentResult.ongoing(LocalDateTime.now());
        }

        LocalDateTime changeAt = resolveStatusChangeAt(lecture.yearTerm());

        if (index < plan.failureCount()) {
            String gradeCode = isPassFail ? "NP" : "F";
            Double score = GRADE_SCORE.get(gradeCode);
            return EnrollmentResult.of("ENR_FAILURE", gradeCode, score, score, changeReason(score, score), changeAt);
        }

        String gradeCode = plan.grades().get(index - plan.failureCount());
        Double auto = GRADE_SCORE.get(gradeCode);
        Double fin = auto;
        if (!isPassFail && auto != null && RANDOM.nextDouble() < 0.1) {
            fin = Math.max(0.0, Math.min(4.5, auto + (RANDOM.nextBoolean() ? 0.5 : -0.5)));
            fin = Math.round(fin * 2) / 2.0;
        }
        String reason = changeReason(auto, fin);
        return EnrollmentResult.of("ENR_DONE", gradeCode, auto, fin, reason, changeAt);
    }

    private List<String> allocateLetterGrades(int doneCount) {
        List<String> grades = new ArrayList<>();
        int aCount = (int) Math.round(doneCount * A_RATIO);
        int bCount = (int) Math.round(doneCount * B_RATIO);
        if (aCount + bCount > doneCount) {
            bCount = Math.max(0, doneCount - aCount);
        }
        int remaining = doneCount - aCount - bCount;
        int cCount = (int) Math.round(remaining * 0.6);
        int dCount = remaining - cCount;

        fillGrades(grades, aCount, List.of("A+", "A0"));
        fillGrades(grades, bCount, List.of("B+", "B0"));
        fillGrades(grades, cCount, List.of("C+", "C0"));
        fillGrades(grades, dCount, List.of("D+", "D0"));

        while (grades.size() < doneCount) {
            grades.add("C0");
        }
        Collections.shuffle(grades, RANDOM);
        return grades;
    }

    private List<String> allocatePassFailGrades(int doneCount) {
        return new ArrayList<>(Collections.nCopies(doneCount, "P"));
    }

    private void fillGrades(List<String> target, int count, List<String> pool) {
        for (int i = 0; i < count; i++) {
            target.add(pool.get(i % pool.size()));
        }
    }

    private String changeReason(Double auto, Double fin) {
        if (Objects.equals(auto, fin)) {
            return "1차 확정한 성적과 같음";
        }
        return "성적 재검토로 조정됨";
    }

    private List<StudentInfo> loadStudents() {
        return jdbcTemplate.query("""
                        SELECT
                            S.STUDENT_NO,
                            S.STU_STATUS_CD,
                            S.UNIV_DEPT_CD,
                            NVL(D.COLLEGE_CD, 'COL-GENR') AS COLLEGE_CD
                        FROM STUDENT S
                        LEFT JOIN UNIV_DEPT D ON S.UNIV_DEPT_CD = D.UNIV_DEPT_CD
                        """,
                new BeanPropertyRowMapper<>(StudentInfo.class))
                .stream()
                .filter(s -> ACTIVE_STATUS.contains(s.getStuStatusCd()))
                .filter(s -> s.entryYear() > 0)
                .toList();
    }

    private List<LectureInfo> loadLectures() {
        return jdbcTemplate.query("""
                        SELECT
                            L.LECTURE_ID,
                            L.YEARTERM_CD,
                            L.PROFESSOR_NO,
                            NVL(L.MAX_CAP, 30) AS MAX_CAP,
                            S.SUBJECT_CD,
                            NVL(S.SUBJECT_TYPE_CD, 'SUBJ_ABSOLUTE') AS SUBJECT_TYPE_CD,
                            S.COMPLETION_CD,
                            NVL(S.CREDIT, 3) AS CREDIT,
                            NVL(S.HOUR, 3) AS HOUR,
                            S.UNIV_DEPT_CD,
                            NVL(D.COLLEGE_CD, 'COL-GENR') AS COLLEGE_CD
                        FROM LECTURE L
                        JOIN SUBJECT S ON L.SUBJECT_CD = S.SUBJECT_CD
                        LEFT JOIN UNIV_DEPT D ON S.UNIV_DEPT_CD = D.UNIV_DEPT_CD
                        WHERE L.CANCEL_YN = 'N'
                          AND L.YEARTERM_CD IN ('2024_REG1','2024_REG2','2025_REG1','2025_REG2')
                        """,
                new BeanPropertyRowMapper<>(LectureInfo.class));
    }

    private int compareTerms(LectureInfo a, LectureInfo b) {
        int yearCompare = Integer.compare(a.year(), b.year());
        if (yearCompare != 0) {
            return yearCompare;
        }
        return a.termSuffix().compareTo(b.termSuffix());
    }

    private LocalDateTime resolveStatusChangeAt(String yearTerm) {
        int year = parseYear(yearTerm);
        String suffix = yearTerm.substring(5);
        Month month = suffix.equals("REG1") ? Month.JULY : Month.DECEMBER;
        int day = suffix.equals("REG1") ? 5 : 22;
        return LocalDateTime.of(LocalDate.of(year, month, day), LocalDateTime.now().toLocalTime().withHour(17).withMinute(0));
    }

    private int parseYear(String yearTerm) {
        return Integer.parseInt(yearTerm.substring(0, 4));
    }

    private String nextEnrollId(AtomicLong seq) {
        long next = seq.incrementAndGet();
        return String.format(Locale.ROOT, "STENRL%09d", next);
    }

    @Data
    public static class StudentInfo {
        private String studentNo;
        private String stuStatusCd;
        private String univDeptCd;
        private String collegeCd;

        public int entryYear() {
            try {
                return Integer.parseInt(studentNo.substring(0, 4));
            } catch (Exception ex) {
                return 0;
            }
        }

        public String collegeCdOrDefault() {
            return Optional.ofNullable(collegeCd).orElse("COL-GENR");
        }
    }

    @Data
    public static class LectureInfo {
        private String lectureId;
        private String yeartermCd;
        private String professorNo;
        private Integer maxCap;
        private String subjectCd;
        private String subjectTypeCd;
        private String completionCd;
        private Integer credit;
        private Integer hour;
        private String univDeptCd;
        private String collegeCd;

        public String yearTerm() {
            return yeartermCd;
        }

        public int year() {
            return Integer.parseInt(yeartermCd.substring(0, 4));
        }

        public String termSuffix() {
            return yeartermCd.substring(5);
        }

        public int maxCapacity() {
            return Optional.ofNullable(maxCap).orElse(30);
        }

        public String subjectTypeCd() {
            return Optional.ofNullable(subjectTypeCd).orElse("SUBJ_ABSOLUTE");
        }

        public int creditOrDefault() {
            return Optional.ofNullable(credit).orElse(3);
        }

        public int contactHours() {
            return Optional.ofNullable(hour).orElse(Math.max(creditOrDefault(), 2));
        }

        public String subjectCd() {
            return subjectCd;
        }

        public String completionCdOrDefault() {
            return Optional.ofNullable(completionCd).orElse("");
        }
    }

    private record EnrollmentPlan(boolean inProgress, int failureCount, List<String> grades) {
        static EnrollmentPlan ongoingPlan() {
            return new EnrollmentPlan(true, 0, List.of());
        }

        static EnrollmentPlan completedPlan(int failureCount, List<String> grades) {
            return new EnrollmentPlan(false, failureCount, grades);
        }

        public boolean inProgress() {
            return inProgress;
        }
    }

    private record EnrollmentResult(String status, String gpaCd, Double autoGrade, Double finalGrade,
                                    String changeReason, LocalDateTime changeAt) {
        static EnrollmentResult ongoing(LocalDateTime changeAt) {
            return new EnrollmentResult("ENR_ING", null, null, null, null, changeAt);
        }

        static EnrollmentResult of(String status, String gpaCd, Double autoGrade, Double finalGrade,
                                   String changeReason, LocalDateTime changeAt) {
            return new EnrollmentResult(status, gpaCd, autoGrade, finalGrade, changeReason, changeAt);
        }

        public LocalDateTime changeAt() {
            return changeAt;
        }
    }
}
