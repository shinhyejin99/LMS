package kr.or.jsu.classroom.dto.response.attandance;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 출석 부를 때 사용하는 응답 객체
 *   
 * @author 송태호
 * @since 2025. 10. 14.
 * @see
 */
@Data
public class StudentForAttendanceResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId; // 수강 테이블
	private String studentNo; // 학생 테이블
	private String lastName; // 사용자 테이블
	private String firstName; // 사용자 테이블
	private String grade; // 학생 테이블
	private String univDeptName; // 학생 테이블(코드) + 캐시
	
	private LocalDateTime attAt;
	private String attStatusCd;
	private String attComment;
}
