package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.Digits;
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
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "certificateCd")
public class CertificateVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String certificateCd;
	
	@NotBlank	
	@Size(max=100)	
	private String certificateName;
	
	@NotNull		
	@Digits(integer=5, fraction=0)	
	private Integer expirePeriod;
	
	@NotBlank	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String shareScopeCd;

	private String templatePath;
}
