<%-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일              수정자           수정내용
 *  ============    ============== =======================
 *  2025. 9. 29.         송태호            최초 생성
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>JSU 클래스룸</title>
	<script type="text/javascript" defer
			src="${pageContext.request.contextPath}/js/app/classroom/professor/prfMain.js"></script>
</head>
<body>
	<div id="prof-main-root" class="container my-4"
		 data-ctx="${pageContext.request.contextPath}"
		 data-api="${pageContext.request.contextPath}/classroom/api/v1/professor/mylist">
		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">내 강의</h1>
			<div class="text-muted small" id="lecture-summary" aria-live="polite"></div>
		</div>
		<div id="error-box" class="alert alert-danger d-none" role="alert"></div>
		<section class="accordion-section mb-4">
			<div class="d-flex align-items-center justify-content-between">
				<h2 class="h5 mb-0">진행중인 강의</h2>
				<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="ongoing-count">0개</span>
			</div>
			<div class="accordion mt-2" id="accordion-ongoing"></div>
		</section>
		<section class="accordion-section">
			<div class="d-flex align-items-center justify-content-between">
				<h2 class="h5 mb-0">종강된 강의</h2>
				<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="ended-count">0개</span>
			</div>
			<div class="accordion mt-2" id="accordion-ended"></div>
		</section>
	</div>

</body>
</html>
