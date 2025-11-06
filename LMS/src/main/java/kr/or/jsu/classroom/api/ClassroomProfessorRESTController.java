package kr.or.jsu.classroom.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classroom.dto.request.AttendanceRecordReq;
import kr.or.jsu.classroom.dto.request.ExamOfflineReq;
import kr.or.jsu.classroom.dto.request.ExamWeightValueModReq;
import kr.or.jsu.classroom.dto.request.GroupTaskCreateReq;
import kr.or.jsu.classroom.dto.request.IndivTaskCreateReq;
import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.request.LecturePostReq;
import kr.or.jsu.classroom.dto.request.ender.LctGPARecordReq;
import kr.or.jsu.classroom.dto.request.ender.LctSectionScoreUpsertReq;
import kr.or.jsu.classroom.dto.request.exam.ExamModifyReq;
import kr.or.jsu.classroom.dto.request.exam.ScoreModifyReq;
import kr.or.jsu.classroom.dto.request.finalize.ChangeScoreReq;
import kr.or.jsu.classroom.dto.request.task.GrouptaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskEvaluateReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.TaskWeightValueReq;
import kr.or.jsu.classroom.dto.response.attandance.LctAttRoundLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentAttLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentForAttendanceResp;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;
import kr.or.jsu.classroom.dto.response.classroom.ProfessorMyInfoResp;
import kr.or.jsu.classroom.dto.response.ender.AttendanceStatusResp;
import kr.or.jsu.classroom.dto.response.ender.EnrollingStudentsAndScoreResp;
import kr.or.jsu.classroom.dto.response.ender.ExamWeightAndStatusResp;
import kr.or.jsu.classroom.dto.response.ender.LctSectionScoreResp;
import kr.or.jsu.classroom.dto.response.ender.LectureProgressResp;
import kr.or.jsu.classroom.dto.response.ender.TaskWeightAndStatusResp;
import kr.or.jsu.classroom.dto.response.exam.EachStudentExamScoreResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.TaskStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.ExamStatisticsResp;
import kr.or.jsu.classroom.dto.response.student.LectureEnrollingStudentResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllExamSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllGrouptaskSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllIndivtaskSubmitResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentGrouptaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentIndivtaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedGrouptaskResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedIndivtaskResp;
import kr.or.jsu.classroom.dto.response.task.TaskWeightValueResp;
import kr.or.jsu.classroom.professor.service.ClassPrfAttendanceService;
import kr.or.jsu.classroom.professor.service.ClassPrfBoardService;
import kr.or.jsu.classroom.professor.service.ClassPrfEnderService;
import kr.or.jsu.classroom.professor.service.ClassPrfExamService;
import kr.or.jsu.classroom.professor.service.ClassPrfStatisticsService;
import kr.or.jsu.classroom.professor.service.ClassPrfStuManageService;
import kr.or.jsu.classroom.professor.service.ClassPrfTaskService;
import kr.or.jsu.classroom.professor.service.ClassroomProfessorService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.lms.professor.service.info.ProfInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/classroom/api/v1/professor")
@RequiredArgsConstructor
public class ClassroomProfessorRESTController {
	
	private final ClassroomProfessorService classPrfService;
	private final ClassPrfBoardService boardService;
	private final ClassPrfAttendanceService attendanceService;
	private final ClassPrfTaskService taskService;
	private final ClassPrfExamService examService;
	private final ClassPrfStuManageService studentManageService;
	private final ClassPrfStatisticsService statisticsService;
	private final ClassPrfEnderService enderService;
	private final ProfInfoService profInfoService;

	// TODO 인가처리, 교수만 사용 가능
	
	// ========================================
	// 레이아웃 + 메인화면 관련
	// ========================================
	
	// 내 정보 요청(학생 기본정보)(헤더에 띄울)
	@GetMapping("/me")
	public ProfessorMyInfoResp showMe() {
		return classPrfService.getMyInfo();
	}
	
	// 내 시간표 요쳥(학년도학기별)
	@GetMapping("/schedule")
	public List<LectureScheduleResp> showMySchedule(
		@RequestParam(required = false) String yeartermCd
	) {
		return classPrfService.readMySchedule(yeartermCd);
	}
	
