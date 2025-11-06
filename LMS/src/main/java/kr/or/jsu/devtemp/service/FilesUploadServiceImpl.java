package kr.or.jsu.devtemp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.dto.FileBundleDTO;
import kr.or.jsu.mybatis.mapper.FilesMapper;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.FilesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *	2025.10. 16.		정태일			파일 첨부 없는 글 수정 시 ORA-17104 방지 로직 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilesUploadServiceImpl implements FilesUploadService {

	@Value("${file-info.repository.path}")
	File directory;

	private final FilesMapper filesMapper;

	@Override
	public List<FileDetailVO> saveAtDirectory(
		List<MultipartFile> files
		, FileUploadDirectory subDirectory
	) {

		// 저장될 파일들의 메타데이터를 보관할 리스트 생성.
		List<FileDetailVO> fileList = new ArrayList<>();
		// 저장하라고 던져준 파일리스트가 null이거나 비어있으면 끝내거나, 예외 던지기
		if (files == null || files.isEmpty()) return fileList;

		// 애플리케이션.프로퍼티스에서 뽑아온 파일저장소 최상위경로와
		// 파라미터에서 가져온 하위경로를 조합
		String saveDir = directory.getPath() + subDirectory.getDirectory();

		// 파일의 멀티파트 하나마다 반복
		for(int i=0; i<files.size(); i++) {
			MultipartFile mf = files.get(i); // 파일 하나 꺼내서
			if (mf == null || mf.isEmpty()) continue; // null이거나 빈파일이면 반복문 넘어가기

			// 원본 파일 이름 꺼내서 파일명/확장자 분리
			String originalFilename = mf.getOriginalFilename();
	        NameParts parts = splitName(originalFilename);

	        // 저장 파일명(UUID) 만들고 그대로 저장.
	        String saveName = UUID.randomUUID().toString();
	        File targetFile = new File(saveDir, saveName);
			try {
				files.get(i).transferTo(targetFile);
			} catch (IllegalStateException | IOException e) { // 저장 실패 시 예외 던지기. 예외는 커스텀익셉션 만들어서 던지기.
	            throw new RuntimeException("파일 저장 실패: " + originalFilename, e);
			}

			// DB에 저장하기 위한 메타데이터 만들기
			FileDetailVO eachFile = new FileDetailVO();
			eachFile.setFileOrder(i+1);
			eachFile.setOriginName(parts.baseName);
			eachFile.setExtension(parts.extension);
			eachFile.setSaveDir(saveDir);
			eachFile.setFileSize(mf.getSize());
			eachFile.setSaveName(saveName);

			fileList.add(eachFile);
		}

		return fileList;
	}

	@Override
	@Transactional
	public String saveAtDB(
		List<FileDetailVO> fileMetaDatas
		, String uploaderUserId
		, boolean isPublic
	) {

		FilesVO vo = new FilesVO();
		vo.setUserId(uploaderUserId);
		vo.setPublicYn(isPublic ? "Y" : "N");

		filesMapper.insertFiles(vo);
		
		String fileId = vo.getFileId();
		// 첨부가 1건 이상 있을 때만 상세 저장
		if (fileMetaDatas != null && !fileMetaDatas.isEmpty()) {
		filesMapper.insertFileDetails(fileId, fileMetaDatas);
		}
		return fileId;
	}

	/**
     * 파일 ID와 순번으로 단일 파일의 메타데이터를 조회. (다운로드 컨트롤러 사용)
     * FilesMapper.selectFileDetail을 호출
     */
    @Override
    public FileDetailVO getFileDetailForDownload(String fileId, int fileOrder) {
        log.debug("getFileDetailForDownload 호출: fileId={}, fileOrder={}", fileId, fileOrder);
        return filesMapper.selectFileDetail(fileId, fileOrder);
    }

    /**
     * 파일 묶음 ID로 해당 묶음의 모든 파일 리스트를 조회 (게시글 상세 화면에서 사용)
     * FilesMapper.selectFileBundle을 사용하여 파일 목록을 추출
     */
    @Override
    public List<FileDetailVO> getFileListByFileId(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return new ArrayList<>();
        }

        FileBundleDTO bundle = filesMapper.selectFileBundle(fileId);
        return bundle != null ? bundle.getFileDetailsInfo() : new ArrayList<>();
    }

	@Override
	public FileDetailVO readPhotoMetaData(String fileId) {

		FileBundleDTO imageFileBunle = filesMapper.selectFileBundle(fileId);

		// 권한 체크할때 이렇게하면 될려나?
//		String uploaderId = imageFileBunle.getFilesInfo().getUserId();
//
//		UsersVO user = (UsersVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String requestUserId = user.getUserId();
//		String requestUserRole = user.getUserType();

		// 0번 인덱스 파일이 무조건 사진일테니깐.
		FileDetailVO imageFileData = imageFileBunle.getFileDetailsInfo().get(0);
		imageFileData.setSaveDir("" + imageFileData.getSaveDir());

		return imageFileData;
	}

	private static class NameParts {
	    final String baseName;  // 확장자 제외한 이름
	    final String extension; // 점(.) 없는 소문자 확장자, 없으면 빈 문자열
	    NameParts(String baseName, String extension) {
	        this.baseName = baseName;
	        this.extension = extension;
	    }
	}

	private static NameParts splitName(String originalFilename) {
	    if (originalFilename == null) return new NameParts("", "");
	    // 오래된 브라우저/클라이언트가 경로를 보내는 경우 대비 (C:\path\to\file.txt, /path/to/file.txt)
	    String justName = originalFilename.replace('\\', '/');
	    int slash = justName.lastIndexOf('/');
	    if (slash >= 0) justName = justName.substring(slash + 1);

	    // 숨김파일(.gitignore), 확장자 없는 경우, 마지막 점 처리
	    int lastDot = justName.lastIndexOf('.');
	    if (lastDot <= 0 || lastDot == justName.length() - 1) {
	        // 점이 없거나, 맨 앞에만 있거나(숨김파일), 맨 끝이 점이면 확장자 없음으로 간주
	        return new NameParts(justName, "");
	    }

	    String base = justName.substring(0, lastDot);
	    String ext  = justName.substring(lastDot + 1).toLowerCase(); // 점 제외 & 소문자
	    return new NameParts(base, ext);
	}

	@Override
	public void modifyFileUsingYn(String fileId) {
		if (fileId != null && !fileId.isEmpty()) { // fileId가 있다는 건 변경사항이 생겼다는 것
	        filesMapper.updateFileUsingYn(fileId, "N"); // 기존 fileId의 사용여부를 N으로 바꿈
	        log.info("파일 사용여부 변경: fileId={}, USING_YN=N", fileId);
	    }
	}
}
