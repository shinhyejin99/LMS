package kr.or.jsu.portal.controller.certificate;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.portal.service.certificate.PortalCertificateService;
import kr.or.jsu.vo.CertificateReqVO;
import kr.or.jsu.vo.CertificateVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      		수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *  2025. 10. 9.     	정태일	          미리보기 발급내역으로 변경
 *
 * </pre>
 */

/**
 * 포털 > 증명서 발급 컨트롤러
 * <p>
 * - 학생의 증명서 신청, 미리보기, 다운로드 기능을 제공한다. <br>
 * - Service 레이어(PortalCertificateService)를 호출하여 비즈니스 로직 수행 후 View 또는 ResponseEntity로 반환한다.
 * </p>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/portal")
@Slf4j
public class PortalCertificateController {

    /** 증명서 서비스 의존성 주입 */
    private final PortalCertificateService certService;

    /**
     * 증명서 신청 내역 및 발급 가능한 증명서 목록 화면 조회
     * <p>
     * - 로그인한 사용자의 증명서 신청 내역을 조회하고 <br>
     * - 현재 발급 가능한 증명서 목록을 모델에 담아 JSP로 전달한다.
     * </p>
     *
     * @param model          화면으로 데이터 전달용 Model
     * @param authentication Spring Security 인증 객체 (로그인 사용자 정보)
     * @return portal/portalCertificate.jsp 뷰 이름
     */
    @GetMapping("/certificate")
    public String certificateList(Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUserId = userDetails.getRealUser().getUserId();

        // 사용자 증명서 신청 내역 조회
        List<CertificateReqVO> certList = certService.getCertificateReqList(currentUserId);
        model.addAttribute("certList", certList);

        // 발급 가능한 증명서 목록 조회
        List<CertificateVO> availableCertificates = certService.getAvailableCertificates();
        model.addAttribute("availableCertificates", availableCertificates);

        return "portal/portalCertificate";
    }

    /**
     * 증명서 발급 신청 처리
     * <p>
     * - AJAX 요청으로 들어온 증명서 신청을 처리한다. <br>
     * - DB에 신청 데이터를 등록하고 성공/실패 메시지를 JSON 형태로 반환한다.
     * </p>
     *
     * @param certificateCd  신청할 증명서 코드
     * @param authentication 로그인 사용자 인증 객체
     * @return 성공 시 "신청이 완료되었습니다." / 실패 시 오류 메시지
     */
    @PostMapping("/certificate")
    @ResponseBody
    public ResponseEntity<String> applyForCertificate(
            @RequestBody CertificateReqVO certReq,
            Authentication authentication
    ) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UsersVO user = userDetails.getRealUser();
            String currentUserId = user.getUserId();

            certReq.setUserId(currentUserId);
            // requestAt, expireAt, statusCd는 서비스 계층에서 기본값 설정
            // certReq.setRequestAt(LocalDateTime.now());
            // certReq.setExpireAt(certReq.getRequestAt().plusDays(90));
            // certReq.setStatusCd("PENDING");

            certService.createCertificateRequest(certReq);
            return new ResponseEntity<>("신청이 완료되었으며, 잠시 후 알림으로 발급 결과를 확인하실 수 있습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("신청 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 증명서 미리보기 PDF 생성
     * <p>
     * - 선택한 증명서 양식을 기반으로 PDF를 생성하여 브라우저에서 바로 미리보기 가능하도록 반환한다. <br>
     * - Content-Disposition을 inline으로 지정하여 새 탭에서 열림.
     * </p>
     *
     * @param certificateCd  미리볼 증명서 코드
     * @param authentication 로그인 사용자 인증 객체
     * @return PDF 파일 데이터 (ResponseEntity<byte[]>)
     */
    @GetMapping("/certificate/preview")
    public ResponseEntity<byte[]> previewCertificate(
            @RequestParam String certificateCd,
            Authentication authentication
    ) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UsersVO user = userDetails.getRealUser();
            String currentUserId = user.getUserId();

            byte[] pdfBytes = certService.generateCertificatePreview(currentUserId, certificateCd);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "preview.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("증명서 미리보기 생성 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 증명서 PDF 다운로드
     * <p>
     * - 신청 완료된 증명서의 PDF를 다운로드 형식으로 반환한다. <br>
     * - Content-Disposition을 attachment로 설정하여 파일 저장 창을 표시한다.
     * </p>
     *
     * @param certReqId 증명서 신청 ID
     * @return PDF 파일 데이터 (ResponseEntity<byte[]>)
     */
    @GetMapping("/certificate/download/{certReqId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String certReqId) {
        try {
            byte[] pdfBytes = certService.getCertificatePdf(certReqId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "certificate-" + certReqId + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 발급 완료된 증명서 PDF 미리보기
     * <p>
     * - 신청 완료된 증명서의 PDF를 브라우저에서 바로 미리보기 가능하도록 반환한다. <br>
     * - Content-Disposition을 inline으로 지정하여 새 탭에서 열림.
     * </p>
     *
     * @param certReqId 증명서 신청 ID
     * @return PDF 파일 데이터 (ResponseEntity<byte[]>)
     */
    @GetMapping("/certificate/preview-issued/{certReqId}")
    public ResponseEntity<byte[]> previewIssuedCertificate(@PathVariable String certReqId) {
        try {
            byte[] pdfBytes = certService.getCertificatePdf(certReqId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "preview-" + certReqId + ".pdf"; // Use a preview filename
            headers.setContentDispositionFormData("inline", filename);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("발급된 증명서 미리보기 생성 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
