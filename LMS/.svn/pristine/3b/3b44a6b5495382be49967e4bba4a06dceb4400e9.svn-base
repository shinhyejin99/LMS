package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDate;

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
@EqualsAndHashCode(of = {"lctRound", "lectureId"})
public class LctAttroundVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Integer lctRound;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lectureId;

	@NotNull
	private LocalDate attDay;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String qrcodeFileId;
}
