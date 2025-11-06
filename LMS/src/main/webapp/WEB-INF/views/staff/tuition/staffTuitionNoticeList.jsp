<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교직원 포털 - 재정업무</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        .search-form-container,
        .section-container {
            padding: 1.5rem;
            border: 1px solid #e0e0e0;
            border-radius: 0.25rem;
            background-color: #f9f9f9;
            margin-bottom: 1.5rem;
        }
        .table-responsive {
            margin-top: 1.5rem;
        }
        .action-buttons {
            margin-bottom: 1.5rem;
        }
    </style>
</head>
<body class="bg-gray-100">
	<!-- Layout wrapper (similar to Sneat's) -->
	<div class="layout-wrapper">
		<div class="layout-container">
            <div class="container-fluid" id="main-content-area">
                <h3 class="mb-4">등록금 납부 현황 및 유형 관리</h3>
                
                
				<!-- 수현 추가) 교직원 납부요청 프로시저 실행 버튼 -->
				<h2>교직원 납부요청 프로시저 실행 버튼</h2>
				<button class="btn-action btn-create" id="create-request">등록금 납부요청 생성</button>
				<button class="btn-action btn-status" id="check-status">현황 조회</button>
				
				
				
                <!-- 등록금 납부 현황 검색 조건 -->
                <div class="search-form-container">
                    <h5><i class="bi bi-search me-2"></i> 등록금 납부 현황 검색</h5>
                    <form class="row g-3">
                        <div class="col-md-3">
                            <label for="searchType" class="form-label">검색 조건</label>
                            <select id="searchType" class="form-select">
                                <option selected>선택</option>
                                <option>연도</option>
                                <option>학기</option>
                                <option>납부 여부</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="searchWord" class="form-label">검색어</label>
                            <input type="text" class="form-control" id="searchWord" placeholder="검색어">
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary me-2"><i class="bi bi-search me-2"></i> 검색</button>
                            <button type="reset" class="btn btn-secondary"><i class="bi bi-arrow-counterclockwise me-2"></i> 초기화</button>
                        </div>
                    </form>
                </div>

                <!-- 등록금 납부 현황 목록 -->
                <h4 class="mt-4 mb-3"><i class="bi bi-cash-stack me-2"></i> 등록금 납부 현황</h4>

<div class="action-buttons d-flex justify-content-between align-items-center mb-3 p-3 bg-light rounded">
    <div>
        <input type="checkbox" id="selectAllStudents">
        <label for="selectAllStudents" class="ms-1 fw-bold">전체 선택</label>
        <span class="ms-3">선택된 항목: <strong id="selectedCount">0</strong>건</span>
    </div>
    <button type="button" class="btn btn-success btn-lg" id="batchPaymentConfirmBtn">
        <i class="bi bi-check-circle me-2"></i> 일괄 납부확인
    </button>
</div>

<div class="table-responsive">
    <table class="table table-hover table-bordered" id="tuitionStatusTable">
        <thead class="table-light">
            <tr>
                <th style="width: 40px;">
                    <input type="checkbox" id="headerCheckbox">
                </th> <th>학번</th>
                <th>이름</th>
                <th>학과</th>
                <th>학기</th>
                <th>등록금액</th>
                <th>납부 여부</th>
                <th>납부일</th>
            </tr>
        </thead>
        <tbody id="tuitionStatusBody">
            </tbody>
    </table>
