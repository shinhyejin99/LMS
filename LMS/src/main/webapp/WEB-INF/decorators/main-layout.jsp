<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<!DOCTYPE html>
<html lang="ko" class="light-style layout-menu-fixed" dir="ltr"
    data-theme="theme-default"
    data-assets-path="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/"
    data-template="vertical-menu-template-free">

<head>
    <title>
        <c:choose>
            <c:when test="${not empty title}">${title}</c:when>
            <c:otherwise>JSU LMS</c:otherwise>
        </c:choose>
    </title>
    <style>
        .layout-page {
            display: flex;
            flex-direction: column;
            min-height: 100vh; /* Ensure it takes at least full viewport height */
        }
        .content-wrapper {
            flex-grow: 1;
        }
    </style>
    <%@ include file="/WEB-INF/fragments/preStyle.jsp"%>
    <sitemesh:write property="head"/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

	<!-- 역할별 사이드바 스타일 추가 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/role-sidebar.css">

</head>

<body class="
    <security:authorize access="hasRole('STUDENT')">role-student</security:authorize>
    <security:authorize access="hasRole('PROFESSOR')">role-professor</security:authorize>
    <security:authorize access="hasRole('STAFF')">role-staff</security:authorize>
">
    <!-- Layout wrapper -->
    <div class="layout-wrapper layout-content-navbar">
        <div class="layout-container">

            <!-- 사이드바 -->
            <security:authorize access="hasRole('STUDENT')">
                <%@ include file="/WEB-INF/fragments/sidebar-student.jsp"%>
            </security:authorize>

            <security:authorize access="hasRole('PROFESSOR')">
                <%@ include file="/WEB-INF/fragments/sidebar-professor.jsp"%>
            </security:authorize>

            <security:authorize access="hasRole('STAFF')">
                <%@ include file="/WEB-INF/fragments/sidebar-staff.jsp"%>
            </security:authorize>

<%--             <security:authorize access="hasRole('ADMIN')"> --%>
<%--                 <%@ include file="/WEB-INF/fragments/sidebar-admin.jsp"%> --%>
<%--             </security:authorize> --%>

            <!-- Layout page -->
            <div class="layout-page">
                <!-- Header -->
                <%@ include file="/WEB-INF/fragments/header.jsp"%>

                <!-- 메인 컨텐츠 -->

                <div class="content-wrapper">
                    <sitemesh:write property="body"/> <!-- 이 자리에 컨텐츠의 body!만 넣겠어 -->

                </div>
                <!-- 메인 컨텐츠 끝 -->

                <!-- Footer -->
                <%@ include file="/WEB-INF/fragments/footer.jsp"%>
            </div>
            <!-- / Layout page -->
        </div>

        <!-- Overlay -->
        <div class="layout-overlay layout-menu-toggle"></div>
    </div>
    <!-- / Layout wrapper -->

    <%@ include file="/WEB-INF/fragments/postScript.jsp"%>

    <security:authorize access="hasRole('STUDENT')">
        <!-- 학생 전용 스크립트: 불필요한 API 호출을 유발했던 스크립트 포함 -->
        <script src="${pageContext.request.contextPath}/js/app/student/studentTuition.js"></script>
        <c:if test="${pageContext.request.requestURI eq '/lms/student/academic-change/status'}">
            <script src="${pageContext.request.contextPath}/js/app/student/studentEnrollmentStatusChange.js"></script>
        </c:if>
    </security:authorize>

</body>
</html>