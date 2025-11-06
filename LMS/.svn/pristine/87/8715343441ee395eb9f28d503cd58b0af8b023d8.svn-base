/**
 * 교수 목록 화면 (staffProfessorinfoList.jsp)의 인터랙션 및 통계 처리를 담당하는 JavaScript
 *
 * 기능:
 * 1. 초기 재직 상태 분포 파이 차트 렌더링
 * 2. 재직 상태 카드 클릭 시 상세 통계 모달 호출 및 Bar Chart 렌더링
 * 3. 모달 내 부서별/직급별 통계 AJAX 처리 및 단계별 뷰 전환
 * 4. 페이지네이션 및 검색 기능 처리
 */

// JSP에서 설정한 전역 변수
// const JSU_CONTEXT_PATH = "${pageContext.request.contextPath}";
// const employmentCountsRaw = { '재직중': N, '휴직중': N, ... };

const STATUS_COLORS = {
	'재직': 'rgba(13, 110, 253, 0.7)',
	'휴직': 'rgba(255, 193, 7, 0.7)',
	'퇴직': 'rgba(220, 53, 69, 0.7)',
	'연구년': 'rgba(13, 202, 240, 0.7)'
};
const STATUS_BORDER_COLORS = {
	'재직': 'rgb(13, 110, 253)',
	'휴직': 'rgb(255, 193, 7)',
	'퇴직': 'rgb(220, 53, 69)',
	'연구년': 'rgb(13, 202, 240)'
};
const HIGHLIGHT_COLOR = 'rgba(220, 53, 69, 1)'; // 강조 색상 (빨간색)


let statusPieChart;
let chartInstances = {};
let currentModalStatus = '';
let currentModalCollege = '';
let currentModalDepartment = '';
let currentModalAppointment = ''; // 임용 구분 상태 저장
let currentView = 'college';
let lastClickedAppointmentIndex = null; // 마지막으로 클릭한 임용 구분 바의 인덱스
let lastAppointmentBarColor = null; // 임용 구분 바의 원래 색상


$(document).ready(function() {

	// 1. 초기 파이 차트 생성 및 로드
	// employmentCountsRaw는 JSP에서 '재직중', '휴직중' 등의 키로 넘어올 수 있으므로 이를 처리해야 함.
	const normalizedCounts = {};
	if (typeof employmentCountsRaw !== 'undefined') {
		// JSP에서 넘어온 데이터를 '재직중' -> '재직' 형태로 정규화
		Object.keys(employmentCountsRaw).forEach(key => {
			const normalizedKey = key.replace('중', '');
			normalizedCounts[normalizedKey] = employmentCountsRaw[key];
		});
		initStatusPieChart(normalizedCounts);
	} else {
		console.error("employmentCountsRaw 변수가 JSP에서 정의되지 않았습니다.");
	}

	// 2. 재직 상태 카드 클릭 이벤트 (모달 열기 및 초기 데이터 로드)
	$(document).on('click', '.status-card', function() {
		currentModalStatus = $(this).data('status'); // '재직중' 형태
		currentModalCollege = '';
		currentModalDepartment = '';
		currentModalAppointment = '';
		currentView = 'college';
		lastClickedAppointmentIndex = null;

		const displayStatus = currentModalStatus.replace('중', '');
		$('#statusDetailModalLabel').text(`[${displayStatus}] 교수 상세 분포`);

		resetModalView('college');
		loadCollegeCounts(currentModalStatus);

		$('#statusDetailModal').modal('show');
	});


	// 3. 모달 내 뒤로가기 버튼 이벤트
	$('#modal-back-btn').on('click', function() {
		const displayStatus = currentModalStatus.replace('중', '');

		if (currentView === 'appointment') {
			// 임용 구분 뷰 -> 학과 뷰
			currentModalDepartment = '';
			currentModalAppointment = '';
			lastClickedAppointmentIndex = null;
			resetModalView('department');

			$('#statusDetailModalLabel').text(`[${displayStatus}] 교수 상세 분포 - ${currentModalCollege}`);
			loadDepartmentCounts(currentModalStatus, currentModalCollege);
		} else if (currentView === 'department') {
			// 학과 뷰 -> 단과대학 뷰
			currentModalCollege = '';
			currentModalDepartment = '';
			currentModalAppointment = '';
			lastClickedAppointmentIndex = null;
			resetModalView('college');

			$('#statusDetailModalLabel').text(`[${displayStatus}] 교수 상세 분포`);
			loadCollegeCounts(currentModalStatus);
		}
	});

	// 4. "목록 보기" 버튼 클릭 이벤트 (임용 구분 필터링이 안된 상태로 목록 조회)
	$('#viewListButton').on('click', function() {
		// 현재 상태까지의 필터만 적용하고 목록 조회 (임용 구분은 비워둠)
		viewProfessorListForAppointment(
			currentModalStatus,
			currentModalCollege,
			currentModalDepartment,
			'' // 임용 구분 필터링을 비우고 조회
		);
	});

	// 5. 페이지네이션 처리 함수 (글로벌)
	window.pageing = function(page) {
		$('#currentPageInput').val(page);
		$('#searchForm').submit();
	};

	// 6. 검색 버튼 클릭 시 폼 제출 함수 바인딩
	$('#searchButton').on('click', function() {
		handleSearchSubmit();
	});
	$("#professorTable tbody tr.professor-row").css('cursor', 'pointer');

	// 행 클릭 이벤트 바인딩
	$("#professorTable tbody tr.professor-row").on('click', function() {
		// 클릭된 행의 data-staff-no 속성 값 (사번)을 가져옴
		const professorNo = $(this).data('professorNo');

		if (professorNo) {
			const detailUrl = JSU_CONTEXT_PATH + '/lms/staff/professors/' + professorNo;
			window.location.href = detailUrl;
		}
	});
});

