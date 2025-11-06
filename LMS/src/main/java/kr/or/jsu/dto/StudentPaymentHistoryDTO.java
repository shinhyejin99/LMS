package kr.or.jsu.dto;

import lombok.Data;

/**
 * 학생) 납부 내역 DTO (납부 내역 테이블에 나올 것)
 * @author 김수현
 * @since 2025. 10. 21.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 21.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class StudentPaymentHistoryDTO {
	private String yeartermCd;        // 2025_REG2
    private String semesterName;      // 2025-2학기
    private Integer tuitionSum;       // 등록금
    private Integer scholarshipSum;   // 장학금
    private Integer finalAmount;      // 실납부액
    private String payDate;           // 납부일자
    private String payDoneYn;         // 납부여부
    private String virtualAccount;    // 가상계좌
}
