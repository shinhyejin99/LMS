package kr.or.jsu.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.or.jsu.core.validate.groups.InsertGroup;
import kr.or.jsu.vo.ProfessorVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ProfessorInfoDTO extends ProfessorVO {
	private static final long serialVersionUID = 1L;
	// 소속정보
	private String collegeName;
	private String departmentName;
	private String employmentStatus;

	// 재직정보
	private String positionName;
	private String employmentType;
	private String employmentDate;

	// 연구정보 등 기타
	private String researchRoom;
	private String finalDegree;
	private String researchArea;
	private String researchSummary;
	private String currentCourses;
	private String departmentHeadPosition;




	@NotBlank(groups = { InsertGroup.class })
	private String baseAddr;
	@NotBlank(groups = { InsertGroup.class })
	private String detailAddr;
	@NotBlank(groups = { InsertGroup.class })
	private String zipCode;

	private String professorName;
    private String univDeptName;

//	private String userId;
//	private String userNo;
//	private String userType;
//	private String pwHash;

//	@NotBlank(groups = { InsertGroup.class })
//	private String firstName;
//	@NotBlank(groups = { InsertGroup.class })
//	private String lastName;
//	@NotBlank(groups = { InsertGroup.class })
//	private String regiNo;
	private String photoId;
	private MultipartFile photoFile;
//
//	@NotBlank(groups = { InsertGroup.class })
//	private String mobileNo;

//	@NotBlank(groups = { InsertGroup.class })
//	private String email;

//	@NotBlank(groups = { InsertGroup.class })
//	private String bankCode; // 은행 코드
	private String bankName; // 은행명

//	@NotBlank(groups = { InsertGroup.class })
//	private String bankAccount;

//	private ProfessorVO professorInfo; // 교수 정보
	private String gender;
	private String officeNo;
	private String collegeCd;

	@NotBlank(groups = { InsertGroup.class })
	@Pattern(regexp = "^[A-Za-z0-9-]*$")
	private String deptCd;
//	@NotBlank(groups = { InsertGroup.class })
//	@Pattern(regexp = "^[A-Za-z0-9-]*$")
//	private String profAppntCd;

	@NotBlank(groups = { InsertGroup.class })
	private String hireDateString; // 임용일 년월일 문자

}