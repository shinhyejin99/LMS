package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.core.dto.info.ProfessorInfo;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.vo.LectureVO;
import kr.or.jsu.vo.ProfessorVO;

@Mapper
public interface ProfessorMapper {

	/**
	 * 플랫 객체로, USERS + PROFESSOR 까지만 조인한 교수정보 조회 <br>
	 * 코드성 데이터는 조인해오지 않으므로, 직접 cache로 넣어서 써야 합니다.
	 * @param professorNo 교번
	 * @return
	 * @author 송태호, 10.15
	 */
	public ProfessorInfo selectBasicProfessorInfo(
		String professorNo
	);
	
	/**
	 * 플랫 객체로, USERS + PROFESSOR 까지만 조인한 교수정보 조회 <br>
	 * 코드성 데이터는 조인해오지 않으므로, 직접 cache로 넣어서 써야 합니다. <br>
	 * 사용자ID로 검색하므로, 결과가 null일 경우 <br>
	 * 1. 해당 사용자가 없거나 <br>
	 * 2. 해당 사용자가 있어도 교수가 아닌 경우입니다.
	 * @param userId 사용자 번호
	 * @return
	 * @author 송태호, 10.15
	 */
	public ProfessorInfo selectBasicProfessorInfoByUserId(
		String userId
	);

	/**
	 * 특정 학과의 학과장 교수를 가져옵니다. <br>
	 * 플랫 객체로, USERS + PROFESSOR 까지만 조인한 교수정보 조회 <br>
	 * 코드성 데이터는 조인해오지 않으므로, 직접 cache로 넣어서 써야 합니다.
	 * 
	 * @param univDepfCd 학과 코드
	 * @return 학과장 정보, 없으면 null
	 */
	public ProfessorInfo selectDeptCheifProfessor(
		String univDepfCd
	);
	
	public ProfessorInfoDTO selectProfessorInfoView(String professorNo);

	public int insertProfessor(ProfessorVO professor);

	public List<ProfessorVO> selectProfessorList();

	public ProfessorVO selectProfessor(String professorNo);

	public int updateProfessor(ProfessorVO professor);

	public int deleteProfessor(String professorNo);

	/**
	 * 교직원 교수정보 목록 전체 조회
	 * 페이징
	 * @author 신혜진, 09.30
	 * @return
	 */
	public List<ProfessorInfoDTO> selectStaffProfessorInfoList(Map<String, Object> paramMap);
	/**
	 * 교직원 교수정보 목록을 전체 조회
	 * 검색조건
	 * @author 신혜진, 09.30
	 * @param paramMap
	 * @return
	 */
	public int selectProfessorCount(Map<String, Object> paramMap);

	/**
	 * 교직원 교수 정보 수정
	 * @param professorInfoDto
	 * @return
	 */
	public int  updateProfessorInfo(ProfessorInfoDTO professorInfoDto);

	/**
	 * 교직원 교수정보 등록 합니다.
	 *
	 * @author 신혜진, 10.01
	 * @return
	 */
	public int insertStaffProfessorInfo(ProfessorInfoDTO ProfessorInfoDTO);
	/**
	 *  교직원이 교수 등록시 자동 교번생성
	 *   @author 신혜진, 10.01
	 * @param year
	 * @return
	 */
	public int findMaxSequenceByYear(@Param("year") String year);

	/**
	 * 교수 재직 상태별 전체 카운트 조회 (파이 차트용)
	 * @return 재직 상태 이름(NAME)과 카운트(COUNT)를 포함하는 맵 리스트
	 */
	public List<Map<String, Object>> selectEmploymentStatusCounts();

	/**
	 * 특정 상태의 단과대학별 교수 통계 조회 (모달 1단계)
	 * @param paramMap 상태 정보를 담은 Map (status)
	 * @return 단과대학명: 카운트 맵
	 */
	public List<Map<String, Object>> selectProfessorStatsByCollege(Map<String, String> paramMap);

	/**
	 * 특정 상태/단과대학의 학과별 교수 통계 조회 (모달 2단계)
	 * @param paramMap 상태, 단과대학 정보를 담은 Map (status, college)
	 * @return 학과명: 카운트 맵
	 */
	public List<Map<String, Object>> selectProfessorStatsByDepartment(Map<String, String> paramMap);

	/**
	 * 특정 상태/단과대학/학과의 직위별 교수 통계 조회 (모달 3단계)
	 * @param paramMap 상태, 단과대학, 학과 정보를 담은 Map (status, college, department)
	 * @return 직위명: 카운트 맵
	 */
	public List<Map<String, Object>> selectProfessorStatsByPosition(Map<String, String> paramMap);

	int selectTotalAdvisedStudentsCount(String professorNo);

	int selectLeaveOfAbsenceAdvisedStudentsCount(String professorNo);

	int selectWithdrawalAdvisedStudentsCount(String professorNo);

	public List<LectureVO> selectRecentConfirmedLectures(String professorNo);

	public List<Map<String, Object>> selectAdviseeGrades(String professorNo);

	    public List<Map<String, Object>> selectAdviseeGradeYearCounts(String professorNo);
	
	    public List<Map<String, Object>> selectLectureGradeDistributions(String professorNo);
	
		public List<ProfessorInfoDTO> selectProfessorListForSearch(Map<String, Object> paramMap);}
