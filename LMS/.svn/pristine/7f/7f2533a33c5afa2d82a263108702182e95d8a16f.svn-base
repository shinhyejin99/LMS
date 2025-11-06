package kr.or.jsu.dto.response.lms.lecture.apply;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class LectureOpenApplyDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LctOpenApplyResp lctOpenApplyResp;
	private SubjectResp subjectResp;
	private ApprovalResp approvalResp;

	private List<LctApplyWeekbyResp> lctApplyWeekbyRespList;
	private List<LctApplyGraderatioResp> applyGraderatioRespList;

	@Data
	public static class LctOpenApplyResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String lctApplyId;
		private String professorNo;
		private String yeartermName; // cache + yeartermCd 로 넣기
		private String lectureIndex;
		private String lectureGoal;
		private String prereqSubject;
		private Integer expectCap;
		private String desireOption;
		private String cancelYn;
		private LocalDateTime applyAt;
	}

	@Data
	public static class SubjectResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String subjectCd;
		private String subjectName;
		private String univDeptName; // cache + univDeptCd
		private String completionName; // cache + completionCd
		private String subjectTypeName; // cache + subjectTypeCd
		private Integer credit;
		private Integer hour;
	}

	@Data
	public static class ApprovalResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String approveId;
		private String prevApproveId;
		private String userId;
		private String approverUserName;
		private String approverUserRole;
		private String applicantUserId;
		private String applicantUserName;
		private String applicantUserRole;
		private String approveStatus; // approveYnnull
		private LocalDateTime approveAt;
		private String comments;
	}

	@Data
	public static class LctApplyWeekbyResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private Integer lectureWeek;
		private String weekGoal;
		private String weekDesc;
	}

	@Data
	public static class LctApplyGraderatioResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String gradeCriteriaCd;
		private Integer ratio;
	}
}