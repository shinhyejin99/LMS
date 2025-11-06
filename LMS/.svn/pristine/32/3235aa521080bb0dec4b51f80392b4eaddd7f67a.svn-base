/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 9. 27.     	정태일            최초 생성
 *
 * </pre>
 */

// /js/app/core/paging.js


/**
 * 1. 서버에서 받은 페이징 정보(숫자)를 기반으로 HTML을 생성하고 삽입하는 함수
 * (portalJob.js의 loadJobList에서 호출)
 * @param {object} paginationInfo - totalPage, currentPage, startPage, endPage 등의 숫자 정보
 */
const displayPagination = (paginationInfo) => {

	const container = document.getElementById('paginationContainer');
	if (!container) return;
	console.log("페이징 정보:", paginationInfo);
	const { totalPage, currentPage, startPage, endPage } = paginationInfo;
	let html = `<div class="pagination">\n`;

	const fnName = 'fnPaging'; // 호출할 JS 함수명

	// ◀ 이전 페이지 (currentPage > 1 일 때만 보임)
	if (currentPage > 1) {
		html += `<a href="javascript:void(0);" class="page-btn prev" onclick="${fnName}(${currentPage - 1})">◀</a>\n`;
	}


	// 이전 블록 버튼 (<<)
	if (startPage > 1) {
		html += `<a href='javascript:void(0);' class='page-btn' onclick='${fnName}(${startPage - 1})'>«</a>\n`;
	}

	// 페이지 번호 버튼
	for (let page = startPage; page <= endPage; page++) {
		const activeClass = page === currentPage ? ' active' : '';
		html += `<a href='javascript:void(0);' class='page-btn${activeClass}' onclick='${fnName}(${page})'>${page}</a>\n`;
	}

	// 다음 블록 버튼 (>>)
	if (endPage < totalPage) {
		html += `<a href='javascript:void(0);' class='page-btn' onclick='${fnName}(${endPage + 1})'>»</a>\n`;
	}

	// ▶ 다음 페이지 (currentPage < totalPage 일 때만 보임)
	if (currentPage < totalPage) {
		html += `<a href="javascript:void(0);" class="page-btn next" onclick="${fnName}(${currentPage + 1})">▶</a>\n`;
	}

	html += `</div>`;

	container.innerHTML = html;
};


/** 2. 페이징 버튼 클릭 시 목록을 다시 로드하기 위해 호출되는 전역 함수 (동일) */
function fnPaging(page) {
	if (window.loadListFn) {
		window.loadListFn(page);
	}
}