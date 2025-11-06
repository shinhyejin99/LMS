package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "commonCd")
public class CommonCodeVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String	commonCd;
	
	@NotBlank	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String	commonSortCd;
		
	@Size(max=100)	
	private String	cdName;
	
	@Size(max=1000)	
	private String	cdDesc;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String	usingYn;
}
