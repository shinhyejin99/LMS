package kr.or.jsu.core.dto.info;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "commonCd")
public class CommonCodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String	commonCd;
	private String	commonSortCd;
	private String	cdName;
	private String	cdDesc;
	private String	usingYn;
}