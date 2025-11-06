package kr.or.jsu.classroom.dto.response.lecture;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProfessorInfoResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
		// USERS 테이블
		private String userId;
		private String firstName;
		private String lastName;
		private String mobileNo;
		private String email;

		// PROFESSOR 테이블
		private String professorNo;
		private String engLname;
		private String engFname;
		private String officeNo;
		
		// 캐시로 가져올 수 있는 정보들
		private String univDeptName; // (소속학과명)
		private String prfStatusName; // (재직상태명)
		private String prfAppntName; // (교수분류명) ex. 전임 정교수, 전임 부교수, 시간강사...
		private String prfPositName; // (직책명) ex. 학과장, 부학과장, 평교수
}
