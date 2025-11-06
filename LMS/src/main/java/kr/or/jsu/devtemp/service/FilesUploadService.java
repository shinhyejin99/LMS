package kr.or.jsu.devtemp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.vo.FileDetailVO;

/**
 * 파일을 D드라이브에 저장하고, 파일 메타데이터를 DB에 저장하는 서비스.
 *
 * @author 송태호
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	송태호	          최초 생성
 *	2025. 9. 29.		김수현			파일 다운로드 관련 메소드 추가
 * </pre>
 */
public interface FilesUploadService {
	/**
	 * MultipartFile List와 저장할 경로를 받아 디스크의 경로에 파일을 저장하고<br>
	 * 저장한 파일들의 메타데이터를 반환하는 메서드입니다.
	 * @author 송태호
	 * @param files 클라이언트가 업로드 요청한 MultipartFile의 List
	 * @param subDirectory 그 파일들을 실제로 저장할 하위 경로. 여기에 포커싱하고 코드 자동완성하면 됩니다. <br>
	 * 설명이 필요하면 직접 들어가서 확인하세요.
	 * @return 저장을 완료한 파일들의 메타데이터. 이 데이터를 DB에 저장합니다.
	 */
	public List<FileDetailVO> saveAtDirectory(List<MultipartFile> files, FileUploadDirectory subDirectory);

	/**
	 * 저장한 파일들의 메타데이터를 DB에 저장하는 메서드입니다.
	 * @param files saveAtDirectory 메서드를 실행한 후 얻은 FileDetailVO List
	 * @param uploaderUserId 업로드한 사용자의 USER_ID
	 * @param publicYn 공개된 자료인지
	 * @return DB의 Files 테이블에 저장한 파일들의 FileId
	 */
	public String saveAtDB(
		List<FileDetailVO> fileMetaDatas
    	, String uploaderUserId
    	, boolean isPublic
    );

	public FileDetailVO readPhotoMetaData(String fileId);

	//////////////////////////

	/**
     * 파일 ID와 순번으로 단일 파일의 메타데이터를 조회 (다운로드 컨트롤러 사용)
     * @param fileId 파일 묶음 ID
     * @param fileOrder 파일의 순번
     * @return FileDetailVO 파일 상세 정보
     */
    public FileDetailVO getFileDetailForDownload(String fileId, int fileOrder);

    /**
     * 파일 묶음 ID로 해당 묶음의 모든 파일 리스트를 조회 (게시글 상세 화면에서 사용 - 첨부된 파일 목록 확인할 때)
     * @param fileId 파일 묶음 ID
     * @return List<FileDetailVO> 파일 상세 정보 목록
     */
    public List<FileDetailVO> getFileListByFileId(String fileId);

    /**
     * 파일 묶음의 사용 여부를 'N'으로 변경
     * @param fileId 파일묶음ID
     */
    public void modifyFileUsingYn(String fileId);
}
