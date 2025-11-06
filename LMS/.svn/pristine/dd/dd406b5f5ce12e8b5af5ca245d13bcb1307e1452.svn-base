package kr.or.jsu.lms.student.controller.academicChange;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.RecordApplyRequestDTO;
import kr.or.jsu.lms.student.service.academicChange.StuRecordApplyService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학적변동 신청 REST Controller (AJAX 처리) - 휴학, 복학, 졸업유예, 자퇴
 *
 * @author 김수현
 * @since 2025. 10. 10.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	김수현	          최초 생성
 *	2025. 10. 13 		신혜진			검증 체크 추가
 *						김수현			신청 현황 기능 분리(조회 API 제거)
 *      </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/student/rest/record")
@RequiredArgsConstructor
public class StuRecordRestContoller {

	private final StuRecordApplyService service;

	public static final String MODELNAME = "requestDTO";

	/**
	 * 현재 로그인한 학생 정보를 모든 메서드에 자동 주입 @ModelAttribute 활성화
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
	 * 재학 상태 변경 신청 (통합) - recordChangeCd로 자퇴/휴학/복학/졸업유예 구분
	 *
	 * @param requestDTO RecordApplyRequestDTO
	 * @param student      현재 로그인 학생
	 * @return ResponseEntity<Map<String, Object>>
	 */
	@PostMapping("/apply")
	public ResponseEntity<Map<String, Object>> applyRecord(@ModelAttribute(MODELNAME) RecordApplyRequestDTO requestDTO,
			@ModelAttribute("currentStudent") StudentVO student) {
		Map<String, Object> result = new HashMap<>();

		try {
			// 수정: student 객체가 null인지 확인하여 403 반환
			if (student == null) {
				log.info("비인가 사용자(교직원/교수)가 학생 신청 API 접근 시도 - RecordChangeCd: {}", requestDTO.getRecordChangeCd());
				result.put("success", false);
				result.put("message", "학생만 신청 가능합니다. (권한 없음)");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
			}

			// 학생 정보 설정
			String studentNo = student.getStudentNo();
			String userId = student.getUserId();
			String univDeptCd = student.getUnivDeptCd();

			log.info("studentNo 학번: {}", studentNo);

			requestDTO.setStudentNo(studentNo);
			requestDTO.setUnivDeptCd(univDeptCd);

			// 신청 처리
			String applyId = service.applyRecord(requestDTO, userId);

			// 성공 응답
			result.put("success", true);
			result.put("message", getSuccessMessage(requestDTO.getRecordChangeCd()));
			result.put("applyId", applyId);

			log.info("재학상태변경 신청 성공 - type: {}, studentNo: {}, applyId: {}", requestDTO.getRecordChangeCd(), studentNo,
					applyId);

			return ResponseEntity.ok(result);

		} catch (Exception e) {
			log.error("재학상태변경 신청 처리 중 오류 발생", e);
			result.put("success", false);
			result.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}
	}


	// ================================
	// 취소 API
	// ================================

	/**
	 * 신청 취소
	 *
	 * @param applyId 학적변동신청ID
	 * @param student 현재 로그인 학생
	 * @return ResponseEntity<Map<String, Object>>
	 */
	@DeleteMapping("/{applyId}")
	public ResponseEntity<Map<String, Object>> cancelApply(@PathVariable("applyId") String applyId,
			@ModelAttribute("currentStudent") StudentVO student) {
		Map<String, Object> result = new HashMap<>();

		try {
			if (student == null) {
				log.warn("비인가 사용자(교직원/교수)가 학생 신청 취소 API 접근 시도 - applyId: {}", applyId);
				result.put("success", false);
				result.put("message", "권한이 없는 사용자입니다.");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
			}

			String studentNo = student.getStudentNo();

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

	// ================================
	// 성공 메시지 반환
	// ================================

	/**
	 * 신청 타입별 성공 메시지 반환
	 *
	 * @param recordChangeCd 재학상태변경코드
	 * @return String 성공 메시지
	 */
	private String getSuccessMessage(String recordChangeCd) {
		switch (recordChangeCd) {
		case "DROP":
			return "자퇴 신청이 완료되었습니다.";
		case "REST":
			return "휴학 신청이 완료되었습니다.";
		case "RTRN":
			return "복학 신청이 완료되었습니다.";
		case "DEFR":
			return "졸업유예 신청이 완료되었습니다.";
		default:
			return "신청이 완료되었습니다.";
		}
	}
}