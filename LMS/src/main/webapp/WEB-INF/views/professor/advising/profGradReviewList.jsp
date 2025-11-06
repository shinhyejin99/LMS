<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>담당학생 지도</title>
<!-- <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous"> -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/professor/profGradReviewList.css" />
<style>
    .status-badge {
        color: white;
        border-radius: 12px;
        font-size: 13px;
        font-weight: 600;
        padding: 4px 12px;
        display: inline-block;
        text-align: center;
    }
    .status-submitted {
        background-color: #28a745; /* green */
    }
    .status-approved {
        background-color: #007bff; /* blue */
    }
    .status-rejected {
        background-color: #dc3545; /* red */
    }
    .status-pending {
        background-color: #6c757d; /* grey */
    }
    .my-sweetalert-popup {
        z-index: 9999 !important;
    }
    .progress-bar-container {
        width: 100px; /* Adjust as needed */
        background-color: #e0e0e0;
        border-radius: 5px;
        overflow: hidden;
        height: 20px; /* Height of the progress bar */
        margin: auto; /* Center the bar in the table cell */
    }
    .progress-bar-fill {
        height: 100%;
        background-color: #4CAF50; /* Green color for progress */
        text-align: center;
        color: white;
        line-height: 20px; /* Center text vertically */
        font-size: 12px;
    }
    .data-table th, .data-table td {
        text-align: center !important;
    }
</style>
</head>
<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<!-- 외부 래퍼 추가 -->
	<div class="graduation-review-page">
		<div class="graduation-container">

			<!-- 페이지 헤더 추가 -->
			<div class="page-header">
				<h1>졸업요건 검토</h1>
			</div>

			<!-- 검색 폼 -->
			<form action="/lms/professor/advising/graduation" method="GET"
				class="search-form">
				<div class="form-group">
					<label for="studentGrade" class="form-label">학년</label> <select
						class="form-select" id="studentGrade" name="grade">
						<!-- Options will be populated by JS -->
					</select>
				</div>
				<div class="form-group">
					<button type="submit" class="btn-search" id="searchStudentsBtn">검색</button>
				</div>
			</form>

			<!-- 학생 목록 -->
			<h5 class="section-title">지도학생 목록</h5>
			<table class="data-table">
				<thead>
					<tr>
						<th>이름</th>
						<th>학번</th>
						<th>학년</th>
						<th>총필요학점</th>
						<th>이수학점</th>
						<th>전공학점</th>
						<th>교양학점</th>
						<th>졸업요건 충족률</th>
						<th>졸업과제 제출</th>
					</tr>
				</thead>
				<tbody id="supervisedStudentTableBody">
					<c:choose>
						<c:when test="${not empty gradReviewList}">
							<c:forEach var="student" items="${gradReviewList}">
								<tr class="student-row" data-student-no="${student.studentNo}"
									data-name="${student.stdName}" data-dept="${student.deptName}"
									data-grade="${student.gradeName}">
									<td>${student.stdName}</td>
									<td>${student.studentNo}</td>
									<td>${student.gradeName}</td>
									<td>${student.totalRequiredCredits}</td>
									<td>${student.completedCredits}</td>
									<td>${student.majorCredits}</td>
									<td>${student.liberalArtsCredits}</td>
									<td>
										<div class="progress-bar-container">
											<div class="progress-bar-fill" style="width: ${student.graduationRate}%;">
												${student.graduationRate}%
											</div>
										</div>
									</td>
									<td><c:choose>
											<c:when test="${student.graduReqCd == 'GRAD_ASSIGNMENT'}">
												<span class="status-badge status-submitted">제출됨</span>
                                            </c:when>
											<c:when test="${student.graduReqCd == 'APPROVED'}">
												<span class="status-badge status-approved">승인</span>
                                            </c:when>
											<c:when test="${student.graduReqCd == 'REJECTED'}">
												<span class="status-badge status-rejected">반려</span>
                                            </c:when>
											<c:otherwise>
												<span class="status-badge status-pending">미제출</span>
                                            </c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="9" class="text-center text-muted">조회된 학생이
									없습니다.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>

			<!-- 페이지네이션 -->
			<nav aria-label="Page navigation">
				<ul class="pagination">
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
		<!-- graduation-container 닫기 -->
	</div>
	<!-- graduation-review-page 닫기 -->

	<!-- Counseling Detail Modal -->
	<div class="modal fade" id="counselingDetailModal" tabindex="-1"
		aria-labelledby="counselingDetailModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="counselingDetailModalLabel">졸업 과제
						제출 정보</h5>
						
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body" id="counselingDetailBody">
					<!-- Details will be populated by JavaScript -->
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-success"
						id="approveGraduationBtn" style="display: none;">승인</button>
					<button type="button" class="btn btn-danger"
						id="rejectGraduationBtn" style="display: none;">반려</button>
					<button type="button" class="btn btn-custom-close"
						data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Rejection Reason Modal -->
	<div class="modal fade" id="rejectionReasonModal" tabindex="-1" aria-labelledby="rejectionReasonModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="rejectionReasonModalLabel">반려 사유 입력</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<form>
						<div class="mb-3">
							<label for="rejectionReasonText" class="form-label">반려 사유를 구체적으로 작성해주세요.</label>
							<textarea class="form-control" id="rejectionReasonText" rows="4"></textarea>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
					<button type="button" class="btn btn-danger" id="confirmRejectionBtn">반려 확정</button>
				</div>
			</div>
		</div>
	</div>

	<script>
