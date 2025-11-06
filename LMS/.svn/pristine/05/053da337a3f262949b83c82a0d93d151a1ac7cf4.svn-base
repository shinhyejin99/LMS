package kr.or.jsu.classroom.common.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

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
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/classroom")
public class ClassroomCommonController {
	
	@GetMapping(value = {"", "/main"})
	public String main(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		String userType = loginUser.getRealUser().getUserType();
		log.info("현재 권한 : {}", userType);
		switch(userType) {
		case "ROLE_PROFESSOR" : return "redirect:/classroom/professor";
		case "ROLE_STUDENT" : return "redirect:/classroom/student/dashboard";
		default : return "redirect:/login";
		}
	}
	
    @GetMapping("/**")
    public String index() {
        return "forward:/classroom.html";
    }
}
