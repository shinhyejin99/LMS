<%-- 
 * == 수정이력(Modification Information) ==
 *   
 *   수정일자      수정자           수정내용
 *  ============  ============== =======================
 *  2025. 10. 16.    Codex          최초 생성
 *
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 게시판</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/student/stuBoard.js" defer></script>
</head>
<body>
	<section id="board-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-lecture-id="<c:out value='${lectureId}'/>" data-api-base="${pageContext.request.contextPath}/classroom/api/v1/student/board">

		<c:set var="activeTab" value="board" />
		<%@ include file="/WEB-INF/fragments/classroom/student/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">게시판</h1>
			
			<div class="d-flex align-items-center gap-2">
				<!-- 필터: 타입/검색 -->
				<select id="filter-type" class="form-select form-select-sm" style="min-width: 120px;">
					<option value="">전체 유형</option>
					<option value="NOTICE">공지사항</option>
					<option value="MATERIAL">자료</option>
				</select>
				<div class="input-group input-group-sm" style="min-width: 220px;">
					<input id="search-q" type="text" class="form-control" placeholder="제목/내용 검색" />
					<button id="btn-search" class="btn btn-outline-secondary">검색</button>
				</div>
			</div>
		</div>
	
		<!-- 탭과 리스트(공지 및 자료를 포함한 전체 뷰) -->
		<section class="mt-4" aria-labelledby="tablist-h">
			<h2 id="tablist-h" class="h5 mb-2">게시글 목록</h2>
			
			<ul class="nav nav-tabs" id="post-tabs" role="tablist">
				<li class="nav-item" role="presentation">
					<button class="nav-link active" id="tab-all" data-bs-toggle="tab" data-bs-target="#pane-all" type="button" role="tab">전체</button>
				</li>
				<li class="nav-item" role="presentation">
					<button class="nav-link" id="tab-notice" data-bs-toggle="tab" data-bs-target="#pane-notice" type="button" role="tab">공지사항</button>
				</li>
				<li class="nav-item" role="presentation">
					<button class="nav-link" id="tab-material" data-bs-toggle="tab" data-bs-target="#pane-material" type="button" role="tab">자료</button>
				</li>
			</ul>
			<div class="tab-content border border-top-0 rounded-bottom">
				<div id="pane-all" class="tab-pane fade show active p-3" role="tabpanel" aria-labelledby="tab-all">
					<div id="list-all">로딩 중...</div>
				</div>
				<div id="pane-notice" class="tab-pane fade p-3" role="tabpanel" aria-labelledby="tab-notice">
					<div id="list-notice">로딩 중...</div>
				</div>
				<div id="pane-material" class="tab-pane fade p-3" role="tabpanel" aria-labelledby="tab-material">
					<div id="list-material">로딩 중...</div>
				</div>
			</div>
		</section>

		<!-- 페이지네이션 -->
		<nav class="mt-3" aria-label="게시글 페이지 이동">
			<ul id="pagination" class="pagination pagination-sm mb-0">
				<!-- JS에서 구성 -->
			</ul>
		</nav>

		<div id="notfound-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>
</body>
</html>
