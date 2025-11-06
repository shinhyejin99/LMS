// /js/app/classroom/professor/prfBoardPost.js
(function() {
	"use strict";

	const $root = document.querySelector("#board-post-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const apiBase = $root.dataset.apiBase || `${ctx}/classroom/api/v1/professor/board`;

	// 네비/버튼 (없을 수도 있으니 널가드)
	const $lnkDashboard = document.getElementById("lnk-dashboard");
	const $lnkBoard = document.getElementById("lnk-board");
	const $bcBoard = document.getElementById("bc-board");
	const $btnList = document.getElementById("btn-list");
	const $btnEditBottom = document.getElementById("btn-edit-bottom");
	const $btnDelBottom = document.getElementById("btn-delete-bottom");
	const $replyForm = document.getElementById("reply-form");
	const $replyContent = document.getElementById("reply-content");
	const $replyParent = document.getElementById("reply-parent");
	const $replySubmit = document.getElementById("reply-submit");
	const $replyAlert = document.getElementById("reply-alert");
	const $replyList = document.getElementById("reply-list");
	const $replyEmpty = document.getElementById("reply-empty");
	const $replyParentIndicator = document.getElementById("reply-parent-indicator");
	const $replyParentLabel = document.getElementById("reply-parent-label");
	const $replyParentClear = document.getElementById("reply-parent-clear");
	const $replySortNewest = document.getElementById("reply-sort-newest");
	const $replySortOldest = document.getElementById("reply-sort-oldest");
	const replyEmptyDefaultMessage = $replyEmpty ? $replyEmpty.textContent.trim() : "";

	const $postBody = document.getElementById("post-body");
	const $error = document.getElementById("error-box");

	// 현재 페이지 컨텍스트(lectureId/postId/boardUrl)를 모아두는 캐시
	const CURRENT = {
		lectureId: "",
		postId: "",
		boardUrl: "",
		professorInfo: null,
		selectedReplyId: "",
		replyIndex: new Map(),
		studentMap: new Map(),
		highlightReplyId: "",
		replies: [],
		replySortOrder: "desc"
	};

	const LATEST_REPLY_SENTINEL = "__LATEST__";

	// ---------- helpers
	const setHref = (el, url) => {
		if (el && typeof url === "string") {
			el.href = url;
			console.debug("[link] set", el.id || el.className || el.tagName, "→", url);
		} else {
			console.debug("[link] skip (el null)", url);
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

	const setReplyAlert = (variant, message) => {
		if (!$replyAlert) return;
		$replyAlert.className = `alert mt-3 alert-${variant}`;
		$replyAlert.textContent = message;
	};

	const clearReplyAlert = () => {
		if (!$replyAlert) return;
		$replyAlert.className = "alert mt-3 d-none";
		$replyAlert.textContent = "";
	};

	const setSortButtonState = () => {
		if (!$replySortNewest || !$replySortOldest) return;
		const applyState = (btn, active) => {
			if (!btn) return;
			btn.classList.remove("btn-primary", "btn-outline-secondary", "active");
			btn.setAttribute("aria-pressed", active ? "true" : "false");
			if (active) {
				btn.classList.add("btn-primary", "active");
			} else {
				btn.classList.add("btn-outline-secondary");
			}
		};
		const isDesc = CURRENT.replySortOrder !== "asc";
		applyState($replySortNewest, isDesc);
		applyState($replySortOldest, !isDesc);
	};

	const pad2 = (n) => String(n).padStart(2, "0");
	const fmtDateTime = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
	};

	const escapeHtml = (s) =>
		String(s ?? "")
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");

	// 서버가 &lt;p&gt;같이 엔티티로 보낼 수 있으므로 필요시 디코딩
	const decodeIfEscaped = (html) => {
		if (!html) return "";
		const looksEscaped = /&lt;|&gt;|&amp;|&quot;|&#39;/.test(html);
		if (!looksEscaped) return html;
		const ta = document.createElement("textarea");
		ta.innerHTML = html;
		const decoded = ta.value;
		console.debug("[content] decoded entities:", decoded.slice(0, 120));
		return decoded;
	};

	// 간단 sanitize: script/style/iframe/object/embed 제거
	const sanitizeBasic = (html) => {
		try {
			const parser = new DOMParser();
			const doc = parser.parseFromString(html, "text/html");
			doc.querySelectorAll("script, style, iframe, object, embed, link[rel='import']").forEach((el) => el.remove());
			// 이벤트 핸들러 속성 제거 (onclick 등)
			doc.querySelectorAll("*").forEach((el) => {
				[...el.attributes].forEach((attr) => {
					if (/^on/i.test(attr.name)) el.removeAttribute(attr.name);
				});
			});
			const sanitized = doc.body.innerHTML;
			console.debug("[content] sanitized html preview:", sanitized.slice(0, 120));
			return sanitized;
		} catch (e) {
			console.warn("[content] sanitize failed, returning raw html");
			return html;
		}
	};

	const badgeForType = (t) => {
		if (t === "NOTICE") return `<span class="badge bg-warning text-dark border border-warning-subtle badge-type">공지</span>`;
		if (t === "MATERIAL") return `<span class="badge bg-info text-dark border border-info-subtle badge-type">자료</span>`;
		return `<span class="badge bg-secondary text-light badge-type">기타</span>`;
	};

	const computeStatus = (post) => {
		if (post.tempSaveYn === "Y") return "DRAFT";
		if (post.revealAt) {
			const now = new Date();
			const r = new Date(post.revealAt);
			if (r.getTime() > now.getTime()) return "SCHEDULED";
		}
		return "PUBLISHED";
	};

	const badgeForStatus = (s) => {
		switch (s) {
			case "DRAFT": return `<span class="badge text-bg-secondary">임시</span>`;
			case "SCHEDULED": return `<span class="badge text-bg-primary">예약</span>`;
			default: return `<span class="badge text-bg-success">공개</span>`;
		}
	};

	const escapeForSelector = (value) => {
		if (typeof value !== "string") return "";
		if (window.CSS && typeof window.CSS.escape === "function") return window.CSS.escape(value);
		return value.replace(/[^a-zA-Z0-9_-]/g, (ch) => `\${ch}`);
	};

	const formatReplyHtml = (text) => {
		const escaped = escapeHtml(text || "");
		if (!escaped) return "";
		return escaped.replace(/\r?\n/g, "<br />");
	};

	const formatFileSize = (bytes) => {
		if (!Number.isFinite(bytes) || bytes <= 0) return "";
		const KB = 1024;
		const MB = KB * 1024;
		let value;
		let unit;
		if (bytes >= MB) {
			value = bytes / MB;
			unit = "mb";
		} else {
			value = bytes / KB;
			unit = "kb";
		}
		const rounded = value >= 10 ? Math.round(value) : Math.round(value * 10) / 10;
		const formatted = Number.isInteger(rounded) ? String(rounded) : rounded.toFixed(1).replace(/\.0$/, "");
		return `${formatted} ${unit}`;
	};

	const formatStudentName = (student) => {
		if (!student) return "";
		const last = student.lastName || "";
		const first = student.firstName || "";
		const fullName = `${last}${first}`.trim();
		return fullName || student.userName || student.userId || "";
	};

	const formatProfessorName = (professor) => {
		if (!professor) return "";
		const last = professor.lastName || "";
		const first = professor.firstName || "";
		const fullName = `${last}${first}`.trim();
		return fullName || professor.userName || professor.professorName || professor.userId || "";
	};

	const isProfessorReply = (reply) => {
		if (!reply) return false;
		const replyUserId = (reply.userId || "").trim();
		const profUserId = (CURRENT.professorInfo?.userId || "").trim();
		return !!replyUserId && !!profUserId && replyUserId === profUserId;
	};

	const formatReplyAuthor = (reply) => {
		if (!reply) return "익명";
		const userId = (reply.userId || "").trim();
		if (userId && isProfessorReply(reply)) {
			const profName = formatProfessorName(CURRENT.professorInfo);
			return profName ? `${profName} 교수` : "담당 교수";
		}
		if (userId && CURRENT.studentMap instanceof Map && CURRENT.studentMap.has(userId)) {
			const info = CURRENT.studentMap.get(userId);
			const name = formatStudentName(info) || userId;
			const dept = info?.univDeptName || "";
			return dept ? `${name}(${dept})` : name;
		}
		return reply.writerName || reply.authorName || reply.userName || userId || "익명";
	};

	const buildReplyIndicatorLabel = (reply) => {
		if (!reply) return "";
		const name = formatReplyAuthor(reply);
		let content = (reply.replyContent || "").replace(/\s+/g, " ").trim();
		if (content.length > 40) content = `${content.slice(0, 40)}…`;
		if (!content) content = "내용 없음";
		return `${name}에게 답글을 작성합니다: ${content}`;
	};

	const getParentReplyId = (reply) => {
		if (!reply) return "";
		if (reply.parentReplyId) return reply.parentReplyId;
		const parent = reply.parentReply;
		if (!parent) return "";
		if (typeof parent === "string") return parent;
		if (typeof parent === "object") return parent.lctReplyId || parent.replyId || parent.id || "";
		return "";
	};

	const replyTimestamp = (reply) => {
		if (reply?.createAt) {
			const ts = Date.parse(reply.createAt);
			if (!Number.isNaN(ts)) return ts;
		}
		const fallback = Number(String(reply?.lctReplyId || "").replace(/\D/g, ""));
		return Number.isNaN(fallback) ? 0 : fallback;
	};

	const compareReplies = (a, b) => {
		const diff = replyTimestamp(a) - replyTimestamp(b);
		if (diff !== 0) return diff;
		return String(a?.lctReplyId || "").localeCompare(String(b?.lctReplyId || ""));
	};

	const findNewestReplyId = (list = []) => {
		let newestId = "";
		let newestTime = -Infinity;
		for (const reply of list) {
			if (!reply) continue;
			const ts = replyTimestamp(reply);
			const candidateId = String(reply.lctReplyId || reply.replyId || reply.id || "") || newestId;
			if (ts > newestTime && candidateId) {
				newestTime = ts;
				newestId = candidateId;
			}
		}
		return newestId;
	};

	const highlightReply = (replyId) => {
		if (!$replyList || !replyId) return;
		const selector = `[data-reply-id="${escapeForSelector(replyId)}"]`;
		const target = $replyList.querySelector(selector);
		if (!target) return;
		target.classList.add("reply-highlight");
		target.scrollIntoView({ behavior: "smooth", block: "center" });
		window.setTimeout(() => target.classList.remove("reply-highlight"), 2400);
	};

	const applyPendingHighlight = (list = []) => {
		if (!CURRENT.highlightReplyId) return;
		let targetId = CURRENT.highlightReplyId;
		if (targetId === LATEST_REPLY_SENTINEL) {
			targetId = findNewestReplyId(list);
		}
		if (targetId) {
			highlightReply(targetId);
		}
		CURRENT.highlightReplyId = "";
	};



	async function loadProfessorInfo(lectureId) {
		if (!lectureId) {
			CURRENT.professorInfo = null;
			return null;
		}
		const encodedId = encodeURIComponent(lectureId);
		const url = `${ctx}/classroom/api/v1/common/${encodedId}/professor`;
		try {
			const res = await fetch(url, { headers: { Accept: 'application/json' } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			CURRENT.professorInfo = data || null;
			return data;
		} catch (err) {
			console.warn('[reply] professor info load failed', err);
			CURRENT.professorInfo = null;
			return null;
		}
	}

	async function loadStudentRoster(lectureId) {
		if (!lectureId) return;
		const encodedId = encodeURIComponent(lectureId);
		const url = `${ctx}/classroom/api/v1/professor/${encodedId}/students`;
		try {
			const res = await fetch(url, { headers: { Accept: 'application/json' } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			if (!Array.isArray(data)) return;
			const map = CURRENT.studentMap instanceof Map ? CURRENT.studentMap : new Map();
			map.clear();
			for (const student of data) {
				if (!student) continue;
				const userId = (student.userId || '').trim();
				if (!userId) continue;
				map.set(userId, student);
			}
			CURRENT.studentMap = map;
		} catch (err) {
			console.warn('[reply] roster load failed', err);
		}
	}
// /classroom/professor/{lectureId}/board/{postId}
	function parseIdsFromPath() {
		const segs = window.location.pathname.split("/").filter(Boolean);
		const idx = segs.findIndex((s) => s === "professor");
		if (idx >= 0 && segs[idx + 2] === "board") {
			const lectureId = decodeURIComponent(segs[idx + 1] || "");
			const postId = decodeURIComponent(segs[idx + 3] || "");
			return { lectureId, postId };
		}
		const u = new URL(window.location.href);
		return {
			lectureId: u.searchParams.get("lectureId") || "",
			postId: u.searchParams.get("postId") || "",
		};
	}

	function linkAll(boardUrl, lectureId, postId) {
		console.groupCollapsed("[linkAll]");
		console.debug("boardUrl:", boardUrl);
		setHref($lnkBoard, boardUrl);
		setHref($bcBoard, boardUrl);
		setHref($btnList, boardUrl);
		setHref($btnEditBottom, `${boardUrl}/${encodeURIComponent(postId)}/edit`);
		if ($btnEditBottom) {
			$btnEditBottom.classList.remove("disabled");
			$btnEditBottom.removeAttribute("aria-disabled");
		}
		if ($btnDelBottom) {
			$btnDelBottom.classList.remove("disabled");
			$btnDelBottom.removeAttribute("aria-disabled");
		}
		setHref($lnkDashboard, `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}`);
		console.groupEnd();
	}

	function render(post) {
		console.groupCollapsed("[render]");
		console.debug("post keys:", Object.keys(post));
		console.debug("post sample:", post);

		const status = computeStatus(post);

		const badgeRow = $postBody?.querySelector("#post-badge-row");
		if (badgeRow) {
			badgeRow.innerHTML = `${badgeForType(post.postType)} ${badgeForStatus(status)}`;
		}

		const titleEl = $postBody?.querySelector("#post-title");
		if (titleEl) {
			titleEl.classList.remove("skeleton");
			titleEl.textContent = (post.title || "").trim() || "(제목 없음)";
		}

		const metaRow = $postBody?.querySelector("#post-meta-row");
		if (metaRow) {
			const meta = [];
			meta.push(`<div class="meta-item">작성: <span class="text-body-secondary">${fmtDateTime(post.createAt)}</span></div>`);
			if (post.revealAt) {
				meta.push(`<div class="meta-item">공개 예정: <span class="text-body-secondary">${fmtDateTime(post.revealAt)}</span></div>`);
			}
			metaRow.innerHTML = meta.join("");
		}

		const raw = post.content || "";
		const decoded = decodeIfEscaped(raw);
		const safe = sanitizeBasic(decoded);
		const contentEl = $postBody?.querySelector("#post-content");
		if (contentEl) {
			contentEl.innerHTML = safe || '<p class="text-muted">내용이 없습니다.</p>';
		}

		const files = Array.isArray(post.attachFileList) ? post.attachFileList : [];
		const attachArea = document.getElementById("attach-area");
		const attachList = document.getElementById("attach-list");

		if (attachArea && attachList) {
			if (files.length) {
				attachArea.classList.remove("d-none");
				const itemsHtml = files.map((file, index) => {
					const fallbackName = `첨부파일 ${file.fileOrder || index + 1}`;
					const baseName =
						file.originName ||
						file.originalFilename ||
						file.originalFileName ||
						file.fileName ||
						file.fileNm ||
						fallbackName;
					const extension = (file.extension || file.fileExtension || "").replace(/^\./, "");
					let displayName = typeof baseName === "string" ? baseName : String(baseName ?? fallbackName);
					if (
						extension &&
						typeof displayName === "string" &&
						!displayName.toLowerCase().endsWith(`.${extension.toLowerCase()}`)
					) {
						displayName = `${displayName}.${extension}`;
					}
					const name = escapeHtml(displayName);
					const sizeLabel = formatFileSize(Number(file.fileSize ?? file.size ?? file.fileLength ?? 0));
					const order = file.fileOrder ?? file.atchFileSn ?? file.fileId ?? file.id ?? index;
					const hrefRaw =
						typeof file.downloadUrl === "string" && file.downloadUrl
							? file.downloadUrl
							: `${ctx}/classroom/api/v1/common/board/${CURRENT.lectureId}/post/${CURRENT.postId}/attach/${encodeURIComponent(order)}`;
					const href = escapeHtml(hrefRaw);
					return `
						<li class="list-group-item d-flex align-items-center gap-3">
							<a class="d-flex align-items-center gap-2 text-decoration-none flex-grow-1" href="${href}" download>
								<i class="bi bi-paperclip text-secondary"></i>
								<span class="text-truncate">${name}</span>
							</a>
							${sizeLabel ? `<span class="text-muted small flex-shrink-0">${escapeHtml(sizeLabel)}</span>` : ""}
						</li>`;
				}).join("");
				attachList.innerHTML = itemsHtml;
			} else {
				attachArea.classList.add("d-none");
				attachList.innerHTML = "";
			}
		}

		console.groupEnd();
	}

	// ---------- 댓글 처리 보조 함수 ----------
	function clearReplyHighlights() {
		if (!$replyList) return;
		$replyList.querySelectorAll('.reply-item.selected').forEach((el) => el.classList.remove('selected'));
		$replyList.querySelectorAll('.reply-item.reply-highlight').forEach((el) => el.classList.remove('reply-highlight'));
	}

	function updateReplySelection(replyOverride) {
		clearReplyHighlights();
		const replyId = CURRENT.selectedReplyId;
		if (!replyId) {
			if ($replyParent) $replyParent.value = '';
			if ($replyParentIndicator) {
				$replyParentIndicator.classList.add('d-none');
				if ($replyParentLabel) $replyParentLabel.textContent = '';
			}
			return;
		}

		const index = CURRENT.replyIndex instanceof Map ? CURRENT.replyIndex : null;
		const reply = replyOverride || (index ? index.get(replyId) : null);
		if (!reply) {
			CURRENT.selectedReplyId = '';
			updateReplySelection();
			return;
		}

		if ($replyParent) $replyParent.value = replyId;
		if ($replyParentIndicator) {
			$replyParentIndicator.classList.remove('d-none');
			if ($replyParentLabel) $replyParentLabel.textContent = buildReplyIndicatorLabel(reply);
		}

		if (!$replyList) return;
		const selector = `[data-reply-id="${escapeForSelector(replyId)}"]`;
		const el = $replyList.querySelector(selector);
		if (!el) {
			CURRENT.selectedReplyId = '';
			updateReplySelection();
			return;
		}
		el.classList.add('selected');
	}

function clearReplyParentSelection() {
		CURRENT.selectedReplyId = '';
		updateReplySelection();
	}

function setReplyParentSelection(replyId, reply) {
		if (!replyId) {
			clearReplyParentSelection();
			return;
		}
		CURRENT.selectedReplyId = replyId;
		updateReplySelection(reply);
		if ($replyContent) {
			try {
				$replyContent.focus({ preventScroll: false });
			} catch (err) {
				$replyContent.focus();
			}
		}
	}

function buildReplyNode(reply, depth, childrenMap) {
		const wrapper = document.createElement('div');
		wrapper.className = 'reply-item border rounded px-3 py-2';
		const professorReply = isProfessorReply(reply);
		if (professorReply) wrapper.classList.add('reply-professor');
		if (depth > 0) {
			wrapper.classList.add('reply-child', 'ms-4');
			if (!professorReply) wrapper.classList.add('bg-light');
		}
		const replyId = String(reply?.lctReplyId || '');
		if (replyId) wrapper.dataset.replyId = replyId;
		wrapper.dataset.depth = String(depth);

		const header = document.createElement('div');
		header.className = 'd-flex align-items-center justify-content-between gap-2 flex-wrap';

		const infoBox = document.createElement('div');
		infoBox.className = 'd-flex align-items-center gap-2 flex-wrap';
		const nameEl = document.createElement('span');
		nameEl.className = 'fw-semibold text-body';
		nameEl.textContent = formatReplyAuthor(reply);
		const timeEl = document.createElement('span');
		timeEl.className = 'text-muted small';
		timeEl.textContent = fmtDateTime(reply?.createAt);
		infoBox.appendChild(nameEl);
		infoBox.appendChild(timeEl);

		header.appendChild(infoBox);

		const actionBox = document.createElement('div');
		actionBox.className = 'd-flex align-items-center gap-2 flex-shrink-0';

		if (depth === 0) {
			const replyBtn = document.createElement('button');
			replyBtn.type = 'button';
			replyBtn.className = 'btn btn-link btn-sm text-decoration-none px-0';
			replyBtn.dataset.action = 'choose-parent';
			if (replyId) replyBtn.dataset.replyId = replyId;
			replyBtn.textContent = '답글';
			actionBox.appendChild(replyBtn);
		}

		const deleteBtn = document.createElement('button');
		deleteBtn.type = 'button';
		deleteBtn.className = 'btn btn-link btn-sm text-danger text-decoration-none px-0';
		deleteBtn.dataset.action = 'delete-reply';
		if (replyId) deleteBtn.dataset.replyId = replyId;
		deleteBtn.textContent = '삭제';
		actionBox.appendChild(deleteBtn);

		header.appendChild(actionBox);

		wrapper.appendChild(header);

		const bodyEl = document.createElement('div');
		bodyEl.className = 'reply-body text-break small mt-1';
		const replyHtml = formatReplyHtml(reply?.replyContent || reply?.content || '');
		if (replyHtml) {
			bodyEl.innerHTML = replyHtml;
		} else {
			const placeholder = document.createElement('span');
			placeholder.className = 'text-muted fst-italic small';
			placeholder.textContent = '내용 없음';
			bodyEl.appendChild(placeholder);
		}
		wrapper.appendChild(bodyEl);

		const children = (replyId && childrenMap.get(replyId)) || [];
		if (children.length) {
			const childContainer = document.createElement('div');
			childContainer.className = 'mt-2 vstack gap-1';
			children.forEach((child) => {
				childContainer.appendChild(buildReplyNode(child, depth + 1, childrenMap));
			});
			wrapper.appendChild(childContainer);
		}

		return wrapper;
	}


	function renderReplies(replies, options = {}) {
		if (!$replyList) return;
		const list = Array.isArray(replies) ? [...replies] : [];
		CURRENT.replies = [...list];
		let index = CURRENT.replyIndex;
		if (!(index instanceof Map)) {
			index = new Map();
			CURRENT.replyIndex = index;
		} else {
			index.clear();
		}
		setSortButtonState();

		const childrenMap = new Map();
		const topLevel = [];

		for (const reply of list) {
			if (!reply) continue;
			const replyId = String(reply.lctReplyId || '');
			if (replyId) index.set(replyId, reply);
			const parentIdRaw = getParentReplyId(reply);
			const parentId = parentIdRaw ? String(parentIdRaw) : '';
			if (parentId && parentId !== replyId) {
				if (!childrenMap.has(parentId)) childrenMap.set(parentId, []);
				childrenMap.get(parentId).push(reply);
			} else {
				topLevel.push(reply);
			}
		}

		const danglingParents = [];
		childrenMap.forEach((_, parentId) => {
			if (!index.has(parentId)) danglingParents.push(parentId);
		});
		danglingParents.forEach((parentId) => {
			const orphanChildren = childrenMap.get(parentId) || [];
			orphanChildren.forEach((child) => topLevel.push(child));
			childrenMap.delete(parentId);
		});

		topLevel.sort(compareReplies);
		if (CURRENT.replySortOrder === "desc") {
			topLevel.reverse();
		}
		childrenMap.forEach((children) => children.sort(compareReplies));

		$replyList.innerHTML = '';

		const errorMessage = options?.errorMessage;
		const hasReplies = topLevel.length > 0;
		if ($replyEmpty) {
			if (hasReplies) {
				$replyEmpty.classList.add('d-none');
				$replyEmpty.classList.remove('text-danger');
				if (!$replyEmpty.classList.contains('text-muted')) $replyEmpty.classList.add('text-muted');
				$replyEmpty.textContent = replyEmptyDefaultMessage;
			} else {
				$replyEmpty.classList.remove('d-none');
				if (errorMessage) {
					$replyEmpty.classList.remove('text-muted');
					$replyEmpty.classList.add('text-danger');
					$replyEmpty.textContent = errorMessage;
				} else {
					$replyEmpty.classList.remove('text-danger');
					if (!$replyEmpty.classList.contains('text-muted')) $replyEmpty.classList.add('text-muted');
					$replyEmpty.textContent = replyEmptyDefaultMessage;
				}
			}
		}

	if (!hasReplies) {
		CURRENT.highlightReplyId = "";
		updateReplySelection();
		return;
	}

		const frag = document.createDocumentFragment();
		topLevel.forEach((reply) => {
			frag.appendChild(buildReplyNode(reply, 0, childrenMap));
		});
		$replyList.appendChild(frag);
		updateReplySelection();
		applyPendingHighlight(list);
	}

	async function loadReplies() {
		if (!$replyList || !CURRENT.lectureId || !CURRENT.postId) {
			console.debug('[reply] skip load (missing context)');
			return [];
		}
		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply`;
		console.debug('[reply] GET', url);
		try {
			const res = await fetch(url, { headers: { Accept: 'application/json' } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			const replies = Array.isArray(data) ? data : [];
			renderReplies(replies);
			return replies;
		} catch (err) {
			console.error('[reply] load error', err);
			renderReplies([], { errorMessage: '댓글 목록을 불러오는 중 오류가 발생했습니다.' });
			return [];
		}
	}

	async function handleReplyListClick(event) {
		const target = event?.target;
		if (!target) return;
		const button = target.closest('[data-action]');
		if (!button || !$replyList?.contains(button)) return;

		const action = button.dataset.action;
		const replyId = button.dataset.replyId;
		if (!replyId) return;

		if (action === "choose-parent") {
			const index = CURRENT.replyIndex instanceof Map ? CURRENT.replyIndex : null;
			const reply = index ? index.get(replyId) : null;
			if (!reply) {
				console.warn('[reply] reply not found for selection', replyId);
				return;
			}
			setReplyParentSelection(replyId, reply);
		} else if (action === "delete-reply") {
			await handleReplyDelete(replyId, button);
		}
	}

	function handleReplyReset() {
		window.setTimeout(() => {
			clearReplyParentSelection();
		}, 0);
	}

	async function handleReplyDelete(replyId, triggerBtn) {
		if (!CURRENT.lectureId || !CURRENT.postId) {
			setReplyAlert("danger", "게시글 정보를 확인할 수 없습니다.");
			return;
		}

		const ok = window.confirm("댓글을 삭제하시겠습니까? 대댓글이 있다면 함께 삭제됩니다.");
		if (!ok) return;

		const prevDisabled = triggerBtn ? triggerBtn.disabled : undefined;
		const prevLabel = triggerBtn ? triggerBtn.textContent : undefined;
		if (triggerBtn) {
			triggerBtn.disabled = true;
			triggerBtn.textContent = "삭제 중...";
		}

		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply/${encodeURIComponent(replyId)}`;

		try {
			const res = await fetch(url, {
				method: "DELETE",
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});

			if (res.ok) {
				if (CURRENT.selectedReplyId === replyId) {
					clearReplyParentSelection();
				}
				setReplyAlert("success", "댓글이 삭제되었습니다.");
				await loadReplies();
			} else {
				const body = await safeReadText(res);
				console.warn("[reply] delete unexpected status", res.status, body);
				setReplyAlert("danger", `댓글 삭제에 실패했습니다. (HTTP ${res.status})`);
			}
		} catch (err) {
			console.error("[reply] delete error", err);
			setReplyAlert("danger", "댓글 삭제 중 오류가 발생했습니다.");
		} finally {
			if (triggerBtn) {
				triggerBtn.disabled = !!prevDisabled;
				triggerBtn.textContent = prevLabel || "삭제";
			}
		}
	}


	// ---------------------- 삭제 처리 핵심 ----------------------
	async function handleDeleteClick(e) {
		e?.preventDefault?.();
		hideError();

		if (!CURRENT.lectureId || !CURRENT.postId) {
			showError("삭제 대상 정보를 확인할 수 없습니다.");
			return;
		}

		// 확인 다이얼로그
		const ok = window.confirm("정말로 삭제하겠습니까?");
		if (!ok) {
			console.debug("[delete] canceled by user");
			return;
		}

		// 중복 클릭 방지 + 진행 상태 표시
		const prevText = $btnDelBottom ? $btnDelBottom.textContent : "";
		if ($btnDelBottom) {
			$btnDelBottom.setAttribute("aria-disabled", "true");
			$btnDelBottom.classList.add("disabled");
			$btnDelBottom.textContent = "삭제중…";
		}

		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}`;
		console.debug("[delete] DELETE", url);

		try {
			const headers = {
				Accept: "application/json"
				// CSRF 사용 시 예: "X-CSRF-TOKEN": getCsrfTokenFromMetaOrCookie()
			};
			const res = await fetch(url, { method: "DELETE", headers });

			console.debug("[delete] status", res.status);

			if (res.status === 204) {
				// 성공 → 게시판으로 이동
				const redirect = CURRENT.boardUrl || `${ctx}/classroom/professor/${encodeURIComponent(CURRENT.lectureId)}/board`;
				console.info("[delete] success → redirect:", redirect);
				// replace를 쓰면 뒤로가기 시 삭제된 상세로 못 돌아옴
				window.location.replace(redirect);
				return;
			}

			// 예상 외 상태코드
			const bodyText = await safeReadText(res);
			console.warn("[delete] unexpected status:", res.status, bodyText);
			showError(`삭제에 실패했습니다. (HTTP ${res.status})`);
		} catch (err) {
			console.error("[delete] error", err);
			showError("삭제 요청 중 오류가 발생했습니다.");
		} finally {
			if ($btnDelBottom) {
				$btnDelBottom.removeAttribute("aria-disabled");
				$btnDelBottom.classList.remove("disabled");
				$btnDelBottom.textContent = prevText || "삭제";
			}
		}
	}

	async function safeReadText(res) {
		try {
			return await res.text();
		} catch {
			return "";
		}
	}

	async function handleReplySubmit(event) {
		event?.preventDefault?.();
		clearReplyAlert();

		if (!CURRENT.lectureId || !CURRENT.postId) {
			setReplyAlert("danger", "게시글 정보를 확인할 수 없어 댓글을 등록할 수 없습니다.");
			return;
		}

		const content = ($replyContent?.value || "").trim();
		if (!content) {
			setReplyAlert("warning", "댓글 내용을 입력해 주세요.");
			$replyContent?.focus();
			return;
		}

		const parentId = ($replyParent?.value || "").trim() || null;
		const payload = {
			parentReplyId: parentId,
			replyContent: content,
		};

		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply`;
		const previousLabel = $replySubmit?.textContent;
		if ($replySubmit) {
			$replySubmit.disabled = true;
			$replySubmit.textContent = "등록 중...";
		}

		try {
			const res = await fetch(url, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					Accept: "application/json",
				},
				credentials: "same-origin",
				body: JSON.stringify(payload),
			});

			const raw = await res.text();
			let responseData = null;
			if (raw) {
				try {
					responseData = JSON.parse(raw);
				} catch (parseErr) {
					responseData = null;
				}
			}

			if (res.ok) {
				setReplyAlert("success", "댓글이 등록되었습니다.");
				$replyForm?.reset();
				clearReplyParentSelection();
				const newReplyId = responseData && typeof responseData === "object"
					? String(responseData.lctReplyId || responseData.replyId || responseData.id || "")
					: "";
				CURRENT.highlightReplyId = newReplyId || LATEST_REPLY_SENTINEL;
				await loadReplies();
			} else {
				console.warn("[reply] unexpected status", res.status, raw);
				setReplyAlert("danger", `댓글 등록에 실패했습니다. (HTTP ${res.status})`);
			}
		} catch (err) {
			console.error("[reply] error", err);
			setReplyAlert("danger", "댓글 등록 중 오류가 발생했습니다.");
		} finally {
			if ($replySubmit) {
				$replySubmit.disabled = false;
				$replySubmit.textContent = previousLabel || "등록";
			}
		}
	}
// -----------------------------------------------------------
// -----------------------------------------------------------

	async function init() {
		console.groupCollapsed("[init]");
		const { lectureId, postId } = parseIdsFromPath();
		console.debug("parsed:", { lectureId, postId });

		if (!lectureId || !postId) {
			linkAll("#", "", "");
			showError("잘못된 접근입니다. (lectureId/postId 파싱 실패)");
			console.groupEnd();
			return;
		}

		CURRENT.lectureId = lectureId;
		CURRENT.postId = postId;
		CURRENT.boardUrl = `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/board`;

		linkAll(CURRENT.boardUrl, lectureId, postId);
		const professorPromise = loadProfessorInfo(lectureId);
		const rosterPromise = loadStudentRoster(lectureId);

		// 삭제 버튼 이벤트 바인딩 (존재 시)
		if ($btnDelBottom) {
			$btnDelBottom.addEventListener("click", handleDeleteClick);
			console.debug("[bind] delete button bound");
		} else {
			console.debug("[bind] delete button not found");
		}

		if ($replyForm) {
			$replyForm.addEventListener("submit", handleReplySubmit);
			$replyForm.addEventListener("reset", handleReplyReset);
			console.debug("[bind] reply form bound");
			clearReplyAlert();
		}

		if ($replyList) {
			$replyList.addEventListener("click", handleReplyListClick);
			console.debug("[bind] reply list bound");
		}

		if ($replyParentClear) {
			$replyParentClear.addEventListener("click", () => {
				clearReplyParentSelection();
				$replyContent?.focus();
			});
		}

		setSortButtonState();
		if ($replySortNewest) {
			$replySortNewest.addEventListener("click", () => {
				if (CURRENT.replySortOrder === "desc") return;
				CURRENT.replySortOrder = "desc";
				setSortButtonState();
				renderReplies(CURRENT.replies);
			});
		}
		if ($replySortOldest) {
			$replySortOldest.addEventListener("click", () => {
				if (CURRENT.replySortOrder === "asc") return;
				CURRENT.replySortOrder = "asc";
				setSortButtonState();
				renderReplies(CURRENT.replies);
			});
		}

		const url = `${apiBase}/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`;
		console.debug("[fetch] GET", url);
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			console.debug("[fetch] status", res.status);
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const post = await res.json();
			console.debug("[fetch] json received");
			render(post);
			await Promise.all([professorPromise, rosterPromise]);
			await loadReplies();
		} catch (e) {
			console.error("[fetch] error", e);
			showError("게시글을 불러오는 중 오류가 발생했습니다.");
		}
		console.groupEnd();
	}

	// DOMContentLoaded 후 실행
	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();
