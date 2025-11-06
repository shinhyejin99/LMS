package kr.or.jsu.mybatis.mapper.cache;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.core.dto.info.CollegeInfo;
import kr.or.jsu.core.dto.info.CommonCodeInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.dto.info.UsersInfo;
import kr.or.jsu.dto.info.StaffDeptInfo;


/**
 * 인메모리에 캐시를 저장하기 위한 클래스입니다.
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
 *  2025. 9. 30.     	송태호	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface CacheMapper {
	
	public List<CommonCodeInfo> selectAllCommonCode();
	
	public List<UsersInfo> selectAllUsers();
	
	public List<UnivDeptInfo> selectAllUnivDept();
    
	public List<CollegeInfo> selectAllCollege();
	
	public List<SubjectInfo> selectAllSubject();

	public List<StaffDeptInfo> selectAllStaffDept();
	
	public String selectCurrentYearterm();
	
}
