package kr.or.jsu.dto;

import java.util.List;

import lombok.Data;

/**
 * 학생) 등록금 고지서 확인 통합 DTO
 * @author 송태호
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
public class TuitionNoticeDTO {
	private TuitionDetailDTO mainInfo; // 등록금 납부메인 정보 DTO
    private List<TuitionSimpleDTO> tuitionDetails; // 등록금 상세 DTO
    private List<ScholarshipSimpleDTO> scholarshipDetails; // 장학금 상세 DTO
}
