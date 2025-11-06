package kr.or.jsu.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프론트엔드 전달용 채용정보 상세 DTO (api로 가져오는 데이터들을)
 * @author 송태호
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailDTO {

	private String recruitId;
    private String title;
    private String organization;      // 조직명
    private String jobType;           // internal, public, student
    private LocalDate recStartDay;
    private LocalDate recEndDay;
    private String employmentType;
    private Integer viewCnt;
    private String content;           // HTML 형식 상세 내용
    private LocalDateTime createAt;

    // 공공 채용 전용
    private String companyScale;
    private String logoUrl;
    private String homepageUrl;       // 회사 홈페이지
    private String recruitUrl;        // 채용 상세 페이지

    // 공공 채용 - 원본 데이터 (JS에서 렌더링용)
    private String recrCommCont;      // 공통자격요건
    private String empSubmitDocCont;  // 제출서류
    private String empRcptMthdCont;   // 접수방법
    private String inqryCont;         // 문의처
    private String empnEtcCont;       // 기타사항
    private String empnRecrSummaryCont; // 채용요약
}
