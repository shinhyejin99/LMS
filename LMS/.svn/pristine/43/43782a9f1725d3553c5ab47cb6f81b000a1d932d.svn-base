(() => {
	"use strict";

	const $root = document.querySelector("#attendance-root");
	if (!$root) return;

	// === Config & Helpers ===
	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const apiBase = $root.dataset.apiBase; // .../classroom/api/v1/professor/attendance
	const commonBase = `${ctx}/classroom/api/v1/common`;

	const bsModal = (id) => new bootstrap.Modal(document.getElementById(id), { backdrop: "static" });

	const $btnOpenCreate = document.getElementById("btn-open-create-modal");
	const $createModal = document.getElementById("createRoundModal");
	const $studentsModal = document.getElementById("studentsModal");
	const $modalRoundLabel = document.getElementById("modal-round-label");

	const $cardManual = document.getElementById("card-manual");
	const $cardQr = document.getElementById("card-qr");
	const $btnCreateManual = document.getElementById("btn-create-manual");
	const $btnCreateAllOk = document.getElementById("btn-create-all-ok");
	const $btnCreateAllNo = document.getElementById("btn-create-all-no");

	const $studentsTbody = document.getElementById("students-tbody");

	// 회차 목록 & 요약 카드
	const $roundTbody = document.getElementById("round-tbody");
	const $sumRatePresent = document.getElementById("sum-rate-present");
	const $sumRateAbsent = document.getElementById("sum-rate-absent");
	const $sumRateLateEarly = document.getElementById("sum-rate-late-early");

	// 단일 체크 패널 요소
	const $checkerPanel = document.getElementById("checker-panel");
	const $chkStudentNo = document.getElementById("chk-studentNo");
	const $chkName = document.getElementById("chk-name");
	const $chkGrade = document.getElementById("chk-grade");
	const $chkDept = document.getElementById("chk-dept");
	const $chkStatus = document.getElementById("chk-status");
	const $chkComment = document.getElementById("chk-comment");
	const $btnPrev = document.getElementById("btn-prev");
	const $btnSkip = document.getElementById("btn-skip");
	const $btnApplyNext = document.getElementById("btn-apply-next");
	const $chkPhoto = document.getElementById("chk-photo");

	let lastCreatedRound = null;   // 최근 생성 회차(제출 시 기본값)
	let currentRound = null;       // 현재 모달에서 작업 중인 회차

	/** @type {Array<{enrollId:string, studentNo:string, lastName:string, firstName:string, grade:string, univDeptName:string, attStatusCd?:string, attComment?:string}>} */
	let students = [];
	let curIdx = 0;
	/** @type {Map<string, {attStatusCd:string, attComment:string}>} */
	const pendingChanges = new Map();

	function alertError(msg, err) {
		console.error("[attendance] " + msg, err);
		const $a = document.getElementById("global-alert");
		if ($a) {
			$a.textContent = msg;
			$a.classList.remove("d-none");
		} else {
			window.alert(msg);
		}
	}

	// --- 출석 코드 → 라벨/뱃지
	const STATUS_LABEL = {
		ATTD_TBD: "미정",
		ATTD_OK: "출석",
		ATTD_NO: "결석",
		ATTD_EARLY: "조퇴",
		ATTD_LATE: "지각",
		ATTD_EXCP: "공결",
	};
	const STATUS_BADGE = {
		ATTD_TBD: "bg-secondary",
		ATTD_OK: "bg-success",
		ATTD_NO: "bg-danger",
		ATTD_EARLY: "bg-warning text-dark",
		ATTD_LATE: "bg-warning text-dark",
		ATTD_EXCP: "bg-info text-dark",
	};
	const labelOf = (cd) => STATUS_LABEL[cd] ?? cd ?? "-";
	const badgeOf = (cd) => STATUS_BADGE[cd] ?? "bg-light text-dark";

	// === 공용 유틸 ===
	const pad2 = (n) => (n < 10 ? "0" + n : "" + n);
	function fmtDateTimeLocal(s) {
		const d = new Date(s);
		if (Number.isNaN(d.getTime())) return "-";
		const yyyy = d.getFullYear();
		const mm = pad2(d.getMonth() + 1);
		const dd = pad2(d.getDate());
		const hh = pad2(d.getHours());
		const mi = pad2(d.getMinutes());
		return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
	}
	const percent = (num, den) => (den > 0 ? Math.round((num / den) * 1000) / 10 : 0);

	// === 회차 목록 가져오기 & 렌더 ===
	async function fetchRounds() {
		const url = `${apiBase}/${encodeURIComponent(lectureId)}/all`;
		console.debug("[attendance] fetchRounds →", url);
		const resp = await fetch(url);
		if (!resp.ok) {
			const text = await resp.text().catch(() => "");
			throw new Error(`회차 목록 조회 실패 (HTTP ${resp.status}) ${text}`);
		}
		const list = await resp.json();
		if (!Array.isArray(list)) throw new Error("서버 응답 형식 오류: 배열 아님");
		return list;
	}

	function renderRoundsTable(list) {
		if (!$roundTbody) return;
		if (!list.length) {
			$roundTbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted">생성된 출석 회차가 없습니다.</td></tr>`;
			setSummaryRates({ ok: 0, no: 0, early: 0, late: 0, excp: 0, total: 0 });
			return;
		}

		const sorted = [...list].sort((a, b) => b.lctRound - a.lctRound);

		const rows = sorted.map((r) => {
			const total = r.totalCnt ?? 0;
			const ok = r.okCnt ?? 0;
			const no = r.noCnt ?? 0;
			const early = r.earlyCnt ?? 0;
			const late = r.lateCnt ?? 0;
			const excp = r.excpCnt ?? 0;
			const tbd = r.tbdCnt ?? 0;

			return `
      <tr>
        <td>
          <a href="#" class="btn btn-sm btn-outline-primary fw-semibold"
             data-round="${r.lctRound}" title="이 회차로 출석체크 하기">#${r.lctPrintRound}</a>
        </td>
        <td class="text-nowrap">${fmtDateTimeLocal(r.attDay)}</td>
        <td><span class="badge ${badgeOf("ATTD_OK")}">${ok}</span></td>
        <td><span class="badge ${badgeOf("ATTD_NO")}">${no}</span></td>
        <td><span class="badge ${badgeOf("ATTD_EARLY")}">${early}</span></td>
        <td><span class="badge ${badgeOf("ATTD_LATE")}">${late}</span></td>
        <td><span class="badge ${badgeOf("ATTD_EXCP")}">${excp}</span></td>
        <td><span class="badge ${badgeOf("ATTD_TBD")}">${tbd}</span></td>
        <td class="text-end">
          <button type="button" class="btn btn-sm btn-outline-danger"
                  data-action="delete" data-round="${r.lctRound}">
            삭제
          </button>
        </td>
      </tr>
    `;
		}).join("");

		$roundTbody.innerHTML = rows;

		const totals = sorted.reduce((acc, r) => {
			acc.total += r.totalCnt ?? 0;
			acc.ok += r.okCnt ?? 0;
			acc.no += r.noCnt ?? 0;
			acc.early += r.earlyCnt ?? 0;
			acc.late += r.lateCnt ?? 0;
			acc.excp += r.excpCnt ?? 0;
			return acc;
		}, { ok: 0, no: 0, early: 0, late: 0, excp: 0, total: 0 });
		setSummaryRates(totals);
	}


	function setSummaryRates(t) {
		const total = t.total || 0;
		const presentRate = percent(t.ok, total);
		const absentRate = percent(t.no, total);
		const lateEarlyRate = percent(t.late + t.early, total);

		if ($sumRatePresent) $sumRatePresent.textContent = `${presentRate}%`;
		if ($sumRateAbsent) $sumRateAbsent.textContent = `${absentRate}%`;
		if ($sumRateLateEarly) $sumRateLateEarly.textContent = `${lateEarlyRate}%`;
	}

	// === 회차 생성 ===
	async function postCreateRound(defaultStatus /* "TBD" | "OK" | "NO" */) {
		const url = `${apiBase}/${encodeURIComponent(lectureId)}?default_status=${encodeURIComponent(defaultStatus)}`;
		const resp = await fetch(url, { method: "POST" });
		if (!resp.ok) {
			const text = await resp.text().catch(() => "");
			throw new Error(`회차 생성 실패 (HTTP ${resp.status}) ${text}`);
		}
		const roundInt = await resp.json();
		if (typeof roundInt !== "number") throw new Error("서버 응답 형식 오류: round number가 아님");
		return roundInt;
	}

	// === 학생 목록 조회(수동) ===
	async function fetchStudents(attRound) {
		const url = `${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(attRound)}/manual`;
		const resp = await fetch(url);
		if (!resp.ok) {
			const text = await resp.text().catch(() => "");
			throw new Error(`학생 목록 조회 실패 (HTTP ${resp.status}) ${text}`);
		}
		const list = await resp.json();
		if (!Array.isArray(list)) throw new Error("서버 응답 형식 오류: 배열 아님");
		return list;
	}

	// === 표 렌더링: 상태/비고만 표시 ===
	function renderStudentsTable(list) {
		if (!$studentsTbody) return;
		if (!list.length) {
			$studentsTbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">수강 학생이 없습니다.</td></tr>`;
			return;
		}

		const rows = list.map((s, idx) => {
			const pending = pendingChanges.get(s.enrollId);
			const statusCd = pending?.attStatusCd ?? s.attStatusCd ?? "ATTD_TBD";
			const comment = pending?.attComment ?? (s.attComment ?? "");

			return `
        <tr data-enroll-id="${s.enrollId}">
          <td class="text-muted text-center">${idx + 1}</td>
          <td class="text-nowrap">${s.studentNo ?? ""}</td>
          <td>${`${s.lastName ?? ""}${s.firstName ?? ""}`.trim() || "-"}</td>
          <td class="text-nowrap">${s.grade ?? "-"}</td>
          <td>${s.univDeptName ?? "-"}</td>
          <td><span class="badge ${badgeOf(statusCd)}">${labelOf(statusCd)}</span></td>
          <td class="text-start">
            <span class="d-inline-block text-truncate" style="max-width: 220px;" title="${escapeHtml(comment)}">
              ${escapeHtml(comment || "")}
            </span>
          </td>
        </tr>
      `;
		}).join("");

		$studentsTbody.innerHTML = rows;
	}

	// 특정 행 즉시 갱신
	function updateRow(enrollId) {
		const s = students.find((x) => x.enrollId === enrollId);
		if (!s) return;
		const tr = $studentsTbody.querySelector(`tr[data-enroll-id="${CSS.escape(enrollId)}"]`);
		if (!tr) return;

		const pending = pendingChanges.get(enrollId);
		const statusCd = pending?.attStatusCd ?? s.attStatusCd ?? "ATTD_TBD";
		const comment = pending?.attComment ?? (s.attComment ?? "");

		const statusTd = tr.children[5];
		const commentTd = tr.children[6];
		statusTd.innerHTML = `<span class="badge ${badgeOf(statusCd)}">${labelOf(statusCd)}</span>`;
		commentTd.innerHTML = `
      <span class="d-inline-block text-truncate" style="max-width: 220px;" title="${escapeHtml(comment)}">
        ${escapeHtml(comment || "")}
      </span>`;
	}

	function photoUrlOf(studentNo) {
		const ts = Date.now();
		return `${commonBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/photo?ts=${ts}`;
	}

	// === 단일 체크 패널 ===
	function showChecker(i) {
		if (!students.length) return;
		if (i < 0) i = 0;
		if (i >= students.length) i = students.length - 1;
		curIdx = i;

		const s = students[curIdx];
		const pending = pendingChanges.get(s.enrollId);
		const statusCd = pending?.attStatusCd ?? s.attStatusCd ?? "ATTD_TBD";
		const comment = pending?.attComment ?? (s.attComment ?? "");

		$chkStudentNo.textContent = s.studentNo ?? "-";
		$chkName.textContent = `${s.lastName ?? ""}${s.firstName ?? ""}`.trim() || "-";
		$chkGrade.textContent = s.grade ?? "-";
		$chkDept.textContent = s.univDeptName ?? "-";
		$chkStatus.value = statusCd;
		$chkComment.value = comment;

		$checkerPanel.classList.remove("d-none");

		$chkPhoto.src = photoUrlOf(s.studentNo);
		$chkPhoto.alt = `${(s.lastName ?? "") + (s.firstName ?? "")} 사진`;
		$chkPhoto.onerror = () => $chkPhoto.classList.add("d-none");
		$chkPhoto.onload = () => $chkPhoto.classList.remove("d-none");

		highlightCurrentRow(s.enrollId);
	}

	function highlightCurrentRow(enrollId) {
		$studentsTbody.querySelectorAll("tr").forEach((tr) => tr.classList.remove("table-active"));
		const tr = $studentsTbody.querySelector(`tr[data-enroll-id="${CSS.escape(enrollId)}"]`);
		if (tr) tr.classList.add("table-active");
	}

	function applyAndNext() {
		if (!students.length) return;
		const s = students[curIdx];
		const attStatusCd = $chkStatus.value;
		const attComment = $chkComment.value?.trim() ?? "";
		pendingChanges.set(s.enrollId, { attStatusCd, attComment });
		updateRow(s.enrollId);
		if (curIdx < students.length - 1) showChecker(curIdx + 1);
		else showChecker(curIdx);
	}

	function skip() {
		if (!students.length) return;
		if (curIdx < students.length - 1) showChecker(curIdx + 1);
	}

	function prev() {
		if (!students.length) return;
		if (curIdx > 0) showChecker(curIdx - 1);
	}

	// HTML escape
	function escapeHtml(str) {
		return String(str ?? "")
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");
	}

	// === 핵심: 특정 회차를 바로 열어 출석체크 ===
	async function openAttendanceForRound(attRound) {
		try {
			currentRound = attRound;
			if ($modalRoundLabel) $modalRoundLabel.textContent = String(attRound);

			bsModal("studentsModal").show();
			$studentsTbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">불러오는 중…</td></tr>`;

			students = await fetchStudents(attRound);
			pendingChanges.clear();
			renderStudentsTable(students);
			$checkerPanel.classList.add("d-none"); // 새로 열릴 때 깔끔하게
			if (students.length) showChecker(0);
		} catch (err) {
			alertError(err.message || "출석체크 화면 열기 실패", err);
		}
	}

	// === 생성 후 출석 진행 ===
	async function createThenAsk(defaultStatus) {
		try {
			const round = await postCreateRound(defaultStatus);
			lastCreatedRound = round;

			bootstrap.Modal.getInstance($createModal)?.hide();
			const go = window.confirm(`새 출석회차가 생성되었습니다.\n출석체크를 진행할까요?`);
			// 목록은 먼저 갱신
			initRoundList().catch((e) => console.warn("목록 갱신 실패(무시):", e));

			if (go) {
				await openAttendanceForRound(round);
			}
		} catch (err) {
			alertError(err.message || "알 수 없는 오류가 발생했습니다.", err);
		}
	}

	// === 제출 ===
	async function submitPendingChanges() {
		try {
			// 현재 모달에서 선택된 회차 우선, 없으면 마지막 생성 회차 사용
			const roundToSubmit = currentRound ?? lastCreatedRound;
			if (!roundToSubmit) {
				window.alert("회차 정보가 없습니다. 회차를 선택하거나 생성한 뒤 제출하세요.");
				return;
			}
			if (pendingChanges.size === 0) {
				if (!window.confirm("변경된 항목이 없습니다. 빈 목록을 제출할까요?")) return;
			}

			const payload = Array.from(pendingChanges, ([enrollId, v]) => ({
				enrollId,
				attStatusCd: v.attStatusCd,
				attComment: v.attComment ?? null,
			}));

			const url = `${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(roundToSubmit)}/manual`;
			const resp = await fetch(url, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(payload),
			});

			if (!resp.ok) {
				const text = await resp.text().catch(() => "");
				throw new Error(`제출 실패 (HTTP ${resp.status}) ${text}`);
			}

			console.group("[attendance] 제출 완료");
			console.log("round:", roundToSubmit);
			console.log("payload:", payload);
			console.groupEnd();

			window.alert("제출 요청을 보냈습니다. (서버는 현재 로깅만 수행)");
		} catch (err) {
			alertError(err.message || "제출 중 오류가 발생했습니다.", err);
		}
	}

	// === 초기 회차 목록 로드 ===
	async function initRoundList() {
		try {
			if ($roundTbody) {
				$roundTbody.innerHTML = `<tr><td colspan="9" class="text-center text-muted">로딩 중…</td></tr>`;
			}
			const rounds = await fetchRounds();
			renderRoundsTable(rounds);
		} catch (err) {
			alertError(err.message || "회차 목록 로딩 중 오류가 발생했습니다.", err);
			if ($roundTbody) {
				$roundTbody.innerHTML = `<tr><td colspan="9" class="text-center text-danger">목록을 불러오지 못했습니다.</td></tr>`;
			}
		}
	}
	
	// 회차 삭제
	async function deleteRound(attRound, btnEl) {
		try {
			if (!Number.isFinite(attRound)) return;
			const ok = window.confirm(`#${attRound} 회차를 삭제할까요?\n삭제 후 되돌릴 수 없습니다.`);
			if (!ok) return;

			// 버튼 잠금
			if (btnEl) {
				btnEl.disabled = true;
				btnEl.textContent = "삭제 중…";
			}

			const url = `${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(attRound)}`;
			const resp = await fetch(url, { method: "DELETE" });
			if (!resp.ok) {
				const text = await resp.text().catch(() => "");
				throw new Error(`삭제 실패 (HTTP ${resp.status}) ${text}`);
			}

			// 현재 열려있는 모달이 방금 삭제한 회차라면 닫기
			if (currentRound === attRound) {
				bootstrap.Modal.getInstance($studentsModal)?.hide();
				currentRound = null;
				pendingChanges.clear();
			}

			// 목록 새로고침
			await initRoundList();
		} catch (err) {
			alertError(err.message || "회차 삭제 중 오류가 발생했습니다.", err);
		} finally {
			if (btnEl) {
				btnEl.disabled = false;
				btnEl.textContent = "삭제";
			}
		}
	}

	
	
	// === Events ===
	$btnOpenCreate?.addEventListener("click", () => bsModal("createRoundModal").show());

	const doManual = () => createThenAsk("TBD");
	$cardManual?.addEventListener("click", doManual);
	$btnCreateManual?.addEventListener("click", doManual);

	$btnCreateAllOk?.addEventListener("click", () => createThenAsk("OK"));
	$btnCreateAllNo?.addEventListener("click", () => createThenAsk("NO"));

	$cardQr?.addEventListener("click", () => {
		window.alert("QR 출석은 준비중입니다. 수동 출석을 이용해 주세요.");
	});

	// 단일 체크 패널 이벤트
	$btnApplyNext?.addEventListener("click", applyAndNext);
	$btnSkip?.addEventListener("click", skip);
	$btnPrev?.addEventListener("click", prev);

	// 표 클릭 시 해당 학생으로 점프
	$studentsTbody?.addEventListener("click", (e) => {
		const tr = e.target.closest("tr[data-enroll-id]");
		if (!tr) return;
		const enrollId = tr.getAttribute("data-enroll-id");
		const idx = students.findIndex((s) => s.enrollId === enrollId);
		if (idx >= 0) showChecker(idx);
	});

	// ✅ 회차 버튼(#n) 클릭 → 모달 열기
	$roundTbody?.addEventListener("click", (e) => {
		// 1) 회차 열기 (#n 버튼)
		const a = e.target.closest('a[data-round]');
		if (a) {
			e.preventDefault();
			const round = Number(a.getAttribute('data-round'));
			if (Number.isFinite(round)) {
				openAttendanceForRound(round);
			}
			return;
		}

		// 2) 삭제 버튼
		const delBtn = e.target.closest('button[data-action="delete"][data-round]');
		if (delBtn) {
			e.preventDefault();
			const round = Number(delBtn.getAttribute('data-round'));
			if (Number.isFinite(round)) {
				deleteRound(round, delBtn);
			}
			return;
		}
	});

	document.getElementById("btn-debug-pending")?.addEventListener("click", () => {
		console.group("[attendance] 상태 디버깅용");
		console.log("currentRound:", currentRound, "lastCreatedRound:", lastCreatedRound);
		console.log("pendingChanges(Map):", pendingChanges);
		const arr = Array.from(pendingChanges, ([enrollId, v]) => ({ enrollId, ...v }));
		console.log("pendingChanges(Array):", arr);
		console.groupEnd();
	});

	document.getElementById("btn-submit-changes")?.addEventListener("click", submitPendingChanges);

	// 페이지 진입 시 회차 목록 초기화
	initRoundList();

})();
