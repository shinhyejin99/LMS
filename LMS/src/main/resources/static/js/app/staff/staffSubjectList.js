// =========================================================================
// $(document).ready() 초기화 블록
// =========================================================================
$(document).ready(function() {
    const creditHourData = window.creditHourData || [];
    const deptCountsData = window.deptCountsData || [];

    // 1️⃣ 교과목당 학점 vs 시수 분포 (Doughnut Chart)
    if (creditHourData.length > 0) {
        drawCreditHourDoughnutChart(creditHourData);
    } else {
        console.warn("creditHourData 데이터가 없거나 비어 있어 학점/시수 차트를 그릴 수 없습니다.");
        $('#creditHourScatterChart').parent().html('<p class="text-center text-muted m-0">데이터 없음</p>');
    }

    // 2️⃣ 학과별 교과목 수 (Bar Chart)
    if (deptCountsData.length > 0) {
        drawDepartmentBarChart(deptCountsData);
    } else {
        console.warn("deptCountsData 데이터가 없어 학과별 교과목 수 차트를 그릴 수 없습니다.");
        $('#departmentBarChart').parent().html('<p class="text-center text-muted m-0">데이터 없음</p>');
    }

    // 필터 클릭 이벤트
    $('#typeFilterList .filter-list-item').on('click', function() {
        const filterType = $(this).data('type');
        $('#filterTypeInput').val(filterType === '전체' ? '' : filterType);
        $('#currentPageInput').val(1);
        $('#searchForm').submit();
    });
});


// =========================================================================
// 차트 관련 유틸리티 함수 (Chart.js)
// =========================================================================

function aggregateCreditHourData(subjects) {
    const counts = {};
    subjects.forEach(subject => {
        const key = `${subject.credit}학점/${subject.hour}시수`;
        counts[key] = (counts[key] || 0) + 1;
    });
    const labels = Object.keys(counts);
    const data = Object.values(counts);
    return { labels, data };
}

function drawCreditHourDoughnutChart(subjects) {
    if (!subjects || subjects.length === 0) {
        $('#creditHourScatterChart').parent().html('<p class="text-center text-muted m-0">데이터 없음</p>');
        return;
    }

    const { labels, data } = aggregateCreditHourData(subjects);
    const backgroundColors = [
        'rgba(0, 123, 255, 0.8)', 'rgba(40, 167, 69, 0.8)', 'rgba(255, 193, 7, 0.8)',
        'rgba(220, 53, 69, 0.8)', 'rgba(108, 117, 125, 0.8)', 'rgba(23, 162, 184, 0.8)',
        'rgba(111, 66, 193, 0.8)', 'rgba(253, 126, 20, 0.8)'
    ];

    const ctx = document.getElementById('creditHourScatterChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                label: '교과목 수',
                data: data,
                backgroundColor: backgroundColors.slice(0, data.length),
                borderColor: '#ffffff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: true, position: 'right', labels: { boxWidth: 10 } },
                title: { display: false },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed;
                            return `${label}: ${value}개`;
                        }
                    }
                }
            }
        }
    });
}


// =========================================================================
// 🎨 학과별 교과목 수 Bar Chart (블루톤 통일 버전)
// =========================================================================

function drawDepartmentBarChart(data) {
    if (!data || data.length === 0) {
        $('#departmentBarChart').parent().html('<p class="text-center text-muted m-0">데이터 없음</p>');
        return;
    }

    const deptNames = data.map(item => item.UNIV_DEPT_NAME);
    const counts = data.map(item => item.COUNT);

    // 💙 통일된 블루 테마 색상
    const backgroundColor = 'rgba(30, 136, 229, 0.75)';   // 밝은 블루 (메인색)
    const borderColor = 'rgba(21, 101, 192, 1)';          // 짙은 블루 (테두리)

    const ctx = document.getElementById('departmentBarChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: deptNames,
            datasets: [{
                label: '교과목 수',
                data: counts,
                backgroundColor: backgroundColor,
                borderColor: borderColor,
                borderWidth: 2,
                borderRadius: 6, // 막대 끝 둥글게
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    beginAtZero: true,
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: { color: '#333' }
                },
                y: {
                    ticks: { color: '#333' },
                    grid: { display: false }
                }
            },
            plugins: {
                legend: { display: false },
                title: {
                    display: true,
                    text: '학과별 교과목 수 비교',
                    font: { size: 18, weight: 'bold' },
                    color: '#1E88E5'
                },
                tooltip: {
                    backgroundColor: '#1565C0',
                    titleColor: '#fff',
                    bodyColor: '#fff',
                    callbacks: {
                        label: (context) => `${context.parsed.x}개`
                    }
                }
            }
        }
    });
}


// =========================================================================
// 🚨 SweetAlert2 적용: 교과목 폐지 함수
// =========================================================================

/**
 * 교과목 폐지 로직 실행 (SweetAlert2 적용)
 * @param {string} subjectId - 폐지할 교과목의 ID
 */
function retireSubject(subjectId) {
    Swal.fire({
        title: '교과목을 폐지하시겠습니까?',
        html: `
            <p>이 교과목을 <strong style="color:red;">폐지(DELETED)</strong> 상태로 변경합니다.</p>
            <p style="color:#555;">폐지된 교과목은 복구할 수 없으며,<br>수강 중인 학생에게 폐지 안내가 발송됩니다.</p>
        `,
        icon: 'warning',
        iconColor: '#ff9800',
        showCancelButton: true,
        confirmButtonText: '예, 폐지합니다',
        cancelButtonText: '취소',
        confirmButtonColor: '#EF5350',
        cancelButtonColor: '#6c757d'
    }).then((result) => {
        if (result.isConfirmed) {
            // ✅ 모달 닫기
            const modifyModal = bootstrap.Modal.getInstance(document.getElementById('subjectModifyModal'));
            if (modifyModal) modifyModal.hide();

            // ✅ 폐지 확인 후 메시지 출력 및 실제 요청
            Swal.fire({
                icon: 'info',
                title: '폐지 처리 중...',
                text: '잠시만 기다려주세요.',
                showConfirmButton: false,
                timer: 1200,
                willClose: () => {
                    // 실제 서버 요청
                    $.ajax({
                        url: `/lms/staff/subjects/retire/${subjectId}`,
                        type: 'POST',
                        success: function() {
                            Swal.fire({
                                icon: 'success',
                                title: '폐지 완료!',
                                text: '교과목이 성공적으로 폐지되었습니다.',
                                confirmButtonColor: '#1E88E5'
                            }).then(() => location.reload());
                        },
                        error: function(xhr) {
                            const msg = xhr.responseJSON?.message || xhr.responseText || '폐지 처리 중 오류가 발생했습니다.';
                            Swal.fire({
                                icon: 'error',
                                title: '폐지 실패',
                                text: msg,
                                confirmButtonColor: '#1E88E5'
                            });
                        }
                    });
                }
            });
        }
    });
}
