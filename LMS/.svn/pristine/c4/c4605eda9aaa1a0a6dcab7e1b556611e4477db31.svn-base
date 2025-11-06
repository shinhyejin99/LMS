/** * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 26.     	정태일           최초 생성
 * 2025.10. 01.			정태일			페이지 및 조회수 기능 추가
 * 2025.10. 23.			정태일			긴급 기능 추가
 * </pre>
 */
// 조회수 증가 호출 함수
//const incrementNoticeViewCount = async (noticeTypeCd, noticeId) => {
const incrementNoticeViewCount = async (noticeId) => {
//	const url = `/rest/portal/notice/${noticeTypeCd}/${noticeId}/view-count`;
	const url = `/rest/portal/notice/${noticeId}/view-count`;
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

  // 상세보기 페이지 버튼
  const listBtn   = document.getElementById('listNoticeBtn');
  const editBtn   = document.getElementById('editNoticeBtn');
  const deleteBtn = document.getElementById('deleteNoticeBtn');

  if (listBtn) {
    listBtn.addEventListener('click', () => {
      window.location.href = '/portal/notice/list';
    });
  }

  if (editBtn) {
    editBtn.addEventListener('click', () => {
      const noticeId = getNoticeIdFromUrl();
      if (!noticeId) {
        alert('수정할 공지사항 ID를 찾을 수 없습니다.');
        return;
      }
      window.location.href = `/portal/notice/modify/${noticeId}`;
    });
  }
  // 삭제 버튼 이벤트 처리
  if (deleteBtn) {
    deleteBtn.addEventListener('click', () => {
      const noticeId = getNoticeIdFromUrl();
      
      
//      if (!noticeId) {
//        alert('삭제할 공지사항 ID를 찾을 수 없습니다.');
//        return;
//      }
//
//      if (!confirm(`공지사항 [${noticeId}]를 정말 삭제하시겠습니까?`)) {
//        return;
//      }

		// 1. 유효성 검증 (코드 중복 제거 및 confirmButtonColor 수정)
		if (!noticeId) {
		    Swal.fire({
		      icon: "error", 
		      title: "삭제 실패",
		      text: "삭제할 채용 정보의 ID를 찾을 수 없습니다. 페이지를 새로고침 후 다시 시도해 주세요.",
		      confirmButtonText: '확인',
		      // 버튼 색상을 오류에 맞춰 붉은색 계열로 수정 (이전 피드백에서 놓침)
		      confirmButtonColor: '#dc3545' 
		    });
		    return;
		}
      
		// 2. 삭제 확인
		Swal.fire({
	        title: `공지사항을 삭제하시겠습니까?`, 
	        text: '삭제된 내용은 복구할 수 없습니다.',
	        icon: 'warning', 
	        iconColor: '#7bcfe4',
	        showCancelButton: true, 
	        confirmButtonColor: '#EF5350', // 빨간색: 삭제 (긍정)
	        cancelButtonColor: '#6c757d',  // 회색: 취소 (부정)
	        confirmButtonText: '삭제',      // '삭제'로 변경
	        cancelButtonText: '취소'       // '취소'로 변경
	    }).then((result) => {
			// 사용자가 '삭제' (빨간색) 버튼을 눌렀을 때
			if (result.isConfirmed) {	
				// 폼을 생성하여 POST 요청으로 삭제 실행
			      const form = document.createElement('form');
			      form.method = 'POST';
			      form.action = `/portal/notice/remove/${noticeId}`;
			      document.body.appendChild(form);
			      form.submit();
			     }
		    });
	    })
  }

  const registBtn = document.getElementById('registNoticeBtn');
  if (registBtn) {
    registBtn.addEventListener('click', () => {
      window.location.href = '/portal/notice/create';
    });
  }

  // 목록 페이지일 경우 자동 로드
  const path = window.location.pathname;
  const segments = path.split('/').filter(Boolean);
  // /portal/notice/list/{type} 인지 판별
//  if (segments.length >= 4 && segments[0] === 'portal' && segments[1] === 'notice' && segments[2] === 'list') {
//    const type = segments[3]; // GENERAL or ACADEMIC (현재 패턴)
//    loadNoticeList(type);
 if (segments[0] === 'portal' && segments[1] === 'notice' && segments[2] === 'list') {
   loadNoticeList();
  }
  
 
 // 상세 자동 로드: /portal/notice/detail/{id}
 if (segments.length >= 4 && segments[0] === 'portal' && segments[1] === 'notice' && segments[2] === 'detail') {
   const noticeId = segments[3];
   fetchAndRenderNoticeDetail(noticeId);
 }
});






