/**
 * 교수 강의 관리 페이지 JavaScript
 */

let currentLectureId = null;
let currentYearterm = '2026_REG1';  // 현재 선택된 학기
let currentLectureList = [];        // D3 및 테이블 업데이트를 위한 전체 강의 목록 데이터 => 동시에 바뀔 때 페이지 전체가 깜빡거리지 않도록 하기 위해서

// 전역으로
window.currentLectureList = currentLectureList;

// 페이지 로드 시
document.addEventListener('DOMContentLoaded', function() {
	console.log('==> 교수 강의 관리 페이지 로드');

	// 드롭다운에서 첫 번째 옵션(selected 되어 있는거)을 기본값으로
    const selector = document.getElementById('yeartermSelector');
    if (selector && selector.options.length > 0) {
        currentYearterm = selector.value;
        console.log('기본 학기:', currentYearterm);
    }

    // 초기 데이터 로드
    loadLectures();

    // 알림 드롭다운 초기화
    initNotificationDropdown();

    // 웹소켓 구독
    setTimeout(function() {
        if (typeof connectWishlistWebSocket === 'function') {
            connectWishlistWebSocket();
        }
    }, 3000);
});

/**
 * 학기 변경 이벤트
 */
function changeYearterm() {
    const selector = document.getElementById('yeartermSelector');
    currentYearterm = selector.value;

    console.log('학기 변경:', currentYearterm);

    // 데이터 새로고침
    loadLectures();
}

/**
 * 알림 드롭다운 초기화 => 드롭다운이 실행되지 않아서 추가함
 */
function initNotificationDropdown() {
	setTimeout(function() {
		const dropdownEl = document.getElementById('notificationDropdown');
		const dropdownMenu = document.getElementById('notification-dropdown-menu');

		if (!dropdownEl || !dropdownMenu) {
			console.log('알림 드롭다운 없음 (정상)');
			return;
		}

		// Bootstrap 인스턴스 생성 => 알림 메세지 드롭다운
		let dropdownInstance = bootstrap.Dropdown.getInstance(dropdownEl);
		if (!dropdownInstance) {
			dropdownInstance = new bootstrap.Dropdown(dropdownEl);
		}

		// 클릭 이벤트 직접 처리
		$(dropdownEl).off('click').on('click', function(e) {
			e.preventDefault();
			e.stopPropagation();

			const isVisible = $(dropdownMenu).hasClass('show');

			if (isVisible) {
				$(dropdownMenu).removeClass('show');
				$(dropdownEl).attr('aria-expanded', 'false');
				if (typeof fetchUnreadCount === 'function') {
					fetchUnreadCount();
				}
			} else {
				$(dropdownMenu).addClass('show');
				$(dropdownEl).attr('aria-expanded', 'true');
				$('#unread-count-badge').hide().text(0);
				if (typeof fetchNotificationsForDropdown === 'function') {
					fetchNotificationsForDropdown();
				}
			}
		});

		// 외부 클릭 시 닫기
		$(document).on('click', function(e) {
			if (!$(e.target).closest('#notificationDropdown, #notification-dropdown-menu').length) {
				if ($(dropdownMenu).hasClass('show')) {
					$(dropdownMenu).removeClass('show');
					$(dropdownEl).attr('aria-expanded', 'false');
					if (typeof fetchUnreadCount === 'function') {
						fetchUnreadCount();
					}
				}
			}
		});

		console.log('[교수] 알림 드롭다운 수동 설정 완료');
	}, 500);
}

// =========================================================================
// D3 시각화 통합 데이터 로딩 함수
// =========================================================================

/**
 * D3 시각화용 통합 데이터 로딩 (통계 + 강의 목록)
 */
