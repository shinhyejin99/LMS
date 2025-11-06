package kr.or.jsu.core.common.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.info.FilesInfo;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.mybatis.mapper.files.LMSFilesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LMSFilesServiceImpl implements LMSFilesService {
	
	@Value("${file-info.repository.path}")
	File directory;
	
	private final LMSFilesMapper filesMapper;
	
	
	// ==================== 
	// 파일 저장하는 로직
	
	@Override
	public List<FileDetailInfo> saveAtDirectory( // 서브디렉토리 하나만 필요할 때
		List<MultipartFile> files,
		FileUploadDirectory subDirectory
	) {
		return saveAtDirectory(files, subDirectory.getDirectory());
	}

	@Override
	public List<FileDetailInfo> saveAtDirectory( // 서브디렉토리 + 추가 서브디렉토리
		List<MultipartFile> files,
		FileUploadDirectory subDirectory,
		String subPath
	) {
		return saveAtDirectory(files, subDirectory.getDirectory()+subPath);
	}
	
	private List<FileDetailInfo> saveAtDirectory(
		List<MultipartFile> files,
		String uploadPath
	) {
		// 저장될 파일들의 메타데이터를 보관할 리스트 생성.
		List<FileDetailInfo> fileList = new ArrayList<>();
		// 저장하라고 던져준 파일리스트가 null이거나 비어있으면 예외 던지기
		if (files == null || files.isEmpty()) throw new RuntimeException("저장할 파일이 없습니다.");
		
		String saveDir = directory.getPath() + uploadPath;
		
		File dir = new File(saveDir);
		if (!dir.exists()) {
	        boolean created = dir.mkdirs(); // 상위 폴더까지 생성
	        if (!created) {
	            throw new RuntimeException("폴더 생성 실패: " + saveDir);
	        }
	    }
		
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
			FileDetailInfo eachFile = 
					new FileDetailInfo(
						i+1
						, null
						, parts.baseName
						, parts.extension
						, uploadPath
						, mf.getSize()
						, saveName
					);
			
			fileList.add(eachFile);
		}
		
		return fileList;
	}
	

	@Override
	@Transactional
	public String saveAtDB(
		List<FileDetailInfo> fileMetaDatas,
		String uploaderUserId,
		boolean isPublic
	) {
		FilesInfo file = new FilesInfo();
		file.setUserId(uploaderUserId);
		file.setPublicYn(isPublic ? "Y" : "N");
		
		filesMapper.insertFiles(file);
		
		String fileId = file.getFileId();
		
		fileMetaDatas.stream().forEach(eachFile -> eachFile.setSaveDir(directory.getPath() + eachFile.getSaveDir()));
		
		filesMapper.insertFileDetails(fileId, fileMetaDatas);
		
		return fileId;
	}
	
	@Override
	public void changeUsingStatus(
		String fileId
		, boolean isUsing
	) {
		filesMapper.updateFileStatus(fileId, isUsing ? "Y" : "N");
	}
	
	@Override
	public void switchActiveFileStatus(
		String originFileId
		, String changedFileId
	) {
		// 파일 변경이 없을 경우
		if(originFileId == null && changedFileId == null) return;
		if(changedFileId.equals(originFileId)) return;
		
		// 파일 변경이 있을 경우
		if(originFileId != null) { // 기존에 파일이 있었으면
			changeUsingStatus(originFileId, false); // 파일 비활성화 시키고
		}
		
		if(changedFileId != null) { // 수정 시 파일을 첨부했으면
			changeUsingStatus(changedFileId, true);
		}
	}
	

	// ====================
	// 파일 쓰기 관련 끝
	// ====================
	
	// ====================
	// 여기서부터 파일 읽기 관련
	// ====================	

	public FilesBundleDTO readFileBundle(
		String fileId
	) {
		// TODO 예외처리
		FilesBundleDTO bundle = filesMapper.selectFileBundle(fileId);
		if(bundle == null) throw new RuntimeException("존재하지 않는 파일ID 입니다.");
		
		return bundle;
	}

	@Override
	public List<FileDetailResp> readFileDetailList(
		String fileId
	) throws RuntimeException {
		FilesBundleDTO bundle = filesMapper.selectFileBundle(fileId);
		String isUsing = bundle.getFilesInfo().getUsingYn();
		
		List<FileDetailInfo> fileDataList = bundle.getFileDetailInfo();
		
		// TODO 예외 제대로 잡기
		if("N".equals(isUsing) || fileDataList.size() == 0) {
			FileDetailResp error = new FileDetailResp();
			error.setOriginName("삭제된 파일");
			List<FileDetailResp> submitFiles = List.of(error);
			return submitFiles;
		}
		
		// 민감정보 빼고 응답용 json객체 만들어서 반환
		return fileDataList.stream().map(f -> {
			FileDetailResp resp = new FileDetailResp();
			BeanUtils.copyProperties(f, resp);
			return resp;
		}).collect(Collectors.toList());
	}
	
	// ====================
	// 파일 읽기 관련 끝
	// ====================	
	
	
	// ==================== 
	// 파일 저장할 때 쓸 1회용 클래스
	// ====================
	
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

	// 파일 저장할 때 쓸 1회용 클래스
	// ====================
}
