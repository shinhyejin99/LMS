package kr.or.jsu.lms.student.service.academicChange;

import java.util.List;
import java.util.Map;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.dto.AffilApplyResponseDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import kr.or.jsu.dto.UnifiedApplyResponseDTO;

/**
 *	학적변동 신청 현황 조회 서비스
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25. 		김수현			파일 이름 수정
 *	2025. 10. 13.		김수현			신청 현황 조회 서비스 추가
 *	2025. 10. 15.		김수현			신청 현황 조회 서비스 추가(소속변경)
 * </pre>
 */
public interface StuRecordStatusService {


	/**
	 * 전체 신청 현황 조회 (재학상태변경 + 소속변경)
	 */
	Map<String, Object> getAllApplications(String studentNo);

    /**
     * 통합된 DTO 목록을 반환하는 메서드
     * @param studentNo
     * @return
     */
    List<UnifiedApplyResponseDTO> getUnifiedApplyList(String studentNo);


	/**
     * 학생의 신청 목록 조회(재학상태변경)
     * @param studentNo 학번
     * @return 신청 목록
     */
    public List<RecordApplyResponseDTO> getApplyList(String studentNo);

    /**
     * 신청 상세 조회(재학상태변경)
     * @param applyId 재학상태변경 신청 ID
     * @return 신청 상세 정보
     */
    public RecordApplyResponseDTO getApplyDetail(String applyId);

    /**
     * 학생의 신청 목록 조회(소속변경)
     * @param studentNo 학번
     * @return 신청목록
     */
    public List<AffilApplyResponseDTO> getAffilList(String studentNo);

    /**
     * @param applyId 소속변경 신청 ID
     * @return
     */
    public AffilApplyResponseDTO getAffilDetail(String applyId);


    /**
     * 학적변동 신청 파일 다운로드를 위해 권한 확인 후 파일 메타데이터 및 Resource를 포함한 정보를 반환
     * @param applyId 학적변동 신청 ID
     * @param fileOrder 첨부파일 순서 (JS에서 fileId 대신 사용)
     * @param studentNo 현재 로그인된 학생 학번 (권한 검사용)
     * @return FileDetailInfo (Resource 포함)
     */
    public FileDetailInfo getAppliedFile(String applyId, int fileOrder, String studentNo);
}
