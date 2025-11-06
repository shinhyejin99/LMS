package kr.or.jsu.portal.controller.notice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.core.validate.groups.InsertGroup;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalNoticeVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
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
 *  2025. 9. 30.     	정태일	          최초 생성
 *  2025.10. 01.     	정태일	          첨부파일 기능 추가(수정 폼 기존 redirect -> map 으로 변경)
 *	2025.10. 02.		정태일			  첨부파일 관련 기능 수정
 *	2025.10. 29.		정태일			  긴급 공지 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/notice")
@RequiredArgsConstructor
public class PortalNoticeCreateController {

    private final PortalNoticeService service;
    private final FilesUploadService fileUploadService; // 파일 업로드 서비스

    public static final String MODELNAME = "portalNotice";
    
    /** 등록 폼 */
    @GetMapping("/create")
    public String formUI() {
        return "portal/portalNoticeForm";
    }


    /** 등록 처리 */
    @PostMapping("/create")
    @ResponseBody
//    public String formProcess(
      public Map<String, Object> formProcess(
    	@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) PortalNoticeVO portalNotice,
        BindingResult errors,
		List<MultipartFile> files,
		Authentication authentication
//		RedirectAttributes redirectAttributes    //리다이렉트 X
    ) {
    	
    	Map<String, Object> response = new HashMap<>();
    	
        // 1. 유효성 검사 (입력값)
        if (errors.hasErrors()) {
            response.put("status", "validation_error");
            response.put("message", "입력 데이터에 오류가 있습니다.");
            // 오류 필드 정보를 포함하려면 BindingResult의 상세 내용을 Map에 추가해야 합니다.
            return response;
        }
    	// 리다이렉트 X
        // 1. 유효성 검사 (입력값)
//        if (errors.hasErrors()) {
//			redirectAttributes.addFlashAttribute(MODELNAME, portalNotice);
//			String errorName = BindingResult.MODEL_KEY_PREFIX + MODELNAME;
//			redirectAttributes.addFlashAttribute(errorName, errors);
//            return "redirect:/portal/notice/create";        	      	
//        }
        
        String fileId = null;
        String uploaderUserId = null;
        String staffNo = null;
        
        // 2. 권한 검사 및 STAFF_NO 추출
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersVO user = userDetails.getRealUser();
			
			if (user != null) {
			    uploaderUserId = user.getUserId();
			if (user instanceof StaffVO) {
				staffNo = ((StaffVO) user).getStaffNo();
				}
			}
		} 
          // 리다이렉트 X  
//    	if (uploaderUserId == null || staffNo == null) {
//			log.error("로그인된 사용자 정보가 누락되었습니다. (Principal 캐스팅/추출 오류 또는 STAFF_NO 누락");
//			redirectAttributes.addFlashAttribute("message", "로그인 정보가 유효하지 않아 등록할 수 없습니다.");
//            return "redirect:/portal/notice/create"; // 권한 없음으로 바로 리턴
//            }
    	
    	if (uploaderUserId == null || staffNo == null) {
			log.error("로그인된 사용자 정보가 누락되었습니다. (Principal 캐스팅/추출 오류 또는 STAFF_NO 누락");
			// ❌ redirectAttributes 대신 Map 사용
			response.put("status", "error");
            response.put("message", "로그인 정보가 유효하지 않아 등록할 수 없습니다.");
            return response; // 권한 없음으로 바로 리턴
        }
        
        
        
        // 3. STAFF_NO 설정 (추가된 핵심 로직)
        portalNotice.setStaffNo(staffNo);
        
        // 4. 공지 유형 코드(NOTICE_TYPE_CD) 설정
        if (portalNotice.getNoticeTypeCd() == null) {
            portalNotice.setNoticeTypeCd("GENERAL"); 
        }
        
        // 5. 긴급 공지 여부 기본값 설정 (체크되지 않은 경우 'N')
        if (portalNotice.getIsUrgent() == null) {
            portalNotice.setIsUrgent("N");
        }
        
        
        portalNotice.setCreateAt(LocalDateTime.now());
        
		// ==========================================================================================================================	
		
        // ----------------------------------------------------
        // 2. 첨부파일 처리 로직 (수정된 핵심 부분)
        // ----------------------------------------------------
    		
		// 1. 실제 내용이 있는 파일만 필터링하여 새로운 리스트 생성
		List<MultipartFile> attachedFiles = files.stream()
		        .filter(file -> !file.isEmpty())
		        .collect(java.util.stream.Collectors.toList());
		
		// 2. 실제 첨부된 파일이 있을 경우에만 파일 처리 로직 실행
		if(!attachedFiles.isEmpty()) {
			try {
				// 파일 디스크에 저장 (필터링된 attachedFiles 사용)
				List<FileDetailVO> fileMetaDatas = fileUploadService.saveAtDirectory(attachedFiles, FileUploadDirectory.DEVTEMP);
				
				// 메타데이터 db에 저장
				fileId = fileUploadService.saveAtDB(fileMetaDatas, uploaderUserId, true);
				log.info("DB에 저장된 파일 묶음 ID : {}", fileId);
			} catch (RuntimeException e) {
				// 파일 처리 실패 시
                log.error("파일 저장/DB 메타데이터 저장 중 오류 발생: {}", e.getMessage(), e);
                // ❌ redirectAttributes 대신 Map 사용
                response.put("status", "error");
                response.put("message", "파일 처리 중 시스템 오류가 발생했습니다. 다시 시도해 주세요.");
                return response;               
            }
        } else {
            // 파일이 첨부되지 않았을 경우 fileId는 null 상태로 유지됩니다. (정상 동작)
        }
		
		// ==========================================================================================================================	
		
		
		
		// fileId setting
		portalNotice.setAttachFileId(fileId);

		// 5. Service 실행 및 리다이렉트 (기존 로직 유지)
		try {
			service.createPortalNotice(portalNotice);
//            redirectAttributes.addFlashAttribute("message", "채용 정보 등록이 완료되었습니다."); // 성공 메시지 추가
//			return "redirect:/portal/notice/";
			// 성공 응답 : 클라이언트 js 가 이 json을 받고 페이지를 이동
            response.put("status", "success");
            response.put("message", "공지사항 등록이 완료되었습니다."); 
            response.put("redirectUrl", "/portal/notice/list"); // 클라이언트가 이동할 경로
			return response;
			
		} catch(Exception e) {
//			redirectAttributes.addFlashAttribute("message", e.getMessage()); // 커스텀 exception에서 꺼냄
//			redirectAttributes.addFlashAttribute(MODELNAME, portalNotice);
//			return "redirect:/portal/notice/create";
			
            log.error("공지사항 등록 서비스 오류", e);
			// ❌ redirectAttributes 대신 Map 사용
			response.put("status", "error");
			response.put("message", e.getMessage()); // 커스텀 exception 메시지 전달
			return response;
			
		}
	}
}