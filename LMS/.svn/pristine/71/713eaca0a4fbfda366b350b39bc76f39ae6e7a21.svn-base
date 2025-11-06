<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>졸업요건 충족 현황</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentGraduation.css" />
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 20.     	 최건우            최초 생성
 *  2025. 10. 29.		김수현			외부래퍼, 차트 글자 설정, css 추가
 *
-->
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>

<!-- 외부 래퍼 -->
<div class="graduation-requirements-page">
    <div class="graduation-container">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>졸업요건 충족 현황</h1>
        </div>

        <!-- 졸업요건 충족 현황 차트 -->
        <div class="chart-grid-container">
            <!-- 총 이수 학점 차트 -->
            <div class="chart-wrapper">
                <canvas id="totalCreditsChart"></canvas>
            </div>
            <!-- 전공 필수 학점 차트 -->
            <div class="chart-wrapper">
                <canvas id="majorRequiredCreditsChart"></canvas>
            </div>
            <!-- 교양 필수 학점 차트 -->
            <div class="chart-wrapper">
                <canvas id="generalRequiredCreditsChart"></canvas>
            </div>
        </div>

        <!-- 졸업요건 충족 현황 테이블 -->
        <div class="graduation-status-table">
            <table class="requirements-table">
                <thead>
                    <tr>
                        <th>구분</th>
                        <th>필요</th>
                        <th>취득</th>
                        <th>부족</th>
                        <th>충족여부</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>총 이수 학점</td>
                        <td>${studentInfo.totalRequiredCredits}</td>
                        <td>${studentInfo.completedCredits}</td>
                        <td>${studentInfo.totalRequiredCredits - studentInfo.completedCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.completedCredits >= studentInfo.totalRequiredCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>전공-핵심 학점</td>
                        <td>${studentInfo.majorRequiredTotalCredits}</td>
                        <td>${studentInfo.majorRequiredCredits}</td>
                        <td>${studentInfo.majorRequiredTotalCredits - studentInfo.majorRequiredCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.majorRequiredCredits >= studentInfo.majorRequiredTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>전공-선택 학점</td>
                        <td>${studentInfo.majorElectiveTotalCredits}</td>
                        <td>${studentInfo.majorElectiveCredits}</td>
                        <td>${studentInfo.majorElectiveTotalCredits - studentInfo.majorElectiveCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.majorElectiveCredits >= studentInfo.majorElectiveTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>전공-기초 학점</td>
                        <td>${studentInfo.majorBasicTotalCredits}</td>
                        <td>${studentInfo.majorBasicCredits}</td>
                        <td>${studentInfo.majorBasicTotalCredits - studentInfo.majorBasicCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.majorBasicCredits >= studentInfo.majorBasicTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>교양-핵심 학점</td>
                        <td>${studentInfo.liberalArtsRequiredTotalCredits}</td>
                        <td>${studentInfo.liberalArtsRequiredCredits}</td>
                        <td>${studentInfo.liberalArtsRequiredTotalCredits - studentInfo.liberalArtsRequiredCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.liberalArtsRequiredCredits >= studentInfo.liberalArtsRequiredTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>교양-선택 학점</td>
                        <td>${studentInfo.liberalArtsElectiveTotalCredits}</td>
                        <td>${studentInfo.liberalArtsElectiveCredits}</td>
                        <td>${studentInfo.liberalArtsElectiveTotalCredits - studentInfo.liberalArtsElectiveCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.liberalArtsElectiveCredits >= studentInfo.liberalArtsElectiveTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>교양-기초 학점</td>
                        <td>${studentInfo.liberalArtsBasicTotalCredits}</td>
                        <td>${studentInfo.liberalArtsBasicCredits}</td>
                        <td>${studentInfo.liberalArtsBasicTotalCredits - studentInfo.liberalArtsBasicCredits}</td>
                        <td>
                            <c:choose>
                                <c:when test="${studentInfo.liberalArtsBasicCredits >= studentInfo.liberalArtsBasicTotalCredits}">
                                    <span class="status-badge status-complete">충족</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미충족</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td>졸업과제</td>
                        <td>-</td>
                        <td>-</td>
                        <td>-</td>
                        <td id="graduationAssignmentStatus">
                            <c:choose>
                                <c:when test="${graduationAssignmentStatus eq 'APPROVED'}">
                                    <span class="status-badge status-complete">승인 완료</span>
                                </c:when>
                                <c:when test="${graduationAssignmentStatus eq 'GRAD_ASSIGNMENT'}">
                                    <span class="status-badge status-pending">제출 완료</span>
                                </c:when>
                                <c:when test="${graduationAssignmentStatus eq 'REJECTED'}">
                                    <span class="status-badge status-rejected">반려
                                        <c:if test="${not empty graduationAssignmentEvaluation}">(${graduationAssignmentEvaluation})</c:if>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-incomplete">미제출</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 유의사항 (동적 표시) -->
        <div id="noticeBox" class="notice-box">
            <!-- 동적 생성 -->
        </div>

        <!-- 졸업과제 제출 폼 -->
        <div class="assignment-section">
            <h2 class="section-title">졸업과제 제출</h2>

            <form id="recordApplyForm" enctype="multipart/form-data">
                <input type="hidden" name="studentNo" value="${studentInfo.studentNo}">

                <div class="form-group">
                    <label for="attachFiles">첨부파일</label>
                    <input type="file" id="attachFiles" name="attachFiles" multiple class="form-input"
                        <c:if test="${graduationAssignmentStatus eq 'APPROVED'}">disabled</c:if>>
                </div>

                <div class="button-group">
                    <button type="reset" class="reset-btn"
                        <c:if test="${graduationAssignmentStatus eq 'APPROVED'}">disabled</c:if>>초기화</button>
                    <button type="submit" class="submit-btn"
                        <c:if test="${graduationAssignmentStatus eq 'APPROVED'}">disabled</c:if>>신청하기</button>
                </div>
            </form>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>\n<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.0.0/dist/chartjs-plugin-datalabels.min.js"></script>
<script>
    // Register the plugin globally
    Chart.register(ChartDataLabels);

    document.addEventListener('DOMContentLoaded', function() {
        // Data from model
        const studentData = {
            completedCredits: ${studentInfo.completedCredits},
            totalRequiredCredits: ${studentInfo.totalRequiredCredits},
            majorRequiredCredits: ${studentInfo.majorRequiredCredits},
            majorRequiredTotalCredits: ${studentInfo.majorRequiredTotalCredits},
            liberalArtsRequiredCredits: ${studentInfo.liberalArtsRequiredCredits},
            liberalArtsRequiredTotalCredits: ${studentInfo.liberalArtsRequiredTotalCredits}
        };

        // Function to create a pie chart with custom colors
        function createCreditChart(ctx, label, completed, total, color1, color2) {
            const needed = Math.max(0, total - completed);
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: ['취득 학점', '부족 학점'],
                    datasets: [{
                        data: [completed, needed],
                        backgroundColor: [color1, color2],
                        borderColor: [color1.replace('0.6', '1'), color2.replace('0.6', '1')],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                            labels: {
                                font: {
                                    size: 14  // 범례 폰트 크기 (기본 12)
                                }
                            }
                        },
                        title: {
                            display: true,
                            text: label,
                            font: {
                                size: 16,      // 제목 폰트 크기 (기본 12)
                                weight: 'bold' // 굵게
                            }
                        },
                        tooltip: {
                            bodyFont: {
                                size: 14  // 툴팁 폰트 크기
                            },
                            titleFont: {
                                size: 14
                            }
                        },
                        datalabels: {
                            formatter: (value, ctx) => {
                                if (value === 0) {
                                    return '';
                                }
                                const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
                                if (total === 0) {
                                    return '0%';
                                }
                                const percentage = ((value / total) * 100).toFixed(1) + '%';

                                return percentage;
                            },
                            color: '#fff',
                            font: {
                                weight: 'bold',
                                size: 16,
                            }
                        }
                    }
                }
            });
        }

        // 총 이수 학점 차트
        const totalCreditsCtx = document.getElementById('totalCreditsChart').getContext('2d');
        createCreditChart(totalCreditsCtx, '총 이수 학점', studentData.completedCredits, studentData.totalRequiredCredits, 'rgba(75, 192, 192, 0.6)', 'rgba(255, 99, 132, 0.6)');

        // 전공 필수 학점 차트
        const majorRequiredCreditsCtx = document.getElementById('majorRequiredCreditsChart').getContext('2d');
        createCreditChart(majorRequiredCreditsCtx, '전공 필수 학점', studentData.majorRequiredCredits, studentData.majorRequiredTotalCredits, 'rgba(54, 162, 235, 0.6)', 'rgba(255, 206, 86, 0.6)');

        // 교양 필수 학점 차트
        const generalRequiredCreditsCtx = document.getElementById('generalRequiredCreditsChart').getContext('2d');
        createCreditChart(generalRequiredCreditsCtx, '교양 필수 학점', studentData.liberalArtsRequiredCredits, studentData.liberalArtsRequiredTotalCredits, 'rgba(153, 102, 255, 0.6)', 'rgba(255, 159, 64, 0.6)');
    });
</script>
<script>
    const recordApplyForm = document.getElementById('recordApplyForm');
    console.log('recordApplyForm found:', recordApplyForm);

    if (recordApplyForm) {
        recordApplyForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            console.log('Submit event triggered.');

            const studentNo = recordApplyForm.querySelector('input[name="studentNo"]').value;
            console.log('studentNo:', studentNo);

            const attachFilesInput = document.getElementById('attachFiles');
            console.log('attachFilesInput found:', attachFilesInput);

            const files = attachFilesInput.files;
            console.log('Files selected:', files);
            console.log('Number of files:', files.length);

            if (files.length === 0) {
                Swal.fire({
                    title: '파일 미선택',
                    text: '제출할 파일을 선택해주세요.',
                    icon: 'error',
                    confirmButtonText: '확인'
                });
                console.log('No files selected, returning.');
                return;
            }

            const formData = new FormData();
            formData.append('studentNo', studentNo);
            for (let i = 0; i < files.length; i++) {
                formData.append('attachFiles', files[i]);
            }
            console.log('FormData prepared.');

            try {
                const response = await fetch('/lms/student/graduation/submitAssignment', {
                    method: 'POST',
                    body: formData
                });
                console.log('Fetch request sent. Response:', response);

                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        Swal.fire({
                            title: '제출 완료',
                            text: '졸업 과제가 성공적으로 제출되었습니다.',
                            icon: 'success',
                            confirmButtonText: '확인'
                        }).then(() => {
                            const statusElement = document.getElementById('graduationAssignmentStatus');
                            if (statusElement) {
                                statusElement.innerHTML = '<span class="status-badge status-pending">제출 완료</span>';
                            }
                        });
                    } else {
                        Swal.fire({
                            title: '제출 실패',
                            text: '졸업 과제 제출에 실패했습니다: ' + result.message,
                            icon: 'error',
                            confirmButtonText: '확인'
                        });
                    }
                } else {
                    Swal.fire({
                        title: '서버 오류',
                        text: '서버 오류가 발생했습니다.',
                        icon: 'error',
                        confirmButtonText: '확인'
                    });
                }
            } catch (error) {
                console.error('Error submitting graduation assignment:', error);
                Swal.fire({
                    title: '오류 발생',
                    text: '졸업 과제 제출 중 오류가 발생했습니다.',
                    icon: 'error',
                    confirmButtonText: '확인'
                });
            }
        });
    }
</script>
</body>
</html>