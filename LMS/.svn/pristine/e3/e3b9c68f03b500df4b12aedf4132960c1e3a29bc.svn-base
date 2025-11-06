<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>교직원 대시보드 - 교직원시스템</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<style>
/* 🚀🚀 최종 레이아웃 및 극한의 공간 최적화 */

/* 1. HTML, BODY, 전체 컨테이너 높이 100% 설정 (페이지 꽉 채움) */
html, body {
    height: 100%;
    margin: 0;
    padding: 0;
}
body {
  background: #f1f3f6; /* 약간 더 밝은 회색 배경 */
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', 'Noto Sans KR', 'Malgun Gothic', '맑은 고딕', Roboto, sans-serif
  font-size: 0.85rem;
  overflow: hidden; /* 페이지 전체 스크롤 제거 */
  display: flex;
  flex-direction: column; /* body를 flex-column으로 설정 */
}

/* 2. 메인 콘텐츠 영역 높이 관리 */
.container-fluid.py-4 {
    padding-top: 0.5rem !important;
    padding-bottom: 0.3rem !important;
    flex-grow: 1; /* 남은 모든 공간을 차지 */
    display: flex;
    flex-direction: column;
}
.row {
    margin-top: 0;
    flex-grow: 1; /* 남은 공간 모두 사용 */
    /* 🌟 열 높이 동기화를 위한 핵심 CSS */
    display: flex;
    align-items: stretch;
}

/* 3. Footer 수정 (body 하단에 고정) */
.dashboard-footer {
    flex-shrink: 0;
    padding: 0.5rem 0;
    font-size: 0.75rem;
    text-align: center;
    color: #6c757d;
    background-color: #fff; /* footer 배경색 */
    width: 100%;
    border-top: 1px solid #e0e0e0; /* 약간 더 뚜렷한 경계선 */
}

/* --- 공통 스타일 --- */
.card {
    border: 1px solid #e0e0e0; /* 얇은 테두리 추가 */
    border-radius: 8px; /* 둥근 모서리 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* 부드러운 그림자 */
    margin-bottom: 8px; /* 마진 증가 (하단 카드와 간격) */
    display: flex;
    flex-direction: column;
    transition: transform 0.2s;
}
.card:hover {
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}
.card-header {
    padding: 0.5rem 1rem; /* 패딩 증가 */
    font-size: 0.95rem;
    background-color: #f7f7f7; /* 헤더 배경색 */
    border-bottom: 1px solid #e0e0e0;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
}
.col-lg-4 {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-left: 0.4rem; /* 여백 조정 */
    padding-right: 0.4rem; /* 여백 조정 */
}
.col-lg-4:first-child {
    padding-left: 0.75rem;
}
.col-lg-4:last-child {
    padding-right: 0.75rem;
}

/* --- 3열 (학사일정 - 높이 기준) --- */
/* 학사일정은 3번째 컬럼에 있으므로 nth-child(3)을 기준으로 설정 */
.col-lg-4:nth-child(3) .card {
    flex-grow: 1; /* 3열의 학사일정 카드가 높이의 기준 */
    margin-bottom: 0; /* 3열은 카드 하나만 있으므로 하단 마진 제거 */
}
.academic-calendar-container {
    padding: 0.1rem;
    height: 100%;
}
#dashboard-calendar-wrapper {
    height: 100%;
}
.fc {
    height: 100%;
    font-size: 0.8em; /* 캘린더 폰트 크기 약간 증가 */
}
.fc .fc-toolbar-title {
    font-size: 1.1em;
    color: #34495e;
}

/* --- 통계 카드 그리드 공통 (Metric Card) --- */
.metric-grid {
    flex-shrink: 0;
    margin-bottom: 8px; /* 마진 증가 */
    height: 120px;
}
.metric-card {
    margin-bottom: 0 !important; /* 내부 카드의 중복 마진 제거 */
    border-radius: 8px;
    height: calc(100% - 6px); /* 부모 높이 120px에서 row의 gap-y 효과 (6px) 제외 */
    display: flex;
    align-items: center;
    overflow: hidden;
    position: relative; /* 아이콘 위치 기준 */
}
/* 통계 카드 색상 테마 생략 */

