package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceRecordReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
    private String enrollId;
	
	@NotNull
    private String attStatusCd;
	
    private String attComment;
}
