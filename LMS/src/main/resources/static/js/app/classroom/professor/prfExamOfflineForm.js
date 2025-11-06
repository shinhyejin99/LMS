(function () {
	const $ = (selector, root = document) => root.querySelector(selector);
	const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

	const normalizeLocalDateTime = (value) => {
		if (!value) return null;
		return value.length === 16 ? `${value}:00` : value;
	};

	const buildFullName = (student) => {
		const lastName = student?.lastName ?? "";
		const firstName = student?.firstName ?? "";
		const fullName = `${lastName} ${firstName}`.trim();
		return fullName || student?.userId || "";
	};

	document.addEventListener("DOMContentLoaded", async () => {
		const root = $("#exam-offline-root");
		if (!root) return;

		const form = $("#exam-offline-form", root);
		const alertBox = $("#alert-box");
		const toastBox = $("#toast-box");
		const submitBtn = $("#submit-btn", form);
		const randomBtn = $("#random-score-btn", root);
		const studentsPlaceholder = $("#students-placeholder", root);
		const studentGrid = $("#student-score-list", root);

		const examNameInput = $("#examName", form);
		const examDescInput = $("#examDesc", form);
		const startAtInput = $("#startAt", form);
		const endAtInput = $("#endAt", form);
		const weightInput = $("#weightValue", form);

		const studentsApi = root.dataset.studentApi || "";
		const submitApi = root.dataset.submitApi || "";

		const defaultSubmitLabel = submitBtn?.textContent?.trim() || "저장";
		let isSubmitting = false;
		let studentsCache = [];

		const clearMessages = () => {
			if (alertBox) {
				alertBox.classList.add("d-none");
				alertBox.textContent = "";
			}
			if (toastBox) {
				toastBox.classList.add("d-none");
				toastBox.textContent = "";
			}
		};

		const showAlert = (message) => {
			if (!alertBox) return;
			alertBox.textContent = message;
			alertBox.classList.remove("d-none");
			if (toastBox) {
				toastBox.classList.add("d-none");
			}
		};

		const showToast = (message) => {
			if (!toastBox) return;
			toastBox.textContent = message;
			toastBox.classList.remove("d-none");
			if (alertBox) {
				alertBox.classList.add("d-none");
			}
		};

		const refreshActionStates = () => {
			const noStudents = studentsCache.length === 0;
			if (submitBtn) {
				submitBtn.disabled = isSubmitting || noStudents;
				submitBtn.textContent = isSubmitting ? "저장 중..." : defaultSubmitLabel;
			}
			if (randomBtn) {
				randomBtn.disabled = noStudents;
			}
		};

		const setSubmitting = (flag) => {
			isSubmitting = Boolean(flag);
			refreshActionStates();
		};

		const setStudentsPlaceholder = (message) => {
			if (studentsPlaceholder) {
				studentsPlaceholder.textContent = message;
				studentsPlaceholder.classList.remove("d-none");
			}
			if (studentGrid) {
				studentGrid.classList.add("d-none");
				studentGrid.replaceChildren();
			}
		};

		const createStudentCard = (student, index) => {
			const col = document.createElement("div");
			col.className = "col";

			const card = document.createElement("div");
			card.className = "card h-100 shadow-sm border-0";

			const body = document.createElement("div");
			body.className = "card-body p-3";

			const indexBadge = document.createElement("div");
			indexBadge.className = "text-muted small mb-1";
			indexBadge.textContent = `#${index + 1}`;

			const nameEl = document.createElement("div");
			nameEl.className = "fw-semibold";
			nameEl.textContent = buildFullName(student);

			const metaEl = document.createElement("div");
			metaEl.className = "text-muted small";
			const metaPieces = [];
			if (student.studentNo) metaPieces.push(student.studentNo);
			if (student.univDeptName) metaPieces.push(student.univDeptName);
			if (student.gradeName) metaPieces.push(student.gradeName);
			metaEl.textContent = metaPieces.join(" · ") || "\u00A0";

			const scoreGroup = document.createElement("div");
			scoreGroup.className = "mt-3";

			const inputId = `score-${student.enrollId || index}`;
			const scoreLabel = document.createElement("label");
			scoreLabel.className = "form-label small mb-1";
			scoreLabel.setAttribute("for", inputId);
			scoreLabel.textContent = "점수 (0~100)";

			const scoreInput = document.createElement("input");
			scoreInput.type = "number";
			scoreInput.id = inputId;
			scoreInput.name = inputId;
			scoreInput.min = "0";
			scoreInput.max = "100";
			scoreInput.step = "1";
			scoreInput.required = true;
			scoreInput.inputMode = "numeric";
			scoreInput.autocomplete = "off";
			scoreInput.className = "form-control score-input";
			scoreInput.dataset.enrollId = student.enrollId || "";
			scoreInput.setAttribute(
				"aria-label",
				`${buildFullName(student) || student.enrollId || "학생"} 점수 입력`
			);

			scoreInput.addEventListener("input", () => {
				scoreInput.classList.remove("is-invalid");
			});

			scoreGroup.appendChild(scoreLabel);
			scoreGroup.appendChild(scoreInput);

			body.appendChild(indexBadge);
			body.appendChild(nameEl);
			body.appendChild(metaEl);
			body.appendChild(scoreGroup);

			card.appendChild(body);
			col.appendChild(card);
			return col;
		};

		const renderStudents = (students) => {
			if (!studentGrid) return;
			if (!Array.isArray(students) || students.length === 0) {
				setStudentsPlaceholder("수강 중인 학생 정보가 없습니다.");
				refreshActionStates();
				return;
			}

			const fragment = document.createDocumentFragment();
			students.forEach((student, idx) => {
				fragment.appendChild(createStudentCard(student, idx));
			});

			studentGrid.replaceChildren(fragment);
			studentGrid.classList.remove("d-none");
			if (studentsPlaceholder) {
				studentsPlaceholder.classList.add("d-none");
			}
			refreshActionStates();
		};

		const fetchStudents = async () => {
			if (!studentsApi) {
				setStudentsPlaceholder("학생 조회 경로가 설정되지 않았습니다.");
				studentsCache = [];
				refreshActionStates();
				return;
			}

			try {
				setStudentsPlaceholder("학생 정보를 불러오는 중입니다...");
				const response = await fetch(studentsApi, {
					method: "GET",
					credentials: "same-origin",
					headers: {
						Accept: "application/json"
					}
				});

				if (!response.ok) {
					throw new Error(`HTTP ${response.status}`);
				}

				const data = await response.json();
				if (!Array.isArray(data)) {
					throw new Error("API 응답 형식이 올바르지 않습니다.");
				}

				studentsCache = data;
				renderStudents(studentsCache);
			} catch (err) {
				console.error("학생 정보 조회 실패:", err);
				studentsCache = [];
				setStudentsPlaceholder("학생 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
				refreshActionStates();
				showAlert("학생 정보를 불러오지 못했습니다. 네트워크 상태를 확인해 주세요.");
			}
		};

		const getScoreInputs = () => $$(".score-input", studentGrid || root);

		const resetScoreInputs = () => {
			getScoreInputs().forEach((input) => {
				input.value = "";
				input.classList.remove("is-invalid");
			});
		};

		const validateAndBuildPayload = () => {
			const examName = examNameInput?.value?.trim() ?? "";
			const examDesc = examDescInput?.value?.trim() ?? "";
			const startAtRaw = startAtInput?.value || "";
			const endAtRaw = endAtInput?.value || "";
			const weightRaw = weightInput?.value?.trim() ?? "";

			if (!examName) {
				examNameInput?.focus();
				throw new Error("시험명을 입력해 주세요.");
			}
			if (!examDesc) {
				examDescInput?.focus();
				throw new Error("시험 설명을 입력해 주세요.");
			}

			const startAt = normalizeLocalDateTime(startAtRaw);
			const endAt = normalizeLocalDateTime(endAtRaw);

			if (!startAt) {
				startAtInput?.focus();
				throw new Error("시험 시작 일시를 선택해 주세요.");
			}
			if (!endAt) {
				endAtInput?.focus();
				throw new Error("시험 종료 일시를 선택해 주세요.");
			}

			const startDate = new Date(startAt);
			const endDate = new Date(endAt);
			if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
				throw new Error("시험 일시 형식이 올바르지 않습니다.");
			}
			if (startDate >= endDate) {
				startAtInput?.focus();
				throw new Error("종료 일시는 시작 일시보다 뒤여야 합니다.");
			}

			let weightValue = null;
			if (weightRaw.length > 0) {
				const parsed = Number.parseInt(weightRaw, 10);
				if (!Number.isInteger(parsed) || parsed < 0 || parsed > 100) {
					weightInput?.focus();
					throw new Error("가중치는 0 이상 100 이하의 정수로 입력해 주세요.");
				}
				weightValue = parsed;
			}

			const scoreInputs = getScoreInputs();
			if (scoreInputs.length === 0) {
				throw new Error("입력할 학생이 없습니다. 학생 목록을 확인해 주세요.");
			}

			const eachResultList = scoreInputs.map((input) => {
				const raw = input.value.trim();
				const enrollId = input.dataset.enrollId || "";
				if (!raw) {
					input.classList.add("is-invalid");
					input.focus();
					throw new Error("모든 학생의 점수를 입력해 주세요.");
				}
				const score = Number.parseInt(raw, 10);
				if (!Number.isInteger(score) || score < 0 || score > 100) {
					input.classList.add("is-invalid");
					input.focus();
					throw new Error("점수는 0 이상 100 이하의 정수로 입력해 주세요.");
				}
				return { enrollId, score };
			});

			const payload = {
				examName,
				examDesc,
				startAt,
				endAt,
				weightValue,
				eachResultList
			};

			console.info("[OfflineExam] 생성 요청 payload", payload);
			return payload;
		};

		form?.addEventListener("reset", () => {
			window.setTimeout(() => {
				clearMessages();
				resetScoreInputs();
			}, 0);
		});

		randomBtn?.addEventListener("click", () => {
			clearMessages();
			const inputs = getScoreInputs();
			if (inputs.length === 0) {
				showAlert("점수를 입력할 학생이 없습니다. 학생 목록을 확인해 주세요.");
				return;
			}
			inputs.forEach((input) => {
				const randomScore = Math.floor(Math.random() * 101);
				input.value = String(randomScore);
				input.classList.remove("is-invalid");
			});
			showToast("디버깅용 랜덤 점수가 채워졌습니다.");
		});

		form?.addEventListener("submit", async (event) => {
			event.preventDefault();
			clearMessages();

			if (!form.checkValidity()) {
				form.reportValidity();
				return;
			}

			if (!submitApi) {
				showAlert("제출 경로가 설정되지 않았습니다.");
				return;
			}

			let payload;
			try {
				payload = validateAndBuildPayload();
			} catch (err) {
				if (err instanceof Error) {
					showAlert(err.message);
				} else {
					showAlert("입력값을 확인해 주세요.");
				}
				return;
			}

			try {
				setSubmitting(true);

				const response = await fetch(submitApi, {
					method: "POST",
					credentials: "same-origin",
					headers: {
						"Content-Type": "application/json",
						Accept: "application/json"
					},
					body: JSON.stringify(payload)
				});

				if (!response.ok) {
					const errorText = await response.text().catch(() => "");
					throw new Error(errorText || `HTTP ${response.status}`);
				}

				showToast("시험 기록이 저장되었습니다.");
				form.reset();
				resetScoreInputs();
			} catch (err) {
				console.error("오프라인 시험 기록 실패:", err);
				const message = err instanceof Error ? err.message : "오류가 발생했습니다.";
				showAlert(`시험 기록 저장에 실패했습니다. ${message}`);
			} finally {
				setSubmitting(false);
			}
		});

		refreshActionStates();
		await fetchStudents();
	});
})();
