/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 29.     	송태호            강의 신청 상세 조회 화면 스크립트
 *
 * </pre>
 */

(function() {
    'use strict';

    // API 엔드포인트
    const API_BASE_URL = '/lms/api/v1/professor/lecture/apply';

    // DOM 요소
    let loadingMessage;
    let errorMessage;
    let detailContent;
    let backBtn;
    let weekSelect;
    let weekCount;

    // 주차별 데이터 저장
    let weeklyPlanData = [];

    // 성적 평가 기준 매핑
    const GRADE_CRITERIA_MAP = {
        'ATTD': '출석',
        'EXAM': '시험',
        'PRAC': '실습',
        'TASK': '과제',
        'PRES': '발표',
        'PART': '수업참여'
    };

    /**
     * 페이지 초기화
     */
    function init() {
        // DOM 요소 캐싱
        loadingMessage = document.getElementById('loadingMessage');
        errorMessage = document.getElementById('errorMessage');
        detailContent = document.getElementById('detailContent');
        backBtn = document.getElementById('backBtn');
        weekSelect = document.getElementById('weekSelect');
        weekCount = document.getElementById('weekCount');

        // 이벤트 리스너 등록
        backBtn.addEventListener('click', goBack);
        weekSelect.addEventListener('change', handleWeekChange);

        // URL에서 lctApplyId 추출
        const lctApplyId = getLctApplyIdFromUrl();

        if (!lctApplyId) {
            showError();
            return;
        }

        // 데이터 로드
        loadDetailData(lctApplyId);
    }

    /**
     * URL에서 lctApplyId 추출
     */
    function getLctApplyIdFromUrl() {
        const pathParts = window.location.pathname.split('/');
        return pathParts[pathParts.length - 1];
    }

    /**
     * 상세 데이터 로드
     */
    async function loadDetailData(lctApplyId) {
        showLoading();

        try {
            const response = await fetch(`${API_BASE_URL}/${lctApplyId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error('서버 응답 오류: ' + response.status);
            }

            const data = await response.json();

            // 데이터 렌더링
            renderDetailData(data);
            showContent();

        } catch (error) {
            console.error('상세 데이터 로드 실패:', error);
            showError();
        }
    }

    /**
     * 상세 데이터 렌더링
     */
    function renderDetailData(data) {
        const { lctOpenApplyResp, subjectResp, approvalResp, lctApplyWeekbyRespList, applyGraderatioRespList } = data;

        // 과목 정보
        if (subjectResp) {
            document.getElementById('subjectName').textContent = subjectResp.subjectName || '-';
            document.getElementById('univDeptName').textContent = subjectResp.univDeptName || '-';
            document.getElementById('completionName').textContent = subjectResp.completionName || '-';
            document.getElementById('subjectTypeName').textContent = subjectResp.subjectTypeName || '-';

            // 학점/시수 합쳐서 표시
            const credit = subjectResp.credit || '-';
            const hour = subjectResp.hour || '-';
            document.getElementById('creditHour').textContent = `${credit} / ${hour}`;
        }

        // 신청 정보
        if (lctOpenApplyResp) {
            document.getElementById('yeartermName').textContent = lctOpenApplyResp.yeartermName || '-';
            document.getElementById('expectCap').textContent = lctOpenApplyResp.expectCap || '-';
            document.getElementById('applyAt').textContent = formatDateTime(lctOpenApplyResp.applyAt);
            document.getElementById('prereqSubject').textContent = lctOpenApplyResp.prereqSubject || '-';
            document.getElementById('lectureIndex').textContent = lctOpenApplyResp.lectureIndex || '-';
            document.getElementById('lectureGoal').textContent = lctOpenApplyResp.lectureGoal || '-';
            document.getElementById('desireOption').textContent = lctOpenApplyResp.desireOption || '-';
        }

        // 신청자 정보 (approvalResp에서 가져옴)
        if (approvalResp && approvalResp.applicantUserName && approvalResp.applicantUserRole) {
            document.getElementById('applicant').textContent =
                `${approvalResp.applicantUserName}(${approvalResp.applicantUserRole})`;
        } else {
            document.getElementById('applicant').textContent = '-';
        }

        // 승인 정보
        if (approvalResp) {
            const statusBadge = document.getElementById('approveStatus');
            const status = approvalResp.approveStatus || '대기중';
            statusBadge.textContent = status;

            // 상태에 따른 배지 스타일
            statusBadge.className = 'status-badge';
            if (status === '대기중') {
                statusBadge.classList.add('waiting');
            } else if (status === '승인') {
                statusBadge.classList.add('approved');
            } else if (status === '반려') {
                statusBadge.classList.add('rejected');
            }

            // 승인자 정보
            if (approvalResp.approverUserName && approvalResp.approverUserRole) {
                document.getElementById('approver').textContent =
                    `${approvalResp.approverUserName}(${approvalResp.approverUserRole})`;
            } else {
                document.getElementById('approver').textContent = '-';
            }

            document.getElementById('approveAt').textContent =
                approvalResp.approveAt ? formatDateTime(approvalResp.approveAt) : '-';
            document.getElementById('comments').textContent = approvalResp.comments || '-';
        }

        // 주차별 계획
        if (lctApplyWeekbyRespList && lctApplyWeekbyRespList.length > 0) {
            renderWeeklyPlan(lctApplyWeekbyRespList);
        }

        // 성적 산출 비율
        if (applyGraderatioRespList && applyGraderatioRespList.length > 0) {
            renderGradeRatio(applyGraderatioRespList);
        }
    }

    /**
     * 주차별 계획 렌더링
     */
    function renderWeeklyPlan(weekList) {
        // 주차별 데이터 저장
        weeklyPlanData = weekList;

        // 총 주차 수 표시
        weekCount.textContent = `총 ${weekList.length}주`;

        // 주차 선택 옵션 생성
        weekSelect.innerHTML = '<option value="">주차 선택</option>';
        weekList.forEach((week, index) => {
            const option = document.createElement('option');
            option.value = index;
            option.textContent = `${week.lectureWeek}주차`;
            weekSelect.appendChild(option);
        });

        // 첫 번째 주차 자동 선택
        if (weekList.length > 0) {
            weekSelect.value = 0;
            displaySelectedWeek(0);
        }
    }

    /**
     * 주차 선택 변경 핸들러
     */
    function handleWeekChange() {
        const selectedIndex = weekSelect.value;
        if (selectedIndex !== '') {
            displaySelectedWeek(parseInt(selectedIndex));
        } else {
            // 선택 해제 시 컨테이너 비우기
            document.getElementById('weeklyPlanContainer').innerHTML = '';
        }
    }

    /**
     * 선택된 주차 정보 표시
     */
    function displaySelectedWeek(index) {
        const week = weeklyPlanData[index];
        if (!week) return;

        const container = document.getElementById('weeklyPlanContainer');
        container.innerHTML = '';

        const weekContent = document.createElement('div');
        weekContent.classList.add('week-content');

        // 학습목표
        const goalItem = document.createElement('div');
        goalItem.classList.add('week-content-item');

        const goalLabel = document.createElement('div');
        goalLabel.classList.add('week-content-label');
        goalLabel.textContent = '학습목표';

        const goalText = document.createElement('div');
        goalText.classList.add('week-content-text');
        goalText.textContent = week.weekGoal || '-';

        goalItem.appendChild(goalLabel);
        goalItem.appendChild(goalText);

        // 학습내용
        const descItem = document.createElement('div');
        descItem.classList.add('week-content-item');

        const descLabel = document.createElement('div');
        descLabel.classList.add('week-content-label');
        descLabel.textContent = '학습내용';

        const descText = document.createElement('div');
        descText.classList.add('week-content-text');
        descText.textContent = week.weekDesc || '-';

        descItem.appendChild(descLabel);
        descItem.appendChild(descText);

        weekContent.appendChild(goalItem);
        weekContent.appendChild(descItem);

        container.appendChild(weekContent);
    }

    /**
     * 성적 산출 비율 렌더링
     */
    function renderGradeRatio(ratioList) {
        const container = document.getElementById('gradeRatioContainer');
        container.innerHTML = '';

        ratioList.forEach(ratio => {
            const ratioItem = document.createElement('div');
            ratioItem.classList.add('grade-ratio-item');

            const label = document.createElement('div');
            label.classList.add('grade-ratio-label');
            label.textContent = GRADE_CRITERIA_MAP[ratio.gradeCriteriaCd] || ratio.gradeCriteriaCd;

            const value = document.createElement('div');
            value.classList.add('grade-ratio-value');
            value.textContent = `${ratio.ratio}%`;

            ratioItem.appendChild(label);
            ratioItem.appendChild(value);

            container.appendChild(ratioItem);
        });
    }

    /**
     * 날짜/시간 포맷팅
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
    }

    /**
     * 목록으로 돌아가기
     */
    function goBack() {
        window.history.back();
    }

    /**
     * 로딩 상태 표시
     */
    function showLoading() {
        detailContent.style.display = 'none';
        errorMessage.style.display = 'none';
        loadingMessage.style.display = 'block';
    }

    /**
     * 에러 메시지 표시
     */
    function showError() {
        detailContent.style.display = 'none';
        loadingMessage.style.display = 'none';
        errorMessage.style.display = 'block';
    }

    /**
     * 컨텐츠 표시
     */
    function showContent() {
        loadingMessage.style.display = 'none';
        errorMessage.style.display = 'none';
        detailContent.style.display = 'block';
    }

    // DOM 로드 완료 시 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
