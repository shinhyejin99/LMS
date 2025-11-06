/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 13.     	김수현            최초 생성
 * 2025. 10. 15.		김수현			접수유형으로 변경
 * </pre>
 */

// 전역 변수: 현재 모달의 applyId 저장
let currentApplyId = null;
let currentApplyStatus = null;
let currentApplyType = null; //openDetailModal 시 저장된 값

/**
 * 전체 학적변동 신청 현황
 */

document.addEventListener('DOMContentLoaded', function() {

    // 초기 로드
    loadStatusList();

    // 필터 변경 시
    document.getElementById('filterType').addEventListener('change', loadStatusList);
    document.getElementById('filterStatus').addEventListener('change', loadStatusList);

    // ================================
    // 신청 목록 조회
    // ================================

    function loadStatusList() {
        const filterType = document.getElementById('filterType').value;
        const filterStatus = document.getElementById('filterStatus').value;

        // URI를 통합된 목록을 반환하는 '/list'로 고정
        const url = '/lms/student/rest/record/list';

        fetch(url, { method: 'GET' })
            .then(response => response.json())
            .then(data => {

				// 전체 데이터를 이용해 요약 정보를 먼저 렌더링
                renderStatusSummary(data);

                let filteredData = data;

                // 1차 필터링: 신청 구분(filterType) - UnifiedChangeCd 사용
                if (filterType) {
                    // 휴학 필터링 로직
	                if (filterType === 'REST') {
	                    // 선택된 타입이 'REST'인 경우, 'REST_'로 시작하는 모든 코드를 허용
	                    filteredData = data.filter(item => item.unifiedChangeCd && item.unifiedChangeCd.startsWith('REST'));
	                } else {
	                    // 그 외의 경우 (자퇴, 전과 등)는 정확히 일치하는 코드만 필터링
	                    filteredData = data.filter(item => item.unifiedChangeCd === filterType);
	                }
                }

                // 2차 필터링: 상태 필터링
                if (filterStatus) {
                    filteredData = filteredData.filter(item => item.applyStatusCd === filterStatus);
                }

                renderStatusTable(filteredData);
            })
            .catch(error => {
                console.error('목록 조회 실패:', error);
                document.getElementById('emptyMessage').style.display = 'block';
            });
    }


    function renderStatusSummary(dataList) {
    // JSP에서 전달받은 전역 변수 applyStatusCodes를 사용
    const summaryContainer = document.getElementById('statusSummary');
    summaryContainer.innerHTML = '';

    // 1. 상태별 카운팅 (데이터를 순회하며 상태 코드별 개수 계산)
    const statusCounts = dataList.reduce((acc, item) => {
        const status = item.applyStatusCd;
        acc[status] = (acc[status] || 0) + 1;
        return acc;
    }, {});

    // 2. 전체 카운트 계산
    const totalCount = dataList.length;

    // 3. '전체' 카드 생성 및 추가
    const totalCard = createStatusCard({ commonCd: 'TOTAL', cdName: '전체' }, totalCount);
    summaryContainer.appendChild(totalCard);

    // 4. 모든 상태 코드 목록 (applyStatusCodes)을 순회하며 카드 생성
    if (typeof applyStatusCodes !== 'undefined' && applyStatusCodes.length > 0) {
        applyStatusCodes.forEach(status => {
            // 해당 상태의 카운트를 가져옴 (없으면 0)
            const count = statusCounts[status.commonCd] || 0;

            const card = createStatusCard(status, count);
            summaryContainer.appendChild(card);
        });
    } else {
         console.warn("처리 상태 코드 목록(applyStatusCodes)이 JavaScript에 전달되지 않았습니다.");
    }
}

/**
 * 개별 상태 요약 카드를 생성하고 클릭 이벤트를 설정
 */
function createStatusCard(status, count) {
    const card = document.createElement('div');
    card.className = 'status-card';
    card.dataset.statusCd = status.commonCd;

    // 상태별 클래스 -> 상태에 따라서 다른 색깔로 나오도록
    const statusClass = getStatusColorClass(status.commonCd);
    if (statusClass) {
        card.classList.add(statusClass);
    }

    // 카드 클릭 이벤트: 클릭 시 해당 상태로 드롭다운 필터링 후 목록 새로고침
    card.addEventListener('click', function() {
        const filterStatusSelect = document.getElementById('filterStatus');

        // 필터 드롭다운 값 설정
        if(status.commonCd === 'TOTAL') {
            filterStatusSelect.value = ''; // '전체'를 의미하는 빈 값 설정
        } else {
            filterStatusSelect.value = status.commonCd;
        }

        // 목록 로드
        loadStatusList();
    });

    // 카드 내부 HTML 구조 설정
    card.innerHTML = `
        <div class="status-name">${status.cdName}</div>
        <div class="status-count">${count}</div>
    `;

    return card;
}

// 상태별 색상 클래스 매핑
function getStatusColorClass(statusCode) {
    const colorMap = {
        'PENDING': 'status-pending',      // 대기
        'IN_PROGRESS': 'status-processing', // 처리중
        'APPROVED': 'status-approved',    // 승인완료
        'REJECTED': 'status-rejected',    // 반려
        'TOTAL': 'status-total'           // 전체
    };
    return colorMap[statusCode] || '';
}



    // ================================
    // 테이블 렌더링
    // ================================

    function renderStatusTable(dataList) {

		console.log(dataList);
        const tbody = document.getElementById('statusTableBody');
        const emptyMessage = document.getElementById('emptyMessage');

        tbody.innerHTML = '';

        if (!dataList || dataList.length === 0) {
            emptyMessage.style.display = 'block';
            return;
        }

        emptyMessage.style.display = 'none';

        dataList.forEach(function(item) {
            const tr = document.createElement('tr');
            tr.className = 'ssc-tr clickable-row'; // 클릭 가능한 스타일 클래스 추가

			// 행(tr)에 클릭 이벤트 관련 속성
            tr.dataset.applyId = item.applyId;
            tr.dataset.applyType = item.applyType;

            // 행 클릭 시 상세보기 모달 호출
            tr.addEventListener('click', function() {
				console.log('Row Clicked. applyId:', this.dataset.applyId, 'applyType:', this.dataset.applyType);
                openDetailModal(this.dataset.applyId, this.dataset.applyType);
            });

			// 신청구분
            const tdType = document.createElement('td');
			tdType.className = 'ssc-td';
			tdType.textContent = item.applyTypeName;
			tr.appendChild(tdType);

            // 신청 구분
            const tdCategory = document.createElement('td');
            tdCategory.className = 'ssc-td';
            const categoryBadge = document.createElement('span');

            // item.unifiedChangeCd를 사용하여 CSS 클래스를 결정
            categoryBadge.className = `ssc-category-badge ssc-${getCategoryClass(item.unifiedChangeCd)}`;

            // 휴학 관련은 모두 "휴학"으로 표시
	        if (item.unifiedChangeCd && item.unifiedChangeCd.startsWith('REST')) {
	            categoryBadge.textContent = '휴학';
	        } else {
	            // item.unifiedChangeName 사용
	            categoryBadge.textContent = item.unifiedChangeName;
	        }

            tdCategory.appendChild(categoryBadge);
            tr.appendChild(tdCategory);

            // 신청일자
            const tdDate = document.createElement('td');
            tdDate.className = 'ssc-td';
            tdDate.textContent = formatDate(item.applyAt);
            tr.appendChild(tdDate);

            // 승인일자
            const tdCfDate = document.createElement('td');
			tdCfDate.className = 'ssc-td';
			tdCfDate.textContent = item.approveAt ? formatDate(item.approveAt) : '-';
			tr.appendChild(tdCfDate);

            // 처리상태
            const tdStatus = document.createElement('td');
            tdStatus.className = 'ssc-td';
            const statusBadge = document.createElement('span');
            statusBadge.className = `ssc-status-badge ssc-${getStatusClass(item.applyStatusCd)}`;
            statusBadge.textContent = item.applyStatusName;
            tdStatus.appendChild(statusBadge);
            tr.appendChild(tdStatus);

            // 승인자
            const tdApprover = document.createElement('td');
            tdApprover.className = 'ssc-td';
            tdApprover.textContent = item.currentApproverName || '대기중';
            tr.appendChild(tdApprover);


            tbody.appendChild(tr);
        });
    }

    // ================================
    // 상세 조회 및 모달
    // ================================

    function openDetailModal(applyId, applyType) {
	    console.log('===== 모달 열기 =====');
	    console.log('applyId:', applyId);
	    console.log('applyType:', applyType);

	    // 타입별 URL
	    const url = applyType === 'RECORD'
	        ? `/lms/student/rest/record/${applyId}`            // 재학상태변경
	        : `/lms/student/rest/record/affil/${applyId}`;     // 소속변경

	    fetch(url, { method: 'GET' })
	        .then(response => response.json())
	        .then(data => {
	            console.log('상세 데이터:', data);
	            displayDetailModal(data, applyType);
	        })
	        .catch(error => {
	            console.error('상세 조회 실패:', error);
	            alert('상세 정보를 불러오는데 실패했습니다.');
	        });
	}

    function displayDetailModal(data, applyType) {
	    currentApplyId = data.applyId;
	    currentApplyStatus = data.applyStatusCd;
	    currentApplyType = applyType;

	    // 타입별 모달 내용
	    if (applyType === 'RECORD') {
	        displayRecordModal(data);
	    } else {
	        displayAffilModal(data);
	    }

	    document.getElementById('sscDetailModal').style.display = 'block';
	}

	// 재학상태변경 모달
	function displayRecordModal(data) {
	    document.getElementById('sscModalReceiptNo').textContent = data.applyId;

	    let categoryText = data.recordChangeName;
	    if (data.recordChangeCd && data.recordChangeCd.startsWith('REST')) {
	        categoryText = '휴학';
	    }
	    document.getElementById('sscModalCategory').textContent = categoryText;

	    document.getElementById('sscModalDate').textContent = formatDate(data.applyAt);
	    document.getElementById('sscModalStatus').textContent = data.applyStatusName;
	    document.getElementById('sscModalApprover').textContent = data.currentApproverName || '대기중';
	    document.getElementById('sscModalReason').textContent = data.applyReason || '사유 없음';

	    // 반려 사유 표시 로직
	    const rejectionSection = document.getElementById('rejectionReasonSection');
	    if (data.applyStatusCd === 'REJECTED' && data.rejectionReason) {
	        rejectionSection.style.display = 'block';
	        document.getElementById('sscModalRejectionReason').textContent = data.rejectionReason;
	    } else {
	        rejectionSection.style.display = 'none';
	    }

	    // 기본 정보 섹션 참조
	    const basicInfoSection = document.querySelector('.ssc-detail-section:first-child');

	    // 휴학 상세 정보
	    const leaveSection = document.getElementById('leaveDetailSection');
	    if (data.recordChangeCd && data.recordChangeCd.startsWith('REST')) {
	        leaveSection.style.display = 'block';
	        document.getElementById('sscModalLeaveType').textContent = data.recordChangeName || '-';

	        let durationText = '-';
	        if (data.recordChangeCd === 'REST_MIL') {
	            durationText = data.disireTerm ? `${data.disireTerm}개월` : '-';
	        } else {
	            durationText = data.disireTerm ? `${data.disireTerm}학기` : '-';
	        }
	        document.getElementById('sscModalLeaveDuration').textContent = durationText;

	        // 휴학일 때는 기본 정보를 왼쪽에만
	        if (basicInfoSection) basicInfoSection.style.gridColumn = '';
	    } else {
	        leaveSection.style.display = 'none';
	        // 휴학이 아닐 때는 기본 정보를 2열 전체로 확장
	        if (basicInfoSection) basicInfoSection.style.gridColumn = '1 / -1';
	    }

	    // 첨부파일
	    renderAttachFiles(data.attachFiles, currentApplyId);

	    // 취소 버튼
	    showCancelButton(data.applyStatusCd);
	}

	// 소속변경 모달
	function displayAffilModal(data) {
	    document.getElementById('sscModalReceiptNo').textContent = data.applyId;
	    document.getElementById('sscModalCategory').textContent = data.affilChangeName;
	    document.getElementById('sscModalDate').textContent = formatDate(data.applyAt);
	    document.getElementById('sscModalStatus').textContent = data.applyStatusName;
	    document.getElementById('sscModalApprover').textContent = data.currentApproverName || '대기중';
	    document.getElementById('sscModalReason').textContent = data.applyReason || '사유 없음';

	    // 반려 사유 표시 로직
	    const rejectionSection = document.getElementById('rejectionReasonSection');
	    if (data.applyStatusCd === 'REJECTED' && data.rejectionReason) {
	        rejectionSection.style.display = 'block';
	        document.getElementById('sscModalRejectionReason').textContent = data.rejectionReason;
	    } else {
	        rejectionSection.style.display = 'none';
	    }

	    // 기본 정보 섹션을 2열 전체로 확장
	    const basicInfoSection = document.querySelector('.ssc-detail-section:first-child');
	    if (basicInfoSection) basicInfoSection.style.gridColumn = '1 / -1';

	    // 휴학 상세는 숨김
	    const leaveSection = document.getElementById('leaveDetailSection');
	    leaveSection.style.display = 'none';

	    // 소속변경 상세 정보 표시
	    const affilSection = document.getElementById('affilDetailSection');
	    if (affilSection) {
	        affilSection.style.display = 'block';
	        document.getElementById('sscModalTargetDept').textContent = data.targetDeptName || '-';
	        document.getElementById('sscModalTargetCollege').textContent = data.targetCollegeName || '-';
	    }

	    // 첨부파일
	    renderAttachFiles(data.attachFiles, currentApplyId);

	    // 취소 버튼
	    showCancelButton(data.applyStatusCd);
	}

	// 첨부파일 렌더링 (공통)
	function renderAttachFiles(files, applyId) {
	    const fileBox = document.getElementById('sscModalFile');
	    if (files && files.length > 0) {
	        fileBox.innerHTML = files.map(file => {
	            const fullName = file.extension
	                ? `${file.originName}.${file.extension}`
	                : file.originName;
		             return `<a href="/lms/student/rest/record/file/${applyId}/attach/${file.fileOrder}" class="ssc-file-link">${fullName}</a>`;
	        }).join('<br>');
	    } else {
	        fileBox.textContent = '첨부파일 없음';
	    }
	}

	// 취소 버튼 표시 (공통)
	function showCancelButton(statusCd) {
	    const cancelBtn = document.getElementById('sscModalCancelBtn');
	    if (statusCd === 'PENDING') {
	        cancelBtn.style.display = 'inline-block';
	    } else {
	        cancelBtn.style.display = 'none';
	    }
	}



    // ================================
    // 유틸리티 함수
    // ================================

    function getCategoryClass(code) {
        const map = {
            'DROP': 'withdrawal',
            'REST': 'leave',
            'REST_GEN': 'leave',
	        'REST_MIL': 'leave',
	        'REST_MED': 'leave',
	        'REST_PARENT': 'leave',
            'RTRN': 'return',
            'DEFR': 'defer'
        };
        return map[code] || '';
    }

    function getStatusClass(code) {
        const map = {
            'PENDING': 'pending',
            'IN_PROGRESS': 'processing',
            'APPROVED': 'approved',
            'REJECTED': 'rejected'
        };
        return map[code] || '';
    }

    function getLeaveTypeName(type) {
        const map = {
            'GENERAL': '일반 휴학',
            'MILITARY': '군입대 휴학',
            'MEDICAL': '질병 휴학',
            'PARENT': '출산/육아 휴학'
        };
        return map[type] || '-';
    }

    function formatDate(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // 취소 함수를 전역으로
    window.cancelApply = async function() {
        if (!currentApplyId) {
            alert('신청 정보를 찾을 수 없습니다.');
            return;
        }

        const result = await Swal.fire({
            title: '신청 취소',
            text: '정말 신청을 취소하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '취소하기',
            cancelButtonText: '닫기',
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d'
        });

        if (!result.isConfirmed) return;

        try {

			// 핵심 수정: applyType에 따라 URI 분기
        	const deleteUrl = currentApplyType === 'RECORD'
            ? `/lms/student/rest/record/${currentApplyId}`        // 재학상태변경 취소
            : `/lms/student/rest/affiliation/${currentApplyId}`; // 소속변경 취소 (새로 만들 URI)


            const response = await fetch(deleteUrl, {
                method: 'DELETE'
            });

            const data = await response.json();

            if (data.success) {
                Swal.fire({
                    icon: 'success',
                    title: '완료',
                    text: data.message,
                    confirmButtonText: '확인',
                    confirmButtonColor: '#007bff'
                });
                closeSSCDetailModal();
                loadStatusList();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '실패',
                    text: data.message,
                    confirmButtonText: '확인',
                    confirmButtonColor: '#007bff'
                });
            }
        } catch (error) {
            console.error('취소 실패:', error);
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '취소 처리 중 오류가 발생했습니다.',
                confirmButtonText: '확인',
                confirmButtonColor: '#007bff'
            });
        }
    };

    // 모달 닫기 (전역 함수)
    window.closeSSCDetailModal = function() {
		currentApplyId = null;
    	currentApplyStatus = null;
        document.getElementById('sscDetailModal').style.display = 'none';
    }

    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        const modal = document.getElementById('sscDetailModal');
        if (event.target == modal) {
            closeSSCDetailModal();
        }
    }
});