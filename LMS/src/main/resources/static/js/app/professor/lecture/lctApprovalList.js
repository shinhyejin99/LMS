/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 29.     	송태호            강의 승인 목록 조회 화면 스크립트
 *
 * </pre>
 */

(function() {
    'use strict';

    // API 엔드포인트
    const API_URL = '/lms/api/v1/professor/lecture/approval';

    // 페이지네이션 설정
    const ITEMS_PER_PAGE = 12;

    // 전역 상태
    let allLectureData = [];       // 전체 데이터
    let filteredData = [];         // 필터링된 데이터
    let currentPage = 1;           // 현재 페이지

    // 필터 값 저장
    let filterOptions = {
        completions: new Set(),    // 이수구분 (전체 고유값)
        credits: new Set(),        // 학점 (전체 고유값)
        hours: new Set(),          // 시수 (전체 고유값)
        statuses: new Set()        // 신청상태 (전체 고유값)
    };

    // 선택된 필터
    let selectedFilters = {
        subjectName: '',
        completions: new Set(),
        credits: new Set(),
        hours: new Set(),
        statuses: new Set(),
        sortOrder: 'desc'
    };

    // DOM 요소
    let tableBody;
    let loadingMessage;
    let errorMessage;
    let emptyMessage;
    let tableContainer;
    let paginationContainer;
    let searchSubjectNameInput;
    let sortDescBtn;
    let sortAscBtn;
    let resetSearchBtn;
    let checkAllCheckbox;
    let bulkApproveBtn;
    let bulkRejectBtn;

    /**
     * 페이지 초기화
     */
    function init() {
        // DOM 요소 캐싱
        tableBody = document.getElementById('lectureApplyTableBody');
        loadingMessage = document.getElementById('loadingMessage');
        errorMessage = document.getElementById('errorMessage');
        emptyMessage = document.getElementById('emptyMessage');
        tableContainer = document.querySelector('.table-container');
        paginationContainer = document.getElementById('paginationContainer');
        searchSubjectNameInput = document.getElementById('searchSubjectName');
        sortDescBtn = document.getElementById('sortDescBtn');
        sortAscBtn = document.getElementById('sortAscBtn');
        resetSearchBtn = document.getElementById('resetSearchBtn');
        checkAllCheckbox = document.getElementById('checkAll');
        bulkApproveBtn = document.getElementById('bulkApproveBtn');
        bulkRejectBtn = document.getElementById('bulkRejectBtn');

        // 이벤트 리스너 등록
        resetSearchBtn.addEventListener('click', handleResetSearch);
        sortDescBtn.addEventListener('click', () => handleSortToggle('desc'));
        sortAscBtn.addEventListener('click', () => handleSortToggle('asc'));
        checkAllCheckbox.addEventListener('change', handleCheckAll);
        bulkApproveBtn.addEventListener('click', () => handleBulkAction(true));
        bulkRejectBtn.addEventListener('click', () => handleBulkAction(false));

        // 즉시 적용: 과목명 입력 시
        searchSubjectNameInput.addEventListener('input', handleInstantApply);

        // 데이터 로드
        loadLectureApprovalList();
    }

    /**
     * 강의 승인 목록 데이터 로드
     */
    async function loadLectureApprovalList() {
        // 로딩 상태 표시
        showLoading();

        try {
            const response = await fetch(API_URL, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('서버 응답 오류: ' + response.status);
            }

            const data = await response.json();

            // 전체 데이터 저장
            allLectureData = data;

            // 필터 옵션 추출
            extractFilterOptions(data);

            // 필터 UI 생성
            renderFilterOptions();

            // 초기 정렬 및 렌더링
            applyFiltersAndSort();

        } catch (error) {
            console.error('강의 승인 목록 로드 실패:', error);
            showError();
        }

        logSelectedApprovalIds();
    }

    /**
     * 데이터에서 필터 옵션 추출
     * @param {Array} data - 전체 데이터
     */
    function extractFilterOptions(data) {
        data.forEach(lecture => {
            if (lecture.completionName) {
                filterOptions.completions.add(lecture.completionName);
            }
            if (lecture.credit !== null && lecture.credit !== undefined) {
                filterOptions.credits.add(lecture.credit);
            }
            if (lecture.hour !== null && lecture.hour !== undefined) {
                filterOptions.hours.add(lecture.hour);
            }
            if (lecture.applyStatus) {
                filterOptions.statuses.add(lecture.applyStatus);
            }
        });
    }

    /**
     * 필터 옵션 UI 렌더링
     */
    function renderFilterOptions() {
        // 이수구분 필터
        const completionContainer = document.getElementById('completionFilterContainer');
        completionContainer.innerHTML = '';
        Array.from(filterOptions.completions).sort().forEach(completion => {
            const item = createCheckboxItem('completion', completion);
            completionContainer.appendChild(item);
        });

        // 학점 필터
        const creditContainer = document.getElementById('creditFilterContainer');
        creditContainer.innerHTML = '';
        Array.from(filterOptions.credits).sort((a, b) => a - b).forEach(credit => {
            const item = createCheckboxItem('credit', credit);
            creditContainer.appendChild(item);
        });

        // 시수 필터
        const hourContainer = document.getElementById('hourFilterContainer');
        hourContainer.innerHTML = '';
        Array.from(filterOptions.hours).sort((a, b) => a - b).forEach(hour => {
            const item = createCheckboxItem('hour', hour);
            hourContainer.appendChild(item);
        });

        // 신청상태 필터
        const statusContainer = document.getElementById('statusFilterContainer');
        statusContainer.innerHTML = '';
        const statusOrder = ['대기중', '승인', '반려'];
        statusOrder.forEach(status => {
            if (filterOptions.statuses.has(status)) {
                const item = createCheckboxItem('status', status);
                statusContainer.appendChild(item);
            }
        });

        // 초기 로드 시 "대기중" 체크
        const waitingCheckbox = document.getElementById('status-대기중');
        if (waitingCheckbox && selectedFilters.statuses.size === 0) {
            waitingCheckbox.checked = true;
            selectedFilters.statuses.add('대기중');
        }

        // 모든 체크박스에 즉시 적용 이벤트 추가
        document.querySelectorAll('input[type="checkbox"][data-type]').forEach(checkbox => {
            checkbox.addEventListener('change', handleInstantApply);
        });
    }

    /**
     * 체크박스 아이템 생성
     * @param {String} type - 필터 타입
     * @param {String|Number} value - 필터 값
     * @returns {HTMLDivElement} 체크박스 아이템
     */
    function createCheckboxItem(type, value) {
        const div = document.createElement('div');
        div.classList.add('filter-checkbox-item');

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = `${type}-${value}`;
        checkbox.value = value;
        checkbox.dataset.type = type;

        const label = document.createElement('label');
        label.htmlFor = `${type}-${value}`;
        label.textContent = value;

        div.appendChild(checkbox);
        div.appendChild(label);

        return div;
    }

    /**
     * 즉시 적용 핸들러
     */
    function handleInstantApply() {
        // 과목명 검색어
        selectedFilters.subjectName = searchSubjectNameInput.value.trim();

        // 이수구분 필터
        selectedFilters.completions.clear();
        document.querySelectorAll('input[data-type="completion"]:checked').forEach(cb => {
            selectedFilters.completions.add(cb.value);
        });

        // 학점 필터
        selectedFilters.credits.clear();
        document.querySelectorAll('input[data-type="credit"]:checked').forEach(cb => {
            selectedFilters.credits.add(Number(cb.value));
        });

        // 시수 필터
        selectedFilters.hours.clear();
        document.querySelectorAll('input[data-type="hour"]:checked').forEach(cb => {
            selectedFilters.hours.add(Number(cb.value));
        });

        // 신청상태 필터
        selectedFilters.statuses.clear();
        document.querySelectorAll('input[data-type="status"]:checked').forEach(cb => {
            selectedFilters.statuses.add(cb.value);
        });

        // 페이지를 1로 초기화
        currentPage = 1;

        // 필터 적용 및 렌더링
        applyFiltersAndSort();
    }

    /**
     * 초기화 버튼 클릭 핸들러
     */
    function handleResetSearch() {
        // 입력 필드 초기화
        searchSubjectNameInput.value = '';

        // 모든 체크박스 해제
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            cb.checked = false;
        });

        // 필터 초기화
        selectedFilters.subjectName = '';
        selectedFilters.completions.clear();
        selectedFilters.credits.clear();
        selectedFilters.hours.clear();
        selectedFilters.statuses.clear();
        selectedFilters.sortOrder = 'desc';

        // 정렬 토글 버튼 초기화
        updateSortToggleButtons('desc');

        // 페이지를 1로 초기화
        currentPage = 1;

        // 필터 적용 및 렌더링
        applyFiltersAndSort();
    }

    /**
     * 정렬 토글 버튼 클릭 핸들러
     * @param {String} order - 'desc' 또는 'asc'
     */
    function handleSortToggle(order) {
        selectedFilters.sortOrder = order;
        updateSortToggleButtons(order);
        applyFiltersAndSort();
    }

    /**
     * 정렬 토글 버튼 UI 업데이트
     * @param {String} order - 'desc' 또는 'asc'
     */
    function updateSortToggleButtons(order) {
        if (order === 'desc') {
            sortDescBtn.classList.add('active');
            sortAscBtn.classList.remove('active');
        } else {
            sortDescBtn.classList.remove('active');
            sortAscBtn.classList.add('active');
        }

        logSelectedApprovalIds();
    }

    /**
     * 필터 적용 및 정렬
     */
    function applyFiltersAndSort() {
        // 필터링
        filteredData = allLectureData.filter(lecture => {
            // 과목명 필터
            if (selectedFilters.subjectName &&
                !lecture.subjectName.includes(selectedFilters.subjectName)) {
                return false;
            }

            // 이수구분 필터
            if (selectedFilters.completions.size > 0 &&
                !selectedFilters.completions.has(lecture.completionName)) {
                return false;
            }

            // 학점 필터
            if (selectedFilters.credits.size > 0 &&
                !selectedFilters.credits.has(lecture.credit)) {
                return false;
            }

            // 시수 필터
            if (selectedFilters.hours.size > 0 &&
                !selectedFilters.hours.has(lecture.hour)) {
                return false;
            }

            // 신청상태 필터
            if (selectedFilters.statuses.size > 0 &&
                !selectedFilters.statuses.has(lecture.applyStatus)) {
                return false;
            }

            return true;
        });

        // 정렬
        filteredData.sort((a, b) => {
            const dateA = new Date(a.applyAt);
            const dateB = new Date(b.applyAt);

            if (selectedFilters.sortOrder === 'desc') {
                return dateB - dateA;  // 최신순
            } else {
                return dateA - dateB;  // 오래된순
            }
        });

        // 렌더링
        renderCurrentPage();
    }

    /**
     * 현재 페이지 렌더링
     */
    function renderCurrentPage() {
        // 기존 내용 초기화
        tableBody.innerHTML = '';

        // 데이터가 없는 경우
        if (!filteredData || filteredData.length === 0) {
            showEmpty();
            paginationContainer.innerHTML = '';
            return;
        }

        // 페이지네이션 계산
        const totalPages = Math.ceil(filteredData.length / ITEMS_PER_PAGE);
        const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        const endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredData.length);
        const pageData = filteredData.slice(startIndex, endIndex);

        // 데이터 렌더링
        pageData.forEach(lecture => {
            const row = createTableRow(lecture);
            tableBody.appendChild(row);
        });

        // 페이지네이션 렌더링
        renderPagination(totalPages);

        // 전체 선택 체크박스 상태 업데이트
        updateCheckAllStatus();

        // 테이블 표시
        showTable();
    }

    /**
     * 페이지네이션 렌더링
     * @param {Number} totalPages - 전체 페이지 수
     */
    function renderPagination(totalPages) {
        paginationContainer.innerHTML = '';

        // 1페이지만 있어도 항상 표시
        if (totalPages < 1) {
            totalPages = 1;
        }

        // 이전 버튼
        const prevBtn = document.createElement('button');
        prevBtn.classList.add('pagination-btn');
        prevBtn.textContent = '이전';
        prevBtn.disabled = currentPage === 1;
        prevBtn.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderCurrentPage();
            }
        });
        paginationContainer.appendChild(prevBtn);

        // 페이지 번호 버튼
        const maxVisiblePages = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage < maxVisiblePages - 1) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.classList.add('pagination-btn');
            if (i === currentPage) {
                pageBtn.classList.add('active');
            }
            pageBtn.textContent = i;
            pageBtn.addEventListener('click', () => {
                currentPage = i;
                renderCurrentPage();
            });
            paginationContainer.appendChild(pageBtn);
        }

        // 다음 버튼
        const nextBtn = document.createElement('button');
        nextBtn.classList.add('pagination-btn');
        nextBtn.textContent = '다음';
        nextBtn.disabled = currentPage === totalPages;
        nextBtn.addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                renderCurrentPage();
            }
        });
        paginationContainer.appendChild(nextBtn);

        // 페이지 정보
        const pageInfo = document.createElement('span');
        pageInfo.classList.add('pagination-info');
        pageInfo.textContent = `${currentPage} / ${totalPages} 페이지`;
        paginationContainer.appendChild(pageInfo);
    }

    /**
     * 전체 체크박스 토글
     */
    function handleCheckAll() {
        const isChecked = checkAllCheckbox.checked;
        const checkboxes = document.querySelectorAll('.row-checkbox');

        if (isChecked) {
            // 전체 선택
            checkboxes.forEach(checkbox => {
                checkbox.checked = true;
            });
        } else {
            // 전체 해제
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
        }

        logSelectedApprovalIds();
    }

    /**
     * 개별 체크박스 상태에 따라 전체 선택 체크박스 업데이트
     */
    function updateCheckAllStatus() {
        const checkboxes = document.querySelectorAll('.row-checkbox');
        const checkedBoxes = document.querySelectorAll('.row-checkbox:checked');

        if (checkboxes.length === 0) {
            checkAllCheckbox.checked = false;
        } else if (checkedBoxes.length === checkboxes.length) {
            // ��� üũ��
            checkAllCheckbox.checked = true;
        } else {
            // �Ϻθ� üũ�ǰų� ��� üũ ������
            checkAllCheckbox.checked = false;
        }
    }


    /**
     * 현재 선택된 승인 ID 콘솔 로그
     */
    function logSelectedApprovalIds() {
        const checkedBoxes = document.querySelectorAll('.row-checkbox:checked');
        const approvalIds = Array.from(checkedBoxes).map(checkbox => checkbox.value);
        console.log('[일괄체크] 현재 선택된 approvalId:', approvalIds);
    }

    /**
     * 일괄 처리 핸들러
     * @param {Boolean} isApproved - true: 승인, false: 반려
     */
    async function handleBulkAction(isApproved) {
        const checkedBoxes = document.querySelectorAll('.row-checkbox:checked');

        if (checkedBoxes.length === 0) {
            Swal.fire({
                title: '알림',
                text: '처리할 항목을 선택해주세요.',
                icon: 'warning',
                confirmButtonText: '확인'
            });
            return;
        }

        const approvalIdList = Array.from(checkedBoxes).map(checkbox => checkbox.value);
        const actionText = isApproved ? '승인' : '반려';

        const result = await Swal.fire({
            title: '확인',
            text: `선택한 ${approvalIdList.length}건을 ${actionText} 처리하시겠습니까?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '예',
            cancelButtonText: '아니오'
        });

        if (!result.isConfirmed) {
            return;
        }

        try {
            const response = await fetch('/lms/api/v1/professor/lecture/approval', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    approvalType: isApproved ? 'APPROVE' : 'REJECT',
                    approveIds: approvalIdList,
                    comments: isApproved ? '일괄 승인' : '일괄 반려'
                })
            });

            if (response.ok) {
                Swal.fire({
                    title: '완료',
                    text: `${actionText} 처리가 완료되었습니다.`,
                    icon: 'success',
                    confirmButtonText: '확인'
                });
                // 체크박스 초기화
                checkAllCheckbox.checked = false;
                // 데이터 새로고침
                loadLectureApprovalList();
            } else {
                throw new Error('처리 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('일괄 처리 오류:', error);
            Swal.fire({
                title: '오류',
                text: `${actionText} 처리 중 오류가 발생했습니다.`,
                icon: 'error',
                confirmButtonText: '확인'
            });
        }

        logSelectedApprovalIds();
    }

    /**
     * 테이블 행 생성
     * @param {Object} lecture - 강의 승인 정보
     * @returns {HTMLTableRowElement} 테이블 행 요소
     */
    function createTableRow(lecture) {
        const row = document.createElement('tr');

        // 체크박스
        const checkboxCell = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'row-checkbox';
        checkbox.value = lecture.approvalId;
        checkbox.addEventListener('change', updateCheckAllStatus);
        checkboxCell.appendChild(checkbox);
        row.appendChild(checkboxCell);

        // 과목명
        const subjectNameCell = document.createElement('td');
        subjectNameCell.textContent = lecture.subjectName || '-';
        row.appendChild(subjectNameCell);

        // 학과
        const deptNameCell = document.createElement('td');
        deptNameCell.textContent = lecture.univDeptName || '-';
        row.appendChild(deptNameCell);

        // 이수구분
        const completionCell = document.createElement('td');
        completionCell.textContent = lecture.completionName || '-';
        row.appendChild(completionCell);

        // 학점/시수
        const creditHourCell = document.createElement('td');
        const credit = lecture.credit || '-';
        const hour = lecture.hour || '-';
        creditHourCell.textContent = `${credit}/${hour}`;
        row.appendChild(creditHourCell);

        // 신청상태
        const statusCell = document.createElement('td');
        const statusBadge = createStatusBadge(lecture.applyStatus);
        statusCell.appendChild(statusBadge);
        row.appendChild(statusCell);

        // 신청자
        const applicantCell = document.createElement('td');
        if (lecture.applicantName && lecture.applicantUnivDeptName) {
            applicantCell.textContent = `${lecture.applicantName} (${lecture.applicantUnivDeptName})`;
            applicantCell.title = `${lecture.applicantName} (${lecture.applicantUnivDeptName})`;
        } else {
            applicantCell.textContent = '-';
        }
        row.appendChild(applicantCell);

        // 신청일시
        const applyAtCell = document.createElement('td');
        applyAtCell.textContent = formatDateTime(lecture.applyAt);
        row.appendChild(applyAtCell);

        // 열람 버튼
        const actionCell = document.createElement('td');
        const viewBtn = document.createElement('button');
        viewBtn.classList.add('btn-view');
        viewBtn.textContent = '열람';
        viewBtn.addEventListener('click', () => {
            goToDetail(lecture.lctApplyId);
        });
        actionCell.appendChild(viewBtn);
        row.appendChild(actionCell);

        return row;
    }

    /**
     * 상세 페이지로 이동
     * @param {String} lctApplyId - 강의 신청 ID
     */
    function goToDetail(lctApplyId) {
        window.location.href = `/lms/professor/lecture/apply/${lctApplyId}`;
    }

    /**
     * 신청상태 배지 생성
     * @param {String} status - 신청상태 (대기중, 승인, 반려)
     * @returns {HTMLSpanElement} 배지 요소
     */
    function createStatusBadge(status) {
        const badge = document.createElement('span');
        badge.classList.add('status-badge');

        if (status === '대기중') {
            badge.classList.add('waiting');
            badge.textContent = '대기중';
        } else if (status === '승인') {
            badge.classList.add('approved');
            badge.textContent = '승인';
        } else if (status === '반려') {
            badge.classList.add('rejected');
            badge.textContent = '반려';
        } else {
            badge.textContent = status || '-';
        }

        return badge;
    }

    /**
     * 날짜/시간 포맷팅
     * @param {String} dateTimeStr - ISO 8601 형식의 날짜/시간 문자열
     * @returns {String} 포맷팅된 날짜/시간 문자열
     */
    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return '-';

        try {
            const date = new Date(dateTimeStr);

            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            return `${year}-${month}-${day} ${hours}:${minutes}`;
        } catch (error) {
            console.error('날짜 포맷 오류:', error);
            return dateTimeStr;
        }

        logSelectedApprovalIds();
    }

    /**
     * 날짜/시간 포맷팅 (줄바꿈 포함)
     * @param {String} dateTimeStr - ISO 8601 형식의 날짜/시간 문자열
     * @returns {String} 포맷팅된 날짜/시간 문자열 (HTML)
     */
    function formatDateTimeWithBreak(dateTimeStr) {
        if (!dateTimeStr) return '-';

        try {
            const date = new Date(dateTimeStr);

            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            return `${year}-${month}-${day}<br>${hours}:${minutes}`;
        } catch (error) {
            console.error('날짜 포맷 오류:', error);
            return dateTimeStr;
        }

        logSelectedApprovalIds();
    }

    /**
     * 로딩 상태 표시
     */
    function showLoading() {
        tableContainer.style.display = 'none';
        errorMessage.style.display = 'none';
        emptyMessage.style.display = 'none';
        loadingMessage.style.display = 'block';
    }

    /**
     * 에러 메시지 표시
     */
    function showError() {
        tableContainer.style.display = 'none';
        loadingMessage.style.display = 'none';
        emptyMessage.style.display = 'none';
        errorMessage.style.display = 'block';
    }

    /**
     * 빈 데이터 메시지 표시
     */
    function showEmpty() {
        tableContainer.style.display = 'none';
        loadingMessage.style.display = 'none';
        errorMessage.style.display = 'none';
        emptyMessage.style.display = 'block';
    }

    /**
     * 테이블 표시
     */
    function showTable() {
        loadingMessage.style.display = 'none';
        errorMessage.style.display = 'none';
        emptyMessage.style.display = 'none';
        tableContainer.style.display = 'block';
    }

    // DOM 로드 완료 시 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();




