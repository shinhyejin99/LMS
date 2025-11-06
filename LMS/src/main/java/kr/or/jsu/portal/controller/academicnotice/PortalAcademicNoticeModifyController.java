package kr.or.jsu.portal.controller.academicnotice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.core.validate.groups.UpdateGroup;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalNoticeVO;
import kr.or.jsu.vo.StaffVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	정태일	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/academicnotice/modify")
@RequiredArgsConstructor
public class PortalAcademicNoticeModifyController {

    private final PortalNoticeService service;
    private final FilesUploadService fileService;
    
    public static final String MODELNAME = "notice";

    @GetMapping("/{noticeId}")
    public String formUI(
		@PathVariable String noticeId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PortalNoticeVO notice = service.readPortalNoticeDetail(noticeId);
        
	    checkAuthorization(noticeId, userDetails);
        
        String attachFileId = notice.getAttachFileId();
        if (attachFileId != null && !attachFileId.isEmpty()) {
            List<FileDetailVO> fileList = fileService.getFileListByFileId(attachFileId);
            model.addAttribute("fileList", fileList);
            log.info("수정 폼 로드: 기존 파일 ID={}, 파일 개수={}", attachFileId, fileList.size());
        }
	    
        model.addAttribute("notice", notice);
        return "portal/portalAcademicNoticeEdit";
    }

    @PostMapping("/{noticeId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> formProcess(
    	@PathVariable String noticeId,
        @Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) PortalNoticeVO notice,
        BindingResult errors,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) List<Integer> keepFiles,
        @RequestParam(required = false) List<MultipartFile> files
    ) {
    	
    	 if (notice.getIsUrgent() == null) {
    		 notice.setIsUrgent("N");
    	 }
    	
	    if(errors.hasErrors()) {
	        return ResponseEntity.badRequest().body(Map.of(
	            "status", "error",
	            "message", "입력값을 확인해주세요."
	        ));
	    }

	    String fileId = null;
	    String uploaderUserId = userDetails.getRealUser().getUserId(); 
    	
	    PortalNoticeVO existingNotice = service.readPortalNoticeDetail(noticeId);
	    String existingFileId = existingNotice.getAttachFileId();
    	
	    notice.setNoticeId(noticeId);
	    
	    try {
	        checkAuthorization(noticeId, userDetails);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
	            "status", "error",
	            "message", e.getMessage()
	        ));
	    }
    	
	    boolean hasNewFiles = !CollectionUtils.isEmpty(files);
	    boolean fileCheckboxChanged = false;
	    
	    if (existingFileId != null && keepFiles != null) {
	        List<Integer> existingOrders = fileService.getFileListByFileId(existingFileId)
	            .stream()
	            .map(FileDetailVO::getFileOrder)
	            .sorted()
	            .collect(Collectors.toList());
	        
	        List<Integer> sortedKeepFiles = new ArrayList<>(keepFiles); 
	        Collections.sort(sortedKeepFiles);
	        
	        fileCheckboxChanged = !existingOrders.equals(sortedKeepFiles);
	        
	        log.info("기존 파일 순번: {}, 유지할 파일 순번: {}, 변경됨: {}", 
	            existingOrders, sortedKeepFiles, fileCheckboxChanged);
	    }
	    
	    boolean fileChanged = fileCheckboxChanged || hasNewFiles;
	    
	    if (fileChanged && existingFileId != null) {
	        try {
	            List<FileDetailVO> allFiles = new ArrayList<>();
	            
	            if (!CollectionUtils.isEmpty(keepFiles)) {
	                log.info("유지할 파일 순번: {}", keepFiles);
	                for (Integer order : keepFiles) {
	                    FileDetailVO keepFile = fileService.getFileDetailForDownload(existingFileId, order);
	                    if (keepFile != null) {
	                        allFiles.add(keepFile);
	                        log.info("기존 파일 유지: order={}, name={}.{}", order, keepFile.getOriginName(), keepFile.getExtension());
	                    }
	                }
	            }
	            
	            if (hasNewFiles) {
	                List<FileDetailVO> newFileMetaDatas = fileService.saveAtDirectory(files, FileUploadDirectory.DEVTEMP);
	                allFiles.addAll(newFileMetaDatas);
	                log.info("새 파일 추가: {}개", newFileMetaDatas.size());
	            }
	            
	            for (int i = 0; i < allFiles.size(); i++) {
	                allFiles.get(i).setFileOrder(i + 1);
	            }
	            
	            if (!allFiles.isEmpty()) {
	                fileId = fileService.saveAtDB(allFiles, uploaderUserId, true);
	                fileService.modifyFileUsingYn(existingFileId);
	                log.info("파일 병합 완료: 새 fileId={}, 총 파일 개수={}", fileId, allFiles.size());
	            } else {
	                fileService.modifyFileUsingYn(existingFileId);
	                fileId = null;
	                log.info("모든 파일 삭제: 기존 fileId={}", existingFileId);
	            }
	        } catch (RuntimeException e) {
	            log.error("파일 처리 중 오류 발생: {}", e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	                "status", "error",
	                "message", "파일 처리 중 시스템 오류가 발생했습니다. 다시 시도해 주세요."
	            ));
	        }
	        
	    } else if (!fileChanged && existingFileId != null) {
	        fileId = existingFileId;
	        log.info("파일 변경 없음: 기존 fileId 유지={}", existingFileId);
	        
	    } else if (hasNewFiles && existingFileId == null) {
	        try {
	            List<FileDetailVO> fileMetaDatas = fileService.saveAtDirectory(files, FileUploadDirectory.DEVTEMP);
	            fileId = fileService.saveAtDB(fileMetaDatas, uploaderUserId, true);
	            log.info("새 파일 등록: fileId={}", fileId);
	        } catch (RuntimeException e) {
	            log.error("파일 저장 중 오류: {}", e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	                "status", "error",
	                "message", "파일 처리 중 오류가 발생했습니다."
	            ));
	        }
	    }
	    
	    notice.setAttachFileId(fileId);    	
    	
    	try {
    		service.modifyPortalNotice(notice);
    		return ResponseEntity.ok(Map.of(
		       "status", "success",
	            "redirectUrl", "/portal/academicnotice/detail/" + noticeId
				));
    				
	    } catch(Exception e) {
	        log.error("게시글 수정 실패: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	            "status", "error",
	            "message", e.getMessage()
	        ));
	    }
	}
    	
	private void checkAuthorization(String noticeId, CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getRealUser() == null || !(userDetails.getRealUser() instanceof StaffVO)) {
            throw new IllegalArgumentException("수정 권한이 없습니다: 교직원만 수정 가능합니다.");
        }
	}	
}
