package kr.or.jsu.classroom.dto.response.student;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 학생이 같은 강의를 수강하는 다른 학생들의 정보를 조회할 때 <br>
 * 노출 가능한 정보만 모아놓은 응답용 객체입니다. 
 * 
 * @author 송태호
 * @since 2025. 10. 15.
 */
@Data
@EqualsAndHashCode(of = "userId")
public class ClassmateResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String userId;
	private String firstName;
	private String lastName;
	
	private String studentNo;
	private String univDeptCd;
	private String univDeptName;
	private String gradeCd;
	private String gradeName;
	
	private String enrollId;
	private String enrollStatusCd;
}