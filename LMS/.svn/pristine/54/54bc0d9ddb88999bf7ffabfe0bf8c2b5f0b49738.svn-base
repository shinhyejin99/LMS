<%-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일            수정자     수정내용
 *  ============    ==========  =======================
 *  2025. 10. 8.     송태호      최초 생성 (과제 메인 2열)
 * 2025. 10. 10.     송태호      과제 메인 (개인+조별) 2열 통합
 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 과제</title>

<!-- (선택) 부트스트랩 / 아이콘 사용 시 페이지 공통 레이아웃에서 포함되어 있다고 가정 -->
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap.min.css"> -->
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap-icons.css"> -->

<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfTask.js"></script>
</head>
<body>
	<section id="task-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-api="${pageContext.request.contextPath}/classroom/api/v1/professor/task">

		<!-- 상단 내비 -->
		<c:set var="activeTab" value="task" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<!-- 타이틀 -->
		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">과제</h1>
			<div class="btn-group">
				<button type="button" class="btn btn-sm btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">새 과제</button>
				<ul class="dropdown-menu">
					<li><a class="dropdown-item" href="/classroom/professor/${lectureId}/task/indiv/new" id="btn-new-indiv">개인과제</a></li>
					<li><a class="dropdown-item" href="/classroom/professor/${lectureId}/task/group/new" id="btn-new-group">조별과제</a></li>
				</ul>
			</div>
		</div>

		<!-- 2열 레이아웃 -->
		<div class="row g-4 align-items-start">
			<!-- 개인과제 컬럼 -->
			<div class="col-12 col-xl-6">
				<div class="card shadow-sm">
					<div class="card-header d-flex align-items-center justify-content-between">
						<h2 class="h5 mb-0">개인과제</h2>
						<div class="small text-muted">표시: 과제명 / 시작일 / 마감일 / 등록일</div>
					</div>
					<div class="card-body">

						<!-- 상태 탭 -->
						<ul class="nav nav-pills mb-3" id="indiv-status-tabs" role="tablist">
							<li class="nav-item" role="presentation">
								<button class="nav-link active" id="indiv-all-tab" data-bs-toggle="tab" data-bs-target="#indiv-all" type="button" role="tab">
									전체 <span class="badge bg-secondary ms-1" id="indiv-count-all">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="indiv-upcoming-tab" data-bs-toggle="tab" data-bs-target="#indiv-upcoming" type="button" role="tab">
									시작 전 <span class="badge bg-secondary ms-1" id="indiv-count-upcoming">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="indiv-ongoing-tab" data-bs-toggle="tab" data-bs-target="#indiv-ongoing" type="button" role="tab">
									진행중 <span class="badge bg-secondary ms-1" id="indiv-count-ongoing">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="indiv-closed-tab" data-bs-toggle="tab" data-bs-target="#indiv-closed" type="button" role="tab">
									마감됨 <span class="badge bg-secondary ms-1" id="indiv-count-closed">0</span>
								</button>
							</li>
						</ul>

						<!-- 리스트 -->
						<div class="tab-content" id="indiv-status-content">
							<div id="indiv-all" class="tab-pane fade show active" role="tabpanel" aria-labelledby="indiv-all-tab">
								<div id="indiv-list-all" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="개인과제 전체 페이지 이동">
									<ul id="indiv-pagination-all" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="indiv-upcoming" class="tab-pane fade" role="tabpanel" aria-labelledby="indiv-upcoming-tab">
								<div id="indiv-list-upcoming" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="개인과제 시작 전 페이지 이동">
									<ul id="indiv-pagination-upcoming" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="indiv-ongoing" class="tab-pane fade" role="tabpanel" aria-labelledby="indiv-ongoing-tab">
								<div id="indiv-list-ongoing" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="개인과제 진행중 페이지 이동">
									<ul id="indiv-pagination-ongoing" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="indiv-closed" class="tab-pane fade" role="tabpanel" aria-labelledby="indiv-closed-tab">
								<div id="indiv-list-closed" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="개인과제 마감된 페이지 이동">
									<ul id="indiv-pagination-closed" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- 조별과제 컬럼 -->
			<div class="col-12 col-xl-6">
				<div class="card shadow-sm">
					<div class="card-header d-flex align-items-center justify-content-between">
						<h2 class="h5 mb-0">조별과제</h2>
						<div class="small text-muted">표시: 과제명 / 시작일 / 마감일 / 등록일</div>
					</div>
					<div class="card-body">

						<!-- 상태 탭 -->
						<ul class="nav nav-pills mb-3" id="group-status-tabs" role="tablist">
							<li class="nav-item" role="presentation">
								<button class="nav-link active" id="group-all-tab" data-bs-toggle="tab" data-bs-target="#group-all" type="button" role="tab">
									전체 <span class="badge bg-secondary ms-1" id="group-count-all">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="group-upcoming-tab" data-bs-toggle="tab" data-bs-target="#group-upcoming" type="button" role="tab">
									시작 전 <span class="badge bg-secondary ms-1" id="group-count-upcoming">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="group-ongoing-tab" data-bs-toggle="tab" data-bs-target="#group-ongoing" type="button" role="tab">
									진행중 <span class="badge bg-secondary ms-1" id="group-count-ongoing">0</span>
								</button>
							</li>
							<li class="nav-item" role="presentation">
								<button class="nav-link" id="group-closed-tab" data-bs-toggle="tab" data-bs-target="#group-closed" type="button" role="tab">
									마감됨 <span class="badge bg-secondary ms-1" id="group-count-closed">0</span>
								</button>
							</li>
						</ul>

						<!-- 리스트 -->
						<div class="tab-content" id="group-status-content">
							<div id="group-all" class="tab-pane fade show active" role="tabpanel" aria-labelledby="group-all-tab">
								<div id="group-list-all" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="조별과제 전체 페이지 이동">
									<ul id="group-pagination-all" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="group-upcoming" class="tab-pane fade" role="tabpanel" aria-labelledby="group-upcoming-tab">
								<div id="group-list-upcoming" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="조별과제 시작 전 페이지 이동">
									<ul id="group-pagination-upcoming" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="group-ongoing" class="tab-pane fade" role="tabpanel" aria-labelledby="group-ongoing-tab">
								<div id="group-list-ongoing" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="조별과제 진행중 페이지 이동">
									<ul id="group-pagination-ongoing" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
							<div id="group-closed" class="tab-pane fade" role="tabpanel" aria-labelledby="group-closed-tab">
								<div id="group-list-closed" class="table-responsive">로딩 중…</div>
								<nav class="mt-3" aria-label="조별과제 마감된 페이지 이동">
									<ul id="group-pagination-closed" class="pagination pagination-sm mb-0"></ul>
								</nav>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 공용 알림 박스 -->
		<div id="task-notice-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>
</body>
</html>
