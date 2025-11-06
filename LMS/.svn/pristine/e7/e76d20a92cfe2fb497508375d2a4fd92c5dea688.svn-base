/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 27.     	김수현            최초 생성
 * 2025. 10. 31.		김수현			포맷팅 함수 추가
 * </pre>
 */

/**
 * 교직원 수강신청 관리
 */

let currentLectureId = null;
let currentStudentPage = 1;
const STUDENT_PAGE_SIZE = 10;

// =====================================================
// 페이지 로드 시 초기화
// =====================================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('==> 교직원 수강신청 관리 페이지 로드');
    console.log('CONTEXT_PATH:', CONTEXT_PATH);
    console.log('currentYearterm:', currentYearterm);

    // 통계 로드
    loadStats();

    initEventListeners();
});

/**
 * 이벤트 리스너 초기화
 */
function initEventListeners() {
    // 학기 선택
    const yeartermSelector = document.getElementById('yeartermSelector');
    if (yeartermSelector) {
        yeartermSelector.addEventListener('change', changeYearterm);
    }

    // 검색 버튼
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', searchCourses);
    }

    // 검색 Enter 키
    const searchKeyword = document.getElementById('searchKeyword');
    if (searchKeyword) {
        searchKeyword.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchCourses();
            }
        });
    }

    // 초기화 버튼
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetSearch);
    }

    // 확정 버튼
    const confirmBtn = document.getElementById('confirmEnrollmentBtn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', confirmEnrollment);
    }

    // 테이블 행 클릭 (이벤트 위임)
    initLectureTableClick();

    // 모달 닫기
    initModalClose();
}

/**
 * 테이블 행 클릭 이벤트
 */
function initLectureTableClick() {
    const tbody = document.getElementById('lectureTableBody');

    if (!tbody) {
        console.log('lectureTableBody 없음');
        return;
    }

    tbody.addEventListener('click', function(e) {
        const row = e.target.closest('.lecture-row');

        if (row) {
            const lectureId = row.dataset.lectureId;
            const lectureName = row.dataset.lectureName;

            console.log('강의 클릭:', lectureId, lectureName);

            showStudentModal(lectureId, lectureName);
        }
    });
}

/**
 * 모달 닫기 이벤트
 */
function initModalClose() {
    // 닫기 버튼
    const closeBtn = document.querySelector('.staff-modal-close');
    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }

    // 모달 외부 클릭
    const modal = document.getElementById('staffStudentModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeModal();
            }
        });
    }

    // ESC 키
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const modal = document.getElementById('staffStudentModal');
            if (modal && modal.style.display === 'block') {
                closeModal();
            }
        }
    });
}

// =====================================================
// 학기 & 검색
// =====================================================

/**
 * 학기 변경
 */
function changeYearterm() {
    const yearterm = document.getElementById('yeartermSelector').value;
    window.location.href = `${CONTEXT_PATH}/lms/staff/course/manage/${yearterm}`;
}

/**
 * 통계 조회
 */
function loadStats() {
    fetch(`${CONTEXT_PATH}/lms/staff/rest/course/stats/${currentYearterm}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('totalLectures').textContent = data.totalLectures || 0;
            document.getElementById('totalStudents').textContent = data.totalStudents || 0;

            const avgRate = (data.avgEnrollRate * 100).toFixed(1);
            document.getElementById('avgEnrollRate').textContent = avgRate + '%';
        })
        .catch(error => {
            console.error('통계 조회 실패:', error);
        });
}

/**
 * 검색
 */
function searchCourses() {
    const keyword = document.getElementById('searchKeyword').value.trim();
    console.log('검색 키워드:', keyword);
    window.location.href = `${CONTEXT_PATH}/lms/staff/course/manage/${currentYearterm}?keyword=${encodeURIComponent(keyword)}`;
}

/**
 * 검색 초기화
 */
function resetSearch() {
    window.location.href = `${CONTEXT_PATH}/lms/staff/course/manage/${currentYearterm}`;
}

// yearterm 포맷팅
function formatYearterm(yearterm) {
    if (!yearterm) return '';

    const parts = yearterm.split('_');
    const year = parts[0];
    const term = parts[1];

    const termMap = {
        'REG1': '1학기',
        'SUM': '여름학기',
        'REG2': '2학기',
        'WIN': '겨울학기'
    };

    return `${year}년 ${termMap[term] || term}`;
}

/**
 * 수강신청 확정
 */
function confirmEnrollment() {

	const formattedTerm = formatYearterm(currentYearterm);

    Swal.fire({
        title: '수강신청 확정',
        html: `<strong>${formattedTerm}</strong> 학기의 수강신청을 확정하시겠습니까?<br><br>
               <span style="color: red;">⚠️ 확정 후에는 취소할 수 없습니다.</span>`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: '확정',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: '처리 중...',
                html: '수강신청을 확정하고 있습니다.',
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading()
            });

            fetch(`${CONTEXT_PATH}/lms/staff/rest/course/confirm-enrollment/${currentYearterm}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 성공
                    Swal.fire({
                        icon: 'success',
                        title: '확정 완료!',
                        html: `<strong>${data.confirmedCount}건</strong>의 수강신청이 확정되었습니다.`,
                        confirmButtonColor: '#4CAF50'
                    }).then(() => location.reload());

                } else if (data.alreadyConfirmed) {
                    // 중복 확정 시도
                    Swal.fire({
                        icon: 'info',
                        title: '이미 확정됨',
                        html: `<strong>${formattedTerm}</strong> 학기는<br>이미 수강신청이 확정되었습니다.<br><br>
                               <span style="color: #666;">확정 후에는 재확정할 수 없습니다.</span>`,
                        confirmButtonColor: '#3085d6'
                    }).then(() => location.reload());

                } else {
                    // 기타 실패
                    Swal.fire('실패', data.message, 'error');
                }
            })
            .catch(error => {
                console.error('확정 실패:', error);
                Swal.fire('오류', '수강신청 확정 중 오류가 발생했습니다.', 'error');
            });
        }
    });
}

