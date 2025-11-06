package kr.or.jsu.lms.professor.service.advising;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kr.or.jsu.mybatis.mapper.StuGraduReqMapper;
// import kr.or.jsu.mybatis.mapper.IndivtaskSubmitMapper; 
import kr.or.jsu.vo.StuGraduReqVO;
// import kr.or.jsu.vo.IndivtaskSubmitVO; 


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
@Service
public class ProfGradReviewServiceImpl implements ProfGradReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ProfGradReviewServiceImpl.class);

    @Autowired
    private StuGraduReqMapper mapper;

    // @Autowired
    // private IndivtaskSubmitMapper indivtaskSubmitMapper; // Removed

    @Override
    public List<StuGraduReqVO> getGradReviewList(String professorNo, String grade) {
        return getGradReviewList(professorNo, grade, 1, 10);
    }

    @Override
    public List<StuGraduReqVO> getGradReviewList(String professorNo, String grade, int page, int size) {
        if (professorNo == null) {
            logger.warn("getGradReviewList: 교번이 유효하지 않습니다. professorNo: {}", professorNo);
            throw new IllegalArgumentException("교번이 유효하지 않습니다.");
        }
        logger.info("getGradReviewList: 교수 번호 {}로 졸업 심사 목록 조회 시작 (학년: {}, 페이지: {}, 사이즈: {})", professorNo, grade, page, size);
        
        Map<String, Object> params = new HashMap<>();
        params.put("professorNo", professorNo);
        params.put("offset", (page - 1) * size);
        params.put("size", size);
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }

        List<StuGraduReqVO> list = mapper.selectStuGraduReqListByProfessorNoPaginated(params);
        if (list.isEmpty()) {
            logger.info("getGradReviewList: 교수 번호 {}에 대한 졸업 심사 목록이 없습니다.", professorNo);
            return Collections.emptyList(); 
        }
        
        for (StuGraduReqVO student : list) {
            Integer totalCredits = student.getTotalRequiredCredits();
            Integer completedCredits = student.getCompletedCredits();

            if (totalCredits != null && totalCredits > 0 && completedCredits != null) {
                int rate = (int) ((double) completedCredits / totalCredits * 100);
                student.setGraduationRate(rate);
            } else {
                student.setGraduationRate(0);
            }
        }
        
        logger.info("getGradReviewList: 교수 번호 {}에 대한 졸업 심사 목록 {}개 조회 완료", professorNo, list.size());
        return list;
    }

    @Override
    public int getGradReviewListCount(String professorNo, String grade) {
        if (professorNo == null) {
            logger.warn("getGradReviewListCount: 교번이 유효하지 않습니다. professorNo: {}", professorNo);
            throw new IllegalArgumentException("교번이 유효하지 않습니다.");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("professorNo", professorNo);
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }
        return mapper.countStudentsByProfessorNo(params); // Assuming mapper method can take map
    }

    @Override
    public List<StuGraduReqVO> getGraduationAssignmentSubmissions(String professorNo, String grade, String semester) {
        if (professorNo == null) {
            logger.warn("getGraduationAssignmentSubmissions: 교번이 유효하지 않습니다. professorNo: {}", professorNo);
            throw new IllegalArgumentException("교번이 유효하지 않습니다.");
        }
        // Assuming 'GRAD_ASSIGNMENT' is the graduReqCd for graduation assignments.
        String graduationReqCd = "GRAD_ASSIGNMENT"; // Placeholder for graduation assignment code

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("professorNo", professorNo);
        paramMap.put("graduReqCd", graduationReqCd); // Changed from graduationTaskId
        paramMap.put("grade", grade); // Changed from academicYear to grade
        paramMap.put("semester", semester);

        logger.info("getGraduationAssignmentSubmissions: 교수 번호 {}로 졸업 과제 제출 목록 조회 시작 (학년: {}, 학기: {})", professorNo, grade, semester);
        List<StuGraduReqVO> submissions = mapper.selectGraduationAssignmentSubmissions(paramMap); // Changed mapper call
        if (submissions.isEmpty()) {
            logger.info("getGraduationAssignmentSubmissions: 교수 번호 {}에 대한 졸업 과제 제출 목록이 없습니다.", professorNo);
            return Collections.emptyList();
        }
        logger.info("getGraduationAssignmentSubmissions: 교수 번호 {}에 대한 졸업 과제 제출 목록 {}개 조회 완료", professorNo, submissions.size());
        return submissions;
    }

    @Override
    public void approveReview(String reviewId) {
        if (reviewId == null) {
            logger.warn("approveReview: 심사 ID가 유효하지 않습니다. reviewId: {}", reviewId);
            throw new IllegalArgumentException("심사 ID가 유효하지 않습니다.");
        }
        logger.info("approveReview: 심사 ID {} 승인 처리 시작", reviewId);
        mapper.updateStuGraduReqStatus(reviewId, "APPROVED");
        StuGraduReqVO updatedReview = mapper.selectStuGraduReq(reviewId);
        if (updatedReview != null) {
            logger.info("approveReview: 심사 ID {} 업데이트 후 상태: {}", reviewId, updatedReview.getGraduReqCd());
        } else {
            logger.warn("approveReview: 심사 ID {} 업데이트 후 레코드를 찾을 수 없습니다.", reviewId);
        }
        logger.info("approveReview: 심사 ID {} 승인 처리 완료", reviewId);
    }

    @Override
    public void rejectReview(String reviewId, String reason) {
        if (reviewId == null || reason == null || reason.trim().isEmpty()) {
            logger.warn("rejectReview: 입력 값이 유효하지 않습니다. reviewId: {}, reason: {}", reviewId, reason);
            throw new IllegalArgumentException("심사 ID 또는 반려 사유가 유효하지 않습니다.");
        }
        logger.info("rejectReview: 심사 ID {} 반려 처리 시작. 사유: {}", reviewId, reason);
        mapper.updateStuGraduReqStatusWithReason(reviewId, "REJECTED", reason);
        logger.info("rejectReview: 심사 ID {} 반려 처리 완료", reviewId);
    }

    @Override
    public StuGraduReqVO getGraduationAssignmentByStudentNo(String professorNo, String studentNo) {
        if (professorNo == null || studentNo == null) {
            logger.warn("getGraduationAssignmentByStudentNo: 필수 파라미터가 유효하지 않습니다. professorNo: {}, studentNo: {}", professorNo, studentNo);
            throw new IllegalArgumentException("필수 파라미터가 유효하지 않습니다.");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("professorNo", professorNo);
        params.put("studentNo", studentNo);
        List<StuGraduReqVO> submissions = mapper.selectGraduationAssignmentByStudentNo(params);
        if (submissions.isEmpty()) {
            return null;
        }
        return submissions.get(0);
    }
}