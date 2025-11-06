/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 11. 3.     	신혜진           최초 생성
 *
 * </pre>
 */

 /**
 * staffStudentInfoList.js
 * 학생 통계 및 목록 페이지의 차트 통계와 순차적 필터링 로직을 담당합니다.
 * JSP에서 JSU_CONTEXT_PATH, statusCountsRaw, genderDataRaw, gradeDataRaw가 정의되어 있어야 합니다.
 */

// =================================================================================
// 1. 전역 변수 및 상수 설정
// =================================================================================

// 🎨 차트 색상 설정 (학생 재적 상태에 맞게 정의)
const CHART_COLORS = {
    '재학': 'rgb(25, 135, 84)',     // success (Green)
    '휴학': 'rgb(255, 193, 7)',     // warning (Yellow)
    '졸업': 'rgb(13, 110, 253)',    // primary (Blue)
    '졸업유예': 'rgb(108, 117, 125)', // info/secondary (Gray)
    '자퇴': 'rgb(220, 53, 69)',     // danger (Red)
    '제적': 'rgb(0, 0, 0)',         // dark (Black)
    '기타': 'rgb(108, 117, 125)'
};

const CHART_BACKGROUND_COLORS = {
    '재학': 'rgba(25, 135, 84, 0.7)',
    '휴학': 'rgba(255, 193, 7, 0.7)',
    '졸업': 'rgba(13, 110, 253, 0.7)',
    '졸업유예': 'rgba(108, 117, 125, 0.7)',
    '자퇴': 'rgba(220, 53, 69, 0.7)',
    '제적': 'rgba(0, 0, 0, 0.7)',
    '기타': 'rgba(108, 117, 125, 0.7)'
};

const HIGHLIGHT_COLOR = 'rgba(220, 53, 69, 1)'; // 학년 클릭 시 강조 색상 (빨간색)

// 🎨 성별 차트 색상
const GENDER_COLORS = {
    '남성': 'rgb(59, 130, 246)',
    '여성': 'rgb(248, 113, 113)'
};

// 🎨 전체 학년별 차트 색상
const GRADE_COLORS = {
    '1학년': 'rgb(16, 185, 129)',
    '2학년': 'rgb(245, 158, 11)',
    '3학년': 'rgb(139, 92, 246)',
    '4학년': 'rgb(34, 197, 94)'
};

// 📊 Chart.js 인스턴스 저장 변수
let collegeBarChartInstance;
let departmentBarChartInstance;
let gradeBarChartInstance;
let genderPieChartInstance;
let overallGradeBarChartInstance;

// 💾 현재 필터 상태를 저장하는 객체
let currentFilters = {
    searchKeyword: $('#searchInput').val() || '',
    currentPage: parseInt($('#currentPageInput').val()) || 1,
    filterStatus: $('#filterStatusInput').val() || '',
    filterCollege: $('#filterCollegeInput').val() || '',
    filterDepartment: $('#filterDepartmentInput').val() || '',
    filterGrade: $('#filterGradeInput').val() || ''
};

// ⚙️ 현재 상세 통계 모달의 상태 (임시 필터)
let modalStep = 'college';
let currentSelectedStatus = currentFilters.filterStatus;
let currentSelectedCollege = currentFilters.filterCollege;
let currentSelectedDepartment = currentFilters.filterDepartment;
let lastClickedGradeIndex = null;

// =================================================================================
// 2. 유틸리티 함수
// =================================================================================

/**
 * RGB 색상을 밝게 만듭니다. (Chart.js hover 효과에 사용)
 */
function lightenColor(color, percent) {
    let R, G, B;
    if (color.startsWith('rgb')) {
        const parts = color.match(/\d+/g);
        R = parseInt(parts[0]);
        G = parseInt(parts[1]);
        B = parseInt(parts[2]);
    } else {
        return color;
    }

    const P = percent / 100;
    R = Math.min(255, R + (255 - R) * P);
    G = Math.min(255, G + (255 - G) * P);
    B = Math.min(255, B + (255 - B) * P);

    return `rgb(${Math.round(R)}, ${Math.round(G)}, ${Math.round(B)})`;
}

// =================================================================================
// 3. 초기화 및 이벤트 바인딩
// =================================================================================

