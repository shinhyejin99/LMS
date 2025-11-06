package kr.or.jsu.ai.service;

import org.springframework.stereotype.Service;

import kr.or.jsu.ai.dto.ConversationContextDTO;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.DepartmentDetailDTO;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.lms.staff.service.department.StaffDepartmentService;
import kr.or.jsu.lms.staff.service.professor.StaffProfessorInfoService;
import kr.or.jsu.lms.staff.service.staff.StaffManagementService;
import kr.or.jsu.lms.staff.service.student.StaffStudentInfoService;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 단과대학/학과 캐시 조회 서비스 (챗봇용) - 단과/학과 질문
 * @author 신혜진
 * @since 2025. 10. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 10. 26.     	신혜진	          최초 생성
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollegeUniveDeptSearchService {


    private final DatabaseCache databaseCache;


    private final StaffDepartmentService collegeDeptMapper;
    private final AIAnswerGeneratorService answerGenerator;
    private final StaffProfessorInfoService staffProfessorInfoService;
    private final StaffStudentInfoService staffStudentInfoService;
    private final StaffManagementService staffManagementService;


    /**
     * 챗봇 질문 처리
     */
    public String search(String question, String userNo, ConversationContextDTO context) {
        log.info("===> 단과대학/학과 검색 시작: {} (userNo: {})", question, userNo);

        if (isMyDepartmentQuestion(question)) {
            return handleMyDepartmentQuestion(question, userNo, context);
        }

        String deptName = extractDepartmentName(question);
        if (deptName != null) {
            return handleSpecificDepartmentQuestion(question, deptName, context);
        }

        // '단과', '학과 알려줘' 같은 질문 처리
        if (isAllDepartmentQuestion(question)) {
            return handleAllDepartmentQuestion(question, context);
        }

        return "죄송합니다. 찾으시는 단과대학 또는 학과 정보를 찾을 수 없습니다.";
    }



    /**
     * '내가 속한 학과/단과' 질문 처리
     */
    private String handleMyDepartmentQuestion(String question, String userNo, ConversationContextDTO context) {
        try {
            // 1. 사용자 정보 조회
            Object userDetailDto = getUserInfoByUserNo(userNo);

            if (userDetailDto == null) {
                // userNo가 로그에 명시됨
                return "로그인 정보를 확인할 수 없거나 해당 계정의 소속 정보를 조회할 수 없습니다. userNo: " + userNo;
            }

            String firstName, lastName, userType, deptCd = null;
            String collegeCd = null;

            // DTO 타입에 따라 정보 및 소속 코드 추출
            if (userDetailDto instanceof StudentDetailDTO student) {
                lastName = student.getLastName();
                firstName = student.getFirstName();
                deptCd = student.getUnivDeptCd();
                userType = "학생";
                collegeCd = student.getCollegeCd(); // 학생 DTO에서 COLLEGE_CD 확보
            } else if (userDetailDto instanceof ProfessorInfoDTO professor) {
                lastName = professor.getLastName();
                firstName = professor.getFirstName();
                deptCd = professor.getDeptCd();
                userType = "교수";
                // ⚠️ 교수의 collegeCd는 아래 DB 조회 로직을 통해 확보될 예정
            } else if (userDetailDto instanceof UserStaffDTO staff) {
                UsersVO usersVO = staff.getUserInfo();
                StaffVO staffVO = staff.getStaffInfo();
                firstName = (usersVO != null) ? usersVO.getFirstName() : "정보없음";
                lastName = (usersVO != null) ? usersVO.getLastName() : "정보없음";
                deptCd = (staffVO != null) ? staffVO.getStfDeptCd() : null;
                userType = "교직원";
            } else {
                return "조회된 사용자 정보 타입이 올바르지 않습니다.";
            }

            if (deptCd == null) {
                return String.format("%s %s님의 소속 정보(코드)를 찾을 수 없습니다.", lastName, firstName);
            }

            // 3. DB를 한 번 더 조회하여 연락처/사무실 정보 보강 및 collegeCd 확보
            DepartmentDetailDTO deptDetail = collegeDeptMapper.readDepartment(deptCd);
            if (collegeCd == null && deptDetail != null) {
                // ⭐️ collegeCd가 없는 교수 DTO, 교직원 DTO 등을 위해 DB 조회 후 collegeCd를 추출합니다.
                collegeCd = deptDetail.getCollegeCd();
            }

            // 2. 캐시에서 이름 조회 및 보강
            String univDeptName = databaseCache.getUnivDeptName(deptCd);
            String finalCollegeName = null;

            if (collegeCd != null) {
                // ⭐️ 확보된 COLLEGE_CD로 단과대학 이름을 조회하여 코드 대신 명칭을 확보합니다.
                finalCollegeName = databaseCache.getCollegeName(collegeCd);
            } else {
                // COLLEGE_CD를 찾을 수 없을 경우, deptCd로 단과대 이름을 시도해봅니다.
                finalCollegeName = databaseCache.getCollegeName(deptCd);
            }


            if (univDeptName == null && deptDetail == null) {
                // 부서 코드일 경우 (교직원)
                String staffDeptName = databaseCache.getStaffDeptName(deptCd);
                if (staffDeptName != null) {
                    String fullName = lastName + firstName;
                    String data = String.format("🌟 **%s**님의 소속 부서는 **%s**입니다. (부서코드: %s)",
                        fullName, staffDeptName, deptCd);
                    return answerGenerator.generateCommonCodeAnswer(question, data, context);
                }
                return "죄송합니다. 귀하의 소속 정보를 찾을 수 없습니다. (코드: " + deptCd + ")";
            }

            // 4. 질문 의도에 따라 최종 데이터 포맷팅 분기
            String data;

            if (isCollegeOnlyQuestion(question)) {
                // 단과대학만 요청한 경우: 이름 포함, 단과대학 명칭만 전달
                data = formatMyCollegeOnlyData(firstName, lastName, finalCollegeName);
            } else {
                // 학과(일반)를 요청한 경우: 이름 포함, 상세 정보 전달
                data = formatMyDepartmentData(firstName, lastName, userType, univDeptName, finalCollegeName, deptDetail);
            }

            // 5. 응답 생성: 포맷팅된 data 문자열을 그대로 전달 (AIAnswerGeneratorService가 data를 수정 없이 사용해야 함)
            return answerGenerator.generateCommonCodeAnswer(question, data, context);

        } catch (Exception e) {
            log.error("===> 소속 학과 조회 실패", e);
            return "소속 정보 조회 중 오류가 발생했습니다.";
        }
    }

    /**
     * userNo 길이 기반으로 사용자 정보 DTO를 조회하는 로직 (9자리 학번 처리 포함)
     */
    private Object getUserInfoByUserNo(String userNo) throws Exception {
        if (userNo == null || "anonymous".equals(userNo)) {
            return null;
        }

        int length = userNo.length();

        // 1순위: 9자리 학번 (학생) 처리
        if (length == 9) {
            try {
                StudentDetailDTO studentDto = staffStudentInfoService.readStaffStudentInfo(userNo);
                if (studentDto != null) {
                    log.info("===> 사용자 유형 판단: 학생 (학번: {})", userNo);
                    return studentDto;
                }
            } catch (Exception ignored) {}
        }

        // 2순위: 8자리 (교수) 처리 로직
        if (length == 8) {
            try {
                ProfessorInfoDTO profDto = staffProfessorInfoService.readStaffProfessorInfo(userNo);
                if (profDto != null) {
                    log.info("===> 사용자 유형 판단: 교수 (교번: {})", userNo);
                    return profDto;
                }
            } catch (Exception ignored) {}
        }

        // 3순위: 7자리 (교직원) 처리 로직
        if (length == 7) {
            try {
                UserStaffDTO staffDto = staffManagementService.readStaffManagement(userNo);
                if (staffDto != null) {
                    log.info("===> 사용자 유형 판단: 교직원 (사번: {})", userNo);
                    return staffDto;
                }
            } catch (Exception ignored) {}
        }

        // 모든 길이 조건에 해당하지 않거나, DB 조회 결과가 null인 경우
        return null;
    }

    /**
     * 학과상세 정보 출력하는 포맷팅 로직 (이름 포함)
     */
    private String formatMyDepartmentData(String firstName, String lastName, String userType, String univDeptName, String collegeName, DepartmentDetailDTO deptDetail) {
        StringBuilder sb = new StringBuilder();
        String fullName = lastName + firstName; // 성/이름 순서로 조합

        // ⭐️ 이름이 포함된 문구 사용
        sb.append(String.format("🌟 **%s**님의 소속 정보입니다.\n\n", fullName));

        sb.append(String.format("▪︎ 구분: %s\n", userType));
        sb.append(String.format("▪︎ 단과대학: %s\n", collegeName != null ? collegeName : "정보 없음"));
        sb.append(String.format("▪︎ 학과(부): **%s**\n", univDeptName)); // 학과 이름 강조

        // DB DTO가 있을 경우 상세 정보 추가
        if (deptDetail != null) {
            sb.append(String.format("▪︎ 사무실: %s\n", deptDetail.getOfficeNo() != null ? deptDetail.getOfficeNo() : "정보 없음"));
            sb.append(String.format("▪︎ 연락처: %s\n", deptDetail.getTelNo() != null ? deptDetail.getTelNo() : "정보 없음"));
        } else {
            sb.append("▪︎ 상세 정보: 추가 정보 없음 (DB 조회 실패)\n");
        }

        return sb.toString();
    }

    /**
     * 단과대학 정보만 출력하는 포맷팅 로직
     */
    private String formatMyCollegeOnlyData(String firstName, String lastName, String collegeName) {
        StringBuilder sb = new StringBuilder();
        String fullName = lastName + firstName; // 성/이름 순서로 조합

        // ⭐️ 이름이 포함된 문구 사용
        sb.append(String.format("🌟 **%s**님의 소속 단과대학은 %s 입니다.\n",
            fullName, collegeName != null ? collegeName : "정보 없음"));

        return sb.toString();
    }


    // 2. 특정 학과 정보 조회
    private String handleSpecificDepartmentQuestion(String question, String deptName, ConversationContextDTO context) {
        // ⭐️ 캐시에서 학과명으로 검색
        Optional<UnivDeptInfo> targetDept = databaseCache.getUnivDeptList().stream()
            .filter(d -> d.getUnivDeptName().contains(deptName))
            .findFirst();

        if (targetDept.isEmpty()) {
            return String.format("죄송합니다. '%s'에 해당하는 학과 정보를 찾을 수 없습니다.", deptName);
        }

        UnivDeptInfo deptInfo = targetDept.get();
        // collegeCd를 사용하여 명칭 조회
        String collegeName = databaseCache.getCollegeName(deptInfo.getCollegeCd());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🔎 **%s**에 대한 정보입니다.\n\n", deptInfo.getUnivDeptName()));
        sb.append(String.format("▪︎ 단과대학: %s\n", collegeName != null ? collegeName : "정보 없음"));

        // DB에서 상세 정보 조회 (추가 로직)
        try {
            DepartmentDetailDTO deptDetail = collegeDeptMapper.readDepartment(deptInfo.getUnivDeptCd());

            // ⭐️ 조회된 학과가 폐지된 학과인지 확인 (챗봇이 폐지된 학과를 조회하지 않도록 명시적으로 체크)
            if (deptDetail != null && deptDetail.getDeleteAt() != null) {
                return String.format("죄송합니다. '%s' 학과는 **폐지된 학과**입니다.", deptInfo.getUnivDeptName());
            }

            if(deptDetail != null) {
                sb.append(String.format("▪︎ 사무실: %s\n", deptDetail.getOfficeNo() != null ? deptDetail.getOfficeNo() : "정보 없음"));
                sb.append(String.format("▪︎ 연락처: %s\n", deptDetail.getTelNo() != null ? deptDetail.getTelNo() : "정보 없음"));
            } else {
                sb.append("▪︎ 상세 정보: 추가 정보 없음 (DB 조회 실패)\n");
            }
        } catch (Exception e) {
            log.error("===> 특정 학과 상세 정보 조회 실패", e);
            sb.append("▪︎ 상세 정보: 조회 중 오류 발생\n");
        }


        return answerGenerator.generateCommonCodeAnswer(question, sb.toString(), context);
    }


    // 3. 전체 목록 조회
    private String handleAllDepartmentQuestion(String question, ConversationContextDTO context) {
        try {
            // 1. 캐시된 학과 목록 사용
            List<UnivDeptInfo> univDepts = databaseCache.getUnivDeptList();

            if (univDepts == null || univDepts.isEmpty()) {
                return "현재 캐시에 등록된 학과 목록이 없습니다.";
            }

            // ⭐️ 챗봇의 응답 정확도를 위해 활성 학과 코드만 별도로 조회하여 Set으로 변환
            List<String> activeDeptCodesList = collegeDeptMapper.readActiveDepartmentCodes(null); // 활성 학과 코드 목록 DB 조회
            Set<String> activeDeptCodes = activeDeptCodesList.stream().collect(Collectors.toSet());

            // 2. 플레이스홀더 학과와 폐지된 학과를 제외하고 실제 활성 학과만 필터링
            List<UnivDeptInfo> actualActiveDepts = univDepts.stream()
                // 1)활성 학과 코드 Set에 포함된 학과만 통과 (폐지된 학과 제외)
                .filter(dept -> activeDeptCodes.contains(dept.getUnivDeptCd()))
                // 2) 기초교양 항목 제외
                .filter(dept -> !dept.getUnivDeptCd().equals("DEP-JSU-BASIC"))
                // 3)  오작동 위험이 높았던 '대학'으로 끝나는 이름 필터를 제거했습니다. (
                // .filter(dept -> !dept.getUnivDeptName().endsWith("대학"))
                .collect(Collectors.toList());

            if (actualActiveDepts.isEmpty()) {
                return "조회된 실제 활성 학과 목록이 없습니다.";
            }

            // 3. 단과대학별로 그룹화하여 목록 포맷팅
            String formattedList = actualActiveDepts.stream() // ⭐️ 필터링된 활성 목록 사용
                .collect(Collectors.groupingBy(
                    // CollegeCd를 이용해 College Name으로 그룹화
                    dept -> databaseCache.getCollegeName(dept.getCollegeCd()),
                    Collectors.mapping(UnivDeptInfo::getUnivDeptName, Collectors.toList())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String collegeName = entry.getKey();
                    String deptNames = String.join(", ", entry.getValue());
                    return String.format("📢 **%s**:\n%s", collegeName, deptNames);
                })
                .collect(Collectors.joining("\n\n"));

            StringBuilder sb = new StringBuilder();
            sb.append("🏫 우리 대학의 전체 **활성 학과** 목록입니다.\n\n");
            sb.append(formattedList);

            return answerGenerator.generateCommonCodeAnswer(question, sb.toString(), context);

        } catch (Exception e) {
            log.error("===> 전체 학과 목록 조회 실패 (캐시)", e);
            return "학과 목록 조회 중 오류가 발생했습니다. (캐시 접근 실패)";
        }
    }


    /**
     * '내가 속한 학과/단과' 질문 판단
     */
    private boolean isMyDepartmentQuestion(String question) {
        String lower = question.toLowerCase();
        return (lower.contains("내") || lower.contains("내가") || lower.contains("소속") || lower.contains("나의"))
            && (lower.contains("학과") || lower.contains("단과") || lower.contains("어디"));
    }

    /**
     * 질문이 소속 학과/단과 중 '단과'만 묻고 있는지 판단하는
     */
    private boolean isCollegeOnlyQuestion(String question) {
        String lower = question.toLowerCase();
        // '단과'는 포함하지만 '학과'는 포함하지 않는 경우
        return lower.contains("단과") && !lower.contains("학과");
    }

    /**
     * 캐시 목록에서 학과 이름을 추출
     */
    private String extractDepartmentName(String question) {
        final String lowerQuestion = question.toLowerCase();

        List<UnivDeptInfo> deptList = databaseCache.getUnivDeptList();

        if (deptList == null || deptList.isEmpty()) {
            return null;
        }

        return deptList.stream()
            .filter(dept -> lowerQuestion.contains(dept.getUnivDeptName().toLowerCase()))
            .findFirst()
            .map(UnivDeptInfo::getUnivDeptName)
            .orElse(null);
    }

    /**
     * '단과', '학과 알려줘'를 포함한 전체 목록 질문 판단
     */
    private boolean isAllDepartmentQuestion(String question) {
        String lower = question.toLowerCase().trim();

        boolean hasRequestKeyword = lower.contains("전체") || lower.contains("모든") || lower.contains("목록") || lower.contains("알려줘");
        boolean isDeptOrCollege = lower.contains("학과") || lower.contains("단과") || lower.contains("학부");

        boolean hasExplicitRequest = hasRequestKeyword && isDeptOrCollege;
        boolean isSimpleRequest = (lower.equals("단과") || lower.equals("학과") || lower.equals("학부"));

        return hasExplicitRequest || isSimpleRequest;
    }
}