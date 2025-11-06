package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LecturePostReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotBlank @Size(max = 100)
	private String title;
	
	@NotBlank @Size(max = 1000)
	private String content;
	
	@Size(min = 15, max = 15)
	private String fileId;
	
	@NotNull
	private String postType;
	
	@NotNull
	private String tempSaveYn;
	
	private LocalDateTime revealAt;
}