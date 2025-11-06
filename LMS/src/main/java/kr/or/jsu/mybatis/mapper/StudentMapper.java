package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.SemesterGradeDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.dto.StudentSimpleDTO;
import kr.or.jsu.vo.StudentVO;

/**
 *
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	송태호	          최초 생성
 *  2025. 10. 13.		김수현			총 이수 학점 조회 추가

 *	2025. 10. 14.		신혜진			학생 학적 상태 갯수 조회 추가
 *
 *  2025. 10. 14.		김수현			학생 평균학점(GPA) 조회 추가
 *  2025. 10. 24.		김수현 			학생 학기별 평균 평점 조회 추가
 *
 * </pre>
 */
@Mapper
public interface StudentMapper {

	/**
	 * 공통 사용자 정보를 제외한 학생 정보만 가져오는 메서드입니다. <br>
	 * 사실상 거의 쓸모가 없어요.
	 *
	 * @author 송태호, 09.25
	 * @param studentNo
	 * @return
	 */
	@Deprecated
	public StudentVO selectStudent(String studentNo);

	/**
	 * 학번으로 공통+학생 정보, 소속 학과+단과대의 정보까지 가져오는 메서드입니다.
	 *
	 * @author 송태호, 09.25
	 * @param studentNo 목표 학생의 학번
	 * @return 목표 학생의 공통+학생 정보, 소속학과+단과대 정보. 존재하지 않는 학번인 경우 null
	 */
	public StudentSimpleDTO selectBasicStudentInfo(String studentNo);

	/**
	 * 학번으로 공통+학생 정보, 학생의 신입생 입학정보+군대정보까지 가져오는 메서드입니다. <br>
	 * 학생의 신입생 입학 정보가 없거나(신입생이 아님) 병역 기록이 없으면 해당 부분은 ...
	 *
	 * @author 송태호, 09.25
	 * @param studentNo 공통 + 학생 정보, 입학 + 군대 정보
	 * @return
	 */
	public StudentDetailDTO selectStudentDetailInfo(String studentNo);

	/**
	 * 학번으로 학생의 상세 정보를 조회합니다.
	 * (전화번호, 이메일, 주소 포함)
	 * @param studentNo 학생 학번
	 * @return StudentVO 학생 상세 정보
	 */
	public StudentVO selectStudentDetailsByStudentNo(String studentNo);

	// 전체 조회
	public List<StudentVO> selectStudentList();

	/**
	 * 교수의 지도 학생 목록을 조회합니다.
	 *
	 * @param paramMap 교번, 학년도, 학기를 포함하는 맵
	 * @return List<StudentVO> 학생 목록
	 */
	public List<StudentVO> selectAdvisingStudentList(Map<String, Object> paramMap);

	/**
	 * 교수의 지도 학생 수를 조회합니다.
	 *
	 * @param paramMap 교번, 학년도, 학기를 포함하는 맵
	 * @return int 학생 수
	 */
	public int selectAdvisingStudentCount(Map<String, Object> paramMap);

	/**
	 * 교직원 학생정보 목록을 전체 조회합니다. 페이징처리
	 *
	 * @author 신혜진, 09.29
	 * @return
	 */
	public List<Map<String, Object>> selectStaffStudentInfoList(Map<String, Object> paramMap);

	/**
	 * 교직원 학생정보 목록을 전체 조회합니다. 폐이징 및 검색조건
	 *
	 * @author 신혜진, 09.30
	 * @return
	 */
	public int selectStudentCount(Map<String, Object> paramMap);

	/**
	 * 교직원 학생정보 목록을 수정 합니다.
	 *
	 * @author 신혜진, 09.30
	 * @return
	 */
	public int updateStaffStudentInfo(StudentDetailDTO studentDto);

	/**
	 * 교직원 학생정보 등록 합니다.
	 *
	 * @author 신혜진, 10.01
	 * @return
	 */
	public int insertStaffStudentInfo(StudentDetailDTO studentDto);

	/**
	 * 교직원이 학생 등록시 자동 학번생성
	 *
	 * @author 신혜진, 10.01
	 * @param year
	 * @return
	 */
	public int findMaxSequenceByYear(@Param("year") String year);

	/**
	 * 학생 - 마이페이지에서 인적정보 수정 (학생, 사용자, 주소 테이블 업데이트)
	 *
	 * @author 김수현, 10.01
	 * @param studentDto
	 * @return
	 */
	public int updateUserDetailInfo(StudentDetailDTO studentDto);

	public int updateAddressInfo(StudentDetailDTO studentDto);

	public int updateStudentPersonalInfo(StudentDetailDTO studentDto);

	int insertBatchStaffStudentInfo(List<StudentVO> studentList);

	// 등록
	public int insertStudent(StudentVO student);

	// 수정
	public int updateStudent(StudentDetailDTO studentDtoForStudentTable);

	// 삭제
	public int deleteStudent(String studentNo);

	/**
	 * 총 이수 학점 조회
	 *
	 * @param studentNo
	 * @return
	 */
	public int selectTotalCredit(String studentNo);


	/**
	 * 학생 학적 상태 수 조회
	 *
	 * @return
	 */
	public List<Map<String, Object>> selectStudentStatusCounts();

	/**
	 * 학생 학적 상태 따라 단과 학생 수 조회
	 *
	 * @return
	 */
	public List<Map<String, Object>> selectStudentCountsByCollege(String stuStatusName);

	/**
	 * 학생 단과대에 따라 학과 학생 수 조회
	 *
	 * @return
	 */
	public List<Map<String, Object>> selectStudentCountsByDepartment(Map<String, Object> paramMap);

	/**
	 * 학생 학과에 따라 학년 학생 수 조회
	 *
	 * @return
	 */
	public List<Map<String, Object>> selectStudentCountsByGrade(Map<String, Object> paramMap);

	public List<Map<String, Object>> selectStudentListForExcel(Map<String, Object> paramMap);



	/**
	 * 학생 평균 학점(GPA) 조회
	 */
	public Double selectStudentGPA(String studentNo);

	/**
	 * 학생 소속 학과 코드 조회
	 */
	public String selectDeptCd(String studentNo);

	/**
	 * 학생 소속 단과대학 코드 조회
	 */
	public String selectCollegeCd(String studentNo);

	/**
	 * 학생) 학년코드 조회
	 * @param studentNo
	 * @return
	 */
	public String selectGradeCd(String studentNo);

	/**
	 * 학생의 학기별 평균 평점 조회
	 * @param studentNo 학번
	 * @return 학기별 평균 평점 리스트
	 */
	public List<SemesterGradeDTO> selectSemesterGrades(@Param("studentNo") String studentNo);

	/**
	 * 학생 성별 비중 통계
	 * @return
	 */
	public List<Map<String, Object>> selectOverallGenderStatistics();

	/**
	 * 학년별 학생 수
	 * @return
	 */
	public List<Map<String, Object>> selectOverallGradeStatistics();

}