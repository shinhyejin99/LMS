package kr.or.jsu.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  *  //////////////////// DTO 정리해야함 /////////////////////////////////
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "staffNo")
public class CertificateStaffDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	// User Fields
	private String userId;
	private String firstName;
	private String lastName;
	private String regiNo;
	private String mobileNo;
	private String email;

	// Staff Fields
	private String staffNo;
	private String stfDeptCd; // 소속 부서 코드
	private String stfDeptName; // 소속 부서명
	private String teleNo; // 학내 전화번호

	// Address Fields
	private String baseAddr;
	private String detailAddr;
	private String zipCode;

	// Other potentially useful fields
	private LocalDate createAt; // 입사일 (사용자 생성일과 동일할 수 있음)
}
