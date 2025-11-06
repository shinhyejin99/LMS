package kr.or.jsu.lms.student.service.financialAid;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.PaymentReceiptDTO;
import kr.or.jsu.dto.StudentPaymentHistoryDTO;
import kr.or.jsu.dto.TuitionNoticeDTO;

/**
 *
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 10. 21.		김수현			등록금 목록 메서드 추가
 *	2025. 10. 22.		김수현			납부확인서 조회 메서드 추가
 * </pre>
 */
public interface StuTuitionService {

    /**
     * 학생의 전체 납부 내역 조회
     * @param userDetails 학생 정보
     * @return
     */
    public List<StudentPaymentHistoryDTO> getPaymentHistory(CustomUserDetails userDetails);

    /**
     * 특정 학기의 장학금 상세 조회
     * @param studentNo 학번
     * @param yeartermCd 학기코드
     * @return
     */
    public Map<String, Object> getScholarshipDetail(String studentNo, String yeartermCd);

	/**
	 * 등록금 고지서 조회
	 * @param userDetails 학생 정보
	 * @param yeartermCd 학기코드
	 * @return
	 */
	public TuitionNoticeDTO getTuitionNotice(CustomUserDetails userDetails, String yeartermCd);

	/**
	 * 납부확인서 조회
	 * @param userDetails 학생 정보
	 * @param yeartermCd 학기코드
	 * @return
	 */
	public PaymentReceiptDTO getPaymentReceipt(CustomUserDetails userDetails, String yeartermCd);
}
