package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 강의 상세 조회 DTO
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class LectureDetailDTO {
	// 기본 강의 정보
    private String lectureId;           // 강의ID
    private String subjectCd;           // 과목코드
    private String professorNo;         // 교수번호
    private String subjectName;         // 과목명
    private String professorName;       // 교수명

    // 강의 상세 정보
    private String lectureIndex;        // 강의개요
    private String lectureGoal;         // 강의목표
    private String prereqSubject;       // 선수과목

    // 학점/시수
    private Integer credit;             // 학점
    private Integer hour;               // 시수

    // 이수구분
    private String completionName;      // 이수구분명 (전필/전선/교선 등)

    // 강의실 및 시간
    private String placeName;           // 강의실
    private String timeInfo;            // 시간 정보 (예: 월 09:00-12:00, 수 14:00-16:00)

    // 정원 정보
    private Integer maxCap;             // 최대 정원
    private Integer currentEnroll;      // 현재 신청 인원
    private Integer remainSeats;        // 남은 자리

    private String targetGrades;  // 대상학년
}
