package kr.or.jsu.dto.response.lms.lecture.apply;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SubjectWithCollegeAndDeptResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String collegeCd;
	private String collegeName;
	private List<SubjectWithDeptResp> deptList;
	
	@Data
	public static class SubjectWithDeptResp implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String univDeptCd;
		private String univDeptName;
		private List<SubjectResp> subjectList;
	}
	
	@Data
	public static class SubjectResp implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String subjectCd;
		private String subjectName;
		private String completionCd;
		private String subjectTypeCd;
		private Integer credit;
		private Integer hour;
		private LocalDateTime createAt;
		private LocalDateTime deleteAt;
		
		private String completionName; // 이수구분코드 - 공통코드 테이블
		private String subjectTypeName;
	}
	
}
