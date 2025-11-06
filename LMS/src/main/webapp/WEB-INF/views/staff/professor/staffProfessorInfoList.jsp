<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교수 관리 - 교수 목록</title>
<!--     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> -->
<!--     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"> -->
<link rel="stylesheet"
	href="<c:url value='/css/staffProfessorinfoList.css' />">
<style>
    .action-buttons {
        display: flex;
        align-items: center;
        justify-content: flex-end; /* Align the action-group to the right */
        gap: 5px; /* Space between the search form and the registration button div */
        flex-wrap: wrap; /* Allow items to wrap on smaller screens */
        margin-bottom: 20px; /* Add some space below the action buttons */
    }

    .action-buttons .search-form {
        display: flex;
        align-items: center;
        gap: 5px; /* Space between search input and search button */
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
        justify: center;
        white-space: nowrap; /* Prevent text wrapping inside buttons */
        padding: 0 10px; /* Further adjust horizontal padding for compactness */
        font-size: 0.9em; /* Reduce font size */
        max-width: 100px; /* Further reduce the maximum width for the buttons */
        overflow: hidden; /* Hide overflowing text */
        text-overflow: ellipsis; /* Add ellipsis for overflowing text */
    }

    .action-buttons > div { /* This rule is no longer needed as the div is gone */
        /* Removed */
    }
