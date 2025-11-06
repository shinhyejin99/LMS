package kr.or.jsu.lms.staff.service.professor;

import java.util.List;
import java.util.Map;

import kr.or.jsu.dto.ProfessorInfoDTO;


public interface StaffProfessorInfoService {


	/**
	 * 교직원이 교수 정보등록
	 * @param professorDto
	 * @return
	 */
	public int createStaffProfessorInfo(ProfessorInfoDTO professorDto);

	/**
     * 교직원이 교수 정보를 전체 조회 하는 메서드 (페이징 및 검색 통합)
     *
     * @param paramMap 페이징 정보(pagingInfo), 검색 키워드, 필터링 조건 등을 담은 Map
     * @return 조회된 교수 목록 (List<ProfessorInfoDTO>)
     */
	public List<ProfessorInfoDTO> readStaffProfessorInfoList(Map<String, Object> paramMap);

	/**
	 * 교수 재직 상태별 전체 카운트 조회 (파이 차트용)
	 * @return 재직 상태(String): 카운트(Integer) 맵
	 */
	public Map<String, Integer> readEmploymentStatusCounts();

	/**
	 * 특정 상태의 단과대학별 교수 통계 조회 (모달 1단계)
	 * @param paramMap 상태 정보를 담은 Map (status)
	 * @return 단과대학명: 카운트 맵
	 */
	public Map<String, Integer> readProfessorStatsByCollege(Map<String, String> paramMap);

	/**
	 * 특정 상태/단과대학의 학과별 교수 통계 조회 (모달 2단계)
	 * @param paramMap 상태, 단과대학 정보를 담은 Map (status, college)
	 * @return 학과명: 카운트 맵
	 */
	public Map<String, Integer> readProfessorStatsByDepartment(Map<String, String> paramMap);

	/**
	 * 특정 상태/단과대학/학과의 직위별 교수 통계 조회 (모달 3단계)
	 * @param paramMap 상태, 단과대학, 학과 정보를 담은 Map (status, college, department)
	 * @return 직위명: 카운트 맵
	 */
	public Map<String, Integer> readProfessorStatsByPosition(Map<String, String> paramMap);

	/**
	 * 교직원이 교수 정보 한건 조회
	 * @param professorNo
	 * @return
	 * @throws {@link RuntimeException}
	 */
	public ProfessorInfoDTO readStaffProfessorInfo(String professorNo) throws RuntimeException;

	/**
	 * 교직원이 교수 정보수정
	 * @param professorDto
	 * @return
	 */
	public void modifyStaffProfessorInfo(ProfessorInfoDTO professorDto);

	/**
	 * 학과별 교수 목록
	 * @param searchKeyword
	 * @return
	 */


	public List<ProfessorInfoDTO> readProfessorListForStudentMapping(Map<String, Object> paramMap);


}