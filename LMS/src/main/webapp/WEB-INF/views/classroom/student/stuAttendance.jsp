<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 출결 현황</title>
</head>
<body>
	<section id="attendance-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-student-api="${pageContext.request.contextPath}/classroom/api/v1/student">

		<c:set var="activeTab" value="attendance" />
		<%@ include file="/WEB-INF/fragments/classroom/student/nav.jspf" %>

		<div class="row justify-content-center">
			<div class="col-lg-10 col-xl-8">
				<header class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-4">
					<div>
						<h1 class="h3 mb-1">출결 현황</h1>
						<p class="text-muted mb-0">나의 전체 출결 기록을 확인합니다.</p>
					</div>
					<div id="attendance-total-badge" class="badge bg-primary-subtle text-primary-emphasis fs-6 rounded-pill"></div>
				</header>

				<div id="attendance-alert" class="alert alert-danger d-none" role="alert"></div>

				<div class="card shadow-sm">
					<div class="card-body" id="attendance-body">
						<div class="text-center text-muted py-5">
							<div class="spinner-border" role="status">
								<span class="visually-hidden">Loading...</span>
							</div>
							<p class="mt-2 mb-0">출결 정보를 불러오는 중입니다...</p>
						</div>
					</div>
				</div>
			</div>
		</div>

	</section>

	<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/student/stuAttendance.js"></script>
</body>
</html>