package kr.or.jsu.lms.user.controller.notification;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lms/notifications")
public class UserNotificationAPIController {

	private final UserNotificationListService listService;

	/**
	 * AJAX 요청으로 로그인 사용자의 알림 목록 데이터를 JSON으로 반환
	 */
	@GetMapping(value = "/api", produces = "application/json")
	public List<PushNoticeDetailDTO> getNoticeListApi(
	    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 🚨 수정: userDetails 또는 getRealUser()가 null인지 확인
        if (userDetails == null || userDetails.getRealUser() == null) {
            log.warn("알림 API 접근 시도: 인증 정보 또는 실제 사용자 정보 누락.");
            return List.of();
        }

	    String userId = userDetails.getRealUser().getUserId();

	    try {
	        return listService.readNotificationsByUserId(userId);
	    } catch (Exception e) {
	        log.error("알림 목록 API 조회 중 오류 발생 (User ID: {}): {}", userId, e.getMessage(), e);
	        return List.of();
	    }
	}


    /**
     * AJAX 요청으로 로그인 사용자의 읽지 않은 알림 개수를 반환 (뱃지 카운트용)
     */
	@GetMapping(value = "/api/count-unread", produces = "application/json")
	public int getUnreadNoticeCountApi(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 🚨 수정: userDetails 또는 getRealUser()가 null인지 확인
        if (userDetails == null || userDetails.getRealUser() == null) {
            log.warn("읽지 않은 알림 개수 API 접근 시도: 인증 정보 또는 실제 사용자 정보 누락.");
            return 0;
        }

		String userId = userDetails.getRealUser().getUserId();

		try {
			return listService.readUnreadNotificationCount(userId);
		} catch (Exception e) {
            log.error("읽지 않은 알림 개수 API 조회 중 오류 발생 (User ID: {}): {}", userId, e.getMessage(), e);
			return 0;
		}
	}
}