// =====================================================
// 학생 목록 모달
// =====================================================

/**
 * 학생 목록 모달 열기
 */
function showStudentModal(lectureId, lectureName) {
    console.log('학생 목록 모달 열기:', lectureId, lectureName);

    currentLectureId = lectureId;
    currentStudentPage = 1;

    // 모달 제목
    document.getElementById('staffModalLectureTitle').textContent = lectureName;

    // 모달 표시
    document.getElementById('staffStudentModal').style.display = 'block';

    // Body 스크롤 방지
    document.body.style.overflow = 'hidden';

    // 데이터 로드
    loadModalStats(lectureId);
    loadStudentList(lectureId, 1);
}

/**
 * 모달 통계 로드
 */
function loadModalStats(lectureId) {
    fetch(`${CONTEXT_PATH}/lms/staff/rest/course/lectures/${lectureId}/enroll-info`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(info => {
            console.log('강의 정보:', info);

            const currentEnroll = info.currentEnroll || 0;
            const maxCap = info.maxCap || 0;
            const rate = maxCap > 0 ? Math.round((currentEnroll / maxCap) * 100 * 10) / 10 : 0;

            document.getElementById('staffModalCurrentEnroll').textContent = currentEnroll;
            document.getElementById('staffModalMaxCap').textContent = maxCap;
            document.getElementById('staffModalEnrollRate').textContent = rate + '%';
        })
        .catch(error => {
            console.error('강의 정보 조회 실패:', error);
        });
}

/**
 * 학생 목록 로드
 */
function loadStudentList(lectureId, page = 1) {
    currentStudentPage = page;

    const tbody = document.getElementById('staffStudentTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="staff-loading-message">
                <div class="staff-spinner"></div>
                학생 목록을 불러오는 중...
            </td>
        </tr>
    `;

    fetch(`${CONTEXT_PATH}/lms/staff/rest/course/lectures/${lectureId}/students?page=${page}&pageSize=${STUDENT_PAGE_SIZE}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('학생 목록:', data);
            renderStudentList(data.students);
            renderStudentPagination(data, lectureId);
        })
        .catch(error => {
            console.error('학생 목록 조회 실패:', error);
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="staff-empty-message">
                        학생 목록을 불러오는 중 오류가 발생했습니다.
                    </td>
                </tr>
            `;
        });
}

/**
 * 학생 목록 렌더링
 */
function renderStudentList(students) {
    const tbody = document.getElementById('staffStudentTableBody');

    if (!students || students.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="staff-empty-message">
                    수강신청한 학생이 없습니다.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = '';

    students.forEach((student, index) => {
        const rowNumber = (currentStudentPage - 1) * STUDENT_PAGE_SIZE + index + 1;

        const row = `
            <tr>
                <td>${rowNumber}</td>
                <td>${student.studentNo}</td>
                <td>${student.studentName}</td>
                <td>${student.gradeName}</td>
                <td>${student.collegeName || '-'}</td>
                <td>${student.univDeptName || '-'}</td>
                <td>${student.applyAt}</td>
            </tr>
        `;

        tbody.insertAdjacentHTML('beforeend', row);
    });
}

/**
 * 학생 목록 페이징 렌더링
 */
function renderStudentPagination(paginationInfo, lectureId) {
    let paginationDiv = document.getElementById('staffStudentPagination');

    if (!paginationDiv) {
        const modalContent = document.querySelector('.staff-modal-content');
        paginationDiv = document.createElement('div');
        paginationDiv.id = 'staffStudentPagination';
        paginationDiv.className = 'staff-modal-pagination';
        modalContent.appendChild(paginationDiv);
    }

    const { currentPage, totalPages } = paginationInfo;

    let html = '';

    if (currentPage > 1) {
        html += `<button class="staff-pagination-btn" data-lecture-id="${lectureId}" data-page="${currentPage - 1}">이전</button>`;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            html += `<span class="staff-current-page">${i}</span>`;
        } else {
            html += `<button class="staff-pagination-btn" data-lecture-id="${lectureId}" data-page="${i}">${i}</button>`;
        }
    }

    if (currentPage < totalPages) {
        html += `<button class="staff-pagination-btn" data-lecture-id="${lectureId}" data-page="${currentPage + 1}">다음</button>`;
    }

    paginationDiv.innerHTML = html;

    // 페이징 버튼 이벤트
    paginationDiv.querySelectorAll('.staff-pagination-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const lectureId = this.dataset.lectureId;
            const page = parseInt(this.dataset.page);
            loadStudentList(lectureId, page);
        });
    });
}

/**
 * 모달 닫기
 */
function closeModal() {
    document.getElementById('staffStudentModal').style.display = 'none';
    currentLectureId = null;
    document.body.style.overflow = '';
}