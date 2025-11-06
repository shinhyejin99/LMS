<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 * 	2025. 10. 31.		정태일			최초 생성
 * 	2025. 10. 31.		정태일			로그인 UI 수정
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko" class="light-style customizer-hide" dir="ltr" data-theme="theme-default">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1, user-scalable=no"/>
  <title>로그인 | JSU LMS</title>
  <meta name="description" content="JSU LMS 로그인"/>

  <!-- Favicon -->
  <link rel="icon" type="image/x-icon" href="<c:url value='/sneat-1.0.0/assets/img/favicon/favicon.ico'/>"/>

  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=Public+Sans:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>

  <!-- Icons -->
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/fonts/boxicons.css'/>"/>

  <!-- Core CSS (Sneat 기본) -->
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/css/core.css'/>" />
  <link rel="stylesheet" href="<c:url value='/sneat-1.0.0/assets/vendor/css/theme-default.css'/>" />
  <!-- 페이지 전용 커스텀 CSS (반드시 마지막에) -->
  <link rel="stylesheet" href="<c:url value='/css/login-custom.css'/>"/>

  <!-- Helpers (Sneat) -->
  <script src="<c:url value='/sneat-1.0.0/assets/vendor/js/helpers.js'/>"></script>
</head>
<body class="lms-login-body">

<!-- ===== 레이아웃 컨테이너 ===== -->
<div class="login-layout-container">
  <!-- 왼쪽: 히어로 슬라이더 -->
  <div class="login-left-column">
    <div class="hero-slider" aria-label="캠퍼스 홍보 슬라이드">
      <div class="slider-container">
<div class="slide active" aria-hidden="false">
  <img src="${pageContext.request.contextPath}/images/slider1_re.png" alt="캠퍼스 이미지">
  <div class="slide-overlay"></div>
  <div class="slide-content">
    <h2 class="slide-title">미래를 설계하는 지식의 허브,<br/>변화를 주도하는 인재의 요람</h2>
    <p class="slide-caption">JSU 대학교</p>
  </div>
</div>

<div class="slide" aria-hidden="true">
  <img src="${pageContext.request.contextPath}/images/slider2.jpg" alt="도서관 이미지">
  <div class="slide-overlay"></div>
  <div class="slide-content">
    <h2 class="slide-title">지식의 전당, JSU 중앙도서관</h2>
    <p class="slide-caption">24시간 열린 학습 공간</p>
  </div>
</div>

<div class="slide" aria-hidden="true">
  <img src="${pageContext.request.contextPath}/images/slider3.jpg" alt="연구실 이미지">
  <div class="slide-overlay"></div>
  <div class="slide-content">
    <h2 class="slide-title">미래를 여는 연구, 혁신의 중심</h2>
    <p class="slide-caption">세계적 수준의 연구 환경</p>
  </div>
</div>
      </div>
    </div>
  </div>

  <!-- 오른쪽: 로그인 폼 -->
  <div class="login-right-column">
    <div class="authentication-wrapper authentication-basic container-p-y">
      <div class="authentication-inner">

        <div class="lms-logo-area">
			<img src="${pageContext.request.contextPath}/images/JSU대학교로고.png" alt="캠퍼스 이미지" class="lms-main-logo">
 	   </div>

        <div class="card lms-login-card">
          <div class="card-body">
            <!-- 로고/브랜드 -->
            <div class="app-brand justify-content-center lms-brand">
              <a href="<c:url value='/'/>" class="app-brand-link gap-2">
                <span class="app-brand-text demo text-body fw-bolder">JSU</span>
              </a>
            </div>

