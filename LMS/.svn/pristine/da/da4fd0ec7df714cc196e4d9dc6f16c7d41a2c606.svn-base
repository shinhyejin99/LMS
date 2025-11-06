package kr.or.jsu.aiDummyDataGenerator.step7_lecture.lecture2026;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step7_5_3_LectureSchedule2026DummyGeneratorTest {

    private static final String TARGET_TERM = "2026_REG1";
    private static final List<String> DAYS = List.of("MO", "TU", "WE", "TH", "FR");
    private static final int DESIRED_BLOCKS_PER_SESSION = 3;
    private static final int MIN_BLOCKS_PER_SESSION = 2;
    private static final int SESSIONS_PER_WEEK = 2;
    private static final Random RANDOM = new Random(2025_11_15_0900L);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void assignSchedulesFor2026Spring() {
        purgeExistingSchedules();

        List<LectureSummary> lectures = loadLectures();
        if (lectures.isEmpty()) {
            log.warn("{} 학기 강의가 존재하지 않습니다.", TARGET_TERM);
            return;
        }

        List<PlaceSummary> classrooms = loadClassrooms();
        if (classrooms.isEmpty()) {
            log.warn("배정 가능한 강의실이 없습니다.");
            return;
        }

        Map<String, List<String>> blocksByDay = loadTimeBlocks();
        if (blocksByDay.values().stream().anyMatch(List::isEmpty)) {
            log.warn("요일별 시간블록 정보가 충분하지 않습니다.");
            return;
        }

        Map<String, Integer> dayRotation = new LinkedHashMap<>();
        Set<String> professorBusy = new LinkedHashSet<>();
        Set<String> placeBusy = new LinkedHashSet<>();
        Set<String> lectureBusy = new LinkedHashSet<>();

        int totalSessions = 0;
        int skippedSessions = 0;

        for (LectureSummary lecture : lectures) {
            int sessionsAssigned = 0;
            List<PlaceSummary> candidateRooms = selectRooms(classrooms, lecture.maxCap());

            for (int session = 0; session < SESSIONS_PER_WEEK; session++) {
                Optional<ScheduledSlot> slot = scheduleOneSession(
                        lecture,
                        candidateRooms,
                        blocksByDay,
                        dayRotation,
                        professorBusy,
                        placeBusy,
                        lectureBusy);
                if (slot.isPresent()) {
                    persistSlot(slot.get());
                    sessionsAssigned++;
                } else {
                    skippedSessions++;
                    log.warn("강의 {} ({})의 {}번째 회차를 배정하지 못했습니다.", lecture.lectureId(), lecture.yearTerm(), session + 1);
                }
            }
            totalSessions += sessionsAssigned;
        }

        log.info("2026_REG1 시간표 배정 완료 - 배정된 세션 {}, 누락된 세션 {}", totalSessions, skippedSessions);
    }

    private void purgeExistingSchedules() {
        jdbcTemplate.update("""
                DELETE FROM LCT_ROOM_SCHEDULE
                WHERE LECTURE_ID IN (
                    SELECT LECTURE_ID FROM LECTURE WHERE YEARTERM_CD = ?
                )
                """, TARGET_TERM);
    }

    private Optional<ScheduledSlot> scheduleOneSession(
            LectureSummary lecture,
            List<PlaceSummary> candidateRooms,
            Map<String, List<String>> blocksByDay,
            Map<String, Integer> dayRotation,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {

        int startDayIdx = dayRotation.computeIfAbsent(lecture.yearTerm(), k -> RANDOM.nextInt(DAYS.size()));

        for (int blocksPerSession = DESIRED_BLOCKS_PER_SESSION; blocksPerSession >= MIN_BLOCKS_PER_SESSION; blocksPerSession--) {
            List<String> dayOrder = new ArrayList<>();
            for (int offset = 0; offset < DAYS.size(); offset++) {
                dayOrder.add(DAYS.get((startDayIdx + offset) % DAYS.size()));
            }
            for (String day : dayOrder) {
                List<String> blocks = blocksByDay.get(day);
                if (blocks.size() < blocksPerSession) {
                    continue;
                }
                List<Integer> startIndices = buildStartIndices(blocks.size(), blocksPerSession);
                for (int startIdx : startIndices) {
                    List<String> candidateBlocks = blocks.subList(startIdx, startIdx + blocksPerSession);
                    for (PlaceSummary room : candidateRooms) {
                        if (!isAvailable(room, lecture, candidateBlocks, professorBusy, placeBusy, lectureBusy)) {
                            continue;
                        }
                        ScheduledSlot slot = new ScheduledSlot(lecture.lectureId(), lecture.yearTerm(),
                                lecture.professorNo(), room.placeCd(), new ArrayList<>(candidateBlocks));
                        reserve(slot, professorBusy, placeBusy, lectureBusy);
                        return Optional.of(slot);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean isAvailable(
            PlaceSummary room,
            LectureSummary lecture,
            List<String> timeBlocks,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {
        for (String block : timeBlocks) {
            String professorKey = key(lecture.yearTerm(), lecture.professorNo(), block);
            if (professorBusy.contains(professorKey)) {
                return false;
            }
            String placeKey = key(lecture.yearTerm(), room.placeCd(), block);
            if (placeBusy.contains(placeKey)) {
                return false;
            }
            String lectureKey = key(lecture.yearTerm(), lecture.lectureId(), block);
            if (lectureBusy.contains(lectureKey)) {
                return false;
            }
        }
        return true;
    }

    private void reserve(ScheduledSlot slot,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {
        for (String block : slot.timeBlocks()) {
            professorBusy.add(key(slot.yearTerm(), slot.professorNo(), block));
            placeBusy.add(key(slot.yearTerm(), slot.placeCd(), block));
            lectureBusy.add(key(slot.yearTerm(), slot.lectureId(), block));
        }
    }

    private void persistSlot(ScheduledSlot slot) {
        for (String block : slot.timeBlocks()) {
            dummyDataMapper.insertSchedule(slot.lectureId(), slot.placeCd(), block);
        }
    }

    private List<Integer> buildStartIndices(int totalBlocks, int blocksPerSession) {
        List<Integer> indices = new ArrayList<>();
        for (int start = 0; start <= totalBlocks - blocksPerSession; start++) {
            indices.add(start);
        }
        Collections.shuffle(indices, RANDOM);
        return indices;
    }

    private Map<String, List<String>> loadTimeBlocks() {
        List<String> timeblocks = jdbcTemplate.queryForList(
                "SELECT TIMEBLOCK_CD FROM TIMEBLOCK ORDER BY TIMEBLOCK_CD",
                String.class);
        Map<String, List<String>> byDay = new LinkedHashMap<>();
        for (String day : DAYS) {
            byDay.put(day, new ArrayList<>());
        }
        for (String block : timeblocks) {
            if (block.length() < 4) {
                continue;
            }
            String day = block.substring(0, 2);
            List<String> list = byDay.get(day);
            if (list != null) {
                list.add(block);
            }
        }
        return byDay;
    }

    private List<PlaceSummary> loadClassrooms() {
        return jdbcTemplate.query("""
                        SELECT PLACE_CD, NVL(CAPACITY, 20) AS CAPACITY, PLACE_USAGE_CD
                        FROM PLACE
                        WHERE PLACE_TYPE_CD = 'ROOM'
                          AND USING_YN = 'Y'
                          AND PLACE_USAGE_CD IN ('CLASSROOM', 'LAB')
                        ORDER BY CAPACITY DESC, PLACE_CD
                        """,
                (rs, rowNum) -> new PlaceSummary(
                        rs.getString("PLACE_CD"),
                        rs.getInt("CAPACITY"),
                        rs.getString("PLACE_USAGE_CD")));
    }

    private List<PlaceSummary> selectRooms(List<PlaceSummary> rooms, int requiredCapacity) {
        List<PlaceSummary> candidates = new ArrayList<>();
        for (PlaceSummary room : rooms) {
            if (room.capacity() >= requiredCapacity) {
                candidates.add(room);
            }
        }
        if (!candidates.isEmpty()) {
            return candidates;
        }
        return new ArrayList<>(rooms);
    }

    private List<LectureSummary> loadLectures() {
        return jdbcTemplate.query("""
                        SELECT LECTURE_ID, PROFESSOR_NO, YEARTERM_CD, NVL(MAX_CAP, 30) AS MAX_CAP
                        FROM LECTURE
                        WHERE CANCEL_YN = 'N'
                          AND YEARTERM_CD = ?
                        ORDER BY MAX_CAP DESC, LECTURE_ID
                        """,
                new BeanPropertyRowMapper<>(LectureSummary.class),
                TARGET_TERM);
    }

    private String key(String term, String id, String block) {
        return term + "|" + id + "|" + block;
    }

    @RequiredArgsConstructor
    private static class ScheduledSlot {
        private final String lectureId;
        private final String yearTerm;
        private final String professorNo;
        private final String placeCd;
        private final List<String> timeBlocks;

        public String lectureId() {
            return lectureId;
        }

        public String yearTerm() {
            return yearTerm;
        }

        public String professorNo() {
            return professorNo;
        }

        public String placeCd() {
            return placeCd;
        }

        public List<String> timeBlocks() {
            return timeBlocks;
        }
    }

    private record PlaceSummary(String placeCd, int capacity, String usageCd) {
    }

    public static class LectureSummary {
        private String lectureId;
        private String professorNo;
        private String yeartermCd;
        private Integer maxCap;

        public String getLectureId() {
            return lectureId;
        }

        public void setLectureId(String lectureId) {
            this.lectureId = lectureId;
        }

        public String getProfessorNo() {
            return professorNo;
        }

        public void setProfessorNo(String professorNo) {
            this.professorNo = professorNo;
        }

        public String getYeartermCd() {
            return yeartermCd;
        }

        public void setYeartermCd(String yeartermCd) {
            this.yeartermCd = yeartermCd;
        }

        public Integer getMaxCap() {
            return maxCap;
        }

        public void setMaxCap(Integer maxCap) {
            this.maxCap = maxCap;
        }

        public String lectureId() {
            return lectureId;
        }

        public String professorNo() {
            return professorNo;
        }

        public String yearTerm() {
            return yeartermCd;
        }

        public int maxCap() {
            return Optional.ofNullable(maxCap).orElse(30);
        }
    }
}

