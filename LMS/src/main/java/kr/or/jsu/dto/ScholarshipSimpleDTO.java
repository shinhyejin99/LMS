package kr.or.jsu.dto;

import lombok.Data;

/**
 * 학생) 등록금 고지서에 나올 장학금 항목 DTO
 * @author 김수현
 * @since 2025. 10. 4.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 4.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class ScholarshipSimpleDTO { //ScholarshipDetailDTO 
	private String itemName;  // 장학금 항목명
    private Integer amount;   // 감면액
}
