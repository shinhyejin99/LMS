package kr.or.jsu.core.common.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 2.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 2.     	송태호	          최초 생성
 *
 * </pre>
 */
public interface LMSFilesService {
	
	// ====================
	// 파일 쓰기 관련
	// ====================
	
	/**
	 * MultipartFile List와 저장할 경로를 받아 디스크의 경로에 파일을 저장하고<br>
	 * 저장한 파일들의 메타데이터를 반환하는 메서드입니다.
	 * @author 송태호
	 * @param files 클라이언트가 업로드 요청한 MultipartFile의 List
	 * @param subDirectory 그 파일들을 실제로 저장할 하위 경로.
	 * @return 저장을 완료한 파일들의 메타데이터. 이 데이터를 DB에 저장합니다.
	 */
	public List<FileDetailInfo> saveAtDirectory(
		List<MultipartFile> files,
		FileUploadDirectory subDirectory
	);
	
	/**
	 * MultipartFile List와 저장할 경로를 받아 디스크의 경로에 파일을 저장하고<br>
	 * 저장한 파일들의 메타데이터를 반환하는 메서드입니다.
	 * @author 송태호
	 * @param files 클라이언트가 업로드 요청한 MultipartFile의 List
	 * @param subDirectory 그 파일들을 실제로 저장할 하위 경로.
	 * @param subPath 직접 지정할 추가 하위 경로
	 * @return 저장을 완료한 파일들의 메타데이터. 이 데이터를 DB에 저장합니다.
	 */
	public List<FileDetailInfo> saveAtDirectory(
		List<MultipartFile> files,
		FileUploadDirectory subDirectory,
		String subPath
	);
	
	/**
	 * 저장한 파일들의 메타데이터를 DB에 저장하는 메서드입니다.
	 * @param files saveAtDirectory 메서드를 실행한 후 얻은 FileDetailVO List
	 * @param uploaderUserId 업로드한 사용자의 USER_ID
	 * @param publicYn 공개된 자료인지
	 * @return DB의 Files 테이블에 저장한 파일들의 FileId
	 */
	public String saveAtDB(
		List<FileDetailInfo> fileMetaDatas
    	, String uploaderUserId
    	, boolean isPublic
    );
	
	/**
	 * 파일 묶음의 사용 여부를 변경해줍니다. <br>
	 * 첨부파일 업로드가 참조문서 작성보다 먼저 이루어지는 경우 <br>
	 * 이 메서드로 최종적으로 사용되는 파일을 Y로 바꿔주세요. <br>
	 * 또한 파일을 사용중인 문서가 삭제되는 경우에도 필요합니다.
	 * 
	 * @param fileId 파일묶음 ID
	 * @param isUsing true = 사용중, false = 사용중이 아님
	 */
	public void changeUsingStatus(
		String fileId
		, boolean isUsing
	);
	
	/**
	 * 파일을 첨부할 수 있는 문서가 변경되었을 때 <br>
	 * 기존/수정할 때 첨부한 파일 묶음의 사용 상태를 처리해줍니다.
	 * 
	 * @param originFileId DB에서 가져온 첨부파일ID를 null 체크 없이 넣으세요.
	 * @param changedFileId 요청에서 가져온 첨부파일ID를 null 체크 없이 넣으세요.
	 */
	public void switchActiveFileStatus(
		String originFileId
		, String changedFileId
	);
	
	// ====================
	// 파일 쓰기 관련 끝
	// ====================
	
	// ====================
	// 여기서부터 파일 읽기 관련
	// ====================
	
	/**
	 * 파일ID로 묶음 저장된 파일들에 대한 메타데이터를 한번에 가져옵니다. <br>
	 * FILE_ID는 사용자에게 알려져선 안 되는 정보이므로 요청으로 내보낼 수 없습니다. <br>
	 * (예1. 학생의 증명사진이 필요하면, 사용자는 "학번"으로만 요청하지, 직접 파일ID를 알아내서 요청하지 않음) <br>
	 * (예2. 게시글의 첨부파일이 필요하면, 사용자는 "어떤 게시글" 의 "n번째" 첨부파일을 요청하지, 파일ID 요청이 아님) <br>
	 * fileId에 해당하는 레코드가 없을 때는 null을 반환하며, <br>
	 * fileId에 해당하는 레코드가 "삭제 상태" 이면 레코드를 그대로 반환합니다.
	 * 
	 * @param fileId
	 * @return 파일묶음 정보 + 파일상세 정보 리스트
	 */
	public FilesBundleDTO readFileBundle(
		String fileId
	);
	
	/**
	 * 파일ID로 저장된 파일에 대한 "정보"만을 요청할 경우 <br>
	 * 실제 저장 경로, 저장명을 제외한 정보를 전달합니다. <br>
	 * readFileBundle()과 달리, 반환값을 바로 요청으로 내보낼 수 있습니다. <br>
	 * USING_YN이 "N" 인 파일을 요청할 경우, "삭제된 파일" 응답을 내보냅니다.
	 * 
	 * @param fileId 파일ID
	 * @return "저장되는 경로", "저장된 UUID"를 제외한 응답용 객체
	 * @throws RuntimeException <br>
	 * 1. 비활성화된(파일 관련 주체가 삭제되거나, 첨부파일을 수정하여 비활성화된 경우<br>
	 * 2. 첨부된 파일 Detail 이 하나도 없을 경우
	 */
	public List<FileDetailResp> readFileDetailList(
		String fileId
	) throws RuntimeException;
	// TODO 예외 타입 정하기
	
	
	
	// ====================
	// 파일 읽기 관련 끝
	// ====================
}
