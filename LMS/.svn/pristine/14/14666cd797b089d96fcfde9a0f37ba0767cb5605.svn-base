<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>담당학생 지도</title>
	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/professor/profAdStudentList.css" />
	<style>
		.student-detail-label {
			color: #444 !important;
			font-weight: 600 !important;
			width: 140px !important;
			background-color: #f8f8f8 !important;
		}
		.student-detail-value {
			font-family: "Public Sans", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Helvetica, Arial, sans-serif;
			font-size: 15px;
			color: #444 !important;
			font-weight: 600 !important;
		}
	</style></head>
<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<!-- 외부 래퍼 추가 -->
	<div class="advising-page">
		<div class="advising-container">

			<!-- 페이지 헤더 추가 -->
			<div class="page-header">
				<h1>담당학생 지도</h1>
			</div>


			<form action="/lms/professor/advising" method="GET">
				<div class="row mb-3">
					<div class="col-md-2">
						<label for="studentGrade" class="form-label">학년</label> <select
							class="form-select" id="studentGrade" name="grade">
							<!-- Options will be populated by JS -->
						</select>
					</div>
					<div class="col-md-2">
						<label for="studentStatus" class="form-label">상태</label> <select
							class="form-select" id="studentStatus" name="status">
							<option value=""
								<c:if test="${empty selectedStatus}">selected</c:if>>전체</option>
							<option value="ATTEND"
								<c:if test="${selectedStatus eq 'ATTEND'}">selected</c:if>>재학</option>
							<option value="REST"
								<c:if test="${selectedStatus eq 'REST'}">selected</c:if>>휴학</option>
							<option value="RTRN"
								<c:if test="${selectedStatus eq 'RTRN'}">selected</c:if>>복학</option>
							<option value="MJ_TRF"
								<c:if test="${selectedStatus eq 'MJ_TRF'}">selected</c:if>>전과</option>
							<option value="DROP"
								<c:if test="${selectedStatus eq 'DROP'}">selected</c:if>>자퇴</option>
							<option value="EXP"
								<c:if test="${selectedStatus eq 'EXP'}">selected</c:if>>제적</option>
						</select>
					</div>
					<div class="col-md-2 d-flex align-items-end">
						<button type="submit" class="btn btn-primary"
							id="searchStudentsBtn">검색</button>
					</div>
				</div>
			</form>
			<div class="content-row">
				<div class="content-left">
					<h5 class="section-title">지도학생 목록</h5>
					<div class="card border-0 shadow" style="height: 400px;">
						<div class="card-body" style="overflow-y: auto;">
							<table class="table table-striped table-hover">
								<thead>
									<tr>
										<th>학번</th>
										<th>이름</th>
										<th>학과</th>
										<th>학년</th>
										<th>상태</th>
									</tr>
								</thead>
								<tbody id="supervisedStudentTableBody">
									<c:choose>
										<c:when test="${not empty adviseeList}">
											<c:forEach var="student" items="${adviseeList}">
												<tr class="student-row"
													data-student-no="${student.studentNo}"
													data-name="${student.stdName}"
													data-dept="${student.deptName}"
													data-grade="${student.gradeName}" style="cursor: pointer;">
													<td>${student.studentNo}</td>
													<td>${student.stdName}</td>
													<td>${student.deptName}</td>
													<td>${student.gradeName}</td>
													<td>${student.stuStatusName}</td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td colspan="5" class="text-center text-muted">조회된 학생이
													없습니다.</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
						</div>
					</div>
					<nav aria-label="Page navigation" class="pagination-container">
						<ul class="pagination justify-content-center">
							<c:if test="${currentPage > 1}">
								<li class="page-item"><a class="page-link"
									href="?page=${currentPage - 1}">Previous</a></li>
							</c:if>
							<c:forEach begin="1" end="${totalPages}" var="i">
								<li
									class="page-item <c:if test='${currentPage == i}'>active</c:if>">
									<a class="page-link" href="?page=${i}">${i}</a>
								</li>
							</c:forEach>
							<c:if test="${currentPage < totalPages}">
								<li class="page-item"><a class="page-link"
									href="?page=${currentPage + 1}">Next</a></li>
							</c:if>
						</ul>
					</nav>
				</div>
				<div class="content-right">
					<h5 class="section-title">학년별 지도학생 분포</h5>
					<div class="card border-0 shadow" style="height: 400px;">
						<div class="card-body">
							<canvas id="adviseeGradeYearChart"></canvas>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- advising-container 닫기 -->
	<!-- advising-page 닫기 -->

	<!-- Student Detail Modal -->
	<div class="modal fade" id="studentDetailModal" tabindex="-1"
		aria-labelledby="studentDetailModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="studentDetailModalLabel">학생 상세 정보</h5>
					
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body" id="studentDetailBody">
					<!-- Details will be populated by JavaScript -->
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-custom-close"
						data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>

	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	<script>
