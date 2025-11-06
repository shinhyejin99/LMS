package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@EqualsAndHashCode(of = "approveId")
public class ApprovalVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min = 15, max = 15)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	private String approveId;

	@Size(min = 15, max = 15)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	private String prevApproveId;
	
	private String applyTypeCd;

	@Size(min = 15, max = 15)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@NotBlank
	private String userId;
	
	@Size(min = 15, max = 15)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@NotBlank
	private String applicantUserId;

	@Size(max = 1)
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	private String approveYnnull;

	private LocalDateTime approveAt;

	@Size(max = 1000)
	private String comments;

	@Size(max = 50)
	private String attachFileId;
}
