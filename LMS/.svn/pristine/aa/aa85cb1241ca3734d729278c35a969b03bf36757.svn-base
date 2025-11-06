package kr.or.jsu.classroom.dto.response.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AttendanceStatisticsResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private TypeCount typeCount;
	private List<StuAttendanceCount> attCountList;
	
	@Data
	public static class TypeCount implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private int totalCnt;
		private int tbdCnt;
		private int okCnt;
		private int noCnt;
		private int earlyCnt;
		private int lateCnt;
		private int excpCnt;
	}
	
	@Data
    public static class StuAttendanceCount implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	private String enrollId;
    	private int recordedCnt;
    	private int nonAbsentCnt;
    }
	
}