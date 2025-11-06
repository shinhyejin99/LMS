<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="decorator" uri="http://www.sitemesh.org/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>

<html
  lang="ko"
  class="light-style customizer-hide"
  dir="ltr"
  data-theme="theme-default"
  data-assets-path="${pageContext.request.contextPath}/resources/sneat-1.0.0/assets/"
  data-template="vertical-menu-template-free"
>
  <head>
    <title><decorator:title default="로그인"/></title>
    <%@ include file="/WEB-INF/fragments/preStyle.jsp"%>
    <!-- Page CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/sneat-1.0.0/assets/vendor/css/pages/page-auth.css'/>" />
    <decorator:head/>
  </head>

  <body>
    <!-- Content -->
    <div class="container-xxl">
      <div class="authentication-wrapper authentication-basic container-p-y">
        <div class="authentication-inner">
          <!-- Card -->
          <div class="card">
            <div class="card-body">
                <decorator:body/>
            </div>
          </div>
          <!-- /Card -->
        </div>
      </div>
    </div>
    <!-- / Content -->

    <%@ include file="/WEB-INF/fragments/postScript.jsp"%>
  </body>
</html>












<%-- <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="decorator" uri="http://www.sitemesh.org/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title><decorator:title default="로그인"/></title>

  <!-- 최소 리소스만 -->
  <link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css'/>">
  <decorator:head/>

  <style>
    .auth-wrapper{min-height:100vh;display:flex;align-items:center;justify-content:center;background:#f8f9fa}
    .auth-card{width:100%;max-width:420px}
  </style>
</head>
<body>
  <div class="auth-wrapper">
    <div class="card shadow-sm auth-card">
      <div class="card-body">
        <decorator:body/>
      </div>
    </div>
  </div>
  <script src="<c:url value='/js/bootstrap.bundle.min.js'/>"></script>
</body>
</html>
 --%>