function loadProfessorStatsForD3() {
    const tbody = document.getElementById('lectureTableBody');
	tbody.innerHTML = `
        <tr>
            <td colspan="9" class="loading-message">
                <div class="spinner"></div>
                강의 목록을 불러오는 중...
            </td>
        </tr>
    `;

    // 통합 API 호출
    fetch(`${CONTEXT_PATH}/lms/professor/rest/lectures/stats/d3-data?yearterm=${currentYearterm}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('D3 통합 데이터 로드 성공:', data);

            // 전역 변수에 강의 목록 저장
            currentLectureList = data.lectureList || [];
             window.currentLectureList = currentLectureList;
             

            // 1. 단순 숫자 통계 카드 업데이트
            document.getElementById('totalLectures').textContent =
                data.stats.totalLectures || 0;
            document.getElementById('totalStudents').textContent =
                data.stats.totalStudents || 0;

            // 2. D3 차트 렌더링
            // avgEnrollRate는 0.0 ~ 1.0 실수형 데이터
            drawAvgEnrollRateDoughnut(data.stats.avgEnrollRate || 0);
            // lectureList- chartRate (0.0 ~ 1.0 실수형) 필드를 사용
            drawLectureEnrollBarChart(currentLectureList); // 저장된 목록 사용

            // 3. 강의 목록 테이블 렌더링
            renderLectures(currentLectureList); // 저장된 목록 사용

        })
        .catch(error => {
            console.error('D3 통합 데이터 로드 실패:', error);
            document.getElementById('lectureTableBody').innerHTML = `
                <tr><td colspan="9" class="empty-message">통계 및 강의 목록을 불러오는 중 오류가 발생했습니다.</td></tr>
            `;
        });
}

/**
 * 강의 통계 및 목록을 포함한 전체 데이터를 로드, 렌더링
 */
function loadLectures() {
    loadProfessorStatsForD3();
}

/**
 * 강의 목록 렌더링
 */
function renderLectures(lectures) {
	const tbody = document.getElementById('lectureTableBody');

	if (!lectures || lectures.length === 0) {
		tbody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-message">
                    개설한 강의가 없습니다.
                </td>
            </tr>
        `;
		return;
	}

	tbody.innerHTML = lectures.map((lecture, index) => {
		const rate = lecture.enrollRate || 0;
		const currentEnroll = lecture.currentEnroll || 0;
		const maxCap = lecture.maxCap || 0;

		let statusClass = 'status-low';
		if (currentEnroll >= maxCap) {
			statusClass = 'status-full';
		} else if (maxCap > 0) {
			const fillRate = currentEnroll / maxCap;
			if (fillRate >= 0.8) statusClass = 'status-high';
			else if (fillRate >= 0.5) statusClass = 'status-medium';
		}

		return `
		    <tr class="lecture-row" onclick="showStudentModal('${lecture.lectureId}', '${lecture.subjectName}')">
		        <td>${index + 1}</td>
		        <td>${lecture.subjectName}</td>
		        <td>${lecture.placeName || '미정'}</td>
		        <td>${lecture.timeInfo || '미정'}</td>
		        <td>${lecture.credit}/${lecture.hour}</td>
		        <td>${lecture.completionName}</td>
		        <td>${lecture.targetGrades}</td>
		        <td id="enroll-${lecture.lectureId}" class="enroll-info ${statusClass}">
		            <strong>${currentEnroll}</strong> / ${maxCap}
		            ${currentEnroll >= maxCap ? '<span class="badge-full">마감</span>' : ''}
		        </td>
		        <td id="rate-${lecture.lectureId}" class="enroll-rate">
		            <div class="enroll-rate-wrapper">
		                <div class="progress-bar">
		                    <div class="progress-fill ${statusClass}" style="width: ${rate}%"></div>
		                </div>
		                <span class="rate-text">${rate}%</span>
		            </div>
		        </td>
		    </tr>
		`;
	}).join('');
}

/**
 * 학생 목록 모달 열기
 */
