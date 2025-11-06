package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class F_LectureDummyGenerator {

    @Autowired
    DummyDataMapper ddMapper;

    private static final String YEAR_TERM = "2025_REG2";
    private static final int MAX_CAP = 40;

    // LECTURE_ID: LECT + 11자리 시퀀스 (시퀀스 미사용)
    private long lectSeq = 90000000000L;
    private String nextLectureId() {
        return "LECT" + String.format("%011d", lectSeq++);
    }

    // SUBJECT_CD: SUBJ + 11자리, 0 ~ 159
    private static String subjectCdOf(int idx) {
        long base = 90000000000L + idx;
        return "SUBJ" + String.format("%011d", base);
    }

    // 학과 순서(각 16과목)
    private static final List<String> DEPT_ORDER_16 = List.of(
        "DEP-SOC-PUBA", // 0~15
        "DEP-SOC-POLS", // 16~31
        "DEP-SOC-ECON", // 32~47
        "DEP-HUM-JPN",  // 48~63
        "DEP-HUM-CHN",  // 64~79
        "DEP-ENGI-ME",  // 80~95
        "DEP-ENGI-IE",  // 96~111
        "DEP-ENGI-EE",  // 112~127
        "DEP-ENGI-CHE", // 128~143
        "DEP-ENGI-CE"   // 144~159
    );

    // 끝자리 → 학과 매핑 (1~10, 11~20 반복)
    private static final Map<Integer, String> LASTDIGIT_TO_DEPT = Map.ofEntries(
        Map.entry(1,  "DEP-ENGI-EE"),
        Map.entry(2,  "DEP-ENGI-ME"),
        Map.entry(3,  "DEP-ENGI-CE"),
        Map.entry(4,  "DEP-ENGI-CHE"),
        Map.entry(5,  "DEP-ENGI-IE"),
        Map.entry(6,  "DEP-HUM-JPN"),
        Map.entry(7,  "DEP-HUM-CHN"),
        Map.entry(8,  "DEP-SOC-ECON"),
        Map.entry(9,  "DEP-SOC-POLS"),
        Map.entry(10, "DEP-SOC-PUBA")
    );

    /**
     * 2021~2024 연도별 교수 생성:
     *   각 연도 20명(YYYY8 001..020), 끝자리 규칙으로 각 학과 2명씩 → 연도 4개 × 2명 = 학과당 8명
     *   결과: 학과별 큐에는 정확히 8명이 들어가며, 각자 2강의씩 맡아 총 16강의 배정 가능.
     */
    private Map<String, Deque<String>> buildDeptProfessorQueues_2021_to_2024() {
        Map<String, List<String>> byDept = new LinkedHashMap<>();
        for (int year = 2021; year <= 2024; year++) {
            for (int last = 1; last <= 20; last++) {
                int slot = ((last - 1) % 10) + 1; // 1..10로 정규화
                String dept = LASTDIGIT_TO_DEPT.get(slot);
                String profNo = year + "8" + String.format("%03d", last); // YYYY8NNN
                byDept.computeIfAbsent(dept, k -> new ArrayList<>()).add(profNo);
            }
        }
        Map<String, Deque<String>> queues = new LinkedHashMap<>();
        byDept.forEach((dept, list) -> queues.put(dept, new ArrayDeque<>(list))); // 입력 순서 유지
        return queues;
    }

    private static String deptOfSubjectIndex(int idx) {
        int block = idx / 16; // 0..9
        return DEPT_ORDER_16.get(block);
    }

    @Test
    void insertLectureDummies_2021_2024_professors() {
        Map<String, Deque<String>> profQueues = buildDeptProfessorQueues_2021_to_2024();

        int inserted = 0;

        for (int i = 0; i < 160; i++) {
            String subjectCd = subjectCdOf(i);
            String dept = deptOfSubjectIndex(i);

            // 해당 학과 교수 큐(8명)를 라운드로빈(정확히 2회전하면 각 2강의)
            Deque<String> q = profQueues.get(dept);
            if (q == null || q.isEmpty()) {
                throw new IllegalStateException("No professors available for dept: " + dept);
            }
            String professorNo = q.pollFirst();
            q.offerLast(professorNo);

            LectureVO vo = new LectureVO();
            vo.setLectureId(nextLectureId());
            vo.setSubjectCd(subjectCd);
            vo.setProfessorNo(professorNo);
            vo.setYeartermCd(YEAR_TERM);
            vo.setMaxCap(MAX_CAP);
            vo.setLectureIndex("추후 작성예정");
            vo.setLectureGoal("추후 작성예정");
            vo.setPrereqSubject(null);
            vo.setCancelYn("N");
            vo.setEndAt(null);

            inserted += ddMapper.insertOneDummyLecture(vo);
        }

        log.info("Inserted LECTURE rows = {}", inserted); // 기대 160
        assertEquals(160, inserted);
    }
}
