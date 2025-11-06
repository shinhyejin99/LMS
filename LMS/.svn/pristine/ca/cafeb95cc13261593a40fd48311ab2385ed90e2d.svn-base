package kr.or.jsu.classroom.dto.db.exam;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.ExamSubmitInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 특정 강의 수강생의 특정 시험에 대한 응시기록을 조회하기 위한 객체. <br>
 * 요청한 강의 수강생의 시험에 대한 응시기록이 없을 경우, submit은 null이 됩니다.
 * 
 * @author 송태호
 * @since 2025. 10. 17.
 */
@Data
@EqualsAndHashCode(of = "enrollId")
public class StudentAndExamSubmitDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	
	private ExamSubmitInfo submit; // nullable
}