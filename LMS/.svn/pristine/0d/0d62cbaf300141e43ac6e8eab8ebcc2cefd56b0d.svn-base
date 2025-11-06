<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 16.     		정태일           최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<aside class="side-menu left">
  <ul class="side-nav">
    <li class="nav-item"><a href="${ctx}/portal/notice/list" class="nav-link">공지사항</a></li>
    <li class="nav-item"><a href="${ctx}/portal/academicnotice/list" class="nav-link">학사공지</a></li>
    <li class="nav-item"><a href="${ctx}/portal/univcalendar" class="nav-link">학사일정</a></li>

    <sec:authorize access="hasRole('STUDENT')">
      <li class="nav-item"><a href="${ctx}/portal/job/student" class="nav-link">채용정보</a></li>
    </sec:authorize>
    <sec:authorize access="!hasRole('STUDENT')">
      <li class="nav-item"><a href="${ctx}/portal/job/internal" class="nav-link">채용정보</a></li>
    </sec:authorize>

    <li class="nav-item"><a href="${ctx}/portal/facility" class="nav-link">시설예약</a></li>
    <li class="nav-item"><a href="${ctx}/portal/certificate" class="nav-link">증명서</a></li>
  </ul>
</aside>