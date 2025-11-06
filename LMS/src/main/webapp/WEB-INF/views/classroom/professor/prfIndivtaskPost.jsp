<!-- 
 * == 수정이력(Modification Information) ==
 *   
 *   수정일       	수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 8.    김현호            최초 작성
 *
-->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 개인과제 보기</title>

<style>
.task-content {
	word-break: break-word;
}

.meta-list {
	gap: .75rem;
}

.meta-item {
	color: #6c757d;
	font-size: .9rem;
}

.badge-type {
	margin-right: .375rem;
}

.skeleton {
	background: #eee;
	border-radius: .5rem;
	min-height: 1rem;
}

#task-action-bar .btn {
	min-width: 64px;
}

.submission-summary-list {
	max-height: 260px;
	overflow-y: auto;
}
</style>

<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfIndivtaskPost.js" defer></script>
</head>
<body>
	<section id="indivtask-post-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-common-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task" data-prof-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task">

		<!-- 네비게이션 -->
		<c:set var="activeTab" value="task" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<!-- 브레드크럼 -->
		<div class="d-flex align-items-center justify-content-between mb-3">
			<nav aria-label="breadcrumb">
				<ol class="breadcrumb mb-0">
					<li class="breadcrumb-item"><a id="bc-task" href="#">과제</a></li>
					<li class="breadcrumb-item active" aria-current="page">개인과제</li>
				</ol>
			</nav>
			<div class="d-flex gap-2">
				<a class="btn btn-sm btn-outline-secondary" id="btn-list" href="#">목록</a>
			</div>
		</div>

		<!-- 과제 카드 -->
		<div class="card shadow-sm">
			<div class="card-body">
				<div class="d-flex align-items-center flex-wrap gap-2 mb-3" id="task-action-bar">
					<div class="btn-group" role="group" aria-label="task actions">
						<a class="btn btn-sm btn-primary" id="btn-edit-task" href="#" role="button">수정</a>
						<button class="btn btn-sm btn-danger" id="btn-delete-task" type="button">삭제</button>
					</div>
				</div>
				<div id="task-body">
					<!-- 로딩 뼈대 -->
					<div class="d-flex align-items-start flex-wrap mb-2">
						<span class="badge skeleton" style="width: 64px; height: 22px;"></span>
						<span class="badge skeleton ms-2" style="width: 56px; height: 22px;"></span>
					</div>
					<h1 class="h4 mb-3 skeleton" style="width: 60%; height: 1.5rem;"></h1>
					<div class="d-flex flex-wrap meta-list mb-3">
						<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
						<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
						<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
					</div>
					<hr class="my-3" />
					<article class="task-content mb-4">
						<div class="skeleton" style="height: 8rem;"></div>
					</article>

					<div id="attach-area" class="mt-4 d-none">
						<h2 class="h6 mb-2">첨부파일</h2>
						<ul class="list-group" id="attach-list"></ul>
					</div>
				</div>
			</div>
		</div>

		<!-- 제출 현황 -->
		<div class="card shadow-sm mt-4">
			<div class="card-header d-flex justify-content-between align-items-center">
				<h2 class="h5 mb-0">제출 현황</h2>
				<span id="submission-rate" class="text-muted fw-bold"></span>
			</div>
			<div class="card-body">
				<div class="row g-3">
					<div class="col-lg-6">
						<div class="accordion" id="submission-accordion-container">
							<p class="text-muted text-center">로딩 중...</p>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="bg-light border rounded p-3 h-100">
							<div class="mb-3">
								<h3 class="h6 mb-2">제출한 사람</h3>
								<ul class="list-group list-group-flush small submission-summary-list" id="submitted-list">
									<li class="list-group-item text-center text-muted">로딩 중...</li>
								</ul>
							</div>
							<div>
								<h3 class="h6 mb-2">제출하지 않은 사람</h3>
								<ul class="list-group list-group-flush small submission-summary-list" id="pending-list">
									<li class="list-group-item text-center text-muted">로딩 중...</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>
</body>
</html>
