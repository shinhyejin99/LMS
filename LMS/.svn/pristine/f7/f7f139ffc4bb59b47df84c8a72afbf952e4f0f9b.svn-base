/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 30.     	송태호            최초 생성
 *
 * </pre>
 */

// 전역 상태 관리
let selectedLecture = null;
let selectedLectureData = null;
let selectedBuilding = null;
let selectedClassroom = null;
let selectedClassroomName = null;
let selectedTimeSlots = [];
let timetableModalInstance = null;
let allLectures = [];
let currentYeartermCd = null;

// 페이지 로드 시 강의 목록 가져오기
document.addEventListener('DOMContentLoaded', function() {
    checkYeartermAndLoad();
    initializeFilterListeners();
    initializeModalAssignButton(); 
    // 기본 필터 적용
    applyDefaultFilter();
});

// 학기 확인 및 데이터 로드
function checkYeartermAndLoad() {
    fetch('/lms/api/v1/common/lecture/apply/yearterm')
        .then(response => response.json())
        .then(data => {
            if (!data || !data.yeartermCd) {
                Swal.fire({
                    icon: 'warning',
                    title: '강의개설 기간 아님',
                    text: '현재 강의개설 기간이 아닙니다.',
                    confirmButtonText: '확인'
                }).then(() => {
                    window.location.href = '/lms/staff';
                });
                return;
            }
            currentYeartermCd = data.yeartermCd;
            loadLectures();
            loadBuildings();
        })
        .catch(error => {
            console.error('Error checking yearterm:', error);
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '현재 강의개설 기간이 아닙니다.',
                confirmButtonText: '확인'
            }).then(() => {
                window.location.href = '/lms/staff';
            });
        });
}

// 강의 목록 로드
function loadLectures(preserveFilters = false) {
    // 현재 필터 상태 저장
    const currentDepartment = preserveFilters ? document.getElementById('departmentFilter')?.value : '';
    const currentCompletion = preserveFilters ? document.getElementById('completionFilter')?.value : '';
    const currentStatus = preserveFilters ? document.getElementById('statusFilter')?.value : '';

    fetch(`/lms/api/v1/staff/lecture/schedule/unassigned/${currentYeartermCd}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Unassigned lecture request failed: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const lectureList = Array.isArray(data) ? data : [];
            if (!Array.isArray(data)) {
                console.warn('Unexpected lecture list payload:', data);
            }
            allLectures = lectureList;
            populateFilters(lectureList);
            renderLectures(lectureList);

            // 필터 상태 복원
            if (preserveFilters) {
                if (currentDepartment) document.getElementById('departmentFilter').value = currentDepartment;
                if (currentCompletion) document.getElementById('completionFilter').value = currentCompletion;
                if (currentStatus) document.getElementById('statusFilter').value = currentStatus;
                filterLectures();
            }
        })
        .catch(error => {
            console.error('Error loading lectures:', error);
            document.getElementById('lectureList').innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>강의 목록을 불러오는데 실패했습니다.</p>
                </div>
            `;
        });
}

// 필터 옵션 동적 생성
function populateFilters(lectures) {
    // 현재 선택값 저장
    const currentDept = document.getElementById('departmentFilter')?.value;
    const currentCompletion = document.getElementById('completionFilter')?.value;

    // 학과 추출
    const departments = [...new Set(lectures.map(l => l.univDeptName).filter(Boolean))].sort();
    const departmentFilter = document.getElementById('departmentFilter');
    departmentFilter.innerHTML = '<option value="">학과</option>';
    departments.forEach(dept => {
        const selected = dept === currentDept ? ' selected' : '';
        departmentFilter.innerHTML += `<option value="${dept}"${selected}>${dept}</option>`;
    });

    // 이수구분 추출
    const completions = [...new Set(lectures.map(l => l.completionName).filter(Boolean))].sort();
    const completionFilter = document.getElementById('completionFilter');
    completionFilter.innerHTML = '<option value="">이수구분</option>';
    completions.forEach(completion => {
        const selected = completion === currentCompletion ? ' selected' : '';
        completionFilter.innerHTML += `<option value="${completion}"${selected}>${completion}</option>`;
    });
}

