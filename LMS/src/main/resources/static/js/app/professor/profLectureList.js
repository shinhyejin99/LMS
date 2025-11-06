/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 28.     	김수현            최초 생성
 *
 * </pre>
 */

/**
 * 교수 강의 목록 관리 스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
    loadLectureList();
});

/**
 * 강의 목록 로딩
 */
function loadLectureList() {
    // 로딩 표시
    showLoading();

    fetch('/lms/professor/rest/lecture/list', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('서버 응답 오류: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        hideLoading();
        displayLectureList(data);
    })
    .catch(error => {
        hideLoading();
        showError(error.message || '강의 목록을 불러오는데 실패했습니다.');
        console.error('Error:', error);
    });
}

/**
 * 강의 목록을 테이블에 표시
 */
function displayLectureList(lectureList) {
    const tableBody = document.getElementById('lectureTableBody');
    const table = document.getElementById('lectureTable');
    const noData = document.getElementById('noData');

    // 기존 데이터 초기화
    tableBody.innerHTML = '';

    if (!lectureList || lectureList.length === 0) {
        table.style.display = 'none';
        noData.style.display = 'block';
        return;
    }

    // 학년도-학기 기준 최신순 정렬
    lectureList.sort((a, b) => {
        return compareYearTerm(b.yeartermCd, a.yeartermCd);
    });

    // 테이블 표시
    table.style.display = 'table';
    noData.style.display = 'none';

    // 각 강의 정보를 테이블 행으로 추가
    lectureList.forEach(lecture => {
        const row = createLectureRow(lecture);
        tableBody.appendChild(row);
    });
}

/**
 * 학기 코드 비교 (최신순으로 정렬되도록)
 */
function compareYearTerm(term1, term2) {
    if (!term1) return -1;
    if (!term2) return 1;

    const [year1, semester1] = term1.split('_');
    const [year2, semester2] = term2.split('_');

    // 연도 비교
    if (year1 !== year2) {
        return parseInt(year1) - parseInt(year2);
    }

    // 같은 연도면 학기 비교
    const semesterOrder = {
        'REG1': 1,
        'SUMMER': 2,
        'REG2': 3,
        'WINTER': 4
    };

    return (semesterOrder[semester1] || 0) - (semesterOrder[semester2] || 0);
}

/**
 * 강의 정보로 테이블 행 생성
 */
function createLectureRow(lecture) {
	// 디버깅: finalized=> true/false 확인용
	console.log('목록 - lectureId:', lecture.lectureId, 'finalized:', lecture.finalized);

    const tr = document.createElement('tr');
    tr.onclick = function() {
        goToLectureDetail(lecture.lectureId);
    };

    tr.innerHTML = `
        <td>${lecture.subjectName || '-'}</td>
        <td class="completion-cell">${lecture.completionName || '-'}</td>
        <td>${formatYearTerm(lecture.yeartermCd) || '-'}</td>
        <td>${lecture.credit || '-'}/${lecture.hour}</td>
        <td>${lecture.subjectTypeName}</td>
        <td>${lecture.currentCap || 0}</td>
        <td>${getLectureStatusBadge(lecture)}</td>
    `;

    return tr;
}

/**
 * 학기 코드 => 한글로
 */
function formatYearTerm(yeartermCd) {
    if (!yeartermCd) return '-';

    const parts = yeartermCd.split('_');
    if (parts.length !== 2) return yeartermCd;

    const year = parts[0];
    const term = parts[1];

    const termMap = {
        'REG1': '1학기',
        'REG2': '2학기',
        'SUMMER': '여름학기',
        'WINTER': '겨울학기'
    };

    return `${year}년 ${termMap[term] || term}`;
}

/**
 * 강의 상태 뱃지 생성
 */
function getLectureStatusBadge(lecture) {
    let statusClass = '';
    let statusText = '';

    if (lecture.finalized) {
        // 종강: finalized가 true
        statusClass = 'status-badge status-ended';
        statusText = '종강';
    } else {
        // 진행중: finalized가 false이면 모두 진행중
        statusClass = 'status-badge status-ongoing';
        statusText = '진행중';
    }

    return `<span class="${statusClass}">${statusText}</span>`;
}

/**
 * 강의 상세 페이지로 이동
 */
function goToLectureDetail(lectureId) {
    if (lectureId) {
        location.href = `/lms/professor/lecture/detail/${lectureId}`;
    }
}

/**
 * 로딩 표시
 */
function showLoading() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('lectureTable').style.display = 'none';
    document.getElementById('noData').style.display = 'none';
    document.getElementById('errorMessage').style.display = 'none';
}

/**
 * 로딩 숨김
 */
function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';

    document.getElementById('lectureTable').style.display = 'none';
    document.getElementById('noData').style.display = 'none';
}