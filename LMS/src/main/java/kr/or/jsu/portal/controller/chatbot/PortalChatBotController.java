package kr.or.jsu.portal.controller.chatbot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
@Controller
public class PortalChatBotController {
	@GetMapping("/portal/chatbot")
	public String chatbotPage() {
		return "portal/portalChatbot";
	}
}
