package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

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
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	정태일	          최초 생성
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "calendarId")
public class UnivCalendarVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String calendarId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 7)
	private String staffNo;
	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String scheduleCd;
	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String yeartermCd;
	
	@NotBlank
	@Size(max = 50)
	private String scheduleName;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String shareScopeCd;
	
	@NotNull
	private LocalDateTime startAt;
	
	@NotNull
	private LocalDateTime endAt;
	
	@NotNull
	private LocalDateTime createAt;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String deleteYn;
}
