package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.PortalRecruitVO;

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
 *  2025. 9. 26.     	김수현	        학내 채용정보 메소드 추가
 *	2025. 9. 27.		김수현			수정 메소드 추가
 *	2025. 9. 28.		김수현			조회수 증가 추가, 조회 메소드 수정(페이징, 검색)
 * </pre>
 */
@Mapper
public interface PortalRecruitMapper {
	
	
	/**
	 * 학내 채용정보 등록(교직원만)
	 * @param portalRecruit
	 * @return
	 */	
	public int insertSchRecruit(PortalRecruitVO portalRecruit);
	
	/**
	 * 채용정보 총 건수 조회 (검색 조건 포함)
	 * @param paging 페이징 및 검색 조건이 담긴 객체
	 * @return 총 레코드 수
	 */
	public int selectTotalRecord(PaginationInfo<?> paging);
	
	/**
	 * 학내 채용정보 조회(모두)
	 * @return List
	 */
	public List<PortalRecruitVO> selectSchRecruitList(PaginationInfo<PortalRecruitVO> paging);
	

	/**
	 * 학내 채용정보 상세보기(모두)
	 * @param recruitId
	 * @return portalRecruitVO
	 */
	public PortalRecruitVO selectSchRecruitDetail(String recruitId);
	
	/**
	 * 학내 채용정보 수정
	 * @param portalRecruit
	 * @return int
	 */
	public int updateSchRecruit(PortalRecruitVO portalRecruit);
	
	/**
	 * 학내 채용정보 삭제
	 * @param recruitId
	 * @return
	 */
	public int deleteSchRecruit(String recruitId);
	
	/**
	 * 채용정보 조회수 증가
	 * @param recruitId
	 * @return
	 */
	public int incrementViewCount(String recruitId);
}