// 강의 목록 렌더링
function renderLectures(lectures) {
    const container = document.getElementById("lectureList");

    if (!lectures || lectures.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-check-circle"></i>
                <p>배정 대기 중인 강의가 없습니다.</p>
            </div>
        `;
        return;
    }

    let html = "";
    lectures.forEach(lecture => {
        const remainingBlocks = (lecture.hour * 2) - lecture.scheduledSlots;
        const status = remainingBlocks === 0 ? 'COMPLETE' : remainingBlocks < 0 ? 'EXCEED' : 'NEED';
        const statusClass = status === 'COMPLETE' ? 'complete' : status === 'EXCEED' ? 'exceed' : 'need';
        const diffHours = Math.abs(remainingBlocks) / 2;
        const formattedDiff = Number.isFinite(diffHours)
            ? (Number.isInteger(diffHours) ? diffHours : diffHours.toFixed(1))
            : 0;
        const badgeText = status === 'COMPLETE'
            ? '배정완료'
            : status === 'EXCEED'
                ? `${formattedDiff}시수 초과`
                : `${formattedDiff}시수 부족`;

        html += `
            <div class="lecture-item"
                 data-lecture-id="${lecture.lectureId}"
                 data-lecture-json='${JSON.stringify(lecture).replace(/'/g, "&apos;")}'
                 data-status="${status}">
                <div class="lecture-main">
                    <div class="lecture-title">
                        <span class="lecture-name">${lecture.subjectName}</span>
                        <span class="lecture-dept">${lecture.univDeptName}</span>
                    </div>
                    <div class="lecture-info-row">
                        <span class="lecture-professor">담당교수: ${lecture.professorName}</span>
                        <span class="lecture-detail">정원: ${lecture.maxCap}명</span>
                        <span class="lecture-detail">${lecture.credit}학점/${lecture.hour}시간</span>
                        <span class="lecture-detail">${lecture.completionName}</span>
                        <span class="lecture-detail">${lecture.subjectTypeName}</span>
                    </div>
                </div>
                <div class="lecture-actions">
                    <span class="lecture-hours ${statusClass}">
                        ${badgeText}
                    </span>
                    <button type="button" class="btn btn-sm btn-outline-primary lecture-detail-btn">상세보기</button>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;

    // 렌더링 후 이벤트 리스너 등록
    attachLectureItemListeners();
}

// 강의 아이템 이벤트 리스너 등록
function attachLectureItemListeners() {
    document.querySelectorAll(".lecture-item").forEach(item => {
        const lectureId = item.dataset.lectureId;
        const lectureJson = item.dataset.lectureJson || "{}";
        let parsedLectureData = {};
        try {
            parsedLectureData = JSON.parse(lectureJson);
        } catch (error) {
            console.warn("Failed to parse lecture JSON:", error);
            parsedLectureData = {};
        }

        item.addEventListener("click", function () {
            document.querySelectorAll(".lecture-item").forEach(i => i.classList.remove("selected"));
            item.classList.add("selected");
            selectedLecture = lectureId;
            selectedLectureData = parsedLectureData;
        });

        item.addEventListener("dblclick", function () {
            selectedLecture = lectureId;
            selectedLectureData = parsedLectureData;
            showLectureDetails(lectureId, parsedLectureData);
        });

        const detailButton = item.querySelector(".lecture-detail-btn");
        if (detailButton) {
            detailButton.addEventListener("click", function (event) {
                event.preventDefault();
                event.stopPropagation();
                document.querySelectorAll(".lecture-item").forEach(i => i.classList.remove("selected"));
                item.classList.add("selected");
                selectedLecture = lectureId;
                selectedLectureData = parsedLectureData;
                showLectureDetails(lectureId, parsedLectureData);
            });
        }
    });
}

function showLectureDetails(lectureId, fallbackData) {
    setLectureDetailLoading(lectureId, fallbackData);

    const modalEl = document.getElementById("lectureApplicationModal");
    const modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
    modalInstance.show();

    const summaryUrl = `/classroom/api/v1/common/${encodeURIComponent(lectureId)}`;
    fetch(summaryUrl, { credentials: "include", headers: { Accept: "application/json" } })
        .then(response => {
            if (!response.ok) {
                throw new Error("Lecture summary request failed");
            }
            return response.json();
        })
        .then(data => {
            if (!data || typeof data !== 'object') {
                throw new Error('Lecture summary payload malformed');
            }
            updateLectureDetailModal(data);
            if (data.lectureId === lectureId) {
                selectedLectureData = data;
            }
        })
        .catch(error => {
            console.error("Error loading lecture summary:", error);
            if (fallbackData && typeof fallbackData === 'object') {
                updateLectureDetailModal(fallbackData);
            }
        });

    const planUrl = `/classroom/api/v1/common/${encodeURIComponent(lectureId)}/plan`;
    fetch(planUrl, { credentials: "include", headers: { Accept: "application/json" } })
        .then(response => {
            if (!response.ok) {
                throw new Error("Lecture plan request failed");
            }
            return response.json();
        })
        .then(plan => {
            if (!Array.isArray(plan)) {
                console.warn('Unexpected lecture plan payload:', plan);
            }
            renderLecturePlan(Array.isArray(plan) ? plan : []);
        })
        .catch(error => {
            console.error("Error loading lecture plan:", error);
            renderLecturePlanError();
        });
}

