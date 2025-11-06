package kr.or.jsu.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "stfDeptCd")
public class StaffDeptInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String stfDeptCd;
	private String stfDeptName;
	private String deptTeleNo;
	private LocalDateTime createAt;
	private LocalDateTime deleteAt;
}
