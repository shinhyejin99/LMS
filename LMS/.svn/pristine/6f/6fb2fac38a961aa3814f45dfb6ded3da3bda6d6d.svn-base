package kr.or.jsu.lms.staff.controller.tuition;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lms/staff/tuitions/payments")
public class StaffTuitionPaymentController {

	// 전체 조회
	@GetMapping
	public String selectstaffTuitionPaymentList() {
		return "staff/tuition/staffTuitionPaymentList";
	}

	// 상세조회
	@GetMapping("/{subjectNo}")
	public String selectstaffTuitionPaymentDetail() {
		return "staff/tuition/staffTuitionPaymentDetail";
	}

	// 등록 폼
	@GetMapping("/create")
	public String createstaffTuitionPaymentForm() {
		return "staff/tuition/staffTuitionPaymentCreate";
	}

	// 등록 프로세스
	@PostMapping("/create")
	public String createstaffTuitionPayment() {
		return "redirect:/lms/staff/tuitions";
	}

	// 수정 폼
	@GetMapping("/modify/{subjectNo}")
	public String modifystaffTuitionPaymentForm() {
		return "staff/tuition/staffTuitionPaymentEdit";
	}

	// 수정 프로세스
	@PostMapping("/modify/{subjectNo}")
	public String modifystaffTuitionPayment() {
		return "redirect:/lms/staff/tuition/{subjectNo}";
	}

}
