package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 교직원) 수강신청 관리용 페이징 DTO
 * @author 김수현
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class StaffCourseSearchDTO {
	private String yeartermCd;      // 학기 코드
    private String searchKeyword;   // 검색어 (강의명 또는 교수명)
    private int page = 1;           // 현재 페이지
    private int pageSize = 10;      // 페이지당 건수
    private int offset;             // OFFSET 계산용

    /**
     * offset 계산
     */
    public void calculateOffset() {
        this.offset = (this.page - 1) * this.pageSize;
    }
}
