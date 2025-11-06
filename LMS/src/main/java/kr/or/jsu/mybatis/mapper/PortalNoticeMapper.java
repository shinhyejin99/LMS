package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.PortalNoticeVO;

@Mapper
public interface PortalNoticeMapper {

	// 공지 등록
	public int insertPortalNotice(PortalNoticeVO portalNotice);

	// 공지 총 건수 조회(검색조건 포함)
	public int selectTotalRecord(PaginationInfo<?> paging);
	
	// 공지 전체 목록 조회
	public List<PortalNoticeVO> selectPortalNoticeList(PaginationInfo<PortalNoticeVO> paging);

	// 특정 공지 조회 
	public PortalNoticeVO selectPortalNoticeDetail(String noticeId);

	// 공지 수정
	public int updatePortalNotice(PortalNoticeVO portalNotice);

	// 공지 삭제 
	public int deletePortalNotice(String noticeId);
	
	// 공지 조회수 증가
	public int incrementViewCount(String noticeId);

	// 긴급 공지 상태 변경 
	public int updatePortalNoticeUrgentStatus(@Param("noticeId") String noticeId,
			@Param("isUrgent") String isUrgent);
	
	// 대시보드용 긴급 공지 목록 조회
	public List<PortalNoticeVO> selectUrgentNoticeList();
}
