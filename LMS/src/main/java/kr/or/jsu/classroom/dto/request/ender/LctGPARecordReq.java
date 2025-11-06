package kr.or.jsu.classroom.dto.request.ender;

import java.io.Serializable;

import lombok.Data;

@Data
public class LctGPARecordReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private String gpaCd;
}
