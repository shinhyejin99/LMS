package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.ScholarshipDistributionDTO;
import kr.or.jsu.dto.ScholarshipHistoryDTO;

/**
 * 
 * @author 김수현
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	김수현	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface ScholarshipMapper {

	// 학생) 총 수혜 금액
    Integer selectTotalScholarship(String studentNo);
    
    // 학생) 종류별 분포
    List<ScholarshipDistributionDTO> selectScholarshipDistribution(String studentNo);
    
    // 학생) 수혜 내역
    List<ScholarshipHistoryDTO> selectScholarshipHistory(String studentNo);
}
