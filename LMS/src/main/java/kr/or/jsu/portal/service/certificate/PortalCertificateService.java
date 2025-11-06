package kr.or.jsu.portal.service.certificate;

import java.util.List;

import kr.or.jsu.vo.CertificateReqVO;
import kr.or.jsu.vo.CertificateVO;

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
 *
 * </pre>
 */

/**
 * 포털 > 증명서 발급 관련 서비스 인터페이스
 * <p> 
 * - 학생의 증명서 신청, 조회, PDF 미리보기/다운로드 기능 제공
 * - 각 기능은 MyBatis Mapper와 연계하여 DB를 조회하거나 openhtmltopdf 등을 통해 PDF를 생성함
 * </p>
 */
public interface PortalCertificateService {

    /**
     * 특정 사용자의 증명서 신청 내역을 조회한다.
     *
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자의 증명서 신청 목록
     */
    public List<CertificateReqVO> getCertificateReqList(String userId);

    /**
     * 증명서 발급 신청을 등록한다.
     * <p>
     * - CertificateReqVO 객체를 받아 신청 데이터를 생성한다.  
     * - 보통 INSERT 쿼리를 통해 CERTIFICATE_REQ 테이블에 기록한다.
     * </p>
     *
     * @param certReq 신청할 증명서 요청 VO 객체
     */
    public void createCertificateRequest(CertificateReqVO certReq);

    /**
     * 증명서 미리보기용 PDF 데이터를 생성한다.
     * <p>
     * - openhtmltopdf 등을 사용하여 HTML 템플릿 기반의 미리보기 PDF를 생성한다.  
     * - 실제 저장은 하지 않으며, 단순히 byte[] 형태로 반환한다.
     * </p>
     *
     * @param userId        미리보기를 요청한 사용자 ID
     * @param certificateCd 미리볼 증명서 코드
     * @return 생성된 PDF의 바이트 배열
     * @throws Exception PDF 생성 중 오류 발생 시
     */
    public byte[] generateCertificatePreview(String userId, String certificateCd) throws Exception;

    /**
     * 발급 완료된 증명서 PDF 파일을 가져온다.
     * <p>
     * - DB 또는 파일시스템에 저장된 PDF 파일을 로드하여 반환한다.  
     * - 다운로드 시 이 데이터를 스트림으로 전송한다.
     * </p>
     *
     * @param certReqId 증명서 신청 ID
     * @return PDF 파일의 바이트 배열
     * @throws Exception 파일 조회 실패 시
     */
    public byte[] getCertificatePdf(String certReqId) throws Exception;

    /**
     * 발급 가능한 증명서 목록을 조회한다.
     * <p>
     * - 증명서 코드 테이블을 조회한다.  
     * - 사용 가능한 증명서만 반환한다.
     * </p>
     *
     * @return 발급 가능한 증명서 목록
     */
    public List<CertificateVO> getAvailableCertificates();

}
