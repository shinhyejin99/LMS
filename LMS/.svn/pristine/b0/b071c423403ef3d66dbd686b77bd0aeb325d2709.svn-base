package kr.or.jsu.lms.student.controller.courseManagement;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.StudentLectureResponseDTO;
import kr.or.jsu.dto.StudentDetailDTO; // StudentDetailDTO 임포트 추가
import kr.or.jsu.dto.PaginatedResponseDTO; // PaginatedResponseDTO 임포트 추가
import kr.or.jsu.lms.student.service.courseManagement.StuLectureService;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *  -----------		-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25. 		김수현			파일 이름 변경
 *  2025. 10. 17.       최건우            수강 포기 신청 페이지 강의 목록 조회 기능 추가
 *  2025. 10. 31.       최건우            페이지네이션 적용에 따른 getStudentLectures 호출 방식 수정
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/lecture/withdrawal")
@RequiredArgsConstructor
public class StuLectureWithdrawalController {

	private final StuLectureService stuLectureService;
	private final StuInfoService stuInfoService;

	// 수강포기 신청 페이지 - 컨트롤러
	
	@GetMapping
	public String getList(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
		// 현재 로그인한 학생의 학번 가져오기
        String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();

        // 학생 상세 정보 조회
        StudentDetailDTO studentInfo = stuInfoService.readStuMyInfo(studentNo);
        model.addAttribute("studentInfo", studentInfo);
        model.addAttribute("studentName", studentInfo.getLastName() + studentInfo.getFirstName());

        // 학생의 수강 내역 조회 (페이지네이션 적용)
        // 수강 포기 신청 페이지에서는 전체 목록을 보여주므로, 기본값으로 1페이지와 충분히 큰 사이즈를 사용합니다.
        int defaultPage = 1;
        int defaultSize = Integer.MAX_VALUE; // Or a sufficiently large number to get all lectures

        PaginatedResponseDTO<StudentLectureResponseDTO> paginatedLectureList = stuLectureService.getStudentLectures(studentNo, defaultPage, defaultSize);
        model.addAttribute("lectureList", paginatedLectureList.getContent()); // Pass the content list to the model

        log.debug("수강 포기 신청 페이지 - 학생 수강 내역 조회 - studentNo: {}, count: {}", studentNo, paginatedLectureList.getTotalElements());
		
		return "student/courseManagement/studentLectureWithdrawal";
	}
}