<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 25.     	정태일            최초 생성
 *	2025. 9. 25.		김수현			style 삭제
-->

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>교수시스템</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/css/studentdashboard.css" />
</head>
<body class="professor-dashboard">
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<div class="container-xxl flex-grow-1 container-p-y">
		<div class="student-profile-container">

			<!-- 성적 & 졸업 현황 섹션 -->
			<div class="dashboard-grid">
				<!-- 프로필 카드 -->
				<div class="profile-card">
					<div class="profile-header">
						<div class="profile-content">
							<div class="dash-info-photo">
								<c:choose>
									<c:when test="${not empty userInfo.professorNo}">
										<img src="/classroom/api/v1/common/photo/professor/${userInfo.professorNo}" alt="증명사진" class="photo-img">
									</c:when>
									<c:otherwise>
										<div class="photo-placeholder">
											<i class="bx bx-user"></i>
											<span>증명사진 없음</span>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="profile-details" style="text-align: center; margin-top: 20px;" justify-content: center;>
							<h2 class="profile-name">${userInfo.lastName}${userInfo.firstName}</h2>
							<div class="profile-info-item">
								<span class="info-label" style="font-weight: 600; color: #555;">교번 :</span> <span class="info-value">${userInfo.professorNo}</span>
							</div>
							<div class="profile-info-item">
								<span class="info-label" style="font-weight: 600; color: #555;">소속:</span> <span class="info-value">${userInfo.departmentName}</span>
							</div>
							<div class="profile-info-item">
						        <span class="info-value">
						           
						        </span>
						    </div>						   					
						</div>
					</div>
				</div>
				<!-- 강의별 성적 분포 -->
				<div class="profile-card profile-card-large" id="lectureGradeChartsContainer">
					<div class="card-header-section">
						<h3 class="card-title">강의별 성적 분포</h3>
						<p class="card-subtitle">강의를 선택하여 성적 분포를 확인하세요.</p>
						<select id="lectureSelect" class="form-select" style="width: auto; margin-top: 10px;"></select>
					</div>
					<div class="chart-container">
						<canvas id="lectureGradeChart"></canvas>
					</div>
				</div>

				<!-- 학년별 지도학생 분포 차트 -->
				<div class="profile-card profile-card-large advisee-grade-by-year-chart-card">
					<div class="card-header-section">
						<h3 class="card-title">학년별 지도학생 분포</h3>
						<p class="card-subtitle"></p>
					</div>
					<div class="chart-container">
						<canvas id="adviseeGradeByYearChart" width="300" height="300" ></canvas>
					</div>
				</div>

				<!-- 수강중인 강의 -->
				<div class="profile-card">
					<h3 class="card-title">
						<a href="/portal/notice/list">공지사항</a>
					</h3>
					<ul class="course-list">
						<c:forEach var="notice" items="${generalNotices}">
							<li class="course-item"><i class="bx bx-bell course-icon"></i>
								<div class="course-info">
									<div class="course-name">
										<a href="/portal/notice/detail/${notice.noticeId}">${notice.title}</a>
									</div>
									<div class="course-time">${notice.createAt != null ? notice.createAt.toLocalDate() : ''}</div>
								</div></li>
						</c:forEach>
						<c:if test="${empty generalNotices}">
							<li class="course-item">
								<div class="course-info">
									<div class="course-name">등록된 공지사항이 없습니다.</div>
								</div>
							</li>
						</c:if>
					</ul>
				</div>

				<!-- 학사공지 -->
				<div class="profile-card">
					<h3 class="card-title">
						<a href="/portal/academicnotice/list">학사공지</a>
					</h3>
					<ul class="course-list">
						<c:forEach var="notice" items="${academicNotices}">
							<li class="course-item"><i class="bx bx-bell course-icon"></i>
								<div class="course-info">
									<div class="course-name">
										<a href="/portal/notice/detail/${notice.noticeId}">${notice.title}</a>
									</div>
									<div class="course-time">${notice.createAt != null ? notice.createAt.toLocalDate() : ''}</div>
								</div></li>
						</c:forEach>
						<c:if test="${empty academicNotices}">
							<li class="course-item">
								<div class="course-info">
									<div class="course-name">등록된 학사공지가 없습니다.</div>
								</div>
							</li>
						</c:if>
					</ul>
				</div>

				<!-- 졸업 이수 현황 -->
				<div class="profile-card">
					<div class="card-header-section">
						<h3 class="card-title">
							<a
								href="${pageContext.request.contextPath}/lms/professor/lecture/list">강의
								목록</a>
						</h3>
						<p class="card-subtitle"></p>
					</div>
					<ul class="course-list">
						<c:forEach var="lecture" items="${recentLectures}">
							<li class="course-item"><i
								class="bx bx-book-open course-icon"></i>
								<div class="course-info">
									<div class="course-name">
										<a
											href="${pageContext.request.contextPath}/lms/professor/lecture/detail/${lecture.lectureId}">${lecture.subjectName}</a>
									</div>
									<div class="course-time">
										${fn:substringBefore(lecture.yeartermCd, '_')}년
										<c:choose>
											<c:when test="${fn:substringAfter(lecture.yeartermCd, '_') eq 'REG1'}">1학기</c:when>
											<c:when test="${fn:substringAfter(lecture.yeartermCd, '_') eq 'REG2'}">2학기</c:when>
											<c:otherwise>${fn:substringAfter(lecture.yeartermCd, '_')}</c:otherwise>
										</c:choose>
									</div>
									<div class="course-students">
										${lecture.currentCap} / ${lecture.maxCap}명
									</div>
								</div></li>
						</c:forEach>
						<c:if test="${empty recentLectures}">
							<li class="course-item">
								<div class="course-info">
									<div class="course-name">개설된 강의가 없습니다.</div>
								</div>
							</li>
						</c:if>
					</ul>
				</div>
			</div>
		</div>

	</div>
	<!-- container-xxl -->
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	<script>
    document.addEventListener('DOMContentLoaded', function() {
        let lectureGradeCharts = {}; // To store chart instances
        let allLectureGradeData = []; // To store all fetched data

        fetch('/classroom/api/v1/professor/lecture-grade-distributions')
            .then(response => response.json())
            .then(lectureGradeDistributions => {
                allLectureGradeData = lectureGradeDistributions;
                const lectureSelect = document.getElementById('lectureSelect');
                const gradeLabels = ['A+', 'A0', 'B+', 'B0', 'C+', 'C0', 'D+', 'D0', 'F'];

                if (allLectureGradeData.length === 0) {
                    const container = document.getElementById('lectureGradeChartsContainer');
                    const noDataMessage = document.createElement('p');
                    noDataMessage.textContent = '개설된 강의의 성적 데이터가 없습니다.';
                    noDataMessage.style.textAlign = 'center';
                    noDataMessage.style.marginTop = '20px';
                    container.appendChild(noDataMessage);
                    return;
                }

                // Populate dropdown
                allLectureGradeData.forEach((lectureData, index) => {
                    const option = document.createElement('option');
                    option.value = index; // Use index to easily retrieve data
                    option.textContent = lectureData.lectureName;
                    lectureSelect.appendChild(option);
                });

                // Function to render/update the chart
                const renderLectureChart = (selectedIndex) => {
                    const lectureData = allLectureGradeData[selectedIndex];
                    const ctx = document.getElementById('lectureGradeChart').getContext('2d');

                    // Destroy existing chart if it exists
                    if (lectureGradeCharts.mainChart) {
                        lectureGradeCharts.mainChart.destroy();
                    }

                    const gradeCounts = gradeLabels.map(label => lectureData.gradeDistribution[label] || 0);

                    lectureGradeCharts.mainChart = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: gradeLabels,
                            datasets: [{
                                label: '학생 수',
                                data: gradeCounts,
                                backgroundColor: 'rgba(75, 192, 192, 0.6)',
                                borderColor: 'rgba(75, 192, 192, 1)',
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'top',
                                },
                                title: {
                                    display: true,
                                    text: `${lectureData.lectureName} 성적 분포`
                                }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    title: {
                                        display: true,
                                        text: '학생 수'
                                    }
                                },
                                x: {
                                    title: {
                                        display: true,
                                        text: '성적 구간'
                                    }
                                }
                            }
                        }
                    });
                };

                // Initial chart render for the first lecture
                renderLectureChart(0);

                // Add event listener for dropdown change
                lectureSelect.addEventListener('change', (event) => {
                    renderLectureChart(event.target.value);
                });
            })
            .catch(error => console.error('Error fetching lecture grade distributions:', error));

        fetch('/classroom/api/v1/professor/advisee-grade-by-year')
            .then(response => response.json())
            .then(data => {
                const labels = Object.keys(data).sort(); // Sort labels for consistent order
                const gradeCounts = labels.map(label => data[label] || 0);

                const ctx = document.getElementById('adviseeGradeByYearChart').getContext('2d');
                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '학생 수',
                            data: gradeCounts,
                            backgroundColor: 'rgba(153, 102, 255, 0.6)',
                            borderColor: 'rgba(153, 102, 255, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: false, // Set to false to control size via HTML attributes
                        maintainAspectRatio: false, // Also set to false for better control
                        plugins: {
                            legend: {
                                position: 'top',
                            },
                            title: {
                                display: true,
                                text: '학년별 지도학생 분포'
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: '학생 수'
                                }
                            },
                            x: {
                                title: {
                                    display: true,
                                    text: '학년'
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error fetching advisee grade by year distribution:', error));
    });
    
</script>
</body>
</html>
