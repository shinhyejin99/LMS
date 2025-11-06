package kr.or.jsu.lms.staff.service.mypage;

import kr.or.jsu.dto.UserStaffDTO;

/**
 * @author 신혜진
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 *  * << 개정이력(Modification Information) >>
 *  * * 수정일 수정자 수정내용
 *  * ----------- ------------- --------------------------- *
 *   2025. 9. 26.     신혜진              최초 생성 * *
 * </pre>
 */
public interface StaffMyPageService {
	/** * 교직원 단건 조회 * * @param staff * @return * @throws {@link RuntimeException} */
	public UserStaffDTO readStaffDetail(String staffNo) throws RuntimeException;
	/**
	 * * 이 메서드는 로그인한 사원이 자신의 마이페이지에서 내 정보 중 "수정 가능한 일부" 만 수정하는 메서드입니다.
	 * @param staffNo 직원 번호
	 * @param address 폼에서 수정된 주소 정보 (Controller에서 병합됨)
	 * @param user 폼에서 수정된 사용자 정보 (Controller에서 병합됨)
	 * @return
	 */
	public void modifyMyStaffInfo(UserStaffDTO userStaffDTO, String staffNo);
}