/* 🚨🚨 1열 하단 공지사항 & 학사공지 2분할 영역 (높이 동기화의 핵심) */
/* notice-and-academic-split는 1번째 컬럼에 있음 */
.notice-and-academic-split {
    /* 🌟 수정: 남은 공간 높이를 calc()로 계산하여 CSS 기반 높이 동기화 */
    height: calc(100% - 120px - 8px); /* 100% - metric-grid 높이(120px) - metric-grid 마진(8px) */
    flex-grow: 0;
    display: flex;
    gap: 8px; /* 카드 간 간격 */
    margin-bottom: 0;
}
.notice-and-academic-split .card {
    flex: 1; /* 두 카드가 1:1로 공간 분할 */
    margin-bottom: 0; /* 분할된 카드 자체 마진 제거 */
    height: 100%; /* 부모의 높이를 상속받아 꽉 채움 */
}

/* 2열 하단 주요업무/채용정보 영역 */
/* tasks-and-jobs-container는 2번째 컬럼에 있음 */
.tasks-and-jobs-container {
    display: flex;
    flex-direction: column;
    /* 🌟 수정: 남은 공간 높이를 calc()로 계산하여 CSS 기반 높이 동기화 */
    height: calc(100% - 120px - 8px); /* 100% - metric-grid 높이(120px) - metric-grid 마진(8px) */
    flex-grow: 0;
    gap: 8px; /* 카드 간 간격 */
    margin-bottom: 0;
}
.tasks-and-jobs-container .card {
    flex-grow: 1;
    margin-bottom: 0;
}


/* --- 목록 공통 스타일 --- */
.job-list-item .urgent-text {
    font-weight: bold;
    color: #e74c3c; /* 빨간색 텍스트 */
    font-size: 0.85rem;
    margin-right: 5px;
    flex-shrink: 0;
}
/* 카드의 body에 스크롤 적용 및 flex 설정 */
.card-body.p-0 {
    flex-grow: 1;
    overflow-y: hidden;
    display: flex;
    flex-direction: column;
}
/* 실제 스크롤 되는 목록 ul */
.card-body.p-0 ul {
    list-style: none;
    padding: 0;
    margin: 0;
    flex-grow: 1;
    overflow-y: auto;
    padding-right: 5px; /* 스크롤바 공간 확보 */
}
/* 스크롤바 디자인 (Webkit) 생략 */
</style>
</head>
<body>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<%-- 다른 스크립트 파일 생략 --%>
<script src="/js/app/portal/portalDashboard.js"></script>

