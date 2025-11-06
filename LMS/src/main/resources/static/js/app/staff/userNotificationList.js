/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 10. 13.     	신혜진           최초 생성

 *
 * </pre>
 */

// ====================================================================================================
// SECTION 1: 전역 상수 및 변수 정의 (최상단)
// ====================================================================================================
// currentUserId는 JSP 파일에서 설정될 것으로 가정합니다.
var currentUserId = '${currentUserId}';
const NOTI_API_URL = '/lms/notifications/api';
const WS_URL = '/lms/ws-stomp';
const DETAIL_BASE_URL = '/lms/notifications/';
let stompClient = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 5; // 최대 재접속 시도 횟수

// ====================================================================================================
// SECTION 2: 헬퍼 함수 정의
// ====================================================================================================

/**
 * 헬퍼 함수: 날짜/시간 형식 변환
 */
function formatDate(dateString) {
	if (!dateString) return '날짜 정보 없음';
	try {
		// YYYY-MM-DDTHH:MM:SS.SSSZ 형식일 경우 Z 제거
		const date = new Date(dateString.replace('Z', ''));

		// Date 객체가 유효한지 확인
		if (isNaN(date.getTime())) {
			throw new Error("Invalid Date Object");
		}

		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		const hours = String(date.getHours()).padStart(2, '0');
		const minutes = String(date.getMinutes()).padStart(2, '0');
		return `${year}-${month}-${day} ${hours}:${minutes}`;
	} catch (e) {
		console.error("Invalid date format:", dateString, e);
		return '날짜 형식 오류';
	}
}

/**
 * 헬퍼 함수: 토스트 메시지 표시 (Bootstrap 5 기능적 수정)
 * 🔔 [수정] 실시간 알림 팝업으로 재사용되도록 title/content 인자 구조 변경 및 클릭 이벤트 추가
 */
function showToast(title, content, pushId) {
	// jQuery가 로드되지 않았거나 Bootstrap이 로드되지 않았다면 실행 중지
	if (typeof $ === 'undefined' || typeof bootstrap === 'undefined') {
		console.error("jQuery 또는 Bootstrap 라이브러리가 로드되지 않았습니다.");
		return;
	}

	const toastId = `toast-${Date.now()}`;
	const detailUrl = DETAIL_BASE_URL + (pushId || '');

	const toastHtml = `
        <div id="${toastId}"
             class="toast align-items-center text-white bg-primary border-0 shadow-lg"
             role="alert"
             aria-live="assertive"
             aria-atomic="true"
             data-bs-delay="7000"
			 style="cursor: pointer;">
            <div class="d-flex align-items-center p-1">
                <i class='bx bx-bell fs-6 me-1'></i>
                <strong class="me-auto fw-bold" style="font-size: 0.85rem;">${title}</strong>
                <button type="button" class="btn-close btn-close-white ms-1" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body p-1 pt-0" style="font-size: 0.8rem;">
                ${content ? content.substring(0, 80) : '...'}...
            </div>
        </div>
    `;

	// 토스트 컨테이너가 없으면 추가
	if (!$('#toast-container').length) {
		$('body').append('<div id="toast-container" class="position-fixed top-0 end-0 p-3" style="z-index: 1080;"></div>');
	}

	$('#toast-container').append(toastHtml);
	const $toastElement = $(`#${toastId}`);
	const toastEl = new bootstrap.Toast(document.getElementById(toastId));

	// ⭐ [추가] 토스트 클릭 시 페이지 이동 및 읽음 처리
	$toastElement.on('click', function() {
		if (pushId) {
			markAsRead(pushId); // 뱃지 카운트를 정확하게 갱신하기 위해 읽음 처리
		}
		location.href = '/lms/notifications' ;
		toastEl.hide();
	});

	toastEl.show();

	$toastElement.on('hidden.bs.toast', function() {
		$(this).remove();
	});
}

/**
 * 🔔 [수정 완료] 실시간으로 알림 카운트 뱃지를 갱신하는 함수 (DOM 조작)
 * @param {number} increment - 증가시킬 값 (보통 1)
 */
