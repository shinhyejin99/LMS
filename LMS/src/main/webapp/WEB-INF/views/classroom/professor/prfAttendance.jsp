<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 출석 관리</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfAttendance.js"></script>
<style>
/* 큰 정사각형 선택 카드 버튼 */
.mode-card {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 220px;
	height: 220px;
	border: 1px solid #dee2e6;
	border-radius: 1rem;
	cursor: pointer;
	text-align: center;
	padding: 1rem;
}

.mode-card:hover {
	box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, .075);
}

.mode-card.disabled {
	opacity: .5;
	cursor: not-allowed;
}

.status-pill {
	min-width: 72px
}

#studentsModal .modal-dialog {
	max-width: 90vw;
}

.avatar-cell {
	display: flex;
	align-items: center;
	gap: .5rem;
}

.avatar-96 {
	width: 96px;
	height: 144px;
	border-radius: .5rem;
	object-fit: cover;
}

.avatar-fallback {
	width: var(--avatar-size);
	height: var(--avatar-size);
	border-radius: .5rem;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 600;
	background: #f1f3f5;
	color: #6c757d;
	border: 1px solid #e9ecef;
}

.status-pill.active {
	outline: 2px solid rgba(0, 0, 0, .1);
}
</style>
</head>
<body>
	<section id="attendance-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-lecture-id="<c:out value='${lectureId}'/>" data-api-base="${pageContext.request.contextPath}/classroom/api/v1/professor/attendance">

		<!-- 상단 네비 -->
		<c:set var="activeTab" value="attendance" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">출석 관리</h1>
		</div>

		<!-- 누적 요약 (자리만) -->
		<div class="row g-3 mb-4" id="summary-cards">
			<div class="col-12 col-md-4">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="small text-muted">누적 출석률</div>
						<div id="sum-rate-present" class="display-6">- %</div>
					</div>
				</div>
			</div>
			<div class="col-12 col-md-4">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="small text-muted">누적 결석률</div>
						<div id="sum-rate-absent" class="display-6">- %</div>
					</div>
				</div>
			</div>
			<div class="col-12 col-md-4">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="small text-muted">지각+조퇴율</div>
						<div id="sum-rate-late-early" class="display-6">- %</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 회차 생성 버튼 -->
		<div class="d-flex flex-wrap gap-2 align-items-center mb-4">
			<button id="btn-open-create-modal" class="btn btn-primary">새 출석 회차 생성</button>
		</div>

		<!-- 생성된 회차 목록 -->
		<section aria-labelledby="round-list-h">
			<h2 id="round-list-h" class="h5 mb-2">생성된 출석 회차</h2>
			<div class="table-responsive">
				<table class="table table-sm align-middle" id="round-table">
					<thead class="table-light">
						<tr>
							<th scope="col" style="width: 92px;">출석회차</th>
							<th scope="col" style="width: 140px;">출석일</th>
							<th scope="col">출석</th>
							<th scope="col">결석</th>
							<th scope="col">조퇴</th>
							<th scope="col">지각</th>
							<th scope="col">공결</th>
							<th scope="col">미정</th>
							<th scope="col" style="width: 96px;">관리</th>
						</tr>
					</thead>
					<tbody id="round-tbody">
						<tr>
							<td colspan="9" class="text-center text-muted">로딩 중…</td>
						</tr>
					</tbody>
				</table>
			</div>
		</section>

		<div id="global-alert" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>

	<!-- 모달 #1: 출석 회차 생성 -->
	<div class="modal fade" id="createRoundModal" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">출석 회차 생성</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
				</div>
				<div class="modal-body">
					<div class="d-flex gap-3 flex-wrap justify-content-center mb-3">
						<div id="card-manual" class="mode-card" data-mode="manual" title="미정 상태로 회차를 생성합니다.">
							<div>
								<div class="h4 mb-2">수동 출석</div>
								<div class="text-muted small">기본상태: 미정(TBD)</div>
							</div>
						</div>
						<div id="card-qr" class="mode-card disabled" title="준비중 기능입니다.">
							<div>
								<div class="h4 mb-2">QR 출석</div>
								<div class="text-muted small">준비중</div>
							</div>
						</div>
					</div>

					<div class="d-flex gap-2 justify-content-center">
						<button id="btn-create-all-ok" class="btn btn-sm btn-success">전원 출석 처리</button>
						<button id="btn-create-all-no" class="btn btn-sm btn-outline-danger">전원 결석 처리</button>
					</div>
				</div>
				<div class="modal-footer">
					<button id="btn-create-manual" type="button" class="btn btn-primary">수동 출석(미정) 생성</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 모달 #2: 학생 목록(출석체크 진행) -->
	<div class="modal fade" id="studentsModal" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						출석 체크 <small class="text-muted">#<span id="modal-round-label">-</span></small>
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
				</div>

				<div class="modal-body">
					<!-- 단일 체크 패널 -->
					<div id="checker-panel" class="card mb-3 d-none">
						<div class="card-body">
							<div class="row g-3 align-items-stretch">
								<div class="col-12 col-md-3">
									<div class="d-flex h-100 align-items-center justify-content-center">
										<img id="chk-photo" class="avatar-96 d-block" alt="학생 사진" />
									</div>
								</div>

								<div class="col-12 col-md-9">
									<div class="row g-3 align-items-end">
										<div class="col-12 col-lg-7">
											<div class="d-flex flex-wrap gap-3 align-items-center">
												<div>
													<div class="small text-muted">학번</div>
													<div id="chk-studentNo" class="fw-semibold">-</div>
												</div>
												<div>
													<div class="small text-muted">이름</div>
													<div id="chk-name" class="fw-semibold">-</div>
												</div>
												<div>
													<div class="small text-muted">학년</div>
													<div id="chk-grade" class="fw-semibold">-</div>
												</div>
												<div>
													<div class="small text-muted">학과</div>
													<div id="chk-dept" class="fw-semibold">-</div>
												</div>
											</div>
										</div>

										<div class="col-12 col-lg-5">
											<div class="row g-2">
												<div class="col-6">
													<label for="chk-status" class="form-label small text-muted mb-1">출결 상태</label> <select id="chk-status" class="form-select form-select-sm">
														<option value="ATTD_TBD">미정</option>
														<option value="ATTD_OK">출석</option>
														<option value="ATTD_NO">결석</option>
														<option value="ATTD_EARLY">조퇴</option>
														<option value="ATTD_LATE">지각</option>
														<option value="ATTD_EXCP">공결</option>
													</select>
												</div>
												<div class="col-6">
													<label for="chk-comment" class="form-label small text-muted mb-1">비고</label> <input id="chk-comment" type="text" class="form-control form-control-sm" placeholder="메모">
												</div>
											</div>
										</div>
									</div>

									<div class="d-flex gap-2 mt-3 justify-content-end">
										<button id="btn-prev" type="button" class="btn btn-outline-secondary btn-sm">이전</button>
										<button id="btn-skip" type="button" class="btn btn-outline-secondary btn-sm">건너뛰기</button>
										<button id="btn-apply-next" type="button" class="btn btn-primary btn-sm">적용하고 다음</button>
									</div>
								</div>
							</div>
						</div>
					</div>

					<!-- 명단 표 -->
					<div class="table-responsive">
						<table class="table table-sm table-bordered text-center align-middle">
							<thead class="table-light">
								<tr>
									<th style="width: 64px;">#</th>
									<th>학번</th>
									<th>이름</th>
									<th>학년</th>
									<th>학과</th>
									<th style="width: 160px;">출결</th>
									<th style="width: 240px;">비고</th>
								</tr>
							</thead>
							<tbody id="students-tbody">
								<tr>
									<td colspan="7" class="text-center text-muted">불러오는 중…</td>
								</tr>
							</tbody>
						</table>
					</div>

					<div class="small text-muted">* 위 패널에서 한 명씩 처리하면, 아래 명단이 즉시 갱신됩니다.</div>
				</div>

				<div class="modal-footer">
					<button id="btn-submit-changes" type="button" class="btn btn-primary">제출</button>
					<button id="btn-debug-pending" type="button" class="btn btn-outline-dark">상태 디버깅용</button>
					<button id="btn-close-students" type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
				</div>

			</div>
		</div>
	</div>
</body>
</html>
