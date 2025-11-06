package kr.or.jsu.classroom.professor.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.catalina.webresources.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.db.GrouptaskGroupWithCrewDTO;
import kr.or.jsu.classroom.dto.db.task.NotEvaluatedGrouptaskDTO;
import kr.or.jsu.classroom.dto.db.task.NotEvaluatedIndivtaskDTO;
import kr.or.jsu.classroom.dto.info.GrouptaskCrewInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskGroupInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import kr.or.jsu.classroom.dto.info.LctTaskWeightInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.request.GroupTaskCreateReq;
import kr.or.jsu.classroom.dto.request.IndivTaskCreateReq;
import kr.or.jsu.classroom.dto.request.task.GrouptaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskEvaluateReq;
import kr.or.jsu.classroom.dto.request.task.IndivtaskModifyReq;
import kr.or.jsu.classroom.dto.request.task.TaskWeightValueReq;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentGrouptaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentIndivtaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskJoResp.CrewResp;
import kr.or.jsu.classroom.dto.response.task.GrouptaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.IndivtaskLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.task.LctGrouptaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp;
import kr.or.jsu.classroom.dto.response.task.LctIndivtaskDetailResp.IndivtaskSubmitResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedGrouptaskResp;
import kr.or.jsu.classroom.dto.response.task.NotEvaluatedIndivtaskResp;
import kr.or.jsu.classroom.dto.response.task.TaskWeightValueResp;
import kr.or.jsu.classroom.professor.service.ClassPrfTaskService;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.LctGrouptaskMapper;
import kr.or.jsu.mybatis.mapper.LctIndivtaskMapper;
import kr.or.jsu.mybatis.mapper.LctTaskMapper;
import kr.or.jsu.mybatis.mapper.StuEnrollLctMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfTaskServiceImpl implements ClassPrfTaskService {
	
	private final StuEnrollLctMapper enrollMapper;
	private final LctTaskMapper taskMapper;
	private final LctIndivtaskMapper indivtaskMapper;
	private final LctGrouptaskMapper grouptaskMapper;
	
	private final LMSFilesService filesService;
	private final ClassroomCommonService classCommonService;
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
	
	private String getUserNo () {
		log.info("과제서비스에서 인증검사 들어갑니다");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails user = (CustomUserDetails) principal;
                String userNo = user.getRealUser().getUserNo();
                
                log.info("요청 사용자 번호 : {}", userNo);
                return userNo;
            }
        }
        
        throw new RuntimeException("사용자 정보를 가져올 수 없습니다.");
	}
		
	@Transactional
	@Override
	public String createIndivtask(
		String lectureId
		, IndivTaskCreateReq taskCreateReq
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의에만 과제를 출제할 수 있습니다.");
		
		// 1. DB에 전달할 데이터전송 객체 생성
		LctIndivtaskInfo newTask = new LctIndivtaskInfo();
		
		// 2. 요청에서 데이터 옮겨담기
		newTask.setLectureId(lectureId);
		BeanUtils.copyProperties(taskCreateReq, newTask);
		
		// 3. 개인과제 출제하고, 셀렉트키로 기본키 가져오기
		indivtaskMapper.insertIndivtask(newTask);
		String indivtaskId = newTask.getIndivtaskId();
		
		// 4. 파일이 첨부되었을 경우, 파일을 사용 상태로 전환
		String fileId = newTask.getAttachFileId();
		if(fileId != null) {
			filesService.changeUsingStatus(fileId, true);
		}
		
		// 5. 출제 대상 학생들을 가져와서, 미리 제출될 파일을 기록할 레코드 생성 
		List<StuEnrollLctInfo> activeStudentList = enrollMapper.selectAllStudentList(lectureId, true);
		List<String> enrollIds = activeStudentList.stream().map(stu -> stu.getEnrollId()).collect(Collectors.toList());
		
		int submitRecordCnt = indivtaskMapper.insertIndivtaskSubmit(enrollIds, indivtaskId);
		log.info("{} 명의 학생에 대한 과제 출제 완료됨", submitRecordCnt);
		
		// 6. 셀렉트키로 생성된 ID반환
		return indivtaskId;
	}
	
	@Override
	public List<IndivtaskLabelResp_PRF> readIndivtaskList(
		String lectureId
	) {
		String userNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(userNo, lectureId);
		if(!isRelavant) throw new RuntimeException("관련된 강의 과제만 조회할 수 있습니다.");
		
		List<LctIndivtaskInfo> searchList = indivtaskMapper.selectIndivtaskList_PRF(lectureId);
		
		return searchList.stream().map(info -> {
			IndivtaskLabelResp_PRF resp = new IndivtaskLabelResp_PRF();
			BeanUtils.copyProperties(info, resp);
			
			resp.setHasAttachFile((info.getAttachFileId() != null));
			return resp;
		}).collect(Collectors.toList());
	}
	
	public LctIndivtaskDetailResp readIndivtask(
		String lectureId
		, String indivtaskId
	) {
		// TODO 예외처리
		CustomUserDetails currentPrf = getUser();
		String prfNo = currentPrf.getRealUser().getUserNo();
		
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의 과제만 조회할 수 있습니다.");
		
		log.info("사용자 교번 : {}", prfNo);
		
		// 1. 응답으로 내보낼 객체 생성
		LctIndivtaskDetailResp result = new LctIndivtaskDetailResp();
				
		// 2. 요구 조건대로 개인과제 검색
		LctIndivtaskInfo search = indivtaskMapper.selectIndivtask(indivtaskId);
		if(search == null)
			throw new RuntimeException("삭제되었거나 존재하지 않는 개인과제입니다.");
		if(!search.getLectureId().equals(lectureId))
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		BeanUtils.copyProperties(search, result);
		
		// 3. 개인과제 출제 시 파일 첨부되었으면 파일 정보 넣기
		String fileId = search.getAttachFileId();
		if(fileId != null) {
			result.setFileId(fileId);
			List<FileDetailResp> fileList = filesService.readFileDetailList(fileId);
			result.setAttachFileList(fileList);
		}
		 
		
		List<IndivtaskSubmitInfo> studentSubmitInfoList = indivtaskMapper.selectIndivtaskSubmit(indivtaskId, null);
		
		List<IndivtaskSubmitResp> studentSubmitList = studentSubmitInfoList.stream().map(each -> {
			LctIndivtaskDetailResp.IndivtaskSubmitResp eachSubmitResp = new LctIndivtaskDetailResp.IndivtaskSubmitResp();
			
			BeanUtils.copyProperties(each, eachSubmitResp);
			
			// 제출마다 첨부된 파일이 있으면 넣기
			if(each.getSubmitFileId() != null) {
				List<FileDetailResp> submitFiles = 
					filesService.readFileDetailList(each.getSubmitFileId());
				eachSubmitResp.setSubmitFiles(submitFiles);
			}
			
			return eachSubmitResp;
		}).collect(Collectors.toList());
		
		result.setStudentSubmitList(studentSubmitList);
		
		return result;
	};
	
	@Override
	public void removeIndivtask(
		String lectureId
		, String indivtaskId
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("강의 담당 교수만 가능한 기능입니다.");
		
		int delRow = indivtaskMapper.deleteIndivtaskSoftly(indivtaskId);
		if(delRow == 0) throw new RuntimeException("존재하지 않는 과제입니다.");
	}
	
	@Override
	public void modifyIndivtask(
		String lectureId
		, IndivtaskModifyReq taskModifyReq			
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("강의 담당 교수만 가능한 기능입니다.");
		
		// 1. 과제ID로 가져와보기
		String originTaskId = taskModifyReq.getIndivtaskId();
		
		LctIndivtaskInfo origin = indivtaskMapper.selectIndivtask(originTaskId);
		if(origin == null) throw new RuntimeException("존재하지 않는 과제입니다.");
		
		// 파일 상태 변경 로직
		String originFileId = origin.getAttachFileId();
		String changedFileId = taskModifyReq.getAttachFileId();
		filesService.switchActiveFileStatus(originFileId, changedFileId);
		
		// 수정용 객체 생성 후 기존내용 -> 변경내용 순서대로 덮어쓰기 
		LctIndivtaskInfo changed = new LctIndivtaskInfo();
		BeanUtils.copyProperties(origin, changed);
		BeanUtils.copyProperties(taskModifyReq, changed);
		
		indivtaskMapper.updateIndivtask(changed);
	}
	
	public void evaluateIndivtaskSubmit(
		String lectureId
		, String indivtaskId
		, IndivtaskEvaluateReq request
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		LctIndivtaskInfo indivtask = indivtaskMapper.selectIndivtask(indivtaskId);
		if(!lectureId.equals(indivtask.getLectureId())) 
			throw new RuntimeException("해당 강의에서 출제된 과제가 아닙니다.");
		
		IndivtaskSubmitInfo submit = new IndivtaskSubmitInfo();
		submit.setIndivtaskId(indivtaskId);
		BeanUtils.copyProperties(request, submit);
		
		indivtaskMapper.updateIndivtaskSubmit(submit);
	};

	@Transactional
	@Override
	public String createGrouptask(
		String lectureId
		, GroupTaskCreateReq groupTaskAndGroups
	) {
		// TODO 예외처리
		// TODO 조 구성 무결성 체크(모든 활성화된 학생이 조원으로 구성되어야 함)
		
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의에만 과제를 출제할 수 있습니다.");
		
		// 1. 조별과제 테이블에 레코드를 생성해야 함.
		// 1-1. DB 전송용 객체 생성
		LctGrouptaskInfo newTask = new LctGrouptaskInfo();
		
		// 1-2. 요청에서 데이터 옮겨담기
		newTask.setLectureId(lectureId);
		BeanUtils.copyProperties(groupTaskAndGroups, newTask);
		
		// 1-3. 과제 출제
		grouptaskMapper.insertGrouptask(newTask);
		
		// 1-4. 파일이 첨부되었을 경우, 파일을 사용 상태로 전환
		String fileId = newTask.getAttachFileId();
		if(fileId != null) {
			filesService.changeUsingStatus(fileId, true);
		}
		
		// 2. 조별과제 레코드 셀렉트키를 가져와서, 조 테이블의 레코드를 만들어야 함.
		// 2-1. 셀렉트키로 생성된 조별과제ID 가져오기
		String grouptaskId = newTask.getGrouptaskId();
		
		// 2-2. 요청에서 "조" 생성에 필요한 그룹이름, 조장이 들어있는 리스트 꺼내기
		List<GroupTaskCreateReq.Groups> groups = groupTaskAndGroups.getGroups();
		int reqGroupSize = groups.size();
		int createdGroupSize = 0;
		int createdSubmitSize = 0;
		
		for (GroupTaskCreateReq.Groups groupReq : groups) {
		    GrouptaskGroupInfo eachGroup = new GrouptaskGroupInfo();
		    eachGroup.setGrouptaskId(grouptaskId);
		    BeanUtils.copyProperties(groupReq, eachGroup);

		    createdGroupSize += grouptaskMapper.insertGroups(eachGroup);
		    String groupId = eachGroup.getGroupId();

		    GrouptaskSubmitInfo blankSubmit = new GrouptaskSubmitInfo();
		    blankSubmit.setGroupId(groupId);
		    blankSubmit.setGrouptaskId(grouptaskId);
		    createdSubmitSize += grouptaskMapper.insertGrouptaskSubmit(blankSubmit);

		    List<String> crewsEnrollIdList = groupReq.getCrews();
		    grouptaskMapper.insertGrouptaskCrew(groupId, crewsEnrollIdList);
		}

		
		if(reqGroupSize != createdGroupSize ||
		   reqGroupSize != createdSubmitSize)
			throw new RuntimeException("조 생성에 실패했습니다.");
		
		// 5. 셀렉트키로 생성된 ID반환
		return grouptaskId;
	}
	
	@Override
	public List<GrouptaskLabelResp_PRF> readGrouptaskList(
		String lectureId
	) {
		String userNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(userNo, lectureId);
		if(!isRelavant) throw new RuntimeException("관련된 강의 과제만 조회할 수 있습니다.");
		
		List<LctGrouptaskInfo> searchList = grouptaskMapper.selectGrouptaskList_PRF(lectureId);
		
		return searchList.stream().map(info -> {
			GrouptaskLabelResp_PRF resp = new GrouptaskLabelResp_PRF();
			BeanUtils.copyProperties(info, resp);
			
			resp.setHasAttachFile((info.getAttachFileId() != null));
			return resp;
		}).collect(Collectors.toList());
	}
	
	@Override
	public LctGrouptaskDetailResp readGrouptask(
		String lectureId
		, String grouptaskId
	) {
		String userNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(userNo, lectureId);
		if(!isRelavant) throw new RuntimeException("관련된 강의 과제만 조회할 수 있습니다.");
		
		// 1. 조별과제를 가져온다.
		LctGrouptaskInfo grouptask = grouptaskMapper.selectGrouptask(grouptaskId);
		if(grouptask == null) 
			throw new RuntimeException("존재하지 않거나 이미 삭제된 과제입니다.");
		if(!lectureId.equals(grouptask.getLectureId()))
			throw new RuntimeException("해당 강의에서 제출된 과제가 아닙니다.");
		
		// 2. 응답용 객체 생성 후 옮겨담기
		LctGrouptaskDetailResp resp = new LctGrouptaskDetailResp();
		BeanUtils.copyProperties(grouptask, resp);
		
		// 3. 파일 첨부되었으면 파일 정보 넣기
		String fileId = grouptask.getAttachFileId();
		if(fileId != null) {
			List<FileDetailResp> fileList = filesService.readFileDetailList(fileId);
			resp.setAttachFileList(fileList);
		}
		
		return resp;
	}
	
	/**
	 * 교수의 조별과제 상세 조회 2 <br>
	 * 조별과제에 구성된 조, 조별 제출, 조별 구성원 등을 반환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param grouptaskId 조별과제ID
	 * @return 
	 * @return 조별과제에 대한 각 조별 정보
	 */
	@Override
	public List<GrouptaskJoResp> readGrouptaskJo(
		String lectureId
		, String grouptaskId
	) {
		String userNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(userNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의에서 출제한 과제만 조회할 수 있습니다.");
		
		// 1. 조별과제를 가져온다.
		LctGrouptaskInfo grouptask = grouptaskMapper.selectGrouptask(grouptaskId);
		if(grouptask == null) 
			throw new RuntimeException("존재하지 않거나 이미 삭제된 과제입니다.");
		if(!lectureId.equals(grouptask.getLectureId()))
			throw new RuntimeException("해당 강의에서 제출된 과제가 아닙니다.");
		
		List<GrouptaskGroupWithCrewDTO> result = grouptaskMapper.selectGroupList(grouptaskId, null);
		
		return result.stream().map(jo -> {
			GrouptaskJoResp resp = new GrouptaskJoResp();
			
			BeanUtils.copyProperties(jo.getGroupInfo(), resp);
			BeanUtils.copyProperties(jo.getSubmitInfo(), resp);
			
			if(resp.getSubmitFileId() != null) {
				List<FileDetailResp> fileList = filesService.readFileDetailList(resp.getSubmitFileId());
				resp.setAttachFileList(fileList);
			}
			
			List<GrouptaskCrewInfo> crews = jo.getCrewInfoList();
			
			List<CrewResp> crewRespList = crews.stream().map(crew -> {
				GrouptaskJoResp.CrewResp crewResp = new GrouptaskJoResp.CrewResp(); 
				BeanUtils.copyProperties(crew, crewResp);
				return crewResp;
			}).toList();
			
			resp.setCrewList(crewRespList);
			
			return resp;
		}).toList();
	}
	
	@Override
	public void modifyGrouptask(
		String lectureId
		, GrouptaskModifyReq taskModifyReq
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("강의 담당 교수만 가능한 기능입니다.");
		
		// 1. 과제ID로 가져와보기
		String originTaskId = taskModifyReq.getGrouptaskId();
		
		LctGrouptaskInfo origin = grouptaskMapper.selectGrouptask(originTaskId);		
		if(origin == null) throw new RuntimeException("존재하지 않는 조별과제입니다.");
		
		// 파일 상태 변경 로직
		String originFileId = origin.getAttachFileId();
		String changedFileId = taskModifyReq.getAttachFileId();
		filesService.switchActiveFileStatus(originFileId, changedFileId);
		
		// 수정용 객체 생성 후 기존내용 -> 변경내용 순서대로 덮어쓰기 
		LctGrouptaskInfo changed = new LctGrouptaskInfo();
		BeanUtils.copyProperties(origin, changed);
		BeanUtils.copyProperties(taskModifyReq, changed);
		
		grouptaskMapper.updateGrouptask(changed);

	}

	@Override
	public void removeGrouptask(
		String lectureId
    	, String grouptaskId
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		LctGrouptaskInfo grouptask = grouptaskMapper.selectGrouptask(grouptaskId);
		if(!lectureId.equals(grouptask.getLectureId()))
			throw new RuntimeException("존재하지 않는 조별과제입니다."); // 니 강의에 그런과제가 없다는거임. 실제론 있을수있음
		
		grouptaskMapper.deleteGrouptaskSoftly(grouptaskId);
	}

	/**
	 * 특정 강의에서 출제된 삭제되지 않은 과제들의 가중치를 요청합니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	@Override
	public List<TaskWeightValueResp> readTaskWeightValues(
		String lectureId
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		List<LctTaskWeightInfo> result = taskMapper.selectWeightValues(lectureId);
		
		return result.stream().map(r -> {
			TaskWeightValueResp resp = new TaskWeightValueResp();
			
			BeanUtils.copyProperties(r, resp);
			return resp;
		}).toList();
	}

	/**
	 * 특정 강의에서 출제된 삭제되지 않은 과제들의 가중치를 일괄 수정합니다. <br>
	 * 수정할 경우 가중치 총합이 100이어야 합니다.
	 * 
	 * @param values
	 */
	@Transactional
	@Override
	public void modifyAllTaskWeightValues(
		String lectureId
		, List<TaskWeightValueReq> values
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		List<IndivtaskLabelResp_PRF> a = readIndivtaskList(lectureId);
		List<GrouptaskLabelResp_PRF> b = readGrouptaskList(lectureId);
		
		Set<String> taskIdSet = new HashSet<String>();
		a.forEach(r -> taskIdSet.add(r.getIndivtaskId()));
		b.forEach(r -> taskIdSet.add(r.getGrouptaskId()));
		
		for(TaskWeightValueReq value : values) {
			String taskId = value.getTaskId();
			boolean isOwn = taskIdSet.contains(taskId);
			if(!isOwn) throw new RuntimeException("출제한 강의만 가중치를 수정할 수 있습니다.");
		}
		
		List<LctTaskWeightInfo> upsertReq = values.stream().map(req -> {
			LctTaskWeightInfo info = new LctTaskWeightInfo();
			BeanUtils.copyProperties(req, info);
			return info;
		}).toList();
		
		taskMapper.upsertWeightValues(upsertReq);
		
		List<TaskWeightValueResp> results = readTaskWeightValues(lectureId);
		
		int valueSum = 0;
		for(TaskWeightValueResp result : results) {
			Integer eachValue =result.getWeightValue();
			if(eachValue != null) valueSum += eachValue;
		}
		if(valueSum != 100) throw new RuntimeException("가중치 총합을 100으로 설정해 주세요.");
	}
	
	/**
	 * 강의의 개인과제 중 <br>
	 * 1. 진행중인 강의의 <br>
	 * 2. 개인과제가 삭제되지 않았고 <br>
	 * 3. 개인과제에 대해 아직 평가받지 않은 학생의 제출이 존재하는 <br>
	 * 강의 & 개인과제와 과제별 채점할 제출물 수를 반환합니다.
	 */
	@Override
	public List<NotEvaluatedIndivtaskResp> readNotEvaluatedIndivtaskList() {
		String professorNo = getUserNo();
		
		List<NotEvaluatedIndivtaskDTO> resultList = indivtaskMapper.notEvaluatedTaskList(professorNo);
		
		return resultList.stream().map(task -> {
			NotEvaluatedIndivtaskResp resp = new NotEvaluatedIndivtaskResp();
			
			LectureLabelResp lecture = new LectureLabelResp();
			LctIndivtaskDetailResp indivtask = new LctIndivtaskDetailResp();
			
			BeanUtils.copyProperties(task.getLecture(), lecture);
			lecture.setSubjectName(cache.getSubjectName(task.getLecture().getSubjectCd()));
			BeanUtils.copyProperties(task.getIndivtask(), indivtask);
			
			resp.setLecture(lecture);
			resp.setIndivtask(indivtask);
			resp.setSubmitCount(task.getSubmitCount());
			
			return resp;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 강의의 조별과제 중 <br>
	 * 1. 진행중인 강의의 <br>
	 * 2. 조별과제가 삭제되지 않았고 <br>
	 * 3. 조별과제에 대해 아직 평가받지 않은 제출이 존재하는 <br>
	 * 강의 & 조별과제와 과제별 채점할 제출물 수를 반환합니다.
	 */
	@Override
	public List<NotEvaluatedGrouptaskResp> readNotEvaluatedGrouptaskList() {
		String professorNo = getUserNo();
		
		List<NotEvaluatedGrouptaskDTO> resultList = grouptaskMapper.notEvaluatedTaskList(professorNo);
		
		return resultList.stream().map(task -> {
			NotEvaluatedGrouptaskResp resp = new NotEvaluatedGrouptaskResp();
			
			LectureLabelResp lecture = new LectureLabelResp();
			LctGrouptaskDetailResp grouptask = new LctGrouptaskDetailResp();
			
			BeanUtils.copyProperties(task.getLecture(), lecture);
			lecture.setSubjectName(cache.getSubjectName(task.getLecture().getSubjectCd()));
			BeanUtils.copyProperties(task.getGrouptask(), grouptask);
			
			resp.setLecture(lecture);
			resp.setGrouptask(grouptask);
			resp.setSubmitCount(task.getSubmitCount());
			
			return resp;
		}).collect(Collectors.toList());
	}

	@Override
	public List<EachStudentIndivtaskScoreResp> readAllIndivtaskAndEachStudentScore(
		String lectureId
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		return taskMapper.selectIndivtaskAndEachStudentScore(lectureId);
	}

	@Override
	public List<EachStudentGrouptaskScoreResp> readAllGrouptaskAndEachStudentScore(
		String lectureId
	) {
		String prfNo = getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 강의가 아닙니다.");
		
		return taskMapper.selectGrouptaskAndEachStudentScore(lectureId);
	}
}