document.addEventListener("DOMContentLoaded", () => {
    console.log("DOMContentLoaded event fired in profAdStudentList.jsp");
    const gradeSelect = document.getElementById("studentGrade");
    const counselingModalElement = document.getElementById('counselingDetailModal');
    const counselingModal = new bootstrap.Modal(counselingModalElement);

    // Populate grade dropdown, ensuring "전체" is first.
    const allOption = new Option("전체", "");
    gradeSelect.add(allOption);

    for (let i = 3; i <= 4; i++) {
        const gradeText = i + "학년";
        const option = new Option(gradeText, String(i));
        gradeSelect.add(option);
    }

    // Set selected value after populating
    gradeSelect.value = "${selectedGrade}" || "";

    // Add click listener to student rows to show counseling modal
    document.querySelectorAll('.student-row').forEach(row => {
        row.addEventListener('click', () => {
            console.log("Clicked row:", row); // Log the element
            const studentNo = row.getAttribute('data-student-no');
            console.log("Raw studentNo from attribute:", studentNo); // Log the raw value

            if (!studentNo || !studentNo.trim()) {
                console.error("Student number is missing or empty on the clicked row!");
                alert("오류: 학생 번호를 찾을 수 없습니다.");
                return;
            }

            const studentData = {
                no: studentNo.trim(),
                name: row.getAttribute('data-name').trim(),
                dept: row.getAttribute('data-dept').trim(),
                grade: row.getAttribute('data-grade').trim()
            };
            console.log("Constructed studentData:", studentData); // Log the final object
            showGraduationAssignmentDetails(studentData, counselingModal);
        });
    });
});

