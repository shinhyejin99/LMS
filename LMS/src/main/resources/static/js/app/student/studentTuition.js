/**
 * 학생 등록금 및 장학금 통합 페이지 JS
 */

let currentYeartermCd = null;

/**
 * 페이지 로드 시 초기화
 */
document.addEventListener('DOMContentLoaded', function() {
    const tbody = document.getElementById('paymentTableBody');
    if (tbody) {
        loadPaymentHistory(); // 납부 내역 조회
    }
});

/**
 * 납부 내역 조회 (맨 위에 나오는 테이블 영역)
 */
function loadPaymentHistory() {
    fetch('/lms/student/tuition/rest/payment-history')
        .then(response => response.json())
        .then(data => {
            renderPaymentTable(data);
        })
        .catch(error => {
            console.error('납부 내역 조회 실패:', error);
            showError('납부 내역을 불러오는데 실패했습니다.');
        });
}

/**
 * 납부 내역 테이블 렌더링
 */
function renderPaymentTable(data) {
    const tbody = document.getElementById('paymentTableBody');

    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">납부 내역이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = data.map(item => `
        <tr class="payment-row"
            data-yearterm="${item.yeartermCd}"
            data-semester-name="${item.semesterName}">
            <td><strong>${item.semesterName}</strong></td>
            <td>${formatNumber(item.tuitionSum)}원</td>
            <td>${formatNumber(item.scholarshipSum)}원</td>
            <td><strong>${formatNumber(item.finalAmount)}원</strong></td>
            <td>${item.payDate || '-'}</td>
            <td>
                <span class="badge ${item.payDoneYn === 'Y' ? 'bg-success' : 'bg-warning'}">
                    ${item.payDoneYn === 'Y' ? '완료' : '미납'}
                </span>
            </td>
            <td>
                ${item.payDoneYn === 'Y'
                    // 버튼에 이벤트 식별 클래스 추가 (action-receipt)
                    ? `<button class="btn btn-sm btn-outline-primary action-receipt" data-yearterm="${item.yeartermCd}">납부확인서</button>`
                    // 버튼에 이벤트 식별 클래스 추가 (action-notice)
                    : `<button class="btn btn-sm btn-primary action-notice" data-yearterm="${item.yeartermCd}">고지서 확인</button>`}
            </td>
        </tr>
    `).join('');

    // 테이블 렌더링 후 이벤트 등록
    attachTableEventListeners();

    // 첫 번째 데이터 자동 로드
    if (data.length > 0) {
        // 초기 메시지 숨기기
        const initialMessage = document.getElementById('initialMessage');
        if (initialMessage) {
            initialMessage.style.display = 'none';
        }

        // 장학금 컨테이너 표시
        const scholarshipContainer = document.getElementById('scholarshipDetailContainer');
        if (scholarshipContainer) {
            scholarshipContainer.classList.add('show');
        }

        // 첫 번째 행에 selected 클래스 추가
        const firstRow = tbody.querySelector('tr');
        if (firstRow) {
            firstRow.classList.add('selected');
        }

        // 첫 번째 학기의 장학금 데이터 로드
        currentYeartermCd  = data[0].yeartermCd;
        loadScholarshipDetail(data[0].yeartermCd, data[0].semesterName);
    }
}

/**
 * 테이블 행과 버튼에 이벤트 등록
 */
function attachTableEventListeners() {
    // 1. 납부 내역 행 클릭 이벤트
    const tableBody = document.getElementById('paymentTableBody');
    if (tableBody) {
        tableBody.addEventListener('click', function(event) {
            let rowElement = event.target.closest('.payment-row');
            if (!rowElement) return; // .payment-row 외부 클릭이면 무시

            const yeartermCd = rowElement.dataset.yearterm;
            const semesterName = rowElement.dataset.semesterName;

            // 클릭된 요소가 버튼(.action-notice, .action-receipt)이면 행 이벤트 무시
            if (event.target.closest('.action-notice, .action-receipt')) {
                return;
            }

            // 행 클릭 이벤트 처리
            toggleScholarshipDetail(yeartermCd, semesterName, rowElement);
        });
    }

    // 2. 고지서 확인 버튼 이벤트
    document.querySelectorAll('.action-notice').forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation(); // 행 클릭 이벤트 방지
            const yeartermCd = this.dataset.yearterm;
            viewNotice(yeartermCd);
        });
    });

    // 3. 납부확인서 버튼 이벤트
    document.querySelectorAll('.action-receipt').forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation(); // 행 클릭 이벤트 방지
            const yeartermCd = this.dataset.yearterm;
            viewReceipt(yeartermCd);
        });
    });
}

/**
 * 장학금 상세 토글
 */
function toggleScholarshipDetail(yeartermCd, semesterName, rowElement) {
    // 이전 선택 해제
    document.querySelectorAll('.tuition-history-table tbody tr').forEach(tr => {
        tr.classList.remove('selected');
    });

    // 현재 행 선택
    rowElement.classList.add('selected');
    currentYeartermCd = yeartermCd;

    // 장학금 상세 조회
    loadScholarshipDetail(yeartermCd, semesterName);
}

/**
 * 장학금 상세 조회
 */
function loadScholarshipDetail(yeartermCd, semesterName) {
    fetch(`/lms/student/tuition/rest/scholarship-detail?yeartermCd=${yeartermCd}`)
        .then(response => response.json())
        .then(data => {
            renderScholarshipDetail(data, semesterName);
        })
        .catch(error => {
            console.error('장학금 상세 조회 실패:', error);
            showError('장학금 정보를 불러오는데 실패했습니다.');
        });
}

/**
* 장학금 상세 렌더링
*/
function renderScholarshipDetail(data, semesterName) {
    // 초기 메시지 숨기기
    document.getElementById('initialMessage').style.display = 'none';

    // 상세 컨테이너 표시
    const container = document.getElementById('scholarshipDetailContainer');
    container.classList.add('show');

    // 제목 업데이트
    document.getElementById('scholarshipTitle').textContent = `${semesterName} 장학금 수혜 상세 내역`;

    // 총 수혜 금액
    document.getElementById('totalScholarshipAmount').textContent = formatNumber(data.totalAmount) + '원';

    // 차트 영역 먼저 비우기
    document.getElementById('donutChart').innerHTML = '';
    document.getElementById('chartLegend').innerHTML = '';
    document.getElementById('scholarshipDetailTable').innerHTML = '';

    setTimeout(() => {
        // 도넛 차트
        if (data.distribution && data.distribution.length > 0) {
            createDonutChart(data.distribution);
        } else {
            document.getElementById('donutChart').innerHTML = '<div class="no-data">장학금 데이터가 없습니다.</div>';
            document.getElementById('chartLegend').innerHTML = '';
        }

        // 상세 테이블
        renderScholarshipTable(data.details);
    }, 100);
}

/**
 * 장학금 상세 테이블 렌더링
 */
function renderScholarshipTable(details) {
    const tbody = document.getElementById('scholarshipDetailTable');

    if (!details || details.length === 0) {
        tbody.innerHTML = '<tr><td colspan="2" class="text-center">장학금 내역이 없습니다.</td></tr>';
        return;
    }

    tbody.innerHTML = details.map(item => `
        <tr>
            <td>${item.itemName}</td>
            <td class="text-end"><strong>${formatNumber(item.amount)}원</strong></td>
        </tr>
    `).join('');
}

/**
 * 도넛 차트 생성 (D3.js + 애니메이션)
 */
function createDonutChart(data) {
    // 차트 영역 초기화
    d3.select("#donutChart").html("");

    // 기존 툴팁 제거 (중복 방지)
    d3.selectAll(".d3-tooltip").remove();

    if (!data || data.length === 0) {
        d3.select("#donutChart").html('<div class="no-data">장학금 데이터가 없습니다.</div>');
        d3.select("#chartLegend").html("");
        return;
    }

    // 차트 영역의 실제 너비를 가져와 사용 => 수정해야할 듯
    const width = 300;
    const height = 300;
    const radius = Math.min(width, height) / 2 - 10;

    // SVG 생성
    const svg = d3.select("#donutChart")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("viewBox", `0 0 ${width} ${height}`)
        .attr("preserveAspectRatio", "xMidYMid meet")
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`); // 중심으로 이동

    // 색상 스케일
    const color = d3.scaleOrdinal()
        .domain(data.map(d => d.scholarshipName))
        .range(["#4e73df", "#1cc88a", "#36b9cc", "#f6c23e", "#e74a3b"]);

    // 파이 레이아웃
    const pie = d3.pie()
        .value(d => d.totalAmount)
        .sort(null)
        .padAngle(0.02); // 조각 사이 간격

    // 아크 생성기 (기본)
    const arc = d3.arc()
        .innerRadius(radius * 0.6)
        .outerRadius(radius);

    // 아크 생성기 (호버 효과용)
    const arcHover = d3.arc()
        .innerRadius(radius * 0.6)
        .outerRadius(radius * 1.08);

    // 툴팁 생성
    const tooltip = d3.select("body")
        .append("div")
        .attr("class", "d3-tooltip")
        .style("opacity", 0);

    // 총 금액과 건수 계산
    const totalAmount = d3.sum(data, d => d.totalAmount);
    const totalCount = d3.sum(data, d => d.count);

    // 도넛 차트 그리기
    const arcs = svg.selectAll("arc")
        .data(pie(data))
        .enter()
        .append("g")
        .attr("class", "arc");

    // 파이 조각 그리기 (애니메이션)
    arcs.append("path")
        .attr("fill", d => color(d.data.scholarshipName))
        .attr("stroke", "white")
        .attr("stroke-width", 3)
        .style("cursor", "pointer")
        .on("mouseover", function(event, d) {
            // 호버 효과: 크기 증가
            d3.select(this)
                .transition()
                .duration(200)
                .attr("d", arcHover);

            // 툴팁 표시
            const percentage = ((d.data.totalAmount / totalAmount) * 100).toFixed(1);
            tooltip.transition()
                .duration(200)
                .style("opacity", 1);
            tooltip.html(`
                <strong>${d.data.scholarshipName}</strong><br>
                금액: ${formatNumber(d.data.totalAmount)}원<br>
                비율: ${percentage}%<br>
                수혜 건수: ${d.data.count}건
            `)
                .style("left", (event.pageX + 10) + "px")
                .style("top", (event.pageY - 10) + "px");

            // 범례 하이라이트
            highlightLegend(d.data.scholarshipName);
        })
        .on("mousemove", function(event) {
            tooltip
                .style("left", (event.pageX + 10) + "px")
                .style("top", (event.pageY - 10) + "px");
        })
        .on("mouseout", function(event, d) {
            // 원래 크기로
            d3.select(this)
                .transition()
                .duration(200)
                .attr("d", arc);

            // 툴팁 숨기기
            tooltip.transition()
                .duration(200)
                .style("opacity", 0);

            // 범례 하이라이트 제거
            removeLegendHighlight();
        })
        // 애니메이션: 0부터 시작해서 최종 각도까지
        .transition()
        .duration(1000)
        .attrTween("d", function(d) {
            const interpolate = d3.interpolate({ startAngle: 0, endAngle: 0 }, d);
            return function(t) {
                return arc(interpolate(t));
            };
        });

    // 중앙 텍스트 (수혜 건수)
    const centerText = svg.append("g")
        .attr("class", "center-text");

    centerText.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "-0.5em")
        .style("font-size", "16px")
        .style("fill", "#666")
        .text("수혜 건수");

    centerText.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "1em")
        .style("font-size", "30px")
        .style("font-weight", "bold")
        .style("fill", "#4e73df")
        // 애니메이션: 숫자 카운트 업
        .transition()
        .duration(1000)
        .tween("text", function() {
            const interpolate = d3.interpolate(0, totalCount);
            return function(t) {
                this.textContent = Math.round(interpolate(t)) + "건";
            };
        });

    // 범례 생성
    createLegend(data, color, totalAmount);
}