function updateNotificationCount(increment) {
	const $badge = $('#unread-count-badge');

    // 뱃지 요소가 없으면 종료
    if ($badge.length === 0) {
        // console.error("뱃지 요소('#unread-count-badge')를 찾을 수 없습니다. DOM ID를 확인하세요.");
        return;
    }

	// 알림이 1개 도착할 때마다 뱃지 카운트를 +1 증가시키는 로직 (실시간 갱신)
	const currentCount = parseInt($badge.text() || 0);
	const updatedCount = currentCount + increment; // 👈 이제 'increment' 변수를 정확히 사용합니다.

	$badge.text(updatedCount);

    // 카운트가 0보다 크면 보이게 설정
    if (updatedCount > 0) {
        $badge.show();
    } else {
        $badge.hide();
    }

    console.log(`뱃지 갱신 완료: ${currentCount} -> ${updatedCount}`);

	// 드롭다운이 열려있다면 드롭다운 목록을 새로고침하여 갱신
	if ($('#notificationDropdown').parent().hasClass('show')) { // 드롭다운 컨테이너 확인
		fetchNotificationsForDropdown();
	}
}

// ====================================================================================================
// SECTION 3: 핵심 비동기 처리 함수 정의
// ====================================================================================================

/**
 * 🔔 [뱃지] 읽지 않은 알림 개수를 서버에서 조회하여 뱃지에 표시 (Ajax GET)
 */
function fetchUnreadCount() {
	// console.log("API Call: " + NOTI_API_URL + '/count-unread');
	$.ajax({
		url: NOTI_API_URL + '/count-unread',
		type: 'GET',
		dataType: 'json',
		success: function(count) {
			// console.log("읽지 않은 알림 수:", count);
			const $badge = $('#unread-count-badge');
			if (count > 0) {
				$badge.text(count);
				$badge.show();
			} else {
				$badge.hide();
			}
		},
		error: function(xhr, status, error) {
			console.error("읽지 않은 알림 카운트 조회 실패. 상태:", xhr.status, "오류:", error);
			$('#unread-count-badge').hide();
		}
	});
}

/**
 * 헬퍼 함수: 알림 읽음 처리 API 호출 (pushId 사용) (Ajax POST)
 */
function markAsRead(id) {
	// console.log(`알림 ${id} 읽음 처리 요청`);
	$.ajax({
		url: `${NOTI_API_URL}/${id}/read`,
		type: 'POST',
		success: function() {
			// console.log(`알림 ${id} 읽음 처리 성공`);
			// 성공 후 전체 뱃지 카운트를 다시 확인하여 갱신합니다.
			fetchUnreadCount();
		},
		error: function(xhr, status, error) {
			console.error(`알림 ${id} 읽음 처리 실패. 상태:`, status, "오류:", error);
		}
	});
}

/**
 * 🔔 [드롭다운] 알림 목록 데이터를 받아 드롭다운 메뉴에 렌더링합니다. (Ajax 응답 처리)
 * @param {Array<Object>} notifications - 알림 객체 배열
 */
