<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>강의실 배정 대시보드</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<style>
/* CSS 스타일은 이전과 동일하므로 생략 */
:root {
	--theme-primary: #0d9488; /* Teal-700 */
	--theme-primary-light: #f0fdfa; /* Teal-50 */
	--theme-success: #16a34a; /* Green-600 */
	--theme-warning: #f59e0b; /* Amber-500 */
	--theme-danger: #dc2626; /* Red-600 */
	--theme-text-dark: #1f2937;
	--theme-text-light: #6b7280;
}

@
keyframes fadeInUp {from { opacity:0;
	transform: translateY(20px);
}

to {
	opacity: 1;
	transform: translateY(0);
}

}
@
keyframes progressAnimate {from { width:0%;

}

}
body {
	background-color: #f0f2f5;
	font-family: 'Noto Sans KR', sans-serif;
	color: var(--theme-text-dark);
}

.card {
	animation: fadeInUp 0.5s ease-out forwards;
	opacity: 0;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05) !important;
	border: none;
}

.kpi-card .card-body {
	display: flex;
	align-items: center;
}

.kpi-icon {
	font-size: 2rem;
	margin-right: 1rem;
	width: 50px;
	text-align: center;
	color: var(--theme-primary);
}

.timetable-wrapper {
	overflow-x: auto;
}

.timetable {
	table-layout: fixed;
	min-width: 1200px;
	border-collapse: separate;
	border-spacing: 0;
	margin-bottom: 2rem;
} /* 테이블 간격 조정 */
.timetable th, .timetable td {
	text-align: center;
	vertical-align: middle;
	padding: 0.5rem;
	font-size: 0.8rem;
}

.timetable th {
	background-color: #f8f9fa;
}

.timetable .time-col {
	width: 80px;
	font-weight: 500;
}

.timetable .room-header {
	cursor: pointer;
	transition: background-color 0.2s ease;
}

.timetable .room-header:hover {
	background-color: #e9ecef;
}

.timetable .fa-video.text-danger {
	color: var(--theme-danger) !important;
}

.slot {
	border: 1px solid #e9ecef;
	height: 60px;
	transition: all 0.2s ease;
}

.slot.booked {
	background-color: #f1f5f9;
	color: var(--theme-text-light);
}

.slot.requested-time {
	background-image: repeating-linear-gradient(-45deg, #ffffff, #ffffff 5px, var(--theme-primary-light
		) 5px, var(--theme-primary-light) 10px);
}

.slot.available:hover {
	background-color: #d1fae5;
	cursor: pointer;
	transform: scale(1.05);
	z-index: 10;
}

.slot.selected {
	background-color: var(--theme-success);
	color: white;
	transform: scale(1.05);
	box-shadow: 0 0 15px rgba(22, 163, 74, 0.5);
	z-index: 10;
}

/* 건물 그룹 헤더 스타일 */
.building-group-header {
	background-color: var(--theme-primary);
	color: white;
	font-weight: bold;
	text-align: left;
	padding: 0.5rem 1rem !important;
	border-bottom: 3px solid #0d7f72;
	cursor: pointer;
	transition: background-color 0.2s;
}

.building-group-header:hover {
	background-color: #0d7f72;
}

.timetable-group-container {
	border: 1px solid #dee2e6;
	margin-bottom: 20px;
	border-radius: 0.375rem;
	overflow: hidden;
}

.progress {
	height: 1.25rem;
	background-color: #e9ecef;
}

.progress-bar {
	animation: progressAnimate 1s ease-out forwards;
	background-color: var(--theme-primary);
}

.progress-bar.bg-danger {
	background-color: var(--theme-danger) !important;
}

.progress-bar.bg-success {
	background-color: var(--theme-success) !important;
}

.progress-bar-label {
	font-size: 0.8em;
	font-weight: 500;
	color: var(--theme-text-light);
}

.btn-primary {
	background-color: var(--theme-primary);
	border-color: var(--theme-primary);
}

.btn-primary:hover {
	background-color: #0a7f72;
	border-color: #0a7f72;
}

.btn-success {
	background-color: var(--theme-success);
	border-color: var(--theme-success);
}

