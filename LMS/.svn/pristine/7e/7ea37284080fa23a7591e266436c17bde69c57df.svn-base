package kr.or.jsu.dto;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 학생- 인적 & 학적정보 조회하기 위한 DTO
 *
 * @author 정태일
 * @since 2025. 9. 27.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 27.     	김수현	          최초 생성
 *	2025. 10. 15.		김수현 			  학생 정보 조회 - 졸업예정년도 추가
 *	2025. 10. 24.		김수현			professorEmail - 담당교수 이메일: 필드 추가
 *      </pre>
 */
@Data
@EqualsAndHashCode(of = "userNo")
public class StudentDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;

	private String userNo;
	private String userType;
	private String pwHash;
	private String studentName;
	private String firstName;
	private String lastName;
	private String regiNo;
	private String photoId;
	private String mobileNo;
	private String email;
	private String bankCode; // 은행 코드
	private String bankName; // 은행명
	private String bankAccount;

	private LocalDate createAt;

	private String studentNo;
	private String univDeptCd; // 소속 학과 코드
	private String univDeptName; // 소속 학과명
	private String gradeCd; // 현재 학년 코드
	private String gradeName; // 현재 학년
	private String stuStatusCd; // 재학 상태 코드
	private String stuStatusName; // 재학 상태명

	private String engLname;
	private String engFname;
	private String guardName;
	private String guardRelation;
	private String guardPhone;
	private String professorId; // 담당교수 교번(교수는 학생과 교수, 교수와 사용자 이용함)
	private String professorUserId; // 담당교수 userId (알림 전송용)
	private String professorName; // 담당교수 이름
	private String professorEmail; // 담당교수 이메일
	private String officeNo; // 담당교수 전화번호

	private String entranceTypeCd; // 입학타입코드(공통코드) - left join 사용
	private String entranceTypeName; // 입학타입명
	private String entranceDate; // 입학일

	private String gradHighschool;
	private String gradYear; // DB에서 가져오는 건 고등학교 졸업년도, 서비스에서 학번으로 입학년도로 설정
	private String gradExamYn;
	private String targetDept;

	private String militaryTypeCd; // 병역타입코드(공통코드) - left join 사용
	private String militaryTypeName; // 병역타입명


	private LocalDate exitAt;

	private String addrId; // 주소 정보
	private String baseAddr;
	private String detailAddr;
	private String zipCode;

	private String CollegeCd;
	private String CollegeName;

	private String gender; // 성별

    private String expectedYeartermCd; // 예상졸업년도
    private String graduateYeartermCd; // 졸업년도
    private int completedCredits; // 총 이수 학점
    private int majorRequiredCredits; // 전공 필수 이수 학점
    private int majorElectiveCredits; // 전공 선택 이수 학점
    private int liberalArtsRequiredCredits; // 교양 필수 이수 학점
    private int liberalArtsElectiveCredits; // 교양 선택 이수 학점
    private int majorBasicCredits; // 전공 기초 이수 학점
    private int liberalArtsBasicCredits; // 교양 기초 이수 학점

    private int totalRequiredCredits; // 총 필요 학점
    private int majorRequiredTotalCredits; // 전공 필수 필요 학점
    private int majorElectiveTotalCredits; // 전공 선택 필요 학점
    private int liberalArtsRequiredTotalCredits; // 교양 필수 필요 학점
    private int liberalArtsElectiveTotalCredits; // 교양 선택 필요 학점
    private int majorBasicTotalCredits; // 전공 기초 필요 학점
    private int liberalArtsBasicTotalCredits; // 교양 기초 필요 학점
    private MultipartFile photoFile; // 졸업년도

    private String overallAvgGrade; // 학기별 평균 평점
}