package kr.or.jsu.dto;

import java.sql.Date;

import lombok.Data;

/**
 * 소속변경 신청 정보 DTO (DB insert)
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
@Data
public class AffilApplyInfoDTO {
	private String applyId;             // 신청ID (시퀀스)
    private String studentNo;           // 학번
    private String targetDeptCd;        // 목표 학과코드
    private String affilChangeCd;       // 소속변경 타입코드
    private Date applyAt;				// 신청일
    private String applyReason;         // 신청사유
    /**
     * 신청상태코드 (PENDING/IN_PROGRESS/APPROVED/REJECTED)
     * 초기값: PENDING
     */
    private String applyStatusCd;
    private String attachFileId;        // 첨부파일ID
    private String approvalLine;        // 승인 라인 ID
}
