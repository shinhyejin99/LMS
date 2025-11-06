<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 학생 관리 상세</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfStudentManageDetail.js"></script>
</head>
<body>
	<section id="student-detail-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-student-no="<c:out value='${studentNo}'/>"
			 data-exam-api="${pageContext.request.contextPath}/classroom/api/v1/professor/${lectureId}/${studentNo}/exam"
			 data-attendance-api="${pageContext.request.contextPath}/classroom/api/v1/professor/${lectureId}/${studentNo}/attendance">

		<c:set var="activeTab" value="students" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex flex-column flex-lg-row gap-3 align-items-lg-center justify-content-between mb-4">
			<div>
				<h1 class="h3 mb-1">학생 관리</h1>
				<div class="text-muted small">학생번호 <span id="student-detail-number"><c:out value='${studentNo}'/></span></div>
			</div>
			<div class="d-flex gap-2 ms-lg-auto">
				<a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/student">
					← 목록으로
				</a>
			</div>
		</div>

		<div id="student-detail-notice" class="alert alert-danger d-none" role="alert"></div>

		<div class="card shadow-sm" id="student-attendance-card">
			<div class="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
				<h2 class="h5 mb-0">출결 기록</h2>
				<div class="text-muted small">출석 기록 <span id="student-attendance-count">0</span>건</div>
			</div>
			<div class="card-body">
				<div id="student-attendance-pivot" class="table-responsive">
					<div class="text-center text-muted py-5">출결 정보를 불러오는 중입니다...</div>
				</div>
				<div class="text-muted small mt-2">
					* 출력 회차는 사용자에게 노출되는 번호입니다.
				</div>
			</div>
		</div>

		<div class="card shadow-sm mt-4" id="student-exam-card">
			<div class="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
				<h2 class="h5 mb-0">시험 응시 현황</h2>
				<div class="text-muted small d-flex flex-column flex-sm-row align-items-sm-center gap-2">
					<span>응시 시험 <span id="student-exam-count">0</span>건</span>
					<span id="student-exam-weight-summary">가중치를 계산 중입니다...</span>
				</div>
			</div>
			<div class="card-body">
				<div class="alert alert-primary d-flex align-items-center justify-content-between" role="status" id="student-exam-final-score">
					<span>최종 시험 점수를 계산하고 있습니다...</span>
				</div>
				<div class="table-responsive">
					<table class="table table-sm align-middle mb-0">
						<thead class="table-light">
							<tr>
								<th scope="col">시험명</th>
								<th scope="col">가중치</th>
								<th scope="col">응시 여부</th>
								<th scope="col">제출 시각</th>
								<th scope="col">점수</th>
								<th scope="col">비고</th>
							</tr>
						</thead>
						<tbody id="student-exam-tbody" data-colspan="6">
							<tr>
								<td colspan="6" class="text-center text-muted py-5">시험 정보를 불러오는 중입니다...</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="text-muted small mt-2">
					* 수정점수가 존재하면 최종점수는 수정점수이고, 없으면 자동채점 점수가 최종점수입니다.
				</div>
			</div>
		</div>
	</section>
</body>
</html>
