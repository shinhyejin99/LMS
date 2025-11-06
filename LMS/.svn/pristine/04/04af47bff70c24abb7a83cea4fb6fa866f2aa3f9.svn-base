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
@EqualsAndHashCode(of = {"careerName", "professorNo"})
public class PrfCareerVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(max = 50)
	private String careerName;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorNo;

	@NotNull
	private LocalDateTime startDay;

	private LocalDateTime endDay;
}
