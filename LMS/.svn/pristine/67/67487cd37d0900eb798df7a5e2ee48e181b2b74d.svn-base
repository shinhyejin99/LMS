package kr.or.jsu.classroom.dto.response.classroom;

import lombok.Data;

@Data
public class StudentMyEnrollInfoResp {
	// USERS 테이블
	private String userId;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String email;

	// STUDENT 테이블
	private String studentNo;
	private String univDeptCd;
		private String univDeptName; // (소속학과명)
	private String gradeCd;
		private String gradeName; // (학년)
	private String stuStatusCd;
		private String stuStatusName; // (재학상태)
	
	// STU_ENROLL_LCT 테이블
	private String enrollId;
	private String enrollStatusCd;
		private String enrollStatusName; // (수강상태)
	private String retakeYn;
	
	// 캐시로 가져올 수 있는 정보들은 들여쓰기되어있음.	
}
