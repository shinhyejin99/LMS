package kr.or.jsu.classroom.common.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.dto.db.LectureSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithWeekbyDTO;
import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctGraderatioInfo;
import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import kr.or.jsu.classroom.dto.info.LctPostInfo;
import kr.or.jsu.classroom.dto.info.LctWeekbyInfo;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.classroom.dto.response.lecture.LctGraderatioResp;
import kr.or.jsu.classroom.dto.response.lecture.LctWeekbyResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.ProfessorInfoResp;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.info.ProfessorInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.LctGrouptaskMapper;
import kr.or.jsu.mybatis.mapper.LctIndivtaskMapper;
import kr.or.jsu.mybatis.mapper.LctPostMapper;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import kr.or.jsu.mybatis.mapper.StuEnrollLctMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomCommonServiceImpl implements ClassroomCommonService {

	private final LectureMapper lectureMapper;
	private final LctPostMapper lctPostMapper;
	private final StuEnrollLctMapper enrollMapper;
	private final StudentMapper studentMapper;
	private final ProfessorMapper professorMapper;
	private final LctIndivtaskMapper indivtaskMapper;
	private final LctGrouptaskMapper grouptaskMapper;

	private final LMSFilesService filesService;
	private final DatabaseCache cache;

	private CustomUserDetails getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null && auth.isAuthenticated()) {
			Object principal = auth.getPrincipal();

			if (principal instanceof CustomUserDetails) {
				CustomUserDetails user = (CustomUserDetails) principal;
				return user;
			}
		}

		throw new RuntimeException("사용자 정보를 가져올 수 없습니다.");
	}

	@Override
	public LectureLabelResp readLecture(String lectureId) {
		LectureSimpleDTO lectureInfo = lectureMapper.selectLecture(lectureId);
		if(lectureInfo == null) throw new RuntimeException("존재하지 않는 강의");

		LectureLabelResp resp = new LectureLabelResp();

		LectureInfo li = lectureInfo.getLectureInfo();
	    SubjectInfo si = lectureInfo.getSubjectInfo();

	    resp.setLectureId(li.getLectureId());
	    resp.setSubjectName(si.getSubjectName());
	    resp.setProfessorNo(li.getProfessorNo());
	    resp.setProfessorName(cache.getUserName(li.getProfessorNo()));
	    resp.setUnivDeptName(cache.getUnivDeptName(si.getUnivDeptCd()));
	    resp.setCompletionName(cache.getCodeName(si.getCompletionCd()));
	    resp.setYeartermCd(li.getYeartermCd());
	    resp.setSubjectTypeCd(si.getSubjectTypeCd());
	    resp.setSubjectTypeName(cache.getCodeName(si.getSubjectTypeCd()));
	    resp.setCredit(si.getCredit());
	    resp.setHour(si.getHour());
	    resp.setCurrentCap(lectureInfo.getCurrentCap());
	    resp.setMaxCap(li.getMaxCap());
	    resp.setFinalized("Y".equals(li.getScoreFinalizeYn()));

	    if(cache.getCurrentYearterm().equals(resp.getYeartermCd()))
	    	resp.setStarted(true);

	    if(lectureInfo.getLectureInfo().getEndAt() != null) {
	    	resp.setStarted(true);
	    	resp.setEnded(true);
	    	resp.setEndAt(lectureInfo.getLectureInfo().getEndAt());
	    }

	    try {
	    	resp.setScheduleJson(readLectureSchedule(lectureId).getScheduleJson());
	    } catch (IndexOutOfBoundsException e) {

	    }


	    return resp;
	}

	@Override
	public ProfessorInfoResp readProfessor(String lectureId) {
		LectureSimpleDTO lecture = lectureMapper.selectLecture(lectureId);
		String prfNo = lecture.getLectureInfo().getProfessorNo();
		ProfessorInfo prf = professorMapper.selectBasicProfessorInfo(prfNo);

		ProfessorInfoResp resp = new ProfessorInfoResp();
		BeanUtils.copyProperties(prf, resp);

		resp.setUnivDeptName(cache.getUnivDeptName(prf.getUnivDeptCd()));
		resp.setPrfStatusName(cache.getCodeName(prf.getPrfStatusCd()));
		resp.setPrfAppntName(cache.getCodeName(prf.getPrfAppntCd()));
		resp.setPrfPositName(cache.getCodeName(prf.getPrfPositCd()));

		return resp;
	}

	@Override
	public List<LctWeekbyResp> readLecturePlan(String lectureId) {
		LectureWithWeekbyDTO result = lectureMapper.selectLectureWithWeekby(lectureId);
		if(result == null) return new ArrayList<>();

		List<LctWeekbyInfo> list = result.getWeekbyList();

		return list.stream().map(week -> {
			LctWeekbyResp resp = new LctWeekbyResp();
			BeanUtils.copyProperties(week, resp);
			return resp;
		}).collect(Collectors.toList());
	}

	@Override
	public LectureWithScheduleDTO readLectureSchedule(String lectureId) {
		List<LectureWithScheduleDTO> schedule = 
			lectureMapper.selectScheduleListJson(List.of(lectureId));
		
		// TODO 시간표 정보가 없는거니까 적절히 예외처리
		if(schedule.size() == 0) return new LectureWithScheduleDTO();
		
		return schedule.get(0);
	}

	/**
	 * 강의의 성적산출비율을 확인합니다.
	 *
	 * @param lectureId 강의ID
	 * @return
	 */
	@Override
	public List<LctGraderatioResp> readLectureGraderatio(
		String lectureId
	) {
		List<LctGraderatioInfo> resultList = lectureMapper.selectGraderatioList(lectureId);

		return resultList.stream().map(result -> {
			LctGraderatioResp resp = new LctGraderatioResp();

			BeanUtils.copyProperties(result, resp);
			resp.setGradeCriteriaName(cache.getCodeName(result.getGradeCriteriaCd()));

			return resp;
		}).toList();
	};

	@Override
	public boolean isRelevantClassroom(String userNo, String lectureId) {
		switch(userNo.length()) {
		case 8 : return isPrfOwnLecture(lectureId, userNo);
		case 9 : return getEnrollInfo(lectureId, userNo) != null;
		default : throw new RuntimeException();
		}
	}

	/**
	 * 교수가 강의를 담당했는지 확인합니다.
	 *
	 * @param lectureId 강의ID
	 * @param prfNo 교번
	 * @return lectureId 강의 담당교수가 prfNo 교수인가?
	 */
	private boolean isPrfOwnLecture(
		String lectureId
		, String prfNo
	) {
		// TODO 예외처리
		LectureSimpleDTO targetLecture = lectureMapper.selectLecture(lectureId);
		if(targetLecture == null)
			throw new RuntimeException("존재하지 않는 강의입니다");
		String teacherNo = targetLecture.getLectureInfo().getProfessorNo();
		return prfNo.equals(teacherNo);
	}

	/**
	 * 강의ID와 학번으로 수강 기록을 반환합니다.
	 *
	 * @param lectureId 강의ID
	 * @param stuNo 학번
	 * @return null일 경우 강의ID가 없거나, 학번이 없거나, 학생이 강의를 수강하지 않은것임.
	 */
	public StuEnrollLctInfo getEnrollInfo(
		String lectureId
		, String stuNo
	) {
		// TODO 예외처리
		return enrollMapper.selectEnrollRecord(lectureId, stuNo);
	}

	@Override
	public FileDetailInfo getPostAttachedFile(
		String lectureId
		, String lctPostId
		, int fileOrder
		, UsersVO realUser
	) {
		// TODO 예외처리

		// 1. 관련있는 사람인지 부터 확인
		boolean relevant = isRelevantClassroom(realUser.getUserNo(), lectureId);
		if(!relevant) throw new RuntimeException("파일 요청 권한이 없습니다.");

		// 2. 강의 존재 확인
		LectureSimpleDTO lct = lectureMapper.selectLecture(lectureId);
		if(lct == null) throw new RuntimeException("존재하지 않는 강의입니다.");

		// 3. 게시글 존재 확인
		LctPostInfo lctPost = lctPostMapper.selectPost(lctPostId);
		if(lctPost == null) throw new RuntimeException("존재하지 않는 게시글입니다.");

		// 4. 삭제 여부 확인
		if("Y".equals(lctPost.getDeleteYn())) throw new RuntimeException("이미 삭제된 게시글입니다.");

		// 5. 학생이면 비공개 게시글에는 접근 불가능
		String userType = realUser.getUserType();
		if(
			"ROLE_STUDENT".equals(userType) &&
			"Y".equals(lctPost.getTempSaveYn())
		) throw new RuntimeException("존재하지 않는 게시글입니다.");

		// 6. 파일 존재 확인
		String fileId = lctPost.getAttachFileId();
		if(fileId == null) throw new RuntimeException("첨부파일이 없는 게시글입니다.");

		// 7. 파일 가져오기. 없는 파일이면 오류 던져짐
		FilesBundleDTO fileMetadata = filesService.readFileBundle(fileId);

		// 8. 묶음에 속한 파일들 돌며 요구한 순번이 있는지 확인
		List<FileDetailInfo> fileDetailList = fileMetadata.getFileDetailInfo();

		FileDetailInfo targetFile =
			fileDetailList.stream().filter(f -> f.getFileOrder() == fileOrder)
								   .findFirst()
								   .orElse(null);
		if(targetFile == null) throw new RuntimeException("존재하지 않는 파일입니다.");

		return targetFile;
	}

	@Override
	public FileDetailInfo getTaskAttachedFile(
		String lectureId
		, String type
		, String taskId
		, int fileOrder
		, UsersVO realUser
	) {
		// TODO 예외처리

		// 1. 관련있는 사람인지 부터 확인
		boolean relevant = isRelevantClassroom(realUser.getUserNo(), lectureId);
		if(!relevant) throw new RuntimeException("파일 요청 권한이 없습니다.");

		// 2. 강의 존재 확인
		LectureSimpleDTO lct = lectureMapper.selectLecture(lectureId);
		if(lct == null) throw new RuntimeException("존재하지 않는 강의입니다.");

		// 3. 과제글 존재 확인
		String fileId = null;
		switch (type.toLowerCase()) {
		case "indiv": {
			LctIndivtaskInfo task = indivtaskMapper.selectIndivtask(taskId);
			if(task == null) throw new RuntimeException("존재하지 않는 과제입니다.");
			if("Y".equals(task.getDeleteYn())) throw new RuntimeException("이미 삭제된 게시글입니다.");
			fileId = task.getAttachFileId();
			break;
		}
		case "group": {
			LctGrouptaskInfo task = grouptaskMapper.selectGrouptask(taskId);
			if(task == null) throw new RuntimeException("존재하지 않는 과제입니다.");
			if("Y".equals(task.getDeleteYn())) throw new RuntimeException("이미 삭제된 게시글입니다.");
			fileId = task.getAttachFileId();
			break;
		}
		default:
			throw new RuntimeException("과제 분류 코드는 \"indiv\", \"group\" 중 하나여야 합니다.");
		}

		if(fileId == null) throw new RuntimeException("첨부파일이 없는 과제입니다.");
		FilesBundleDTO fileMetadata = filesService.readFileBundle(fileId);
		List<FileDetailInfo> fileDetailList = fileMetadata.getFileDetailInfo();
		FileDetailInfo targetFile =
				fileDetailList.stream().filter(f -> f.getFileOrder() == fileOrder)
				.findFirst()
				.orElse(null);
		if(targetFile == null) throw new RuntimeException("존재하지 않는 파일입니다.");

		return targetFile;
	}

	/**
	 * 요청한 사용자(교수, 학생)가 <br>
	 * 개인과제 제출에 첨부한 파일에 대한 권한이 있는지 확인하고 <br>
	 * 파일 메타데이터를 반환합니다. <br><br>
	 * 유효성 검사 목록 <br>
	 * 1. 사용자가 강의와 관계있는가? <br>
	 * 2. 요청한 과제가 강의 소속인가? <br>
	 * 3-1. (사용자가 교수일 경우) 학번에 해당하는 학생이 수강중인가? <br>
	 * 3-2. 그 학생이 해당 과제를 제출했는가? <br>
	 * 3-3. 학생이 파일을 첨부했는가? <br>
	 * 3-4. 학생의 파일이 삭제되었는가? <br>
	 * 3-4. 학생의 파일묶음에 해당하는 순번이 있는가? <br>
	 * 4-1. (사용자가 학생일 경우) 학번에 해당하는 학생이 나 자신인가? <br>
	 * 4-2. 내가 해당 과제를 제출했는가? <br>
	 * 4-3. 내가 파일을 첨부했는가? <br>
	 * 4-4. 내가 올린 파일이 삭제되었는가? <br>
	 * 4-5. 내가 올린 파일묶음에 요청한 순번이 있는가?
	 *
	 * @param lectureId 강의ID
	 * @param indivtaskId 과제ID
	 * @param studentNo 제출한 학생 학번
	 * @param fileOrder 요청한 파일 순번
	 * @param realUser 로그인한 사용자 정보
	 * @return 파일 메타데이터
	 */
	@Override
	public FileDetailInfo getIndivtaskSubmitAttachedFile(
		String lectureId
		, String indivtaskId
		, String studentNo
		, int fileOrder
		, UsersVO realUser
	) {
		// 1. 사용자가 강의와 관계있는가?
		boolean relevant = isRelevantClassroom(realUser.getUserNo(), lectureId);
		if(!relevant) throw new RuntimeException("파일 요청 권한이 없습니다.");

		LectureSimpleDTO lct = lectureMapper.selectLecture(lectureId);
		if(lct == null) throw new RuntimeException("존재하지 않는 강의입니다.");

		// 2. 요청한 과제가 강의 소속인가?
		LctIndivtaskInfo task = indivtaskMapper.selectIndivtask(indivtaskId);
		if(task == null || "Y".equals(task.getDeleteYn()))
			throw new RuntimeException("삭제되었거나 존재하지 않는 과제입니다.");
		if(!task.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");

		String role = realUser.getUserType();

		switch (role) {
		case "ROLE_PROFESSOR":
			// 3-1. (사용자가 교수일 경우) 학번에 해당하는 학생이 수강중인가? <br>
			StuEnrollLctInfo enroll = enrollMapper.selectEnrollRecord(lectureId, studentNo);
			if(enroll == null) throw new RuntimeException("이 강의 수강생이 아닙니다.");

			// 3-2. 그 학생이 해당 과제를 제출했는가?
			List<IndivtaskSubmitInfo> submitRecord =
				indivtaskMapper.selectIndivtaskSubmit(indivtaskId, enroll.getEnrollId());
			if(submitRecord.size() == 0) throw new RuntimeException("해당 학생은 아직 제출하지 않았습니다.");

			// 3-3. 학생이 파일을 첨부했는가?
			String attachFileId = submitRecord.get(0).getSubmitFileId();
			if(attachFileId == null) throw new RuntimeException("해당 학생이 첨부한 파일이 없습니다.");

			// 3-4. 학생의 파일이 삭제되었는가?
			FilesBundleDTO fileBundle = filesService.readFileBundle(attachFileId);
			if("N".equals(fileBundle.getFilesInfo().getUsingYn()))
				throw new RuntimeException("삭제된 파일입니다.");

			// 3-5. 학생의 파일묶음에 해당하는 순번이 있는가?
			Map<Integer, FileDetailInfo> detailMap =
				fileBundle.getFileDetailInfo().stream()
				    .collect(Collectors.toMap(
				        FileDetailInfo::getFileOrder
				        , Function.identity()
				    ));

			if(!detailMap.containsKey(fileOrder))
				throw new RuntimeException("해당 파일 순번으로 첨부된 파일이 없습니다..");
			return detailMap.get(fileOrder);
		case "ROLE_STUDENT":
			// 4-1. (사용자가 학생일 경우) 학번에 해당하는 학생이 나 자신인가?
			if(!realUser.getUserNo().equals(studentNo))
				throw new RuntimeException("본인 과제 제출만 조회 가능합니다.");
			// 4-2. 내가 이 강의 수강생인가?
			StuEnrollLctInfo myEnroll = enrollMapper.selectEnrollRecord(lectureId, studentNo);
			if(myEnroll == null) throw new RuntimeException("이 강의 수강생이 아닙니다.");

			// 4-3. 내가 해당 과제를 제출했는가?
			List<IndivtaskSubmitInfo> mySubmitRecord = indivtaskMapper.selectIndivtaskSubmit(indivtaskId, myEnroll.getEnrollId());
			if(mySubmitRecord.size() == 0) throw new RuntimeException("아직 과제 결과를 제출하지 않았습니다.");

			// 4-4. 내가 파일을 첨부했는가? <br>
			String myAttachFileId = mySubmitRecord.get(0).getSubmitFileId();
			if(myAttachFileId == null) throw new RuntimeException("파일을 첨부하지 않았습니다.");

			// 4-5. 내가 올린 파일이 삭제되었는가? <br>
			FilesBundleDTO myFileBundle = filesService.readFileBundle(myAttachFileId);
			if("N".equals(myFileBundle.getFilesInfo().getUsingYn()))
				throw new RuntimeException("삭제된 파일입니다.");

			// 4-6. 내가 올린 파일묶음에 요청한 순번이 있는가?
			Map<Integer, FileDetailInfo> myDetailMap =
					myFileBundle.getFileDetailInfo().stream()
					    .collect(Collectors.toMap(
					        FileDetailInfo::getFileOrder
					        , Function.identity()
					    ));

			if(!myDetailMap.containsKey(fileOrder))
				throw new RuntimeException("해당 파일 순번으로 첨부된 파일이 없습니다..");
			return myDetailMap.get(fileOrder);
		default:
			throw new IllegalArgumentException("요청 자격이 없는 사용자입니다");
		}
	}

	@Override
	public FileDetailInfo getStudentIdPhoto(
		String lectureId
		, String targetStudentNo
		, UsersVO realUser
	) {
		// TODO 예외처리

		// 1. 강의와 관련있는지 확인
		String userNo = realUser.getUserNo();
		// TODO 파라미터에서 리얼유저 분리...
		String userNo2 = getUser().getRealUser().getUserNo();
		log.info("유저는 컨트롤러에서 받지 마라... {} {}", userNo, userNo2);

		boolean relevant = isRelevantClassroom(userNo, lectureId);

		if("ROLE_PROFESSOR".equals(realUser.getUserType())) {
			if(!relevant) throw new RuntimeException("담당한 수강생의 사진만 조회 가능합니다.");
		} else {
			if(!relevant) throw new RuntimeException("해당 강의의 수강생이 아닙니다.");
		}

		// 2. 같은 강의 수강하는 수강생인지 확인
		StuEnrollLctInfo result = enrollMapper.selectEnrollRecord(lectureId, targetStudentNo);
		if(result == null) throw new RuntimeException("해당 강의의 수강생이 아닙니다.");

		String fileId = studentMapper.selectBasicStudentInfo(targetStudentNo).getUserInfo().getPhotoId();

		// 증명사진은 Detail에 하나밖에 없으므로.
		return filesService.readFileBundle(fileId).getFileDetailInfo().get(0);
	}

	/**
	 * 교번으로 교수의 사진 파일을 요청하면 <br>
	 * 파일 메타데이터와 실제 파일을 반환합니다.
	 *
	 * @param professorNo 교번
	 * @return 파일 메타데이터
	 */
	public FileDetailInfo getProfessorIdPhoto(
		String professorNo
	) {
		String fileId = professorMapper.selectBasicProfessorInfo(professorNo).getPhotoId();
//		if(fileId == null) throw new RuntimeException("해당 교수의 증명사진이 등록되지 않았습니다.");
		if(fileId == null) return new FileDetailInfo();
		
		// 증명사진은 Detail에 하나밖에 없으므로.
		return filesService.readFileBundle(fileId).getFileDetailInfo().get(0);
	}
}
