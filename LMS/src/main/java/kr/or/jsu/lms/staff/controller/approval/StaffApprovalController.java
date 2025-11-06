package kr.or.jsu.lms.staff.controller.approval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.ApprovalLineRequestDetailDTO;
import kr.or.jsu.lms.staff.service.approval.StaffApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/approvals")
@RequiredArgsConstructor
public class StaffApprovalController {

	private final StaffApprovalService service;

	@GetMapping
	public String selectstaffApprovalList(@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo,
			HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails loginUser, Model model) {

		String currentUserId = loginUser.getRealUser().getUserId();
		if (currentUserId == null || currentUserId.isEmpty()) {
			System.err.println("🚨 심각한 오류: CustomUserDetails에서 User ID를 찾을 수 없습니다.");
			model.addAttribute("error", "로그인 사용자 정보를 찾을 수 없습니다.");
			return "error/accessDenied";
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("pagingInfo", pagingInfo);
		paramMap.put("currentUserId", currentUserId);

		if (pagingInfo.getCurrentPage() < 1) {
			pagingInfo.setCurrentPage(1);
		}

		// 1. 상세 검색 조건 처리 (디자인의 필터링 버튼과 연동)
		Map<String, Object> detailSearchMap = new HashMap<>();
		String stfDeptCd = request.getParameter("stfDeptCd");
		String statusCd = request.getParameter("statusCd"); // '대기', '배정완료', '승인' 필터링용
		String approvalType = request.getParameter("applyTypeCd");

		if (stfDeptCd != null && !stfDeptCd.isEmpty()) {
			detailSearchMap.put("stfDeptCd", stfDeptCd);
		}
		if (statusCd != null && !statusCd.isEmpty()) {
			detailSearchMap.put("stfStatusCd", statusCd);
		}
		if (approvalType != null && !approvalType.isEmpty()) {
			detailSearchMap.put("approvalType", approvalType);
		}

		if (!detailSearchMap.isEmpty()) {
			pagingInfo.setDetailSearch(detailSearchMap);
		}

// 검색 조건 paramMap에 통합
		paramMap.put("pagingInfo", pagingInfo);

// 🚨 [추가 로직] 2. 디자인의 현황판(파이 차트) 데이터를 조회
// Service에 readApprovalStatusCounts 메소드가 구현되어야 함.
		Map<String, Integer> statusCounts = service.readApprovalStatusCounts(currentUserId);
		model.addAttribute("statusCounts", statusCounts);

		// 3. 목록 데이터 조회
		List<Map<String, Object>> approvalList = service.readStaffApprovalList(paramMap);
		model.addAttribute("approvalList", approvalList);

		return "staff/approval/staffApprovalList";
	}

	@GetMapping("/{approveId}") // 수정됨: @PathVariable에 맞게 경로를 수정
	public String selectstaffApprovalDetail(@PathVariable String approveId, Model model) {
		ApprovalLineRequestDetailDTO approvalDetail = service.readStaffApproval(approveId);

		if (approvalDetail == null) {
			log.warn("🚨 경고: 서비스에서 조회된 결재 문서 (ID: {})가 null입니다!", approveId);
		} else {
			model.addAttribute("approval", approvalDetail);
		}

		return "staff/approval/staffApprovalDetail";
	}

	@GetMapping("/modify/{approveId}")
	public String modifystaffApprovalForm() {
		return "staff/approval/staffApprovalDetail";
	}

	/**
	 * 강의 개설 최종 승인/반려 처리 (강의 확정 로직 포함)
	 */
	@PostMapping("/process/{approveId}")
	public String processLectureApproval(@PathVariable String approveId,
	    @ModelAttribute ApprovalLineRequestDetailDTO approvalDto,
	    // @RequestParam Map<String, Object> formData 제거 (배정 로직 분리로 인해 불필요)
	    RedirectAttributes redirectAttributes) {

	    Map<String, Object> paramMap = new HashMap<>();

	    // 1. 결재 관련 데이터
	    paramMap.put("approveId", approveId);
	    paramMap.put("approveYn", approvalDto.getApproveYnnull()); // 이름 오타 (approveYnnull -> approveYn) 수정 권장
	    paramMap.put("comments", approvalDto.getComments());
	    paramMap.put("attachFileId", approvalDto.getAttachFileId());

	    // *****************************************************************
	    // 2. 강의 배정 관련 데이터는 여기서 처리하지 않습니다.
	    //    이미 /lms/staff/classroom/assignment/saveAssignment에서 DB에 저장했다고 가정합니다.
	    // *****************************************************************

	    try {
	        // [TODO: Service Layer Method]
	        // Service는 APPROVAL 상태를 변경하고, 승인('Y')인 경우 LCT_OPEN_APPLY 정보를 기반으로
	        // LECTURE 테이블에 최종 강의를 확정(INSERT)하는 로직을 수행해야 합니다.
	        service.modifyStaffApprovalProcess(paramMap);

	        redirectAttributes.addFlashAttribute("successMessage", "강의 개설 신청 결재 처리가 완료되었습니다.");

	    } catch (IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "업무 오류: " + e.getMessage());
	        return "redirect:/lms/staff/approvals/" + approveId;
	    } catch (Exception e) {
	        log.error("강의 개설 최종 승인 처리 중 시스템 오류 발생 (APPROVE_ID: {}): {}", approveId, e.getMessage(), e);
	        redirectAttributes.addFlashAttribute("errorMessage", "결재 처리 중 시스템 오류가 발생했습니다.");
	        return "redirect:/lms/staff/approvals/" + approveId;
	    }

	    return "redirect:/lms/staff/approvals";
	}
}