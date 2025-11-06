package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "studentNo")
public class StuMilitaryVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(max = 9)
	private String studentNo;

	@NotBlank
	@Size(max = 50)
	private String militaryTypeCd;

	@NotNull
	private LocalDateTime joinAt;

	private LocalDateTime exitAt;
}
