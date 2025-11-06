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
@EqualsAndHashCode(of = "reserveId")
public class UserReserveVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String reserveId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String userId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 40)
	private String placeCd;
	
	@NotBlank
	@Size(max = 500)
	private String reserveReason;
	
	@NotNull
	private LocalDateTime createAt;
	
	@NotNull
	private LocalDateTime startAt;
	
	@NotNull
	private LocalDateTime endAt;
	
	@NotNull
	@Digits(integer = 5, fraction = 0)
	private Integer headcount;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String cancelYn;
	
	private String placeName; // 시설이름
	private String userName; // 시설이름
	private String userNo; // 시설이름
	
}
