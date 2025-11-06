/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 14.     	정태일            최초 생성
 * 2025. 10. 17.     	정태일            하드코딩된 일정 데이터를 API 연동으로 변경
 * 2025. 10. 17.     	정태일            학사일정 필터링 기능 추가
 * 2025. 10. 17.     	정태일            학사일정 등록,수정 삭제 모달 및 기능 추가
 * 2025. 11. 03.     	정태일            일정등록 자동완성 추가
 *
 * </pre>
 */

document.addEventListener('DOMContentLoaded', function() {
	const calendarEl		   = document.getElementById('calendar');
	const upcomingEventsListEl = document.getElementById('upcomingEventsList'); // 오른쪽 목록 요소
	const termFilterEl		   = document.getElementById('termFilter');
	const categoryFilterEl 	   = document.getElementById('categoryFilter');
    const viewAllEventsLink    = document.getElementById('viewAllEventsLink'); // 전체 일정 목록보기 링크
    const allEventsListContainer = document.getElementById('allEventsListContainer'); // 전체 일정 목록 컨테이너
    const allEventsList        = document.getElementById('allEventsList'); // 전체 일정 목록
    const backToCalendarBtn    = document.getElementById('backToCalendarBtn'); // 캘린더로 돌아가기 버튼
    const mainContainer        = document.querySelector('.main-container'); // 메인 컨테이너

    // 일정 등록 모달 관련 요소
    const eventModal           = document.getElementById('eventModal');
    const closeModalBtn        = document.getElementById('closeModalBtn');
    const eventForm            = document.getElementById('eventForm');
    const createEventBtn       = document.getElementById('createEventBtn'); // createEventBtn은 여기서 한 번만 선언
    const modalTitle           = document.getElementById('modalTitle');
    const deleteEventBtn       = document.getElementById('deleteEventBtn'); // 삭제 버튼

	const calendar = new FullCalendar.Calendar(calendarEl, {
		// --- 기본 설정 ---
		initialView: 'dayGridMonth',
		locale: 'ko',
		height: 'auto', // 캘린더 크기를 컨테이너 높이에 맞게 자동 조정 (반응형 유리)
		firstDay: 0, // 일요일 시작 (0)

		// --- 헤더/버튼 설정 ---
		headerToolbar: {
			left: 'prev,next today',
			center: 'title',
			right: 'dayGridMonth,timeGridWeek,timeGridDay,listMonth' // 목록 뷰 추가
		},
		buttonText: {
			today: '오늘',
			month: '월',
			week:  '주',
			day:   '일',
			list:  '목록'
		},

		// --- 시간 표시 설정 ---
		// 주/일 보기의 시간 형식 (예: 오전 9:00)
		eventTimeFormat: {
			hour: 'numeric',
			minute: '2-digit',
			meridiem: 'short'
		},
		// 월 보기에서는 이벤트 시간 숨기기
		displayEventTime: false,

		// --- 달력 안에 (일) 제거
		dayCellContent: function(info) {
			// info.date.getDate()는 해당 날짜의 숫자(1~31)를 반환합니다.
			// FullCalendar의 기본 렌더링을 덮어써서 '일'을 표시하지 않습니다.
			return info.date.getDate();
		},

		// --- 이벤트 설정 ---
		// 서버 API 엔드포인트에서 이벤트 데이터를 가져오도록 설정
		events: {
            url: '/portal/univcalendar/events',
            method: 'GET',
            extraParams: function() { // 필터 파라미터를 동적으로 추가
                return {
                    termFilter: termFilterEl ? termFilterEl.value : '',
                    categoryFilter: categoryFilterEl ? categoryFilterEl.value : ''
                };
            }
        },
		eventDisplay: 'block',
		dayMaxEvents: 3,
//		eventColor: '#3788d8', // 모든 이벤트의 기본 색상 (필요시)
//		eventBackgroundColor: function(event) { return event.backgroundColor; }, // 백엔드에서 받은 색상 사용
//		eventBorderColor: function(event) { return event.borderColor; }, // 백엔드에서 받은 색상 사용

		// --- 사용자 정의 콜백 ---
		// 캘린더의 날짜 범위가 변경될 때마다 호출 (월 이동 시)
//		datesSet: function(dateInfo) {
		eventsSet: function(events) {
			updateUpcomingEvents(calendar, upcomingEventsListEl);
		},
        // 날짜 클릭 시 이벤트 (일정 등록 모달 열기)
        dateClick: function(info) {
			if (isStaff){
            openEventModal('add', info.dateStr);
			}
        },
        // 이벤트 클릭 시 이벤트 (일정 수정/삭제 모달 열기)
        eventClick: function(info) {
			if (isStaff){
            openEventModal('edit', info.event.id);
            }
        }
	});

	calendar.render();

	// 초기 로드 시 목록 업데이트
	// updateUpcomingEvents(calendar, upcomingEventsListEl); // eventsSet 콜백으로 대체됨 

	/* ----------------------------------------------------
	 * 함수: 금월 주요 일정 목록 업데이트
	 * ---------------------------------------------------- */
	function updateUpcomingEvents(calendarInstance, listElement) {
		// 현재 캘린더의 중앙에 표시된 월(Month)을 가져옵니다.
		const currentMonthStart = calendarInstance.getDate();
		const currentMonth = currentMonthStart.getMonth();
		const currentYear = currentMonthStart.getFullYear();

		// 모든 이벤트를 가져와 현재 월에 해당하는 이벤트만 필터링합니다.
		const allEvents = calendarInstance.getEvents();

		const filteredEvents = allEvents
			.filter(event => {
				const eventStart = event.start;
				// 이벤트 시작일이 현재 캘린더의 월과 같은 경우만 포함
				return eventStart &&
					eventStart.getMonth() === currentMonth &&
					eventStart.getFullYear() === currentYear;
			})
			.sort((a, b) => a.start - b.start); // 시작 날짜 기준으로 정렬

		// 목록 HTML 생성 및 업데이트
		listElement.innerHTML = ''; // 기존 목록 초기화

		if (filteredEvents.length === 0) {
			listElement.innerHTML = '<p class="no-events">이번 달에 예정된 주요 일정이 없습니다.</p>';
			return;
		}

		filteredEvents.forEach(event => {
			// 날짜 포맷팅 (예: 10/14 (화))
			const startDate = FullCalendar.formatDate(event.start, {
				month: '2-digit',
				day: '2-digit',
				weekday: 'short',
				locale: 'ko'
			});

			let endDate = '';
			// 다중 일정이거나 시작일과 종료일이 다른 경우 종료일 포맷팅
			if (event.end &&
				FullCalendar.formatDate(event.start, { year: 'numeric', month: 'numeric', day: 'numeric' }) !==
				FullCalendar.formatDate(event.end, { year: 'numeric', month: 'numeric', day: 'numeric' })) {

				// FullCalendar의 end는 Exclusive이므로, 실제 종료일을 위해 하루 전으로 계산
				const actualEnd = new Date(event.end.valueOf() - 1);
				endDate = ' ~ ' + FullCalendar.formatDate(actualEnd, {
					month: '2-digit',
					day: '2-digit',
					weekday: 'short',
					locale: 'ko'
				});
			}

			// 목록 아이템 HTML 생성
			const itemHtml = `
                <div class="event-item" style="border-left: 5px solid ${event.backgroundColor || '#007bff'};">
                    <span class="event-date">${startDate}${endDate}</span>
                    <span class="event-title">${event.title}</span>
                </div>
            `;
			listElement.innerHTML += itemHtml;
		});
	}

	/* ----------------------------------------------------
	 * 필터링 로직 (백엔드 연동 필요)
	 * ---------------------------------------------------- */

	function handleFilterChange() {
		console.log(`필터 변경 감지: 학기=${termFilterEl.value}, 분류=${categoryFilterEl.value}`);
		// API 호출 후 calendar.refetchEvents()를 호출하여 캘린더를 업데이트합니다。
		calendar.refetchEvents();
	}

	if (termFilterEl) {
		termFilterEl.addEventListener('change', handleFilterChange);
	}
	if (categoryFilterEl) {
		categoryFilterEl.addEventListener('change', handleFilterChange);
	}

/* ----------------------------------------------------
 * 함수: 전체 일정 목록 렌더링 (격자 레이아웃 및 색상 적용)
 * ---------------------------------------------------- */
function renderAllEventsList() {
    // 캘린더 범례 색상 정의 (기존 캘린더 CSS의 색상 매핑)
    const categoryColors = {
        '등록': '#007bff', // var(--primary-color)
        '시험': '#ffc107', 
        '방학': '#28a745',
        '행사': '#6c757d',
        '수강신청': '#6f42c1',
        'DEFAULT': '#4a90e2' // 기본 색상
    };
    
    fetch('/portal/univcalendar/events') 
        .then(response => response.json())
        .then(events => {
            const allEventsList = document.getElementById('allEventsList');
            const allEventsListContainer = document.getElementById('allEventsListContainer');
            allEventsList.innerHTML = ''; 

            if (events.length === 0) {
                allEventsList.innerHTML = '<p class="no-events">등록된 전체 일정이 없습니다.</p>';
                // ... (컨테이너 표시 로직)
                return;
            }

            events.sort((a, b) => new Date(a.start) - new Date(b.start));

            const groupedEvents = events.reduce((acc, event) => {
                const eventDate = new Date(event.start);
                const month = (eventDate.getMonth() + 1).toString().padStart(2, '0');
                if (!acc[month]) { acc[month] = []; }
                acc[month].push(event);
                return acc;
            }, {});
            
            const monthNamesKo = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
            const monthNamesEn = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            
            let htmlContent = '<div class="academic-grid">'; // 최상위 격자 컨테이너 시작
            
            // 3. 월별 그룹을 순회하며 HTML 생성
            Object.keys(groupedEvents).sort().forEach(monthKey => {
                const monthIndex = parseInt(monthKey, 10) - 1;
                const monthKo = monthNamesKo[monthIndex];
                const monthEn = monthNamesEn[monthIndex];
                const eventsInMonth = groupedEvents[monthKey];
                
                // 각 월별 데이터를 담을 격자 셀 시작
                htmlContent += `<div class="grid-column">`; 
                
                // 월 헤더 (좌측 세로 영역)
                htmlContent += `
                    <div class="month-header-grid">
                        <span class="month-ko-grid">${monthKo}</span>
                        <span class="month-en-grid">${monthEn}</span>
                    </div>
                    <div class="month-events-list">
                `;
                
                // 해당 월의 이벤트 목록
                eventsInMonth.forEach(event => {
                    const category = event.scheduleCd || 'DEFAULT';
                    const color = event.backgroundColor || categoryColors[category] || categoryColors['DEFAULT'];
                    
                    // 날짜 포맷팅: 11월 18일 (월) 
                    const startDate = FullCalendar.formatDate(event.start, {
                        month: 'numeric',
                        day: '2-digit',
                        weekday: 'short',
                        locale: 'ko'
                    });

                    // 이벤트 기간 처리
                    let period = `${startDate}`;
                    if (event.end) {
                        const startDay = FullCalendar.formatDate(event.start, { year: 'numeric', month: 'numeric', day: 'numeric' });
                        const actualEnd = new Date(new Date(event.end).valueOf() - 1); 
                        const endDay = FullCalendar.formatDate(actualEnd, { year: 'numeric', month: 'numeric', day: 'numeric' });
                        
                        // 시작일과 종료일이 다를 경우에만 기간 표시 (월일(요일) ~ 월일(요일))
                        if (startDay !== endDay) {
                            period = `${startDate} ~ ${FullCalendar.formatDate(actualEnd, { month: 'numeric', day: '2-digit', weekday: 'short', locale: 'ko' })}`;
                        }
                    }

                    // 이벤트 항목 HTML (좌측 색상 바 추가)
                    htmlContent += `
                        <div class="event-row" style="border-left-color: ${color};">
                            <span class="event-date-grid">${period}</span>
                            <span class="event-title-grid">${event.title}</span>
                        </div>
                    `;
                });
                
                htmlContent += `</div></div>`; // .month-events-list, .grid-column 닫기
            });

            htmlContent += '</div>'; // .academic-grid 닫기

            allEventsList.innerHTML = htmlContent;
            
            allEventsListContainer.style.display = 'block'; 
            allEventsListContainer.scrollIntoView({ behavior: 'smooth', block: 'start' }); 
        })
        .catch(error => {
            console.error('Error fetching all events:', error);
            document.getElementById('allEventsList').innerHTML = '<p class="error-message">전체 일정을 불러오는 중 오류가 발생했습니다.</p>';
            document.getElementById('allEventsListContainer').style.display = 'block';
            document.getElementById('allEventsListContainer').scrollIntoView({ behavior: 'smooth', block: 'start' }); 
        });
}

	// 전체 일정 목록보기 링크 클릭 이벤트
	if (viewAllEventsLink) {
		viewAllEventsLink.addEventListener('click', function(e) {
			e.preventDefault(); // 기본 링크 동작 방지
			renderAllEventsList();
		});
	}

	// 교직원/관리자용 일정 등록 버튼 이벤트 (모달 구현 시 필요)
	if (createEventBtn) {
		createEventBtn.addEventListener('click', function() {
			openEventModal('add');
		});
	}

	// 모달 닫기 버튼
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', function() {
            eventModal.style.display = 'none';
        });
    }

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target == eventModal) {
            eventModal.style.display = 'none';
        }
    });

    // 캘린더로 돌아가기 버튼 클릭 이벤트
    if (backToCalendarBtn) {
        backToCalendarBtn.addEventListener('click', function() {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }

////////////////////////////////////////

    // 삭제 버튼 클릭 이벤트
    deleteEventBtn.addEventListener('click', function() {
        const calendarId = document.getElementById('calendarId').value;
        if (!calendarId) {
            Swal.fire('오류', '삭제할 일정의 ID가 없습니다.', 'error');
            return;
        }

        Swal.fire({
            title: '정말 삭제하시겠습니까?',
            text: "삭제된 일정은 복구할 수 없습니다.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch('/portal/univcalendar/events/' + calendarId, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        Swal.fire('삭제 완료', data.message, 'success');
                        eventModal.style.display = 'none';
                        calendar.refetchEvents();
                    } else {
                        Swal.fire('삭제 실패', data.message, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire('오류', '일정 삭제 중 오류가 발생했습니다.', 'error');
                });
            }
        });
    });