function setLectureDetailLoading(lectureId, fallbackData) {
    const codeEl = document.getElementById("modalLectureCode");
    const nameEl = document.getElementById("modalLectureName");
    const professorEl = document.getElementById("modalProfessor");
    const capacityEl = document.getElementById("modalCapacity");
    const buildingEl = document.getElementById("modalPreferredBuilding");
    const timeEl = document.getElementById("modalPreferredTime");

    if (codeEl) codeEl.textContent = lectureId || "-";
    if (nameEl) nameEl.textContent = "불러오는 중...";
    if (professorEl) professorEl.textContent = "-";
    if (capacityEl) capacityEl.textContent = "-";
    if (buildingEl) buildingEl.textContent = "불러오는 중...";
    if (timeEl) timeEl.textContent = "불러오는 중...";

    renderLecturePlanLoading();

    if (fallbackData && typeof fallbackData === "object") {
        updateLectureDetailModal(fallbackData);
    }
}

function updateLectureDetailModal(lecture) {
    if (!lecture || typeof lecture !== "object") return;

    const codeEl = document.getElementById("modalLectureCode");
    const nameEl = document.getElementById("modalLectureName");
    const professorEl = document.getElementById("modalProfessor");
    const capacityEl = document.getElementById("modalCapacity");
    const buildingEl = document.getElementById("modalPreferredBuilding");
    const timeEl = document.getElementById("modalPreferredTime");

    if (codeEl) codeEl.textContent = lecture.lectureId || "-";
    if (nameEl) nameEl.textContent = lecture.subjectName || "-";
    if (professorEl) professorEl.textContent = lecture.professorName || "-";

    if (capacityEl) {
        const current = Number.isFinite(Number(lecture.currentCap)) ? Number(lecture.currentCap) : null;
        const max = Number.isFinite(Number(lecture.maxCap)) ? Number(lecture.maxCap) : null;
        if (current !== null && max !== null) {
            capacityEl.textContent = `${current}명 / ${max}명`;
        } else if (max !== null) {
            capacityEl.textContent = `${max}명`;
        } else {
            capacityEl.textContent = "-";
        }
    }

    const schedules = parseScheduleJson(lecture.scheduleJson);
    const placeNames = extractPlaceNames(schedules);
    if (buildingEl) {
        if (placeNames.length > 0) {
            buildingEl.textContent = placeNames.join(", ");
        } else if (lecture.univDeptName) {
            buildingEl.textContent = lecture.univDeptName;
        } else {
            buildingEl.textContent = "미정";
        }
    }

    if (timeEl) {
        const timeInfo = formatScheduleInfo(schedules);
        timeEl.textContent = timeInfo || "미정";
    }
}

let currentPlanList = [];

function renderLecturePlanLoading() {
    const container = document.getElementById("modalLecturePlan");
    const selectEl = document.getElementById("modalWeekSelect");
    if (!container) return;

    container.innerHTML = '<div class="lecture-plan-empty">주차별 계획을 불러오는 중입니다...</div>';
    if (selectEl) {
        selectEl.style.display = 'none';
    }
}

function renderLecturePlan(planList) {
    const container = document.getElementById("modalLecturePlan");
    const selectEl = document.getElementById("modalWeekSelect");
    if (!container) return;

    if (!Array.isArray(planList) || planList.length === 0) {
        container.innerHTML = '<div class="lecture-plan-empty">등록된 주차별 계획이 없습니다.</div>';
        if (selectEl) {
            selectEl.style.display = 'none';
        }
        return;
    }

    currentPlanList = planList;

    // 셀렉트 박스 채우기
    if (selectEl) {
        selectEl.innerHTML = '<option value="">주차를 선택하세요</option>';
        planList.forEach(plan => {
            const week = plan.lectureWeek != null ? plan.lectureWeek : '';
            const goal = plan.weekGoal || '주차 계획';
            const option = document.createElement('option');
            option.value = week;
            option.textContent = week ? `${week}주차 - ${goal}` : goal;
            selectEl.appendChild(option);
        });
        selectEl.style.display = 'block';

        // 이벤트 리스너 등록
        selectEl.onchange = function() {
            const selectedWeek = this.value;
            if (selectedWeek === '') {
                container.innerHTML = '<div class="lecture-plan-empty">주차를 선택하세요.</div>';
            } else {
                const selectedPlan = planList.find(p => String(p.lectureWeek) === selectedWeek);
                if (selectedPlan) {
                    renderSinglePlan(selectedPlan);
                }
            }
        };
    }

    container.innerHTML = '<div class="lecture-plan-empty">주차를 선택하세요.</div>';
}

