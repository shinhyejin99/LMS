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

/**
 * 강의 데이터 생성 (2025학년 1학기 강의 생성)
 *
 * @author 김수현
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	김수현	          최초 생성
 *
 * </pre>
 */
@SpringBootTest
@Slf4j
class F_LectureDummyGenerator4 {
	@Autowired
    DummyDataMapper ddMapper;

    // ===== 고정 파라미터 =====
    private static final String YEAR_TERM = "2025_REG1";       // 요구사항: 25년도 정규1학기
    private static final LocalDateTime END_AT = LocalDateTime.of(2025, 6, 22, 0, 0);

    // LECTURE(강의) 테이블 PK : LECTURE_ID: 시퀀스 미사용, 0부터 시작
    private long lectSeq = 70000000000L;
    private String nextLectureId() {
        return "LECT" + String.format("%011d", lectSeq++);
    }

 // ===== 컴공 2학년 대상 과목 코드 (9개) =====
    private static final String[] SUBJECTS = {
        "SUBJ00000000001", // 컴퓨터 개론 및 실습
        "SUBJ00000000002", // 자료구조
        "SUBJ00000000002", // 선형대수학 (자료구조 코드 재사용)
        "SUBJ00000000001", // 컴파일러 구성론 (컴퓨터개론 코드 재사용)
        "SUBJ00000000010", // 객체지향 프로그래밍 (Java)
        "SUBJ00000000006", // 데이터베이스 시스템
        "SUBJ00000000007", // 네트워크 프로그래밍
        "SUBJ00000000011", // 인공지능 개론 및 실습
        "SUBJ00000000013"  // 정보보호 및 암호학
    };

    // ===== 정원 =====
    private static final int CAPACITY = 40;

 // ===== 컴공 교수 풀 (11명) =====
    private static final String[] PROF_POOL_CSE = {
        "20220001", "20220002", "20230001", "20230002", "20230003",
        "20240001", "20240002", "20240003", "20240004", "20240005",
        "20218020"
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
        vo.setLectureIndex("컴퓨터공학과 2학년 대상 강의입니다.");
        vo.setLectureGoal("해당 과목의 핵심 개념을 이해하고 실무 능력을 향상시킵니다.");
        vo.setPrereqSubject(null);
        vo.setCancelYn("N");
        vo.setEndAt(END_AT);

        int r = ddMapper.insertOneDummyLecture(vo);
        log.debug("INSERT LECTURE: id={} subj={} prof={} cap={}",
                  vo.getLectureId(), subjectCd, professorNo, maxCap);
        return r;
    }

    @Test
    void insertLectureDummies_CSE_2ndYear() {
        int inserted = 0;

        // 컴공 교수 풀로 라운드로빈
        Deque<String> profQueue = rrQueueOf(PROF_POOL_CSE);

        // 9개 과목 각 1개씩 강의 생성
        for (String subjectCd : SUBJECTS) {
            inserted += insertOne(subjectCd, pollAndRotate(profQueue), CAPACITY);
        }

        log.info("Inserted LECTURE rows (CSE 2nd Year) = {}", inserted);
        assertEquals(9, inserted, "9개 강의가 생성되어야 합니다.");

        log.info("LECTURE_ID 범위: LECT90000000300 ~ LECT{}",
                 String.format("%011d", lectSeq - 1));
    }

}
