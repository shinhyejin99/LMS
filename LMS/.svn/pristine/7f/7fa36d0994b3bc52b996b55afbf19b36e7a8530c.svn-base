package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class G_StuEnrollDummyGenerator {

    @Autowired
    DummyDataMapper ddMapper;

    // ===== 설정 =====
    private static final int TOTAL_STUDENTS = 1000;
    private static final int START_STUDENT_NO = 202590000; // 2025학번 90000대 시작
    private static final String ENROLL_STATUS = "ENR_ING";
    private static final String RETAKE_YN = "N";
    private static final LocalDateTime STATUS_CHANGE_AT = LocalDateTime.of(2025, 9, 26, 21, 54, 6);

    // ENROLL_ID: STENRL + 9자리
    private long enrollSeq = 900000000L;
    private String nextEnrollId() {
        return "STENRL" + String.format("%09d", enrollSeq++);
    }

    // 학과별 1학년 전핵 강의 2개 (각 50명 정원)
    private static final Map<String, String[]> DEPT_LECTURES = new LinkedHashMap<>();
    static {
        DEPT_LECTURES.put("DEP-ENGI-EE",  new String[] {"LECT90000000112", "LECT90000000113"});
        DEPT_LECTURES.put("DEP-ENGI-ME",  new String[] {"LECT90000000080", "LECT90000000081"});
        DEPT_LECTURES.put("DEP-ENGI-CE",  new String[] {"LECT90000000144", "LECT90000000145"});
        DEPT_LECTURES.put("DEP-ENGI-CHE", new String[] {"LECT90000000128", "LECT90000000129"});
        DEPT_LECTURES.put("DEP-ENGI-IE",  new String[] {"LECT90000000096", "LECT90000000097"});
        DEPT_LECTURES.put("DEP-HUM-JPN",  new String[] {"LECT90000000048", "LECT90000000049"});
        DEPT_LECTURES.put("DEP-HUM-CHN",  new String[] {"LECT90000000064", "LECT90000000065"});
        DEPT_LECTURES.put("DEP-SOC-ECON", new String[] {"LECT90000000032", "LECT90000000033"});
        DEPT_LECTURES.put("DEP-SOC-POLS", new String[] {"LECT90000000016", "LECT90000000017"});
        DEPT_LECTURES.put("DEP-SOC-PUBA", new String[] {"LECT90000000000", "LECT90000000001"});
    }

    // 학번 끝자리 → 학과
    private static String deptByTailDigit(int tail) {
        switch (tail) {
            case 0: return "DEP-ENGI-EE";
            case 1: return "DEP-ENGI-ME";
            case 2: return "DEP-ENGI-CE";
            case 3: return "DEP-ENGI-CHE";
            case 4: return "DEP-ENGI-IE";
            case 5: return "DEP-HUM-JPN";
            case 6: return "DEP-HUM-CHN";
            case 7: return "DEP-SOC-ECON";
            case 8: return "DEP-SOC-POLS";
            case 9: return "DEP-SOC-PUBA";
            default: throw new IllegalArgumentException("invalid tail: " + tail);
        }
    }

    @Test
    void insertFreshmanCoreEnrollments() {
        // 각 학과별로 첫 번째 강의에 50명, 두 번째 강의에 50명 배정
        Map<String, Integer> deptCount = new LinkedHashMap<>();
        DEPT_LECTURES.keySet().forEach(d -> deptCount.put(d, 0));

        int inserted = 0;

        for (int i = 0; i < TOTAL_STUDENTS; i++) {
            int studentNo = START_STUDENT_NO + i;
            String studentNoStr = String.format("%09d", studentNo);
            int tail = studentNo % 10;
            String dept = deptByTailDigit(tail);

            int countForDept = deptCount.get(dept);
            String[] lectures = DEPT_LECTURES.get(dept);
            // 앞 50명 → lectures[0], 뒤 50명 → lectures[1]
            String lectureId = (countForDept < 50) ? lectures[0] : lectures[1];
            if (countForDept >= 100) {
                throw new IllegalStateException("Department " + dept + " exceeded 100 freshman students.");
            }
            deptCount.put(dept, countForDept + 1);

            StuEnrollLctVO vo = new StuEnrollLctVO();
            vo.setEnrollId(nextEnrollId());
            vo.setStudentNo(studentNoStr);
            vo.setLectureId(lectureId);
            vo.setEnrollStatusCd(ENROLL_STATUS);
            vo.setRetakeYn(RETAKE_YN);
            vo.setStatusChangeAt(STATUS_CHANGE_AT);
            vo.setAutoGrade(null);
            vo.setFinalGrade(null);
            vo.setChangeReason(null);

            inserted += ddMapper.insertOneDummyEnrollment(vo);
        }

        log.info("Inserted STU_ENROLL_LCT rows = {}", inserted);
        assertEquals(1000, inserted, "총 1000건이 삽입되어야 합니다.");

        // 각 강의에 50명씩 들어갔는지 검증(선택)
        DEPT_LECTURES.forEach((dept, lects) -> {
            // deptCount는 총 100으로 끝나야 함
            assertEquals(100, deptCount.get(dept).intValue(), dept + " 학생 수는 100이어야 합니다.");
        });
    }
}
