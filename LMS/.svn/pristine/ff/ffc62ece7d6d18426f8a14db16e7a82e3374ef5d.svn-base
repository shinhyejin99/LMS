<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교직원 관리 - 교직원 목록</title>
<!-- 	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> -->
<!-- 	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"> -->

<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" /> --%>
<link rel="stylesheet" href="<c:url value='/css/staffinfoList.css' />">
<style>
    .action-buttons {
        display: flex;
        align-items: center;
        justify-content: flex-end; /* Align the search form to the right */
        flex-wrap: wrap; /* Allow items to wrap on smaller screens */
        margin-bottom: 20px; /* Add some space below the action buttons */
    }

    .action-buttons .search-form {
        display: flex;
        align-items: center;
        gap: 5px; /* Space between search input, search button, and registration button */
        margin-left: auto; /* Push the search form to the right */
    }

    .action-buttons .search-form #searchInput {
        width: auto !important; /* Override inline style */
        flex-grow: 1; /* Allow input to grow */
        max-width: 250px; /* Keep a reasonable max-width */
        height: 38px; /* Standardize height with buttons */
    }

    .action-buttons .search-form .btn,
    .action-buttons .search-form a.btn {
        height: 38px; /* Standardize height with input */
        display: flex;
        align-items: center;
        justify-content: center;
        white-space: nowrap; /* Prevent text wrapping inside buttons */
        padding: 0 10px; /* Further adjust horizontal padding for compactness */
        font-size: 0.9em; /* Reduce font size */
        max-width: 100px; /* Further reduce the maximum width for the buttons */
        overflow: hidden; /* Hide overflowing text */
        text-overflow: ellipsis; /* Add ellipsis for overflowing text */
    }
</style>

</head>
<body>

	<!-- 외부 래퍼 추가 -->
	<div class="staff-list-page">
		<div class="staff-container">

			<!-- 페이지 헤더 추가 -->
			<div class="page-header">
				<h1>교직원 목록</h1>
			</div>

			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="/staff">Home</a></li>
				<li class="breadcrumb-item">인사업무</li>
				<li class="breadcrumb-item active" aria-current="page">교직원 목록</li>

			</ol>



			<div class="status-cards-row" id="status-cards-container">
				<c:set var="deptLabels" value="인사처,행정처" />
				<c:set var="deptCodes" value="STF-HR,STF-ADM" />
				<c:set var="deptIcons" value="building-check,building-fill" />
				<c:set var="deptColors" value="primary,warning" />

				<c:forTokens items="${deptLabels}" delims="," var="label"
					varStatus="i">
					<c:set var="icon" value="${fn:split(deptIcons, ',')[i.index]}" />
					<c:set var="color" value="${fn:split(deptColors, ',')[i.index]}" />
					<c:set var="code" value="${fn:split(deptCodes, ',')[i.index]}" />

					<div class="status-card" data-dept-cd="${code}">
						<div class="status-card-body">
							<div>
								<div class="status-card-title text-${color}">${label}</div>
								<div class="status-card-count" data-count-target="${label}">
									<c:choose>
										<c:when test="${not empty employmentCountsMap[label]}">
                                ${employmentCountsMap[label]}
                            </c:when>
										<c:otherwise>0</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div>
								<i class="bi bi-${icon} status-card-icon"></i>
							</div>
						</div>
					</div>
				</c:forTokens>
			</div>

			<%-- 👇👇👇 노란색 메모지 스타일 및 기능 안내만 적용 👇👇👇 --%>
			<%-- 👆👆👆 사용자 요청 메모 끝 👆👆👆 --%>

			<div class="content-row">
				<div class="content-left">
					<div class="custom-notice-box">
						<h6>
							<i class="bi bi-lightbulb-fill"></i> 목록 필터링 기능 안내
						</h6>
						<ul>
							<li>상단의 '부서별 카드'를 클릭하시면 해당 부서에 소속된 교직원만 자동으로 목록에 필터링되어 표시
								됩니다.</li>
							<li>필터링된 상태에서 검색어를 입력하여 추가적인 조건 검색을 진행할 수 있습니다.</li>
						</ul>
					</div>

					<div class="action-buttons">
						<form class="search-form" id="searchForm"
							action="${pageContext.request.contextPath}/lms/staffs"
							method="GET">
							<input class="form-control" type="search" name="searchKeyword"
								id="searchInput" placeholder="통합 검색 (이름, 사번 등)"
								value="${searchKeyword}">
							<button class="btn btn-primary" type="button" id="searchButton"
								onclick="handleSearchSubmit()">검색</button>
							<c:url var="createUrl" value="/lms/staffs/create" />
							<a href="${createUrl}" class="btn btn-success" role="button">
								<i class="bi bi-person-plus"></i>교직원 등록
							</a>

							<input type="hidden" name="filterDeptName" id="filterDeptName" value="${filterDeptName}"> <input type="hidden"
								name="filterStfDeptCd" id="filterStatusInput"
								value="${filterStfDeptCd}">
						</form>
					</div>

					<h5 class="filter-header" id="filter-header">
						<i class="bi bi-list-ul"></i>
						<c:choose>
							<c:when
								test="${not empty filterStfDeptCd || not empty searchKeyword}">
                    필터링된 교직원 목록 (총 ${pagingInfo.totalRecord}명)
                </c:when>
							<c:otherwise>
                    전체 교직원 목록 (총 ${pagingInfo.totalRecord}명)
                </c:otherwise>
						</c:choose>
					</h5>

					<div class="table-responsive">
						<table class="data-table" id="staffTable">
							<thead>
								<tr>
									<th>부서</th>
									<th>사번</th>
									<th>이름</th>
									<th>연락처</th>
									<th>입사년도</th>
									<th>학내일반전화</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty staffList}">
										<c:forEach items="${staffList}" var="staff">
											<tr class="staff-row"
												data-staff-no="${staff.staffInfo.staffNo}">
												<td>${staff.staffDeptInfo.stfDeptName}</td>
												<td class="text-nowrap">${staff.staffInfo.staffNo}</td>
												<td>${staff.userInfo.lastName}${staff.userInfo.firstName}</td>
												<td><c:choose>
														<c:when
															test="${not empty staff.userInfo.mobileNo and fn:length(staff.userInfo.mobileNo) ge 9}">
                                                ${fn:substring(staff.userInfo.mobileNo, 0, fn:length(staff.userInfo.mobileNo) - 4)}****
                                            </c:when>
														<c:otherwise>정보 없음</c:otherwise>
													</c:choose></td>
												<td><c:choose>
														<c:when
															test="${not empty staff.staffInfo.staffNo and fn:length(staff.staffInfo.staffNo) ge 4}">
                                                ${fn:substring(staff.staffInfo.staffNo, 0, 4)}
                                            </c:when>
														<c:otherwise>N/A</c:otherwise>
													</c:choose></td>
												<td>${staff.staffInfo.teleNo}</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="6" class="text-center">조회된 교직원 정보가 없습니다.</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>
