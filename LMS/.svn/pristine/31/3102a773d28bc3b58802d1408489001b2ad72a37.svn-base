package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.RecordApplyInfoDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;

/**
 * 학적변동(소속 변경 제외) 신청 mapper (휴학/복학/졸업유예/자퇴)
 * @author 김수현
 * @since 2025. 10. 10.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 10.     	김수현	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface RecordApplyMapper {

	/**
	 * 학적변동 신청 등록(공통) - 학생_학적변동신청 테이블
	 * recordChangeCd로 구분함(DROP/REST/RTRN/DEFR)
	 * @param applyInfo 신청 정보 (applyID : 시퀀스)
	 * @return
	 */
	public int insertRecordApply(RecordApplyInfoDTO applyInfo);

	/**
	 * 신청 상세 조회
	 * @param applyId 학적변동신청 ID
	 * @return 신청 상세 정보
	 */
	public RecordApplyResponseDTO selectApplyDetail(String applyId);

	/**
	 * 학생) 신청 목록 조회
	 * @param studentNo 학번
	 * @return 학생) 모든 학적변동 신청 목록
	 */
	public List<RecordApplyResponseDTO> selectApplyListByStudent(String studentNo);

	/**
	 * 신청 취소 - PENDING 상태일 때만 삭제 가능
	 * @param applyId 학적변동신청ID
	 * @return
	 */
	public int deleteApply(String applyId);

	/**
	 * 신청 상태 변경
	 * -> 이건 교수랑 교직원이 사용할 기능인데 우선 만듦
	 * @param applyId 학적변동신청ID
	 * @param applyStatusCd 변경할 상태 코드 (PENDING/IN_PROGRESS/APPROVED/REJECTED)
	 * @return
	 */
	public int updateApplyStatus(String applyId, String applyStatusCd);

	// 신청 가능 여부 체크 ===================================

	/**
	 * 학생의 현재 학적 상태 조회
	 * @param studentNo 학번
	 * @return 학적상태코드 (ENROLLED/ON_LEAVE/WITHDRAWN 등)
	 */
	public String selectStudentStatus(String studentNo);

	/**
	 * 학생의 진행중인 신청 개수 조회 (중복 신청 방지용)
	 * @param studentNo 학번
	 * @param recordChangeCd 학적변동타입코드 (DROP/REST/RTRN/DEFR)
	 * @return 진행중인 신청 개수 (0이면 신청 가능)
	 */
	public int countPendingApply(String studentNo, String recordChangeCd);

	// ==================================================


	// 승인 절차============================================

    /**
     * 지도교수 USER_ID 조회
     * @param studentNo 학번
     * @return
     */
    public String selectProfessorUserId(String studentNo);

    /**
     * 학과장 USER_ID 조회
     * @param deptCd 학과코드
     * @return
     */
    public String selectDeptHeadUserId(String deptCd);

    // ==================================================


    // 중복 신청 방지 ============================================
    /**
     * 휴학 신청 중복 확인
     * @param studentNo
     * @return
     */
    public boolean existsPendingLeave(String studentNo);

    /**
     * 복학 신청 중복 확인
     * @param studentNo
     * @return
     */
    public boolean existsPendingReturn(String studentNo);

    /**
     * 졸업유예 신청 중복 확인
     * @param studentNo
     * @return
     */
    public boolean existsPendingDefer( String studentNo);
    // ==================================================
}