$(function() {
    console.log("staffStudentinfoList.js 로드 완료.");

    // 초기 차트 렌더링 (JSP에서 정의된 Raw Data 사용)
    // genderDataRaw, gradeDataRaw 변수는 JSP 인라인 스크립트에서 이미 전역으로 선언되었으므로 window. 없이 직접 사용합니다.
    if (typeof genderDataRaw !== 'undefined') initializeGenderChart();
    if (typeof gradeDataRaw !== 'undefined') initializeOverallGradeChart();

    bindEventHandlers();
    updateStatusCardActiveState();

    // 학생 로우 클릭 이벤트 (상세 페이지 이동)
    $('#studentTable tbody').on('click', '.student-row', function() {
        const studentNo = $(this).data('studentNo');
        if (studentNo) {
            window.location.href = `${JSU_CONTEXT_PATH}/lms/staff/students/${studentNo}`;
        }
    });

    // 전역 함수 노출 (JSP 인라인 호출 대비)
    window.pageing = pageing;
    window.handleSearchSubmit = handleSearchSubmit;
});

/**
 * 전체 학생의 성별 통계를 파이 차트로 렌더링합니다.
 */
function initializeGenderChart() {
    // ⭐ 수정된 부분: JSP에서 선언된 전역 변수 genderDataRaw를 직접 사용 ⭐
    if (typeof genderDataRaw === 'undefined') {
        console.error("genderDataRaw 전역 변수가 정의되지 않았습니다. 차트를 로드할 수 없습니다.");
        return;
    }

    const genderLabels = Object.keys(genderDataRaw);
    const genderData = Object.values(genderDataRaw);

    const backgroundColors = genderLabels.map(label => GENDER_COLORS[label] || CHART_COLORS['기타']);
    const borderColors = genderLabels.map(label => lightenColor(GENDER_COLORS[label] || CHART_COLORS['기타'], 50));

    const ctx = document.getElementById('genderPieChartCanvas');
    if (ctx) {
        if (genderPieChartInstance) genderPieChartInstance.destroy();

        genderPieChartInstance = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: genderLabels,
                datasets: [{
                    data: genderData,
                    backgroundColor: backgroundColors,
                    hoverOffset: 10,
                    borderWidth: 1,
                    borderColor: borderColors
                }],
            },
            options: {
                maintainAspectRatio: false,
                responsive: true,
                plugins: {
                    title: { display: true, text: '전체 학생 성별 비율', font: { size: 16, weight: 'bold' } },
                    legend: { position: 'bottom', labels: { font: { size: 12 } } },
                    tooltip: {
                        callbacks: {
                            label: function(tooltipItem) {
                                const total = genderData.reduce((a, b) => a + b, 0);
                                const value = tooltipItem.raw;
                                const percent = (total > 0 ? (value / total) * 100 : 0).toFixed(1);
                                return `${tooltipItem.label}: ${value}명 (${percent}%)`;
                            }
                        }
                    }
                },
                layout: { padding: { left: 10, right: 10, top: 0, bottom: 10 } },
            }
        });
    }
}

/**
 * 전체 학생의 학년별 통계를 막대 차트로 렌더링합니다.
 */
function initializeOverallGradeChart() {
    // ⭐ 수정된 부분: JSP에서 선언된 전역 변수 gradeDataRaw를 직접 사용 ⭐
    if (typeof gradeDataRaw === 'undefined') {
        console.error("gradeDataRaw 전역 변수가 정의되지 않았습니다. 차트를 로드할 수 없습니다.");
        return;
    }

    const gradeLabels = Object.keys(gradeDataRaw);
    const gradeData = Object.values(gradeDataRaw);

    const backgroundColors = gradeLabels.map(label => GRADE_COLORS[label] || CHART_COLORS['기타']);
    const borderColor = 'rgb(108, 117, 125)';

    const ctx = document.getElementById('overallGradeBarChartCanvas');
    if (ctx) {
        if (overallGradeBarChartInstance) overallGradeBarChartInstance.destroy();

        overallGradeBarChartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: gradeLabels,
                datasets: [{
                    label: '학생 수',
                    data: gradeData,
                    backgroundColor: backgroundColors,
                    borderColor: borderColor,
                    borderWidth: 1,
                    borderRadius: 5,
                    hoverBackgroundColor: (context) => lightenColor(context.dataset.backgroundColor[context.dataIndex], 20),
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: '전체 학년별 학생 수', font: { size: 16, weight: 'bold' } },
                    tooltip: { callbacks: { label: (context) => `${context.label}: ${context.formattedValue} 명` } }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: '학생 수 (명)' },
                        ticks: { precision: 0 }
                    },
                    x: {
                        grid: { display: false },
                        title: { display: true, text: '학년' }
                    }
                }
            }
        });
    }
}

