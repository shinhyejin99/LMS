package kr.or.jsu.dto;

import lombok.Data;

/**
 * 학생 학기별 성적 DTO
 * @author 김수현
 * @since 2025. 10. 24.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 24.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class SemesterGradeDTO {
	private String yeartermCd;      // 학년도학기코드 (2025_REG1)
    private String yeartermName;    // 학년도학기명 (2025학년도 1학기)
    private Double avgGrade;        // 평균 평점
    private String formattedAvgGrade;
    private Integer subjectCount;   // 수강 과목 수


}
