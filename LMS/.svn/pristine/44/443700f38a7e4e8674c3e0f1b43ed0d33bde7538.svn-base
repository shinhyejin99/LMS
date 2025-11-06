<%-- 
 * == 개정이력(Modification Information) ==
 *   수정일          수정자     수정내용
 *  2025. 9.30.      송태호     최초 생성
 *  2025.10.02.      송태호     링크 추가
 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸</title>
</head>
<body>
	<section id="lecture-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-lecture-id="<c:out value='${lectureId}'/>">

		<c:set var="activeTab" value="dashboard" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<h1 class="h3 mb-4">강의 상세</h1>

		<!-- 주차 계획(좌 2/3) + 시간표(우 1/3) -->
		<div class="row g-4">
			<!-- 좌측: 주차별 계획 -->
			<section id="plan-section" class="col-12 col-lg-8" aria-labelledby="plan-h">
				<div class="d-flex align-items-center justify-content-between mb-2">
					<h2 id="plan-h" class="h5 mb-0">주차별 계획</h2>
					<div class="ms-3" style="min-width: 200px;">
						<label for="plan-select" class="form-label visually-hidden">주차 선택</label> <select id="plan-select" class="form-select form-select-sm"></select>
					</div>
				</div>

				<div id="plan-detail" class="card">
					<div class="card-body">로딩 중…</div>
				</div>

				<div id="plan-empty" class="text-body-secondary small mt-2 d-none">등록된 주차 계획이 없습니다.</div>
			</section>

			<!-- 우측: 강의 시간표 -->
			<section id="schedule-section" class="col-12 col-lg-4" aria-labelledby="schedule-h">
				<h2 id="schedule-h" class="h5 mb-2">강의 시간/강의실</h2>
				<div id="schedule-box" class="card">
					<div class="card-body">로딩 중…</div>
				</div>
			</section>
		</div>

		<!-- 수강생 목록: 버튼 + 모달 (한 벌만 유지) -->
		<section id="trainee-section" class="mt-4" aria-labelledby="trainee-h">
			<div class="d-flex align-items-center justify-content-between mb-2">
				<h2 id="trainee-h" class="h5 mb-0">수강생 목록</h2>
				<!-- 목록 열기 버튼 (로딩 후 건수 표기) -->
				<button type="button" id="open-trainee-modal-btn" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#traineeModal">수강생 보기</button>
			</div>

			<!-- 더 이상 카드로 본문에 표를 직접 렌더링하지 않음 -->
			<div id="trainee-box" class="d-none">로딩 중…</div>
		</section>

		<!-- Bootstrap Modal (한 벌만 유지) -->
		<div class="modal fade" id="traineeModal" tabindex="-1" aria-labelledby="traineeModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg modal-dialog-scrollable">
				<div class="modal-content">
					<div class="modal-header">
						<h3 id="traineeModalLabel" class="modal-title h5">수강생 목록</h3>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
					</div>
					<div class="modal-body">
						<div id="trainee-modal-body">
							<!-- JS에서 표 렌더링 -->
							<div class="text-body-secondary">로딩 중…</div>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">닫기</button>
					</div>
				</div>
			</div>
		</div>

		<div id="notfound-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>

	<script src="${pageContext.request.contextPath}/js/app/classroom/professor/prfClassroom.js"></script>
</body>
</html>
