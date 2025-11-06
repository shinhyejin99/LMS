package kr.or.jsu.classroom.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.request.task.TaskSubmitReq;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyEnrollInfoResp;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyInfoResp;
import kr.or.jsu.classroom.dto.response.ender.ScoreAndGPAResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_STU;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.classroom.dto.response.review.LectureReviewQuestionResp;
import kr.or.jsu.classroom.dto.response.student.ClassmateResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.NotSubmittedIndivtaskResp;
import kr.or.jsu.classroom.student.service.ClassStuBoardService;
import kr.or.jsu.classroom.student.service.ClassStuEnderService;
import kr.or.jsu.classroom.student.service.ClassStuExamService;
import kr.or.jsu.classroom.student.service.ClassStuTaskService;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.vo.UsersVO;
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
@RequestMapping("/classroom/api/v1/student")
@RequiredArgsConstructor
public class ClassroomStudentRESTController {
	
	private final ClassroomStudentService studentRootService;
	private final ClassStuBoardService boardService;
	private final ClassStuTaskService taskService;
	private final ClassStuExamService examService;
	private final ClassStuEnderService enderService;
	
	// TODO 인가처리, 학생만 사용 가능하도록
	
	// ========================================
	// 레이아웃 + 메인화면 관련
	// ========================================
	
	// 내 정보 요청(학생 기본정보)(헤더에 띄울)
	@GetMapping("/me")
	public StudentMyInfoResp showMe(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return studentRootService.getMyInfo(loginUser.getRealUser());
	}
	
	// 내 시간표 요쳥(학년도학기별)
	@GetMapping("/schedule")
	public List<LectureScheduleResp> showMySchedule(
		@AuthenticationPrincipal CustomUserDetails loginUser
		, @RequestParam(required = false) String yeartermCd
	) {
		return studentRootService.readMySchedule(loginUser.getRealUser(), yeartermCd);
	}
	
	// 제출해야 할 개인과제 요청
	@GetMapping("/me/indivtask")
	public List<NotSubmittedIndivtaskResp> notSubmittedIndivtask(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return taskService.readNotSubmittedTaskList(loginUser.getRealUser());
	}
	
	// 제출해야 할 조별과제 목록 요청
	
	// 강의평가 해야 할 강의 요청
	@GetMapping("/review")
	public List<String> showReviewNeededLecture(
		@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return enderService.reviewNeededLectures(loginUser.getRealUser());
	}
	
	// ========================================
	// 강의 관련
	// ========================================
	
	// 특정 강의에서의 내 정보 요청(기본정보 + 수강정보)
	@GetMapping("/{lectureId}/me")
	public StudentMyEnrollInfoResp showMyEnrollment(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return studentRootService.getMyInfoAndEnrollment(loginUser.getRealUser(), lectureId);
	}
	
	@GetMapping("/mylist")
	public List<LectureLabelResp> myLectureList(
		@RequestParam(required = false) String ongoing
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		UsersVO user = loginUser.getRealUser();
		List<LectureLabelResp> result = studentRootService.readMyLectureList(user);
		
		return result;
	}
	
	// ====================
	// 강의 관련 끝
	// ====================
	
	// ========================================
	// 수강생 관련
	// ========================================
	
	// 특정 강의의 동료 수강생 목록 요청
	@GetMapping("/{lectureId}/students")
	public List<ClassmateResp> showClassmates(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return studentRootService.readClassmates(loginUser.getRealUser(), lectureId);
	}
	
	// ====================
	// 수강생 관련 끝
	// ====================

	// ========================================
	// 게시판 관련
	// ========================================
	
	/**
	 * 학생의 강의 게시글 목록 요청을 처리합니다.
	 * 
	 * @param lectureId 강의 ID
	 * @param postType 게시글 타입(공지, 자료), null일 시 모든 타입
	 * @param loginUser 로그인된 사용자 정보
	 * @return 강의 게시글 목록에 필요한 정보 리스트(게시글번호, 제목, 작성일, 타입)
	 */
	@GetMapping("/board/{lectureId}/post")
	public List<LctPostLabelResp_STU> readPostList(
		@PathVariable String lectureId
		, @RequestParam(required = false) String postType
		, @AuthenticationPrincipal CustomUserDetails loginUser 
	) {
		return boardService.readPostList(loginUser.getRealUser(), lectureId, postType);
	}
	
