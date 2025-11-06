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
@EqualsAndHashCode(of = "apiRecruitId")
public class PortalRecruitApiVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String apiRecruitId;

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 200)
	private String agencyName;

	@Size(max = 200)
	private String agencyType;

	@Size(max = 200)
	private String recType;

	@NotNull
	private LocalDateTime recStartDay;

	@NotNull
	private LocalDateTime recEndDay;

	@Size(max = 1000)
	private String agencyUrl;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String document;

	@NotBlank
	@Size(max = 200)
	private String applyWay;

	@NotNull
	private LocalDateTime announceDay;

	@Size(max = 1000)
	private String inquiry;

	@Size(max = 1000)
	private String others;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String logoImg;
}