function renderSinglePlan(plan) {
    const container = document.getElementById("modalLecturePlan");
    if (!container) return;

    const week = plan.lectureWeek != null ? `${plan.lectureWeek}주차` : "주차 미정";
    const goal = plan.weekGoal || "";
    const desc = plan.weekDesc || "";

    container.innerHTML = `
        <div class="lecture-plan-item" style="background: #f8f9fa; border-radius: 6px; padding: 15px;">
            <div class="lecture-plan-week" style="font-weight: 600; font-size: 14px; color: #343a40; margin-bottom: 8px;">
                ${week}${goal ? ` · ${goal}` : ''}
            </div>
            <p class="lecture-plan-desc" style="margin: 0; font-size: 13px; color: #495057; line-height: 1.6; white-space: pre-line;">
                ${desc}
            </p>
        </div>
    `;
}

function renderLecturePlanError() {
    const container = document.getElementById("modalLecturePlan");
    const selectEl = document.getElementById("modalWeekSelect");
    if (!container) return;

    container.innerHTML = '<div class="lecture-plan-empty">주차별 계획을 불러오지 못했습니다.</div>';
    if (selectEl) {
        selectEl.style.display = 'none';
    }
}

function parseScheduleJson(scheduleJson) {
    if (!scheduleJson) return [];
    if (Array.isArray(scheduleJson)) return scheduleJson;
    try {
        return JSON.parse(scheduleJson);
    } catch (error) {
        console.error("Failed to parse scheduleJson:", error);
        return [];
    }
}

function extractPlaceNames(schedules) {
    if (!Array.isArray(schedules)) return [];
    const names = schedules
        .map(schedule => schedule.placeName)
        .filter(name => typeof name === "string" && name.trim().length > 0);
    return [...new Set(names)];
}

function formatScheduleInfo(schedules) {
    if (!Array.isArray(schedules) || schedules.length === 0) {
        return "";
    }

    const segments = [];
    schedules.forEach(schedule => {
        const slots = Array.isArray(schedule.slots) ? schedule.slots : [];
        if (slots.length === 0) {
            return;
        }
        const slotText = slots
            .map(slot => `${getDayName(slot.day)} ${formatTime(slot.start)}-${formatTime(slot.end)}`)
            .join(", ");
        segments.push(slotText);
    });

    return segments.join(" / ");
}

function getDayName(day) {
    const days = {
        MO: "월",
        TU: "화",
        WE: "수",
        TH: "목",
        FR: "금",
        SA: "토",
        SU: "일"
    };
    return days[day] || day;
}

function formatTime(time) {
    if (!time || typeof time !== "string" || time.length < 4) {
        return "-";
    }
    return `${time.substring(0, 2)}:${time.substring(2, 4)}`;
}

// 건물 목록 로드
function loadBuildings() {
    fetch(`/lms/api/v1/staff/lecture/schedule/available-classroom/${currentYeartermCd}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Available classroom request failed: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) {
                console.warn('Unexpected classroom list payload:', data);
            }
            renderBuildings(Array.isArray(data) ? data : []);
        })
        .catch(error => {
            console.error('Error loading buildings:', error);
            document.getElementById('buildingList').innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>건물 목록을 불러오는데 실패했습니다.</p>
                </div>
            `;
        });
}

