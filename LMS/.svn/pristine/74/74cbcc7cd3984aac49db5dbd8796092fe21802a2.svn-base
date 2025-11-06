package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.ApprovalLineRequestDetailDTO;
import kr.or.jsu.vo.ApprovalVO;

/**
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
 *  2025. 10. 14.     	김수현	        학생이 학적변동 신청 취소 시 승인데이터 삭제하는 메소드 추가
 *
 * </pre>
 */
@Mapper
public interface ApprovalMapper {
	public int insertApproval(Map<String, Object> paramMap);

	// 페이징 승인 문서 전체 조회
	List<Map<String, Object>> selectApprovalList(Map<String, Object> paramMap);

	// 상세 조회 결과를 ApprovalLineRequestDetailDTO로 반환하도록 수정
	public ApprovalLineRequestDetailDTO selectApproval(String approveId);

	// 문서 제목 조회
	public Map<String, Object> selectApprovalWithDocId(String approveId);

	public int updateApproval(ApprovalVO approval);

	public int deleteApproval(String prevApproveId);

	// 페이징 처리
	int selectApprovalCount(Map<String, Object> paramMap);


	// 다음 결재선 조회
	public List<ApprovalVO> selectNextApprovalLines(String approveId);

	/**
	 * 학생이 학적변동 취소시 승인 데이터가 삭제되는 메소드
	 * @param approveId 승인ID
	 * @return 1 / 0
	 */
	public int stuApplyDeleteApproval(String approveId);

	public Map<String, Object> selectApprovalStatusCounts(Map<String, Object> paramMap);
	
}
