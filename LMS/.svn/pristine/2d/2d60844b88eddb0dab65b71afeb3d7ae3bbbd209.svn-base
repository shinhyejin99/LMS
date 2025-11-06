package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.dto.db.subject.SubjectWithCollegeAndDeptDTO;
import kr.or.jsu.vo.SubjectVO;

/**
 *
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	      바닐라 CRUD에 @Deprecated
 *
 * </pre>
 */
@Mapper
public interface SubjectMapper {

	// 쓰실분이 직접 해제하고 사용
	@Deprecated
    public int insertSubject(SubjectVO subject);
	@Deprecated
    public SubjectInfoDetailDTO selectSubject(String subjectCd);
	@Deprecated
    public List<SubjectInfoDetailDTO> selectSubjectsList();
	@Deprecated
    public int updateSubject(SubjectInfoDetailDTO subject);
	@Deprecated
    public int deleteSubject(String subjectCd);

    // New methods for lecture registration form
    List<Map<String, Object>> selectAllSubjectCodes(@Param("univDeptCd") String univDeptCd);
    public SubjectInfoDetailDTO selectstaffSubjectDetail(String subjectCd);
    /**
     * 교직원 교과목 전체조회 및 페이징
     * @param paramMap
     * @return
     */
	public List<Map<String, Object>> selectStaffSubjectList(Map<String, Object> paramMap);
	/**
	 * 페이지 갯수
	 * @param paramMap
	 * @return
	 */
	public int selectTotalSubjectCount(Map<String, Object> paramMap);
	/**
	 * 교과목 갯수
	 * @return
	 */
	public List<Map<String, Object>> selectSubjectCountByType();
	/**
	 * 학과 갯수
	 * @return
	 */
	public List<Map<String, Object>> selectSubjectCountByDept();
	/**
	 * 학년 명수
	 * @return
	 */
	public List<Map<String, Object>> selectAverageCreditByGrade();
	/**
	 * 그래프 총 교과목 수
	 * @return
	 */
	public int selectTotalActiveSubjectCount();
	/**
	 * 그래프
	 * @return
	 */
	public Double selectGlobalAverageCredit();
	public Map<String, Object> selectSubjectDetails(String subjectCode);
	public List<String> selectUsersForSubjectObsolete(String subjectCd);

	/**
	 * 모든 과목을 가져오되, 과목이 소속된 단과대학과 학과까지 같이 가져옵니다.
	 *
	 * @return
	 */
	public List<SubjectWithCollegeAndDeptDTO> selectAllSubjectWithCollegeAndDept();
	public List<Map<String, Object>> selectAverageHourByDept();
}