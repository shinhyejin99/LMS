package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.StuEnrollLctVO;
import kr.or.jsu.vo.StuGraduationVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 3: 30개의 학생이 될 사용자, 학생 정보, 수강 정보를 넣는다.
 * USERS → 한글 이름 / STUDENT → 영어 이름 (랜덤 조합이지만 1:1 일치)
 */
@SpringBootTest
@Slf4j
class Step3_1_StudentDummyGeneratorTest {

    // === Direct input values ===
    private static final String PROF_PREFIX = "218"; // three-digit chunk provided by caller
    private static final String USER_PREFIX = "318"; // three-digit chunk provided by caller

    private static final String ADVISOR_PROFESSOR_NO = "2020" + PROF_PREFIX + "1";
    private static final String TARGET_LECTURE_ID = "LECT" + PROF_PREFIX + "00000001";

    // === Fixed values ===
    private static final int STUDENT_COUNT = 30;
    private static final String BANK_CODE = "BANK_DG";
    private static final String BANK_ACCOUNT = "111111-3333333";
    private static final String ADDR_ID = "ADDR00000000001";
    private static final String DEFAULT_EXPECTED_YEARTERM = "2027_REG2";
    private static final LocalDateTime CREATE_AT = LocalDateTime.of(2020, 2, 5, 0, 0);
    private static final String PW_HASH = "{bcrypt}$2a$10$1vrhrg0sCry19KPz/pyJ5OmwYQZNw805uL9lYF8dFiVMWgCbodQwi";
    private static final String EMAIL_DOMAIN = "student.lms.ac.kr";
    private static final String GRADE_CD = "3RD";
    private static final String STU_STATUS_CD = "ENROLLED";
    private static final String ENROLL_STATUS_CD = "ENR_ING";
    private static final String RETAKE_YN = "N";

    private static final String DEPT_DEFAULT = "DEP-ENGI-CSE";
    private static final String[] DEPT_SUFFIX = { "DEP-ENGI-EE", "DEP-ENGI-ME", "DEP-ENGI-CE", "DEP-ENGI-IE" };

    @Autowired
    private DummyDataMapper dummyDataMapper;

    // === 이름 풀 ===
    private static final String[] KOR_LAST_NAME_POOL = {
        "김", "이", "박", "최", "정", "조", "윤", "장", "임", "한",
        "오", "서", "신", "권", "황"
    };

    private static final String[] KOR_FIRST_PREFIX_POOL = {
        "지", "민", "서", "하", "연", "수", "도", "유", "나", "준",
        "윤", "채", "예", "아", "건", "규", "선", "다", "라", "현"
    };

    private static final String[] KOR_FIRST_SUFFIX_POOL = {
        "민", "서", "준", "아", "연", "빈", "영", "리", "인", "솔",
        "윤", "지", "후", "린", "혁", "결", "온", "율", "담", "휘"
    };

    // 영어 대응 풀 (같은 인덱스 조합으로 로마자 표기 일관)
    private static final String[] ENG_LAST_NAME_POOL = {
        "Kim", "Lee", "Park", "Choi", "Jung", "Cho", "Yoon", "Jang", "Lim", "Han",
        "Oh", "Seo", "Shin", "Kwon", "Hwang"
    };

    private static final String[] ENG_FIRST_PREFIX_POOL = {
        "Ji", "Min", "Seo", "Ha", "Yeon", "Soo", "Do", "Yoo", "Na", "Jun",
        "Yoon", "Chae", "Ye", "Ah", "Gun", "Gyu", "Sun", "Da", "Ra", "Hyun"
    };

    private static final String[] ENG_FIRST_SUFFIX_POOL = {
        "min", "seo", "jun", "ah", "yeon", "bin", "young", "ri", "in", "sol",
        "yoon", "ji", "hoo", "rin", "hyuk", "gyeol", "on", "yul", "dam", "hwi"
    };

