/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 10. 20.     	신혜진       강의 개설시 강의실 배정
 * 2025. 10. 20.        Gemini         JSP에서 주입된 데이터 사용 및 expectCap 처리 개선
 * 2025. 10. 20.        Gemini         강의실 선택 해제 및 배정 완료 로직 추가
 * 2025. 10. 20.        Gemini         placeCd/placeName 필드명 통일 및 건물 헤더 토글 기능 추가
 * 2025. 10. 20.        Gemini         AJAX 실패 시 버튼 상태 복원 로직 개선 및 데이터 파싱 강화
 * </pre>
 */

/**
 * 파일: assignmentDashboard.js
 * 설명: 강의실 배정 대시보드의 동적 렌더링 및 사용자 인터랙션 로직 처리
 * 의존성: Bootstrap 5.1.3, Font Awesome, JSP에서 설정된 window.lectureRoomsData, window.LECTURE_INFO, window.requiredSchedule
 */

document.addEventListener('DOMContentLoaded', () => {
    // ----------------------------------------------------------------------
    // 1. 전역 상수 및 초기 상태 정의
    // ----------------------------------------------------------------------

    const TIME_SLOTS = [
        { index: 1, time: '09:00', duration: '1교시 (09:00~10:00)' },
        { index: 2, time: '10:00', duration: '2교시 (10:00~11:00)' },
        { index: 3, time: '11:00', duration: '3교시 (11:00~12:00)' },
        { index: 4, time: '12:00', duration: '4교시 (12:00~13:00)' }, // 점심 시간
        { index: 5, time: '13:00', duration: '5교시 (13:00~14:00)' },
        { index: 6, time: '14:00', duration: '6교시 (14:00~15:00)' },
        { index: 7, time: '15:00', duration: '7교시 (15:00~16:00)' },
        { index: 8, time: '16:00', duration: '8교시 (16:00~17:00)' },
        { index: 9, time: '17:00', duration: '9교시 (17:00~18:00)' },
    ];
    const DAYS_OF_WEEK = { 1: '월', 2: '화', 3: '수', 4: '목', 5: '금' }; // DB 기준 (1: 월요일)
    const MAX_CAPACITY_THRESHOLD = 0.8; // 수용 인원 기준: 요청 인원의 80% 이상

    // 초기 상태
    let selectedRoom = {
        placeCd: null, // placeCd 사용
        roomName: null,
        daySlotPairs: [] // 예: [{dayOfWeek: 1, timeSlot: 2}, {dayOfWeek: 1, timeSlot: 3}, ...]
    };

    const $timetableContainer = document.getElementById('timetable-container');
    const $assignBtn = document.getElementById('assignBtn');

    // JSP에서 전달받은 데이터 (전역으로 설정됨)
    const LECTURE_DATA = window.lectureRoomsData || [];
    const LECTURE_INFO = window.LECTURE_INFO || {}; // expectCap이 숫자로 포함됨

    // **[개선]** LECTURE_INFO.expectCap을 확실하게 숫자로 파싱
    const EXPECT_CAP = parseInt(LECTURE_INFO.expectCap) || 0;

    // ----------------------------------------------------------------------
    // 2. 강의 시간표 설정 (JSP에서 주입된 실제 데이터 사용)
    // ----------------------------------------------------------------------
    // JSP에서 window.requiredSchedule로 설정되었습니다.
    // DTO 필드명에 맞게 조정 (RoomScheduleDetailDTO의 필드를 가정)
    selectedRoom.daySlotPairs = (window.requiredSchedule || []).map(item => ({
        // **[개선]** 혹시 모를 타입 불일치에 대비해 숫자로 확실하게 변환
        dayOfWeek: parseInt(item.dayOfWeek),
        timeSlot: parseInt(item.timeSlot)
    })).filter(p => !isNaN(p.dayOfWeek) && !isNaN(p.timeSlot)); // 유효한 데이터만 필터링

    if (selectedRoom.daySlotPairs.length === 0) {
        console.warn("경고: 요청된 강의 시간표 데이터(window.requiredSchedule)가 로드되지 않았습니다. 현재 배정 가능한 시간이 없습니다.");
        // 요청 시간이 없으면 배정 자체가 불가능하므로 버튼 비활성화 유지
    } else {
        // 요청 시간이 있을 경우, 초기에는 선택된 강의실이 없으므로 비활성화 유지
        $assignBtn.disabled = true;
    }


    // ----------------------------------------------------------------------
    // 3. 데이터 전처리 및 그룹화
    // ----------------------------------------------------------------------

    /**
     * 강의실 목록을 건물별로 그룹화합니다.
     * @param {Array} rooms - 전체 강의실 데이터
     * @returns {Object} 건물별로 그룹화된 객체
     */
    const groupRoomsByBuilding = (rooms) => {
        return rooms.reduce((acc, room) => {
            // placeCd에서 건물 코드를 추출하거나, 건물 코드를 직접 DTO에 추가해야 합니다.
            // 여기서는 placeCd의 두 번째 '-'까지를 건물 코드로 가정합니다. (RM-ENGI-HQ-0201 -> ENGI-HQ)
            const parts = room.placeCd.split('-');
            const building = parts.length > 2 ? `${parts[1]}-${parts[2]}` : '기타 건물';
            room.buildingName = building; // 임시 필드 추가

            if (!acc[building]) {
                acc[building] = [];
            }
            acc[building].push(room);
            return acc;
        }, {});
    };

    const groupedRooms = groupRoomsByBuilding(LECTURE_DATA);

    // ----------------------------------------------------------------------
    // 4. UI 렌더링 함수
    // ----------------------------------------------------------------------

    /**
     * 전체 시간표 컨테이너를 건물별로 렌더링합니다.
     */
    const renderTimetable = () => {
        if (LECTURE_DATA.length === 0) {
            $timetableContainer.innerHTML = `
                <div class="card p-5 text-center">
                    <i class="fas fa-exclamation-circle fa-3x text-warning mb-3"></i>
                    <p class="fs-5 fw-semibold text-dark">로드된 강의실 데이터가 없습니다.</p>
                    <p class="text-muted">데이터 소스를 확인해 주세요.</p>
                </div>
            `;
            return;
        }

        $timetableContainer.innerHTML = '';
        Object.keys(groupedRooms).forEach(buildingName => {
            const $groupContainer = createBuildingGroup(buildingName, groupedRooms[buildingName]);
            $timetableContainer.appendChild($groupContainer);
        });

        // 이벤트 리스너 부착
        $timetableContainer.querySelectorAll('.slot.available').forEach(slot => {
            slot.addEventListener('click', handleSlotClick);
        });

        // 건물 그룹 헤더의 Collapse 토글 이벤트 리스너
        $timetableContainer.querySelectorAll('.building-group-header').forEach($header => {
            $header.addEventListener('click', (e) => {
                 const icon = e.currentTarget.querySelector('.fas');
                 // Collapse show 클래스 토글 확인
                 setTimeout(() => {
                    const targetId = $header.getAttribute('data-bs-target');
                    const isExpanded = document.querySelector(targetId).classList.contains('show');
                    // 현재 상태에 따라 아이콘을 업데이트
                    icon.className = isExpanded ? 'fas fa-chevron-up' : 'fas fa-chevron-down';
                 }, 50); // 짧은 지연
            });
        });
    };

    /**
     * 건물 그룹 헤더 및 테이블 컨테이너를 생성합니다.
     */
    const createBuildingGroup = (buildingName, rooms) => {
        const $div = document.createElement('div');
        $div.className = 'timetable-group-container';

        // 건물명 포맷 변경 (RM-ENGI-HQ -> 공과대학 본관)
        let displayBuildingName = buildingName;
        if (buildingName.includes('ENGI-HQ')) {
            displayBuildingName = '공과대학 본관';
        } else if (buildingName.includes('HUM-HQ')) {
            displayBuildingName = '인문대학 본관';
        } else if (buildingName.includes('ENGI-LA')) {
            displayBuildingName = '공과대학 A동';
        }


        // 건물 그룹 헤더
        const $header = document.createElement('div');
        $header.className = 'building-group-header d-flex justify-content-between align-items-center';
        $header.innerHTML = `
            <span><i class="fas fa-building me-2"></i>${displayBuildingName} (${rooms.length}개 강의실)</span>
            <i class="fas fa-chevron-up"></i>
        `;
        const collapseId = `collapse-${buildingName.replace(/[^a-zA-Z0-9]/g, '-')}`;
        $header.setAttribute('data-bs-toggle', 'collapse');
        $header.setAttribute('data-bs-target', `#${collapseId}`);
        $header.setAttribute('aria-expanded', 'true');
        $div.appendChild($header);

        // 시간표 테이블 (Collapse 영역)
        const $collapseDiv = document.createElement('div');
        $collapseDiv.id = collapseId;
        $collapseDiv.className = 'collapse show'; // 기본적으로 펼쳐서 보여줌

        const $tableWrapper = document.createElement('div');
        $tableWrapper.className = 'timetable-wrapper';
        $tableWrapper.appendChild(createTimetableTable(rooms));
        $collapseDiv.appendChild($tableWrapper);

        $div.appendChild($collapseDiv);
        return $div;
    };


    /**
     * 특정 건물에 속한 강의실들의 시간표 테이블을 생성합니다.
     */
    const createTimetableTable = (rooms) => {
        const $table = document.createElement('table');
        $table.className = 'table table-bordered timetable align-middle mb-0';

        // 1. 테이블 헤더 (요일 및 시간)
        const $thead = $table.createTHead();
        let headerHtml = '<tr><th class="time-col" rowspan="2">강의실 정보</th>';

        // 요일 헤더 (월~금)
        for (let i = 1; i <= 5; i++) {
            headerHtml += `<th colspan="${TIME_SLOTS.length}" class="text-center">${DAYS_OF_WEEK[i]}요일</th>`;
        }
        headerHtml += '</tr><tr>';

        // 시간 헤더 (09:00~18:00)
        for (let i = 0; i < 5; i++) { // 5일
            TIME_SLOTS.forEach(slot => {
                headerHtml += `<th>${slot.time}</th>`;
            });
        }
        headerHtml += '</tr>';
        $thead.innerHTML = headerHtml;

        // 2. 테이블 본문 (강의실별 시간표)
        const $tbody = $table.createTBody();

        rooms.forEach(room => {
            const $row = $tbody.insertRow();
            $row.setAttribute('data-place-cd', room.placeCd); // placeCd 사용

            // **[개선]** room.capacity를 숫자로 파싱
            const roomCapacity = parseInt(room.capacity) || 0;

            // 첫 번째 셀: 강의실 정보
            const $infoCell = $row.insertCell();
            $infoCell.className = 'room-header text-start bg-light';
            // **[개선]** capacity가 0이면 'N/A' 대신 '정보 없음' 또는 '0' 표시가 더 정확함.
            $infoCell.innerHTML = `
                <div class="fw-bold fs-6 text-dark">${room.placeName}</div>
                <div class="small text-muted">정원: ${roomCapacity > 0 ? roomCapacity : '정보 없음'}명</div>
                ${room.hasVideo ? '<i class="fas fa-video text-danger" title="화상 강의 장비 보유"></i>' : '<i class="fas fa-desktop text-success" title="일반 강의실"></i>'}
            `;

            // 강의실 정원 충족 여부 표시
            const capRatio = roomCapacity > 0 ? EXPECT_CAP / roomCapacity : Infinity;

            let capColor = 'bg-success';
            let capText = '충족';
            if (roomCapacity === 0) {
                capColor = 'bg-secondary';
                capText = '정원 미정';
            } else if (capRatio > 1) {
                capColor = 'bg-danger';
                capText = '정원 초과!';
            } else if (capRatio > MAX_CAPACITY_THRESHOLD) {
                capColor = 'bg-warning';
                capText = '인원 근접';
            }

            $infoCell.insertAdjacentHTML('beforeend', `
                <div class="small mt-1">
                    <span class="badge ${capColor} text-white">${capText}</span>
                </div>
            `);

            // 시간표 셀
            for (let dayIndex = 1; dayIndex <= 5; dayIndex++) {
                TIME_SLOTS.forEach(slot => {
                    const $slotCell = $row.insertCell();
                    $slotCell.className = 'slot';
                    $slotCell.dataset.placeCd = room.placeCd; // placeCd 사용
                    $slotCell.dataset.day = dayIndex;
                    $slotCell.dataset.slot = slot.index;

                    // 강의실 스케줄 데이터가 없는 경우를 대비해 안전하게 접근
                    const daySchedule = room.schedule ? room.schedule[dayIndex] : null;
                    const isBooked = daySchedule ? daySchedule[slot.index] : false;

                    // daySlotPairs의 객체 구조를 JSP에서 넘어온 형식으로 맞춤
                    const isRequired = selectedRoom.daySlotPairs.some(p => p.dayOfWeek === dayIndex && p.timeSlot === slot.index);
                    const isCapacityOk = capRatio <= 1; // 정원 초과 여부

                    if (isBooked) {
                        // 이미 예약됨
                        $slotCell.classList.add('booked');
                        $slotCell.innerHTML = `<span class="small text-truncate d-block">${isBooked}</span>`; // 강의명 표시
                    } else if (isRequired) {
                        // 요청된 시간대
                        $slotCell.classList.add('requested-time');
                        if (isCapacityOk) {
                             $slotCell.classList.add('available'); // 요청된 시간 중 사용 가능
                             $slotCell.innerHTML = `<i class="fas fa-plus text-primary"></i>`; // 선택 가능 표시
                        } else {
                            $slotCell.classList.add('not-suitable'); // 정원 초과로 선택 불가
                            $slotCell.innerHTML = `<i class="fas fa-times text-danger"></i>`;
                            $slotCell.title = "요청 인원 초과";
                        }
                    } else {
                        // 기타 시간대 (클릭 방지)
                        $slotCell.title = "현재 강의의 요청 시간대가 아닙니다.";
                    }
                });
            }
        });

        return $table;
    };

    // ----------------------------------------------------------------------
    // 5. 인터랙션 및 상태 관리 함수
    // ----------------------------------------------------------------------

    /**
     * 특정 강의실이 요청된 모든 시간대에 사용 가능한지 확인합니다.
     * @param {string} placeCd - 확인할 강의실 ID (placeCd)
     * @returns {boolean} 모든 요청 시간이 사용 가능하면 true
     */
    const checkRoomAvailability = (placeCd) => {
        const room = LECTURE_DATA.find(r => r.placeCd === placeCd);
        if (!room) return false;

        // **[개선]** roomCapacity를 숫자로 파싱
        const roomCapacity = parseInt(room.capacity) || 0;

        // 정원 초과 확인 (100% 초과)
        if (roomCapacity < EXPECT_CAP) {
            console.warn(`[${room.placeName}] 정원(${roomCapacity})이 요청 인원(${EXPECT_CAP})보다 적습니다.`);
            return false;
        }

        // 시간표 충돌 확인
        for (const req of selectedRoom.daySlotPairs) {
            const daySchedule = room.schedule ? room.schedule[req.dayOfWeek] : null;
            if (daySchedule && daySchedule[req.timeSlot]) {
                // 이미 예약된 시간이 하나라도 있으면 false 반환
                console.log(`[${room.placeName}] ${DAYS_OF_WEEK[req.dayOfWeek]} ${TIME_SLOTS.find(t=>t.index===req.timeSlot)?.time} 에 예약 충돌 발생.`);
                return false;
            }
        }
        return true; // 모든 조건 충족
    };

    /**
     * 강의실 선택 및 UI 업데이트를 처리합니다.
     */
    const handleSlotClick = (e) => {
        const $clickedSlot = e.currentTarget;
        const placeCd = $clickedSlot.dataset.placeCd;
        const room = LECTURE_DATA.find(r => r.placeCd === placeCd);

        // 이미 선택된 강의실을 다시 클릭하면 선택 해제
        if (selectedRoom.placeCd === placeCd) {
            deselectRoom();
            return;
        }

        // 1. 기존 선택 해제
        deselectRoom();

        // 2. 가용성 확인 (UI에서는 이미 확인되었지만, 안전을 위해 다시 확인)
        if (!checkRoomAvailability(placeCd)) {
             console.error(`선택 불가: 강의실 ${room.placeName}는 요청 시간대에 모두 사용 가능하지 않거나 정원이 부족합니다.`);
             return;
        }

        // 3. 새 강의실 선택 및 UI 업데이트
        selectedRoom.placeCd = placeCd;
        selectedRoom.roomName = room.placeName;

        // 해당 강의실의 모든 요청 시간대 슬롯을 선택 상태로 변경
        const $allSlots = $timetableContainer.querySelectorAll(`[data-place-cd="${placeCd}"]`);
        $allSlots.forEach($slot => {
            const day = parseInt($slot.dataset.day);
            const slot = parseInt($slot.dataset.slot);

            if (selectedRoom.daySlotPairs.some(p => p.dayOfWeek === day && p.timeSlot === slot)) {
                $slot.classList.add('selected');
                $slot.classList.remove('available', 'requested-time');
                $slot.innerHTML = `<i class="fas fa-check"></i>`;
            }
        });

        $assignBtn.disabled = false;
        console.log(`강의실 선택 완료: ${room.placeName} (ID: ${placeCd})`);
    };

    /**
     * 현재 선택된 강의실을 해제하고 UI를 초기화합니다.
     */
    const deselectRoom = () => {
        if (selectedRoom.placeCd) {
            const prevPlaceCd = selectedRoom.placeCd; // 해제할 강의실 ID

            // 해제할 강의실의 모든 슬롯을 선택 해제
            const $prevSelectedSlots = $timetableContainer.querySelectorAll(`[data-place-cd="${prevPlaceCd}"]`);

            $prevSelectedSlots.forEach($slot => {
                const day = parseInt($slot.dataset.day);
                const slot = parseInt($slot.dataset.slot);

                // 선택 상태만 제거
                $slot.classList.remove('selected');

                // 요청 시간대 슬롯에 대해서만 원래 스타일 복원
                if (selectedRoom.daySlotPairs.some(p => p.dayOfWeek === day && p.timeSlot === slot)) {
                     // checkRoomAvailability를 다시 호출하여 가용 상태를 판단하는 것은 비효율적이므로,
                     // 이전에 available 상태였을 것이라는 전제 하에 복원 로직을 단순화합니다.
                     // (not-suitable은 애초에 selected 상태가 될 수 없음)
                     $slot.classList.add('requested-time', 'available');
                     $slot.innerHTML = `<i class="fas fa-plus text-primary"></i>`;
                }
            });

            selectedRoom.placeCd = null;
            selectedRoom.roomName = null;
            $assignBtn.disabled = true;
            console.log("강의실 선택이 해제되었습니다.");
        }
    };

    /**
     * 강의실 배정 정보를 서버에 임시 저장합니다.
     */
    const handleAssignClick = async () => {
        if (!selectedRoom.placeCd) {
            alert("먼저 강의실을 선택해 주세요.");
            return;
        }

        const originalBtnHtml = $assignBtn.innerHTML;
        $assignBtn.disabled = true;
        $assignBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>처리 중...';

        // 서버에 보낼 시간표 정보 문자열 생성
        // 형식: "1_2,1_3,2_5" (요일_교시)
        const timeblockCdsString = selectedRoom.daySlotPairs
            .map(p => `${p.dayOfWeek}_${p.timeSlot}`)
            .join(',');

        const requestData = {
            lctApplyId: LECTURE_INFO.lctApplyId,
            placeCd: selectedRoom.placeCd,
            timeblockCdsString: timeblockCdsString
        };

        console.log("배정 요청 데이터:", requestData);
        let success = false; // 성공 여부 플래그

        try {
            const response = await fetch('/lms/staff/classroom/assignment/saveAssignment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            const result = await response.json();

            if (response.ok && result.status === 'SUCCESS') {
                success = true;
                // 성공 모달 띄우기
                document.getElementById('assignedRoomName').textContent = selectedRoom.roomName;
                const modal = new bootstrap.Modal(document.getElementById('approvalSuccessModal'));
                modal.show();
                console.log("배정 임시 저장 성공:", result.message);

                // 모달의 확인 버튼 클릭 시 window.location.reload()가 실행됨
            } else {
                alert(`배정 실패: ${result.message || '알 수 없는 오류'}`);
                console.error("배정 임시 저장 실패:", result);
            }
        } catch (error) {
            alert(`서버 통신 오류가 발생했습니다: ${error.message}`);
            console.error("강의실 배정 AJAX 오류:", error);
        } finally {
            if (!success) {
                // 실패했거나 예외가 발생한 경우 버튼 상태 복원
                $assignBtn.innerHTML = originalBtnHtml;
                $assignBtn.disabled = false;
            }
            // 성공한 경우 모달에서 리로드하므로 버튼 상태 복원 불필요
        }
    };

    // ----------------------------------------------------------------------
    // 6. 초기화
    // ----------------------------------------------------------------------

    // 배정 버튼 이벤트 리스너 부착
    $assignBtn.addEventListener('click', handleAssignClick);

    // UI 초기 렌더링
    renderTimetable();
});