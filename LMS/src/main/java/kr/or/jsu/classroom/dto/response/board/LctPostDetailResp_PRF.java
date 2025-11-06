package kr.or.jsu.classroom.dto.response.board;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctPostId")
public class LctPostDetailResp_PRF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctPostId;
	private String lectureId;
	private String title;
	private String content;
	private LocalDateTime createAt;
	private String attachFileId;
	private String postType;
	private String tempSaveYn;
	private LocalDateTime revealAt;
	
	private List<FileDetailResp> attachFileList;
}