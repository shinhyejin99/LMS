package kr.or.jsu.classroom.dto.response.attandance;

import java.io.Serializable;

import lombok.Data;

@Data
public class StudentAttLabelResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private int okCnt;
	private int noCnt;
	private int earlyCnt;
	private int lateCnt;
	private int excpCnt;
	private int tbdCnt;
}
