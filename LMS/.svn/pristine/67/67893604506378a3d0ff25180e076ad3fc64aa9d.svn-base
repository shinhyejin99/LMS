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
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 3-3: 1, 2학년 지도 학생(총 40명)을 생성하여 교수의 상담 대상 풀을 확장한다.
 * 학번은 입학연도(2025/2024)와 USER_PREFIX 조합으로 발급하며 수강 정보는 생성하지 않는다.
 */
@SpringBootTest
@Slf4j
class Step3_3_DummyTeachingGeneratorTest {

    // === Direct input values ===
    private static final String PROF_PREFIX = "218";
    private static final String USER_PREFIX = "318";

    private static final String ADVISOR_PROFESSOR_NO = "2020" + PROF_PREFIX + "1";

    // === Student composition ===
    private static final int FIRST_YEAR_COUNT = 20;
    private static final int SECOND_YEAR_COUNT = 20;
    private static final int STUDENT_COUNT = FIRST_YEAR_COUNT + SECOND_YEAR_COUNT;

    private static final int FIRST_YEAR_ENTRY = 2025;
    private static final int SECOND_YEAR_ENTRY = 2024;
    private static final int FIRST_YEAR_BIRTH = 2006;
    private static final int SECOND_YEAR_BIRTH = 2005;

    // === Fixed values ===
    private static final String BANK_CODE = "BANK_DG";
    private static final String BANK_ACCOUNT = "111111-3333333";
    private static final String ADDR_ID = "ADDR00000000001";
    private static final LocalDateTime CREATE_AT = LocalDateTime.of(2021, 2, 5, 0, 0);
    private static final String PW_HASH = "{bcrypt}$2a$10$1vrhrg0sCry19KPz/pyJ5OmwYQZNw805uL9lYF8dFiVMWgCbodQwi";
    private static final String EMAIL_DOMAIN = "student.lms.ac.kr";
    private static final String STU_STATUS_CD = "ENROLLED";

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

    // 영어 이름 풀 (한글 음차 이름과 순서를 맞추기 위해 동일 인덱스 사용)
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
    void insertAdviseeStudentsWithoutEnrollments() {
        insertStudentUsersAndProfiles();
    }

    private void insertStudentUsersAndProfiles() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<StudentSeed> seeds = buildStudentSeeds();
        List<UsersVO> users = new ArrayList<>(STUDENT_COUNT);
        List<StudentVO> students = new ArrayList<>(STUDENT_COUNT);

        for (int idx = 0; idx < seeds.size(); idx++) {
            int sequence = idx + 1;
            StudentSeed seed = seeds.get(idx);

            boolean isMale = random.nextBoolean();
            NameBundle name = pickName(random);
            BirthBundle birth = generateBirthInfo(random, isMale, seed.birthYear());

            String userId = buildUserId(sequence);
            String studentNo = buildStudentNo(seed.entryYear(), sequence);

            // === USERS (한글) ===
            UsersVO user = new UsersVO();
            user.setUserId(userId);
            user.setPwHash(PW_HASH);
            user.setFirstName(name.givenName());
            user.setLastName(name.lastName());
            user.setRegiNo(buildRegiNo(birth));
            user.setPhotoId(buildPhotoId(sequence));
            user.setAddrId(ADDR_ID);
            user.setMobileNo(generateMobile(random));
            user.setEmail(buildEmail(userId));
            user.setBankCode(BANK_CODE);
            user.setBankAccount(BANK_ACCOUNT);
            user.setCreateAt(CREATE_AT);
            users.add(user);

            // === STUDENT (학적) ===
            StudentVO student = new StudentVO();
            student.setStudentNo(studentNo);
            student.setUserId(userId);
            student.setUnivDeptCd(resolveDepartment(sequence));
            student.setGradeCd(seed.gradeCd());
            student.setStuStatusCd(STU_STATUS_CD);
            student.setEngLname(name.englishSurname());
            student.setEngFname(name.englishGiven());
            student.setGuardName(null);
            student.setGuardRelation(null);
            student.setGuardPhone(null);
            student.setProfessorId(ADVISOR_PROFESSOR_NO);
            students.add(student);
        }

        int insertedUsers = dummyDataMapper.insertDummyUser(users);
        assertEquals(STUDENT_COUNT, insertedUsers, "USERS must receive 40 rows.");

        int insertedStudents = 0;
        for (StudentVO student : students) {
            insertedStudents += dummyDataMapper.insertOneDummyStudent(student);
        }
        assertEquals(STUDENT_COUNT, insertedStudents, "STUDENT must receive 40 rows.");

        log.info("Inserted freshman/sophomore advisees: USERS={}, STUDENT={}", insertedUsers, insertedStudents);
    }

    private List<StudentSeed> buildStudentSeeds() {
        List<StudentSeed> seeds = new ArrayList<>(STUDENT_COUNT);
        for (int i = 0; i < FIRST_YEAR_COUNT; i++) {
            seeds.add(new StudentSeed("1ST", FIRST_YEAR_ENTRY, FIRST_YEAR_BIRTH));
        }
        for (int i = 0; i < SECOND_YEAR_COUNT; i++) {
            seeds.add(new StudentSeed("2ND", SECOND_YEAR_ENTRY, SECOND_YEAR_BIRTH));
        }
        return seeds;
    }

    // === 이름 생성 ===
    private NameBundle pickName(ThreadLocalRandom random) {
        int lastIdx = random.nextInt(KOR_LAST_NAME_POOL.length);
        int firstPrefixIdx = random.nextInt(KOR_FIRST_PREFIX_POOL.length);
        int firstSuffixIdx = random.nextInt(KOR_FIRST_SUFFIX_POOL.length);

        String korLast = KOR_LAST_NAME_POOL[lastIdx];
        String korFirst = KOR_FIRST_PREFIX_POOL[firstPrefixIdx] + KOR_FIRST_SUFFIX_POOL[firstSuffixIdx];

        String engLast = ENG_LAST_NAME_POOL[lastIdx];
        String engFirst = capitalize(ENG_FIRST_PREFIX_POOL[firstPrefixIdx]) + ENG_FIRST_SUFFIX_POOL[firstSuffixIdx];

        return new NameBundle(korLast, korFirst, engLast.toUpperCase(Locale.ROOT), engFirst);
    }

    // === 생년월일 / 주민등록번호 ===
    private BirthBundle generateBirthInfo(ThreadLocalRandom random, boolean isMale, int birthYear) {
        String yearCode = String.format("%02d", birthYear - 2000);
        LocalDate start = LocalDate.of(birthYear, 1, 1);
        LocalDate end = LocalDate.of(birthYear, 12, 31);
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

    // === 보조 빌더 ===
    private String buildUserId(int sequence) {
        return "USER" + USER_PREFIX + "1" + String.format("%07d", sequence);
    }

    private String buildPhotoId(int sequence) {
        return "FILE" + USER_PREFIX + "1" + String.format("%07d", sequence);
    }

    private String buildStudentNo(int entryYear, int sequence) {
        return entryYear + USER_PREFIX + String.format("%02d", sequence);
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
    private record StudentSeed(String gradeCd, int entryYear, int birthYear) {}
}
