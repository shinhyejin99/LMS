package kr.or.jsu.portal.controller.academicnotice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.portal.service.notice.PortalNoticeService;
import lombok.RequiredArgsConstructor;

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
@Controller
@RequestMapping("/portal/academicnotice")
@RequiredArgsConstructor
public class PortalAcademicNoticeRemoveController {

    private final PortalNoticeService service;

    @PostMapping("/remove/{noticeId}")
    public String remove(@PathVariable String noticeId) {
        service.removePortalNotice(noticeId);
        return "redirect:/portal/academicnotice/list"; 
    }
}
