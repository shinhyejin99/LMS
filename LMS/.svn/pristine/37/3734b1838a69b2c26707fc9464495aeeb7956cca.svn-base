package kr.or.jsu.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;

/**
 * 학내 채용 상세 정보 + 첨부파일 목록 통합 DTO
 * @author 김수현
 * @since 2025. 10. 31.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 31.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class SchRecruitDetailDTO implements Serializable {
private static final long serialVersionUID = 1L;

    // ===== PortalRecruitVO 필드 복사 =====
    private String recruitId;
    private String title;
    private String content;
    private String stfDeptName;
    private String attachFileId;
    private Integer viewCnt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recStartDay;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recEndDay;

    private String agencyName;
    private String recTarget;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteAt;

    // ===== 추가 필드 =====
    /**
     * 첨부파일 목록
     */
    private List<FileDetailResp> attachFiles;

    /**
     * 기본 생성자
     */
    public SchRecruitDetailDTO() {
    }
}
