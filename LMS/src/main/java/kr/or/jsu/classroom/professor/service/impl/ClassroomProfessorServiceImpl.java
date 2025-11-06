package kr.or.jsu.classroom.professor.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.dto.db.LectureSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.classroom.dto.response.classroom.ProfessorMyInfoResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.classroom.professor.service.ClassroomProfessorService;
import kr.or.jsu.core.dto.info.ProfessorInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import lombok.RequiredArgsConstructor;

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
 *  2025. 11.03         최건우              내용 추가 (주석으로 설명 ,132 줄)
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class ClassroomProfessorServiceImpl implements ClassroomProfessorService {

	private final ProfessorMapper professorMapper;
	private final LectureMapper lectureMapper;
	
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
	/**
	 * 사용자 교수의 개인정보를 조회합니다.
	 * 
	 * @return 개인정보(간략)
	 */
	@Override
	public ProfessorMyInfoResp getMyInfo() {
		
		ProfessorInfo result = professorMapper.selectBasicProfessorInfo(getUser().getRealUser().getUserNo());
		ProfessorMyInfoResp resp = new ProfessorMyInfoResp();
		BeanUtils.copyProperties(result, resp);
		
		resp.setUnivDeptName(cache.getUnivDeptName(resp.getUnivDeptCd()));
		resp.setPrfStatusName(cache.getCodeName(resp.getPrfStatusCd()));
		resp.setPrfAppntName(cache.getCodeName(resp.getPrfAppntCd()));
		if(resp.getPrfPositCd() != null)
			resp.setPrfPositName(cache.getCodeName(resp.getPrfPositCd()));
		
		return resp;
	}

	/**
	 * 사용자 교수가 담당한 강의 목록을 반환합니다.
	 * 
	 * @return 응답용 강의목록 정보 리스트
	 */
	@Override
	public List<LectureLabelResp> readMyLectureList() {
		String prfNo = getUser().getRealUser().getUserNo();
		
		// 사용자 교번으로 담당한 강의 검색
		List<LectureSimpleDTO> list = lectureMapper.selectTeachingLectureList(prfNo);
		
		// 검색한 거 없으면 null 반환
		// TODO 대신 예외처리
		if(list==null || list.isEmpty()) return null;
		
		// 검색된 게 있으면, 응답용 DTO로 변환.
		List<LectureLabelResp> result = list.stream().map(dto -> {
			LectureLabelResp resp = new LectureLabelResp();
			
			LectureInfo li = dto.getLectureInfo();
		    SubjectInfo si = dto.getSubjectInfo();
			
		    resp.setLectureId(li.getLectureId());
		    resp.setSubjectName(si.getSubjectName());
		    resp.setProfessorNo(li.getProfessorNo());
		    resp.setProfessorName(cache.getUserName(li.getProfessorNo()));
		    resp.setUnivDeptName(cache.getUnivDeptName(si.getUnivDeptCd()));
		    resp.setCompletionName(cache.getCodeName(si.getCompletionCd()));
		    resp.setYeartermCd(li.getYeartermCd());
		    resp.setSubjectTypeName(cache.getCodeName(si.getSubjectTypeCd()));
		    resp.setCredit(si.getCredit());
		    resp.setHour(si.getHour());
		    resp.setCurrentCap(dto.getCurrentCap());
		    resp.setMaxCap(li.getMaxCap());
		    
		    // yearterm_code가
		    if(cache.getCurrentYearterm().equals(resp.getYeartermCd()))
		    	resp.setStarted(true);
		    
		    if(dto.getLectureInfo().getEndAt() != null) {
		    	resp.setStarted(true);
		    	resp.setEnded(true);
		    	resp.setEndAt(dto.getLectureInfo().getEndAt());
		    }
		    
		    // 강의 종료일(endAt)이 설정되어 있으면 강의가 시작되었고 종료되었음을 표시
		    // isFinalized는 scoreFinalizeYn에 따라 결정되므로 여기서는 isEnded만 설정
		    if ("Y".equals(dto.getLectureInfo().getScoreFinalizeYn())) { // 성적 확정 여부가 'Y'이면 최종 완료된 강의로 간주
		        resp.setFinalized(true);
		    }
		    
		    return resp;
		}).collect(Collectors.toList());
		
		// 아이디만 빼기
		List<String> ids = result.stream().map(r -> r.getLectureId()).collect(Collectors.toList());
		
		// 뺀 아이디로 스케줄 가져오기
		List<LectureWithScheduleDTO> schedules = lectureMapper.selectScheduleListJson(ids);
		
		// 스케줄을 넣기 편하게 맵으로 변경
		Map<String, String> scheduleById = 
				schedules.stream()
			    .collect(Collectors.toMap(
			        LectureWithScheduleDTO::getLectureId,
			        LectureWithScheduleDTO::getScheduleJson
			    ));
		
		result.forEach(lct -> {
			String eachSchedule = scheduleById.get(lct.getLectureId());
			lct.setScheduleJson(eachSchedule);
		});
		
		return result;
	}

	public List<LectureScheduleResp> readMySchedule(
		String yeartermCd
	) {
		if(yeartermCd == null) yeartermCd = cache.getCurrentYearterm();
		String prfNo = getUser().getRealUser().getUserNo();
		
		return lectureMapper.selectPrfLectureSchedule(yeartermCd, prfNo);
	}
}