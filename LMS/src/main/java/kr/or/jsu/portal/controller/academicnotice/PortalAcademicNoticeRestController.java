package kr.or.jsu.portal.controller.academicnotice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;


/**
 * 
 * @author 정태일
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	정태일	          최초 생성
 *
 * </pre>
 */
@RestController
@RequestMapping("/rest/portal/academicnotice")
@RequiredArgsConstructor
public class PortalAcademicNoticeRestController {

    private final PortalNoticeService service;

    /** 학사 공지사항 목록 JSON */
    @GetMapping("/list")
    public Map<String,Object> getAcademicNoticeList(
        @ModelAttribute PaginationInfo<PortalNoticeVO> paging
    ) {
        paging.setNoticeTypeCd("ACADEMIC"); // 학사 공지사항으로 고정
        List<PortalNoticeVO> list = service.readPortalNoticeList(paging);

        Map<String,Object> result = new HashMap<>();
        result.put("success", true);
        result.put("list", list);
        result.put("totalCount", paging.getTotalRecord());
        
        result.put("paging", Map.of(
                "totalPage",   paging.getTotalPage(),
                "currentPage", paging.getCurrentPage(),
                "startPage",   paging.getStartPage(),
                "endPage",     paging.getEndPage()
            ));
        
        return result;
    }
    
    /** 학사 공지사항 상세 JSON */
    @GetMapping("/detail/{noticeId}")
    public Map<String,Object> getAcademicNoticeDetail(@PathVariable String noticeId) {
        PortalNoticeVO detail = service.readPortalNoticeDetail(noticeId);
        
        Map<String,Object> result = new HashMap<>();
        if (detail != null) {
            result.put("success", true);
            result.put("data", detail);
        } else {
            result.put("success", false);
            result.put("message", "존재하지 않는 학사 공지입니다.");
        }
        return result;
    }
}
