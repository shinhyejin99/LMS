package kr.or.jsu.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  //////////////////// DTO 정리해야함 /////////////////////////////////
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
@EqualsAndHashCode(of = "professorNo")
public class CertificateProfessorDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	// User Fields
	private String userId;
	private String firstName;
	private String lastName;
	private String regiNo;
	private String mobileNo;
	private String email;

	// Professor Fields
	private String professorNo;
	private String univDeptCd; // 소속 학과 코드
	private String univDeptName; // 소속 학과명
	private String prfStatusCd; // 재직 상태 코드
	private String prfStatusName; // 재직 상태명
	private String prfAppntCd; // 임용 코드
	private String prfAppntName; // 임용명
	private String prfPositCd; // 직책 코드
	private String prfPositName; // 직책명
	private String officeNo; // 사무실 전화번호

	// Address Fields
	private String baseAddr;
	private String detailAddr;
	private String zipCode;

	// Other potentially useful fields
	private String collegeName;
	private String careerName; // PRF_CAREER 테이블에서 가져올 경력명 
	private LocalDate createAt; // 임용일 (사용자 생성일과 동일할 수 있음)
}
