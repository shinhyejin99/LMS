package kr.or.jsu.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.UsersVO;

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
 *
 * </pre>
 */
@Mapper
public interface AuthAccountMapper {
	/**
	 * 로그인 할때만 사용함
	 * 
	 * @param userNo(username)
	 * @return
	 */
	UsersVO selectUserForAuth(@Param("userNo") String userNo);
}
