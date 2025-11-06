package kr.or.jsu.lms.professor.service.academicChangeStatus;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.mybatis.mapper.UnivAffilApplyMapper;
import kr.or.jsu.mybatis.mapper.UnivAffilHistoryMapper;
import kr.or.jsu.mybatis.mapper.UnivRecordApplyMapper;
import kr.or.jsu.mybatis.mapper.UnivRecordHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class profAcademicChangeStatusServiceImpl implements profAcademicChangeStatusService{
	
	private final UnivRecordApplyMapper RecordApplyMapper;
	private final UnivRecordHistoryMapper recordHistoryMapper;
	private final UnivAffilApplyMapper affilApplyMapper;
	private final UnivAffilHistoryMapper affHistoryMapper;
	
	@Override
	public int createProfAcademicChangeStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> readProfAcademicChangeStatusList(PaginationInfo<Map<String, Object>> pagingInfo) {
		// 1. 전체 레코드 수 조회 (내부 호출)
				int totalRecords = RecordApplyMapper.selectProfAcademicChangeStatusCount(pagingInfo);

				// 2. PaginationInfo에 총 건수 설정
				pagingInfo.setTotalRecord(totalRecords);

				// 3. 페이징된 목록 조회
				List<Map<String, Object>> staffList = RecordApplyMapper.selectProfAcademicChangeStatusList(pagingInfo);

				return staffList;
			}

	@Override
	public UserStaffDTO readProfAcademicChangeStatus(String staffNo) throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyProfAcademicChangeStatus(UserStaffDTO userStaffDTO) {
		// TODO Auto-generated method stub
		
	}

}
