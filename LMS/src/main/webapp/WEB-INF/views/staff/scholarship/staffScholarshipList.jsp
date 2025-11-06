<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS 교직원 포털 - 장학 관리</title>
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
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<!-- Layout wrapper (similar to Sneat's) -->
	<div class="layout-wrapper">
		<div class="layout-container">
            <div class="container-fluid" id="main-content-area"> 
                <h3 class="mb-4">장학금 정보 조회 및 관리</h3>

                <!-- 검색 조건 -->
                <div class="search-form-container">
                    <h5><i class="bi bi-search me-2"></i> 장학금 검색 조건</h5>
                    <form class="row g-3">
                                                <div class="col-md-3">
                            <label for="searchType" class="form-label">검색 조건</label>
                            <select id="searchType" class="form-select">
                                <option selected>선택</option>
                                <option>학번</option>
                                <option>이름</option>
                                <option>장학유형</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="searchTerm" class="form-label">검색어</label>
                            <input type="text" class="form-control" id="searchTerm" placeholder="검색어">
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary me-2"><i class="bi bi-search me-2"></i> 검색</button>
                            <button type="reset" class="btn btn-secondary"><i class="bi bi-arrow-counterclockwise me-2"></i> 초기화</button>
                        </div>
                    </form>
                </div>

                <!-- 장학금 수혜 학생 현황 -->
                <h4 class="mt-4 mb-3"><i class="bi bi-wallet2 me-2"></i> 장학금 수혜 학생 현황</h4>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>학번</th>
                                <th>이름</th>
                                <th>학과</th>
                                <th>학기</th>
                                <th>장학유형</th>
                                <th>장학금액</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>202300001</td>
                                <td>김철수</td>
                                <td>컴퓨터공학과</td>
                                <td>1학기</td>
                                <td>성적 우수</td>
                                <td>1,000,000</td>
                            </tr>
                            <tr>
                                <td>202200005</td>
                                <td>이지영</td>
                                <td>경영학과</td>
                                <td>2학기</td>
                                <td>국가 장학</td>
                                <td>2,000,000</td>
                            </tr>
                            <!-- 더 많은 장학금 수혜 학생 데이터가 여기에 추가됩니다 -->
                        </tbody>
                    </table>
                </div>

                <!-- 장학금 유형 관리 -->
                <h4 class="mt-5 mb-3"><i class="bi bi-tags me-2"></i> 장학금 유형 관리</h4>
                <div class="section-container">
                    <h5>장학금 유형 목록</h5>
                    <div class="action-buttons d-flex justify-content-end mb-3">
                        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#scholarshipTypeRegistrationModal"><i class="bi bi-plus-circle me-2"></i> 유형 등록</button>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered">
                            <thead class="table-light">
                                <tr>
                                    <th>유형명</th>
                                    <th>금액</th>
                                    <th>설명</th>
                                    <th>수정/삭제</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>성적 우수 장학</td>
                                    <td>1,000,000</td>
                                    <td>성적 우수 학생에게 지급되는 장학금</td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#scholarshipTypeDetailModal">수정</button>
                                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>국가 장학</td>
                                    <td>2,000,000</td>
                                    <td>국가에서 지원하는 장학금</td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#scholarshipTypeDetailModal">수정</button>
                                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                                    </td>
                                </tr>
                                <!-- 더 많은 장학금 유형 데이터가 여기에 추가됩니다 -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modals for Scholarship Type Detail and Registration -->

    <!-- 장학금 유형 상세/수정 Modal -->
    <div class="modal fade" id="scholarshipTypeDetailModal" tabindex="-1" aria-labelledby="scholarshipTypeDetailModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="scholarshipTypeDetailModalLabel">장학금 유형 상세 및 수정</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="mb-3">
                            <label for="editTypeName" class="form-label">유형명</label>
                            <input type="text" class="form-control" id="editTypeName" value="성적 우수 장학">
                        </div>
                        <div class="mb-3">
                            <label for="editAmount" class="form-label">금액</label>
                            <input type="text" class="form-control" id="editAmount" value="1,000,000">
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">설명</label>
                            <textarea class="form-control" id="editDescription" rows="3">성적 우수 학생에게 지급되는 장학금</textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    <button type="button" class="btn btn-primary">저장</button>
                    <button type="button" class="btn btn-danger">삭제</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 장학금 유형 등록 Modal -->
    <div class="modal fade" id="scholarshipTypeRegistrationModal" tabindex="-1" aria-labelledby="scholarshipTypeRegistrationModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="scholarshipTypeRegistrationModalLabel">장학금 유형 등록</h5>
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
                            <label for="regDescription" class="form-label">설명</label>
                            <textarea class="form-control" id="regDescription" rows="3"></textarea>
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
                        // const scholarshipId = row.querySelector('td:first-child').innerText; // Assuming the first cell is the ID
                        
                        // In a real application, you would send a request to the server to delete the item.
                        // fetch(`/api/scholarships/${scholarshipId}`, { method: 'DELETE' })
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

            // Add confirmation to delete button in the modal
            const deleteModalButton = document.querySelector('#scholarshipTypeDetailModal .btn-danger');
            if (deleteModalButton) {
                deleteModalButton.addEventListener('click', function(event) {
                    if (confirm('정말 삭제하시겠습니까?')) {
                        // const scholarshipId = document.querySelector('#editTypeName').value; // Assuming the type name is the ID

                        // In a real application, you would send a request to the server to delete the item.
                        // fetch(`/api/scholarship-types/${scholarshipId}`, { method: 'DELETE' })
                        //     .then(response => {
                        //         if (response.ok) {
                        //             alert("삭제되었습니다.");
                        //             var modal = bootstrap.Modal.getInstance(document.getElementById('scholarshipTypeDetailModal'));
                        //             modal.hide();
                        //             // You might want to refresh the table here
                        //         } else {
                        //             alert("삭제에 실패했습니다.");
                        //         }
                        //     })
                        //     .catch(error => {
                        //         console.error('Error:', error);
                        //         alert("삭제 중 오류가 발생했습니다.");
                        //     });
                        
                        alert("삭제되었습니다. (데모)");
                        var modal = bootstrap.Modal.getInstance(document.getElementById('scholarshipTypeDetailModal'));
                        modal.hide();
                    }
                });
            }
        });
    </script>
</body>
</html>>
</body>
</html>