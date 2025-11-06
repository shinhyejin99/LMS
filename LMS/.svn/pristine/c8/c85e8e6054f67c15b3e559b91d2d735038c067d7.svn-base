package kr.or.jsu.dto;

import java.util.List;

import lombok.Data;

/**
 * 등록금 납부확인서(pdf) DTO
 * @author 김수현
 * @since 2025. 10. 22.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 22.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class PaymentReceiptDTO {
	// 인적사항
    private String deptName;        // 학과
    private String studentNo;       // 학번
    private String birthDate;       // 생년월일 (주민번호 앞자리)
    private String studentName;     // 이름
    private String payDate;         // 납부일자

    // 납부 정보
    private String yearTerm;        // 2025학년도 1학기 (2025_REG1)
    private String universityName;  // 대학교명
    private int tuitionSum;     // 등록금 합계
    private Integer scholarshipSum; // 감면액 합계
    private Integer finalAmount;    // 실납입액

    // 세부 내역
    private List<TuitionSimpleDTO> tuitionDetails;         // 등록금 세부
    private List<ScholarshipSimpleDTO> scholarshipDetails; // 장학금 세부
}
