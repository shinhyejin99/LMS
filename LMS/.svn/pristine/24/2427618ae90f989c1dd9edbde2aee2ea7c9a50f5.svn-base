package kr.or.jsu.portal.controller.notice;

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
 * @since 2025. 9. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 27.     	정태일	          최초 생성
 *  2025.10. 01.     	정태일	          첨부파일 기능 추가(수정 폼 기존 redirect -> map 으로 변경)
 *  2025.10. 29.     	정태일	          긴급 기능 추가
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/notice/modify")
@RequiredArgsConstructor
public class PortalNoticeModifyController {

    private final PortalNoticeService service;
    private final FilesUploadService fileService; // 파일 업로드 서비스
    
    public static final String MODELNAME = "notice";

    /** 수정 폼 */
    @GetMapping("/{noticeId}")
    public String formUI(
		@PathVariable String noticeId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails userDetails
    ) {
    	// 게시글 조회
        PortalNoticeVO notice = service.readPortalNoticeDetail(noticeId);
        
	    // 권한 검증
	    checkAuthorization(noticeId, userDetails);
        
	    /// 추가
        // 기존 파일 목록 조회 및 Model 추가 
        String attachFileId = notice.getAttachFileId();
        if (attachFileId != null && !attachFileId.isEmpty()) {
            List<FileDetailVO> fileList = fileService.getFileListByFileId(attachFileId);
            model.addAttribute("fileList", fileList); // JSP에서 기존 파일 표시용
            log.info("수정 폼 로드: 기존 파일 ID={}, 파일 개수={}", attachFileId, fileList.size());
        }
	    
        model.addAttribute("notice", notice);
        return "portal/portalNoticeEdit"; // 같은 폼 재사용
    }

    /** 수정 처리 */
    @PostMapping("/{noticeId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> formProcess(
    	@PathVariable String noticeId,
        @Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) PortalNoticeVO notice,
        BindingResult errors,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) List<Integer> keepFiles, // 기존 파일 순번
        @RequestParam(required = false) List<MultipartFile> files // 새로운 첨부파일
        
