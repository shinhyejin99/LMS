package kr.or.jsu.classroom.dto.response.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TaskStatisticsResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private TaskTypeCount taskTypeCount;
	private OngoingIndivtask ongoingIndivtask;
	private OngoingGrouptask ongoingGrouptask;
	private ClosedIndivtask closedIndivtask;
	private ClosedGrouptask closedGrouptask;
	private List<ScoresByStudentAndTask> scoresByStudentAndTask;
	
	@Data
	public static class TaskTypeCount implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private Integer indivCnt;
	    private Integer groupCnt;
	}

	@Data
	public static class OngoingIndivtask implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private Integer ongoingTaskCnt;
	    private Integer targetCnt;
	    private Integer submittedCnt;
	    private java.math.BigDecimal submitRatePct; // 100.00 같은 퍼센트 문자면 BigDecimal 추천
	}

	@Data
	public static class OngoingGrouptask implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private Integer ongoingTaskCnt;
	    private Integer targetCnt;
	    private Integer submittedCnt;
	    private java.math.BigDecimal submitRatePct;
	}

	@Data
	public static class ClosedIndivtask implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private Integer closedTaskCnt;
	    private Integer targetCnt;
	    private Integer submittedCnt;
	    private java.math.BigDecimal submitRatePct;
	}

	@Data
	public static class ClosedGrouptask implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private Integer closedTaskCnt;
	    private Integer targetCnt;
	    private Integer submittedCnt;
	    private java.math.BigDecimal submitRatePct;
	}

	@Data
	public static class ScoresByStudentAndTask implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private String  enrollId;
	    private String  taskType; // "INDIV" / "GROUP"
	    private String  taskId;
	    private Integer score;    // NULL 허용이면 Integer(참조형) 권장
	}

}
