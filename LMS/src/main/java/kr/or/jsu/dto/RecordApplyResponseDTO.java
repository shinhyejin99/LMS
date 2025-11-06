package kr.or.jsu.dto;

import java.sql.Date;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;

/**
 * 학적변동(소속변경제외) 신청 응답 DTO
 * @author 김수현
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	김수현	          최초 생성
 *  2025. 10. 16.		김수현			approveAt 추가
 * </pre>
 */
@Data
public class RecordApplyResponseDTO {

	private String applyId;
    private String studentNo;
    private String studentName;

    /**
     * 학적변동타입코드 (DROP/REST/RTRN/DEFR)
     */
    private String recordChangeCd;

    /**
     * 학적변동타입명 (조인)
     * 예: 자퇴, 휴학, 복학, 졸업유예
     */
    private String recordChangeName;

    /**
     * 희망기간 (복학/졸업유예 시)
     * 예: 2025_REG1
     */
    private String disireTerm;
    private Date applyAt; // 신청일
    private String applyReason; // 신청사유

    /**
     * 신청상태코드 (PENDING/IN_PROGRESS/APPROVED/REJECTED)
     */
    private String applyStatusCd;

    /**
     * 신청상태명 (조인)
     * 예: 대기, 처리중, 승인 완료, 반려
     */
    private String applyStatusName;
    private String attachFileId;
    private List<FileDetailResp> attachFiles;

    /**
     * 현재 처리중인 승인자 정보 (선택)
     * TODO: 승인 기능 구현 시 사용
     */
    private String currentApproverName;

    /**
     * 승인 라인 ID (첫 번째 승인자)
     */
    private String approvalLine;
    private String approveAt; // 승인 테이블의 처리일시
    
    /**
     * 반려사유
     */
    private String rejectionReason;
}
