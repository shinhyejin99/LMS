package kr.or.jsu.lms.student.service.financialAid;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.dto.ScholarshipDistributionDTO;
import kr.or.jsu.dto.ScholarshipHistoryDTO;
import kr.or.jsu.dto.ScholarshipResponseDTO;
import kr.or.jsu.mybatis.mapper.ScholarshipMapper;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class StuScholarshipServiceImpl implements StuScholarshipService {

	private final ScholarshipMapper mapper;
	
	@Override
    public ScholarshipResponseDTO getScholarshipInfo(String studentNo) {
        // 총 수혜 금액 조회
        Integer totalScholarship = mapper.selectTotalScholarship(studentNo);
        if (totalScholarship == null) {
            totalScholarship = 0;
        }
        
        // 종류별 분포 조회
        List<ScholarshipDistributionDTO> distribution = 
        		mapper.selectScholarshipDistribution(studentNo);
        
        // 수혜 내역 조회
        List<ScholarshipHistoryDTO> history = 
        		mapper.selectScholarshipHistory(studentNo);
        
        // 응답 DTO 반환
        return new ScholarshipResponseDTO(totalScholarship, distribution, history);
    }
}
