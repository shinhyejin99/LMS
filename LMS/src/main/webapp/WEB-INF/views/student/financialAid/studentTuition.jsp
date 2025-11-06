<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>등록금 관리</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentTuition.css" />
</head>

<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<!-- 외부 래퍼 -->
	<div class="tuition-management-page">
	    <div class="tuition-container">

	        <!-- 페이지 헤더 추가 -->
	        <div class="page-header">
	            <h1>등록금 및 장학금 통합 조회</h1>
	        </div>

			<!-- 납부 내역 테이블 -->
			<div class="row">
				<div class="col-12">
					<div class="card">
						<div class="card-header">
							<h5 class="card-title mb-0">납부 내역</h5>
						</div>
						<div class="card-body">
							<div class="table-responsive">
								<table class="table table-hover tuition-history-table">
									<thead>
										<tr>
											<th>학기</th>
											<th>등록금</th>
											<th>장학금</th>
											<th>실납부액</th>
											<th>납부일자</th>
											<th>상태</th>
											<th>확인</th>
										</tr>
									</thead>
									<tbody id="paymentTableBody">
										<tr>
											<td colspan="7" class="text-center">
												<div class="spinner-border text-primary" role="status">
													<span class="visually-hidden">Loading...</span>
												</div>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- 장학금 상세 (토글) -->
			<div class="row scholarship-section">
				<div class="col-12">
					<div class="card">
						<div class="card-header">
							<h5 class="card-title mb-0" id="scholarshipTitle">장학금 수혜 상세
								내역</h5>
						</div>
						<div class="card-body">
							<div id="initialMessage" class="text-center text-muted py-5">
								위 납부 내역 중 한 행을 클릭하면, 해당 학기에 대한 장학금 수혜 내역이 표시됨</div>
							<div id="scholarshipDetailContainer"
								class="scholarship-detail-container">
								<!-- 장학금 총액 -->
								<div class="scholarship-summary">
									<p class="mb-2">총 수혜 금액</p>
									<h3 id="totalScholarshipAmount">0원</h3>
								</div>

								<!-- 차트와 상세 내역 -->
								<div class="chart-container">
									<!-- 도넛 차트 -->
									<div class="chart-wrapper">
										<div id="donutChart"></div>
									</div>

									<!-- 범례 및 상세 내역 -->
									<div class="chart-wrapper-desc">
										<h6 class="mb-3">장학금 내역</h6>
										<div id="chartLegend"></div>
									</div>
								</div>

								<!-- 상세 테이블 -->
								<div class="table-responsive">
									<table class="table">
										<thead>
											<tr>
												<th>장학금명</th>
												<th class="text-end">금액</th>
											</tr>
										</thead>
										<tbody id="scholarshipDetailTable">
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 등록금 고지서 모달 -->
		<div id="tuitionModal" class="modal-overlay">
			<div class="modal-container">
				<div class="modal-header">
					<div class="modal-title" id="modalTitle">등록금 납부 고지서</div>
					<button class="close-btn" onclick="closeTuitionModal()">&times;</button>
				</div>
				<div class="modal-body">
					<!-- 워터마크 HTML 요소를 여기에 추가합니다. -->
					<div id="pdfPreviewWatermark">미리보기용</div>
					<iframe id="pdfPreview"></iframe>
				</div>
				<div class="modal-footer">
					<button class="btn btn-primary" id="downloadPdfButton"
						onclick="downloadTuitionPdf()">PDF 다운로드</button>
					<button class="btn btn-secondary" onclick="closeTuitionModal()">닫기</button>
				</div>
			</div>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/app/student/studentTuition.js"></script>
</body>
</html>