<div class="container-fluid py-4">

  <div class="row">

    <%-- 1열: 기존 2열 내용 (총 학생 수, 총 교원 수 통계 + 공지사항/학사공지) --%>
    <div class="col-lg-4">
        <%-- 통계 카드 2개 (총 학생 수, 총 교원 수) --%>
        <div class="row metric-grid">
            <div class="col-6">
              <div class="card metric-card">
                <div class="card-body">
                  <div class="icon text-primary"><i class="bi bi-people-fill"></i></div>
                  <h5><c:out value="${totalStudents != null ? totalStudents : '0'}" />명</h5>
                  <p class="text-secondary">총 학생 수</p>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="card metric-card">
                <div class="card-body">
                  <div class="icon text-success"><i class="bi bi-person-badge"></i></div>
                  <h5><c:out value="${totalFaculty != null ? totalFaculty : '0'}" />명</h5>
                  <p class="text-secondary">총 교원 수</p>
                </div>
              </div>
            </div>
        </div>
 <%-- 주요 업무 및 채용 정보 (남은 공간 1:1 분할, 높이 타겟) --%>
        <div class="tasks-and-jobs-container">

            <%-- 주요 업무 카드 (데이터 연동) --%>
            <div class="card">
                <div class="card-header"><i class="bi bi-check2-square me-2 text-info"></i>처리해야 할 주요 업무</div>
                <div class="card-body p-0">
                    <div class="list-group list-group-flush">

                        <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            <span><i class="bi bi-people me-2 text-success"></i>학생 학적 / 재적 현황</span>
                            <span class="badge bg-warning rounded-pill"><c:out value="${studentApplicationCount != null ? studentApplicationCount : '25'}" />건</span>
                        </a>
                        <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            <span><i class="bi bi-easel me-2 text-info"></i>강의 시간표 배정 요청</span>
                            <span class="badge bg-info rounded-pill"><c:out value="${courseRequestCount != null ? courseRequestCount : '12'}" />건</span>
                        </a>
                        <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            <span><i class="bi bi-bell me-2 text-secondary"></i>미처리 민원</span>
                            <span class="badge bg-secondary rounded-pill"><c:out value="${pendingComplaintCount != null ? pendingComplaintCount : '20'}" />건</span>
                        </a>
                    </div>
                </div>
            </div>

            <%-- 채용정보 카드 --%>
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark"></i>💼 채용정보</span>
                    <a href="/portal/job/internal" class="text-decoration-none text-muted small">더보기 +</a>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="jobNotice" items="${jobNotices}" end="4">
                            <li class="list-group-item job-list-item">
                                <a href="/portal/job/internal/${jobNotice.recruitId}" class="title-link text-dark text-decoration-none">
                                    ${jobNotice.title}
                                </a>
                                <span class="date-info">${jobNotice.recStartDay}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty jobNotices}">
                            <li class="list-group-item text-center text-muted job-list-item p-3">등록된 채용정보가 없습니다.</li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>

    </div>


    <%-- 2열: 기존 3열 내용 (시설 예약률, 등록금 납부율 통계 + 주요 업무/채용 정보) --%>
    <div class="col-lg-4">

        <%-- 통계 카드 2개 (시설 예약률, 등록금 납부율) --%>
        <div class="row metric-grid">
            <div class="col-6">
              <div class="card metric-card">
                <div class="card-body">
                  <div class="icon text-warning"><i class="bi bi-building-check"></i></div>
                  <h5><c:out value="${facilityReservationRate != null ? facilityReservationRate : '0'}" />%</h5>
                  <p class="text-secondary">시설 예약률</p>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="card metric-card">
                <div class="card-body">
                  <div class="icon text-danger"><i class="bi bi-cash-stack"></i></div>
                  <h5><c:out value="${tuitionPaymentRate != null ? tuitionPaymentRate : '25'}" />%</h5>
                  <p class="text-secondary">등록금 납부율</p>
                </div>
              </div>
            </div>
        </div>

        <%-- 주요 업무 및 채용 정보 (남은 공간 1:1 분할, 높이 타겟) --%>
        <%-- 🚨🚨 공지사항 & 학사공지 2분할 카드 (높이 타겟) 🚨🚨 --%>
        <div class="notice-and-academic-split">

            <%-- 📢 1. 공지사항 카드 --%>
            <div class="card notice-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark"><i class="bi bi-megaphone me-2 text-danger"></i>📢 공지사항</span>
                    <a href="/portal/notice/list" class="text-decoration-none text-muted small">더보기 +</a>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="notice" items="${generalNotices}" end="7">
                            <li class="list-group-item job-list-item">
                                <a href="/portal/notice/detail/${notice.noticeId}" class="title-link text-dark text-decoration-none">
                                  <c:if test="${notice.isUrgent == 'Y'}">
                                       <span class="urgent-text">긴급</span>
                                    </c:if>
                                <span class="title">${notice.title}</span>
                                </a>
                                <span class="date-info">${notice.createAt != null ? fn:substring(notice.createAt, 0, 10) : ''}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty generalNotices}">
                            <li class="list-group-item text-center text-muted job-list-item p-3">등록된 공지사항이 없습니다.</li>
                        </c:if>
                    </ul>
                </div>
            </div>

            <%-- 🎓 2. 학사공지 카드 --%>
            <div class="card academic-card">
                <div  class="card-header d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark"><i class="bi bi-mortarboard me-2 text-primary"></i>🎓 학사공지</span>
                    <a href="/portal/academicnotice/list" class="text-decoration-none text-muted small">더보기 +</a>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="notice" items="${academicNotices}" end="7">
                            <li class="list-group-item job-list-item">
                                <a href="/portal/academicnotice/detail/${notice.noticeId}" class="title-link text-dark text-decoration-none">
                                <span class="title">${notice.title}</span>
                                </a>
                                <span class="date-info">${notice.createAt != null ? fn:substring(notice.createAt, 0, 10) : ''}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty academicNotices}">
                            <li class="list-group-item text-center text-muted job-list-item p-3">등록된 학사공지가 없습니다.</li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>


    <%-- 3열: 기존 1열 내용 (학사일정) (유지) --%>
    <div class="col-lg-4">
        <div class="card h-100">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span class="fw-bold"></i>📅 학사일정</span>
                <a href="/portal/univcalendar" class="text-decoration-none text-muted small">더보기 +</a>
            </div>
            <div class="card-body p-3">
                <div class="academic-calendar-container">
                    <div id="dashboard-calendar-wrapper">
                        <div id="dashboard-calendar"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

  </div>
