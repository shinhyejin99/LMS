package kr.or.jsu.portal.controller.univcalendar;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.portal.service.univcalendar.PortalUnivCalendarService;
import kr.or.jsu.vo.StaffVO; // Add this import
import kr.or.jsu.vo.UnivCalendarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Add this import

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
 *  2025.10. 17.     	정태일	          학사일정 데이터 조회 API 추가
 *  2025.10. 17.     	정태일	          학사일정 등록 API 추가
 *
 * </pre>
 */
@Slf4j // Add this annotation
@RestController
@RequestMapping("/portal/univcalendar")
@RequiredArgsConstructor
public class PortalUnivCalendarRestController {

    private final PortalUnivCalendarService portalUnivCalendarService;

    // 1. 학사일정 데이터 조회 API
    @GetMapping("/events")
    public List<Map<String, Object>> getCalendarEvents(
        @RequestParam(required = false) String termFilter,
        @RequestParam(required = false) String categoryFilter
    ) {
        List<UnivCalendarVO> calendarList = portalUnivCalendarService.readCalendarList(termFilter, categoryFilter);

        // FullCalendar 이벤트 형식으로 변환
        return calendarList.stream().map(event -> {
            Map<String, Object> eventMap = new java.util.HashMap<>();
            eventMap.put("id", event.getCalendarId());
            eventMap.put("title", event.getScheduleName());
            // 'allDay' 속성을 제거하고, start/end 포맷을 ISO_LOCAL_DATE_TIME으로 변경하여
            // FullCalendar가 시간 포함 여부에 따라 자동으로 종일/시간 이벤트를 구분하도록 합니다.
            boolean isAllDay = event.getStartAt().toLocalTime().equals(java.time.LocalTime.MIDNIGHT) &&
                               event.getEndAt().toLocalTime().equals(java.time.LocalTime.MIDNIGHT);

            if (isAllDay) {
                eventMap.put("start", event.getStartAt().format(DateTimeFormatter.ISO_LOCAL_DATE));
                // 종일 이벤트의 경우, FullCalendar의 end는 exclusive이므로 하루를 더해줍니다.
                eventMap.put("end", event.getEndAt().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
                eventMap.put("allDay", true);
            } else {
                eventMap.put("start", event.getStartAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                eventMap.put("end", event.getEndAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                eventMap.put("allDay", false);
            }

            // 색상 및 기타 속성은 SCHEDULE_CD에 따라 동적으로 설정 가능
            // 예시: SCHEDULE_CD에 따라 다른 색상 부여
            String color = "#007bff"; // 기본 색상
            if ("등록".equals(event.getScheduleCd())) {
                color = "#007bff"; // 파랑
            } else if ("시험".equals(event.getScheduleCd())) {
                color = "#ffc107"; // 노랑
            } else if ("방학".equals(event.getScheduleCd())) {
                color = "#28a745"; // 초록
            } else if ("행사".equals(event.getScheduleCd())) {
                color = "#6c757d"; // 회색
            } else if ("수강신청".equals(event.getScheduleCd())) { 
            	color = "#6f42c1"; // 보라 
            }
            
            
            eventMap.put("backgroundColor", color);
            eventMap.put("borderColor", color);
            
            // 추가 정보 (필요시)
            eventMap.put("scheduleCd", event.getScheduleCd());
            eventMap.put("yeartermCd", event.getYeartermCd());
            
            return eventMap;
        }).collect(Collectors.toList());
    }

    // 2. 학사일정 등록 API
    @PostMapping("/events")
    public Map<String, Object> createCalendarEvent(
        @RequestBody UnivCalendarVO univCalendarVO,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            if (userDetails == null || userDetails.getRealUser() == null || !(userDetails.getRealUser() instanceof StaffVO)) {
                response.put("success", false);
                response.put("message", "권한이 없습니다. 교직원만 일정을 등록할 수 있습니다.");
                return response;
            }
            
            String staffNo = ((StaffVO) userDetails.getRealUser()).getStaffNo();
            univCalendarVO.setStaffNo(staffNo); // 로그인한 직원의 STAFF_NO 설정

            int result = portalUnivCalendarService.createCalendarEvent(univCalendarVO);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "일정이 성공적으로 등록되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "일정 등록에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            log.warn("일정 등록 유효성 검사 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage()); // 서비스단에서 던진 구체적인 오류 메시지를 전달
        } catch (Exception e) {
            log.error("일정 등록 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
        }
        return response;
    }

    // 3. 학사일정 상세 조회 API
    @GetMapping("/events/{calendarId}")
    public UnivCalendarVO getCalendarEvent(@PathVariable String calendarId) {
        return portalUnivCalendarService.readCalendarEvent(calendarId);
    }

    // 4. 학사일정 수정 API
    @PutMapping("/events/{calendarId}")
    public Map<String, Object> updateCalendarEvent(
        @PathVariable String calendarId,
        @RequestBody UnivCalendarVO univCalendarVO,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            if (userDetails == null || userDetails.getRealUser() == null || !(userDetails.getRealUser() instanceof StaffVO)) {
                response.put("success", false);
                response.put("message", "권한이 없습니다. 교직원만 일정을 수정할 수 있습니다.");
                return response;
            }
            
            univCalendarVO.setCalendarId(calendarId); // URL의 ID를 VO에 설정
            int result = portalUnivCalendarService.modifyCalendarEvent(univCalendarVO);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "일정이 성공적으로 수정되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "일정 수정에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            log.warn("일정 수정 유효성 검사 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage()); // 서비스단에서 던진 구체적인 오류 메시지를 전달
        } catch (Exception e) {
            log.error("일정 수정 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
        }
        return response;
    }

    // 5. 학사일정 삭제 API
    @DeleteMapping("/events/{calendarId}")
    public Map<String, Object> deleteCalendarEvent(
        @PathVariable String calendarId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            if (userDetails == null || userDetails.getRealUser() == null || !(userDetails.getRealUser() instanceof StaffVO)) {
                response.put("success", false);
                response.put("message", "권한이 없습니다. 교직원만 일정을 삭제할 수 있습니다.");
                return response;
            }

            int result = portalUnivCalendarService.removeCalendarEvent(calendarId);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "일정이 성공적으로 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "일정 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("일정 삭제 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
        }
        return response;
    }
}
