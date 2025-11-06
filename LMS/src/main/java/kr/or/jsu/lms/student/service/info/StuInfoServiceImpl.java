package kr.or.jsu.lms.student.service.info;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.dto.SemesterGradeDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *  2025. 9. 25.     	김수현	          최초 생성
 *	2025. 10. 1.		김수현			인적정보 업데이트 메소드 추가
 *	2025. 10. 14.		김수현			총 이수학점, 평균학점 조회 추가
 *	2025. 10. 15.		김수현			증명사진 불러오기 추가
 *
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StuInfoServiceImpl implements StuInfoService {

	private final StudentMapper mapper;
	private final LMSFilesService filesService;

	@Override
	public StudentDetailDTO readStuMyInfo(String studentNo) {
		StudentDetailDTO student = mapper.selectStudentDetailInfo(studentNo);
		if (student == null) {
			throw new RuntimeException(String.format("%s 학생 존재하지 않음", studentNo));
		}
		// 주민번호 설정
		String regiNoHyphen = student.getRegiNo();
		if(!regiNoHyphen.isBlank() && regiNoHyphen.length() >= 7) {
			String regiNo = regiNoHyphen.replace("-", "").trim();

			// 1. 성별 코드 추출 (6번째 인덱스, 7번째 자리)
	        if(regiNo.length() >= 7) {
	            String genderCode = regiNo.substring(6, 7);
	            student.setGender(genderCode);
	        }

	        // 2. 마스킹과 하이픈 추가
	        if (regiNo.length() == 13) {
	             // 생년월일
	             String frontPart = regiNo.substring(0, 6);
	             // 성별 코드
	             String genderDigit = regiNo.substring(6, 7);
	             String maskedRrn = frontPart + "-" + genderDigit + "******";
	             student.setRegiNo(maskedRrn);
	        }
		}

		// 입학년도 설정
		if (!studentNo.isBlank() && studentNo.length() >= 4) {
	        String entranceYear = studentNo.substring(0, 4);

	        student.setGradYear(entranceYear);
	    }

		// 졸업년도 설정
		String yearTermCd = student.getExpectedYeartermCd();
		if (StringUtils.hasText(yearTermCd) && yearTermCd.contains("_")) {
		    String[] parts = yearTermCd.split("_");
		    String year = parts[0]; // 년도
		    String termCode = parts[1]; // REG1, REG2

		    String graduationType;
		    if("REG1".equals(termCode)) {
		        graduationType = "전기";
		    } else if ("REG2".equals(termCode)) {
		        graduationType = "후기";
		    } else {
		        graduationType = "구분불명";
		    }

		    String finalName = year + "년 " + graduationType;

		    student.setExpectedYeartermCd(finalName);
		}
		return student;
	}

	/**
	 * 학생(자신의) 인적 정보 수정 메소드 (users, address, student 테이블 업데이트)
	 */
	@Override
	@Transactional
	public int updateStuMyInfo(StudentDetailDTO student, String nameValue) {

        // 이름 분리: 별도 파라미터 nameValue 사용
        processNameSplit(student, nameValue);

        // USERS 테이블 업데이트 (한글 이름, 이메일, 휴대폰, 은행)
        mapper.updateUserDetailInfo(student);

        // ADDRESS 테이블 업데이트 (우편번호, 주소)
        mapper.updateAddressInfo(student);

        // STUDENT 테이블 업데이트 (영문 이름, 비상 연락처 등)
        mapper.updateStudentPersonalInfo(student);

        // STU_MILITARY 테이블 업데이트는 필요에 따라 추가
        // studentMapper.updateStudentMilitaryInfo(dto);

        // 모든 쿼리가 성공적으로 실행되면 트랜잭션 커밋
        return 1;
	}


    /**
     * 이름 필드("김 새봄 / Kim Saebom")를 분리하여 DTO에 설정하는 헬퍼 메서드
     * @param dto
     * @param nameValue 합쳐진 이름 문자열 (JSP에서 직접 받은 값)
     */
    private void processNameSplit(StudentDetailDTO dto, String nameValue) {
    	// nameValue를 사용하도록 수정
        if (nameValue == null || !nameValue.contains(" / ")) {
            return;
        }

        try {
            String[] parts = nameValue.split(" / ");
            // ... (이하 이름 분리 로직은 이전과 동일) ...
            String koreanName = parts[0].trim();
            String englishName = parts[1].trim();

            // 한글 이름 분리 및 DTO 설정
            String[] korParts = koreanName.split(" ", 2);
            if (korParts.length == 2) {
                dto.setLastName(korParts[0]);
                dto.setFirstName(korParts[1]);
            } else {
                 throw new IllegalArgumentException("한글 이름 형식 오류: 성과 이름이 띄어쓰기로 분리되지 않았습니다.");
            }

            // 영문 이름 분리 및 DTO 설정
            String[] engParts = englishName.split(" ", 2);
            if (engParts.length == 2) {
                dto.setEngLname(engParts[0]);
                dto.setEngFname(engParts[1]);
            } else {
                 throw new IllegalArgumentException("영문 이름 형식 오류: 성과 이름이 띄어쓰기로 분리되지 않았습니다.");
            }

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("이름 필드 분리 중 시스템 오류 발생.", e);
        }
    }

	/**
	 * 총 이수 학점 조회
	 */
	@Override
	public int readTotalCredit(String studentNo) {
		return mapper.selectTotalCredit(studentNo);
	}

	/**
	 * 학생 평균 학점(GPA) 조회
	 */
	@Override
	public Double readStudentGPA(String studentNo) {
		return mapper.selectStudentGPA(studentNo);
	}

	/**
	 * 학생의 증명사진 파일 정보 조회
	 */
	@Override
	public FileDetailInfo getStudentIdPhoto(String studentNo) {
		// 1. 메타데이터 가져오기 (DB 조회)
	    StudentDetailDTO student = readStuMyInfo(studentNo);
	    String fileId = student.getPhotoId();
	    if (fileId == null) throw new RuntimeException("등록된 증명사진이 없습니다.");

	    FilesBundleDTO bundle = filesService.readFileBundle(fileId);
	    if (bundle.getFileDetailInfo().isEmpty()) throw new RuntimeException("파일 상세 정보가 없습니다.");

	    FileDetailInfo detail = bundle.getFileDetailInfo().get(0);

	    // 2. 경로 정규화 및 업데이트
	    String dbSaveDir = detail.getSaveDir(); // DB에 저장된 이름 => D:\LMSFileRepository/devtemp
	    String dbSaveName = detail.getSaveName(); // UUID

	    try {
	        // 혼합된 경로 구분자 => OS에 맞는 절대 경로로 정규화
	        Path normalizedDir = Paths.get(dbSaveDir);

	        // saveDir => 정규화된 절대 경로로 업데이트 ( D:\LMSFileRepository\devtemp)
	        detail.setSaveDir(normalizedDir.toString());

	        // SAVE_NAME도
	        Path normalizedName = Paths.get(dbSaveName).getFileName();
	        detail.setSaveName(normalizedName.toString());

	        // 3. getRealFile() 호출 (FileDetailInfo 메소드 호출)
	        detail.getRealFile();
	        return detail;

	    } catch (Exception e) {
	        // 경로 정규화 후에도 파일 리소스를 찾지 못했을 때
	        throw new RuntimeException("파일 리소스를 불러오는 중 오류가 발생했습니다.", e);
	    }
	}

	/**
	 * 학생 학기별 평균 평점 조회
	 */
	@Override
	public List<SemesterGradeDTO> readSemesterGrades(String studentNo) {
	    List<SemesterGradeDTO> result = mapper.selectSemesterGrades(studentNo);

        // avgGrade를 String으로 포맷팅
        DecimalFormat df = new DecimalFormat("0.00"); // 소수점 두 자리 포맷 지정

        for (SemesterGradeDTO grade : result) {

            String formattedGrade = df.format(grade.getAvgGrade());

            // DTO의 새로운 필드에 저장
            grade.setFormattedAvgGrade(formattedGrade);
        }
	    log.info("======>Mapper 조회 결과: {}", result);
	    return result;
	}
}
