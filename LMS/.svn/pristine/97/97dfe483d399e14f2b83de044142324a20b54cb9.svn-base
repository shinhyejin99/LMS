package kr.or.jsu.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 27.     	최건우	          학생이름 , 학과이름, 연락처, 학년 추가
 *  2025.10. 18.        최건우              성적 목록 추가
 *
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"studentNo"})
@ToString(callSuper = true)
public class StudentVO extends UsersVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 9)
	private String studentNo;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String userId;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String univDeptCd;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String gradeCd;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String stuStatusCd;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String engLname;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String engFname;
	
	@Size(max = 50)
	private String guardName;
	
	@Size(max = 50)
	private String guardRelation;
	
	@Size(max = 50)
	private String guardPhone;
	
	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 8)
	private String professorId;
	
	
	private String stdName;     // 학생                                             
    private String deptName;    // 학과 이름                                              
		private String stdGrade;    // 학년
		
		private String gradeName; // 현재 학년
		private String stuStatusName; // 재학 상태명
		
		private int graduationRate; // 졸업요건 충족률	
		
		private String stdTel; // 전화번호
		private String stdEmail; // 이메일
		private String stdBaseAddr; // 기본 주소
		private String stdDetailAddr; // 상세 주소
		private String stdZipCode; // 우편번호

	private java.util.List<LectureGradeVO> lectureGrades; // 학생의 강의 성적 목록
}