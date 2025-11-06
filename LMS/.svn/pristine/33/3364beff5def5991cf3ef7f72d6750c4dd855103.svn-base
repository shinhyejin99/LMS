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
 * 교수 강의 상세 페이지 스크립트
 */

let lectureData = null;
let studentsData = null;
let scoresData = null;
let weekPlanData = null;
let filteredScores = []; // 검색/정렬된 데이터 (검색, 정렬용)

document.addEventListener('DOMContentLoaded', function() {
    const lectureId = document.getElementById('lectureId').value;
    loadLectureDetail(lectureId);

    // 검색 이벤트
    document.getElementById('searchInput').addEventListener('input', handleSearch);

    // 정렬 이벤트
    document.getElementById('sortSelect').addEventListener('change', handleSort);
});

/**
 * 강의 상세 정보 로드
 */
async function loadLectureDetail(lectureId) {
    try {
        showLoading();

        // 1. 강의 기본 정보 조회
        lectureData = await fetchLectureInfo(lectureId);
        displayLectureInfo(lectureData);

        // 2. 주차 계획 조회
        try {
            weekPlanData = await fetchWeekPlan(lectureId);
            displayWeekPlan(weekPlanData);
        } catch (error) {
            console.warn('주차 계획 조회 실패:', error);
            displayWeekPlan([]); // 빈 배열로 처리
        }

        // 3. 종강된 강의인 경우 성적 정보 조회
        if (lectureData.finalized) {
            await loadScoreData(lectureId);
        } else {
            showNoScoreSection();
        }

        hideLoading();
        showMainContent();

    } catch (error) {
        hideLoading();
        showError(error.message || '강의 정보를 불러오는데 실패했습니다.');
        console.error('Error:', error);
    }
}

/**
 * 강의 기본 정보 조회
 */
async function fetchLectureInfo(lectureId) {
    const response = await fetch(`/classroom/api/v1/common/${lectureId}`);
    if (!response.ok) {
        throw new Error('강의 정보 조회 실패');
    }
    return await response.json();
}

/**
 * 주차 계획 조회
 */
async function fetchWeekPlan(lectureId) {
    const response = await fetch(`/classroom/api/v1/common/${lectureId}/plan`);
    if (!response.ok) {
        throw new Error('주차 계획 조회 실패');
    }
    return await response.json();
}

/**
 * 성적 데이터 로드 (수강생 정보 + 성적 정보)
 */
async function loadScoreData(lectureId) {
    // 수강생 정보 조회
    const studentsResponse = await fetch(`/classroom/api/v1/professor/${lectureId}/students`);
    if (!studentsResponse.ok) {
        throw new Error('수강생 정보 조회 실패');
    }
    studentsData = await studentsResponse.json();

    // 성적 정보 조회
    const scoresResponse = await fetch(`/classroom/api/v1/professor/${lectureId}/finalize/score`);
    if (!scoresResponse.ok) {
        throw new Error('성적 정보 조회 실패');
    }
    scoresData = await scoresResponse.json();

    // 학생 정보와 성적 정보 병합
    mergeStudentAndScore();

    // 성적 테이블 표시
    displayScoreTable();
}

/**
 * 학생 정보와 성적 정보 병합
 */
function mergeStudentAndScore() {
    const studentMap = {};
    studentsData.forEach(student => {
        studentMap[student.enrollId] = student;
    });

    filteredScores = scoresData.map(score => {
        const student = studentMap[score.enrollId];
        return {
            ...score,
            studentNo: student?.studentNo || '-',
            studentName: `${student?.lastName || ''}${student?.firstName || ''}`,
            univDeptName: student?.univDeptName || '-',
            student: student
        };
    });

    // 초기 정렬: 성적 높은 순
    filteredScores.sort((a, b) => {
        return (b.finalGrade || 0) - (a.finalGrade || 0);
    });
}

/**
 * 강의 기본 정보 표시
 */
function displayLectureInfo(lecture) {
	// 디버깅 finalized : true/false 확인용
	console.log('상세 - lectureId:', lecture.lectureId, 'finalized:', lecture.finalized);

    document.getElementById('subjectName').textContent = lecture.subjectName;
    document.getElementById('subjectTypeName').textContent = lecture.subjectTypeName;
    document.getElementById('univDeptName').textContent = lecture.univDeptName;
    document.getElementById('yeartermCd').textContent = formatYearTerm(lecture.yeartermCd);
    document.getElementById('creditHour').textContent = `${lecture.credit}학점 / ${lecture.hour}시간`;
    document.getElementById('capacity').textContent = `${lecture.currentCap}명`;

    // 강의 상태 표시
    const statusElement = document.getElementById('lectureStatus');
    if (lecture.finalized) {
        statusElement.innerHTML = '<span class="status-badge status-ended">종강</span>';
    } else {
        statusElement.innerHTML = '<span class="status-badge status-ongoing">진행중</span>';
    }
}

/**
 * 주차 계획 표시
 */
