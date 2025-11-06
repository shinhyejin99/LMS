package kr.or.jsu.classroom.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.response.lecture.LctGraderatioResp;
import kr.or.jsu.classroom.dto.response.lecture.LctWeekbyResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.ProfessorInfoResp;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클래스룸 API 중, <br>
 * 강의와 관련없는 모든 사용자가 접근할 수 있는 정보 
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      	수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.01     	송태호	          최초 생성
 *  2025.10.10     	송태호	          과제 관련 요청 처리
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/classroom/api/v1/common")
@RequiredArgsConstructor
public class ClassroomCommonRESTController {
	
	private final ClassroomCommonService classroomCommonService;
	
	private final LMSFilesService fileService;
	
	// 상대평가 평점 구간 요청
	@GetMapping("/subject/evaluate/interval")
	Map<String, Integer> relativeMap() {
		return Map.of("A", 30, "B", 30);
	}
	
	// 출석상태별 기본 점수 요청
	@GetMapping("/lecture/evaluate/score-by-attendance-status")
	Map<String, Integer> attScoreMap() {
		return Map.of("ATTD_OK", 100, "ATTD_NO", 0, "ATTD_EARLY", 50, "ATTD_LATE", 50, "ATTD_EXCP", 80);
	}
	
	
	// ========================================
	// 강의 관련
	// ========================================
	
	/**
	 * 강의 정보(강의+과목 기본정보, 수강생 수)를 출력합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	@GetMapping("/{lectureId}")
	LectureLabelResp printLecture(
		@PathVariable String lectureId
	) {
		// TODO 검색결과가 없으면 예외처리?
		LectureLabelResp result = classroomCommonService.readLecture(lectureId);
		return result;
	}
	
	/**
	 * 강의 담당 교수 정보를 출력합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	@GetMapping("/{lectureId}/professor")
	ProfessorInfoResp printProfessor(
		@PathVariable String lectureId
	) {
		return classroomCommonService.readProfessor(lectureId);
	}
		
	/**
	 * 강의 주차계획을 확인합니다. <br>
	 * 강의와 관련없는 모든 사용자가 접근할 수 있습니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 강의ID와 강의 정보, 주차계획이 들어있는 DTO
	 */
	@GetMapping("/{lectureId}/plan")
	List<LctWeekbyResp> printPlan(
		@PathVariable String lectureId
	) {
		// TODO 검색결과가 없으면 예외처리?
		return classroomCommonService.readLecturePlan(lectureId);
	}
	
	// 강의 시간표 요청
	@GetMapping("/{lectureId}/schedule")
	String printSchedule(
		@PathVariable String lectureId
	) {
		// TODO 검색결과가 없으면 예외처리?
		var result = classroomCommonService.readLectureSchedule(lectureId).getScheduleJson();
		
		if(result == null) result = "{\"schedule\" : \"시간표 정보가 없습니다.\"}";
		return result;
				
	}
	
	// 강의 성적산출비율 요청
	@GetMapping("/{lectureId}/ratio")
	List<LctGraderatioResp> printGraderatio(
		@PathVariable String lectureId
	) {
		// TODO 검색결과가 없으면 예외처리?
		return classroomCommonService.readLectureGraderatio(lectureId);
	}
	
	// ====================
	// 강의 관련 끝
	// ====================
	
	// ========================================
	// 파일 관련
	// ========================================
	
	/**
	 * 특정 교수의 증명사진을 요청합니다. <br>
	 * 교수의 증명사진은 공공재이므로 별도의 검증이 필요없습니다.
	 * 
	 * @param professorNo 대상 교수 교번
	 * @return img src=""에 해당 url을 입력하면 사진 출력
	 */
	@GetMapping("/photo/professor/{professorNo}")
	public ResponseEntity<Resource> showProfessorIdPhoto(
	    @PathVariable String professorNo
	) {
	    FileDetailInfo fileInfo =
	        classroomCommonService.getProfessorIdPhoto(professorNo);
	    
	    Resource file = fileInfo.getRealFile();
	
	    String ext = fileInfo.getExtension() == null ? "" : fileInfo.getExtension().toLowerCase();
	
	    MediaType mediaType = switch (ext) {
	        case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
	        case "png" -> MediaType.IMAGE_PNG;
	        case "gif" -> MediaType.IMAGE_GIF;
	        case "webp" -> MediaType.parseMediaType("image/webp");
	        case "bmp" -> MediaType.parseMediaType("image/bmp");
	        default -> MediaType.APPLICATION_OCTET_STREAM;
	    };
	
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(mediaType);
	
	    return new ResponseEntity<>(file, headers, HttpStatus.OK);
	}
		
