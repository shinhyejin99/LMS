package kr.or.jsu.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 장학금 조회 응답(통합) DTO
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
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipResponseDTO {
	private Integer totalScholarship;                              // 총 수혜 금액
    private List<ScholarshipDistributionDTO> scholarshipDistribution;  // 종류별 분포
    private List<ScholarshipHistoryDTO> scholarshipHistory;           // 수혜 내역
}
