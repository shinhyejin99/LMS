package kr.or.jsu.classroom.dto.response.board;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctReplyId")
public class LctPostReplyResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctReplyId;
	private String parentReply;
	private String userId;
	private String replyContent;
	private LocalDateTime createAt;
}