function displayWeekPlan(weekPlan) {
    const container = document.getElementById('weekPlanContent');
    container.innerHTML = '';

    if (!weekPlan || weekPlan.length === 0) {
        container.innerHTML = '<div class="no-data-message">등록된 주차 계획이 없습니다.</div>';
        return;
    }

    weekPlan.forEach(week => {
        const weekItem = document.createElement('div');
        weekItem.className = 'week-item';
        weekItem.innerHTML = `
            <div class="week-number">${week.lectureWeek}주차</div>
            <div class="week-goal">${week.weekGoal || '-'}</div>
            <div class="week-desc">${week.weekDesc || '-'}</div>
        `;
        container.appendChild(weekItem);
    });
}

/**
 * 성적 테이블 표시
 */
function displayScoreTable() {
    const tbody = document.getElementById('scoreTableBody');
    tbody.innerHTML = '';

    // 학생 정보만 있고 성적 정보가 없는 경우
    if (!scoresData || scoresData.length === 0) {
        // 학생 목록만으로 테이블 생성
        studentsData.forEach(student => {
            const row = createScoreRowWithoutScore(student);
            tbody.appendChild(row);
        });
    } else {
        // 정상적으로 성적 데이터 표시
        filteredScores.forEach(scoreData => {
            const row = createScoreRow(scoreData);
            tbody.appendChild(row);
        });
    }

    document.getElementById('scoreSection').style.display = 'flex';
}

/**
 * 성적 없을 때 => 학생 정보만으로 행 생성
 */
function createScoreRowWithoutScore(student) {
    const tr = document.createElement('tr');

    tr.innerHTML = `
        <td>${student.studentNo}</td>
        <td>${student.lastName}${student.firstName}</td>
        <td>${student.univDeptName}</td>
        <td>-</td>
    `;

    return tr;
}

/**
 * 성적 테이블 행 생성
 */
function createScoreRow(scoreData) {
    const tr = document.createElement('tr');

    // 각 평가항목별 점수를 맵으로 변환
    const scoreMap = {};
    if (scoreData.sectionScores) {
        scoreData.sectionScores.forEach(section => {
            scoreMap[section.gradeCriteriaCd] = section.rawScore;
        });
    }

    tr.innerHTML = `
        <td>${scoreData.studentNo}</td>
        <td>${scoreData.studentName}</td>
        <td>${scoreData.univDeptName}</td>
        <td><span class="${getGradeClass(scoreData.gpaCd)}">${scoreData.gpaCd || '-'}</span> (${formatScore(scoreData.finalGrade)})</td>
    `;

    return tr;
}

/**
 * 검색 처리
 */
function handleSearch(e) {
    const searchTerm = e.target.value.toLowerCase().trim();

    if (!searchTerm) {
        // 검색어가 없으면 전체 데이터 표시
        mergeStudentAndScore();
    } else {
        // 학번, 이름, 학과로 필터링
        filteredScores = scoresData.map(score => {
            const student = studentsData.find(s => s.enrollId === score.enrollId);
            return {
                ...score,
                studentNo: student?.studentNo || '-',
                studentName: `${student?.lastName || ''}${student?.firstName || ''}`,
                univDeptName: student?.univDeptName || '-',
                student: student
            };
        }).filter(score => {
            return score.studentNo.toLowerCase().includes(searchTerm) ||
                   score.studentName.toLowerCase().includes(searchTerm) ||
                   score.univDeptName.toLowerCase().includes(searchTerm);
        });
    }

    // 현재 정렬 옵션 유지
    const sortOption = document.getElementById('sortSelect').value;
    sortScores(sortOption);

    displayScoreTable();
}

/**
 * 정렬 처리
 */
function handleSort(e) {
    const sortOption = e.target.value;
    sortScores(sortOption);
    displayScoreTable();
}

/**
 * 점수 정렬
 */
function sortScores(sortOption) {
    const [field, order] = sortOption.split('-');

    filteredScores.sort((a, b) => {
        const aScore = a.finalGrade || 0;
        const bScore = b.finalGrade || 0;

        if (order === 'desc') {
            // 성적 높은 순 (내림차순)
            return bScore - aScore;
        } else {
            // 성적 낮은 순 (오름차순)
            return aScore - bScore;
        }
    });
}

/**
 * 점수 포맷팅
 */
function formatScore(score) {
    if (score === null || score === undefined || score === '') return '-';
    if (typeof score === 'number') {
        return score === 0 ? '0.00' : score.toFixed(2);
    }
    return score;
}

/**
 * 학점에 따른 CSS 클래스 다르게
 */
function getGradeClass(gpaCd) {
    if (!gpaCd) return '';

    if (gpaCd.startsWith('A')) return 'grade-excellent';
    if (gpaCd.startsWith('B')) return 'grade-good';
    if (gpaCd.startsWith('C')) return 'grade-normal';
    return 'grade-poor';
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
 * 성적 미확정 표시
 */
function showNoScoreSection() {
    document.getElementById('noScoreSection').style.display = 'block';
}

/**
 * 로딩 표시
 */
function showLoading() {
    document.getElementById('loading').style.display = 'block';
}

/**
 * 로딩 숨김
 */
function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

/**
 * 메인 컨텐츠 표시
 */
function showMainContent() {
    document.getElementById('mainContent').style.display = 'grid';
}

/**
 * 에러 메시지 표시
 */
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}