.btn {
	transition: all 0.2s ease;
}

.btn:hover {
	transform: translateY(-2px);
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.nav-tabs .nav-link {
	transition: all 0.2s ease;
	color: var(--theme-text-light);
}

.nav-tabs .nav-link.active {
	font-weight: 700;
	color: var(--theme-primary);
	border-bottom-color: var(--theme-primary);
}

.badge.bg-warning {
	background-color: var(--theme-warning) !important;
}

.badge.bg-primary {
	background-color: var(--theme-primary) !important;
}

.badge.bg-success {
	background-color: var(--theme-success) !important;
}
</style>
</head>
<body>
	<div class="container-fluid p-3">
		<div class="row mb-3">
			<div class="col-md-4">
				<div class="card kpi-card shadow-sm" style="animation-delay: 0.1s;">
					<div class="card-body">
						<div class="kpi-icon text-primary">
							<i class="fas fa-chalkboard-teacher"></i>
						</div>
						<div>
							<div class="text-muted">배정 대기 강의</div>
							<h4 class="mb-0">3 건</h4>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<div class="card kpi-card shadow-sm" style="animation-delay: 0.2s;">
					<div class="card-body">
						<div class="kpi-icon text-success">
							<i class="fas fa-building-circle-check"></i>
						</div>
						<div>
							<div class="text-muted">오늘 강의실 사용률</div>
							<h4 class="mb-0">72%</h4>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<div class="card kpi-card shadow-sm" style="animation-delay: 0.3s;">
					<div class="card-body">
						<div class="kpi-icon text-danger">
							<i class="fas fa-triangle-exclamation"></i>
						</div>
						<div>
							<div class="text-muted">시설 이슈</div>
							<h4 class="mb-0">1 건 (공학-305호 프로젝터)</h4>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-lg-9">
				<div class="card shadow-sm" style="animation-delay: 0.4s;">
					<div class="card-header">
						<ul class="nav nav-tabs card-header-tabs" id="day-tabs">
							<li class="nav-item"><a class="nav-link" href="#"
								data-day="mon">월</a></li>
							<li class="nav-item"><a class="nav-link" href="#"
								data-day="tue">화</a></li>
							<li class="nav-item"><a class="nav-link active"
								aria-current="true" href="#" data-day="wed">수</a></li>
							<li class="nav-item"><a class="nav-link" href="#"
								data-day="thu">목</a></li>
							<li class="nav-item"><a class="nav-link" href="#"
								data-day="fri">금</a></li>
						</ul>
					</div>

					<div class="card-body p-0 timetable-wrapper">
						<div id="timetable-groups">
							<div class="text-center p-4 text-muted">요일을 선택하거나, 강의실 데이터를
								로딩 중입니다.</div>
						</div>
					</div>
				</div>

				<div class="card shadow-sm mt-3" style="animation-delay: 0.5s;">
					<div class="card-header">
						<h5 class="mb-0">강의실별 사용률 (수요일)</h5>
					</div>
					<div class="card-body">
						<div class="mb-2">
							<div class="progress-bar-label d-flex justify-content-between">
								<span>공학-101</span><span>50%</span>
							</div>
							<div class="progress">
								<div class="progress-bar" style="width: 50%;"></div>
							</div>
						</div>
						<div class="mb-2">
							<div class="progress-bar-label d-flex justify-content-between">
								<span>공학-102</span><span>100%</span>
							</div>
							<div class="progress">
								<div class="progress-bar bg-danger" style="width: 100%;"></div>
							</div>
						</div>
						<div class="mb-2">
							<div class="progress-bar-label d-flex justify-content-between">
								<span>IT-501</span><span>25%</span>
							</div>
							<div class="progress">
								<div class="progress-bar bg-success" style="width: 25%;"></div>
							</div>
						</div>
					</div>
				</div>

				<div class="card shadow-sm mt-3" style="animation-delay: 0.8s;">
					<div class="card-header">
						<h5 class="mb-0">최근 활동 로그</h5>
					</div>
					<div class="card-body">
						<ul class="list-group list-group-flush">
							<li class="list-group-item d-flex align-items-center py-3"><i
								class="fas fa-check-circle text-success me-3"></i>
							<div>
									'AI개론' 강의가 <strong>공학-101호</strong>에 배정되었습니다. <small
										class="text-muted">- by 김교직원</small>
								</div>
								<small class="text-muted ms-auto">방금 전</small></li>
							<li class="list-group-item d-flex align-items-center py-3"><i
								class="fas fa-plus-circle text-primary me-3"></i>
							<div>'컴퓨터 비전' 강의 개설 신청이 접수되었습니다.</div>
								<small class="text-muted ms-auto">15분 전</small></li>
						</ul>
					</div>
				</div>
			</div>

			<div class="col-lg-3">
				<div class="card shadow-sm mb-3" style="animation-delay: 0.6s;">
					<div class="card-header">
						<h5 class="mb-0">신청 정보</h5>
					</div>
					<div class="card-body">
						<c:if test="${not empty lectureAssignmentInfo}">
							<p>
								<strong>과목:</strong>
								<c:out value="${lectureAssignmentInfo.subjectName}" />
							</p>
							<p>
								<strong>교수:</strong>
								<c:out value="${lectureAssignmentInfo.professorName}" />
							</p>
							<p>
								<strong>희망인원:</strong>
								<c:out value="${lectureAssignmentInfo.expectCap}" />
								명
							</p>
							<p>
								<strong>개설학기:</strong>
								<%-- ⚠️ 수정: DTO 필드명과 일치하도록 yeartermCd (소문자 t)로 변경 --%>
								<c:out value="${lectureAssignmentInfo.yeartermCd}" />
							</p>

							<p>
								<strong>상태:</strong> <span class="badge bg-warning text-dark"
									id="assignment-status">배정 대기</span>
							</p>
						</c:if>
						<c:if test="${empty lectureAssignmentInfo}">
							<p class="text-danger">강의 신청 정보를 불러올 수 없습니다. (매핑 오류)</p>
						</c:if>
						<hr>
						<p class="mb-1">
							<strong>선택된 강의실:</strong> <span id="selected-room">-</span>
						</p>
						<p class="mb-1">
							<strong>선택된 시간:</strong> <span id="selected-time">-</span>
						</p>
						<div class="d-grid gap-2 mt-3">
							<button class="btn btn-primary" id="assign-btn" disabled>배정하기</button>
							<button class="btn btn-success" id="approve-btn" disabled>최종
								승인</button>
						</div>
					</div>
				</div>
				<div id="room-details" class="card shadow-sm mb-3"
					style="animation-delay: 0.7s;">
					<div class="card-header">
						<h5 class="mb-0">강의실 상세 정보</h5>
					</div>
					<div class="card-body text-muted">강의실 헤더를 클릭하여 상세 정보를 확인하세요.</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="approvalSuccessModal" tabindex="-1"
		aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header border-0">
					<h5 class="modal-title">승인 처리 완료</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="text-center mb-3">
						<i class="fas fa-check-circle text-success fa-4x"></i>
					</div>
					<p class="text-center fw-bold">'AI개론' 강의가 최종 승인되었습니다.</p>
					<ul class="list-group list-group-flush mt-3">
						<li
							class="list-group-item d-flex justify-content-between align-items-center border-0 ps-0">
							강의 개설 승인 <span class="badge bg-success">완료</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center border-0 ps-0">
							담당 교수에게 알림 발송 <span class="badge bg-success">완료</span>
						</li>
						<li
							class="list-group-item d-flex justify-content-between align-items-center border-0 ps-0">
							담당 교수 시간표에 업데이트 <span class="badge bg-success">완료</span>
						</li>
					</ul>
				</div>
				<div class="modal-footer border-0">
					<button type="button" class="btn btn-primary w-100"
						id="returnToListBtn">목록으로 돌아가기</button>
				</div>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
	<script>
    // **1. 서버 데이터 JS 변수 초기화 및 그룹화**
    // DTO의 실제 필드명인 subjectName 사용
    const subjectName = '<c:out value="${lectureAssignmentInfo.subjectName}"/>';
    const professorName = '<c:out value="${lectureAssignmentInfo.professorName}"/>';

    const roomData = JSON.parse(lectureRoomsJson || '[]');
    const requiredScheduleList = JSON.parse(requiredScheduleJson || '[]');

    // placeCd (예: RM-ENGI-HQ-0201)에서 건물 코드 (RM-ENGI-HQ)를 추출하여 그룹화
    const lectureRoomsGrouped = roomData.reduce((acc, room) => {
        const placeCdPrefix = room.placeCd ? room.placeCd.split('-').slice(0, 3).join('-') : "기타-건물";

        // 그룹 이름 매핑 (실제 건물 이름으로 변경)
        let buildingName = placeCdPrefix;
        if (placeCdPrefix.includes('ENGI')) {
             buildingName = '공학관';
        } else if (placeCdPrefix.includes('IT')) {
             buildingName = 'IT관';
        } else if (placeCdPrefix.includes('LIB')) {
             buildingName = '도서관'; // 예시
        }

        if (!acc[buildingName]) {
            acc[buildingName] = [];
        }
        acc[buildingName].push(room);
        return acc;
    }, {});

    // **2. 시간표 렌더링 함수 (건물별 그룹 생성)**
    function renderTimetable() {
        const container = document.getElementById('timetable-groups');
        container.innerHTML = '';

        // 시간표 세로 시간 데이터 (교시 코드 및 시간 정보를 담는 Map으로 대체 필요)
        const timeSlots = [
            { code: 'T1', time: '09:00-10:30' },
            { code: 'T2', time: '10:30-12:00' },
            { code: 'T3', time: '13:00-14:30' }
        ];

        // 현재 선택된 요일 (활성화된 탭에서 가져오거나, 기본값 'wed')
        const currentDay = document.querySelector('#day-tabs .nav-link.active').dataset.day;

        // 요청된 강의의 현재 선택된 요일 시간대 코드 목록
        const requiredTimeCodes = requiredScheduleList
                                    .filter(item => item.dayOfWeek.toLowerCase() === currentDay)
                                    .map(item => item.timeblockCd);

        if (Object.keys(lectureRoomsGrouped).length === 0) {
            container.innerHTML = '<div class="text-center p-4 text-muted">배정할 강의실 목록이 없습니다.</div>';
            return;
        }

        for (const [buildingName, rooms] of Object.entries(lectureRoomsGrouped)) {
            const actualRooms = rooms.filter(r => r.placeName.includes('강의실'));
            if(actualRooms.length === 0) continue;

            const safeId = buildingName.replace(/ /g, '-');

            // 🚨 JSP EL 파싱을 피하기 위해 모든 JS 템플릿 리터럴(\${...})에 백슬래시를 사용
            const groupHtml = `
                <div class="timetable-group-container">
                    <div class="building-group-header" data-bs-toggle="collapse" data-bs-target="#collapse-\${safeId}" aria-expanded="true">
                        <i class="fas fa-angles-down me-2"></i> \${buildingName} (\${actualRooms.length}개 강의실)
                    </div>
                    <div class="collapse show" id="collapse-\${safeId}">
                        <div class="timetable-wrapper">
                            <table class="table table-bordered timetable">
                                <thead>
                                    <tr>
                                        <th class="time-col">시간</th>
                                        \${actualRooms.map(room => `
                                            <th class="room-header" data-room-id="\${room.placeCd}">
                                                \${room.placeName.replace('강의실 ', '')}
                                                <br><small>👤 \${room.capacity || 'N/A'}
                                                \${room.equipmentName && room.equipmentName.includes('화상') ? '<i class="fas fa-video"></i>' : ''}
                                                </small>
                                            </th>
                                        `).join('')}
                                    </tr>
                                </thead>
                                <tbody>
                                    \${timeSlots.map((slot, timeIndex) => `
                                        <tr>
                                            <td class="time-col">\${slot.time}</td>
                                            \${actualRooms.map(room => {
                                                let classList = "slot available";
                                                let content = "";

                                                // 1. 요청 시간대 표시
                                                if (requiredTimeCodes.includes(slot.code)) {
                                                    classList += " requested-time";
                                                }

                                                // 2. 예약된 시간대 표시 (실제 서버 데이터로 대체해야 함)
                                                // 현재는 임시 더미 데이터로 대체
                                                if (room.placeCd.includes('202') && slot.code === 'T2') {
                                                    classList = "slot booked";
                                                    content = "운영체제<br><small>이교수</small>";
                                                } else if (room.placeCd.includes('101') && slot.code === 'T1') {
                                                    classList = "slot booked";
                                                    content = "데이터베이스<br><small>김교수</small>";
                                                }

                                                // 3. 강의실 배정 불가능 조건 (예: 인원 초과, 시설 문제 등)
                                                const expectCap = '<c:out value="${lectureAssignmentInfo.expectCap}" />';
                                                if (expectCap > room.capacity) {
                                                     // 인원 초과 시에도 선택은 가능하게 하되, 경고 표시
                                                }


                                                // 템플릿 리터럴 내의 템플릿 리터럴은 이스케이프해야 합니다.
                                                return \`<td class="\${classList.trim()}" data-place-cd="\${room.placeCd}" data-time-slot="\${slot.code}">\${content}</td>\`;
                                            }).join('')}
                                        </tr>
                                    `).join('')}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
            container.insertAdjacentHTML('beforeend', groupHtml);
        }

        // 새로운 슬롯에 이벤트 리스너 재부착
        attachEventListeners();
    }

    // **3. 이벤트 리스너 재부착 함수 (유지)**
    function attachEventListeners() {
        let selectedSlot = null;

        // 강의실 상세 정보를 표시하기 위한 맵 재구성
        const roomDetailsMap = roomData.reduce((acc, room) => {
            acc[room.placeCd] = {
                name: room.placeName,
                capacity: room.capacity || 0,
                equipment: room.equipmentName ? room.equipmentName.split(', ') : ['정보 없음'],
                // placeCd 기반으로 이미지 URL 생성 (JSP EL 오류 방지를 위해 JS 템플릿 리터럴 사용)
                image: `https://via.placeholder.com/400x250.png?text=\${room.placeName.replace(' ', '+')}`
            };
            return acc;
        }, {});

        // 강의실 헤더 클릭 -> 상세정보 표시
        document.querySelectorAll('.room-header').forEach(header => {
            header.addEventListener('click', () => {
                const roomId = header.dataset.roomId;
                const room = roomDetailsMap[roomId];
                const detailsEl = document.getElementById('room-details').querySelector('.card-body');

                if (room) {
                    // 🚨 JSP EL 파싱을 피하기 위해 모든 JS 템플릿 리터럴(\${...})에 백슬래시를 사용
                    const equipmentListHtml = (Array.isArray(room.equipment) ? room.equipment : [room.equipment])
                                              .map(e => `<li>\${e}</li>`).join('');

                    detailsEl.innerHTML = `
                        <img src="\${room.image}" class="img-fluid rounded mb-2" alt="\${room.name}">
                        <h6>\${room.name}</h6>
                        <p><strong>수용인원:</strong> \${room.capacity}명</p>
                        <p><strong>보유장비:</strong></p>
                        <ul>\${equipmentListHtml}</ul>
                    `;
                } else {
                    detailsEl.innerHTML = `<p class="text-danger">강의실 상세 정보를 찾을 수 없습니다.</p>`;
                }
            });
        });

        // 빈 슬롯 클릭 -> 선택
        document.querySelectorAll('.slot.available').forEach(slot => {
            slot.addEventListener('click', () => {
                if(selectedSlot) selectedSlot.classList.remove('selected');
                slot.classList.add('selected');
                selectedSlot = slot;

                const time = slot.parentElement.querySelector('.time-col').textContent;

                const roomIndex = slot.cellIndex;
                // 🚨 JSP EL 파싱을 피하기 위해 모든 JS 템플릿 리터럴(\${...})에 백슬래시를 사용
                const roomHeader = slot.closest('.timetable').querySelector(`thead th:nth-child(\${roomIndex + 1})`);

                let roomName = '-';
                if (roomHeader) {
                    roomName = roomHeader.innerText.split('\n')[0].trim();
                }

                document.getElementById('selected-room').textContent = roomName;
                document.getElementById('selected-time').textContent = time;
                document.getElementById('assign-btn').disabled = false;
            });
        });

        // 배정 버튼 클릭 이벤트
        document.getElementById('assign-btn').onclick = () => {
            if(!selectedSlot) return;

            // DTO의 실제 필드명인 subjectName 사용
            const subjectNameForAssignment = '<c:out value="${lectureAssignmentInfo.subjectName}"/>';
            const professorNameForAssignment = '<c:out value="${lectureAssignmentInfo.professorName}"/>';
            const lctApplyId = '<c:out value="${lectureAssignmentInfo.lctApplyId}"/>';
            const placeCd = selectedSlot.dataset.placeCd;
            const timeSlot = selectedSlot.dataset.timeSlot; // 요일과 시간대 코드를 조합해야 함
            const currentDay = document.querySelector('#day-tabs .nav-link.active').dataset.day.toUpperCase();

            // 실제 서버 전송 데이터 준비
            const requestData = {
                 lctApplyId: lctApplyId,
                 placeCd: placeCd,
                 // 서버에서 필요한 형식으로 변환해야 함 (예: WED-T2, MON-T1,MON-T2)
                 timeblockCdsString: currentDay + '-' + timeSlot
            };

            // API 호출 로직 (Controller의 @PostMapping("/saveAssignment")와 연결)
            fetch('/lms/staff/classroom/assignment/saveAssignment', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'SUCCESS') {
                    // 성공 시 UI 업데이트
                    selectedSlot.classList.remove('available', 'requested-time', 'selected');
                    selectedSlot.classList.add('booked');
                    // subjectNameForAssignment 변수를 사용하여 UI 업데이트
                    selectedSlot.innerHTML = `\${subjectNameForAssignment || '강의명 없음'}<br><small>\${professorNameForAssignment || '교수명 없음'}(배정)</small>`;

                    alert(document.getElementById('selected-room').textContent + '에 임시 배정되었습니다.');
                    document.getElementById('assign-btn').disabled = true;
                    document.getElementById('approve-btn').disabled = false;
                    document.getElementById('assignment-status').className = 'badge bg-primary';
                    document.getElementById('assignment-status').textContent = '배정 완료';
                    selectedSlot = null; // 선택 해제
                } else {
                    alert('배정 저장에 실패했습니다: ' + data.message);
                    // 실패 시 선택 상태 유지 또는 해제
                }
            })
            .catch(error => {
                console.error('API Error:', error);
                alert('배정 저장 중 통신 오류가 발생했습니다.');
            });

        };

        // 최종 승인 버튼
        const approvalSuccessModal = new bootstrap.Modal(document.getElementById('approvalSuccessModal'));
        document.getElementById('approve-btn').addEventListener('click', () => {
             // DTO의 실제 필드명인 subjectName 사용
             const subjectNameForApproval = '<c:out value="${lectureAssignmentInfo.subjectName}"/>';
             document.querySelector('#approvalSuccessModal .modal-body .fw-bold').textContent = `'${subjectNameForApproval || '강의'}' 강의가 최종 승인되었습니다.`;
             approvalSuccessModal.show();
        });

        // 목록으로 돌아가기 버튼
        document.getElementById('returnToListBtn').addEventListener('click', () => {
            // 이 경로는 실제 승인 목록 페이지로 대체해야 함
            const approvalId = '${param.approvalId}';
            window.location.href = approvalId ? '/lms/staff/approvals/' + approvalId : '/lms/staff/approvals/list';
        });

        // 요일 탭 클릭 이벤트
        document.querySelectorAll('#day-tabs .nav-link').forEach(tab => {
            tab.addEventListener('click', function(e) {
                e.preventDefault();
                // 활성화 클래스 변경
                document.querySelectorAll('#day-tabs .nav-link').forEach(t => t.classList.remove('active'));
                this.classList.add('active');
                // 시간표 다시 렌더링
                renderTimetable();
            });
        });
    }

    // **4. 초기 로드 시 렌더링**
    document.addEventListener('DOMContentLoaded', () => {
        renderTimetable();
    });
</script>
</body>
</html>