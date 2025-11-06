package kr.or.jsu.lms.professor.controller.facility;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Date; // Add this import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.lms.professor.service.facility.ProfFacilityHistoryService;

@Controller
@RequestMapping("/lms/professor/facility/history")
public class ProfFacilityHistoryController {

    @Autowired
    private ProfFacilityHistoryService profFacilityHistoryService;

    @GetMapping
    public String showFacilityHistory(Principal principal, Model model) {
        // 로그인한 교수의 교번을 얻어온다.
        String professorNo = principal.getName();

        // 서비스를 통해 시설 사용 이력 목록을 조회한다.
        List<Map<String, Object>> historyList = profFacilityHistoryService.retrieveFacilityHistory(professorNo);

        // 모델에 이력 목록을 추가하여 View로 전달한다.
        model.addAttribute("historyList", historyList);
        model.addAttribute("now", new Date()); // Add current date to model

        // 이력을 표시할 View의 이름을 반환한다.
        return "professor/facility/profFacilityHistory";
    }
}
