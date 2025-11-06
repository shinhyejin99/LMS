<%-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      	수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 29.    송태호            최초 작성
 *
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<nav class="navbar navbar-dark">
	<div class="container-fluid d-flex align-items-center">
		<!-- 브랜드 영역 -->
		<div class="d-flex align-items-center me-auto gap-2">
			<a class="navbar-brand fw-bold m-0" href="/classroom">Classroom</a>
			<c:if test="${not empty lectureId}">
				<span class="navbar-text text-white fw-semibold ms-2" data-lecture-subject-label="true">강의 정보</span>
				<button type="button" class="btn btn-link nav-link text-white-50 p-0 ms-1" data-lecture-info-trigger="true" data-lecture-id="<c:out value='${lectureId}'/>" data-ctx="${pageContext.request.contextPath}" data-api="${pageContext.request.contextPath}/classroom/api/v1/common" aria-label="강의 정보 보기">
					<i class="bi bi-info-circle" aria-hidden="true"></i> 강의 정보
				</button>
			</c:if>
		</div>
		<!-- 내비게이션 링크 -->
		<ul class="navbar-nav flex-row ms-auto align-items-center">
			<li class="nav-item me-3" id="user-info-container" style="cursor: pointer;" data-user-role="student">
				<span class="navbar-text" id="user-info-text">
					<i class="bi bi-person-circle me-1"></i> 내 정보
				</span>
			</li>
			<li class="nav-item"><a class="nav-link p-0 text-danger" href="/logout">로그아웃</a></li>
		</ul>
	</div>
</nav>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/common/lectureInfoTooltip.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/common/userInfo.js"></script>
