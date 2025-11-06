/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -----------   -------------    ---------------------------
 * 2025. 10. 22.     정태일            최초 생성
 *
 * </pre>
 */


document.addEventListener('DOMContentLoaded', function () {
  "use strict";

  // 1. Constants & Configuration
  const GRID_SLOT_PX = 40;
  const DAY_START_MIN = 9 * 60;
  const DAY_END_MIN = 18 * 60;
  const DEFAULT_DURATION_MIN = 60;
  const STEP_MIN = 60;
  const MAX_DAYS = 7;

  // 2. DOM References
  const calendarView = document.getElementById('calendar-view');
  const modalElement = document.getElementById('reservation-modal');
  const reservationModal = new bootstrap.Modal(modalElement);
  const reservationForm = document.getElementById('reservation-form');
  const calendarGrid = calendarView.querySelector('.calendar-grid-weekly');
  const resDateInput = document.getElementById('res-date');
  const resEndDateInput = document.getElementById('res-end-date');
  const startTimeEl = document.getElementById('start-time');
  const endTimeEl = document.getElementById('end-time');
  const reasonInput = document.getElementById('res-reason');
  const headcountInput = document.getElementById('res-headcount');
  const prevWeekBtn = document.getElementById('prev-week-btn');
  const nextWeekBtn = document.getElementById('next-week-btn');
  const currentWeekDisplay = document.getElementById('current-week-display');

  // 3. State
  let currentCalendarStartDate = new Date();
  let weeklyReservations = []; // Store fetched reservations
  let currentReservationIdForModification = null; // To track if we are modifying an existing reservation

  // 4. Utility Functions
  function minutesToHHmm(mins) {
    const h = String(Math.floor(mins / 60)).padStart(2, '0');
    const m = String(mins % 60).padStart(2, '0');
    return `${h}:${m}`;
  }

  function clamp(v, lo, hi) { return Math.max(lo, Math.min(hi, v)); }

  function roundToStep(mins, step) { return Math.round(mins / step) * step; }

  function fmtDate(d) {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  function parseDate(str) {
    const [y, m, d] = str.split('-').map(Number);
    return new Date(y, m - 1, d);
  }

  function addDays(str, n) {
    const d = parseDate(str);
    d.setDate(d.getDate() + n);
    return fmtDate(d);
  }

  function diffDaysInclusive(a, b) {
    const da = parseDate(a), db = parseDate(b);
    return Math.floor((db - da) / (1000 * 60 * 60 * 24)) + 1;
  }

  function getCurrentCalendarDates() {
    const start = new Date(currentCalendarStartDate);
    const end = new Date(currentCalendarStartDate);
    end.setDate(end.getDate() + 6);
    return { start: fmtDate(start), end: fmtDate(end) };
  }

  // 5. Core Functions
  async function loadReservations(placeCd, startDate, endDate) {
    calendarGrid.querySelectorAll('.reservation-item').forEach(item => item.remove());

    if (!placeCd) return;

    try {
      const url = `${C_PATH}/api/facilities/${placeCd}/reservations?startAt=${startDate}T00:00:00&endAt=${endDate}T23:59:59`;
      const res = await fetch(url);
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const reservations = await res.json();
      weeklyReservations = reservations; // Store fetched reservations

      reservations.forEach(res => {
        const resStart = new Date(res.startAt);
        const resEnd = new Date(res.endAt);

        // Calculate the number of days the reservation spans
        const daysSpan = diffDaysInclusive(fmtDate(resStart), fmtDate(resEnd));

        for (let i = 0; i < daysSpan; i++) {
          const currentDay = new Date(resStart);
          currentDay.setDate(resStart.getDate() + i);
          const resDate = fmtDate(currentDay);

          const dayColumn = calendarGrid.querySelector(`.day-column[data-date="${resDate}"]`);
          if (!dayColumn) continue; // Skip if day column is not in current view

          let currentDayStartMinutes = DAY_START_MIN;
          let currentDayEndMinutes = DAY_END_MIN;

          // Adjust start/end times for the current day
          if (i === 0) { // First day of reservation
            currentDayStartMinutes = resStart.getHours() * 60 + resStart.getMinutes();
          }
          if (i === daysSpan - 1) { // Last day of reservation
            currentDayEndMinutes = resEnd.getHours() * 60 + resEnd.getMinutes();
          }

          // Clamp to operating hours
          currentDayStartMinutes = clamp(currentDayStartMinutes, DAY_START_MIN, DAY_END_MIN);
          currentDayEndMinutes = clamp(currentDayEndMinutes, DAY_START_MIN, DAY_END_MIN);

          // Only render if there's a valid time span for the day
          if (currentDayEndMinutes <= currentDayStartMinutes) continue;

          const relativeStartMinutes = currentDayStartMinutes - DAY_START_MIN;
//          const top = (relativeStartMinutes / 60) * GRID_SLOT_PX;
		  const top = relativeStartMinutes * (GRID_SLOT_PX / 60);
          const height = ((currentDayEndMinutes - currentDayStartMinutes) / 60) * GRID_SLOT_PX;

          const reservationItem = document.createElement('div');
          reservationItem.className = `reservation-item reservation-block ${res.userId ? 'my-reservation' : 'other-reservation'}`;
          reservationItem.dataset.reserveId = res.reserveId; // Add this line
          reservationItem.style.top = `${top}px`;
          reservationItem.style.height = `${height}px`;
          reservationItem.innerHTML = `
          <div class="time">${minutesToHHmm(currentDayStartMinutes)} - ${minutesToHHmm(currentDayEndMinutes)}</div>
                                <div class="reason">예약중</div>`;
          dayColumn.appendChild(reservationItem);
        }
      });
    } catch (err) {
      console.error('Error loading reservations:', err);
      Swal.fire('예약 현황을 불러오는 데 실패했습니다.', '', 'error');
    }
  }

  function renderCalendar(startDate) {
    currentCalendarStartDate = new Date(startDate);

    const dayNames = ['일', '월', '화', '수', '목', '금', '토'];
    const gridHeaders = calendarGrid.querySelectorAll('.grid-header');
    const dayColumns = calendarGrid.querySelectorAll('.day-column');

    const startOfWeek = new Date(currentCalendarStartDate);
    const endOfWeek = new Date(currentCalendarStartDate);
    endOfWeek.setDate(endOfWeek.getDate() + 6);

    currentWeekDisplay.textContent = `${startOfWeek.getFullYear()}년 ${startOfWeek.getMonth() + 1}월`;

    for (let i = 0; i < 7; i++) {
      const date = new Date(currentCalendarStartDate);
      date.setDate(currentCalendarStartDate.getDate() + i);

      const monthDay = `${date.getMonth() + 1}/${date.getDate()}`;
      const dayName = dayNames[date.getDay()];
      const fullDate = fmtDate(date);

      gridHeaders[i + 1].textContent = `${monthDay} (${dayName})`;
      gridHeaders[i + 1].dataset.date = fullDate;
      dayColumns[i].dataset.date = fullDate;
      dayColumns[i].innerHTML = '';
    }

    const { start, end } = getCurrentCalendarDates();
    loadReservations(PLACE_CD, start, end);
  }

  function openModal(date, presetStartMin = null) {
    let startMin = presetStartMin ?? DAY_START_MIN;
    startMin = clamp(roundToStep(startMin, STEP_MIN), DAY_START_MIN, DAY_END_MIN - STEP_MIN);
    let endMin = clamp(startMin + DEFAULT_DURATION_MIN, DAY_START_MIN + STEP_MIN, DAY_END_MIN);

    resDateInput.value = date;
    startTimeEl.value = minutesToHHmm(startMin);
    endTimeEl.value = minutesToHHmm(endMin);

    resEndDateInput.min = date;
    resEndDateInput.max = addDays(date, MAX_DAYS - 1);
    resEndDateInput.value = date;

    reservationModal.show();
  }

  function closeModal() {
    if (document.activeElement) {
      document.activeElement.blur();
    }
    reservationModal.hide();
  }

function hideModalAsync() {
  return new Promise((resolve) => {
    const handler = () => {
      modalElement.removeEventListener('hidden.bs.modal', handler);
      resolve();
    };
    modalElement.addEventListener('hidden.bs.modal', handler);
    reservationModal.hide();
  });
}



  // 6. Event Handlers
  prevWeekBtn.addEventListener('click', function() {
    const newStartDate = new Date(currentCalendarStartDate);
    newStartDate.setDate(newStartDate.getDate() - 7);
    renderCalendar(newStartDate);
  });

  nextWeekBtn.addEventListener('click', function() {
    const newStartDate = new Date(currentCalendarStartDate);
    newStartDate.setDate(newStartDate.getDate() + 7);
    renderCalendar(newStartDate);
  });

  calendarGrid.addEventListener('click', function (event) {
    const col = event.target.closest('.day-column');
    if (!col) return;

    const clickedReservationBlock = event.target.closest('.reservation-block');
    if (clickedReservationBlock) {
            if (clickedReservationBlock.classList.contains('my-reservation')) {
              const reserveId = clickedReservationBlock.dataset.reserveId;
              if (reserveId) {
                Swal.fire({
                  title: '내 예약 관리',
                  text: '예약을 수정하거나 취소하시겠습니까?',
                  icon: 'info',
                  showCancelButton: true,
                  showDenyButton: true, // Show "Modify" button
                  confirmButtonColor: '#d33', // Cancel button color
                  denyButtonColor: '#3085d6', // Modify button color
                  cancelButtonColor: '#6c757d', // Close button color
                  confirmButtonText: '예약 취소',
                  denyButtonText: '예약 수정',
                  cancelButtonText: '닫기'
                }).then(async (result) => {
                  if (result.isConfirmed) { // User clicked "예약 취소"
                    try {
                      const response = await fetch(`${C_PATH}/api/facilities/${PLACE_CD}/reservations/${reserveId}`, {
                        method: 'DELETE',
                        headers: { 'Content-Type': 'application/json' }
                      });
      
                      if (!response.ok) {
                        const errorText = await response.text();
                        throw new Error(`예약 취소 실패: ${errorText || response.status}`);
                      }
      
                      await Swal.fire('예약이 성공적으로 취소되었습니다.', '', 'success');
                      renderCalendar(currentCalendarStartDate); // Re-render calendar
                    } catch (error) {
                      console.error('Error cancelling reservation:', error);
                      Swal.fire(error.message || '예약 취소 중 오류가 발생했습니다.', '', 'error');
                    }
                              } else if (result.isDenied) { // User clicked "예약 수정"
                                try {
                                  const response = await fetch(`${C_PATH}/api/facilities/${PLACE_CD}/reservations/${reserveId}`, {
                                    method: 'GET',
                                    headers: { 'Content-Type': 'application/json' }
                                  });
                  
                                  if (!response.ok) {
                                    const errorText = await response.text();
                                    throw new Error(`예약 정보 조회 실패: ${errorText || response.status}`);
                                  }
                  
                                  const reservationDetails = await response.json();
                  
                                  // Populate modal with reservation details
                                  resDateInput.value = fmtDate(new Date(reservationDetails.startAt));
                                  resEndDateInput.value = fmtDate(new Date(reservationDetails.endAt));
                                  startTimeEl.value = minutesToHHmm(new Date(reservationDetails.startAt).getHours() * 60 + new Date(reservationDetails.startAt).getMinutes());
                                  endTimeEl.value = minutesToHHmm(new Date(reservationDetails.endAt).getHours() * 60 + new Date(reservationDetails.endAt).getMinutes());
                                  reasonInput.value = reservationDetails.reserveReason;
                                  headcountInput.value = reservationDetails.headcount;
                  
                                  currentReservationIdForModification = reserveId; // Set modification mode
                                  reservationModal.show(); // Open modal
                                } catch (error) {
                                  console.error('Error fetching reservation details for modification:', error);
                                  Swal.fire(error.message || '예약 정보를 불러오는 데 실패했습니다.', '', 'error');
                                }
                              }                });
              }
            } else {
        // Other user's reservation
        Swal.fire('다른 사용자가 예약한 시간입니다.', '', 'warning');
      }
      return; // Prevent opening the reservation modal
    }

    const date = col.dataset.date;
    if (!date) return;

    const rect = col.getBoundingClientRect();
    const y = event.clientY - rect.top;
    
    
//    const slotsFromStart = Math.max(0, Math.floor(y / GRID_SLOT_PX));
//    let startMin = DAY_START_MIN + (slotsFromStart * 60);
//    startMin = clamp(startMin, DAY_START_MIN, DAY_END_MIN - STEP_MIN);

// **[수정 부분]** y좌표를 분당 픽셀 값으로 나누어 정확한 분 단위를 계산합니다.
    let minutesFromStart = y / (GRID_SLOT_PX / 60);
    
    // **[수정 부분]** 캘린더 시작 시간(9:00)을 더합니다.
    let startMin = DAY_START_MIN + minutesFromStart;
    
    // **[수정 부분]** 계산된 startMin을 다시 STEP_MIN(60분) 단위로 반올림하고 운영 시간 내로 제한합니다.
    startMin = clamp(roundToStep(startMin, STEP_MIN), DAY_START_MIN, DAY_END_MIN - STEP_MIN);


    
    let endMin = startMin + DEFAULT_DURATION_MIN; // Assume default duration for new reservations

    // --- NEW: Check for overlaps with existing reservations --- 
    const clickedStartDateTime = new Date(parseDate(date));
    clickedStartDateTime.setHours(Math.floor(startMin / 60), startMin % 60, 0, 0);
    const clickedEndDateTime = new Date(parseDate(date));
    clickedEndDateTime.setHours(Math.floor(endMin / 60), endMin % 60, 0, 0);

    const hasOverlap = weeklyReservations.some(res => {
        const existingStart = new Date(res.startAt);
        const existingEnd = new Date(res.endAt);

        // Check for overlap: (startA < endB) && (endA > startB)
        return (clickedStartDateTime < existingEnd) && (clickedEndDateTime > existingStart);
    });

    if (hasOverlap) {
        Swal.fire('선택하신 시간에 이미 예약이 존재합니다.', '', 'warning');
        return;
    }
    // --- END NEW ---

    openModal(date, startMin);
  });

  reservationForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    const startDate = resDateInput.value;
    const endDate = resEndDateInput.value;
    const startTime = startTimeEl.value;
    const endTime = endTimeEl.value;
    const reason = reasonInput.value.trim();
    const headcount = Number(headcountInput.value);

    if (!startDate || !endDate || !startTime || !endTime || !reason || headcount < 1) {
        return Swal.fire('모든 필드를 올바르게 입력해주세요.', '', 'warning');
    }

// **[추가 부분]** 시간 문자열을 분 단위로 변환 및 유효성 검사
    const startMins = Number(startTime.split(':')[0]) * 60 + Number(startTime.split(':')[1]);
    const endMins = Number(endTime.split(':')[0]) * 60 + Number(endTime.split(':')[1]);

    if (endMins <= startMins) {
      return Swal.fire('종료 시간은 시작 시간보다 늦어야 합니다.', '', 'warning');
    }
    if (endMins > DAY_END_MIN) {
      // DAY_END_MIN은 18*60 = 1080분, 즉 18:00입니다.
      return Swal.fire('예약은 18:00 이전에 종료되어야 합니다.', '', 'warning');
    }
    // **[추가 부분 끝]**


    // --- 3. 서버 전송 --- 
    try {
      let url = `${C_PATH}/api/facilities/${PLACE_CD}/reservations`;
      let method = 'POST';

      if (currentReservationIdForModification) { // Modification mode
        url = `${C_PATH}/api/facilities/${PLACE_CD}/reservations/${currentReservationIdForModification}`;
        method = 'PUT';
      }

      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          reserveId: currentReservationIdForModification, // Include reserveId for modification
          placeCd: PLACE_CD,
          reserveReason: reason,
          startAt: `${startDate}T${startTime}:00`,
          endAt: `${endDate}T${endTime}:00`,
          headcount: headcount,
        })
      });

      if (!response.ok) {
        const errorText = await response.text();
        // 409 Conflict(중복예약)에러 처리
        if(response.status === 409){
			throw new Error(errorText);
		}
        throw new Error(`${currentReservationIdForModification ? '예약 수정' : '예약 등록'} 실패: ${errorText || response.status}`);
      }
	  await hideModalAsync();
	  
      await Swal.fire(`${currentReservationIdForModification ? '예약이 성공적으로 수정되었습니다.' : '예약이 성공적으로 등록되었습니다.'}`, '', 'success');
	  // ✅ [추가] 모달이 닫힌 후, 포커스를 명시적으로 목록 버튼으로 이동시킵니다.
      // 모달을 열기 전의 버튼이나, 페이지 내 주요 요소(목록 버튼)로 포커스를 돌리는 것이 좋습니다.
      const backButton = document.querySelector('.btn-back');
      if (backButton) {
          backButton.focus();
      }
