package kr.or.jsu.core.security.utils;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthorizeCheckUtils {
	public static boolean checkAuthorize(Authentication authentication, String role) {
		if(authentication==null) return false;
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority(role));		
	}
}
