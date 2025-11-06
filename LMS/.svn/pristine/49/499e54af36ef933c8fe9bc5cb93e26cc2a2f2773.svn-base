package kr.or.jsu.lms.staff.controller.tuition;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.lms.staff.service.tuition.StaffTuitionRequestService;
import lombok.RequiredArgsConstructor;

/**
 * 교직원이 납부요청하는 rest 컨트롤러
 * @author 송태호
 * @since 2025. 10. 4.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 4.     	김수현	         최초 생성
 *
 * </pre>
 */
@RestController
@RequestMapping("/lms/staff/tuition")
@RequiredArgsConstructor
public class StaffTuitionRequestRestController {
	
	private final StaffTuitionRequestService service;
	
	/**
	 * 등록금 납부요청 생성 (프로시저 실행)
	 * @return
	 */
	@PostMapping("/request/create")
	public ResponseEntity<Map<String, Object>> createTuitionRequest() {
		Map<String, Object> result = new HashMap<>();
        
        try {
        	service.executeTuitionRequest(); // 프로시저 실행
            result.put("success", true);
            result.put("message", "등록금 납부요청이 완료되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "등록금 납부요청 중 오류가 발생했습니다: " + e.getMessage());
        }
		
		return ResponseEntity.ok(result);
	}
	
	
//	// DTO 추가 후 서비스랑 매퍼 추가하고 바꾸기
//	/**
//	 * 납부 목록 조회
//	 * @param yeartermCd
//	 * @param keyword
//	 */
//	public ResponseEntity<List<>> getPaymentList(
//		@RequestParam String yeartermCd
//		, @RequestParam(required = false, defaultValue = "") String keyword
//	) {
//		List<E> list = service.
//		return ResponseEntity.ok(list);
//	}
	
	// 선택 항목 납부 확인 (or 일괄 처리)

}