	// 수강생의 증명사진 요청
	@GetMapping("/photo/student/{lectureId}/{studentNo}")
	public ResponseEntity<Resource> showStudentIdPhotoInLecture(
	    @PathVariable String lectureId
	    , @PathVariable String studentNo
	    , @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    FileDetailInfo fileInfo =
	        classroomCommonService.getStudentIdPhoto(lectureId, studentNo, loginUser.getRealUser());
	    
	    
//	    Resource file = null;
//	    try {
//	    	file = fileInfo.getRealFile();
//	    } catch (RuntimeException e) {
//	    	file = fileService.readFileBundle("FILE00000000416").getFileDetailInfo().get(0).getRealFile();
//	    }
	    Resource file = fileInfo.getRealFile();
	
	    String ext = fileInfo.getExtension() == null ? "" : fileInfo.getExtension().toLowerCase();
	
	    MediaType mediaType = switch (ext) {
	        case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
	        case "png" -> MediaType.IMAGE_PNG;
	        case "gif" -> MediaType.IMAGE_GIF;
	        case "webp" -> MediaType.parseMediaType("image/webp");
	        case "bmp" -> MediaType.parseMediaType("image/bmp");
	        default -> MediaType.APPLICATION_OCTET_STREAM;
	    };
	
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(mediaType);
	
	    return new ResponseEntity<>(file, headers, HttpStatus.OK);
	}
	
	private final Set<String> uploadType = Set.of("board", "qrcode", "task");
	
