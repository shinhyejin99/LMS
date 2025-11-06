/**
 * 
 */

/**
 * 학생 장학금 조회 - D3.js 도넛 차트
 */

// 도넛 차트 생성
function createDonutChart(data) {
    // 차트 영역 초기화
    d3.select("#donutChart").html("");
    d3.select("#chartLegend").html("");
    
    if (!data || data.length === 0) {
        d3.select("#donutChart")
            .append("div")
            .attr("class", "no-data")
            .text("표시할 데이터가 없습니다.");
        return;
    }
    
    // 차트 설정
    const width = 400;
    const height = 400;
    const margin = 40;
    const radius = Math.min(width, height) / 2 - margin;
    
    // SVG 생성
    const svg = d3.select("#donutChart")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", `translate(${width / 2}, ${height / 2})`);
    
    // 색상 스케일
    const color = d3.scaleOrdinal()
        .domain(data.map(d => d.scholarshipName))
        .range(["#4e73df", "#1cc88a", "#36b9cc", "#f6c23e", "#e74a3b", "#858796", "#5a5c69"]);
    
    // 파이 레이아웃
    const pie = d3.pie()
        .value(d => d.totalAmount)
        .sort(null);
    
    // 아크 생성기
    const arc = d3.arc()
        .innerRadius(radius * 0.6)
        .outerRadius(radius);
    
    const arcHover = d3.arc()
        .innerRadius(radius * 0.6)
        .outerRadius(radius * 1.08);
    
    // 도넛 차트 그리기
    const arcs = svg.selectAll("arc")
        .data(pie(data))
        .enter()
        .append("g")
        .attr("class", "arc");
    
    arcs.append("path")
        .attr("d", arc)
        .attr("fill", d => color(d.data.scholarshipName))
        .attr("stroke", "white")
        .attr("stroke-width", 2)
        .on("mouseover", function(event, d) {
            d3.select(this)
                .transition()
                .duration(200)
                .attr("d", arcHover);
            
            // 툴팁 표시
            showTooltip(event, d.data);
        })
        .on("mouseout", function(event, d) {
            d3.select(this)
                .transition()
                .duration(200)
                .attr("d", arc);
            
            hideTooltip();
        });
    
    // 중앙 텍스트 (총 금액과 수혜 건수)
    const totalAmount = d3.sum(data, d => d.totalAmount);
    const totalCount = d3.sum(data, d => d.count);
    
    svg.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "-1.2em")
        .style("font-size", "14px")
        .style("fill", "#666")
        .text("수혜 건수");
    
    svg.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "0.2em")
        .style("font-size", "32px")
        .style("font-weight", "bold")
        .style("fill", "#4e73df")
        .text(totalCount + "건");
    
    svg.append("text")
        .attr("text-anchor", "middle")
        .attr("dy", "1.8em")
        .style("font-size", "12px")
        .style("fill", "#999")
        .text(formatNumber(totalAmount) + "원");
    
    // 범례 생성
    createLegend(data, color);
}

// 범례 생성
function createLegend(data, colorScale) {
    const legend = d3.select("#chartLegend")
        .append("div")
        .attr("class", "legend");
    
    data.forEach(d => {
        const legendItem = legend.append("div")
            .attr("class", "legend-item");
        
        legendItem.append("div")
            .attr("class", "legend-color")
            .style("background-color", colorScale(d.scholarshipName));
        
        const percentage = ((d.totalAmount / d3.sum(data, item => item.totalAmount)) * 100).toFixed(1);
        
        legendItem.append("div")
            .attr("class", "legend-text")
            .html(`
                <strong>${d.scholarshipName}</strong><br>
                ${formatNumber(d.totalAmount)}원 (${percentage}%)<br>
                <span style="color: #999; font-size: 12px;">${d.count}건</span>
            `);
    });
}

// 툴팁 표시
function showTooltip(event, data) {
    const tooltip = d3.select("body")
        .append("div")
        .attr("class", "tooltip")
        .style("position", "absolute")
        .style("left", (event.pageX + 10) + "px")
        .style("top", (event.pageY - 10) + "px");
    
    tooltip.html(`
        <strong>${data.scholarshipName}</strong><br>
        금액: ${formatNumber(data.totalAmount)}원<br>
        수혜 건수: ${data.count}건
    `);
}

// 툴팁 숨기기
function hideTooltip() {
    d3.selectAll(".tooltip").remove();
}

// 숫자 포맷팅
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 페이지 로드 시 차트 생성
document.addEventListener("DOMContentLoaded", function() {
    console.log("scholarshipDistribution:", scholarshipDistribution); // 디버깅용
    
    if (typeof scholarshipDistribution !== 'undefined' && scholarshipDistribution.length > 0) {
        createDonutChart(scholarshipDistribution);
    } else {
        console.log("장학금 데이터가 없습니다.");
        d3.select("#donutChart")
            .append("div")
            .attr("class", "no-data")
            .text("표시할 데이터가 없습니다.");
    }
});