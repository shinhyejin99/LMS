package kr.or.jsu.mybatis.mapper.lms.professor.approval;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.info.approval.ApprovalInfo;

/**
 * 강의 개설 신청에 대한 승인만을 다루는 매퍼입니다. <br>
 * 지금 코딩 규칙으로는 approval에 같이 두는 게 맞긴 한데, <br>
 * 괜히 꼬일까봐 + 기존 approvalMapper에는 공용 승인 생성 로직이 없어서 <br>
 * 따로 분리했습니다. 
 * 
 * @author 송태호
 * @since 2025. 10. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 28.     	송태호	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface LctApplyApprovalMapper {

	/**
	 * 새로운 승인 한 건을 생성합니다.
	 * 
	 * @param approval 내용이 오류 없으리라 검증된 승인 한 건 
	 * @return 생성된 레코드 수 + 셀렉트키로 생성된 기본키
	 * @author 송태호
	 */
	public int insertNewApproval(
		ApprovalInfo approval
	);
	
	/**
	 * 승인ID 리스트로 해당 승인ID들의 정보를 한꺼번에 가져옵니다.
	 * 
	 * @param approveIds 승인ID 리스트
	 * @return
	 */
	public List<ApprovalInfo> selectApprovalList(
		List<String> approveIds
	);
	
	/**
	 * 승인의 상태를 변경합니다.
	 * 
	 * @param approval approveId와 승인 상태를 기록한 객체
	 * @return 업데이트된 레코드 수
	 */
	public int updateApprovalStatus(
		ApprovalInfo approval
	);
}