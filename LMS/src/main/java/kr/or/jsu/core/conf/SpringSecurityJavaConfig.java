package kr.or.jsu.core.conf;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.jsu.core.security.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SpringSecurityJavaConfig {

	// 공용 화이트리스트
    private final String[] WHITELIST = {
        "/sneat-1.0.0/**",
        "/css/**",
        "/js/**",
        "/html/**",
        "/images/**", 
        "/error/**",
        "/devtemp/**",
        "/ai/**"  //
    };

    // 보안 체인을 거치지 않을 방법 화이트리스트로 나열
	private final DispatcherType[] DISPATCHERTYPE_WHITELIST = {
		DispatcherType.FORWARD, // 서버 안에서 포워딩하는건 인증/인가 따지지말기
		DispatcherType.INCLUDE, // 서버 안에서 인클루드한건 인증/인가 따지지말기
		DispatcherType.ERROR    // 에러 메시지 보내는거도 따지지 말기
	};

	@Autowired
	private CustomAuthenticationSuccessHandler successHandler;

	// =========================
    // 1) API 전용 체인 (JSON)
    // =========================
    @Bean
    @Order(1) // ← 먼저 검사/적용됨
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
            // 이 체인은 /classroom/api/** 에만 적용
            .securityMatcher("/classroom/api/**")

            // API는 보통 CSRF 비활성 + STATELESS 세션
            .csrf(csrf -> csrf.disable())
//            .csrf(csrf -> csrf
//        	    .ignoringRequestMatchers("/lms/ws-stomp/**") // 알림 소켓
//        	)
            // csrf는 그냥 전부 비활성으로 갑니다

            // API 권한 규칙
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .dispatcherTypeMatchers(DISPATCHERTYPE_WHITELIST).permitAll()
                // ↓ API 엔드포인트 권한
                .requestMatchers("/classroom/api/v1/student").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/classroom/api/v1/professor").hasAuthority("ROLE_PROFESSOR")
                .requestMatchers("/classroom/api/v1/common").hasAnyAuthority(new String[] {"ROLE_STUDENT", "ROLE_PROFESSOR"})
                .anyRequest().authenticated()
            )

            // API는 예외도 항상 JSON
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jsonEntryPoint())     // 401
                .accessDeniedHandler(jsonAccessDeniedHandler()) // 403
            )

            // 필요 시 Basic 허용(선택)
            .httpBasic(Customizer.withDefaults())

	       // X-Frame-Options 허용 추가 - pdf
	        .headers(headers -> headers
	            .frameOptions(frameOptions -> frameOptions.sameOrigin()) // 같은 도메인에서 iframe 허용
	        );

        return http.build();
    }


    // =========================
    // 2) 웹(뷰) 전용 체인 (HTML)
    // =========================
    @Bean
    @Order(2)
    SecurityFilterChain webChain(HttpSecurity http) throws Exception {
        http
            // 나머지 모든 요청(웹 화면)
            .securityMatcher(request -> true)

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DISPATCHERTYPE_WHITELIST).permitAll()
                .requestMatchers(WHITELIST).permitAll()
                .requestMatchers("/lms/ws-stomp/**").permitAll()
                .requestMatchers("/login",
                                 "/portal/user/resetpassword",
                                 "/portal/user/changepassword",
                                 "/portal/user/findid").permitAll()

                // 부분 공개
//                .requestMatchers("/dash4", "/dash4/**").authenticated()
                .requestMatchers("/portal", "/portal/**").authenticated()

                // 대시보드별 권한
                .requestMatchers("/student").hasAnyAuthority("ROLE_STUDENT", "ROLE_ADMIN")
                .requestMatchers("/professor").hasAnyAuthority("ROLE_PROFESSOR", "ROLE_ADMIN")
                .requestMatchers("/staff").hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")

                // 클래스룸별 권한
                .requestMatchers("/classroom/student").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/classroom/professor").hasAuthority("ROLE_PROFESSOR")

                // 학내채용정보 권한
                .requestMatchers("/portal/job/internal/write").hasAuthority("ROLE_STAFF")
                .requestMatchers("/portal/job/internal/modify/**").hasAuthority("ROLE_STAFF")
                .requestMatchers("/portal/job/internal/remove/**").hasAuthority("ROLD_STAFF")

                .anyRequest().authenticated()
            )

            // 폼 로그인
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .permitAll()
            )

            // X-Frame-Options 허용 추가 (웹 체인) - pdf
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	private AuthenticationEntryPoint jsonEntryPoint() {
        return (request, response, ex) ->
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다.");
    }

    private AccessDeniedHandler jsonAccessDeniedHandler() {
        return (request, response, ex) ->
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "권한이 없습니다.");
    }

    private void writeJson(
    	HttpServletResponse response
        , int status
        , String code
        , String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        var body = Map.of(
            "success", false,
            "code", code,
            "message", message,
            "timestamp", OffsetDateTime.now().toString()
        );
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}