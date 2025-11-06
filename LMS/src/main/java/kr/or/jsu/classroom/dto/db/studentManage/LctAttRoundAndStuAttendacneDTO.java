package kr.or.jsu.classroom.dto.db.studentManage;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.LctAttRoundInfo;
import kr.or.jsu.classroom.dto.info.LctStuAttInfo;
import lombok.Data;

/**
 * 1. "특정 강의에 대해 생성된 출석회차" 와, <br>
 * 2. "특정 학생의 그 출석회차에 대한 출석 상황" 을 <br>
 * 동시에 가져올 객체입니다. <br>
 * 출석회차는 존재하나 출석에 대한 기록이 없을 수 있습니다. (LEFT JOIN) <br>
 * (ex. 수강 중간부터 들어올 경우)
 * 
 * @author 송태호
 * @since 2025. 10. 14.
 */
@Data
public class LctAttRoundAndStuAttendacneDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int lctPrintRound;
	private LctAttRoundInfo attRoundInfo;
	private LctStuAttInfo stuAttInfo;
}
