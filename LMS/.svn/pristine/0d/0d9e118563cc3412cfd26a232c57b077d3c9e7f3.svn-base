package kr.or.jsu.portal.service.academicnotice;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.mybatis.mapper.PortalNoticeMapper;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 26.     	정태일	          최초 생성
 *  2025.10. 14.     	정태일	          조회 목록
 *
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class PortalAcademicNoticeServiceImpl implements PortalAcademicNoticeService {

	private final PortalNoticeMapper noticeMapper;
	
	@Override
	public List<PortalNoticeVO> readPortalAcademicNoticeList(PaginationInfo<PortalNoticeVO> paging) {
        paging.setNoticeTypeCd("ACADEMIC");
        int totalRecord = noticeMapper.selectTotalRecord(paging);
        paging.setTotalRecord(totalRecord);
        return noticeMapper.selectPortalNoticeList(paging);
	}

}