<!--             <h1 class="lms-heading">JSU LMS</h1> -->
            <p class="lms-subheading">계정으로 로그인하고 LMS 서비스를 시작하세요.</p>

            <!-- 메시지 -->
            <c:if test="${not empty message}">
              <div class="alert alert-success" role="status">${message}</div>
            </c:if>
            <c:if test="${param.error ne null}">
              <div class="alert alert-danger" role="alert">아이디 또는 비밀번호가 올바르지 않습니다.</div>
            </c:if>
            <c:if test="${param.logout ne null}">
              <div class="alert alert-success" role="status">로그아웃 되었습니다.</div>
            </c:if>

            <!-- 로그인 폼 -->
            <form id="formAuthentication" class="mb-3 lms-form" action="<c:url value='/login'/>" method="POST" novalidate>
              <div class="mb-3">
                <div class="d-flex justify-content-between align-items-center">
                  <label for="username" class="form-label">아이디</label>
                  <a class="link-underline" href="<c:url value='/portal/user/findid'/>"><small>아이디 찾기</small></a>
                </div>
                <input type="text"
                       class="form-control form-control-lg"
                       id="username"
                       name="username"
                       placeholder="학번/사번 또는 계정 ID"
                       autocomplete="username"
                       autofocus
                       required/>
              </div>

              <div class="mb-3 form-password-toggle">
                <div class="d-flex justify-content-between align-items-center">
                  <label class="form-label" for="password">비밀번호</label>
                  <a class="link-underline" href="<c:url value='/portal/user/resetpassword'/>"><small>비밀번호 찾기</small></a>
                </div>
                <div class="input-group input-group-merge">
                  <input type="password"
                         id="password"
                         class="form-control form-control-lg"
                         name="password"
                         placeholder="비밀번호"
                         aria-describedby="password"
                         autocomplete="current-password"
                         required/>
                  <span class="input-group-text cursor-pointer" title="비밀번호 보기">
                    <i class="bx bx-hide" aria-hidden="true"></i>
                  </span>
                </div>
              </div>

              <div class="mb-3 d-flex justify-content-between align-items-center">
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" id="remember-me" name="remember-me"/>
                  <label class="form-check-label" for="remember-me">로그인 상태 유지</label>
                </div>
                <div class="public-hint" aria-hidden="true">공용 PC 사용 시 체크 금지</div>
              </div>

              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

              <button class="btn btn-primary btn-lg w-100 lms-login-btn" type="submit">로그인</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- [DEV] 개발용 빠른 로그인 패널 (운영 전 이 블록 전체 삭제) -->
<div style="position:fixed;top:20px;left:20px;background:#fff;border:2px solid #ff3e1d;border-radius:8px;padding:15px;box-shadow:0 2px 10px rgba(0,0,0,0.1);z-index:9999;max-width:250px;">
  <div style="font-weight:bold;margin-bottom:10px;color:#ff3e1d;font-size:14px;">🚀 개발용 빠른 로그인</div>
  <div style="display:grid;gap:8px;">
    <button type="button" style="padding:8px;border:1px solid #ddd;border-radius:4px;background:#f8f9fa;cursor:pointer;font-size:13px;" onclick="quickLogin('202331801','java')">🎓 시연 학생</button>
    <button type="button" style="padding:8px;border:1px solid #ddd;border-radius:4px;background:#f8f9fa;cursor:pointer;font-size:13px;" onclick="quickLogin('20202181','java')">👨 시연 교수</button>
    <button type="button" style="padding:8px;border:1px solid #ddd;border-radius:4px;background:#f8f9fa;cursor:pointer;font-size:13px;" onclick="quickLogin('2023001','java')">👔 시연 직원</button>
  </div>
  <div style="margin-top:10px;font-size:11px;color:#999;text-align:center;">운영 전 제거</div>
</div>
<!-- [/DEV] 개발용 빠른 로그인 패널 끝 -->

<!-- 필수 JS (부트스트랩 의존) -->
<script src="<c:url value='/sneat-1.0.0/assets/vendor/libs/popper/popper.js'/>"></script>
<script src="<c:url value='/sneat-1.0.0/assets/vendor/js/bootstrap.js'/>"></script>

<!-- [DEV] 개발용 빠른 로그인 패널 (운영 전 이 블록 전체 삭제) -->
<script>
  function quickLogin(u, p) {
    var f = document.getElementById('formAuthentication');
    if (!f) return;
    f.username.value = u || '';
    f.password.value = p || '';
    var rm = document.getElementById('remember-me');
    if (rm) rm.checked = true;
    f.submit(); // 자동 제출
  }
</script>

<!-- 슬라이더: 30초 간격 자동 전환 -->
<script>
document.addEventListener("DOMContentLoaded", function () {
  const slides = document.querySelectorAll('.hero-slider .slide');
  if (!slides.length) return;
  let idx = 0;
  const dur = 5000; // 10초

  console.log("슬라이드 개수:", slides.length);

  setInterval(() => {
    slides[idx].classList.remove('active');
    slides[idx].setAttribute('aria-hidden', 'true');
    idx = (idx + 1) % slides.length;
    slides[idx].classList.add('active');
    slides[idx].setAttribute('aria-hidden', 'false');
    console.log("현재 슬라이드:", idx);
  }, dur);
});
</script>
</body>
</html>
