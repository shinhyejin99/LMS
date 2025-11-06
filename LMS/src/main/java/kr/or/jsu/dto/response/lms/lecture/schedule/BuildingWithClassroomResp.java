package kr.or.jsu.dto.response.lms.lecture.schedule;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BuildingWithClassroomResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private String placeCd;
	private String placeName;
	private List<ClassroomResp> classrooms;

	@Data
	public static class ClassroomResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String placeCd;
		private String placeName;
		private Integer capacity;
		private String placeUsageCd;
		private int usedBlocks;
		private double usagePercent; // 강의실 사용률
	}
}
