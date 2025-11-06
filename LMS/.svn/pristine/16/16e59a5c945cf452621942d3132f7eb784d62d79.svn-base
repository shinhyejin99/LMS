package kr.or.jsu.portal.controller.notice;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author 정태일
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	정태일	       최초 생성
 *	2025.10. 01.		정태일		   파일 기능 관련 추가	
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/notice")
@RequiredArgsConstructor
public class PortalNoticeReadController {

	private final PortalNoticeService service;
	private final FilesUploadService filesUploadService;
	
	@GetMapping("/list")
	public String getDefaultNoticeList() {
	    // 기본 타입은 GENERAL
//	    return "redirect:/portal/notice/list/GENERAL";
		return "portal/portalNoticeList";
	}
	
    /**
     * 공지사항 목록 뷰
     * URL 예: /portal/notice/list/GENERAL
     *        /portal/notice/list/ACADEMIC
     */
//    @GetMapping("/list/{type}")
//    @GetMapping("/list")
    public String getNoticeList(
//    		@PathVariable("type") String type, 
    		Model model) {
//        model.addAttribute("noticeTypeCd", type); // GENERAL / ACADEMIC
        return "portal/portalNoticeList";
    }

    /**
     * 공지사항 상세 뷰
     * URL 예: /portal/notice/detail/NTC00000001
     */
    @GetMapping("/detail/{noticeId}")
    public String getNoticeDetail(@PathVariable String noticeId, Model model) {
        PortalNoticeVO notice = service.readPortalNoticeDetail(noticeId);
    	model.addAttribute("notice", notice);

        String noticeTypeCd = notice.getNoticeTypeCd(); 
        if (noticeTypeCd != null) {
            model.addAttribute("noticeTypeCd", noticeTypeCd);
        } else {
            // 값이 없는 경우 로그를 남기거나 기본값 설정 등을 고려해야 합니다.
            log.warn("Notice ID {}의 공지 유형 코드(noticeTypeCd)가 조회 결과에 누락되었습니다.", noticeId);
        }
    	
//    	첨부파일이 있다면, 파일 목록을 조회해서 모델에 추가
    	String attachFileId = notice.getAttachFileId();
    	if (attachFileId != null && !attachFileId.isEmpty()) {
        // FilesUploadService를 통해 파일 ID에 해당하는 파일 목록을 가져옵니다.
        List<FileDetailVO> fileList = filesUploadService.getFileListByFileId(attachFileId);
        model.addAttribute("fileList", fileList);	
    	}
    	
    	return "portal/portalNoticeDetail";
    	
    	
    	
    	
    	
    }
    
    
    /**
     * 공지사항 파일 다운로드
     * @param fileId
     * @return
     * @throws MalformedURLException
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
        @RequestParam String fileId // 다운로드할 파일의 고유 ID (FileDetailVO의 ID)
    ) throws MalformedURLException {
        
        // 1. DB에서 파일 메타데이터 조회
        // fileId를 통해 서버에 저장된 경로, 저장명, 원본명을 담은 FileDetailVO를 가져옵니다.
        FileDetailVO meta = filesUploadService.readPhotoMetaData(fileId);
        
        log.info("가져온 다운로드파일 정보 : {}", PrettyPrint.pretty(meta));
        
        // 1-1. 메타데이터가 없는 경우 404 응답
        if (meta == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 2. 서버 디스크 내 파일 경로 설정 및 Resource 객체 생성
        Path path = Paths.get(meta.getSaveDir().trim(), meta.getSaveName().trim()).normalize();
        UrlResource res = new UrlResource(path.toUri());
        
        // 2-1. 실제 파일이 존재하지 않는 경우 404 응답
        if (!res.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 3. 파일명 인코딩 (브라우저/운영체제에 따른 한글 깨짐 방지)
        // '+' 문자를 '%20'으로 치환하여 공백 처리 문제를 방지합니다.
        String encodedFileName = URLEncoder.encode(meta.getOriginName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        
        // 4. Content-Disposition 헤더 설정 (다운로드 핵심)
        // 튜토리얼의 'inline' 대신 'attachment'를 사용하여 무조건 '다운로드'를 유도합니다.
        return ResponseEntity.ok()
            // MIME 타입은 `toMediaType` 대신 `APPLICATION_OCTET_STREAM` (모든 파일)을 사용하거나
            // MimeTypeResolver 등을 사용하여 동적으로 처리할 수도 있습니다.
            .contentType(MediaType.APPLICATION_OCTET_STREAM) 
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
            .body(res);
    }
    
    
}