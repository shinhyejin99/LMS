package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
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
@EqualsAndHashCode(of = "tuitionReqId")
public class StuTuitionReqVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String tuitionReqId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String studentNo;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String yeartermCd;
	
	@NotBlank
	@Size(max = 100)
	private String virtualAccount;
	
	@NotNull
	@Digits(integer = 10, fraction = 0)
	private Integer tuitionSum;
	
	@NotNull
	@Digits(integer = 10, fraction = 0)
	private Integer scholarshipSum;
	
	@NotNull
	@Digits(integer = 10, fraction = 0)
	private Integer finalAmount;
	
	@NotNull
	private LocalDateTime payStartDay;
	
	@NotNull
	private LocalDateTime payEndDay;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String payDoneYn;
}
