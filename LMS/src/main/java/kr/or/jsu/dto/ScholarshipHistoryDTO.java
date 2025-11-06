package kr.or.jsu.dto;

import lombok.Data;

/**
 * 학생) 개별 장학금 지급 상세 내역
 * @author 김수현
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class ScholarshipHistoryDTO {
	private String semesterName;      // 2025-1학기
    private String scholarshipName;   // 성적우수장학금
    private Integer amount;           // 1,500,000
    private String status;            // 지급완료
}
