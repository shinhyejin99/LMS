<%--
 * /WEB-INF/views/classroom/student/stuIndivtaskPost.jsp
 * 학생의 개인과제 확인 및 제출 페이지
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 개인과제</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/student/stuIndivtaskPost.js" defer></script>
</head>
<body>
<section id="post-root" class="container py-4"
         data-ctx="${pageContext.request.contextPath}"
         data-lecture-id="<c:out value='${lectureId}'/>"
         data-task-id="<c:out value='${indivtaskId}'/>">

    <c:set var="activeTab" value="task" />
    <%@ include file="/WEB-INF/fragments/classroom/student/nav.jspf" %>

    <div class="d-flex align-items-center justify-content-between mb-3">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-0">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/classroom/student/${lectureId}/task">과제</a></li>
                <li class="breadcrumb-item active" aria-current="page">개인과제 상세</li>
            </ol>
        </nav>
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/classroom/student/${lectureId}/task">목록</a>
    </div>

    <!-- 과제 정보 -->
    <div class="card shadow-sm mb-4">
        <div class="card-body" id="task-body">
            <!-- Skeleton UI -->
            <div class="placeholder-glow">
                <h1 class="h4 mb-3 placeholder col-8"></h1>
                <div class="d-flex flex-wrap gap-3 text-muted small mb-3">
                    <span class="placeholder col-3"></span>
                    <span class="placeholder col-4"></span>
                    <span class="placeholder col-4"></span>
                </div>
                <hr/>
                <div class="placeholder col-12" style="height: 120px;"></div>
            </div>
        </div>
        <div class="card-footer d-none" id="attach-area">
            <h2 class="h6 mb-2">첨부파일</h2>
            <ul class="list-group list-group-flush" id="attach-list"></ul>
        </div>
    </div>

    <!-- 내 제출 정보 -->
    <div id="submission-status-card" class="card shadow-sm mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h2 class="h5 mb-0">내 제출 현황</h2>
            <button type="button" id="show-submit-form-btn" class="btn btn-primary btn-sm">과제 제출</button>
        </div>
        <div class="card-body" id="submission-history-body">
            <p class="text-muted">데이터를 불러오는 중입니다...</p>
        </div>
    </div>

    <!-- 과제 제출/수정 (초기에는 숨김) -->
    <div id="submission-form-card" class="card shadow-sm d-none">
        <div class="card-header">
            <h2 class="h5 mb-0" id="submit-form-title">과제 제출</h2>
        </div>
        <div class="card-body">
            <form id="submit-form">
                <div class="mb-3">
                    <label for="submit-desc" class="form-label">설명</label>
                    <textarea class="form-control" id="submit-desc" rows="4" placeholder="과제에 대한 설명을 입력하세요."></textarea>
                </div>
                <div class="mb-3">
                    <label for="submit-file-input" class="form-label">파일 첨부</label>
                    <div id="existing-files-container" class="mb-3 d-none">
                        <label class="form-label">현재 제출된 파일</label>
                        <ul class="list-group" id="existing-files-list"></ul>
                    </div>
                    <input class="form-control" type="file" id="submit-file-input" multiple>
                    <input type="hidden" id="submit-file-id">
                    <div class="form-text">새로운 파일을 첨부하면 기존 파일들을 덮어씁니다.</div>
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <button type="button" id="cancel-submit-btn" class="btn btn-secondary">취소</button>
                    <button type="submit" class="btn btn-primary" id="submit-button">제출하기</button>
                </div>
            </form>
        </div>
    </div>

    <div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
</section>
</body>
</html>