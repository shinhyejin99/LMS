<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 게시글</title>

<style>
.board-layout {
	max-width: 1040px;
}

@media (max-width: 1200px) {
	.board-layout {
		max-width: 100%;
	}
}

.post-content {
	word-break: break-word;
}

.meta-list {
	gap: 0.75rem;
}

.meta-item {
	color: #6c757d;
	font-size: 0.9rem;
}

.badge-type {
	margin-right: 0.375rem;
}

.skeleton {
	background: #eee;
	border-radius: 0.5rem;
	min-height: 1rem;
}

.reply-item.reply-child {
	background-color: #f8f9fa;
}

.reply-item.reply-professor {
	background-color: #eef1f4;
	border-color: #d0d5dd;
}

.reply-item.selected {
	border-color: #0d6efd;
	box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.15);
}

.reply-item.reply-highlight {
	border-color: #0d6efd;
	box-shadow: 0 0 0.6rem rgba(13, 110, 253, 0.25);
	animation: replyGlow 2.4s ease-out;
}

.reply-textarea {
	resize: none;
}

.reply-body p {
	margin-bottom: 0.25rem;
}

.reply-body p:last-child {
	margin-bottom: 0;
}

@keyframes replyGlow {
	0% {
		background-color: rgba(13, 110, 253, 0.18);
	}
	100% {
		background-color: transparent;
	}
}

.post-header-actions {
	display: none;
}
</style>

<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/student/stuBoardPost.js" defer></script>
</head>
<body>
	<section id="board-post-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-api-base="${pageContext.request.contextPath}/classroom/api/v1/student/board">

		<c:set var="activeTab" value="board" />
		<%@ include file="/WEB-INF/fragments/classroom/student/nav.jspf" %>

		<div class="board-layout mx-auto">
			<div class="d-flex align-items-center justify-content-between mb-3">
				<nav aria-label="breadcrumb">
					<ol class="breadcrumb mb-0">
						<li class="breadcrumb-item"><a id="bc-board" href="#">게시판</a></li>
						<li class="breadcrumb-item active" aria-current="page">상세</li>
					</ol>
				</nav>
				<div class="d-flex gap-2">
					<a class="btn btn-sm btn-outline-secondary" id="btn-list" href="#">목록</a>
				</div>
			</div>

			<div class="card shadow-sm mb-4">
				<div class="card-body" id="post-body">
					<div class="d-flex align-items-start justify-content-between gap-3 mb-3 flex-wrap" id="post-header">
						<div class="flex-grow-1">
							<div class="d-flex align-items-center flex-wrap gap-2 mb-2" id="post-badge-row">
								<span class="badge skeleton" style="width: 72px; height: 24px;"></span>
								<span class="badge skeleton" style="width: 56px; height: 24px;"></span>
							</div>
							<h1 class="h4 mb-2 skeleton" id="post-title" style="width: 70%; height: 1.75rem;"></h1>
							<div class="d-flex flex-wrap meta-list mb-0" id="post-meta-row">
								<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
								<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
								<div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
							</div>
						</div>
						<div class="d-flex align-items-start gap-2 post-header-actions">
							<!-- 학생 화면에서는 수정/삭제 버튼이 노출되지 않습니다 -->
						</div>
					</div>
					<hr class="my-3" />
					<article class="post-content mb-4" id="post-content">
						<div class="skeleton" style="height: 8rem;"></div>
					</article>

					<div id="attach-area" class="mt-4 d-none">
						<h2 class="h6 mb-2">첨부파일</h2>
						<ul class="list-group" id="attach-list"></ul>
					</div>
				</div>
			</div>

			<section class="mt-5" id="reply-section">
				<div class="card shadow-sm">
					<div class="card-header d-flex align-items-center justify-content-between gap-2 flex-wrap">
						<h2 class="h5 mb-0">댓글</h2>
						<div class="btn-group btn-group-sm" role="group" aria-label="댓글 정렬">
							<button type="button" class="btn btn-primary active" id="reply-sort-newest" data-sort="desc" aria-pressed="true">최신순</button>
							<button type="button" class="btn btn-outline-secondary" id="reply-sort-oldest" data-sort="asc" aria-pressed="false">오래된순</button>
						</div>
					</div>
					<div class="card-body">
						<form id="reply-form" class="vstack gap-3 mb-4">
							<div>
								<label for="reply-content" class="form-label">댓글 내용</label>
								<textarea class="form-control reply-textarea" id="reply-content" name="replyContent" rows="3" placeholder="댓글을 입력해 주세요."></textarea>
							</div>
							<div class="d-flex justify-content-end">
								<button type="submit" class="btn btn-primary btn-sm" id="reply-submit">등록</button>
							</div>
						</form>

						<hr class="my-4" />

						<div class="vstack gap-1">
							<div id="reply-empty" class="text-muted small text-center py-4">댓글이 아직 없습니다. 첫 댓글을 남겨보세요.</div>
							<div id="reply-list" class="vstack gap-1"></div>
						</div>

						<div id="reply-alert" class="alert mt-4 d-none" role="alert"></div>
					</div>
				</div>
			</section>

			<div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
		</div>
	</section>
</body>
</html>
