package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctPostId")
public class LctPostInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctPostId;
	private String lectureId;
	private String title;
	private String content;
	private LocalDateTime createAt;
	private String attachFileId;
	private String deleteYn;
	private String postType;
	private String tempSaveYn;
	private LocalDateTime revealAt;
}