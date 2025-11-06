/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 10.     	김수현            최초 생성
 *
 * </pre>
 */

/**
 * 교직원 등록금 납부 현황 및 유형 관리 페이지 JS
 * * - 현황 조회 (AJAX)
 * - 상태별 필터링
 * - 전체 선택/해제, 일괄 납부 확인 처리
 */

let currentStatus = 'all';
let paymentData = [];

document.addEventListener('DOMContentLoaded', function() {
    // DOM 로드 완료 후 초기 데이터 조회 시작
    loadPaymentData();

    // 현황 조회/검색 버튼에 이벤트 리스너 추가
    const checkStatusBtn = document.getElementById('check-status');
    if (checkStatusBtn) {
        checkStatusBtn.addEventListener('click', searchPayments);
    }

    // 검색 폼 제출 이벤트 리스너 추가 (폼의 기본 동작 방지)
    const searchForm = document.querySelector('.search-form-container form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            searchPayments();
        });
    }


/**
 * 납부 데이터 조회 (검색 조건 포함)
 */
function searchPayments() {
    loadPaymentData();
}

function loadPaymentData() {
    // JSP에 추가한 검색 조건 ID 사용
    const yeartermCd = document.getElementById('yeartermSelect').value;
    const searchKeyword = document.getElementById('searchInput').value;

    // 실제 서버 통신 주소 사용
    fetch(`/lms/staff/tuition/payment-list?yeartermCd=${yeartermCd}&keyword=${searchKeyword}`)
        .then(response => {
             if (!response.ok) {
                // HTTP 상태 코드가 200 범위가 아니면 오류 발생
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            paymentData = data;
            renderTable(currentStatus);
            updateSummary();
        })
        .catch(error => {
            console.error('데이터 조회 실패:', error);
            alert('데이터를 불러오는데 실패했습니다. 서버/API URL을 확인해주세요.');
            // 데이터 로드 실패 시 테이블을 비워줌
            paymentData = [];
            renderTable(currentStatus);
            updateSummary();
        });
}

/**
 * 상태별 필터링 (HTML 버튼의 onclick 속성에서 호출됨)
 */
function filterByStatus(status) {
    currentStatus = status;

    // 탭 스타일 변경 (첫 번째 JSP 디자인에 맞게)
    document.querySelectorAll('.status-tab').forEach(tab => {
        tab.classList.remove('active', 'btn-primary', 'btn-danger', 'btn-success');
        tab.classList.add('btn-outline-primary', 'btn-outline-danger', 'btn-outline-success');
    });
    const activeTab = document.querySelector(`[data-status="${status}"]`);
    if(activeTab) {
        activeTab.classList.add('active');
        activeTab.classList.remove('btn-outline-primary', 'btn-outline-danger', 'btn-outline-success');
        if (status === 'unpaid') activeTab.classList.add('btn-danger');
        else if (status === 'paid') activeTab.classList.add('btn-success');
        else activeTab.classList.add('btn-primary');
    }

    // 헤더 체크박스 상태 초기화
    document.getElementById('selectAll').checked = false;
    renderTable(status);
}

/**
 * 테이블 렌더링
 */
function renderTable(status) {
    const tbody = document.getElementById('paymentTableBody');

    let filteredData = paymentData;
    // 'all' 상태가 아니면 필터링 적용
    if (status !== 'all') {
         filteredData = paymentData.filter(item => {
            const isPaid = item.payDoneYn === 'Y';
            return status === 'paid' ? isPaid : !isPaid;
        });
    }

    if (filteredData.length === 0) {
        // 테이블 컬럼 개수 (8개)에 맞게 colspan 설정
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">조회된 데이터가 없습니다.</td></tr>';
        updateSelectedCount();
        return;
    }

    tbody.innerHTML = filteredData.map(item => `
        <tr data-status="${item.payDoneYn === 'Y' ? 'paid' : 'unpaid'}" data-student-id="${item.studentNo}">
            <td style="text-align:center;">
                ${item.payDoneYn === 'N' ?
                    `<input type="checkbox" class="student-select-checkbox"
                            value="${item.tuitionReqId}"
                            data-student-no="${item.studentNo}"
                            onchange="updateSelectedCount()">` :
                    '-'}
            </td>
            <td>${item.studentNo}</td>
            <td>${item.studentName}</td>
            <td>${item.deptName}</td>
            <td>${item.yearterm}</td>
            <td>${formatNumber(item.tuitionSum)}</td>
            <td>
                <span class="badge ${item.payDoneYn === 'Y' ? 'bg-success' : 'bg-danger'}">
                    ${item.payDoneYn === 'Y' ? '납부 완료' : '미납'}
                </span>
            </td>
            <td>${item.payDoneYn === 'Y' ? item.paidDate : '-'}</td>
        </tr>
    `).join('');

    updateSelectedCount();
    // 헤더 체크박스 상태를 테이블과 동기화
    document.getElementById('headerCheckbox').checked = document.getElementById('selectAll').checked;
}

/**
 * 전체 선택/해제 (HTML 버튼의 onchange/onclick 속성에서 호출됨)
 */
function toggleSelectAll() {
    const selectAll = document.getElementById('selectAll');
    const headerCheckbox = document.getElementById('headerCheckbox');

    const isChecked = selectAll.checked || headerCheckbox.checked;
    selectAll.checked = isChecked;
    headerCheckbox.checked = isChecked;

    // 현재 화면에 보이는 '미납' 체크박스만 선택/해제
    const checkboxes = document.querySelectorAll('.student-select-checkbox:not(:disabled)');

    checkboxes.forEach(checkbox => {
        checkbox.checked = isChecked;
    });

    updateSelectedCount();
}

/**
 * 선택 개수 업데이트 (HTML 체크박스의 onchange 속성에서 호출됨)
 */
function updateSelectedCount() {
    const checkboxes = document.querySelectorAll('.student-select-checkbox:checked');
    document.getElementById('selectedCount').textContent = checkboxes.length;

    // 전체 선택 체크박스 상태 업데이트
    const allCheckboxes = document.querySelectorAll('.student-select-checkbox:not(:disabled)');
    const allChecked = allCheckboxes.length > 0 &&
                      allCheckboxes.length === checkboxes.length;

    document.getElementById('selectAll').checked = allChecked;
    document.getElementById('headerCheckbox').checked = allChecked;
}

/**
 * 선택된 항목 납부 확인 (HTML 버튼의 onclick 속성에서 호출됨)
 */
function confirmSelectedPayments() {
    const checkboxes = document.querySelectorAll('.student-select-checkbox:checked');

    if (checkboxes.length === 0) {
        alert('납부 확인할 항목을 선택해주세요.');
        return;
    }

    const tuitionReqIds = Array.from(checkboxes).map(cb => cb.value);
    const studentNos = Array.from(checkboxes).map(cb => cb.dataset.studentNo);

    if (!confirm(`선택된 ${checkboxes.length}명의 납부를 확인하시겠습니까?\n\n학번: ${studentNos.join(', ')}`)) {
        return;
    }

    fetch('/lms/staff/tuition/confirm-payments', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            tuitionReqIds: tuitionReqIds
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(`${data.count}건의 납부 확인이 완료되었습니다.`);
            loadPaymentData(); // 데이터 새로고침
        } else {
            alert('처리 중 오류가 발생했습니다: ' + data.message);
        }
    })
    .catch(error => {
        console.error('납부 확인 실패:', error);
        alert('시스템 오류가 발생했습니다.');
    });
}

/**
 * 요약 정보 업데이트
 */
function updateSummary() {
    const total = paymentData.length;
    const paid = paymentData.filter(item => item.payDoneYn === 'Y').length;
    const unpaid = paymentData.filter(item => item.payDoneYn === 'N').length;

    document.getElementById('totalCount').textContent = total;
    document.getElementById('paidCount').textContent = paid;
    document.getElementById('unpaidCount').textContent = unpaid;

    // 상태 탭 카운트 업데이트
    document.getElementById('countAll').textContent = total;
    document.getElementById('countPaid').textContent = paid;
    document.getElementById('countUnpaid').textContent = unpaid;
}

/**
 * 숫자 포맷팅
 */
function formatNumber(num) {
     if (num === undefined || num === null) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// **[중요]** HTML의 onclick 이벤트에서 함수를 직접 호출하기 위해 window에 노출시킵니다.
window.searchPayments = searchPayments;
window.filterByStatus = filterByStatus;
window.toggleSelectAll = toggleSelectAll;
window.updateSelectedCount = updateSelectedCount;
window.confirmSelectedPayments = confirmSelectedPayments;
});