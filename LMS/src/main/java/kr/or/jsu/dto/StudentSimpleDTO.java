package kr.or.jsu.dto;

import java.io.Serializable;

import kr.or.jsu.vo.CollegeVO;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.UnivDeptVO;
import kr.or.jsu.vo.UsersVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "userInfo")
public class StudentSimpleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private UsersVO userInfo;
	private StudentVO studentInfo;
	private UnivDeptVO majorDeptInfo; // 학과
	private CollegeVO majorCollegeInfo; // 대학교
	
	
}
