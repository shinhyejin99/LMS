package kr.or.jsu.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프론트엔드 전달용 채용정보 목록 DTO (api로 가져오는 데이터들을)
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
public class JobListItemDTO {

	private String recruitId;        // 채용ID (학내: SCRECR000000001, 공공: empSeqno)
    private String title;            // 채용 제목
    private String organization;     // 조직명 (학내: 부서명, 공공: 회사명)
    private String jobType;          // 채용 유형 (internal, public, student)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recStartDay;   // 접수 시작일
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recEndDay;     // 접수 종료일
    private String employmentType;   // 채용 형태 (정규직, 계약직 등)
    private Integer viewCnt;         // 조회수
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;    // 작성일

    // 공공 채용 전용
    private String companyScale;     // 기업 규모 (대기업, 중소기업 등)
    private String logoUrl;          // 로고 이미지 URL
    private String detailUrl;        // 외부 상세 페이지 URL
}
