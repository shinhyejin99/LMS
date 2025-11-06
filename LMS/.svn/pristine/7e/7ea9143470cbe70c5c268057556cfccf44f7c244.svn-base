<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 27.     	김수현            최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>수강신청 관리</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/staffCourseManage.css">
</head>
<body>
	<!-- 최상위 wrapper -->
	<div class="staff-course-page">
        <div class="container">

            <div class="page-header">
                <h1>수강신청 관리</h1>

                <!-- 학기 선택 & 확정 버튼 -->
                <div class="header-actions">
                    <select id="yeartermSelector" class="form-select">
                        <option value="2026_REG1" ${currentYearterm == '2026_REG1' ? 'selected' : ''}>2026학년도 1학기</option>
                        <option value="2025_REG2" ${currentYearterm == '2025_REG2' ? 'selected' : ''}>2025학년도 2학기</option>
                        <option value="2025_REG1" ${currentYearterm == '2025_REG1' ? 'selected' : ''}>2025학년도 1학기</option>
                    </select>
                    <button class="btn-confirm" id="confirmEnrollmentBtn">
                        🔒 수강신청 확정
                    </button>
                </div>
            </div>

            <!-- 통계 카드 -->
            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-label">총 강의 수</div>
                    <div class="stat-value" id="totalLectures">0</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">총 수강생 수</div>
                    <div class="stat-value" id="totalStudents">0</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">평균 정원 충족률</div>
                    <div class="stat-value" id="avgEnrollRate">0%</div>
                </div>
            </div>

            <!-- 검색 -->
            <div class="search-section">
                <form id="searchForm">
                    <input type="text"
                           id="searchKeyword"
                           placeholder="강의명 또는 교수명을 입력하세요"
                           value="${searchKeyword}">
                    <button type="button" class="btn-search" id="searchBtn">검색</button>
                    <button type="button" class="btn-reset" id="resetBtn">초기화</button>
                </form>
            </div>

            <!-- 강의 목록 테이블 -->
            <div class="table-header">
                <h2>강의 목록</h2>
                <span class="total-count">총 <strong>${totalCount}</strong>건</span>
            </div>

            <table class="lecture-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>과목명</th>
                        <th>교수명</th>
                        <th>강의실</th>
                        <th>시간</th>
                        <th>학점/시수</th>
                        <th>이수구분</th>
                        <th>대상학년</th>
                        <th>정원</th>
                        <th>충족률</th>
                    </tr>
                </thead>
                <tbody id="lectureTableBody">
                    <c:choose>
                        <c:when test="${empty lectureList}">
                            <tr>
                                <td colspan="10" class="empty-message">강의가 없습니다.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="lecture" items="${lectureList}" varStatus="status">
                                <!-- 🆕 data 속성으로 정보 저장, onclick 없음 -->
                                <tr class="lecture-row"
                                    data-lecture-id="${lecture.lectureId}"
                                    data-lecture-name="${lecture.subjectName}">
                                    <td>${(currentPage - 1) * 10 + status.index + 1}</td>
                                    <td>${lecture.subjectName}</td>
                                    <td>${lecture.professorName}</td>
                                    <td>${lecture.placeName}</td>
                                    <td>${lecture.timeInfo}</td>
                                    <td>${lecture.credit}/${lecture.hour}</td>
                                    <td>${lecture.completionName}</td>
                                    <td>${lecture.targetGrades}</td>
                                    <td id="enroll-${lecture.lectureId}" class="enroll-info">
                                        <strong>${lecture.currentEnroll}</strong> / ${lecture.maxCap}
                                    </td>
                                    <td id="rate-${lecture.lectureId}" class="enroll-rate">
                                        ${lecture.enrollRate}%
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- 페이징 -->
            <c:if test="${totalPages > 0}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/lms/staff/course/manage/${currentYearterm}?keyword=${searchKeyword}&page=${currentPage - 1}">이전</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span class="current">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/lms/staff/course/manage/${currentYearterm}?keyword=${searchKeyword}&page=${i}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/lms/staff/course/manage/${currentYearterm}?keyword=${searchKeyword}&page=${currentPage + 1}">다음</a>
                    </c:if>
                </div>
            </c:if>

        </div> <!-- container 끝 -->
    </div> <!-- staff-course-page 끝 -->

    <!-- 학생 목록 모달 -->
    <div id="staffStudentModal" class="staff-modal" style="display: none;">
        <div class="staff-modal-content">
            <span class="staff-modal-close">&times;</span>

            <h2 id="staffModalLectureTitle">강의명</h2>

            <!-- 정원 정보 -->
            <div class="staff-modal-stats">
                <div class="staff-stat-item">
                    <span class="staff-stat-label">현재 인원:</span>
                    <strong id="staffModalCurrentEnroll">0</strong>명
                </div>
                <div class="staff-stat-item">
                    <span class="staff-stat-label">정원:</span>
                    <strong id="staffModalMaxCap">0</strong>명
                </div>
                <div class="staff-stat-item">
                    <span class="staff-stat-label">정원 충족률:</span>
                    <strong id="staffModalEnrollRate">0%</strong>
                </div>
            </div>

            <!-- 학생 목록 테이블 -->
            <table class="staff-student-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>학번</th>
                        <th>이름</th>
                        <th>학년</th>
                        <th>단과대학</th>
                        <th>학과</th>
                        <th>신청일시</th>
                    </tr>
                </thead>
                <tbody id="staffStudentTableBody">
                    <tr>
                        <td colspan="7" class="staff-loading-message">
                            <div class="staff-spinner"></div>
                            학생 목록을 불러오는 중...
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- 페이징 -->
            <div id="staffStudentPagination" class="staff-modal-pagination"></div>
        </div>
    </div>

    <!-- 전역 변수 설정 -->
    <script>
        const CONTEXT_PATH = '${pageContext.request.contextPath}';
        let currentYearterm = '${currentYearterm}';

        console.log('==> JSP 전역 변수 설정');
        console.log('CONTEXT_PATH:', CONTEXT_PATH);
        console.log('currentYearterm:', currentYearterm);
    </script>
    <script src="${pageContext.request.contextPath}/js/app/classregistration/staffCourseManage.js"></script>

</body>
</html>