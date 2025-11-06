package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 강의 목록 표시 DTO
 * @author 김수현
 * @since 2025. 10. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class LectureListDTO {
	private String lectureId;           // 강의ID
    private String subjectName;         // 과목명
    private String lectureName;         // 강의명 (과목명과 동일)
    private String professorName;       // 담임교수
    private String placeName;           // 강의실
    private String timeInfo;            // 시간
    private Integer credit;             // 학점
    private Integer hour;               // 시수
    private String completionName;      // 강의타입 (전필/전선/교선)
    private Integer maxCap;             // 정원
    private Integer currentEnroll;      // 현재 신청 인원
    private Integer remainSeats;        // 남은 자리
    private boolean wishlisted;       	// 찜 여부 (로그인한 학생 기준)
    private boolean applied;     		// 수강신청 여부

    private String periodInfo;			// 교시 형태(가공될)
    private String targetGrades;  		// 대상학년 (예: "1,2학년")
    private Double enrollRate;  		// 충족률
}