async function showGraduationAssignmentDetails(studentData, modal) {
    console.log("Received studentData in showGraduationAssignmentDetails:", studentData);
    console.log("Student Data for Modal:", studentData);
    const modalBody = document.getElementById('counselingDetailBody');
    modalBody.innerHTML = '<p>로딩 중...</p>';
    modal.show();

    const contextPath = "${pageContext.request.contextPath}";
    console.log("studentData.no before API call:", studentData.no);
    console.log("studentData.no before API call:", studentData.no);
    const url = contextPath + "/lms/professor/advising/graduation/api/assignments?studentNo=" + studentData.no;
    console.log("Fetching URL:", url);

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const responseText = await response.text();
        console.log("Raw API Response:", responseText); // Debugging line
        const assignment = responseText ? JSON.parse(responseText) : null;
        console.log("Parsed assignment object:", assignment); // Add this line
        console.log("assignment.graduReqSubmitId from parsed object:", assignment ? assignment.graduReqSubmitId : "N/A"); // Add this line

        let content = "<hr style=\"border-top: 1px solid #696CFF;\">";

        // Fetch full student details
        const studentDetailsUrl = contextPath + "/lms/professor/advising/api/studentDetails?studentNo=" + encodeURIComponent(studentData.no);
        const studentDetailsResponse = await fetch(studentDetailsUrl);
        if (!studentDetailsResponse.ok) {
            throw new Error(`Failed to fetch student details! status: ${studentDetailsResponse.status}`);
        }
        const fullStudentData = await studentDetailsResponse.json();

        content += 
        			"<h6>학생 정보</h6>" +
			        "<table class='table table-bordered table-sm'>" +
			        "<tbody>" +
			        "<tr><th class='student-detail-label'>이름</th><td class='student-detail-value'>" + fullStudentData.stdName + "</td><th class='student-detail-label'>학번</th><td class='student-detail-value'>" + fullStudentData.studentNo + "</td></tr>" +
			        "<tr><th class='student-detail-label'>학과</th><td class='student-detail-value'>" + fullStudentData.deptName + "</td><th class='student-detail-label'>학년</th><td class='student-detail-value'>" + fullStudentData.gradeName + "</td></tr>" +
			        "<tr><th class='student-detail-label'>전화번호</th><td class='student-detail-value'>" + fullStudentData.stdTel + "</td><th class='student-detail-label'>이메일</th><td class='student-detail-value'>" + fullStudentData.stdEmail + "</td></tr>" +
			        "<tr><th class='student-detail-label'>주소</th><td class='student-detail-value' colspan='3'>" + fullStudentData.stdZipCode + " " + fullStudentData.stdBaseAddr + " " + fullStudentData.stdDetailAddr + "</td></tr>" +
			        "</tbody>" +
			        "</table>" +
                   "<h6>졸업 과제 제출 정보</h6>"; // This is the heading for graduation assignment info

        if (assignment && assignment.graduReqSubmitId) { // Check if an assignment exists
            let statusText = "미제출";
            if (assignment.graduReqCd === 'GRAD_ASSIGNMENT') {
                statusText = "제출됨";
            } else if (assignment.graduReqCd === 'APPROVED') {
                statusText = "승인";
            } else if (assignment.graduReqCd === 'REJECTED') {
                statusText = "반려";
            }

            content += "<table class='table table-bordered table-sm'>" +
                       "<tbody>" +
                       "<tr><th class='student-detail-label'>제출일</th><td class='student-detail-value'>" + (assignment.submitAt ? assignment.submitAt.substring(0, 10) : 'N/A') + "</td></tr>" +
                       "<tr><th class='student-detail-label'>상태</th><td class='student-detail-value'>" + statusText + "</td></tr>";

            if (assignment.fileId && Number(assignment.fileOrder) !== -1) {
                const downloadUrl = contextPath + "/portal/file/download/" + assignment.fileId + "/" + String(assignment.fileOrder);
                content += "<tr><th class='student-detail-label'>첨부 파일</th><td class='student-detail-value'><a href=\"" + downloadUrl + "\" target=\"_blank\">다운로드</a></td></tr>";
            } else {
                content += "<tr><th class='student-detail-label'>첨부 파일</th><td class='student-detail-value'>없음</td></tr>";
            }

            if (assignment.evaluation) {
                content += "<tr><th class='student-detail-label'>평가/반려 사유</th><td class='student-detail-value'>" + assignment.evaluation + "</td></tr>";
            }
            content += "</tbody>" +
                       "</table>";

            // Add approve/reject buttons if status is '제출됨'
            if (assignment.graduReqCd === 'GRAD_ASSIGNMENT') {
            }

        } else {
            content += "<p>졸업 과제 제출 내역이 없습니다。</p>";
        }

        modalBody.innerHTML = content;

        const approveBtn = document.getElementById('approveGraduationBtn');
        const rejectBtn = document.getElementById('rejectGraduationBtn');

        // Hide buttons by default
        approveBtn.style.display = 'none';
        rejectBtn.style.display = 'none';

        if (assignment && assignment.graduReqCd === 'GRAD_ASSIGNMENT') {
            approveBtn.style.display = 'inline-block';
            rejectBtn.style.display = 'inline-block';

            const trimmedReviewId = assignment.graduReqSubmitId.trim();
            approveBtn.setAttribute('data-review-id', trimmedReviewId);
            rejectBtn.setAttribute('data-review-id', trimmedReviewId);

            console.log("Approve Button data-review-id:", approveBtn.getAttribute('data-review-id'));
            console.log("Reject Button data-review-id:", rejectBtn.getAttribute('data-review-id'));

            // Remove previous event listeners to prevent multiple bindings
            approveBtn.onclick = null;
            rejectBtn.onclick = null;

            approveBtn.onclick = (event) => approveGraduationAssignment(event.target.getAttribute('data-review-id'));
            rejectBtn.onclick = (event) => rejectGraduationAssignment(event.target.getAttribute('data-review-id'));
        }

    } catch (error) {
        console.error('졸업 과제 제출 정보를 불러오는 중 오류 발생:', error);
        modalBody.innerHTML = '<p>졸업 과제 제출 정보를 불러오는 중 오류가 발생했습니다.</p>';
    }
}

