package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "timeblockCd")
public class TimeblockVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Z0-9]*$")
	@Size(max = 6)
	private String timeblockCd;

	@NotBlank
	@Pattern(regexp = "^[A-Z]*$")
	@Size(max = 2)
	private String weekday;

	@NotBlank
	@Pattern(regexp = "^[0-9]*$")
	@Size(max = 4)
	private String startHhmm;

	@NotBlank
	@Pattern(regexp = "^[0-9]*$")
	@Size(max = 4)
	private String endHhmm;
}
