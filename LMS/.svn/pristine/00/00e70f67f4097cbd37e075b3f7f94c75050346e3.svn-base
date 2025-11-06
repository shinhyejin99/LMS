package kr.or.jsu.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
*
* </pre>
*/
@Data
public class AffilApplyRequestDTO {
	private String studentNo;           // 학번
    private String univDeptCd;          // 현재 학과코드
    /**
     * 소속변경 타입코드 (ML_TRF: 전과 이후/ML_DBL/ML_SUB)
     */
    private String affilChangeCd;

    /**
     * 목표 학과코드
     */
    private String targetDeptCd;

    /**
     * 신청사유
     */
    private String applyReason;

    /**
     * 첨부파일
     */
    private List<MultipartFile> attachFiles;
}
