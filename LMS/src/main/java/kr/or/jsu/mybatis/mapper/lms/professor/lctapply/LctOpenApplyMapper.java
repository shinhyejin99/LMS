package kr.or.jsu.mybatis.mapper.lms.professor.lctapply;

import java.util.List;

import kr.or.jsu.dto.db.subject.LctOpenApplyDetailDTO;
import kr.or.jsu.dto.db.subject.LctOpenApplySimpleDTO;
import kr.or.jsu.dto.info.lecture.apply.LctApplyGraderatioInfo;
import kr.or.jsu.dto.info.lecture.apply.LctApplyWeekbyInfo;
import kr.or.jsu.dto.info.lecture.apply.LctOpenApplyInfo;

public interface LctOpenApplyMapper {
	
	/**
	 * 강의 개설 신청을 생성합니다. <br>
	 * 셀렉트키로 강의개설번호를 생성하며, <br>
	 * 신청 레코드 생성 전에 미리 생성한 approval ID를 입력해야 합니다. 
	 * 
	 * @param lectureInfo
	 * @return 생성된 레코드 수 + 셀렉트키로 생성된 기본키
	 */
	public int insertLctOpenApply(
		LctOpenApplyInfo lectureInfo
	);
	
	/**
	 * 트랜잭션으로, 강의 개설 신청 직후 강의 주차별 계획을 기록합니다.
	 * 
	 * @param weekbyList
	 * @return 생성된 레코드 수
	 */
	public int insertLctApplyWeekbyList(
		List<LctApplyWeekbyInfo> weekbyList
	);
	
	/**
	 * 트랜잭션으로, 강의 개설 신청 직후 강의 성적산출기준과 비율을 기록합니다.
	 * 
	 * @param criteriaList
	 * @return 생성된 레코드 수
	 */
	public int insertLctApplyGraderatioList(
		List<LctApplyGraderatioInfo> criteriaList
	);
	
	/**
	 * "특정 교수가 신청한" <br>
	 * 강의신청과 관련된 데이터들의 목록을 가져옵니다. <br>
	 * 메인 테이블 : 강의신청 <br>
	 * 조인하는 테이블 : 과목, 승인 <br>
	 * 
	 * @param professorNo 강의 신청자 교수의 교번
	 * @return
	 */
	public List<LctOpenApplySimpleDTO> selectLctApplyList(
		String professorNo
	);
	
	/**
	 * "특정 강의에 대한" <br>
	 * 강의신청에 포함된 데이터를 가져옵니다. <br>
	 * 메인 테이블 : 강의신청 <br>
	 * 조인하는 테이블 : <br>
	 * assosiation (과목, 승인) <br>
	 * collection : (강의 주차별 계획, 강의 성적산출기준/비율)
	 * 
	 * @return
	 */
	public LctOpenApplyDetailDTO selectLctApplyDetail(
		String lctApplyId
	);
	
	/**
	 * "특정 사용자에게 승인 권한이 있는" <br>
	 * 강의신청과 관련된 데이터들의 목록을 가져옵니다. <br>
	 * 메인 테이블 : 강의신청 <br>
	 * 조인하는 테이블 : 과목, 승인 <br>
	 * 
	 * @param userId 승인자 userId
	 * @return
	 */
	public List<LctOpenApplySimpleDTO> selectLctApprovalList(
		String userId
	);
	
