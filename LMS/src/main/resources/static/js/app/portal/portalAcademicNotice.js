/**
 * <pre>
 * << 개정이력(Modification Information) >>
 * 수정일      		수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 26.     	정태일           최초 생성 (portalNotice.js 복사)
 * 2025.10. 14.       	정태일           공용 조회수 API 및 코드 정리
 * </pre>
 */

// ✅ 조회수 증가 호출 함수 (공용 API 사용)
const incrementNoticeViewCount = async (noticeId) => {
  const url = `/rest/portal/notice/${noticeId}/view-count`; // This URL is for general notices

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({})
    });
    if (!response.ok) {
      console.error(`조회수 증가 실패: HTTP Status ${response.status}`);
    }
  } catch (error) {
    console.error('조회수 증가 중 통신 오류 발생:', error);
  }
};

// ✅ 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {

  // 검색 버튼 이벤트
  const searchBtn = document.querySelector('#searchBtn');
  if (searchBtn) {
    searchBtn.addEventListener('click', searchNotices);
  }

  // 엔터 키로 검색
  const searchInput = document.querySelector('.search-input');
  if (searchInput) {
    searchInput.addEventListener('keyup', (e) => {
      if (e.key === 'Enter') {
        searchNotices();
      }
    });
  }

  // 등록 버튼 (교직원용)
  const registBtn = document.getElementById('registNoticeBtn');
  if (registBtn) {
    registBtn.addEventListener('click', () => {
      window.location.href = '/portal/academicnotice/create';
    });
  }

  const path = window.location.pathname;
  const segments = path.split('/').filter(Boolean);

  // 목록 페이지 자동 로드
  if (path.includes('/portal/academicnotice/list')) {
    loadNoticeList();
  }

  // 상세 자동 로드: /portal/academicnotice/detail/{id}
  if (segments.length >= 4 && segments[0] === 'portal' && segments[1] === 'academicnotice' && segments[2] === 'detail') {
    const noticeId = segments[3];
    fetchAndRenderAcademicNoticeDetail(noticeId);
  }

  // 상세보기 페이지 버튼
  const listBtn   = document.getElementById('listNoticeBtn');
  const editBtn   = document.getElementById('editNoticeBtn');
  const deleteBtn = document.getElementById('deleteNoticeBtn');

  if (listBtn) {
    listBtn.addEventListener('click', () => {
      window.location.href = '/portal/academicnotice/list';
    });
  }

  if (editBtn) {
    editBtn.addEventListener('click', () => {
      const noticeId = getNoticeIdFromUrl();
      if (!noticeId) {
        alert('수정할 학사 공지 ID를 찾을 수 없습니다.');
        return;
      }
      window.location.href = `/portal/academicnotice/modify/${noticeId}`;
    });
  }
  // 삭제 버튼 이벤트 처리
  if (deleteBtn) {
    deleteBtn.addEventListener('click', () => {
      const noticeId = getNoticeIdFromUrl();

      if (!noticeId) {
        Swal.fire({
          icon: "error",
          title: "삭제 실패",
          text: "삭제할 학사 공지 ID를 찾을 수 없습니다. 페이지를 새로고침 후 다시 시도해 주세요.",
          confirmButtonText: '확인',
          confirmButtonColor: '#dc3545'
        });
        return;
      }

      Swal.fire({
        title: `학사 공지를 삭제하시겠습니까?`,
        text: '삭제된 내용은 복구할 수 없습니다.',
        icon: 'warning',
        iconColor: '#7bcfe4',
        showCancelButton: true,
        confirmButtonColor: '#EF5350',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
      }).then((result) => {
        if (result.isConfirmed) {
          const form = document.createElement('form');
          form.method = 'POST';
          form.action = `/portal/academicnotice/remove/${noticeId}`;
          document.body.appendChild(form);
          form.submit();
        }
      });
    });
  }
});

/** ✅ 학사공지 목록 조회 */
const loadNoticeList = async (currentPage = 1, searchType = '', searchWord = '', screenSize = 10) => {
  const blockSize = 5;
  let url = `/rest/portal/academicnotice/list?currentPage=${currentPage}&screenSize=${screenSize}`;

  if (searchWord) {
    const params = new URLSearchParams();
    params.append('simpleSearch.searchType', searchType);
    params.append('simpleSearch.searchWord', searchWord);
    url += `&${params.toString()}`;
  }

  console.log(`==> fetch URL: ${url}`);

  fetch(url)
    .then(response => {
      if (!response.ok) throw new Error(`HTTP Error! Status: ${response.status}`);
      return response.json();
    })
    .then(data => {
      console.log('API 응답:', data);

      if (data.success && data.list && data.list.length > 0) {
        const totalCount = Number(data.totalCount ?? 0);
        let paging = data.paging;

        if (!paging) {
          const totalPage = Math.max(1, Math.ceil(totalCount / screenSize));
          const currentPageSafe = Math.min(Math.max(1, currentPage), totalPage);
          const currentBlock = Math.floor((currentPageSafe - 1) / blockSize);
          const startPage = currentBlock * blockSize + 1;
          const endPage = Math.min(startPage + blockSize - 1, totalPage);

          paging = {
            totalPage, currentPage: currentPageSafe,
            startPage, endPage,
            pageSize: blockSize,
            screenSize, totalCount
          };
        }

        displayNoticeTable(data.list, paging, totalCount, screenSize);

        displayPagination({
          totalPage: paging.totalPage,
          currentPage: paging.currentPage,
          startPage: paging.startPage,
          endPage: paging.endPage,
        });

        window.fnPaging = (nextPage) => loadNoticeList(nextPage, searchType, searchWord, screenSize);
      } else {
        clearAndShowError('등록된 학사공지가 없습니다.');
        document.getElementById('paginationContainer').innerHTML = '';
      }
    })
    .catch(error => {
      console.error('목록 로드 오류:', error);
      clearAndShowError('데이터를 불러오는 중 오류가 발생했습니다.');
      document.getElementById('paginationContainer').innerHTML = '';
    });
};