    @Test
    void insertStudentsAndEnrollments() {
        List<StudentVO> students = insertStudentUsersAndProfiles();
        insertStudentEnrollments(students);
    }

    private List<StudentVO> insertStudentUsersAndProfiles() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<UsersVO> users = new ArrayList<>(STUDENT_COUNT);
        List<StudentVO> students = new ArrayList<>(STUDENT_COUNT);
        List<StuGraduationVO> graduations = new ArrayList<>(STUDENT_COUNT);

        for (int idx = 1; idx <= STUDENT_COUNT; idx++) {
            boolean isMale = random.nextBoolean();
            NameBundle name = pickName(random);
            BirthBundle birth = generateBirthInfo(random, isMale);

            String userId = buildUserId(idx);
            String studentNo = buildStudentNo(idx);

            // === USERS (한글) ===
            UsersVO user = new UsersVO();
            user.setUserId(userId);
            user.setPwHash(PW_HASH);
            user.setFirstName(name.givenName());  // 한글 이름
            user.setLastName(name.lastName());    // 한글 성
            user.setRegiNo(buildRegiNo(birth));
            user.setPhotoId(buildPhotoId(idx));
            user.setAddrId(ADDR_ID);
            user.setMobileNo(generateMobile(random));
            user.setEmail(buildEmail(userId));
            user.setBankCode(BANK_CODE);
            user.setBankAccount(BANK_ACCOUNT);
            user.setCreateAt(CREATE_AT);
            users.add(user);

            // === STUDENT (영문) ===
            StudentVO student = new StudentVO();
            student.setStudentNo(studentNo);
            student.setUserId(userId);
            student.setUnivDeptCd(resolveDepartment(idx));
            student.setGradeCd(GRADE_CD);
            student.setStuStatusCd(STU_STATUS_CD);
            student.setEngLname(name.englishSurname());
            student.setEngFname(name.englishGiven());
            student.setGuardName(null);
            student.setGuardRelation(null);
            student.setGuardPhone(null);
            student.setProfessorId(ADVISOR_PROFESSOR_NO);
            students.add(student);

            StuGraduationVO graduation = new StuGraduationVO();
            graduation.setStudentNo(studentNo);
            graduation.setExpectedYeartermCd(DEFAULT_EXPECTED_YEARTERM);
            graduation.setUpdateAt(CREATE_AT);
            graduations.add(graduation);
        }

        int insertedUsers = dummyDataMapper.insertDummyUser(users);
        assertEquals(STUDENT_COUNT, insertedUsers, "USERS must receive 30 rows.");

        int insertedStudents = 0;
        for (StudentVO student : students) {
            insertedStudents += dummyDataMapper.insertOneDummyStudent(student);
        }
        assertEquals(STUDENT_COUNT, insertedStudents, "STUDENT must receive 30 rows.");

        insertGraduationRecords(graduations);

