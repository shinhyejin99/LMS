<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 시험</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfExam.js"></script>
</head>
<body>
	<section id="exam-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-api="${pageContext.request.contextPath}/classroom/api/v1/professor/exam">

		<c:set var="activeTab" value="exam" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">시험</h1>
			<div class="d-flex align-items-center gap-2">
								<div class="btn-group">
					<button type="button" class="btn btn-sm btn-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
						시험 등록
					</button>
					<ul class="dropdown-menu">
						<li><a class="dropdown-item" href="/classroom/professor/${lectureId}/exam/offline/new">오프라인 시험</a></li>
						<li><a class="dropdown-item" href="/classroom/professor/${lectureId}/exam/online/new">온라인 시험</a></li>
					</ul>
				</div>
				<button type="button" class="btn btn-sm btn-outline-secondary" id="btn-reload-exam">새로고침</button>
				<button type="button" class="btn btn-sm btn-outline-primary" id="btn-weight-bulk">가중치 일괄수정</button>
			</div>
		</div>

		<div class="card shadow-sm">
			<div class="card-header">
				<h2 class="h5 mb-0">시험 목록</h2>
			</div>
			<div class="card-body">
				<ul class="nav nav-pills mb-3" id="exam-status-tabs" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="exam-tab-all-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-all" type="button" role="tab">
							전체 <span class="badge bg-secondary ms-1" id="exam-count-all">0</span>
						</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="exam-tab-upcoming-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-upcoming" type="button" role="tab">
							응시대기 <span class="badge bg-secondary ms-1" id="exam-count-upcoming">0</span>
						</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="exam-tab-ongoing-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-ongoing" type="button" role="tab">
							진행중 <span class="badge bg-secondary ms-1" id="exam-count-ongoing">0</span>
						</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="exam-tab-closed-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-closed" type="button" role="tab">
							마감 <span class="badge bg-secondary ms-1" id="exam-count-closed">0</span>
						</button>
					</li>
				</ul>

				<div class="tab-content" id="exam-status-content">
					<div class="tab-pane fade show active" id="exam-tab-all" role="tabpanel" aria-labelledby="exam-tab-all-btn">
						<div class="table-responsive">
							<table class="table table-hover table-sm align-middle mb-0">
								<thead class="table-light">
									<tr>
										<th scope="col">시험명</th>
										<th scope="col">유형</th>
										<th scope="col">진행 상태</th>
										<th scope="col">시험 시간</th>
										<th scope="col">응시 현황</th>
										<th scope="col">가중치</th>
										<th scope="col">등록일</th>
									</tr>
								</thead>
								<tbody id="exam-table-all">
									<tr>
										<td colspan="7" class="text-center text-muted py-4">불러오는 중입니다...</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="tab-pane fade" id="exam-tab-upcoming" role="tabpanel" aria-labelledby="exam-tab-upcoming-btn">
						<div class="table-responsive">
							<table class="table table-hover table-sm align-middle mb-0">
								<thead class="table-light">
									<tr>
										<th scope="col">시험명</th>
										<th scope="col">유형</th>
										<th scope="col">진행 상태</th>
										<th scope="col">시험 시간</th>
										<th scope="col">응시 현황</th>
										<th scope="col">가중치</th>
										<th scope="col">등록일</th>
									</tr>
								</thead>
								<tbody id="exam-table-upcoming">
									<tr>
										<td colspan="7" class="text-center text-muted py-4">불러오는 중입니다...</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="tab-pane fade" id="exam-tab-ongoing" role="tabpanel" aria-labelledby="exam-tab-ongoing-btn">
						<div class="table-responsive">
							<table class="table table-hover table-sm align-middle mb-0">
								<thead class="table-light">
									<tr>
										<th scope="col">시험명</th>
										<th scope="col">유형</th>
										<th scope="col">진행 상태</th>
										<th scope="col">시험 시간</th>
										<th scope="col">응시 현황</th>
										<th scope="col">가중치</th>
										<th scope="col">등록일</th>
									</tr>
								</thead>
								<tbody id="exam-table-ongoing">
									<tr>
										<td colspan="7" class="text-center text-muted py-4">불러오는 중입니다...</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<div class="tab-pane fade" id="exam-tab-closed" role="tabpanel" aria-labelledby="exam-tab-closed-btn">
						<div class="table-responsive">
							<table class="table table-hover table-sm align-middle mb-0">
								<thead class="table-light">
									<tr>
										<th scope="col">시험명</th>
										<th scope="col">유형</th>
										<th scope="col">진행 상태</th>
										<th scope="col">시험 시간</th>
										<th scope="col">응시 현황</th>
										<th scope="col">가중치</th>
										<th scope="col">등록일</th>
									</tr>
								</thead>
								<tbody id="exam-table-closed">
									<tr>
										<td colspan="7" class="text-center text-muted py-4">불러오는 중입니다...</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div id="exam-notice-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
	</section>

	<div class="modal fade" id="exam-weight-modal" tabindex="-1" aria-labelledby="exam-weight-modal-label" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h2 class="modal-title h5 mb-0" id="exam-weight-modal-label">시험 가중치 일괄 수정</h2>
					<button type="button" class="btn-close" data-action="close-weight-modal" aria-label="닫기"></button>
				</div>
				<div class="modal-body">
					<div id="exam-weight-error" class="alert alert-danger d-none" role="alert"></div>
					<form id="exam-weight-form" class="vstack gap-3">
						<div class="table-responsive">
							<table class="table table-sm align-middle mb-0">
								<thead class="table-light">
									<tr>
										<th scope="col" style="width: 56px;">#</th>
										<th scope="col">시험명</th>
										<th scope="col" class="text-center" style="width: 200px;">진행 기간</th>
										<th scope="col" class="text-end" style="width: 160px;">가중치</th>
									</tr>
								</thead>
								<tbody id="exam-weight-list">
									<tr>
										<td colspan="4" class="text-center text-muted py-4">시험 목록이 없습니다.</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="d-flex align-items-center justify-content-between">
							<div class="fw-semibold">가중치 합계: <span id="exam-weight-total">0</span></div>
							<div class="text-muted small">모든 가중치 합계는 100이어야 합니다.</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-outline-secondary" data-action="close-weight-modal">취소</button>
					<button type="submit" form="exam-weight-form" class="btn btn-primary" id="exam-weight-save">저장</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
