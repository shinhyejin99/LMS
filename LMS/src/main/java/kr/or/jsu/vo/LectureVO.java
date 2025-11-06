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
@EqualsAndHashCode(of = "lectureId")
public class LectureVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lectureId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String subjectCd;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorNo;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String yeartermCd;

	@NotNull
	@Digits(integer = 5, fraction = 0)
	private Integer maxCap;

	@NotBlank
	@Size(max = 1000)
	private String lectureIndex;

	@NotBlank
	@Size(max = 1000)
	private String lectureGoal;

	@Size(max = 200)
	private String prereqSubject;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String cancelYn;

	private LocalDateTime endAt;
	
	private String scoreFinalizeYn;
	
	private String subjectName;
	private Integer currentCap;
}
