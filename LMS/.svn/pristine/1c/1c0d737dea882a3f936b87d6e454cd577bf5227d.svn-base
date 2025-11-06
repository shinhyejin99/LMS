import { uploadClassroomFiles } from "../common/uploadFiles.js";

(() => {
	"use strict";

	const $root = document.querySelector("#task-edit-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const indivtaskId = $root.dataset.indivtaskId || "";
	const commonBase = $root.dataset.commonBase || "";
	const profBase = $root.dataset.profBase || "";
	const detailBase = profBase || commonBase;

	const $form = document.getElementById("task-edit-form");
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

	const showError = (msg) => {
		if (!$error || !$info) return;
		$error.textContent = msg || "오류가 발생했습니다.";
		$error.classList.remove("d-none");
		$info.classList.add("d-none");
	};

	const showInfo = (msg) => {
		if (!$info || !$error) return;
		$info.textContent = msg || "처리되었습니다.";
		$info.classList.remove("d-none");
		$error.classList.add("d-none");
	};

	const clearInfo = () => {
		if ($error) $error.classList.add("d-none");
		if ($info) $info.classList.add("d-none");
	};

	const pad = (n) => String(n).padStart(2, "0");
	const isoToLocalDT = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return "";
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const toggleBusy = (busy) => { if ($fileInput) $fileInput.disabled = !!busy; };

	const resolveBundleId = (bundleId, details) => {
		if (bundleId) return bundleId;
		if (!Array.isArray(details)) return "";
		for (const item of details) {
			if (item?.bundleId) return item.bundleId;
			if (item?.fileId) return item.fileId;
		}
		return "";
	};

	const normalizeAttachDetails = (raw) => {
		if (Array.isArray(raw)) return raw;
		if (raw?.attachFileList && Array.isArray(raw.attachFileList)) return raw.attachFileList;
		return [];
	};

	const renderAttach = (bundleId, details = []) => {
		const effectiveId = resolveBundleId(bundleId, details);
		if ($attachFileId) $attachFileId.value = effectiveId || "";
		if ($currentFileId) $currentFileId.textContent = effectiveId || "(없음)";
		if ($currentFileList) {
			$currentFileList.innerHTML = "";
			if (Array.isArray(details) && details.length > 0) {
				const items = details.map((d) => {
					const name = d.originName ?? d.originalName ?? `첨부 ${d.fileOrder ?? d.order ?? ""}`;
					const ext = d.extension ? `.${d.extension}` : "";
					const size = typeof d.fileSize === "number" ? ` (${d.fileSize.toLocaleString()}B)` : "";
					const order = d.fileOrder ?? d.order;
					const href = (order == null)
						? "#"
						: `${commonBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}/attach/${encodeURIComponent(order)}`;
					return `
          <li class="list-group-item d-flex justify-content-between align-items-center">
            <div class="text-truncate" style="max-width:70%;">${name}${ext}${size}</div>
            <a class="btn btn-sm btn-outline-secondary" href="${href}">다운로드</a>
          </li>
        `;
				}).join("");
				$currentFileList.insertAdjacentHTML("beforeend", items);
			}
		}
		if ($currentBox) $currentBox.classList.remove("d-none");
	};

	$fileInput?.addEventListener("change", async () => {
		clearInfo();
		if (!$fileInput.files || $fileInput.files.length === 0) return;
		try {
			if (!lectureId) throw new Error("lectureId가 없습니다.");
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
			showInfo("첨부 파일이 업로드되어 교체되었습니다.");
		} catch (err) {
			console.error(err);
			showError(err?.message);
		} finally {
			toggleBusy(false);
		}
	});

	const initEditor = async () => {
		if (!window.ClassicEditor || !$desc) return;
		editorInstance = await window.ClassicEditor.create($desc, {
			toolbar: ["heading", "|", "bold", "italic", "link", "bulletedList", "numberedList", "blockQuote", "|", "insertTable", "undo", "redo"]
		});
	};

	const init = async () => {
		try {
			if (!lectureId || !indivtaskId) throw new Error("잘못된 요청입니다.(lectureId/indivtaskId)");
			await initEditor();

			const url = `${detailBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`;
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`과제 조회 실패(HTTP ${res.status})`);
			const task = await res.json();

			$name.value = task.indivtaskName || "";
			if (editorInstance) editorInstance.setData(task.indivtaskDesc || "");
			$startAt.value = isoToLocalDT(task.startAt);
			$endAt.value = isoToLocalDT(task.endAt);

			const details = normalizeAttachDetails(task.attachFileList);
			const bundleId = task.fileId || task.attachFileId || "";
			renderAttach(bundleId, details);
		} catch (err) {
			console.error(err);
			showError(err?.message);
		}
	};

	$form?.addEventListener("submit", async (e) => {
		e.preventDefault();
		clearInfo();
		try {
			if (!lectureId || !indivtaskId) throw new Error("잘못된 요청입니다.");
			if (editorInstance) $desc.value = editorInstance.getData();

			const indivtaskName = ($name.value || "").trim();
			const indivtaskDesc = ($desc.value || "").trim();
			const startAt = ($startAt.value || "").trim();
			const endAt = ($endAt.value || "").trim();
			const attachFileIdValue = ($attachFileId?.value || "").trim();

			if (!indivtaskName) throw new Error("과제명을 입력해주세요.");
			if (!indivtaskDesc) throw new Error("과제 내용을 입력해주세요.");
			if (!startAt) throw new Error("시작 일시를 입력해주세요.");
			if (startAt && endAt && startAt > endAt) throw new Error("시작 일시는 마감 일시 이전이어야 합니다.");

			const payload = {
				indivtaskId,
				indivtaskName,
				indivtaskDesc,
				startAt,
				endAt: endAt || null,
				attachFileId: attachFileIdValue // 업로드로 교체되었거나 기존 값 유지
			};

			const url = `${profBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`;
			const resp = await fetch(url, {
				method: "PUT",
				headers: { "Content-Type": "application/json", "Accept": "application/json" },
				credentials: "same-origin",
				body: JSON.stringify(payload)
			});
			if (!resp.ok) {
				const msg = await resp.text().catch(() => resp.statusText);
				throw new Error(`과제 저장 실패(${resp.status}): ${msg}`);
			}
			showInfo("저장되었습니다.");
			location.replace(`${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/${encodeURIComponent(indivtaskId)}`);
		} catch (err) {
			console.error(err);
			showError(err?.message);
		}
	});

	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();
