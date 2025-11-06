package kr.or.jsu.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
//@AllArgsConstructor
public class MemberVO {
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String memid;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 100)
	private String mempass;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 100)
	private String memname;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String memrole;
}