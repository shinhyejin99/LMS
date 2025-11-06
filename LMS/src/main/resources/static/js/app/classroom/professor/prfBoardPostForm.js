/**
 *
 * << 개정이력(Modification Information) >>
 *  -----------    -------------    ---------------------------
 *  2025. 10. 2.   송태호           최초 생성
 *
 * 역할
 * - CKEditor5 초기화
 * - 상태 라디오 변경 시 예약 입력행 토글(d-none)
 * - 파일 선택시 파일 리스트 표시(미리보기용)
 * - 폼 submit 가로채서 JSON으로 직렬화 후 POST 전송
 * - 서버 컨트롤러 @PostMapping("/{lectureId}/post") 에 맞게 payload 매핑
 * 
 * 변경사항
 * - CSRF 관련 코드 제거
 * - 예약 미설정 시 revealAt = null (즉시 게시 포함)
 */

(function() {
	// 유틸: DOM 선택
	const $ = (sel, root = document) => root.querySelector(sel);
	const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

	document.addEventListener("DOMContentLoaded", async () => {
		const root = $("#post-root");
		if (!root) return;

		const lectureId = root.dataset.lectureId || "";
		const apiBase = root.dataset.apiBase || ""; // ex) /classroom/api/v1/professor/board
		const redirectUrl = root.dataset.redirect || ""; // 저장 후 이동할 곳

		const form = $("#post-form");
		const scheduleRow = $("#schedule-row");
		const radioStatus = $$('input[name="status"]', form);
		const publishDate = $("#publishDate");
		const publishTime = $("#publishTime");

		const alertBox = $("#alert-box");
		const toastBox = $("#toast-box");

		const titleInput = $("#title");
		const postTypeSelect = $("#postType");
		const fileIdHiddenInput = $("#fileId");

		// ===== 1) CKEditor 초기화 =====
		let editor;
		const editorEl = $("#ck-editor");
		try {
			if (window.__postEditor) {
				editor = window.__postEditor;
			} else {
				// CKEditor5 CDN이 로드되어 있어야 함
				editor = await ClassicEditor.create(editorEl);
				window.__postEditor = editor; // 전역 보관 (중복 초기화 방지)
			}
		} catch (e) {
			console.error("CKEditor 초기화 실패:", e);
		}

		// ===== 2) 상태 라디오 -> 예약행 토글 =====
		const toggleScheduleRow = () => {
			const val = (form.querySelector('input[name="status"]:checked') || {}).value;
			if (val === "SCHEDULED") {
				scheduleRow.classList.remove("d-none");
			} else {
				scheduleRow.classList.add("d-none");
			}
		};
		radioStatus.forEach((r) => r.addEventListener("change", toggleScheduleRow));
		toggleScheduleRow(); // 초기 반영

		// ===== 4) JSON 직렬화 & 전송 =====
		// LocalDateTime 직렬화: 'yyyy-MM-ddTHH:mm:ss' 형태로 보냄
		const toLocalDateTimeString = (dateStr, timeStr) => {
			if (!dateStr || !timeStr) return null;
			const [hh, mm] = timeStr.split(":").map(Number);
			return `${dateStr}T${String(hh).padStart(2, "0")}:${String(mm).padStart(
				2,
				"0"
			)}:00`;
		};

		const buildPayload = () => {
			const status = (form.querySelector('input[name="status"]:checked') || {}).value;
			const title = titleInput?.value?.trim() || "";
			const content = editor ? editor.getData() : editorEl?.value ?? "";
			const postType = postTypeSelect?.value || "NOTICE";
			const fileId = fileIdHiddenInput.value;

			// 기본값: 예약 미설정 → revealAt = null
			let tempSaveYn = "N";
			let revealAt = null;

			if (status === "DRAFT") {
				tempSaveYn = "Y";
				revealAt = null; // 명시적
			} else if (status === "PUBLISHED") {
				tempSaveYn = "N";
				revealAt = null; // 즉시 게시지만 revealAt은 보내지 않음
			} else if (status === "SCHEDULED") {
				tempSaveYn = "N";
				// 예약 선택 시에만 날짜+시간을 revealAt으로 세팅
				revealAt = toLocalDateTimeString(publishDate?.value, publishTime?.value);
			}


			return {
				title,
				content,
				fileId,
				postType,
				tempSaveYn,
				revealAt,
			};
		};

		const showAlert = (msg) => {
			alertBox.textContent = msg;
			alertBox.classList.remove("d-none");
			toastBox.classList.add("d-none");
		};
		const showToast = (msg) => {
			toastBox.textContent = msg;
			toastBox.classList.remove("d-none");
			alertBox.classList.add("d-none");
		};

		form.addEventListener("submit", async (e) => {
			e.preventDefault();

			const payload = buildPayload();

			// 간단 검증
			if (!payload.title) {
				showAlert("제목을 입력하세요.");
				titleInput?.focus();
				return;
			}
			if (!payload.content || payload.content.replace(/<[^>]*>/g, "").trim().length === 0) {
				showAlert("내용을 입력하세요.");
				return;
			}
			// 예약 선택 시엔 날짜/시간 필수
			if (
				(form.querySelector('input[name="status"]:checked') || {}).value === "SCHEDULED" &&
				!payload.revealAt
			) {
				showAlert("예약 게시를 선택하셨습니다. 날짜와 시간을 모두 선택하세요.");
				return;
			}

			const url = `${apiBase}/${encodeURIComponent(lectureId)}/post`;

			try {
				const res = await fetch(url, {
					method: "POST",
					headers: {
						"Content-Type": "application/json"
					},
					credentials: "same-origin",
					body: JSON.stringify(payload),
				});

				if (!res.ok) {
					const text = await res.text().catch(() => "");
					throw new Error(`HTTP ${res.status} - ${text || "요청 실패"}`);
				}

				showToast("저장되었습니다.");
				if (redirectUrl) {
					window.location.assign(redirectUrl);
				}
			} catch (err) {
				console.error(err);
				showAlert(`저장 중 오류가 발생했습니다. ${err.message}`);
			}
		});
	});
})();
