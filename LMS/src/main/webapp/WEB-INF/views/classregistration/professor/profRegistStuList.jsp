<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *	2025. 10. 19.		김수현			최초 제정
 *  2025. 10. 21.		김수현			학기 동적 생성 추가
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>수강신청 학생 목록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/profRegistStuList.css" />
</head>
<body>
	 <div class="professor-lecture-page">
        <div class="container">
            <div class="page-header">
                <h1>수강신청 현황</h1>
				<!-- 학기 선택 드롭다운 -->
		        <select id="yeartermSelector" class="form-select" style="width: 200px;" onchange="changeYearterm()">
				    <c:forEach var="yearTerm" items="${yearTermList}" varStatus="status">
				        <c:set var="termCode" value="${fn:substring(yearTerm.yeartermCd, 5, 9)}" />
				        <%-- SUB1 (하계학기)와 SUB2 (동계학기)는 출력하지 않도록 제외 --%>
				        <c:if test="${termCode != 'SUB1' and termCode != 'SUB2'}">

				            <option value="${yearTerm.yeartermCd}" ${status.first ? 'selected' : ''}>
				                <%-- 2025_REG1 => 2025학년도 1학기 변환 --%>
				                <c:set var="year" value="${fn:substring(yearTerm.yeartermCd, 0, 4)}" />
				                	${year}학년도
				                <c:choose>
				                    <c:when test="${termCode == 'REG1'}">1학기</c:when>
				                    <c:when test="${termCode == 'REG2'}">2학기</c:when>
				                    <c:otherwise>${termCode}</c:otherwise>
				                </c:choose>
				            </option>

				        </c:if>

				    </c:forEach>
				</select>
            </div>

			<!-- 통계(도넛차트랑 수강정보) -->
            <div class="stats-major-container">
			    <div class="stat-card stat-combined-summary">
			        <div class="summary-item">
			            <div class="stat-icon">📖</div>
			            <div class="stat-content">
			                <div class="stat-label">개설 강의 수</div>
			                <div class="stat-value" id="totalLectures">-</div>
			            </div>
			        </div>
			        <div class="summary-item">
			            <div class="stat-icon">👥</div>
			            <div class="stat-content">
			                <div class="stat-label">총 수강 학생</div>
			                <div class="stat-value" id="totalStudents">-</div>
			            </div>
			        </div>
			    </div>
			    <div class="stat-card chart-card stats-right-panel">
			        <div class="stat-content-chart">
				        <svg id="avgEnrollRateChart"></svg>
				        <div class="chart-value-overlay" id="avgEnrollRateText">0%</div>
				    </div>

				    <div class="chart-label-group">
				        <div class="chart-label">평균 정원 충족률</div>
				    </div>
			    </div>
			</div>

			<!-- 막대 그래프 -->
            <div class="summary-container">
                <div class="table-header">
                    <h2>📈 강의 현황 요약 (정원 충족률 비교)</h2>
                </div>
                <div class="prof-lecture-chart-wrapper">  <!-- 클래스명 변경 -->
                    <svg id="lectureBarChart"></svg>
                </div>
            </div>

			<!-- 강의 목록 테이블 -->
            <div class="table-container">
                <div class="table-header">
                    <h2>강의 목록</h2>

                </div>

                <table class="lecture-table">
                    <thead>
                        <tr>
                            <th style="width: 5%;">No</th>
                            <th style="width: auto;">과목명</th>
                            <th>강의실</th>
                            <th>시간</th>
                            <th>학점/시수</th>
                            <th>이수구분</th>
                            <th>대상학년</th>
                            <th style="width: 12%;">수강 정원</th>
                            <th style="width: 12%;">정원 충족률</th>
                        </tr>
                    </thead>
                    <tbody id="lectureTableBody">
                        <tr>
                            <td colspan="9" class="loading-message">
                                <div class="spinner"></div>
                                강의 목록을 불러오는 중...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
       </div> <!-- container 끝 -->

       <!-- 학생 모달(상세보기) -->
	    <div id="studentModal" class="modal">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h2 id="modalLectureTitle">강의명</h2>
	                <span class="close" onclick="closeModal()">&times;</span>
	            </div>

	            <div class="modal-stats">
	                <div class="modal-stat-item">
	                    현재 수강 학생: <strong id="modalCurrentEnroll">0</strong> / <span id="modalMaxCap">0</span>명
	                </div>
	                <div class="modal-stat-item">
	                    정원 충족률: <strong id="modalEnrollRate">0%</strong>
	                </div>
	            </div>

	            <div class="student-list-header">
	                <h3>수강신청 학생 목록</h3>
<!-- 	                <button class="btn-excel" onclick="exportToExcel()"> -->
<!-- 	                    <i class="bx bx-download"></i> 엑셀 다운로드 -->
<!-- 	                </button> -->
	            </div>

	            <div class="student-table-container">
	                <table class="student-table">
	                    <thead>
	                        <tr>
	                            <th style="width: 8%;">No</th>
	                            <th style="width: 12%;">학번</th>
	                            <th style="width: 10%;">이름</th>
	                            <th style="width: 8%;">학년</th>
	                            <th style="width: 15%;">단과대학</th>
	                            <th style="width: 15%;">학과</th>
	                            <th style="width: 18%;">신청일시</th>
	                        </tr>
	                    </thead>
	                    <tbody id="studentTableBody">
	                        <tr>
	                            <td colspan="7" class="loading-message">
	                                학생 목록을 불러오는 중...
	                            </td>
	                        </tr>
	                    </tbody>
	                </table>
	            </div>

	            <div class="modal-footer">
	                <div id="studentPagination" class="pagination"></div>
	            </div>
	        </div>
	    </div>

	</div> <!-- professor-lecture-page 끝 -->

    <script>
        const CONTEXT_PATH = "${pageContext.request.contextPath}";
        const PROFESSOR_NO = "${professorNo}";
        const YEARTERM_CD = "2026_REG1"; // 시연용
    </script>

    <script src="${pageContext.request.contextPath}/js/app/classregistration/profRegistStuList.js"></script>
    <script src="${pageContext.request.contextPath}/js/app/classregistration/wishlist-websocket.js"></script>

	<script>
	    document.addEventListener('DOMContentLoaded', function() {
	        console.log('교수 페이지 로드');

	        // 웹소켓 구독 (학생과 동일)
	        setTimeout(function() {
	            if (typeof connectWishlistWebSocket === 'function') {
	                connectWishlistWebSocket();
	            }
	        }, 2000);
	    });
	</script>
</body>
</html>