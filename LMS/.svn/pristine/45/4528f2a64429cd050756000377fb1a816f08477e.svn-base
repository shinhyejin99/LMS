package kr.or.jsu.core.conf;
import org.springframework.messaging.converter.MessageConverter;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
import kr.or.jsu.core.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 송태호
 * @since 2025. 10. 23.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 23.     	김수현	        WebSocket Handshake 시 세션 정보 전달 관련 추가
 *
 * </pre>
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트 연결용 엔드포인트
		registry.addEndpoint("/lms/ws-stomp")
			.setAllowedOriginPatterns("*")
			.addInterceptors(new HttpSessionHandshakeInterceptor())  // 인터셉터 추가
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue", "/topic");
	    registry.setApplicationDestinationPrefixes("/app");
	    // 🚨 이게 정확해야 convertAndSendToUser가 /user/queue/...로 라우팅됨
	    registry.setUserDestinationPrefix("/user");
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
	    // 기본으로 제공되는 JSON 메시지 컨버터를 추가
	    messageConverters.add(new MappingJackson2MessageConverter());

	    // 다른 컨버터(예: StringMessageConverter)를 제거하지 않고 추가만 하는 것이 일반적입니다.
	    return true; // true를 반환하여 기본 컨버터는 유지합니다.
	}

	/**
     * WebSocket Handshake 시 세션 정보 전달
     */
    private static class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Map<String, Object> attributes) throws Exception {

            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                HttpSession session = servletRequest.getServletRequest().getSession(false);

                if (session != null) {
                    // HTTP 세션 정보를 WebSocket 세션으로 전달

                    // SecurityContext에서 가져오기
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated()) {
                        Object principal = auth.getPrincipal();

                        if (principal instanceof CustomUserDetails) {
                            CustomUserDetails userDetails = (CustomUserDetails) principal;
                            String userId = userDetails.getUsername();
                            String userNo = userDetails.getRealUser().getUserNo();

                            // WebSocket 세션에 저장
                            attributes.put("userId", userId);
                            attributes.put("userNo", userNo);

                            log.info("===> WebSocket 연결: userId = {}, userNo = {} isHeadProf = {}", userId, userNo, userDetails.getRealUser().isHeadProf());
                        }
                    }
                }
            }

            return true;
        }

        @Override
        public void afterHandshake(
                ServerHttpRequest request,
                ServerHttpResponse response,
                WebSocketHandler wsHandler,
                Exception exception) {

        }
    }
}
