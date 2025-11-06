<%--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 29.     		송태호            강의 승인 목록 조회 화면
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>강의 승인 목록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lctApprovalList.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<!-- 외부 wrapper -->
<div class="professor-lecture-page">
    <div class="ssc-wrapper">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>강의 승인 목록</h1>
        </div>

        <!-- 메인 컨텐츠 영역 (2단 레이아웃) -->
        <div class="content-layout">

            <!-- 왼쪽: 테이블 영역 -->
            <div class="left-content">

                <!-- 일괄 처리 버튼 -->
                <div class="bulk-action-buttons">
                    <button type="button" id="bulkApproveBtn" class="btn-bulk-action btn-approve">일괄승인</button>
                    <button type="button" id="bulkRejectBtn" class="btn-bulk-action btn-reject">일괄반려</button>
                </div>

                <!-- 테이블 영역 -->
                <div class="table-container">
                    <table class="lecture-table" id="lectureApplyTable">
                        <thead>
                            <tr>
                                <th>
                                    <input type="checkbox" id="checkAll" class="check-all">
                                </th>
                                <th>과목명</th>
                                <th>학과</th>
                                <th>이수구분</th>
                                <th>학점/시수</th>
                                <th>신청상태</th>
                                <th>신청자</th>
                                <th>신청일시</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="lectureApplyTableBody">
                            <!-- 데이터는 JS로 렌더링됨 -->
                        </tbody>
                    </table>
                </div>

                <!-- 페이지네이션 -->
                <div class="pagination-container" id="paginationContainer">
                    <!-- 페이지네이션은 JS로 렌더링됨 -->
                </div>

                <!-- 로딩/에러 메시지 -->
                <div id="loadingMessage" class="message-box loading" style="display: none;">
                    <p>데이터를 불러오는 중...</p>
                </div>

                <div id="errorMessage" class="message-box error" style="display: none;">
                    <p>데이터를 불러오는 중 오류가 발생했습니다.</p>
                </div>

                <div id="emptyMessage" class="message-box empty" style="display: none;">
                    <p>승인할 강의가 없습니다.</p>
                </div>

            </div>

            <!-- 오른쪽: 검색 패널 -->
            <div class="right-content">
                <div class="search-panel">
                    <h3 class="panel-title">상세 검색</h3>

                    <!-- 과목명 검색 -->
                    <div class="search-group">
                        <label class="search-label">과목명</label>
                        <input type="text" id="searchSubjectName" class="search-input" placeholder="과목명 입력">
                    </div>

                    <!-- 이수구분 필터 -->
                    <div class="search-group">
                        <label class="search-label">이수구분</label>
                        <div id="completionFilterContainer" class="filter-checkbox-group-grid">
                            <!-- JS로 동적 생성됨 -->
                        </div>
                    </div>

                    <!-- 학점 필터 -->
                    <div class="search-group">
                        <label class="search-label">학점</label>
                        <div id="creditFilterContainer" class="filter-checkbox-group-horizontal">
                            <!-- JS로 동적 생성됨 -->
                        </div>
                    </div>

                    <!-- 시수 필터 -->
                    <div class="search-group">
                        <label class="search-label">시수</label>
                        <div id="hourFilterContainer" class="filter-checkbox-group-horizontal">
                            <!-- JS로 동적 생성됨 -->
                        </div>
                    </div>

                    <!-- 신청상태 필터 -->
                    <div class="search-group">
                        <label class="search-label">신청상태</label>
                        <div id="statusFilterContainer" class="filter-checkbox-group-horizontal">
                            <!-- JS로 동적 생성됨 -->
                        </div>
                    </div>

                    <!-- 버튼 영역 -->
                    <div class="search-buttons">
                        <button type="button" id="resetSearchBtn" class="btn-reset-search">초기화</button>
                        <div class="sort-toggle-group">
                            <button type="button" id="sortDescBtn" class="sort-toggle-btn active">최신순</button>
                            <button type="button" id="sortAscBtn" class="sort-toggle-btn">오래된순</button>
                        </div>
                    </div>

                </div>
            </div>

        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app/professor/lecture/lctApprovalList.js"></script>

</body>
</html>
