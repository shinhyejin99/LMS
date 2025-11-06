package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"enrollId", "lctRound"})
public class LctStuAttInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private Integer lctRound;
	private String enrollId;
	private LocalDateTime attAt;
	private String attStatusCd;
	private String attComment;
}
