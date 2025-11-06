package kr.or.jsu.vo;

import java.io.Serializable;

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
@EqualsAndHashCode(of = {"questionNo", "lctExamId"})
public class ExamQuestionVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull			
	private Integer	questionNo;
	
	@NotNull	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=15)	
	private String	lctExamId;
	
	@NotBlank		
	@Size(max=500)	
	private String	question;
	
	@NotBlank	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String	answerTypeCd;
	
	@NotNull		
	@Digits(integer=3, fraction=0)	
	Integer	questionScore;
	
	@Pattern(regexp="^[A-Za-z0-9]*$")	
	@Size(max=50)	
	private String	imageFileId;
}
