package kr.or.jsu.core.paging;

import java.io.Serializable;

import lombok.Getter;

/**
 * 페이징 처리와 관련된 모든 데이터를 가진 객체
 *
 * @author 정태일
 * @since 2025. 9. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 27.     	정태일	          최초 생성
 *	2025. 9. 29.		김수현			screenSize setter 추가
 * </pre>
 */
@Getter
public class PaginationInfo<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public PaginationInfo() {
		this(10, 5);
	}

	public PaginationInfo(int screenSize, int blockSize) {
		super();
		this.screenSize = screenSize;
		this.blockSize = blockSize;
	}

	private int totalRecord; // 총 건수 (쿼리로 채움)DB 조회

	private int screenSize;  // 페이지당 레코드 수(임의 결정)

	private int blockSize;   // 페이지 블록 크기(임의 결정)

	private int currentPage; // 요청 페이지(1-base)

	private String noticeTypeCd; // 공지사항 유형 코드 (공지 목록 조회 조건으로 사용)

	// 검색 조건
	private SimpleSearch simpleSearch; // 단순 키워드 검색에 사용
	public void setSimpleSearch(SimpleSearch simpleSearch) {
		this.simpleSearch = simpleSearch;
	}

	private T detailSearch;
	public void setDetailSearch(T detailSearch) {
		this.detailSearch = detailSearch;
	}

	public int getStartPage() {
		return ((currentPage - 1) / blockSize) * blockSize + 1;
	}

	public int getEndPage() {
		int endPage = blockSize * ((currentPage + (blockSize - 1)) / blockSize);
		return Math.min(endPage, getTotalPage());
	}

	public int getTotalPage() {
//		return (totalRecord + (screenSize - 1)) / screenSize;
		// 0건이면 1페이지로 보정(렌더러에서 totalPage<=1이면 비노출)
        return Math.max(1, (totalRecord + (screenSize - 1)) / screenSize);
	}

	public int getEndRow() {
		return screenSize * currentPage;
	}

	public int getStartRow() {
		return getEndRow() - (screenSize - 1);
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	// url 파라미터로 size를 받기 때문에 screenSize에 바인딩할 수 있도록 setter 추가
	public void setScreenSize(int screenSize) {
		this.screenSize = screenSize;
	}

	/**
	 * 공지사항 유형 코드를 설정합니다.
	 * @param noticeTypeCd 공지 유형 코드
	 */
	public void setNoticeTypeCd(String noticeTypeCd) {
		this.noticeTypeCd = noticeTypeCd;
	}
}
