package kr.or.jsu.classroom.dto.db.studentManage;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.ExamSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctExamInfo;
import lombok.Data;

/**
 * 1. "특정 강의에서 출제된 시험"(삭제되지 않았고, 완료된) 과, <br>
 * 2. "특정 학생의 그 시험에 대한 응시 결과" 를 <br>
 * 동시에 가져올 객체입니다. <br>
 * 시험은 존재하나 학생의 응시 기록이 없을 수 있습니다. (LEFT JOIN) <br>
 * (ex. 아예 응시하지 않았거나, 마감될 때 수강중이 아닌 경우 등) 
 * 
 * @author 송태호
 * @since 2025. 10. 14.
 */
@Data
public class LctExamAndStuSubmitDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LctExamInfo examInfo;
	private ExamSubmitInfo submitInfo;
}
