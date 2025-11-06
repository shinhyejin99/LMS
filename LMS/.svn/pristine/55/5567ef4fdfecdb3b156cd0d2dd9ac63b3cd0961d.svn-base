package kr.or.jsu.classroom.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/classroom/student")
public class ClassroomStudentController {

    @GetMapping("/dashboard")
    public String studentDashboard() {
        log.info("학생 클래스룸 대시보드 페이지 요청");
        return "classroom/student/dashboard"; // This will resolve to /WEB-INF/views/classroom/student/dashboard.jsp
    }
}
