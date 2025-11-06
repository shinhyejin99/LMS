<%--
 * == Modification Information ==
 *
 *    Date         Author        Description
 * ===========    ============   =======================
 * 2025. 10. 13.                Initial create
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU Classroom - 조별 과제 상세</title>

<style>
.task-content {
  word-break: break-word;
}

.meta-list {
  gap: .75rem;
}

.meta-item {
  color: #6c757d;
  font-size: .9rem;
}

.badge-type {
  margin-right: .375rem;
}

.skeleton {
  background: #eee;
  border-radius: .5rem;
  min-height: 1rem;
}

.group-summary {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: 100%;
}

.group-summary-text {
  flex: 1 1 auto;
}

.group-summary-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: .5rem;
  margin-bottom: .25rem;
}

.group-summary-meta {
  display: flex;
  flex-wrap: wrap;
  gap: .5rem 1rem;
  font-size: .875rem;
  color: #6c757d;
}

.photo {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #dee2e6;
  background-color: #fff;
  display: block;
}

.photo-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: 1px solid #dee2e6;
  background-color: #f8f9fa;
  color: #adb5bd;
  font-size: 1.5rem;
}

.photo-sm {
  width: 48px;
  height: 48px;
}

.member-card {
  border: 1px solid #e9ecef;
  border-radius: .75rem;
  padding: 1rem;
  height: 100%;
}

.member-card .member-meta {
  font-size: .85rem;
  color: #6c757d;
}

.member-card .evaluation-box {
  font-size: .875rem;
  color: #495057;
}

.member-card .evaluation-note {
  font-size: .85rem;
  color: #6c757d;
}
</style>

<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfGrouptaskPost.js" defer></script>
</head>
<body>
  <section id="grouptask-post-root" class="container py-4"
    data-ctx="${pageContext.request.contextPath}"
    data-common-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task"
    data-prof-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task"
    data-student-base="${pageContext.request.contextPath}/classroom/api/v1/common">

    <!-- 상단 내비 -->
    <c:set var="activeTab" value="task" />
    <%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

    <!-- breadcrumb -->
    <div class="d-flex align-items-center justify-content-between mb-3">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-0">
          <li class="breadcrumb-item"><a id="bc-task" href="#">과제</a></li>
          <li class="breadcrumb-item active" aria-current="page">조별 과제 상세</li>
        </ol>
      </nav>
      <div class="d-flex gap-2">
        <a class="btn btn-sm btn-outline-secondary" id="btn-list" href="#">목록으로</a>
      </div>
    </div>

    <!-- detail card -->
    <div class="card shadow-sm mb-4">
      <div class="card-body" id="task-body">
        <div class="d-flex align-items-start flex-wrap mb-2">
          <span class="badge skeleton" style="width: 64px; height: 22px;"></span>
          <span class="badge skeleton ms-2" style="width: 56px; height: 22px;"></span>
        </div>
        <h1 class="h4 mb-3 skeleton" style="width: 60%; height: 1.5rem;"></h1>
        <div class="d-flex flex-wrap meta-list mb-3">
          <div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
          <div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
          <div class="meta-item skeleton" style="width: 220px; height: 1rem;"></div>
        </div>
        <hr class="my-3" />
        <article class="task-content mb-4">
          <div class="skeleton" style="height: 8rem;"></div>
        </article>

        <div id="attach-area" class="mt-4 d-none">
          <h2 class="h6 mb-2">첨부 파일</h2>
          <ul class="list-group" id="attach-list"></ul>
        </div>
      </div>
    </div>

    <!-- group accordion -->
    <div class="card shadow-sm" id="group-accordion-card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h2 class="h6 mb-0">조 구성 현황</h2>
      </div>
      <div class="card-body">
        <div id="group-accordion" class="accordion">
          <div class="skeleton" style="height: 200px;"></div>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-end gap-2 mt-3">
      <a class="btn btn-sm btn-primary" id="btn-edit-bottom" href="#" role="button">수정</a>
      <a class="btn btn-sm btn-danger" id="btn-delete-bottom" href="#" role="button">삭제</a>
    </div>

    <div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
  </section>
</body>
</html>