//        RedirectAttributes redirectAttributes
    ) {
    	if (notice.getIsUrgent() == null) { 
    		notice.setIsUrgent("N");
    	}
    	
    	//=========================
	    // 에러가 있을 때
	    //=========================
	    if(errors.hasErrors()) {
	        return ResponseEntity.badRequest().body(Map.of(
	            "status", "error",
	            "message", "입력값을 확인해주세요."
	        ));
	    }
	    //=========================
	    // 에러가 없을 때
	    //=========================
	    String fileId = null;
//	    String uploaderUserId = null;
	    String uploaderUserId = userDetails.getRealUser().getUserId(); 
    	
	    // 기존 게시글 정보 조회
	    PortalNoticeVO existingNotice = service.readPortalNoticeDetail(noticeId);
	    String existingFileId = existingNotice.getAttachFileId(); // 기존 첨부파일ID
    	
	    notice.setNoticeId(noticeId);
	    
	    //=========================
	    // 권한 검증
	    //=========================
	    try {
	        checkAuthorization(noticeId, userDetails);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
	            "status", "error",
	            "message", e.getMessage()
	        ));
	    }
    	
	    //=========================
	    // 파일 변경 여부 확인 (새로운 파일 업로드 여부)
	    //=========================
	    boolean hasNewFiles = !CollectionUtils.isEmpty(files);
	    //= files != null && !files.isEmpty(); StringUtils 쓰던 거랑 똑같!
	    
	    // 기존 파일 체크박스 변경 여부 확인
	    boolean fileCheckboxChanged = false;
	    
	    // 기존 파일이 변경되지 않았을 때(기존 첨부파일ID가 존재할 때 && 기존 파일 체크박스를 그대로 체크된 상태로 냅뒀을 때)
	    if (existingFileId != null && keepFiles != null) {
	        // 1. 기존 파일 순번 조회
	        List<Integer> existingOrders = fileService.getFileListByFileId(existingFileId)
	            .stream() // 스트림으로 변환
	            .map(FileDetailVO::getFileOrder) // fileOrder 만 추출함
	            .sorted() // 오름차순으로 정렬하고
	            .collect(Collectors.toList()); // 변환했던 스트림을 list로 변환한다.
	        
	        // 2. js에서 받은 순번 정렬
	        List<Integer> sortedKeepFiles = new ArrayList<>(keepFiles); 
	        Collections.sort(sortedKeepFiles);
	        
	        // 1번과 2번 비교: (순번이) 다르면 파일이 변경된 것
	        fileCheckboxChanged = !existingOrders.equals(sortedKeepFiles);
	        
	        log.info("기존 파일 순번: {}, 유지할 파일 순번: {}, 변경됨: {}", 
	            existingOrders, sortedKeepFiles, fileCheckboxChanged);
	    }
	    
	    // ==== 최종 파일 변경 여부 판단 =====
	    // true : 체크박스 변경 or 새 파일 추가
	    boolean fileChanged = fileCheckboxChanged || hasNewFiles;
	    
	    //=========================
	    // 파일 처리
	    //=========================
	    // 1. 기존 파일 O + 변경 발생 O
	    if (fileChanged && existingFileId != null) {
	        try {
	        	// 새롭게 변경된 파일 순번을 담을 List 생성
	            List<FileDetailVO> allFiles = new ArrayList<>();
	            
	            // 1-1. 기존 파일 중 유지할 것들 조회
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
	            
	            // 1-2. 새 파일들 디스크에 저장
	            if (hasNewFiles) {
	                List<FileDetailVO> newFileMetaDatas = fileService.saveAtDirectory(files, FileUploadDirectory.DEVTEMP);
	                allFiles.addAll(newFileMetaDatas);
	                log.info("새 파일 추가: {}개", newFileMetaDatas.size());
	            }
	            
	            // 1-3. fileOrder 재정렬
	            for (int i = 0; i < allFiles.size(); i++) {
	                allFiles.get(i).setFileOrder(i + 1);
	            }
	            
	            // 1-4. 파일이 하나라도 있으면 새 파일 묶음 생성 & 기존 파일ID의 usingYn을 N으로 변경
	            if (!allFiles.isEmpty()) {
	                fileId = fileService.saveAtDB(allFiles, uploaderUserId, true);
	                fileService.modifyFileUsingYn(existingFileId); // 기존 파일의 사용여부를 N으로 바꾼다.
	                log.info("파일 병합 완료: 새 fileId={}, 총 파일 개수={}", fileId, allFiles.size());
	            } else {
	                // 1-5. 모든 파일 삭제 (체크박스 모두 해제 + 첨부파일 X)
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
	        
	    // 2. 파일 변경 X → 기존 fileId 유지
	    } else if (!fileChanged && existingFileId != null) {
	        fileId = existingFileId;
	        log.info("파일 변경 없음: 기존 fileId 유지={}", existingFileId);
	        
	    // 3. 기존 파일 X + 새 파일 추가
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
	    
	    // fileId setting
	    notice.setAttachFileId(fileId);    	
    	
	    //=========================
	    // 게시글 수정
	    //=========================
	    // service 실행 
    	try {
    		service.modifyPortalNotice(notice);
    		// 리다이렉트 경로 수정 (목록 -> 상세 보기)
    		return ResponseEntity.ok(Map.of(
		       "status", "success",
	            "redirectUrl", "/portal/notice/detail/" + noticeId
				));
    				
	    } catch(Exception e) {
	        log.error("게시글 수정 실패: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	            "status", "error",
	            "message", e.getMessage()
	        ));
	    }
	}
    	
	// 권한 검증 로직
	private void checkAuthorization(String noticeId, CustomUserDetails userDetails) {
//	    PortalNoticeVO notice = service.readPortalNoticeDetail(noticeId);

        if (userDetails == null || userDetails.getRealUser() == null || !(userDetails.getRealUser() instanceof StaffVO)) {
            throw new IllegalArgumentException("수정 권한이 없습니다: 교직원만 수정 가능합니다.");
        }

	    // 작성부서 기능 추가했을떄 추후 수정
//	    StaffVO staff = (StaffVO) userDetails.getRealUser();
//	    String currentDeptName = service.readStfDeptNameByCode(staff.getStfDeptCd());
//
//	    if (!currentDeptName.equals(job.getStfDeptName())) {
//	        throw new IllegalArgumentException("수정 권한이 없습니다: 작성 부서 불일치");
//	    }
	}	
    	
    	
//    	notice.setNoticeId(noticeId);
//        if (errors.hasErrors()) {
//        	service.modifyPortalNotice(notice);
//        	redirectAttributes.addAttribute("noticeId", noticeId);
//            return "redirect:/portal/notice/{noticeId}";  // 상세보기 페이지로 
//        } else {
//        	// 검증 실패 : prodId 가 없다는 뜻(그래서 입력한 what을 넣어준다)
//			// 저 위에 getMapping으로 보낸다
//        	redirectAttributes.addAttribute("noticeId", notice.getNoticeId());
//        	// 에러는 커스텀으로 넣을 수 있다(직접적으로 못 넣음)
//        	String errorsAttributeName = BindingResult.MODEL_KEY_PREFIX + "notice";
//        	redirectAttributes.addFlashAttribute(errorsAttributeName, errors); // model 통해서 수신가능
//         return "redirect:/portal/notice/modify/{noticeId}";
//        }
//    	
//    	
//        // 1. URL 경로 변수의 noticeId를 VO 객체에 설정 (누락 방지)
//        notice.setNoticeId(noticeId);
//        
//        // 2. 유효성 검증 실패 (errors.hasErrors() == true)
//        if (errors.hasErrors()) {
//            // 검증 실패 시:
//            // - DB 업데이트(modifyPortalNotice)를 시도하지 않습니다.
//            // - 리다이렉트(redirect) 대신, 현재 폼 뷰로 포워딩하여 에러 메시지를 표시합니다.
//            return "portal/portalNoticeEdit"; 
//        } 
//        
//        // 3. 유효성 검증 성공 (errors.hasErrors() == false)
//        
//        // 데이터베이스 수정 처리
//        service.modifyPortalNotice(notice);
//        
//        // 수정 완료 후 상세 보기 페이지로 리다이렉트
//        redirectAttributes.addAttribute("noticeId", noticeId);
//        return "redirect:/portal/notice/detail/{noticeId}"; 
//        
//        // **기존 코드의 else 블록은 삭제되거나 위와 같이 정리됨**
//	    
//    }
}
