package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.dto.AffilApplyInfoDTO;
import kr.or.jsu.dto.AffilApplyResponseDTO;

/**
 * 소속변경 신청 Mapper - 전과, 복수전공, 부전공
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
@Mapper
public interface UnivAffilApplyMapper {

    /**
     * 소속변경 신청 등록
     * @param affilApplyInfo 폼 데이터
     * @return 1 / 0
     */
    public int insertAffilApply(AffilApplyInfoDTO affilApplyInfo);

    /**
     * 학생 - 소속변경 신청 목록 조회
     * @param studentNo 학번
     * @return 신청 목록 list
     */
    public List<AffilApplyResponseDTO> selectApplyListByStudent(String studentNo);

    /**
     * 소속변경 신청 상세 조회
     * @param applyId 승인ID
     * @return 상세목록 DTO
     */
    public AffilApplyResponseDTO selectApplyDetail(String applyId);

    /**
     * 소속변경 신청 삭제 (신청취소)
     * @param applyId 승인ID
     * @return 1 / 0
     */
    public int deleteApply(String applyId);

    /**
     * 중복 신청 확인
     * @param studentNo 학번
     * @param affilChangeCd 소속변경 타입코드
     * @return
     */
    public int countPendingApply (String studentNo, String affilChangeCd);

    /**
     * 같은 단과대학 내 학과 목록 조회 (전과용)
     * @param studentNo
     * @return
     */
    public List<UnivDeptInfo> selectSameCollegeDepts(String studentNo);

    /**
     * 전체 학과 목록 조회(복수전공/부전공)
     * @param studentNo
     * @return
     */
    public List<UnivDeptInfo> selectAllDepts(String studentNo);
}
