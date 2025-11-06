package kr.or.jsu.classroom.dto.response.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ExamStatisticsResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExamTypeCount examTypeCount;
	private OngoingExam ongoingExam;
	private ClosedExam closedExam;
	private List<ScoresByStudentAndExam> scoresByStudentAndExam;

	@Data
	public static class ExamTypeCount implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer totalCnt;
		private Integer onlineCnt;
		private Integer offlineCnt;
	}

	@Data
	public static class OngoingExam implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer ongoingExamCnt;
		private Integer targetCnt;
		private Integer submittedCnt;
		private BigDecimal submitRatePct;
	}

	@Data
	public static class ClosedExam implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer closedExamCnt;
		private Integer targetCnt;
		private Integer submittedCnt;
		private BigDecimal submitRatePct;
	}

	@Data
	public static class ScoresByStudentAndExam implements Serializable {
		private static final long serialVersionUID = 1L;
		private String enrollId;
		private String examId;
		private Integer score;
	}
}
