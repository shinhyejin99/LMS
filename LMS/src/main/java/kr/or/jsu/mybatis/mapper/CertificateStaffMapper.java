package kr.or.jsu.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.CertificateStaffDetailDTO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface CertificateStaffMapper {
    public CertificateStaffDetailDTO selectStaffDetailInfo(String userId);
}
