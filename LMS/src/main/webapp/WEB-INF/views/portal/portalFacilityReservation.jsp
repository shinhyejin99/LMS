<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 20.     		정태일           최초 생성
 *  2025. 10. 31.		김수현			css 추가 및 브래드크럼 조정
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>시설 예약</title>
<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/portalFacility.css">

<style>
/* 메인 컨텐츠 레이아웃 */
.main-content {
	display: flex; /* Flexbox 컨테이너로 설정 */
	flex-wrap: wrap; /* 내용이 넘치면 다음 줄로 */
	justify-content: center; /* 중앙 정렬 */
	align-items: flex-start; /* 상단 정렬 */
	gap: 20px; /* 요소들 사이의 간격 */
	max-width: 1200px; /* 전체 너비 제한 */
	margin: 20px auto; /* 중앙 정렬 */
	transition: all 0.5s ease-in-out; /* 부드러운 전환 효과 */
}
/* 초기 상태: 지도 뷰가 전체 너비를 차지 */
#campus-map-view {
	width: 100%;
	max-width: 1000px; /* 지도 컨테이너의 최대 너비 */
	transition: opacity 0.5s ease-in-out; /* 너비 트랜지션 제거 */
}
/* 시설 목록 컨테이너 (초기에는 숨김) */
#facility-list-container {
	width: 100%; /* 초기에는 지도의 아래에 위치 */
	max-width: 1000px;
	display: none; /* 초기에는 숨김 */
	transition: opacity 0.5s ease-in-out; /* 너비 트랜지션 제거 */
	opacity: 0; /* 숨김 상태에서 투명 */
}
/* 분할 뷰 상태: 지도와 목록이 옆으로 나란히 */
.main-content.split-view #campus-map-view {
	width: 48%; /* 지도가 절반 너비 */
	max-width: 580px; /* 최대 너비 조정 */
}

.main-content.split-view #facility-list-container {
	width: 48%; /* 목록이 절반 너비 */
	max-width: 580px; /* 최대 너비 조정 */
	display: block; /* 보이도록 */
	opacity: 1; /* 투명도 복원 */
}
/* 반응형 디자인: 작은 화면에서는 다시 세로로 쌓이도록 */
@media ( max-width : 992px) {
	.main-content.split-view #campus-map-view, .main-content.split-view #facility-list-container
		{
		width: 100%;
		max-width: 100%;
	}
}
/* 캠퍼스 지도 컨테이너 */
#campus-map-container {
	position: relative;
	width: 100%;
	max-width: 1200px; /* 너비 증가 */
	margin: 20px auto;
	border-radius: 8px;
	overflow: hidden;
	border: 1px solid #dee2e6;
	box-shadow: 0 6px 12px rgba(0, 0, 0, 0.08);
	/* [⭐수정: 배경 이미지 제거] 코드가 SVG 내 <image>를 사용하므로 중복 제거 */
	/* background-image: url('${pageContext.request.contextPath}/images/campus-clean.png'); */
	/* background-size: cover; */
	/* background-position: center; */
}
/* 인라인 SVG 스타일 */
#campus-map-container svg {
	width: 100%;
	height: auto;
	display: block;
}
/* SVG 내부 건물 그룹 하이라이트 스타일 */
#buildings .building-group {
	fill: transparent; /* 기본적으로 투명 */
	stroke: none;
	transition: fill 0.2s ease-in-out; /* 부드러운 전환 효과 */
}

/* [⭐추가: 버튼/페이징 영역 스타일] */
.facility-res-page .list-footer-container {
	display: flex;
	justify-content: space-between; /* 버튼과 페이징을 양 끝으로 배치 */
	align-items: center;
	margin-top: 15px;
	padding: 5px 0;
}

/* [⭐수정: '지도에서 다시 선택' 버튼 스타일] */
.facility-res-page #back-to-map-btn {
	background: #6b7280; /* 회색 계열로 변경 */
	color: #fff;
	border: none;
	padding: 8px 15px;
	border-radius: 8px;
	font-size: 13px;
	font-weight: 700;
	cursor: pointer;
}

