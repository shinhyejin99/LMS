package kr.or.jsu.lms.professor.controller.advising;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.service.advising.ProfAdStudentService;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.UsersVO;

@Controller
@RequestMapping("/lms/professor/advising")
public class ProfAdStudentController {

    @Autowired
    private ProfAdStudentService service;

    // 1. 화면 보여주기 기능
    @GetMapping
    public String showAdvisingPage(
            Authentication authentication,
            @RequestParam(value = "grade", required = false) String grade,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UsersVO realUser = userDetails.getRealUser();
        String professorNo = realUser.getUserNo();
        System.out.println("Logged-in ProfessorNo: " + professorNo); // Debugging statement

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("professorNo", professorNo);
        paramMap.put("offset", (page - 1) * size);
        paramMap.put("size", size);

        if (grade != null && !grade.isEmpty()) {
            paramMap.put("grade", grade);
        }
        if (status != null && !status.isEmpty()) {
            if ("ATTEND".equals(status)) {
                paramMap.put("status", "재학");
            } else {
                paramMap.put("status", status);
            }
        }

        List<StudentVO> adviseeList = service.retrieveAdvisingStudentList(paramMap);
        int total = service.retrieveAdvisingStudentCount(paramMap);

        System.out.println("Controller: Advisee list size after service call: " + adviseeList.size()); // Debugging statement
        model.addAttribute("adviseeList", adviseeList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) total / size));

        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedStatus", status);

        return "professor/advising/profAdStudentList";
    }

    // 2. 지도 학생 목록 API 기능
    @GetMapping(value = "/api/advisedStudents", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<StudentVO> getAdvisedStudents(
            Authentication authentication,
            @RequestParam(required = false) String grade
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UsersVO realUser = userDetails.getRealUser();
        String professorNo = realUser.getUserNo(); // Use authenticated professor's ID

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("professorNo", professorNo);
        if (grade != null && !grade.isEmpty()) {
            paramMap.put("grade", grade);
        }
        
        List<StudentVO> studentList = service.retrieveAdvisingStudentList(paramMap);
        
        return studentList;
    }

    // 3. 상담 요청 목록 API 기능
    @GetMapping(value = "/api/counselingRequests", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public List<Map<String, Object>> getCounselingRequests(
            Authentication authentication,
            @RequestParam(value = "studentNo", required = false) String studentNo
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UsersVO realUser = userDetails.getRealUser();
        String professorNo = realUser.getUserNo();

        if (studentNo != null && !studentNo.isEmpty()) {
            // If a specific student is requested, fetch their requests
            return service.retrieveCounselingRequestListByStudentNo(studentNo);
        } else {
            // Otherwise, fetch all requests for the professor
            return service.retrieveCounselingRequestList(professorNo);
        }
    }

    // 4. 학생 상세 정보 API 기능
    @GetMapping(value = "/api/studentDetails", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public StudentVO getStudentDetails(
            @RequestParam(value = "studentNo") String studentNo
    ) {
        return service.getStudentDetailsByStudentNo(studentNo);
    }
}