package kr.or.jsu.lms.staff.service.department;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.DepartmentDetailDTO;

public interface StaffDepartmentService {



	/**
	 * 학과 상세 조회
	 * @param univDeptCd 학과 코드
	 * @return 학과 상세 정보 DTO
	 */
	DepartmentDetailDTO readDepartment(String univDeptCd);

	/**
	 * 학과 등록
	 * @param departmentDTO 등록할 학과 정보
	 * @return 등록 성공 여부
	 */
	boolean createDepartment(DepartmentDetailDTO departmentDTO);

	/**
	 * 학과 정보 수정  
	 * @param departmentDTO 수정할 학과 정보
	 * @return 수정 성공 여부
	 */
	boolean modifyDepartment(DepartmentDetailDTO departmentDTO);



	/**
	 * 학과 목록 및 페이징 조회
	 *
	 * @param pagingInfo    페이징 정보를 담은 객체
	 * @param searchKeyword 검색어
	 * @param filterType    필터 (ACTIVE/DELETED)
	 * @return 학과 목록 (List<Map<String, Object>>)
	 */
	List<Map<String, Object>> readDepartmentList(PaginationInfo<?> pagingInfo, String searchKeyword, String filterType);

	Map<String, Integer> readDepartmentStatusCounts(Map<String, Object> countParamMap);

	List<String> readActiveDepartmentCodes(Object object);

	List<DepartmentDetailDTO> selectAllDepartmentDetails();





}