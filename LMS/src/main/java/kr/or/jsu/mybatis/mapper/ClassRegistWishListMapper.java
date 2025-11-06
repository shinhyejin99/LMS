package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.classregist.common.LectureSearchCondition;
import kr.or.jsu.classregist.dto.AppliedLectureDTO;
import kr.or.jsu.classregist.dto.LectureDetailDTO;
import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.WishlistCheckDTO;
import kr.or.jsu.classregist.dto.WishlistDTO;
import kr.or.jsu.classregist.dto.WishlistSearchDTO;
import kr.or.jsu.vo.LectureVO;

/**
 * 예비수강신청 매퍼
 *
 * @author 김수현
 * @since 2025. 10. 16.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	김수현	          최초 생성
 *	2025. 10. 17.		김수현			메소드 추가
 *	2025. 10. 18.		김수현			수강신청 관련 추가
 *      </pre>
 */
@Mapper
public interface ClassRegistWishListMapper {

    /**
     * 찜 목록 조회 (페이지네이션)
     * @param paging
     * @param studentNo 학번
     * @return
     */
    public List<WishlistDTO> selectWishlistWithPaging(WishlistSearchDTO searchDTO);

    /**
     * 찜 목록 전체 개수
     * @param studentNo 학번
     * @return
     */
    public int countWishlist(@Param("studentNo") String studentNo);

    /**
     * 찜하기
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public int insertWishlist(@Param("studentNo") String studentNo,
                       @Param("lectureId") String lectureId);

    /**
     * 찜 취소
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public int deleteWishlist(@Param("studentNo") String studentNo,
                       @Param("lectureId") String lectureId);

    /**
     * 찜 여부 확인
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public int existsWishlist(@Param("studentNo") String studentNo,
                       @Param("lectureId") String lectureId);

    // ===== 찜 경고 체크용 =====

    /**
     * 같은 과목 찜 여부 체크
     * @param studentNo 학번
     * @param subjectCd 과목코드
     * @return
     */
    public int existsSameSubjectInWishlist(@Param("studentNo") String studentNo,
                                     @Param("subjectCd") String subjectCd);

    /**
     * 시간표 겹침 체크
     * @param studentNo 학번
     * @param lectureId 강의ID
     * @return
     */
    public int countTimeConflictInWishlist(@Param("studentNo") String studentNo,
                                     @Param("lectureId") String lectureId);

    /**
     * 찜한 강의들의 총 학점 조회
     * @param studentNo 학번
     * @return
     */
    public Integer sumWishlistCredits(@Param("studentNo") String studentNo);

    /**
     * 찜한 강의 개수 조회 (과목 수)
     */
    public int countWishlistSubjects(@Param("studentNo") String studentNo);


    /**
     * 강의 정보 조회 (과목코드 포함)
     * @param lectureId 강의ID
     * @return
     */
    public LectureVO selectLectureWithSubject(String lectureId);


    // ===== 강의 목록 조회(예비 + 본 수강신청) =====

    /**
     * 강의 목록 조회 (검색 + 페이지네이션)
     * @param paging
     * @param studentNo 학번
     * @param yearTermCd 학기코드
     * @return
     */
    public List<LectureListDTO> selectLectureListWithSearch(
		@Param("searchCondition") LectureSearchCondition searchCondition
	    , @Param("studentNo") String studentNo
    );


    /**
     * 찜하기 체크용
     * @param lectureId 강의ID
     * @return
     */
    public WishlistCheckDTO selectLectureForWishlistCheck(String lectureId);

    /**
     * 강의 목록 전체 개수
     * @param searchType 검색조건
     * @param searchWord 검색어
     * @param yearTermCd 학기코드
     * @return
     */
    public int countLectureList(LectureSearchCondition searchCondition);

    /**
     * 강의 상세 조회
     * @param lectureId 강의ID
     * @return
     */
    public LectureDetailDTO selectLectureDetail(String lectureId);

    //== 과목의 대상 학년 (예비 + 본 수강신청)==

    /**
     * 학생의 학년 조회
     * @param studentNo 학번
     * @return
     */
    public String selectStudentGrade(String studentNo);

    /**
     * 과목의 대상학년 목록 조회
     * @param subjectCd 과목코드
     * @return
     */
    public List<String> selectTargetGrades(String subjectCd);


    // =====수강신청======
    /**
     * 수강신청 여부 확인
     */
    public int existsAppliedLecture(@Param("studentNo") String studentNo,
                             @Param("lectureId") String lectureId);

    /**
     * 현재 수강신청 인원 (FOR UPDATE 없이)
     */
    public int countCurrentEnroll(@Param("lectureId") String lectureId);

    /**
     * 강의 정보 조회 (비관적 락)
     */
    public WishlistCheckDTO selectLectureForApplyWithLock(@Param("lectureId") String lectureId);

    /**
     * 신청한 강의들의 총 학점 조회
     */
    public Integer sumAppliedCredits(@Param("studentNo") String studentNo);

    /**
     * 시간표 겹침 체크 (신청 내역 기준)
     */
    public int countTimeConflictInApplied(@Param("studentNo") String studentNo,
                                    @Param("lectureId") String lectureId);

    /**
     * 같은 과목 신청 여부 체크
     */
    public int existsSameSubjectInApplied(@Param("studentNo") String studentNo,
                                    @Param("subjectCd") String subjectCd);

    /**
     * 수강신청
     */
    public int insertApplyLecture(@Param("studentNo") String studentNo,
                           @Param("lectureId") String lectureId);

    /**
     * 수강신청 취소
     */
    public int deleteApplyLecture(@Param("studentNo") String studentNo,
                           @Param("lectureId") String lectureId);

    /**
     * 수강신청 로그 기록
     */
    public int insertApplyLog(@Param("logId") String logId,
                       @Param("studentNo") String studentNo,
                       @Param("lectureId") String lectureId,
                       @Param("conflictYn") char conflictYn,
                       @Param("successYn") char successYn,
                       @Param("actionType") char actionType);

    /**
     * 로그 시퀀스 조회
     */
    Long getNextLogSeq();

    /**
     * 수강신청 목록 조회 (페이지네이션)
     */
    public List<AppliedLectureDTO> selectAppliedLectureList(
        @Param("studentNo") String studentNo,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );

    /**
     * 수강신청 전체 개수
     */
    public int countAppliedLecture(@Param("studentNo") String studentNo);

    /**
     * 수강신청 내역 기준 신청 학점
     */
    public int countAppliedCredits(String studentNo);

    /**
     * 수강신청 내역 기준 신청 과목 수
     */
    public int countAppliedSubjects(String studentNo);

}
