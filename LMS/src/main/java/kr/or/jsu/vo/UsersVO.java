package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.or.jsu.core.validate.groups.InsertGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"userId"})
public class UsersVO implements Serializable {
	private static final long serialVersionUID = 1L;


	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String userId;

	private String userNo;

	private String userType;
	
	// 송태호가 야매로 넣음
	private boolean isHeadProf;

	@Size(max = 500)
	private String pwHash;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 50)
	private String firstName;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 50)
	private String lastName;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 500)
	private String regiNo;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String photoId;


	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String addrId;

	@NotBlank(groups = { InsertGroup.class })
	@Pattern(regexp = "^[0-9-]*$")
	@Size(max = 15)
	private String mobileNo;

	@Size(max = 50)
	private String email;

	@NotBlank(groups = { InsertGroup.class })
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String bankCode;

	@NotBlank(groups = { InsertGroup.class })
	@Size(max = 50)
	private String bankAccount;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createAt;
}
