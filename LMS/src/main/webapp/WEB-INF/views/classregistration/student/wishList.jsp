<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 16.     	김수현            최초 생성
 *	2025. 10. 18.		김수현			탭 추가 및 페이지네이션 수정
 *  2025. 10. 20.		김수현			소켓 설정 수정
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>수강신청</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/wishList.css" />
<!-- SweetAlert2 -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<!-- 전체를 wishlist-page로 감싸기 -->
	<div class="wishlist-page">
		<div class="main-container">

			<!-- 메인 컨텐츠 -->
			<main class="content">

				<!-- 탭 메뉴 -->
				<div class="tab-menu">
					<button class="tab-btn ${currentTab == 'search' ? 'active' : ''}"
						data-tab="search" onclick="switchTab('search')">개설과목 조회/신청
					</button>
					<button class="tab-btn ${currentTab == 'wishlist' ? 'active' : ''}"
						data-tab="wishlist" onclick="switchTab('wishlist')">찜 목록
					</button>
					<button class="tab-btn ${currentTab == 'applied' ? 'active' : ''}"
						data-tab="applied" onclick="switchTab('applied')">수강신청 내역
					</button>
				</div>
				<div class="tab-status-container">
					<!-- 조회/신청 탭일 때만 학년 탭 표시 -->
					<c:if test="${currentTab == 'search'}">
						<!-- 학년 탭 (전체 강의 조회용) -->
						<div class="grade-tabs">
							<button
								class="grade-tab ${empty searchWord and (empty gradeFilter or gradeFilter == 'ALL') ? 'active' : ''}"
								onclick="filterByGrade('ALL')">전체</button>
							<button
								class="grade-tab ${empty searchWord and gradeFilter == '1ST' ? 'active' : ''}"
								onclick="filterByGrade('1ST')">1학년</button>
							<button
								class="grade-tab ${empty searchWord and gradeFilter == '2ND' ? 'active' : ''}"
								onclick="filterByGrade('2ND')">2학년</button>
							<button
								class="grade-tab ${empty searchWord and gradeFilter == '3RD' ? 'active' : ''}"
								onclick="filterByGrade('3RD')">3학년</button>
							<button
								class="grade-tab ${empty searchWord and gradeFilter == '4TH' ? 'active' : ''}"
								onclick="filterByGrade('4TH')">4학년</button>
						</div>
					</c:if>

					<!-- 신청 현황 (탭별로 다르게 표시) -->
					<div class="status-bar">
						<div class="apply-status-header">
							<div class="status-badge">
								<span class="badge-label">신청과목</span> <strong
									id="currentSubjects">0</strong> <span class="unit">과목</span>
							</div>
							<span class="divider">|</span>
							<div class="status-badge">
								<span class="badge-label">신청학점</span> <strong
									id="currentCredits">0</strong> <span class="unit">/</span> <span
									class="unit" id="maxCredits">24</span>
							</div>
						</div>
					</div>
				</div>

				<!-- 검색바 -->
				<c:if test="${currentTab == 'search'}">
					<div class="search-section">
						<form id="searchForm" method="get"
							action="${pageContext.request.contextPath}/classregist/student/wish">
							<input type="hidden" name="tab" value="search"> <select
								name="searchType" id="searchType">
								<option value="ALL" ${searchType == 'ALL' ? 'selected' : ''}>전체</option>
								<option value="SUBJECT"
									${searchType == 'SUBJECT' ? 'selected' : ''}>과목명</option>
								<option value="PROFESSOR"
									${searchType == 'PROFESSOR' ? 'selected' : ''}>교수명</option>
							</select> <input type="text" name="searchWord" id="searchWord"
								value="${searchWord}" placeholder="검색어를 입력하세요">
							<button type="submit" class="btn-search">검색</button>
							<button type="button" class="btn-reset" onclick="resetSearch()">초기화</button>
						</form>
					</div>
				</c:if>


				<!-- 목록 제목 -->
				<div class="list-header">
					<h2 id="listTitle">
						<c:choose>
							<c:when test="${currentTab == 'search'}">
								<c:choose>
									<c:when test="${not empty searchWord}">
			                        "<strong>${searchWord}</strong>"에 대한 검색 결과

			                        <!-- 검색 결과 필터 -->
										<div class="search-result-filter">
											<select id="searchResultGradeFilter"
												onchange="filterSearchResult(this.value)">
												<option value="ALL"
													${gradeFilter == 'ALL' ? 'selected' : ''}>전체 학년</option>
												<option value="1ST"
													${gradeFilter == '1ST' ? 'selected' : ''}>1학년</option>
												<option value="2ND"
													${gradeFilter == '2ND' ? 'selected' : ''}>2학년</option>
												<option value="3RD"
													${gradeFilter == '3RD' ? 'selected' : ''}>3학년</option>
												<option value="4TH"
													${gradeFilter == '4TH' ? 'selected' : ''}>4학년</option>
											</select>
										</div>
									</c:when>
									<c:otherwise>
										<!-- 학년 필터별 표시 -->
										<c:choose>
											<c:when test="${gradeFilter == '1ST'}">1학년 대상 강의</c:when>
											<c:when test="${gradeFilter == '2ND'}">2학년 대상 강의</c:when>
											<c:when test="${gradeFilter == '3RD'}">3학년 대상 강의</c:when>
											<c:when test="${gradeFilter == '4TH'}">4학년 대상 강의</c:when>
											<c:otherwise>전체 개설 강의</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${currentTab == 'applied'}">
			                내 수강신청 내역
			            </c:when>
							<c:otherwise>
			                내 찜 목록
			            </c:otherwise>
						</c:choose>
					</h2>
					<span class="total-count">총 <strong>${totalCount}</strong>건
					</span>
				</div>

				<!-- 강의 목록 테이블 -->
				<table class="lecture-table">
					<thead>
						<tr>
							<th>과목명</th>
							<th>교수명</th>
							<th>강의실</th>
							<th>시간</th>
							<th>학점/시수</th>
							<th>이수구분</th>
							<th>대상학년</th>
							<th>정원</th>
							<c:if test="${currentTab == 'wishlist'}">
								<th>찜한 시간</th>
							</c:if>
							<c:if test="${currentTab == 'applied'}">
								<th>신청 시간</th>
							</c:if>
							<th><c:choose>
									<c:when test="${currentTab == 'search'}">찜/신청</c:when>
									<c:when test="${currentTab == 'applied'}">취소</c:when>
									<c:otherwise>취소/신청</c:otherwise>
								</c:choose></th>
						</tr>
					</thead>
					<tbody id="lectureTableBody">
						<c:choose>
							<c:when test="${empty lectureList}">
								<tr>
									<td colspan="10" class="empty-message"><c:choose>
											<c:when
												test="${currentTab == 'search' and not empty searchWord}">
				                            검색 결과가 없습니다.
				                        </c:when>
											<c:when test="${currentTab == 'applied'}">
				                            수강신청한 강의가 없습니다.
				                        </c:when>
											<c:otherwise>
				                            찜한 강의가 없습니다.
				                        </c:otherwise>
										</c:choose></td>
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach var="lecture" items="${lectureList}">
									<tr class="lecture-row"
										onclick="showDetail('${lecture.lectureId}')">
										<td>${lecture.subjectName}</td>
										<td>${lecture.professorName}</td>
										<td>${lecture.placeName}</td>
										<td>${lecture.timeInfo}</td>
										<td>${lecture.credit}/${lecture.hour}</td>
										<td>${lecture.completionName}</td>
										<td>${lecture.targetGrades}</td>
										<td id="enroll-${lecture.lectureId}"
											class="enroll-info
									    ${lecture.currentEnroll >= lecture.maxCap ? 'status-full' :
									      lecture.currentEnroll >= lecture.maxCap * 0.8 ? 'status-high' :
									      lecture.currentEnroll >= lecture.maxCap * 0.5 ? 'status-medium' : 'status-low'}">
											<strong>${lecture.currentEnroll}</strong> / ${lecture.maxCap}
											<c:if test="${lecture.currentEnroll >= lecture.maxCap}">
												<span class="badge-full">마감</span>
											</c:if>
										</td>

										<%-- 찜한 시간 / 신청 시간 --%>
										<c:if test="${currentTab == 'wishlist'}">
											<td>${lecture.wishlistAt}</td>
										</c:if>
										<c:if test="${currentTab == 'applied'}">
											<td>${lecture.applyAt}</td>
										</c:if>

										<td onclick="event.stopPropagation()"><c:choose>
												<%-- 조회/신청 탭 (search) --%>
												<c:when test="${currentTab == 'search'}">
													<div class="action-buttons">
														<%-- 찜 버튼 --%>
														<c:choose>
															<c:when test="${lecture.wishlisted}">
																<button class="btn-wishlist active"
																	onclick="removeWishlist('${lecture.lectureId}')">♥</button>
															</c:when>
															<c:otherwise>
																<button class="btn-wishlist"
																	onclick="addWishlist('${lecture.lectureId}')">♡</button>
															</c:otherwise>
														</c:choose>

														<%-- 신청 버튼 --%>
														 <c:choose>
														    <%-- 이미 수강신청한 경우 --%>
														    <c:when test="${lecture.applied}">
														        <button class="btn-applied" disabled>

														            <span>신청완료</span>
														        </button>
														    </c:when>
														    <%-- 정원 마감된 경우 --%>
														    <c:when test="${lecture.currentEnroll >= lecture.maxCap}">
														        <button class="btn-apply btn-full" disabled>
														            <i class='bx bx-lock'></i>
														            <span>마감</span>
														        </button>
														    </c:when>
														    <%-- 신청 가능한 경우 --%>
														    <c:otherwise>
														        <button class="btn-apply"
														                onclick="applyLecture('${lecture.lectureId}')">
														            <i class='bx bx-plus-circle'></i>
														            <span>신청</span>
														        </button>
														    </c:otherwise>
														</c:choose>
													</div>
												</c:when>

												<%-- 수강신청 내역 탭 (applied) --%>
												<c:when test="${currentTab == 'applied'}">
													<button class="btn-cancel-apply"
														onclick="cancelApplyLecture('${lecture.lectureId}')">취소</button>
												</c:when>

												<%-- 찜 목록 탭 (wishlist - 'search'나 'applied'가 아닐 때) --%>
												<c:otherwise>
													<div class="action-buttons">
														<button class="btn-cancel"
															onclick="removeWishlist('${lecture.lectureId}')">찜
															취소</button>
														<button class="btn-apply"
															onclick="applyLecture('${lecture.lectureId}')">신청</button>
													</div>
												</c:otherwise>
											</c:choose></td>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>

				<!-- 페이지네이션 -->
				<c:if test="${totalPages > 0}">
					<div class="pagination">
						<c:if test="${currentPage > 1}">
							<c:choose>
								<c:when test="${currentTab == 'search'}">
									<a
										href="?tab=search&page=${currentPage - 1}&searchType=${searchType}&searchWord=${searchWord}&gradeFilter=${gradeFilter}">이전</a>
								</c:when>
								<c:when test="${currentTab == 'applied'}">
									<a href="?tab=applied&page=${currentPage - 1}">이전</a>
								</c:when>
								<c:otherwise>
									<a href="?tab=wishlist&page=${currentPage - 1}">이전</a>
								</c:otherwise>
							</c:choose>
						</c:if>

						<c:forEach begin="1" end="${totalPages}" var="i">
							<c:choose>
								<c:when test="${i == currentPage}">
									<span class="current">${i}</span>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${currentTab == 'search'}">
											<a
												href="?tab=search&page=${i}&searchType=${searchType}&searchWord=${searchWord}&gradeFilter=${gradeFilter}">${i}</a>
										</c:when>
										<c:when test="${currentTab == 'applied'}">
											<a href="?tab=applied&page=${i}">${i}</a>
										</c:when>
										<c:otherwise>
											<a href="?tab=wishlist&page=${i}">${i}</a>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</c:forEach>

						<c:if test="${currentPage < totalPages}">
							<c:choose>
								<c:when test="${currentTab == 'search'}">
									<a
										href="?tab=search&page=${currentPage + 1}&searchType=${searchType}&searchWord=${searchWord}&gradeFilter=${gradeFilter}">다음</a>
								</c:when>
								<c:when test="${currentTab == 'applied'}">
									<a href="?tab=applied&page=${currentPage + 1}">다음</a>
								</c:when>
								<c:otherwise>
									<a href="?tab=wishlist&page=${currentPage + 1}">다음</a>
								</c:otherwise>
							</c:choose>
						</c:if>
					</div>
				</c:if>
			</main>
		</div> <!-- main-container 끝 -->
		<!-- 강의 상세 모달 -->
		<div id="detailModal" class="modal lecture-detail-modal">
			<div class="modal-content">
				<span class="close" onclick="closeModal()">&times;</span>
				<h2>강의계획서</h2>
				<div id="detailContent">
					<!-- AJAX로 로드 -->
				</div>
			</div>
		</div>
	</div> <!-- wishlist-page 끝 - 모달 아래로 이동 -->


	<!-- 전역 변수만 설정 (함수 호출 X) -->
	<script>
		const CONTEXT_PATH = "${pageContext.request.contextPath}";
		var currentTab = '${currentTab != null ? currentTab : "search"}';

		console.log('==> JSP 전역 변수 설정');
		console.log('currentTab:', currentTab);
	</script>
	<script
		src="${pageContext.request.contextPath}/js/app/classregistration/wishlist.js"></script>
	<script
		src="${pageContext.request.contextPath}/js/app/classregistration/wishlist-websocket.js"></script>
</body>
</html>