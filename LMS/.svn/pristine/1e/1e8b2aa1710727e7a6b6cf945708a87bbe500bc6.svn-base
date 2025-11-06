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
@EqualsAndHashCode(of = "stfDeptCd")

public class StaffDeptVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 10)
	private String stfDeptCd;

	@NotBlank
	@Size(max = 50)
	private String stfDeptName;

	@Size(max = 15)
	private String deptTeleNo;

	@NotNull
	private LocalDateTime createAt;

	private LocalDateTime deleteAt;
}