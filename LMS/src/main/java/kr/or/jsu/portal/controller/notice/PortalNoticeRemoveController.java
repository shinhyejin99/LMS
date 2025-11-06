package kr.or.jsu.portal.controller.notice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.portal.service.notice.PortalNoticeService;
import lombok.RequiredArgsConstructor;

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
 *
 * </pre>
 */
@Controller
@RequestMapping("/portal/notice")
@RequiredArgsConstructor
public class PortalNoticeRemoveController {

    private final PortalNoticeService service;

    /** 삭제 처리 */
    @PostMapping("/remove/{noticeId}")
    public String remove(@PathVariable String noticeId) {
        service.removePortalNotice(noticeId);
        return "redirect:/portal/notice/list"; 
        // or 삭제된 공지의 noticeTypeCd 를 조회해서 리다이렉트 가능
    }
}



/*
 * @Controller
 * 
 * @RequestMapping("/portal/notice/remove")
 * 
 * @RequiredArgsConstructor public class PortalNoticeRemoveController {
 * 
 * private final PortalNoticeService service;
 * 
 * @PostMapping("/{noticeId}") public String removeProcess(
 * 
 * @PathVariable String noticeId, RedirectAttributes redirectAttributes
 * 
 * ) {
 * 
 * service.removePortalNotice(noticeId); String noticeTypeCd = "general";
 * redirectAttributes.addAttribute("noticeTypeCd", noticeTypeCd); return
 * "redirect:/portal/notice/{noticeTypeCd}"; // 리스트로 리다이렉트 } }
 */