function renderDropdownNotifications(notifications) {
	console.log("DEBUG: Starting dropdown rendering...");

	const container = $('#dropdown-list-container');
	container.empty();

	// 알림이 없더라도 드롭다운이 닫히지 않도록 메시지를 포함합니다.
	if (!Array.isArray(notifications) || notifications.length === 0) {
		container.append('<span class="dropdown-item text-muted">받은 알림이 없습니다.</span>');
		$('#dropdown-unread-count').text(0);
		return;
	}

	// ⭐ [수정 핵심] 읽지 않은 알림만 필터링 ⭐
    const unreadNotifications = notifications.filter(n => (n && n.isRead !== 'Y'));
    const unreadCount = unreadNotifications.length;

	console.log("DEBUG: 읽지 않은 알림:", unreadCount);

	$('#dropdown-unread-count').text(unreadCount);

    // 필터링된 배열을 기반으로 목록이 비었는지 다시 확인합니다.
	if (unreadNotifications.length === 0) {
        container.append('<span class="dropdown-item text-muted">읽지 않은 알림이 없습니다.</span>');
        return;
    }

	// 렌더링할 알림 목록을 필터링된 목록으로 변경합니다. (최대 5개)
	const limitedNotifications = unreadNotifications.slice(0, 5);

	try {
		limitedNotifications.forEach((noti) => {
			if (!noti) return;

			// ⭐ 발신자 정보 구성 (stfDeptName이 가장 우선되어야 함) ⭐
			const senderInfo = noti.senderDeptName || // 부서명 우선
				(noti.senderLastName && noti.senderFirstName ? noti.senderLastName + noti.senderFirstName : '') ||
				noti.sender ||
				'시스템';

			// 읽지 않은 알림만 표시되므로, isRead는 항상 'N'으로 간주하고 UI를 구성합니다.
			const readBgClass = 'bg-light';
			const readTextClass = 'text-dark fw-bold';
			const formattedReceiveTime = formatDate(noti.receiveAt || noti.createAt);
			const pushId = noti.pushId || '';
			const detailUrl = DETAIL_BASE_URL + pushId;
			const pushDetailText = noti.pushDetail || '알림 내용 없음';
			const titleText = noti.pushTitle || pushDetailText.substring(0, 30);

			// list-group-item 대신 드롭다운 메뉴 아이템으로 구성
			const itemHtml = `
                <a href="${detailUrl}" class="dropdown-item ${readBgClass} d-flex align-items-start py-2">
                    <i class="bx bxs-circle me-2 mt-1" style="font-size: 8px; color: #dc3545;"></i>
                    <div class="flex-grow-1">
                        <div class="d-flex w-100 justify-content-between">
                            <h6 class="mb-1 ${readTextClass}" style="font-size: 0.9rem; max-width: 75%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                ${titleText}
                            </h6>
                            <small class="text-secondary">${formattedReceiveTime.split(' ')[0]}</small>
                        </div>
                        <p class="mb-1 small text-secondary"
                           style="max-width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                           발신: ${senderInfo} / ${pushDetailText}
                        </p>
                    </div>
                </a>
            `;

			const $item = $(itemHtml);

			// 클릭 이벤트에 읽음 처리 로직 추가
			$item.on('click', (e) => {
				if (pushId) {
					// 페이지 이동 전 비동기로 읽음 처리 요청
					markAsRead(pushId);
				}
			});

			container.append($item);
		});

		console.log("DEBUG: 전체 렌더링 완료!");

	} catch (e) {
		console.error("드롭다운 알림 렌더링 중 오류 발생:", e);
		container.empty();
		container.append('<span class="dropdown-item text-danger fw-bold">데이터 렌더링 중 오류 발생: ' + e.name + '</span>');
	}
}


/**
 * 🔔 [전체 목록] 전체 목록 페이지를 렌더링합니다. (Ajax GET)
 */
