<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 10.     		신혜진         최초 생성
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>    
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>    
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>    
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<h1>교수-학생 승인문서 관리-전체 신청 현황 페이지</h1>
	<div class="ssc-wrapper">
        <div class="ssc-page-header">
            <h1 class="ssc-title">전체 승인문서 신청 현황</h1>
        </div>

        <div class="ssc-table-container">
     
					<form id="searchForm" action="/lms/professor/academic-change-status" method="GET" class="row g-3">
                        <input type="hidden" name="currentPage" value="${pagingInfo.currentPage}">
                        <input type="hidden" name="screenSize" value="${pagingInfo.screenSize}">
                        
                        <input type="hidden" name="deptCd" value="${param.deptCd}">
                        <input type="hidden" name="statusCd" value="${param.statusCd}">

						<div class="col-md-3">
							<label for="searchType" class="form-label">검색 조건</label> 
                            <select id="searchType" name="simpleSearch.searchType" class="form-select">
								<option value="" ${empty pagingInfo.simpleSearch.searchType ? 'selected' : ''}>선택</option>
								<option value="recordChangeCd" ${pagingInfo.simpleSearch.searchType eq 'recordChangeCd' ? 'selected' : ''}>학적변동타입</option>
								<option value="applyStatusCd" ${pagingInfo.simpleSearch.searchType eq 'applyStatusCd' ? 'selected' : ''}>신청상태</option>
                            </select>
						</div>
						<div class="col-md-3">
							<label for="searchWord" class="form-label">검색어</label> 
                            <input type="text" name="simpleSearch.searchWord" class="form-control" id="searchWord"
								placeholder="검색어" value="${pagingInfo.simpleSearch.searchWord}">
						</div>
						<div class="col-md-3 d-flex align-items-end">
							<button type="submit" class="btn btn-primary me-2">
								<i class="bi bi-search me-2"></i> 검색
							</button>
							<button type="reset" class="btn btn-secondary" onclick="location.href='/lms/professor/academic-change-status'; return false;">
								<i class="bi bi-arrow-counterclockwise me-2"></i> 초기화
							</button>
						</div>
					</form>
				</div>
		<div class="ssc-table-container">
            <table class="ssc-table">
                <thead class="ssc-thead">
                    <tr class="ssc-tr">
                        <th class="ssc-th">접수번호</th>
                        <th class="ssc-th">신청 구분</th>
                        <th class="ssc-th">신청일자</th>
                        <th class="ssc-th">처리상태</th>
                        <th class="ssc-th">상세</th>
                    </tr>
                </thead>
                <tbody class="ssc-tbody">
                <c:choose>
								<c:when test="${not empty academicChangeStatusList}">
									<c:forEach items="${academicChangeStatusList}" var="academicChangeStatus">
										<tr class="ssc-tr">
											<td class="ssc-td">${academicChangeStatus.staffNo}</td>

											<td class="ssc-td">${staff.userInfo.lastName}${staff.userInfo.firstName}</td>

											<td class="ssc-td">${staff.staffDeptInfo.stfDeptName}</td>

											<td class="ssc-td">${staff.userInfo.mobileNo}</td>

											<td class="ssc-td">
                                            <c:url var="detailUrl" value="/lms/professor/academic-change-status${staff.staffInfo.staffNo}" />
                                            <a href="${detailUrl}" class="btn btn-sm btn-outline-primary">보기</a>
                                        </td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="7" class="text-center">조회된 학적변동 신청 정보가 없습니다.</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
					<div class="pagination-area">
	                    <c:if test="${pagingInfo.startPage > 1}">
	                        <a href="javascript:pageing(${pagingInfo.startPage - 1});" title="이전 페이지">◀</a>
	                    </c:if>
	
	                    <c:forEach begin="${pagingInfo.startPage}" end="${pagingInfo.endPage}" var="p">
	                        <a href="javascript:pageing(${p});" class="${pagingInfo.currentPage eq p ? 'active' : ''}">${p}</a>
	                    </c:forEach>
	                    
	                    <c:if test="${pagingInfo.endPage < pagingInfo.totalPage}">
	                        <a href="javascript:pageing(${pagingInfo.endPage + 1});" title="다음 페이지">▶</a>
	                    </c:if>
	              </div>	           
	     </div>

    <!-- 상세보기 모달 -->
    <div id="sscDetailModal" class="ssc-modal">
        <div class="ssc-modal-content">
            <div class="ssc-modal-header">
                <h2 class="ssc-modal-title">승인문서 신청 상세정보</h2>
                <span class="ssc-close" onclick="closeSSCDetailModal()">&times;</span>
            </div>
            <div class="ssc-modal-body">
                <div class="ssc-detail-section">
                    <div class="ssc-detail-row">
                        <span class="ssc-detail-label">접수번호</span>
                        <span class="ssc-detail-value" id="sscModalReceiptNo"></span>
                    </div>
                    <div class="ssc-detail-row">
                        <span class="ssc-detail-label">신청 구분</span>
                        <span class="ssc-detail-value" id="sscModalCategory"></span>
                    </div>
                    <div class="ssc-detail-row">
                        <span class="ssc-detail-label">신청일자</span>
                        <span class="ssc-detail-value" id="sscModalDate"></span>
                    </div>
                    <div class="ssc-detail-row">
                        <span class="ssc-detail-label">처리상태</span>
                        <span class="ssc-detail-value" id="sscModalStatus"></span>
                    </div>
                </div>

                <div class="ssc-detail-section">
                    <div class="ssc-detail-row ssc-full-width">
                        <span class="ssc-detail-label">신청사유</span>
                        <div class="ssc-detail-value ssc-reason-box" id="sscModalReason"></div>
                    </div>
                </div>

                <div class="ssc-detail-section">
                    <div class="ssc-detail-row ssc-full-width">
                        <span class="ssc-detail-label">첨부파일</span>
                        <div class="ssc-detail-value ssc-file-box" id="sscModalFile"></div>
                    </div>
                </div>
            </div>
            <div class="ssc-modal-footer">
                <button class="ssc-btn-close" onclick="closeSSCDetailModal()">닫기</button>
            </div>
        </div>
    </div>

    <script>
        function openSSCDetailModal(receiptNo, category, date, status, reason, fileName) {
            document.getElementById('sscModalReceiptNo').textContent = receiptNo;
            document.getElementById('sscModalCategory').textContent = category;
            document.getElementById('sscModalDate').textContent = date;
            document.getElementById('sscModalStatus').textContent = status;
            document.getElementById('sscModalReason').textContent = reason || '사유 없음';
            
            var fileBox = document.getElementById('sscModalFile');
            if (fileName && fileName !== 'null' && fileName !== '') {
                fileBox.innerHTML = '<a href="#" class="ssc-file-link" onclick="alert(\'파일 다운로드: ' + fileName + '\'); return false;">' + 
                                   fileName + '</a>';
            } else {
                fileBox.textContent = '첨부파일 없음';
            }
            
            document.getElementById('sscDetailModal').style.display = 'block';
        }

        function closeSSCDetailModal() {
            document.getElementById('sscDetailModal').style.display = 'none';
        }

        // 모달 외부 클릭 시 닫기
        window.onclick = function(event) {
            var modal = document.getElementById('sscDetailModal');
            if (event.target == modal) {
                closeSSCDetailModal();
            }
        }
    </script>
</body>
</html>