package kr.or.jsu.lms.professor.controller.approval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.service.approval.ProfApprovalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      		수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	최건우	          최초 생성
 *
 * </pre>
 */
@Controller
@RequestMapping("/lms/professor/approvals")
public class ProfApprovalListController {
	
	private static final Logger log = LoggerFactory.getLogger(ProfApprovalListController.class);

	@Autowired
	private ProfApprovalService service;
	
	@GetMapping
	public String selectprofessorApprovalList(
			@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo
			, @RequestParam(required = false) String approvalType
			, @RequestParam(required = false) String processStatus
			, Model model, Authentication authentication
			) {
		
		if (pagingInfo.getCurrentPage() < 1) {
            pagingInfo.setCurrentPage(1);
        }

        // 로그인한 교수 정보 가져오기
        String approverUserId = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            approverUserId = customUserDetails.getRealUser().getUserId();
        }
        if (approverUserId == null) {
            // 교수 정보가 없으면 에러 처리 또는 빈 목록 반환
            model.addAttribute("approvalList", List.of());
            return "professor/approval/professorApprovalList";
        }

        // 상세 검색 파라미터를 수동으로 Map에 담아 PaginationInfo에 설정.
        Map<String, Object> detailSearchMap = new HashMap<>();
        detailSearchMap.put("approverUserId", approverUserId); // Add approverUserId to filter

        if (approvalType != null && !approvalType.isEmpty()) {
            detailSearchMap.put("approvalType", approvalType);
        }

        if (processStatus != null && !processStatus.isEmpty()) {
            detailSearchMap.put("processStatus", processStatus);
        }

        if (!detailSearchMap.isEmpty()) {
            pagingInfo.setDetailSearch(detailSearchMap);
        }
        
        List<Map<String, Object>> approvalList = service.readProfApprovalList(pagingInfo);
        
        log.debug("Approval List: {}", approvalList); // Add this debug log
        
        model.addAttribute("approvalList", approvalList);
        model.addAttribute("approvalType", approvalType);
        model.addAttribute("processStatus", processStatus);
        return "professor/approval/professorApprovalList";
    }

	// 상세조회
	@GetMapping("/{approveId}")
	public String selectprofessorApprovalDetail(@PathVariable String approveId, Model model, HttpServletRequest request) {
		Map<String, Object> approvalDetail = service.readProfApprovalDetail(approveId);
		model.addAttribute("approvalDetail", approvalDetail);
		
		// Check if it's an AJAX request
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			return "professor/approval/professorApprovalDetailFragment";
		} else {
			return "professor/approval/professorApprovalDetail";
		}
	}
	
	@GetMapping("/api/detail/{approveId}")
	@ResponseBody
	public Map<String, Object> getApprovalDetailApi(@PathVariable String approveId) {
		return service.readProfApprovalDetail(approveId);
	}

	@PostMapping("/api/approve")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> approveDocument(
	        @RequestBody Map<String, String> payload,
	        Authentication authentication
	) {
	    Map<String, Object> response = new HashMap<>();
	    String approveId = payload.get("approveId");
	    try {
	        String comments = payload.get("comments");
	        
	        // The service will handle extracting user details from Authentication
	        service.approveAndForward(approveId, comments, authentication);
	        
	        response.put("success", true);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        log.error("결재 승인 처리 중 오류 발생. ID: {}", approveId, e);
	        response.put("success", false);
	        response.put("message", "결재 처리 중 오류가 발생했습니다: " + e.getMessage());
	        return ResponseEntity.badRequest().body(response);
	    }
	}

	@PostMapping("/api/reject")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> rejectDocument(
	        @RequestBody Map<String, String> payload,
			Authentication authentication
	) {
	    Map<String, Object> response = new HashMap<>();
	    String approveId = payload.get("approveId");
	    String comments = payload.get("comments");

	    try {
	        service.rejectDocument(approveId, comments, authentication);
	        response.put("success", true);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        log.error("결재 반려 처리 중 오류 발생. ID: {}", approveId, e);
	        response.put("success", false);
	        response.put("message", "반려 처리 중 오류가 발생했습니다: " + e.getMessage());
	        return ResponseEntity.badRequest().body(response);
	    }
	}
}
/*
 * 교수가 자신에게 온 결재 요청을 확인하고 처리하는 기능을 담당 즉 교수가 '결재자' 입장에서 사용하는 기능
 */