/**
 * 페이지 내 모든 동적 이벤트 핸들러를 바인딩합니다.
 */
function bindEventHandlers() {
    // 1. 재적 상태 카드 클릭 이벤트 (모달 트리거)
    $('.status-card').on('click', function() {
        const status = $(this).data('status');
        handleStatusCardClick(status);
    });

    // 2. 모달 닫기 시 상태 초기화 및 백 버튼 숨기기
    $('#statusDetailModal').on('hide.bs.modal', function () {
        // 모달을 닫아도 currentFilters는 그대로 유지되어야 하므로, currentSelected만 초기화
        modalStep = 'college';
        currentSelectedStatus = currentFilters.filterStatus;
        currentSelectedCollege = currentFilters.filterCollege;
        currentSelectedDepartment = currentFilters.filterDepartment;
        lastClickedGradeIndex = null;
        $('#modal-back-btn').addClass('d-none');
    });

    // 3. 모달 '목록 보기' 버튼 클릭 이벤트
    $('#viewListButton').on('click', function() {
        const modal = bootstrap.Modal.getInstance(document.getElementById('statusDetailModal'));
        if (modal) { modal.hide(); }
        // 필터링 적용 후 폼 제출
        submitSearchFormWithFilters(true);
    });

    // 4. 모달 '뒤로가기' 버튼 클릭 이벤트
    $('#modal-back-btn').on('click', handleModalBack);

}

/**
 * 현재 URL 쿼리 파라미터를 기반으로 활성화된 상태 카드에 시각적 표시를 합니다.
 */
function updateStatusCardActiveState() {
    $('.status-card').removeClass('active-filter');
    const filterStatus = $('#filterStatusInput').val();
    if (filterStatus) {
        $(`.status-card[data-status="${filterStatus}"]`).addClass('active-filter');
    }
}

// =================================================================================
// 4. 검색 및 페이징 로직
// =================================================================================

/**
 * 현재 필터 상태를 업데이트하고 검색 폼을 제출합니다.
 * @param {boolean} isModalSubmit - 모달에서 호출되었는지 여부
 */
function submitSearchFormWithFilters(isModalSubmit = false) {
    // Hidden Input에 필터 값 업데이트
    $('#searchInput').val(currentFilters.searchKeyword);
    $('#currentPageInput').val(currentFilters.currentPage);

    if (isModalSubmit) {
        // 모달 최종 선택 값을 Hidden Input에 반영
        $('#filterStatusInput').val(currentSelectedStatus);
        $('#filterCollegeInput').val(currentSelectedCollege);
        $('#filterDepartmentInput').val(currentSelectedDepartment);
        $('#filterGradeInput').val(currentFilters.filterGrade); // 학년은 이미 currentFilters에 저장됨
    } else {
        // 일반 검색/페이징 시, currentFilters 값을 Hidden Input에 반영
        $('#filterStatusInput').val(currentFilters.filterStatus);
        $('#filterCollegeInput').val(currentFilters.filterCollege);
        $('#filterDepartmentInput').val(currentFilters.filterDepartment);
        $('#filterGradeInput').val(currentFilters.filterGrade);
    }

    // 폼 제출
    $('#searchForm').submit();
}

/**
 * 페이지 이동을 처리합니다.
 */
function pageing(page) {
    currentFilters.currentPage = page;
    // 기존 필터 값들을 currentFilters에 로드
    currentFilters.searchKeyword = $('#searchInput').val();
    currentFilters.filterStatus = $('#filterStatusInput').val();
    currentFilters.filterCollege = $('#filterCollegeInput').val();
    currentFilters.filterDepartment = $('#filterDepartmentInput').val();
    currentFilters.filterGrade = $('#filterGradeInput').val();

    submitSearchFormWithFilters(false);
}

