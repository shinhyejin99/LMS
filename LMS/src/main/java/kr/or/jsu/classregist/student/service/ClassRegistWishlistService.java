package kr.or.jsu.classregist.student.service;

import java.util.List;

import kr.or.jsu.classregist.common.LectureSearchCondition;
import kr.or.jsu.classregist.dto.AppliedLectureDTO;
import kr.or.jsu.classregist.dto.ApplyStatusDTO;
import kr.or.jsu.classregist.dto.LectureDetailDTO;
import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.WishlistDTO;
import kr.or.jsu.classregist.dto.WishlistResponseDTO;
import kr.or.jsu.classregist.dto.WishlistSearchDTO;

/**
 * 예비수강신청 서비스 인터페이스
 *
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *  2025. 10. 18.		김수현			수강신청 관련 추가
 *
 * </pre>
 */
public interface ClassRegistWishlistService {

    /**
     * 찜 목록 조회
     *
     * @param paging
     * @param studentNo 학번
     * @return
     */
    public List<WishlistDTO> getWishlistWithPaging(WishlistSearchDTO searchDTO);

    /**
     * 찜 목록 전체 개수
     */
    int getWishlistCount(WishlistSearchDTO searchDTO);

    /**
     * 강의 목록 조회
     *
     * @param paging
     * @param studentNo 학번
     * @param yearTermCd 학기코드
     * @return
     */
    public List<LectureListDTO> getLectureListWithSearch(LectureSearchCondition searchCondition, String studentNo);

    /**
     * 검색용
     * @param searchCondition
     * @return
     */
    public int getLectureCount(LectureSearchCondition searchCondition);

    /**
     * 강의 상세 조회
     *
     * @param lectureId 강의ID
     * @return
     */
    public LectureDetailDTO getLectureDetail(String lectureId);

    /**
     * 찜하기
     *
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public WishlistResponseDTO addWishlist(String studentNo, String lectureId);

    /**
     * 찜 취소
     *
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public boolean removeWishlist(String studentNo, String lectureId);

    /**
     * 찜한 강의들의 총 학점 조회
     * @param studentNo 학번
     * @return
     */
    public Integer sumWishlistCredits(String studentNo);


    // ======== 수강신청 ================

    /**
     * 수강신청
     */
    public WishlistResponseDTO applyLecture(String studentNo, String lectureId);

    /**
     * 수강신청 취소
     */
    public WishlistResponseDTO cancelApplyLecture(String studentNo, String lectureId);

    /**
     * 수강신청 목록 조회
     */
    public List<AppliedLectureDTO> getAppliedLectureList(String studentNo, int page, int pageSize);

    /**
     * 수강신청 전체 개수
     */
    public int getAppliedLectureCount(String studentNo);

    /**
     * 신청 현황 조회 (찜 + 수강신청)
     */
    public ApplyStatusDTO getApplyStatus(String studentNo);
}
