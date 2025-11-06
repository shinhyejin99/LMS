<%-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 29.     	송태호            최초 생성
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Bootstrap 5 CSS & Icons (CDN) -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

<style>
/* 1) 페이지 자체는 스크롤 금지, 뷰포트에 꽉 채움 */
html, body {
	height: 100%;
}

body {
	margin: 0;
	min-height: 100dvh;
	display: grid;
	grid-template-rows: auto 1fr auto; /* 헤더 / 본문 / 푸터 */
	overflow: hidden; /* ★ 페이지 스크롤 완전 차단 */
	background: #f8f9fa;
}

/* 2) 헤더: 그냥 상단 영역(고정 느낌은 grid가 보장) */
.app-header {
	background: #212529;
	color: #fff;
}

/* 3) 본문 레이아웃: 사이드바 + 콘텐츠, 본문에서도 스크롤 차단 */
.app-main {
	display: grid;
	grid-template-columns: 0 1fr; /* 모바일: 사이드바 0 */
	overflow: hidden; /* ★ 본문 컨테이너 스크롤 차단 */
	min-height: 0; /* 내부 스크롤 컨테이너가 제대로 계산되도록 */
}

@media ( min-width : 992px) {
	.app-main {
		grid-template-columns: 240px 1fr;
	}
}

/* 4) 사이드바: 세로 꽉, 자체 스크롤 가능하도록(필요시) */
.app-sidebar {
	border-right: 1px solid #dee2e6;
	background: #f8f9fa;
	min-height: 0;
	overflow: auto; /* 메뉴 길어지면 사이드바 내부만 스크롤 */
}

/* 5) 콘텐츠 영역: 필요 시 여기만 스크롤하도록 선택 */
.app-content {
	min-height: 0;
	/* 만약 콘텐츠가 길 수 있고, 오른쪽 내용만 스크롤되게 하려면 ↓ 주석 해제*/
	overflow: auto;
	/* 기본: 페이지에 스크롤이 전혀 없게 하려면 hidden 유지 */
/* 	overflow: hidden; /* ★ 완전 무스크롤(넘치는 건 잘림) */ */
	
	background: #fff;
}

/* 6) 푸터: 하단 고정(그리드의 마지막 줄) */
.app-footer {
	background: #fff;
	border-top: 1px solid #dee2e6;
}
</style>