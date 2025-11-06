import { uploadClassroomFiles } from "../common/uploadFiles.js";

(() => {
	"use strict";

	const $root = document.querySelector("#grouptask-form-root");
	if (!$root) return;

	// ===== dataset & DOM =====
	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const rawStudentsBase = $root.dataset.commonBase || `${ctx}/classroom/api/v1/common`;
	const studentsBase = rawStudentsBase.replace("/api/v1/common", "/api/v1/professor");
	const profBase = $root.dataset.profBase || "";     // (작성 완료 시 사용할 예정)

	const $form = document.getElementById("grouptask-form");
	const $name = document.getElementById("grouptask-name");
	const $desc = document.getElementById("grouptask-desc");
	const $startAt = document.getElementById("start-at");
	const $endAt = document.getElementById("end-at");

	const $fileInput = document.getElementById("file-input");
	const $attachFileId = document.getElementById("attach-file-id");
	const $currentBox = document.getElementById("current-attach-box");
	const $currentFileId = document.getElementById("current-file-id");
	const $currentFileList = document.getElementById("current-file-list");

	const $studentList = document.getElementById("student-list");
	const $studentSearch = document.getElementById("student-search");
	const $btnRefreshStudents = document.getElementById("btn-refresh-students");
	const $btnAssignSelected = document.getElementById("btn-assign-selected");
	const $btnUnassignAll = document.getElementById("btn-unassign-all");
	const $groupList = document.getElementById("group-list");
	const $btnAddGroup = document.getElementById("btn-add-group");
	const $btnRemoveEmptyGroups = document.getElementById("btn-remove-empty-groups");

	const $error = document.getElementById("error-box");
	const $info = document.getElementById("info-box");
	const $debug = document.getElementById("debug-json");

	let editorInstance = null;

	// ===== state =====
	const STATE = {
		students: /** @type {Array<{enrollId:string, studentNo:string, studentName:string, deptName?:string}>} */([]),
		groups: /** @type {Array<{tempId:string, groupName:string, leaderEnrollId:string|null, members:string[]}>} */([]), // members: enrollId[]
	};

	// ===== utils =====
	const uid = (p = "G") => `${p}${Math.random().toString(36).slice(2, 10)}`;
	const showError = (m) => { $error.textContent = m || "오류가 발생했습니다."; $error.classList.remove("d-none"); $info.classList.add("d-none"); };
	const showInfo = (m) => { $info.textContent = m || "처리되었습니다."; $info.classList.remove("d-none"); $error.classList.add("d-none"); };
	const clearInfo = () => { $error.classList.add("d-none"); $info.classList.add("d-none"); };
	const toIsoOrNull = (v) => { v = (v || "").trim(); return v.length ? v : null; };
	const pad = (n) => String(n).padStart(2, "0");
	const isoToLocalDT = (iso) => { if (!iso) return ""; const d = new Date(iso); if (Number.isNaN(d.getTime())) return ""; return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`; };
	const escapeHtml = (s) => String(s).replace(/[&<>"']/g, (m) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[m]));
	const toggleBusy = (b) => { if ($fileInput) $fileInput.disabled = !!b; };
	const normalizeLocalDT = (v) => {
		if (!v) return null;
		const s = String(v).trim();
		if (!s) return null;
		if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(s)) return `${s}:00`;
		return s;
	};

	// ===== CKEditor =====
	const initEditor = async () => {
		if (!window.ClassicEditor || !$desc) return;
		editorInstance = await window.ClassicEditor.create($desc, {
			toolbar: ["heading", "|", "bold", "italic", "link", "bulletedList", "numberedList", "blockQuote", "|", "insertTable", "undo", "redo"],
			placeholder: "과제 목적, 제출 형식, 평가 기준 등을 작성하세요.",
		});
	};

	// ===== students =====
	// 공용 API: /classroom/api/v1/professor/{lectureId}/students
	const fetchStudents = async () => {
		const url = `${studentsBase}/${encodeURIComponent(lectureId)}/students`;
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`학생 목록 조회 실패 (HTTP ${res.status})`);
			const data = await res.json();
			if (!Array.isArray(data)) throw new Error("학생 목록 응답 형식이 올바르지 않습니다.");

			// 수강중(취소/철회 제외)만 남기기
			const EXCLUDE = new Set(["ENR_CANCEL", "ENR_WITHDRAW"]);
			STATE.students = data
				.filter(s => !EXCLUDE.has(s.enrollStatusCd))
				.map(s => ({
					enrollId: s.enrollId,                       // 조 배치의 key
					studentNo: s.studentNo || "",               // 학번
					studentName: `${s.lastName || ""}${s.firstName || ""}`.trim(),  // 이름(성+이름)
					deptName: s.univDeptName || ""              // 소속학과명
				}))
				.filter(s => !!s.enrollId);

			renderStudentList();
			showInfo(`학생 ${STATE.students.length}명 불러왔습니다.`);
		} catch (e) {
			console.error(e);
			showError(e?.message || "학생 목록 조회 중 오류");
			STATE.students = [];
			renderStudentList();
		}
	};


	const renderStudentList = () => {
		const q = ($studentSearch.value || "").trim().toLowerCase();

		// 이미 그룹에 배치된 학생은 왼쪽 목록에서 제외
		const selectedEnrolls = new Set(STATE.groups.flatMap(g => g.members));

		const list = STATE.students.filter(s => {
			if (selectedEnrolls.has(s.enrollId)) return false;
			if (!q) return true;
			return (
				(s.studentNo || "").toLowerCase().includes(q) ||
				(s.studentName || "").toLowerCase().includes(q) ||
				(s.deptName || "").toLowerCase().includes(q)
			);
		});

		$studentList.innerHTML =
			list
				.map(
					s => `
      <label class="d-flex align-items-center justify-content-between px-2 py-1 border-bottom">
        <span class="small text-truncate">
          <input type="checkbox" class="form-check-input me-2" data-enroll="${s.enrollId}">
          <b>${s.studentNo}</b> · ${s.studentName}
          ${s.deptName ? `<small class="text-muted">/ ${s.deptName}</small>` : ""}
        </span>
      </label>`
				)
				.join("") || `<div class="p-2 text-muted small">표시할 학생이 없습니다.</div>`;
	};


	// ===== groups =====
	const addGroup = () => {
		STATE.groups.push({
			tempId: uid("GRP"),
			groupName: `그룹 ${STATE.groups.length + 1}`,
			leaderEnrollId: null,
			members: []
		});
		renderGroups();
	};

	const removeEmptyGroups = () => {
		const before = STATE.groups.length;
		STATE.groups = STATE.groups.filter(g => g.members.length > 0);
		renderGroups();
		showInfo(`빈 그룹 ${before - STATE.groups.length}개 삭제`);
	};

	const renderGroups = () => {
		$groupList.innerHTML = STATE.groups.map(g => {
			const membersHtml = g.members.map(enrollId => {
				const s = STATE.students.find(x => x.enrollId === enrollId) || { studentName: "(제외됨)", studentNo: "" };
				const isLeader = g.leaderEnrollId === enrollId;
				return `
    <div class="member-badge ${isLeader ? "leader" : ""}" data-enroll="${enrollId}">
      <span><b>${s.studentNo || ""}</b> · ${s.studentName || ""}</span>
      ${isLeader ? `<span class="badge text-bg-primary">조장</span>` : ""}
      <span class="member-actions">
        ${!isLeader ? `<button type="button" class="btn btn-sm btn-outline-primary btn-set-leader" data-gid="${g.tempId}" data-enroll="${enrollId}">조장지정</button>` : ""}
        <button type="button" class="btn btn-sm btn-outline-danger btn-remove-member" data-gid="${g.tempId}" data-enroll="${enrollId}">제외</button>
      </span>
    </div>
  `;
			}).join("");


			return `
        <div class="group-card" data-gid="${escapeHtml(g.tempId)}">
          <div class="group-header mb-2">
            <div class="d-flex align-items-center gap-2">
              <input class="form-control form-control-sm group-name-input" data-gid="${escapeHtml(g.tempId)}" value="${escapeHtml(g.groupName)}" style="width:220px;">
            </div>
            <div class="d-flex gap-2">
              <button type="button" class="btn btn-sm btn-outline-secondary btn-clear-members" data-gid="${escapeHtml(g.tempId)}">전원 해제</button>
              <button type="button" class="btn btn-sm btn-outline-danger btn-delete-group" data-gid="${escapeHtml(g.tempId)}">그룹 삭제</button>
            </div>
          </div>
          <div class="group-members">
            ${membersHtml || `<div class="text-muted small">아직 배치된 조원이 없습니다.</div>`}
          </div>
        </div>
      `;
		}).join("") || `<div class="p-2 text-muted small">그룹이 없습니다. ‘그룹 추가’를 눌러 생성하세요.</div>`;
	};

	const assignSelectedToGroup = () => {
		// 마지막(또는 첫) 그룹에 배치. 그룹이 없으면 자동 생성
		if (STATE.groups.length === 0) addGroup();
		const target = STATE.groups[STATE.groups.length - 1];

		const checks = $studentList.querySelectorAll("input[type=checkbox]:checked");
		let count = 0;
		checks.forEach(ch => {
			const enrollId = ch.dataset.enroll;
			if (!enrollId) return;
			if (!target.members.includes(enrollId)) {
				target.members.push(enrollId);
				count++;
			}
		});

		if (count === 0) { showError("선택된 학생이 없습니다."); return; }
		renderStudentList();
		renderGroups();
		showInfo(`${target.groupName}에 ${count}명 배치`);
	};

	const unassignAll = () => {
		const moved = STATE.groups.reduce((n, g) => n + g.members.length, 0);
		STATE.groups.forEach(g => {
			g.members = [];
			g.leaderEnrollId = null;
		});
		renderStudentList();
		renderGroups();
		showInfo(`배치 해제: ${moved}명`);
	};

	// ===== attach (간소화) =====
	const renderAttach = (bundleId, details = []) => {
		$attachFileId.value = bundleId || "";
		$currentFileId.textContent = bundleId || "(없음)";
		$currentFileList.innerHTML = "";
		if (details.length) {
			const items = details.map(d => {
				const name = d.originName ?? d.originalName ?? `파일 ${d.fileOrder ?? d.order ?? ""}`;
				const ext = d.extension ? `.${d.extension}` : "";
				const size = typeof d.fileSize === "number" ? ` (${d.fileSize.toLocaleString()}B)` : "";
				return `<li class="list-group-item">${escapeHtml(name)}${ext}${size}</li>`;
			}).join("");
			$currentFileList.insertAdjacentHTML("beforeend", items);
		}
		$currentBox.classList.remove("d-none");
	};

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
		} catch (e) {
			console.error(e);
			showError(e?.message);
		} finally {
			toggleBusy(false);
		}
	});

	// ===== events =====
	$btnRefreshStudents?.addEventListener("click", fetchStudents);
	$studentSearch?.addEventListener("input", renderStudentList);
	$btnAssignSelected?.addEventListener("click", assignSelectedToGroup);
	$btnUnassignAll?.addEventListener("click", unassignAll);
	$btnAddGroup?.addEventListener("click", addGroup);
	$btnRemoveEmptyGroups?.addEventListener("click", removeEmptyGroups);

	// 그룹 내부 위임 이벤트
	$groupList?.addEventListener("click", (e) => {
		const t = e.target;
		if (!(t instanceof HTMLElement)) return;

		if (t.classList.contains("btn-delete-group")) {
			const gid = t.dataset.gid;
			const idx = STATE.groups.findIndex(g => g.tempId === gid);
			if (idx >= 0) STATE.groups.splice(idx, 1);
			renderGroups();
		}

		if (t.classList.contains("btn-clear-members")) {
			const gid = t.dataset.gid;
			const g = STATE.groups.find(x => x.tempId === gid);
			if (!g) return;
			g.members = [];
			g.leaderEnrollId = null;
			renderStudentList();
			renderGroups();
		}

		if (t.classList.contains("btn-remove-member")) {
			const gid = t.dataset.gid;
			const enroll = t.dataset.enroll;
			const g = STATE.groups.find(x => x.tempId === gid);
			if (!g) return;
			g.members = g.members.filter(m => m !== enroll);
			if (g.leaderEnrollId === enroll) g.leaderEnrollId = null;
			renderStudentList();
			renderGroups();
		}

		if (t.classList.contains("btn-set-leader")) {
			const gid = t.dataset.gid;
			const enroll = t.dataset.enroll;
			const g = STATE.groups.find(x => x.tempId === gid);
			if (!g) return;
			if (!g.members.includes(enroll)) return;
			g.leaderEnrollId = enroll;
			renderGroups();
		}
	});

	$groupList?.addEventListener("input", (e) => {
		const t = e.target;
		if (!(t instanceof HTMLInputElement)) return;
		if (t.classList.contains("group-name-input")) {
			const gid = t.dataset.gid;
			const g = STATE.groups.find(x => x.tempId === gid);
			if (g) g.groupName = t.value;
		}
	});

	// ===== submit (서버 전송) =====
	$form?.addEventListener("submit", async (e) => {
		e.preventDefault();
		clearInfo();

		try {
			if (!lectureId) throw new Error("lectureId가 비어있습니다.");
			if (editorInstance) $desc.value = editorInstance.getData();

			const grouptaskName = ($name.value || "").trim();
			const grouptaskDesc = ($desc.value || "").trim();
			const startAt = normalizeLocalDT($startAt.value);
			const endAt = normalizeLocalDT($endAt.value);
			const attachFileId = ($attachFileId.value || "").trim();

			if (!grouptaskName) throw new Error("과제명을 입력하세요.");
			if (!grouptaskDesc) throw new Error("과제설명을 입력하세요.");
			if (!startAt) throw new Error("과제 시작일을 입력하세요.");
			if (startAt && endAt && startAt > endAt) throw new Error("마감일은 시작일 이후여야 합니다.");

			// ★ DTO(GroupTaskCreateReq)에 맞춘 최종 페이로드
			const payload = {
				grouptaskName,
				grouptaskDesc,
				startAt,        // 예: '2025-10-10T14:30:00'
				endAt,          // null 가능
				attachFileId,   // "" 가능
				groups: STATE.groups.map(g => ({
					groupName: g.groupName,
					leaderEnrollId: g.leaderEnrollId,
					crews: g.members, // enrollId[]
				})),
			};

			// 프리티 로깅 + 프리뷰
			console.groupCollapsed("[GROUPTASK SUBMIT PREVIEW]");
			console.log(payload);
			console.groupEnd();
			$debug.textContent = JSON.stringify(payload, null, 2);

			// === 실제 전송 ===
			const url = `${profBase}/${encodeURIComponent(lectureId)}/group`;
			toggleBusy(true);
			const res = await fetch(url, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					"Accept": "application/json",
				},
				body: JSON.stringify(payload),
			});

			if (!res.ok) {
				// 서버에서 에러 json/text를 줄 수도 있으니 최대한 읽어보고 메시지 구성
				let serverMsg = "";
				try {
					const ct = res.headers.get("Content-Type") || "";
					if (ct.includes("application/json")) {
						const j = await res.json();
						serverMsg = j?.message || j?.error || JSON.stringify(j);
					} else {
						serverMsg = await res.text();
					}
				} catch { /* ignore */ }
				throw new Error(`조별과제 생성 실패 (HTTP ${res.status})${serverMsg ? " - " + serverMsg : ""}`);
			}

			showInfo("조별과제가 등록되었습니다.");
			// 필요 시 Location 헤더 활용:
			// const loc = res.headers.get("Location");  // 현재는 null로 내려옴
			// if (loc) { window.location.href = loc; }
		} catch (err) {
			console.error(err);
			showError(err?.message || "요청 처리 중 오류가 발생했습니다.");
		} finally {
			toggleBusy(false);
		}
	});


	// ===== init =====
	const init = async () => {
		await initEditor();
		// 최초 진입 시 학생 목록 로드 시도
		fetchStudents(); // 실패해도 화면은 열림
	};

	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();