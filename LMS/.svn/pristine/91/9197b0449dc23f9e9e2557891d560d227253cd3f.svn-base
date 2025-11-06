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
@EqualsAndHashCode(of = "graduReqSubmitId")
public class StuGraduReqVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String graduReqSubmitId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String studentNo;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorNo;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String graduReqCd;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String fileId;

	@NotNull
	private LocalDateTime submitAt;

	private LocalDateTime evaluateAt;

	@Size(max = 1000)
	private String evaluation;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String deleteYn;
	private String stdName; // 학생 이름
	private Integer totalRequiredCredits; // 총 필요 학점
	private Integer completedCredits;     // 이수 학점
	private Integer majorCredits;         // 전공 학점
	private Integer liberalArtsCredits;   // 교양 학점
	private Integer graduationRate;       // 졸업 요건 충족률
	private String deptName;              // 학과명
	private String gradeName;             // 학년명
	private String stdTel;                // 학생 연락처
	private int fileOrder;                // 파일 순번
}
