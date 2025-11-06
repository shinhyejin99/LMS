package kr.or.jsu.portal.service.job;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.JobDetailDTO;
import kr.or.jsu.dto.JobListItemDTO;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.vo.PortalRecruitVO;

/**
 *
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 26.		김수현			학내 채용정보 메소드 추가
 *	2025. 9. 29.		김수현			조회수 증가 추가, 조회 메소드 수정(페이징, 검색), 공공채용 관련 추가
 *	2025. 9. 30.		김수현			교직원 부서명 메소드 추가
 * </pre>
 */
public interface PortalJobService {


	// === 공공 채용 (고용24 API) ===
    /**
     * 공공 채용정보 목록 조회
     * @param paging
     * @return
     */
    List<JobListItemDTO> readPublicRecruitList(PaginationInfo<PortalRecruitVO> paging);

    /**
     * 공공 채용정보 상세보기
     * @param empSeqno
     * @return
     */
    JobDetailDTO readPublicRecruitDetail(String empSeqno);

    //================================

	/**
	 * 학내 채용정보 등록(교직원만)
	 * 파일 처리 및 게시글 DB 저장을 하나의 트랜잭션으로 묶어 처리
	 * @param portalRecruit 채용정보VO
	 * @param files 첨부 파일 목록
	 * @param uploaderUserId 파일 업로더(작성자) ID
	 * @return true/false
	 */
	public boolean createSchRecruit(PortalRecruitVO portalRecruit, List<MultipartFile> files, String uploaderUserId);

	/**
	 * 학내 채용정보 조회(모두)
	 * @return List<PortalRecruitVO>
	 */
	public List<PortalRecruitVO> readSchRecruitList(PaginationInfo<PortalRecruitVO> paging);

	/**
	 * 학내 채용정보 상세보기
	 * @param recruitId
	 * @return PortalRecruitVO
	 */
	public SchRecruitDetailDTO readSchRecruitDetail(String recruitId);

	/**
	 * 학내 채용정보 수정 (교직원만)
	 * @param portalRecruit
	 * @return true/false
	 */
	public boolean modifySchRecruit(PortalRecruitVO portalRecruit);

	/**
	 * 학내 채용정보 삭제 (교직원만)
	 * @param recruitId
	 * @return true/false
	 */
	public boolean removeSchRecruit(String recruitId);

	/**
	 * 채용정보 조회수 증가
	 * @param recruitId
	 * @return
	 */
	public boolean modifyIncrementViewCount(String recruitId);


	/**
	 * 교직원 부서명 select
	 * @param stfDeptCd
	 * @return
	 */
	public String readStfDeptNameByCode(String stfDeptCd);

	//==============================
	/**
	 * 학생 맞춤형 채용정보
	 * @param paging
	 * @param studentNo
	 * @return
	 */
	public List<JobListItemDTO> readStudentRecruitList(PaginationInfo<PortalRecruitVO> paging, String studentNo);

	/**
     * 학생 맞춤형 채용정보 상세보기
     * @param empSeqno
     * @return
     */
    JobDetailDTO readStudentRecruitDetail(String empSeqno);

    /**
     * 학내 채용 첨부파일 다운로드 처리
     * @param recruitId 채용 공고 ID
     * @param fileOrder 파일 순번
     * @param userId 요청자 ID (권한 검사용, null 가능)
     * @return 파일 상세 정보
     */
    public FileDetailInfo getRecruitFile(String recruitId, int fileOrder, String userId);

}
