package kr.or.jsu.portal.controller.academicnotice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@RequestMapping("/portal/academicnotice")
@RequiredArgsConstructor
public class PortalAcademicNoticeCreateController {

    private final PortalNoticeService service;
    private final FilesUploadService fileUploadService;

    public static final String MODELNAME = "portalNotice";
    
    /** 등록 폼 */
    @GetMapping("/create")
    public String formUI() {
        return "portal/portalAcademicNoticeForm";
    }
    
    /** 등록 처리 */
    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> formProcess(
    	@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) PortalNoticeVO portalNotice,
        BindingResult errors,
		List<MultipartFile> files,
		Authentication authentication
    ) {
    	
    	Map<String, Object> response = new HashMap<>();
    	// 1. 유효성 검사 (입력값)
        if (errors.hasErrors()) {
            response.put("status", "validation_error");
            response.put("message", "입력 데이터에 오류가 있습니다.");
            return response;
        }
        
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
    	
    	if (uploaderUserId == null || staffNo == null) {
			log.error("로그인된 사용자 정보가 누락되었습니다. (Principal 캐스팅/추출 오류 또는 STAFF_NO 누락");
			response.put("status", "error");
            response.put("message", "로그인 정보가 유효하지 않아 등록할 수 없습니다.");
            return response;
        }
        
    	// 3. STAFF_NO 설정 (추가된 핵심 로직)
        portalNotice.setStaffNo(staffNo);
        
        portalNotice.setNoticeTypeCd("ACADEMIC"); 
        
        portalNotice.setCreateAt(LocalDateTime.now());
        
		List<MultipartFile> attachedFiles = files.stream()
		        .filter(file -> !file.isEmpty())
		        .collect(Collectors.toList());
		
		if(!attachedFiles.isEmpty()) {
			try {
				List<FileDetailVO> fileMetaDatas = fileUploadService.saveAtDirectory(attachedFiles, FileUploadDirectory.DEVTEMP);
				
				fileId = fileUploadService.saveAtDB(fileMetaDatas, uploaderUserId, true);
				log.info("DB에 저장된 파일 묶음 ID : {}", fileId);
			} catch (RuntimeException e) {
                log.error("파일 저장/DB 메타데이터 저장 중 오류 발생: {}", e.getMessage(), e);
                response.put("status", "error");
                response.put("message", "파일 처리 중 시스템 오류가 발생했습니다. 다시 시도해 주세요.");
                return response;               
            }
        } else {
        }
		
		portalNotice.setAttachFileId(fileId);

		try {
			service.createPortalNotice(portalNotice);
            response.put("status", "success");
            response.put("message", "학사공지 등록이 완료되었습니다."); 
            response.put("redirectUrl", "/portal/academicnotice/list");
			return response;
			
		} catch(Exception e) {
            log.error("학사공지 등록 서비스 오류", e);
			response.put("status", "error");
			response.put("message", e.getMessage());
			return response;
		}
	}
}