/**
 * 검색 버튼 클릭 시 처리 로직.
 */
function handleSearchSubmit() {
    currentFilters.searchKeyword = $('#searchInput').val();
    currentFilters.currentPage = 1;
    // 기존 필터 값들을 currentFilters에 로드
    currentFilters.filterStatus = $('#filterStatusInput').val();
    currentFilters.filterCollege = $('#filterCollegeInput').val();
    currentFilters.filterDepartment = $('#filterDepartmentInput').val();
    currentFilters.filterGrade = $('#filterGradeInput').val();

    submitSearchFormWithFilters(false);
}


// =================================================================================
// 5. 모달 및 차트 상세 필터링 로직 (AJAX 통신 포함)
// =================================================================================

/**
 * 재적 상태 카드 클릭 시 상세 통계 모달을 엽니다.
 */
function handleStatusCardClick(status) {
    // 1. 모달 임시 필터 초기화 및 현재 필터 상태(currentFilters) 초기화
    currentSelectedStatus = status;
    currentSelectedCollege = '';
    currentSelectedDepartment = '';
    currentFilters.filterStatus = status;
    currentFilters.filterCollege = '';
    currentFilters.filterDepartment = '';
    currentFilters.filterGrade = '';
    modalStep = 'college';
    lastClickedGradeIndex = null;

    // 2. UI 업데이트
    $('#statusDetailModalLabel').text(`[${currentSelectedStatus}] 학생 상세 통계`);
    $('#modal-back-btn').addClass('d-none');
    showModalView('college-view');
    $('#viewListButton').text(`[${currentSelectedStatus}] 학생 목록 보기`);

    // 3. 데이터 로드: 단과대학별 통계
    fetchChartData('college', { stuStatusName: status }, function(data) {
        renderBarChart('collegeBarChart', '단과대학별 학생 수', data, currentSelectedStatus, 'college');
        $('#statusDetailModal').modal('show');
    });
}

/**
 * AJAX를 통해 차트 데이터를 가져옵니다.
 */
function fetchChartData(chartType, params, callback) {
    const viewDivId = chartType === 'grade' ? 'grade-view' : `${chartType}-view`;
    const viewDiv = $(`#${viewDivId}`);

    // 로딩 표시
    viewDiv.html('');
    viewDiv.removeClass('d-none').addClass('chart-bar').css('height', '400px');
    viewDiv.addClass('d-flex justify-content-center align-items-center').html('<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div>');

    $.ajax({
        url: `${JSU_CONTEXT_PATH}/lms/staff/students/stats/${chartType}`,
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(response) {
            // 로딩 제거 및 Canvas 재추가
            viewDiv.removeClass('d-flex justify-content-center align-items-center').html('');
            const canvasId = chartType === 'grade' ? 'gradeBarChart' : `${chartType}BarChart`;
            viewDiv.append($('<canvas>').attr('id', canvasId));
            callback(response);
        },
        error: function(xhr, status, error) {
            console.error(`차트 데이터 로드 실패 (${chartType}): `, xhr.status, error);
             // 오류 처리 로직
            let errorMsg = `데이터 로드 중 오류가 발생했습니다. (HTTP: ${xhr.status})`;
            if (xhr.status === 400) { errorMsg += ` - 파라미터 불일치 가능성`; }
            errorMsg += `<br><small class="text-muted">요청 파라미터: ${JSON.stringify(params)}</small>`;

            viewDiv.removeClass('d-flex justify-content-center align-items-center').html(`
                <div class="alert alert-danger mx-3" role="alert">
                    <h5>통계 데이터 로드 실패</h5>
                    <p class="mb-0">${errorMsg}</p>
                </div>
            `);
        }
    });
}

/**
 * 막대 차트를 렌더링하거나 업데이트합니다.
 */
