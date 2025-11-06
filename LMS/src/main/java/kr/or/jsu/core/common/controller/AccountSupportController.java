package kr.or.jsu.core.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.common.service.AccountSupportService;
import kr.or.jsu.core.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountSupportController {

    @Autowired
    private AccountSupportService accountSupportService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

	// 아이디 찾기
	@GetMapping("/portal/user/findid")
	public String findIdPage() {
		return "portal/portalFindId";
	}

	// 아이디 찾기 처리
	@PostMapping("/portal/user/findid")
	public String findIdProcess(
			@RequestParam("name") String name
		  , @RequestParam("email") String email
		  , Model model
		) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", name);
		paramMap.put("email", email);
		
		String foundId = accountSupportService.findUserId(paramMap);
		
		if (foundId != null) {
			model.addAttribute("foundId", foundId);
			model.addAttribute("message", "아이디를 찾았습니다.");
		} else {
			model.addAttribute("error", "입력하신 정보와 일치하는 아이디가 없습니다.");
		}
		return "portal/portalFindIdResult";
	}
	
	// 패스워드 찾기
	@GetMapping("/portal/user/resetpassword")
	public String resetPasswordPage() {
		return "portal/portalResetPassword";
	}
	
	// 패스워드 재설정 처리 (1단계: 사용자 유효성 검사)
	@PostMapping("/portal/user/resetpassword")
	public String resetPasswordValidateProcess(
			@RequestParam("username") String username, 
			@RequestParam("email") String email, 
			RedirectAttributes redirectAttributes
		) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("username", username);
		paramMap.put("email", email);
		
		boolean isValidUser = accountSupportService.validateUserForPasswordReset(paramMap);
		
		if (isValidUser) {
			redirectAttributes.addFlashAttribute("username", username); 
			redirectAttributes.addFlashAttribute("email", email);
			log.info("사용자 유효. /portal/user/changepassword 로 리다이렉트합니다.");
			return "redirect:/portal/user/changepassword";
		} else {
			redirectAttributes.addFlashAttribute("error", "입력하신 정보와 일치하는 사용자가 없습니다.");
			log.warn("사용자 유효하지 않음. /portal/user/resetpassword 로 리다이렉트합니다.");
			return "redirect:/portal/user/resetpassword";
		}
	}
	
	// 비밀번호 변경 페이지 (유효성 검사 후 접근)
	@GetMapping("/portal/user/changepassword")
	public String changePasswordPage(Model model) {
		if (!model.containsAttribute("username") || !model.containsAttribute("email")) {
			return "redirect:/portal/user/resetpassword";
		}
		return "portal/portalChangePassword";
	}

	// 비밀번호 변경 처리 (2단계: 새 비밀번호 설정)
	@PostMapping("/portal/user/changepassword")
	public String changePasswordProcess(
			@RequestParam("username") String username, 
			@RequestParam("email") String email, 
			@RequestParam("newPassword") String newPassword, 
			RedirectAttributes redirectAttributes
		) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("username", username);
		paramMap.put("email", email);
		paramMap.put("newPassword", newPassword);
		
		boolean isReset = accountSupportService.resetPassword(paramMap);
		
		if (isReset) {
			redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 재설정되었습니다. 새 비밀번호로 로그인해주세요.");
			return "redirect:/login";
		} else {

			redirectAttributes.addFlashAttribute("error", "비밀번호 재설정에 실패했습니다.");

			return "redirect:/portal/user/changepassword";

		}

	}

    /**
     * 로그인된 사용자의 비밀번호 변경 요청 (AJAX용)
     * 
     * 현재는 CSRF 비활성화 상태.
     * 추후 Spring Security에서 CSRF 활성화 시, JS에서 토큰 헤더만 추가해주면 그대로 작동.
     */
    @PostMapping("/portal/user/ajaxchangepassword")
    public ResponseEntity<Map<String, Object>> ajaxChangePasswordProcess(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
        Map<String, Object> res = new HashMap<>();

        // 1) 현재 비번 일치 확인
        UserDetails dbUser = userDetailsService.loadUserByUsername(loginUser.getUsername());
        String stored = dbUser.getPassword();
        String normalized = (stored != null && !stored.startsWith("{")) ? "{bcrypt}" + stored : stored;

        boolean matches;
        try {
            matches = passwordEncoder.matches(currentPassword, normalized);
        } catch (IllegalArgumentException e) {
            matches = stored != null && stored.startsWith("$2")
                    && new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                       .matches(currentPassword, stored);
        }
        if (!matches) {
            res.put("success", false);
            res.put("message", "현재 비밀번호가 일치하지 않습니다.");
            // 비번 불일치는 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        // 2) 로그인 사용자의 USER_ID로 직접 변경 (이메일 사용 X)
//        boolean ok = accountSupportService.modifyPasswordByUserId(loginUser.getUserId(), newPassword);
        boolean ok = accountSupportService.modifyPasswordByUserNo(loginUser.getUsername(), newPassword);
        if (ok) {
            res.put("success", true);
            res.put("message", "비밀번호가 성공적으로 변경되었습니다. 보안을 위해 다시 로그인 해주세요.");
            return ResponseEntity.ok(res);
        } else {
            // DB 갱신 실패는 500 유지 (원인 로그 확인 용도)
            res.put("success", false);
            res.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
}
