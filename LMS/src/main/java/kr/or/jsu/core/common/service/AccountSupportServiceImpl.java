package kr.or.jsu.core.common.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.AccountSupportMapper;

//@Service
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
@Service
public class AccountSupportServiceImpl implements AccountSupportService {

    @Autowired
    private AccountSupportMapper accountSupportMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
	
	@Override
	public String findUserId(Map<String, Object> paramMap) {
		return accountSupportMapper.findUserIdByNameAndEmail(paramMap);
	}

	@Override
	public boolean validateUserForPasswordReset(Map<String, Object> paramMap) {
		return accountSupportMapper.validateUserForPasswordReset(paramMap) > 0;
	}

	@Override
	public boolean resetPassword(Map<String, Object> paramMap) {
        String newPassword = (String) paramMap.get("newPassword");
        if (newPassword != null) {
            paramMap.put("newPassword", passwordEncoder.encode(newPassword));
        }
        return accountSupportMapper.updatePassword(paramMap) > 0;
	}

	@Override
	@Transactional
	public boolean modifyPasswordByUserNo(String userNo, String rawNewPassword) {
	    String encoded = passwordEncoder.encode(rawNewPassword);
	    int updated = accountSupportMapper.updatePasswordByUserNo(userNo, encoded);
	    return updated == 1;
	}

}