function renderBarChart(canvasId, title, data, status, dataType) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const viewDiv = $(ctx).parent();

    // 이전 차트 인스턴스 파괴
    if (canvasId === 'collegeBarChart' && collegeBarChartInstance) collegeBarChartInstance.destroy();
    else if (canvasId === 'departmentBarChart' && departmentBarChartInstance) departmentBarChartInstance.destroy();
    else if (canvasId === 'gradeBarChart' && gradeBarChartInstance) gradeBarChartInstance.destroy();

    let labels, counts;
    let labelKey = '';
    const countKey = 'STUDENT_COUNT';

    if (dataType === 'college') { labelKey = 'COLLEGE_NAME'; }
    else if (dataType === 'department') { labelKey = 'UNIV_DEPT_NAME'; }
    else if (dataType === 'grade') { labelKey = 'GRADE_NAME'; }

    if (Array.isArray(data)) {
        labels = data.map(item => item[labelKey] || 'N/A');
        counts = data.map(item => item[countKey] || 0);
    } else {
        viewDiv.removeClass('d-flex justify-content-center align-items-center').html(`
            <div class="alert alert-warning mx-3" role="alert"><p>데이터 형식 오류.</p></div>
        `);
        return;
    }

    if (counts.every(count => count === 0) || labels.length === 0) {
        viewDiv.removeClass('d-flex justify-content-center align-items-center').html(`
            <div class="alert alert-info mx-3 text-center" role="alert">
                <p class="mb-0"><strong>${title}</strong>에 해당하는 학생 데이터가 존재하지 않습니다.</p>
            </div>
        `);
        return;
    }

    const primaryColor = CHART_BACKGROUND_COLORS[status] || CHART_BACKGROUND_COLORS['기타'];
    const borderColor = CHART_COLORS[status] || CHART_COLORS['기타'];
    let backgroundColors = counts.map(() => primaryColor);

    // 학년 차트 하이라이트 유지
    if (canvasId === 'gradeBarChart' && lastClickedGradeIndex !== null && lastClickedGradeIndex < backgroundColors.length) {
        backgroundColors[lastClickedGradeIndex] = HIGHLIGHT_COLOR;
    }

    const chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '학생 수',
                data: counts,
                backgroundColor: backgroundColors,
                borderColor: borderColor,
                borderWidth: 1,
                hoverBackgroundColor: (context) => {
                    const index = context.dataIndex;
                    if (canvasId === 'gradeBarChart' && index === lastClickedGradeIndex) {
                        return lightenColor(HIGHLIGHT_COLOR, 20);
                    }
                    return lightenColor(borderColor, 20);
                },
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: { callbacks: { label: (context) => `${context.label}: ${context.formattedValue} 명` } }
            },
            scales: {
                y: { beginAtZero: true, title: { display: true, text: '학생 수 (명)' }, ticks: { precision: 0 } },
                x: { grid: { display: false }, title: { display: true, text: (dataType === 'college') ? '단과대학' : (dataType === 'department') ? '학과' : '학년' } }
            },
            onClick: function(event, elements) {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const clickedLabel = labels[index];
                    handleBarChartClick(canvasId, clickedLabel, index);
                }
            }
        }
    });

    // 전역 변수에 인스턴스 저장
    if (canvasId === 'collegeBarChart') collegeBarChartInstance = chartInstance;
    else if (canvasId === 'departmentBarChart') departmentBarChartInstance = chartInstance;
    else if (canvasId === 'gradeBarChart') gradeBarChartInstance = chartInstance;
}


/**
 * 막대 차트 클릭 시 다음 단계의 상세 통계를 로드합니다.
 */
