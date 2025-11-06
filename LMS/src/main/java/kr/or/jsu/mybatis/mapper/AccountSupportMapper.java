package kr.or.jsu.mybatis.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	정태일	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface AccountSupportMapper {
    /**
     * 이름과 이메일로 사용자 아이디를 조회합니다.
     * @param paramMap 이름(name)과 이메일(email)을 포함하는 Map
     * @return 조회된 사용자 아이디 (String), 없으면 null
     */
    String findUserIdByNameAndEmail(Map<String, Object> paramMap);

    /**
     * 아이디와 이메일로 사용자 존재 여부를 확인합니다. (비밀번호 재설정 전 유효성 검사)
     * @param paramMap 아이디(username)와 이메일(email)을 포함하는 Map
     * @return 사용자가 존재하면 1, 없으면 0
     */
    int validateUserForPasswordReset(Map<String, Object> paramMap);

    /**
     * 사용자 비밀번호를 업데이트합니다.
     * @param paramMap 아이디(username)와 새로운 비밀번호(newPassword)를 포함하는 Map
     * @return 업데이트된 행의 수
     */
    int updatePassword(Map<String, Object> paramMap);
    
    /**
     * 로그인된 사용자 비밀번호를 업데이트.
     * @param userNo
     * @param encodedPassword
     * @return
     */
    int updatePasswordByUserNo(@Param("userNo") String userNo,
            @Param("encodedPassword") String encodedPassword);
}
