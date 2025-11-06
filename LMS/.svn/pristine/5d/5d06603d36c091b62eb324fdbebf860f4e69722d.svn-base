package kr.or.jsu.lms.professor.controller.info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.lms.professor.service.info.ProfInfoService;
import kr.or.jsu.vo.ProfessorVO;

@Controller
@RequestMapping("/lms/professor/info")
public class ProfInfoController {

    @Autowired
    private ProfInfoService profInfoService;

    @Autowired
    private CommonCodeService commonCodeService;

    @GetMapping
    public String view(Authentication authentication, Model model) {
        String professorNo = authentication.getName();
        ProfessorInfoDTO professorInfo = profInfoService.getProfessorInfoView(professorNo);

        // 연락처, 금융 정보가 하나라도 입력되었는지 확인
        boolean hasExistingInfo = (professorInfo.getMobileNo() != null && !professorInfo.getMobileNo().isEmpty())
                                  || (professorInfo.getEmail() != null && !professorInfo.getEmail().isEmpty())
                                  || (professorInfo.getBankCode() != null && !professorInfo.getBankCode().isEmpty())
                                  || (professorInfo.getBankAccount() != null && !professorInfo.getBankAccount().isEmpty())
                                  || (professorInfo.getZipCode() != null && !professorInfo.getZipCode().isEmpty())
                                  || (professorInfo.getBaseAddr() != null && !professorInfo.getBaseAddr().isEmpty())
                                  || (professorInfo.getDetailAddr() != null && !professorInfo.getDetailAddr().isEmpty())
                                  || (professorInfo.getOfficeNo() != null && !professorInfo.getOfficeNo().isEmpty());
        model.addAttribute("hasExistingInfo", hasExistingInfo);

        model.addAttribute("professor", professorInfo);
        return "professor/info/professorInfo";
    }

    @GetMapping("/modify")
    public String modifyForm(Authentication authentication, Model model) {
        String professorNo = authentication.getName();
        ProfessorInfoDTO professorInfo = profInfoService.getProfessorInfoView(professorNo);
        model.addAttribute("professor", professorInfo);
        model.addAttribute("bankList", commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE));
        return "professor/info/professorInfoModify";
    }

    @PostMapping
    public String update(ProfessorVO professorVO, Authentication authentication, RedirectAttributes redirectAttributes) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ProfessorVO originalProfessor = (ProfessorVO) userDetails.getRealUser();

        professorVO.setUserId(originalProfessor.getUserId());
        professorVO.setProfessorNo(originalProfessor.getProfessorNo());
        
        boolean updated = profInfoService.updateProfessorInfo(professorVO);
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "성공적으로 저장되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "정보 저장에 실패했습니다. 변경된 내용이 없습니다.");
        }
        return "redirect:/lms/professor/info";
    }
}
