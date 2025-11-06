package kr.or.jsu.lms.professor.approval.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.ApprovalVO;

/**
 *
 * @author 송태호
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 08.     	최건우	         최초 생성
 *	2025. 11. 03		신혜진			알림 발송 추가
 *
 * </pre>
 */
@Mapper
@Repository
public interface ProfApprovalMapper {

    List<Map<String, Object>> selectProfApprovalList(PaginationInfo<Map<String, Object>> pagingInfo);

    int selectProfApprovalCount(PaginationInfo<Map<String, Object>> pagingInfo);

    Map<String, Object> selectProfApprovalDetail(String approveId);

    int updateApprovalReject(ApprovalVO approvalVO);

    int updateRecordApplyStatus(Map<String, Object> params);

    int updateAffilApplyStatus(Map<String, Object> params);

    String selectApprovalApplyType(String approveId);

    int updateApprovalStatus(Map<String, Object> params);

    Map<String, Object> findDepartmentHead(String deptCd);

    int insertNextApproval(ApprovalVO approvalVO);

    int updateRecordApplyApprovalLine(@Param("oldApproveId") String oldApproveId, @Param("newApproveId") String newApproveId);

    int updateAffilApplyApprovalLine(@Param("oldApproveId") String oldApproveId, @Param("newApproveId") String newApproveId);

    void insertApproval(ApprovalVO approvalVO);
    List<ApprovalVO> selectApprovalList(String userId);

    /**
     *  사용자명으로 Id 조회
     * @param currentRejecterId
     * @return
     */
	String selectUserNameById(String currentRejecterId);

	ApprovalVO selectApprovalById(String approveId);

	/**
	 * 지도교수 조회
	 * @param applicantUserId
	 * @return
	 */
	String selectSupervisorIdByStudent(String userId);

}