package kr.or.jsu.core.dto.response;

import java.io.Serializable;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"fileOrder", "fileId"})
public class FileDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer fileOrder;
	private String fileId;
	private String originName;
	private String extension;
	private Long fileSize;
	
	public String getOriginalFileName() {
		if(!StringUtils.isBlank(extension)) return originName + "." + extension;
		return originName;
	}
}