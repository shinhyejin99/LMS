package kr.or.jsu.core.security.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.mybatis.mapper.LoginHistoryMapper;
import kr.or.jsu.vo.LoginHistoryVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	정태일	          최초 생성
 *  2025. 10. 14.     	정태일	          로그인 기록 추가
 *
 * </pre>
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private LoginHistoryMapper loginHistoryMapper;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
//		super.onAuthenticationSuccess(request, response, authentication);
	
		try {
			Object principal = authentication.getPrincipal();
			if (principal instanceof CustomUserDetails) {
				UsersVO user = ((CustomUserDetails) principal).getRealUser();
				String userId = user.getUserId();
				String clientIp = request.getRemoteAddr();

				LoginHistoryVO history = new LoginHistoryVO();
				history.setUserId(userId);
				history.setTryIp(clientIp);
				history.setSuccessYn("Y");
				history.setTryAt(LocalDateTime.now());

				loginHistoryMapper.insertLoginHistory(history);
			}
		} catch (Exception e) {
			log.error("로그인 이력 저장 실패", e);
		}
		
		// ROLE_STUDENT
		if (authentication.getAuthorities().stream().anyMatch(at -> at.getAuthority().equals("ROLE_STUDENT"))) {
			getRedirectStrategy().sendRedirect(request, response, "/portal");
									
		 // ROLE_PROFESSOR
		} else if (authentication.getAuthorities().stream().anyMatch(at -> at.getAuthority().equals("ROLE_PROFESSOR"))) {
			getRedirectStrategy().sendRedirect(request, response, "/portal");

		// ROLE_STAFF
		} else if (authentication.getAuthorities().stream().anyMatch(at -> at.getAuthority().equals("ROLE_STAFF"))) {
			getRedirectStrategy().sendRedirect(request, response, "/portal");
			
		// ROLE_STAFF
		} else if (authentication.getAuthorities().stream().anyMatch(at -> at.getAuthority().equals("ROLE_ADMIN"))) {
			getRedirectStrategy().sendRedirect(request, response, "/portal");

			
		} else {
			throw new ServletException("로그인 하면 안되는 아이디 접근");
		}
	}

}
