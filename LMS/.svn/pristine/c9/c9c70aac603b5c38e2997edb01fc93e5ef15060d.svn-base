package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 검색 조건용
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
public class WishlistSearchDTO {
	private String searchType;      // "ALL", "SUBJECT", "LECTURE", "PROFESSOR"
    private String searchKeyword;   // 검색어
    private String studentNo;       // 학번

    // 페이지네이션
    private int page = 1;           // 현재 페이지
    private int pageSize = 10;      // 페이지당 개수
    private int offset;             // OFFSET 계산용

    // 정렬
    private String orderBy = "WISHLIST_AT ASC"; // 오래된 순

    public void calculateOffset() {
        this.offset = (page - 1) * pageSize;
    }
}
