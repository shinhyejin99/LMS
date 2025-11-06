package kr.or.jsu.lms.professor.service.advising;

import java.util.List;
import java.util.Map;
import kr.or.jsu.vo.StudentVO;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	최건우	          최초 생성
 *  2025. 9. 27.        최건우              내용 수정
 *
 * </pre>
 */
public interface ProfAdStudentService {
    
    public List<StudentVO> retrieveAdvisingStudentList(Map<String, Object> paramMap);
    
    public int retrieveAdvisingStudentCount(Map<String, Object> paramMap);

    public List<Map<String, Object>> retrieveCounselingRequestList(String professorNo);

    public List<Map<String, Object>> retrieveCounselingRequestListByStudentNo(String studentNo);

    public StudentVO getStudentDetailsByStudentNo(String studentNo);
}
