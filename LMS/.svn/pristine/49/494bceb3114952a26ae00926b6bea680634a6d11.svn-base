package kr.or.jsu.lms.staff.service.student;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import kr.or.jsu.dto.StudentDetailDTO;

/**
 *
 *
 * @author 신혜진
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	신혜진	          최초 생성
 *
 *      </pre>
 */
public interface StaffStudentInfoService {

	/**
	 * 교직원이 학생 정보를 등록 하는 메서드
	 *
	 * @param student
	 * @return
	 */
	void createStaffStudentInfo(StudentDetailDTO student);

	/**
	 * 교직원이 학생 정보를 전체 조회 하는 메서드 (페이징 및 검색 통합)
	 *
	 * @param pagingInfo 페이징 정보 및 검색 조건을 담은 객체
	 * @return 조회된 학생 목록 (List<Map<String, Object>>)
	 */
	public List<Map<String, Object>> readStaffStudentInfoList(Map<String, Object> paramMap);


	/**
	 * 교직원이 학생 정보를 한건만 조회 하는 메서드
	 *
	 * @param student
	 * @return
	 * @throws {@link RuntimeException}
	 */
	public StudentDetailDTO readStaffStudentInfo(String studentNo) throws RuntimeException;

	/**
	 * 엑셀 파일을 읽어 학생 정보 일괄 등록
	 *
	 * @param excelFile    엑셀 파일 (MultipartFile)
	 * @param ZIP_CODE_COL
	 * @return 등록된 학생 수
	 */
	public Map<String, Integer> createBatchStudentsByExcel(MultipartFile excelFile, Map<String, Map<String, String>> codeMaps);

	/**
	 *  엑셀 파일을 읽어 유효성을 검사하고 등록될 학생의 학과별 인원수를 미리보기 합니다.
	 * (실제 DB 등록은 수행하지 않습니다.)
	 * * @param excelFile 엑셀 파일
	 * @param codeMaps 학과 이름/코드, 학년 이름/코드 등의 매핑 맵
	 * @return 학과 이름(String)과 등록될 예상 인원수(Integer)를 담은 맵
	 * @throws RuntimeException 데이터 유효성 검사 실패 시
	 */
	public Map<String, Integer> previewBatchStudentsByExcel(MultipartFile excelFile,Map<String, Map<String, String>> codeMaps);
    /**
     * 학생 목록 데이터를 받아 Apache POI Workbook 객체를 생성
     * (이 메서드가 실질적으로 엑셀 파일 구조를 만듭니다.)
     * @param students 엑셀에 포함될 학생 목록 데이터
     * @return 생성된 엑셀 Workbook 객체
     */
    public Workbook createStudentExcel(List<StudentDetailDTO> students);



	/**
	 * 학적 상태별 학생 수를 조회하고 '휴학'을 통합하여 Map으로 반환하는 메서드
	 *
	 * @return {재학: 5111, 휴학: 7, 졸업: 5, ...} 형태의 Map
	 */
	public Map<String, Long> readStudentStatusCounts();

	/**
	 * 특정 학적 상태에 해당하는 학생을 단과대학별로 카운트합니다.
	 * @param statusName 학적 상태 이름 (예: "재학", "휴학")
	 * @return List<Map<String, Object>> (단과대학 이름, 학생 수)
	 */
	public List<Map<String, Object>> readStudentCountsByCollege(String statusName);

	 /**
     * 특정 학적 상태와 단과대학에 해당하는 학생을 학과별로 카운트
     * @param statusName 학적 상태 이름 (예: "재학")
     * @param collegeName 단과대학 이름 (예: "공과대학")
     * @return List<Map<String, Object>> (학과 이름, 학생 수)
     */
    public List<Map<String, Object>> readStudentCountsByDepartment(String statusName, String collegeName);

    /**
     * 특정 학적 상태와 단과대학과 학과에 해당하는 학생을 학년별로 카운트
     * @param statusName학적 상태 이름 (예: "재학")
     * @param collegeName단과대학 이름 (예: "공과대학")
     * @param univDeptName학과 이름(예: "컴퓨터공학")
     * @returnList<Map<String, Object>> (학년, 학생 수)
     */
    public List<Map<String, Object>> readStudentCountsByGrade(String statusName, String collegeName, String univDeptName);

    public void modifyStaffStudentInfo(@Valid StudentDetailDTO student);

	Map<String, Map<String, String>> readCommonCodeMaps();

	/**
	 * 엑셀 배치 미리보기 (학과별 카운트 계산)
	 * @return totalCount(전체 학생 수), detailCounts(학과별 학생 수)를 포함한 Map
	 */
	Map<String, Object> previewBatchStudentInfo(MultipartFile excelFile);
	/**
	 * 학생 성별 비중
	 * @return
	 */
	Map<String, Integer> getOverallGenderStatistics();

	/**
	 * 학년별 학생 수
	 * @return
	 */
	Map<String, Integer> getOverallGradeStatistics();





}