function renderFullNotificationList() {
	// ... (기존 로직 유지)
	if (!$('#notification-area').length) {
		console.warn("WARN: #notification-area가 없으므로 renderFullNotificationList를 건너뜜니다.");
		return;
	}

	$.ajax({
		url: NOTI_API_URL,
		type: 'GET',
		dataType: 'json',
		success: function(notifications) {
			const area = $('#notification-area');
			area.empty();

			if (!Array.isArray(notifications) || notifications.length === 0) {
				area.append('<div class="card-body"><p class="card-text">받은 알림이 없습니다.</p></div>');
				fetchUnreadCount();
				return;
			}

			notifications.forEach(noti => {
				if (!noti) return;

				// ⭐ 발신자 정보 변수 추가 (stfDeptName이 가장 우선되어야 함) ⭐
				const senderInfo = noti.senderDeptName || // [수정] 부서명 우선
					(noti.senderLastName && noti.senderFirstName ? noti.senderLastName + noti.senderFirstName : '') ||
					noti.sender ||
					'시스템';

				const isRead = noti.isRead === 'Y';
				// 전체 목록 페이지에서는 읽음 여부에 따라 스타일 적용
				const readClass = isRead ? '' : 'unread';
				const rawDateTime = noti.receiveAt || noti.createAt;
				const formattedReceiveTime = formatDate(rawDateTime);
				const detailUrl = DETAIL_BASE_URL + (noti.pushId || '');

				const itemHtml = `
				    <a href="${detailUrl}" class="list-group-item list-group-item-action ${readClass} mb-2 shadow-sm rounded">
				        <div class="d-flex w-100 justify-content-between">
				            <h5 class="mb-1 text-dark" style="font-size: 1rem;">
				                ${noti.pushDetail || '알림 내용 없음'}
				            </h5>
				            <small class="text-secondary">
				                ${formattedReceiveTime}
				                ${!isRead ? '<span class="badge bg-danger ms-2" style="font-size: 10px; padding: 3px 8px;">NEW</span>' : ''}
				            </small>
				        </div>
				        <p class="mb-1 small text-muted">
				           발신: ${senderInfo}
				        </p>
				    </a>
				`;

				const $item = $(itemHtml);

				// 클릭 시 읽음 처리 로직 추가
				$item.on('click', (e) => {
					if (!isRead && noti.pushId) {
						markAsRead(noti.pushId);
					}
					// 페이지 이동 (e.preventDefault()는 제거하여 기본 동작 수행)
				});

				area.append($item);
			});

			fetchUnreadCount();
		},
		error: function(xhr, status, error) {
			console.error("알림 목록 조회 실패:", error);
			const area = $('#notification-area');
			let errorMessage = "알림 목록을 불러오는 중 오류가 발생했습니다.";
			if (xhr.status === 500) {
				errorMessage += "<br><strong class='text-danger'>[심각한 서버 오류 발생]</strong>";
			} else if (xhr.status === 403) {
				errorMessage += "<br><span class='text-secondary'>[403 접근 권한 오류] Spring Security 설정을 확인하세요.</span>";
			}
			area.html('<div class="card-body"><p class="card-text text-danger">' + errorMessage + '</p></div>');
			fetchUnreadCount();
		}
	});
}


/**
 * SockJS와 STOMP를 사용하여 WebSocket 서버에 연결 및 구독 (비동기 실시간 수신)
 */
function connectWebSocket() {
	// [수정] SockJS 및 StompJS 라이브러리 로드 확인
	if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
		console.error("SockJS 또는 StompJS 라이브러리가 로드되지 않았습니다. WebSockcet 연결을 건너뜜니다.");
		return;
	}

	if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
		console.error(`WebSocket 재연결 최대 시도 횟수(${MAX_RECONNECT_ATTEMPTS}) 도달. 연결을 중단합니다.`);
		$('#realtime-status').html('❌ <span class="text-danger">실시간 연결 실패 (재시도 중단)</span>');
		return;
	}

	const statusDiv = $('#realtime-status');
	const socket = new SockJS(WS_URL);
	stompClient = Stomp.over(socket);

	stompClient.debug = null; // 로그를 깔끔하게 유지합니다.

	stompClient.connect({}, function(frame) {
		// 연결 성공 시, 재시도 횟수 초기화
		reconnectAttempts = 0;
		if (statusDiv.length) {
			statusDiv.html('✅ <span class="text-success">실시간 서버 연결 성공</span>');
		}

		// 실시간 알림 구독
		stompClient.subscribe('/user/queue/notifications', function(notification) {

			// 1. 🚨 [h 필터링] 하트비트(h) 또는 빈 메시지 필터링
			if (!notification.body || notification.body === 'h' || notification.body.trim().length === 0) {
				console.log('STOMP Heartbeat/Empty 메시지 무시');
				return; // 실제 JSON이 아니므로 여기서 처리 중단
			}

			let messageBody;
			try {
				// 2. 🚨 [JSON 파싱] 메시지 본문(body)을 JSON 객체로 파싱
				messageBody = JSON.parse(notification.body);
				console.log("✅ JSON 파싱 성공:", messageBody); // 성공 로그 추가
			} catch (e) {
				// 파싱 실패 시, 콘솔에 원인 명확히 출력
				console.error("❌ 알림 메시지 JSON 파싱 오류. 원본:", notification.body, "오류:", e);
				return; // 파싱 실패 시 처리 중단
			}

			// 3. 🚨 [뱃지 갱신] 알림 숫자를 즉시 갱신 (성공 시에만 실행)
			updateNotificationCount(1);

			// 4. 🚨 [팝업 표시] Toast 사용 (성공 시에만 실행)
			const title = messageBody.senderDeptName ? `${messageBody.senderDeptName}에서 알림` : '새 알림';
			showToast(title, messageBody.pushDetail, messageBody.pushId);
		});

	}, function(error) {
		// 연결 실패 시 처리
		reconnectAttempts++;
		const nextAttemptIn = 5000 + (reconnectAttempts * 1000); // 지연 시간 점진적 증가

		if (statusDiv.length) {
			statusDiv.html(`❌ <span class="text-danger">연결 실패. ${Math.ceil(nextAttemptIn / 1000)}초 후 재시도 (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})</span>`);
		}

		console.error("WebSocket 연결 오류:", error);
		// 연결 실패 시 재귀적 재시도
		setTimeout(connectWebSocket, nextAttemptIn);
	});
}

