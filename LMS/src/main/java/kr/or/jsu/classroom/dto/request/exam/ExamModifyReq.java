package kr.or.jsu.classroom.dto.request.exam;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExamModifyReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String examName;
	private String examDesc;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer weightValue;
}