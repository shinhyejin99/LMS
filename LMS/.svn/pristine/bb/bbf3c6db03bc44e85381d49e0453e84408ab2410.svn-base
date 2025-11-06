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
 * 졸업 이수 현황 차트
 * D3.js를 사용한 애니메이션 도넛 차트
 */

// 차트 색상 설정 (여기서 자유롭게 변경 가능!)
const CHART_COLORS = {
    completed: '#4CAF50',      // 이수 학점 색상
    remaining: '#E0E0E0',      // 미이수 학점 색상
    textPrimary: '#2C3E50',    // 주요 텍스트 색상
    textSecondary: '#7F8C8D',  // 보조 텍스트 색상
    background: '#FFFFFF'       // 배경 색상
};

// 애니메이션 설정
const ANIMATION_CONFIG = {
    duration: 1500,            // 애니메이션 지속 시간
    delay: 100,                // 시작 지연 시간
    ease: d3.easeCubicOut      // 이징 함수
};

class GraduationChart {
    constructor(containerId) {
        this.containerId = containerId;
        this.container = d3.select(`#${containerId}`);
        this.data = null;
        this.svg = null;
        this.width = 0;
        this.height = 0;
    }

    /**
     * 데이터 로드
     */
    async loadData() {
        try {
            const response = await fetch('/rest/student/graduation-status');
            if (!response.ok) {
                throw new Error('데이터를 불러오는데 실패했습니다.');
            }
            const studentData = await response.json();

            // StudentDetailDTO에서 필요한 데이터 추출
            this.data = {
                totalRequired: studentData.totalRequiredCredits,
                completed: studentData.completedCredits,
                remaining: studentData.totalRequiredCredits - studentData.completedCredits,
                percentage: Math.round((studentData.completedCredits / studentData.totalRequiredCredits) * 1000) / 10
            };

            return true;
        } catch (error) {
            console.error('Error loading graduation data:', error);
            this.showError('졸업 이수 현황을 불러올 수 없습니다.');
            return false;
        }
    }

    /**
     * 차트 초기화 및 렌더링
     */
    async render() {
        const loaded = await this.loadData();
        if (!loaded) return;

        // 컨테이너 초기화
        this.container.html('');

        // 반응형 크기 설정
        const containerNode = this.container.node();
	    const containerRect = containerNode.getBoundingClientRect();
	    const containerWidth = containerRect.width;
	    const containerHeight = containerRect.height;

	    this.width = Math.min(containerWidth, 220); // 300 → 250 (차트 크기 줄임)
	    this.height = this.width;

        // SVG 생성
        this.svg = this.container
            .append('svg')
	        .attr('width', '100%')
	        .attr('height', this.height) // 고정 높이
	        .attr('viewBox', `0 0 ${this.width} ${this.height}`)
	        .attr('preserveAspectRatio', 'xMidYMid meet')
	        .style('max-width', '100%')
	        .style('max-height', `${this.height}px`) // 최대 높이 제한
	        .style('display', 'block')
	        .style('margin', '0 auto'); // 중앙 정렬

        // 차트 그룹
        const chartGroup = this.svg
            .append('g')
            .attr('transform', `translate(${this.width / 2}, ${this.height / 2})`);

        // 도넛 차트 그리기
        this.drawDonutChart(chartGroup);

        // 중앙 텍스트 그리기
        this.drawCenterText(chartGroup);

        // 학점 정보 표시
        this.drawCreditInfo();
    }

    /**
     * 도넛 차트 그리기
     */
    drawDonutChart(group) {
        const radius = Math.min(this.width, this.height) / 2 - 20;
        const thickness = radius * 0.3; // 도넛 두께

        // 파이 레이아웃 생성
        const pie = d3.pie()
            .value(d => d.value)
            .sort(null)
            .startAngle(-Math.PI / 2)
            .endAngle(Math.PI * 1.5);

        // 아크 생성기
        const arc = d3.arc()
            .innerRadius(radius - thickness)
            .outerRadius(radius);

        // 애니메이션용 아크
        const arcTween = function(d) {
            const interpolate = d3.interpolate(
                { startAngle: -Math.PI / 2, endAngle: -Math.PI / 2 },
                d
            );
            return function(t) {
                return arc(interpolate(t));
            };
        };

        // 데이터 준비
        const chartData = [
            { label: '이수', value: this.data.completed, color: CHART_COLORS.completed },
            { label: '미이수', value: this.data.remaining, color: CHART_COLORS.remaining }
        ];

        // 도넛 차트 그리기
        const paths = group
            .selectAll('path')
            .data(pie(chartData))
            .enter()
            .append('path')
            .attr('fill', d => d.data.color)
            .attr('stroke', CHART_COLORS.background)
            .attr('stroke-width', 2)
            .style('filter', 'drop-shadow(0px 2px 4px rgba(0,0,0,0.1))')
            .each(function(d) {
                this._current = { startAngle: -Math.PI / 2, endAngle: -Math.PI / 2 };
            });

        // 애니메이션 적용
        paths
            .transition()
            .duration(ANIMATION_CONFIG.duration)
            .delay(ANIMATION_CONFIG.delay)
            .ease(ANIMATION_CONFIG.ease)
            .attrTween('d', arcTween);

        // 호버 효과
        paths
            .on('mouseover', function(event, d) {
                d3.select(this)
                    .transition()
                    .duration(200)
                    .attr('transform', function(d) {
                        const [x, y] = arc.centroid(d);
                        return `translate(${x * 0.05}, ${y * 0.05})`;
                    })
                    .style('filter', 'drop-shadow(0px 4px 8px rgba(0,0,0,0.2))');
            })
            .on('mouseout', function(event, d) {
                d3.select(this)
                    .transition()
                    .duration(200)
                    .attr('transform', 'translate(0, 0)')
                    .style('filter', 'drop-shadow(0px 2px 4px rgba(0,0,0,0.1))');
            });
    }

