<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>LMS 교직원 포털 - 학과 관리 대시보드</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/staff/staffDepartmentList.css" />


</head>
<body>
	<div class="department-list-page">
		<div class="department-container">

			<div class="page-header">
				<h1>학과 목록</h1>
			</div>

			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="/staff">Home</a></li>
				<li class="breadcrumb-item">학사업무</li>
				<li class="breadcrumb-item active" aria-current="page">학과 목록</li>
			</ol>

			<h6 class="chart-section-header">
				<i class="bi bi-graph-up"></i> 학과 운영 통계
			</h6>

			<div class="chart-row">
				<div class="chart-card">
					<div class="chart-card-header bg-primary">
						<h6 class="text-white">
							<i class="bi bi-bullseye"></i> 활성 학과 학생/교수 인원 비율
						</h6>
					</div>
					<div class="chart-card-body">
						<div class="chart-container">
							<canvas id="capacityDoughnutChart"></canvas>
						</div>
					</div>
				</div>

				<div class="chart-card">
					<div class="chart-card-header bg-secondary">
						<h6 class="text-white">
							<i class="bi bi-bar-chart-fill"></i> 단과대학별 학과 수
						</h6>
					</div>
					<div class="chart-card-body">
						<div class="chart-container">
							<canvas id="collegeBarChart"></canvas>
						</div>
					</div>
				</div>
			</div>

			<div class="quick-filter-card">
				<div class="card-header">
					<i class="bi bi-funnel-fill"></i> 상태 필터
				</div>
				<div class="card-body">
                    					<div class="filter-search-group">
                                            <ul class="filter-list" id="statusFilterList">
                                                <li
                                                    class="filter-list-item <c:if test="${empty filterType or filterType eq '전체'}">active</c:if>"
                                                    data-status="전체">전체 보기 <span class="badge">${pagingInfo.totalRecord}</span>
                                                </li>
                                                <li
                                                    class="filter-list-item <c:if test="${filterType eq 'ACTIVE'}">active</c:if>"
                                                    data-status="ACTIVE">활성 <span class="badge"
                                                    id="active-dept-count">${activeDeptCount}</span>
                                                </li>
                                                <li
                                                    class="filter-list-item <c:if test="${filterType eq 'DELETED'}">active</c:if>"
                                                    data-status="DELETED">폐지 <span class="badge"
                                                    id="deleted-dept-count">${deletedDeptCount}</span>
                                                </li>
                                            </ul>
                                        </div>
                                        <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 15px;">
                                            <form class="search-form" id="searchForm" style="display: flex; flex-grow: 1; gap: 5px;"
                                                action="${pageContext.request.contextPath}/lms/staff/departments"
                                                method="GET">
                                                <input class="form-control" type="search" name="searchKeyword"
                                                    id="searchInput" placeholder="단과대/학과 통합 검색"
                                                    value="${searchKeyword}">
                                                <button class="btn btn-primary" type="submit" id="searchButton">검색</button>

                                                <input type="hidden" name="page" id="currentPageInput"
                                                    value="${pagingInfo.currentPage}">
                                                <input type="hidden" name="filterType" id="filterTypeInput" value="${filterType}">
                                                <input type="hidden" name="filterGrade" id="filterGradeInput"
                                                    value="${filterGrade}">
                                            </form>
                    						<button type="button" class="btn btn-success"
                    							data-bs-toggle="modal"
                    							data-bs-target="#univDeptRegistrationModal">
                    							<i class="bi bi-journal-plus"></i> 학과 등록
                    						</button>
                    					</div>				</div>
			</div>

			<div class="content-row">
				<div class="content-left">
					<h5 class="filter-header">
						<i class="bi bi-list-ul"></i> 학과 목록 (총 <span
							id="current-list-count">${pagingInfo.totalRecord}</span>개)
					</h5>

					<div class="table-container">
						<table class="data-table" id="univDeptTable">
							<thead>
								<tr>
									<th>단과대</th>
									<th>학과</th>
									<th>학과장</th>
									<th>교수 인원</th>
									<th>학생 인원</th>

									<th>상태</th>
									<th>등록일</th>
								</tr>
							</thead>
							<tbody id="univDeptTableBody">
								<c:choose>
									<c:when test="${not empty staffunivDeptList}">
										<c:forEach items="${staffunivDeptList}" var="univDept"
											varStatus="vs">
											<tr class="univDept-row"
												data-dept-cd="${univDept.univDeptCd}"
												onclick="showDeptDetailModal('${univDept.univDeptCd}');">
												<td>${univDept.collegeName}</td>
												<td>${univDept.univDeptName}</td>
												<td><c:choose>
														<c:when test="${not empty univDept.deptHeadName}">
															${univDept.deptHeadName}
														</c:when>
														<c:otherwise>
															<span class="text-secondary">-</span>
														</c:otherwise>
													</c:choose></td>
												<td><c:choose>
														<c:when
															test="${not empty univDept.professorCount and univDept.professorCount ne 0}">
															${univDept.professorCount}
														</c:when>
														<c:otherwise>
															<span class="text-secondary">-</span>
														</c:otherwise>
													</c:choose></td>
												<td><c:choose>
														<c:when
															test="${not empty univDept.studentCount and univDept.studentCount ne 0}">
															${univDept.studentCount}
														</c:when>
														<c:otherwise>
															<span class="text-secondary">-</span>
														</c:otherwise>
													</c:choose></td>
