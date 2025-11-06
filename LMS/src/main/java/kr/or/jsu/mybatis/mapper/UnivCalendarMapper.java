package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.UnivCalendarVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	정태일	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface UnivCalendarMapper {

	/**
	 * 학사일정 목록을 조회합니다.
	 * @return List<UnivCalendarVO> 학사일정 목록
	 */
	List<UnivCalendarVO> selectCalendarList(
			@Param("termFilter") String termFilter,	
			@Param("categoryFilter") String categoryFilter);
 
	/**
	 * 학사일정 이벤트를 생성
	 * @param univCalendarVO 생성할 학사일정 이벤트 정보
	 * @return int 삽입된 행의 수
	 */
	int insertCalendarEvent(UnivCalendarVO univCalendarVO);
	
	/**
	 * 학사일정 상세 조회
	 * @param id 조회할 학사일정 ID
	 * @return 학사일정 상세 정보
	 */
	UnivCalendarVO selectCalendarEvent(@Param("calendarId") String calendarId);

	/**
	 * 학사일정 수정
	 * @param univCalendarVO 수정할 학사일정 정보
	 * @return 수정 성공 시 1, 실패 시 0
	 */
	int updateCalendarEvent(UnivCalendarVO univCalendarVO);

	/**
	 * 학사일정 삭제 (논리적)
	 * @param calendarId 삭제할 학사일정 ID 
	 * @return 삭제 성공 시 1, 실패 시 0
	 */
	int deleteCalendarEvent(@Param("calendarId") String calendarId);
	
	
	
	
}
