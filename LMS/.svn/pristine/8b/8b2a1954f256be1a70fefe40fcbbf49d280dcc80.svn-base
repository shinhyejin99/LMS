package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@Transactional
public class F_LectureDummyGenerator5 {
	@Autowired
    DummyDataMapper ddMapper;

    // ===== 고정 파라미터 =====
    private static final String YEAR_TERM = "2026_REG1";
    // VO에 맞춰 LocalDateTime 유지
    private static final LocalDateTime END_AT = LocalDateTime.of(2026, 6, 22, 0, 0);
    private static final int TARGET_LECTURES = 8; // 20개 강의 목표 (10과목 * 2분반)

    // 강의실 접두사 : 20개 배정을 위해 공학관 전체로 확대 => 3층 강의실 20개를 사용
    private static final String ROOM_PREFIX_CSE = "RM-ENGI-HQ-04%";

    // LECTURE(강의) 테이블 PK : 시퀀스 미사용 (70000001000L부터 시작)
    private long lectSeq = 70000001100L;
    private String nextLectureId() {
        return "LECT" + String.format("%011d", lectSeq++);
    }

	// ===== 컴공 대상 과목 코드 (총 10개만 사용, 20개 강의 생성) =====
    private static final String[] SUBJECTS = {
    		"SUBJ90000000160", "SUBJ90000000161", "SUBJ90000000162", "SUBJ90000000163",
            "SUBJ90000000160", "SUBJ90000000161", "SUBJ90000000162", "SUBJ90000000163"
    };

    // ===== 정원 (20~29 사이 랜덤값으로 사용) =====
    private static final int MIN_CAPACITY = 20;
    private static final int MAX_CAPACITY = 30;

	// ===== 컴공 교수 풀 (총 11명) =====
    private static final String[] PROF_POOL_CSE = {
    		"20218010", "20228010", "20228020", "20238010",
    	    "20238020", "20248010", "20248020", "20258010"
    };

    // ===== 3학점 강의 시간 블록 정의 (주당 6블록, 20개 그룹) =====
    private static final String[][] TIMEBLOCK_GROUPS = {
		// 1-5: 오전 시간대 (각 요일별 완전 분리)
	    {"MO0900", "MO0930", "MO1000", "MO1030", "MO1100", "MO1130"}, // G1: 월 오전
	    {"TU0900", "TU0930", "TU1000", "TU1030", "TU1100", "TU1130"}, // G2: 화 오전
	    {"WE0900", "WE0930", "WE1000", "WE1030", "WE1100", "WE1130"}, // G3: 수 오전
	    {"TH0900", "TH0930", "TH1000", "TH1030", "TH1100", "TH1130"}, // G4: 목 오전
	    {"FR0900", "FR0930", "FR1000", "FR1030", "FR1100", "FR1130"}, // G5: 금 오전

	    // 6-10: 오후 시간대 (각 요일별 완전 분리)
	    {"MO1300", "MO1330", "MO1400", "MO1430", "MO1500", "MO1530"}, // G6: 월 오후
	    {"TU1300", "TU1330", "TU1400", "TU1430", "TU1500", "TU1530"}, // G7: 화 오후
	    {"WE1300", "WE1330", "WE1400", "WE1430", "WE1500", "WE1530"}, // G8: 수 오후
	    {"TH1300", "TH1330", "TH1400", "TH1430", "TH1500", "TH1530"}, // G9: 목 오후
	    {"FR1300", "FR1330", "FR1400", "FR1430", "FR1500", "FR1530"}, // G10: 금 오후

	    // 11-15: 저녁 시간대 (각 요일별 완전 분리)
	    {"MO1600", "MO1630", "MO1700", "MO1730", "MO1800", "MO1830"}, // G11: 월 저녁
	    {"TU1600", "TU1630", "TU1700", "TU1730", "TU1800", "TU1830"}, // G12: 화 저녁
	    {"WE1600", "WE1630", "WE1700", "WE1730", "WE1800", "WE1830"}, // G13: 수 저녁
	    {"TH1600", "TH1630", "TH1700", "TH1730", "TH1800", "TH1830"}, // G14: 목 저녁
	    {"FR1600", "FR1630", "FR1700", "FR1730", "FR1800", "FR1830"}, // G15: 금 저녁

	    // 🆕 16-20: 점심시간 (11:30~14:00) 활용 - 완전 독립!
	    {"MO1130", "MO1200", "MO1230", "TU1130", "TU1200", "TU1230"}, // G16: 월+화 점심
	    {"WE1130", "WE1200", "WE1230", "TH1130", "TH1200", "TH1230"}, // G17: 수+목 점심
	    {"FR1130", "FR1200", "FR1230", "MO1530", "MO1600", "MO1630"}, // G18: 금 점심 + 월 늦은 오후
	    {"TU1530", "TU1600", "TU1630", "WE1530", "WE1600", "WE1630"}, // G19: 화+수 늦은 오후
	    {"TH1530", "TH1600", "TH1630", "FR1530", "FR1600", "FR1630"}  // G20: 목+금 늦은 오후
    };

	 // ===== 유틸: 라운드로빈 큐 구성 및 순환 =====
	 private static Deque<String> rrQueueOf(String[] profNos) {
	     Deque<String> q = new ArrayDeque<>();
	     for (String p : profNos) q.addLast(p);
	     return q;
	 }

