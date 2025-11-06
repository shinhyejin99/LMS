package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "addrId")
public class AddressVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min = 15, max = 15)
	private String addrId;

	@NotBlank
	@Size(max = 500)
	private String baseAddr;

	@Size(max = 500)
	private String detailAddr;

	@Size(min = 5, max = 5)
	@Pattern(regexp = "^[0-9]*$")
	private String zipCode;

	private Double latitude;

	private Double longitude;

	@Size(min = 1, max = 1)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	private String usingYn;
}