/** ✅ 검색 처리 */
const collectSearch = () => {
  const searchType = document.querySelector('.search-select').value;
  const searchWord = document.querySelector('.search-input').value.trim();
  return { searchType, searchWord };
};

const searchNotices = () => {
  const { searchType, searchWord } = collectSearch();
  loadNoticeList(1, searchType, searchWord);
};

/** ✅ 에러 메시지 표시 */
const clearAndShowError = (message) => {
  const container = document.getElementById('academicNoticeList');
  container.innerHTML = `<div class="no-data">${message}</div>`;
};

/** ✅ 목록 테이블 렌더링 */
const displayNoticeTable = (academicNoticeList, paging, totalCount, screenSize) => {
  const container = document.getElementById('academicNoticeList');
  if (!academicNoticeList || academicNoticeList.length === 0) {
    container.innerHTML = '<div class="no-data">등록된 학사공지가 없습니다.</div>';
    return;
  }

  const safeTotal = Number.isFinite(totalCount) ? totalCount : academicNoticeList.length;
  const safePage = Number(paging?.currentPage) > 0 ? Number(paging.currentPage) : 1;
  const safeSize = Number(screenSize) > 0 ? Number(screenSize) : academicNoticeList.length;
  const startNo = Math.max(0, safeTotal - (safePage - 1) * safeSize);

  let html = `
    <table class="job-table">
      <thead>
        <tr>
          <th class="col-num">번호</th>
          <th class="col-title">제목</th>
          <th class="col-dept">작성부서</th>
          <th class="col-date">작성일</th>
          <th class="col-views">조회수</th>
        </tr>
      </thead>
      <tbody>
  `;

  academicNoticeList.forEach((notice, idx) => {
    const rownum = startNo - idx;
    html += `
      <tr>
        <td>${rownum}</td>
        <td>
          <a href="/portal/academicnotice/detail/${notice.noticeId}" class="job-title-link" data-notice-id="${notice.noticeId}">
            ${notice.title}
          </a>
        </td>
        <td>${notice.stfDeptName || '-'}</td>
        <td>${formatDate(notice.createAt)}</td>
        <td>${notice.viewCnt}</td>
      </tr>
    `;
  });

  html += '</tbody></table>';
  container.innerHTML = html;

  // 클릭 시 상세 이동 + 조회수 증가
  document.querySelectorAll('.job-title-link').forEach((link) => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      const noticeId = link.dataset.noticeId.trim();
      const detailUrl = link.href;
      incrementNoticeViewCount(noticeId);
      window.location.href = detailUrl;
    });
  });
};

/** 작성일 포맷 */
function formatDate(dateString) {
  if (!dateString) return '-';
  try {
    const d = new Date(dateString);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  } catch {
    return '-';
  }
}



/* ===== 학사공지 상세: 최소 구현 ===== */

/** URL에서 ID 추출: /portal/academicnotice/detail/{id} */
function getNoticeIdFromUrl() {
  const seg = window.location.pathname.split('/').filter(Boolean);
  return (seg[0] === 'portal' && seg[1] === 'academicnotice' && seg[2] === 'detail') ? seg[3] : null;
}

/** 상세 불러와서 화면에 그리기 */
async function fetchAndRenderAcademicNoticeDetail(noticeId) {
  if (!noticeId) return console.error('학사 공지 ID 없음');

  try {
    const res = await fetch(`/rest/portal/academicnotice/detail/${noticeId}`, {
      headers: { 'Accept': 'application/json' }
    });
    if (!res.ok) throw new Error('HTTP ' + res.status);

    const payload = await res.json();
    const detail = payload?.data ?? payload; // {success,data} 또는 바로 객체 둘 다 대응

    // 제목
    const titleEl = document.querySelector('.notice-title');
    if (titleEl) titleEl.textContent = detail.title ?? '';

    // 요약
    const deptEl = document.getElementById('stfDeptName');
    const dateEl = document.getElementById('createAt');
    const viewEl = document.getElementById('viewCnt');
    if (deptEl) deptEl.textContent = detail.stfDeptName ?? '-';
    if (dateEl) dateEl.textContent = formatDate(detail.createAt);
    if (viewEl) viewEl.textContent = detail.viewCnt ?? 0;

    // 본문(HTML)
    const contentEl = document.getElementById('noticeDetailContent');
    if (contentEl) contentEl.innerHTML = detail.content ?? '';
  } catch (e) {
    console.error('상세 로드 실패:', e);
  }
}

// 전역 노출 (DOMContentLoaded에서 호출하므로 필요)
window.getNoticeIdFromUrl = getNoticeIdFromUrl;
window.fetchAndRenderAcademicNoticeDetail = fetchAndRenderAcademicNoticeDetail;