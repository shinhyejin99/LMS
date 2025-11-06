package kr.or.jsu.lms.student.controller.academicChange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.AffilApplyRequestDTO;
import kr.or.jsu.lms.student.service.academicChange.StuAffilApplyService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 소속변경 신청 REST Controller (AJAX 처리)
 * - 전과, 복수전공, 부전공
 *
 * @author 김수현
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/student/rest/affiliation")
@RequiredArgsConstructor
public class StuAffilRestController {

	private final StuAffilApplyService service;

	public static final String MODELNAME = "requestDTO";

	/**
	 * 현재 로그인한 학생 정보를 모든 메서드에 자동 주입
	 * @param authentication
	 * @return
	 */
	@ModelAttribute("currentStudent")
	public StudentVO currentStudent(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Object realUser = userDetails.getRealUser();

		if (realUser instanceof StudentVO) {
	        return (StudentVO) realUser;
	    } else {
	        return null;
	        // 교직원/교수 등 비학생 사용자는 무조건 null을 반환
	    }

	}

	// ================================
	// 통합 신청 엔드포인트
	// ================================

	/**
	 * 소속변경 신청(통합) - 전과, 복수전공, 부전공
	 * @return
	 */
	@PostMapping("/apply")
	public ResponseEntity<Map<String, Object>> applyAffil(
		@ModelAttribute("currentStudent") StudentVO student
		, @ModelAttribute(MODELNAME) AffilApplyRequestDTO requestDTO
	) {
		// map 생성
		Map<String, Object> result = new HashMap<>();

		try {
			// 학생이 null 인지 확인함
			if(student == null) {
				result.put("success", false);
				result.put("message", "학생만 신청 가능합니다.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
			}

			// 학생 정보 설정
            requestDTO.setStudentNo(student.getStudentNo());
            requestDTO.setUnivDeptCd(student.getUnivDeptCd());

            String userId = student.getUserId();

            log.info("===== 소속변경 신청 =====");
            log.info("학번: {}", student.getStudentNo());
            log.info("타입: {}", requestDTO.getAffilChangeCd());
            log.info("목표학과: {}", requestDTO.getTargetDeptCd());

			// 신청 처리 - service 메소드
			String applyId = service.applyAffil(requestDTO, userId);

			// 성공 응답
			result.put("success", true);
			result.put("message", "소속변경 신청이 완료됐습니다.");
			result.put("applyId", applyId);

			log.info("소속변경 신청 성공 - type: {}, applyId: {}", requestDTO.getAffilChangeCd(), applyId);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			log.error("소속변경 신청 처리 중 오류 발생", e);
			result.put("success", false);
			result.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}
	}

	/**
	 * 소속변경 신청 취소
	 * @param applyId
	 * @param student
	 * @return
	 */
	@DeleteMapping("/{applyId}")
	public ResponseEntity<Map<String, Object>> cancelApply(
		@PathVariable("applyId") String applyId,
		@ModelAttribute("currentStudent") StudentVO student
	) {
		Map<String, Object> result = new HashMap<>();

		try {
			if (student == null) {
				log.warn("비인가 사용자(교직원/교수)가 학생 신청 취소 API 접근 시도 - applyId: {}", applyId);
				result.put("success", false);
				result.put("message", "권한이 없는 사용자입니다.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
			}

			String studentNo = student.getStudentNo();
			log.info("소속변경 신청 취소 - applyId: {}, studentNo: {}", applyId, studentNo);

			// 신청 취소
			service.cancelApply(applyId, studentNo);

			result.put("success", true);
			result.put("message", "신청이 취소되었습니다.");

			log.info("신청 취소 성공 - studentNo: {}, applyId: {}", studentNo, applyId);

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			log.error("신청 취소 중 오류 발생 - applyId: {}", applyId, e);
			result.put("success", false);
			result.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}
	}

	/**
     * 전과 가능한 학과 목록 조회 (같은 단과대학 내)
     */
    @GetMapping("/depts/transfer")
    public ResponseEntity<List<UnivDeptInfo>> getTransferableDepts(
        @ModelAttribute("currentStudent") StudentVO student
    ) {

        try {
            String studentNo = student.getStudentNo();
            log.info("studentNo 학번: {}", studentNo);

            List<UnivDeptInfo> depts = service.getTransferableDepts(studentNo);

            log.info("전과 가능 학과 조회 - studentNo: {}, count: {}", studentNo, depts.size());

            return ResponseEntity.ok(depts);

        } catch (Exception e) {
            log.error("전과 가능 학과 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 복수전공/부전공 가능한 학과 목록 조회 (전체)
     */
    @GetMapping("/depts/all")
    public ResponseEntity<List<UnivDeptInfo>> getAllDepts(
        @ModelAttribute("currentStudent") StudentVO student
    ) {
        try {
            String studentNo = student.getStudentNo();
            List<UnivDeptInfo> depts = service.getAllDepts(studentNo);

            log.info("전체 학과 조회 - studentNo: {}, count: {}", studentNo, depts.size());

            return ResponseEntity.ok(depts);

        } catch (Exception e) {
            log.error("전체 학과 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
