<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>수강내역</title>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">

<style>
.ssc-wrapper {
    width: 100%;
    max-width: 100%;
    margin: 0;
    padding: 30px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    min-height: calc(100vh - 100px);
    box-sizing: border-box;
    margin-top: 20px;
    margin-left: 20px;
}

.student-personal-info-page {
    display: flex;
    /* Add any specific styles for this wrapper if needed */
}

.ssc-th {
    text-align: center;
}

.ssc-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px; /* Add some space above the table */
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    overflow: hidden; /* Ensures border-radius applies to children */
}

.ssc-th, .ssc-td {
    padding: 12px 15px;
    vertical-align: middle;
    border-bottom: 1px solid #f0f0f0;
}

.ssc-th {
    background-color: #f8f8f8;
    font-weight: 600;
    color: #333;
    text-align: center; /* Keep this centered as per previous request */
}

.ssc-td {
    color: #555;
    text-align: center; /* Default text alignment for data cells */
}

.ssc-tr:hover {
    background-color: #f5f5f5;
}

/* General button style */
.ssc-btn {
    padding: 6px 12px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 500;
    transition: background-color 0.2s ease;
}

/* Withdraw button specific style */
.withdrawBtn {
    background-color: #dc3545; /* Red */
    color: white;
}

.withdrawBtn:hover {
    background-color: #c82333; /* Darker red on hover */
}

/* Enrollment status badges */
.enroll-status-badge {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 600;
    text-align: center;
    min-width: 60px; /* Ensure consistent width */
}

.status-ing { /* For "수강중" */
    background-color: #e0f2f7; /* Light blue */
    color: #007bff; /* Blue */
}

.status-done { /* For "수강완료" */
    background-color: #d4edda; /* Light green */
    color: #28a745; /* Green */
}

.status-stop { /* For "수강 포기" */
	background-color: #f8d7da; /* Light red background */
	color: #dc3545; /* Darker red text */
 }

/* Pagination styles */
.pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
}

.pagination {
    display: flex;
    list-style: none;
    padding: 0;
    margin: 0;
    border-radius: 5px;
    overflow: hidden;
    border: 1px solid #dee2e6;
}

.page-item {
    margin: 0;
}

.page-link {

    display: block;

    padding: 8px 15px;

    text-decoration: none;

    color: white; /* Changed to white */

    background-color: #4CAF50; /* Changed to #4CAF50 */

    border-right: 1px solid #dee2e6;

    transition: background-color 0.2s ease, color 0.2s ease;

}



.page-item:last-child .page-link {

    border-right: none;

}



.page-link:hover {

    background-color: #45a049; /* Slightly darker green for hover */

    color: white;

}



.page-item.active .page-link {

    color: white;

    background-color: #4CAF50; /* Changed to #4CAF50 */

    border-color: #4CAF50; /* Changed to #4CAF50 */

    cursor: default;

}



.page-item.active .page-link:hover {

    background-color: #4CAF50;

    color: white;

}



</style>
</head>

