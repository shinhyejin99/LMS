package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	정태일	          최초 생성
 *  2025. 10. 23.     	정태일	          Dday 수동 getter/setter 생성
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "certReqId")
public class CertificateReqVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String certReqId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String userId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String certificateCd;

	private String certificateName;
	
	@NotNull
	private LocalDateTime requestAt;

	@NotNull
	private LocalDateTime expireAt;
	
	private Integer copies;

	private String submissionPlace;

	private String purpose;

	private String statusCd;
	
	private String dDayText; // D-Day 표시용 필드  
	
    public String getdDayText() {
        return dDayText;
    }

    public void setdDayText(String dDayText) {
        this.dDayText = dDayText;
    }
	
    public String getFormattedRequestAt() {
        if (this.requestAt != null) {
            return this.requestAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        }
        return "";
    }
	
    public String getFormattedExpireAt() {
        if (this.expireAt != null) {
            return this.expireAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
        }
        return "";
    }
}
