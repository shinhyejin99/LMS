package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;


/**
 *
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	김수현	          게시판 관련 교직원 부서명 select 추가
 *
 * </pre>
 */
@Mapper
public interface StaffMapper {

	// 단건 조회
	public UserStaffDTO selectStaffInfo(String staffNo);

	// 전체 조회
	public List<Map<String, Object>> selectStaffInfoList(Map<String, Object> paramMap);

	public int selectStaffCount(Map<String, Object> paramMap);

	/**
	 * user 등록
	 * @param user
	 * @return
	 */
	 int insertUser(UsersVO user);
	 /**
	  * staff 등록
	  * @param staff
	  * @return
	  */
	 int insertStaff(StaffVO staff);

	 /**
	  * user 수정
	  * @param userStaff
	  * @return
	  */
	 int updateUserInfo( UsersVO userInfo);

	 /**
	  * staff 수정
	  * @param userStaff
	  * @return
	  */
	 int updateStaffInfo( StaffVO staffInfo);


	 /**
	  * 게시글 교직원 권한 관련 부서명 select
	 * @param stfDeptCd
	 * @return
	 */
	public String selectStfDeptNameByCode(String stfDeptCd);

	/**
	 * 교직원 교직원정보 등록 합니다.
	 *
	 * @author 신혜진, 10.02
	 * @return
	 */
	public int insertStaffInfo(UserStaffDTO userStaffDTO);
	/**
	 *  교직원이 교직원 등록시 자동 교번생성
	 *   @author 신혜진, 10.02
	 * @param year
	 * @return
	 */
	public int findMaxSequenceByYear(@Param("hireYear") String year);

	/**
	 * 부서별 교직원 수 통계 조회 (파이 차트용)
	 * @return List<Map<String, Object>> (DEPT_NAME, COUNT)
	 */
	public List<Map<String, Object>> selectStfDeptStatusCounts();



}
