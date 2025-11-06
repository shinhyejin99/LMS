package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.ai.dto.CollegeUnvieDeptInfoDTO;
import kr.or.jsu.vo.CollegeVO;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성
 *	2025.10.26			신혜진			쳇봇) 단과대 전체 조회
 * </pre>
 */
@Mapper
public interface CollegeMapper {
	
	// @Deprecated 붙어있는건 쓸 사람이 직접 주석 해제하고 쓰세요
	@Deprecated
	public int insertCollege(CollegeVO college);
	@Deprecated
	public List<CollegeVO> selectCollegeList();
	@Deprecated
	public int updateCollege(CollegeVO college);
	@Deprecated
	public int deleteCollege(String collegeCd);
	//단과대 전체조회
	public List<CollegeUnvieDeptInfoDTO> selectCollegesList();
	// 단과대 상세조회
	public CollegeUnvieDeptInfoDTO selectCollegeInfoByName(String collegeKeyword);
}
