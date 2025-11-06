package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.ai.dto.CollegeUnvieDeptInfoDTO;
import kr.or.jsu.dto.DepartmentDetailDTO;
import kr.or.jsu.vo.UnivDeptVO;

/**
 *
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 30.     	송태호	      바닐라 CRUD에 @Deprecated 추가
 *
 * </pre>
 */
@Mapper
public interface UnivDeptMapper {
	
	/**
	 * 학내 모든 학과 목록을 조건 없이 반환합니다.
	 * 
	 * @return
	 */
	public List<UnivDeptVO> selectAllUnivDepts();
	
	// ----------------------------------------------------------------------
	// DTO 기반의 상세 및 목록 조회 (페이징 포함)
	// ----------------------------------------------------------------------

	/**
	 * 학과 목록 및 검색/필터링 조건에 따른 페이징 전체 갯수 조회
	 */
	public int selectTotalDepartments(Map<String, Object> paramMap);


	/**
	 * 학과 전체 정보 목록 조회 (페이징, 검색 조건 적용)
	 */
	List<Map<String, Object>> selectDepartmentList(Map<String, Object> paramMap);

	/**
	 * 학과 상세 정보 조회
	 */
	DepartmentDetailDTO selectDepartmentDetail(@Param("univDeptCd") String univDeptCd);

	/**
	 * 조건 검색
	 */
	List<Map<String, Object>> selectDepartmentConditions(@Param("univDeptCd") String univDeptCd);


	// ----------------------------------------------------------------------
	// DTO 기반의 등록 및 수정/상태 변경
	// ----------------------------------------------------------------------

	/**
	 * 신규 학과 등록
	 */
	int insertDepartment(DepartmentDetailDTO departmentDTO);

	/**
	 * 학과 정보 수정
	 */
	int updateDepartment(DepartmentDetailDTO departmentDTO);


	/**
	 * 학생, 직원 알림 발송
	 * @param subjectCd
	 * @return
	 */
	public List<String> selectUserUnviDeptObsolete(String univDeptCd);

	/**
     * 전체 학과 목록의 활성(ACTIVE) 및 폐지(DELETED) 상태별 총 개수를 조회합니다.
     * 검색 키워드(searchKeyword)가 있을 경우 이를 반영합니다.
     */
    Map<String, Integer> selectDepartmentStatusCounts(Map<String, Object> paramMap);

    /**
     * 학과 정보 상세조회
     * @param deptKeyword
     * @return
     */
	public CollegeUnvieDeptInfoDTO selectDepartmentInfoByName(String deptKeyword);

	public List<CollegeUnvieDeptInfoDTO> selectDeptListByCollegeName(String collegeKeyword);
	/**
	 * 폐지학과 목록 조회
	 * @return
	 */
	public List<String> selectActiveDepartmentCodes();

	public List<DepartmentDetailDTO> selectAllDepartmentDetails();

}