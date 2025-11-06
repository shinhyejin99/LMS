package kr.or.jsu.classroom.dto.response.lecture;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lectureId")
public class LectureLabelResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String subjectName;
	private String professorNo;
	private String professorName;
	private String univDeptName;
	private String completionName;
	private String yeartermCd;
	private String subjectTypeCd;
	private String subjectTypeName;
	private int credit;
	private int hour;
	private int currentCap;
	private int maxCap;
	private boolean isStarted;
	private boolean isEnded;
	private boolean isFinalized;
	private LocalDateTime endAt;
	private String scheduleJson;
}