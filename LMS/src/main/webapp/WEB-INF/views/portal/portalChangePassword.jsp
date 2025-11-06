<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 10.     		정태일           최초 생성
 *  2025. 10. 13.     		정태일           비밀번호 변경 메세지추가
 *
-->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"/>
  <title>비밀번호 변경 | JSU LMS</title>
  <meta name="description" content="JSU LMS 비밀번호 변경"/>
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
            <h4 class="mb-2">비밀번호 변경</h4>
            <p>새로운 비밀번호를 입력해주세요.</p>
            <c:if test="${not empty error}">
              <p class="mb-4 text-danger">${error}</p>
            </c:if>
            <form id="changePasswordForm" class="mb-3" action="<c:url value='/portal/user/changepassword'/>" method="POST">
              <div class="mb-3">
                <label for="username" class="form-label">아이디</label>
                <input type="text" class="form-control" id="username" name="username" value="${username}" readonly/>
                <input type="hidden" name="email" value="${email }"/>
              </div>
              <div class="mb-3 form-password-toggle">
                <label class="form-label" for="newPassword">새 비밀번호</label>
                <div class="input-group input-group-merge">
                  <input type="password" id="newPassword" class="form-control" name="newPassword" placeholder="&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;" aria-describedby="newPassword" required/>
                  <span class="input-group-text cursor-pointer"><i class="bx bx-hide"></i></span>
                </div>
              </div>
              <div class="mb-3 form-password-toggle">
                <label class="form-label" for="confirmPassword">새 비밀번호 확인</label>
                <div class="input-group input-group-merge">
                  <input type="password" id="confirmPassword" class="form-control" name="confirmPassword" placeholder="&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;" aria-describedby="confirmPassword" required/>
                  <span class="input-group-text cursor-pointer"><i class="bx bx-hide"></i></span>
                </div>
              </div>
              <button class="btn btn-primary d-grid w-100" type="submit">비밀번호 변경</button>
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
  <script src="<c:url value='/assets/js/main.js'/>"></script>
  <script>
    // 클라이언트 측 비밀번호 일치 확인 (선택 사항)
    document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
      const newPassword = document.getElementById('newPassword').value;
      const confirmPassword = document.getElementById('confirmPassword').value;
      if (newPassword !== confirmPassword) {
        alert('새 비밀번호와 비밀번호 확인이 일치하지 않습니다.');
        event.preventDefault(); // 폼 제출 방지
      }
    });
  </script>
</body>
</html>