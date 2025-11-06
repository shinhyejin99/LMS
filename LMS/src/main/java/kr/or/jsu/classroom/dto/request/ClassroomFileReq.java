package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomFileReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String userNo;
	private String fileId;
	private int fileOrder;

}
