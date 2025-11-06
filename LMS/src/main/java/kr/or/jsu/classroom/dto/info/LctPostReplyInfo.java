package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctReplyId")
public class LctPostReplyInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctReplyId;
	private String lctPostId;
	private String parentReply;
	private String userId;
	private String replyContent;
	private LocalDateTime createAt;
	private String deleteYn;
}
