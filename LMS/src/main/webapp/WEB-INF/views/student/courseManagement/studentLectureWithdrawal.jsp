<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>전체 학적변동 신청 현황</title>
</head>

<body>
	<%-- <%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%> --%>
	<div class="ssc-wrapper">
		<div class="ssc-page-header">
			<h1 class="ssc-title">수강 포기 신청</h1>
		</div>
		<div class="list-main-section">

			<!-- 상단: 현재 재학 상태 -->
			<div class="student-status-header">
				<div class="status-card">
					<div class="status-info">
						<span class="status-label">학번:</span> <span class="status-value">${studentInfo.studentNo}</span>
					</div>
					<div class="status-info">
						<span class="status-label">이름:</span> <span class="status-value">${studentName}</span>
					</div>
					<div class="status-info">
						<span class="status-label">학과:</span> <span class="status-value">${studentInfo.univDeptName}</span>
					</div>
					<div class="status-info">
						<span class="status-label">학년:</span> <span class="status-value">${studentInfo.gradeName}</span>
					</div>
					<div class="status-info highlight">
						<span class="status-label">현재 학적 상태:</span> <span
							class="status-badge status-${studentInfo.stuStatusCd}">
							${studentInfo.stuStatusName} </span>
					</div>
				</div>
			</div>
			<form action="/lms/student/lecture/withdrawal" method="post">
				<div class="ssc-table-container">
					<table class="ssc-table">
						<thead class="ssc-thead">
							<tr class="ssc-tr">
								<th class="ssc-th">선택</th>
								<th class="ssc-th">학년도학기</th>
								<th class="ssc-th">강의명</th>
								<th class="ssc-th">이수구분</th>
								<th class="ssc-th">학점</th>
								<th class="ssc-th">교수명</th>
							</tr>
						</thead>
						<tbody class="ssc-tbody" id="statusTableBody">
							<c:forEach var="lecture" items="${lectureList}">
								<tr class="ssc-tr">
									<td class="ssc-td"><input type="checkbox" name="selectedLectures" value="${lecture.lectureId}"></td>
									<td class="ssc-td">${lecture.yearTermCd}</td>
									<td class="ssc-td">${lecture.subjectName}</td>
									<td class="ssc-td">${lecture.completionName}</td>
									<td class="ssc-td">${lecture.credit}</td>
									<td class="ssc-td">${lecture.professorName}</td>
								</tr>
							</c:forEach>
							<c:if test="${empty lectureList}">
								<tr class="ssc-tr">
									<td class="ssc-td" colspan="6">수강 중인 강의가 없습니다.</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
				<div class="ssc-button-group">
					<button type="submit" class="ssc-button ssc-button-primary">선택 강의 수강 포기 신청</button>
				</div>
			</form>
		</div>
	</div>
	</div>
	</div>
	</div>

	<%-- <script>
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
</script> --%>
	<%-- <script
		src="<c:url value='/js/app/student/studentLectureWithdrawal.js' />"></script> --%>
</body>
</html>