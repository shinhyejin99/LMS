package kr.or.jsu.lms.professor.controller.approval;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.service.ApprovalService;
import kr.or.jsu.vo.ApprovalVO;

/**
 * 
 * @author 최건우
 * @since 2025. 10. 10.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	최건우	          최초 생성
 * 
 *
 *      </pre>
 */
@Controller
@RequestMapping("/lms/professor/approvals")
public class ProfApprovalCreateController {

	@Autowired
	private ApprovalService approvalService;

	// 등록 폼
	@GetMapping("/create")
	public String createProfessorApprovalForm() {
		return "professor/approval/professorApprovalCreate";
	}


    // 등록 프로세스
    @PostMapping("/create")
    public String createApproval(
            @ModelAttribute ApprovalVO approvalVO,
            @RequestParam List<String> approverIds,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        try {
            // The user ID from the authenticated principal is passed to the service.
            approvalService.createApproval(approvalVO, approverIds, user.getRealUser().getUserId());
            return "redirect:/lms/professor/approvals"; // Redirect to approval list on success
        } catch (Exception e) {
            // Log the error and potentially add an error message to the model
            // model.addAttribute("errorMessage", "Failed to create approval: " + e.getMessage());
            return "error/exceptionView"; // Or redirect to an error page
        }
    }

	// 수정 폼
	@GetMapping("/modify/{subjectNo}")
	public String modifyProfessorApprovalForm() {
		return "prodessor/approval/professorApprovalEdit";
	}

	// 수정 프로세스
	@PostMapping("/modify/{subjectNo}")
	public String modifyProfessorrApproval() {
		return "redirect:/lms/professor/approval/{subjectNo}";
	}
}

/*
 * 교수가 결재를 '상신'하는(만들고 제출하는) 핵심적인 역할을 하는 컨트롤러. 수정 관련 기능도 일부 존재하지만, 주된 기능은 생성과
 * 조회임.
 */