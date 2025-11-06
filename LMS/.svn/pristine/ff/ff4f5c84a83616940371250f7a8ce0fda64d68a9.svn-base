// 교직원에서 쓰는 알림이랑 다른 소켓임
// 테이블 전체를 업로드 하는 방법에서 => 정원 부분과 같이 변화하는 셀 부분만 업데이트 하는 방법으로 바꿈 

(function() {
    if (!document.querySelector('.lecture-table')) {
        return;
    }

    let lectureSubscription = null;
    let reconnectTimer = null;
    let reconnectAttempts = 0;
    const MAX_RECONNECT_ATTEMPTS = 30; // 최대 시도 횟수

    /**
     * 웹소켓 구독 시작
     */
    window.connectWishlistWebSocket = function() {
        console.log('[수강신청]WebSocket 구독 시작 시도...');

        // 기존 타이머 정리
        if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        }

        // 최대 재시도 체크
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            console.error('[수강신청] 최대 재시도 횟수 도달');
            return;
        }
        reconnectAttempts++;

        // stompClient 확인
        if (typeof stompClient !== 'undefined' && stompClient !== null) {
            if (stompClient.connected) {
                console.log('[수강신청] WebSocket 연결됨! 구독 시작');
                reconnectAttempts = 0; // 성공 시 카운트 리셋
                subscribeToLectureEnroll();
            } else {
                console.log(`[수강신청] 연결 대기 중... (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})`);
                reconnectTimer = setTimeout(window.connectWishlistWebSocket, 1000);
            }
        } else {
            console.log(`[수강신청] stompClient 대기 중... (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})`);
            reconnectTimer = setTimeout(window.connectWishlistWebSocket, 1000);
        }
    };

    /**
     * 정원 업데이트 구독
     */
    function subscribeToLectureEnroll() {
        // 기존 구독이 있으면 먼저 해제
        if (lectureSubscription) {
            try {
                lectureSubscription.unsubscribe();
                console.log('[수강신청] 기존 구독 해제');
            } catch (e) {
                console.warn('[수강신청] 기존 구독 해제 실패:', e);
            }
            lectureSubscription = null;
        }

        try {
            console.log('[수강신청] /topic/lecture-enroll 구독 시도...');

            lectureSubscription = stompClient.subscribe('/topic/lecture-enroll', function(message) {
                // 하트비트 무시
                if (!message.body || message.body === 'h' || message.body.trim().length === 0) {
                    return;
                }

                try {
                    const data = JSON.parse(message.body);
                    console.log('🔔 [수강신청] 웹소켓 메시지 수신:', data);
			        console.log('🔔 lectureId:', data.lectureId);
			        console.log('🔔 currentEnroll:', data.currentEnroll);
			        console.log('🔔 maxCap:', data.maxCap);

                    // 화면 업데이트
                    updateLectureEnroll(data.lectureId, data.currentEnroll, data.maxCap);
                } catch (e) {
                    console.error('[수강신청] JSON 파싱 실패:', e);
                }
            });

            console.log('[수강신청] 구독 완료');

        } catch (error) {
            console.error('[수강신청] 구독 실패:', error);
            lectureSubscription = null;
            
            // 구독 실패 시 재시도
            reconnectTimer = setTimeout(window.connectWishlistWebSocket, 2000);
        }
    }

    /**
     * 구독 해제
     */
    window.disconnectWishlistWebSocket = function() {
        if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        }

        if (lectureSubscription) {
            try {
                lectureSubscription.unsubscribe();
                console.log('[수강신청] 구독 해제 완료');
            } catch (e) {
                console.error('구독 해제 중 오류:', e);
            }
            lectureSubscription = null;
        }
        
        reconnectAttempts = 0; // 카운트 리셋
    };

    /**
     * 화면의 정원 정보 업데이트
     */
    function updateLectureEnroll(lectureId, currentEnroll, maxCap) {
        console.log('[수강신청] 화면 업데이트:', lectureId, currentEnroll, maxCap);

		console.log('📍 [수강신청] updateLectureEnroll 호출됨');
	    console.log('📍 lectureId:', lectureId);
	    console.log('📍 currentEnroll:', currentEnroll);
	    console.log('📍 maxCap:', maxCap);
		
        const rows = document.querySelectorAll('.lecture-row');
        
        rows.forEach(row => {
            const onclickAttr = row.getAttribute('onclick');
            if (onclickAttr && onclickAttr.includes(lectureId)) {
                
                // 1. 정원 셀 업데이트
                const enrollCell = document.getElementById(`enroll-${lectureId}`);
                const rateCell = document.getElementById(`rate-${lectureId}`);

                const rate = maxCap > 0 ? Math.round((currentEnroll / maxCap) * 100 * 10) / 10 : 0;
                let statusClass = 'status-low';

                if (currentEnroll >= maxCap) {
                    statusClass = 'status-full';
                } else if (maxCap > 0) {
                    const fillRate = currentEnroll / maxCap;
                    if (fillRate >= 0.8) statusClass = 'status-high';
                    else if (fillRate >= 0.5) statusClass = 'status-medium';
                }

                if (enrollCell) {
                    enrollCell.className = `enroll-info ${statusClass} updated`;
                    let enrollHtml = `<strong>${currentEnroll}</strong> / ${maxCap}`;
                    if (currentEnroll >= maxCap) {
                        enrollHtml += ' <span class="badge-full">마감</span>';
                    }
                    enrollCell.innerHTML = enrollHtml;

                    setTimeout(() => enrollCell.classList.remove('updated'), 1000);
                }

                if (rateCell) {
                    rateCell.innerHTML = `
                        <div class="enroll-rate-wrapper">
                            <div class="progress-bar">
                                <div class="progress-fill ${statusClass}" style="width: ${rate}%"></div>
                            </div>
                            <span class="rate-text">${rate}%</span>
                        </div>
                    `;
                    rateCell.classList.add('updated');
                    setTimeout(() => rateCell.classList.remove('updated'), 1000);
                }

                // 2. 버튼 상태 업데이트 - 비활성화 (정원 정보만 업데이트)
	            // 신청 버튼은 웹소켓으로 업데이트하지 않음
            }
        });

        // 신청 현황 업데이트
        if (typeof updateApplyStatus === 'function') {
            updateApplyStatus();
        }
        
        // 교수 페이지 업데이트 부분
    	console.log('교수 함수 체크:', typeof window.updateLectureEnrollRealtime);

        // ========================================
	    // 3. 교수 페이지 실시간 업데이트
	    // ========================================
	    if (typeof window.updateLectureEnrollRealtime === 'function') {
	        console.log('[교수] 실시간 업데이트 호출 시작');
	        try {
	            window.updateLectureEnrollRealtime(lectureId, currentEnroll, maxCap);
	            console.log('[교수] 실시간 업데이트 완료');
	        } catch (error) {
	            console.error('[교수] 실시간 업데이트 실패:', error);
	        }
	    } else {
	        console.log('[교수] updateLectureEnrollRealtime 함수 없음');
	    }
    }

    // 페이지 언로드 시 정리
    window.addEventListener('beforeunload', function() {
        window.disconnectWishlistWebSocket();
    });

    // 페이지 visibility 변경 시 재연결 처리
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            console.log('[수강신청] 페이지 활성화 - 연결 상태 확인');
            // WebSocket 연결 끊어졌으면 재연결
            if (!stompClient || !stompClient.connected) {
                reconnectAttempts = 0;
                window.connectWishlistWebSocket();
            }
        }
    });

    console.log('[수강신청] wishlist-websocket.js 로드 완료');
})();