	// 강의에서 발생하는 첨부파일 업로드를 처리합니다.
	@PostMapping(value = "/{lectureId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadFiles(
        @PathVariable String lectureId,
        @RequestParam("type") String type,
        @RequestParam("files") List<MultipartFile> files,
        @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    // 파일 입력 체크
	    if(files == null || files.isEmpty()) {
	        return ResponseEntity.badRequest().body("업로드할 파일이 없습니다.");
	    }
	    if(files.size() > 5) {
	        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
	                .body("최대 5개까지 업로드할 수 있습니다.");
	    }
	    if(!uploadType.contains(type)) {
	    	return ResponseEntity.badRequest().body("파일 저장 용도가 불명확합니다.");
	    }

	    String uploaderUserId = loginUser.getRealUser().getUserId();
        log.info("파일 업로드 요청: lectureId={}, uploader={}", lectureId, uploaderUserId);
        
        log.info("업로드 요청 파일 수 : {}", files.size());
        
        files.stream().forEach(file -> log.info("{} 파일 크기 : {}", file.getName(), file.getSize()));
	    
	    try {
	        // 파일 서비스 호출 (FILES 1건 + FILE_DETAIL N건 등록)
	    	String subDir = "/" + type + "/" + lectureId;
	        List<FileDetailInfo> fileMetadata = fileService.saveAtDirectory(files, FileUploadDirectory.CLASSROOM, subDir);
	        String fileId = fileService.saveAtDB(fileMetadata, uploaderUserId, false);

	        log.info("업로드 완료: lectureId={}, FILE_ID={}", lectureId, fileId);

	        // 클라이언트로 FILE_ID 반환
	        return ResponseEntity.ok(Map.of("fileId", fileId));

	    } catch (Exception e) {
	        log.error("파일 업로드 실패", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}
	
	// ====================
	// 파일 다운로드 엔드포인트 (리팩토링 완료)
	// ====================

	/**
	 * 강의 게시글의 첨부파일 다운로드 요청을 처리합니다.
	 *
	 * @param lectureId 강의 ID
	 * @param lctPostId 게시글 ID
	 * @param fileOrder 첨부파일 순번
	 * @param loginUser 로그인된 사용자
	 */
	@GetMapping("/board/{lectureId}/post/{lctPostId}/attach/{fileOrder}")
	public ResponseEntity<Resource> downloadPostAttachedFile(
	    @PathVariable String lectureId,
	    @PathVariable String lctPostId,
	    @PathVariable int fileOrder,
	    @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    UsersVO realUser = loginUser.getRealUser();

	    FileDetailInfo fileInfo =
	        classroomCommonService.getPostAttachedFile(
	            lectureId,
	            lctPostId,
	            fileOrder,
	            realUser
	        );

	    return buildDownloadResponse(fileInfo);
	}

	/**
	 * 과제 첨부파일 다운로드 처리
	 *
	 * @param lectureId 강의ID
	 * @param type 과제 타입 (indiv/grouptask 등)
	 * @param taskId 과제 ID
	 * @param fileOrder 파일 순번
	 * @param loginUser 로그인 사용자
	 */
	@GetMapping("/task/{lectureId}/{type}/{taskId}/attach/{fileOrder}")
	public ResponseEntity<Resource> downloadTaskAttachedFile(
	    @PathVariable String lectureId,
	    @PathVariable String type,
	    @PathVariable String taskId,
	    @PathVariable int fileOrder,
	    @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    UsersVO realUser = loginUser.getRealUser();

	    FileDetailInfo fileInfo =
	        classroomCommonService.getTaskAttachedFile(
	            lectureId,
	            type,
	            taskId,
	            fileOrder,
	            realUser
	        );

	    return buildDownloadResponse(fileInfo);
	}

	/**
	 * 개인과제 제출의 첨부파일 다운로드 처리
	 *
	 * @param lectureId 강의ID
	 * @param indivtaskId 과제ID
	 * @param studentNo 제출 학생 번호
	 * @param fileOrder 파일순서
	 * @param loginUser 로그인 사용자
	 */
	@GetMapping("/{lectureId}/indivtask/{indivtaskId}/submit/{studentNo}/attach/{fileOrder}")
	public ResponseEntity<Resource> downloadIndivtaskSubmitAttachedFile(
	    @PathVariable String lectureId,
	    @PathVariable String indivtaskId,
	    @PathVariable String studentNo,
	    @PathVariable int fileOrder,
	    @AuthenticationPrincipal CustomUserDetails loginUser
	) {
	    UsersVO realUser = loginUser.getRealUser();

	    FileDetailInfo fileInfo =
	        classroomCommonService.getIndivtaskSubmitAttachedFile(
	            lectureId,
	            indivtaskId,
	            studentNo,
	            fileOrder,
	            realUser
	        );

	    return buildDownloadResponse(fileInfo);
	}

	// ====================
	// 공통 ResponseEntity 빌더
	// ====================

	private ResponseEntity<Resource> buildDownloadResponse(FileDetailInfo fileInfo) {
	    Resource file = fileInfo.getRealFile();

	    // 원본 파일명 구성 (확장자 없으면 이름만)
	    String baseName = safe(fileInfo.getOriginName());
	    String ext = safe(fileInfo.getExtension());
	    String originalFileName = (ext.isBlank()) ? baseName : baseName + "." + ext;

	    HttpHeaders headers = new HttpHeaders();
	    try {
	        // 공백 -> %20 (플러스 치환 방지)
	        String encoded = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
	        // RFC 5987 확장: filename*
	        String contentDisposition = "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded;
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
	    } catch (UnsupportedEncodingException e) {
	        log.error("파일명 인코딩 실패", e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일명 인코딩 실패");
	    }

	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

	    // 가능한 경우 Content-Length 설정
	    try {
	        long len = file.contentLength();
	        if (len >= 0) headers.setContentLength(len);
	    } catch (IOException ignore) {
	        // 리소스 길이를 알 수 없으면 생략
	    }

	    return new ResponseEntity<>(file, headers, HttpStatus.OK);
	}

	// null-safe helper
	private static String safe(String s) {
	    return (s == null) ? "" : s;
	}

} 