package kr.or.jsu.lms.staff.controller.mypage;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.core.validate.groups.UpdateGroup;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.lms.staff.service.mypage.StaffMyPageService;
import kr.or.jsu.vo.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lms/staff/mypage")
public class StaffMyPageController {

	private final StaffMyPageService service;
	private final CommonCodeService commonCodeService;

	public static final String MODELNAME = "staff";

	@GetMapping
	public String selectStaffMyPageDetail(Model model, @AuthenticationPrincipal CustomUserDetails loginUser) {
		String staffNo = loginUser.getUsername();
		UserStaffDTO userStaffDTO = service.readStaffDetail(staffNo);

		if (userStaffDTO == null || userStaffDTO.getUserInfo() == null) {
			model.addAttribute("error", "직원 상세 정보를 찾을 수 없습니다.");
			return "staff/mypage/staffMyPageDetail";
		}

		model.addAttribute("userStaffDTO", userStaffDTO);

		List<CommonCodeVO> bankList = commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE);
		model.addAttribute("bankList", bankList);

		String userBankCd = userStaffDTO.getUserInfo().getBankCode();
		model.addAttribute("userBankCd", userBankCd);

		return "staff/mypage/staffMyPageDetail";
	}

	@GetMapping("/modify")
	public String modifyStaffMyPageForm(Model model, @AuthenticationPrincipal CustomUserDetails loginUser) {
		String staffNo = loginUser.getUsername();
		if (staffNo == null) {
			return "redirect:/login";
		}

		UserStaffDTO staff = service.readStaffDetail(staffNo);
		List<CommonCodeVO> bankList = commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE);

		model.addAttribute("bankList", bankList);
		model.addAttribute("userStaffDTO", staff);

		String userBankCd = null;
		if (staff != null && staff.getUserInfo() != null) {
			userBankCd = staff.getUserInfo().getBankCode();
		}
		model.addAttribute("userBankCd", userBankCd);

		return "staff/mypage/staffMyPageEdit";
	}

	/**
	 * 직원 자신의 개인정보 수정 프로세스 (BeanUtils 사용을 위해 DTO와 UsersVO를 병합하여 Service로 전달)
	 */
	@PostMapping("/modify")
	public String modifyStaffMyPage(@Validated(UpdateGroup.class) @ModelAttribute("userStaffDTO") UserStaffDTO staff,

			@AuthenticationPrincipal CustomUserDetails loginUser, BindingResult errors,
			RedirectAttributes redirectAttributes) {

		log.error("DEBUG: Form Data MobileNo (Controller): {}", staff.getUserInfo().getMobileNo());

		String staffNo = loginUser.getUsername();

		if (errors.hasErrors()) {

			return "redirect:/lms/staff/mypage/modify";
		}

		try {

			service.modifyMyStaffInfo(staff, staffNo);

			redirectAttributes.addFlashAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
		} catch (Exception e) {

		}

		return "redirect:/lms/staff/mypage";
	}
}