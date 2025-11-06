package kr.or.jsu.portal.service.notice;

import java.util.List;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.PortalNoticeVO;
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
 *  2025.10. 23.     	정태일	          긴급 공지 기능 추가
 *
 * </pre>
 */
public interface PortalNoticeService {

	// 등록 (C: Create)
    /**
     * 새로운 공지사항을 등록합니다.
     * @param portalNotice 등록할 공지사항 VO 객체
     * @return 등록된 레코드 수 (성공 시 1)
     */
    public int createPortalNotice(PortalNoticeVO portalNotice);

    // 목록 조회 (R: Read - List)
    /**
     * 공지사항 전체 목록을 조회합니다.
     * @return 공지사항 목록
     */
    public List<PortalNoticeVO> readPortalNoticeList(PaginationInfo<PortalNoticeVO> paging);

    // 단건 조회 (R: Read - Detail)
    /**
     * 특정 공지사항 ID로 상세 정보를 조회합니다.
     * @param noticeId 공지사항 ID
     * @return 공지사항 VO 객체
     */
    public PortalNoticeVO readPortalNoticeDetail(String noticeId);

    // 수정 (U: Update)
    /**
     * 공지사항 정보를 수정합니다.
     * @param portalNotice 수정할 공지사항 VO 객체
     * @return 수정된 레코드 수 (성공 시 1)
     */
    public int modifyPortalNotice(PortalNoticeVO portalNotice);

    // 삭제 (D: Delete)
    /**
     * 특정 공지사항을 삭제 처리(DELETE_YN='Y')합니다.
     * @param noticeId 공지사항 ID
     * @return 삭제 처리된 레코드 수 (성공 시 1)
     */
    public int removePortalNotice(String noticeId);
    
        /**
     * 공지사항 조회수 증가
     * @param noticeId
     * @return
     */
    public int modifyIncrementViewCount(String noticeId);
    
    /**
     * 긴급 공지 상태를 변경합니다.
     * @param noticeId 공지사항 ID
     * @param isUrgent 변경할 상태 ('Y' 또는 'N')
     * @return 수정된 레코드 수 (성공 시 1)
     */
    public int modifyPortalNoticeUrgentStatus(String noticeId, String isUrgent);
    
    
    /**
     * 대시보드에 표시할 긴급 공지 목록을 조회합니다.
     * @return 긴급 공지 목록 (최대 5개)
     */
    public List<PortalNoticeVO> readUrgentNoticeList(); 
    
    
}