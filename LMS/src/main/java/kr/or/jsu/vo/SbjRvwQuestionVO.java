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
@EqualsAndHashCode(of = {"questionNo", "subjectCd"})
public class SbjRvwQuestionVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Integer questionNo;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String subjectCd;

	@NotBlank
	@Size(max = 500)
	private String question;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String answerTypeCd;
}
