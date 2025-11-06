package kr.or.jsu.portal.controller.academicnotice;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *  2025.10. 14.     	정태일	          목록 생성
 *  2025.10. 16.     	정태일	          상세보기 메서드 추가
 *
 *      </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/academicnotice")
@RequiredArgsConstructor
public class PortalAcademicNoticeReadController {

	private final PortalNoticeService noticeService;
	private final FilesUploadService filesUploadService;

	@GetMapping("/list")
	public String academicNoticeListPage(
		@ModelAttribute("paging") PaginationInfo<PortalNoticeVO> paging,
		Model model
	) {
		paging.setNoticeTypeCd("ACADEMIC");
		noticeService.readPortalNoticeList(paging);
		model.addAttribute("paging", paging);
		return "portal/portalAcademicNoticeList";
	}
	
	@GetMapping("/detail/{noticeId}")
	public String getAcademicNoticeDetail(
			@PathVariable String noticeId, 
			Model model) {
		PortalNoticeVO notice = noticeService.readPortalNoticeDetail(noticeId);
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
    	
		return "portal/portalAcademicNoticeDetail";
	}
}












