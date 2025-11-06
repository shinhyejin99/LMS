package kr.or.jsu.lms.professor.controller.approval;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.service.approval.ProfApprovalService;
import kr.or.jsu.vo.ApprovalVO;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 22.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 22.     	최건우	          최초 생성
 *
 * </pre>
 */
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ProfApprovalApiController {

    private final ProfApprovalService profApprovalService;

    @PostMapping("/{approveId}/reject")
    public ResponseEntity<String> rejectApproval(
            @PathVariable String approveId,
            @RequestBody java.util.Map<String, String> payload,
            Authentication authentication
    ) {
        try {
            String comments = payload.get("comments");
            profApprovalService.rejectDocument(approveId, comments, authentication);

            return ResponseEntity.ok("{\"message\":\"성공적으로 반려 처리되었습니다.\"}");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"message\":\"서버 내부 오류가 발생했습니다.\"}");
        }
    }
}
