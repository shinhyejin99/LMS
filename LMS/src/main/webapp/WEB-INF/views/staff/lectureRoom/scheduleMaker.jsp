<%--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 30.     		송태호            최초 생성
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>시간표 배정</title>
    <link rel="stylesheet" href="<c:url value='/css/scheduleMaker.css' />">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <div class="schedule-maker-page">
        <div class="content-row">
            <!-- 좌측: 강의 목록 섹션 -->
            <div class="content-left">
                <div class="card" style="animation-delay: 0.1s;">
                    <div class="card-header">
                        <h5>배정 대기 강의</h5>
                    </div>
                    <div class="card-body">
                            <!-- 필터 -->
                            <div class="filter-row" style="margin-top: 5px;">
                                <select class="form-select" id="departmentFilter">
                                    <option value="">학과</option>
                                </select>
                                <select class="form-select" id="completionFilter">
                                    <option value="">이수구분</option>
                                </select>
                                <select class="form-select" id="statusFilter">
                                    <option value="">배정상태</option>
                                    <option value="NEED" selected>시수부족</option>
                                    <option value="EXCEED">시수초과</option>
                                </select>
                            </div>

                            <!-- 강의 목록 -->
                            <div id="lectureList" style="max-height: 600px; overflow-y: auto;">
                                <div class="empty-state">
                                    <i class="fas fa-spinner fa-spin"></i>
                                    <p>강의 목록을 불러오는 중...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 우측: 장소 섹션 -->
                <div class="content-right">
                    <div class="card" style="animation-delay: 0.2s;">
                        <div class="card-header">
                            <h5>강의실 선택</h5>
                        </div>
                        <div class="card-body">
                            <!-- 건물 선택 -->
                            <h6 style="margin-top: 5px; margin-bottom: 15px; font-size: 14px; font-weight: 600;">건물</h6>
                            <div class="building-list" id="buildingList">
                                <div class="empty-state">
                                    <i class="fas fa-spinner fa-spin"></i>
                                    <p>건물 목록을 불러오는 중...</p>
                                </div>
                            </div>

                            <hr style="margin: 20px 0; border-top: 1px solid #dee2e6;">

                            <!-- 강의실 목록 -->
                            <h6 style="margin-bottom: 15px; font-size: 14px; font-weight: 600;">강의실</h6>
                            <div id="classroomListContainer">
                                <div class="empty-state">
                                    <i class="fas fa-building"></i>
                                    <p>건물을 먼저 선택해주세요</p>
                                </div>
                            </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 강의 신청서 보기 모달 -->
    <div class="modal fade" id="lectureApplicationModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">강의 신청서</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 15px;">
                            <div>
                                <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">과목코드</label>
                                <p style="font-weight: 600; margin: 0;" id="modalLectureCode">CSE101-01</p>
                            </div>
                            <div>
                                <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">과목명</label>
                                <p style="font-weight: 600; margin: 0;" id="modalLectureName">데이터베이스</p>
                            </div>
                        </div>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 15px;">
                            <div>
                                <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">담당교수</label>
                                <p style="font-weight: 600; margin: 0;" id="modalProfessor">김교수</p>
                            </div>
                            <div>
                                <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">수강정원</label>
                                <p style="font-weight: 600; margin: 0;" id="modalCapacity">45명</p>
                            </div>
                        </div>
                        <div style="margin-bottom: 15px;">
                            <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">희망 건물</label>
                            <p style="font-weight: 600; margin: 0;" id="modalPreferredBuilding">공학관</p>
                        </div>
                        <div style="margin-bottom: 15px;">
                            <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 5px;">희망 시간대</label>
                            <p style="font-weight: 600; margin: 0;" id="modalPreferredTime">월 10:30-12:00, 수 10:30-12:00</p>
                        </div>
                        <div style="margin-bottom: 10px;">
                            <label style="font-size: 13px; color: #6c757d; display: block; margin-bottom: 8px;">주차별 강의 계획</label>
                            <select id="modalWeekSelect" class="form-select form-select-sm" style="margin-bottom: 12px; display: none;">
                                <option value="">주차를 선택하세요</option>
                            </select>
                            <div id="modalLecturePlan" class="lecture-plan-container">
                                <div class="lecture-plan-empty">주차별 계획을 불러오는 중입니다...</div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    </div>
                </div>
            </div>
    </div>

    <!-- 시간표 배정 모달 -->
    <div class="modal fade" id="timetableModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">시간표 배정</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close">&times;</button>
                    </div>
                    <div class="modal-body">
                        <!-- 배정 정보 요약 -->
                        <div class="alert alert-info">
                            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px;">
                                <div><strong>강의:</strong> <span id="modalSelectedLecture">-</span></div>
                                <div><strong>강의실:</strong> <span id="modalSelectedClassroom">-</span></div>
                                <div><strong>현재/목표 시수:</strong> <span id="modalHourProgress" style="font-weight: 600;">0 / 0</span></div>
                            </div>
                        </div>

                        <!-- 선택된 시간 표시 -->
                        <div style="margin-bottom: 20px;">
                            <h6 style="font-size: 14px; font-weight: 600; margin-bottom: 10px;">선택된 시간 블록</h6>
                            <div id="selectedTimeSlotsDisplay" style="padding: 15px; background: #f8f9fa; border-radius: 6px;">
                                <em style="color: #6c757d; font-size: 13px;">시간 블록을 선택해주세요.</em>
                            </div>
                        </div>

                        <!-- 시간표 -->
                        <div id="modalTimetableContainer">
                            <div class="empty-state">
                                <i class="fas fa-calendar-alt"></i>
                                <p>시간표를 불러오는 중...</p>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn btn-primary" id="modalAssignBtn" disabled>
                            <i class="fas fa-check"></i> 배정하기
                        </button>
                    </div>
                </div>
            </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/js/app/staff/scheduleMaker.js' />"></script>
</body>
</html>
