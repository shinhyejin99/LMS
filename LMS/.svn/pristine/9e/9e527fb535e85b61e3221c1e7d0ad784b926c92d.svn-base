package kr.or.jsu.classroom.student.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.db.GrouptaskGroupWithCrewDTO;
import kr.or.jsu.classroom.dto.info.GrouptaskCrewInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.request.task.TaskSubmitReq;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_STU;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp.IndivtaskSubmitResp;
import kr.or.jsu.classroom.dto.response.task.NotSubmittedIndivtaskResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp.CrewResp;
import kr.or.jsu.classroom.student.service.ClassStuTaskService;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.mybatis.mapper.LctGrouptaskMapper;
import kr.or.jsu.mybatis.mapper.LctIndivtaskMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassStuTaskServiceImpl implements ClassStuTaskService {
	
	private final LctIndivtaskMapper indivtaskMapper;
	private final LctGrouptaskMapper grouptaskMapper;
	
	private final LMSFilesService filesService;
	private final ClassroomCommonService commonService;
	private final ClassroomStudentService classRootService;
	
	/**
	 * 특정 강의에 대한 개인과제 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다. <br>
	 * 해당 과제에 대해 자신의 제출 기록이 있는지 확인하여 추가 기록합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	@Override
	public List<IndivtaskLabelResp_STU> readIndivtaskList(
		UsersVO user
		, String lectureId
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 내가 제출해야할 과제+제출 여부 목록 가져오기.
		List<LctIndivtaskInfo> searchList
			= indivtaskMapper.selectIndivtaskList_STU(lectureId, enroll.getEnrollId());
		
		// 3. 응답용 객체에 넣어서 반환
		return searchList.stream().map(info -> {
			IndivtaskLabelResp_STU resp = new IndivtaskLabelResp_STU();
			BeanUtils.copyProperties(info, resp);
			
			resp.setHasAttachFile((info.getAttachFileId() != null));
			resp.setSubmitted(info.getIsSubmitted() == 1);
			
			return resp;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 특정 강의의 특정 개인과제 상세조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 정보를 반환합니다. <br>
	 * (교수가 출제한 과제에 대한 내용 + 자신의 제출 기록)
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	public LctIndivtaskDetailResp readIndivtask(
		UsersVO user
		, String lectureId
		, String indivtaskId
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 요구 조건대로 개인과제 검색 후 요청과 대조
		LctIndivtaskInfo search = indivtaskMapper.selectIndivtask(indivtaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 개인과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		// 3. 응답으로 내보낼 객체 생성, 과제에 대한 정보 입력
		LctIndivtaskDetailResp result = new LctIndivtaskDetailResp();
		BeanUtils.copyProperties(search, result);
		
		// 4. 과제에 파일 첨부되었으면 파일 정보 넣기
		String fileId = search.getAttachFileId();
		if(fileId != null) {
			List<FileDetailResp> fileList = filesService.readFileDetailList(fileId);
			result.setAttachFileList(fileList);
		}
		
		// 5. 내 제출기록 가져오기
		List<IndivtaskSubmitInfo> submit = indivtaskMapper.selectIndivtaskSubmit(indivtaskId, enroll.getEnrollId());
		
		// 6. 응답용 객체로 변환
		List<IndivtaskSubmitResp> studentSubmitList = submit.stream().map(each -> {
			IndivtaskSubmitResp eachSubmitResp = new IndivtaskSubmitResp();	
			BeanUtils.copyProperties(each, eachSubmitResp);
			// 제출마다 첨부된 파일이 있으면 넣기
			if(each.getSubmitFileId() != null) {
				List<FileDetailResp> submitFiles = filesService.readFileDetailList(each.getSubmitFileId());
				eachSubmitResp.setSubmitFiles(submitFiles);
			}
			
			return eachSubmitResp;
		}).collect(Collectors.toList());
		result.setStudentSubmitList(studentSubmitList);
		
		return result;
	}

	/**
	 * 특정 개인과제에 대한 제출 요청을 받아, <br>
	 * 제출 권한을 확인한 뒤 제출을 기록합니다. <br>
	 * 제출 권한 : <br>
	 * 1. 강의를 수강중이며 <br>
	 * 2. 제출 대상으로 등록되었는가 <br>
	 * 3. 제출 기간인가
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param indivtaskId 개인과제ID
	 * @param request 제출 내용 + 제출 파일
	 */
	@Override
	public void submitIndivtask(
		UsersVO user
		, String lectureId
		, String indivtaskId
		, TaskSubmitReq request
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 요청 개인과제 검색 후 요청과 대조
		LctIndivtaskInfo search = indivtaskMapper.selectIndivtask(indivtaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 개인과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		// 3. 내 제출기록 가져오기
		List<IndivtaskSubmitInfo> submitList =
			indivtaskMapper.selectIndivtaskSubmit(indivtaskId, enroll.getEnrollId());
		if(submitList.size() == 0)
			throw new RuntimeException("제출 대상이 아닙니다.");
		
		// 4. 제출 기간 확인
		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(search.getStartAt()))
			throw new RuntimeException("제출 기간이 아직 시작되지 않았습니다.");

		if (search.getEndAt() != null && now.isAfter(search.getEndAt()))
			throw new RuntimeException("이미 마감된 과제입니다.");

		// 5. 파일이 올라가있고, 올린 사람이 본인인지?
		if(request.getSubmitFileId() != null) {
			FilesBundleDTO attachFile = filesService.readFileBundle(request.getSubmitFileId());
			
			// 5-1. 내가 올린 파일이 아닌 파일을 첨부하려고 시도하면
			if(!attachFile.getFilesInfo().getUserId().equals(user.getUserId()))
				throw new RuntimeException("업로드 중 문제가 발생하였습니다. 다시 시도해 주세요.");
			
			// 5-2. 업로드한 파일을 사용한다고 확정.
			filesService.changeUsingStatus(attachFile.getFileId(), true);
		}
		
		// 6. DB 객체에 옮겨담기
		IndivtaskSubmitInfo submit = new IndivtaskSubmitInfo();
		submit.setEnrollId(enroll.getEnrollId());
		submit.setIndivtaskId(indivtaskId);
		BeanUtils.copyProperties(request, submit);
		
		// 4. 제출
		indivtaskMapper.updateIndivtaskSubmit(submit);
	}

	@Override
	public List<GrouptaskLabelResp_STU> readGrouptaskList(
		UsersVO user
		, String lectureId
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 내가 제출해야할 과제+제출 여부 목록 가져오기.
		List<LctGrouptaskInfo> searchList
			= grouptaskMapper.selectGrouptaskList_STU(lectureId, enroll.getEnrollId());
		
		// 3. 응답용 객체에 넣어서 반환
		return searchList.stream().map(info -> {
			GrouptaskLabelResp_STU resp = new GrouptaskLabelResp_STU();
			BeanUtils.copyProperties(info, resp);
			
			resp.setTarget(info.getIsSubmitted() != null);
			resp.setHasAttachFile((info.getAttachFileId() != null));
			resp.setSubmitted(info.getIsSubmitted() != null && info.getIsSubmitted() == 1);
			
			return resp;
		}).collect(Collectors.toList());
	}

	@Override
	public LctGrouptaskDetailResp readGrouptask(
		UsersVO user
		, String lectureId
		, String grouptaskId
	) {
		// 1. 관련 있는 학생인지 확인
		classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 요구 조건대로 개인과제 검색 후 요청과 대조
		LctGrouptaskInfo search = grouptaskMapper.selectGrouptask(grouptaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		// 3. 응답으로 내보낼 객체 생성, 과제에 대한 정보 입력
		LctGrouptaskDetailResp result = new LctGrouptaskDetailResp();
		BeanUtils.copyProperties(search, result);
		
		// 4. 과제에 파일 첨부되었으면 파일 정보 넣기
		String fileId = search.getAttachFileId();
		if(fileId != null) {
			List<FileDetailResp> fileList = filesService.readFileDetailList(fileId);
			result.setAttachFileList(fileList);
		}
		
		return result;
	}
	
	@Override
	public GrouptaskJoResp readGrouptaskJo(
		UsersVO user
		, String lectureId
		, String grouptaskId
	) {
		// 1. 관련 있는 학생인지 확인
		classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 요구 조건대로 개인과제 검색 후 요청과 대조
		LctGrouptaskInfo search = grouptaskMapper.selectGrouptask(grouptaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		// 3. 내 조, 내 조 구성원, 내 조의 제출
		
		List<GrouptaskGroupWithCrewDTO> jo = grouptaskMapper.selectGroupList(grouptaskId, user.getUserNo());
		if(jo.size() == 0)
			throw new RuntimeException("제출 대상이 아닙니다.");
		
		GrouptaskGroupWithCrewDTO myJo = jo.get(0);
		
		GrouptaskJoResp resp = new GrouptaskJoResp();;
		
		BeanUtils.copyProperties(myJo.getGroupInfo(), resp);
		BeanUtils.copyProperties(myJo.getSubmitInfo(), resp);
		
		if(resp.getSubmitFileId() != null) {
			List<FileDetailResp> fileList = filesService.readFileDetailList(resp.getSubmitFileId());
			resp.setAttachFileList(fileList);
		}
		
		List<GrouptaskCrewInfo> crews = myJo.getCrewInfoList();
		
		List<CrewResp> crewRespList = crews.stream().map(crew -> {
			GrouptaskJoResp.CrewResp crewResp = new GrouptaskJoResp.CrewResp(); 
			BeanUtils.copyProperties(crew, crewResp);
			return crewResp;
		}).toList();
		
		resp.setCrewList(crewRespList);
		
		return resp;
	}
	
	
	/**
	 * 특정 조별과제에 대한 제출 요청을 받아, <br>
	 * 제출 권한을 확인한 뒤 제출을 기록합니다. <br>
	 * 제출 권한 : <br>
	 * 1. 강의를 수강중이며 <br>
	 * 2. 제출 대상으로 등록되었는가 <br>
	 * 3. 해당 조의 조장이며 <br>
	 * 4. 제출 기간인가
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @param request 제출 내용 + 제출 파일
	 */
	@Override
	public void submitGrouptask(
		UsersVO user
		, String lectureId
		, String grouptaskId
		, TaskSubmitReq request
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 요청 조별과제 검색 후 요청과 대조
		LctGrouptaskInfo search = grouptaskMapper.selectGrouptask(grouptaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 개인과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		// 3. 내 조의 제출기록 가져오기
		List<GrouptaskGroupWithCrewDTO> submitList = 
			grouptaskMapper.selectGroupList(grouptaskId, user.getUserNo());
		
		if(submitList.size() == 0)
			throw new RuntimeException("제출 대상이 아닙니다.");
		
		// 4. 제출 기간 확인
		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(search.getStartAt()))
			throw new RuntimeException("제출 기간이 아직 시작되지 않았습니다.");

		if (search.getEndAt() != null && now.isAfter(search.getEndAt()))
			throw new RuntimeException("이미 마감된 과제입니다.");
		
		// 5. 조장만 제출할 수 있는데, 본인이 조장인지?
		GrouptaskGroupWithCrewDTO jo = submitList.get(0);
		if(!enroll.getEnrollId().equals(jo.getGroupInfo().getLeaderEnrollId()))
			throw new RuntimeException("조장만 과제 결과를 제출할 수 있습니다.");
		
		// 6. 파일이 올라가있고, 올린 사람이 본인인지?
		if(request.getSubmitFileId() != null) {
			FilesBundleDTO attachFile = filesService.readFileBundle(request.getSubmitFileId());
			
			// 6-1. 내가 올린 파일이 아닌 파일을 첨부하려고 시도하면
			if(!attachFile.getFilesInfo().getUserId().equals(user.getUserId()))
				throw new RuntimeException("업로드 중 문제가 발생하였습니다. 다시 시도해 주세요.");
			
			// 6-2. 업로드한 파일을 사용한다고 확정.
			filesService.changeUsingStatus(attachFile.getFileId(), true);
		}
		
		// 7. DB 객체에 옮겨담기
		GrouptaskSubmitInfo submit = new GrouptaskSubmitInfo();
		submit.setGrouptaskId(grouptaskId);
		submit.setGroupId(jo.getGroupId());
		BeanUtils.copyProperties(request, submit);
		
		// 8. 제출
		grouptaskMapper.updateGrouptaskSubmit(submit);
		
	}
	
	@Override
	public List<NotSubmittedIndivtaskResp> readNotSubmittedTaskList(
		UsersVO user
	) {
		List<LctIndivtaskInfo> resultList = indivtaskMapper.notSubmittedTaskList(user.getUserNo());
		
		return resultList.stream().map(task -> {
			NotSubmittedIndivtaskResp resp = new NotSubmittedIndivtaskResp();
			
			LectureLabelResp lecture = commonService.readLecture(task.getLectureId());
			
			BeanUtils.copyProperties(lecture, resp);
			BeanUtils.copyProperties(task, resp);
			
			return resp;
		}).collect(Collectors.toList());
	}
}