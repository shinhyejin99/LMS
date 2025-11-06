package kr.or.jsu.lms.staff.controller.scholarship;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/lms/staff/scholarships")
public class StaffScholarshipController {

	// 전체 조회
		@GetMapping
		public String selectstaffScholarshipList() {
			return "staff/scholarship/staffScholarshipList";
		}

		// 상세조회
		@GetMapping("/{subjectNo}")
		public String selectstaffScholarshipDetail() {
			return "staff/scholarship/staffScholarshipDetail";
		}

		// 등록 폼
		@GetMapping("/create")
		public String createstaffScholarshipForm() {
			return "staff/scholarship/staffScholarshipCreate";
		}

		// 등록 프로세스
		@PostMapping("/create")
		public String createstaffScholarship() {
			return "redirect:/lms/staff/scholarships";
		}

		// 수정 폼
		@GetMapping("/modify/{subjectNo}")
		public String modifystaffScholarshipForm() {
			return "staff/scholarship/staffScholarshipEdit";
		}

		// 수정 프로세스
		@PostMapping("/modify/{subjectNo}")
		public String modifystaffScholarship() {
			return "redirect:/lms/staff/scholarship/{subjectNo}";
		}

		
	}