package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.CertificateDetailVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *
 *      </pre>
 */
/**
 * 증명서 상세 정보(CertificateDetail) DB 접근 매퍼 인터페이스
 */
@Mapper
public interface CertificateDetailMapper {

	/**
	 * 상세 정보 **등록 (Create)**
	 */
	public int insertCertificateDetail(CertificateDetailVO certificateDetail);

	/**
	 * 전체 상세 정보 **조회 (Read)**
	 */
	public List<CertificateDetailVO> selectCertificateDetailList();

	/**
	 * 요청 ID별 상세 정보 **조회 (Read)**
	 */
	public List<CertificateDetailVO> selectCertificateDetailsByReqId(String certReqId);

	/**
	 * 상세 정보 **수정 (Update)**
	 */
	public int updateCertificateDetail(CertificateDetailVO certificateDetail);

	/**
	 * 상세 정보 **삭제 (Delete)**
	 */
	public int deleteCertificateDetail(String certReqId);
}