// ==========================================================
// 챠트 초기화 및 AJAX 데이터 로드 함수
// ==========================================================

function initStatusPieChart(countsMap) {
	if (!countsMap || Object.keys(countsMap).length === 0) {
		console.warn("차트 데이터가 없어 파이 차트를 그릴 수 없습니다.");
		return;
	}

	const ctx = document.getElementById('statusPieChart');
	if (!ctx) return;

	if (statusPieChart) {
		statusPieChart.destroy();
	}

	// ⭐⭐ 핵심 수정: 데이터가 0인 상태도 포함하여 일관된 차트 구조 유지 ⭐⭐
	const allStatusLabels = ['재직', '휴직', '퇴직', '연구년'];
	let labels = [];
	let dataValues = [];

	// 모든 고정 라벨을 검사하고, 데이터가 0이더라도 라벨과 값을 추가
	allStatusLabels.forEach(label => {
		// JSP에서 '재직중' 등으로 넘어왔다면 countsMap은 '재직' 키를 가짐
		const count = countsMap[label] || 0; // 데이터가 없으면 0으로 처리

		if (count > 0) { // 데이터가 0보다 클 때만 실제로 차트 섹션에 포함
			labels.push(label);
			dataValues.push(count);
		}
	});

	// 모든 데이터 값이 0이거나 라벨 배열이 비어있으면 차트 대신 메시지 표시
	if (labels.length === 0 || dataValues.every(v => v === 0)) {
		const context = ctx.getContext('2d');
		context.clearRect(0, 0, ctx.width, ctx.height);
		context.font = '16px Arial';
		context.fillStyle = '#6c757d';
		context.textAlign = 'center';
		context.fillText('조회된 데이터가 없습니다.', ctx.width / 2, ctx.height / 2);
		return;
	}


	const backgroundColors = labels.map(label => STATUS_COLORS[label]);
	const borderColors = labels.map(label => STATUS_BORDER_COLORS[label]);


	statusPieChart = new Chart(ctx.getContext('2d'), {
		type: 'doughnut',
		data: {
			labels: labels,
			datasets: [{
				data: dataValues,
				backgroundColor: backgroundColors,
				hoverBackgroundColor: borderColors,
				hoverBorderColor: "rgba(234, 236, 244, 1)",
			}],
		},
		options: {
			maintainAspectRatio: false,
			responsive: true,
			plugins: {
				tooltip: {
					callbacks: {
						title: function(tooltipItems) {
							return tooltipItems[0].label;
						},
						label: function(context) {
							const value = context.raw;
							// 총합 계산
							const total = context.dataset.data.reduce((sum, current) => sum + current, 0);
							const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;

							return '교수 수: ' + value.toLocaleString() + '명 (' + percentage + '%)';
						}
					}
				},
				legend: {
					position: 'bottom',
				}
			},
			cutout: '80%',
		},
	});
}

