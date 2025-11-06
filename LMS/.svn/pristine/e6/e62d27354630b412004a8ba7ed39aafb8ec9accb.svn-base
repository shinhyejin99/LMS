package kr.or.jsu.aiDummyDataGenerator.step7_lecture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step7_3_LectureSubjectScheduleDummyGeneratorTest {

    private static final List<String> DAYS = List.of("MO", "TU", "WE", "TH", "FR");
    private static final Random RANDOM = new Random(2025_11_05_2100L);

    private static final Map<String, List<String>> COLLEGE_PREFIXES = Map.ofEntries(
            Map.entry("COL-HUMN", List.of("BLDGHUMNA", "BLDGHUMNB")),
            Map.entry("COL-SOCS", List.of("BLDGSOCSA", "BLDGSOCSB")),
            Map.entry("COL-NATS", List.of("BLDGNATSA", "BLDGNATSB")),
            Map.entry("COL-ENGR", List.of("BLDGENGRA", "BLDGENGRB", "BLDGENGRC")),
            Map.entry("COL-EDUC", List.of("BLDGEDUCA", "BLDGEDUCB")),
            Map.entry("COL-ARTS", List.of("BLDGARTSA", "BLDGARTSB")),
            Map.entry("COL-GENR", List.of("BLDGLIBRA", "BLDGSTUCA"))
    );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void assignSchedulesPerSubject() {
        List<LectureDetail> lectures = loadLecturesWithSubjects();
        if (lectures.isEmpty()) {
            log.warn("시간표를 배정할 강의가 없습니다.");
            return;
        }

        List<RoomInfo> rooms = loadEligibleRooms();
        if (rooms.isEmpty()) {
            log.warn("사용 가능한 강의실이 없습니다.");
            return;
        }

        Map<String, List<String>> dayBlocks = loadTimeBlocksByDay();
        if (dayBlocks.isEmpty()) {
            log.warn("시간블록 정보가 없어 시간표를 생성하지 않습니다.");
            return;
        }

        jdbcTemplate.update("DELETE FROM LCT_ROOM_SCHEDULE");

        Set<String> professorBusy = new LinkedHashSet<>();
        Set<String> placeBusy = new LinkedHashSet<>();
        Set<String> lectureBusy = new LinkedHashSet<>();

        int assigned = 0;
        int failed = 0;

        for (LectureDetail lecture : lectures) {
            int totalBlocks = Math.max(2, lecture.contactHours() * 2);
            Optional<ScheduleEntry> entry = scheduleLecture(lecture, rooms, dayBlocks, totalBlocks,
                    professorBusy, placeBusy, lectureBusy);
            if (entry.isPresent()) {
                persist(entry.get(), professorBusy, placeBusy, lectureBusy);
                assigned++;
            } else {
                failed++;
                log.warn("강의 {}({})에 대한 시간표를 배정하지 못했습니다.", lecture.lectureId(), lecture.yearTerm());
            }
        }

        log.info("강의 시간표 배정 완료 - 성공 {}건, 실패 {}", assigned, failed);
    }

    private Optional<ScheduleEntry> scheduleLecture(
            LectureDetail lecture,
            List<RoomInfo> rooms,
            Map<String, List<String>> dayBlocks,
            int totalBlocks,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {

        List<RoomInfo> candidateRooms = selectRoomsForLecture(lecture, rooms);
        if (candidateRooms.isEmpty()) {
            return Optional.empty();
        }

        List<String> dayOrder = new ArrayList<>(DAYS);
        Collections.rotate(dayOrder, RANDOM.nextInt(dayOrder.size()));

        for (RoomInfo room : candidateRooms) {
            for (String day : dayOrder) {
                List<String> blocks = dayBlocks.get(day);
                if (blocks == null || blocks.size() < totalBlocks) {
                    continue;
                }
                List<Integer> indices = buildStartIndices(blocks.size(), totalBlocks);
                for (int start : indices) {
                    List<String> candidate = blocks.subList(start, start + totalBlocks);
                    if (isAvailable(lecture, room, candidate, professorBusy, placeBusy, lectureBusy)) {
                        return Optional.of(new ScheduleEntry(lecture, room.placeCd(), candidate));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean isAvailable(
            LectureDetail lecture,
            RoomInfo room,
            List<String> blocks,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {
        for (String block : blocks) {
            String term = lecture.yearTerm();
            if (professorBusy.contains(key(term, lecture.professorNo(), block))) {
                return false;
            }
            if (placeBusy.contains(key(term, room.placeCd(), block))) {
                return false;
            }
            if (lectureBusy.contains(key(term, lecture.lectureId(), block))) {
                return false;
            }
        }
        return true;
    }

    private void persist(ScheduleEntry entry,
            Set<String> professorBusy,
            Set<String> placeBusy,
            Set<String> lectureBusy) {
        for (String block : entry.timeBlocks()) {
            dummyDataMapper.insertSchedule(entry.lectureId(), entry.placeCd(), block);
        }
        String term = entry.lecture().yearTerm();
        for (String block : entry.timeBlocks()) {
            professorBusy.add(key(term, entry.lecture().professorNo(), block));
            placeBusy.add(key(term, entry.placeCd(), block));
            lectureBusy.add(key(term, entry.lectureId(), block));
        }
    }

    private List<RoomInfo> selectRoomsForLecture(LectureDetail lecture, List<RoomInfo> rooms) {
        List<String> prefixes = resolvePrefixes(lecture);
        List<RoomInfo> matched = new ArrayList<>();
        for (RoomInfo room : rooms) {
            if (room.capacity() < lecture.maxCap()) {
                continue;
            }
            if (prefixes.stream().anyMatch(room.placeCd()::startsWith)) {
                matched.add(room);
            }
        }
        if (!matched.isEmpty()) {
            return matched;
        }
        // fallback to any room that can host
        List<RoomInfo> fallback = new ArrayList<>();
        for (RoomInfo room : rooms) {
            if (room.capacity() >= lecture.maxCap()) {
                fallback.add(room);
            }
        }
        if (!fallback.isEmpty()) {
            return fallback;
        }
        return new ArrayList<>(rooms);
    }

    private List<String> resolvePrefixes(LectureDetail lecture) {
        String college = Optional.ofNullable(lecture.collegeCd()).orElse("COL-GENR");
        List<String> prefixes = new ArrayList<>(COLLEGE_PREFIXES.getOrDefault(college, List.of()));
        if (prefixes.isEmpty() && lecture.deptCd() != null) {
            if (lecture.deptCd().startsWith("DEPT-ENGR")) {
                prefixes = List.of("BLDGENGRA", "BLDGENGRB", "BLDGENGRC");
            } else if (lecture.deptCd().startsWith("DEPT-NATS")) {
                prefixes = List.of("BLDGNATSA", "BLDGNATSB");
            }
        }
        if (prefixes.isEmpty()) {
            prefixes = COLLEGE_PREFIXES.getOrDefault("COL-GENR", List.of());
        }
        return prefixes;
    }

    private List<Integer> buildStartIndices(int totalBlocks, int length) {
        List<Integer> indices = new ArrayList<>();
        for (int start = 0; start <= totalBlocks - length; start++) {
            indices.add(start);
        }
        Collections.shuffle(indices, RANDOM);
        return indices;
    }

    private Map<String, List<String>> loadTimeBlocksByDay() {
        List<String> blocks = jdbcTemplate.queryForList(
                "SELECT TIMEBLOCK_CD FROM TIMEBLOCK ORDER BY TIMEBLOCK_CD",
                String.class);
        Map<String, List<String>> byDay = new LinkedHashMap<>();
        for (String day : DAYS) {
            byDay.put(day, new ArrayList<>());
        }
        for (String block : blocks) {
            if (block.length() >= 2) {
                String day = block.substring(0, 2);
                List<String> list = byDay.get(day);
                if (list != null) {
                    list.add(block);
                }
            }
        }
        return byDay;
    }

    private List<RoomInfo> loadEligibleRooms() {
        return jdbcTemplate.query("""
                        SELECT PLACE_CD, NVL(CAPACITY, 20) AS CAPACITY, PLACE_USAGE_CD
                        FROM PLACE
                        WHERE PLACE_TYPE_CD = 'ROOM'
                          AND USING_YN = 'Y'
                          AND PLACE_USAGE_CD IN ('CLASSROOM', 'LAB')
                        ORDER BY CAPACITY DESC, PLACE_CD
                        """,
                (rs, rowNum) -> new RoomInfo(
                        rs.getString("PLACE_CD"),
                        rs.getInt("CAPACITY"),
                        rs.getString("PLACE_USAGE_CD")));
    }

    private List<LectureDetail> loadLecturesWithSubjects() {
        return jdbcTemplate.query("""
                        SELECT
                            L.LECTURE_ID,
                            L.PROFESSOR_NO,
                            L.YEARTERM_CD,
                            NVL(L.MAX_CAP, 30) AS MAX_CAP,
                            S.SUBJECT_CD,
                            NVL(S.CREDIT, 3) AS CREDIT,
                            NVL(S.HOUR, 3) AS HOUR,
                            S.UNIV_DEPT_CD,
                            NVL(D.COLLEGE_CD, 'COL-GENR') AS COLLEGE_CD
                        FROM LECTURE L
                        JOIN SUBJECT S ON L.SUBJECT_CD = S.SUBJECT_CD
                        LEFT JOIN UNIV_DEPT D ON S.UNIV_DEPT_CD = D.UNIV_DEPT_CD
                        WHERE L.CANCEL_YN = 'N'
                        ORDER BY L.YEARTERM_CD, S.SUBJECT_CD
                        """,
                new BeanPropertyRowMapper<>(LectureDetail.class));
    }

    private String key(String term, String id, String block) {
        return term + "|" + id + "|" + block;
    }

    private record RoomInfo(String placeCd, int capacity, String usageCd) {
    }

    public static class LectureDetail {
        private String lectureId;
        private String professorNo;
        private String yeartermCd;
        private Integer maxCap;
        private String subjectCd;
        private Integer credit;
        private Integer hour;
        private String univDeptCd;
        private String collegeCd;

        public String lectureId() {
            return Objects.requireNonNull(lectureId);
        }

        public String professorNo() {
            return Objects.requireNonNull(professorNo);
        }

        public String yearTerm() {
            return Objects.requireNonNull(yeartermCd);
        }

        public int maxCap() {
            return Optional.ofNullable(maxCap).orElse(30);
        }

        public String subjectCd() {
            return Objects.requireNonNull(subjectCd);
        }

        public int credit() {
            return Optional.ofNullable(credit).orElse(3);
        }

        public int contactHours() {
            return Optional.ofNullable(hour).orElseGet(() -> Math.max(credit(), 2));
        }

        public String deptCd() {
            return univDeptCd;
        }

        public String collegeCd() {
            return collegeCd;
        }

        public void setLectureId(String lectureId) {
            this.lectureId = lectureId;
        }

        public void setProfessorNo(String professorNo) {
            this.professorNo = professorNo;
        }

        public void setYeartermCd(String yeartermCd) {
            this.yeartermCd = yeartermCd;
        }

        public void setMaxCap(Integer maxCap) {
            this.maxCap = maxCap;
        }

        public void setSubjectCd(String subjectCd) {
            this.subjectCd = subjectCd;
        }

        public void setCredit(Integer credit) {
            this.credit = credit;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public void setUnivDeptCd(String univDeptCd) {
            this.univDeptCd = univDeptCd;
        }

        public void setCollegeCd(String collegeCd) {
            this.collegeCd = collegeCd;
        }
    }

    private record ScheduleEntry(
            LectureDetail lecture,
            String placeCd,
            List<String> timeBlocks) {

        public String lectureId() {
            return lecture.lectureId();
        }

        public String placeCd() {
            return placeCd;
        }

        public List<String> timeBlocks() {
            return timeBlocks;
        }

        public LectureDetail lecture() {
            return lecture;
        }

    }
}
