package kr.or.jsu.dto.response.lms.lecture.schedule;

import java.io.Serializable;

import lombok.Data;

@Data
public class UnAssignedLectureResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String subjectName; // cache로 넣기
	private String professorNo;
	private String professorName; // cache로 넣기
	private String yeartermCd;
	private int maxCap;
	
	private String univDeptName; // cache로 넣기
	private String completionName; // cache로 넣기
	private String subjectTypeName; // cache로 넣기
	private int credit;
	private int hour;
	
	private int scheduledSlots;
	private String scheduleJson;	
}