/**
 * 드롭다운이 열릴 때 알림을 불러오는 로직을 별도 함수로 분리 (재사용성 향상)
 */
function fetchNotificationsForDropdown() {
	$.ajax({
		url: NOTI_API_URL,
		type: 'GET',
		dataType: 'json',
		success: function(notifications) {
			renderDropdownNotifications(notifications);
		},
		error: function(xhr, status, error) {
			const container = $('#dropdown-list-container');
			container.empty();
			const errorCode = xhr.status || '연결';

			if (xhr.status === 500) {
				container.append('<span class="dropdown-item text-danger fw-bold">알림 로드 실패 (500 서버 오류)</span>');
			} else {
				container.append('<span class="dropdown-item text-danger fw-bold">알림 로드 실패 (' + errorCode + ' 오류)</span>');
			}
			console.error("드롭다운 알림 목록 조회 실패. 상태:", status, "오류:", error, "XHR:", xhr);
			fetchUnreadCount();
		}
	});
}


// ====================================================================================================
// SECTION 4: 초기화 및 이벤트 등록 (가장 마지막에 실행되는 부분)
// ====================================================================================================

/**
 * 🔔 [시작] 페이지 로드 및 이벤트 등록
 */
// [수정] window.addEventListener('load') 대신 jQuery의 ready 함수를 사용하여 DOM 준비 완료 시 실행
$(function() {
	// 1. 초기 뱃지 카운트 비동기 로드
	fetchUnreadCount();

	// 2. WebSocket 연결 시작 (비동기)
	connectWebSocket();

	// 3. '전체 알림 목록' 페이지인 경우에만 전체 목록 비동기 로드
	// jQuery를 사용하여 DOM 존재 여부를 확인합니다.
	if ($('#notification-area').length > 0) {
		renderFullNotificationList();
	}

	// 4. 드롭다운 이벤트 핸들러 등록
	const $dropdown = $('#notificationDropdown');

	if ($dropdown.length) {
		// 드롭다운 요소가 DOM에 존재하는지 확인 후 이벤트 등록
		$dropdown.on('show.bs.dropdown', function() {
			console.log("DEBUG: Dropdown 'show' event triggered. Fetching notifications.");

			// 드롭다운이 열리면 뱃지 카운트를 즉시 숨김 (시각적 처리)
			// 알림 목록이 로드된 후 fetchUnreadCount()를 호출하여 최종 카운트를 갱신합니다.
			// 읽지 않은 알림만 드롭다운에 표시하므로 뱃지 카운트도 0으로 숨기는 것이 논리적으로 맞습니다.
			$('#unread-count-badge').hide().text(0);

			// 드롭다운이 열리면 알림 목록을 비동기 조회 및 렌더링
			fetchNotificationsForDropdown();

		}).on('hidden.bs.dropdown', function() {
			// 드롭다운이 닫힐 때 뱃지 카운트를 다시 조회하여 정확하게 표시
			fetchUnreadCount();
		});
	} else {
		console.log("INFO: 드롭다운 토글 요소(#notificationDropdown)를 찾을 수 없습니다. 포탈 페이지의 HTML 구조를 확인하세요.");
	}
});