package kr.or.jsu.lms.user.controller.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.vo.PushNoticeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lms/notifications")
public class UserNotificationCreateController {

	public static final String MODELNAME = "notification";
	private final UserNotificationCreateService service;
	private final DatabaseCache databaseCache;
	private final CommonCodeService commonCodeService;

	private void commonData(Model model) {
		model.addAttribute("gradeList", commonCodeService.readCommonCodeList(CommonCodeSort.GRADE_CD));
		model.addAttribute("univDeptList", databaseCache.getUnivDeptList());
		model.addAttribute("collegeList", databaseCache.getCollegeList());
	}

	// 알림 생성 폼 화면 (senderDeptName을 모델에 담아 JSP로 전달)
	@GetMapping("/create")
	public String createUserNotificationForm(@AuthenticationPrincipal CustomUserDetails userDetails,
			@ModelAttribute(MODELNAME) PushNoticeDetailDTO pushNoticeDetailDTO, Model model) {
		String senderId = userDetails.getUsername();
		String senderName = userDetails.getRealUser().getLastName() + userDetails.getRealUser().getFirstName();
		// 소속 부서명 조회 로직
		String senderDeptName = service.readSenderDeptName(senderId);
		log.error("✅ CHECK POINT: Sender ID: {}, Final Dept Name: {}", senderId, senderDeptName);

		// ⭐️ 모델에 부서명 저장 (JSP의 hidden 필드에서 사용될 값)
		model.addAttribute("senderId", senderId);
		model.addAttribute("senderName", senderName);
		model.addAttribute("senderDeptName", senderDeptName);

		model.addAttribute("notification", new PushNoticeVO());
		commonData(model);
		return "user/notification/userNotificationCreate";
	}

	// 수신 대상 인원 수 조회 (AJAX 요청 처리)
	@GetMapping("/count-recipients")
	@ResponseBody
	public Map<String, Integer> countRecipients(@RequestParam("recipientType") String targetType,
			@RequestParam(value = "collegeCode", required = false) String collegeCode,
			@RequestParam(value = "departmentCode", required = false) String departmentCode,
			@RequestParam(value = "gradeCode", required = false) String gradeCode) {

		Map<String, Integer> response = new HashMap<>();
		int count = 0;

		String actualTargetCode = null;
		if ("GRADE".equals(targetType) && StringUtils.hasText(gradeCode)) {
			actualTargetCode = gradeCode;
		} else if ("DEPARTMENT".equals(targetType) && StringUtils.hasText(departmentCode)) {
			actualTargetCode = departmentCode;
		}

		try {
			count = service.countGroupNotificationRecipients(targetType, actualTargetCode, gradeCode);
		} catch (Exception e) {
			log.error("수신 인원 수 조회 중 오류 발생: {}", e.getMessage(), e);
			count = 0;
		}

		response.put("count", count);
		return response;
	}

