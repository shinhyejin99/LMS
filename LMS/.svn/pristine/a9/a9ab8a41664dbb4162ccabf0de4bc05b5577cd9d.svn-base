package kr.or.jsu.core.common.service;

import java.util.Map;

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
 *  2025.10. 10.     	정태일	          ID/PW 찾기 기능 추가
 *
 * </pre>
 */
public interface AccountSupportService {
    /**
     * 이름과 이메일로 사용자 아이디를 찾습니다.
     * @param paramMap 이름과 이메일 정보를 담은 Map (name, email)
     * @return 찾은 아이디 (String) 또는 null
     */
    String findUserId(Map<String, Object> paramMap);

    /**
     * 아이디와 이메일로 비밀번호 재설정을 위한 유효성 검사를 수행합니다.
     * @param paramMap 아이디와 이메일 정보를 담은 Map (username, email)
     * @return 유효성 검사 성공 여부 (true/false)
     */
    boolean validateUserForPasswordReset(Map<String, Object> paramMap);

    /**
     * 사용자 비밀번호를 재설정합니다.
     * @param paramMap 아이디와 새로운 비밀번호 정보를 담은 Map (username, newPassword) (비밀번호는 암호화되어 전달되어야 함)
     * @return 비밀번호 재설정 성공 여부 (true/false)
     */
    boolean resetPassword(Map<String, Object> paramMap);
    
    
    
    /**
     * 사용자 비밀번호 변경 (로그인했을 때 비밀번호 변경)
     * @param userNo
     * @param rawNewPassword
     * @return
     */
    boolean modifyPasswordByUserNo(String userNo, String rawNewPassword);
}
