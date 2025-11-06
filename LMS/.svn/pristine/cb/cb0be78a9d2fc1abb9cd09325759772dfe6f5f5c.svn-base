<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 26.     	최건우          header area searchbar delete
 * 									  header area profile delete
 *									  header area log-out icon add and text add
 *	2025. 10. 06.       신혜진          알림 href 연결
 *	2025. 10. 11.		김수현		  header 레이아웃 변경 및 자체 header.css 추가
 *	2025. 10. 24.		김수현		  header 레이아웃 구조 변경 및 class 추가
-->


<nav class="layout-navbar navbar navbar-expand-xl align-items-center bg-white" id="layout-navbar">
	<div class="layout-menu-toggle navbar-nav align-items-xl-center me-3 me-xl-0 d-xl-none">
		<a class="nav-item nav-link px-0 me-xl-4" href="javascript:void(0)"> <i class="bx bx-menu bx-sm"></i>
		</a>
	</div>

	<div class="navbar-nav-right d-flex align-items-center container-fluid" id="navbar-collapse">

        <div class="nav-item lh-1 me-4 d-flex align-items-center user-info-badge minimal-style">
            <security:authorize access="isAuthenticated()">
                <security:authentication property="principal" var="userDetails" />
                <security:authentication property="authorities" var="authorities" />

				<!-- 역할 뱃지 -->
				<security:authorize access="hasRole('STUDENT')">
				    <span class="role-text role-badge-student">학생</span>
				</security:authorize>
				<security:authorize access="hasRole('PROFESSOR')">
				    <span class="role-text role-badge-professor">교수</span>
				</security:authorize>
				<security:authorize access="hasRole('STAFF')">
				    <span class="role-text role-badge-staff">교직원</span>
				</security:authorize>

                <span class="name-text" id="user-info-text">
                    <security:authorize access="hasRole('STAFF')">
                        <span class="user-name-main">${userDetails.realUser.lastName}${userDetails.realUser.firstName}</span>
				    </security:authorize>
                    <security:authorize access="hasRole('STUDENT')">
                        <!-- Will be populated by JS -->
                    </security:authorize>
                    <security:authorize access="hasRole('PROFESSOR')">
                        <!-- Will be populated by JS -->
                    </security:authorize>
                </span>
            </security:authorize>
        </div>

        <ul class="navbar-nav flex-row align-items-center **ms-auto**">
	        <security:authorize access="hasRole('PROFESSOR')">
	        <li class="nav-item lh-1 me-3 dropdown position-relative">
	            <a href="#" class="nav-link position-relative" id="timetableDropdown" data-bs-toggle="dropdown" aria-expanded="false">
	                <i class='bx bx-calendar-alt me-1'></i> 시간표
	            </a>

	            <div id="timetable-dropdown-menu" class="dropdown-menu dropdown-menu-end shadow border-0 p-3"
	                 aria-labelledby="timetableDropdown"
	                 style="min-width: 850px; max-width: 900px; z-index: 1100; border-radius: 0.75rem;"
	                 onclick="event.stopPropagation();">

	                <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
	                    <h6 class="mb-0 fw-bold text-dark">시간표</h6>
	                    <div class="d-flex gap-2">
	                        <select id="timetable-year" class="form-select form-select-sm" style="width: 120px;">
	                            <option value="">연도</option>
	                        </select>
	                        <select id="timetable-semester" class="form-select form-select-sm" style="width: 120px;">
	                            <option value="">학기</option>
	                            <option value="REG1">1학기</option>
	                            <option value="REG2">2학기</option>
	                            <option value="SUB1">여름학기</option>
	                            <option value="SUB2">겨울학기</option>
	                        </select>
	                    </div>
	                </div>

	                <div id="timetable-loading" class="text-center py-3" style="display: none;">
	                    <div class="spinner-border spinner-border-sm text-primary" role="status">
	                        <span class="visually-hidden">Loading...</span>
	                    </div>
	                </div>

	                <div id="timetable-container" style="max-height: 600px; overflow-y: auto;">
	                    <!-- Timetable will be rendered here -->
	                </div>
	            </div>
	        </li>
	        </security:authorize>

	        <security:authorize access="hasRole('STUDENT')">
	        <li class="nav-item lh-1 me-3 dropdown position-relative">
	            <a href="#" class="nav-link position-relative" id="studentTimetableDropdown" data-bs-toggle="dropdown" aria-expanded="false">
	                <i class='bx bx-calendar-alt me-1'></i> 시간표
	            </a>

	            <div id="student-timetable-dropdown-menu" class="dropdown-menu dropdown-menu-end shadow border-0 p-3"
	                 aria-labelledby="studentTimetableDropdown"
	                 style="min-width: 850px; max-width: 900px; z-index: 1100; border-radius: 0.75rem;"
	                 onclick="event.stopPropagation();">

	                <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
	                    <h6 class="mb-0 fw-bold text-dark">시간표</h6>
	                    <div class="d-flex gap-2">
	                        <select id="student-timetable-year" class="form-select form-select-sm" style="width: 120px;">
	                            <option value="">연도</option>
	                        </select>
	                        <select id="student-timetable-semester" class="form-select form-select-sm" style="width: 120px;">
	                            <option value="">학기</option>
	                            <option value="REG1">1학기</option>
	                            <option value="REG2">2학기</option>
	                            <option value="SUB1">여름학기</option>
	                            <option value="SUB2">겨울학기</option>
	                        </select>
	                    </div>
	                </div>

	                <div id="student-timetable-loading" class="text-center py-3" style="display: none;">
	                    <div class="spinner-border spinner-border-sm text-primary" role="status">
	                        <span class="visually-hidden">Loading...</span>
	                    </div>
	                </div>

	                <div id="student-timetable-container" style="max-height: 600px; overflow-y: auto;">
	                    <!-- Timetable will be rendered here -->
	                </div>
	            </div>
	        </li>
	        </security:authorize>

          <li class="nav-item lh-1 me-3 dropdown position-relative">
	<a href="#" class="nav-link position-relative" id="notificationDropdown" data-bs-toggle="dropdown" aria-expanded="false">
	    <i class='bx bx-bell' style="font-size: 1.25rem;"></i>

        <span id="unread-count-badge"
              class="badge rounded-pill bg-danger position-absolute top-0"
              style="left: 18px; top: 0px; display: none; border: 1px solid #fff; padding: 0.25em 0.55em; min-width: 1.5em; height: 1.5em; font-size: 0.75em; display: flex; align-items: center; justify-content: center;">
            0
        </span>
    </a>

    <div id="notification-dropdown-menu" class="dropdown-menu dropdown-menu-end shadow border-0 p-0"
     aria-labelledby="notificationDropdown"
     style="min-width: 450px; max-width: 480px; z-index: 1100; border-radius: 0.75rem;">

        <div class="d-flex justify-content-between align-items-center dropdown-header border-bottom py-2 px-4 bg-white" style="border-top-left-radius: 0.75rem; border-top-right-radius: 0.75rem;">
            <h6 class="mb-0 fw-bold text-dark">알림</h6>
            <span class="badge bg-primary rounded-pill" id="dropdown-unread-count" style="font-size: 0.8rem; padding: 0.35em 0.65em;">0</span>
        </div>

        <div id="dropdown-list-container" class="list-group list-group-flush">
            <a class="list-group-item text-center text-muted" href="#" style="border: none; cursor: default;">로딩 중...</a>
        </div>

        <a class="dropdown-item text-center fw-bold text-primary py-2 border-top" href="/lms/notifications" style="font-size: 0.95rem;">
            전체 알림 보기 &gt;
        </a>
    </div>
