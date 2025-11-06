<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교수 포털 - 내 정보</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />
</head>
<body>
    <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

    <!-- 외부 래퍼 -->
    <div class="student-personal-info-page">.
        <div class="student-info-container">

            <!-- 페이지 헤더 -->
            <div class="page-header">
                <h1>개인정보</h1>
            </div>

            <!-- 인사정보 섹션 -->
            <div class="info-card">
                <div class="info-card-header">
                    <h2 class="info-card-title">인사정보</h2>
                </div>
                <div class="info-card-body">
                    <div class="stu-info-layout">
                        <div class="stu-info-flex-container">

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">교번</div>
                                <div class="stu-item-value">${professor.professorNo}</div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">이름</div>
                                <div class="stu-item-value">${professor.lastName}${professor.firstName} / ${professor.engLname} ${professor.engFname}</div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">주민번호</div>
                                <div class="stu-item-value">
                                    <c:if test="${not empty professor.regiNo and fn:length(professor.regiNo) ge 7}">
                                        ${fn:substring(professor.regiNo, 0, 6)}-${fn:substring(professor.regiNo, 6, 7)}******
                                    </c:if>
                                </div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">성별</div>
                                <div class="stu-item-value">
                                    <c:set var="genderCode" value="${fn:substring(professor.regiNo, 6, 7)}" />
                                    <c:choose>
                                        <c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}">남성</c:when>
                                        <c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}">여성</c:when>
                                        <c:otherwise>정보 없음</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">단과대학</div>
                                <div class="stu-item-value">${professor.collegeName}</div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">소속학과</div>
                                <div class="stu-item-value">${professor.departmentName}</div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">직급</div>
                                <div class="stu-item-value">
                                    <c:choose>
                                        <c:when test="${not empty professor.positionName}">${professor.positionName}</c:when>
                                        <c:otherwise>${professor.employmentType}</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="stu-item-wrapper">
                                <div class="stu-item-label">임용일자</div>
                                <div class="stu-item-value">
                                    <fmt:parseDate value="${professor.employmentDate}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedEmploymentDate"/>
                                    <fmt:formatDate value="${parsedEmploymentDate}" pattern="yyyy-MM-dd"/>
                                </div>
                            </div>
                        </div>
                        <div class="info-photo">
                            <c:choose>
                                <c:when test="${not empty professor.photoId}">
                                    <c:url var="photoUrl" value="/devtemp/files/idphoto">
                                        <c:param name="fileId" value="${professor.photoId}"/>
                                    </c:url>
                                    <img src="${photoUrl}" alt="증명사진" class="photo-img">
                                </c:when>
                                <c:otherwise>
                                    <div class="photo-placeholder"><span>증명사진 없음</span></div>
                                </c:otherwise>
                            </c:choose>
                            <div class="photo-name">${professor.lastName}${professor.firstName}</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 연락 및 금융 정보 섹션 -->
            <div class="info-card">
                <div class="info-card-header">
                    <h2 class="info-card-title">연락 및 금융 정보</h2>
                </div>
                <div class="info-card-body">
                    <div class="stu-info-flex-container">
                        <div class="stu-item-wrapper stu-col-2">
                            <div class="stu-item-label">이메일</div>
                            <div class="stu-item-value">${professor.email}</div>
                        </div>
                        <div class="stu-item-wrapper stu-col-2">
                            <div class="stu-item-label">휴대전화</div>
                            <div class="stu-item-value">${professor.mobileNo}</div>
                        </div>
                        <div class="stu-item-wrapper stu-col-2">
                            <div class="stu-item-label">연구실 전화번호</div>
                            <div class="stu-item-value">${professor.officeNo}</div>
                        </div>
                        <div class="stu-item-wrapper stu-col-2">
                            <div class="stu-item-label">우편번호</div>
                            <div class="stu-item-value">${professor.zipCode}</div>
                        </div>
                        <div class="stu-item-wrapper" style="width: 100%;">
                            <div class="stu-item-label">주소</div>
                            <div class="stu-item-value">${professor.baseAddr} ${professor.detailAddr}</div>
                        </div>
                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">예금주</div>
                            <div class="stu-item-value">${professor.lastName}${professor.firstName}</div>
                        </div>
                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">은행</div>
                            <div class="stu-item-value">${professor.bankName}</div>
                        </div>
                        <div class="stu-item-wrapper stu-col-2">
                            <div class="stu-item-label">계좌번호</div>
                            <div class="stu-item-value">${professor.bankAccount}</div>
                        </div>
                    </div>
                    <div class="form-actions">
                        <a href="<c:url value='/lms/professor/info/modify'/>" class="btn-submit">수정</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>