package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "enrollId")
public class StuReviewLctInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private String enrollId;
	private LocalDateTime createAt;
	private String resultJson;
}