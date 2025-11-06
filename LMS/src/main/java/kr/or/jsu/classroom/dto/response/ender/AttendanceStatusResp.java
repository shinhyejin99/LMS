package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttendanceStatusResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer lctRound;
	private LocalDateTime attDay;
}