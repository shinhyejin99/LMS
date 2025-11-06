package kr.or.jsu.classroom.dto.db.studentManage;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import lombok.Data;

@Data
public class IndivtaskAndStuSubmitDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LctIndivtaskInfo indivtaskInfo;
	private IndivtaskSubmitInfo submitInfo;
}
