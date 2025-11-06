package kr.or.jsu.classroom.dto.request.finalize;

import java.io.Serializable;

import lombok.Data;

@Data
public class ChangeScoreReq implements Serializable {
	private static final long serialVersionUID = 1L;

	private String enrollId;
	private String gpaCd;
	private String changeReason;
}
