package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 3-1: 기존 학생(30명)에 대해 1학년 1학기~3학년 1학기 수강완료 강의 35개,
 * 3학년 2학기 진행중 강의 6개를 등록한다.
 */
@SpringBootTest
@Slf4j
class Step3_2_DummyEnrollGeneratorTest {

    private static final String USER_PREFIX = "318";
    
    private static final int STUDENT_COUNT = 30;
    private static final String CURRENT_YEARTERM = "2025_REG2";
    private static final String STATUS_DONE = "ENR_DONE";
    private static final String STATUS_ING = "ENR_ING";
    private static final String RETAKE_YN = "N";
    private static final int EXTENDED_PREFIX = Integer.parseInt(USER_PREFIX) + 999;
    private static final List<String> COMPLETED_LECTURE_IDS = buildLectureIds(1, 35);
    private static final List<String> ONGOING_LECTURE_IDS = buildLectureIds(36, 41);
    private static final double[] GRADE_POOL = { 4.5, 4.3, 4.0, 3.7, 3.5, 3.2 };
    private static final LocalDateTime COMPLETED_STATUS_AT = LocalDateTime.of(2024, 6, 30, 18, 0);
    private static final LocalDateTime ONGOING_STATUS_AT = LocalDateTime.of(2024, 9, 1, 9, 0);

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertHistoricalAndCurrentEnrollments() {
        int cleared = dummyDataMapper.clearLectureEndAtByYearterm(CURRENT_YEARTERM);
        log.info("Cleared END_AT for yearterm {}: {}", CURRENT_YEARTERM, cleared);

        List<String> studentNos = buildStudentNos();
        List<StuEnrollLctVO> enrollments = new ArrayList<>(
                studentNos.size() * (COMPLETED_LECTURE_IDS.size() + ONGOING_LECTURE_IDS.size()));

        int sequence = 1;
        for (int stuIdx = 0; stuIdx < studentNos.size(); stuIdx++) {
            String studentNo = studentNos.get(stuIdx);

            for (int lectIdx = 0; lectIdx < COMPLETED_LECTURE_IDS.size(); lectIdx++) {
                String lectureId = COMPLETED_LECTURE_IDS.get(lectIdx);
                StuEnrollLctVO enrollment = new StuEnrollLctVO();
                enrollment.setEnrollId(buildHistoricalEnrollId(sequence++));
                enrollment.setStudentNo(studentNo);
                enrollment.setLectureId(lectureId);
                enrollment.setEnrollStatusCd(STATUS_DONE);
                enrollment.setRetakeYn(RETAKE_YN);
                enrollment.setStatusChangeAt(COMPLETED_STATUS_AT);
                double grade = pickGrade(stuIdx, lectIdx);
                enrollment.setAutoGrade(grade);
                enrollment.setFinalGrade(grade);
                enrollment.setChangeReason(null);
                enrollments.add(enrollment);
            }

            for (int lectIdx = 0; lectIdx < ONGOING_LECTURE_IDS.size(); lectIdx++) {
                String lectureId = ONGOING_LECTURE_IDS.get(lectIdx);
                StuEnrollLctVO enrollment = new StuEnrollLctVO();
                enrollment.setEnrollId(buildHistoricalEnrollId(sequence++));
                enrollment.setStudentNo(studentNo);
                enrollment.setLectureId(lectureId);
                enrollment.setEnrollStatusCd(STATUS_ING);
                enrollment.setRetakeYn(RETAKE_YN);
                enrollment.setStatusChangeAt(ONGOING_STATUS_AT);
                enrollment.setAutoGrade(null);
                enrollment.setFinalGrade(null);
                enrollment.setChangeReason(null);
                enrollments.add(enrollment);
            }
        }

        int expected = enrollments.size();
        int inserted = 0;
        for (StuEnrollLctVO enrollment : enrollments) {
            inserted += dummyDataMapper.insertOneDummyEnrollment(enrollment);
        }

        assertEquals(expected, inserted, "STU_ENROLL_LCT must receive the expected number of rows.");
        log.info("Inserted historical/ongoing enrollments: {}", inserted);
    }

    private static List<String> buildLectureIds(int start, int endInclusive) {
        return Collections.unmodifiableList(
                IntStream.rangeClosed(start, endInclusive)
                        .mapToObj(idx -> String.format("LECT999%08d", idx))
                        .collect(Collectors.toList()));
    }

    private List<String> buildStudentNos() {
        return IntStream.rangeClosed(1, STUDENT_COUNT)
                .mapToObj(this::buildStudentNo)
                .collect(Collectors.toList());
    }

    private String buildStudentNo(int sequence) {
        return "2023" + USER_PREFIX + String.format("%02d", sequence);
    }

    private String buildHistoricalEnrollId(int sequence) {
        return "STENRL" + EXTENDED_PREFIX + String.format("%05d", sequence);
    }

    private double pickGrade(int studentIndex, int lectureIndex) {
        int slot = Math.abs(studentIndex + lectureIndex) % GRADE_POOL.length;
        return GRADE_POOL[slot];
    }
}
