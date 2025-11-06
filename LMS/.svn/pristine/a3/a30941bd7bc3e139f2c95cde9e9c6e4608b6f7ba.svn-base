package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"enrollId", "lctExamId"})
public class ExamSubmitInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String enrollId;
	private String lctExamId;
	private LocalDateTime submitAt;
	private Integer autoScore;
	private Integer modifiedScore;
	private String modifyReason;
}
