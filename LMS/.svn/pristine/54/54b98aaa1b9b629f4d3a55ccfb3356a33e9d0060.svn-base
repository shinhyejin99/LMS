import { uploadClassroomFiles } from "../common/uploadFiles.js";

// /js/app/classroom/student/stuIndivtaskPost.js
(() => {
	"use strict";

	const $root = document.getElementById("post-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const taskId = $root.dataset.taskId;
	const apiBaseUrl = `${ctx}/classroom/api/v1/student/task/${lectureId}/indiv/${taskId}`;

	// --- state
	let studentInfo = null;
	let lastSubmission = null;

	// --- DOM Hooks
	const $taskBody = document.getElementById("task-body");
	const $attachArea = document.getElementById("attach-area");
	const $attachList = document.getElementById("attach-list");
	
	const $statusCard = document.getElementById("submission-status-card");
	const $historyBody = document.getElementById("submission-history-body");
	const $showSubmitFormBtn = document.getElementById("show-submit-form-btn");

	const $formCard = document.getElementById("submission-form-card");
	const $submitForm = document.getElementById("submit-form");
	const $submitFormTitle = document.getElementById("submit-form-title");
	const $submitDesc = document.getElementById("submit-desc");
	const $submitFileInput = document.getElementById("submit-file-input");
	const $submitFileId = document.getElementById("submit-file-id");
	const $existingFilesContainer = document.getElementById("existing-files-container");
	const $existingFilesList = document.getElementById("existing-files-list");
	const $submitButton = document.getElementById("submit-button");
	const $cancelSubmitBtn = document.getElementById("cancel-submit-btn");
	
	const $errorBox = document.getElementById("error-box");

	// --- Helpers
	const showError = (msg) => {
		if (!$errorBox) return;
		$errorBox.textContent = msg;
		$errorBox.classList.remove("d-none");
	};

	const fmtDateTime = (isoStr) => {
		if (!isoStr) return "-";
		const d = new Date(isoStr);
		if (Number.isNaN(d.getTime())) return "-";
		const pad = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};
	
	const switchView = (view) => { // "status" or "form"
		if (view === 'form') {
			$statusCard.classList.add('d-none');
			$formCard.classList.remove('d-none');
		} else {
			$formCard.classList.add('d-none');
			$statusCard.classList.remove('d-none');
			// 폼 초기화
			$submitFileInput.value = '';
			$existingFilesContainer.classList.add('d-none');
			$existingFilesList.innerHTML = '';
		}
	};

	// --- Renderers
	const renderTaskDetails = (task) => {
		const { indivtaskName, indivtaskDesc, createAt, startAt, endAt, attachFileList } = task;
		$taskBody.innerHTML = `
			<h1 class="h4 mb-3">${indivtaskName}</h1>
			<div class="d-flex flex-wrap gap-3 text-muted small mb-3">
				<span><strong>작성일</strong>: ${fmtDateTime(createAt)}</span>
				<span><strong>시작일</strong>: ${fmtDateTime(startAt)}</span>
				<span><strong>마감일</strong>: ${fmtDateTime(endAt)}</span>
			</div>
			<hr/>
			<article class="task-content mb-4">${indivtaskDesc || "(내용 없음)"}</article>
		`;

		if (attachFileList && attachFileList.length > 0) {
			$attachArea.classList.remove("d-none");
			$attachList.innerHTML = attachFileList.map(file => {
				const name = file.originalFileName || `파일 ${file.fileOrder}`;
				const size = typeof file.fileSize === "number" ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : "";
				const href = `${ctx}/classroom/api/v1/common/task/${lectureId}/indiv/${taskId}/attach/${file.fileOrder}`;
				return `
					<li class="list-group-item d-flex justify-content-between align-items-center">
						<div class="text-truncate" style="max-width:70%;">
							<i class="bi bi-paperclip"></i> ${name} <small class="text-muted">${size}</small>
						</div>
						<a class="btn btn-sm btn-outline-secondary" href="${href}">다운로드</a>
					</li>
				`;
			}).join("");
		}
	};

	const renderSubmissionHistory = (submissions) => {
		if (!submissions || submissions.length === 0) {
			$historyBody.innerHTML = '<p class="text-muted">아직 제출한 과제가 없습니다.</p>';
			$showSubmitFormBtn.textContent = "과제 제출하기";
			$submitFormTitle.textContent = "과제 제출";
			return;
		}

		lastSubmission = submissions[submissions.length - 1];
		const { submitAt, submitDesc, evaluScore, evaluDesc, evaluAt, submitFiles } = lastSubmission;

		let filesHtml = '<p class="text-muted small">첨부파일 없음</p>';
		if (submitFiles && submitFiles.length > 0 && submitFiles[0].fileId) {
			filesHtml = submitFiles.map(file => {
				const name = file.originalFileName || `파일 ${file.fileOrder}`;
				const size = typeof file.fileSize === "number" ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : "";
				const href = `${ctx}/classroom/api/v1/common/${lectureId}/indivtask/${taskId}/submit/${studentInfo.studentNo}/attach/${file.fileOrder}`;
				return `
					<li class="list-group-item list-group-item-light d-flex justify-content-between align-items-center">
						<div class="text-truncate" style="max-width:70%;">
							<i class="bi bi-paperclip"></i> ${name} <small class="text-muted">${size}</small>
						</div>
						<a class="btn btn-sm btn-outline-secondary" href="${href}">다운로드</a>
					</li>
				`;
			}).join("");
		}

		let evaluationHtml = '<p class="mt-3 mb-0"><span class="badge bg-secondary">평가 대기중</span></p>';
		if (evaluAt) {
			evaluationHtml = `
				<div class="mt-3 p-3 bg-light rounded">
					<h6>교수님 평가</h6>
					<p class="mb-1"><strong>점수:</strong> ${evaluScore}점</p>
					<p class="mb-1"><strong>평가:</strong> ${evaluDesc || '-'}</p>
					<p class="small text-muted mb-0">평가일: ${fmtDateTime(evaluAt)}</p>
				</div>
			`;
		}

		$historyBody.innerHTML = `
			<p><strong>제출일:</strong> ${fmtDateTime(submitAt)}</p>
			<p><strong>설명:</strong></p>
			<div class="p-2 bg-body-secondary rounded">${submitDesc || '-'}</div>
			<h6 class="mt-3">첨부파일</h6>
			<ul class="list-group mb-3">${filesHtml}</ul>
			${evaluationHtml}
		`;

		$showSubmitFormBtn.textContent = "과제 수정하기";
		$submitFormTitle.textContent = "과제 수정";
		$submitDesc.value = submitDesc || "";
	};

	const prepareEditForm = () => {
		if (!lastSubmission) return;

		const { submitDesc, submitFiles } = lastSubmission;
		$submitDesc.value = submitDesc || "";

		if (submitFiles && submitFiles.length > 0 && submitFiles[0].fileId) {
			$submitFileId.value = submitFiles[0].fileId;
			$existingFilesContainer.classList.remove("d-none");
			$existingFilesList.innerHTML = submitFiles.map(file => {
				const name = file.originalFileName || `파일 ${file.fileOrder}`;
				const size = typeof file.fileSize === "number" ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : "";
				return `<li class="list-group-item list-group-item-light">${name} <small class="text-muted">${size}</small></li>`;
			}).join("");
		} else {
			$submitFileId.value = "";
			$existingFilesContainer.classList.add("d-none");
			$existingFilesList.innerHTML = "";
		}
	};

	// --- Data & Event Handlers
	const loadData = async () => {
		try {
			const [task, me] = await Promise.all([
				fetch(apiBaseUrl, { headers: { "Accept": "application/json" } }).then(res => {
					if (!res.ok) throw new Error(`과제 정보 로딩 실패: HTTP ${res.status}`);
					return res.json();
				}),
				fetch(`${ctx}/classroom/api/v1/student/${lectureId}/me`, { headers: { "Accept": "application/json" } }).then(res => {
					if (!res.ok) throw new Error(`학생 정보 로딩 실패: HTTP ${res.status}`);
					return res.json();
				})
			]);
			
			studentInfo = me;
			renderTaskDetails(task);
			renderSubmissionHistory(task.studentSubmitList);

		} catch (error) {
			console.error(error);
			showError("데이터를 불러오는 중 오류가 발생했습니다: " + error.message);
		}
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		showError("");
		$submitButton.disabled = true;
		$submitButton.textContent = "제출 중...";

		try {
			if ($submitFileInput.files.length > 0) {
				await uploadClassroomFiles(lectureId, 'task', $submitFileInput, 'submit-file-id');
			}

			const payload = {
				submitDesc: $submitDesc.value,
				submitFileId: $submitFileId.value || null
			};

			const res = await fetch(apiBaseUrl, {
				method: "PATCH",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(payload)
			});

			if (res.status !== 204) {
				const errorText = await res.text().catch(() => "알 수 없는 오류");
				throw new Error(`제출 실패 (HTTP ${res.status}): ${errorText}`);
			}

			alert("과제를 성공적으로 제출했습니다.");
			location.reload();

		} catch (error) {
			console.error(error);
			showError(error.message);
		} finally {
			$submitButton.disabled = false;
			$submitButton.textContent = "제출하기";
		}
	};

	// --- Init
	const init = () => {
		if (!lectureId || !taskId) {
			showError("강의 또는 과제 정보를 찾을 수 없습니다.");
			return;
		}
		
		$submitForm.addEventListener("submit", handleSubmit);
		$showSubmitFormBtn.addEventListener("click", () => {
			prepareEditForm();
			switchView('form');
		});
		$cancelSubmitBtn.addEventListener("click", () => switchView('status'));
		
		loadData();
	};

	init();
})();