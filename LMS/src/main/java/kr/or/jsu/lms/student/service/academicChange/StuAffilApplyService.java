package kr.or.jsu.lms.student.service.academicChange;

import java.util.List;

import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.dto.AffilApplyRequestDTO;

/**
 * 소속변경 신청 서비스 - 전과, 복수전공, 부전공
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
 *  2025. 10. 14.		김수현			소속변경 처리 기능 추가
 *
 * </pre>
 */
public interface StuAffilApplyService {

	/**
     * 소속변경 신청 처리
     * @param requestDTO 신청 정보
     * @param userId 신청자 userId
     * @return 신청 ID
     */
    String applyAffil(AffilApplyRequestDTO requestDTO, String userId);

    /**
     * 소속변경 신청 취소
     * @param applyId 신청 ID
     * @param studentNo 학번
     */
    void cancelApply(String applyId, String studentNo);

    /**
     * 전과 가능한 학과 목록 조회 (같은 단과대학 내)
     * @param studentNo 학번
     * @return 학과 목록
     */
    List<UnivDeptInfo> getTransferableDepts(String studentNo);

    /**
     * 복수전공/부전공 가능한 학과 목록 조회 (전체)
     * @param studentNo 학번
     * @return 학과 목록
     */
    List<UnivDeptInfo> getAllDepts(String studentNo);

}
