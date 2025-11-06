package kr.or.jsu.core.dto.db;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.info.FilesInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "fileId")
public class FilesBundleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String fileId;
	private FilesInfo filesInfo;
	private List<FileDetailInfo> fileDetailInfo;
}