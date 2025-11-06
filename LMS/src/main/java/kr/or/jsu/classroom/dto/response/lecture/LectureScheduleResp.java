package kr.or.jsu.classroom.dto.response.lecture;

import java.io.Serializable;

import lombok.Data;

@Data
public class LectureScheduleResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String subjectName;
	private String professorNo;
	private String professorName;
	private String placeName;
	private String timeblockCd;
}