////////////////////////////////////////

    // 일정 등록/수정 모달 열기 함수
    function openEventModal(mode, data) {
        eventForm.reset(); // 폼 초기화
        deleteEventBtn.style.display = 'none'; // 삭제 버튼 숨김

        if (mode === 'add') {
            modalTitle.textContent = '새 일정 등록';
            // 날짜 클릭 시 시작일 자동 설정
            if (data) {
                document.getElementById('eventStart').value = data + 'T09:00'; // 기본 시간 09:00
            }
        } else if (mode === 'edit') {
            modalTitle.textContent = '일정 수정';
            deleteEventBtn.style.display = 'inline-block'; // 수정 모드에서 삭제 버튼 표시
            // data(event.id)를 사용하여 서버에서 일정 상세 정보 조회 후 폼 채우기
            fetch('/portal/univcalendar/events/' + data) // data is the calendarId
                .then(response => {
                    if (!response.ok) {
                        throw new Error('일정 정보를 가져오는 데 실패했습니다.');
                    }
                    return response.json();
                })
                .then(event => {
                    // 폼에 데이터 채우기
                    document.getElementById('calendarId').value   = event.calendarId;
                    document.getElementById('eventTitle').value   = event.scheduleName;
                    document.getElementById('scheduleCd').value   = event.scheduleCd;
                    document.getElementById('yeartermCd').value   = event.yeartermCd;
                    document.getElementById('shareScopeCd').value = event.shareScopeCd;

                    // datetime-local 형식(YYYY-MM-DDTHH:mm)에 맞게 문자열 자르기
                    if (event.startAt) {
                        document.getElementById('eventStart').value = event.startAt.substring(0, 16);
                    }
                    if (event.endAt) {
                        document.getElementById('eventEnd').value = event.endAt.substring(0, 16);
                    }
                })
                .catch(error => {
                    console.error('Error fetching event details:', error);
                    Swal.fire({
                        icon: 'error',
                        title: '오류',
                        text: '일정 정보를 불러오는 중 오류가 발생했습니다.'
                    });
                    // 오류 발생 시 모달을 닫음
                    eventModal.style.display = 'none';
                });
        }
        
 ////////////////////////////////////////     
           
        eventModal.style.display = 'flex';
    }

    // 폼 제출 처리
    eventForm.addEventListener('submit', function(e) {
        e.preventDefault(); // 기본 제출 동작 방지

        const formData = new FormData(eventForm);
        const eventData = Object.fromEntries(formData.entries());

        // calendarId가 있으면 수정, 없으면 등록
        const isEditMode = eventData.calendarId && eventData.calendarId !== '';
        const method = isEditMode ? 'PUT' : 'POST'; // 수정은 PUT, 등록은 POST
        const url = '/portal/univcalendar/events' + (isEditMode ? '/' + eventData.calendarId : '');

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(eventData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
				Swal.fire({
					icon: 'success',
					title: '성공',
					text: data.message
				});				
                eventModal.style.display = 'none'; // 모달 닫기
                calendar.refetchEvents(); // 캘린더 이벤트 새로고침
                updateUpcomingEvents(calendar, upcomingEventsListEl); // 목록 업데이트
            } else {
				Swal.fire({
					icon: 'error',
					title: '오류',
					text: data.message
				});		
            }
        })
        .catch(error => {
            console.error('Error:', error);
				Swal.fire({
					icon: 'error',
					title: '오류',
					text: '일정 저장 중 오류가 발생했습니다.'
				});
        });
    });

    // '예시 일정 채우기' 버튼 클릭 이벤트
    const fillAcademicEventBtn = document.getElementById('fillAcademicEventBtn');
    if (fillAcademicEventBtn) {
        fillAcademicEventBtn.addEventListener('click', () => {
            document.getElementById('eventTitle').value = '2025학년도 2학기 수강신청 기간';
            document.getElementById('eventStart').value = '2025-11-10T09:00';
            document.getElementById('eventEnd').value = '2025-11-14T17:00';
            document.getElementById('scheduleCd').value = '수강신청';
            document.getElementById('yeartermCd').value = '2025_REG2';
        });
    }
});