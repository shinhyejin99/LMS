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
@EqualsAndHashCode(of = {"enrollId", "lctExamId"})
public class ExamSubmitVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String enrollId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lctExamId;

	@NotNull
	private LocalDateTime submitAt;

	@Digits(integer = 3, fraction = 0)
	private Integer autoScore;

	@Digits(integer = 3, fraction = 0)
	private Integer modifiedScore;

	@Size(max = 500)
	private String modifyReason;
}
