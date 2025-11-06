package kr.or.jsu.core.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 송태호
 * @since 2025.10.01.
 */
@Data
@EqualsAndHashCode(of = "professorNo")
public class ProfessorInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// USERS 테이블
	private String userId;
	private String pwHash;
	private String firstName;
	private String lastName;
	private String regiNo;
	private String photoId;
	private String addrId;
	private String mobileNo;
	private String email;
	private String bankCode;
	private String bankAccount;
	private LocalDateTime createAt;

	// PROFESSOR 테이블
	private String professorNo;
	private String univDeptCd;
	private String engLname;
	private String engFname;
	private String prfStatusCd;
	private String prfAppntCd;
	private String prfPositCd;
	private String officeNo;
	
	// 캐시로 가져올 수 있는 정보들
	private String bankName; // (은행명)
	private String univDeptName; // (소속학과명)
	private String prfStatusName; // (재직상태명)
	private String prfAppntName; // (교수분류명) ex. 전임 정교수, 전임 부교수, 시간강사...
	private String prfPositName; // (직책명) ex. 학과장, 부학과장, 평교수
}