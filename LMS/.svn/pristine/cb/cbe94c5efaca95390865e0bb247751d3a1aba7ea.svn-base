package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 5: assign lecture room/time blocks for a lecture.
 * Update the direct input constants before running.
 */
@SpringBootTest
@Slf4j
class Step4_LectureScheduleDummyGeneratorTest {

    // === Direct input values ===
    private static final String LECTURE_PREFIX = "218"; // three-digit chunk provided by caller
    
    private static final String PLACE_CODE = "RM-ENGI-HQ-0401";

    // === Fixed values ===
    private static final List<String> TIMEBLOCK_CODES = List.of(
            "MO1000", "MO1030", "MO1100",
            "TU1000", "TU1030", "TU1100"
    );

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertLectureSchedule() {
        String lectureId = "LECT" + LECTURE_PREFIX + "00000001";
        int inserted = 0;
        for (String timeblock : TIMEBLOCK_CODES) {
            inserted += dummyDataMapper.insertSchedule(lectureId, PLACE_CODE, timeblock);
        }
        assertEquals(TIMEBLOCK_CODES.size(), inserted, "LCT_ROOM_SCHEDULE must receive 6 rows.");
        log.info("Inserted lecture schedule blocks: {}", inserted);
    }
}
