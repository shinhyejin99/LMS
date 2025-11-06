<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
  <title>아이디 찾기 결과 | JSU LMS</title>
  <meta name="description" content="JSU LMS 아이디 찾기 결과"/>
  <!-- Common CSS/JS includes if necessary, similar to loginForm.jsp -->
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/css/core.css'/>" class="template-customizer-core-css"/>
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/css/theme-default.css'/>" class="template-customizer-theme-css"/>
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/css/demo.css'/>" />
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/css/pages/page-auth.css'/>" />
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/js/helpers.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/js/config.js'/>"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
  <div class="container-xxl">
    <div class="authentication-wrapper authentication-basic container-p-y">
      <div class="authentication-inner">
        <div class="card">
          <div class="card-body">
            <h4 class="mb-2">아이디 찾기 결과</h4>
            <c:if test="${not empty foundId}">
              <p class="mb-8">찾으시는 아이디는 <strong>${foundId}</strong> 입니다.</p>
            </c:if>
            <c:if test="${not empty error}">
              <p class="mb-8 text-danger">${error}</p>
            </c:if>
            <c:if test="${not empty message}">
              <p class="mb-8 text-success">${message}</p>
            </c:if>
            <div class="mb-3">
              <a href="<c:url value='/login'/>" class="btn btn-primary d-grid w-100">로그인 페이지로 이동</a>
            </div>
            <p class="text-center">
              <span><a href="<c:url value='/portal/user/resetpassword'/>">비밀번호 재설정</a></span>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/libs/jquery/jquery.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/libs/popper/popper.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/js/bootstrap.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/js/menu.js'/>"></script>
  <script src="<c:url value='/sneat-1.0.0/assets/js/main.js'/>"></script>
</body>
</html>