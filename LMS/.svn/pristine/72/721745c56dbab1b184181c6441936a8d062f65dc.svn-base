package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author 정태일
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 26.     	김수현	        필드 삭제 및 추가
 *	2025. 9. 27.		김수현			제약조건 수정
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "recruitId")
public class PortalRecruitVO implements Serializable {
	private static final long serialVersionUID = 1L;

//	@NotBlank
//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String recruitId;

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	@Size(max = 4000)
	private String content;

	@NotBlank
	private String stfDeptName; // 우선 부서명으로 하드코딩


//	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;

//	@NotNull 이거 안되서 지움
	private Integer viewCnt;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate recStartDay;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate recEndDay;

	@NotBlank
	@Size(max = 200)
	private String agencyName;

	@Size(max = 200)
	private String recTarget;

//	@NotNull 이거 안되서 지운거
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createAt;

	private LocalDateTime deleteAt;
}
