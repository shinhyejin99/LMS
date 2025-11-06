<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>전자 결재</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f9f9;
            color: #333;
            padding: 20px;
            text-align: center;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
        }
        h2 {
            font-size: 1.8rem;
            font-weight: bold;
            margin-bottom: 1.5rem;
        }
        .form-group {
            margin-bottom: 1.5rem;
            text-align: left;
        }
        label {
            display: block;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        select {
            width: 100%;
            padding: 0.75rem;
            font-size: 1rem;
            border: 1px solid #ccc;
            border-radius: 0.25rem;
        }
        input[type="text"],
        textarea {
            width: 100%;
            padding: 0.75rem;
            font-size: 1rem;
            border: 1px solid #ccc;
            border-radius: 0.25rem;
        }
        textarea {
            height: 150px;
        }
        .btn {
            padding: 0.5rem 1rem;
            font-size: 1rem;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
        }
        .btn-primary {
            background: #007bff;
            color: #fff;
        }
        .btn-secondary {
            background: #6c757d;
            color: #fff;
        }
        .mt-3 {
            margin-top: 1.5rem;
        }
        .text-right {
            text-align: right;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="container">
        <div id="approval-create-view" class="py-4">
            <div class="row">
                <div class="col-12">
                    <div class="card border-0 shadow">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h2 class="fs-5 fw-bold mb-0">결재 작성</h2>
                        </div>
                        <div class="card-body">
                            <form id="approvalForm" action="/lms/professor/approvals/create" method="post">
                                <div class="form-group">
                                    <label>결재 양식 선택</label>
                                    <select name="approvalType">
                                        <option value="report">휴학신청서</option>
                                        <option value="request">복수전공신청서</option>
                                        <option value="proposal">기안서</option>
                                        <option value="academic_change">보고서</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>제목</label><br>
                                    <input type="text" class="form-control" name="title">
                                </div>
                                <div class="form-group">
                                    <label>내용</label><br>
                                    <textarea rows="6" class="form-control" name="content"></textarea>
                                </div>
                            <div class="form-group" id="approversContainer">
                                <label>결재자 지정</label><br>
                                <div class="d-flex align-items-center">
                                    <select class="form-control me-2" name="approverIds">
                                        <option value="">결재자 선택</option>
                                        <option value="headOfDepartment">학과장</option>
                                        <option value="staff">교직원</option>
                                        <c:forEach items="${professors}" var="prof">
                                            <option value="${prof.professorId}">${prof.professorName}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="button" class="btn btn-info btn-sm text-nowrap" onclick="addApproverDropdown()"> + 결재자 추가</button>
                                </div>
                            </div>
                            <div class="text-right mt-3">
                                <button type="submit" class="btn btn-primary">제출</button>
                                <button type="button" class="btn btn-secondary">취소</button>
                            </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
<script>
    function addApproverDropdown() {
        const container = document.getElementById('approversContainer');
        const newDropdownWrapper = document.createElement('div');
        newDropdownWrapper.className = 'd-flex align-items-center mt-2'; // Add margin-top for spacing

        const originalSelect = container.querySelector('select');
        const clonedSelect = originalSelect.cloneNode(true); // true to clone children (options)
        clonedSelect.removeAttribute('id'); // Remove ID if it had one, to avoid duplicates
        clonedSelect.value = ""; // Reset selected value
        clonedSelect.name = "approverIds"; // Add name attribute for cloned selects

        const removeButton = document.createElement('button');
        removeButton.type = 'button';
                removeButton.className = 'btn btn-danger btn-sm ms-2 text-nowrap'; // Bootstrap classes for a small danger button with margin-left        removeButton.textContent = ' - 삭제';
        removeButton.onclick = function() {
            newDropdownWrapper.remove(); // Remove the entire new dropdown div
        };

        newDropdownWrapper.appendChild(clonedSelect);
        newDropdownWrapper.appendChild(removeButton);
        container.appendChild(newDropdownWrapper);
    }
</script>
</body>
