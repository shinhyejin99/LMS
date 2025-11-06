package kr.or.jsu.lms.staff.service.staff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.mybatis.mapper.AddressMapper;
import kr.or.jsu.mybatis.mapper.StaffMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *
 *      </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffManagementServiceImpl implements StaffManagementService {

	private final UsersMapper usersMapper;
	private final StaffMapper mapper;
	private final AddressMapper addressMapper;

	@Override
	@Transactional
	public int createStaffManagement(UserStaffDTO userStaffDto) {

		// 1. DTO → VO 변환
		StaffVO staffVO = new StaffVO();
		// DTO에서 staffInfo를 staffVO로 복사
		BeanUtils.copyProperties(userStaffDto.getStaffInfo(), staffVO);

		// ⭐️⭐️ 교직원 번호(StaffNo) 생성 및 설정 (길이 7로 수정) ⭐️
		if (!StringUtils.hasText(staffVO.getStaffNo()) && StringUtils.hasText(userStaffDto.getHireDateString())) {

			String hireYear = userStaffDto.getHireDateString().substring(0, 4);

			// findMaxSequenceByYear 메서드가 올바른 순번을 반환한다고 가정
			int nextSequence = mapper.findMaxSequenceByYear(hireYear) + 1;

			// ⭐️ ORA-12899 해결: 순번을 3자리로 포맷하여 총 7자리(2025001)로 만듭니다.
			String newStaffNo = hireYear + String.format("%03d", nextSequence);

			staffVO.setStaffNo(newStaffNo);
			// DTO에도 StaffNo를 설정하여 MyBatis가 사용할 수 있도록 합니다.
			userStaffDto.getStaffInfo().setStaffNo(newStaffNo);
		}

		// 2. 필수 필드 처리
		// ... (부서 코드 처리 로직은 생략. DB 제약 조건을 따른다고 가정) ...
		if (userStaffDto.getStaffDeptInfo() != null
				&& StringUtils.hasText(userStaffDto.getStaffDeptInfo().getStfDeptCd())) {
			staffVO.setStfDeptCd(userStaffDto.getStaffDeptInfo().getStfDeptCd());
			userStaffDto.getStaffInfo().setStfDeptCd(userStaffDto.getStaffDeptInfo().getStfDeptCd());
		} else {
			// 기본값 처리 (DB에서 NOT NULL이라면)
			staffVO.setStfDeptCd("STF-HR");
			userStaffDto.getStaffInfo().setStfDeptCd("STF-HR");
		}

		// 3. ADDRESS → USERS → STAFF 순서로 등록
		int insertedCount = 0;

		// ADDRESS 등록
		AddressVO addrVO = userStaffDto.getAddressInfo();
		if (addrVO == null) {
			throw new RuntimeException("주소 정보(AddressVO)가 누락되었습니다.");
		}
		int addrResult = addressMapper.insertAddress(addrVO);
		if (addrResult != 1) {
			throw new RuntimeException("주소 등록 실패");
		}

		insertedCount += addrResult;

		// USERS 등록
		UsersVO userVO = new UsersVO();
		// ⭐️ ORA-01400: FIRST_NAME 해결: DTO의 userInfo 객체에서 userVO로 복사합니다.
		BeanUtils.copyProperties(userStaffDto.getUserInfo(), userVO);

		// 주소 ID 설정
		userVO.setAddrId(addrVO.getAddrId());
		// PW_HASH는 이미 userInfo에서 userVO로 복사되었을 것입니다.

		int userResult = usersMapper.insertUser(userVO);
		if (userResult != 1)
			throw new RuntimeException("사용자 등록 실패");
		insertedCount += userResult;

		// ⭐️⭐️ ORA-01400: USER_ID 해결: 생성된 USER_ID를 DTO에 복사 ⭐️⭐️
		String generatedUserId = userVO.getUserId();
		if (!StringUtils.hasText(generatedUserId)) {
			throw new RuntimeException("사용자 등록 후 USER_ID가 생성되지 않았습니다. MyBatis 설정 확인 필요.");
		}

		// STAFF 등록 시 DTO에서 USER_ID를 가져오므로, DTO의 userInfo에 설정합니다.
		userStaffDto.getUserInfo().setUserId(generatedUserId);

		// --------------------------------------------------------

		// STAFF 등록 (MyBatis XML이 DTO를 받으므로 DTO에 모든 필수 값이 설정되어 있어야 합니다.)
		int profResult = mapper.insertStaffInfo(userStaffDto);
		if (profResult != 1)
			throw new RuntimeException("교직원 등록 실패");
		insertedCount += profResult;

		return insertedCount;
	}

	@Override
	public List<Map<String, Object>> readStaffManagementList(Map<String, Object> paramMap) {

		@SuppressWarnings("unchecked")
		PaginationInfo<Map<String, Object>> pagingInfo = (PaginationInfo<Map<String, Object>>) paramMap
				.get("pagingInfo");

		if (pagingInfo == null) {
			log.error("PaginationInfo가 paramMap에 없습니다.");
			return List.of();
		}

		//  전체 레코드 수 조회 (paramMap을 그대로 사용 -> MyBatis에서 searchKeyword, filterStfDeptCd에 접근)
		int totalRecords = mapper.selectStaffCount(paramMap);
		pagingInfo.setTotalRecord(totalRecords);

		List<Map<String, Object>> staffList = mapper.selectStaffInfoList(paramMap);

		return staffList;
	}

	@Override
	public UserStaffDTO readStaffManagement(String staffNo) throws RuntimeException {
		UserStaffDTO staff = mapper.selectStaffInfo(staffNo);

		if (staff == null) {
			// 학생이 없는 경우 예외 발생
			throw new RuntimeException(String.format("%s 관련 학생 없음", staffNo));
		}

		String regiNoHyphen = staff.getUserInfo().getRegiNo();
		// 주민번호 가져오기

		if (regiNoHyphen != null && regiNoHyphen.length() >= 7) {
			String regiNo = regiNoHyphen.replace("-", "").trim();

			if (regiNo.length() >= 7) {
				char genderCode = regiNo.charAt(6); // 7번째 자리 문자 추출 (예: '1', '2', '3', '4'...)

				// **[수정]** 주민번호의 성별 코드(1, 2, 3, 4, 5, 6)를 'M' 또는 'F'로 변환
				if (genderCode == '1' || genderCode == '3' || genderCode == '5') {
					staff.setGender("M");
				} else if (genderCode == '2' || genderCode == '4' || genderCode == '6') {
					staff.setGender("F");
				} else {
					staff.setGender(null);
				}
			}
		}
		return staff;
	}

	@Override
	public void modifyStaffManagementDetail(UserStaffDTO userStaffDTO) {
//		int result = mapper.updateStaffInfo(staffInfo);
//		if (result != 1) {
//			throw new RuntimeException("교직원 수정 실패");
//		}

	}

	// 재직 상태별 전체 카운트 조회 구현 (파이 차트용) - 인터페이스에 맞게 @Override 추가
	@Override
	public Map<String, Object> readStfDeptStatusCounts() {
		List<Map<String, Object>> rawCountsList = mapper.selectStfDeptStatusCounts();

		// 2. 이 List를 JSP가 원하는 Map<String, Integer> 형태로 변환합니다.
		Map<String, Object> finalCountsMap = new HashMap<>();

		if (rawCountsList != null) {
			for (Map<String, Object> item : rawCountsList) {
				// JSP에서 부서이름(인사처, 행정처)을 키로 사용하기 위해 'STATUS' 대신 부서 이름을 사용해야 함
				// MyBatis 쿼리가 부서 이름을 'DEPT_NAME'으로 반환한다고 가정하고 수정
				String deptName = (String) item.get("DEPT_NAME"); // MyBatis 쿼리의 컬럼 이름 확인 필요

				// COUNT 값은 BigDecimal 등 숫자로 올 수 있으므로 Integer로 변환
				Number countValue = (Number) item.get("COUNT");

				if (deptName != null && countValue != null) {
					finalCountsMap.put(deptName, countValue.intValue());
				}
			}
		}

		// 3. 변환된 Map을 반환합니다.
		return finalCountsMap;
	}

}