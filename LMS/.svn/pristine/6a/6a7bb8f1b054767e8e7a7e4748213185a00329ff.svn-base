package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LctAttroundVO;
import kr.or.jsu.vo.LctStuAttVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 6: generate attendance rounds and student attendance records.
 * Replace the prefix constants before running.
 */
@SpringBootTest
@Slf4j
class Step6_LectureAttendanceDummyGeneratorTest {

    // === Direct input values ===
    private static final String LECTURE_PREFIX = "218"; // professor-based prefix
    private static final String STUDENT_PREFIX = "318"; // student-based prefix

    // === Fixed values ===
    private static final int STUDENT_COUNT = 30;
    private static final String LECTURE_ID = "LECT" + LECTURE_PREFIX + "00000001";
    private static final List<LocalDate> ATTENDANCE_DATES = List.of(
            LocalDate.of(2025, 7, 28),
            LocalDate.of(2025, 7, 29),
            LocalDate.of(2025, 8, 4),
            LocalDate.of(2025, 8, 5),
            LocalDate.of(2025, 8, 11),
            LocalDate.of(2025, 8, 12),
            LocalDate.of(2025, 8, 18),
            LocalDate.of(2025, 8, 19),
            LocalDate.of(2025, 8, 25),
            LocalDate.of(2025, 8, 26),
            LocalDate.of(2025, 9, 1),
            LocalDate.of(2025, 9, 2),
            LocalDate.of(2025, 9, 8),
            LocalDate.of(2025, 9, 9),
            LocalDate.of(2025, 9, 15),
            LocalDate.of(2025, 9, 16),
            LocalDate.of(2025, 9, 22),
            LocalDate.of(2025, 9, 23),
            LocalDate.of(2025, 9, 29),
            LocalDate.of(2025, 9, 30),
            LocalDate.of(2025, 10, 6),
            LocalDate.of(2025, 10, 7),
            LocalDate.of(2025, 10, 13),
            LocalDate.of(2025, 10, 14),
            LocalDate.of(2025, 10, 20),
            LocalDate.of(2025, 10, 21),
            LocalDate.of(2025, 10, 27),
            LocalDate.of(2025, 10, 28),
            LocalDate.of(2025, 11, 3)
    );

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertLectureAttendance() {
        List<LctAttroundVO> rounds = buildRounds();
        int insertedRounds = dummyDataMapper.insertDummyLctAttrounds(rounds);
        assertEquals(ATTENDANCE_DATES.size(), insertedRounds, "LCT_ATTROUND must receive 29 rows.");

        List<LctStuAttVO> records = buildAttendanceRecords();
        int insertedRecords = dummyDataMapper.insertDummyLctStuAtt(records);
        assertEquals(records.size(), insertedRecords, "LCT_STU_ATT must receive 29 x 29 rows.");

        log.info("Inserted attendance: rounds={}, records={}", insertedRounds, insertedRecords);
    }

    private List<LctAttroundVO> buildRounds() {
        List<LctAttroundVO> rounds = new ArrayList<>(ATTENDANCE_DATES.size());
        for (int i = 0; i < ATTENDANCE_DATES.size(); i++) {
            LctAttroundVO round = new LctAttroundVO();
            round.setLctRound(i + 1);
            round.setLectureId(LECTURE_ID);
            round.setAttDay(ATTENDANCE_DATES.get(i));
            round.setQrcodeFileId(null);
            rounds.add(round);
        }
        return rounds;
    }

    private List<LctStuAttVO> buildAttendanceRecords() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<LctStuAttVO> records = new ArrayList<>(ATTENDANCE_DATES.size() * STUDENT_COUNT);
        for (int roundIndex = 0; roundIndex < ATTENDANCE_DATES.size(); roundIndex++) {
            LocalDate date = ATTENDANCE_DATES.get(roundIndex);
            LocalDateTime attAt = LocalDateTime.of(date, LocalTime.of(10, 0));
            int lctRound = roundIndex + 1;
            for (int studentIndex = 1; studentIndex <= STUDENT_COUNT; studentIndex++) {
                String enrollId = buildEnrollId(studentIndex);
                LctStuAttVO record = new LctStuAttVO();
                record.setLectureId(LECTURE_ID);
                record.setLctRound(lctRound);
                record.setEnrollId(enrollId);
                record.setAttAt(attAt);
                record.setAttStatusCd(resolveStatus(studentIndex, roundIndex, random));
                records.add(record);
            }
        }
        return records;
    }

    private String resolveStatus(int studentIndex, int roundIndex, ThreadLocalRandom random) {
        if (studentIndex == 8) {
            return (roundIndex % 2 == 0) ? "ATTD_OK" : "ATTD_NO";
        }
        double pick = random.nextDouble();
        if (pick < 0.75) {
            return "ATTD_OK";
        } else if (pick < 0.80) {
            return "ATTD_NO";
        } else if (pick < 0.85) {
            return "ATTD_EARLY";
        } else if (pick < 0.95) {
            return "ATTD_LATE";
        } else {
            return "ATTD_EXCP";
        }
    }

    private String buildEnrollId(int sequence) {
        return "STENRL" + STUDENT_PREFIX + String.format("%06d", sequence);
    }
}