/**
 * 범례 생성
 */
function createLegend(data, colorScale, totalAmount) {
    const legend = d3.select("#chartLegend");
    legend.html("");

    data.forEach(d => {
        const percentage = ((d.totalAmount / totalAmount) * 100).toFixed(1);

        const legendItem = legend.append("div")
            .attr("class", "legend-item")
            .attr("data-name", d.scholarshipName)
            .style("opacity", 0)
            .on("mouseover", function() {
                d3.select(this).style("background-color", "#f5f5f5");
            })
            .on("mouseout", function() {
                d3.select(this).style("background-color", "transparent");
            });

        legendItem.append("div")
            .attr("class", "legend-color")
            .style("background-color", colorScale(d.scholarshipName));

        legendItem.append("div")
            .attr("class", "legend-text")
            .html(`
                <strong>${d.scholarshipName}</strong><br>
                ${formatNumber(d.totalAmount)}원 (${percentage}%)<br>
                <small class="text-muted">${d.count}건</small>
            `);

        // 애니메이션: 순차적으로 나타남
        legendItem.transition()
            .delay((data.indexOf(d)) * 100)
            .duration(300)
            .style("opacity", 1);
    });
}

/**
 * 범례 하이라이트
 */
function highlightLegend(scholarshipName) {
    d3.selectAll(".legend-item")
        .style("opacity", function() {
            return d3.select(this).attr("data-name") === scholarshipName ? 1 : 0.3;
        })
        .style("background-color", function() {
            return d3.select(this).attr("data-name") === scholarshipName ? "#e3f2fd" : "transparent";
        });
}

