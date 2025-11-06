package kr.or.jsu.lms.user.controller.notification;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/lms/notifications")
@RequiredArgsConstructor
public class UserNotificationListController {

	private final UserNotificationListService service;

	// 알림 목록 페이지 로드 (개인 수신함)
	@GetMapping
	public String selectstaffNotificationList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String staffId = userDetails.getRealUser().getUserId();
		model.addAttribute("currentUserId", staffId);

		return "user/notification/userNotificationList";
	}

	// 알림 상세 조회 및 읽음 처리
	@GetMapping("/{pushId}")
	public String selectstaffNotificationDetail(@PathVariable String pushId,
			@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
		String userId = userDetails.getRealUser().getUserId();

		// 먼저 현재 상태를 조회
		PushNoticeDetailDTO notification = service.readNotificationDetail(pushId, userId);

		if (notification != null && notification.getCheckAt() == null) {
			// 미확인 상태라면, DB를 업데이트
			service.markNotificationAsRead(pushId, userId);

			// 업데이트 후, 데이터베이스에서 최신 상태를 다시 조회
			notification = service.readNotificationDetail(pushId, userId);
		}

		model.addAttribute("notification", notification);

		return "user/notification/userNotificationDetail";
	}

	@GetMapping("/history")
	public String listNotificationHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
			@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo, Model model) {

		String staffId = userDetails.getRealUser().getUserId();
		String senderLastName = userDetails.getRealUser().getLastName();
		String senderFirstName = userDetails.getRealUser().getFirstName();
		String senderName = senderLastName + senderFirstName; // 발신자 이름

		if (pagingInfo.getDetailSearch() == null) {
			pagingInfo.setDetailSearch(new HashMap<>());
		}

		// 발신자 ID를 'userId' 키로 추가하여 MyBatis 쿼리로 전달
		pagingInfo.getDetailSearch().put("userId", staffId);

		// currentPage가 0 또는 음수이면 1로 강제 설정
		if (pagingInfo.getCurrentPage() < 1) {
			pagingInfo.setCurrentPage(1);
		}

		// 페이징에 필요한 startRow, screenSize 계산 및 전달 (Service에서 사용)
		int startRow = pagingInfo.getStartRow();
		int screenSize = pagingInfo.getScreenSize();
		pagingInfo.getDetailSearch().put("startRow", startRow);
		pagingInfo.getDetailSearch().put("screenSize", screenSize);

		// Service 호출 및 목록 조회
		List<Map<String, Object>> historyList = service.readNotificationHistoryList(pagingInfo);

		model.addAttribute("historyList", historyList);
		model.addAttribute("pagingInfo", pagingInfo);
		model.addAttribute("senderId", staffId);
		model.addAttribute("senderName", senderName);

		return "user/notification/userNotificationHistoryList";
	}
}