function handleBarChartClick(canvasId, clickedLabel, index) {
    if (canvasId === 'collegeBarChart') {
        // 단과대학 클릭 -> 학과별 통계 로드 (Step 1 -> 2)
        currentSelectedCollege = clickedLabel;
        currentSelectedDepartment = '';
        currentFilters.filterGrade = '';
        modalStep = 'department';
        lastClickedGradeIndex = null;

        $('#statusDetailModalLabel').text(`[${currentSelectedStatus}] > ${currentSelectedCollege} 학과별 통계`);
        $('#modal-back-btn').removeClass('d-none');
        $('#viewListButton').text(`[${currentSelectedStatus}] > ${currentSelectedCollege} 목록 보기`);
        showModalView('department-view');

        fetchChartData('department', {
            stuStatusName: currentSelectedStatus,
            collegeName: currentSelectedCollege
        }, function(data) {
            renderBarChart('departmentBarChart', '학과별 학생 수', data, currentSelectedStatus, 'department');
        });

    } else if (canvasId === 'departmentBarChart') {
        // 학과 클릭 -> 학년별 통계 로드 (Step 2 -> 3)
        currentSelectedDepartment = clickedLabel;
        currentFilters.filterGrade = '';
        modalStep = 'grade';
        lastClickedGradeIndex = null;

        $('#statusDetailModalLabel').text(`[${currentSelectedStatus}] > ${currentSelectedCollege} > ${currentSelectedDepartment} 학년별 통계`);
        $('#viewListButton').text(`[${currentSelectedStatus}] > ${currentSelectedDepartment} 목록 보기`);
        showModalView('grade-view');

        fetchChartData('grade', {
            stuStatusName: currentSelectedStatus,
            collegeName: currentSelectedCollege,
            univDeptName: currentSelectedDepartment
        }, function(data) {
            renderBarChart('gradeBarChart', '학년별 학생 수', data, currentSelectedStatus, 'grade');
        });

    } else if (canvasId === 'gradeBarChart') {
        // 학년 클릭 -> 최종 필터 적용 및 하이라이트 (Step 3)
        const chartInstance = gradeBarChartInstance;
        const dataset = chartInstance.data.datasets[0];
        const primaryColor = CHART_BACKGROUND_COLORS[currentSelectedStatus] || CHART_BACKGROUND_COLORS['기타'];

        // 하이라이트 적용
        if (lastClickedGradeIndex !== null && lastClickedGradeIndex !== index) {
            dataset.backgroundColor[lastClickedGradeIndex] = primaryColor;
        }
        dataset.backgroundColor[index] = HIGHLIGHT_COLOR;
        chartInstance.update();
        lastClickedGradeIndex = index;

        // 최종 선택된 모든 필터 값을 currentFilters에 동기화
        currentFilters.filterStatus = currentSelectedStatus;
        currentFilters.filterCollege = currentSelectedCollege;
        currentFilters.filterDepartment = currentSelectedDepartment;
        currentFilters.filterGrade = clickedLabel;

        // 목록 보기 버튼의 텍스트 업데이트 및 즉시 제출
        const finalFilterText = `${currentFilters.filterStatus} > ${currentFilters.filterCollege} > ${currentFilters.filterDepartment} > ${currentFilters.filterGrade}학년`;
        $('#statusDetailModalLabel').text(`[${finalFilterText}] 목록 보기`);
        $('#viewListButton').text(`[${finalFilterText}] 목록 보기`);

        // 모달 닫고 검색 폼 제출
        const modal = bootstrap.Modal.getInstance(document.getElementById('statusDetailModal'));
        if (modal) { modal.hide(); }
        submitSearchFormWithFilters(true);
    }
}


/**
 * 모달 뷰 전환을 처리합니다.
 */
function showModalView(viewId) {
    $('.modal-body > div').addClass('d-none');
    $(`#${viewId}`).removeClass('d-none');
}

/**
 * 모달 '뒤로가기' 버튼 클릭 시 이전 단계로 돌아갑니다.
 */
function handleModalBack() {
    if (modalStep === 'grade') {
        // 학년 -> 학과로
        modalStep = 'department';
        currentFilters.filterGrade = '';
        lastClickedGradeIndex = null;

        $('#statusDetailModalLabel').text(`[${currentSelectedStatus}] > ${currentSelectedCollege} 학과별 통계`);
        $('#viewListButton').text(`[${currentSelectedStatus}] > ${currentSelectedCollege} 목록 보기`);

        showModalView('department-view');
    } else if (modalStep === 'department') {
        // 학과 -> 단과대학으로
        modalStep = 'college';
        currentSelectedCollege = '';
        currentSelectedDepartment = '';
        currentFilters.filterGrade = '';
        lastClickedGradeIndex = null;

        $('#statusDetailModalLabel').text(`[${currentSelectedStatus}] 학생 상세 통계`);
        $('#modal-back-btn').addClass('d-none');
        $('#viewListButton').text(`[${currentSelectedStatus}] 학생 목록 보기`);

        showModalView('college-view');
    }
}