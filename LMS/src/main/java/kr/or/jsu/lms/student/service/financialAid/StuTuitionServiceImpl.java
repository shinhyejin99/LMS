package kr.or.jsu.lms.student.service.financialAid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.PaymentReceiptDTO;
import kr.or.jsu.dto.ScholarshipSimpleDTO;
import kr.or.jsu.dto.StudentPaymentHistoryDTO;
import kr.or.jsu.dto.TuitionDetailDTO;
import kr.or.jsu.dto.TuitionNoticeDTO;
import kr.or.jsu.dto.TuitionSimpleDTO;
import kr.or.jsu.mybatis.mapper.TuitionMapper;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;

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
 *	2025. 10. 4. 		김수현			학생) 등록금 고지서 조회 추가
 *	2025. 10. 21.		김수현			등록금 목록 메서드 추가
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class StuTuitionServiceImpl implements StuTuitionService {

	private final TuitionMapper mapper;
	private final DatabaseCache dbCache;

	/**
	 * 학생의 전체 납부 내역 조회
	 */
	@Override
    public List<StudentPaymentHistoryDTO> getPaymentHistory(CustomUserDetails userDetails) {
        String studentNo = userDetails.getUsername();
        return mapper.selectPaymentHistory(studentNo);
    }

	/**
	 * 특정 학기의 장학금 상세 조회
	 */
	@Override
    public Map<String, Object> getScholarshipDetail(String studentNo, String yeartermCd) {
        Map<String, Object> result = new HashMap<>();

        // 총 수혜 금액
        Integer totalAmount = mapper.selectTotalScholarship(studentNo, yeartermCd);

        // 장학금 상세 내역
        List<ScholarshipSimpleDTO> details = mapper.selectScholarshipDetails(studentNo, yeartermCd);

        // 장학금 분포 (도넛 차트)
        List<kr.or.jsu.dto.ScholarshipDistributionDTO> distribution =
            mapper.selectScholarshipDistribution(studentNo, yeartermCd);

        result.put("totalAmount", totalAmount);
        result.put("details", details);
        result.put("distribution", distribution);

        return result;
    }

	/**
	 * 등록금 고지서 조회
	 */
	@Override
	public TuitionNoticeDTO getTuitionNotice(CustomUserDetails userDetails, String yeartermCd) {
        StudentVO student = (StudentVO) userDetails.getRealUser();
        String studentNo = student.getStudentNo();

        // 1. 메인 정보 조회(등록금 고지서)
        TuitionDetailDTO mainInfo = mapper.selectTuitionNoticeMain(studentNo, yeartermCd);

        // 2. 등록금 상세 내역 조회
        List<TuitionSimpleDTO> tuitionDetails = mapper.selectTuitionDetails(studentNo, yeartermCd);

        // 3. 장학금 상세 내역 조회
        List<ScholarshipSimpleDTO> scholarshipDetails = mapper.selectScholarshipDetails(studentNo, yeartermCd);

        // 4. 통합 DTO로 묶어서 반환
        TuitionNoticeDTO result = new TuitionNoticeDTO();
        result.setMainInfo(mainInfo);
        result.setTuitionDetails(tuitionDetails);
        result.setScholarshipDetails(scholarshipDetails);

        return result;
	}

	/**
	 * 납부확인서 조회
	 */
	@Override
	public PaymentReceiptDTO getPaymentReceipt(CustomUserDetails userDetails, String yeartermCd) {
		StudentVO student = (StudentVO) userDetails.getRealUser();
        String studentNo = student.getStudentNo();

        // 1. 납부확인서 메인 정보(pdf)
        PaymentReceiptDTO receipt = mapper.selectPaymentReceipt(studentNo, yeartermCd);
        if(receipt == null) {
        	throw new IllegalStateException("납부 완료된 내역이 없습니다.");
        }

        // 2. 등록금 세부 내역
        List<TuitionSimpleDTO> tuitionDetails = mapper.selectTuitionDetails(studentNo, yeartermCd);

        // 3. 장학금 세부 내역
        List<ScholarshipSimpleDTO> scholarshipDetails = mapper.selectScholarshipDetails(studentNo, yeartermCd);

        receipt.setTuitionDetails(tuitionDetails);
        receipt.setScholarshipDetails(scholarshipDetails);

		return receipt;
	}
}
