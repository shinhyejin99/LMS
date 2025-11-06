package kr.or.jsu.portal.controller.academicmanagement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *  2025. 9. 26.		정태일			  학사관리 시스템 변경
 *
 * </pre>
 */
@Controller
public class AcademicManagementController {

    @GetMapping("/portal/academic")
    public String academicManagementPage() {
        return "portal/portalAcademic";
    }
       
}
