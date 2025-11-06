package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "grouptaskId")
public class LctGrouptaskDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String grouptaskId;
	private String grouptaskName;
	private String grouptaskDesc;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	
	private List<FileDetailResp> attachFileList;
}