<div class="pagination-area">

						<%-- 기본 버튼 스타일 (크기/모양) --%>
						<c:set var="baseStyle"
							value="
                    padding: 4px 8px; /* 크기 축소 */
                    margin: 0 4px;
                    border-radius: 4px; /* 사각형에 가까운 모서리 */
                    font-weight: 500;
                    min-width: 30px; /* 최소 너비 축소 */
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
								style="${baseStyle}"> &#9664; </a>
						</c:if>

						<c:forEach begin="${pagingInfo.startPage}"
							end="${pagingInfo.endPage}" var="p">
							<c:choose>
								<c:when test="${pagingInfo.currentPage eq p}">
									<%-- ⭐️ 현재 페이지: 파란색 배경, 흰색 텍스트 !important 강제 적용 ⭐️ --%>
									<a href="javascript:void(0);" onclick="pageing(${p});"
										class="active"
										style="${baseStyle} background-color: #007bff !important; color: white !important; border-color: #007bff !important; font-weight: bold; cursor: default;">
										${p} </a>
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0);" onclick="pageing(${p});"
										style="${baseStyle}"> ${p} </a>
								</c:otherwise>
							</c:choose>
						</c:forEach>

						<c:if test="${pagingInfo.endPage < pagingInfo.totalPage}">
							<a href="javascript:void(0);"
								onclick="pageing(${pagingInfo.endPage + 1});"
								style="${baseStyle}"> &#9654; </a>
						</c:if>
					</div>

				</div>

				<div class="content-right">
					<div class="chart-card">
						<div class="chart-card-header">
							<h6 class="text-white">부서 상태 분포</h6>
						</div>
						<div class="chart-card-body">
							<div class="chart-container">
								<canvas id="statusPieChart"></canvas>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>

	<script
		src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>


	<script>
		const JSU_CONTEXT_PATH = "${pageContext.request.contextPath}";

		// 부서별 카운트 데이터를 JS 객체로 전달
		const employmentCountsRaw = {
		    '인사처': <c:choose><c:when test="${not empty employmentCountsMap['인사처']}">${employmentCountsMap['인사처']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
		    '행정처': <c:choose><c:when test="${not empty employmentCountsMap['행정처']}">${employmentCountsMap['행정처']}</c:when><c:otherwise>0</c:otherwise></c:choose>
		};

	</script>

	<%-- 3. 완성된 JavaScript 파일 로드 --%>
	<script src="<c:url value='/js/app/staff/staffinfoList.js' />"></script>
</body>
</html>
