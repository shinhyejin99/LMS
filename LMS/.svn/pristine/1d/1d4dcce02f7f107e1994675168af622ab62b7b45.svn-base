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
@EqualsAndHashCode(of = {"choiceNo", "lctExamId", "questionNo"})
public class QuestionChoiceVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Integer choiceNo;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lctExamId;

	@NotNull
	private Integer questionNo;

	@NotBlank
	@Size(max = 500)
	private String choiceContent;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String isCorrectYn;
}
