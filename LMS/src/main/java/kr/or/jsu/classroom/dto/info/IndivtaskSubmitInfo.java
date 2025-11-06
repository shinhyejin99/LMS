package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IndivtaskSubmitInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private String indivtaskId;
	private String submitDesc;
	private String submitFileId;
	private LocalDateTime submitAt;
	private Integer evaluScore;
	private LocalDateTime evaluAt;
	private String evaluDesc;
}