</div>



<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<%-- FullCalendar 라이브러리 및 JS 파일 --%>
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app/portal/portalDashboardCalendar.js"></script>
 <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<script>
$(document).ready(function() {
    function syncColumnHeights() {
        // 1열 전체 컨테이너 (공지사항/학사공지)
        const col1Container = $('.col-lg-4:nth-child(1)');
        // 2열 전체 컨테이너 (주요 업무/채용 정보)
        const col2Container = $('.col-lg-4:nth-child(2)');

        // 🌟 1열 하단 공지사항 & 학사공지 내부 카드-바디 높이 조정
        if (col1Container.length) {
            const noticeSplitContainer = col1Container.find('.notice-and-academic-split');
            if (noticeSplitContainer.length) {
                noticeSplitContainer.find('.card').each(function() {
                    const cardHeaderHeight = $(this).find('.card-header').outerHeight(true);
                    // card-body의 높이를 card 높이의 100%에서 header 높이를 뺀 값으로 설정하여 스크롤 영역 확보
                    $(this).find('.card-body').css('height', 'calc(100% - ' + cardHeaderHeight + 'px)');
                });
            }
        }

        // 🌟 2열 하단 주요 업무 / 채용 정보 내부 카드-바디 높이 조정
        if (col2Container.length) {
            const tasksContainer = col2Container.find('.tasks-and-jobs-container');
            if (tasksContainer.length) {
                tasksContainer.find('.card').each(function() {
                    const cardHeaderHeight = $(this).find('.card-header').outerHeight(true);
                    // card-body의 높이를 card 높이의 100%에서 header 높이를 뺀 값으로 설정하여 스크롤 영역 확보
                    $(this).find('.card-body').css('height', 'calc(100% - ' + cardHeaderHeight + 'px)');
                });
            }
        }
    }

    // 1. 페이지 로드 시 즉시 동기화
    syncColumnHeights();

    // 2. 윈도우 크기 변경 시 동기화 (반응형 대응)
    $(window).on('resize', syncColumnHeights);

    // 3. FullCalendar 로딩 및 렌더링 완료 후 재동기화 (FullCalendar가 렌더링 된 후 높이 조정)
    setTimeout(syncColumnHeights, 500);
    setTimeout(syncColumnHeights, 1000);

});
</script>

</body>
</html>