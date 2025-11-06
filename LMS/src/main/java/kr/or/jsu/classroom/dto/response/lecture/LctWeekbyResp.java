package kr.or.jsu.classroom.dto.response.lecture;

import java.io.Serializable;

import lombok.Data;

@Data
public class LctWeekbyResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int lectureWeek;
	private String weekGoal;
	private String weekDesc;
}
