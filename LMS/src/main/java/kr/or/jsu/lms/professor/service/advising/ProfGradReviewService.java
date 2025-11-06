package kr.or.jsu.lms.professor.service.advising;

import java.util.List;

import kr.or.jsu.vo.StuGraduReqVO;
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
	 *  2025. 9. 25.     	최건우	          최초 생성
	 *
	 * </pre>
	 */
public interface ProfGradReviewService {
	 /**
     * 교수 지도 학생의 졸업 심사 목록 조회
     * @param professorNo 교번
     * @return List<StuGraduReqVO>
     */
    public List<StuGraduReqVO> getGradReviewList(String professorNo, String grade);
    public List<StuGraduReqVO> getGradReviewList(String professorNo, String grade, int page, int size);
    public int getGradReviewListCount(String professorNo, String grade);

    /**
     * 교수 지도 학생의 졸업 과제 제출 목록 조회
     * @param professorNo 교번
     * @param grade 학년
     * @param semester 학기
     * @return List<IndivtaskSubmitVO>
     */
    List<StuGraduReqVO> getGraduationAssignmentSubmissions(String professorNo, String grade, String semester);

    /**
     * 졸업 심사 승인
     * @param reviewId 심사 ID
     */
    void approveReview(String reviewId);

    /**
     * 졸업 심사 반려
     * @param reviewId 심사 ID
     * @param reason 반려 사유
     */
    public void rejectReview(String reviewId, String reason);

    public StuGraduReqVO getGraduationAssignmentByStudentNo(String professorNo, String studentNo);
}