// 건물 목록 렌더링
function renderBuildings(buildings) {
    const container = document.getElementById('buildingList');

    if (buildings.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-building"></i>
                <p>사용 가능한 건물이 없습니다.</p>
            </div>
        `;
        return;
    }

    let html = '';
    buildings.forEach(building => {
        html += `
            <div class="building-item" data-building-code="${building.placeCd}" data-building-data='${JSON.stringify(building).replace(/'/g, "&apos;")}'>
                <div class="building-name">${building.placeName}</div>
            </div>
        `;
    });
    container.innerHTML = html;

    // 이벤트 리스너 등록
    initializeBuildingListeners();
}

// 건물 선택 리스너 초기화
function initializeBuildingListeners() {
    document.querySelectorAll('.building-item').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('.building-item').forEach(i => i.classList.remove('selected'));
            this.classList.add('selected');
            selectedBuilding = this.dataset.buildingCode;
            const buildingData = JSON.parse(this.dataset.buildingData);
            loadClassrooms(buildingData.classrooms);
        });
    });
}

// 강의실 목록 로드
function loadClassrooms(classrooms) {
    const container = document.getElementById('classroomListContainer');

    if (!classrooms || classrooms.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-door-closed"></i>
                <p>사용 가능한 강의실이 없습니다.</p>
            </div>
        `;
        return;
    }

    let html = '<div class="classroom-list">';
    classrooms.forEach(room => {
        const usageClass = room.usagePercent >= 80 ? 'high' : room.usagePercent >= 50 ? 'medium' : 'low';
        const buildingName = selectedBuilding ? (document.querySelector(`.building-item[data-building-code="${selectedBuilding}"]`)?.querySelector('.building-name')?.textContent || '') : '';
        const fullName = buildingName ? `${buildingName} ${room.placeName}` : room.placeName;
        html += `
            <div class="classroom-item" data-classroom-code="${room.placeCd}" data-classroom-name="${room.placeName}" data-classroom-full-name="${fullName}">
                <div class="classroom-name">${room.placeName}</div>
                <div class="classroom-capacity">수용인원: ${room.capacity}명</div>
                <div class="classroom-usage">사용률: ${room.usagePercent.toFixed(1)}%</div>
                <div class="usage-bar">
                    <div class="usage-bar-fill ${usageClass}" style="width: ${room.usagePercent}%"></div>
                </div>
            </div>
        `;
    });
    html += '</div>';
    container.innerHTML = html;

    container.querySelectorAll('.classroom-item').forEach(item => {
        item.addEventListener('click', function() {
            if (!selectedLecture) {
                Swal.fire({
                    icon: 'warning',
                    title: '강의 미선택',
                    text: '강의를 먼저 선택해주세요.',
                    confirmButtonText: '확인'
                });
                return;
            }
            selectedClassroom = this.dataset.classroomCode;
            selectedClassroomName = this.dataset.classroomFullName || this.dataset.classroomName;
            openTimetableModal();
        });
    });
}

// 시간표 모달 열기
function openTimetableModal() {
    const lectureNameForModal = selectedLectureData && selectedLectureData.subjectName ? selectedLectureData.subjectName : '-';
    document.getElementById('modalSelectedLecture').textContent = lectureNameForModal;
    document.getElementById('modalSelectedClassroom').textContent = selectedClassroomName || '-';
    selectedTimeSlots = [];
    document.getElementById('selectedTimeSlotsDisplay').innerHTML = '<em style="color: #6c757d; font-size: 13px;">시간 블록을 선택해주세요.</em>';
    updateHourProgress();
    updateModalAssignButton();
    loadTimetableInModal(selectedClassroom);

    const modalEl = document.getElementById('timetableModal');
    if (!timetableModalInstance) {
        timetableModalInstance = new bootstrap.Modal(modalEl);
    }
    timetableModalInstance.show();
}

// 시수 진행 상황 업데이트
function updateHourProgress() {
    const progressEl = document.getElementById('modalHourProgress');
    if (!progressEl) return;

    const targetHours = selectedLectureData && selectedLectureData.hour ? selectedLectureData.hour : 0;
    const currentHours = selectedTimeSlots.length * 0.5;

    // 색상 결정: 부족=빨강, 딱 맞음=연두, 초과=주황
    let color = '#dc3545'; // 빨강 (부족)
    if (currentHours === targetHours) {
        color = '#28a745'; // 연두 (딱 맞음)
    } else if (currentHours > targetHours) {
        color = '#fd7e14'; // 주황 (초과)
    }

    progressEl.innerHTML = `<span style="color: ${color};">${currentHours}</span> / ${targetHours}`;
}

// 시간표 렌더링 상수
const DAYS = ['월', '화', '수', '목', '금'];
const DAY_CODES = { '월': 'MO', '화': 'TU', '수': 'WE', '목': 'TH', '금': 'FR' };
const TIME_SLOTS = [];
for (let hour = 9; hour < 19; hour++) {
    TIME_SLOTS.push(String(hour).padStart(2, '0') + '00');
    TIME_SLOTS.push(String(hour).padStart(2, '0') + '30');
}
TIME_SLOTS.push('1900');

