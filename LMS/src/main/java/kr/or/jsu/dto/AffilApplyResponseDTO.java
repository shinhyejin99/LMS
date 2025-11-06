package kr.or.jsu.dto;

import java.sql.Date;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;

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
*  2025. 10. 14.     	김수현	          최초 생성
*  2025. 10. 16.		김수현			approveAt 추가
* </pre>
*/
@Data
public class AffilApplyResponseDTO {
	private String applyId;             // 신청ID
    private String studentNo;           // 학번
    private String studentName;         // 학생명

    /**
     * 목표 학과코드
     */
    private String targetDeptCd;

    /**
     * 목표 학과명
     */
    private String targetDeptName;

    /**
     * 목표 단과대학명
     */
    private String targetCollegeName;

    /**
     * 소속변경 타입코드 (ML_TRF/ML_DBL/ML_SUB)
     */
    private String affilChangeCd;

    /**
     * 소속변경 타입명 (전과 이후/복수전공/부전공)
     */
    private String affilChangeName;

    /**
     * 신청일
     */
    private Date applyAt;

    /**
     * 신청사유
     */
    private String applyReason;

    /**
     * 신청상태코드 (PENDING/IN_PROGRESS/APPROVED/REJECTED)
     */
    private String applyStatusCd;

    /**
     * 신청상태명
     */
    private String applyStatusName;

    /**
     * 첨부파일ID
     */
    private String attachFileId;

    /**
     * 첨부파일 목록
     */
    private List<FileDetailResp> attachFiles;

    /**
     * 현재 승인자명
     */
    private String currentApproverName;

    /**
     * 승인 라인 ID
     */
    private String approvalLine;
    private String approveAt; // 승인 테이블의 처리일시
    
    /**
     * 반려사유
     */
    private String rejectionReason;
}
