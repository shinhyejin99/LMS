package kr.or.jsu.classroom.dto.response.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class EnrollStuStatisticsResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private EnrollmentStats stats;
    private List<GradeCount> byGrade;
    private List<DeptCount> byDept;
    
    @Data
    public static class EnrollmentStats implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	private int totalCnt;
    	private int dropoutCnt;
    	private int activeCnt;
    }

    @Data
    public static class GradeCount implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
        private String gradeCd; // 1ST, 2ND, 3RD, 4TH
        private int cnt;
    }

    @Data
    public static class DeptCount implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
        private String univDeptCd;
        private int cnt;
    }
}



