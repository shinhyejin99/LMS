<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title><c:out value="${pageTitle != null ? pageTitle : 'Classroom'}" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1">
	<%@ include file="/WEB-INF/fragments/classroom/preStyle.jsp"%>
	<sitemesh:write property="head"/>
</head>
<body>

	<header class="app-header">
		<security:authorize access="hasRole('STUDENT')">
			<%@ include file="/WEB-INF/fragments/classroom/header-student.jsp"%>
		</security:authorize>
		<security:authorize access="hasRole('PROFESSOR')">
			<%@ include file="/WEB-INF/fragments/classroom/header-professor.jsp"%>
		</security:authorize>
	</header>
	
	<main class="app-main">		
		<aside class="app-sidebar d-flex flex-column">
            <security:authorize access="hasRole('STUDENT')">
                <%@ include file="/WEB-INF/fragments/classroom/sidebar-student.jsp"%>
            </security:authorize>
            <security:authorize access="hasRole('PROFESSOR')">
                <%@ include file="/WEB-INF/fragments/classroom/sidebar-professor.jsp"%>
            </security:authorize>
		</aside>
		
		<section class="app-content">
			<sitemesh:write property="body"/>
		</section>
	</main>

	<footer class="app-footer">
		<%@ include file="/WEB-INF/fragments/classroom/footer.jsp" %>
	</footer>
	
	<%@ include file="/WEB-INF/fragments/classroom/postScript.jsp"%>
</body>
</html>