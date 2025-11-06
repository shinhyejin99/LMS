package kr.or.jsu.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 학적변동(소속변경 제외) 신청 요청 DTO (공통)
 * 자퇴/휴학/복학/졸업유예
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
 *	2025. 10. 11.		김수현			휴학) 필드 삭제 및 추가
 *
 * </pre>
 */
@Data
public class RecordApplyRequestDTO {

	// ====================
    // 공통 필드 (모든 타입)
    // ====================

    /**
     * 신청 학생 학번
     */
    private String studentNo;

    /**
     * 학적변동타입코드 (DROP/REST/RTRN/DEFR)
     */
    private String recordChangeCd;
    private String univDeptCd; // 학과코드
    /**
     * 신청사유 (필수)
     */
    private String applyReason;

    /**
     * 첨부파일 (선택)
     */
    private List<MultipartFile> attachFiles;

    // ====================
    // 휴학 전용 필드
    // ====================

    /**
     * 휴학 종류 코드 (일반/군입대/질병/출산)
     * 예: GENERAL, MILITARY, MEDICAL, PARENT
     */
    private String leaveType;

    /**
     * 휴학 시작 학기 (예: 2025_REG1)
     * 프론트에서만 사용
     */
    private String leaveStartTerm;

    /**
     * 휴학 기간 (학기 수)
     * 1 = 1학기, 2 = 2학기
     * 프론트에서만 사용
     */
    private Integer leaveDuration;


    /**
     * 입대구분 (ARMY/NAVY/AIRF/MARN/PBLC)
     * 군입대 휴학일 때만 사용
     */
    private String militaryTypeCd;

    /**
     * 입영일 (군입대 휴학인 경우)
     * REST + MILITARY일 때만 사용
     * 형식: yyyy-MM-dd
     */
    private String joinAt;

    /**
     * 전역예정일 (군입대 휴학인 경우)
     * REST + MILITARY일 때만 사용
     * 형식: yyyy-MM-dd
     */
    private String exitAt;

    // ====================
    // 복학 전용 필드
    // ====================

    /**
     * 복학 예정 학기 (예: 2025_REG1)
     * RTRN(복학)일 때 필수
     */
    private String returnTerm;

    // ====================
    // 졸업유예 전용 필드
    // ====================

    /**
     * 희망 졸업 학기 (예: 2026_REG1)
     * DEFR(졸업유예)일 때 필수
     */
    private String deferTerm;


}
