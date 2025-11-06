package kr.or.jsu.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.or.jsu.mybatis.mapper.AuthAccountMapper;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
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
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthAccountMapper mapper;
	
	@Autowired
	private ProfessorMapper profMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username은 'userNo'(학번/사번/교번)로 사용
		UsersVO realUser = mapper.selectUserForAuth(username);
		
		// 송태호가 야매로 넣음
		if(username.length() == 8) {
			
			var professor = profMapper.selectProfessor(username);
			String positCd = professor.getPrfPositCd();
			realUser.setHeadProf(positCd != null && "PRF_POSIT_HEAD".equals(positCd));
		}
		// 여기까지 송태호가 야매로 넣음.
		
		if (realUser == null) {
			throw new UsernameNotFoundException(String.format("%s 학번/사번/교번 해당되는 사용자 없음.", username));
		}

		return new CustomUserDetails(realUser);
	}

}