// 모달 내부 시간표 로드
function loadTimetableInModal(classroomId) {
    const container = document.getElementById('modalTimetableContainer');
    container.innerHTML = '<div class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary" role="status"></div></div>';

    // API 요청: 강의실의 시간표 조회
    fetch(`/lms/api/v1/staff/lecture/schedule/classroom/${classroomId}/${currentYeartermCd}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch classroom timetable: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            renderTimetableInModal(data || []);
        })
        .catch(error => {
            console.error('Error fetching classroom timetable:', error);
            container.innerHTML = '<div class="text-center py-3 text-danger">시간표 로드 실패</div>';
        });
}

function parseTimeblock(timeblockCd) {
    return {
        dayCode: timeblockCd.substring(0, 2),
        time: timeblockCd.substring(2)
    };
}

function addThirtyMinutes(timeStr) {
    const hour = parseInt(timeStr.substring(0, 2));
    const min = parseInt(timeStr.substring(2));
    if (min === 30) {
        return String(hour + 1).padStart(2, '0') + '00';
    } else {
        return String(hour).padStart(2, '0') + '30';
    }
}

function groupLecturesByIdAndTime(data) {
    const grouped = {};
    data.forEach(item => {
        if (!grouped[item.lectureId]) {
            grouped[item.lectureId] = [];
        }
        grouped[item.lectureId].push(item);
    });

    Object.keys(grouped).forEach(lectureId => {
        grouped[lectureId].sort((a, b) => {
            const timeA = parseTimeblock(a.timeblockCd);
            const timeB = parseTimeblock(b.timeblockCd);
            if (timeA.dayCode !== timeB.dayCode) {
                return timeA.dayCode.localeCompare(timeB.dayCode);
            }
            return timeA.time.localeCompare(timeB.time);
        });
    });

    return grouped;
}

function findConsecutiveBlocks(items) {
    const blocks = [];
    let currentBlock = null;

    items.forEach(item => {
        const { dayCode, time } = parseTimeblock(item.timeblockCd);

        if (!currentBlock || currentBlock.dayCode !== dayCode) {
            if (currentBlock) blocks.push(currentBlock);
            currentBlock = {
                lectureId: item.lectureId,
                subjectName: item.subjectName,
                professorName: item.professorName,
                dayCode: dayCode,
                startTime: time,
                endTime: time
            };
        } else {
            const expectedNext = addThirtyMinutes(currentBlock.endTime);
            if (time === expectedNext) {
                currentBlock.endTime = time;
            } else {
                blocks.push(currentBlock);
                currentBlock = {
                    lectureId: item.lectureId,
                    subjectName: item.subjectName,
                    professorName: item.professorName,
                    dayCode: dayCode,
                    startTime: time,
                    endTime: time
                };
            }
        }
    });

    if (currentBlock) blocks.push(currentBlock);
    return blocks;
}

function renderTimetableInModal(scheduleData) {
    const container = document.getElementById('modalTimetableContainer');

    // 빈 배열이어도 시간표는 렌더링 (빈 시간표)
    const grouped = scheduleData && scheduleData.length > 0 ? groupLecturesByIdAndTime(scheduleData) : {};
    const allBlocks = [];

    // 옅은 색상 팔레트 (구분용)
    const colorPalette = [
        '#ffcdd2', // 옅은 빨강
        '#ffccbc', // 옅은 주황
        '#fff9c4', // 옅은 노랑
        '#c8e6c9', // 옅은 초록
        '#b3e5fc', // 옅은 파랑
        '#c5cae9', // 옅은 남색
        '#e1bee7'  // 옅은 보라
    ];

    // 강의 ID별로 색상 할당
    const lectureColors = {};
    let colorIndex = 0;
    Object.keys(grouped).forEach(lectureId => {
        lectureColors[lectureId] = colorPalette[colorIndex % colorPalette.length];
        colorIndex++;
        const blocks = findConsecutiveBlocks(grouped[lectureId]);
        allBlocks.push(...blocks);
    });

    // 시간표 그리드 초기화
    const timetableGrid = {};
    DAYS.forEach(day => {
        timetableGrid[day] = {};
        TIME_SLOTS.forEach(time => {
            timetableGrid[day][time] = null;
        });
    });

    // 블록 배치
    allBlocks.forEach(block => {
        const dayName = Object.keys(DAY_CODES).find(key => DAY_CODES[key] === block.dayCode);
        if (!dayName) return;

        const startIndex = TIME_SLOTS.indexOf(block.startTime);
        const endTime = addThirtyMinutes(block.endTime);
        const endIndex = TIME_SLOTS.indexOf(endTime);

        if (startIndex !== -1 && endIndex !== -1) {
            const rowSpan = endIndex - startIndex;
            timetableGrid[dayName][block.startTime] = {
                ...block,
                rowSpan: rowSpan,
                color: lectureColors[block.lectureId] || '#f1f5f9'
            };

            for (let i = startIndex + 1; i < endIndex; i++) {
                timetableGrid[dayName][TIME_SLOTS[i]] = 'occupied';
            }
        }
    });

    // HTML 생성 - 스크롤 컨테이너 추가
    let html = '<div class="timetable-wrapper" style="max-height: 500px; overflow-y: auto; overflow-x: hidden;"><table class="table table-bordered table-sm mb-0" style="font-size: 0.85rem; table-layout: fixed; width: 100%;">';
    html += '<thead><tr>';
    html += '<th style="width: 80px; min-width: 80px; text-align: center;">시간</th>';
    DAYS.forEach(day => {
        html += '<th style="text-align: center; width: calc((100% - 80px) / 5);">' + day + '</th>';
    });
    html += '</tr></thead><tbody>';

    TIME_SLOTS.slice(0, -1).forEach((time, idx) => {
        const nextTime = TIME_SLOTS[idx + 1];
        html += '<tr style="height: 40px;">';
        html += '<td class="text-center align-middle" style="font-size: 0.75rem; line-height: 1.2;">' +
                time.substring(0, 2) + ':' + time.substring(2) + '<br>~' + nextTime.substring(0, 2) + ':' + nextTime.substring(2) + '</td>';

        DAYS.forEach(day => {
            const cell = timetableGrid[day][time];

            if (cell === 'occupied') {
                return;
            }

            if (cell && cell.rowSpan) {
                const bgColor = cell.color || '#f1f5f9';
                html += '<td rowspan="' + cell.rowSpan + '" ' +
                        'class="p-2 text-center align-middle time-slot booked" ' +
                        'style="background-color: ' + bgColor + '; cursor: not-allowed; border: 1px solid rgba(0,0,0,0.1);">' +
                        '<div style="font-size: 0.85rem; font-weight: 600;">' + cell.subjectName + '</div>' +
                        '<div style="font-size: 0.85rem; font-weight: 600; margin-top: 2px; color: #000;">' + (cell.professorName || '') + '</div>' +
                        '</td>';
            } else {
                const dayCode = DAY_CODES[day];
                const timeblockCd = dayCode + time;
                html += '<td class="time-slot available" style="cursor: pointer;" data-day="' + day + '" data-daycode="' + dayCode + '" data-time="' + time + '" data-timeblock="' + timeblockCd + '"></td>';
            }
        });

        html += '</tr>';
    });

    html += '</tbody></table></div>';
    container.innerHTML = html;

    // 빈 슬롯 클릭 이벤트
    container.querySelectorAll('.time-slot.available').forEach(slot => {
        slot.addEventListener('click', function() {
            const timeblock = this.dataset.timeblock;
            const day = this.dataset.day;
            const time = this.dataset.time;

            if (this.classList.contains('selected')) {
                // 선택 해제
                this.classList.remove('selected');
                this.style.backgroundColor = '';
                this.style.color = '';

                // selectedTimeSlots 배열에서 제거
                const index = selectedTimeSlots.findIndex(t => t.timeblock === timeblock);
                if (index !== -1) {
                    selectedTimeSlots.splice(index, 1);
                }
            } else {
                // 선택
                this.classList.add('selected');
                this.style.backgroundColor = '#28a745';
                this.style.color = 'white';

                // selectedTimeSlots 배열에 추가
                selectedTimeSlots.push({
                    timeblock: timeblock,
                    day: day,
                    time: time
                });
            }

            updateSelectedTimeSlotsDisplay();
            updateHourProgress();
            updateModalAssignButton();
        });
    });
}

function updateSelectedTimeSlotsDisplay() {
    const displayEl = document.getElementById('selectedTimeSlotsDisplay');
    if (selectedTimeSlots.length === 0) {
        displayEl.innerHTML = '<em style="color: #6c757d; font-size: 13px;">시간 블록을 선택해주세요.</em>';
    } else {
        // 시간블록을 정렬 (요일 -> 시간 순)
        const sorted = [...selectedTimeSlots].sort((a, b) => {
            const dayOrder = DAYS.indexOf(a.day) - DAYS.indexOf(b.day);
            if (dayOrder !== 0) return dayOrder;
            return a.time.localeCompare(b.time);
        });

        displayEl.innerHTML = sorted.map(slot => {
            const startHour = slot.time.substring(0, 2);
            const startMin = slot.time.substring(2, 4);
            const endTime = addThirtyMinutes(slot.time);
            const endHour = endTime.substring(0, 2);
            const endMin = endTime.substring(2, 4);
            return `<span class="badge bg-success" style="margin-right: 8px; margin-bottom: 8px; font-size: 0.85rem; padding: 6px 10px; display: inline-flex; flex-direction: column; align-items: center; text-align: center;">${slot.day}요일<br>${startHour}:${startMin} ~ ${endHour}:${endMin}</span>`;
        }).join('');
    }
}

function updateModalAssignButton() {
    const btn = document.getElementById('modalAssignBtn');
    btn.disabled = !(selectedLecture && selectedClassroom && selectedTimeSlots.length > 0);
}

function initializeModalAssignButton() {
    document.getElementById('modalAssignBtn').addEventListener('click', function() {
        if (!selectedLecture || !selectedClassroom || selectedTimeSlots.length === 0) {
            Swal.fire({
                icon: 'warning',
                title: '선택 항목 부족',
                text: '강의, 강의실, 시간을 모두 선택해주세요.',
                confirmButtonText: '확인'
            });
            return;
        }

        // API 요청 데이터 구성
        const requestBody = selectedTimeSlots.map(slot => ({
            lectureId: selectedLecture,
            placeCd: selectedClassroom,
            timeblockCd: slot.timeblock
        }));

        // 배정하기 버튼 비활성화
        const btn = document.getElementById('modalAssignBtn');
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 배정 중...';

        // API 호출
        fetch('/lms/api/v1/staff/lecture/schedule/assign', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`배정 실패: ${response.status}`);
            }
            return response.text().then(text => text ? JSON.parse(text) : null);
        })
        .then(() => {
            Swal.fire({
                icon: 'success',
                title: '배정 완료',
                text: '강의 시간표가 성공적으로 배정되었습니다.',
                confirmButtonText: '확인'
            }).then(() => {
                // 모달 닫기
                timetableModalInstance.hide();

                // 페이지 새로고침 또는 데이터 갱신 (필터 유지)
                loadLectures(true);
                loadBuildings();
            });
        })
        .catch(error => {
            console.error('Assignment error:', error);
            Swal.fire({
                icon: 'error',
                title: '배정 실패',
                text: error.message || '강의 시간표 배정 중 오류가 발생했습니다.',
                confirmButtonText: '확인'
            });
        })
        .finally(() => {
            // 버튼 복구
            btn.disabled = false;
            btn.innerHTML = '<i class="fas fa-check"></i> 배정하기';
        });
    });
}

// 필터 리스너 초기화
function initializeFilterListeners() {
    document.getElementById('departmentFilter').addEventListener('change', function() {
        filterLectures();
    });

    document.getElementById('completionFilter').addEventListener('change', function() {
        filterLectures();
    });

    document.getElementById('statusFilter').addEventListener('change', function() {
        filterLectures();
    });
}

function filterLectures() {
    const departmentFilter = document.getElementById('departmentFilter').value;
    const completionFilter = document.getElementById('completionFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;

    let filteredLectures = allLectures;

    if (departmentFilter) {
        filteredLectures = filteredLectures.filter(lecture =>
            lecture.univDeptName && lecture.univDeptName.includes(departmentFilter)
        );
    }

    if (completionFilter) {
        filteredLectures = filteredLectures.filter(lecture =>
            lecture.completionName && lecture.completionName.includes(completionFilter)
        );
    }

    renderLectures(filteredLectures);

    // 렌더링 후 상태 필터 적용 (DOM 기반)
    if (statusFilter) {
        document.querySelectorAll('.lecture-item').forEach(item => {
            const itemStatus = item.dataset.status;
            if (itemStatus !== statusFilter) {
                item.style.display = 'none';
            }
        });
    }
}

// 기본 필터 적용 (시수부족)
function applyDefaultFilter() {
    // statusFilter가 이미 'NEED'로 설정되어 있으므로 필터만 트리거
    setTimeout(() => {
        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter && statusFilter.value === 'NEED') {
            filterLectures();
        }
    }, 100);
}





