function showStudentModal(lectureId, lectureName) {

	console.log('=== showStudentModal 호출 ===');
	console.log('lectureId:', lectureId);
	console.log('lectureName:', lectureName);

	const modalElement = document.getElementById('studentModal');
	console.log('모달 요소 찾기:', modalElement);

	if (!modalElement) {
		console.error('❌ 모달 요소를 찾을 수 없습니다!');
		alert('모달 요소를 찾을 수 없습니다. JSP 확인 필요');
		return;
	}

	console.log('학생 목록 모달 열기:', lectureId, lectureName);

	currentLectureId = lectureId;

	document.getElementById('modalLectureTitle').textContent = lectureName;
    document.getElementById('studentModal').style.display = 'block';

	// 모달 통계 로드
	loadModalStats(lectureId);

	// 학생 목록 로드
	loadStudentList(lectureId);
}

/**
 * 모달 통계 로드 (기존 코드 유지)
 */
function loadModalStats(lectureId) {
	fetch(`${CONTEXT_PATH}/lms/professor/rest/lectures/${lectureId}/enroll-info`)
		.then(response => response.json())
		.then(info => {
			console.log('강의 정보 조회 성공:', info);

			const currentEnroll = info.currentEnroll || 0;
			const maxCap = info.maxCap || 0;
			const rate = maxCap > 0 ? Math.round((currentEnroll / maxCap) * 100 * 10) / 10 : 0;

			document.getElementById('modalCurrentEnroll').textContent = currentEnroll;
			document.getElementById('modalMaxCap').textContent = maxCap;
			document.getElementById('modalEnrollRate').textContent = rate + '%';
		})
		.catch(error => {
			console.error('강의 정보 조회 실패:', error);
		});
}

/**
 * 학생 목록 로드
 */
let currentPage = 1;
const pageSize = 10; // 테스트용

function loadStudentList(lectureId, page = 1) {
    currentPage = page;

    const tbody = document.getElementById('studentTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="loading-message">
                <div class="spinner"></div>
                학생 목록을 불러오는 중...
            </td>
        </tr>
    `;

    fetch(`${CONTEXT_PATH}/lms/professor/rest/lectures/${lectureId}/students?page=${page}&pageSize=${pageSize}`)
        .then(response => response.json())
        .then(data => {
            console.log('학생 목록 조회 성공:', data);
            renderStudentList(data.students);
            renderPagination(data, lectureId);
        })
        .catch(error => {
            console.error('학생 목록 조회 실패:', error);
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-message">
                        학생 목록을 불러오는 중 오류가 발생했습니다.
                    </td>
                </tr>
            `;
        });
}

/**
 * 학생 목록 렌더링
 */
