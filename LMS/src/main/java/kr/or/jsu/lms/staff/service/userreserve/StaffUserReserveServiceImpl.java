package kr.or.jsu.lms.staff.service.userreserve;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.mybatis.mapper.UserReserveMapper;

import kr.or.jsu.vo.UserReserveVO;
import lombok.RequiredArgsConstructor;

/**
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
 *  2025.10. 26.     	정태일	          시설예약정보 조회 추가
 *  2025.10. 27.     	신혜진	          시설예약률 계산로직 추가
 *
 *
 *      </pre>
 */

@Service
@RequiredArgsConstructor
public class StaffUserReserveServiceImpl implements StaffUserReserveService {

	private final UserReserveMapper mapper;

	/**
	 * 학생 등록
	 */
	@Override
	public void createStaffUserReserve(UserReserveVO userReserve) {
		mapper.insertUserReserve(userReserve);

	}

	/**
	 * 모든 사용자 시설 예약 정보를 조회합니다.
	 * 검색 조건(searchTerm)이 있을 경우 해당 조건에 따라 필터링된 목록을 조회합니다.
	 *
	 * @param searchTerm 통합 검색어 (시설명, 사용자 이름 등으로 검색)
	 * @return 모든 사용자 시설 예약 정보 목록
	 */
	@Override
	public List<UserReserveVO> readStaffUserReserveList(String searchTerm) { // searchTerm 파라미터 추가
		return mapper.selectUserReservesList(searchTerm); // searchTerm 전달
	}

	/**
	 * 학생 한건 조회
	 */
	@Override
	public UserReserveVO readStaffUserReserve(String reserveId) throws RuntimeException {
		UserReserveVO student = mapper.selectUserReserve(reserveId);
		if (student == null)
			throw new RuntimeException(String.format("%s 관련 학생 없음", reserveId));
		return student;
	}

	/**
	 * 학생 수정
	 */
	@Override
	public void modifyStaffUserReserve(UserReserveVO userReserve) {
		mapper.updateUserReserve(userReserve);

	}
	/**
	 * 전체 예약 가능 슬롯/시간 수를 조회
	 * @return 총 예약 가능 슬롯/시간 수
	 */
	@Override
	public int readTotalReservableSlots() {
		return mapper.selectTotalReservableSlots();
	}
	/**
	 * 현재 예약이 확정된 슬롯/시간 수를 조회
	 * @return 예약이 확정된 슬롯/시간 수
	 */
	@Override
	public int readConfirmedReservationSlots() {
		return mapper.selectConfirmedReservationSlots();
	}

}
