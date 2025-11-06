package kr.or.jsu.dto;

import java.sql.Date;

import lombok.Data;

/**
 * 학적변동(소속 변경 제외) 신청 정보 DTO 
 * (학생_학적변동신청 UNIV_RECORD_APPLY 테이블)
 * 
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
 *
 * </pre>
 */
@Data
public class RecordApplyInfoDTO {

    private String applyId;
    private String studentNo;
    
    /**
     * 학적변동타입코드 (DROP/REST/RTRN/DEFR)
     */
    private String recordChangeCd;
    
    /**
     * 희망기간 (복학/졸업유예 시 사용)
     * 예: 2025_REG1
     * 자퇴/휴학은 NULL
     */
    private String disireTerm;
    private Date applyAt; // 신청일
    private String applyReason; // 신청사유
    
    /**
     * 신청상태코드 (PENDING/IN_PROGRESS/APPROVED/REJECTED)
     * 초기값: PENDING
     */
    private String applyStatusCd; 
    private String attachFileId;
    
    /**
     * 최초승인ID (결재라인, nullable)
     * TODO: 승인 기능 구현 시 사용
     */
    private String approvalLine;
}