	// 평가해야 할 개인과제 목록
	@GetMapping("/me/indivtask")
	public List<NotEvaluatedIndivtaskResp> notEvaluatedIndivtask() {
		return taskService.readNotEvaluatedIndivtaskList();
	}
	
	// 평가해야 할 조별과제 목록
	@GetMapping("/me/grouptask")
	public List<NotEvaluatedGrouptaskResp> notEvaluatedGrouptask() {
		
		
		return taskService.readNotEvaluatedGrouptaskList();
	}
	
	
	// ====================
	// 레이아웃 + 메인화면 끝
	// ====================

	// ========================================
	// 대시보드(통계) 관련
	// ========================================
	
	@GetMapping("/advisee-grade-distribution")
    public ResponseEntity<Map<String, Integer>> getAdviseeGradeDistribution(
            @AuthenticationPrincipal CustomUserDetails loginUser) {
        String professorNo = loginUser.getRealUser().getUserNo();
        Map<String, Integer> gradeDistribution = profInfoService.getAdviseeGradeDistribution(professorNo);
        return ResponseEntity.ok(gradeDistribution);
    }

    @GetMapping("/advisee-grade-by-year")
    public ResponseEntity<Map<String, Integer>> getAdviseeGradeByYearDistribution(
            @AuthenticationPrincipal CustomUserDetails loginUser) {
        String professorNo = loginUser.getRealUser().getUserNo();
        Map<String, Integer> gradeYearDistribution = profInfoService.getAdviseeGradeByYearDistribution(professorNo);
        return ResponseEntity.ok(gradeYearDistribution);
    }

    @GetMapping("/lecture-grade-distributions")
    public ResponseEntity<List<kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp>> getLectureGradeDistributions(
            @AuthenticationPrincipal CustomUserDetails loginUser) {
        String professorNo = loginUser.getRealUser().getUserNo();
        List<kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp> lectureGradeDistributions = profInfoService.getLectureGradeDistributions(professorNo);
        return ResponseEntity.ok(lectureGradeDistributions);
    }

	@GetMapping("/{lectureId}/statis/enroll")
	public EnrollStuStatisticsResp readEnrollmentStat(
		@PathVariable String lectureId
	) {
		return statisticsService.readEnrollmentStat(lectureId);
	}
	
	@GetMapping("/{lectureId}/statis/attendance")
	public AttendanceStatisticsResp b(
		@PathVariable String lectureId
	) {
		return statisticsService.readAttendanceStat(lectureId);
	}
	
	@GetMapping("/{lectureId}/statis/task")
	public TaskStatisticsResp c(
		@PathVariable String lectureId
	) {
		return statisticsService.readTaskStat(lectureId);
	}

	@GetMapping("/{lectureId}/statis/exam")
	public ExamStatisticsResp readExamStatistic(
		@PathVariable String lectureId
	) {
		return statisticsService.readExamStat(lectureId);
	}
	
	// ====================
	// 대시보드(통계) 관련
	// ====================
	
	// ========================================
	// 강의 관련
	// ========================================
	
	/**
	 * 사용자 교수와 관련된 모든 강의 목록을 조회합니다.
	 * 
	 * @return 강의목록에 출력할 정보
	 */
	@GetMapping("/mylist")
	public List<LectureLabelResp> myLectureList() {
		
		List<LectureLabelResp> list = classPrfService.readMyLectureList();
		// TODO 오류처리
//		if(list.size() == 0) {
//			throw new RuntimeException("해당 조건에 일치하는 강의가 없습니다.");
//		}
		
		return list;
	}
	
	// ====================
	// 강의 관련 끝
	// ====================
	