<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
    <c:set var="paginatedLectureList" value="${paginatedLectureList}" />
    <c:set var="totalElements" value="${paginatedLectureList.totalElements}" />
    <c:set var="currentPage" value="${paginatedLectureList.currentPage}" />
    <c:set var="totalPages" value="${paginatedLectureList.totalPages}" />
    <c:set var="pageSize" value="${paginatedLectureList.pageSize}" />

	<div class="ssc-wrapper">
		<div class="ssc-page-header">
			<h1 class="ssc-title" style="font-size: 23px; font-weight: bold; color: #333; margin: 0;">수강 내역 조회</h1>
		</div>
		<hr>
		<div class="list-main-section">

			<!-- 상단: 현재 재학 상태 -->

			<div class="ssc-table-container">
				<table class="ssc-table">
					<thead class="ssc-thead">
						<tr class="ssc-tr">
							<th class="ssc-th">번호</th>
							<th class="ssc-th">강의명</th>
							<th class="ssc-th">대상학년</th>
							<th class="ssc-th">이수구분</th>
							<th class="ssc-th">학점</th>
							<th class="ssc-th">교수명</th>
							<th class="ssc-th">수강상태</th>
							<th class="ssc-th">수강포기</th>

						</tr>
					</thead>
					<tbody class="ssc-tbody" id="lectureTableBody">
						<c:choose>
							<c:when test="${not empty lectureList}">
								<c:forEach var="lecture" items="${lectureList}"
									varStatus="status">
									<tr class="ssc-tr clickable-row" data-lecture-id="${lecture.lectureId}" data-enroll-status="${lecture.enrollStatusCd}">
										<td class="ssc-td">${(currentPage - 1) * pageSize + status.count}</td>
										<td class="ssc-td">${lecture.subjectName}</td>
										<td class="ssc-td">${lecture.targetGradeNames}</td>
										<td class="ssc-td">${lecture.completionName}</td>
										<td class="ssc-td">${lecture.credit}</td>
										<td class="ssc-td">${lecture.professorName}</td>
										                                        <td class="ssc-td">
																				    <span class="enroll-status-badge ${lecture.enrollStatusCd == 'ENR_ING' ? 'status-ing' : (lecture.enrollStatusCd == 'ENR_DONE' ? 'status-done' : (lecture.enrollStatusCd == 'ENR_DROP' ? 'status-stop' : ''))}">
																				        ${lecture.enrollStatusName}
																				    </span>
																				</td>										<td class="ssc-td">
										    <c:if test="${lecture.enrollStatusCd == 'ENR_ING'}">
										        <button type="button" class="withdrawBtn ssc-btn" data-lecture-id="${lecture.lectureId}" data-enroll-status="${lecture.enrollStatusCd}">수강 포기</button>
										    </c:if>
										</td>									</tr>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<tr class="ssc-tr">
									<td class="ssc-td" colspan="8">수강 내역이 없습니다.</td>
								</tr>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
			</div>
            <div class="pagination-container">
                <ul class="pagination">
                    <c:if test="${currentPage > 1}">
                        <li class="page-item"><a class="page-link" href="?page=${currentPage - 1}&size=${pageSize}">이전</a></li>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <li class="page-item ${currentPage == i ? 'active' : ''}"><a class="page-link" href="?page=${i}&size=${pageSize}">${i}</a></li>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <li class="page-item"><a class="page-link" href="?page=${currentPage + 1}&size=${pageSize}">다음</a></li>
                    </c:if>
                </ul>
            </div>
		</div>
	</div>


	</div>
	</div>
	</div>

	</div>
	</div>
	<script>
	// applyStatusCodes 변수를 전역으로 선언하여 외부 JS 파일에서 접근 가능하게 합니다.
	<c:if test="${not empty applyList}">
	    var applyStatusCodes = [
	        <c:forEach var="apply" items="${applyList}" varStatus="status">
	            { commonCd: "${apply.commonCd}", cdName: "${apply.cdName}" }${!status.last ? ',' : ''}
	        </c:forEach>
	    ];
	</c:if>

	// 안전을 위해 변수가 정의되지 않았으면 빈 배열로 초기화합니다.
	if (typeof applyStatusCodes === 'undefined') {
	    var applyStatusCodes = [];
	}

	document.getElementById('lectureTableBody').addEventListener('click', function(event) {
	    if (event.target.classList.contains('withdrawBtn')) {
	        const button = event.target;
	        const lectureId = button.dataset.lectureId;
	        const enrollStatusCd = button.dataset.enrollStatus;

	        if (enrollStatusCd === 'ENR_DONE') {
	            Swal.fire({
	                title: '수강 포기 불가',
	                text: '이미 수강 완료된 강의는 포기할 수 없습니다.',
	                icon: 'error',
	                confirmButtonColor: '#3085d6',
	                confirmButtonText: '확인'
	            });
	            return;
	        }

	        Swal.fire({
	            title: '정말 수강 포기하시겠습니까?',
	            text: '수강 포기한 강의는 되돌릴 수 없습니다.',
	            icon: 'warning',
	            showCancelButton: true,
	            confirmButtonColor: '#d33',
	            cancelButtonColor: '#3085d6',
	            confirmButtonText: '수강 포기',
	            cancelButtonText: '취소'
	        }).then((result) => {
	            if (result.isConfirmed) {
	                fetch('/lms/student/lecture/drop', {
	                    method: 'POST',
	                    headers: {
	                        'Content-Type': 'application/json',
	                    },
	                    body: JSON.stringify({ lectureId: lectureId })
	                })
	                .then(response => {
	                    if (response.ok) {
	                        return response.text();
	                    } else {
	                        return response.text().then(errorText => {
	                            throw new Error(errorText);
	                        });
	                    }
	                })
	                .then(message => {
	                    Swal.fire({
	                        title: '성공',
	                        text: message,
	                        icon: 'success',
	                        confirmButtonColor: '#3085d6',
	                        confirmButtonText: '확인'
	                    }).then(() => {
	                        location.reload();
	                    });
	                })
	                .catch(error => {
	                    console.error('Error:', error);
	                    Swal.fire({
	                        title: '오류',
	                        text: error.message || '수강 포기 중 오류가 발생했습니다.',
	                        icon: 'error',
	                        confirmButtonColor: '#3085d6',
	                        confirmButtonText: '확인'
	                    });
	                });
	            }
	        });
	    }
	});

    // Modal script
    const modal = document.getElementById('detailModal');
    const tableBody = document.getElementById('lectureTableBody');
    const closeBtn = document.querySelector('.close');

    tableBody.addEventListener('click', function(event) {
        const row = event.target.closest('.clickable-row');
        if (!row) return;

        // Prevent checkbox click from opening modal
        if (event.target.type === 'checkbox') {
            return;
        }

        const lectureId = row.dataset.lectureId;
        fetch(`/lms/student/lecture/${lectureId}`)
            .then(response => response.json())
            .then(data => {
                document.getElementById('modalSubjectName').textContent = data.subjectName;
                document.getElementById('modalCreditHour').textContent = `${data.credit}학점 / ${data.hour}시간`;
                document.getElementById('modalLectureRoom').textContent = data.lectureRoom || '미배정';
                document.getElementById('modalMaxCap').textContent = `${data.maxCap}명`;
                document.getElementById('modalProfessorName').textContent = data.professorName;
                document.getElementById('modalCompletionName').textContent = data.completionName;
                document.getElementById('modalLectureTime').textContent = data.lectureTime || '미배정';
                document.getElementById('modalLectureGoal').textContent = data.lectureGoal || '내용 없음';

                const modalGradeHeader = document.getElementById('modalGradeHeader');
                const modalFinalGrade = document.getElementById('modalFinalGrade');
                if (data.finalGrade !== null && data.finalGrade !== undefined) {
                    modalFinalGrade.textContent = data.finalGrade.toFixed(2);
                    modalGradeHeader.style.display = 'table-cell';
                    modalFinalGrade.style.display = 'table-cell';
                } else {
                    modalGradeHeader.style.display = 'none';
                    modalFinalGrade.style.display = 'none';
                }

                modal.style.display = 'block';
            })
            .catch(error => console.error('Error fetching lecture details:', error));
    });

    closeBtn.onclick = function() {
        modal.style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
</script>
	<%-- <script src="<c:url value='/js/app/student/studentLectureWithdrawal.js' />"></script> --%>
</body>
</html>