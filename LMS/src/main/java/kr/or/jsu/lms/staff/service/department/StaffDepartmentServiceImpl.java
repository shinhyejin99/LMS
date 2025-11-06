package kr.or.jsu.lms.staff.service.department;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.DepartmentDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.mybatis.mapper.UnivDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffDepartmentServiceImpl implements StaffDepartmentService {

    private final UnivDeptMapper mapper;
    private final UserNotificationCreateService notificationService;

    @Override
    public List<Map<String, Object>> readDepartmentList(PaginationInfo<?> pagingInfo, String searchKeyword, String filterType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("searchKeyword", searchKeyword);
        paramMap.put("filterType", filterType);

        int totalCount = mapper.selectTotalDepartments(paramMap);
        pagingInfo.setTotalRecord(totalCount);

        paramMap.put("firstIndex", pagingInfo.getStartRow());
        paramMap.put("lastIndex", pagingInfo.getEndRow());

        return mapper.selectDepartmentList(paramMap);
    }

    @Override
    public DepartmentDetailDTO readDepartment(String univDeptCd) {
        return mapper.selectDepartmentDetail(univDeptCd);
    }

    @Transactional
    @Override
    public boolean createDepartment(DepartmentDetailDTO departmentDTO) {
        if (departmentDTO.getUnivDeptCd() == null || departmentDTO.getUnivDeptName() == null)
            throw new IllegalArgumentException("필수 학과 정보가 누락되었습니다.");
        return mapper.insertDepartment(departmentDTO) == 1;
    }

    @Override
    public boolean modifyDepartment(DepartmentDetailDTO departmentDTO) {
        boolean isObsolete = "DELETED".equals(departmentDTO.getStatus());
        departmentDTO.setDeleteAt(isObsolete ? LocalDate.now() : null);

        int updateCount = mapper.updateDepartment(departmentDTO);
        if (updateCount == 1 && isObsolete)
            sendSubjectObsoleteNotification(departmentDTO);

        return updateCount == 1;
    }


    private void sendSubjectObsoleteNotification(DepartmentDetailDTO department) {
        List<String> userIds = mapper.selectUserUnviDeptObsolete(department.getUnivDeptCd());
        if (userIds.isEmpty()) return;

        for (String userId : userIds) {
            AutoNotificationRequest alert = AutoNotificationRequest.builder()
                .receiverId(userId)
                .title("📢 중요 안내: 학과 폐지 처리 (" + department.getUnivDeptName() + ")")
                .content("학과 [" + department.getUnivDeptName() + "]가 폐지되었습니다. 폐지일: " + department.getDeleteAt())
                .senderName("LMS 행정팀")
                .pushUrl("/lms/department/detail/" + department.getUnivDeptCd())
                .build();
            notificationService.sendAutoNotification(alert);
        }
        log.info("폐지 알림 완료: {}명에게 전송", userIds.size());
    }

    /**
     * 학과 상태별 전체 카운트 조회
     */
    public Map<String, Integer> readDepartmentStatusCounts(Map<String, Object> paramMap) {
        // Mapper 호출 (검색 키워드가 있다면 필터링된 전체 카운트 반환)
        return mapper.selectDepartmentStatusCounts(paramMap);
    }

	/**
	 * ⭐️ 활성 상태인 학과 코드 목록만 조회 (폐지된 학과 제외)
	 * Chatbot Service에서 전체 학과 목록 필터링용으로 사용됩니다.
	 */
	@Override
	public List<String> readActiveDepartmentCodes(Object object) {
		// Mapper를 통해 STATUS = 'ACTIVE' 또는 DELETE_AT IS NULL인 학과 코드를 조회합니다.
		// MyBatis Mapper에 selectActiveDepartmentCodes 메서드가 선언되어 있어야 합니다.
		try {
			// Object object는 보통 searchParam 등이 들어오지만, 여기서는 null로 가정하고 전체 활성 코드를 조회합니다.
			// 실제 구현에 맞게 파라미터를 사용하거나 무시할 수 있습니다.
			return mapper.selectActiveDepartmentCodes();
		} catch (Exception e) {
			log.error("활성 학과 코드 조회 중 오류 발생", e);
			return List.of(); // 오류 시 빈 리스트 반환
		}
	}

	/**
	 * 전체 학과 조회
	 */
	@Override
	public List<DepartmentDetailDTO> selectAllDepartmentDetails() {
		return mapper.selectAllDepartmentDetails();
	}
}