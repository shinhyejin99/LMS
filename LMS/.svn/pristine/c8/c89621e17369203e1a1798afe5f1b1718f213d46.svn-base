package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "subjectCd")
public class SubjectInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subjectCd;
	private String univDeptCd;
	private String subjectName;
	private String completionCd;
	private Integer credit;
	private Integer hour;
	private LocalDateTime createAt;
	private LocalDateTime deleteAt;
	private String subjectTypeCd;
	
	private String univDeptName; // 소속학과 - 학과 테이블
	private String completionName; // 이수구분코드 - 공통코드 테이블
}
