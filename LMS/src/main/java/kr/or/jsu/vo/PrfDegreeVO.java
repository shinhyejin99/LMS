package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.or.jsu.core.validate.groups.InsertGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "degreeRegiId", "professorNo" })
public class PrfDegreeVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 100)
	private String degreeRegiId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorNo;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 50)
	private String degreeName;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 50)
	private String institution;

	@NotBlank(groups = { InsertGroup.class })
	private LocalDateTime acquireAt;

	@NotBlank(groups = { InsertGroup.class })
	@Pattern(regexp = "^[YN]$")
	private String finalYn;
}