.facility-res-page #back-to-map-btn:hover {
	background: #4b5563;
}
/* [⭐추가: 페이징 스타일 (가상 컴포넌트)] */
.facility-res-page .pagination-area {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 5px;
}
.facility-res-page .pagination-area .page-btn {
	padding: 5px 10px;
	border: 1px solid #e5e7eb;
	border-radius: 4px;
	color: #4b5563;
	background: #fff;
	cursor: pointer;
	font-size: 13px;
}
.facility-res-page .pagination-area .page-btn.active {
	background: #2563eb;
	color: #fff;
	border-color: #2563eb;
}
</style>
</head>
<!-- <body> -->
<body class="facility-res-page">
	<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
	<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
	<div class="main-container">

		<div class="breadcrumb">
			<a href="/portal">
	        	<i class='bx bx-home-alt'></i>
	        	홈
	        </a>
	        <span>&gt;</span>
 			<a href="/portal/facility/reservation" class="breadcrumb-current">시설예약</a>
		</div>

		<h1 class="page-title">시설 예약</h1>



		<div class="tab-navigation">
			<a href="/portal/facility/reservation" class="tab-item active">시설
				예약하기</a> <a href="/portal/facility/my-reservations" class="tab-item">나의
				예약 현황</a>
		</div>
		<main class="main-content">
			<!-- 1단계: 캠퍼스 지도 뷰 -->
			<div id="campus-map-view">
				<h2 class="text-center mb-4">지도에서 건물을 선택하세요</h2>
				<div id="campus-map-container">
					<!-- SVG를 직접 인라인으로 삽입 -->
					<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1536 1024"
						width="100%"> <!-- Clean photographic base --> <image
							href="${pageContext.request.contextPath}/images/campus-clean.png"
							x="0" y="0" width="1536" height="1024" preserveAspectRatio="none" /> <!-- Clickable overlays: 6 buildings --> <g
							id="buildings" fill="transparent" stroke="none"> <c:forEach
							items="${buildings}" var="building" varStatus="status"> <c:choose> <c:when
									test="${building.placeCd eq 'BLD-ENGI-HQ'}"> <!-- 공과대학 본부관 (중앙도서관 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <polygon
										points="240,180 520,165 540,305 260,320" /> </g> </c:when> <c:when
									test="${building.placeCd eq 'BLD-HUM-HQ'}"> <!-- 인문대학 본부관 (학생회관 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <rect x="770"
										y="165" width="300" height="170" rx="12" /> </g> </c:when> <c:when
									test="${building.placeCd eq 'BLD-SCI-HQ'}"> <!-- 자연과학대학 본부관 (제1공학관 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <circle
										cx="1265" cy="245" r="105" /> </g> </c:when> <c:when
									test="${building.placeCd eq 'BLD-ENGI-LAB-A'}"> <!-- 공과대학 실습관 A (제2공학관 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <polygon
										points="170,560 480,545 500,705 190,720" /> </g> </c:when> <c:when
									test="${building.placeCd eq 'BLD-BUS-HQ'}"> <!-- 경영대학 본부관 (본관 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <polygon
										points="1070,545 1400,525 1420,705 1090,725" /> </g> </c:when> <c:when
									test="${building.placeCd eq 'BLD-MED-HQ'}"> <!-- 의과대학 본부관 (기숙사 위치) --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"> <rect x="590"
										y="630" width="360" height="190" rx="14" /> </g> </c:when> <c:otherwise> <!-- 매핑되지 않은 건물은 기본 핫스팟으로 처리하거나 숨김 --> <g
										class="building-group" id="${building.placeCd}"
										data-building-cd="${building.placeCd}"
										data-building-name="${building.placeName}"
										style="display:none;"> <!-- 기본 도형 (예: 작은 원) --> <circle
										cx="100" cy="100" r="10" /> </g> </c:otherwise> </c:choose> </c:forEach> </g> </svg>
				</div>
			</div>
			<!-- 2단계: 특정 시설 목록 뷰 (초기에는 숨김) -->
			<div id="facility-list-container">
				<h2 id="facility-list-title"></h2>
				<div class="filter-buttons-container mb-3">
					<button type="button"
						class="btn btn-outline-primary btn-sm filter-btn active"
						data-filter="ALL">전체</button>
					<!-- 필터 버튼들이 여기에 동적으로 추가됩니다 -->
				</div>
				<div class="table-responsive">
					<table class="table table-hover facility-table"
						id="specific-facility-list">
						<thead class="table-light">
							<tr>
								<th scope="col">시설명</th>
								<th scope="col">용도</th>
								<th scope="col">수용인원</th>
								<th scope="col">예약</th>
							</tr>
						</thead>
						<tbody>
							<!-- 시설 목록이 여기에 동적으로 로드됩니다 -->
						</tbody>
					</table>
				</div>
				<div class="list-footer-container">
					<button type="button" class="btn-secondary" id="back-to-map-btn">지도에서
						다시 선택</button>
				</div>
				<!-- 로딩 스피너 오버레이 -->
				<div id="loading-spinner-overlay" class="loading-spinner-overlay"
					style="display: none;">
					<div class="spinner-border text-primary" role="status">
						<span class="visually-hidden">Loading...</span>
					</div>
				</div>
			</div>
		</main>
	</div>
	<footer class="footer"> Copyright © 2025 JSU University. All
		Rights Reserved. </footer>
	<script>
		const C_PATH = "${pageContext.request.contextPath}";
	</script>
	<script>
		const USER_ROLE = "${userRole}";
	</script>
	<script>
		const LECTURE_HALL_USAGE_CD = "CLASSROOM";
	</script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script
		src="${pageContext.request.contextPath}/js/app/portal/portalFacilityReservation.js"></script>
</body>
</html>