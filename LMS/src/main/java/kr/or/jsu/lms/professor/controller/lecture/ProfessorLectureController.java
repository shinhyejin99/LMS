package kr.or.jsu.lms.professor.controller.lecture;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/lms/professor/lecture")
public class ProfessorLectureController {

	// 강의 개설 신청 UI
	@GetMapping("/apply/new")
	public String applyForm() {
		return "professor/lecture/lctApplyForm";
	}
	
	// 강의 신청 목록
	@GetMapping("/apply/list")
	public String applyList() {
		return "professor/lecture/lctApplyList";
	}
	
	// 강의 신청 상세
	@GetMapping("/apply/{lctOpenApplyId}")
	public String applyDetail(
		@PathVariable String lctOpenApplyId
		, Model model
	) {
		model.addAttribute("lctOpenApplyId", lctOpenApplyId);
		
		return "professor/lecture/lctApplyDetail";
	}
	
	// 강의 신청 "승인" 목록
	@GetMapping("/approval/list")
	public String approvalList() {
		return "professor/lecture/lctApprovalList";
	}
}