	/**
	 * 학생의 강의 게시글 상세조회 요청을 처리합니다.
	 * 
	 * @param lectureId 강의 ID
	 * @param lctPostId 게시글 ID
	 * @param loginUser 로그인된 사용자
	 * @return 
	 */
	@GetMapping("/board/{lectureId}/post/{lctPostId}")
	public LctPostDetailResp_STU readPost(
		@PathVariable String lectureId
		, @PathVariable String lctPostId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return boardService.readPost(loginUser.getRealUser(), lctPostId);
	}
	
	/**
	 * 댓글 작성 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID
	 * @param lctPostId 경로변수 : 대상 게시글ID
	 * @param newReply 댓글내용, (대댓글일 경우) 부모댓글ID
	 * @param loginUser 로그인된 사용자
	 */
	@PostMapping("/board/{lectureId}/post/{lctPostId}/reply")
	public ResponseEntity<Void> createReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId 
		, @RequestBody LctPostReplyReq newReply
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		log.info("댓글 작성 요청 발생, 대상 강의 : {}, 게시글 : {}", lectureId, lctPostId);
		log.info("댓글 내용 : {}", PrettyPrint.pretty(newReply));
		
		boardService.createReply(loginUser.getRealUser(), newReply, lectureId, lctPostId);
		
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * 댓글 목록 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID
	 * @param lctPostId 경로변수 : 대상 게시글ID
	 * @param loginUser 로그인된 사용자
	 * @return 
	 */
	@GetMapping("/board/{lectureId}/post/{lctPostId}/reply")
	public List<LctPostReplyResp> readReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return boardService.readReplyList(loginUser.getRealUser(), lectureId, lctPostId);
	}
	
	/**
	 * 댓글 삭제 요청
	 * 
	 * @param lectureId 경로변수 : 대상 강의ID(사용안함)
	 * @param lctPostId 경로변수 : 대상 게시글ID(사용안함)
	 * @param lctReplyId 경로변수 : 대상 댓글ID
	 * @return
	 */
	@DeleteMapping("/board/{lectureId}/post/{lctPostId}/reply/{lctReplyId}")
	public ResponseEntity<Void> deleteReply(
		@PathVariable String lectureId
		, @PathVariable String lctPostId 
		, @PathVariable String lctReplyId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		boardService.deleteReply(loginUser.getRealUser(), lctReplyId);
		
		return ResponseEntity.noContent().build();
	}
	
	// ====================
	// 게시판 관련 끝
	// ====================
	
	// ========================================
	// 출석 관련
	// ========================================
	
	/**
	 * 강의에서 이루어진 모든 출석회차 기록과, <br>
	 * 그 출석회차들에 대한 자신의 출석 기록을 가져옵니다. 
	 * 
	 * @param lectureId 강의ID
	 * @param loginUser 로그인된 사용자 정보
	 */
	@GetMapping("/{lectureId}/attendance")
	public List<StudentsAllAttendanceResp> showMyAttendanceRecord(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
		return studentRootService.readMyAttendacneList(loginUser.getRealUser(), lectureId);
	}
	
	// ====================
	// 출석 관련 끝
	// ====================
	
	// ========================================
	// 과제 관련
	// ========================================
	
	// 개인과제 목록 조회(제출여부 포함)
    @GetMapping("/{lectureId}/task/indiv")
	public List<IndivtaskLabelResp_STU> readIndivtaskList(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
	) {
    	return taskService.readIndivtaskList(loginUser.getRealUser(), lectureId);
    }
	
	// 개인과제 상세 보기 (내용, 내가 제출한거, 내 평가)
    @GetMapping("/{lectureId}/task/indiv/{indivtaskId}")
    public LctIndivtaskDetailResp readIndivtask(
    	@PathVariable String lectureId
    	, @PathVariable String indivtaskId
    	, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	return taskService.readIndivtask(loginUser.getRealUser(), lectureId, indivtaskId);
    }
	
	// 개인 과제 제출
    @PatchMapping("/{lectureId}/task/indiv/{indivtaskId}")
    public ResponseEntity<Void> submitIndivtask(
    	@PathVariable String lectureId
    	, @PathVariable String indivtaskId
    	, @RequestBody TaskSubmitReq request
    	, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	taskService.submitIndivtask(loginUser.getRealUser(), lectureId, indivtaskId, request);
    	return ResponseEntity.noContent().build();
    }
	
    // 조별과제 목록 조회(내 조의 제출여부 포함)
    @GetMapping("/{lectureId}/task/group")
    public List<GrouptaskLabelResp_STU> readGrouptaskList(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	return taskService.readGrouptaskList(loginUser.getRealUser(), lectureId);
    }
    
    // 조별과제 상세 보기 (과제내용, 내 조의 조장과 구성원들, 내 조의 제출, 내 평가)
    @GetMapping("/{lectureId}/task/group/{grouptaskId}")
    public LctGrouptaskDetailResp readGrouptask(
		@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    	, @AuthenticationPrincipal CustomUserDetails loginUser	
    ) {
    	return taskService.readGrouptask(loginUser.getRealUser(), lectureId, grouptaskId);
    }
    
    @GetMapping("/{lectureId}/task/group/{grouptaskId}/jo")
    public GrouptaskJoResp readGrouptaskJo(
		@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    	, @AuthenticationPrincipal CustomUserDetails loginUser	
    ) {
    	return taskService.readGrouptaskJo(loginUser.getRealUser(), lectureId, grouptaskId);
    }
	
	// 조별 과제 제출(팀장인 경우)
    @PatchMapping("/{lectureId}/task/group/{grouptaskId}")
    public ResponseEntity<Void> submitGrouptask(
    	@PathVariable String lectureId
    	, @PathVariable String grouptaskId
    	, @RequestBody TaskSubmitReq request
    	, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	taskService.submitGrouptask(loginUser.getRealUser(), lectureId, grouptaskId, request);
    	return ResponseEntity.noContent().build();
    }
    
	// ====================
	// 과제 관련 끝
	// ====================
	
	// ========================================
	// 시험 관련
	// ========================================
	
    // 모든 시험 목록 + 응시 여부 조회
    @GetMapping("/{lectureId}/exam")
    public List<LctExamLabelResp_STU> readExamList(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	return examService.readExamList(loginUser.getRealUser(), lectureId);
    }
    
    // 시험 내용 + 응시 결과 조회
    @GetMapping("/{lectureId}/exam/{lctExamId}")
    public ExamAndEachStudentsSubmitResp readExamDetail(
		@PathVariable String lectureId
		, @PathVariable String lctExamId
		, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	return examService.readExam(loginUser.getRealUser(), lectureId, lctExamId);
    }
    
	// ====================
	// 시험 관련 끝
	// ====================
    
    // ========================================
 	// 강의평가 관련
 	// ========================================
    
    // 강의평가 할 문제와 문제유형
    @GetMapping("/{lectureId}/review/question")
	public List<LectureReviewQuestionResp> readReviewQuestionList(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser	
	) {
		return enderService.readReviewQuestionList(loginUser.getRealUser(), lectureId);
	}
    
    // 강의평가 결과 JSON 전송
    @PostMapping("/{lectureId}/review")
    public ResponseEntity<?> createReview(
		@PathVariable String lectureId
		, @RequestBody String reviewJson
		, @AuthenticationPrincipal CustomUserDetails loginUser	
	) {
    	log.info("강평 JSON : {}", reviewJson);
    	
    	enderService.createReview(loginUser.getRealUser(), lectureId, reviewJson);
    	
    	// Location 헤더 (시험결과 상세 페이지 URI - 나중에 수정)
	    URI location = URI.create(String.format(
	        "/classroom/student/%s/result", lectureId
	    ));
    	
    	return ResponseEntity.created(location) // 201 Created
    						 .body(Map.of("success", "강의평가 등록 성공"));
    }
    
	// ====================
	// 강의평가 관련 끝
	// ====================
    
    // ========================================
 	// 성적조회/이의제기 관련
 	// ========================================
 	
    @GetMapping("/{lectureId}/score")
    public ScoreAndGPAResp myLectureScore(
		@PathVariable String lectureId
		, @AuthenticationPrincipal CustomUserDetails loginUser
    ) {
    	return enderService.readLectureScore(loginUser.getRealUser(), lectureId);
    }
 	
 	 
}