function renderStudentList(students) {
	const tbody = document.getElementById('studentTableBody');

	if (!students || students.length === 0) {
		tbody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-message">
                    수강신청한 학생이 없습니다.
                </td>
            </tr>
        `;
		return;
	}

	tbody.innerHTML = '';

	students.forEach((student, index) => {
        // 페이지별 번호 계산
        const rowNumber = (currentPage - 1) * pageSize + index + 1;

        const row = `
            <tr>
                <td>${rowNumber}</td>
                <td>${student.studentNo}</td>
                <td>${student.studentName}</td>
                <td>${student.gradeName}</td>
                <td>${student.collegeName || '-'}</td>
                <td>${student.univDeptName || '-'}</td>
                <td>${student.applyAt}</td>
            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

/**
 * 모달 닫기
 */
function closeModal() {
	const modal = document.getElementById('studentModal');
    const modalContent = modal.querySelector('.modal-content');

    // 페이드아웃 애니메이션 추가
    modalContent.style.animation = 'modalFadeOut 0.3s ease-out';

    setTimeout(() => {
        modal.removeAttribute('style');
        modal.style.display = 'none';
        modalContent.style.animation = ''; // 애니메이션 초기화
        currentLectureId = null;
    }, 300);
}

/**
 * 모달 외부 클릭 시 닫기
 */
window.onclick = function(event) {
	const modal = document.getElementById('studentModal');
	if (event.target === modal) {
		closeModal();
	}
};


/**
 * 실시간 정원 업데이트 함수 (WebSocket용)
 * - 전역 함수로 선언해서 wishlist-websocket.js에서 호출 가능
 * 서버 재호출 없이 메모리 데이터, D3 차트만 업데이트(화면 깜빡임 X)
 */
window.updateLectureEnrollRealtime = function(lectureId, currentEnroll, maxCap) {
	console.log('==> [교수] 실시간 정원 업데이트:', lectureId, currentEnroll, maxCap);
	
    let isUpdated = false;

	// 1. 메모리상의 강의 목록(currentLectureList) 데이터 업데이트
    currentLectureList = currentLectureList.map(lecture => {
        if (lecture.lectureId === lectureId) {
            // 새로운 정원 정보로 데이터 업데이트
            lecture.currentEnroll = currentEnroll;
            lecture.maxCap = maxCap;

            // 정원 충족률 필드 업데이트
            lecture.enrollRate = maxCap > 0 ? Math.round((currentEnroll / maxCap) * 100 * 10) / 10 : 0;
            lecture.chartRate = maxCap > 0 ? (currentEnroll / maxCap) : 0;

            isUpdated = true;
        }
        return lecture;
    });
    
    // window에도 동기화
    window.currentLectureList = currentLectureList;

	// 2. DOM 업데이트 (테이블 행 및 차트)
	if (isUpdated) {
        // 1. 총 학생 수 업데이트
        const totalStudents = currentLectureList.reduce((sum, l) => sum + l.currentEnroll, 0);
        document.getElementById('totalStudents').textContent = totalStudents;

        // 2. 평균 정원 충족률 계산 및 업데이트
        const totalMaxCap = currentLectureList.reduce((sum, l) => sum + l.maxCap, 0);
        const totalCurrentEnroll = currentLectureList.reduce((sum, l) => sum + l.currentEnroll, 0);
        const newAvgRate = totalMaxCap > 0 ? totalCurrentEnroll / totalMaxCap : 0;
        
        // 3. 도넛 차트 업데이트 (평균 정원 충족률)
        drawAvgEnrollRateDoughnut(newAvgRate);

        // 4. 막대 그래프 업데이트 (강의 현황 요약)
        drawLectureEnrollBarChart(currentLectureList);

        // 5. 강의 목록 테이블 개별 업데이트
        const enrollCell = document.getElementById(`enroll-${lectureId}`);
        const rateCell = document.getElementById(`rate-${lectureId}`);

        if (enrollCell) {
            const rate = currentLectureList.find(l => l.lectureId === lectureId).enrollRate;

            // 상태 클래스 계산
            let statusClass = 'status-low';
            if (currentEnroll >= maxCap) {
                statusClass = 'status-full';
            } else if (maxCap > 0) {
                const fillRate = currentEnroll / maxCap;
                if (fillRate >= 0.8) statusClass = 'status-high';
                else if (fillRate >= 0.5) statusClass = 'status-medium';
            }

            // 정원 셀 업데이트
            enrollCell.className = `enroll-info ${statusClass} updated`;
            enrollCell.classList.add('updated'); // 애니메이션
            enrollCell.innerHTML = `<strong>${currentEnroll}</strong> / ${maxCap}${currentEnroll >= maxCap ? ' <span class="badge-full">마감</span>' : ''}`;

            // 정원 충족률 셀 업데이트
            if (rateCell) {
                const progressBarFill = rateCell.querySelector('.progress-fill');
                const rateText = rateCell.querySelector('.rate-text');

                if (progressBarFill && rateText) {
                    progressBarFill.className = `progress-fill ${statusClass}`;
                    progressBarFill.style.width = `${rate}%`;
                    rateText.textContent = `${rate}%`;
                    rateCell.classList.add('updated');

                    setTimeout(() => {
                        rateCell.classList.remove('updated');
                    }, 1000);
                }
            }
            // 애니메이션 효과 제거
            setTimeout(() => {
                enrollCell.classList.remove('updated');
            }, 1000);
        }
    }

	// 3. 모달이 열려있고 해당 강의라면 모달도 업데이트
	if (currentLectureId === lectureId) {
		const modalCurrentEnroll = document.getElementById('modalCurrentEnroll');
		const modalMaxCap = document.getElementById('modalMaxCap');
		const modalEnrollRate = document.getElementById('modalEnrollRate');

		if (modalCurrentEnroll && modalMaxCap && modalEnrollRate) {
			modalCurrentEnroll.textContent = currentEnroll;
			modalMaxCap.textContent = maxCap;

			const rate = maxCap > 0 ? Math.round((currentEnroll / maxCap) * 100 * 10) / 10 : 0;
			modalEnrollRate.textContent = rate + '%';

			// 학생 목록 새로고침
			loadStudentList(lectureId);
		}
	}
};

// 페이징 렌더링
function renderPagination(paginationInfo, lectureId) {
    console.log(' ==> paginationInfo:', paginationInfo);

    let paginationDiv = document.getElementById('studentPagination');
    console.log('기존 paginationDiv:', paginationDiv);

    if (!paginationDiv) {
        const modalBody = document.querySelector('.modal-body');

        if (!modalBody) return;

        paginationDiv = document.createElement('div');
        paginationDiv.id = 'studentPagination';
        paginationDiv.className = 'pagination';
        modalBody.appendChild(paginationDiv);
    }

    const { currentPage, totalPages } = paginationInfo;
    console.log(`currentPage: ${currentPage}, totalPages: ${totalPages}`);

    let html = '';

    if (currentPage > 1) {
        html += `<button onclick="loadStudentList('${lectureId}', ${currentPage - 1})">이전</button>`;
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            html += `<span class="current">${i}</span>`;
        } else {
            html += `<button onclick="loadStudentList('${lectureId}', ${i})">${i}</button>`;
        }
    }

    if (currentPage < totalPages) {
        html += `<button onclick="loadStudentList('${lectureId}', ${currentPage + 1})">다음</button>`;
    }
    console.log('생성된 HTML:', html);
    paginationDiv.innerHTML = html;
}

// =========================================================================
// D3.js 차트 렌더링 함수
// =========================================================================

/**
 * D3 도넛 차트 렌더링 (평균 정원 충족률)
 */
function drawAvgEnrollRateDoughnut(avgRate) {
    // 텍스트 중앙에 업데이트
    const displayRate = (avgRate * 100).toFixed(1);
    document.getElementById('avgEnrollRateText').textContent = `${displayRate}%`;

    // 차트 설정
    const width = 150, height = 150, radius = Math.min(width, height) / 2;
    const innerRadius = radius * 0.8;
    const colorFill = "#85aef3";
    const colorEmpty = "#e9ecef";

    // SVG 초기화 및 그룹 생성
    const svg = d3.select("#avgEnrollRateChart")
        .attr("width", width)
        .attr("height", height)
        .html('')
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);

    // D3 레이아웃 설정
    const pie = d3.pie().value(d => d.value).sort(null);
    const arc = d3.arc().innerRadius(innerRadius).outerRadius(radius);

    // 데이터: 충족된 비율과 남은 비율
    const data = [{ label: "충족", value: avgRate }, { label: "미충족", value: 1 - avgRate }];

    // Arc Transition 함수 (탄성 애니메이션)
    function arcTween(d) {
        const i = d3.interpolate(this._current || { startAngle: 0, endAngle: 0 }, d);
        this._current = i(0);
        return function(t) { return arc(i(t)); }
    }

    // path 생성 및 애니메이션 적용
    svg.selectAll("path")
        .data(pie(data))
        .enter()
        .append("path")
        .attr("fill", (d, i) => i === 0 ? colorFill : colorEmpty)
        .attr("d", arc)
        .transition()
        .duration(1200)
        // 부드러운 종료 효과
        .ease(d3.easeQuadOut)
        // 더 부드러운 효과: .ease(d3.easeCubicOut)
        .attrTween("d", arcTween);
}


