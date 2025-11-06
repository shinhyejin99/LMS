package kr.or.jsu.dto;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.CommonCodeVO;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.FilesVO;
import kr.or.jsu.vo.StaffDeptVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "userInfo")
public class UserStaffDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private UsersVO userInfo;
	private StaffVO staffInfo;
	private StaffDeptVO staffDeptInfo;
	private AddressVO addressInfo;
	private FilesVO filesInfo;
	private FileDetailVO fileDetailInfo;
	private CommonCodeVO commonCodeInfo;

	private String gender; // 성별
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String HireDateString;

	private MultipartFile photoFile;




}