/**
 * 모달 내 뷰(단과대, 학과, 임용 구분) 전환
 */
function resetModalView(view) {
	// 'position-view'는 'appointment-view'로 대체되었다고 가정하고 JSP의 ID에 맞춤
	$('#college-view, #department-view, #appointment-view').addClass('d-none');
	$(`#${view}-view`).removeClass('d-none');

	if (view === 'college') {
		$('#modal-back-btn').addClass('d-none');
	} else {
		$('#modal-back-btn').removeClass('d-none');
	}

	currentView = view;
}

// ------------------- 1. 단과대학 통계 (Bar Chart) -------------------
function loadCollegeCounts(statusName) {
	const apiUrl = `${JSU_CONTEXT_PATH}/lms/staff/professors/stats/college`;
	console.log(`[AJAX] 단과대학 통계 요청 URL: ${apiUrl}?status=${statusName}`);

	// 로딩 상태 표시
	const collegeViewEl = $('#college-view');
	collegeViewEl.html('<div class="d-flex justify-content-center align-items-center h-100"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>');


	$.ajax({
		url: apiUrl,
		type: 'GET',
		data: { status: statusName },
		dataType: 'json',
		success: function(data) {
			// Canvas를 다시 삽입하여 차트가 그려질 공간 확보
			collegeViewEl.html('<canvas id="collegeBarChart"></canvas>');

			console.log("[AJAX Success] 단과대학 데이터 수신:", data);

			let collegeData = data;
			// 서버에서 JSON 문자열로 넘어올 경우 대비
			if (typeof collegeData === 'string') {
				try {
					collegeData = JSON.parse(collegeData);
				} catch (e) {
					console.error("수신된 응답 문자열을 JSON으로 파싱할 수 없습니다:", e);
					initBarChart('collegeBarChart', ['JSON 파싱 오류'], [0]);
					return;
				}
			}

			let labels = [];
			let dataValues = [];

			if (collegeData && typeof collegeData === 'object' && !Array.isArray(collegeData)) {
				// Map 형태 { "단과대A": 10, "단과대B": 5 }
				labels = Object.keys(collegeData);
				dataValues = Object.values(collegeData);
			} else if (Array.isArray(collegeData)) {
				// List 형태 [{ NAME: "단과대A", COUNT: 10 }, ...]
				labels = collegeData.map(item => item.NAME || item.collegeName || item.college_name || 'N/A');
				dataValues = collegeData.map(item => item.COUNT || item.count || 0);
			} else {
				console.error("수신된 단과대학 데이터 형식이 올바르지 않습니다.");
				initBarChart('collegeBarChart', ['데이터 형식 오류'], [0]);
				return;
			}

			lastClickedAppointmentIndex = null;
			initBarChart('collegeBarChart', labels, dataValues, '단과대학별 교수 분포');
		},
		error: function(xhr, status, error) {
			// 로딩 상태 제거 후 오류 메시지 표시
			collegeViewEl.html('<canvas id="collegeBarChart"></canvas>');
			console.error("단과대학 통계 로드 실패. 404 또는 서버 오류:", status, error, xhr.responseText);
			alert("통계 정보를 가져오는 데 실패했습니다. 서버 API 경로를 확인하세요.");
			initBarChart('collegeBarChart', ['데이터 로드 실패 (API 에러)'], [0], '단과대학별 교수 분포');
		}
	});
}

