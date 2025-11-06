package kr.or.jsu.lms.staff.service.tuition;

/**
 * 교직원이 납부요청하는 서비스
 * @author 송태호
 * @since 2025. 10. 4.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 4.     	김수현	          최초 생성
 *
 * </pre>
 */
public interface StaffTuitionRequestService {

	/**
	 * 등록금 납부요청 프로시저 실행하는 메소드
	 * @throws Exception
	 */
	public void executeTuitionRequest() throws Exception;
}
