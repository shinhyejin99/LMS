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
@EqualsAndHashCode(of = "applyId")
public class UnivAffilApplyVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String applyId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String studentNo;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String targetDeptCd;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String affilChangeCd;
	
	@NotNull
	private LocalDateTime applyAt;
	
	@NotBlank
	@Size(max = 1000)
	private String applyReason;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String applyStatusCd;
	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String approvalLine;
}
