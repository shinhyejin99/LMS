<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 25.     			정태일            최초 생성
 *  2025.10. 10.     			정태일            아이디찾기 view 생성 
 *
-->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
  <title>아이디 찾기 | JSU LMS</title>
  <meta name="description" content="JSU LMS 아이디 찾기"/>
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
            <h4 class="mb-2">아이디 찾기</h4>
            <p class="mb-8">가입 시 등록한 정보로 아이디를 찾을 수 있습니다.</p>
            <!-- 아이디 찾기 폼이 들어갈 자리 -->
<!--             <form id="findIdForm" class="mb-3" action="#" method="POST"> -->
				 <form id="findIdForm" class="mb-3" action="<c:url value='/portal/user/findid'/>" method="POST">
              <div class="mb-3">
                <label for="name" class="form-label">이름</label>
                <input type="text" class="form-control" id="name" name="name" placeholder="이름을 입력하세요" autofocus required/>
              </div>
              <div class="mb-3">
                <label for="email" class="form-label">이메일</label>
                <input type="email" class="form-control" id="email" name="email" placeholder="이메일을 입력하세요" required/>
              </div>
              <button class="btn btn-primary d-grid w-100" type="submit">아이디 찾기</button>
            </form>
            <p class="text-center">
              <span><a href="<c:url value='/login'/>">로그인 페이지로 돌아가기</a></span>
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