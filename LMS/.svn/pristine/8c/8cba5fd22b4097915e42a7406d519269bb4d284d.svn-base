package kr.or.jsu.core.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import kr.or.jsu.vo.UsersVO;
import lombok.ToString;

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
@ToString(callSuper = true)
public class CustomUserDetails extends User implements RealUserWrapper {
	private static final long serialVersionUID = 1L;
	
	private final UsersVO realUser;

	public CustomUserDetails(UsersVO realUser, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked) {
		super(realUser.getUserNo(), realUser.getPwHash(), enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, AuthorityUtils.createAuthorityList(realUser.getUserType()));
		this.realUser = realUser;
	}

	public CustomUserDetails(UsersVO realUser) {
		this(realUser, true, true, true, true);
	}

	@Override
	public UsersVO getRealUser() {
		return realUser;
	}
	

}
