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
@EqualsAndHashCode(of = "grouptaskId")
public class LctGrouptaskVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String grouptaskId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lectureId;

	@NotBlank
	@Size(max = 50)
	private String grouptaskName;

	@NotBlank
	@Size(max = 1000)
	private String grouptaskDesc;

	@NotNull
	private LocalDateTime createAt;

	@NotNull
	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String deleteYn;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;
}
