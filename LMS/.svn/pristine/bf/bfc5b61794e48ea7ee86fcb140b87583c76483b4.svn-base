package kr.or.jsu.portal.service.univcalendar;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.mybatis.mapper.UnivCalendarMapper;
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
 *  2025.10. 17.        정태일              학사일정 목록 조회 기능 
 *  2025.10. 18.        정태일              학사일정 시간 유효성 검사 추가 
 *
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class PortalUnivCalendarServiceImpl implements PortalUnivCalendarService {

    private final UnivCalendarMapper univCalendarMapper;

    /**
     * 학사일정 목록을 조회합니다.
     * @return List<UnivCalendarVO> 학사일정 목록
     */
    @Override
    public List<UnivCalendarVO> readCalendarList(String termFilter, String categoryFilter) {
        return univCalendarMapper.selectCalendarList(termFilter, categoryFilter);
    }

    /**
     * 학사일정 이벤트를 생성합니다.
     * @param univCalendarVO 생성할 학사일정 이벤트 정보
     * @param staffNo 일정을 등록하는 직원의 사번
     * @return int 삽입된 행의 수
     */
    @Override
    public int createCalendarEvent(UnivCalendarVO univCalendarVO) {
        // 시작일시가 종료일시보다 늦을 수 없도록 유효성 검사
        if (univCalendarVO.getStartAt().isAfter(univCalendarVO.getEndAt())) {
            throw new IllegalArgumentException("종료일시는 시작일시보다 빠를 수 없습니다.");
        }

        return univCalendarMapper.insertCalendarEvent(univCalendarVO);
    }

    /**
     * 학사일정 이벤트를 상세 조회합니다.
     * @param calendarId 조회할 학사일정 ID
     * @return UnivCalendarVO 학사일정 상세 정보
     */
	@Override
	public UnivCalendarVO readCalendarEvent(String calendarId) {
		return univCalendarMapper.selectCalendarEvent(calendarId);
	}

    /**
     * 학사일정 이벤트를 수정합니다.
     * @param univCalendarVO 수정할 학사일정 정보
     * @return int 수정된 행의 수
     */
    @Override
	public int modifyCalendarEvent(UnivCalendarVO univCalendarVO) {
        // 시작일시가 종료일시보다 늦을 수 없도록 유효성 검사
        if (univCalendarVO.getStartAt().isAfter(univCalendarVO.getEndAt())) {
            throw new IllegalArgumentException("종료일시는 시작일시보다 빠를 수 없습니다.");
        }
		return univCalendarMapper.updateCalendarEvent(univCalendarVO);
	}

    /**
     * 학사일정 이벤트를 삭제합니다.
     * @param calendarId 삭제할 학사일정 ID
     * @return int 삭제된 행의 수
     */
	@Override
	public int removeCalendarEvent(String calendarId) {
		return univCalendarMapper.deleteCalendarEvent(calendarId);
	}
}
