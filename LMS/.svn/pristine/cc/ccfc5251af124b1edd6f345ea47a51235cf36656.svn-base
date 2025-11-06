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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class F_LectureDummyGenerator2 {

    @Autowired
    DummyDataMapper ddMapper;

    private static final String YEAR_TERM = "2025_REG2";
    private static final int    MAX_CAP   = 40;

    // LECTURE_ID: LECT + 11자리 시퀀스 (미사용 시퀀스 규칙 그대로)
    private long lectSeq = 90000000160L;
    private String nextLectureId() {
        return "LECT" + String.format("%011d", lectSeq++);
    }

    // ----- 입력 원천(그대로 붙여넣기용) -----
    private static final String SUBJECTS_RAW = """
        SUBJ90000000160\tDEP-SOC-PUBA
        SUBJ90000000161\tDEP-SOC-PUBA
        SUBJ90000000162\tDEP-SOC-PUBA
        SUBJ90000000163\tDEP-SOC-PUBA
        SUBJ90000000164\tDEP-SOC-POLS
        SUBJ90000000165\tDEP-SOC-POLS
        SUBJ90000000166\tDEP-SOC-POLS
        SUBJ90000000167\tDEP-SOC-POLS
        SUBJ90000000168\tDEP-SOC-ECON
        SUBJ90000000169\tDEP-SOC-ECON
        SUBJ90000000170\tDEP-SOC-ECON
        SUBJ90000000171\tDEP-SOC-ECON
        SUBJ90000000172\tDEP-HUM-JPN
        SUBJ90000000173\tDEP-HUM-JPN
        SUBJ90000000174\tDEP-HUM-JPN
        SUBJ90000000175\tDEP-HUM-JPN
        SUBJ90000000176\tDEP-HUM-CHN
        SUBJ90000000177\tDEP-HUM-CHN
        SUBJ90000000178\tDEP-HUM-CHN
        SUBJ90000000179\tDEP-HUM-CHN
        SUBJ90000000180\tDEP-ENGI-ME
        SUBJ90000000181\tDEP-ENGI-ME
        SUBJ90000000182\tDEP-ENGI-ME
        SUBJ90000000183\tDEP-ENGI-ME
        SUBJ90000000184\tDEP-ENGI-IE
        SUBJ90000000185\tDEP-ENGI-IE
        SUBJ90000000186\tDEP-ENGI-IE
        SUBJ90000000187\tDEP-ENGI-IE
        SUBJ90000000188\tDEP-ENGI-EE
        SUBJ90000000189\tDEP-ENGI-EE
        SUBJ90000000190\tDEP-ENGI-EE
        SUBJ90000000191\tDEP-ENGI-EE
        SUBJ90000000192\tDEP-ENGI-CHE
        SUBJ90000000193\tDEP-ENGI-CHE
        SUBJ90000000194\tDEP-ENGI-CHE
        SUBJ90000000195\tDEP-ENGI-CHE
        SUBJ90000000196\tDEP-ENGI-CE
        SUBJ90000000197\tDEP-ENGI-CE
        SUBJ90000000198\tDEP-ENGI-CE
        SUBJ90000000199\tDEP-ENGI-CE
    """;

    private static final String PROFESSORS_RAW = """
        20258001\tUSER80000000080\tDEP-ENGI-EE
        20258002\tUSER80000000081\tDEP-ENGI-ME
        20258003\tUSER80000000082\tDEP-ENGI-CE
        20258004\tUSER80000000083\tDEP-ENGI-CHE
        20258005\tUSER80000000084\tDEP-ENGI-IE
        20258006\tUSER80000000085\tDEP-HUM-JPN
        20258007\tUSER80000000086\tDEP-HUM-CHN
        20258008\tUSER80000000087\tDEP-SOC-ECON
        20258009\tUSER80000000088\tDEP-SOC-POLS
        20258010\tUSER80000000089\tDEP-SOC-PUBA
        20258011\tUSER80000000090\tDEP-ENGI-EE
        20258012\tUSER80000000091\tDEP-ENGI-ME
        20258013\tUSER80000000092\tDEP-ENGI-CE
        20258014\tUSER80000000093\tDEP-ENGI-CHE
        20258015\tUSER80000000094\tDEP-ENGI-IE
        20258016\tUSER80000000095\tDEP-HUM-JPN
        20258017\tUSER80000000096\tDEP-HUM-CHN
        20258018\tUSER80000000097\tDEP-SOC-ECON
        20258019\tUSER80000000098\tDEP-SOC-POLS
        20258020\tUSER80000000099\tDEP-SOC-PUBA
    """;

    // ----- 파싱용 작은 DTO -----
    @Getter @AllArgsConstructor
    private static class SubjectRow {
        String subjectCd;
        String dept;
    }

    @Getter @AllArgsConstructor
    private static class ProfessorRow {
        String professorNo; // 교번(CHAR(8))
        String userId;      // 참고용
        String dept;
    }

    private static List<SubjectRow> parseSubjects(String raw) {
        List<SubjectRow> list = new ArrayList<>();
        for (String line : raw.strip().split("\\R")) {
            String[] t = line.trim().split("\\s+");
            if (t.length < 2) continue;
            list.add(new SubjectRow(t[0], t[1]));
        }
        return list;
    }

    private static List<ProfessorRow> parseProfessors(String raw) {
        List<ProfessorRow> list = new ArrayList<>();
        for (String line : raw.strip().split("\\R")) {
            String[] t = line.trim().split("\\s+");
            if (t.length < 3) continue;
            list.add(new ProfessorRow(t[0], t[1], t[2]));
        }
        return list;
    }

    /**
     * 학과 → 교수 큐(라운드로빈) 구성
     */
    private static Map<String, Deque<String>> buildProfessorQueues(List<ProfessorRow> profs) {
        Map<String, Deque<String>> map = new LinkedHashMap<>();
        for (ProfessorRow p : profs) {
            map.computeIfAbsent(p.getDept(), k -> new ArrayDeque<>()).add(p.getProfessorNo());
        }
        return map;
    }

    @Test
    void insertLectureDummies_40subjects_20professors_roundRobinByDept() {
        List<SubjectRow> subjects = parseSubjects(SUBJECTS_RAW);
        List<ProfessorRow> professors = parseProfessors(PROFESSORS_RAW);

        if (subjects.isEmpty()) throw new IllegalStateException("과목 데이터 없음");
        if (professors.isEmpty()) throw new IllegalStateException("교수 데이터 없음");

        Map<String, Deque<String>> profQueues = buildProfessorQueues(professors);

        int inserted = 0;

        for (SubjectRow s : subjects) {
            Deque<String> q = profQueues.get(s.getDept());
            if (q == null || q.isEmpty()) {
                throw new IllegalStateException("해당 학과 교수 없음: " + s.getDept() + " (과목 " + s.getSubjectCd() + ")");
            }
            // 라운드로빈 배정
            String professorNo = q.pollFirst();
            q.offerLast(professorNo);

            LectureVO vo = new LectureVO();
            vo.setLectureId(nextLectureId());
            vo.setSubjectCd(s.getSubjectCd());
            vo.setProfessorNo(professorNo);
            vo.setYeartermCd(YEAR_TERM);
            vo.setMaxCap(MAX_CAP);
            vo.setLectureIndex("추후 작성예정");
            vo.setLectureGoal("추후 작성예정");
            vo.setPrereqSubject(null);
            vo.setCancelYn("N");
            vo.setEndAt(null);

            inserted += ddMapper.insertOneDummyLecture(vo);

            log.debug("INSERT LECTURE: {} / subj={} / dept={} / prof={}",
                    vo.getLectureId(), s.getSubjectCd(), s.getDept(), professorNo);
        }

        log.info("Inserted LECTURE rows = {}", inserted); // 기대 40
        assertEquals(subjects.size(), inserted);
    }
}
