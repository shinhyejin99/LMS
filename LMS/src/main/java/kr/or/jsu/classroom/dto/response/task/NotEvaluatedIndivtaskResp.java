package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import lombok.Data;

@Data
public class NotEvaluatedIndivtaskResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LectureLabelResp lecture;
	private LctIndivtaskDetailResp indivtask;
	private int submitCount;
}