	// 알림 발송 처리 (그룹 선택만 사용)
	@PostMapping("/send-notification")
	public String sendNotification(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("senderName") String senderName,
			@RequestParam("messageContent") String content,

			// ⭐️ [수정] senderDeptName을 필수가 아니게 (required = false) 변경
			@RequestParam(value = "senderDeptName", required = false) String senderDeptName,

			// 그룹 선택 파라미터
			@RequestParam(value = "recipientType", required = false) String recipientType,
			@RequestParam(value = "collegeCode", required = false) String collegeCode,
			@RequestParam(value = "departmentCode", required = false) String departmentCode,
			@RequestParam(value = "gradeCode", required = false) String gradeCode,

			RedirectAttributes redirectAttributes) {
		log.info("그룹 알림 발송 시도: Type={}, Grade={}, College={}, Dept={}", recipientType, gradeCode, collegeCode,
				departmentCode);

		String senderId = userDetails.getRealUser().getUserId();
		int totalRecipients = 0;
		int groupCount = 0;
		String targetCode = null;
		String validationError = null;

		// ⭐️ [추가] senderDeptName이 null이거나 비어있으면 다시 조회하여 안정성 확보
		if (!StringUtils.hasText(senderDeptName) || "소속 부서 없음".equals(senderDeptName)) {
			senderDeptName = service.readSenderDeptName(senderId);
			log.warn("파라미터 누락으로 인해 senderDeptName을 다시 조회했습니다: {}", senderDeptName);
		}

		// 1. 기본 DTO 생성 (공통 정보)
		PushNoticeDetailDTO baseNotificationDTO = new PushNoticeDetailDTO();
		baseNotificationDTO.setSender(senderId);
		baseNotificationDTO.setPushDetail(content);
		baseNotificationDTO.setSenderDeptName(senderDeptName);


		// 2. 그룹 선택 수신자 처리
		if (StringUtils.hasText(recipientType)) {

			String targetType = recipientType;
            String titlePrefix = ""; // 제목 접두사 변수 추가

			// 유효성 검사 및 targetCode 설정
			if ("ALL".equals(targetType)) {
                titlePrefix = "전체 학생 대상 공지"; // 그룹 발송 제목 설정
				// ALL은 targetCode가 필요 없음
			} else if ("GRADE".equals(targetType)) {
				if (StringUtils.hasText(gradeCode)) {
					targetCode = gradeCode;
                    titlePrefix = gradeCode + " 학년 대상 공지"; // 그룹 발송 제목 설정
				} else {
					validationError = "학년 코드가 누락되었습니다.";
				}
			} else if ("DEPARTMENT".equals(targetType)) {
				// 학과별은 departmentCode가 targetCode가 되며, gradeCode도 필요
				if (StringUtils.hasText(departmentCode) && StringUtils.hasText(collegeCode)
						&& StringUtils.hasText(gradeCode)) {
					targetCode = departmentCode; // 학과별은 departmentCode를 사용
                    // 학과/학년 코드를 사용하여 제목을 설정 (실제 이름으로 변경하는 로직이 있으면 더 좋습니다.)
                    titlePrefix = "[" + departmentCode + "] " + gradeCode + " 학년 대상 공지";
				} else {
					validationError = "학과, 단과 대학 또는 학년 코드가 누락되었습니다.";
				}
			} else {
				validationError = "유효하지 않은 그룹 대상 선택입니다.";
			}

            // 동적 제목을 최종 DTO에 설정
            baseNotificationDTO.setPushTitle(titlePrefix);

			if (validationError != null) {
				log.warn("그룹 알림 유효성 검사 실패: {}", validationError);
				redirectAttributes.addFlashAttribute("error", "그룹 대상 선택 오류: " + validationError);
				return "redirect:/lms/notifications/create";
			}

			try {
				// Service 호출: 그룹 대상 목록을 조회, DB 저장 및 웹소켓 전송까지 모두 수행
				groupCount = service.createAndSendGroupNotification(baseNotificationDTO, targetType, targetCode,
						gradeCode);

				totalRecipients += groupCount;

			} catch (Exception e) {
				log.error("그룹 알림 전송 중 오류 발생: {}", e.getMessage(), e);
				redirectAttributes.addFlashAttribute("groupError", "그룹 알림 전송에 실패했습니다. (오류: " + e.getMessage() + ")");
			}
		}
		boolean hasError = redirectAttributes.getFlashAttributes().containsKey("groupError");

		if (totalRecipients > 0) {
			StringBuilder messageBuilder = new StringBuilder();
			messageBuilder.append("알림이 성공적으로 등록 및 전송되었습니다. (총 대상: ").append(totalRecipients).append("명)");
			redirectAttributes.addFlashAttribute("message", messageBuilder.toString());

		} else if (hasError) {
			redirectAttributes.addFlashAttribute("error", "알림 전송 중 오류가 발생했으며, 전송된 유효 수신자가 없습니다.");

		} else {
			redirectAttributes.addFlashAttribute("error", "알림을 보낼 유효한 그룹 대상이 선택되지 않았습니다.");
		}

		return "redirect:/lms/notifications/create";
	}

	// [LEGACY] 단일 알림 전송 메서드 (유지)
	@PostMapping("/create")
	public String createNotification(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam("targetUserId") String targetUserId, @RequestParam String title, @RequestParam String content,
			RedirectAttributes redirectAttributes) {
		String senderId = userDetails.getRealUser().getUserId();
		PushNoticeDetailDTO sentNoti = null;

		try {
			// 단일 발송은 사용자 정의 제목을 그대로 사용
			sentNoti = service.createAndSendNotification(senderId, targetUserId, title, content);
			redirectAttributes.addFlashAttribute("message", targetUserId + " 사용자에게 알림이 성공적으로 전송되었습니다.");

		} catch (Exception e) {
			log.error("단일 알림 생성 및 전송 중 오류 발생: {}", e.getMessage(), e);
			String errorMessage = (sentNoti != null) ? "알림은 등록되었으나, 실시간 전송에 실패했습니다. (오류: " + e.getMessage() + ")"
					: "알림 등록에 실패했습니다. (오류: " + e.getMessage() + ")";
			redirectAttributes.addFlashAttribute("error", errorMessage);
		}
		return "redirect:/lms/notifications/create";
	}
}