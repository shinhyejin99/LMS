package kr.or.jsu.lms.student.controller.dashboard;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.SemesterGradeDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학생 대시보드 REST Controller
 * @author 김수현
 * @since 2025. 10. 24.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 24.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/student")
public class StuDashBoardRestController {
	private final StuInfoService service;

    @GetMapping("/graduation-status")
    public ResponseEntity<StudentDetailDTO> getGraduationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();
            StudentDetailDTO student = service.readStuMyInfo(studentNo);

            log.info("===> 가져온 학생 정보: {}", student);

            return ResponseEntity.ok(student);

        } catch (Exception e) {
            log.error("졸업 이수 현황 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 학기별 평균 평점 조회
     * @param userDetails 사용자 정보
     * @return 학기별 평균 평점 리스트
     */
    @GetMapping("/semester-grades")
    public ResponseEntity<List<SemesterGradeDTO>> getSemesterGrades(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
    	log.info("===== 학기별 성적 조회 API 호출 =====");
        if (userDetails == null) {
        	log.info("인증 정보 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String studentNo = ((StudentVO) userDetails.getRealUser()).getStudentNo();
            log.info("학번: {}", studentNo);

            List<SemesterGradeDTO> grades = service.readSemesterGrades(studentNo);
            log.info("조회된 학기 수: {}", grades != null ? grades.size() : 0);
            log.info("조회 결과: {}", grades);

            return ResponseEntity.ok(grades);

        } catch (Exception e) {
            log.info("학기별 성적 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
