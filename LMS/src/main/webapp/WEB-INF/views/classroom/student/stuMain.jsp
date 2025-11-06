<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>JSU 클래스룸</title>
	<script type="text/javascript" defer src="${pageContext.request.contextPath}/js/app/classroom/student/stuMain.js"></script>
	<style>
		.stu-card-professor {
			width: 120px;
			aspect-ratio: 3 / 4;
			object-fit: cover;
			object-position: center center;
			background-color: #f8f9fa;
			border: 1px solid rgba(0, 0, 0, 0.08);
			border-radius: 0.5rem;
			box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.03);
		}

		@media (max-width: 575.98px) {
			.stu-card-professor {
				width: 96px;
			}
		}
	</style>
</head>
<body>
	<div id="stu-main-root" class="container my-4"
		 data-ctx="${pageContext.request.contextPath}"
		 data-api="${pageContext.request.contextPath}/classroom/api/v1/student/mylist">
		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">내 강의 현황</h1>
			<div class="text-muted small" id="stu-lecture-summary" aria-live="polite"></div>
		</div>
		<div id="stu-error-box" class="alert alert-danger d-none" role="alert"></div>

		<section class="accordion-section mb-4">
			<div class="d-flex align-items-center justify-content-between">
				<h2 class="h5 mb-0">진행중인 강의</h2>
				<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="stu-ongoing-count">0개</span>
			</div>
			<div class="accordion mt-2" id="stu-accordion-ongoing"></div>
		</section>

		<section class="accordion-section">
			<div class="d-flex align-items-center justify-content-between">
				<h2 class="h5 mb-0">종료된 강의</h2>
				<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="stu-ended-count">0개</span>
			</div>
			<div class="accordion mt-2" id="stu-accordion-ended"></div>
		</section>
	</div>
</body>
</html>
