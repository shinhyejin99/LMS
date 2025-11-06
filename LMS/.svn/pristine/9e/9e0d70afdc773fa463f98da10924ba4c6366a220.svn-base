// /js/app/classroom/professor/prfStudentManage.js
(() => {
	"use strict";

	const $root = document.querySelector("#student-root");
	if (!$root) {
		console.warn("[prfStudentManage] #student-root not found. Abort.");
		return;
	}

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const studentApi = $root.dataset.studentApi;
	const photoApiBase = $root.dataset.photoApi;

	const $notice = document.getElementById("student-notice-box");
	const $container = document.getElementById("student-card-container");
	const $totalCount = document.getElementById("student-total-count");
	const $columnButtons = document.querySelectorAll("[data-card-columns]");

	const FALLBACK_IMG =
		"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='640' height='480' viewBox='0 0 640 480'%3E%3Crect width='640' height='480' fill='%23f1f3f5'/%3E%3Ctext x='50%25' y='45%25' text-anchor='middle' font-family='Arial,sans-serif' font-size='64' fill='%23adb5bd'%3ENo Photo%3C/text%3E%3Ccircle cx='320' cy='240' r='96' fill='%23ced4da'/%3E%3Crect x='200' y='360' width='240' height='60' rx='30' fill='%23ced4da'/%3E%3C/svg%3E";

	const showNotice = (message, variant) => {
		if (!$notice) return;
		$notice.textContent = message;
		$notice.classList.remove("d-none", "alert-danger", "alert-success");
		if (variant) {
			$notice.classList.add(variant);
		}
	};

	const showError = (message) => showNotice(message, "alert-danger");
	const clearNotice = () => {
		if (!$notice) return;
		$notice.textContent = "";
		$notice.classList.add("d-none");
		$notice.classList.remove("alert-danger", "alert-success");
	};

	const setContainerMessage = (message) => {
		if (!$container) return;
		$container.textContent = "";
		const col = document.createElement("div");
		col.className = "col-12";
		const box = document.createElement("div");
		box.className = "text-center text-muted py-5";
		box.textContent = message;
		col.appendChild(box);
		$container.appendChild(col);
	};

	const formatPhone = (phone) => {
		if (!phone) return "-";
		const digits = phone.replace(/\D/g, "");
		if (digits.length === 11) {
			return digits.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
		}
		if (digits.length === 10) {
			return digits.replace(/(\d{2,3})(\d{3,4})(\d{4})/, "$1-$2-$3");
		}
		return phone;
	};

	const buildCard = (student) => {
		const studentNo = student.studentNo || student.userId || "";
		const fullName = [student.lastName, student.firstName].filter(Boolean).join("") || student.userId || "미상";
		const department = student.univDeptName || "-";
		const grade = student.gradeName || "";
		const status = student.stuStatusName || "";

		const col = document.createElement("div");
		col.className = "col";

		const link = document.createElement("a");
		link.className = "card h-100 shadow-sm text-decoration-none text-reset";
		link.href = `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/student/${encodeURIComponent(studentNo)}`;
		link.dataset.studentNo = studentNo;
		link.setAttribute("aria-label", `${fullName} 학생 관리 페이지로 이동`);

		const imgWrapper = document.createElement("div");
		imgWrapper.className = "ratio bg-light";
		imgWrapper.style.setProperty("--bs-aspect-ratio", "135%");

		const img = document.createElement("img");
		img.className = "w-100 h-100";
		img.style.objectFit = "cover";
		img.loading = "lazy";
		if (studentNo && photoApiBase) {
			img.src = `${photoApiBase}/${encodeURIComponent(studentNo)}/photo`;
		} else {
			img.src = FALLBACK_IMG;
			img.style.objectFit = "contain";
		}
		img.alt = `${fullName} 증명사진`;
		img.addEventListener("error", () => {
			img.src = FALLBACK_IMG;
			img.style.objectFit = "contain";
			img.classList.add("p-4");
		});

		imgWrapper.appendChild(img);
		link.appendChild(imgWrapper);

		const body = document.createElement("div");
		body.className = "card-body";

		const nameRow = document.createElement("div");
		nameRow.className = "d-flex align-items-center gap-2 mb-1";

		const nameEl = document.createElement("h3");
		nameEl.className = "h5 mb-0";
		nameEl.textContent = fullName;
		nameRow.appendChild(nameEl);

		if (status) {
			const badge = document.createElement("span");
			badge.className = "badge bg-secondary text-white";
			badge.textContent = status;
			nameRow.appendChild(badge);
		}

		body.appendChild(nameRow);

		const idEl = document.createElement("div");
		idEl.className = "text-muted small mb-2";
		idEl.textContent = `학번 ${studentNo || "-"}`;
		body.appendChild(idEl);

		const deptEl = document.createElement("div");
		deptEl.className = "mb-2";
		deptEl.innerHTML = `<strong>${department}</strong>${grade ? ` · ${grade}` : ""}`;
		body.appendChild(deptEl);

		const contactList = document.createElement("dl");
		contactList.className = "row g-0 small mb-0";

		const phoneDt = document.createElement("dt");
		phoneDt.className = "col-4 text-muted";
		phoneDt.textContent = "연락처";
		const phoneDd = document.createElement("dd");
		phoneDd.className = "col-8 mb-1 text-end";
		phoneDd.textContent = formatPhone(student.mobileNo);

		const emailDt = document.createElement("dt");
		emailDt.className = "col-4 text-muted";
		emailDt.textContent = "이메일";
		const emailDd = document.createElement("dd");
		emailDd.className = "col-8 text-end text-truncate";
		emailDd.title = student.email || "-";
		emailDd.textContent = student.email || "-";

		contactList.appendChild(phoneDt);
		contactList.appendChild(phoneDd);
		contactList.appendChild(emailDt);
		contactList.appendChild(emailDd);
		body.appendChild(contactList);

		link.appendChild(body);
		col.appendChild(link);
		return col;
	};

	const renderStudents = (students) => {
		if (!$container) return;

		if (!students || !students.length) {
			setContainerMessage("등록된 수강생이 없습니다.");
			if ($totalCount) $totalCount.textContent = "0";
			return;
		}

		const fragment = document.createDocumentFragment();
		students.forEach((student) => fragment.appendChild(buildCard(student)));

		$container.textContent = "";
		$container.appendChild(fragment);

		if ($totalCount) {
			$totalCount.textContent = String(students.length);
		}
	};

	const loadStudents = async () => {
		if (!lectureId || !studentApi) {
			setContainerMessage("수강생 정보를 불러올 수 없습니다. (잘못된 요청)");
			showError("필수 파라미터가 누락되었습니다.");
			return;
		}

		setContainerMessage("수강생 정보를 불러오는 중입니다...");
		if ($totalCount) $totalCount.textContent = "0";
		clearNotice();

		try {
			const response = await fetch(studentApi, {
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}`);
			}

			const payload = await response.json();
			if (!Array.isArray(payload)) {
				throw new Error("Unexpected payload");
			}

			const sorted = [...payload].sort((a, b) => {
				const aKey = (a.studentNo || a.userId || "").toString();
				const bKey = (b.studentNo || b.userId || "").toString();
				return aKey.localeCompare(bKey);
			});

			renderStudents(sorted);
			clearNotice();
		} catch (error) {
			console.error("[prfStudentManage] Failed to load students:", error);
			setContainerMessage("수강생 정보를 불러오지 못했습니다.");
			showError("수강생 목록을 불러오는데 실패했습니다. 잠시 후 다시 시도해 주세요.");
			if ($totalCount) $totalCount.textContent = "0";
		}
	};

	loadStudents();

	const columnClassMap = (() => {
		if (!$container) return {};
		try {
			const raw = $container.dataset.columnsClasses;
			return raw ? JSON.parse(raw) : {};
		} catch (error) {
			console.warn("[prfStudentManage] Failed to parse column classes:", error);
			return {};
		}
	})();

	const applyColumnClass = (columnCount) => {
		if (!$container) return;
		const classes = columnClassMap[columnCount];
		if (!classes) return;

		Object.values(columnClassMap).forEach((cls) => {
			cls.split(/\s+/).forEach((c) => c && $container.classList.remove(c));
		});

		classes.split(/\s+/).forEach((c) => c && $container.classList.add(c));

		$columnButtons.forEach((btn) => {
			const value = btn.getAttribute("data-card-columns");
			if (value === columnCount) {
				btn.classList.add("active");
			} else {
				btn.classList.remove("active");
			}
		});
	};

	const defaultColumns = $container?.dataset.columnsDefault;
	if (defaultColumns) {
		applyColumnClass(String(defaultColumns));
	}

	$columnButtons.forEach((btn) => {
		btn.addEventListener("click", (event) => {
			event.preventDefault();
			const value = btn.getAttribute("data-card-columns");
			if (!value) return;
			applyColumnClass(value);
		});
	});
})();