/**
 * 범례 하이라이트 제거
 */
function removeLegendHighlight() {
    d3.selectAll(".legend-item")
        .style("opacity", 1)
        .style("background-color", "transparent");
}

/**
 * 페이지 언로드 시 툴팁 제거
 */
window.addEventListener('beforeunload', function() {
    d3.selectAll(".d3-tooltip").remove();
});

/**
 * 고지서 확인 (미납)
 */
function viewNotice(yeartermCd) {
    const previewUrl = `/lms/student/tuition/rest/notice/view?yeartermCd=${yeartermCd}`;

	// 모달을 열 때 currentYeartermCd 업데이트
    currentYeartermCd = yeartermCd;

    // 모달 제목 및 다운로드 타입 설정
    document.getElementById('modalTitle').textContent = '등록금 납부 고지서';
    document.getElementById('downloadPdfButton').dataset.documentType = 'notice';

    document.getElementById('pdfPreview').src = previewUrl;
    document.getElementById('tuitionModal').style.display = 'block';
    document.body.style.overflow = 'hidden';
}

/**
 * 납부확인서 (납부 완)
 */
function viewReceipt(yeartermCd) {
    const previewUrl = `/lms/student/tuition/rest/receipt/view?yeartermCd=${yeartermCd}`;

	// 모달을 열 때 currentYeartermCd 업데이트
    currentYeartermCd = yeartermCd;

    // 모달 제목 및 다운로드 타입 설정
    document.getElementById('modalTitle').textContent = '등록금 납부 확인서';
    document.getElementById('downloadPdfButton').dataset.documentType = 'receipt';

    document.getElementById('pdfPreview').src = previewUrl;
    document.getElementById('tuitionModal').style.display = 'block';
    document.body.style.overflow = 'hidden';
}

