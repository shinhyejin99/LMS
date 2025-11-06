package kr.or.jsu.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학점 정보 DTO
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
@NoArgsConstructor
@AllArgsConstructor
public class CreditInfoDTO {
	private String gradeCd;       // 학년 (1, 2, 3, 4)
    private String termCd;        // 학기 (1, 2)
    private Integer maxCredit;    // 최대 학점
    private String label;         // 현재 학기/다음 학기
    private String yeartermCd;    // yearterm_cd (2024_REG1)
}
