package kr.or.jsu.lms.staff.service.tuition;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.TuitionMapper;
import lombok.RequiredArgsConstructor;

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
@Service
@RequiredArgsConstructor
public class StaffTuitionRequestServiceImpl implements StaffTuitionRequestService {

	private final TuitionMapper mapper;
	
	@Override
	@Transactional
	public void executeTuitionRequest() throws Exception {
		try {
			// 납부요청 생성하는 프로시저 실행
			mapper.callTuitionRequestProcedure();
		} catch(Exception e) {
			throw new Exception("등록금 납부요청 실행 중 오류 발생", e);
		}
		
	}

}
