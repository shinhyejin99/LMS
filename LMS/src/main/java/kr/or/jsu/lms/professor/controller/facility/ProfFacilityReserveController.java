package kr.or.jsu.lms.professor.controller.facility;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lms/professor/facility/reserve")
public class ProfFacilityReserveController {

	// 시설 예약
	@GetMapping
	public String formUI() {
		
		return "professor/facility/profFacilityReserve";
	}
	
//	POST  (예약 프로세스)
}
