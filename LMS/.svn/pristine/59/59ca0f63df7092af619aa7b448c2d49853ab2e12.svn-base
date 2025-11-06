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
@EqualsAndHashCode(of = "studentNo")
public class StudentInfo implements Serializable {
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

	// STUDENT 테이블
	private String studentNo;
	private String univDeptCd;
	private String gradeCd;
	private String stuStatusCd;
	private String engLname;
	private String engFname;
	private String guardName;
	private String guardRelation;
	private String guardPhone;
	private String professorId;
	
	// 캐시로 가져올 수 있는 정보들
	private String bankName; // (은행명)
	private String univDeptName; // (소속학과명)
	private String gradeName; // (학년)
	private String stuStatusName; // (재학상태)
	private String professorName; // (지도교수명)
}