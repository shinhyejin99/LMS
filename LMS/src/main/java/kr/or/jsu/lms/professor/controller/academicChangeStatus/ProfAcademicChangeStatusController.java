package kr.or.jsu.lms.professor.controller.academicChangeStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.lms.professor.service.academicChangeStatus.profAcademicChangeStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/lms/professor/academic-change-status")
@RequiredArgsConstructor
@Slf4j
public class ProfAcademicChangeStatusController {

	private final profAcademicChangeStatusService service;
	
	@GetMapping
	public String selectProfAcademicChangeStatusList(
			@ModelAttribute("pagingInfo")PaginationInfo<Map<String, Object>> pagingInfo, HttpServletRequest request 
			,Model model) {
		

		// currentPage가 0 또는 음수이면 1로 강제 설정
		if (pagingInfo.getCurrentPage() < 1) {
			pagingInfo.setCurrentPage(1);
		}

		// 상세 검색 파라미터를 수동으로 Map에 담아 PaginationInfo에 설정.
		Map<String, Object> detailSearchMap = new HashMap<>();

		// 요청에서 "deptCd"와 "statusCd" 파라미터를 받아 Map에 담기.
		// JSP에서 넘어온 파라미터 이름 그대로 사용합니다.
		String stfDeptCd = request.getParameter("stfDeptCd");
		String statusCd = request.getParameter("statusCd");

		if (stfDeptCd != null && !stfDeptCd.isEmpty()) {
			// 서비스에서 사용할 키 이름("stfDeptCd")으로 변경하여 detailSearchMap에 저장.
			detailSearchMap.put("stfDeptCd", stfDeptCd);
		}
		if (statusCd != null && !statusCd.isEmpty()) {
			// 서비스에서 사용할 키 이름("stfStatusCd")으로 변경하여 detailSearchMap에 저장합니다.
			detailSearchMap.put("stfStatusCd", statusCd);
		}

		// 상세 검색 Map을 PaginationInfo에 주입.
		if (!detailSearchMap.isEmpty()) {
			pagingInfo.setDetailSearch(detailSearchMap);
		}

		// 서비스의 페이징/검색 통합 메서드를 호출.
		List<Map<String, Object>> academicChangeStatusList = service.readProfAcademicChangeStatusList(pagingInfo);

		// 뷰로 목록과 완성된 페이징 정보를 전달.
		model.addAttribute("academicChangeStatusList", academicChangeStatusList);

		return "professor/academicChangStatus/academicChangStatusList";
	}
	
	
}