</style>
</head>
<body>
	<!-- 외부 래퍼 추가 -->
	<div class="professor-list-page">
		<div class="professor-container">

			<!-- 페이지 헤더 추가 -->
			<div class="page-header">
				<h1>교수 목록</h1>
			</div>
			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="/staff">Home</a></li>
				<li class="breadcrumb-item">인사업무</li>
				<li class="breadcrumb-item active" aria-current="page">교수 목록</li>


			</ol>

			<%-- 검색 및 등록 버튼 영역 --%>


			<%-- 재직 상태 카드 (여기서 클릭 이벤트 발생) --%>
			<div class="status-cards-row" id="status-cards-container">
				<c:set var="statusLabels" value="재직중,휴직중,연구년" />
				<c:set var="statusIcons"
					value="person-check-fill,person-dash-fill,person-x-fill,book-half" />
				<c:set var="statusColors" value="primary,warning,danger,info" />

				<c:forTokens items="${statusLabels}" delims="," var="label"
					varStatus="i">
					<c:set var="icon" value="${fn:split(statusIcons, ',')[i.index]}" />
					<c:set var="color" value="${fn:split(statusColors, ',')[i.index]}" />

					<div class="status-card status-${label}" data-status="${label}">
						<div class="status-card-body">
							<div>
								<div class="status-card-title text-${color}">${label}</div>
								<div class="status-card-count"
									data-status-count-value="${employmentCountsMap[label]}"
									data-count-target="${label}">
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

			<div class="content-row">
				<div class="content-left">
					<div class="custom-notice-box">
						<h6>
							<i class="bi bi-lightbulb-fill"></i> 목록 필터링 기능 안내
						</h6>
						<ul>
							<li>상단의 '재직 상태 카드' 클릭을 시작으로, **단과대/학과/임용 그래프**를 순차적으로 클릭하여
								상세 필터링을 적용할 수 있습니다.</li>
							<li>필터링된 상태에서 검색어를 입력하여 추가적인 조건 검색을 진행할 수 있습니다.</li>
						</ul>
					</div>
					<div class="action-buttons">
						<form class="search-form" id="searchForm"
							action="${pageContext.request.contextPath}/lms/staff/professors/list"
							method="GET">
							<input class="form-control" type="search" name="searchKeyword"
								id="searchInput" placeholder="통합 검색 (이름, 교번 등)"
								value="${searchKeyword}">
							<button class="btn btn-primary" type="button" id="searchButton"
								onclick="handleSearchSubmit()">검색</button>
							<c:url var="createUrl" value="/lms/staff/professors/create" />
							<a href="${createUrl}" class="btn btn-success" role="button">
								<i class="bi bi-person-plus"></i>교수 등록
							</a>

							<input type="hidden" name="currentPage" id="currentPageInput"
								value="${pagingInfo.currentPage}"> <input type="hidden"
								name="filterEmploymentStatus" id="filterStatusInput"
								value="${filterEmploymentStatus}"> <input type="hidden"
								name="filterCollege" id="filterCollegeInput"
								value="${filterCollege}"> <input type="hidden"
								name="filterDepartment" id="filterDepartmentInput"
								value="${filterDepartment}"> <input type="hidden"
								name="filterAppointment" id="filterAppointmentInput"
								value="${filterAppointment}">
						</form>
					</div>

					<h5 class="filter-header" id="filter-header">
						<i class="bi bi-list-ul"></i>
						<c:choose>
							<c:when
								test="${not empty filterEmploymentStatus || not empty filterCollege || not empty filterDepartment || not empty filterPosition || not empty searchKeyword}">
			                   필터링된 교수 목록 (총 ${pagingInfo.totalRecord}명)
			                </c:when>
							<c:otherwise>
			                    전체 교수 목록 (총 ${pagingInfo.totalRecord}명)
			                </c:otherwise>
						</c:choose>
					</h5>

					<div class="table-responsive">
						<table class="data-table" id="professorTable">
							<thead class="table-light">
								<tr>
									<th>학과</th>
									<th>교번</th>
									<th>이름</th>
									<th>연락처</th>
									<th>임용상태</th>

									<th>임용년도</th>
									<th>재직상태</th>
								</tr>
							</thead>
							<tbody id="professorTableBody">
								<c:choose>
									<c:when test="${not empty professorList}">
										<c:forEach items="${professorList}" var="professor">
											<tr class="professor-row"
												data-professor-no="${professor.professorNo}"
												data-status="${professor.employmentStatus}"
												data-college="${professor.collegeName}"
												data-dept="${professor.departmentName}"
												data-position="${professor.positionName}"
												style="cursor: pointer;"
												onclick="redirectToDetail('${professor.professorNo}');">
												<td>${professor.departmentName}</td>
												<td>${professor.professorNo}</td>
												<td class="text-nowrap">${professor.lastName}${professor.firstName}</td>

												<%-- ⭐ 휴대전화 마스킹 처리 (010-XXXX-**** 형식 가정) ⭐ --%>
												<td><c:choose>
														<c:when
															test="${not empty professor.mobileNo and fn:length(professor.mobileNo) ge 9}">
                                                    ${fn:substring(professor.mobileNo, 0, fn:length(professor.mobileNo) - 4)}****
                                                </c:when>
														<c:otherwise>
                                                    정보 없음
                                                </c:otherwise>
													</c:choose></td>
												<td>${professor.employmentType}</td>

												<%-- ⭐ 학사전화번호 마스킹 처리 (지역번호 포함된 형식 가정) ⭐ --%>

												<td><c:choose>
														<c:when
															test="${not empty professor.professorNo and fn:length(professor.professorNo) ge 4}">
                                                    ${fn:substring(professor.professorNo, 0, 4)}
                                                </c:when>
														<c:otherwise>
                                                    N/A
                                                </c:otherwise>
													</c:choose></td>

												<td>${professor.employmentStatus}</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="8" class="text-center">조회된 교수 정보가 없습니다.</td>
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
							<h6 class="text-white">재직 상태 분포</h6>
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

	<%-- 모달: 재직 상태 상세 통계 --%>
	<div class="modal fade" id="statusDetailModal" tabindex="-1"
		aria-labelledby="statusDetailModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="statusDetailModalLabel">상세 통계</h5>
					<button type="button" class="btn btn-sm btn-secondary d-none"
						id="modal-back-btn">
						<i class="bi bi-arrow-left me-1"></i> 뒤로가기
					</button>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<%-- 단과대학 뷰 (college-view) --%>
					<div id="college-view" class="chart-bar" style="height: 400px;">
						<canvas id="collegeBarChart"></canvas>
					</div>
					<%-- 학과 목록 뷰 (department-view) --%>
					<div id="department-view" class="d-none chart-bar"
						style="height: 400px;">
						<canvas id="departmentBarChart"></canvas>
					</div>
					<%-- 직위 뷰 (position-view) --%>
					<div id="appointment-view" class="d-none chart-bar"
						style="height: 400px;">
						<canvas id="positionBarChart"></canvas>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">닫기</button>
					<button type="button" id="viewListButton" class="btn btn-primary">목록
						보기</button>
				</div>
			</div>
		</div>
	</div>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<script
		src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

	<%-- 2. Context Path 변수 및 DB에서 가져온 카운트 데이터를 JS 변수에 할당 --%>
	<script>
       const JSU_CONTEXT_PATH = "${pageContext.request.contextPath}";

       // JSP 변수명이 '재직중' 형태를 가지므로, JS에서도 이를 사용하도록 수정합니다.
       const employmentCountsRaw = {
           '재직중': <c:choose><c:when test="${not empty employmentCountsMap['재직중']}">${employmentCountsMap['재직중']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
           '휴직중': <c:choose><c:when test="${not empty employmentCountsMap['휴직중']}">${employmentCountsMap['휴직중']}</c:when><c:otherwise>0</c:otherwise></c:choose>,

           '연구년': <c:choose><c:when test="${not empty employmentCountsMap['연구년']}">${employmentCountsMap['연구년']}</c:when><c:otherwise>0</c:otherwise></c:choose>
       };
    </script>

	<%-- 3. 완성된 JavaScript 파일 로드 --%>
	<script src="<c:url value='/js/app/staff/staffProfessorinfoList.js' />"></script>
</body>
</html>