(() => {
	"use strict";

	const $container = document.getElementById("user-info-container");
	const $text = document.getElementById("user-info-text");
	if (!$container || !$text) return;

	const ctx = document.body.dataset.ctx || "";
	const userRole = $container.dataset.userRole;

	let api_url = "";
	let is_professor = false;

	if (userRole === "professor") {
		api_url = `${ctx}/classroom/api/v1/professor/me`;
		is_professor = true;
	} else if (userRole === "student") {
		api_url = `${ctx}/classroom/api/v1/student/me`;
		is_professor = false;
	} else {
		return; // No user role found
	}

	const key_map = {
		lastName: "성",
		firstName: "이름",
		engLname: "영문 성",
		engFname: "영문 이름",
		mobileNo: "연락처",
		email: "이메일",
		professorNo: "교번",
		studentNo: "학번",
		univDeptName: "소속",
		gradeName: "학년",
		stuStatusName: "학적상태",
		prfStatusName: "상태",
		prfAppntName: "임용구분",
		prfPositName: "직책",
	};

	const format_popover_content = (user) => {
		let content = "<dl class=\"mb-0\">";
		for (const [key, value] of Object.entries(user)) {
			if (!value || key === "userId" || key.endsWith("Cd")) {
				continue;
			}
			const label = key_map[key] || key;
			content += `<dt>${label}</dt><dd>${value}</dd>`;
		}
		content += "</dl>";
		return content;
	};

	const init = async () => {
		try {
			const res = await fetch(api_url, { headers: { "Accept": "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const user = await res.json();

			// Update text
			let display_text = "";
			if (is_professor) {
				display_text = `${user.univDeptName || ""} ${user.lastName || ""}${user.firstName || ""}`;
			} else {
				display_text = `${user.univDeptName || ""} ${user.gradeName || ""} ${user.lastName || ""}${user.firstName || ""}`;
			}
			$text.innerHTML = `<i class=\"bi bi-person-circle me-1\"></i> ${display_text.trim()}`;

			// Init popover
			new bootstrap.Popover($container, {
				trigger: "hover",
				placement: "bottom",
				html: true,
				title: "내 정보",
				content: format_popover_content(user),
			});

		} catch (err) {
			console.error("Failed to fetch user info:", err);
			$text.textContent = "정보 로딩 실패";
		}
	};

	init();

})();