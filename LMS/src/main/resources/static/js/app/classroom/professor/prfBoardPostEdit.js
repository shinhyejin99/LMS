// /js/app/classroom/professor/prfBoardPostEdit.js
import { uploadClassroomFiles } from "../common/uploadFiles.js";

(function() {
	"use strict";

	const $root = document.querySelector("#post-edit-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const postId = $root.dataset.postId || "";
	const apiBase = $root.dataset.apiBase || `${ctx}/classroom/api/v1/professor`;
	const redirectDtl = $root.dataset.redirectDetail || `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/board/${encodeURIComponent(postId)}`;

	const $error = document.getElementById("error-box");
	const $toast = document.getElementById("toast-box");
	const $form = document.getElementById("edit-form");
	const $title = document.getElementById("title");
	const $postType = document.getElementById("postType");
	const $fileId = document.getElementById("fileId");
	const $files = document.getElementById("files");
	const $fileList = document.getElementById("file-list");
	const $stPub = document.getElementById("stPublished");
	const $stDraft = document.getElementById("stDraft");
	const $stSch = document.getElementById("stScheduled");
	const $schRow = document.getElementById("schedule-row");
	const $pDate = document.getElementById("publishDate");
	const $pTime = document.getElementById("publishTime");
	const $btnCancel = document.getElementById("btn-cancel");

	const $csrfToken = document.getElementById("csrfToken");

	let editor = null;

	// ---------- utils
	const showError = (msg) => {
		if (!$error) return;
		$error.classList.remove("d-none");
		$error.textContent = msg;
		console.error("[error]", msg);
	};
	const hideError = () => {
		if (!$error) return;
		$error.classList.add("d-none");
		$error.textContent = "";
	};
	const showToast = (msg = "저장되었습니다.") => {
		if (!$toast) return;
		$toast.textContent = msg;
		$toast.classList.remove("d-none");
		setTimeout(() => $toast.classList.add("d-none"), 2000);
	};

	const pad2 = (n) => String(n).padStart(2, "0");
	const fmtDate = (d) => `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
	const fmtTime = (d) => `${pad2(d.getHours())}:${pad2(d.getMinutes())}`;

	// 서버 기준 status 산출(초기 셋업용)
	const computeStatus = (post) => {
		if (post.tempSaveYn === "Y") return "DRAFT";
		if (post.revealAt) {
			const now = new Date();
			const r = new Date(post.revealAt);
			if (r.getTime() > now.getTime()) return "SCHEDULED";
		}
		return "PUBLISHED";
	};

	const setStatusUI = (status, revealAtIso) => {
		if (status === "DRAFT") {
			$stDraft.checked = true;
			$schRow.classList.add("d-none");
		} else if (status === "SCHEDULED") {
			$stSch.checked = true;
			$schRow.classList.remove("d-none");
			if (revealAtIso) {
				const d = new Date(revealAtIso);
				if (!Number.isNaN(d.getTime())) {
					$pDate.value = fmtDate(d);
					$pTime.value = fmtTime(d);
				}
			}
		} else {
			$stPub.checked = true;
			$schRow.classList.add("d-none");
		}
	};

	const getStatusFromUI = () => {
		if ($stDraft.checked) return "DRAFT";
		if ($stSch.checked) return "SCHEDULED";
		return "PUBLISHED";
	};

	const buildRevealAt = () => {
		const status = getStatusFromUI();
		if (status !== "SCHEDULED") return null;
		const d = ($pDate?.value || "").trim();
		const t = ($pTime?.value || "").trim();
		if (!d || !t) return null; // 서버에서 검증
		// 로컬 시각→서버에서 KST로 간주 저장한다고 가정
		return `${d}T${t}:00`;
	};

	// ---------- CKEditor
	async function ensureEditor() {
		if (editor) return editor;
		editor = await ClassicEditor.create(document.querySelector("#ck-editor"));
		return editor;
	}

	// ---------- 초기 데이터 로딩
	async function loadPost() {
		const url = `${apiBase}/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`;
		console.debug("[GET]", url);
		const res = await fetch(url, { headers: { "Accept": "application/json" } });
		if (!res.ok) throw new Error(`HTTP ${res.status}`);
		return await res.json();
	}

	function renderFiles(attachFileId, attachFileList) {
		if (attachFileId) $fileId.value = attachFileId;
		if (!$fileList) return;

		const files = Array.isArray(attachFileList) ? attachFileList : [];
		if (files.length === 0) {
			$fileList.textContent = "현재 첨부 없음";
			return;
		}
		const html = files.map((af) => {
			const name = (af.originName || `파일 ${af.fileOrder}`) + (af.extension ? `.${af.extension}` : "");
			const size = typeof af.fileSize === "number" ? ` (${af.fileSize.toLocaleString()}B)` : "";
			return `• ${name}${size}`;
		}).join("<br/>");
		$fileList.innerHTML = html;
	}

	// ---------- 이벤트
	function bindEvents() {
		// 상태 라디오 ↔ 예약 입력
		[$stPub, $stDraft, $stSch].forEach(r => {
			r.addEventListener("change", () => {
				if ($stSch.checked) $schRow.classList.remove("d-none");
				else $schRow.classList.add("d-none");
			});
		});

		// 파일 업로드(대체)
		if ($files) {
			$files.addEventListener("change", async () => {
				if (!$files.files || $files.files.length === 0) return;
				try {
					const data = await uploadClassroomFiles(lectureId, "board", $files, "fileId");
					// 성공하면 hidden fileId가 채워짐
					const names = Array.from($files.files).map(f => `• ${f.name} (${(f.size / 1024).toFixed(1)} KB)`).join("<br/>");
					$fileList.innerHTML = names || "현재 첨부 없음";
					console.log("[upload] success fileId:", data?.fileId || $fileId.value);
				} catch (e) {
					showError(`파일 업로드 실패: ${e.message}`);
				}
			});
		}

		// 취소 → 상세로
		if ($btnCancel) {
			$btnCancel.addEventListener("click", (e) => {
				e.preventDefault();
				window.location.href = redirectDtl;
			});
		}

		// 저장(PUT)
		if ($form) {
			$form.addEventListener("submit", async (e) => {
				e.preventDefault();
				hideError();

				try {
					const ed = await ensureEditor();
					const content = (ed.getData() || "").trim();

					// 클라이언트 쪽 간단 검증(서버 검증은 그대로 둠)
					if (!$title.value.trim()) throw new Error("제목을 입력하세요.");
					if ($title.value.trim().length > 100) throw new Error("제목은 최대 100자입니다.");
					if (!content) throw new Error("내용을 입력하세요.");
					if (content.length > 1000) {
						throw new Error("내용은 최대 1000자입니다. (서버 제한)");
					}

					// 상태 매핑
					const status = getStatusFromUI();
					let tempSaveYn = "N";
					let revealAt = null;
					if (status === "DRAFT") {
						tempSaveYn = "Y";
					} else if (status === "SCHEDULED") {
						tempSaveYn = "N";
						revealAt = buildRevealAt();
						if (!revealAt) throw new Error("예약 게시를 선택했으면 날짜/시각을 모두 지정하세요.");
					}

					const payload = {
						title: $title.value.trim(),
						content,
						fileId: $fileId.value || null,
						postType: $postType.value,
						tempSaveYn,
						revealAt
					};

					const url = `${apiBase}/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`;
					
					console.log('url : ', url);
					
					const headers = {
						"Accept": "application/json",
						"Content-Type": "application/json"
					};
					if ($csrfToken?.value) headers["X-CSRF-TOKEN"] = $csrfToken.value;

					console.debug("[PUT]", url, payload);
					const res = await fetch(url, { method: "PUT", headers, body: JSON.stringify(payload) });
					if (res.status === 204) {
						showToast();
						// 잠깐 보여주고 상세로
						setTimeout(() => window.location.replace(redirectDtl), 300);
						return;
					}
					const txt = await res.text();
					throw new Error(`수정 실패 (HTTP ${res.status}) ${txt?.slice(0, 200) || ""}`);
				} catch (err) {
					console.error(err);
					showError(err.message || "저장 중 오류가 발생했습니다.");
				}
			});
		}
	}

	// ---------- 초기화
	async function init() {
		try {
			const ed = await ensureEditor();
			const post = await loadPost();

			// 폼 채우기
			$title.value = post.title || "";
			$postType.value = post.postType || "NOTICE";
			renderFiles(post.attachFileId, post.attachFileList);

			// 에디터 내용
			ed.setData(post.content || "");

			// 상태/예약
			const status = computeStatus(post);
			setStatusUI(status, post.revealAt);

			bindEvents();
			console.debug("[edit] ready", { lectureId, postId });
		} catch (e) {
			console.error(e);
			showError("게시글을 불러오는 중 오류가 발생했습니다.");
		}
	}

	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();
