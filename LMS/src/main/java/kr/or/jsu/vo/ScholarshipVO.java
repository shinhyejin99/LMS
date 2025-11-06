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
@EqualsAndHashCode(of = "scholarshipId")
public class ScholarshipVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String scholarshipId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String scholarshipCd;

	@NotBlank
	@Size(max = 200)
	private String scholarshipName;

	@NotNull
	private LocalDateTime createAt;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String usingYn;

	@NotNull
	@Digits(integer = 10, fraction = 0)
	private Integer amount;

	@Size(max = 1000)
	private String description;
}