/** 공지사항 목록 조회 */
//const loadNoticeList = async (noticeTypeCd, currentPage = 1, searchType = '', searchWord = '', screenSize = 10) => {
const loadNoticeList = async (currentPage = 1, searchType = '', searchWord = '', screenSize = 10) => {
  const blockSize = 5;
//  let url = `/rest/portal/notice/list/${noticeTypeCd}?currentPage=${currentPage}&screenSize=${screenSize}`;
  let url = `/rest/portal/notice/list?currentPage=${currentPage}&screenSize=${screenSize}`;

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

        // paging 정보 없으면 직접 계산 (portalJob 패턴에서처럼)
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

//        displayNoticeTable(data.list, noticeTypeCd, paging, totalCount, screenSize);
        displayNoticeTable(data.list, paging, totalCount, screenSize);

        displayPagination({
          totalPage: paging.totalPage,
          currentPage: paging.currentPage,
          startPage: paging.startPage,
          endPage: paging.endPage,
        });

        // 페이징 버튼 함수 이름 통일
//        window.fnPaging = (nextPage) => loadNoticeList(noticeTypeCd, nextPage, searchType, searchWord, screenSize);
        window.fnPaging = (nextPage) => loadNoticeList(nextPage, searchType, searchWord, screenSize);
      } else {
        clearAndShowError('등록된 공지사항이 없습니다.');
        document.getElementById('paginationContainer').innerHTML = '';
      }
    })
    .catch(error => {
      console.error('목록 로드 오류:', error);
      clearAndShowError('데이터를 불러오는 중 오류가 발생했습니다.');
      document.getElementById('paginationContainer').innerHTML = '';
    });
};

/** 검색 */
const collectSearch = () => {
  const searchType = document.querySelector('.search-select').value;
//  const searchType = ''; // 통합검색으로 바꿀때 적용
  const searchWord = document.querySelector('.search-input').value.trim();
  return { searchType, searchWord };
};

const searchNotices = () => {
  const { searchType, searchWord } = collectSearch();
//  const noticeTypeCd = getNoticeTypeFromUrl();
//  loadNoticeList(noticeTypeCd, 1, searchType, searchWord);
  loadNoticeList(1, searchType, searchWord);
};

/** 에러 메시지 표시 */
const clearAndShowError = (message) => {
  const container = document.getElementById('noticeList');
  container.innerHTML = `<div class="no-data">${message}</div>`;
};

/** 목록 테이블 렌더링 */