<%-- 												<td><c:choose> --%>
<%-- 														<c:when --%>
<%-- 															test="${not empty univDept.subjectsCount and univDept.subjectsCount ne 0}"> --%>
<%-- 															${univDept.subjectsCount} --%>
<%-- 														</c:when> --%>
<%-- 														<c:otherwise> --%>
<!-- 															<span class="text-secondary">-</span> -->
<%-- 														</c:otherwise> --%>
<%-- 													</c:choose></td> --%>
												<td><c:choose>
														<c:when test="${empty univDept.deleteAt}">
															<span class="badge bg-success">활성</span>
														</c:when>
														<c:otherwise>
															<span class="badge bg-danger">폐지</span>
														</c:otherwise>
													</c:choose></td>
												<td>${univDept.createAt}</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="8" class="text-center">등록된 학과 정보가 없습니다.</td>
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
						<div class="kpi-card border-primary">
							<div class="kpi-label text-primary">총 학과 수</div>
							<div class="kpi-value" id="kpi-total-univDepts">
								0<span>개</span>
							</div>
						</div>

						<div class="kpi-card border-success">
							<div class="kpi-label text-success">활성 학과 비율</div>
							<div class="kpi-value" id="kpi-active-ratio">
								0.0<span>%</span>
							</div>
						</div>

						<!-- <div class="kpi-card border-danger">
							<div class="kpi-label text-danger">폐지 학과 비율</div>
							<div class="kpi-value" id="kpi-deleted-ratio">
								0.0<span>%</span>
							</div>
						</div> -->

						<div class="kpi-card border-info">
							<div class="kpi-label text-info">총 강의 정원</div>
							<div class="kpi-value" id="kpi-total-capacity">
								0<span>명</span>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>
	<jsp:include page="staffDepartmentDetail_fragment.jsp" />
	<jsp:include page="staffDepartmentEdit_fragment.jsp" />
	<jsp:include page="staffDepartmentCreate_fragment.jsp" />

	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

	<script
		src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js"></script>

    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>

	<c:set var="cleanStaffListJson"
		value="${fn:trim(staffunivDeptListJson)}" />
	<c:set var="cleanAllChartDeptsJson"
		value="${fn:trim(allChartDeptsJson)}" />

	<script>
        // 💡 JAVASCRIPT_DATA 전역 변수 초기화 블록
        var JAVASCRIPT_DATA = {};

        // ⭐ Context Path를 JS 변수에 할당 ⭐
        const contextPath = '${contextPath}';

        // 🚨 [SyntaxError 방지 및 JSON 파싱 최종본]
        const rawStaffList = '${cleanStaffListJson}'.replace(/&quot;/g, '"');
        const rawChartDepts = '${cleanAllChartDeptsJson}'.replace(/&quot;/g, '"');

        const staffunivDeptListJson = (rawStaffList.trim() === '' || rawStaffList.trim() === 'null') ? '[]' : rawStaffList;
        const allChartDeptsJson = (rawChartDepts.trim() === '' || rawChartDepts.trim() === 'null') ? '[]' : rawChartDepts;


        // 💡 페이지 이동 요청인지 확인하는 전역 플래그
        let isPagingRequest = false;

        // ✅ 페이지 이동 함수: 플래그 설정 후 제출
        function pageing(page) {
            const form = document.getElementById('searchForm');
            const pageInput = form.querySelector('input[name="page"]');
            if (!pageInput) {
                console.error("오류: page hidden input을 찾을 수 없습니다.");
                return;
            }
            isPagingRequest = true;
            pageInput.value = page;
            form.submit();
        }

        // 💡 모든 DOM 요소 로드 완료 후 실행
        $(document).ready(function() {

            let listData = [];
            let chartData = [];

            // ✅ 백엔드에서 전달받은 전체 활성/폐지 카운트 변수를 JS로 가져옴
            const totalActive = ${activeDeptCount != null ? activeDeptCount : 0};
            const totalDeleted = ${deletedDeptCount != null ? deletedDeptCount : 0};


            try {
                // 1. 테이블 목록 데이터 파싱 (페이징된 목록)
                if (staffunivDeptListJson.trim() !== '[]') {
                    listData = JSON.parse(staffunivDeptListJson);
                }

                // 2. 차트 통계용 전체 목록 데이터 파싱
                if (allChartDeptsJson.trim() !== '[]') {
                    chartData = JSON.parse(allChartDeptsJson);
                } else {
                    console.warn("차트 통계용 전체 학과 데이터(allChartDeptsJson)가 비어있습니다. 차트가 표시되지 않을 수 있습니다.");
                }

            } catch (e) {
                console.error("Error parsing JSON data for JAVASCRIPT_DATA:", e);
                // 파싱 오류 시 빈 배열로 초기화하여 JS 에러 방지
                listData = [];
                chartData = [];
            }

            // 💡 JAVASCRIPT_DATA 전역 변수에 최종 값 할당
            JAVASCRIPT_DATA = {
                totalRecord: ${pagingInfo.totalRecord != null ? pagingInfo.totalRecord : 0},
                activeCount: totalActive,
                deletedCount: totalDeleted,
                staffunivDeptList: listData,  // 페이징된 목록
                allChartDepts: chartData      // 💡 [핵심] 차트 통계용 전체 목록
            };

            console.log("JAVASCRIPT_DATA 초기화 완료:", JAVASCRIPT_DATA);

            // ----------------------------------------------------
            // KPI 값 반영
            // ----------------------------------------------------
            const totalDepts = JAVASCRIPT_DATA.activeCount + JAVASCRIPT_DATA.deletedCount;
            // 1. 총 학과 수: toLocaleString()으로 쉼표(,) 추가, 기존 span <span>개</span> 유지
            $('#kpi-total-univDepts').html(totalDepts.toLocaleString() + '<span>개</span>');

            // 총 강의 정원 (전체 학과 목록 사용)
            const totalCapacity = JAVASCRIPT_DATA.allChartDepts.reduce((sum, dept) => sum + (Number(dept.capacity) || 0), 0);
            // 2. 총 강의 정원: toLocaleString()으로 쉼표(,) 추가, 기존 span <span>명</span> 유지
            $('#kpi-total-capacity').html(totalCapacity.toLocaleString() + '<span>명</span>');

            if (totalDepts > 0) {
                const activeRatio = (JAVASCRIPT_DATA.activeCount / totalDepts * 100).toFixed(1);
                const deletedRatio = (JAVASCRIPT_DATA.deletedCount / totalDepts * 100).toFixed(1);

                // 3. 활성 학과 비율: toFixed(1)로 소수점 첫째 자리까지 표시하고 <span>%</span> 추가
                $('#kpi-active-ratio').html(activeRatio + '<span>%</span>');

                // 4. 폐지 학과 비율: toFixed(1)로 소수점 첫째 자리까지 표시하고 <span>%</span> 추가
                $('#kpi-deleted-ratio').html(deletedRatio + '<span>%</span>');

            } else {
                $('#kpi-active-ratio').html('0.0<span>%</span>');
                $('#kpi-deleted-ratio').html('0.0<span>%</span>');
            }

            // ----------------------------------------------------
            // 필터/검색 로직
            // ----------------------------------------------------
            $('#searchForm').on('submit', function() {
                if (!isPagingRequest) {
                    $(this).find('input[name="page"]').val(1);
                } else {
                    isPagingRequest = false;
                }
            });

            $('#statusFilterList .filter-list-item').on('click', function() {
                $('#statusFilterList .filter-list-item').removeClass('active');
                $(this).addClass('active');

                const status = $(this).data('status');
                const form = $('#searchForm');

                $('#filterTypeInput').val(status);
                form.find('input[name="page"]').val(1);
                form.submit();
            });

        });
        </script>
	<script src="${pageContext.request.contextPath}/js/app/staff/staffDepartmentList.js"></script>
</body>
</html>
