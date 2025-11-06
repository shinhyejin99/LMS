package kr.or.jsu.portal.service.univcalendar;

import java.util.List;

import kr.or.jsu.vo.UnivCalendarVO;

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
 *  2025.10. 17.     	정태일	          조회 목록 추가
 *
 * </pre>
 */
public interface PortalUnivCalendarService {
	/**
	 * 학사일정 목록을 조회합니다.
	 * @return List<UnivCalendarVO> 학사일정 목록
	 */
	List<UnivCalendarVO> readCalendarList(String termFilter, String categoryFilter);
	
    /**
     * 학사일정 이벤트를 생성합니다.
     * @param univCalendarVO 생성할 학사일정 이벤트 정보
     * @return int 삽입된 행의 수
     */
	int createCalendarEvent(UnivCalendarVO univCalendarVO);
	
    /**
     * 학사일정 이벤트를 상세 조회합니다.
     * @param calendarId 조회할 학사일정 ID 
     * @return UnivCalendarVO 학사일정 상세 정보
     */
	UnivCalendarVO readCalendarEvent(String calendarId); 

    /**
     * 학사일정 이벤트를 수정합니다.
     * @param univCalendarVO 수정할 학사일정 정보
     * @return int 수정된 행의 수
     */
    int modifyCalendarEvent(UnivCalendarVO univCalendarVO);

    /**
     * 학사일정 이벤트를 삭제합니다.
     * @param calendarId 삭제할 학사일정 ID
     * @return int 삭제된 행의 수
     */
    int removeCalendarEvent(String calendarId);
}
