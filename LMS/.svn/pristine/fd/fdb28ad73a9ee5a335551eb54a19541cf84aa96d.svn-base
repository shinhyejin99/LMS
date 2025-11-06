package kr.or.jsu.portal.controller.facility;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.portal.service.facility.PortalFacilityService;
import kr.or.jsu.vo.PlaceVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 16.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	정태일	          최초 생성
 *  2025. 10. 20.     	정태일	          조회 기능 추가
 *  2025. 10. 21.     	정태일	          나의 예약 현황 추가
 *  2025. 10. 30.     	정태일	          건물 선택 기능 추가 및 동적 시설조회 기능 추가
 *
 *      </pre>
 */
@Controller
public class PortalFacilityController {

	@Autowired
	private PortalFacilityService facilityService;

    @GetMapping("/portal/facility/reservation")
    public String facilityReservationPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        // 1. 사용 가능한 건물 목록을 조회합니다.
        List<PlaceVO> buildings = facilityService.retrieveBuildings();
        // 2. 이 목록을 JSP에서 사용할 수 있도록 Model에 추가합니다.
        model.addAttribute("buildings", buildings);

        // 3. 사용자 역할을 추출하여 Model에 추가합니다.
        if (customUserDetails != null) {
            String userRole = customUserDetails.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority())
                                .collect(Collectors.joining(",")); // 모든 역할을 콤마로 구분된 문자열로 가져옵니다.
            model.addAttribute("userRole", userRole);
        } else {
            model.addAttribute("userRole", "ROLE_ANONYMOUS"); // 인증되지 않은 사용자를 위한 기본값
        }

        return "portal/portalFacilityReservation";
    }

    // 건물 선택 후 해당 건물 내의 시설 유형을 조회하는 AJAX 엔드포인트
    @GetMapping("/portal/facility/getFacilityTypes")
    @ResponseBody
    public List<Map<String, String>> getFacilityTypes(
    		@AuthenticationPrincipal CustomUserDetails customUserDetails,
    		@RequestParam String parentCd
		) {
    	
        String userRole = "ROLE_ANONYMOUS";
        if (customUserDetails != null) {
            userRole = customUserDetails.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority())
                                .collect(Collectors.joining(","));
        }
    	
        return facilityService.retrieveFacilityTypesByBuilding(parentCd, userRole);
    }

    // 시설 유형 선택 후 해당 유형의 특정 시설 목록을 조회하는 AJAX 엔드포인트
    @GetMapping("/portal/facility/getFacilities")
    @ResponseBody
    public Map<String, Object> getFacilities(
    		@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String parentCd,
            @RequestParam(required = false) String placeUsageCd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    	) {
    	
        String userRole = "ROLE_ANONYMOUS";
        if (customUserDetails != null) {
            userRole = customUserDetails.getAuthorities().stream()
                                .map(grantedAuthority -> grantedAuthority.getAuthority())
                                .collect(Collectors.joining(","));
        }

        PaginationInfo<PlaceVO> paginationInfo = new PaginationInfo<>(size, 5); // 한 페이지에 5개, 블록 크기 5
        paginationInfo.setCurrentPage(page);

        List<PlaceVO> facilities;
        if (placeUsageCd == null || placeUsageCd.isEmpty()) {
            facilities = facilityService.retrieveFacilitiesByBuilding(parentCd, paginationInfo, userRole);
        } else {
            facilities = facilityService.retrieveFacilitiesByBuildingAndType(parentCd, placeUsageCd, paginationInfo, userRole);
        }

        // 해당 건물에 있는 모든 시설 유형 목록을 가져와서 필터 버튼 생성에 사용
        List<Map<String, String>> allFacilityTypes = facilityService.retrieveFacilityTypesByBuilding(parentCd, userRole);
        System.out.println("allFacilityTypes: " + allFacilityTypes); // 디버깅
        List<String> allUniqueUsageCds = allFacilityTypes.stream()
                                            .map(map -> map.get("COMMONCD"))
                                            .distinct()
                                            .collect(Collectors.toList());
        System.out.println("allUniqueUsageCds: " + allUniqueUsageCds); // 디버깅

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("facilities", facilities);
        response.put("paginationInfo", paginationInfo);
        response.put("allUniqueUsageCds", allUniqueUsageCds);

        return response;
    }

	@GetMapping("/portal/facility/calendar/{placeCd}")
	public String facilityCalendarPage(@PathVariable String placeCd, Model model) {
		model.addAttribute("place", facilityService.retrieveFacility(placeCd));
		return "portal/portalFacilityCalendar";
	}

	/**
	 * 나의 예약 현황 페이지를 처리합니다.
	 *
	 * @param principal 현재 로그인한 사용자 정보
	 * @param model     뷰로 전달할 데이터를 담는 객체
	 * @return 나의 예약 현황 JSP 페이지 경로
	 */
	@GetMapping("/portal/facility/my-reservations")
	public String myReservationsPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
		String userId = customUserDetails.getRealUser().getUserId();
		model.addAttribute("myReservations", facilityService.retrieveMyReservations(userId));
		return "portal/portalFacilityMyReservations";
	}
}