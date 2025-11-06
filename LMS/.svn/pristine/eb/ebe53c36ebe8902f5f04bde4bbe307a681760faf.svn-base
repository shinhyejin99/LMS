<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교직원 포털 - 통계 조회</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .statistics-container {
            padding: 1.5rem;
            border: 1px solid #e0e0e0;
            border-radius: 0.25rem;
            background-color: #f9f9f9;
            margin-bottom: 1.5rem;
        }
        .chart-placeholder {
            background-color: #e9ecef;
            height: 300px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #6c757d;
            font-size: 1.2rem;
            border-radius: 0.25rem;
        }
    </style>
</head>
<body class="bg-gray-100">
	<!-- Layout wrapper (similar to Sneat's) -->
	<div class="layout-wrapper">
		<div class="layout-container">
            <div class="container-fluid" id="main-content-area">
                <h3 class="mb-4">통계 정보 조회</h3>

                <div class="row align-items-stretch">
                    <div class="col-md-4">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5><i class="bi bi-bar-chart me-2"></i> 전체 학생 통계</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="studentStatsChart"></canvas>
                                <div class="card summary-card-custom mt-3">
                                    <div class="card-body text-center">
                                        <p class="card-text">총 학생 수: 1000명, 재학생: 700명, 휴학생: 200명</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5><i class="bi bi-graph-up me-2"></i> 학과별 학생 수 통계</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="departmentStatsChart"></canvas>
                                <div class="card summary-card-custom mt-3">
                                    <div class="card-body text-center">
                                        <p class="card-text">컴퓨터공학과: 150명, 경영학과: 120명, 디자인학과: 80명</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5><i class="bi bi-cash-coin me-2"></i> 등록금 납부 현황 통계</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="tuitionStatsChart"></canvas>
                                <div class="card summary-card-custom mt-3">
                                    <div class="card-body text-center">
                                        <p class="card-text">총 등록금: 100,000,000원, 납부 완료: 85,000,000원, 미납: 15,000,000원</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 필요에 따라 더 많은 통계 섹션 추가 -->

            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/dynamic_loader.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Student Statistics Chart (Doughnut Chart)
            const studentStatsCtx = document.getElementById('studentStatsChart').getContext('2d');
            new Chart(studentStatsCtx, {
                type: 'doughnut',
                data: {
                    labels: ['재학생', '휴학생', '졸업생'],
                    datasets: [{
                        data: [700, 200, 100], // Dummy data
                        backgroundColor: ['#81D4FA', '#A5D6A7', '#FFAB91'],
                        hoverOffset: 4
                    }]
                },
                options: {
                    responsive: true,
                    aspectRatio: 1, // Make it square
                    plugins: {
                        legend: {
                            position: 'bottom', // Move legend to bottom
                        },
                        title: {
                            display: true,
                            text: '전체 학생 통계'
                        }
                    }
                },
            });

            // Department Student Count Chart (Bar Chart)
            const departmentStatsCtx = document.getElementById('departmentStatsChart').getContext('2d');
            new Chart(departmentStatsCtx, {
                type: 'bar',
                data: {
                    labels: ['컴퓨터공학과', '경영학과', '디자인학과', '전자공학과', '건축학과'],
                    datasets: [{
                        label: '학생 수',
                        data: [150, 120, 80, 100, 70], // Dummy data
                        backgroundColor: [
                            'rgba(129, 212, 250, 0.8)', // Soft Blue
                            'rgba(165, 214, 167, 0.8)', // Soft Green
                            'rgba(255, 171, 145, 0.8)', // Soft Red/Orange
                            'rgba(255, 241, 118, 0.8)', // Soft Yellow
                            'rgba(206, 147, 216, 0.8)'  // Soft Purple
                        ],
                        borderColor: [
                            'rgba(129, 212, 250, 1)',
                            'rgba(165, 214, 167, 1)',
                            'rgba(255, 171, 145, 1)',
                            'rgba(255, 241, 118, 1)',
                            'rgba(206, 147, 216, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    aspectRatio: 1, // Make it square for consistency
                    plugins: {
                        legend: {
                            display: false,
                        },
                        title: {
                            display: true,
                            text: '학과별 학생 수 통계'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                },
            });

            // Tuition Payment Status Chart (Pie Chart)
            const tuitionStatsCtx = document.getElementById('tuitionStatsChart').getContext('2d');
            new Chart(tuitionStatsCtx, {
                type: 'pie',
                data: {
                    labels: ['납부 완료', '미납'],
                    datasets: [{
                        data: [850, 150], // Dummy data
                        backgroundColor: ['#A5D6A7', '#FFAB91'],
                        hoverOffset: 4
                    }]
                },
                options: {
                    responsive: true,
                    aspectRatio: 1, // Make it square
                    plugins: {
                        legend: {
                            position: 'bottom', // Move legend to bottom
                        },
                        title: {
                            display: true,
                            text: '등록금 납부 현황 통계'
                        }
                    }
                },
            });
        });
    </script>
</body>
</html>