	 private static Deque<String[]> rrQueueOf(String[][] timeGroups) {
	     Deque<String[]> q = new ArrayDeque<>();
	     for (String[] t : timeGroups) q.addLast(t);
	     return q;
	 }

	 // 교수 번호 순환
	 private static String pollAndRotate(Deque<String> q) {
	     String p = q.pollFirst();
	     q.offerLast(p);
	     return p;
	 }

	 // 시간 그룹 순환
	 private static String[] pollAndRotateTimeGroup(Deque<String[]> q) {
	     String[] t = q.pollFirst();
	     q.offerLast(t);
	     return t;
	 }


	// ===== 공통으로 한 건 INSERT (강의실 배정 로직 포함) =====
    private int insertOne(String subjectCd, String professorNo, String[] timeBlocks, String roomPrefix) {
        Random random = new Random();
        LectureVO vo = new LectureVO();

        // VO 설정
        vo.setLectureId(nextLectureId());
        vo.setSubjectCd(subjectCd);
        vo.setProfessorNo(professorNo);
        vo.setYeartermCd(YEAR_TERM);

        // 정원 범위 (20~29) 사이 랜덤값 사용
        vo.setMaxCap(random.nextInt(MAX_CAPACITY - MIN_CAPACITY + 1) + MIN_CAPACITY);

        vo.setLectureIndex("수강신청 데이터 컴퓨터공학과 강의입니다.");
        vo.setLectureGoal("해당 과목의 핵심 개념을 이해하고 실무 능력을 향상시킵니다.");
        vo.setPrereqSubject(null);
        vo.setCancelYn("N");

        // ⭐ LECTURE 테이블 삽입을 위한 필수 값 설정 ⭐
        vo.setEndAt(END_AT);

        // 1. LECTURE 테이블에 강의 삽입
        int r = ddMapper.insertOneDummyLecture(vo);

        if (r > 0) {
            String lectureId = vo.getLectureId();

            // ⭐ 2. 교수 시간 중복 체크 (가장 먼저 수행) ⭐
            int busyCount = ddMapper.isProfessorBusy(professorNo, timeBlocks);

            if (busyCount > 0) {
                log.error("❌ PROF_BUSY: {} 교수님은 {}개 시간블록이 이미 사용중 (강의ID: {})",
                        professorNo, busyCount, lectureId);
                return 0; // 교수 중복으로 실패
            }

            // 3. 강의실 찾기 (공간 중복 및 정원 체크)
            String roomPlaceCd = ddMapper.findAvailableRoom(timeBlocks, vo.getMaxCap(), roomPrefix);

            if (roomPlaceCd != null) {
                // 4. LCT_ROOM_SCHEDULE에 배정 및 삽입 (6개 블록 모두)
                for (String timeBlockCd : timeBlocks) {
                    ddMapper.insertSchedule(lectureId, roomPlaceCd, timeBlockCd);
                }
                log.info("✅ SUCCESS: {} -> 교수: {} / 강의실: {} / 정원: {}",
                        lectureId, professorNo, roomPlaceCd, vo.getMaxCap());
               return 1; // 성공
            } else {
            	log.error("❌ NO_ROOM: 강의ID {} - 교수 {}, 정원 {}, 시간 [{}]에 맞는 강의실 없음",
                        lectureId, professorNo, vo.getMaxCap(), String.join(",", timeBlocks));
            	return 0; // 강의실 부족으로 실패
            }
        }
        return 0;
    }

    @Test
    void insertLectureDummies_CSE_2ndYear() {
    	int inserted = 0;

        Deque<String> profQueue = rrQueueOf(PROF_POOL_CSE);
        Deque<String[]> timeQueue = rrQueueOf(TIMEBLOCK_GROUPS);

        for (int i = 0; i < TARGET_LECTURES; i++) {
            String subjectCd = SUBJECTS[i % SUBJECTS.length];
            String[] blocks = pollAndRotateTimeGroup(timeQueue);

            // 🆕 교수 충돌 시 다른 교수 찾기
            boolean success = false;
            int maxRetry = PROF_POOL_CSE.length; // 최대 22번 시도

            for (int retry = 0; retry < maxRetry && !success; retry++) {
                String professorNo = pollAndRotate(profQueue);

                log.info("시도 #{}: 과목={}, 교수={} (시도 {}/{}), 시간블록={}",
                         i+1, subjectCd, professorNo, retry+1, maxRetry, String.join(",", blocks));

                int result = insertOne(subjectCd, professorNo, blocks, ROOM_PREFIX_CSE);

                if (result > 0) {
                    inserted++;
                    success = true;
                } else {
                    log.warn("🔄 재시도: 교수 {} 실패, 다음 교수 시도...", professorNo);
                }
            }

            if (!success) {
                log.error("❌❌❌ 강의 #{} 완전 실패: 모든 교수 시도했지만 배정 불가", i+1);
            }
        }

        log.info("===== 결과 요약 =====");
        log.info("성공: {} / 실패: {} / 목표: {}", inserted, TARGET_LECTURES - inserted, TARGET_LECTURES);

        assertEquals(TARGET_LECTURES, inserted, TARGET_LECTURES + "개 강의가 생성되어야 합니다.");
    }
}