//      closeModal();
      currentReservationIdForModification = null; // Reset modification mode
      renderCalendar(currentCalendarStartDate); // Re-render calendar
    } catch (error) {
      console.error(`${currentReservationIdForModification ? '예약 수정' : '예약 등록'} 중 오류 발생:`, error);
      Swal.fire(error.message || `${currentReservationIdForModification ? '예약 수정' : '예약 등록'} 중 오류가 발생했습니다.`, '', 'error');
    }
  });

  // Add event listener to close button to blur it on click
  const closeButton = modalElement.querySelector('.btn-close');
  if (closeButton) {
    closeButton.addEventListener('click', function() {
      closeButton.blur();
    });
  }

  /**
   * 시간 선택 드롭다운을 09:00부터 18:00까지 1시간 단위로 채웁니다.
   */
  function populateTimeSelects() {
    const startSelect = document.getElementById('start-time');
    const endSelect = document.getElementById('end-time');

    for (let i = DAY_START_MIN; i <= DAY_END_MIN; i += STEP_MIN) {
      const time = minutesToHHmm(i);
      const option = document.createElement('option');
      option.value = time;
      option.textContent = time;
      startSelect.appendChild(option);

      const endOption = document.createElement('option');
      endOption.value = time;
      endOption.textContent = time;
      endSelect.appendChild(endOption);
    }
  }

  // 7. Initialization
  function initialize() {
    populateTimeSelects(); // Call the new function
    const today = new Date();
    const dayOfWeek = today.getDay();
    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1));
    renderCalendar(startOfWeek);
  }

  initialize();
});