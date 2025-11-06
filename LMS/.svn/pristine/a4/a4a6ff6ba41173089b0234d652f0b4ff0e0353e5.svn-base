package kr.or.jsu.dto;

import lombok.Data;

@Data
public class LctApplyDetailDTO {
	private String approveId;       // 결재 문서 ID (APPROVAL 조인 키)
    private String lctApplyId;      // 강의 신청 ID (LCT_OPEN_APPLY 기본 키)

    // === LCT_OPEN_APPLY 정보 (LECTURE INSERT에 필요) ===
    private String subjectCd;       // 과목 코드 (강의 개설 시 LECT_CD로 사용)
    private String professorNo;     // 교수 번호
    private String yeartermCd;      // 연/학기 코드
    private String lectureIndex;    // 분반 (LECTURE_INDEX)
    private int expectCap;          // 희망 인원 (EXPECT_CAP)
    private String lectureGoal;     // 강의 목표 (LECTURE_GOAL)
    private String prereqSubject;   // 선수 과목 (PREREQ_SUBJECT)

    // === 최종 배정 정보 (JS에서 받아옴) ===
    private String assignedRoomId;      // 배정된 강의실 ID (예: 101)
    private String assignedTimeblock;   // 배정된 시간대 (예: 0900_1030)
    private String assignedDay;

    private String subjectName;     // 강의명 (S.SUBJECT_NAME -> JSP에서 lectureName 대신 사용)
    private String lectureHours;    // 이수 시간 (S.HOUR)
    private String credit;          // 학점 (S.CREDIT)
    private String professorName;   // 교수명 (U.LAST_NAME || U.FIRST_NAME)
}
