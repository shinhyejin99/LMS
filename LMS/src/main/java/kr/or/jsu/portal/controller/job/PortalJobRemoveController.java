package kr.or.jsu.portal.controller.job;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.vo.StaffVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 정태일
 * @since 2025. 9. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 27.     	김수현	          최초 생성
 *	2025. 9. 30.		김수현			교직원 권한 인증 코드 추가
 *  2025. 10. 31.		김수현			JSON 응답으로 변경
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/job/internal/remove")
@RequiredArgsConstructor
public class PortalJobRemoveController {

	private final PortalJobService service;

	@PostMapping("/{recruitId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeProcess(
		@PathVariable String recruitId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		try {
			// 1. 권한 검증 (교직원인지)
			if (userDetails == null || !(userDetails.getRealUser() instanceof StaffVO)) {
				log.warn("삭제 권한 없음 - 교직원 아님: recruitId={}", recruitId);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
					"success", false,
					"message", "삭제 권한이 없습니다. 교직원만 삭제할 수 있습니다."
				));
			}

			// 2. 게시글 조회
			SchRecruitDetailDTO original = service.readSchRecruitDetail(recruitId);
			if (original == null) {
				log.warn("삭제 대상 게시글 없음: recruitId={}", recruitId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
					"success", false,
					"message", "삭제할 게시글을 찾을 수 없습니다."
				));
			}

			// 3. 작성 부서 일치 여부 검증
			StaffVO staff = (StaffVO) userDetails.getRealUser();
			String currentDeptName = service.readStfDeptNameByCode(staff.getStfDeptCd());

			if (!currentDeptName.equals(original.getStfDeptName())) {
				log.warn("삭제 권한 없음 - 작성 부서 불일치: recruitId={}, 요청부서={}, 작성부서={}",
					recruitId, currentDeptName, original.getStfDeptName());
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
					"success", false,
					"message", "삭제 권한이 없습니다. 작성 부서만 삭제할 수 있습니다."
				));
			}

			// 4. 삭제 실행
			boolean deleted = service.removeSchRecruit(recruitId);

			if (deleted) {
				log.info("채용 공고 삭제 성공 - recruitId={}, 삭제자={}", recruitId, staff.getUserId());
				return ResponseEntity.ok(Map.of(
					"success", true,
					"message", "삭제되었습니다."
				));
			} else {
				log.error("채용 공고 삭제 실패 - recruitId={}", recruitId);
				return ResponseEntity.ok(Map.of(
					"success", false,
					"message", "삭제 처리에 실패했습니다."
				));
			}

		} catch (IllegalArgumentException e) {
			log.error("삭제 권한 검증 오류 - recruitId={}, error={}", recruitId, e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
				"success", false,
				"message", e.getMessage()
			));

		} catch (Exception e) {
			log.error("삭제 처리 중 예외 발생 - recruitId={}", recruitId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
				"success", false,
				"message", "삭제 처리 중 오류가 발생했습니다."
			));
		}
	}
}