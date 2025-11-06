package kr.or.jsu.lms.student.service.info;

import java.util.List;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.dto.SemesterGradeDTO;
import kr.or.jsu.dto.StudentDetailDTO;

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
 *	2025. 10. 1.		김수현			메소드 수정, 추가
 *	2025. 10. 24.		김수현			학생 학기별 평균 평점 조회 추가
 * </pre>
 */
public interface StuInfoService {


	/**
	 * @return 학생(자신의) 인적&학적 정보 조회
	 */
	public StudentDetailDTO readStuMyInfo(String studentNo);


	/**
	 * 학생(자신의) 인적 정보 수정 (users, address, student 테이블 업데이트)
	 * @param student
	 * @return
	 */
	public int updateStuMyInfo(StudentDetailDTO student, String nameValue);

	/**
	 * 총 이수 학점 조회
	 * @param studentNo
	 * @return
	 */
	public int readTotalCredit(String studentNo);

	/**
	 * 학생 평균 학점(GPA) 조회
	 */
	public Double readStudentGPA(String studentNo);

    /**
     * 학생의 증명사진 파일 정보를 조회
     * @param studentNo
     * @return
     */
    public FileDetailInfo getStudentIdPhoto(String studentNo);

    /**
     * 학생 학기별 평균 평점 조회
     * @param studentNo
     * @return
     */
    public List<SemesterGradeDTO> readSemesterGrades(String studentNo);

}