        log.info("Inserted student users and profiles: USERS={}, STUDENT={}", insertedUsers, insertedStudents);
        return students;
    }

    private void insertStudentEnrollments(List<StudentVO> students) {
        List<StuEnrollLctVO> enrollments = new ArrayList<>(students.size());
        for (int idx = 0; idx < students.size(); idx++) {
            StudentVO student = students.get(idx);
            StuEnrollLctVO enrollment = new StuEnrollLctVO();
            enrollment.setEnrollId(buildEnrollId(idx + 1));
            enrollment.setStudentNo(student.getStudentNo());
            enrollment.setLectureId(TARGET_LECTURE_ID);
            enrollment.setEnrollStatusCd(ENROLL_STATUS_CD);
            enrollment.setRetakeYn(RETAKE_YN);
            enrollment.setStatusChangeAt(LocalDateTime.now());
            enrollment.setAutoGrade(null);
            enrollment.setFinalGrade(null);
            enrollment.setChangeReason(null);
            enrollments.add(enrollment);
        }

        int inserted = 0;
        for (StuEnrollLctVO enrollment : enrollments) {
            inserted += dummyDataMapper.insertOneDummyEnrollment(enrollment);
        }
        assertEquals(students.size(), inserted, "STU_ENROLL_LCT must receive 30 rows.");
        log.info("Inserted student enrollments: {}", inserted);
    }

    private void insertGraduationRecords(List<StuGraduationVO> graduations) {
        int inserted = dummyDataMapper.insertDummyStuGraduations(graduations);
        assertEquals(graduations.size(), inserted, "STU_GRADUATION must receive 30 rows.");
        log.info("Inserted student graduation records: {}", inserted);
    }

    // === 이름 생성 ===
    private NameBundle pickName(ThreadLocalRandom random) {
        int lastIdx = random.nextInt(KOR_LAST_NAME_POOL.length);
        int firstPrefixIdx = random.nextInt(KOR_FIRST_PREFIX_POOL.length);
        int firstSuffixIdx = random.nextInt(KOR_FIRST_SUFFIX_POOL.length);

        // 한글 조합
        String korLast = KOR_LAST_NAME_POOL[lastIdx];
        String korFirst = KOR_FIRST_PREFIX_POOL[firstPrefixIdx] + KOR_FIRST_SUFFIX_POOL[firstSuffixIdx];

        // 영어 조합 (동일 인덱스 대응)
        String engLast = ENG_LAST_NAME_POOL[lastIdx];
        String engFirst = capitalize(ENG_FIRST_PREFIX_POOL[firstPrefixIdx]) + ENG_FIRST_SUFFIX_POOL[firstSuffixIdx];

        return new NameBundle(korLast, korFirst, engLast.toUpperCase(Locale.ROOT), engFirst);
    }

    // === 생년월일 / 주민번호 ===
    private BirthBundle generateBirthInfo(ThreadLocalRandom random, boolean isMale) {
        String yearCode = isMale ? "02" : "04";
        int year = 2000 + Integer.parseInt(yearCode);
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        long days = end.toEpochDay() - start.toEpochDay() + 1;
        LocalDate birthDate = start.plusDays(random.nextLong(days));
        return new BirthBundle(yearCode, birthDate, isMale);
    }

    private String buildRegiNo(BirthBundle birth) {
        String mmdd = String.format("%02d%02d", birth.date().getMonthValue(), birth.date().getDayOfMonth());
        String genderDigit = birth.isMale() ? "3" : "4";
        String prefix = birth.yearCode() + mmdd + genderDigit;
        int tail = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return prefix + String.format("%06d", tail);
    }

    // === 식별자 생성 ===
    private String buildUserId(int sequence) {
        return "USER" + USER_PREFIX + String.format("%08d", sequence);
    }

    private String buildPhotoId(int sequence) {
        return "FILE999" + String.format("%08d", sequence);
        
    }

    private String buildStudentNo(int sequence) {
        return "2023" + USER_PREFIX + String.format("%02d", sequence);
    }

    private String buildEnrollId(int sequence) {
        return "STENRL" + USER_PREFIX + String.format("%06d", sequence);
    }

    private String buildEmail(String userId) {
        return userId.toLowerCase(Locale.ROOT) + "@" + EMAIL_DOMAIN;
    }

    private String generateMobile(ThreadLocalRandom random) {
        return "010" + String.format("%08d", random.nextInt(0, 100_000_000));
    }

    private String resolveDepartment(int index) {
        if (index <= 25) {
            return DEPT_DEFAULT;
        }
        int extraIdx = (index - 26) % DEPT_SUFFIX.length;
        return DEPT_SUFFIX[extraIdx];
    }

    private String capitalize(String token) {
        if (token == null || token.isEmpty()) return token;
        String lower = token.toLowerCase(Locale.ROOT);
        return lower.substring(0, 1).toUpperCase(Locale.ROOT) + lower.substring(1);
    }

    // === 내부 record ===
    private record NameBundle(String lastName, String givenName, String englishSurname, String englishGiven) {}
    private record BirthBundle(String yearCode, LocalDate date, boolean isMale) {}
}
