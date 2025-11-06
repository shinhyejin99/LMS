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
@EqualsAndHashCode(of = "placeCd")
public class PlaceVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 40)
	private String placeCd;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 40)
	private String parentCd;

	@NotBlank
	@Size(max = 200)
	private String placeName;

	@NotNull
	private LocalDateTime createAt;

	private LocalDateTime modifyAt;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String usingYn;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String addrId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String placeTypeCd;

	@Digits(integer = 10, fraction = 0)
	private Integer capacity;
	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String placeUsageCd;
}