/**
 * 모달 닫기
 */
function closeTuitionModal() {
    document.getElementById('tuitionModal').style.display = 'none';
    document.getElementById('pdfPreview').src = '';
    document.body.style.overflow = 'auto';
}

/**
 * PDF 다운로드 (고지서 또는 납부확인서)
 */
function downloadTuitionPdf() {
	const downloadButton = document.getElementById('downloadPdfButton');
    const documentType = downloadButton.dataset.documentType; // 'notice' 또는 'receipt'

    // 현재 선택된 학기 코드가 없으면 기본값으로 처리 => 수정
    const yeartermCd = currentYeartermCd || '2025_REG2';

    let downloadUrl;

    if (documentType === 'receipt') {
        downloadUrl = `/lms/student/tuition/rest/receipt/download?yeartermCd=${yeartermCd}`;
    } else if (documentType === 'notice') {
        downloadUrl = `/lms/student/tuition/rest/notice/download?yeartermCd=${yeartermCd}`;
    } else {
        console.error('다운로드할 문서 유형이 정의되지 않았습니다.');
    }
    window.location.href = downloadUrl;
}

/**
 * 모달 외부 클릭 시 닫기
 */
document.addEventListener('click', function(e) {
    const modal = document.getElementById('tuitionModal');
    if (modal && e.target === modal) {
        closeTuitionModal();
    }
});

/**
 * ESC 키로 모달 닫기
 */
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeTuitionModal();
    }
});

/**
 * 숫자 포맷팅
 */
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    // 문자열로 변환하여 정규식을 사용해 쉼표 추가
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * 에러 표시
 */
function showError(message) {
    alert(message);
}