<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025.10. 26.     	정태일            최초 생성
 *  2025.10. 26.     	정태일            시설예약목록 추가
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교직원 포털 - 시설 예약 전체 현황</title>
<!--     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> -->
<!--     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"> -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff/staffUserReserveList.css">

</head>
<body>
	<!-- 외부 래퍼 추가 -->
    <div class="reserve-list-page">
        <div class="reserve-container">

            <!-- 페이지 헤더 추가 -->
            <div class="page-header">
                <h1>시설 예약 전체 현황</h1>
            </div>

            <!-- 검색 섹션 -->
            <div class="search-section">
                <h5><i class="bi bi-search"></i> 시설 예약 검색</h5>
                <form id="searchForm" class="search-form">
                    <div class="form-group">
                        <label for="searchTerm">검색어</label>
                        <input type="text" class="form-control" id="searchTerm" name="searchTerm" placeholder="시설명, 사용자 이름 등으로 검색" value="">
                    </div>
                    <div class="form-group">
                        <label for="placeTypeFilter">시설 종류</label>
                        <select class="form-control" id="placeTypeFilter">
                            <option value="">전체</option>
                            <option value="스터디룸">스터디룸</option>
                            <option value="세미나실">세미나실</option>
                            <option value="강의실">강의실</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> 검색</button>
                        <button type="button" class="btn btn-secondary" onclick="location.href='${pageContext.request.contextPath}/lms/staff/userreserves'"><i class="bi bi-arrow-counterclockwise"></i> 초기화</button>
                    </div>
                </form>
            </div>
			<div class="total-info">
                <strong>총 예약 인원수: <span id="totalHeadcount">0</span>명</strong>
            </div>
            <!-- 콘텐츠 영역 -->
            <div class="content-row">
                <div class="content-left">
                    <h5 class="section-title"><i class="bi bi-calendar-check"></i> 전체 시설 예약 목록</h5>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>예약 번호</th>
                                    <th>사용자 이름</th>
                                    <th>시설명</th>
                                    <th>예약 사유</th>
                                    <th>예약 생성일</th>
                                    <th>사용 시작일</th>
                                    <th>사용 종료일</th>
                                    <th>인원수</th>
                                    <th>예약 현황</th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>

                    <nav class="pagination-area" id="pagination">
						<!-- 동적 생성 -->
                    </nav>

                </div>

                <div class="content-right">
                    <h5 class="section-title"><i class="bi bi-bar-chart-line"></i> 시설별 예약 현황</h5>
                    <div class="chart-container">
                        <canvas id="reservationChart"></canvas>
                    </div>
                </div>
            </div>

        </div> <!-- reserve-container 닫기 -->
    </div> <!-- reserve-list-page 닫기 -->

    <textarea id="userReserveListJsonData" style="display:none;"><c:out value="${userReserveListJson}" escapeXml="true" /></textarea>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        // LocalDateTime 문자열을 원하는 형식으로 포맷팅하는 함수
        function formatLocalDateTime(dateTimeString, format) {
            if (!dateTimeString) return '';
            const date = new Date(dateTimeString);

            // 유효성 검사 추가
            if (isNaN(date.getTime())) {
                try {
                    // T를 사용하여 ISO 8601 형식으로 변환 시도
                    const normalizedDateString = dateTimeString.replace(' ', 'T');
                    const normalizedDate = new Date(normalizedDateString);
                    if (!isNaN(normalizedDate.getTime())) {
                        date = normalizedDate;
                    } else {
                         console.error("Invalid date string after normalization:", dateTimeString);
                         return '';
                    }
                } catch (e) {
                     console.error("Date parsing error:", e);
                     return '';
                }
            }

            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            let formatted = format;
            formatted = formatted.replace(/yyyy/g, year);
            formatted = formatted.replace(/MM/g, month);
            formatted = formatted.replace(/dd/g, day);
            formatted = formatted.replace(/HH/g, hours);
            formatted = formatted.replace(/mm/g, minutes);
            return formatted;
        }

        document.addEventListener('DOMContentLoaded', function() {
            const userReserveListJsonString = document.getElementById('userReserveListJsonData').value;
            const allReservations = JSON.parse(userReserveListJsonString || '[]');
            let filteredReservations = allReservations;

            const tableBody = document.querySelector('.data-table tbody');
            const pagination = document.getElementById('pagination');
            const searchForm = document.getElementById('searchForm');
            const searchTermInput = document.getElementById('searchTerm');
            const placeTypeFilter = document.getElementById('placeTypeFilter');

            const rowsPerPage = 10;
            let currentPage = 1;

            function displayTable(page, reservations) {
                tableBody.innerHTML = '';
                page = page < 1 ? 1 : page;
                const start = (page - 1) * rowsPerPage;
                const end = start + rowsPerPage;
                const paginatedItems = reservations.slice(start, end);

                if (paginatedItems.length > 0) {
                    paginatedItems.forEach(function(reserve) {
                        const row = tableBody.insertRow();
                        row.insertCell().innerText = reserve.reserveId;
                        row.insertCell().innerText = reserve.userName;
                        row.insertCell().innerText = reserve.placeName;
                        row.insertCell().innerText = reserve.reserveReason;
                        row.insertCell().innerText = formatLocalDateTime(reserve.createAt, 'yyyy-MM-dd');
                        row.insertCell().innerText = formatLocalDateTime(reserve.startAt, 'yyyy-MM-dd HH:mm');
                        row.insertCell().innerText = formatLocalDateTime(reserve.endAt, 'yyyy-MM-dd HH:mm');
                        row.insertCell().innerText = reserve.headcount;
                        row.insertCell().innerText = reserve.cancelYn === 'Y' ? '예약취소' : '예약중';
                    });
                } else {
                    const row = tableBody.insertRow();
                    const cell = row.insertCell();
                    cell.colSpan = 9;
                    cell.className = 'text-center';
                    cell.innerText = '예약 내역이 없습니다.';
                }
            }

            function setupPagination(totalPages, reservations) {
                pagination.innerHTML = '';

                if (totalPages <= 1) return;

                // 페이지네이션 로직은 이전과 동일하게 유지
                // ... (생략) ...

                const maxPagesToShow = 5;
                let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
                let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

                if (endPage - startPage + 1 < maxPagesToShow) {
                    startPage = Math.max(1, endPage - maxPagesToShow + 1);
                }

                // Previous button
                const prevBtn = document.createElement('a');
	            prevBtn.href = '#';
	            prevBtn.innerHTML = '&laquo;';
	            prevBtn.className = currentPage === 1 ? 'disabled' : '';
	            prevBtn.addEventListener('click', function(e) {
	                e.preventDefault();
	                if (currentPage > 1) {
	                    currentPage--;
	                    displayTable(currentPage, reservations);
	                    setupPagination(totalPages, reservations);
	                }
	            });
	            pagination.appendChild(prevBtn);

                // Page numbers
                for (let i = startPage; i <= endPage; i++) {
	                const pageBtn = document.createElement('a');
	                pageBtn.href = '#';
	                pageBtn.innerText = i;
	                pageBtn.className = currentPage === i ? 'active' : '';
	                pageBtn.addEventListener('click', function(e) {
	                    e.preventDefault();
	                    currentPage = i;
	                    displayTable(currentPage, reservations);
	                    setupPagination(totalPages, reservations);
	                });
	                pagination.appendChild(pageBtn);
	            }

                // Next button
                const nextBtn = document.createElement('a');
                nextBtn.href = '#';
                nextBtn.innerHTML = '&raquo;';
                nextBtn.className = currentPage === totalPages ? 'disabled' : '';
                nextBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    if (currentPage < totalPages) {
                        currentPage++;
                        displayTable(currentPage, reservations);
                        setupPagination(totalPages, reservations);
                    }
                });
                pagination.appendChild(nextBtn);
            }

            let myChart;

            function updateChart(reservations) {
                const reserveData = {};
                reservations.forEach(function(reserve) {
                    if (reserveData[reserve.placeName]) {
                        reserveData[reserve.placeName]++;
                    } else {
                        reserveData[reserve.placeName] = 1;
                    }
                });
                const placeNames = Object.keys(reserveData);
                const reservationCounts = Object.values(reserveData);

                const ctx = document.getElementById('reservationChart').getContext('2d');
                if (myChart) {
                    myChart.destroy();
                }
                myChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: placeNames,
                        datasets: [{
                            label: '시설별 예약 건수',
                            data: reservationCounts,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.6)',
                                'rgba(54, 162, 235, 0.6)',
                                'rgba(255, 206, 86, 0.6)',
                                'rgba(75, 192, 192, 0.6)',
                                'rgba(153, 102, 255, 0.6)',
                                'rgba(255, 159, 64, 0.6)'
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        // 높이 100%를 채우기 위해 maintainAspectRatio: false 설정 유지
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: '예약 건수'
                                }
                            },
                            x: {
                                title: {
                                    display: true,
                                    text: '시설명'
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            },
                            title: {
                                display: false
                            }
                        }
                    }
                });
            }

            function updateTotalHeadcount(reservations) {
                let totalHeadcount = 0;
                reservations.forEach(function(reserve) {
                    totalHeadcount += reserve.headcount;
                });
                document.getElementById('totalHeadcount').innerText = totalHeadcount;
            }

            function filterAndDisplay() {
                const searchTerm = searchTermInput.value.toLowerCase();
                const placeType = placeTypeFilter.value;

                filteredReservations = allReservations.filter(function(reserve) {
                    const matchesSearchTerm = searchTerm === '' ||
                        reserve.placeName.toLowerCase().includes(searchTerm) ||
                        reserve.userName.toLowerCase().includes(searchTerm);

                    const matchesPlaceType = placeType === '' ||
                        (placeType === '스터디룸' && reserve.placeName.includes('스터디룸')) ||
                        (placeType === '세미나실' && reserve.placeName.includes('세미나실')) ||
                        (placeType === '강의실' && reserve.placeName.includes('강의실'));

                    return matchesSearchTerm && matchesPlaceType;
                });

                currentPage = 1;
                const totalPages = Math.ceil(filteredReservations.length / rowsPerPage);
                displayTable(currentPage, filteredReservations);
                setupPagination(totalPages, filteredReservations);
                updateChart(filteredReservations);
                updateTotalHeadcount(filteredReservations);
            }

            searchForm.addEventListener('submit', function(e) {
                e.preventDefault();
                filterAndDisplay();
            });

            placeTypeFilter.addEventListener('change', filterAndDisplay);

            // Initial display
            filterAndDisplay();

            // 윈도우 크기 변경 시 차트 크기 조정을 위해 차트 업데이트 함수를 다시 호출할 수 있음
            window.addEventListener('resize', function() {
                if (myChart) {
                    myChart.resize();
                }
            });
        });
    </script>
</body>
</html>