	/**
	 * 교수의 게시글 작성 요청을 처리합니다.
	 *
	 * @param lectureId 강의ID
	 * @param post 게시글작성 전용 요청 Bean
	 * @param loginUser 로그인 중인 교수
	 * @return 게시글 생성 결과
	 */
	@PostMapping("/board/{lectureId}/post")
	public ResponseEntity<?> createPost(
	    @PathVariable String lectureId,
	    @RequestBody LecturePostReq post,
	    @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    log.info("대상 강의ID : {}", lectureId);
	    log.info("포스트 내용 : {}", PrettyPrint.pretty(post));

	    String prfNo = loginUser.getRealUser().getUserNo();

	    // 실제 게시글 생성 처리
	    String postId = boardService.createPost(post, lectureId, prfNo);

	    // Location 헤더 (게시글 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/classroom/professor/%s/board/%s", lectureId, postId
	    ));

	    return ResponseEntity
	            .created(location) // 201 Created
	            .body(Map.of("success", "게시글 작성 성공", "postId", postId));
	}
	
	/**
	 * 교수의 강의 게시글 목록 요청을 처리합니다.
	 * 
	 * @param lectureId 강의 ID
	 * @param postType 게시글 타입(공지, 자료), null일 시 모든 타입
	 * @param loginUser 로그인 중인 교수
	 * @return 강의 게시글 목록에 필요한 정보 리스트(게시글번호, 제목, 작성일, 타입)
	 */
	@GetMapping("/board/{lectureId}/post")
	public List<LctPostLabelResp_PRF> readPostList(
		@PathVariable String lectureId
		, @RequestParam(required = false) String postType
		, @AuthenticationPrincipal CustomUserDetails loginUser 
	) {
		String prfNo = loginUser.getRealUser().getUserNo();
		
		return boardService.readPostList(lectureId, prfNo, postType);
	}
	
	/**
	 * 교수의 강의 게시글 상세조회 요청을 처리합니다.
	 * 
	 * @param lectureId 강의 ID
	 * @param lctPostId 게시글 ID
	 * @param loginUser 로그인된 사용자
	 * @return 
	 */
	@GetMapping("/board/{lectureId}/post/{lctPostId}")
	public LctPostDetailResp_PRF readPost(
		@PathVariable String lectureId
		, @PathVariable String lctPostId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		String prfNo = loginUser.getRealUser().getUserNo();
		
		// TODO lectureId는 경로변수긴 한데, 굳이 확인을 안 해도 될거같은데...
		LctPostDetailResp_PRF post = boardService.readPost(lctPostId, prfNo);
		return post;
	}
	
	/**
	 * 게시글 삭제 요청
	 * 
	 * @param lectureId
	 * @param lctPostId
	 * @param loginUser
	 * @return
	 */
	@DeleteMapping("/board/{lectureId}/post/{lctPostId}")
	public ResponseEntity<Void> removePost(
		@PathVariable String lectureId
		, @PathVariable String lctPostId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		String prfNo = loginUser.getRealUser().getUserNo();
		boardService.removePost(lectureId, lctPostId, prfNo);
		
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * 게시글 수정 요청
	 * 
	 * @param lectureId
	 * @param lctPostId
	 * @param post
	 * @param loginUser
	 * @return
	 */
	@PutMapping("/board/{lectureId}/post/{lctPostId}")
	public ResponseEntity<Void> modifyPost(
		@PathVariable String lectureId
		, @PathVariable String lctPostId 
		, @RequestBody LecturePostReq post
	    , @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		String prfNo = loginUser.getRealUser().getUserNo();
		boardService.editPost(post, lectureId, lctPostId, prfNo);
		
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * 댓글 작성 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID
	 * @param lctPostId 경로변수 : 대상 게시글ID
	 * @param newReply 댓글내용, (대댓글일 경우) 부모댓글ID
	 * @return
	 */
	@PostMapping("/board/{lectureId}/post/{lctPostId}/reply")
	public ResponseEntity<?> createReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId 
		, @RequestBody LctPostReplyReq newReply
	) {
		log.info("댓글 작성 요청 발생, 대상 강의 : {}, 게시글 : {}", lectureId, lctPostId);
		log.info("댓글 내용 : {}", PrettyPrint.pretty(newReply));
		
		String lctReplyId = boardService.createReply(newReply, lectureId, lctPostId);
		
		return ResponseEntity.ok().body(Map.of("success", "댓글 작성 성공", "lctReplyId", lctReplyId));
	}
	
	/**
	 * 댓글 목록 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID
	 * @param lctPostId 경로변수 : 대상 게시글ID
	 * @return 
	 */
	@GetMapping("/board/{lectureId}/post/{lctPostId}/reply")
	public List<LctPostReplyResp> readReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId	
	) {
		return boardService.readReplyList(lectureId, lctPostId);
	}
	
	/**
	 * 댓글 삭제 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID
	 * @param lctPostId 경로변수 : 대상 게시글ID
	 * @param lctReplyId 경로변수 : 대상 댓글ID
	 * @return
	 */
	@DeleteMapping("/board/{lectureId}/post/{lctPostId}/reply/{lctReplyId}")
	public ResponseEntity<Void> deleteReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId 
		, @PathVariable String lctReplyId 
	) {
		boardService.deleteReply(lectureId, lctPostId, lctReplyId);
		
		return ResponseEntity.noContent().build();
	}
	
	// ====================
	// 출석 관련 시작
	// ====================
	
	// 새 강의 출석회차 생성
	@PostMapping("/attendance/{lectureId}")
	public int newAttandanceRound(
		@PathVariable String lectureId
		, @RequestParam(value = "default_status", required = false, defaultValue = "TBD") String defaultStatus
	) {
		// TODO 예외처리
		Set<String> availableStatus = Set.of("TBD", "OK", "NO");
		if(!availableStatus.contains(defaultStatus))
			throw new RuntimeException("학생 출석상태 기본값은 TBD(미정), OK(출석), NO(결석) 중 하나여야 합니다.");
		
		int newRound = attendanceService.createManualAttRound(lectureId, defaultStatus);
		
		return newRound;
	}
	
	// 강의 출석회차 삭제하기
	@DeleteMapping("/attendance/{lectureId}/{lctRound}")
	public ResponseEntity<Void> removeAttandanceRound(
		@PathVariable String lectureId
		, @PathVariable int lctRound
	) {
		attendanceService.removeAttRound(lectureId, lctRound);
		return ResponseEntity.noContent().build();
	}
	
	// 생성된 강의 수동출석체크를 위한 학생정보 요청
	@GetMapping("/attendance/{lectureId}/{lctRound}/manual")
	public List<StudentForAttendanceResp> studentInfosForManualAtt(
		@PathVariable String lectureId
		, @PathVariable int lctRound
	) {
		// 출석체크할 때 사용할 학생의 학번, 수강ID, 이름, 학년, 소속, (증명사진)파일ID의 리스트 반환
		return attendanceService.getStudentListForAtt(lectureId, lctRound);
	}

    // 수동 출석체크의 결과를 저장
    @PostMapping("/attendance/{lectureId}/{lctRound}/manual")
    public ResponseEntity<Void> saveAttendanceRecordList(
        @PathVariable String lectureId
        , @PathVariable int lctRound
        , @RequestBody List<AttendanceRecordReq> items
    ) {
    	// 1. items가 비었으면 400코드 + 체크한 학생이 없습니다
    	if(items.size() == 0) throw new RuntimeException("출석 체크한 학생이 없습니다.");
    	
    	// 2. 데이터 때려넣고 해줘
    	attendanceService.modifyAttendanceRecord(lectureId, lctRound, items);

        // 처리 미구현: 202(accepted)로 응답
        return ResponseEntity.ok().build();
    }
    
    // 강의의 모든 출석회차를 불러옴
    @GetMapping("/attendance/{lectureId}/all")
    public List<LctAttRoundLabelResp> readAttendanceList(
    	@PathVariable String lectureId
    ) {
    	return attendanceService.readAttRoundLabel(lectureId);
    }
    
    // 강의의 수강생별 출석 요약을 불러옴
    @GetMapping("/attendance/{lectureId}/summary")
    public List<StudentAttLabelResp> readAttendanceSummary(
		@PathVariable String lectureId
    ) {
    	return attendanceService.getStudentAttendanceSummary(lectureId);
    };
    
	// ====================
	// 출석 관련 끝
	// ====================
    
	// ========================================
	// 과제 관련
	// ========================================
    
    // 개인과제 생성
    @PostMapping("/task/{lectureId}/indiv")
    public ResponseEntity<?> createIndivTask(
		@PathVariable String lectureId
	    , @RequestBody IndivTaskCreateReq indivTask
    ) {
    	
    	log.info("개인과제 : {}", PrettyPrint.pretty(indivTask));
    	String indivtaskId = taskService.createIndivtask(lectureId, indivTask);
    	
    	// Location 헤더 (게시글 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/classroom/professor/%s/task/indiv/%s", lectureId, indivtaskId
	    ));

	    return ResponseEntity
	            .created(location) // 201 Created
	            .body(Map.of("success", "과제 출제 성공", "indivtaskId", indivtaskId));
    }

	// 개인과제 목록 조회(제출율 포함)
    @GetMapping("/task/{lectureId}/indiv")
	public List<IndivtaskLabelResp_PRF> readIndivtaskList(
		@PathVariable String lectureId
	) {
    	return taskService.readIndivtaskList(lectureId);
    }
	
	// 개인과제 상세 + 학생별 제출 조회
    @GetMapping("/task/{lectureId}/indiv/{indivtaskId}")
    public LctIndivtaskDetailResp readIndivtask(
    	@PathVariable String lectureId
    	, @PathVariable String indivtaskId
    ) {
    	return taskService.readIndivtask(lectureId, indivtaskId);
    }
    
    // 개인과제 삭제
    @DeleteMapping("/task/{lectureId}/indiv/{indivtaskId}")
    public ResponseEntity<Void> removeIndivTask(
		@PathVariable String lectureId
		, @PathVariable String indivtaskId
	) {
    	taskService.removeIndivtask(lectureId, indivtaskId);
    	return ResponseEntity.noContent().build();
    }
    
    // 개인과제 수정
    @PutMapping("/task/{lectureId}/indiv/{indivtaskId}")
    public ResponseEntity<Void> modifyIndivTask(
		@PathVariable String lectureId
	    , @RequestBody IndivtaskModifyReq indivTask
    ) {
    	log.info("개인과제 수정 요청, 내용 : {}", PrettyPrint.pretty(indivTask));
    	
    	taskService.modifyIndivtask(lectureId, indivTask);

	    return ResponseEntity.ok().build();
    }
    
    // 개인과제 제출에 대한 평가 작성
    @PatchMapping("/task/{lectureId}/indiv/{indivtaskId}/eval")
    public ResponseEntity<Void> evaluateIndivTask(
		@PathVariable String lectureId
	    , @PathVariable String indivtaskId
	    , @RequestBody IndivtaskEvaluateReq request
    ) {
    	taskService.evaluateIndivtaskSubmit(lectureId, indivtaskId, request);
    	
	    return ResponseEntity.ok().build();
    }
    
    // 조별과제 생성
    @PostMapping("/task/{lectureId}/group")
    public ResponseEntity<?> createGroupTask(
		@PathVariable String lectureId
		, @RequestBody GroupTaskCreateReq groupTaskAndGroups
	) {
    	log.info("조별과제 작성 요청, 대상 강의 : {}, 내용 : {}", lectureId, PrettyPrint.pretty(groupTaskAndGroups));
    	
    	String grouptaskId = taskService.createGrouptask(lectureId, groupTaskAndGroups);
    	
    	// Location 헤더 (시험결과 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/classroom/professor/%s/task/group/%s", lectureId, grouptaskId
	    ));
    	
    	return ResponseEntity.created(location) // 201 Created
    						 .body(Map.of("success", "조별과제 생성 성공", "grouptaskId", grouptaskId));    	
    }
    
    // 조별과제 목록 + 제출율 조회
    @GetMapping("/task/{lectureId}/group")
    public List<GrouptaskLabelResp_PRF> readGrouptaskList(
		@PathVariable String lectureId
	) {
    	return taskService.readGrouptaskList(lectureId);
    }
    
    // 조별과제 상세 조회 1 : 조별과제 내용 조회
    @GetMapping("/task/{lectureId}/group/{grouptaskId}")
    public LctGrouptaskDetailResp readGrouptask(
    	@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    ) {
    	return taskService.readGrouptask(lectureId, grouptaskId);
    }
    
    // 조별과제 상세 조회 2 : 조 구성 조회
    @GetMapping("/task/{lectureId}/group/{grouptaskId}/jo")
    public List<GrouptaskJoResp> readGrouptaskJo(
    	@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    ) {
    	return taskService.readGrouptaskJo(lectureId, grouptaskId);
    }
    
    // 조별과제 조 재구성
    
    // 조별과제 수정
    @PutMapping("/task/{lectureId}/group/{grouptaskId}")
    public ResponseEntity<Void> modifyGroupTask(
		@PathVariable String lectureId
	    , @RequestBody GrouptaskModifyReq groupTask
    ) {
    	log.info("조별과제 수정 요청, 내용 : {}", PrettyPrint.pretty(groupTask));
    	taskService.modifyGrouptask(lectureId, groupTask);
	    return ResponseEntity.ok().build();
    }
    
    // 조별과제 삭제
    @DeleteMapping("/task/{lectureId}/group/{grouptaskId}")
    public ResponseEntity<Void> removeGroupTask(
		@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    ) {
    	taskService.removeGrouptask(lectureId, grouptaskId);
    	return ResponseEntity.noContent().build();
    }
    
    // 과제별 가중치 요청
    @GetMapping("/task/{lectureId}/weight")
    public List<TaskWeightValueResp> readTaskWeightValues(
		@PathVariable String lectureId
    ) {
    	return taskService.readTaskWeightValues(lectureId);
    }
    
    // 과제별 가중치 수정
    @PutMapping("/task/{lectureId}/weight")
    public ResponseEntity<Void> modifyTaskWeightValues(
		@PathVariable String lectureId
		, @RequestBody List<TaskWeightValueReq> request
	) {
    	
    	log.info("수정 요청 : {}", PrettyPrint.pretty(request));
    	
    	taskService.modifyAllTaskWeightValues(lectureId, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 종강시 과제 점수계산용 1
    @GetMapping("/task/{lectureId}/indiv/summary")
    public List<EachStudentIndivtaskScoreResp> readAllIndivtaskScore(
		@PathVariable String lectureId	
    ) {
    	return taskService.readAllIndivtaskAndEachStudentScore(lectureId);
    }
    
    // 종강시 과제 점수계산용 2
    @GetMapping("/task/{lectureId}/group/summary")
    public List<EachStudentGrouptaskScoreResp> readAllGrouptaskScore(
		@PathVariable String lectureId	
    ) {
    	return taskService.readAllGrouptaskAndEachStudentScore(lectureId);
    }
    
	// ====================
	// 과제 관련 끝
	// ====================
    
	// ========================================
	// 시험 관련
	// ========================================

    // 오프라인 시험 + 채점결과 기록
    @PostMapping("/exam/{lectureId}/offline")
    public ResponseEntity<?> createOfflineExam(
		@PathVariable String lectureId
		, @RequestBody ExamOfflineReq examAndEachResults
    ) {
    	examAndEachResults.setLectureId(lectureId);
    	log.info("시험 결과를 기록하려는 강의ID : {}", lectureId);
    	log.info("기록하려는 시험과 학생들의 채점결과 : {}", PrettyPrint.pretty(examAndEachResults));
    	
    	String lctExamId = examService.createOfflineExamRecord(examAndEachResults);
    	
    	// Location 헤더 (시험결과 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/classroom/professor/%s/exam/%s", lectureId, lctExamId
	    ));
    	
    	return ResponseEntity.created(location) // 201 Created
    						 .body(Map.of("success", "시험기록 생성 성공", "lctExamId", lctExamId));
    }
    
    // 시험 목록 + 응시율 조회
    @GetMapping("/exam/{lectureId}")
    public List<LctExamLabelResp_PRF> readLctExamList(
		@PathVariable String lectureId
    ) {
    	return examService.readExamList(lectureId);
    }
    
    // 시험별 가중치 일괄수정
    @PutMapping("/exam/{lectureId}/weightValue")
    public ResponseEntity<Void> modifyAllExamWeightValue(
		@PathVariable String lectureId
		, @RequestBody List<ExamWeightValueModReq> request
    ) {
    	examService.modifyAllWeightValues(lectureId, request);
    	return ResponseEntity.noContent().build();
    }
    
    // 시험 상세보기 (시험 내용 + 학생별 응시 기록과 점수)
    @GetMapping("/exam/{lectureId}/{lctExamId}")
    public ExamAndEachStudentsSubmitResp readExam(
		@PathVariable String lectureId
		, @PathVariable String lctExamId
    ) {
    	return examService.readExamDetail(lectureId, lctExamId);
    }
    
    // 시험 응시기록 점수 수정
    @PutMapping("/exam/{lectureId}/{lctExamId}/score")
    public ResponseEntity<Void> modifyScore(
		@PathVariable String lectureId
		, @PathVariable String lctExamId
		, @RequestBody ScoreModifyReq request
    ) {
    	examService.modifyScore(lectureId, lctExamId, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 시험 내용 수정
    @PutMapping("/exam/{lectureId}/{lctExamId}")
    public ResponseEntity<Void> modifyExam(
		@PathVariable String lectureId
		, @PathVariable String lctExamId
		, @RequestBody ExamModifyReq request
    ) {
    	examService.modifyExam(lectureId, lctExamId, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 시험 삭제
    @DeleteMapping("/exam/{lectureId}/{lctExamId}")
    public ResponseEntity<Void> removeExam(
		@PathVariable String lectureId
		, @PathVariable String lctExamId
    ) {
    	examService.removeExam(lectureId, lctExamId);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 종강 시 시험 점수계산용
    @GetMapping("/exam/{lectureId}/summary")
    public List<EachStudentExamScoreResp> readAllExamScore(
		@PathVariable String lectureId	
    ) {
    	return examService.readAllExamAndEachStudentScore(lectureId);
    }
    
    // ====================
 	// 시험 관련 끝
 	// ====================
     
 	// ========================================
 	// 학생 관리 관련
 	// ========================================

	// 수강생 목록 요청
	@GetMapping("/{lectureId}/students")
	List<LectureEnrollingStudentResp> printTrainees(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		// 먼저 고유번호(학번 or 교번)를 얻고,
		String uqNo = loginUser.getRealUser().getUserNo();
		
		// 강의로 검색해서 수강생 리스트를 얻는다.
		List<LectureEnrollingStudentResp> list = studentManageService.readStudentList(lectureId);
		
		log.info("학생 목록 : {}", PrettyPrint.pretty(list));
		log.info("요청한 사람 : {}", uqNo);
		
		return list;
	}
    
	// 특정 학생에 대한 출석 기록 요청
    @GetMapping("/{lectureId}/{studentNo}/attendance")
    public List<StudentsAllAttendanceResp> readPersonalAttendanceRecords(
		@PathVariable String lectureId
		, @PathVariable String studentNo
    ) {
    	return studentManageService.readStudentAttendanceList(lectureId, studentNo);
    }
    
    // 특정 학생에 대한 개인과제 제출 기록 요청
    @GetMapping("/{lectureId}/{studentNo}/indivtask")
    public List<StudentsAllIndivtaskSubmitResp> readPersonalIndivtaskSubmitRecords(
		@PathVariable String lectureId
		, @PathVariable String studentNo
    ) {
    	return studentManageService.readStudentIndivtaskSubmitList(lectureId, studentNo);
    }
    
    // 특정 학생에 대한 조별과제 제출 기록 요청
    @GetMapping("/{lectureId}/{studentNo}/grouptask")
    public List<StudentsAllGrouptaskSubmitResp> readPersonalGrouptaskSubmitRecords(
		@PathVariable String lectureId
		, @PathVariable String studentNo
	) {
    	return studentManageService.readStudentGrouptaskSubmitList(lectureId, studentNo);
    }
    
    // 특정 학생에 대한 시험 응시기록 요청
    @GetMapping("/{lectureId}/{studentNo}/exam")
    public List<StudentsAllExamSubmitResp> readPersonalExamSubmitRecords(
		@PathVariable String lectureId
		, @PathVariable String studentNo
    ) {
    	return studentManageService.readStudentExamSubmitList(lectureId, studentNo);
    }
    
    // ====================
 	// 학생 관련 끝
 	// ====================
     
 	// ========================================
 	// 종강 관련
 	// ========================================
    
    // 종강 조건 1. 강의 진행도 확인
    @GetMapping("/{lectureId}/end/progress")
    public LectureProgressResp readEndCondition1(
		@PathVariable String lectureId
    ) {
    	return enderService.readProgress(lectureId);
    }
    
    // 종강 조건 2. 강의 과제 가중치 & 마감여부 확인
    @GetMapping("/{lectureId}/end/task")
    public List<TaskWeightAndStatusResp> readEndCondition2(
		@PathVariable String lectureId
    ) {
    	return enderService.readTaskStatus(lectureId);
    }
    
    // 종강 조건 3. 강의 시험 가중치 & 마감여부 확인
    @GetMapping("/{lectureId}/end/exam")
    public List<ExamWeightAndStatusResp> readEndCondition3(
		@PathVariable String lectureId
    ) {
    	return enderService.readExamStatus(lectureId);
    }
    
    // 종강 조건 4. 강의 출석 미정상태인 항목이 남아있는지 확인
    @GetMapping("/{lectureId}/end/attendance")
    public List<AttendanceStatusResp> readEndCondition4(
		@PathVariable String lectureId
    ) {
    	return enderService.readAttendanceStatus(lectureId);
    }
    
    // 종강 절차 1. 강의 평점 산출 비율별로 수강생 점수 일괄 입력/수정(upsert)
    @PatchMapping("/{lectureId}/end/score")
    public ResponseEntity<Void> upsertSectionScore(
		@PathVariable String lectureId
		, @RequestBody List<LctSectionScoreUpsertReq> request
    ) {
    	log.info("받은 요청 : {}", PrettyPrint.pretty(request));
    	
    	enderService.mergeEachSectionScore(lectureId, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 종강 절차 2. 강의 평점 산출 비율별로 수강생 점수 일괄 요청
    @GetMapping("/{lectureId}/end/score")
    public List<LctSectionScoreResp> selectSectionScore(
		@PathVariable String lectureId
		, @RequestParam(required = true, value = "section") String gradeCriteriaCd
    ) {
    	return enderService.readEachSectionScore(lectureId, gradeCriteriaCd);
    } 
    
    // 종강 절차 3. 모든 학생 점수 받아서 STU_ENROLL_LCT의 AUTO_GRADE, 
    @PatchMapping("/{lectureId}/end/record")
    public ResponseEntity<Void> recordGPA(
    	@PathVariable String lectureId
    	, @RequestBody List<LctGPARecordReq> request
    ) {
    	enderService.recordGPAAndScore(lectureId, request);
    
    	return ResponseEntity.noContent().build();
    }
    
    // 성적확정 절차 1. 모든 학생들의 성적을 포함한 정보를 요청
    @GetMapping("/{lectureId}/finalize/score")
    public List<EnrollingStudentsAndScoreResp> readScoreList(
		@PathVariable String lectureId
    ) {
    	return enderService.readStudentAndScoreList(lectureId);
    }
    
    // 성적확정 절차 2. 특정 학생의 평점을 수정하고 이유를 기록
    @PatchMapping("/{lectureId}/finalize/score")
    public ResponseEntity<Void> changeScore(
		@PathVariable String lectureId
		, @RequestBody ChangeScoreReq request
    ) {
    	enderService.modifyStudentGrade(lectureId, request);
    	
    	return ResponseEntity.noContent().build();
    }
    
    // 성적확정 절차 3.
    // 평점 수정이 없는 모든 학생의 autoScore를 finalScore로 이동하고
    // lecture 테이블의 SCORE_FINALIZE_YN 을 'Y'로 변경하여 완전 종강
    @PutMapping("/{lectureId}/finalize")
    public ResponseEntity<Void> finalizeLecture(
		@PathVariable String lectureId
    ) {
    	enderService.finalizeLecture(lectureId);
    	
    	return ResponseEntity.noContent().build();
    }
}
