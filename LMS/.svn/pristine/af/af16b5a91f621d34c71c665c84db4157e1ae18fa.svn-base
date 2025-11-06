package kr.or.jsu.classroom.dto.response.exam;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 강의에서 출제된 시험 한 건과 <br>
 * 그 시험을 응시한 학생들의 응시 기록들
 * 
 * @author 송태호
 * @since 2025. 10. 17.
 */
@Data
public class ExamAndEachStudentsSubmitResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lctExamId;
	private String examName;
	private String examDesc;
	private String examType;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer weightValue;
	
	private List<StudentAndSubmitResp> submitList;
	
	@Data
	public static class StudentAndSubmitResp implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String enrollId;
		private ExamSubmitResp submit;
	}
	
	@Data
	public static class ExamSubmitResp implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private LocalDateTime submitAt;
		private Integer autoScore;
		private Integer modifiedScore;
		private String modifyReason;
	}
}