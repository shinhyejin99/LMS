package kr.or.jsu.dto.request.lms.lecture.apply;

import lombok.Data;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * 강의개설 신청용 DTO. <br>
 * 주차별 계획, 성적산출항목&비율 객체는 inner class로 포함합니다.
 * 
 * @author 송태호
 * @since 2025. 10. 28.
 */
@Data
public class LctOpenApplyReq implements Serializable {
    private static final long serialVersionUID = 1L;

    // ===== 기본 강의 정보 =====
    private String subjectCd;      // 과목 코드
    private String yeartermCd;     // 학년도_학기 코드 (ex: 2025_REG1)
    private String lectureIndex;   // 강의 개요
    private String lectureGoal;    // 강의 목표
    private String prereqSubject;  // 선수 과목
    @Min(value = 1, message = "예상수강인원은 0명 초과여야 합니다.")
    private Integer expectCap;     // 예상 수강인원
    private String desireOption;   // 희망 개설 옵션

    // ===== 하위 목록 =====
    private List<LctWeekbyReq> weekbyList;           // 주차별 계획 목록
    private List<LctGraderatioReq> graderatioList;   // 평가비율 목록

    // -------------------------------------------------------
    // ✅ 내부 static 클래스: 주차별 계획
    // -------------------------------------------------------
    @Data
    public static class LctWeekbyReq implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer lectureWeek;  // 주차 번호
        private String weekGoal;      // 주차별 목표
        private String weekDesc;      // 주차별 내용
    }

    // -------------------------------------------------------
    // ✅ 내부 static 클래스: 평가비율
    // -------------------------------------------------------
    @Data
    public static class LctGraderatioReq implements Serializable {
        private static final long serialVersionUID = 1L;

        private String gradeCriteriaCd; // 평가항목 코드 (ATTD, TASK 등)
        private Integer ratio;          // 비율 (0~100)
    }
}