</div>

                <!-- 등록금 유형 관리 -->
                <h4 class="mt-5 mb-3"><i class="bi bi-tags me-2"></i> 등록금 유형 관리</h4>
                <div class="section-container">
                    <h5>등록금 유형 목록</h5>
                    <div class="action-buttons d-flex justify-content-end mb-3">
                        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#tuitionTypeRegistrationModal"><i class="bi bi-plus-circle me-2"></i> 유형 등록</button>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered">
                            <thead class="table-light">
                                <tr>
                                    <th>유형명</th>
                                    <th>금액</th>
                                    <th>적용 학과</th>
                                    <th>수정/삭제</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>공과대학 등록금</td>
                                    <td>4,000,000</td>
                                    <td>컴퓨터공학과, 전자공학과</td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#tuitionTypeDetailModal">수정</button>
                                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>경영대학 등록금</td>
                                    <td>3,800,000</td>
                                    <td>경영학과, 회계학과</td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#tuitionTypeDetailModal">수정</button>
                                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                                    </td>
                                </tr>
                                <!-- 더 많은 등록금 유형 데이터가 여기에 추가됩니다 -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modals for Tuition Type Detail and Registration -->

    <!-- 등록금 유형 상세/수정 Modal -->
    <div class="modal fade" id="tuitionTypeDetailModal" tabindex="-1" aria-labelledby="tuitionTypeDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tuitionTypeDetailModalLabel">등록금 유형 상세 및 수정</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="mb-3">
                            <label for="editTypeName" class="form-label">유형명</label>
                            <input type="text" class="form-control" id="editTypeName" value="공과대학 등록금">
                        </div>
                        <div class="mb-3">
                            <label for="editAmount" class="form-label">금액</label>
                            <input type="text" class="form-control" id="editAmount" value="4,000,000">
                        </div>
                        <div class="mb-3">
                            <label for="editApplicableDepartment" class="form-label">적용 학과</label>
                            <input type="text" class="form-control" id="editApplicableDepartment" value="컴퓨터공학과, 전자공학과">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    <button type="button" class="btn btn-primary">저장</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 등록금 유형 등록 Modal -->
    <div class="modal fade" id="tuitionTypeRegistrationModal" tabindex="-1" aria-labelledby="tuitionTypeRegistrationModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tuitionTypeRegistrationModalLabel">등록금 유형 등록</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="mb-3">
                            <label for="regTypeName" class="form-label">유형명</label>
                            <input type="text" class="form-control" id="regTypeName">
                        </div>
                        <div class="mb-3">
                            <label for="regAmount" class="form-label">금액</label>
                            <input type="text" class="form-control" id="regAmount">
                        </div>
                        <div class="mb-3">
                            <label for="regApplicableDepartment" class="form-label">적용 학과</label>
                            <input type="text" class="form-control" id="regApplicableDepartment">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-primary">등록</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/sidebar_active.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const searchTypeSelect = document.getElementById('searchType');
            const searchTermInput = document.getElementById('searchTerm');

            function updatePlaceholder() {
                let placeholderText = '검색어';
                searchTermInput.placeholder = placeholderText;
            }

            // Initial call to set placeholder on page load
            updatePlaceholder();

            // Listen for changes
            searchTypeSelect.addEventListener('change', updatePlaceholder);

            // Add confirmation to delete buttons in the table
            document.querySelectorAll('.table .btn-outline-danger').forEach(button => {
                button.addEventListener('click', function(event) {
                    if (confirm('정말 삭제하시겠습니까?')) {
                        const row = this.closest('tr');
                        // const tuitionId = row.querySelector('td:first-child').innerText; // Assuming the first cell is the ID

                        // In a real application, you would send a request to the server to delete the item.
                        // fetch(`/api/tuition/${tuitionId}`, { method: 'DELETE' })
                        //     .then(response => {
                        //         if (response.ok) {
                        //             row.remove();
                        //             alert("삭제되었습니다.");
                        //         } else {
                        //             alert("삭제에 실패했습니다.");
                        //         }
                        //     })
                        //     .catch(error => {
                        //         console.error('Error:', error);
                        //         alert("삭제 중 오류가 발생했습니다.");
                        //     });

                        // For this demo, we will just remove the row from the table.
                        row.remove();
                        alert("삭제되었습니다. (데모)");
                    }
                });
            });
        });
    </script>
</body>
</html>