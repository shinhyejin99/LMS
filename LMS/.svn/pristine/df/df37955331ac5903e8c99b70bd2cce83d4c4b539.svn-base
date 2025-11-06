<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 15.     		정태일           최초 생성
 *  2025. 10. 16.     		정태일           시설예약 view 생성
 *  2025. 10. 20.     		정태일           대시보드 피드백 수정(학사관리버튼추가)
 *  2025. 10. 30.     		김수현           구조 변경
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!-- portal-navbar - 2단 구조 (Bootstrap 충돌 방지) -->
<nav class="portal-navbar">
    <!-- 상단 영역 -->
    <div class="portal-navbar-top">
        <!-- 로고 영역 -->
        <div class="portal-navbar-logo">
            <a href="/portal" class="portal-logo-link">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="JSU 로고" class="portal-logo-image">
            </a>
        </div>

        <!-- 우측 버튼 영역 -->
        <div class="portal-navbar-top-right">
            <ul class="portal-navbar-nav flex-row align-items-center">
                <li class="nav-item dropdown position-relative">
                    <a href="#" class="nav-link p-0" id="notificationDropdown"
                       data-bs-toggle="dropdown" aria-expanded="false" title="알림">
                        <i class='bx bx-bell' style="font-size:1.3rem;line-height:1;display:inline-block;"></i>
                        <span id="unread-count-badge"
                              class="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle"
                              style="display:none;">0</span>
                    </a>

                   <div id="notification-dropdown-menu" class="dropdown-menu dropdown-menu-end shadow border-0 p-0"
                         aria-labelledby="notificationDropdown"
                         style="min-width: 450px; max-width: 480px; z-index: 1100; border-radius: 0.75rem;">

                        <div class="d-flex justify-content-between align-items-center dropdown-header border-bottom py-3 px-4 bg-white">
                            <h6 class="mb-0 fw-bold text-dark">알림</h6>
                            <span class="badge bg-primary rounded-pill" id="dropdown-unread-count" style="font-size:0.8rem;padding:0.35em 0.65em;">0</span>
                        </div>
                        <div id="dropdown-list-container" class="list-group list-group-flush">
                            <a class="list-group-item text-center text-muted" href="#" style="border:none;cursor:default;">로딩 중...</a>
                        </div>
                        <a class="dropdown-item text-center fw-bold text-primary py-3 border-top"
                           href="${pageContext.request.contextPath}/lms/notifications" style="font-size:0.95rem;">
                          전체 알림 보기 &gt;
                        </a>
                    </div>
                </li>
            </ul>

            <!-- 비밀번호 변경 버튼 추가 -->
		    <button type="button" class="btn portal-password-btn" id="openPwModal">
		        <i class='bx bx-lock-alt' style="font-size:1.1rem;"></i>
		        비밀번호변경
		    </button>

            <button class="btn btn-link portal-logout-btn" onclick="location.href='/logout'">로그아웃</button>
        </div>
    </div>

    <!-- 하단 메뉴 영역 -->
    <div class="portal-navbar-bottom">
        <ul class="portal-navbar-nav-main">
            <li class="portal-nav-item"><a href="/portal/notice/list" class="portal-nav-link">공지사항</a></li>
            <li class="portal-nav-item"><a href="/portal/academicnotice/list" class="portal-nav-link">학사공지</a></li>
            <li class="portal-nav-item"><a href="/portal/univcalendar" class="portal-nav-link">학사일정</a></li>
            <li class="portal-nav-item">
                <sec:authorize access="hasRole('STUDENT')">
                    <a href="/portal/job/student" class="portal-nav-link">채용정보</a>
                </sec:authorize>
                <sec:authorize access="!hasRole('STUDENT')">
                    <a href="/portal/job/internal" class="portal-nav-link">채용정보</a>
                </sec:authorize>
            </li>
            <li class="portal-nav-item"><a href="/portal/facility/reservation" class="portal-nav-link">시설예약</a></li>
            <li class="portal-nav-item"><a href="/portal/certificate" class="portal-nav-link">증명서</a></li>
            <li class="portal-nav-item portal-nav-item-highlight">
                <sec:authorize access="hasRole('STUDENT')">
                    <a href="/student" class="portal-nav-link">학사관리</a>
                </sec:authorize>
                <sec:authorize access="hasRole('PROFESSOR')">
                    <a href="/professor" class="portal-nav-link">학사관리</a>
                </sec:authorize>
                <sec:authorize access="hasAnyRole('STAFF', 'ADMIN')">
                    <a href="/staff" class="portal-nav-link">학사관리</a>
                </sec:authorize>
            </li>
        </ul>
    </div>
</nav>