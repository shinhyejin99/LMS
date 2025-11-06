package kr.or.jsu.classroom.dto.response.classroom;

import lombok.Data;

@Data
public class ProfessorMyInfoResp {
	// USERS 테이블
	private String userId;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String email;

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
	private String univDeptName; // (소속학과명)
	private String prfStatusName; // (재직상태명)
	private String prfAppntName; // (임용구분명)
	private String prfPositName; // (직책명)
}