	/**
	 * "특정 신청ID로 신청한" <br>
	 * 강의신청에 대한 모든 데이터를 가져옵니다. <br>
	 * 메인 테이블 : 강의신청 <br>
	 * 조인하는 테이블 : <br>
	 * collection : (강의 주차별 계획, 강의 성적산출기준/비율) <br>
	 * DTO 더 만들기 귀찮아서 있는거 재활용함 <br>
	 * "subjectInfo" : null <br>
	 * "approvalInfo" : null <br>
	 * 
	 * @return
	 */
	public LctOpenApplyDetailDTO selectLctApplyByApproveId(
		String approveId
	);
	
//	@Deprecated
//	List<Map<String, Object>> selectLctOpenApplyList(
//		Map<String, Object> paramMap
//	);
//	
//	@Deprecated
//    int selectLctOpenApplyCount(Map<String, Object> paramMap);
//	
//	@Deprecated
//	Map<String, Object> selectLectureApplicationDetail(String lctApplyId);
//	
//	@Deprecated
//	List<Map<String, Object>> selectLctApplyWeekbyList(String lctApplyId);
//	
//	@Deprecated
//	List<Map<String, Object>> selectLctApplyGradeRatioList(String lctApplyId);
//	
//	@Deprecated
//	Map<String, Object> selectProfessorPosition(String professorNo);
//	
//	@Deprecated
//    Map<String, Object> getProfessorDepartment(String professorNo);
//	
//	@Deprecated
//    String getDepartmentHead(String univDeptCd);
//	
//	@Deprecated
//    String getGenericStaffId();
//	
//	@Deprecated
//    int insertApproval(Map<String, Object> approvalInfo);
//	
//	@Deprecated
//    int insertApprovalChain(Map<String, Object> paramMap);
//	
//	@Deprecated
//    String getUserIdByProfessorNo(String professorNo);
//	
//	@Deprecated
//    String getUserIdByStaffNo(String staffNo);
//	
//	@Deprecated
//    int updateLctOpenApplyApprovalId(Map<String, Object> paramMap);
//		
//	// by 최건우
//	// New methods for lecture registration form
//	
//	
//	@Deprecated
//	int insertLctApplyWeekby(Map<String, Object> weeklyPlan);
//	
//	@Deprecated
//	int insertLctApplyGradeRatio(Map<String, Object> gradeRatio);
//	
//	@Deprecated
//	int insertDraftApproval(Map<String, Object> params);
//
//    // Approval process methods
//	@Deprecated
//    Map<String, Object> getApprovalById(String approveId);
//	
//	@Deprecated
//    int updateApprovalStatus(Map<String, Object> params);
//	
//	@Deprecated
//    String findNextApprovalId(String currentApproveId);
//	
//	@Deprecated
//	int updateLectureApplicationStatus(String lctApplyId, String status);
//	
//    
//<!--     강의 신청 주차별 계획 등록 -->
//<!--     <insert id="insertLctApplyWeekby" parameterType="map"> -->
//<!--         INSERT INTO LCT_APPLY_WEEKBY ( -->
//<!--             LCT_APPLY_ID, -->
//<!--             LECTURE_WEEK, -->
//<!--             WEEK_GOAL, -->
//<!--             WEEK_DESC -->
//<!--         ) VALUES ( -->
//<!--             #{lctApplyId}, -->
//<!--             #{lectureWeek}, -->
//<!--             #{weekGoal}, -->
//<!--             #{weekDesc} -->
//<!--         ) -->
//<!--     </insert> -->
//    
//<!--     강의 신청 성적 비율 등록 -->
//<!--     <select id="selectProfessorPosition" parameterType="string" resultType="map"> -->
//<!--         SELECT -->
//<!--             PRF_POSIT_CD -->
//<!--         FROM -->
//<!--             PROFESSOR -->
//<!--         WHERE -->
//<!--             PROFESSOR_NO = #{professorNo} -->
//<!--     </select> -->
//    
//    
//<!--     승인 체인 등록 -->
//<!--     <insert id="insertApprovalChain" parameterType="map"> -->
//<!--         <selectKey keyProperty="approveId" resultType="string" order="BEFORE"> -->
//<!--             SELECT 'APPR' || LPAD(SEQ_APPROVAL.NEXTVAL, 11, '0') FROM DUAL -->
//<!--         </selectKey> -->
//<!--         INSERT INTO APPROVAL ( -->
//<!--             APPROVE_ID, -->
//<!--             PREV_APPROVE_ID, -->
//<!--             USER_ID, -->
//<!--             APPLICANT_USER_ID, -->
//<!--             APPLY_TYPE_CD, -->
//<!--             APPROVE_YNNULL, -->
//<!--             APPROVE_AT -->
//<!--         ) VALUES ( -->
//<!--             #{approveId}, -->
//<!--             #{prevApproveId, jdbcType=CHAR}, -->
//<!--             #{userId}, -->
//<!--             #{applicantUserId}, -->
//<!--             #{applyTypeCd}, -->
//<!--             #{approveYn, jdbcType=VARCHAR}, -->
//<!--             SYSDATE -->
//<!--         ) -->
//<!--     </insert> -->
//    
//
//
//
//    
//
//<!--     <select id="getProfessorDepartment" parameterType="string" resultType="map"> -->
//<!--         SELECT -->
//<!--             UNIV_DEPT_CD -->
//<!--         FROM -->
//<!--             PROFESSOR -->
//<!--         WHERE -->
//<!--             PROFESSOR_NO = #{professorNo} -->
//<!--     </select> -->
//
//<!--     <select id="getDepartmentHead" parameterType="string" resultType="string"> -->
//<!--         SELECT -->
//<!--             PROFESSOR_NO -->
//<!--         FROM -->
//<!--             PROFESSOR -->
//<!--         WHERE -->
//<!--             UNIV_DEPT_CD = #{univDeptCd} -->
//<!--             AND PRF_POSIT_CD = 'PRF_POSIT_HEAD' -->
//<!--             AND ROWNUM = 1 -->
//<!--     </select> -->
//
//<!--     <select id="getGenericStaffId" resultType="string"> -->
//<!--         SELECT -->
//<!--             STAFF_NO -->
//<!--         FROM -->
//<!--             STAFF -->
//<!--         WHERE ROWNUM = 1 -- Selects the first staff member found -->
//<!--     </select> -->
//
//
//
//<!--     교수 번호로 사용자 ID 조회 -->
//<!--     <select id="getUserIdByProfessorNo" parameterType="string" resultType="string"> -->
//<!--         SELECT USER_ID FROM PROFESSOR WHERE PROFESSOR_NO = #{professorNo} -->
//<!--     </select> -->
//
//<!--     직원 번호로 사용자 ID 조회 -->
//<!--     <select id="getUserIdByStaffNo" parameterType="string" resultType="string"> -->
//<!--         SELECT USER_ID FROM STAFF WHERE STAFF_NO = #{staffNo} -->
//<!--     </select> -->
//
//<!--     <insert id="insertLctApplyGradeRatio" parameterType="map"> -->
//<!--         INSERT INTO LCT_APPLY_GRADERATIO ( -->
//<!--             LCT_APPLY_ID, -->
//<!--             GRADE_CRITEIRA_CD, -->
//<!--             RATIO -->
//<!--         ) VALUES ( -->
//<!--             #{lctApplyId}, -->
//<!--             #{gradeCriteriaCd}, -->
//<!--             #{ratio} -->
//<!--         ) -->
//<!--     </insert> -->
//
//
//
//<!--     임시저장용 승인 레코드 등록 -->
//<!--     <insert id="insertDraftApproval" parameterType="map"> -->
//<!--         <selectKey keyProperty="approveId" resultType="string" order="BEFORE"> -->
//<!--             SELECT 'APPR' || LPAD(SEQ_APPROVAL.NEXTVAL, 11, '0') FROM DUAL -->
//<!--         </selectKey> -->
//<!--         INSERT INTO APPROVAL ( -->
//<!--             APPROVE_ID, -->
//<!--             USER_ID, -->
//<!--             APPLICANT_USER_ID, -->
//<!--             APPLY_TYPE_CD, -->
//<!--             APPROVE_YNNULL -->
//<!--         ) VALUES ( -->
//<!--             #{approveId}, -->
//<!--             #{userId}, -->
//<!--             #{applicantUserId}, -->
//<!--             #{applyTypeCd}, -->
//<!--             NULL -->
//<!--         ) -->
//<!--     </insert> -->
//
//
//    
//
//
//
// 
//
// 
//
//<!--     <update id="updateLctOpenApply"> -->
//<!--         UPDATE lct_open_apply -->
//<!--         SET -->
//<!--             subject_cd = #{subjectCd}, -->
//<!--             professor_no = #{professorNo}, -->
//<!--             yearterm_cd = #{yeartermCd}, -->
//<!--             lecture_index = #{lectureIndex}, -->
//<!--             lecture_goal = #{lectureGoal}, -->
//<!--             prereq_subject = #{prereqSubject}, -->
//<!--             expect_cap = #{expectCap}, -->
//<!--             desire_option = #{desireOption}, -->
//<!--             cancel_yn = #{cancelYn}, -->
//<!--             approve_id = #{approveId} -->
//<!--         WHERE -->
//<!--             lct_apply_id = #{lctApplyId} -->
//<!--     </update> -->
//
//<!--     <delete id="deleteLctOpenApply"> -->
//<!--         DELETE FROM lct_open_apply -->
//<!--         WHERE -->
//<!--             lct_apply_id = #{lctApplyId} -->
//<!--     </delete> -->
//	
//
//
//<!--     <select id="selectLctOpenApplyCount" parameterType="map" resultType="int"> -->
//<!--         SELECT -->
//<!--             COUNT(*) -->
//<!--         FROM -->
//<!--             LCT_OPEN_APPLY loa -->
//<!--         JOIN SUBJECT s ON -->
//<!--             loa.SUBJECT_CD = s.SUBJECT_CD -->
//<!--         LEFT JOIN APPROVAL A ON loa.APPROVE_ID = A.APPROVE_ID -->
//<!--         LEFT JOIN PROFESSOR p ON loa.PROFESSOR_NO = p.PROFESSOR_NO -->
//<!--         <where> -->
//<!--             <if test="academicYear != null and academicYear != ''"> -->
//<!--                 AND loa.YEARTERM_CD LIKE #{academicYear} || '%' -->
//<!--             </if> -->
//<!--             <if test="completionCd != null and completionCd != ''"> -->
//<!--                 AND s.COMPLETION_CD = #{completionCd} -->
//<!--             </if> -->
//<!--             <if test="status != null and status != ''"> -->
//<!--                 AND ( -->
//<!--                 <choose> -->
//<!--                     <when test="status == 'PENDING'"> -->
//<!--                         A.APPROVE_YNNULL IS NULL AND loa.CANCEL_YN = 'N' -->
//<!--                     </when> -->
//<!--                     <when test="status == 'APPROVED'"> -->
//<!--                         A.APPROVE_YNNULL = 'Y' -->
//<!--                     </when> -->
//<!--                     <when test="status == 'REJECTED'"> -->
//<!--                         A.APPROVE_YNNULL = 'N' -->
//<!--                     </when> -->
//<!--                     <when test="status == 'CANCELED'"> -->
//<!--                         loa.CANCEL_YN = 'Y' -->
//<!--                     </when> -->
//<!--                     <otherwise> -->
//<!--                         1=1 -->
//<!--                     </otherwise> -->
//<!--                 </choose> -->
//<!--                 ) -->
//<!--             </if> -->
//<!--             <if test="univDeptCd != null and univDeptCd != ''"> -->
//<!--                 AND p.UNIV_DEPT_CD = #{univDeptCd} -->
//<!--             </if> -->
//<!--             <if test="professorNo != null and professorNo != ''"> -->
//<!--                 AND loa.PROFESSOR_NO = #{professorNo} -->
//<!--             </if> -->
//<!--         </where> -->
//<!--     </select> -->
//
//<!--     <select id="selectLctOpenApplyList" parameterType="map" resultType="map"> -->
//<!--         SELECT B.* -->
//<!--         FROM ( -->
//<!--             SELECT ROWNUM AS RNUM, A.* -->
//<!--             FROM ( -->
//<!--                 SELECT -->
//<!--                     loa.LCT_APPLY_ID, -->
//<!--                     loa.APPLY_AT, -->
//<!--                     loa.SUBJECT_CD, -->
//<!--                     REGEXP_REPLACE(s.SUBJECT_NAME, '\\([^)]*\\)', '') AS SUBJECT_NAME, -->
//<!--                     (SELECT CD_NAME FROM COMMON_CODE WHERE COMMON_CD = s.COMPLETION_CD AND COMMON_SORT_CD = 'COMPLETION_CD') AS COMPLETION_CD_NAME, -->
//<!--                     s.CREDIT, loa.CANCEL_YN, -->
//<!--                     loa.APPROVE_ID, -->
//<!--                     CASE -->
//<!--                         WHEN loa.CANCEL_YN = 'Y' THEN '신청 취소' -->
//<!--                         WHEN A.APPROVE_YNNULL = 'Y' THEN '승인' -->
//<!--                         WHEN A.APPROVE_YNNULL = 'N' THEN '반려' -->
//<!--                         ELSE '대기중' -->
//<!--                     END AS APPROVE_STATUS_NAME -->
//<!--                 FROM -->
//<!--                     LCT_OPEN_APPLY loa -->
//<!--                 JOIN SUBJECT s ON -->
//<!--                     loa.SUBJECT_CD = s.SUBJECT_CD -->
//<!--                 LEFT JOIN APPROVAL A ON loa.APPROVE_ID = A.APPROVE_ID -->
//<!--                 LEFT JOIN PROFESSOR p ON loa.PROFESSOR_NO = p.PROFESSOR_NO -->
//<!--                 <where> -->
//<!--                     <if test="academicYear != null and academicYear != ''"> -->
//<!--                         AND loa.YEARTERM_CD LIKE #{academicYear} || '%' -->
//<!--                     </if> -->
//<!--                     <if test="completionCd != null and completionCd != ''"> -->
//<!--                         AND s.COMPLETION_CD = #{completionCd} -->
//<!--                     </if> -->
//<!--                     <if test="status != null and status != ''"> -->
//<!--                         AND ( -->
//<!--                         <choose> -->
//<!--                             <when test="status == 'PENDING'"> -->
//<!--                                 A.APPROVE_YNNULL IS NULL AND loa.CANCEL_YN = 'N' -->
//<!--                             </when> -->
//<!--                             <when test="status == 'APPROVED'"> -->
//<!--                                 A.APPROVE_YNNULL = 'Y' -->
//<!--                             </when> -->
//<!--                             <when test="status == 'REJECTED'"> -->
//<!--                                 A.APPROVE_YNNULL = 'N' -->
//<!--                             </when> -->
//<!--                             <when test="status == 'CANCELED'"> -->
//<!--                                 loa.CANCEL_YN = 'Y' -->
//<!--                             </when> -->
//<!--                             <otherwise> -->
//<!--                                 1=1 -->
//<!--                             </otherwise> -->
//<!--                         </choose> -->
//<!--                         ) -->
//<!--                     </if> -->
//<!--                     <if test="univDeptCd != null and univDeptCd != ''"> -->
//<!--                         AND p.UNIV_DEPT_CD = #{univDeptCd} -->
//<!--                     </if> -->
//<!--                     <if test="professorNo != null and professorNo != ''"> -->
//<!--                         AND loa.PROFESSOR_NO = #{professorNo} -->
//<!--                     </if> -->
//<!--                 </where> -->
//<!--                 ORDER BY -->
//<!--                     loa.LCT_APPLY_ID DESC -->
//<!--             ) A -->
//<!--         ) B -->
//<!--         WHERE RNUM BETWEEN #{startRow} AND #{endRow} -->
//<!--     </select> -->
//<!--     <select id="selectLectureApplicationDetail" parameterType="string" resultType="map"> -->
//<!--         SELECT -->
//<!--             loa.LCT_APPLY_ID, -->
//<!--             loa.APPLY_AT, -->
//<!--             loa.SUBJECT_CD, -->
//<!--             s.SUBJECT_NAME, -->
//<!--             (SELECT CD_NAME FROM COMMON_CODE WHERE COMMON_CD = s.COMPLETION_CD AND COMMON_SORT_CD = 'COMPLETION_CD') AS COMPLETION_CD_NAME, -->
//<!--             s.CREDIT, -->
//<!--             s.HOUR AS HOURS, -->
//<!--             loa.PROFESSOR_NO, -->
//<!--             (SELECT U.LAST_NAME || U.FIRST_NAME FROM USERS U JOIN PROFESSOR P ON U.USER_ID = P.USER_ID WHERE P.PROFESSOR_NO = loa.PROFESSOR_NO) AS PROFESSOR_NAME, -->
//<!--             loa.YEARTERM_CD, -->
//<!--             loa.LECTURE_INDEX, -->
//<!--             loa.LECTURE_GOAL, -->
//<!--             loa.PREREQ_SUBJECT, -->
//<!--             loa.EXPECT_CAP, -->
//<!--             loa.APPROVE_ID, -->
//<!--             A.COMMENTS, -->
//<!--             CASE -->
//<!--                 WHEN loa.CANCEL_YN = 'Y' THEN '신청 취소' -->
//<!--                 WHEN A.APPROVE_YNNULL = 'Y' THEN '승인' -->
//<!--                 WHEN A.APPROVE_YNNULL = 'N' THEN '반려' -->
//<!--                 ELSE '대기중' -->
//<!--             END AS APPROVE_STATUS_NAME -->
//<!--         FROM -->
//<!--             LCT_OPEN_APPLY loa -->
//<!--         JOIN SUBJECT s ON -->
//<!--             loa.SUBJECT_CD = s.SUBJECT_CD -->
//<!--         LEFT JOIN APPROVAL A ON loa.APPROVE_ID = A.APPROVE_ID -->
//<!--         WHERE -->
//<!--             loa.LCT_APPLY_ID = #{lctApplyId} -->
//<!--     </select> -->
//
//<!--     <select id="selectLctApplyWeekbyList" parameterType="string" resultType="map"> -->
//<!--         SELECT -->
//<!--             LECTURE_WEEK, -->
//<!--             WEEK_GOAL, -->
//<!--             WEEK_DESC -->
//<!--         FROM -->
//<!--             LCT_APPLY_WEEKBY -->
//<!--         WHERE -->
//<!--             LCT_APPLY_ID = #{lctApplyId} -->
//<!--         ORDER BY -->
//<!--             LECTURE_WEEK ASC -->
//<!--     </select> -->
//
//<!--     <select id="selectLctApplyGradeRatioList" parameterType="string" resultType="map"> -->
//<!--         SELECT -->
//<!--             GRADE_CRITEIRA_CD, -->
//<!--             (SELECT CD_NAME FROM COMMON_CODE WHERE COMMON_CD = GRADE_CRITEIRA_CD AND COMMON_SORT_CD = 'GRADE_CRITERIA_CD') AS GRADE_CRITEIRA_CD_NAME, -->
//<!--             RATIO -->
//<!--         FROM -->
//<!--             LCT_APPLY_GRADERATIO -->
//<!--         WHERE -->
//<!--             LCT_APPLY_ID = #{lctApplyId} -->
//<!--         ORDER BY -->
//<!--             GRADE_CRITEIRA_CD ASC -->
//<!--     </select> -->
//
//
//
//<!--     <update id="updateLctOpenApplyApprovalId" parameterType="map"> -->
//<!--         UPDATE LCT_OPEN_APPLY -->
//<!--         SET -->
//<!--             APPROVE_ID = #{approveId} -->
//<!--         WHERE -->
//<!--             LCT_APPLY_ID = #{lctApplyId} -->
//<!--     </update> -->
//
//<!--     <update id="updateLectureApplicationStatus" parameterType="map"> -->
//<!--         UPDATE LCT_OPEN_APPLY -->
//<!--         SET -->
//<!--             CANCEL_YN = #{status} -->
//<!--         WHERE -->
//<!--             LCT_APPLY_ID = #{lctApplyId} -->
//<!--     </update> -->
//
//<!--     승인 처리를 위한 쿼리들 -->
//<!--     <select id="getApprovalById" parameterType="string" resultType="map"> -->
//<!--         SELECT * FROM APPROVAL WHERE APPROVE_ID = #{approveId} -->
//<!--     </select> -->
//
//<!--     <update id="updateApprovalStatus" parameterType="map"> -->
//<!--         UPDATE APPROVAL -->
//<!--         SET  -->
//<!--             APPROVE_YNNULL = #{status}, -->
//<!--             COMMENTS = #{comment}, -->
//<!--             APPROVE_AT = SYSDATE -->
//<!--         WHERE APPROVE_ID = #{approveId} -->
//<!--     </update> -->
//
//<!--     <select id="findNextApprovalId" parameterType="string" resultType="string"> -->
//<!--         SELECT APPROVE_ID FROM APPROVAL WHERE PREV_APPROVE_ID = #{currentApproveId} -->
//<!--     </select> -->

}
