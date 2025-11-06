package kr.or.jsu.mybatis.mapper.files;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.info.FilesInfo;

@Mapper
public interface LMSFilesMapper {
	/**
	 * 개별 파일 데이터들을 저장하기에 앞서 파일묶음을 만들어줍니다. <br>
	 * FilesInfo를 생성하고, 두 파라미터를 채워주세요. 셀렉트키로 생성한 fileId가 VO에 주입됩니다.
	 * @param userId 파일을 업로드 요청한 사용자의 ID (USER0...x)
	 * @param publicYn 저장할 파일이 공개되어도 상관없으면 true, 비공개 파일이면 false
	 * @return 생성된 레코드 수 + FilesInfo에 들어있는 셀렉트키로 생성한 fileId
	 * @author 송태호
	 */
	public int insertFiles(FilesInfo files); // FILES 1건
	
	
	/**
	 * 파일들을 넣을 파일묶음 레코드를 생성한 후, 개별 파일 데이터들을 저장합니다. <br>
	 * 
	 * @param fileId insertFiles(files) 로 파일묶음을 생성한 후, filesVO.getFileId()로 가져온 기본키
	 * @param details 각 파일들의 메타데이터가 들어있는 List
	 * @return
	 * @author 송태호
	 */
	public int insertFileDetails(String fileId, List<FileDetailInfo> details);
	
	/**
	 * 파일ID와 파일순번을 지정하여 특정한 파일 하나의 메타데이터를 가져옵니다.
	 * 
	 * @param fileId 파일묶음ID
	 * @param fileOrder 파일의 순번
	 * @return 특정 파일의 메타데이터
	 * @author 송태호
	 */
	public FileDetailInfo selectFileDetail(String fileId, int fileOrder);
	
	/**
	 * 파일ID를 지정하여 그 파일묶음의 정보와, 각 파일들의 메타데이터를 가져옵니다. 
	 * 
	 * @param fileId 파일묶음ID
	 * @return ID가 일치하는 FilesVO와 FileDetailVO의 List
	 * @author 송태호
	 */
	public FilesBundleDTO selectFileBundle(String fileId);
	
	
	/**
	 * 파일의 사용여부를 변경합니다.
	 * 
	 * @param fileId 파일ID
	 * @param usingYn true : 사용중 / false : 사용안함
	 * @return
	 */
	public int updateFileStatus(
		String fileId
		, String usingYn
	);
}
