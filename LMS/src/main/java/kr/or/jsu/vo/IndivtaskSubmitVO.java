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
@EqualsAndHashCode(of = {"enrollId", "indivtaskId"})
public class IndivtaskSubmitVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String enrollId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String indivtaskId;

	@NotBlank
	@Size(max = 1000)
	private String submitDesc;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String submitFileId;

	@NotNull
	private LocalDateTime submitAt;

	@Digits(integer = 3, fraction = 0)
	private Integer evaluScore;

	private LocalDateTime evaluAt;

	@Size(max = 1000)
	private String evaluDesc;
}
