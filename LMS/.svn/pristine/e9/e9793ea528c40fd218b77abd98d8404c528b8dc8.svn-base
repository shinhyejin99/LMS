package kr.or.jsu.portal.service.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.mybatis.mapper.PortalNoticeMapper;
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
 *  2025.10. 23.     	정태일	          긴급 기능 추가
 *
 * </pre>
 */
@Service
public class PortalNoticeServiceImpl implements PortalNoticeService {

	  @Autowired
	    private PortalNoticeMapper mapper;

	    // 등록 (C)
	    @Override
	    public int createPortalNotice(PortalNoticeVO portalNotice) {
	        return mapper.insertPortalNotice(portalNotice);
	    }

	    // 목록 조회 (R - List)
	    @Override
	    public List<PortalNoticeVO> readPortalNoticeList(PaginationInfo<PortalNoticeVO> paging) {
	    	int totalRecord = mapper.selectTotalRecord(paging); // ✅ 총건수 조회 (selectTotalRecord -> selectPortalNoticeListCount)
	        paging.setTotalRecord(totalRecord);                 // ✅ 페이징 메타 세팅
	        List<PortalNoticeVO> portalNoticeList = mapper.selectPortalNoticeList(paging);
	        return portalNoticeList;       // ✅ 목록 조회
	    }
	    
	    // 단건 조회 (R - Detail)
	    @Override
	    public PortalNoticeVO readPortalNoticeDetail(String noticeId) {
	        return mapper.selectPortalNoticeDetail(noticeId);
	    }

	    // 수정 (U)
	    @Override
	    public int modifyPortalNotice(PortalNoticeVO portalNotice) {
	        return mapper.updatePortalNotice(portalNotice);
	    }

	    // 삭제 (D)
	    @Override
	    public int removePortalNotice(String noticeId) {
	        return mapper.deletePortalNotice(noticeId);
	    }
	    
	    // 조회수
		@Override
		public int modifyIncrementViewCount(String noticeId) {
			return mapper.incrementViewCount(noticeId);
		}
		
		// 긴급 공지상태 변경
		@Override
		public int modifyPortalNoticeUrgentStatus(String noticeId, String isUrgent) {
			return mapper.updatePortalNoticeUrgentStatus(noticeId, isUrgent);
		}

		// 긴급 공지상태 조회
		@Override
		public List<PortalNoticeVO> readUrgentNoticeList() {
			return mapper.selectUrgentNoticeList(); 
		}
		
		
		
}