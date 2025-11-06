package kr.or.jsu.dto.db.subject;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.core.dto.info.CollegeInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "subject")
public class SubjectWithCollegeAndDeptDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private CollegeInfo college;
	private UnivDeptInfo univDept;
	private SubjectInfo subject;
}
