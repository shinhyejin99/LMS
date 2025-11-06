package kr.or.jsu.portal.controller.notice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 23.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 09. 29.     	정태일	          최초 생성
 *  2025. 10. 23.     	정태일	          긴급 공지 추가
 *
 * </pre>
 */
@RestController
@RequestMapping("/rest/portal/notice")
@RequiredArgsConstructor
public class PortalNoticeRestController {

    private final PortalNoticeService service;
    
    

    /** 공지사항 목록 JSON */
//    @GetMapping("/list/{type}")
    @GetMapping("/list")
    public Map<String,Object> getNoticeList(
//        @PathVariable("type") String type,
        @ModelAttribute PaginationInfo<PortalNoticeVO> paging
    ) {
//        paging.setNoticeTypeCd(type); // GENERAL / ACADEMIC
    	paging.setNoticeTypeCd("GENERAL");
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

    /** 공지사항 상세 JSON */
    @GetMapping("/detail/{noticeId}")
    public Map<String,Object> getNoticeDetail(@PathVariable String noticeId) {
        PortalNoticeVO detail = service.readPortalNoticeDetail(noticeId);
        
        Map<String,Object> result = new HashMap<>();
        if (detail != null) {
            result.put("success", true);
            result.put("detail", detail);
        } else {
            result.put("success", false);
            result.put("message", "존재하지 않는 공지입니다.");
        }
        return result;
    }
//    조회수
//    @PostMapping("/{noticeTypeCd}/{noticeId}/view-count")
    @PostMapping("/{noticeId}/view-count")
    public Map<String, Object> incrementNoticeViewCount(
//    		@PathVariable String noticeTypeCd,
    		@PathVariable String noticeId
	) {
    	Map<String, Object> result = new HashMap<>();
//    	if (!List.of("GENERAL", "ACADEMIC").contains(noticeTypeCd)) {
//    		result.put("success", false);
//    		result.put("message", "존재하지 않는 공지분류 타입입니다.");
//    		return result;
//    	}
    	// GENERAL 타입만 허용
//    	String noticeTypeCd = "GENERAL";    	
    	
    	try {
    		service.modifyIncrementViewCount(noticeId);
    		
    		result.put("success", true);
    		result.put("message", "조회수가 성공적으로 증가");
    		
    	} catch (Exception e) {
    		// 예외처리
    		System.err.println("조회수 증가 중 오류 발생 : " + e.getMessage());
    		result.put("success", false);
    		result.put("message", "조회수 증가 처리중 오류 발생");
    	}
    	return result;
    }
    
    /**
     * 특정 공지사항의 긴급 상태를 변경합니다. (Y/N 토글)
     * @param noticeId 공지사항 ID
     * @param payload 요청 본문, {"isUrgent": "Y"} 또는 {"isUrgent": "N"} 형태
     * @return 처리 결과 (성공/실패 메시지)
     */
    @PatchMapping("/{noticeId}/urgent")
    public Map<String, Object> updateUrgentStatus(
            @PathVariable String noticeId,
            @RequestBody Map<String, String> payload
    ) {
        Map<String, Object> result = new HashMap<>();
        String isUrgent = payload.get("isUrgent");

        if (!"Y".equals(isUrgent) && !"N".equals(isUrgent)) {
            result.put("success", false);
            result.put("message", "isUrgent 값은 'Y' 또는 'N' 이어야 합니다.");
            return result;
        }

        try {
            int updatedRows = service.modifyPortalNoticeUrgentStatus(noticeId, isUrgent);
            if (updatedRows > 0) {
                result.put("success", true);
                result.put("message", "긴급 상태가 성공적으로 변경되었습니다.");
            } else {
                result.put("success", false);
                result.put("message", "공지를 찾을 수 없거나 상태 변경에 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "처리 중 오류가 발생했습니다.");
            // log the exception e
        }
        return result;
    }
    
    
}