</li>
	        <li class="nav-item lh-1 me-3">
                <a href="/logout">
                    <i class='bx bx-log-out me-1'></i> 로그아웃
                </a>
            </li>
	    </ul>
	</div>
</nav>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

<!-- 추가! -->
<script src="${pageContext.request.contextPath}/js/app/staff/userNotificationList.js"></script>

<security:authorize access="isAuthenticated()">
<script>
$(document).ready(function() {
    <security:authorize access="hasRole('STUDENT')">
    // Fetch student info
    fetch('/classroom/api/v1/student/me')
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch student info');
            return response.json();
        })
        .then(data => {
        	const html = '<span class="user-name-main">' + data.lastName + data.firstName +
			            ' (' + data.studentNo + ')</span> ' +
			            '<span class="user-dept-info">' + data.univDeptName + ' ' + data.gradeName + ' | ' + data.stuStatusName + '</span>';
			$('#user-info-text').html(html);
        })
        .catch(error => {
            console.error('Error fetching student info:', error);
            $('#user-info-text').text('정보 로드 실패');
        });
    </security:authorize>

    <security:authorize access="hasRole('PROFESSOR')">
    // Fetch professor info
    fetch('/classroom/api/v1/professor/me')
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch professor info');
            return response.json();
        })
        .then(data => {
            const html = '<span class="user-name-main">' + data.lastName + data.firstName +
			            ' (' + data.professorNo + ')</span> ' +
			            '<span class="user-dept-info">' + data.univDeptName + ' ' + data.prfAppntName + '</span>';
			$('#user-info-text').html(html);
        })
        .catch(error => {
            console.error('Error fetching professor info:', error);
            $('#user-info-text').text('정보 로드 실패');
        });

    // Timetable functionality
    const DAYS = ['월', '화', '수', '목', '금', '토'];
    const DAY_CODES = { '월': 'MO', '화': 'TU', '수': 'WE', '목': 'TH', '금': 'FR', '토': 'SA' };
    const TIME_SLOTS = [];
    for (let hour = 9; hour < 19; hour++) {
        TIME_SLOTS.push(String(hour).padStart(2, '0') + '00');
        TIME_SLOTS.push(String(hour).padStart(2, '0') + '30');
    }
    TIME_SLOTS.push('1900');

    const COLORS = [
        '#FFB3BA', '#FFDFBA', '#FFFFBA', '#BAFFC9', '#BAE1FF',
        '#D4BAFF', '#FFB3E6', '#FFC9BA', '#BAF0FF', '#E6BAFF',
        '#FFBABA', '#FFE5BA', '#F0FFBA', '#BAFFDB', '#BADEFF'
    ];

    let timetableInitialized = false;

    // Initialize year options
    const currentYear = new Date().getFullYear();
    for (let y = currentYear - 3; y <= currentYear + 1; y++) {
        $('#timetable-year').append('<option value="' + y + '">' + y + '년</option>');
    }

    // Load timetable when dropdown is opened
    $('#timetableDropdown').on('click', function(e) {
        if (!timetableInitialized) {
            loadTimetable();
            timetableInitialized = true;
        }
    });

    // Reload when year or semester changes
    $('#timetable-year, #timetable-semester').on('change', function() {
        const year = $('#timetable-year').val();
        const semester = $('#timetable-semester').val();
        if (year && semester) {
            loadTimetable();
        }
    });

    function loadTimetable() {
        const year = $('#timetable-year').val();
        const semester = $('#timetable-semester').val();

        let url = '/classroom/api/v1/professor/schedule';
        if (year && semester) {
            url += '?yeartermCd=' + year + '_' + semester;
        }

        $('#timetable-loading').show();
        $('#timetable-container').html('');

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch timetable');
                return response.json();
            })
            .then(data => {
                $('#timetable-loading').hide();
                if (data && data.length > 0) {
                    renderTimetable(data);
                } else {
                    $('#timetable-container').html(
                        '<div class="text-center py-3 text-muted">해당 학기에 시간표가 없습니다.</div>'
                    );
                }
            })
            .catch(error => {
                console.error('Error fetching timetable:', error);
                $('#timetable-loading').hide();
                $('#timetable-container').html(
                    '<div class="text-center py-3 text-danger">시간표 로드 실패</div>'
                );
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
                    placeName: item.placeName,
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
                        placeName: item.placeName,
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

    function renderTimetable(scheduleData) {
        const grouped = groupLecturesByIdAndTime(scheduleData);
        const allBlocks = [];
        const lectureColorMap = {};
        let colorIndex = 0;

        const sortedLectureIds = Object.keys(grouped).sort((a, b) => {
            const firstA = parseTimeblock(grouped[a][0].timeblockCd);
            const firstB = parseTimeblock(grouped[b][0].timeblockCd);
            if (firstA.dayCode !== firstB.dayCode) {
                return firstA.dayCode.localeCompare(firstB.dayCode);
            }
            return firstA.time.localeCompare(firstB.time);
        });

        sortedLectureIds.forEach(lectureId => {
            lectureColorMap[lectureId] = COLORS[colorIndex % COLORS.length];
            colorIndex++;
            const blocks = findConsecutiveBlocks(grouped[lectureId]);
            allBlocks.push(...blocks);
        });

        const timetableGrid = {};
        DAYS.forEach(day => {
            timetableGrid[day] = {};
            TIME_SLOTS.forEach(time => {
                timetableGrid[day][time] = null;
            });
        });

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
                    color: lectureColorMap[block.lectureId]
                };

                for (let i = startIndex + 1; i < endIndex; i++) {
                    timetableGrid[dayName][TIME_SLOTS[i]] = 'occupied';
                }
            }
        });

        let html = '<table class="table table-bordered table-sm mb-0" style="font-size: 0.85rem;">';
        html += '<thead><tr>';
        html += '<th style="width: 70px; min-width: 70px; text-align: center;">시간</th>';
        DAYS.forEach(day => {
            html += '<th style="width: 15%; text-align: center;">' + day + '</th>';
        });
        html += '</tr></thead><tbody>';

        TIME_SLOTS.slice(0, -1).forEach((time, idx) => {
            const nextTime = TIME_SLOTS[idx + 1];
            html += '<tr style="height: 35px;">';
            html += '<td class="text-center align-middle" style="font-size: 0.7rem; line-height: 1.2;">' +
                    time.substring(0, 2) + ':' + time.substring(2) + '<br>~' + nextTime.substring(0, 2) + ':' + nextTime.substring(2) + '</td>';

            DAYS.forEach(day => {
                const cell = timetableGrid[day][time];

                if (cell === 'occupied') {
                    return;
                }

                if (cell && cell.rowSpan) {
                    html += '<td rowspan="' + cell.rowSpan + '" ' +
                            'class="p-2 text-center align-middle" ' +
                            'style="background-color: ' + cell.color + '; vertical-align: middle;">' +
                            '<div style="font-size: 0.85rem; font-weight: 600;">' + cell.subjectName + '</div>' +
                            '<div style="font-size: 0.7rem; margin-top: 2px;">' + cell.placeName + '</div>' +
                            '</td>';
                } else {
                    html += '<td></td>';
                }
            });

            html += '</tr>';
        });

        html += '</tbody></table>';
        $('#timetable-container').html(html);
    }
    </security:authorize>

    <security:authorize access="hasRole('STUDENT')">
    // Student Timetable functionality
    const STUDENT_DAYS = ['월', '화', '수', '목', '금', '토'];
    const STUDENT_DAY_CODES = { '월': 'MO', '화': 'TU', '수': 'WE', '목': 'TH', '금': 'FR', '토': 'SA' };
    const STUDENT_TIME_SLOTS = [];
    for (let hour = 9; hour < 19; hour++) {
        STUDENT_TIME_SLOTS.push(String(hour).padStart(2, '0') + '00');
        STUDENT_TIME_SLOTS.push(String(hour).padStart(2, '0') + '30');
    }
    STUDENT_TIME_SLOTS.push('1900');

    const STUDENT_COLORS = [
        '#FFB3BA', '#FFDFBA', '#FFFFBA', '#BAFFC9', '#BAE1FF',
        '#D4BAFF', '#FFB3E6', '#FFC9BA', '#BAF0FF', '#E6BAFF',
        '#FFBABA', '#FFE5BA', '#F0FFBA', '#BAFFDB', '#BADEFF'
    ];

    let studentTimetableInitialized = false;

    // Initialize year options
    const studentCurrentYear = new Date().getFullYear();
    for (let y = studentCurrentYear - 3; y <= studentCurrentYear + 1; y++) {
        $('#student-timetable-year').append('<option value="' + y + '">' + y + '년</option>');
    }

    // Load timetable when dropdown is opened
    $('#studentTimetableDropdown').on('click', function(e) {
        if (!studentTimetableInitialized) {
            loadStudentTimetable();
            studentTimetableInitialized = true;
        }
    });

    // Reload when year or semester changes
    $('#student-timetable-year, #student-timetable-semester').on('change', function() {
        const year = $('#student-timetable-year').val();
        const semester = $('#student-timetable-semester').val();
        if (year && semester) {
            loadStudentTimetable();
        }
    });

    function loadStudentTimetable() {
        const year = $('#student-timetable-year').val();
        const semester = $('#student-timetable-semester').val();

        let url = '/classroom/api/v1/student/schedule';
        if (year && semester) {
            url += '?yeartermCd=' + year + '_' + semester;
        }

        $('#student-timetable-loading').show();
        $('#student-timetable-container').html('');

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch timetable');
                return response.json();
            })
            .then(data => {
                $('#student-timetable-loading').hide();
                if (data && data.length > 0) {
                    renderStudentTimetable(data);
                } else {
                    $('#student-timetable-container').html(
                        '<div class="text-center py-3 text-muted">해당 학기에 시간표가 없습니다.</div>'
                    );
                }
            })
            .catch(error => {
                console.error('Error fetching timetable:', error);
                $('#student-timetable-loading').hide();
                $('#student-timetable-container').html(
                    '<div class="text-center py-3 text-danger">시간표 로드 실패</div>'
                );
            });
    }

    function parseStudentTimeblock(timeblockCd) {
        return {
            dayCode: timeblockCd.substring(0, 2),
            time: timeblockCd.substring(2)
        };
    }

    function addStudentThirtyMinutes(timeStr) {
        const hour = parseInt(timeStr.substring(0, 2));
        const min = parseInt(timeStr.substring(2));
        if (min === 30) {
            return String(hour + 1).padStart(2, '0') + '00';
        } else {
            return String(hour).padStart(2, '0') + '30';
        }
    }

    function groupStudentLecturesByIdAndTime(data) {
        const grouped = {};
        data.forEach(item => {
            if (!grouped[item.lectureId]) {
                grouped[item.lectureId] = [];
            }
            grouped[item.lectureId].push(item);
        });

        Object.keys(grouped).forEach(lectureId => {
            grouped[lectureId].sort((a, b) => {
                const timeA = parseStudentTimeblock(a.timeblockCd);
                const timeB = parseStudentTimeblock(b.timeblockCd);
                if (timeA.dayCode !== timeB.dayCode) {
                    return timeA.dayCode.localeCompare(timeB.dayCode);
                }
                return timeA.time.localeCompare(timeB.time);
            });
        });

        return grouped;
    }

    function findStudentConsecutiveBlocks(items) {
        const blocks = [];
        let currentBlock = null;

        items.forEach(item => {
            const { dayCode, time } = parseStudentTimeblock(item.timeblockCd);

            if (!currentBlock || currentBlock.dayCode !== dayCode) {
                if (currentBlock) blocks.push(currentBlock);
                currentBlock = {
                    lectureId: item.lectureId,
                    subjectName: item.subjectName,
                    placeName: item.placeName,
                    dayCode: dayCode,
                    startTime: time,
                    endTime: time
                };
            } else {
                const expectedNext = addStudentThirtyMinutes(currentBlock.endTime);
                if (time === expectedNext) {
                    currentBlock.endTime = time;
                } else {
                    blocks.push(currentBlock);
                    currentBlock = {
                        lectureId: item.lectureId,
                        subjectName: item.subjectName,
                        placeName: item.placeName,
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

    function renderStudentTimetable(scheduleData) {
        const grouped = groupStudentLecturesByIdAndTime(scheduleData);
        const allBlocks = [];
        const lectureColorMap = {};
        let colorIndex = 0;

        const sortedLectureIds = Object.keys(grouped).sort((a, b) => {
            const firstA = parseStudentTimeblock(grouped[a][0].timeblockCd);
            const firstB = parseStudentTimeblock(grouped[b][0].timeblockCd);
            if (firstA.dayCode !== firstB.dayCode) {
                return firstA.dayCode.localeCompare(firstB.dayCode);
            }
            return firstA.time.localeCompare(firstB.time);
        });

        sortedLectureIds.forEach(lectureId => {
            lectureColorMap[lectureId] = STUDENT_COLORS[colorIndex % STUDENT_COLORS.length];
            colorIndex++;
            const blocks = findStudentConsecutiveBlocks(grouped[lectureId]);
            allBlocks.push(...blocks);
        });

        const timetableGrid = {};
        STUDENT_DAYS.forEach(day => {
            timetableGrid[day] = {};
            STUDENT_TIME_SLOTS.forEach(time => {
                timetableGrid[day][time] = null;
            });
        });

        allBlocks.forEach(block => {
            const dayName = Object.keys(STUDENT_DAY_CODES).find(key => STUDENT_DAY_CODES[key] === block.dayCode);
            if (!dayName) return;

            const startIndex = STUDENT_TIME_SLOTS.indexOf(block.startTime);
            const endTime = addStudentThirtyMinutes(block.endTime);
            const endIndex = STUDENT_TIME_SLOTS.indexOf(endTime);

            if (startIndex !== -1 && endIndex !== -1) {
                const rowSpan = endIndex - startIndex;
                timetableGrid[dayName][block.startTime] = {
                    ...block,
                    rowSpan: rowSpan,
                    color: lectureColorMap[block.lectureId]
                };

                for (let i = startIndex + 1; i < endIndex; i++) {
                    timetableGrid[dayName][STUDENT_TIME_SLOTS[i]] = 'occupied';
                }
            }
        });

        let html = '<table class="table table-bordered table-sm mb-0" style="font-size: 0.85rem;">';
        html += '<thead><tr>';
        html += '<th style="width: 70px; min-width: 70px; text-align: center;">시간</th>';
        STUDENT_DAYS.forEach(day => {
            html += '<th style="width: 15%; text-align: center;">' + day + '</th>';
        });
        html += '</tr></thead><tbody>';

        STUDENT_TIME_SLOTS.slice(0, -1).forEach((time, idx) => {
            const nextTime = STUDENT_TIME_SLOTS[idx + 1];
            html += '<tr style="height: 35px;">';
            html += '<td class="text-center align-middle" style="font-size: 0.7rem; line-height: 1.2;">' +
                    time.substring(0, 2) + ':' + time.substring(2) + '<br>~' + nextTime.substring(0, 2) + ':' + nextTime.substring(2) + '</td>';

            STUDENT_DAYS.forEach(day => {
                const cell = timetableGrid[day][time];

                if (cell === 'occupied') {
                    return;
                }

                if (cell && cell.rowSpan) {
                    html += '<td rowspan="' + cell.rowSpan + '" ' +
                            'class="p-2 text-center align-middle" ' +
                            'style="background-color: ' + cell.color + '; vertical-align: middle;">' +
                            '<div style="font-size: 0.85rem; font-weight: 600;">' + cell.subjectName + '</div>' +
                            '<div style="font-size: 0.7rem; margin-top: 2px;">' + cell.placeName + '</div>' +
                            '</td>';
                } else {
                    html += '<td></td>';
                }
            });

            html += '</tr>';
        });

        html += '</tbody></table>';
        $('#student-timetable-container').html(html);
    }
    </security:authorize>
});
</script>
</security:authorize>


