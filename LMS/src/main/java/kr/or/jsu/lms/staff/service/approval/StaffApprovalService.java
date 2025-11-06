package kr.or.jsu.lms.staff.service.approval;

import java.util.List;
import java.util.Map;

import kr.or.jsu.dto.ApprovalLineRequestDetailDTO;

/**
 *
 *
 * @author 신혜진
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 26.     	신혜진	          최초 생성
 *
 * </pre>
 */
public interface StaffApprovalService {

	/**
	 * 승인 여부 등록
	 * @param approval
	 */
	public void createStaffApproval(ApprovalLineRequestDetailDTO approval);

	/**
	 * 승인 전체 조회
	 * @param pagingInfo 페이징 정보 및 검색 조건을 담은 PaginationInfo (detailSearchMap에 approvalType 포함 가능)
	 * @return
	 */
	public List<Map<String, Object>> readStaffApprovalList(Map<String, Object> paramMap);

	/**
	 * 승인 한건 조회
	 * @param prevApproveId
	 * @return
	 * @throws RuntimeException
	 */
	public ApprovalLineRequestDetailDTO readStaffApproval(String prevApproveId)throws RuntimeException;

	/**
	 * 승인 수정
	 * @param approval
	 */
	public void modifyStaffApproval(ApprovalLineRequestDetailDTO approval);

	/**
	 * 결재 상태 변경 및 문서 종류별 최종 처리 (강의 개설의 경우 강의 확정 처리 포함)
	 * @param paramMap 결재 정보 (approveId, approveYnnull, comments) 및 추가 데이터 (placeCd, timeblockCdsString 등)
	 */
	public void modifyStaffApprovalProcess(Map<String, Object> paramMap);

	/**
	 * 현황판(대시보드 차트)용 결재 상태별 건수 조회
	 * @param currentUserId 현재 로그인한 사용자 ID
	 * @return Map<String, Integer> 상태별 건수 (pendingCount, rejectedCount, approvedCount, totalCount)
	 */
	public Map<String, Integer> readApprovalStatusCounts(String currentUserId);
}