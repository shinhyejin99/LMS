package kr.or.jsu.lms.staff.controller.userreserve;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import kr.or.jsu.lms.staff.service.userreserve.StaffUserReserveService;
import kr.or.jsu.vo.UserReserveVO;

/**
 * 교직원용 시설 예약 현황 페이지를 처리하는 컨트롤러.
 */
/**
 * 
 * @author 송태호
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	정태일		   최초 생성
 *
 * </pre>
 */
@Controller
@RequestMapping("/lms/staff/userreserves")
public class StaffUserReserveController {

    // StaffUserReserveService를 주입받아 비즈니스 로직을 처리합니다.
    @Autowired
    private StaffUserReserveService staffUserReserveService;

    // ObjectMapper를 사용하여 객체를 JSON 문자열로 변환합니다.
    // LocalDateTime 처리를 위해 JavaTimeModule을 등록하고, 특정 포맷으로 직렬화하도록 설정합니다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StaffUserReserveController() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true); // 가독성을 위해 JSON 출력 들여쓰기 (선택 사항)
    }

    /**
     * 모든 사용자 시설 예약 목록을 조회하여 JSP에 전달합니다.
     * 검색 조건(searchTerm)이 있을 경우 해당 조건에 따라 필터링된 목록을 조회합니다.
     *
     * @param model 뷰로 데이터를 전달하기 위한 Model 객체
     * @param searchTerm 통합 검색어 (시설명, 사용자 이름 등으로 검색)
     * @return 시설 예약 현황 JSP 뷰 경로
     */
    @GetMapping
    public String selectstaffUserReserveList(Model model,
                                             @RequestParam(value = "searchTerm", required = false) String searchTerm) {
        List<UserReserveVO> userReserveList = staffUserReserveService.readStaffUserReserveList(searchTerm);
        model.addAttribute("userReserveList", userReserveList);

        try {
            // userReserveList를 JSON 문자열로 변환하여 JSP에 전달 (차트 데이터용)
            String userReserveListJson = objectMapper.writeValueAsString(userReserveList);
            model.addAttribute("userReserveListJson", userReserveListJson);
        } catch (Exception e) {
            // JSON 변환 실패 시 에러 로깅 (실제 환경에서는 더 상세한 에러 처리 필요)
            e.printStackTrace();
            model.addAttribute("userReserveListJson", "[]"); // 빈 배열 전달
        }

        return "staff/userreserve/staffUserReserveList";
    }

    // 상세조회
    @GetMapping("/{subjectNo}")
    public String selectstaffUserReserveDetail() {
        return "staff/userreserve/staffUserReserveDetail";
    }

    // 등록 폼
    @GetMapping("/create")
    public String createstaffUserReserveForm() {
        return "staff/userreserve/staffUserReserverCreate";
    }

    // 등록 프로세스
    @PostMapping("/create")
    public String createstaffUserReserve() {
        return "redirect:/lms/staff/userreserves";
    }

    // 수정 폼
    @GetMapping("/modify/{subjectNo}")
    public String modifystaffUserReserveForm() {
        return "staff/userreserve/staffUserReserveEdit";
    }

    // 수정 프로세스
    @PostMapping("/modify/{subjectNo}")
    public String modifystaffUserReserve() {
        return "redirect:/lms/staff/userreserve/{subjectNo}";
    }

    // 삭제
    @GetMapping("/delete/{subjectNo}")
    public String deletestaffUserReserve() {
        return "redirect:/lms/staff/userreserves";
    }
}
