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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "enrollId")
public class StuEnrollLctVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String enrollId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String studentNo;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lectureId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String enrollStatusCd;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String retakeYn;
	
	@NotNull
	private LocalDateTime statusChangeAt;
	
	private Double autoGrade;
	
	private Double finalGrade;
	
	@Size(max = 500)
	private String changeReason;
}
