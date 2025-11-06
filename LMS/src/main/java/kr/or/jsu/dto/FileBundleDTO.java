package kr.or.jsu.dto;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.FilesVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "fileId")
public class FileBundleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String fileId;
	private FilesVO filesInfo;
	private List<FileDetailVO> fileDetailsInfo;
}