package kr.or.jsu.lms.student.service.academicChange;

import kr.or.jsu.dto.RecordApplyRequestDTO;

/**
 *
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 10. 10.		김수현			메소드 추가
 *
 * </pre>
 */
public interface StuRecordApplyService {

	/**
	 * 학적변동 신청 처리 (공통) - 수현
	 */
	String applyRecord(RecordApplyRequestDTO requestDTO, String userId);

	/**
	 * 신청 취소 - 수현
	 */
	public void cancelApply(String applyId, String studentNo);


}
