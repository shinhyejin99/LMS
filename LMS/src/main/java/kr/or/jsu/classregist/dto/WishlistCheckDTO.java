package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 찜하기 체크용 DTO (내부 로직용)
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class WishlistCheckDTO {
	private String lectureId;
    private String subjectCd;
    private String professorNo;
    private Integer maxCap;
    private Integer credit;        // SUBJECT 테이블에서 가져옴
    private String subjectName;
}
