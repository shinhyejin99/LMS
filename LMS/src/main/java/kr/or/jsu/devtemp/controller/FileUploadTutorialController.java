package kr.or.jsu.devtemp.controller;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/devtemp/files")
@RequiredArgsConstructor
public class FileUploadTutorialController {
	
	private final FilesUploadService fileUploadService;
	
	// 업로드 폼
	@GetMapping("/upload")
	public String uploadForm() {
		return "devTemp/files/uploadForm";
	}

	// 업로드 처리
	@PostMapping("/upload")
	public String handleUpload(
		List<MultipartFile> files
		, @ModelAttribute PortalNoticeVO portalNoticeVO
		, RedirectAttributes ra
	) {
		// 이게 학사공지 insert 프로세스라고 가정하겠습니다.
		// uploadForm.jsp 확인해보시면, 각각 VO의 필드와 name을 맞춘 인풋이 있죠?
		// 그리고 파일 관련해서는 VO 컬럼이랑 이름을 맞추지 않습니다.
		
		log.info("학사공지 VO에 들어온 내용 : {}", PrettyPrint.pretty(portalNoticeVO));
		/*
			"noticeId" : null,
			"staffNo" : null,
			"noticeTypeCd" : "그냥타입",
			"title" : "제목입니다",
			"content" : "내용입니다",
			"attachFileId" : null,
			"createAt" : null,
			"modifyAt" : null,
			"deleteYn" : null
		*/
		// 이 시점에서 PORTAL_NOTICE 테이블에 "사용자가 직접" 채울 내용은 가져왔고, attachFileId는 비어있죠?
		// 그리고 그 파일들이 List<MultipartFile> files 안에 들어있습니다.
		// 그러면 이제 PortalNoticeServiceImpl pnService를 불러서,
		// pnService.createPortalNotice(portalNoticeVO, files) 이렇게 두 인자를 던져줍니다. (다음부터는 service 안이라고 가정)
		
		// service에서는 먼저 fileUploadService를 가져와서 의존성 주입합니다. (private final FileUploadService ㄱㄱ)
		// 그리고 어떻게든 이 파일 올리려는 사용자의 USER_ID를 갖고오세요.
		String uploaderUserId = null;
		
		// 그런다음 파일을 먼저 저장할겁니다.
		// fileUploadService.saveAtDirectory 메서드로 파일을 D드라이브의 파일저장소에 실제로 저장함과 동시에,
		// DB에 저장할 파일 메타데이터가 들어있는 result를 가져옵니다.
		List<FileDetailVO> result = fileUploadService.saveAtDirectory(files, FileUploadDirectory.DEVTEMP);
		
		// 여기서 한번 로그 찍어보기.
		log.info("저장된 파일들 : {}", PrettyPrint.pretty(result));
		
		// 그리고 그 "메타데이터 + 파일 업로드한 유저의 ID +  공개 여부 boolean" 3개의 인자를 사용해 DB에 파일정보를 저장합니다.
		// return값인 fileId가 그 파일묶음 ID이고요.
		String fileId = fileUploadService.saveAtDB(result, uploaderUserId, false);
		
		log.info("DB의 FILES 테이블에 저장된 파일묶음 ID : {}", fileId);
		
		ra.addFlashAttribute("fileDetailVO", result);
		ra.addFlashAttribute("fileId", fileId);
		
		return "redirect:/devtemp/files/uploadresult";
	}
	
	// 업로드 결과
	@GetMapping("/uploadresult")
	public String uploadResult() {
		return "devTemp/files/uploadResult";
	}
	
	// <img src>로 사진 출력
	@GetMapping("/print")
	public String showImageFileForm() {
		return "devTemp/files/imgform";
	}
	
	@PostMapping("/print")
	public String showImageFileProcess(
		@RequestParam(name = "fileId", required = true) String fileId
		, Model model
	) {
		model.addAttribute("fileId", fileId);
		return "devTemp/files/imgsrc";
	}
	
	@GetMapping("/idphoto")
	public ResponseEntity<Resource> idPhoto(
		@RequestParam String fileId
	) throws MalformedURLException {
		FileDetailVO meta = fileUploadService.readPhotoMetaData(fileId);
		
		log.info("가져온 이미지파일 정보 : {}", PrettyPrint.pretty(meta));
		
		if (meta == null) return ResponseEntity.notFound().build();
		
		Path path = Paths.get(meta.getSaveDir().trim(), meta.getSaveName().trim()).normalize();
        UrlResource res = new UrlResource(path.toUri());
        if (!res.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(toMediaType(meta.getExtension()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + URLEncoder.encode(meta.getOriginName(), StandardCharsets.UTF_8) + "\"")
                .body(res);
		
		
	}
	
	private MediaType toMediaType(String ext) {
        if (ext == null) return MediaType.APPLICATION_OCTET_STREAM;
        switch (ext.toLowerCase()) {
            case "png":  return MediaType.IMAGE_PNG;
            case "jpg":
            case "jpeg": return MediaType.IMAGE_JPEG;
            case "gif":  return MediaType.IMAGE_GIF;
            case "webp": return MediaType.parseMediaType("image/webp");
            case "bmp":  return MediaType.parseMediaType("image/bmp");
            default:     return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
