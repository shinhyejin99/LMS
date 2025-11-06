package kr.or.jsu.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 학년 결정 결과 DTO
 * @author 김수현
 * @since 2025. 10. 23.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 23.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
@AllArgsConstructor
public class GradeResultDTO {
	private String gradeCd;
    private String userInfo;
    private boolean showAll;  // 전체 학년 조회 여부
}
