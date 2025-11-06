package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.CertificateItemVO;

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
@Mapper
public interface CertificateItemMapper {
	public int insertCertificateItem(CertificateItemVO certificateItem);

	public List<CertificateItemVO> selectCertificateItemList();

	public List<CertificateItemVO> selectCertificateItemListByCertCd(String certificateCd);

	public int updateCertificateItem(CertificateItemVO certificateItem);

	public int deleteCertificateItem(String item);
}
