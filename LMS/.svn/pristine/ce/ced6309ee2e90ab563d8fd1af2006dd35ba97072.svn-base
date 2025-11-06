// -------------------------------------------------------------------------
// 📊 교직원 부서별 상태 차트 + 목록 필터링 JS
// -------------------------------------------------------------------------

// 부서별 색상을 정의 (파이 차트 등 시각화용)
const DEPT_COLORS = {
    '인사처': 'rgba(13, 110, 253, 0.7)',  // primary
    '행정처': 'rgba(255, 193, 7, 0.7)',   // warning
    '기타': 'rgba(108, 117, 125, 0.7)'    // secondary
};

let statusPieChart; // Chart.js 인스턴스 전역변수

// =========================================================================
// $(document).ready() 초기화 블록
// =========================================================================
$(document).ready(function() {

    // 1️⃣ 차트 초기화 (employmentCountsRaw는 JSP에서 전달됨)
    if (typeof employmentCountsRaw !== 'undefined') {
        initStatusPieChart(employmentCountsRaw);
    } else {
        console.warn("employmentCountsRaw 변수가 JSP에서 정의되지 않았습니다. 차트를 로드할 수 없습니다.");
    }

    // 2️⃣ 부서 상태 카드 클릭 → 필터링
    $(document).on('click', '.status-card', function() {
        const deptCd = $(this).data('dept-cd');
        const filterInput = $('#filterDeptName'); // ✅ JSP hidden input 이름 맞춤
        const currentFilter = filterInput.val();

        // 동일 부서 클릭 시 전체보기로 토글
        if (currentFilter === deptCd && deptCd !== "") {
            filterInput.val('');
        } else {
            filterInput.val(deptCd);
        }

        // 검색어 초기화 및 페이지 리셋
        $('#searchInput').val('');
        $('#currentPageInput').val(1);

        // 폼 제출
        $('#searchForm').submit();
    });

    // 3️⃣ 페이지 이동 함수
    window.pageing = function(page) {
        $('#currentPageInput').val(page);
        $('#searchForm').submit();
    };

    // 4️⃣ 검색 버튼 클릭 이벤트
    $('#searchButton').on('click', function() {
        handleSearchSubmit();
    });

    // 5️⃣ 행 클릭 시 상세 페이지로 이동
    $("#staffTable tbody tr.staff-row").css('cursor', 'pointer').on('click', function() {
        const staffNo = $(this).data('staffNo');
        if (staffNo) {
            const detailUrl = JSU_CONTEXT_PATH + '/lms/staffs/' + staffNo;
            window.location.href = detailUrl;
        }
    });
});

// =========================================================================
// 📈 파이 차트 초기화 함수
// =========================================================================
function initStatusPieChart(countsMap) {
    if (!countsMap || Object.keys(countsMap).length === 0) {
        console.warn("차트 데이터가 없어 파이 차트를 그릴 수 없습니다.");
        return;
    }

    const ctx = document.getElementById('statusPieChart');
    if (!ctx) return;

    // 기존 차트 파괴
    if (statusPieChart) statusPieChart.destroy();

    const labels = Object.keys(countsMap);
    const dataValues = Object.values(countsMap);

    const backgroundColors = labels.map(label => DEPT_COLORS[label] || DEPT_COLORS['기타']);
    const borderColors = backgroundColors.map(color => color.replace('0.7', '1'));

    statusPieChart = new Chart(ctx.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataValues,
                backgroundColor: backgroundColors,
                hoverBackgroundColor: borderColors,
                hoverBorderColor: "rgba(234, 236, 244, 1)",
            }]
        },
        options: {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
                tooltip: {
                    callbacks: {
                        title: function(tooltipItems) {
                            return tooltipItems[0].label;
                        },
                        label: function(context) {
                            const value = context.raw;
                            return '교직원 수: ' + value.toLocaleString() + '명';
                        }
                    }
                },
                legend: {
                    position: 'bottom',
                    labels: { color: '#333', font: { size: 13 } }
                }
            },
            cutout: '75%',
        }
    });
}

// =========================================================================
// 🔍 검색 폼 처리 함수
// =========================================================================
function handleSearchSubmit() {
    $('#filterDeptName').val(''); // ✅ 부서 필터 초기화
    $('#currentPageInput').val(1);
    document.getElementById('searchForm').submit();
}
