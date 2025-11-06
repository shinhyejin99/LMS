package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author 최건우
 * @since 2025. 9. 26.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 26.     	최건우
 *
 *      </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"professorNo"})
@ToString(callSuper = true)
public class ProfessorVO extends UsersVO implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorNo;

	
	@Pattern(regexp = "^[A-Za-z0-9-]*$")
	@Size(max = 50)
	private String univDeptCd;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String engLname;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String engFname;

	@NotBlank
	@Size(max = 50)
	private String prfStatusCd;

	@NotBlank
	@Pattern(regexp="^[A-Za-z0-9-_]*$")
	@Size(max = 50)
	private String prfAppntCd;


	@Size(max = 50)
	private String prfPositCd;
	
	private String profImgPath;
	
	private String postcode;
	private String address;
	private String detailAddress;
	private String officeNo;
	private String accountHolder;
}
