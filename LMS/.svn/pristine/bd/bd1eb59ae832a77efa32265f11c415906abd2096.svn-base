(function() {
	"use strict";

	const $root = document.querySelector("#grouptask-post-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const commonBase = $root.dataset.commonBase || `${ctx}/classroom/api/v1/common/task`;
	const profBase = $root.dataset.profBase || `${ctx}/classroom/api/v1/professor/task`;
	const studentApiBase = `${ctx}/classroom/api/v1/professor`;
	const studentPhotoBase = $root.dataset.studentBase || `${ctx}/classroom/api/v1/common`;

	const $lnkDashboard = document.getElementById("lnk-dashboard");
	const $lnkBoard = document.getElementById("lnk-board");
	const $lnkTask = document.getElementById("lnk-task");
	const $bcTask = document.getElementById("bc-task");
	const $btnList = document.getElementById("btn-list");
	const $btnEditBottom = document.getElementById("btn-edit-bottom");
	const $btnDelBottom = document.getElementById("btn-delete-bottom");
	const $taskBody = document.getElementById("task-body");
	const $attachArea = document.getElementById("attach-area");
	const $attachList = document.getElementById("attach-list");
	const $groupAccordion = document.getElementById("group-accordion");
	const $error = document.getElementById("error-box");

	const CURRENT = {
		lectureId: "",
		grouptaskId: "",
		taskListUrl: "",
	};

	// ---------- helpers
	const setHref = (el, url) => {
		if (el && typeof url === "string") {
			el.href = url;
			console.debug("[link] set", el.id || el.className || el.tagName, "=>", url);
		}
	};

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

	const pad2 = (n) => String(n).padStart(2, "0");
	const fmtDateTime = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
	};

	const decodeIfEscaped = (html) => {
		if (!html) return "";
		const looksEscaped = /&lt;|&gt;|&amp;|&quot;|&#39;/.test(html);
		if (!looksEscaped) return html;
		const ta = document.createElement("textarea");
		ta.innerHTML = html;
		return ta.value;
	};

	const sanitizeBasic = (html) => {
		try {
			const parser = new DOMParser();
			const doc = parser.parseFromString(html, "text/html");
			doc.querySelectorAll("script, style, iframe, object, embed, link[rel='import']").forEach((el) => el.remove());
			doc.querySelectorAll("*").forEach((el) => {
				[...el.attributes].forEach((attr) => {
					if (/^on/i.test(attr.name)) el.removeAttribute(attr.name);
				});
			});
			return doc.body.innerHTML;
		} catch {
			return html;
		}
	};

	const escapeHtml = (str) => {
		if (!str && str !== 0) return "";
		return String(str)
			.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;")
			.replace(/"/g, "&quot;")
			.replace(/'/g, "&#39;");
	};

	const taskPhaseBadge = (startAt, endAt) => {
		const now = new Date();
		const s = startAt ? new Date(startAt) : null;
		const e = endAt ? new Date(endAt) : null;

		if (s && now < s) return `<span class="badge text-bg-secondary badge-type">시작 예정</span>`;
		if (e && now > e) return `<span class="badge text-bg-dark badge-type">마감</span>`;
		return `<span class="badge text-bg-success badge-type">진행 중</span>`;
	};

	function toStudentMap(list) {
		const map = new Map();
		(list || []).forEach((stu) => {
			if (stu?.enrollId) map.set(stu.enrollId, stu);
		});
		return map;
	}

	function resolveStudent(enrollId, fallback) {
		if (!enrollId) return fallback || null;
		return fallback?.enrollId === enrollId ? fallback : (CURRENT.studentMap?.get(enrollId) || fallback || null);
	}

	function studentDisplay(student) {
		if (!student) return "-";
		const name = [student.lastName, student.firstName].filter(Boolean).join("");
		const num = student.studentNo ? `<div class="small text-muted">${escapeHtml(student.studentNo)}</div>` : "";
		return `${escapeHtml(name || student.userId || "-")}${num}`;
	}

	function memberExtra(student) {
		if (!student) return "";
		const parts = [];
		if (student.univDeptName) parts.push(student.univDeptName);
		if (student.gradeName) parts.push(student.gradeName);
		if (student.stuStatusName) parts.push(student.stuStatusName);
		return parts.length ? `<div class="small text-muted">${escapeHtml(parts.join(" / "))}</div>` : "";
	}

	function studentName(student) {
		if (!student) return "-";
		return [student.lastName, student.firstName].filter(Boolean).join("") || student.userId || "-";
	}

	function studentPhotoUrl(student) {
		if (!student) return "";
		const lectureId = CURRENT.lectureId;
		const studentNo = student.studentNo || student.studentId || student.userId;
		if (!lectureId || !studentNo) return "";
		return `${studentPhotoBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/photo`;
	}

	function photoHtml(student, alt, className = "photo") {
		const url = studentPhotoUrl(student);
		const safeAlt = escapeHtml(alt || "");
		const classes = className.split(" ").filter(Boolean);
		const hasPhotoClass = classes.includes("photo");
		const hasSmallClass = classes.includes("photo-sm");
		const extraClasses = classes.filter((cls) => cls !== "photo" && cls !== "photo-sm");

		if (url) {
			const imgClasses = hasPhotoClass ? classes.join(" ") : ["photo", ...classes].join(" ");
			return `<img src="${url}" alt="${safeAlt}" class="${imgClasses}">`;
		}

		const fallbackClasses = ["photo-fallback"];
		if (hasSmallClass) fallbackClasses.push("photo-sm");
		fallbackClasses.push(...extraClasses);
		return `<div class="${fallbackClasses.join(" ")}"><i class="bi bi-person-fill"></i></div>`;
	}

	function scoreDisplay(score) {
		if (score === "" || score === null || score === undefined) return "미평가";
		const num = Number(score);
		if (Number.isNaN(num)) return escapeHtml(String(score));
		return `${(Math.round(num * 2) / 2).toFixed(1)}점`;
	}

	function formatNote(note) {
		if (!note) return '<span class="text-muted">평가 의견이 없습니다.</span>';
		return escapeHtml(String(note)).replace(/\r?\n/g, "<br>");
	}

	function parseIdsFromPath() {
		const segs = window.location.pathname.split("/").filter(Boolean);
		const idx = segs.findIndex((s) => s === "professor");
		if (idx >= 0) {
			// ... professor / {lectureId} / task / group / {grouptaskId}
			if (segs[idx + 2] === "task" && segs[idx + 3] === "group") {
				const lectureId = decodeURIComponent(segs[idx + 1] || "");
				const grouptaskId = decodeURIComponent(segs[idx + 4] || "");
				return { lectureId, grouptaskId };
			}
		}
		const u = new URL(window.location.href);
		return {
			lectureId: u.searchParams.get("lectureId") || "",
			grouptaskId: u.searchParams.get("grouptaskId") || "",
		};
	}

	function linkAll(taskListUrl, lectureId, grouptaskId) {
		console.groupCollapsed("[linkAll]");
		setHref($lnkTask, taskListUrl);
		setHref($bcTask, taskListUrl);
		setHref($btnList, taskListUrl);
		setHref($lnkDashboard, `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}`);
		setHref($lnkBoard, `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/board`);
		setHref($btnEditBottom, `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}/edit`);
		console.groupEnd();
	}

	function renderTask(task) {
		if (!$taskBody) return;
		const groupCount = task?.groupList?.length || 0;
		const memberCount = (task?.groupList || []).reduce((acc, g) => acc + (g?.crewInfoList?.length || 0), 0);

		const badges = [
			taskPhaseBadge(task?.startAt, task?.endAt),
			`<span class="badge text-bg-primary badge-type">조별 과제</span>`,
		].join("");

		const descHtml = sanitizeBasic(decodeIfEscaped(task?.grouptaskDesc || ""));
		const safeDesc = descHtml || `<p class="text-muted mb-0">작성된 설명이 없습니다.</p>`;
		const startText = fmtDateTime(task?.startAt) || "-";
		const endText = fmtDateTime(task?.endAt) || "-";

		$taskBody.innerHTML = `
      <div class="d-flex align-items-start flex-wrap mb-2">${badges}</div>
      <h1 class="h4 mb-3">${escapeHtml(task?.grouptaskName || "제목 미지정 조별 과제")}</h1>
      <div class="d-flex flex-wrap meta-list mb-3">
        <div class="meta-item"><i class="bi bi-calendar-event"></i> 과제 기간 ${startText} ~ ${endText}</div>
        <div class="meta-item"><i class="bi bi-people"></i> 조 ${groupCount}개</div>
        <div class="meta-item"><i class="bi bi-person-lines-fill"></i> 총 인원 ${memberCount}명</div>
      </div>
      <hr class="my-3" />
      <article class="task-content mb-4">${safeDesc}</article>
    `;

		renderAttachments(task?.attachFileList);
		renderGroupAccordion(task?.groupList || []);
	}

	function renderAttachments(list) {
		if (!$attachArea || !$attachList) return;
		if (!Array.isArray(list) || list.length === 0) {
			$attachArea.classList.add("d-none");
			$attachList.innerHTML = "";
			return;
		}

		const items = list.map((file) => {
			const name = escapeHtml(file?.originalFileName || file?.originName || "파일");
			const ext = file?.extension ? `.${escapeHtml(file.extension)}` : "";
			const size = file?.fileSize ? `${Number(file.fileSize).toLocaleString()} 바이트` : "";
			const href = `${commonBase}/${encodeURIComponent(CURRENT.lectureId)}/group/${encodeURIComponent(CURRENT.grouptaskId)}/attach/${encodeURIComponent(file.fileOrder ?? 0)}`;
			return `
        <li class="list-group-item d-flex justify-content-between align-items-center">
          <div class="text-truncate" style="max-width:70%;">
            <i class="bi bi-paperclip"></i> ${name}${ext} ${size ? `<small class="text-muted">(${size})</small>` : ""}
          </div>
          <a class="btn btn-sm btn-outline-secondary" href="${href}">다운로드</a>
        </li>
      `;
		}).join("");

		$attachList.innerHTML = items;
		$attachArea.classList.remove("d-none");
	}

	function renderGroupAccordion(groupList) {
		if (!$groupAccordion) return;
		if (!Array.isArray(groupList) || groupList.length === 0) {
			$groupAccordion.innerHTML = `<p class="text-muted mb-0">등록된 조가 없습니다.</p>`;
			return;
		}

		const accordionItems = groupList.map((group, index) => {
			const info = group?.groupInfo || {};
			const crewList = group?.crewInfoList || [];
			const leader = resolveStudent(
				info.leaderEnrollId,
				crewList.find((c) => c.enrollId === info.leaderEnrollId)?.studentInfo || null,
			);
			const leaderName = studentName(leader);
			const memberCount = crewList.length;
			const submitInfo = group?.submitInfo || {};
			const submittedAt = submitInfo.submitAt ? fmtDateTime(submitInfo.submitAt) : "";
			const submitBadge = submitInfo.submitAt
				? `<span class="badge text-bg-success">제출 완료</span>`
				: `<span class="badge text-bg-secondary">미제출</span>`;
			const submitStatus = submitInfo.submitAt ? `제출 ${submittedAt}` : "미제출";
			const headingId = `group-acc-head-${index}`;
			const collapseId = `group-acc-body-${index}`;
			const groupName = info.groupName ? escapeHtml(info.groupName) : `${index + 1}조`;
			const leaderMeta = leader?.studentNo ? ` (${escapeHtml(leader.studentNo)})` : "";
			const submitDescHtml = sanitizeBasic(decodeIfEscaped(submitInfo.submitDesc || "")).trim()
				|| '<span class="text-muted">제출 설명이 없습니다.</span>';

			return `
      <div class="accordion-item">
        <h2 class="accordion-header" id="${headingId}">
          <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#${collapseId}"
            aria-expanded="false" aria-controls="${collapseId}">
            <div class="group-summary">
              ${photoHtml(leader, `${groupName} 조장`, "photo me-2")}
              <div class="group-summary-text">
                <div class="group-summary-title">
                  <span class="fw-semibold">${groupName}</span>
                  ${submitBadge}
                </div>
                <div class="group-summary-meta">
                  <span><i class="bi bi-person-badge"></i> 조장 ${escapeHtml(leaderName)}${leaderMeta || ""}</span>
                  <span><i class="bi bi-people"></i> 구성원 ${memberCount}명</span>
                  <span><i class="bi bi-clock-history"></i> ${submitStatus}</span>
                </div>
              </div>
            </div>
          </button>
        </h2>
        <div id="${collapseId}" class="accordion-collapse collapse" aria-labelledby="${headingId}" data-bs-parent="#group-accordion">
          <div class="accordion-body">
            <div class="mb-4">
              <h3 class="h6 mb-3">제출 정보</h3>
              <div class="row g-3">
                <div class="col-12 col-md-4">
                  <div class="small text-muted">제출 시각</div>
                  <div>${submitInfo.submitAt ? submittedAt : '<span class="text-muted">미제출</span>'}</div>
                </div>
                <div class="col-12 col-md-8">
                  <div class="small text-muted">제출 설명</div>
                  <div class="mt-1">${submitDescHtml}</div>
                </div>
              </div>
              <div class="mt-3">
                <div class="small text-muted">제출 파일</div>
                <div class="mt-1">${buildSubmitFilesMarkup(submitInfo, info)}</div>
              </div>
            </div>
            <div>
              <h3 class="h6 mb-3">조 구성원</h3>
              ${buildMemberCards(crewList, info)}
            </div>
          </div>
        </div>
      </div>
    `;
		}).join("");

		$groupAccordion.innerHTML = accordionItems;
	}

	function buildSubmitFilesMarkup(submitInfo, groupInfo) {
		if (!submitInfo?.submitFileId) {
			return '<span class="text-muted">제출된 파일이 없습니다.</span>';
		}
		const groupId = groupInfo?.groupId || submitInfo.groupId;
		if (!groupId) {
			return '<span class="text-muted">제출 파일을 다운로드할 수 없습니다.</span>';
		}
		const href = `${profBase}/${encodeURIComponent(CURRENT.lectureId)}/group/${encodeURIComponent(CURRENT.grouptaskId)}/submit/${encodeURIComponent(groupId)}`;
		return `<a class="btn btn-sm btn-outline-secondary" href="${href}">제출 파일 다운로드</a>`;
	}

	function buildMemberCards(crewList, groupInfo) {
		if (!Array.isArray(crewList) || crewList.length === 0) {
			return '<p class="text-muted mb-0">조 구성원이 없습니다.</p>';
		}

		const cards = crewList.map((crew) => {
			const student = resolveStudent(crew?.enrollId, crew?.studentInfo || null);
			const name = studentName(student);
			const isLeader = groupInfo?.leaderEnrollId && crew?.enrollId === groupInfo.leaderEnrollId;
			const studentNo = student?.studentNo ? escapeHtml(student.studentNo) : "학번 정보 없음";
			const extra = memberExtra(student);
			const hasScore = crew?.evaluScore !== "" && crew?.evaluScore !== null && crew?.evaluScore !== undefined;
			const score = scoreDisplay(crew?.evaluScore);
			const evalAt = crew?.evaluAt ? fmtDateTime(crew.evaluAt) : "미평가";

			return `
        <div class="col-12 col-md-6 col-lg-4">
          <div class="member-card h-100">
            <div class="d-flex align-items-center gap-3">
              ${photoHtml(student, `${name} 사진`, "photo photo-sm")}
              <div>
                <div class="fw-semibold">${escapeHtml(name)}</div>
                <div class="member-meta">${studentNo}</div>
                ${isLeader ? `<span class="badge text-bg-primary mt-2">조장</span>` : ""}
              </div>
            </div>
            ${extra ? `<div class="mt-2">${extra}</div>` : ""}
            <div class="evaluation-box mt-3">
              <div><span class="fw-semibold">평가 점수 :</span> ${hasScore ? `${score} / 10점` : score}</div>
              <div class="evaluation-note mt-2">${formatNote(crew?.evaluDesc)}</div>
              <div class="evaluation-note mt-2">평가 일시: ${evalAt}</div>
            </div>
          </div>
        </div>
      `;
		}).join("");

		return `<div class="row g-3">${cards}</div>`;
	}

	async function handleDeleteClick(e) {
		e?.preventDefault?.();
		hideError();

		if (!CURRENT.lectureId || !CURRENT.grouptaskId) {
			showError("식별자(lectureId/grouptaskId)가 없습니다.");
			return;
		}

		if (!window.confirm("이 조별 과제를 삭제하시겠습니까?")) return;

		const prevText = $btnDelBottom ? $btnDelBottom.textContent : "";
		if ($btnDelBottom) {
			$btnDelBottom.setAttribute("aria-disabled", "true");
			$btnDelBottom.classList.add("disabled");
			$btnDelBottom.textContent = "삭제 중...";
		}

		const url = `${profBase}/${encodeURIComponent(CURRENT.lectureId)}/group/${encodeURIComponent(CURRENT.grouptaskId)}`;
		console.debug("[delete] DELETE", url);

		try {
			const res = await fetch(url, { method: "DELETE", headers: { Accept: "application/json" } });
			if (res.status === 204) {
				const redirect = CURRENT.taskListUrl || `${ctx}/classroom/professor/${encodeURIComponent(CURRENT.lectureId)}/task/group`;
				window.location.replace(redirect);
				return;
			}
			const bodyText = await safeReadText(res);
			console.warn("[delete] unexpected status:", res.status, bodyText);
			showError(`조별 과제 삭제에 실패했습니다. (HTTP ${res.status})`);
		} catch (err) {
			console.error("[delete] error", err);
			showError("조별 과제를 삭제하는 중 오류가 발생했습니다.");
		} finally {
			if ($btnDelBottom) {
				$btnDelBottom.removeAttribute("aria-disabled");
				$btnDelBottom.classList.remove("disabled");
				$btnDelBottom.textContent = prevText || "삭제";
			}
		}
	}

	async function safeReadText(res) {
		try { return await res.text(); } catch { return ""; }
	}

	async function fetchStudents(lectureId) {
		const url = `${studentApiBase}/${encodeURIComponent(lectureId)}/students`;
		console.debug("[fetch] students GET", url);
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			return Array.isArray(data) ? data : [];
		} catch (err) {
			console.warn("[fetch] students failed:", err);
			return [];
		}
	}

	async function init() {
		console.groupCollapsed("[init]");
		const { lectureId, grouptaskId } = parseIdsFromPath();
		console.debug("parsed:", { lectureId, grouptaskId });

		if (!lectureId || !grouptaskId) {
			linkAll("#", "", "");
			showError("경로가 올바르지 않아 lectureId/grouptaskId를 읽을 수 없습니다.");
			console.groupEnd();
			return;
		}

		CURRENT.lectureId = lectureId;
		CURRENT.grouptaskId = grouptaskId;
		CURRENT.taskListUrl = `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/task/group`;

		CURRENT.studentMap = new Map();

		linkAll(CURRENT.taskListUrl, lectureId, grouptaskId);

		if ($btnDelBottom) {
			$btnDelBottom.addEventListener("click", handleDeleteClick);
		}

		const detailUrl = `${commonBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}`;
		console.debug("[fetch] detail GET", detailUrl);

		try {
			const [taskRes, studentList] = await Promise.all([
				fetch(detailUrl, { headers: { Accept: "application/json" } }),
				fetchStudents(lectureId),
			]);

			if (!taskRes.ok) {
				const txt = await safeReadText(taskRes);
				throw new Error(`HTTP ${taskRes.status} ${txt}`);
			}

			CURRENT.studentMap = toStudentMap(studentList);

			const task = await taskRes.json();
			renderTask(task);
		} catch (err) {
			console.error("[fetch] detail failed:", err);
			showError("조별 과제 상세 정보를 불러오지 못했습니다.");
		}
		console.groupEnd();
	}

	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();
