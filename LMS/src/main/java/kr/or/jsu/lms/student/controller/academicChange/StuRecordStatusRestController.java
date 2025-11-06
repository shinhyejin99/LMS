package kr.or.jsu.lms.student.controller.academicChange;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.AffilApplyResponseDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import kr.or.jsu.dto.UnifiedApplyResponseDTO;
import kr.or.jsu.lms.student.service.academicChange.StuRecordStatusService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학적변동 신청 현황 조회 REST 컨트롤러
 * @author 김수현
 * @since 2025. 10. 13.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 13.     	김수현	          최초 생성
 *	2025. 10. 31.		김수현			파일 엔드포인트 추가
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/student/rest/record")
@RequiredArgsConstructor
public class StuRecordStatusRestController {

	private final StuRecordStatusService statusService;

	@ModelAttribute("currentStudent")
    public StudentVO currentStudent(@AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. 사용자 객체가 StudentVO 타입인지 확인
        if (userDetails.getRealUser() instanceof StudentVO) {
            // 학생일 경우에만 StudentVO로 형 변환하여 반환 (원래 목적)
            return (StudentVO) userDetails.getRealUser();
        } else {
            // 학생이 아닌 사용자(예: 직원)가 학생 전용 API에 접근했을 경우
            // ClassCastException 대신, 로그를 남기고 null 반환 또는 예외 발생
            log.warn("권한 없는 사용자({} - Type: {})가 학생 전용 API에 접근 시도.",
                     userDetails.getUsername(), userDetails.getRealUser().getClass().getSimpleName());

            // StudentVO가 필요한 API에서 null이 들어가면 NullPointerException이 발생하므로
            // 더 명확한 HTTP 상태 코드를 반환하는 예외를 던집니다.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 기능은 학생만 접근할 수 있습니다.");
        }
    }


    /**
     * 전체 신청 현황 조회 (통합)
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllApplications(
        @ModelAttribute("currentStudent") StudentVO student
    ) {
        try {
            String studentNo = student.getStudentNo();
            Map<String, Object> result = statusService.getAllApplications(studentNo);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("전체 신청 현황 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 모든 통합 목록을 반환
     * @param student
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<UnifiedApplyResponseDTO>> getApplyListAll(
        @ModelAttribute("currentStudent") StudentVO student
    ) {
        try {
            String studentNo = student.getStudentNo();

            // 서비스에서 이미 통합 및 매핑된 목록
            List<UnifiedApplyResponseDTO> unifiedList = statusService.getUnifiedApplyList(studentNo);

            log.debug("신청 목록 조회 (통합) - studentNo: {}, count: {}", studentNo, unifiedList.size());

            return ResponseEntity.ok(unifiedList);

        } catch (Exception e) {
            log.error("신청 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    /**
//     * 신청 목록 조회 (전체)
//     */
//    @GetMapping("/list")
//    public ResponseEntity<List<RecordApplyResponseDTO>> getApplyListAll(
//        @ModelAttribute("currentStudent") StudentVO student
//    ) {
//        try {
//            String studentNo = student.getStudentNo();
//            List<RecordApplyResponseDTO> applyList = statusService.getApplyList(studentNo);
//
//            log.debug("신청 목록 조회 - studentNo: {}, count: {}", studentNo, applyList.size());
//
//            return ResponseEntity.ok(applyList);
//
//        } catch (Exception e) {
//            log.error("신청 목록 조회 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

//    /**
//     * 타입별 신청 목록 조회
//     */
//    @GetMapping("/list/{type}")
//    public ResponseEntity<List<RecordApplyResponseDTO>> getApplyListByType(
//        @PathVariable("type") String type
//        , @ModelAttribute("currentStudent") StudentVO student
//    ) {
//        try {
//            String studentNo = student.getStudentNo();
//
//            // 전체 목록 조회
//            List<RecordApplyResponseDTO> applyList = statusService.getApplyList(studentNo);
//
//            // 타입 필터링
//            List<RecordApplyResponseDTO> filteredList = applyList.stream()
//                .filter(apply -> type.equals(apply.getRecordChangeCd()))
//                .toList();
//
//            log.debug("타입별 신청 목록 조회 - type: {}, studentNo: {}, count: {}",
//                type, studentNo, filteredList.size());
//
//            return ResponseEntity.ok(filteredList);
//
//        } catch (Exception e) {
//            log.error("타입별 신청 목록 조회 중 오류 발생 - type: {}", type, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    /**
     * 재학상태변경 신청 상세 조회
     */
    @GetMapping("/{applyId}")
    public ResponseEntity<RecordApplyResponseDTO> getApplyDetail(
        @PathVariable("applyId") String applyId
        , @ModelAttribute("currentStudent") StudentVO student
    ) {
        try {
            String studentNo = student.getStudentNo();

            RecordApplyResponseDTO apply = statusService.getApplyDetail(applyId);

            // 본인 확인
            if (!apply.getStudentNo().equals(studentNo)) {
                log.warn("권한 없는 신청 조회 시도 - applyId: {}, requestStudent: {}",
                    applyId, studentNo);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            return ResponseEntity.ok(apply);

        } catch (Exception e) {
            log.error("신청 상세 조회 중 오류 발생 - applyId: {}", applyId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    /**
     * 소속변경 신청 상세 조회
     */
    @GetMapping("/affil/{applyId}")
    public ResponseEntity<AffilApplyResponseDTO> getAffilDetail(
        @PathVariable("applyId") String applyId,
        @ModelAttribute("currentStudent") StudentVO student
    ) {
        try {
            String studentNo = student.getStudentNo();

            AffilApplyResponseDTO apply = statusService.getAffilDetail(applyId);

            // 본인 확인
            if (!apply.getStudentNo().equals(studentNo)) {
                log.warn("권한 없는 신청 조회 시도 - applyId: {}, requestStudent: {}",
                    applyId, studentNo);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            return ResponseEntity.ok(apply);

        } catch (Exception e) {
            log.error("소속변경 신청 상세 조회 중 오류 발생 - applyId: {}", applyId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    /**
     * @param applyId 신청ID
     * @param fileOrder 첨부파일 순번
     * @param student 권한확인 student
     * @return
     */
    @GetMapping("/file/{applyId}/attach/{fileOrder}")
    public ResponseEntity<Resource> downloadAppliedFile(
        @PathVariable("applyId") String applyId,
        @PathVariable("fileOrder") int fileOrder,
        @ModelAttribute("currentStudent") StudentVO student
    ) {
        // 1. Service 호출 (권한 검사 및 DB 정보 획득)
        FileDetailInfo fileInfo = statusService.getAppliedFile(applyId, fileOrder, student.getStudentNo());

        // 2. getRealFile() 호출: FileDetailInfo (getRealFile() 메소드)
        Resource file = fileInfo.getRealFile();

        // 3. 파일명 구성 및 헤더 설정
        String fileName = fileInfo.getOriginName();
        String ext = fileInfo.getExtension();
        String originalFileName = (ext.isBlank()) ? fileName : fileName + "." + ext;

        HttpHeaders headers = new HttpHeaders();
        try {
            String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        } catch (UnsupportedEncodingException e) {
            log.error("파일명 인코딩 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일명 인코딩 실패");
        }

        // 4. ResponseEntity 반환
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
}
