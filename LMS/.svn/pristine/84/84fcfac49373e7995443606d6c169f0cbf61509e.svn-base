package kr.or.jsu.portal.controller.univcalendar;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.portal.service.univcalendar.PortalUnivCalendarService;
import kr.or.jsu.vo.UnivCalendarVO;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *  2025.10. 14.     	정태일	          네이밍 수정
 *
 * </pre>
 */
@Controller
@RequestMapping("/portal/univcalendar")
@RequiredArgsConstructor
public class PortalUnivCalendarReadController {

    private final PortalUnivCalendarService portalUnivCalendarService;

	
    // 1. 일정 메인 페이지
    @GetMapping
    public String univCalendarPage() {
        return "portal/portalUnivCalendar";
    }
    
    // 2. 전체 일정 목록 페이지
    @GetMapping("/list")
    public String calendarListPage(Model model) {
        List<UnivCalendarVO> calendarList = portalUnivCalendarService.readCalendarList(null, null);
        
        // JSTL fmt:formatDate가 LocalDateTime을 직접 지원하지 않으므로 java.util.Date로 변환합니다.
        List<java.util.Map<String, Object>> eventListForView = calendarList.stream().map(event -> {
            java.util.Map<String, Object> eventMap = new java.util.HashMap<>();
            eventMap.put("yeartermCd", event.getYeartermCd());
            eventMap.put("scheduleCd", event.getScheduleCd());
            eventMap.put("scheduleName", event.getScheduleName());
            // LocalDateTime -> Date 변환
            eventMap.put("startAt", java.util.Date.from(event.getStartAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            eventMap.put("endAt", java.util.Date.from(event.getEndAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));
            return eventMap;
        }).collect(Collectors.toList()); 

        model.addAttribute("calendarList", eventListForView);
        return "portal/portalUnivCalendarList";
    }
    
    
}
