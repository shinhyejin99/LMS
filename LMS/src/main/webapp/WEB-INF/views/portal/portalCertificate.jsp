<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025.10. 01.     	정태일            최초 생성
 * 	2025. 10. 14.		김수현			네비게이션 채용 탭 active 설정 변경
 * 	2025. 10. 15.		정태일			navbar 통합(중복제거)
 * 	2025. 10. 23.		정태일			미리보기 기능 수정
 *  2025. 10. 31.		김수현			css 추가
 *  2025. 10. 31.		정태일			css 추가
 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>

<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>증명서 발급</title>

    <!-- SweetAlert2 추가 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

	<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalCertificate.css">

</head>
<body class="certificate-page">
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="container">
    <div class="main">
    	<div class="breadcrumb">
            <a href="/portal">홈</a>
            <span>&gt;</span>
            <a href="/portal/certificate" class="breadcrumb-current">증명서</a>
        </div>

        <div >
            <h1 class="page-title">증명서 발급</h1>
        </div>

        <!-- 증명서 신청 섹션 -->
        <div class="section">
            <h3 class="section-title">증명서 신청</h3>
            <div class="card-body">
                <div class="application-container">
                    <div class="form-column">
                        <form id="certificate-form">
                            <div class="form-group">
                                <label for="cert-type" class="form-label">증명서 종류</label>
                                <select id="cert-type" class="form-control">
                                    <option value="">선택하세요</option>
                                    <c:forEach var="cert" items="${availableCertificates}">
                                        <option value="${cert.certificateCd}">${cert.certificateName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="cert-copies" class="form-label">발급 부수</label>
                                <input type="number" id="cert-copies" class="form-control" value="1" min="1">
                            </div>
                            <div class="form-group">
                                <label for="cert-submission" class="form-label">제출처</label>
                                <input type="text" id="cert-submission" class="form-control" placeholder="예: ABC 은행">
                            </div>
                            <div class="form-group">
                                <label for="cert-purpose" class="form-label">용도</label>
                                <input type="text" id="cert-purpose" class
="form-control" placeholder="예: 장학금 신청용">
                            </div>
                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">신청</button>
                            </div>
                        </form>
                    </div>
                    <div class="procedure-column">
                        <h4>증명서 발급 절차</h4>
                        <ol>
                            <li>증명서 종류, 부수, 제출처, 용도를 입력하고 '신청' 버튼을 클릭합니다.</li>
                            <li>'나의 증명서 발급 내역'에서 신청 상태를 확인합니다.</li>
                            <li>발급이 완료되면 '발급완료' 상태로 변경되고, 다운로드할 수 있습니다.</li>
                            <li>다운로드한 증명서는 지정된 만료일까지 유효합니다.</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>

        <!-- 나의 증명서 발급 내역 섹션 -->
        <div class="section">
             <h3 class="section-title">나의 증명서 발급 내역</h3>
            <div class="card-body">
                <table class="table">
                    <thead>
                        <tr>
                            <th>No</th>
                            <th>발급번호</th>
                            <th>증명서 종류</th>
                            <th>발급일</th>
                            <th>만료일</th>
                            <th>남은일수</th>
                            <th>상태</th>
                            <th>첨부파일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty certList}">
                                <tr>
                                    <td colspan="8" style="text-align: center;">증명서 발급 내역이 없습니다.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${certList}" var="cert" varStatus="status">
                                    <tr>
                                        <td>${status.count}</td>
                                        <td>${cert.certReqId}</td>
                                        <td>${cert.certificateName}</td>
                                        <td>${cert.formattedRequestAt}</td>
                                        <td>${cert.formattedExpireAt}</td>
                                        <td><span class="dday-normal">${cert.dDayText}</span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${cert.statusCd == 'PENDING'}">
                                                    <span class="badge badge-warning">대기</span>
                                                </c:when>
                                                <c:when test="${cert.statusCd == 'APPROVED'}">
                                                    <span class="badge badge-info">승인</span>
                                                </c:when>
                                                <c:when test="${cert.statusCd == 'REJECTED'}">
                                                    <span class="badge badge-danger">반려</span>
                                                </c:when>
                                                <c:when test="${cert.statusCd == 'ISSUED'}">
                                                    <span class="dday-normal">발급완료</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-secondary">${cert.statusCd}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
									    <button type="button" class="btn btn-outline-primary btn-sm btn-preview-history" data-cert-req-id="${cert.certReqId}">미리보기</button>
									    <button class="btn btn-primary btn-sm" onclick="location.href='/portal/certificate/download/${cert.certReqId}'">다운로드</button>
										</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>

<script src="/js/app/portal/portalCertificate.js"></script>

<!-- PDF 미리보기 모달 -->
	<div id="pdfPreviewModal" class="modal"
		style="display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0, 0, 0, 0.4);">
		<div class="modal-content"
			style="background-color: #fefefe; margin: 5% auto; padding: 20px; border: 1px solid #888; width: 90%; max-width: 1000px; height: 90%; position: relative;">
			<span class="close-button"
				style="color: #aaa; position: absolute; top: 10px; right: 10px; font-size: 28px; font-weight: bold; cursor: pointer;">&times;</span>
			<h3 class="modal-title" style="margin-bottom: 15px;">증명서 미리보기</h3>
			<div class="modal-body" style="height: calc(100% - 80px);">
				<iframe id="pdfViewer"
					style="width: 100%; height: 100%; border: none;"></iframe>
			</div>
		</div>
	</div>

</body>
</html>