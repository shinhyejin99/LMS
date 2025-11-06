package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 교수) 수강신청 학생 목록 DTO
 * @author 김수현
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	김수현	        최초 생성
 *	2025. 10. 21.		김수현			D3 차트용 필드 추가
 * </pre>
 */
@Data
public class ProfLectureStuListDTO {
	private String lectureId;           // 강의ID
    private String subjectName;         // 과목명
    private String lectureName;         // 강의명
    private Integer credit;             // 학점
    private Integer hour;               // 시수
    private String completionName;      // 이수구분명
    private String targetGrades;        // 대상학년 (예: "1,2학년")
    private Integer maxCap;             // 정원
    private Integer currentEnroll;      // 현재 수강 인원
    private Double enrollRate;          // 수강률 (%)

    private double chartRate; 			// D3 차트용 (0.805 형태의 실수)

    private String timeInfo;			// 교시 형태(가공될)
    private String placeName;           // 강의실
}
