package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	정태일          최초 생성
 *  2025.10. 23.     	정태일          긴급 필드 추가
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "noticeId")
public class PortalNoticeVO implements Serializable {
	private static final long serialVersionUID = 1L;

//	@NotBlank
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String noticeId;

//	@NotBlank
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(min = 7, max = 7, message = "등록자 사번은 7자리여야 합니다.")
	private String staffNo;

//	@NotBlank
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String noticeTypeCd;

//	@NotBlank
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 200)
	private String title;

//	@NotBlank
	@Size(max = 1000)
	private String content;

//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;

//	@NotNull
	private LocalDateTime createAt;

	private LocalDateTime modifyAt;

	@NotNull
//	@Pattern(regexp = "^[YN]$")
	private String deleteYn;
	
//	@NotNull 이거 안되서 지움
	private Integer viewCnt;
	
	private String isUrgent;  
	
//	@NotBlank
	private String stfDeptName; // 우선 부서명으로 하드코딩
}
