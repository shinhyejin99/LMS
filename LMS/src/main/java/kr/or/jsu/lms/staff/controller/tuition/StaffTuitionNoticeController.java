package kr.or.jsu.lms.staff.controller.tuition;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lms/staff/tuitions/notices")
public class StaffTuitionNoticeController {

	// 전체 조회
	@GetMapping
	public String selectstaffTuitionNoticeList() {
		return "staff/tuition/staffTuitionNoticeList";
	}

	// 상세조회
	@GetMapping("/{subjectNo}")
	public String selectstaffTuitionNoticeDetail() {
		return "staff/tuition/staffTuitionNoticeDetail";
	}

	// 등록 폼
	@GetMapping("/create")
	public String createstaffTuitionNoticeForm() {
		return "staff/tuition/staffTuitionNoticeCreate";
	}

	// 등록 프로세스
	@PostMapping("/create")
	public String createstafTuitionNotice() {
		return "redirect:/lms/staff/tuitions/notices";
	}

	// 수정 폼
	@GetMapping("/modify/{subjectNo}")
	public String modifystaffTuitionNoticeForm() {
		return "staff/tuition/staffTuitionNoticeEdit";
	}

	// 수정 프로세스
	@PostMapping("/modify/{subjectNo}")
	public String modifystaffTuitionNotice() {
		return "redirect:/lms/staff/tuition/{subjectNo}";
	}
}
