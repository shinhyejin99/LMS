package kr.or.jsu.lms.professor.service.lecture;

import java.util.List;
import java.util.Map;
import kr.or.jsu.core.paging.PaginationInfo;

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
 *  2025. 9. 30.        최건우          강의 등록 신청 메서드 추가
 * </pre>
 */
public interface ProfLectureRegistService {
    String registerLecture(Map<String, Object> lectureInfo);
    List<Map<String, Object>> getSubjectCodes(String professorNo);
    Map<String, Object> getSubjectDetails(String subjectCode);
    List<Map<String, Object>> getLctOpenApplyList(Map<String, Object> paramMap, PaginationInfo<Map<String, Object>> pagingInfo);
    Map<String, Object> getLectureApplicationDetail(String lctApplyId);
    Map<String, Object> getProfessorPosition(String professorNo);
    Map<String, Object> getProfessorDepartment(String professorNo);
    String createApprovalChain(String professorNo, String lctApplyId, String userType);
    int updateLectureApplicationApprovalId(String lctApplyId, String approveId);

    void approveApplication(String lctApplyId, String approveId, String userId);
    void rejectApplication(String lctApplyId, String approveId, String professorNo, String rejectionReason);
    boolean isLectureApplicationOwner(String lctApplyId, String professorNo);
    void cancelApplication(String lctApplyId);
}