package kr.or.jsu.lms.staff.service.subject;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.vo.SubjectVO;

/**
 *
 *
 * @author 신혜진
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	신혜진	          최초 생성
 *
 *      </pre>
 */
public interface StaffSubjectService {

	/**
	 * 교과목 등록
	 *
	 * @param subject
	 */
	public void createStaffSubject(SubjectVO subject);

	/**
	 * 교과목 전체 조회 (페이징 포함)
	 *
	 * @param pagingInfo
	 * @param filterType
	 * @param cleanKeyword
	 * @return
	 */
	public List<Map<String, Object>> readStaffSubjectList(PaginationInfo<?> pagingInfo, String searchKeyword,
			String filterType);

	/**
	 * 교과목 한건 조회
	 *
	 * @param subjectCd
	 * @return
	 * @throws RuntimeException
	 */
	public SubjectInfoDetailDTO readStaffSubject(String subjectCd) throws RuntimeException;

	/**
	 * 교과목 수정
	 *
	 * @param subject
	 * @return
	 */
	public void modifyStaffSubject(SubjectInfoDetailDTO subject);

	/**
	 * 교과목 전체 조회
	 *
	 * @returny
	 */
	/* List<SubjectInfoDetailDTO> readStaffSubjectList(); */

	/**
	 * 이수별 교과목 갯수
	 *
	 * @return
	 */
	public List<Map<String, Object>> readSubjectCountByType();

	/**
	 * 학과별 교과목 갯수
	 *
	 * @return
	 */
	List<Map<String, Object>> readSubjectCountByDept();

	/**
	 * 학년별 평균 학점 조회
	 *
	 * @return
	 */
	List<Map<String, Object>> readAverageCreditByGrade();

	/**
	 * 활성 교과목 갯수
	 * @return
	 */
	public int readTotalActiveSubjectCount();

	/**
	 * 평균 학점
	 * @return
	 */
	public Double readGlobalAverageCredit();

	public List<Map<String, Object>> readAverageHourByDept();

		

			}
