package kr.or.jsu.classregist.professor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classregist.dto.LectureEnrollStatsDTO;
import kr.or.jsu.classregist.dto.ProfLectureStuListDTO;
import kr.or.jsu.classregist.dto.StudentListResponseDTO;
import kr.or.jsu.classregist.professor.service.ProfLectRegistService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.ProfessorVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교수) 수강신청 관련 REST Controller
 * @author 김수현
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	김수현	          최초 생성
 *	2025. 10. 21.		김수현			d3 엔드포인트 추가
 * </pre>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lms/professor/rest")
public class ProfRegistStuRestController {
	private final ProfLectRegistService profLectRegistService;

	/**
     * 내 강의 목록 조회
     */
    @GetMapping("/lectures")
    public ResponseEntity<List<ProfLectureStuListDTO>> getLectures(
        @RequestParam(defaultValue = "2026_REG1") String yearterm
        , @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        ProfessorVO professor = (ProfessorVO) userDetails.getRealUser();
        String professorNo = professor.getProfessorNo();

        List<ProfLectureStuListDTO> lectures =
        		profLectRegistService.getProfessorLectures(professorNo, yearterm);

        return ResponseEntity.ok(lectures);
    }

    /**
     * 특정 강의의 수강 학생 목록 조회
     */
    @GetMapping("/lectures/{lectureId}/students")
    public ResponseEntity<StudentListResponseDTO> getLectureStudents(
         @PathVariable String lectureId
         , @RequestParam(defaultValue = "1") int page
         , @RequestParam(defaultValue = "10") int pageSize
    ) {
    	 StudentListResponseDTO response =
	        profLectRegistService.getLectureStudentsWithPaging(lectureId, page, pageSize);

	    return ResponseEntity.ok(response);
    }

    /**
     * 내 강의 통계 조회
     */
    @GetMapping("/lectures/stats")
    public ResponseEntity<LectureEnrollStatsDTO> getLectureStats(
        @RequestParam(defaultValue = "2026_REG1") String yearterm
        , @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        ProfessorVO professor = (ProfessorVO) userDetails.getRealUser();
        String professorNo = professor.getProfessorNo();

        LectureEnrollStatsDTO stats =
        		profLectRegistService.getLectureStats(professorNo, yearterm);

        return ResponseEntity.ok(stats);
    }

    /**
     * 특정 강의의 수강 정보 조회 (실시간 정원)
     */
    @GetMapping("/lectures/{lectureId}/enroll-info")
    public ResponseEntity<LectureEnrollStatsDTO> getLectureEnrollInfo(
        @PathVariable String lectureId
    ) {
        LectureEnrollStatsDTO enrollInfo =
        		profLectRegistService.getLectureEnrollInfo(lectureId);

        return ResponseEntity.ok(enrollInfo);
    }

    /**
     * d3 엔드포인트
     * @param yearterm 학기코드
     * @param userDetails 사용자 정보
     * @return
     */
    @GetMapping("/lectures/stats/d3-data")
    public ResponseEntity<Map<String, Object>> getProfessorLectureStatusForD3(
		@RequestParam(defaultValue = "2026_REG1") String yearterm
        , @AuthenticationPrincipal CustomUserDetails userDetails
    ){
    	ProfessorVO professor = (ProfessorVO) userDetails.getRealUser();
    	String professorNo = professor.getProfessorNo();

    	Map<String, Object> d3Data = profLectRegistService.getProfessorLectureStats(professorNo, yearterm);

    	return ResponseEntity.ok(d3Data); // Map -> JSON으로 변환하여 반환
    }
}
