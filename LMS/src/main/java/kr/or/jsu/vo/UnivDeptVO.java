package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "univDeptCd")
public class UnivDeptVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(max = 50)
	private String univDeptCd;

	@NotBlank
	@Size(max = 50)
	private String collegeCd;

	@NotBlank
	@Size(max = 50)
	private String univDeptName;

	@NotNull
	private LocalDateTime createAt;

	private LocalDateTime deleteAt;
}
