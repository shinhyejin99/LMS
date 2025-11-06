package kr.or.jsu.classregist.common;

import kr.or.jsu.core.paging.SimpleSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 강의 조회 페이지네이션용 검색
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
@EqualsAndHashCode(callSuper = true)
public class LectureSearchCondition extends SimpleSearch {
	private static final long serialVersionUID = 1L;

	private String yearTermCd;      // 학기

    // 페이지네이션
    private int page = 1;
    private int pageSize = 10;
    private int offset;

    private String gradeFilter;      // 학년 필터 (1ST, 2ND, 3RD, 4TH, ALL)
    private String studentGrade;     // 학생 학년 (정렬용)

    public void calculateOffset() {
        this.offset = (page - 1) * pageSize;
    }
}
