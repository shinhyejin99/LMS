package kr.or.jsu.lms.professor.service.approval;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.ApprovalVO;

public interface ProfApprovalService {

    /**
     * 교수 결재 문서 목록을 조회합니다.
     * @param pagingInfo 페이징 정보 및 검색 조건을 담은 PaginationInfo (detailSearchMap에 approvalType 포함 가능)
     * @return 결재 문서 목록 (List<Map<String, Object>>)
     */
    List<Map<String, Object>> readProfApprovalList(PaginationInfo<Map<String, Object>> pagingInfo);

    Map<String, Object> readProfApprovalDetail(String approveId);

    void rejectDocument(String approveId, String comments, org.springframework.security.core.Authentication authentication);

    /**
     * 결재를 승인하고 다음 결재자(학과장)에게 넘깁니다.
     * @param approveId 현재 결재 ID
     * @param comments 승인 의견
     * @param authentication 현재 로그인한 사용자 정보
     */
    void approveAndForward(String approveId, String comments, org.springframework.security.core.Authentication authentication);
}