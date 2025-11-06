package kr.or.jsu.lms.staff.service.staff;

import java.util.List;
import java.util.Map;

import kr.or.jsu.dto.UserStaffDTO;

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
public interface StaffManagementService {

	/**
	 * 교직원 등록
	 *
	 * @param staff
	 * @return
	 */
	public int createStaffManagement(UserStaffDTO userStaffDto);

	/**
	 * 교직원 다건 조회
	 *
	 * @return
	 */
	public List<Map<String, Object>> readStaffManagementList(Map<String, Object> paramMap);

	/**
	 * 교직원 단건 조회
	 *
	 * @param staff
	 * @return
	 * @throws {@link RuntimeException}
	 */
	public UserStaffDTO readStaffManagement(String staffNo) throws RuntimeException;

	/**
	 * 교직원 수정
	 *
	 * @param userStaffDTO
	 * @return
	 *
	 */
	public void modifyStaffManagementDetail(UserStaffDTO userStaffDTO);

	/**
	 * 부서별 교직원 수 통계 조회 (파이 차트용)
	 *
	 * @return Map<부서이름, 교직원수>
	 */
	public Map<String, Object> readStfDeptStatusCounts();

}