// ------------------- 2. 학과 통계 (Bar Chart) -------------------
function loadDepartmentCounts(statusName, collegeName) {
	const apiUrl = `${JSU_CONTEXT_PATH}/lms/staff/professors/stats/department`;
	console.log(`[AJAX] 학과 통계 요청 URL: ${apiUrl}?status=${statusName}&college=${collegeName}`);

	// 로딩 상태 표시
	const departmentViewEl = $('#department-view');
	departmentViewEl.html('<div class="d-flex justify-content-center align-items-center h-100"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>');


	$.ajax({
		url: apiUrl,
		type: 'GET',
		data: { status: statusName, college: collegeName },
		success: function(data) {
			// Canvas를 다시 삽입하여 차트가 그려질 공간 확보
			departmentViewEl.html('<canvas id="departmentBarChart"></canvas>');
			console.log("[AJAX Success] 학과 데이터 수신:", data);

			let deptData = data;
			let labels = [];
			let dataValues = [];

			if (deptData && typeof deptData === 'object' && !Array.isArray(deptData)) {
				labels = Object.keys(deptData);
				dataValues = Object.values(deptData);
			} else if (Array.isArray(deptData)) {
				labels = deptData.map(item => item.NAME || item.departmentName || item.dept_name || 'N/A');
				dataValues = deptData.map(item => item.COUNT || item.count || 0);
			} else {
				console.error("수신된 학과 데이터 형식이 올바르지 않습니다.");
				initBarChart('departmentBarChart', ['데이터 형식 오류'], [0], '학과별 교수 분포');
				return;
			}


			lastClickedAppointmentIndex = null;
			initBarChart('departmentBarChart', labels, dataValues, `${collegeName} 학과별 교수 분포`);

			const displayStatus = statusName.replace('중', '');
			$('#statusDetailModalLabel').text(`[${displayStatus}] 교수 상세 분포 - ${collegeName}`);
		},
		error: function(xhr, status, error) {
			// 로딩 상태 제거 후 오류 메시지 표시
			departmentViewEl.html('<canvas id="departmentBarChart"></canvas>');
			console.error("학과 통계 로드 실패. 404 또는 서버 오류:", status, error, xhr.responseText);
			alert("학과 통계 정보를 가져오는 데 실패했습니다. 서버 API 경로를 확인하세요.");
			initBarChart('departmentBarChart', ['데이터 로드 실패 (API 에러)'], [0], '학과별 교수 분포');
		}
	});
}

