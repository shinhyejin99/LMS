package kr.or.jsu.classroom.dto.response.review;

import java.io.Serializable;

import lombok.Data;

@Data
public class LectureReviewQuestionResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer questionNo;
	private String question;
	private String answerTypeCd;
	private String answerTypeName;
}