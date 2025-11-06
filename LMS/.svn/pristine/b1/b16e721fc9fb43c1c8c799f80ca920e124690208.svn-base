<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 개설 신청 내역</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body{
            display:flex;flex-direction:column;justify-content:center;align-items:center;
            height:100vh;font-family:sans-serif;background:#f5f5f9;color:#566a7f;text-align:center;
        }
        h1{font-size:2em;margin-bottom:.5em;}  p{font-size:1.2em;}
        .uri{font-family:monospace;background:#e0e0e0;padding:.2em .5em;border-radius:4px;}
        .clickable-row {
            cursor: pointer;
        }
        .clickable-row:hover {
            background-color: #f0f0f0; /* Light gray on hover */
        }
        .pagination {
            display: flex;
            padding-left: 0;
            list-style: none;
            border-radius: .25rem;
        }
        .page-btn {
            position: relative;
            display: block;
            padding: .5rem .75rem;
            margin-left: -1px;
            line-height: 1.25;
            color: #007bff;
            background-color: #fff;
            border: 1px solid #dee2e6;
            text-decoration: none;
        }
        .page-btn:hover {
            color: #0056b3;
            background-color: #e9ecef;
            border-color: #dee2e6;
        }
        .page-btn.active {
            z-index: 1;
            color: #fff;
            background-color: #696cff;
            border-color: #696cff;
        }
        .page-btn.disabled {
            color: #6c757d;
            pointer-events: none;
            background-color: #fff;
            border-color: #dee2e6;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="container">
    <div class="py-4">
        <div class="row">
            <div class="col-12">
                <div class="card border-0 shadow">
                    <div class="card-header">
                        <h2 class="fs-5 fw-bold mb-0">강의 개설 신청 내역</h2>
                        <hr>
                    </div>

                    <div class="card-body">
                        <!-- ▶ 조회조건 -->
                        <form method="GET" action="${pageContext.request.contextPath}/lms/professor/lectureRegist/list">
	                        <div class="row align-items-center mb-4">
	                            <!-- 학년도 -->
	                            <div class="col-md-3">
	                                <label for="statsAcademicYear" class="form-label">학년도</label>
	                                <select class="form-select" id="statsAcademicYear" name="academicYear">
	                                    <c:forEach var="year" items="${academicYears}">
	                                        <option value="${year.value}"
	                                                <c:if test="${year.selected}">selected</c:if>>
	                                            ${year.label}
	                                        </option>
	                                    </c:forEach>
	                                </select>
	                            </div>
	                        
	                            <!-- 이수구분 -->
	                            <div class="col-md-3">
	                                <label for="completionCd" class="form-label">이수구분</label>
	                                <select class="form-select" id="completionCd" name="completionCd">
	                                    <option value="">전체</option>
	                                    <option value="MAJ_CORE" <c:if test="${param.completionCd == 'MAJ_CORE'}">selected</c:if>>전공-핵심</option>
	                                    <option value="MAJ_ELEC" <c:if test="${param.completionCd == 'MAJ_ELEC'}">selected</c:if>>전공-선택</option>
	                                    <option value="MAJ_BASIC" <c:if test="${param.completionCd == 'MAJ_BASIC'}">selected</c:if>>전공-기초</option>
	                                    <option value="GE_CORE" <c:if test="${param.completionCd == 'GE_CORE'}">selected</c:if>>교양-핵심</option>
	                                    <option value="GE_ELEC" <c:if test="${param.completionCd == 'GE_ELEC'}">selected</c:if>>교양-선택</option>
	                                    <option value="GE_BASIC" <c:if test="${param.completionCd == 'GE_BASIC'}">selected</c:if>>교양-기초</option>
	                                </select>
	                            </div>
	
	                            <!-- 처리상태 -->
	                            <div class="col-md-3">
	                                <label for="lectureStatus" class="form-label">처리 상태</label>
	                                <select class="form-select" id="lectureStatus" name="status">
	                                    <c:forEach var="st" items="${statuses}">
	                                        <option value="${st.value}"
	                                                <c:if test="${st.selected}">selected</c:if>>
	                                            ${st.label}
	                                        </option>
	                                    </c:forEach>
	                                </select>
	                            </div>
	
	                            <!-- 조회버튼 -->
	                            <div class="col-md-3 d-flex align-items-end">
	                                <button type="submit" class="btn btn-primary w-100">검색</button>
	                            </div>
	                        </div>
                        </form>

                        <!-- ▶ 결과 테이블 : 데이터 행은 JS/AJAX 또는 JSTL로 동적 삽입 -->
                        <div class="table-responsive">
                            <table class="table table-centered table-hover mb-0" id="applyListTable">
                                <thead class="thead-light">
                                <tr>
                                    <th class="border-0">번호</th>
                                    <th class="border-0">신청일</th>
                                    <th class="border-0">강의명</th>
                                    <th class="border-0">이수구분</th>
                                    <th class="border-0">학점</th>
                                    <th class="border-0">처리상태</th>
                                    
                                </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty applyList}">
                                            <tr>
                                                <td colspan="6" class="text-center text-muted">조회된 신청 내역이 없습니다.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="apply" items="${applyList}" varStatus="status">
                                                <tr class="clickable-row" data-lct-apply-id="${apply.LCT_APPLY_ID}" onclick="location.href='${pageContext.request.contextPath}/lms/professor/lectureRegist/detail?lctApplyId=${apply.LCT_APPLY_ID}'">
                                                    <td>${pagingInfo.startRow + status.index}</td>
                                                    <td><fmt:formatDate value="${apply.APPLY_AT}" pattern="yyyy-MM-dd"/></td>
                                                    <td>${apply.SUBJECT_NAME}</td>
                                                    <td>${apply.COMPLETION_CD_NAME}</td>
                                                    <td>${apply.CREDIT}</td>
                                                    <td>${apply.APPROVE_STATUS_NAME}</td>
                                                
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>

                        <div id="paginationContainer" class="d-flex justify-content-center mt-4"></div>

                    </div><!-- card-body -->
                </div><!-- card -->
            </div><!-- col -->
        </div><!-- row -->
    </div><!-- py-4 -->
</div><!-- container -->

<script src="${pageContext.request.contextPath}/js/app/core/paging.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    console.log("profLectureRegistList.jsp script is executing!");

    // paging.js가 로드된 후 displayPagination 호출
    if (typeof displayPagination === 'function') {
        const paginationInfo = {
            totalRecord: ${pagingInfo.totalRecord},
            screenSize: ${pagingInfo.screenSize},
            blockSize: ${pagingInfo.blockSize},
            currentPage: ${pagingInfo.currentPage},
            startPage: ${pagingInfo.startPage},
            endPage: ${pagingInfo.endPage},
            totalPage: ${pagingInfo.totalPage}
        };
        console.log("Raw paginationInfo from model:", paginationInfo);

        const displayPagingInfo = {
            totalPage: paginationInfo.totalPage,
            currentPage: paginationInfo.currentPage,
            startPage: paginationInfo.startPage,
            endPage: paginationInfo.endPage
        };
        console.log("displayPagingInfo for displayPagination:", displayPagingInfo);
        displayPagination(displayPagingInfo);
    }

    // 전역 함수로 선언하여 paging.js에서 호출할 수 있도록 함
    window.loadListFn = function(pageNo) {
        // 현재 검색 조건을 가져옴
        const academicYear = document.getElementById('statsAcademicYear').value;
        const completionCd = document.getElementById('completionCd').value;
        const status = document.getElementById('lectureStatus').value;

        // URLSearchParams를 사용하여 URL을 안전하게 생성
        const params = new URLSearchParams();
        params.append('academicYear', academicYear);
        params.append('status', status);
        params.append('page', pageNo);

        if (completionCd) {
            params.append('completionCd', completionCd);
        }

        location.href = `${pageContext.request.contextPath}/lms/professor/lectureRegist/list?${params.toString()}`;
    };
});
</script>
</body>
</html>