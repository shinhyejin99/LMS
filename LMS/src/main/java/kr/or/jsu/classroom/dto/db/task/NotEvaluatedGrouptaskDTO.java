package kr.or.jsu.classroom.dto.db.task;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import lombok.Data;

@Data
public class NotEvaluatedGrouptaskDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LectureInfo lecture;
	private LctGrouptaskInfo grouptask;
	private int submitCount;
}
