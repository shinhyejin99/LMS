package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.extern.slf4j.Slf4j;

/**
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
class G_StuEnrollDummyGenerator4 {
	@Autowired
    DummyDataMapper ddMapper;

    // ===== 공통 설정 =====
    private static final String ENROLL_STATUS = "ENR_DONE";
    private static final String RETAKE_YN = "N";
    private static final LocalDateTime STATUS_CHANGE_AT = LocalDateTime.of(2025, 6, 22, 0, 0);

    // ===== ENROLL_ID 더미 시퀀스 =====
    private long enrollSeq = 900010000L;

    private String nextEnrollId() {
        return "STENRL" + String.format("%09d", enrollSeq++);
    }

    // ===== 대상 학생 (컴공 2학년 20명) =====
    private static final String[] STUDENTS = {
        "202400001", "202400002", "202400003", "202400004", "202400005",
        "202400006", "202400007", "202400008", "202400009", "202400010",
        "202300002", "202300004", "202300006", "202300008", "202300010",
        "202200002", "202200004", "202200006", "202200008", "202200010"
    };

    // ===== 대상 강의 (9개) =====
    private static final String[] LECTURES = {
		"LECT70000000000", "LECT70000000001", "LECT70000000002",
	    "LECT70000000003", "LECT70000000004", "LECT70000000005",
	    "LECT70000000006", "LECT70000000007", "LECT70000000008"
    };

    // ===== 성적 풀 (70%는 3.0 이상, 30%는 낮은 성적 포함) =====
    private static final double[] HIGH_GRADE_POOL = {4.5, 4.0, 3.5, 3.0}; // 3.0 이상
    private static final double[] LOW_GRADE_POOL = {3.0, 2.5, 2.0};       // 낮은 성적 포함

    /**
     * 학생별로 성적 경향 결정:
     * - 70% 학생: 주로 높은 성적 (평점 3.0 이상 보장)
     * - 30% 학생: 섞인 성적
     */
    private double pickGradeForStudent(int studentIndex) {
        boolean isHighAchiever = studentIndex < (STUDENTS.length * 0.7); // 첫 70%

        if (isHighAchiever) {
            // 높은 성적 위주
            int idx = ThreadLocalRandom.current().nextInt(HIGH_GRADE_POOL.length);
            return HIGH_GRADE_POOL[idx];
        } else {
            // 섞인 성적
            int idx = ThreadLocalRandom.current().nextInt(LOW_GRADE_POOL.length);
            return LOW_GRADE_POOL[idx];
        }
    }

    private int insertOne(String stuNo, String lectureId, int studentIndex) {
        StuEnrollLctVO vo = new StuEnrollLctVO();
        vo.setEnrollId(nextEnrollId());
        vo.setStudentNo(stuNo);
        vo.setLectureId(lectureId);
        vo.setEnrollStatusCd(ENROLL_STATUS);
        vo.setRetakeYn(RETAKE_YN);
        vo.setStatusChangeAt(STATUS_CHANGE_AT);

        double grade = pickGradeForStudent(studentIndex);
        vo.setAutoGrade(grade);
        vo.setFinalGrade(grade);
        vo.setChangeReason(null);

        return ddMapper.insertOneDummyEnrollment(vo);
    }

    @Test
    void insertCSE2ndYearEnrollments() {
        int totalInserted = 0;
        Random rand = new Random(20250614); // 재현 가능한 시드

        // 각 학생마다 5~9개 과목을 랜덤하게 수강
        for (int i = 0; i < STUDENTS.length; i++) {
            String studentNo = STUDENTS[i];

            // 이 학생이 들을 과목 수 (5~9개 사이 랜덤)
            int numCourses = 5 + rand.nextInt(5); // 5, 6, 7, 8, 9 중 하나

            // 9개 강의 중에서 numCourses개를 랜덤 선택
            List<String> allLectures = new ArrayList<>(Arrays.asList(LECTURES));
            Collections.shuffle(allLectures, rand);
            List<String> selectedLectures = allLectures.subList(0, numCourses);

            // 선택된 강의들에 수강 등록
            for (String lectureId : selectedLectures) {
                totalInserted += insertOne(studentNo, lectureId, i);
            }

            log.debug("학생 {} : {}개 과목 수강", studentNo, numCourses);
        }

        log.info("Inserted STU_ENROLL_LCT rows (CSE 2nd Year) = {}", totalInserted);
        assertTrue(totalInserted >= 100, "최소 100건 이상 생성되어야 합니다.");
        assertTrue(totalInserted <= 180, "최대 180건 이하여야 합니다.");

        log.info("Last ENROLL_ID used = STENRL{}", String.format("%09d", enrollSeq - 1));
        log.info("예상 범위: 각 학생당 5~9개 과목 수강 (총 20명 × 평균 7개 = 약 140건)");
    }
}
