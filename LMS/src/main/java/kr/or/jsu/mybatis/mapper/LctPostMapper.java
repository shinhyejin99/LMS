package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.LctPostInfo;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 4.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      	수정자            수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.04     	송태호	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface LctPostMapper {
	
	/**
	 * 특정 강의 게시판에 글을 작성합니다.
	 * 
	 * @param newPost 작성할 게시글 정보
	 * @return 작성한 레코드 수
	 */
	public int insertLctPost(LctPostInfo newPost);
	
	/**
	 * 교수가 요청한 강의의 게시글 목록을 불러옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 게시글 레코드 리스트
	 */
	public List<LctPostInfo> selectPostListForPrf(
		String lectureId
		, String postType
	);
		
	/**
	 * 요청한 게시글의 전체 내용을 가져옵니다.
	 * 
	 * @param lctPostId 게시글ID
	 * @return 게시글 상세정보
	 */
	public LctPostInfo selectPost(
		String lctPostId
	);
	
	/**
	 * 강의의 게시글 하나를 삭제 상태로 변경합니다.
	 * 
	 * @param lctPostId
	 * @return 변경된 행 수. 성공 시 1, 변경된 데이터가 없으면 0 반환 
	 */
	public int deleteLctPostSoft(
		String lctPostId
	);
	
	/**
	 * 강의의 게시글 하나의 내용을 변경합니다.
	 * 
	 * @param changed
	 * @return 변경된 레코드 수. 변경된 데이터가 없으면 0.
	 */
	public int updateLctPost(
		LctPostInfo changed
	);
}
