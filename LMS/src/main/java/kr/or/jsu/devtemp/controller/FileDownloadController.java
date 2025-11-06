package kr.or.jsu.devtemp.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.vo.FileDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김수현
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------	   	-------------    ---------------------------
 *  2025. 9. 29.     		김수현	          최초 생성
 *  2025. 10. 30            최건우              수정 내용 주석으로 설명
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/file")
@RequiredArgsConstructor
public class FileDownloadController {

	@Value("${file-info.repository.path}")
	private String fileRepositoryPath;

	private final FilesUploadService fileService;

	/**
	 * 파일 ID와 순번(File Order)을 받아 실제 파일을 다운로드 처리
	 * URL: /portal/file/download/{fileId}/{fileOrder}
	 * * @param fileId 파일 묶음 ID
	 * @param fileOrder 파일의 순번
	 * @return 파일 리소스와 헤더가 포함된 ResponseEntity
	 */
	@GetMapping("/download/{fileId}/{fileOrder}")
	public ResponseEntity<Resource> downloadFile(
		@PathVariable("fileId") String fileId,
		@PathVariable("fileOrder") int fileOrder
	) {
		log.info("파일 다운로드 요청 시작: fileId={}, fileOrder={}", fileId, fileOrder);
		
		// DB 메타데이터 조회 및 유효성 검사
		// DB에서 파일의 저장 경로와 이름 가져오기
		FileDetailVO fileDetail = fileService.getFileDetailForDownload(fileId, fileOrder);
		
		if (fileDetail == null) {
			log.warn("파일의 메타데이터를 찾을 수 없습니다. fileId={}, fileOrder={}", fileId, fileOrder);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일의 메타데이터가 없습니다.");
		}
		
		// 파일 객체 생성
		String saveDir = fileDetail.getSaveDir();
		String saveName = fileDetail.getSaveName();
		// 다운로드 시 보여줄 원본 파일명 (원본 이름 + 확장자)
		String originalFileName = fileDetail.getOriginName() + "." + fileDetail.getExtension();
		
		// 2025.10.30 최건우: 파일 저장 경로 문제 해결을 위해 fileRepositoryPath를 사용하여 절대 경로 생성
		// file-info.repository.path 값에서 'file:' 접두사를 제거하고 사용
		String basePath = fileRepositoryPath.replace("file:", "");
		File file = new File(basePath + saveDir, saveName);
		
		if (!file.exists() || !file.canRead()) {
			log.error("디스크에서 파일을 찾거나 읽을 수 없습니다: {}", file.getAbsolutePath());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다.");
		}
		
		// File 객체 -> FileSystemResource 로 바꾸기
		Resource resource = new FileSystemResource(file);
		
		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		
		try {
			// 파일명 인코딩 (한글 파일명 깨짐 방지: 공백을 %20으로 치환)
			String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
			
			// Content-Disposition: 이 응답은 다운로드(attachment)다 라는 뜻
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
			
		} catch (UnsupportedEncodingException e) {
			log.error("파일명 인코딩 실패", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일명 인코딩 실패");
		}
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		
		// 4. ResponseEntity 반환 (파일 전송 시작)
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	/**
	 * 파일 묶음 ID로 해당 묶음의 모든 파일 목록을 JSON으로 반환
	 * URL: /portal/file/list/{fileId}
	 * @param fileId 파일 묶음 ID
	 * @return 파일 목록 (JSON)
	 */
	@GetMapping("/list/{fileId}")
	@ResponseBody  // JSON 응답을 위한 어노테이션
	public ResponseEntity<List<FileDetailVO>> getFileList(@PathVariable("fileId") String fileId) {
		log.info("파일 목록 조회 요청: fileId={}", fileId);
		
		List<FileDetailVO> files = fileService.getFileListByFileId(fileId);
		
		if (files == null || files.isEmpty()) {
			log.warn("파일 목록이 비어있습니다. fileId={}", fileId);
			return ResponseEntity.ok(List.of()); // 빈 리스트 반환
		}
		
		log.info("파일 목록 조회 성공: fileId={}, 파일 개수={}", fileId, files.size());
		return ResponseEntity.ok(files);
	}
}
