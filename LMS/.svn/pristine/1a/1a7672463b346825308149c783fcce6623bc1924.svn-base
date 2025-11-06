/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 24.     	김수현            최초 생성
 *
 * </pre>
 */
/**
 * 전체 학기 성적 차트
 * D3.js를 사용한 라인 차트
 * @author 김수현
 * @since 2025. 10. 24.
 */

// 차트 색상 설정
const GRADE_CHART_COLORS = {
    line: '#696cff',           // 라인 색상
    area: 'rgba(105, 108, 255, 0.1)', // 영역 색상
    point: '#696cff',          // 포인트 색상
    pointHover: '#5f43b2',     // 포인트 호버 색상
    grid: '#e0e0e0',           // 그리드 색상
    text: '#666',              // 텍스트 색상
    textDark: '#333'           // 진한 텍스트 색상
};

// 애니메이션 설정
const GRADE_ANIMATION_CONFIG = {
    duration: 1000,
    delay: 50,
    ease: d3.easeCubicOut
};

class GradeChart {
    constructor(containerId) {
        this.containerId = containerId;
        this.container = d3.select(`#${containerId}`);
        this.data = null;
        this.svg = null;
        this.width = 0;
        this.height = 0;
        this.margin = { top: 20, right: 30, bottom: 80, left: 50 };

        console.log('[GradeChart] 생성자 호출');
    }

    /**
     * 데이터 로드
     */
    async loadData() {
        try {
            console.log('[GradeChart] 데이터 로드 시작');
            const response = await fetch('/rest/student/semester-grades');

            if (!response.ok) {
                throw new Error(`HTTP ${response.status} 오류 발생`);
            }

            this.data = await response.json();
            console.log('[GradeChart] 받은 데이터:', this.data);

            if (!this.data || this.data.length === 0) {
                this.showMessage('아직 성적 데이터가 없습니다.');
                return false;
            }

            return true;
        } catch (error) {
            console.error('[GradeChart] 데이터 로드 실패:', error);
            this.showError('성적 데이터를 불러올 수 없습니다.');
            return false;
        }
    }

    /**
     * 차트 렌더링
     */
    async render() {
        console.log('[GradeChart] 렌더링 시작');

        if (this.container.empty()) {
            console.error('[GradeChart] 컨테이너를 찾을 수 없습니다');
            return;
        }

        const loaded = await this.loadData();
        if (!loaded) return;

        // 컨테이너 초기화
        this.container.html('');

        // 크기 설정
        const containerNode = this.container.node();
        const containerWidth = containerNode.getBoundingClientRect().width;
        const containerHeight = containerNode.getBoundingClientRect().height; // ✅ 추가

        this.width = containerWidth - this.margin.left - this.margin.right;
        this.height = containerHeight - this.margin.top - this.margin.bottom; // ✅ 300 대신 컨테이너 높이 사용

        // SVG 생성
        this.svg = this.container
            .append('svg')
		    .attr('width', '100%')                           // ✅ containerWidth 대신 100%
		    .attr('height', '100%')                          // ✅ 300 대신 100%
		    .attr('viewBox', `0 0 ${containerWidth} ${containerHeight}`)   // ✅ viewBox 추가
		    .attr('preserveAspectRatio', 'xMidYMid meet')   // ✅ 추가
		    .style('max-width', '100%')                      // ✅ 추가
		    .style('max-height', '100%')                     // ✅ 추가
		    .append('g')
		    .attr('transform', `translate(${this.margin.left}, ${this.margin.top})`);

        // 차트 그리기
        this.drawChart();

        console.log('[GradeChart] 렌더링 완료');
    }

