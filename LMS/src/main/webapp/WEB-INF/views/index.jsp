<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 26.     		정태일           최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.or.jsu.core.security.utils.AuthorizeCheckUtils"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>well come 페이지</h1>
	<h2>아 집에가고싶다</h2>
	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal" var="userDetails" />
		<security:authentication property="authorities" var="authorities" />
	 ${userDetails.realUser }
	 ${authorities }
	</security:authorize>
</body>
</html>
