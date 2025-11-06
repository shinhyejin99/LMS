package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lectureId")
public class LectureInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lectureId;
	private String subjectCd;
	private String professorNo;
	private String yeartermCd;
	private Integer maxCap;
	private String lectureIndex;
	private String lectureGoal;
	private String prereqSubject;
	private String cancelYn;
	private LocalDateTime endAt;
	private String scoreFinalizeYn;
		
	private String subjectName;
	private String professorName;
}
