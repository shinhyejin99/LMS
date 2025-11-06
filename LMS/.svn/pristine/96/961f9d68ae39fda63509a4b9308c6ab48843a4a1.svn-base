<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>학생 관리 - 학생 목록</title>
<!--     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> -->
<!--     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"> -->

<link rel="stylesheet"
	href="<c:url value='/css/staffStudentinfoList.css' />">
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
        gap: 5px; /* Space between search input, search button, and registration buttons */
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
	<div class="student-list-page">
		<div class="student-container">

			<!-- 페이지 헤더 추가 -->
			<div class="page-header">
				<h1>학생 목록</h1>
			</div>

			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="/staff">Home</a></li>
				<li class="breadcrumb-item">인사업무</li>
				<li class="breadcrumb-item active" aria-current="page">학생 목록</li>
			</ol>

			<div class="status-cards-row" id="status-cards-container">
				<c:set var="statusLabels" value="재학,휴학,졸업,졸업유예" />
				<c:set var="statusIcons"
					value="person-check-fill,person-dash-fill,mortarboard-fill,hourglass-split" />
				<c:set var="statusColors" value="primary,warning,success,info" />

				<c:forTokens items="${statusLabels}" delims="," var="label"
					varStatus="i">
					<c:set var="icon" value="${fn:split(statusIcons, ',')[i.index]}" />
					<c:set var="color" value="${fn:split(statusColors, ',')[i.index]}" />

					<div class="status-card status-${label}" data-status="${label}">
						<div class="status-card-body">
							<div>
								<div class="status-card-title text-${color}">${label}</div>
								<div class="status-card-count"
									data-status-count-value="${statusCountsMap[label]}"
									data-count-target="${label}">
									<c:choose>
										<c:when test="${not empty statusCountsMap[label]}">
										${statusCountsMap[label]}
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
							<li>상단의 '학적 상태 카드' 클릭을 시작으로, 단과대/학과/학년 그래프를 순차적으로 클릭하여
								상세 필터링을 적용할 수 있습니다.</li>
							<li>필터링된 상태에서 검색어를 입력하여 추가적인 조건 검색을 진행할 수 있습니다.</li>
						</ul>
					</div>
					<div class="action-buttons">
						<form class="search-form" id="searchForm"
							action="${pageContext.request.contextPath}/lms/staff/students/list"
							method="GET">

							<input class="form-control" type="search"
								name="searchKeyword" id="searchInput" placeholder="통합 검색(학과, 이름, 학번)"
								value="${searchKeyword}">
							<button class="btn btn-primary" type="button" id="searchButton"
								onclick="handleSearchSubmit()">검색</button>

							<c:url var="createUrl" value="/lms/staff/students/create" />
							<a href="${createUrl}" class="btn btn-success" role="button"><i
								class="bi bi-person-plus"></i>학생 개별 등록</a>
							<button type="button" class="btn btn-info text-white"
								data-bs-toggle="modal" data-bs-target="#excelUploadModal">
								<i class="bi bi-file-earmark-excel"></i>학생 일괄 등록
							</button>

							<input type="hidden" name="currentPage" id="currentPageInput"
								value="${pagingInfo.currentPage}"> <input type="hidden"
								name="filterStatus" id="filterStatusInput"
								value="${filterStatus}"> <input type="hidden"
								name="filterCollege" id="filterCollegeInput"
								value="${filterCollege}"> <input type="hidden"
								name="filterDepartment" id="filterDepartmentInput"
								value="${filterDepartment}"> <input type="hidden"
								name="filterGrade" id="filterGradeInput" value="${filterGrade}">
						</form>
					</div>
					<%-- 필터 헤더 문구 --%>
					<h5 class="filter-header" id="filter-header">
						<c:choose>
							<c:when
								test="${not empty filterStatus || not empty filterCollege || not empty filterDepartment || not empty filterGrade || not empty searchKeyword}">
                           필터링된 학생 목록 (총 ${pagingInfo.totalRecord}명)
                        </c:when>
							<c:otherwise>
                            전체 학생 목록 (총 ${pagingInfo.totalRecord}명)
                        </c:otherwise>
						</c:choose>
					</h5>
					<div class="table-responsive">
						<table class="data-table" id="studentTable">
							<thead class="table-light">
								<tr>
									<th>학과</th>
									<th>학번</th>
									<th>이름</th>
									<th>연락처</th>
									<th>학년</th>
									<th>지도교수</th>
									<th>입학년도</th>
									<th>재적상태</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${not empty studentList}">
										<c:forEach items="${studentList}" var="student">
											<tr class="student-row"
												data-student-no="${student.studentInfo.studentNo}"
												data-status="${student.studentInfo.stuStatusName}"
												data-college="${student.majorCollegeInfo.collegeName}"
												data-dept="${student.majorDeptInfo.univDeptName}"
												data-grade="${fn:substringBefore(student.studentInfo.gradeName, '학년')}">
												<td>${student.majorDeptInfo.univDeptName}</td>
												<td class="text-nowrap">${student.studentInfo.studentNo}</td>
												<td>${student.userInfo.lastName}${student.userInfo.firstName}</td>

												<%-- ⭐ 연락처 마스킹 적용 시작 ⭐ --%>
												<td><c:choose>
														<c:when
															test="${not empty student.userInfo.mobileNo and fn:length(student.userInfo.mobileNo) ge 9}">
                                                    ${fn:substring(student.userInfo.mobileNo, 0, fn:length(student.userInfo.mobileNo) - 4)}****
                                                </c:when>
														<c:otherwise>
                                                    정보 없음
                                                </c:otherwise>
													</c:choose></td>

												<%-- ⭐ 연락처 마스킹 적용 끝 ⭐ --%>

												<td>${student.studentInfo.gradeName}</td>
												<td>${student.professorName}</td>

												<td><c:choose>
														<c:when
															test="${not empty student.studentInfo.studentNo and fn:length(student.studentInfo.studentNo) ge 4}">
                                                    ${fn:substring(student.studentInfo.studentNo, 0, 4)}
                                                </c:when>
														<c:otherwise>
                                                    N/A
                                                </c:otherwise>
													</c:choose></td>
												<td>${student.studentInfo.stuStatusName}</td>
											</tr>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<tr>
											<td colspan="9" class="text-center">조회된 학생 정보가 없습니다.</td>
										</tr>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

					<%-- ⭐️ 페이징 영역 ⭐️ --%>
					<div class="pagination-area">

						<c:set var="baseStyle"
							value="
                    padding: 4px 8px;
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
								style="${baseStyle}"> &#9664; </a>
						</c:if>

						<c:forEach begin="${pagingInfo.startPage}"
							end="${pagingInfo.endPage}" var="p">
							<c:choose>
								<c:when test="${pagingInfo.currentPage eq p}">
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


				<%-- ⭐ 통계 차트 영역 (col-lg-4) ⭐ --%>
				<div class="content-right">
					<div class="chart-card">
						<div class="chart-card-header">
							<h6 class="text-white">학생 통계 분석 (성별/학년별)</h6>
						</div>
						<div class="chart-card-body">
							<div class="chart-container" id="gender-chart-container">
								<canvas id="genderPieChartCanvas"></canvas>
							</div>

							<div class="chart-container" id="grade-chart-container">
								<canvas id="overallGradeBarChartCanvas"></canvas>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<%-- 모달 1: 학생 정보 엑셀 일괄 등록 (폼) --%>
	<div class="modal fade" id="excelUploadModal" tabindex="-1"
		aria-labelledby="excelUploadModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form id="excelUploadForm"
					action="${pageContext.request.contextPath}/lms/staff/students/batch-excel-preview"
					method="POST" enctype="multipart/form-data">
					<div class="modal-header">
						<h5 class="modal-title" id="excelUploadModalLabel">학생 정보 엑셀
							일괄 등록</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<p class="alert alert-info small">
							<strong>주의:</strong> 파일 양식에 맞게 데이터를 입력해주세요. <a
								href="${pageContext.request.contextPath}/lms/staff/students/downloadExcel"
								class="alert-link ms-2" download="학생 일괄 등록 양식.xlsx"> 양식 다운로드
							</a>
						</p>

						<div class="mb-3">
							<label for="excelFile" class="form-label fw-semibold text-dark">
								엑셀 파일 (.xlsx 또는 .xls) </label> <input class="form-control" type="file"
								id="excelFile" name="excelFile" accept=".xlsx, .xls" required>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">취소</button>
						<button type="submit" class="btn btn-primary"
							id="batchUploadSubmit">
							<i class="bi bi-cloud-arrow-up me-1"></i> 등록 실행
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<%-- ⭐ 엑셀 등록 전 최종 확인 모달 (ID 확인: confirmDetailCounts) ⭐ --%>
	<div class="modal fade" id="batchConfirmModal" tabindex="-1"
		aria-labelledby="batchConfirmModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header bg-warning text-dark">
					<h5 class="modal-title" id="batchConfirmModalLabel">⚠️ 학생 일괄
						등록 최종 확인</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p class="lead fw-semibold text-danger">🚨 아래 내용으로 일괄 등록을
						진행하시겠습니까?</p>
					<div class="alert alert-light border p-3">
						<h6 class="fw-bold text-dark">
							총 등록 예정 인원: <span id="confirmTotalCount" class="text-primary">0명</span>
						</h6>
						<hr>
						<p class="fw-bold text-dark mb-2">[학과별 등록 예정 인원]</p>
						<ul id="confirmDetailCounts" class="modal-detail-list">
						</ul>
					</div>
					<p class="small text-muted mt-3">확인 버튼을 누르면 즉시 등록이 시작됩니다. (취소 시
						등록되지 않습니다.)</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal" id="confirmCancelBtn">취소</button>
					<button type="button" class="btn btn-danger" id="confirmProceedBtn">
						<i class="bi bi-cloud-arrow-up me-1"></i> 확인 및 등록 실행
					</button>
				</div>
			</div>
		</div>
	</div>


	<%-- ⭐ 엑셀 등록 결과 표시 모달 (최종) ⭐ --%>
	<div class="modal fade" id="batchResultModal" tabindex="-1"
		aria-labelledby="batchResultModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header bg-success text-white"
					id="batchResultModalHeader">
					<h5 class="modal-title" id="batchResultModalLabel">🎉 학생 일괄 등록
						결과</h5>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p id="resultMessage" class="lead fw-semibold"></p>
					<div id="resultDetailArea" class="mt-3 d-none">
						<p class="fw-bold text-dark">[세부 내역]</p>
						<ul id="modalDetailCounts" class="modal-detail-list">
						</ul>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary"
						data-bs-dismiss="modal" id="resultConfirmBtn">확인</button>
				</div>
			</div>
		</div>
	</div>


	<%-- 모달 3: 학적 상태 상세 통계 --%>
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
					<div id="college-view" class="chart-bar" style="height: 400px;">
						<canvas id="collegeBarChart"></canvas>
					</div>
					<div id="department-view" class="modal-view d-none chart-bar"
						style="height: 400px;">
						<canvas id="departmentBarChart"></canvas>
					</div>
					<div id="grade-view" class="d-none chart-bar"
						style="height: 400px;">
						<canvas id="gradeBarChart"></canvas>
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


	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

	<script src="<c:url value='/js/app/staff/staffStudentInfoList.js' />"></script>

	<script>
    const JSU_CONTEXT_PATH = "${pageContext.request.contextPath}";

    // 서버에서 전달된 통계 데이터를 JavaScript 변수에 바인딩 (재적 상태는 statusCountsMap 사용)
    const statusCountsRaw = {
        '재학': <c:choose><c:when test="${not empty statusCountsMap['재학']}">${statusCountsMap['재학']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '휴학': <c:choose><c:when test="${not empty statusCountsMap['휴학']}">${statusCountsMap['휴학']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '졸업': <c:choose><c:when test="${not empty statusCountsMap['졸업']}">${statusCountsMap['졸업']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '졸업유예': <c:choose><c:when test="${not empty statusCountsMap['졸업유예']}">${statusCountsMap['졸업유예']}</c:when><c:otherwise>0</c:otherwise></c:choose>
    };

    // ⭐ JSP에 genderStatsMap, gradeStatsMap이 모델에 담겨있어야 차트가 정상 작동합니다.
    const genderDataRaw = {
        '남성':  <c:choose><c:when test="${not empty genderStatsMap['남성']}">${genderStatsMap['남성']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '여성': <c:choose><c:when test="${not empty genderStatsMap['여성']}">${genderStatsMap['여성']}</c:when><c:otherwise>0</c:otherwise></c:choose>
    };
    const gradeDataRaw = {
        '1학년': <c:choose><c:when test="${not empty gradeStatsMap['1학년']}">${gradeStatsMap['1학년']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '2학년': <c:choose><c:when test="${not empty gradeStatsMap['2학년']}">${gradeStatsMap['2학년']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '3학년': <c:choose><c:when test="${not empty gradeStatsMap['3학년']}">${gradeStatsMap['3학년']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
        '4학년': <c:choose><c:when test="${not empty gradeStatsMap['4학년']}">${gradeStatsMap['4학년']}</c:when><c:otherwise>0</c:otherwise></c:choose>,
    };


    // ⭐️ 엑셀 업로드 AJAX 2단계 처리 로직 (모달 기반 확인) ⭐️
    $(document).ready(function() {

        // --- 부트스트랩 모달 인스턴스 정의 ---
        const excelUploadModal = new bootstrap.Modal(document.getElementById('excelUploadModal'));
        const batchResultModal = new bootstrap.Modal(document.getElementById('batchResultModal'));
        const batchConfirmModal = new bootstrap.Modal(document.getElementById('batchConfirmModal'), {
            backdrop: 'static',
            keyboard: false
        });

        let pendingFormData = null;

        // 엑셀 업로드 폼 제출 이벤트 (1단계: 미리보기 요청)
        $('#excelUploadForm').on('submit', function(e) {
            e.preventDefault();

            const excelFile = $('#excelFile')[0];
            if (excelFile.files.length === 0) {
                alert("⚠️ 등록할 엑셀 파일을 선택해주세요.");
                $('#excelFile').focus();
                return false;
            }

            let formData = new FormData(this);
            const previewUrl = JSU_CONTEXT_PATH + '/lms/staff/students/batch-excel-preview';

            // 1단계: 미리보기 요청 (유효성 검사 및 인원수 계산)
            $.ajax({
                url: previewUrl,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                dataType: 'json',

                beforeSend: function() {
                    $('#batchUploadSubmit').prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> 파일 분석 중...');
                },

                success: function(response) {
                    if (response.success) {
                        // 2단계: 미리보기 성공 -> 확인 모달 띄우기
                        pendingFormData = formData;

                        // 확인 모달의 내용을 업데이트하고 표시
                        updateConfirmationModal(response.totalCount, response.detailCounts);
                        batchConfirmModal.show();

                        // 기존 업로드 모달 숨기기 전 포커스 이동 (ARIA-hidden 경고 방지)
                        $('#batchUploadSubmit').focus();
                        // 기존 업로드 모달 숨기기
                        excelUploadModal.hide();

                    } else {
                        alert("⚠️ 엑셀 파일 분석 실패: " + response.message);
                        $('#batchUploadSubmit').prop('disabled', false).html('<i class="bi bi-cloud-arrow-up me-1"></i> 등록 실행');
                    }
                },
                error: function(xhr, status, error) {
                    console.error("미리보기 AJAX 실패. 상태:", status, "오류:", error, "응답:", xhr.responseText);
                    alert("서버와의 통신 중 오류가 발생했습니다. (응답 형식 문제일 수 있음)");
                    $('#batchUploadSubmit').prop('disabled', false).html('<i class="bi bi-cloud-arrow-up me-1"></i> 등록 실행');
                },
                complete: function() {
                    if(excelUploadModal._isShown) {
                        $('#batchUploadSubmit').prop('disabled', false).html('<i class="bi bi-cloud-arrow-up me-1"></i> 등록 실행');
                    }
                }
            });
        });

        /**
         * 1단계: 최종 확인 모달에 학과별 인원수를 표시하는 함수
         */
         function updateConfirmationModal(totalCount, detailCounts) {
            let finalTotalCount = 0;

            // totalCount 값 처리 로직 강화
            if (typeof totalCount === 'number' && totalCount >= 0) {
                finalTotalCount = totalCount;
            } else if (typeof totalCount === 'string' && !isNaN(parseInt(totalCount))) {
                finalTotalCount = parseInt(totalCount);
            } else if (detailCounts) {
                // totalCount가 유효하지 않을 경우, detailCounts의 합으로 계산 시도
                const deptNames = Object.keys(detailCounts);
                finalTotalCount = deptNames.reduce((sum, deptName) => sum + (detailCounts[deptName] || 0), 0);
            }

        	    // 1. 총 등록 인원 수 표시: totalCount 값을 #confirmTotalCount 요소에 삽입
        	    $('#confirmTotalCount').text(finalTotalCount + '명');

        	    let detailHtml = '';
        	    // detailCounts가 null 또는 undefined일 경우 빈 객체로 처리
        	    const deptNames = Object.keys(detailCounts || {});

        	    if (deptNames.length > 0) {
        	        deptNames.forEach(function(deptName) {
        	            const count = detailCounts[deptName] || 0;
        	            // 유효한 카운트만 리스트에 추가
        	            if ((typeof count === 'number' && count > 0) || (typeof count === 'string' && !isNaN(parseInt(count)) && parseInt(count) > 0)) {
        	                detailHtml += '<li>';
//         	                detailHtml += '<span class="text-secondary me-2">•</span> ';
        	                detailHtml += deptName;
        	                detailHtml += ' : ';
        	                detailHtml += '<span class="fw-bold text-dark">' + count + '명</span>';
        	                detailHtml += '</li>';
        	            }
        	        });

        	        if (!detailHtml && finalTotalCount > 0) {
        	            detailHtml = '<li class="text-muted">학과별 상세 정보는 누락되었지만, 총 ' + finalTotalCount + '명이 등록될 예정입니다.</li>';
        	        } else if (!detailHtml && finalTotalCount === 0) {
        	            detailHtml = '<li class="text-muted">등록할 유효한 학생 데이터가 발견되지 않았습니다.</li>';
        	        }

        	    } else {
        	        // 학과별 목록이 비어있거나 데이터가 없을 때의 처리
        	        detailHtml = '<li class="text-muted">등록할 유효한 학생 데이터가 발견되지 않았습니다.</li>';
        	    }

        	    // 학과별 인원 리스트를 #confirmDetailCounts 요소에 삽입
        	    $('#confirmDetailCounts').html(detailHtml);
        	}


        // ⭐ 확인 모달의 "등록 실행" 버튼 클릭 이벤트 핸들러 ⭐
        $('#confirmProceedBtn').on('click', function() {
            // ⭐ ARIA-hidden 경고 방지: 모달을 닫기 전에 포커스를 안전한 요소로 이동
            $('#batchUploadSubmit').focus();

            batchConfirmModal.hide();

            if (pendingFormData) {
                processBatchUpload(pendingFormData);
            }
        });

        // ⭐ 확인 모달의 "취소" 버튼 클릭 이벤트 핸들러 ⭐
        $('#confirmCancelBtn').on('click', function() {
            pendingFormData = null;
            $('#batchUploadSubmit').prop('disabled', false).html('<i class="bi bi-cloud-arrow-up me-1"></i> 등록 실행');
            // ARIA-hidden 경고 방지: 취소 시에도 포커스 이동
            $('#batchUploadSubmit').focus();
        });


        // 3단계: 실제 등록을 진행하는 함수
      function processBatchUpload(formData) {

    const uploadUrl = JSU_CONTEXT_PATH + '/lms/staff/students/batch-excel-create';

    $.ajax({
        url: uploadUrl,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        dataType: 'json',

        beforeSend: function() {
            // 로딩 모달 띄우기 (기존 로직 유지)
            $('#batchResultModalLabel').text('등록 처리 중...');
            $('#resultMessage').html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>등록 요청을 처리 중입니다. 잠시만 기다려주세요...');
            batchResultModal.show();
            $('#batchResultModalHeader').removeClass('bg-success bg-danger').addClass('bg-info');
            $('#resultDetailArea').addClass('d-none');
        },
        success: function(response) {
            console.log("최종 등록 응답 전체:", response);

            // ⭐⭐ 수정된 추출 로직: response.data 또는 response 최상위에서 totalCount를 추출 ⭐⭐
            const data = response.data || {};
            // totalCount는 response.data.totalCount 또는 response.totalCount에서 가져옵니다.
            const finalTotalCount = data.totalCount || response.totalCount || 0;
            const finalDetailCounts = data.detailCounts || response.detailCounts || {};

            const successMessage = response.message || '등록이 성공적으로 완료되었습니다.';

            // ⭐ 디버깅 로그 추가: 추출된 인원수 확인 ⭐
            console.log("processBatchUpload - 추출된 finalTotalCount:", finalTotalCount);

            // 등록 결과 모달로 데이터 전달 및 표시
            showBatchResultModal(
                response.success,
                finalTotalCount, // 강화된 로직으로 추출된 값 전달
                finalDetailCounts, // 강화된 로직으로 추출된 값 전달
                successMessage
            );
        },
        error: function(xhr) {
                    const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "서버 통신 오류가 발생했습니다. (HTTP 상태 코드: " + xhr.status + ")";
                    showBatchResultModal(false, 0, {}, errorMsg);
                },
                complete: function() {
                    pendingFormData = null;
                    // '파일 분석 중...' 상태를 복구할 필요는 없음 (성공/실패 모달이 덮어쓰기 때문)
                }
            });
        }

        /**
         * 3단계: 일괄 등록 결과를 모달로 표시하는 함수 (⭐️ 인원수 출력 최종 보강 ⭐️)
         */
         function showBatchResultModal(success, totalCount, detailCounts, errorMessage) {

        	    let finalCount = 0;

        	    // ⭐⭐ 수정된 로직: 전달받은 totalCount가 문자열/숫자든 관계없이 안전하게 정수로 변환 ⭐⭐
        	    const parsedCount = parseInt(totalCount);
        	    if (!isNaN(parsedCount) && parsedCount >= 0) {
        	        finalCount = parsedCount;
        	    } else {
        	        // 혹시 모를 상황 대비 (전달된 값이 유효하지 않을 때 0으로 설정)
        	        finalCount = 0;
        	    }

        	    // 모달 콘텐츠 초기화
        	    $('#resultMessage').empty();
        	    $('#modalDetailCounts').empty();
        	    $('#resultDetailArea').addClass('d-none');

        	    // 모달 헤더 및 제목 업데이트
        	    $('#batchResultModalHeader').removeClass('bg-info bg-success bg-danger').addClass(success ? 'bg-success' : 'bg-danger');
        	    $('#batchResultModalLabel').text(success ? '🎉 학생 일괄 등록 성공' : '❌ 학생 일괄 등록 실패');

        	    if (success) {
                    // 최종 인원수 삽입 로직: 문자열 연결(+) 사용 보장
        	        const resultHtml = '총 <strong class="text-success">' + finalCount + '명</strong>의 학생 등록이 완료되었습니다.';
        	        $('#resultMessage').html(resultHtml);

                    // 상세 내역 표시 (완성)
        	        let detailHtml = '';
        	        const deptNames = Object.keys(detailCounts || {});

        	        if (deptNames.length > 0) {
        	            deptNames.forEach(function(deptName) {
        	                const count = detailCounts[deptName] || 0;
                            // 유효한 카운트만 리스트에 추가
        	                if ((typeof count === 'number' || (typeof count === 'string' && !isNaN(parseInt(count)))) && parseInt(count) > 0) {
        	                    detailHtml += '<li>';
//         	                    detailHtml += '<span class="text-secondary me-2">•</span> ';
        	                    detailHtml += deptName;
        	                    detailHtml += ' : ';
        	                    detailHtml += '<span class="fw-bold text-dark">' + count + '명</span>';
        	                    detailHtml += '</li>';
        	                }
        	            });

        	            if (detailHtml) {
        	                $('#modalDetailCounts').html(detailHtml);
        	                $('#resultDetailArea').removeClass('d-none');
        	            }
        	        }

                    // 확인 버튼 클릭 시 페이지 새로고침
                    $('#resultConfirmBtn').off('click').on('click', function() {
                        window.location.reload();
                    });

        	    } else {
        	        // 실패 시
        	        const failHtml = '<i class="bi bi-exclamation-triangle-fill me-1"></i> 등록 중 치명적인 오류가 발생했습니다.<br><strong>' + (errorMessage || '상세 오류 정보는 서버 로그를 확인해주세요.') + '</strong>';
        	        $('#resultMessage').html(failHtml);

                    // 실패 시에는 확인 버튼 클릭 시 모달만 닫도록 설정
                    $('#resultConfirmBtn').off('click').on('click', function() {
                        batchResultModal.hide();
                    });
        	    }

                // 최종 결과 모달 표시
        	    batchResultModal.show();
        	}

        // 필터 표시를 업데이트하는 함수 (staffStudentInfoList.js에서 submitSearchFormWithFilters가 호출하지만, JSP 로직에는 영향 없음)
        function updateFilterDisplay() {
            // 이 함수는 현재 JSP에서 목록이 새로 로드될 때마다 필터 헤더가 자동으로 업데이트되므로, 비워둡니다.
        }

    });

</script>
</body>
</html>