/**
 * D3 가로 막대 차트 렌더링 (개별 강의 정원 충족률)
 * @param {Array<Object>} lectureList - 개별 강의 목록 데이터
 */
function drawLectureEnrollBarChart(lectureList) {
    if (!lectureList || lectureList.length === 0) {
        d3.select("#lectureBarChart").html('<text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-size="14" fill="#6c757d">개설된 강의가 없어 비교할 수 없습니다.</text>').attr("height", 50);
        return;
    }

    // chartRate (0.0~1.0 실수)를 사용하기 위해 정렬
    const sortedList = lectureList.sort((a, b) => b.chartRate - a.chartRate);

    // 차트 크기 설정
    const margin = { top: 10, right: 100, bottom: 50, left: 300 };
    const chartWidth = 1400;
    const width = chartWidth - margin.left - margin.right;
    const barHeight = 25;
    const padding = 15;
    const height = sortedList.length * (barHeight + padding) + margin.top + margin.bottom;

    // SVG 초기화 및 그룹 생성
    // transition을 활용하여 부드럽게 새로 그리는 방식
    const svg = d3.select("#lectureBarChart")
        .attr("width", chartWidth)
        .attr("height", height)
        .html('')
        .append("g")
        .attr("transform", `translate(${margin.left}, ${margin.top})`);

    // Scale 설정
    const xScale = d3.scaleLinear().domain([0, 1]).range([0, width]);
    const yScale = d3.scaleBand()
        .domain(sortedList.map(d => d.subjectName))
        .range([0, sortedList.length * (barHeight + padding)])
        .paddingInner(padding / (barHeight + padding))
        .paddingOuter(0.5);

    // 막대 그룹 생성
    const bars = svg.selectAll(".bar-group")
        .data(sortedList)
        .enter()
        .append("g")
        .attr("class", "bar-group")
        .attr("transform", d => `translate(0, ${yScale(d.subjectName)})`);

    // 막대 본체 (애니메이션 적용)
    bars.append("rect")
        .attr("class", "bar")
        .attr("fill", d => d.chartRate >= 0.8 ? "#1398eb" : (d.chartRate >= 0.25 ? "#a7e49f" : "#f6bb73"))
        .attr("height", yScale.bandwidth())
        .attr("x", 0)
        .attr("width", 0)
        .transition()
        .duration(1500)
        .delay((d, i) => i * 150)
        .ease(d3.easeQuadOut)
        .attr("width", d => xScale(d.chartRate)); // chartRate 필드 사용

    // 강의명 라벨 (Y축 역할)
    bars.append("text")
        .attr("class", "lecture-label")
        .attr("x", -15)
        .attr("y", yScale.bandwidth() / 2)
        .attr("dy", "0.35em")
        .attr("text-anchor", "end")
        .text(d => d.subjectName)
        .style("opacity", 0)
        .transition().delay((d, i) => i * 150 + 500).duration(500).style("opacity", 1);

    // 값 라벨 (충족률 텍스트)
    bars.append("text")
        .attr("class", "value-label")
        .attr("x", 0)
        .attr("y", yScale.bandwidth() / 2)
        .attr("dy", "0.35em")
        .attr("text-anchor", "start")
        .text(d => (d.chartRate * 100).toFixed(1) + '%') // chartRate 필드 사용
        .style("fill", "#333").style("opacity", 0)
        .transition()
        .duration(1500)
        .delay((d, i) => i * 150 + 500)
        .ease(d3.easeQuadOut)
        .attr("x", d => xScale(d.chartRate) + 8) // 막대와 텍스트 간격 증가
        .style("opacity", 1);

    // X축 (비율 0%~100%) 추가
    svg.append("g")
        .attr("transform", `translate(0, ${height - margin.top - margin.bottom + 5})`)
        .call(d3.axisBottom(xScale).tickFormat(d3.format(".0%")));
}