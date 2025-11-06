package kr.or.jsu.lms.professor.service.academicChangeStatus;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.UserStaffDTO;

/**
 * 
 * 
 * @author 신혜진
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	신혜진	          최초 생성
 *
 * </pre>
 */
public interface profAcademicChangeStatusService {

	/**
	 * 교수 학생 학적변동 신청 등록
	 * @return
	 */
	public int createProfAcademicChangeStatus();
	
	/**
	 * 교수 학생 학적변동 신청 전체 조회
	 * @return
	 */
	public List<Map<String, Object>> readProfAcademicChangeStatusList(PaginationInfo<Map<String, Object>> pagingInfo);

	/**
	 * 교수 학생 학적변동 상세 조회
	 * 
	 * @param staff
	 * @return
	 * @throws {@link RuntimeException}
	 */
	public UserStaffDTO readProfAcademicChangeStatus(String staffNo) throws RuntimeException;

	/**
	 * 교수 학생 학적변동 수정
	 * 
	 * @param userStaffDTO
	 * @return
	 * 
	 */
	public void modifyProfAcademicChangeStatus (UserStaffDTO userStaffDTO);

	
}
