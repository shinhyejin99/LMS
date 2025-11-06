package kr.or.jsu.lms.student.controller.financialAid;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.pdf.PdfService;
import kr.or.jsu.dto.PaymentReceiptDTO;
import kr.or.jsu.dto.StudentPaymentHistoryDTO;
import kr.or.jsu.dto.TuitionNoticeDTO;
import kr.or.jsu.lms.student.service.financialAid.StuTuitionService;
import lombok.RequiredArgsConstructor;

/**
 * 등록금 & 장학금 Rest Controller
 * @author 김수현
 * @since 2025. 10. 22.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 22.     	김수현	          최초 생성
 *
 * </pre>
 */
@RestController
@RequestMapping("/lms/student/tuition/rest")
@RequiredArgsConstructor
public class StuTuitionRestController {

	private final StuTuitionService service;
	private final PdfService pdfService;

	/**
     * 납부 내역 조회 API
     */
    @GetMapping("/payment-history")
    public ResponseEntity<List<StudentPaymentHistoryDTO>> getPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<StudentPaymentHistoryDTO> history = service.getPaymentHistory(userDetails);
        return ResponseEntity.ok(history);
    }

    /**
     * 장학금 상세 조회 API
     */
    @GetMapping("/scholarship-detail")
    public ResponseEntity<Map<String, Object>> getScholarshipDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String yeartermCd) {

        String studentNo = userDetails.getUsername();
        Map<String, Object> detail = service.getScholarshipDetail(studentNo, yeartermCd);
        return ResponseEntity.ok(detail);
    }

	/**
     * 등록금 고지서 미리보기 (브라우저에서 보기) - 모달창으로 구현 예정
     */
    @GetMapping("/notice/view")
    public ResponseEntity<byte[]> viewTuitionNotice(
        @AuthenticationPrincipal CustomUserDetails userDetails
        , @RequestParam String yeartermCd
    ) throws Exception {

        // service 실행
    	// TuitionNoticeDTO : 고지서 확인 통합 DTO
    	TuitionNoticeDTO data = service.getTuitionNotice(userDetails, yeartermCd);

        // PDF 생성
        byte[] pdfBytes = pdfService.generatePdf("pdf/tuition-notice", data);

        // 응답 헤더 설정 및 반환 - 브라우저에서 생성된 PDF 볼 수 있도록
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.inline()
                .filename("등록금고지서.pdf", StandardCharsets.UTF_8)
                .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // ------------------------------------------------------------------

    /**
     * 등록금 고지서 PDF 다운로드
     */
    @GetMapping("/notice/download")
    public ResponseEntity<byte[]> downloadTuitionNotice(
		@AuthenticationPrincipal CustomUserDetails userDetails
		,  @RequestParam String yeartermCd
    ) throws Exception {

        // service 실행
    	TuitionNoticeDTO data = service.getTuitionNotice(userDetails, yeartermCd);

        // PDF 생성
    	byte[] pdfBytes = pdfService.generatePdf("pdf/tuition-notice-download", data);

        // 다운로드 헤더 설정 및 반환
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // 파일명에 학번 포함
        String filename = "등록금고지서_" + userDetails.getUsername() + ".pdf";
        headers.setContentDisposition(
            ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    //------------------------------------------------------------

    /**
     * 납부확인서 미리보기
     */
    @GetMapping("/receipt/view")
    public ResponseEntity<byte[]> viewPaymentReceipt(
            @AuthenticationPrincipal CustomUserDetails userDetails
            , @RequestParam String yeartermCd) throws Exception {

        PaymentReceiptDTO data = service.getPaymentReceipt(userDetails, yeartermCd);
        byte[] pdfBytes = pdfService.generatePdf("pdf/payment-receipt", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.inline()
                .filename("납부확인서.pdf", StandardCharsets.UTF_8)
                .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * 납부확인서 다운로드
     */
    @GetMapping("/receipt/download")
    public ResponseEntity<byte[]> downloadPaymentReceipt(
            @AuthenticationPrincipal CustomUserDetails userDetails
            , @RequestParam String yeartermCd) throws Exception {

        PaymentReceiptDTO data = service.getPaymentReceipt(userDetails, yeartermCd);
        byte[] pdfBytes = pdfService.generatePdf("pdf/payment-receipt-download", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = "납부확인서_" + userDetails.getUsername() + ".pdf";
        headers.setContentDisposition(
            ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

}
