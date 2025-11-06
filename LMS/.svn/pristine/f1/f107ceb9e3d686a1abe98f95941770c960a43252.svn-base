import { uploadClassroomFiles } from "../common/uploadFiles.js";

(() => {
	"use strict";
	const $root = document.querySelector("#task-form-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const apiBase = $root.dataset.apiBase || "";

	const $form = document.getElementById("task-form");
	const $name = document.getElementById("task-name");
	const $desc = document.getElementById("task-desc");
	const $startAt = document.getElementById("start-at");
	const $endAt = document.getElementById("end-at");
	const $fileInput = document.getElementById("file-input");
	const $attachFileId = document.getElementById("attach-file-id");
	const $currentBox = document.getElementById("current-attach-box");
	const $currentFileId = document.getElementById("current-file-id");
	const $currentFileList = document.getElementById("current-file-list");
	const $error = document.getElementById("error-box");
	const $info = document.getElementById("info-box");

	let editorInstance = null;

	const showError = (m) => { $error.textContent = m || "오류가 발생했습니다."; $error.classList.remove("d-none"); $info.classList.add("d-none"); };
	const showInfo = (m) => { $info.textContent = m || "처리되었습니다."; $info.classList.remove("d-none"); $error.classList.add("d-none"); };
	const clearInfo = () => { $error.classList.add("d-none"); $info.classList.add("d-none"); };
	const toIsoOrNull = (v) => { v = (v || "").trim(); return v.length ? v : null; };
	const toggleBusy = (b) => { if ($fileInput) $fileInput.disabled = !!b; };

	const renderAttach = (bundleId, details = []) => {
		$attachFileId.value = bundleId || "";
		$currentFileId.textContent = bundleId || "(없음)";
		$currentFileList.innerHTML = "";
		if (details.length) {
			const items = details.map(d => {
				const name = d.originName ?? d.originalName ?? `파일 ${d.fileOrder ?? d.order ?? ""}`;
				const ext = d.extension ? `.${d.extension}` : "";
				const size = typeof d.fileSize === "number" ? ` (${d.fileSize.toLocaleString()}B)` : "";
				return `<li class="list-group-item">${name}${ext}${size}</li>`;
			}).join("");
			$currentFileList.insertAdjacentHTML("beforeend", items);
		}
		$currentBox.classList.remove("d-none");
	};

	// 파일 선택 → 즉시 업로드 → 현재 첨부 대체
	$fileInput?.addEventListener("change", async () => {
		clearInfo();
		if (!$fileInput.files || $fileInput.files.length === 0) return;
		try {
			if (!lectureId) throw new Error("lectureId가 비어있습니다.");
			toggleBusy(true);
			const data = await uploadClassroomFiles(lectureId, "task", $fileInput, "attach-file-id");
			const bundleId =
				data.bundleId || data.fileId ||
				(Array.isArray(data.fileIds) && data.fileIds[0]) ||
				(Array.isArray(data.files) && data.files[0]?.fileId) ||
				(Array.isArray(data.details) && data.details[0]?.fileId) || "";
			const details =
				(Array.isArray(data.files) && data.files) ||
				(Array.isArray(data.details) && data.details) || [];
			renderAttach(bundleId, details);
			showInfo("첨부가 업로드되어 현재 첨부로 대체되었습니다.");
		} catch (e) { console.error(e); showError(e?.message); }
		finally { toggleBusy(false); }
	});

	// CKEditor
	const initEditor = async () => {
		if (!window.ClassicEditor || !$desc) return;
		editorInstance = await window.ClassicEditor.create($desc, {
			toolbar: ["heading", "|", "bold", "italic", "link", "bulletedList", "numberedList", "blockQuote", "|", "insertTable", "undo", "redo"],
			placeholder: "과제 목적, 제출 형식, 평가 기준 등을 작성하세요.",
		});
	};

	$form?.addEventListener("submit", async (e) => {
		e.preventDefault(); clearInfo();
		try {
			if (!lectureId) throw new Error("lectureId가 비어있습니다.");
			if (!apiBase) throw new Error("API base 누락");

			if (editorInstance) $desc.value = editorInstance.getData();

			const indivtaskName = ($name.value || "").trim();
			const indivtaskDesc = ($desc.value || "").trim();
			const startAt = toIsoOrNull($startAt.value);
			const endAt = toIsoOrNull($endAt.value);
			const attachFileId = ($attachFileId.value || "").trim();

			if (!indivtaskName) throw new Error("과제명을 입력하세요.");
			if (!indivtaskDesc) throw new Error("과제설명을 입력하세요.");
			if (!startAt) throw new Error("과제 시작일을 입력하세요.");
			if (startAt && endAt && startAt > endAt) throw new Error("마감일은 시작일 이후여야 합니다.");

			const payload = { indivtaskName, indivtaskDesc, startAt, endAt, attachFileId };

			const url = `${apiBase}/${encodeURIComponent(lectureId)}/indiv`;
			const resp = await fetch(url, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				credentials: "same-origin",
				body: JSON.stringify(payload),
			});
			if (!resp.ok) {
				const msg = await resp.text().catch(() => resp.statusText);
				throw new Error(`저장 실패(${resp.status}): ${msg}`);
			}
			const loc = resp.headers.get("Location");
			showInfo("저장되었습니다.");
			location.href = loc || `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv`;
		} catch (err) { console.error(err); showError(err?.message); }
	});

	document.addEventListener("DOMContentLoaded", initEditor);
})();
