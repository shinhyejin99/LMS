package kr.or.jsu.aiDummyDataGenerator.step2_campus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.PlaceVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step2_3_CampusPlaceDummyGeneratorTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2014, 3, 1, 9, 0);
    private static final String CAMPUS_MAIN = "CAMPUSMAIN";

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertPlaceHierarchy() {
        AtomicInteger order = new AtomicInteger(0);
        List<PlaceVO> places = new ArrayList<>();

        places.add(place(order, CAMPUS_MAIN, null, "메인캠퍼스", "CAMPUS", null, null));

        addFacilities(order, places);

        List<BuildingPlan> plans = List.of(
                new BuildingPlan("BLDGHUMNA", "인문관 A동", 8, 2, 2, 3, 2, true),
                new BuildingPlan("BLDGHUMNB", "인문관 B동", 6, 1, 2, 2, 1, false),
                new BuildingPlan("BLDGSOCSA", "사회과학관 A동", 8, 2, 2, 2, 2, false),
                new BuildingPlan("BLDGSOCSB", "사회과학관 B동", 6, 1, 1, 2, 1, false),
                new BuildingPlan("BLDGNATSA", "자연과학관 A동", 6, 6, 1, 2, 2, false),
                new BuildingPlan("BLDGNATSB", "자연과학관 B동", 6, 4, 1, 1, 1, false),
                new BuildingPlan("BLDGENGRA", "공학관 A동", 10, 8, 2, 3, 3, true),
                new BuildingPlan("BLDGENGRB", "공학관 B동", 8, 6, 2, 2, 2, true),
                new BuildingPlan("BLDGENGRC", "공학관 C동", 6, 5, 1, 2, 2, false),
                new BuildingPlan("BLDGEDUCA", "사범관 A동", 6, 3, 1, 2, 2, false),
                new BuildingPlan("BLDGEDUCB", "사범관 B동", 4, 2, 1, 1, 1, false),
                new BuildingPlan("BLDGARTSA", "예술관 A동", 4, 6, 1, 2, 1, false),
                new BuildingPlan("BLDGARTSB", "예술관 B동", 3, 4, 1, 1, 1, false),
                new BuildingPlan("BLDGLIBRA", "중앙도서관", 0, 0, 0, 6, 2, false),
                new BuildingPlan("BLDGSTUCA", "학생회관", 0, 0, 0, 4, 3, true)
        );

        for (BuildingPlan plan : plans) {
            places.add(place(order, plan.code(), CAMPUS_MAIN, plan.name(), "BUILDING", null, null));
            addRoomsForBuilding(order, places, plan);
        }

        int rows = dummyDataMapper.insertDummyPlaces(places);
        log.info("Inserted place rows: {}", rows);
    }

    private void addFacilities(AtomicInteger order, List<PlaceVO> places) {
        places.add(place(order, "FACLAUDMAIN", CAMPUS_MAIN, "중앙 대강당", "FACILITY", "AUDITORIUM", 800));
        places.add(place(order, "FACLFIELD", CAMPUS_MAIN, "중앙운동장", "FACILITY", "PLAZA", 500));
        places.add(place(order, "FACLCENTR", CAMPUS_MAIN, "학생지원센터", "FACILITY", "ADMIN_OFFICE", 120));
    }

    private void addRoomsForBuilding(AtomicInteger order, List<PlaceVO> places, BuildingPlan plan) {
        for (int i = 1; i <= plan.classrooms(); i++) {
            String code = plan.code() + "CR" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("강의실", 100 + i);
            places.add(place(order, code, plan.code(), name, "ROOM", "CLASSROOM", 60));
        }

        for (int i = 1; i <= plan.labs(); i++) {
            String code = plan.code() + "LB" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("실습실", 200 + i);
            int capacity;
            if (plan.code().startsWith("BLDGART")) {
                capacity = 20;
            } else if (plan.code().startsWith("BLDGNAT")) {
                capacity = 32;
            } else if (plan.code().startsWith("BLDGENGR")) {
                capacity = 36;
            } else {
                capacity = 28;
            }
            places.add(place(order, code, plan.code(), name, "ROOM", "LAB", capacity));
        }

        for (int i = 1; i <= plan.seminars(); i++) {
            String code = plan.code() + "SM" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("세미나실", 300 + i);
            places.add(place(order, code, plan.code(), name, "ROOM", "SEMINAR", 40));
        }

        for (int i = 1; i <= plan.meetings(); i++) {
            String code = plan.code() + "MT" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("회의실", 400 + i);
            places.add(place(order, code, plan.code(), name, "ROOM", "MEETING", 18));
        }

        for (int i = 1; i <= plan.studyRooms(); i++) {
            String code = plan.code() + "SR" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("스터디룸", 500 + i);
            int capacity = plan.code().equals("BLDGLIBRA") ? 16 : 12;
            places.add(place(order, code, plan.code(), name, "ROOM", "STUDYROOM", capacity));
        }

        // 교수 연구실 3개씩 기본 생성
        for (int i = 1; i <= 3; i++) {
            String code = plan.code() + "PF" + String.format(Locale.ROOT, "%02d", i);
            String name = roomName("교수연구실", i);
            places.add(place(order, code, plan.code(), name, "ROOM", "PROF_OFFICE", 6));
        }

        if (plan.hasLounges()) {
            String code = plan.code() + "LNG01";
            places.add(place(order, code, plan.code(), roomName("휴게라운지", 1), "ROOM", "LOUNGE", 40));
        }
    }

    private String roomName(String category, int ordinal) {
        return String.format(Locale.ROOT, "%s %d", category, ordinal);
    }

    private PlaceVO place(AtomicInteger order, String code, String parent, String name,
            String type, String usage, Integer capacity) {
        PlaceVO vo = new PlaceVO();
        vo.setPlaceCd(code);
        vo.setParentCd(parent);
        vo.setPlaceName(name);
        vo.setCreateAt(BASE_TIME.plusDays(order.getAndIncrement()));
        vo.setModifyAt(null);
        vo.setUsingYn("Y");
        vo.setAddrId(null);
        vo.setPlaceTypeCd(type);
        vo.setCapacity(capacity);
        vo.setPlaceUsageCd(usage);
        return vo;
    }

    private record BuildingPlan(
            String code,
            String name,
            int classrooms,
            int labs,
            int seminars,
            int studyRooms,
            int meetings,
            boolean hasLounges
    ) {
    }
}
