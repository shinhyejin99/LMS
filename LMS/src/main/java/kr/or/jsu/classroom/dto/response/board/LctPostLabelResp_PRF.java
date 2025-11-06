package kr.or.jsu.classroom.dto.response.board;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctPostId")
public class LctPostLabelResp_PRF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctPostId;
	private String title;
	private LocalDateTime createAt;
	private String postType;
	private String tempSaveYn;
	private LocalDateTime revealAt;
}