// ------------------- 3. 임용 구분 통계 (Bar Chart) -------------------
function loadAppointmentCounts(statusName, collegeName, deptName) {
	// MyBatis 쿼리가 임용 구분을 반환하도록 수정되었으므로 엔드포인트는 'position'을 그대로 사용합니다.
	const apiUrl = `${JSU_CONTEXT_PATH}/lms/staff/professors/stats/position`;
	console.log(`[AJAX] 임용 통계 요청 URL: ${apiUrl}?status=${statusName}&college=${collegeName}&department=${deptName}`);

	// 로딩 상태 표시
	const appointmentViewEl = $('#appointment-view');
	appointmentViewEl.html('<div class="d-flex justify-content-center align-items-center h-100"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>');


	$.ajax({
		url: apiUrl,
		type: 'GET',
		data: { status: statusName, college: collegeName, department: deptName },
		success: function(data) {
			// Canvas를 다시 삽입하여 차트가 그려질 공간 확보
			appointmentViewEl.html('<canvas id="positionBarChart"></canvas>');
			console.log("[AJAX Success] 임용 통계 데이터 수신:", data);

			let appointmentData = data;
			let labels = [];
			let dataValues = [];

			if (appointmentData && typeof appointmentData === 'object' && !Array.isArray(appointmentData)) {
				labels = Object.keys(appointmentData);
				dataValues = Object.values(appointmentData);
			} else if (Array.isArray(appointmentData)) {
				labels = appointmentData.map(item => item.NAME || item.appointmentName || item.appnt_name || 'N/A');
				dataValues = appointmentData.map(item => item.COUNT || item.count || 0);
			} else {
				console.error("수신된 임용 데이터 형식이 올바르지 않습니다.");
				initBarChart('positionBarChart', ['데이터 형식 오류'], [0], '임용 구분별 교수 분포');
				return;
			}


			lastClickedAppointmentIndex = null;
			const displayStatus = statusName.replace('중', '');

			if (dataValues.some(val => val > 0)) {
				// 'positionBarChart' ID를 재활용
				initBarChart('positionBarChart', labels, dataValues, `${deptName} 임용 구분별 교수 분포`);

				$('#statusDetailModalLabel').text(`[${displayStatus}] 교수 상세 분포 - ${collegeName} - ${deptName} (임용 구분)`);
			} else {
				console.log("해당 학과에는 임용 구분별 교수 데이터가 없습니다.");
				initBarChart('positionBarChart', ['데이터 없음'], [0], '임용 구분별 교수 분포');
			}
		},
		error: function(xhr, status, error) {
			// 로딩 상태 제거 후 오류 메시지 표시
			appointmentViewEl.html('<canvas id="positionBarChart"></canvas>');
			console.error("임용 통계 로드 실패. 404 또는 서버 오류:", status, error, xhr.responseText);
			alert("임용 통계 정보를 가져오는 데 실패했습니다. 서버 API 경로를 확인하세요.");
			initBarChart('positionBarChart', ['데이터 로드 실패 (API 에러)'], [0], '임용 구분별 교수 분포');
		}
	});
}

/**
 * 막대 차트 초기화 및 클릭 이벤트 바인딩
 */
