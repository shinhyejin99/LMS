package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class LctPostReplyReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String parentReplyId;
	private String replyContent;
}