    /**
     * 차트 그리기
     */
    drawChart() {
        // X축 스케일 (학기)
        const xScale = d3.scaleBand()
            .domain(this.data.map(d => d.yeartermName))
            .range([0, this.width])
            .padding(0.3);

        // Y축 스케일 (평점: 0 ~ 4.5)
        const yScale = d3.scaleLinear()
            .domain([0, 4.5])
            .range([this.height, 0])
            .nice();

        // 그리드 라인
        this.svg.append('g')
            .attr('class', 'grid')
            .selectAll('line')
            .data(yScale.ticks(5))
            .enter()
            .append('line')
            .attr('x1', 0)
            .attr('x2', this.width)
            .attr('y1', d => yScale(d))
            .attr('y2', d => yScale(d))
            .attr('stroke', GRADE_CHART_COLORS.grid)
            .attr('stroke-dasharray', '3,3')
            .attr('opacity', 0.5);

        // 라인 생성기
        const line = d3.line()
            .x(d => xScale(d.yeartermName) + xScale.bandwidth() / 2)
            .y(d => yScale(d.avgGrade))
            .curve(d3.curveMonotoneX);

        // 영역 생성기
        const area = d3.area()
            .x(d => xScale(d.yeartermName) + xScale.bandwidth() / 2)
            .y0(this.height)
            .y1(d => yScale(d.avgGrade))
            .curve(d3.curveMonotoneX);

        // 영역 그리기
        this.svg.append('path')
            .datum(this.data)
            .attr('fill', GRADE_CHART_COLORS.area)
            .attr('d', area)
            .style('opacity', 0)
            .transition()
            .duration(GRADE_ANIMATION_CONFIG.duration)
            .style('opacity', 1);

        // 라인 그리기
        const path = this.svg.append('path')
            .datum(this.data)
            .attr('fill', 'none')
            .attr('stroke', GRADE_CHART_COLORS.line)
            .attr('stroke-width', 3)
            .attr('d', line);

        // 라인 애니메이션
        const totalLength = path.node().getTotalLength();
        path
            .attr('stroke-dasharray', totalLength + ' ' + totalLength)
            .attr('stroke-dashoffset', totalLength)
            .transition()
            .duration(GRADE_ANIMATION_CONFIG.duration)
            .ease(GRADE_ANIMATION_CONFIG.ease)
            .attr('stroke-dashoffset', 0);

        // 포인트 그리기
        const points = this.svg.selectAll('.point')
            .data(this.data)
            .enter()
            .append('circle')
            .attr('class', 'point')
            .attr('cx', d => xScale(d.yeartermName) + xScale.bandwidth() / 2)
            .attr('cy', d => yScale(d.avgGrade))
            .attr('r', 0)
            .attr('fill', GRADE_CHART_COLORS.point)
            .attr('stroke', '#fff')
            .attr('stroke-width', 2)
            .style('cursor', 'pointer');

        // 포인트 애니메이션
        points
            .transition()
            .duration(GRADE_ANIMATION_CONFIG.duration)
            .delay((d, i) => i * GRADE_ANIMATION_CONFIG.delay)
            .attr('r', 5);

        // 포인트 호버 효과
        points
            .on('mouseover', (event, d) => {
                d3.select(event.currentTarget)
                    .transition()
                    .duration(200)
                    .attr('r', 8)
                    .attr('fill', GRADE_CHART_COLORS.pointHover);

                this.showTooltip(event, d);
            })
            .on('mouseout', (event, d) => {
                d3.select(event.currentTarget)
                    .transition()
                    .duration(200)
                    .attr('r', 5)
                    .attr('fill', GRADE_CHART_COLORS.point);

                this.hideTooltip();
            });

        // X축
        this.svg.append('g')
            .attr('transform', `translate(0, ${this.height})`)
	        .call(d3.axisBottom(xScale))
	        .selectAll('text')
	        .attr('transform', 'rotate(-45)') // 각도 조정
	        .style('text-anchor', 'end')
	        .attr('dx', '-0.5em')  // ✅ 추가: 가로 위치 조정
	        .attr('dy', '0.5em')   // ✅ 추가: 세로 위치 조정
	        .style('font-size', '11px') // ✅ 12px → 11px (약간 작게)
	        .style('fill', GRADE_CHART_COLORS.text);

        // Y축
        this.svg.append('g')
            .call(d3.axisLeft(yScale).ticks(5))
            .selectAll('text')
            .style('font-size', '12px')
            .style('fill', GRADE_CHART_COLORS.text);

        // Y축 레이블
        this.svg.append('text')
            .attr('transform', 'rotate(-90)')
            .attr('y', -40)
            .attr('x', -this.height / 2)
            .attr('text-anchor', 'middle')
            .style('font-size', '13px')
            .style('fill', GRADE_CHART_COLORS.textDark)
            .style('font-weight', '600')
            .text('평점');
    }

    /**
     * 툴팁 표시
     */
    showTooltip(event, d) {
        const tooltip = this.container
            .append('div')
            .attr('class', 'grade-tooltip')
            .style('position', 'absolute')
            .style('background', 'rgba(0, 0, 0, 0.8)')
            .style('color', '#fff')
            .style('padding', '8px 12px')
            .style('border-radius', '4px')
            .style('font-size', '13px')
            .style('pointer-events', 'none')
            .style('z-index', '1000')
            .html(`
                <div><strong>${d.yeartermName}</strong></div>
                <div>평균 평점: <strong>${d.avgGrade.toFixed(2)}</strong></div>
                <div>수강 과목: ${d.subjectCount}개</div>
            `);

        const containerRect = this.container.node().getBoundingClientRect();
        const tooltipRect = tooltip.node().getBoundingClientRect();

        tooltip
            .style('left', `${event.pageX - containerRect.left - tooltipRect.width / 2}px`)
            .style('top', `${event.pageY - containerRect.top - tooltipRect.height - 10}px`);
    }

    /**
     * 툴팁 숨기기
     */
    hideTooltip() {
        this.container.selectAll('.grade-tooltip').remove();
    }

    /**
     * 메시지 표시
     */
    showMessage(message) {
        this.container.html('')
            .append('div')
            .attr('class', 'chart-message')
            .style('text-align', 'center')
            .style('padding', '60px 20px')
            .style('color', '#999')
            .style('font-size', '14px')
            .text(message);
    }

    /**
     * 에러 메시지 표시
     */
    showError(message) {
        this.container.html('')
            .append('div')
            .attr('class', 'chart-error')
            .style('text-align', 'center')
            .style('padding', '60px 20px')
            .style('color', '#E74C3C')
            .html(`
                <div style="font-size: 16px; margin-bottom: 8px;">⚠️</div>
                <div style="font-size: 14px;">${message}</div>
            `);
    }

    /**
     * 리사이즈
     */
    resize() {
        if (this.data) {
            this.render();
        }
    }
}