function initBarChart(canvasId, labels, dataValues, chartTitle) {
	const ctx = document.getElementById(canvasId);

	if (chartInstances[canvasId]) {
		chartInstances[canvasId].destroy();
		delete chartInstances[canvasId];
	}

	if (!ctx) {
		console.error(`Canvas element with ID '${canvasId}' not found. Cannot initialize chart.`);
		return;
	}

	// 데이터가 모두 0인 경우 차트 대신 메시지 표시
	if (dataValues.every(v => v === 0)) {
		const context = ctx.getContext('2d');
		context.clearRect(0, 0, ctx.width, ctx.height);
		context.font = '16px Arial';
		context.fillStyle = '#6c757d';
		context.textAlign = 'center';
		context.fillText('조회된 데이터가 없습니다.', ctx.width / 2, ctx.height / 2);
		return;
	}

	const baseStatus = currentModalStatus.replace('중', '');
	const initialBarColor = STATUS_COLORS[baseStatus] || 'rgba(78, 115, 223, 0.5)';
	const borderColor = STATUS_BORDER_COLORS[baseStatus] || 'rgb(78, 115, 223)';

	if (canvasId === 'positionBarChart') {
		lastAppointmentBarColor = initialBarColor;
	}

	let backgroundColors = dataValues.map(() => initialBarColor);

	// 임용 구분 차트에서 이전에 클릭된 항목 하이라이트 유지
	if (canvasId === 'positionBarChart' && lastClickedAppointmentIndex !== null && lastClickedAppointmentIndex < backgroundColors.length) {
		backgroundColors[lastClickedAppointmentIndex] = HIGHLIGHT_COLOR;
	}

	const chartInstance = new Chart(ctx.getContext('2d'), {
		type: 'bar',
		data: {
			labels: labels,
			datasets: [{
				label: '교수 수',
				data: dataValues,
				backgroundColor: backgroundColors,
				borderColor: borderColor,
				borderWidth: 1
			}]
		},
		options: {
			maintainAspectRatio: false,
			responsive: true,
			plugins: {
				title: {
					display: true,
					text: chartTitle,
					font: {
						size: 16
					}
				},
				legend: {
					display: false
				},
				tooltip: {
					callbacks: {
						label: function(context) {
							let label = context.dataset.label || '';
							if (label) {
								label += ': ';
							}
							if (context.parsed.y !== null) {
								label += context.parsed.y.toLocaleString() + '명';
							}
							return label;
						}
					}
				}
			},
			scales: {
				x: {
					title: {
						display: true,
						text: canvasId === 'positionBarChart' ? '임용 구분' : '부서/단과대학',
					}
				},
				y: {
					beginAtZero: true,
					ticks: {
						callback: function(value) {
							if (value % 1 === 0) {
								return value.toLocaleString();
							}
							return null;
						},
					}
				}
			},
		},
	});

	chartInstances[canvasId] = chartInstance;

	//  차트 클릭 이벤트 처리 로직
	chartInstance.canvas.onclick = (event) => {
		const chart = chartInstance;
		// 'nearest' 모드로 변경하여 터치/마우스에 더 잘 반응하도록 함
		const points = chart.getElementsAtEventForMode(event, 'nearest', { intersect: true }, true);

		if (points.length) {
			const index = points[0].index;
			const selectedLabel = chart.data.labels[index];

			if (canvasId === 'collegeBarChart') {
				currentModalCollege = selectedLabel;
				resetModalView('department');
				loadDepartmentCounts(currentModalStatus, currentModalCollege);
			} else if (canvasId === 'departmentBarChart') {
				currentModalDepartment = selectedLabel;
				resetModalView('appointment');
				loadAppointmentCounts(currentModalStatus, currentModalCollege, currentModalDepartment);
			} else if (canvasId === 'positionBarChart') {
				// 임용 구분 차트 클릭 시
				const clickedAppointmentName = selectedLabel;
				const dataset = chart.data.datasets[0];

				// 이전 하이라이트 제거
				if (lastClickedAppointmentIndex !== null && lastClickedAppointmentIndex !== index && lastClickedAppointmentIndex < dataset.backgroundColor.length) {
					dataset.backgroundColor[lastClickedAppointmentIndex] = lastAppointmentBarColor;
				}

				if (index < dataset.backgroundColor.length) {
					// 새 항목 하이라이트 적용
					dataset.backgroundColor[index] = HIGHLIGHT_COLOR;
					chart.update();

					lastClickedAppointmentIndex = index;
					currentModalAppointment = clickedAppointmentName;
				}
				// 목록 보기 실행
				viewProfessorListForAppointment(currentModalStatus, currentModalCollege, currentModalDepartment, currentModalAppointment);
			}
		}
	};
}

/**
 * 필터 값을 설정하고 목록을 조회 (임용 구분에 맞춰 필터링)
 */
function viewProfessorListForAppointment(statusName, collegeName, deptName, appointmentName) {
	$('#filterStatusInput').val(statusName);
	$('#filterCollegeInput').val(collegeName);
	$('#filterDepartmentInput').val(deptName);
	$('#filterAppointmentInput').val(appointmentName); // 임용 구분 필터

	$('#currentPageInput').val(1);
	$('#searchForm').submit();

	$('#statusDetailModal').modal('hide');
}

/**
 * 통합 검색 버튼 클릭 시 처리 (필터 값 초기화 포함)
 */
function handleSearchSubmit() {
	const form = document.getElementById('searchForm');
	const searchInput = document.getElementById('searchInput');

	if (searchInput.value.trim() === '') {
		// 검색어가 없으면 모든 필터 초기화
		$('#filterStatusInput').val('');
		$('#filterCollegeInput').val('');
		$('#filterDepartmentInput').val('');
		$('#filterAppointmentInput').val(''); // 임용 구분 필터 초기화

		searchInput.value = '';
		$('#currentPageInput').val('1');

	} else {
		// 검색어가 있으면 필터는 유지하고 페이지네이션만 1로 리셋
		$('#currentPageInput').val('1');
	}

	form.submit();
}

/**
 * 페이징 처리 (글로벌 함수)
 */
function pageing(page) {
	$('#currentPageInput').val(page);
	$('#searchForm').submit();
}
