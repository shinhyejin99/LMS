package kr.or.jsu.dto;

import java.sql.Date;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;

/**
 * 학적변동 신청 통합 DTO
 * @author 김수현
 * @since 2025. 10. 15.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 15.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class UnifiedApplyResponseDTO {
	// --- 공통 필드 ---
    private String applyId;
    private String studentNo;
    private String studentName;
    private Date applyAt;
    private String applyReason;
    private String applyStatusCd;
    private String applyStatusName;
    private String attachFileId;
    private List<FileDetailResp> attachFiles;
    private String currentApproverName;
    private String approvalLine;
    private String approveAt; // 승인 테이블의 처리일시

    // --- 재학변동 필드 ---
    private String recordChangeCd;
    private String recordChangeName;
    private String disireTerm;

    // --- 소속변경 필드 ---
    private String targetDeptCd;
    private String targetDeptName;
    private String targetCollegeName;
    private String affilChangeCd; // 소속변경 고유 코드 (MJ_TRF 등)
    private String affilChangeName; // 소속변경 고유 이름

    // --- 목록 통합 및 클라이언트 필터링을 위한 식별자 ---
    /**
     * JS에서 상세 API 호출 시 사용할 신청 타입 구분자 (RECORD/AFFIL)
     */
    private String applyType;
    private String applyTypeName;

    /**
     * 목록 표시 시 사용할 최종 신청 구분 코드 (recordChangeCd 또는 affilChangeCd)
     */
    private String unifiedChangeCd;

    /**
     * 목록 표시 시 사용할 최종 신청 구분명
     */
    private String unifiedChangeName;
}