document.addEventListener("DOMContentLoaded", () => {
    console.log("DOMContentLoaded event fired in profAdStudentList.jsp");
    const gradeSelect = document.getElementById("studentGrade");
    const studentDetailModalElement = document.getElementById('studentDetailModal');
    const studentDetailModal = new bootstrap.Modal(studentDetailModalElement);

    // Populate grade dropdown, ensuring "전체" is first.
    const allOption = new Option("전체", "");
    gradeSelect.add(allOption);

    for (let i = 1; i <= 4; i++) {
        const gradeText = i + "학년";
        const option = new Option(gradeText, String(i));
        gradeSelect.add(option);
    }

    // Set selected value after populating
    gradeSelect.value = "${selectedGrade}" || "";

    // Add click listener to student rows to show counseling modal
    document.querySelectorAll('.student-row').forEach(row => {
        row.addEventListener('click', () => {
            console.log("Raw data-student-no:", row.dataset.studentNo);
            const studentData = {
                no: row.dataset.studentNo.trim(),
                name: row.dataset.name.trim(),
                dept: row.dataset.dept.trim(),
                grade: row.dataset.grade.trim()
            };
            console.log("studentData from row click:", studentData);
            showStudentDetails(studentData, studentDetailModal);
        });
    });

    // Fetch and render Advisee Grade Year Distribution Chart
    fetch('/classroom/api/v1/professor/advisee-grade-by-year')
        .then(response => response.json())
        .then(data => {
            const labels = ['1학년', '2학년', '3학년', '4학년'];
            const gradeYearCounts = labels.map(label => data[label] || 0);

            const ctx = document.getElementById('adviseeGradeYearChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '학생 수',
                        data: gradeYearCounts,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.6)',
                            'rgba(54, 162, 235, 0.6)',
                            'rgba(255, 206, 86, 0.6)',
                            'rgba(75, 192, 192, 0.6)'
                        ],
                        borderColor: [
                            'rgba(255, 99, 132, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: false,
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
                            },
                            ticks: {
                                precision: 0
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
        .catch(error => console.error('Error fetching advisee grade year distribution:', error));
});

function formatYearTerm(yeartermCd) {
    console.log("formatYearTerm input: ", yeartermCd);
    if (!yeartermCd) {
        return '';
    }
    const parts = yeartermCd.split('_');
    console.log("formatYearTerm parts: ", parts);
    if (parts.length === 2) {
        const year = parts[0];
        const term = parts[1];
        if (term === 'REG1') {
            return year + '/1학기';
        } else if (term === 'REG2') {
            return year + '/2학기';
        } else if (term === 'SUMR') {
            return year + '/여름학기';
        } else if (term === 'WINT') {
            return year + '/겨울학기';
        }
    }
    return yeartermCd;
}

async function showStudentDetails(studentData, studentDetailModal) {
    console.log("showStudentDetails called with studentData:", studentData);
    const modalBody = document.getElementById('studentDetailBody');
    const studentDetailModalElement = document.getElementById('studentDetailModal');

    if (!studentDetailModalElement) {
        console.error('Error: studentDetailModalElement not found.');
        return;
    }

    studentDetailModal.hide();
    modalBody.innerHTML = '<p>로딩 중...</p>';
    studentDetailModal.show();

    if (!studentData.no) {
        modalBody.innerHTML = '<p>오류: 학생 번호를 찾을 수 없습니다.</p>';
        console.error('Error: studentData.no is empty or undefined.');
        return;
    }
    console.log("Making API call with studentNo:", studentData.no);
    const contextPath = "${pageContext.request.contextPath}";
    const url = contextPath + "/lms/professor/advising/api/studentDetails?studentNo=" + encodeURIComponent(studentData.no);
    console.log("API URL:", url);
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const fullStudentData = await response.json();

        let content = "<hr class='modal-hr'>" +
                      "<h6>학생 정보</h6>" +
                      "<table class='table table-bordered table-sm'>" +
                      "<tbody>" +
                      "<tr><th class='student-detail-label'>이름</th><td class='student-detail-value'>" + fullStudentData.stdName + "</td><th class='student-detail-label'>학번</th><td class='student-detail-value'>" + fullStudentData.studentNo + "</td></tr>" +
                      "<tr><th class='student-detail-label'>학과</th><td class='student-detail-value'>" + fullStudentData.deptName + "</td><th class='student-detail-label'>학년</th><td class='student-detail-value'>" + fullStudentData.gradeName + "</td></tr>" +
                      "<tr><th class='student-detail-label'>전화번호</th><td class='student-detail-value'>" + fullStudentData.stdTel + "</td><th class='student-detail-label'>이메일</th><td class='student-detail-value'>" + fullStudentData.stdEmail + "</td></tr>" +
                      "<tr><th class='student-detail-label'>주소</th><td class='student-detail-value' colspan='3'>" + fullStudentData.stdZipCode + " " + fullStudentData.stdBaseAddr + " " + fullStudentData.stdDetailAddr + "</td></tr>" +
                      "</tbody>" +
                      "</table>";
                     

        if (fullStudentData.lectureGrades && fullStudentData.lectureGrades.length > 0) {
            content += "<h6 class=\"mt-4\">성적 정보</h6>";
            content += "<table class=\"table table-bordered table-sm\"><thead><tr><th>과목명</th><th>이수구분</th><th>학년도/학기</th><th>학점</th><th>성적</th></tr></thead><tbody>";
            fullStudentData.lectureGrades.forEach(grade => {
                content += `<tr><td>\${grade.subjectName}</td><td>\${grade.completionName}</td><td>\${formatYearTerm(grade.yeartermCd)}</td><td>\${grade.credit}</td><td>\${grade.finalGrade !== null ? grade.finalGrade : 'N/A'}</td></tr>`;
            });
            content += "</tbody></table>";
        } else {
            content += "<h6 class=\"mt-4\">성적 정보</h6><p>조회된 성적 정보가 없습니다.</p>";
        }

        modalBody.innerHTML = content;

    } catch (error) {
        console.error('Could not fetch student details:', error);
        modalBody.innerHTML = '<p>학생 상세 정보를 불러오는 중 오류가 발생했습니다.</p>';
    }
}
</script>
</body>
</html>