    /**
     * 중앙 텍스트 그리기
     */
    drawCenterText(group) {
        const centerGroup = group.append('g').attr('class', 'center-text');

        // 퍼센트 텍스트 (0에서 시작해서 애니메이션)
        const percentText = centerGroup
            .append('text')
            .attr('text-anchor', 'middle')
            .attr('dy', '-0.2em')
            .style('font-size', `${this.width * 0.15}px`)
            .style('font-weight', 'bold')
            .style('fill', CHART_COLORS.textPrimary)
            .text('0%');

        // 퍼센트 애니메이션
        percentText
            .transition()
            .duration(ANIMATION_CONFIG.duration)
            .delay(ANIMATION_CONFIG.delay)
            .ease(ANIMATION_CONFIG.ease)
            .tween('text', () => {
                const interpolate = d3.interpolateNumber(0, this.data.percentage);
                return function(t) {
                    this.textContent = `${Math.round(interpolate(t))}%`;
                };
            });

        // 라벨 텍스트
        centerGroup
            .append('text')
            .attr('text-anchor', 'middle')
            .attr('dy', '1.2em')
            .style('font-size', `${this.width * 0.05}px`)
            .style('fill', CHART_COLORS.textSecondary)
            .text('이수율')
            .style('opacity', 0)
            .transition()
            .duration(500)
            .delay(ANIMATION_CONFIG.delay + ANIMATION_CONFIG.duration / 2)
            .style('opacity', 1);
    }

    /**
     * 학점 정보 표시
     */
    drawCreditInfo() {
        const infoContainer = this.container
            .append('div')
            .attr('class', 'graduation-chart-info')

            .style('opacity', 0);

        // 학점 정보
        const creditInfo = infoContainer
            .append('div')
            .attr('class', 'credit-info')
            .style('text-align', 'center');

        // 이수 학점 / 총 학점
        creditInfo
            .append('div')
            .style('font-size', '14px')
            .style('margin-bottom', '6px')
            .html(`
                <span style="color: ${CHART_COLORS.completed}; font-weight: bold; font-size: 24px;">
                    ${this.data.completed}
                </span>
                <span style="color: ${CHART_COLORS.textSecondary}; margin: 0 6px; font-size: 18px;">
                    /
                </span>
                <span style="color: ${CHART_COLORS.textPrimary}; font-weight: 600; font-size: 20px;">
                    ${this.data.totalRequired}
                </span>
                <span style="color: ${CHART_COLORS.textSecondary}; margin-left: 4px; font-size: 16px;">
                    학점
                </span>
            `);

        // 남은 학점
        creditInfo
            .append('div')
            .style('font-size', '12px')
            .style('color', CHART_COLORS.textSecondary)
            .html(`
                졸업까지
                <span style="color: ${CHART_COLORS.textPrimary}; font-weight: 600;">
                    ${this.data.remaining}학점
                </span>
                남았습니다
            `);

        // 페이드인 애니메이션
        infoContainer
            .transition()
            .duration(500)
            .delay(ANIMATION_CONFIG.delay + ANIMATION_CONFIG.duration)
            .style('opacity', 1);
    }

    /**
     * 에러 메시지 표시
     */
    showError(message) {
        this.container.html('')
            .append('div')
            .attr('class', 'chart-error')
            .style('text-align', 'center')
            .style('padding', '40px 20px')
            .style('color', '#E74C3C')
            .html(`
                <div style="font-size: 16px; margin-bottom: 8px;">⚠️</div>
                <div style="font-size: 14px;">${message}</div>
            `);
    }

    /**
     * 차트 리사이즈
     */
    resize() {
        if (this.data) {
            this.render();
        }
    }
}

// 페이지 로드 시 차트 초기화
