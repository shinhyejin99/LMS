<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교과목 관리 종합 대시보드</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/staff/staffSubjectList.css" />
<style>
    .filter-list {
        display: flex;
        flex-wrap: nowrap;
        overflow-x: auto;
        padding: 0;
        list-style: none;
        -webkit-overflow-scrolling: touch; /* for smooth scrolling on iOS */
    }
    .filter-list-item {
        margin-right: 10px;
        margin-bottom: 5px;
        flex-shrink: 0; /* prevent items from shrinking */
    }
    /* Optional: custom scrollbar for webkit browsers */
    .filter-list::-webkit-scrollbar {
        height: 5px;
    }
    .filter-list::-webkit-scrollbar-track {
        background: #f1f1f1;
    }
    .filter-list::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 2px;
    }
    .filter-list::-webkit-scrollbar-thumb:hover {
        background: #555;
    }
</style>
</head>
<body>

	<div class="subject-list-page">
		<div class="subject-container">

			<div class="page-header">
				<h1>교과목 목록</h1>
			</div>

			<ol class="breadcrumb mb-3">
				<li class="breadcrumb-item"><a href="/staff">Home</a></li>
				<li class="breadcrumb-item">학사업무</li>
				<li class="breadcrumb-item active" aria-current="page">교과목 목록</li>
			</ol>

			<h6 class="chart-section-header">
				<i class="bi bi-graph-up"></i> 상세 교육 통계
			</h6>

			<div class="chart-row">
				<div class="chart-card">
					<div class="chart-card-header bg-primary">
						<h6 class="text-white">
							<i class="bi bi-pie-chart-fill"></i> 학점/시수 조합별 교과목 수
						</h6>
					</div>
					<div class="chart-card-body">
						<div class="chart-container">
							<canvas id="creditHourScatterChart"></canvas>
						</div>
					</div>
				</div>

				<div class="chart-card">
					<div class="chart-card-header bg-secondary">
						<h6 class="text-white">
							<i class="bi bi-bar-chart-fill"></i> 학과별 교과목 수 비교
						</h6>
					</div>
					<div class="chart-card-body">
						<div class="chart-container">
							<canvas id="departmentBarChart"></canvas>
						</div>
					</div>
				</div>
			</div>

			<div class="quick-filter-card">

				<div class="card-header">
					<i class="bi bi-funnel-fill"></i> 이수 구분 필터
				</div>
				<div class="card-body">
					                    <div class="filter-search-group">
											                        <c:set var="totalSubjectCount" value="0" />
											                        <c:forEach items="${subjectCountMap}" var="countMap">
											                            <c:set var="totalSubjectCount" value="${totalSubjectCount + countMap.COUNT}" />
											                        </c:forEach>
											                        <%-- Debugging logs for totalSubjectCount --%>
											                        <script>
											                            console.log("DEBUG: subjectCountMap:", JSON.parse('<c:out value="${subjectCountMap}" escapeXml="false"/>'));
											                            console.log("DEBUG: totalSubjectCount:", ${totalSubjectCount});
											                        </script>
											                        <ul class="filter-list" id="typeFilterList">
											                            <li style="margin-right: 10px; margin-bottom: 5px;"
											                                class="filter-list-item <c:if test="${empty filterType or filterType eq '전체'}">active</c:if>"
											                                data-type="전체">전체 보기 <span class="badge bg-secondary">${totalSubjectCount}</span>
											                            </li>					                            <c:forEach items="${completionList}" var="code">
													<c:set var="completionCode" value="${code.commonCd}" />
													<c:set var="completionName" value="${code.cdName}" />
					
													<%-- 교과목 수 매칭 로직 --%>
													<c:set var="count" value="0" />
													<c:forEach items="${subjectCountMap}" var="countMap">
														<c:if test="${countMap.TYPE_CODE eq completionCode}">
															<c:set var="count" value="${countMap.COUNT}" />
														</c:if>
													</c:forEach>
					
													<li
														class="filter-list-item <c:if test="${filterType eq completionCode}">active</c:if>"
														data-type="${completionCode}">${completionName}<span
														class="badge">${empty count ? 0 : count}</span>
													</li>
												</c:forEach>
											</ul>
										</div>
										<div style="display: flex; align-items: center; gap: 10px; margin-bottom: 15px;">
					                        <form class="search-form" id="searchForm" style="display: flex; flex-grow: 1; gap: 5px;"
												action="${pageContext.request.contextPath}/lms/staff/staffSubjects"
												method="GET">
												<input class="form-control" type="search" name="searchKeyword"
													id="searchInput" placeholder="교과목 통합 검색"
													value="${searchKeyword}">
												<button class="btn btn-primary" type="submit" id="searchButton">검색</button>
					
												<input type="hidden" name="currentPage" id="currentPageInput"
													value="${pagingInfo.currentPage}"> <input type="hidden"
													name="filterType" id="filterTypeInput" value="${filterType}">
												<input type="hidden" name="filterGrade" id="filterGradeInput"
													value="${filterGrade}">
					                            <input type="hidden" name="filterStatus" id="filterStatusInput"
					                                value="${filterStatus}">
											</form>
											<c:url var="createUrl" value="/lms/staff/staffSubjects/create" />
											<a href="${createUrl}" class="btn btn-success"><i
												class="bi bi-journal-plus"></i> 교과목 등록</a>
										</div>				</div>
			</div>

			<div class="content-row">
				<div class="content-left">
					<h5 class="filter-header">
						<i class="bi bi-list-ul"></i>
						<c:choose>
							<c:when test="${not empty filterType || not empty searchKeyword}">
								필터링된 교과목 목록 (총 ${pagingInfo.totalRecord}개)
							</c:when>
							<c:otherwise>
								전체 교과목 목록 (총 ${pagingInfo.totalRecord}개)
							</c:otherwise>
						</c:choose>
					</h5>

					<div class="table-container">
						<table class="data-table">
							<thead>
								<tr>
									<th class="text-nowrap">소속 학부(과)</th>
									<th class="text-nowrap">학년/학기</th>
									<th class="text-nowrap">이수구분</th>
									<th class="text-nowrap">교과목명</th>
									<th class="text-nowrap">정원</th>
									<th class="text-center">학점</th>
									<th class="text-center">시수</th>
									<th class="text-center">상태</th>
									<th class="text-center">등록일</th>
									</tr>
							</thead>
							<tbody id="subjectTableBody">
								<c:choose>
									<c:when test="${not empty staffSubjectList}">
										<c:forEach items="${staffSubjectList}" var="subject"
											varStatus="vs">
											<tr class="subject-row" data-type="${subject.completionCd}"
												data-bs-toggle="modal" data-bs-target="#subjectDetailModal"
												data-subject-cd="${subject.subjectCd}">
												<td>${subject.univDeptName}</td>
												<td>${(vs.index % 4) + 1}학년${(vs.index % 2 == 0) ? '1학기' : '2학기'}</td>
												<td>${subject.completionName}</td>
												<td>${subject.subjectName}</td>
												<td>${subject.maxCap}</td>
												<td class="text-center">${subject.credit}</td>
												<td class="text-center">${subject.hour}</td>
												<td class="text-center"><c:choose>
														<c:when test="${empty subject.deleteAt}">
															<span class="badge bg-success">활성</span>
														</c:when>
														<c:otherwise>
															<span class="badge bg-danger">폐지</span>
														</c:otherwise>
													</c:choose></td>
												<td class="text-center">${subject.createAt}</td>
												</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="9" class="text-center">등록된 교과목 정보가 없습니다.</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

					<div class="pagination-area">
						<c:set var="baseStyle"
							value="
							padding: 4px 10px;
							margin: 0 4px;
							border-radius: 4px;
							font-weight: 500;
							min-width: 30px;
							text-align: center;
							font-size: 0.9rem;
							text-decoration: none;
							display: inline-block;
							cursor: pointer;
							transition: all 0.2s;
							color: #212529;
							background-color: #ffffff;
							border: 1px solid #dee2e6;
						" />
						<c:if test="${pagingInfo.startPage > 1}">
							<a href="javascript:void(0);"
								onclick="pageing(${pagingInfo.startPage - 1});"
								style="${baseStyle}">&#9664;</a>
						</c:if>
						<c:forEach begin="${pagingInfo.startPage}"
							end="${pagingInfo.endPage}" var="p">
							<c:choose>
								<c:when test="${pagingInfo.currentPage eq p}">
									<a href="javascript:void(0);" onclick="pageing(${p});"
										class="active"
										style="${baseStyle} color: #ffffff; background-color: #007bff; border-color: #007bff;">${p}</a>
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0);" onclick="pageing(${p});"
										style="${baseStyle}">${p}</a>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${pagingInfo.endPage < pagingInfo.totalPage}">
							<a href="javascript:void(0);"
								onclick="pageing(${pagingInfo.endPage + 1});"
								style="${baseStyle}">&#9654;</a>
						</c:if>
					</div>
				</div>

				<div class="content-right">
					<h6>
						<i class="bi bi-bar-chart-fill"></i> 주요 KPI 요약
					</h6>
					<div class="kpi-wrapper">
						<c:set var="totalRecords" value="${pagingInfo.totalRecord}" />

						<%-- 활성 교과목 수 (totalActiveCount)는 컨트롤러에서 이미 계산되어 requestScope에 있을 것으로 가정합니다. --%>
						<c:set var="totalActiveCount"
								value="${requestScope.totalActiveCount ne null ? requestScope.totalActiveCount : 0}" />

						<%-- 비율 계산 --%>
						<c:set var="activeCountRatio" value="0.0" />
						<c:if test="${totalRecords > 0}">
							<%-- EL 연산으로 실수 비율 계산. JSTL fmt:formatNumber가 없으므로 문자열 변환으로 소수점 1자리까지 표시 --%>
							<c:set var="rawActiveRatio" value="${(totalActiveCount / totalRecords) * 100}" />
							<c:set var="activeCountRatio"
								value="${fn:substring(rawActiveRatio, 0, fn:indexOf(rawActiveRatio, '.') + 2)}" />
						</c:if>

						<%-- 평균 학점 (globalAverageCredit)는 컨트롤러에서 이미 계산되어 requestScope에 있을 것으로 가정합니다. --%>
						<c:set var="globalAverageCredit"
							value="${requestScope.globalAverageCredit ne null ? requestScope.globalAverageCredit : 0}" />

						<%-- 평균 학점 소수점 처리 --%>
						<c:set var="displayAverageCredit" value="N/A" />
						<c:if test="${globalAverageCredit ne 0}">
							<c:set var="displayAverageCredit"
								value="${fn:substring(globalAverageCredit, 0, fn:indexOf(globalAverageCredit, '.') + 2)}" />
						</c:if>


						<div class="kpi-card border-primary">
							<div class="kpi-label text-primary">
								<i class="bi bi-book-half"></i> 전체 등록 교과목 수
							</div>
							<div class="kpi-value">${totalRecords}<span>개</span>
							</div>
						</div>

						<div class="kpi-card border-success">
							<div class="kpi-label text-success">
								<i class="bi bi-check-circle-fill"></i> 활성 교과목 비율
							</div>
							<div class="kpi-value">
								<c:choose>
									<c:when test="${totalRecords > 0}">
										${activeCountRatio}
									</c:when>
									<c:otherwise>0.0</c:otherwise>
								</c:choose>
								<span>%</span>
							</div>
						</div>


						<div class="kpi-card border-warning">
							<div class="kpi-label text-warning">
								<i class="bi bi-star-fill"></i> 전체 교과목 평균 학점
							</div>
							<div class="kpi-value">
								<c:choose>
									<c:when test="${not empty globalAverageCredit and globalAverageCredit ne 0}">
										${displayAverageCredit}
									</c:when>
									<c:otherwise>0.0</c:otherwise>
								</c:choose>
								<span>점</span>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
		</div>
	<div class="modal fade" id="subjectDetailModal" tabindex="-1"
		aria-labelledby="subjectDetailModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header bg-light">
					<h5 class="modal-title fw-bold text-dark"
						id="subjectDetailModalLabel">
						<i class="bi bi-info-circle-fill me-2 text-primary"></i> 교과목 상세 정보
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body p-4" id="subjectDetailBody"></div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="subjectModifyModal" tabindex="-1"
		aria-labelledby="subjectModifyModalLabel" aria-hidden="true">
		<div
			class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
			<div class="modal-content">
				<div class="modal-header bg-warning text-white">
					<h5 class="modal-title fw-bold" id="subjectModifyModalLabel">
						<i class="bi bi-pencil-square me-2"></i> 교과목 정보 수정
					</h5>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body p-4" id="subjectModifyBody"></div>
			</div>
		</div>
	</div>


	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

	<script>
    // JSON 파싱 및 전역 변수 설정 (유지)
    const deptCountsJson = '<c:out value="${deptCounts}" escapeXml="false"/>';
    const creditHourDataJson = '<c:out value="${creditHourData}" escapeXml="false"/>';

    window.deptCountsData = [];
    window.creditHourData = [];

    try {
        window.deptCountsData = JSON.parse(deptCountsJson);
    } catch (e) {
        console.error("Dept Counts JSON 파싱 실패. 빈 배열로 초기화:", e);
    }

    try {
        window.creditHourData = JSON.parse(creditHourDataJson);
    } catch (e) {
        console.error("Credit Hour Data JSON 파싱 실패. 빈 배열로 초기화:", e);
    }


    // ⭐ 페이지 이동 함수 ⭐
    function pageing(page) {
        $('#currentPageInput').val(page);
        $('#searchForm').submit();
    }

    $(document).ready(function() {
        $('.subject-row').css('cursor', 'pointer');

        const contextPath = '${pageContext.request.contextPath}';

        // 🌟🌟🌟 [핵심] 성공 메시지 SweetAlert2 알림 🌟🌟🌟
        const successMessage = '${message}';

        if (successMessage && successMessage !== '') {
            Swal.fire({
                icon: 'success',
                title: '✅ 처리 완료',
                text: successMessage,
                confirmButtonText: '확인',
                confirmButtonColor: '#28a745'
            });
        }
        // 🌟🌟🌟 [핵심] 끝 🌟🌟🌟


        // 1. 상세 모달 로직 (유지)
        const detailModalElement = document.getElementById('subjectDetailModal');
        if (detailModalElement) {
            detailModalElement.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const subjectCd = button.getAttribute('data-subject-cd');
                const modalBody = document.getElementById('subjectDetailBody');

                modalBody.innerHTML = `
                    <div class="text-center p-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2 text-muted small">상세 정보를 불러오는 중입니다...</p>
                    </div>
                `;

                // Controller의 상세 Fragment 경로
                $.ajax({
                    url: contextPath + '/lms/staff/staffSubjects/detail/fragment/' + subjectCd,
                    type: 'GET',
                    success: function(response) {
                        modalBody.innerHTML = response;
                    },
                    error: function(xhr) {
                        modalBody.innerHTML = '<div class="alert alert-danger">상세 정보를 불러오는 데 실패했습니다. (Error: ' + xhr.status + ')</div>';
                    }
                });
            });
        }

        // 2. 수정 모달 로직 (유지)
        const modifyModalElement = document.getElementById('subjectModifyModal');
        if (modifyModalElement) {
            modifyModalElement.addEventListener('show.bs.modal', function (event) {

                const button = event.relatedTarget;
                const subjectCd = button.getAttribute('data-subject-cd');
                const modalBody = document.getElementById('subjectModifyBody');

                // 로딩 스피너 표시
                modalBody.innerHTML = `
                    <div class="text-center p-5">
                        <div class="spinner-border text-warning" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2 text-muted small">수정 폼을 불러오는 중입니다...</p>
                    </div>
                `;

                // Controller의 수정 Fragment 경로
                $.ajax({
                    url: contextPath + '/lms/staff/staffSubjects/modify/fragment/' + subjectCd,
                    type: 'GET',
                    success: function(response) {
                        modalBody.innerHTML = response;
                    },
                    error: function(xhr) {
                        modalBody.innerHTML = '<div class="alert alert-danger">수정 폼을 불러오는 데 실패했습니다. (Error: ' + xhr.status + ')</div>';
                    }
                });
            });
        }
    });
    </script>
	<script src="/js/app/staff/staffSubjectList.js"></script>
</body>
</html>