async function approveRequest(reqId) {
    if (!confirm('해당 상담 요청을 승인하시겠습니까?')) {
        return;
    }
    const contextPath = "${pageContext.request.contextPath}";
    const url = `${contextPath}/lms/professor/advising/api/counselingRequests/${reqId}/approve`;
    try {
        const response = await fetch(url, { method: 'POST' });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        alert('상담 요청이 승인되었습니다.');
        const counselingModalElement = document.getElementById('counselingDetailModal');
        const counselingModal = bootstrap.Modal.getInstance(counselingModalElement);
        counselingModal.hide();
        location.reload();
    } catch (error) {
        console.error('상담 요청 승인 중 오류 발생:', error);
        alert('상담 요청 승인 중 오류가 발생했습니다.');
    }
}

async function rejectRequest(reqId) {
    const rejectReason = prompt('해당 상담 요청을 반려하시겠습니까? 반려 사유를 입력해주세요.');
    if (rejectReason === null || rejectReason.trim() === '') {
        alert('반려 사유를 입력해야 합니다.');
        return;
    }
    const contextPath = "${pageContext.request.contextPath}";
    const url = `${contextPath}/lms/professor/advising/api/counselingRequests/${reqId}/reject`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ reason: rejectReason })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        alert('상담 요청이 반려되었습니다.');
        const counselingModalElement = document.getElementById('counselingDetailModal');
        const counselingModal = bootstrap.Modal.getInstance(counselingModalElement);
        counselingModal.hide();
        location.reload();
    } catch (error) {
        console.error('상담 요청 반려 중 오류 발생:', error);
        alert('상담 요청 반려 중 오류가 발생했습니다.');
    }
}

// Add event listeners for graduation assignment buttons
document.addEventListener("DOMContentLoaded", () => {
    // ... existing DOMContentLoaded logic ...


});

async function approveGraduationAssignment(graduReqSubmitId) {
    console.log("approveGraduationAssignment called with ID:", graduReqSubmitId);

    const confirmationResult = await Swal.fire({
		target: '#counselingDetailModal',
        title: '졸업 과제 승인',
        text: '해당 졸업 과제를 승인하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#007bff',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '승인',
        cancelButtonText: '취소'
    });

    if (!confirmationResult.isConfirmed) {
        return;
    }

    const contextPath = "${pageContext.request.contextPath}";
    const url = `${contextPath}/lms/professor/advising/graduation/approve`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: "reviewId=" + encodeURIComponent(graduReqSubmitId)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        await Swal.fire({
            title: '승인 완료',
            text: '졸업 과제가 성공적으로 승인되었습니다.',
            icon: 'success',
            confirmButtonColor: '#007bff',
            customClass: {
                popup: 'my-sweetalert-popup'
            }
        });
        location.reload();
    } catch (error) {
        console.error('졸업 과제 승인 중 오류 발생:', error);
        Swal.fire({
            title: '오류 발생',
            text: '졸업 과제 승인 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#EF5350',
            customClass: {
                popup: 'my-sweetalert-popup'
            }
        });
    }
}

async function rejectGraduationAssignment(graduReqSubmitId) {
    console.log("rejectGraduationAssignment called with ID:", graduReqSubmitId);

    const { value: rejectReason } = await Swal.fire({
		target: '#counselingDetailModal',
        title: '졸업 과제 반려',
        input: 'textarea',
        inputLabel: '반려 사유를 입력해주세요.',
        inputPlaceholder: '반려 사유...',
        inputAttributes: {
            'aria-label': 'Type your message here'
        },
        showCancelButton: true,
        confirmButtonColor: '#EF5350',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '반려',
        cancelButtonText: '취소',
        inputValidator: (value) => {
            if (!value) {
                return '반려 사유를 입력해야 합니다.'
            }
        }
    });

    if (!rejectReason) {
        return;
    }

    const contextPath = "${pageContext.request.contextPath}";
    const url = `${contextPath}/lms/professor/advising/graduation/reject`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: "reviewId=" + graduReqSubmitId + "&reason=" + encodeURIComponent(rejectReason)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        await Swal.fire({
            title: '반려 완료',
            text: '졸업 과제가 반려되었습니다.',
            icon: 'success',
            confirmButtonColor: '#007bff',
            customClass: {
                popup: 'my-sweetalert-popup'
            }
        });
        location.reload();
    } catch (error) {
        console.error('졸업 과제 반려 중 오류 발생:', error);
        Swal.fire({
            title: '오류 발생',
            text: '졸업 과제 반려 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonColor: '#EF5350',
            customClass: {
                popup: 'my-sweetalert-popup'
            }
        });
    }
}
</script>
</body>
</html>