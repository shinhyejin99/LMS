<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교직원 포털 - 내 정보</title>
    <style>
        /* 외부 래퍼 */
        .staff-info-page {
            padding: 20px;
            background-color: #f8f9fa;
            min-height: 100vh;
        }

        /* 메인 컨테이너 */
        .staff-info-page .staff-info-container {
            width: 100%;
            max-width: 100%;
            margin: 0;
            padding: 30px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            min-height: calc(100vh - 100px);
            box-sizing: border-box;
        }

        /* 페이지 헤더 */
        .staff-info-page .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e9ecef; /* 회색으로 */
        }

        .staff-info-page .page-header h1 {
            font-size: 23px;
            font-weight: bold;
            color: #333;
            margin: 0;
        }

        /* Breadcrumb */
        .staff-info-page .breadcrumb {
            display: flex;
            gap: 8px;
            list-style: none;
            padding: 0;
            margin: 0;
            font-size: 13px;
        }

        .staff-info-page .breadcrumb li {
            display: flex;
            align-items: center;
            color: #666;
        }

        .staff-info-page .breadcrumb li:not(:last-child)::after {
            content: '›';
            margin-left: 8px;
            color: #999;
        }

        .staff-info-page .breadcrumb a {
            color: #5b7ceb;
            text-decoration: none;
        }

        .staff-info-page .breadcrumb a:hover {
            text-decoration: underline;
        }

        .staff-info-page .breadcrumb .active {
            color: #666;
        }

        /* 본문 컨테이너 */
        .staff-info-page .py-4 {
            padding: 0;
        }

        /* 섹션 제목 */
        .staff-info-page h4 {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 2px solid #e9ecef;
        }

        .staff-info-page .mb-5 {
            margin-bottom: 35px;
        }

        /* 카드 */
        .staff-info-page .card {
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            box-shadow: none;
        }

        .staff-info-page .card-body {
            padding: 25px;
        }

        /* 그리드 행 */
        .staff-info-page .row {
            display: flex;
            flex-wrap: wrap;
            margin: 0 -7.5px;
        }

        .staff-info-page .row.g-3 {
            gap: 0;
        }

        .staff-info-page .col-md-6 {
            flex: 0 0 50%;
            max-width: 50%;
            padding: 0 7.5px;
            margin-bottom: 15px;
        }

        .staff-info-page .col-md-3 {
            flex: 0 0 25%;
            max-width: 25%;
            padding: 0 7.5px;
            margin-bottom: 15px;
        }

        .staff-info-page .col-md-9 {
            flex: 0 0 75%;
            max-width: 75%;
            padding: 0 7.5px;
            margin-bottom: 15px;
        }

        .staff-info-page .col-12 {
            flex: 0 0 100%;
            max-width: 100%;
            padding: 0 7.5px;
            margin-bottom: 15px;
        }

        .staff-info-page .col-lg-9 {
            flex: 0 0 75%;
            max-width: 75%;
            padding: 0 7.5px;
        }

        .staff-info-page .col-lg-3 {
            flex: 0 0 25%;
            max-width: 25%;
            padding: 0 7.5px;
        }

        /* 정보 필드 */
        .staff-info-page .info-field {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 12px 15px;
        }

        .staff-info-page .info-field .fw-bold {
            font-size: 13px;
            font-weight: 600;
            color: #495057;
            margin-bottom: 6px;
        }

        .staff-info-page .info-field-text {
            font-size: 14px;
            color: #212529;
            font-weight: 400;
        }

        /* 폼 라벨 */
        .staff-info-page .form-label {
            font-size: 13px;
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
            display: block;
        }

        .staff-info-page .form-control {
            width: 100%;
            padding: 10px 15px;
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            font-size: 14px;
            color: #212529;
            box-sizing: border-box;
        }

        /* 사진 영역 */
        .staff-info-page .text-center {
            text-align: center;
        }

        .staff-info-page .text-center img {
            width: 180px;
            height: 220px;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            padding: 10px;
            background: #f8f9fa;
            object-fit: cover;
            margin-bottom: 15px;
        }

        .staff-info-page .text-center .fw-bold {
            font-size: 16px;
            font-weight: 600;
            color: #212529;
        }

        /* 구분선 */
        .staff-info-page hr {
            border: none;
            height: 1px;
            background: #dee2e6;
            margin: 20px 0;
        }

        /* 버튼 */
        .staff-info-page .text-end {
            text-align: right;
        }

        .staff-info-page .mt-1 {
            margin-top: 15px;
        }

        .staff-info-page .btn {
            padding: 8px 20px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.2s;
        }

        .staff-info-page .btn-sm {
            padding: 6px 16px;
            font-size: 13px;
        }

        .staff-info-page .btn-primary {
            background-color: #007bff;
            color: white;
        }

        .staff-info-page .btn-primary:hover {
            background-color: #0056b3;
        }

        /* 반응형 */
        @media (max-width: 992px) {
            .staff-info-page .col-lg-9,
            .staff-info-page .col-lg-3 {
                flex: 0 0 100%;
                max-width: 100%;
            }
        }

        @media (max-width: 768px) {
            .staff-info-page {
                padding: 10px;
            }

            .staff-info-page .staff-info-container {
                padding: 20px;
                border-radius: 8px;
            }

            .staff-info-page .col-md-6,
            .staff-info-page .col-md-3,
            .staff-info-page .col-md-9 {
                flex: 0 0 100%;
                max-width: 100%;
            }
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

    <!-- 외부 래퍼 -->
    <div class="staff-info-page">
        <div class="staff-info-container">

            <!-- 페이지 헤더 -->
            <div class="page-header">
                <h1>교직원 개인신상 정보 페이지</h1>
                <ol class="breadcrumb">
                    <li><a href="/staff">Home</a></li>
                    <li>개인정보</li>
                    <li class="active" aria-current="page">내 정보</li>
                </ol>
            </div>

            <div class="py-4">
                <div class="mb-5">
                    <h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">인사정보</h4>
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <div class="row">
                                <div class="col-lg-9">
                                    <div class="row g-3">

                                        <%-- 1. 사번 --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                                <div class="fw-bold text-dark mb-0 me-3">사번</div>
                                                <div class="info-field-text">${userStaffDTO.staffInfo.staffNo}</div>
                                            </div>
                                        </div>

                                        <%-- 2. 이름 --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                                <div class="fw-bold text-dark mb-0 me-3">이름</div>
                                                <div class="info-field-text">${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}</div>
                                            </div>
                                        </div>

                                        <%-- 3. 주민번호 (마스킹) --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                                <div class="fw-bold text-dark mb-0 me-3">주민번호</div>
                                                <div id="regiNo" class="info-field-text">
                                                     <c:choose>
                                                        <c:when test="${not empty userStaffDTO.userInfo.regiNo and fn:length(userStaffDTO.userInfo.regiNo) ge 7}">
                                                            ${fn:substring(userStaffDTO.userInfo.regiNo, 0, 6)}-${fn:substring(userStaffDTO.userInfo.regiNo, 6, 7)}******
                                                        </c:when>
                                                        <c:otherwise>
                                                            정보 없음
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>

                                        <%-- 4. 성별 --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                                <div class="fw-bold text-dark mb-0 me-3">성별</div>
                                                <div id="genderSelect" class="info-field-text">
                                                    <c:set var="genderCode" value="${fn:substring(userStaffDTO.userInfo.regiNo, 6, 7)}" />
                                                    <c:choose>
                                                        <c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}"> 남성 </c:when>
                                                        <c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}"> 여성 </c:when>
                                                        <c:otherwise> 정보 없음 </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>

                                        <%-- 5. 소속 부서 --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                                <div class="fw-bold text-dark mb-0 me-3">소속 부서</div>
                                                <div class="info-field-text">${userStaffDTO.staffDeptInfo.stfDeptName}</div>
                                            </div>
                                        </div>

                                        <%-- 6. 학내일반전화 --%>
                                        <div class="col-md-6">
                                            <div class="border rounded p-2 bg-light info-field">
                                              <div class="fw-bold text-dark mb-0 me-3">학내일반전화</div>
                                                <div class="info-field-text">${userStaffDTO.staffInfo.teleNo}</div>
                                              </div>
                                        </div>

                                    </div>
                                </div>

                                <%-- 사진 영역 --%>
                                <div class="col-lg-3">
                                 <div class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty userStaffDTO.userInfo.photoId}">
                                            <c:url var="photoUrl" value="/devtemp/files/idphoto">
                                                <c:param name="fileId" value="${userStaffDTO.userInfo.photoId}"/>
                                            </c:url>
                                            <img src="${photoUrl}"
                                                 alt="${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName} 증명사진"
                                                 class="border border-2 border-secondary rounded p-4 mb-3 bg-light">

                                        </c:when>
                                        <c:otherwise>
                                                <div class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center" style="width: 120px; height: 150px; margin: 0 auto;">
                                                    <span class="text-muted">사진 없음</span>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    <div class="fw-bold text-dark">${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="mb-5">
                    <h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">연락 및 금융 정보</h4>
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <div class="row g-3">

                                <%-- 1. 이메일 --%>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold text-dark">이메일</label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.userInfo.email}
                                    </div>
                                </div>

                                <%-- 2. 휴대전화 --%>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold text-dark">
                                         휴대전화
                                    </label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.userInfo.mobileNo}
                                    </div>
                                </div>

                                <%-- 3. 우편번호 --%>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold text-dark">
                                         우편번호
                                    </label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.addressInfo.zipCode}
                                    </div>
                                </div>

                                <%-- 4. 기본 주소 --%>
                                <div class="col-md-9">
                                    <label class="form-label fw-semibold text-dark">
                                         주소
                                    </label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.addressInfo.baseAddr}
                                    </div>
                                </div>

                                <%-- 5. 상세 주소 (전체 너비 사용) --%>
                                <div class="col-12">
                                    <label class="form-label fw-semibold text-dark">
                                         상세주소
                                    </label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.addressInfo.detailAddr}
                                    </div>
                                </div>

                                <div class="col-12"><hr class="my-0 mt-2 mb-2"></div>

                                <%-- 6. 예금주 --%>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold text-dark">
                                         예금주
                                    </label>
                                    <div class="form-control border-2 bg-light info-field info-field-text p-2">
                                        ${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}
                                    </div>
                                </div>

                                <%-- 7. 은행 구분 (Bank Code Name) --%>
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold text-dark">
                                         은행구분
                                    </label>
                                    <div class="form-control border-2 bg-light info-field info-field-text p-2">
                                        <c:forEach var="bank" items="${bankList}">
                                            <c:if test="${bank.commonCd eq userBankCd}">
                                                ${bank.cdName}
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </div>

                                <%-- 8. 계좌번호 --%>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold text-dark">
                                         계좌번호
                                    </label>
                                    <div class="form-control border-2 bg-white info-field info-field-text p-2">
                                        ${userStaffDTO.userInfo.bankAccount}
                                    </div>
                                </div>

                            </div>

                            <div class="text-end mt-1">
                                <a href="<c:url value='/lms/staff/mypage/modify'/>" class="btn btn-sm btn-primary">수정</a>
                            </div>
                        </div>
                </div>
            </div>
        </div>

        </div>
    </div>

</body>
</html>