//const displayNoticeTable = (noticeList, noticeTypeCd, paging, totalCount, screenSize) => {

  const displayNoticeTable = (noticeList, paging, totalCount, screenSize) => {

  const container = document.getElementById('noticeList');

  if (!noticeList || noticeList.length === 0) {

    container.innerHTML = '<div class="no-data">등록된 공지사항이 없습니다.</div>';

    return;

  }



  const safeTotal = Number.isFinite(totalCount) ? totalCount : noticeList.length;

  const safePage = Number(paging?.currentPage) > 0 ? Number(paging.currentPage) : 1;

  const safeSize = Number(screenSize) > 0 ? Number(screenSize) : noticeList.length;

  const startNo = Math.max(0, safeTotal - (safePage - 1) * safeSize);



  let html = `

    <style>

        .btn-sm { padding: 0.2rem 0.5rem; font-size: 0.8rem; }

    </style>

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



  noticeList.forEach((notice, idx) => {

    const rownum = startNo - idx;

    const urgentBadge = notice.isUrgent === 'Y' ? '<span class="badge-urgent">긴급</span>' : '';

    

    let managementCell = '';

    if (isStaff) {

      const buttonText = notice.isUrgent === 'Y' ? '해제' : '긴급';

      managementCell = `

        <td>

          <button class="btn btn-sm btn-urgent-toggle" data-notice-id="${notice.noticeId}" data-is-urgent="${notice.isUrgent}">

            ${buttonText}

          </button>

        </td>

      `;

    }



    html += `

      <tr>

        <td>${rownum}</td>

        <td style="text-align: left;">

          <a href="/portal/notice/detail/${notice.noticeId}" class="job-title-link" data-notice-id="${notice.noticeId}">

            ${urgentBadge}${notice.title}

          </a>

        </td>

        <td>${notice.stfDeptName || '-' }</td>

        <td>${formatNoticeDate(notice.createAt)}</td>

        <td>${notice.viewCnt}</td>

      </tr>

    `;

  });



  html += '</tbody></table>';

  container.innerHTML = html;



  // 목록 클릭 핸들러 

  document.querySelectorAll('.job-title-link').forEach((link) => {

    link.addEventListener('click', (e) => {

      e.preventDefault();

      const noticeId = link.dataset.noticeId.trim();

      const detailUrl = link.href;

      incrementNoticeViewCount(noticeId);

      window.location.href = detailUrl;

    });

  });



  // 긴급 버튼 핸들러 (교직원일 경우에만 등록)

  if (isStaff) {

    document.querySelectorAll('.btn-urgent-toggle').forEach(button => {

      button.addEventListener('click', (e) => {

        const noticeId = e.target.dataset.noticeId;

        const isUrgent = e.target.dataset.isUrgent;

        // updateUrgentStatus 함수는 곧 추가될 예정입니다.

        updateUrgentStatus(noticeId, isUrgent);

      });

    });

  }

};

/** 작성일 포맷 */
function formatNoticeDate(dateString) {
  if (!dateString) return '-';
  try {
    const d = new Date(dateString);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  } catch {
    return '-';
  }
}

///** URL에서 타입 추출 */
//const getNoticeTypeFromUrl = () => {
//  const seg = window.location.pathname.split('/').filter(Boolean);
//  return seg[3] || 'GENERAL';
//};

/** URL에서 ID 추출 */
const getNoticeIdFromUrl = () => {
  const seg = window.location.pathname.split('/').filter(Boolean);
  return seg[3];
};


const fetchAndRenderNoticeDetail = (noticeId) => {
  const url = `/rest/portal/notice/detail/${noticeId}`;
  fetch(url)
    .then(response => {
      if (!response.ok) throw new Error(`HTTP Error! Status: ${response.status}`);
      return response.json();
    })
    .then(data => {
      if (data.success && data.detail) {
        const detail = data.detail;
        document.querySelector('.notice-title').innerText = detail.title || '-';
        document.getElementById('stfDeptName').innerText = detail.stfDeptName || '-';
        document.getElementById('createAt').innerText = formatNoticeDate(detail.createAt);
        document.getElementById('viewCnt').innerText = detail.viewCnt ?? '-';
        document.getElementById('noticeDetailContent').innerHTML = detail.content || '-';

        // 첨부파일 렌더링 필요 시 구현 가능

      } else {
        alert(data.message || '존재하지 않는 공지사항입니다.');
      }
    })
    .catch(error => {
      console.error('공지사항 상세 데이터 로드 오류:', error);
      alert('공지사항 상세 데이터를 불러오는 중 오류가 발생했습니다.');
    });
};

/** 긴급 공지 상태 변경 */
const updateUrgentStatus = async (noticeId, currentIsUrgent) => {
  const newIsUrgent = currentIsUrgent === 'Y' ? 'N' : 'Y';
  const actionText = newIsUrgent === 'Y' ? '긴급 공지로 등록' : '긴급 공지를 해제';

  // SweetAlert2를 사용한 확인 창 (기존 코드 스타일과 통일)
  Swal.fire({
      title: `${actionText}하시겠습니까?`,
      icon: 'question',
      iconColor: '#7bcfe4',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#6c757d',
      confirmButtonText: '확인',
      cancelButtonText: '취소'
  }).then(async (result) => {
      if (result.isConfirmed) {
          const url = `/rest/portal/notice/${noticeId}/urgent`;
          try {
              const response = await fetch(url, {
                  method: 'PATCH',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ isUrgent: newIsUrgent })
              });
              const resData = await response.json();
              if (resData.success) {
                  Swal.fire('성공', '상태가 성공적으로 변경되었습니다.', 'success');
                  loadNoticeList(); // 목록 새로고침
              } else {
                  Swal.fire('실패', `상태 변경에 실패했습니다: ${resData.message}`, 'error');
              }
          } catch (error) {
              console.error('긴급 상태 변경 중 오류 발생:', error);
              Swal.fire('오류', '처리 중 오류가 발생했습니다.', 'error');
          }
      }
  });
};
