package kr.or.jsu.classregist.student.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classregist.dto.ApplyStatusDTO;
import kr.or.jsu.classregist.dto.LectureDetailDTO;
import kr.or.jsu.classregist.dto.WishlistResponseDTO;
import kr.or.jsu.classregist.student.service.ClassRegistWishlistService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;

/**
 * 예비수강신청 REST 컨트롤러
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *	2025. 10. 18.		김수현			수강신청 추가
 * </pre>
 */
@RestController
@RequestMapping("/classregist/student/rest/wish")
@RequiredArgsConstructor
public class ClassRegistRestController {

	private final ClassRegistWishlistService wishlistService;

	/**
     * 강의 상세 조회
     */
    @GetMapping("/lecture/detail")
    public LectureDetailDTO getLectureDetail(@RequestParam String lectureId) {
         return wishlistService.getLectureDetail(lectureId);
    }

    /**
     * 찜하기
     */
    @PostMapping("/{lectureId}")
    public WishlistResponseDTO addWishlist(
    	@PathVariable String lectureId
        , @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
    	StudentVO student = (StudentVO) userDetails.getRealUser();
		String studentNo = student.getStudentNo();

        return wishlistService.addWishlist(studentNo, lectureId);
    }

    /**
     * 찜 취소
     */
    @DeleteMapping("/{lectureId}")
    public Map<String, Object> removeWishlist(
    	@PathVariable String lectureId
        , @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Map<String, Object> result = new HashMap<>();

        StudentVO student = (StudentVO) userDetails.getRealUser();
		String studentNo = student.getStudentNo();

        boolean success = wishlistService.removeWishlist(studentNo, lectureId);

        result.put("success", success);
        if (success) {
            result.put("message", "찜 목록에서 삭제되었습니다.");
        } else {
            result.put("message", "삭제에 실패했습니다.");
        }

        return result;
    }

    /**
     * 신청 현황 조회 (찜한 학점 합계)
     */
    @GetMapping("/apply-status")
    public ResponseEntity<ApplyStatusDTO> getApplyStatus(  // 🆕 Map → ApplyStatusDTO
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        StudentVO student = (StudentVO) userDetails.getRealUser();
        ApplyStatusDTO status = wishlistService.getApplyStatus(student.getStudentNo());

        return ResponseEntity.ok(status);
    }

    // ======= 수강신청 =========
    /**
     * 수강신청
     */
    @PostMapping("/apply/{lectureId}")
    public WishlistResponseDTO applyLecture(
            @PathVariable String lectureId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        StudentVO student = (StudentVO) userDetails.getRealUser();
        String studentNo = student.getStudentNo();

        return wishlistService.applyLecture(studentNo, lectureId);
    }

    /**
     * 수강신청 취소
     */
    @DeleteMapping("/apply/{lectureId}")
    public WishlistResponseDTO cancelApplyLecture(
            @PathVariable String lectureId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        StudentVO student = (StudentVO) userDetails.getRealUser();
        String studentNo = student.getStudentNo();

        return wishlistService.cancelApplyLecture(studentNo, lectureId);
    }
}
