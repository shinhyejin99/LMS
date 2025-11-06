package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class F_LectureDummyGenerator3 {
	
    @Autowired
    DummyDataMapper ddMapper;

    // ===== 고정 파라미터 =====
    private static final String YEAR_TERM = "2025_REG1";       // 요구사항: 25년도 정규1학기
    private static final LocalDateTime END_AT = LocalDateTime.of(2025, 6, 22, 0, 0);

    // LECTURE_ID: 시퀀스 미사용, 200부터 시작
    private long lectSeq = 90000000200L;
    private String nextLectureId() {
        return "LECT" + String.format("%011d", lectSeq++);
    }

    // ===== 과목 코드 =====
    private static final String SUBJ_SEMINAR   = "SUBJ00000000021"; // 공학입문세미나
    private static final String SUBJ_ETHICS    = "SUBJ00000000022"; // 공학윤리와안전
    private static final String SUBJ_CALC      = "SUBJ00000000023"; // 공학수학(미적분 기초)
    private static final String SUBJ_PHYS      = "SUBJ00000000024"; // 공학물리 기초
    private static final String SUBJ_PYTHON    = "SUBJ00000000025"; // 프로그래밍 기초(Python)

    // ===== 정원 =====
    private static final int CAP_50 = 50;
    private static final int CAP_40 = 40;

    // ===== 교수 풀 =====
    // 1) 2024 시작(공대 5개 학과) – 세미나/윤리용 (총 10명: 학과별 2명)
    //    DEP-ENGI-EE/ME/CE/CHE/IE 각각 2명씩
    private static final String[] PROF_POOL_2024 = {
        "20248001","20248011", // EE
        "20248002","20248012", // ME
        "20248003","20248013", // CE
        "20248004","20248014", // CHE
        "20248005","20248015"  // IE
    };

    // 2) 2025 시작(공대 5개 학과) – 공학수학/공학물리용 (총 10명)
    private static final String[] PROF_POOL_2025 = {
        "20258001","20258011", // EE
        "20258002","20258012", // ME
        "20258003","20258013", // CE
        "20258004","20258014", // CHE
        "20258005","20258015"  // IE
    };

    // 3) CSE 전용 – 프로기초(Python)용 (총 5명)
    private static final String[] PROF_POOL_CSE = {
        "20230001","20230002","20230003","20240001","20240002"
    };

    // ===== 유틸: 라운드로빈 큐 구성 =====
    private static Deque<String> rrQueueOf(String[] profNos) {
        Deque<String> q = new ArrayDeque<>();
        for (String p : profNos) q.addLast(p);
        return q;
    }
    private static String pollAndRotate(Deque<String> q) {
        String p = q.pollFirst();
        q.offerLast(p);
        return p;
    }

    // ===== 공통으로 한 건 INSERT =====
    private int insertOne(String subjectCd, String professorNo, int maxCap) {
        LectureVO vo = new LectureVO();
        vo.setLectureId(nextLectureId());
        vo.setSubjectCd(subjectCd);
        vo.setProfessorNo(professorNo);
        vo.setYeartermCd(YEAR_TERM);
        vo.setMaxCap(maxCap);
        vo.setLectureIndex("추후 작성예정입니다");
        vo.setLectureGoal("추후 작성예정입니다");
        vo.setPrereqSubject(null);
        vo.setCancelYn("N");
        vo.setEndAt(END_AT);

        int r = ddMapper.insertOneDummyLecture(vo);
        log.debug("INSERT LECTURE: id={} subj={} prof={} cap={}", vo.getLectureId(), subjectCd, professorNo, maxCap);
        return r;
    }

    @Test
    void insertLectureDummies_engineering_basic_plan() {
        int inserted = 0;

        // ── 1) 공학입문세미나 (정원50 × 10개) : 2024교수풀 라운드로빈
        Deque<String> q2024 = rrQueueOf(PROF_POOL_2024);
        for (int i = 0; i < 10; i++) {
            inserted += insertOne(SUBJ_SEMINAR, pollAndRotate(q2024), CAP_50);
        }

        // ── 2) 공학윤리와안전 (정원50 × 10개) : 2024교수풀 라운드로빈
        for (int i = 0; i < 10; i++) {
            inserted += insertOne(SUBJ_ETHICS, pollAndRotate(q2024), CAP_50);
        }
        // ⇒ 결과적으로 2024교수 각각 세미나 1개 + 윤리 1개 = 2개씩 맡게 됨 (총 20개)

        // ── 3) 공학수학(미적분 기초) (정원40 × 10개) : 2025교수풀 라운드로빈
        Deque<String> q2025 = rrQueueOf(PROF_POOL_2025);
        for (int i = 0; i < 10; i++) {
            inserted += insertOne(SUBJ_CALC, pollAndRotate(q2025), CAP_40);
        }

        // ── 4) 공학물리 기초 (정원40 × 10개) : 2025교수풀 라운드로빈(연속)
        for (int i = 0; i < 10; i++) {
            inserted += insertOne(SUBJ_PHYS, pollAndRotate(q2025), CAP_40);
        }
        // ⇒ 결과적으로 2025교수들이 수학+물리 합쳐 평균 2개씩 맡게 됨 (총 20개)

        // ── 5) 프로그래밍 기초(Python) (정원40 × 10개) : CSE 교수풀 라운드로빈
        Deque<String> qCSE = rrQueueOf(PROF_POOL_CSE);
        for (int i = 0; i < 10; i++) {
            inserted += insertOne(SUBJ_PYTHON, pollAndRotate(qCSE), CAP_40);
        }
        // ⇒ CSE 5명이 각 2개씩 맡음 (총 10개)

        log.info("Inserted LECTURE rows = {}", inserted); // 기대 50
        assertEquals(50, inserted);
    }
}
