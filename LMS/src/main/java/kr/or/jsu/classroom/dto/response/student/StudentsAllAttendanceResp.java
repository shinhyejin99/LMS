package kr.or.jsu.classroom.dto.response.student;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentsAllAttendanceResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private int lctPrintRound; // 출석 회차
	private Integer lctRound; // db에 기록된 출석회차(중간중간 비어있음)
	private LocalDate attDay; // 출석회차가 생성된 시점
	private String qrcodeFileId; // 이게 있으면 QR코드 출석이었던 거임
	
	private boolean record;
	
	private LocalDateTime attAt; // 학생이 출석기록이 생성된 시점(QR코드면 QR코드 찍은시간, 아니면 교수가 출석체크한 시간)
	private String attStatusCd; // 출석 상태 공통코드
	private String attStatusName; // 출석 상태. 캐시로 넣어야 함.
	private String attComment; // 교수가 출석에 남긴 비고
	
}
