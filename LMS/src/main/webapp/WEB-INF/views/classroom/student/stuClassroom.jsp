<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸</title>
</head>
<body>
	<section id="stu-lecture-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-common-api="${pageContext.request.contextPath}/classroom/api/v1/common"
			 data-student-api="${pageContext.request.contextPath}/classroom/api/v1/student">

		<c:set var="activeTab" value="dashboard" />
		<%@ include file="/WEB-INF/fragments/classroom/student/nav.jspf" %>

		<header class="mb-4">
			<h1 class="h3 mb-1" id="stu-lecture-title">강의 홈</h1>
			<p class="text-muted mb-0" id="stu-lecture-subtitle">강의 정보를 불러오는 중입니다...</p>
		</header>

		<div id="stu-lecture-alert" class="alert alert-danger d-none" role="alert"></div>

		<div class="row g-4 align-items-stretch mb-4">
			<section id="plan-section" class="col-12 col-xl-8" aria-labelledby="plan-heading">
				<div class="card h-100">
					<div class="card-header d-flex align-items-center justify-content-between">
						<h2 class="h5 mb-0" id="plan-heading">주차별 강의 계획</h2>
					</div>
					<div class="card-body">
						<div class="accordion" id="plan-accordion">
							<div class="accordion-item">
								<h2 class="accordion-header" id="plan-loading-heading">
									<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#plan-loading-body" aria-expanded="true" aria-controls="plan-loading-body">
										주차 정보를 불러오는 중입니다...
									</button>
								</h2>
								<div id="plan-loading-body" class="accordion-collapse collapse show" aria-labelledby="plan-loading-heading" data-bs-parent="#plan-accordion">
									<div class="accordion-body">
										잠시만 기다려 주세요.
									</div>
								</div>
							</div>
						</div>
						<div id="plan-empty" class="text-body-secondary small mt-3 d-none">등록된 강의 계획이 없습니다.</div>
					</div>
				</div>
			</section>

			<div class="col-12 col-xl-4 d-flex flex-column gap-4">
				<section id="overview-section" class="card h-100" aria-labelledby="overview-heading">
					<div class="card-header">
						<h2 class="h6 mb-0" id="overview-heading">강의 개요</h2>
					</div>
					<div class="card-body" id="overview-body">
						강의 정보를 불러오는 중입니다...
					</div>
				</section>

				<section id="schedule-section" class="card h-100" aria-labelledby="schedule-heading">
					<div class="card-header">
						<h2 class="h6 mb-0" id="schedule-heading">강의 시간/강의실</h2>
					</div>
					<div class="card-body" id="schedule-body">
						시간표 정보를 불러오는 중입니다...
					</div>
				</section>
			</div>
		</div>

		<div class="row g-4 align-items-stretch">
			<section id="classmate-section" class="col-12" aria-labelledby="classmate-heading">
				<div class="card h-100">
					<div class="card-header d-flex flex-wrap align-items-center justify-content-between gap-2">
						<h2 class="h6 mb-0" id="classmate-heading">함께 듣는 학우</h2>
						<div class="d-flex align-items-center gap-2">
							<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="classmate-total-badge">0명</span>
							<button type="button" class="btn btn-sm btn-outline-primary" id="open-classmate-modal-btn"
									data-bs-toggle="modal" data-bs-target="#classmateModal">
								학우 목록 보기
							</button>
						</div>
					</div>
					<div class="card-body">
						<div id="classmate-summary" class="text-muted small">
							학우 정보를 불러오는 중입니다...
						</div>
					</div>
				</div>
			</section>
		</div>
	</section>

	<div class="modal fade" id="classmateModal" tabindex="-1" aria-labelledby="classmateModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title h5 mb-0" id="classmateModalLabel">함께 듣는 학우</h3>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
				</div>
				<div class="modal-body" id="classmate-modal-body">
					학우 정보를 불러오는 중입니다...
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>

	<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/student/stuClassroom.js"></script>
</body>
</html>