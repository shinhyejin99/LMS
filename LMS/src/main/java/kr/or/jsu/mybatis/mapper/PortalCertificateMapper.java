package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.CertificateReqVO;
import kr.or.jsu.vo.CertificateVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 2.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 2.     	정태일	          최초 생성
 *  2025. 10. 7.     	정태일	          증명서 신청/조회 기능 추가
 *      </pre>
 */
@Mapper
public interface PortalCertificateMapper {

	public int insertCertificateReq(CertificateReqVO certReq);

	public List<CertificateReqVO> selectCertificateReqList(String userId);

	public List<CertificateVO> selectAllCertificates();

	public CertificateVO selectCertificateByCd(String certificateCd);

	public CertificateReqVO selectCertificateReqById(String certReqId);

	public String selectUserTypeByUserId(String userId);

}
