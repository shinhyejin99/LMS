package kr.or.jsu.lms.staff.service.professor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.mybatis.mapper.AddressMapper;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author 신혜진
 * @since 2025. 9. 25.
 * @see
 *
 *      * * *
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일             수정자           수정내용
 * -----------      -------------   ---------------------------
 * 2025. 9. 25.       신혜진           최초 생성
 *      </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffProfessorInfoServiceImpl implements StaffProfessorInfoService {

	private final ProfessorMapper mapper; // DAO/Mapper
	private final UsersMapper usersMapper;
	private final AddressMapper addressMapper;
	private final FilesUploadService filesUploadService; // ⭐️ 파일 서비스 주입

	@Override
	@Transactional
	public int createStaffProfessorInfo(ProfessorInfoDTO dto) {

		// 1. DTO → VO 변환 및 기본값 설정
		ProfessorVO vo = new ProfessorVO();
		BeanUtils.copyProperties(dto, vo);

		// DTO의 deptCd (등록 폼 값)을 VO의 univDeptCd (DB 필드)에 설정
		if (StringUtils.hasText(dto.getDeptCd())) {
			vo.setUnivDeptCd(dto.getDeptCd());
			dto.setUnivDeptCd(dto.getDeptCd()); // DTO에도 univDeptCd 설정
		}

		// 기본값 처리
		if (!StringUtils.hasText(vo.getPrfStatusCd()))
			vo.setPrfStatusCd("PRF_STATUS_ACTV"); // 재직상태 기본값
		if (!StringUtils.hasText(vo.getPrfAppntCd()))
			vo.setPrfAppntCd("PRF_APPNT_REG"); // 임용구분 기본값

		// ⚠️ 보직명 (prfPositCd) 기본값 설정 제거 (수정 반영)
		// if (!StringUtils.hasText(vo.getPrfPositCd()))
		// vo.setPrfPositCd("PRF_POSIT_ASSOC");

		// 안전 장치: 학과 코드가 없으면 기본값 설정 (DB에 'DEP-NONE' 코드가 있어야 함)
		if (!StringUtils.hasText(vo.getUnivDeptCd())) {
			vo.setUnivDeptCd("DEP-NONE");
			dto.setUnivDeptCd("DEP-NONE");
		}

		// ⚠️ 잘못 삽입된 교직원 수정 로직 제거

		// 2. 교수 번호(PROFESSOR_NO) 생성 및 설정
		if (!StringUtils.hasText(vo.getProfessorNo()) && StringUtils.hasText(dto.getHireDateString())) {

			String hireYear = dto.getHireDateString().substring(0, 4);

			// 시퀀스 번호는 0001부터 시작 (mapper에서 +1 처리했다고 가정)
			int nextSequence = mapper.findMaxSequenceByYear(hireYear);

			String newProfessorNo = hireYear + String.format("%04d", nextSequence);

			vo.setProfessorNo(newProfessorNo);
			dto.setProfessorNo(newProfessorNo); // DTO에도 설정
		}

		// 3. ADDRESS → USERS → PROFESSOR 순서로 등록
		int insertedCount = 0;

		// ADDRESS 등록
		AddressVO addrVO = new AddressVO();
		BeanUtils.copyProperties(dto, addrVO);
		addrVO.setUsingYn("Y");
		int addrResult = addressMapper.insertAddress(addrVO);
		if (addrResult != 1)
			throw new RuntimeException("주소 등록 실패");
		insertedCount += addrResult;

		// USERS 등록
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(dto, userVO);
		userVO.setAddrId(addrVO.getAddrId());
		userVO.setPwHash(dto.getPwHash());
		int userResult = usersMapper.insertUser(userVO);
		if (userResult != 1)
			throw new RuntimeException("사용자 등록 실패");
		insertedCount += userResult;

		// 4. 사진 파일 처리 (Users 등록 후) - 수정 반영
		MultipartFile photoFile = dto.getPhotoFile();
		if (photoFile != null && !photoFile.isEmpty()) {

			// A. 단일 파일을 List로 래핑하여 디스크에 저장하고 메타데이터를 받습니다.
			List<FileDetailVO> fileDetails = filesUploadService.saveAtDirectory(List.of(photoFile),
					FileUploadDirectory.IDPHOTO // 프로필 사진 경로 지정 (Enum 사용)
			);

			if (!fileDetails.isEmpty()) {
				// B. 메타데이터를 DB에 저장하고, 새로운 FileId를 받습니다. (프로필 사진은 공개 'Y')
				String photoId = filesUploadService.saveAtDB(fileDetails, userVO.getUserId(), // 업로더 ID는 새로 생성된 USER_ID
						true // isPublic = true
				);
				dto.setPhotoId(photoId); // DTO에 PhotoId 설정
			} else {
				log.warn("프로필 사진 디스크 저장 실패: File ID가 생성되지 않았습니다.");
			}
		}

		// 5. PROFESSOR 등록
		vo.setUserId(userVO.getUserId());
		dto.setUserId(userVO.getUserId());

		int profResult = mapper.insertStaffProfessorInfo(dto);
		if (profResult != 1)
			throw new RuntimeException("교수 등록 실패");
		insertedCount += profResult;

		// 6. DTO에 PhotoId가 설정되었다면, 이를 Users 테이블에 업데이트
		if (StringUtils.hasText(dto.getPhotoId())) {
			UsersVO photoUpdateVO = new UsersVO();
			photoUpdateVO.setUserId(dto.getUserId());
			photoUpdateVO.setPhotoId(dto.getPhotoId());
			usersMapper.updateUser(photoUpdateVO);
		}

		return insertedCount;
	}

	// 재직 상태별 전체 카운트 조회 구현 (파이 차트용)
	@Override
	public Map<String, Integer> readEmploymentStatusCounts() {
		List<Map<String, Object>> rawCountsList = mapper.selectEmploymentStatusCounts();

		// 2. 이 List를 JSP가 원하는 Map<String, Integer> 형태로 변환합니다.
		Map<String, Integer> finalCountsMap = new HashMap<>();

		if (rawCountsList != null) {
			for (Map<String, Object> item : rawCountsList) {
				String statusName = (String) item.get("STATUS"); // MyBatis 쿼리의 STATUS

				// COUNT 값은 BigDecimal 등 숫자로 올 수 있으므로 Integer로 변환
				Number countValue = (Number) item.get("COUNT");

				if (statusName != null && countValue != null) {
					finalCountsMap.put(statusName, countValue.intValue());
				}
			}
		}

		// 3. 변환된 Map을 반환합니다.
		return finalCountsMap;
	}

	// ⭐ 2-2. 상세 통계 조회 구현 (3단계) ⭐
	@Override
	public Map<String, Integer> readProfessorStatsByCollege(Map<String, String> paramMap) {
		List<Map<String, Object>> rawData = mapper.selectProfessorStatsByCollege(paramMap);

		return rawData.stream().collect(
				Collectors.toMap(map -> (String) map.get("NAME"), map -> ((Number) map.get("COUNT")).intValue()));
	}

	@Override
	public Map<String, Integer> readProfessorStatsByDepartment(Map<String, String> paramMap) {
		List<Map<String, Object>> rawData = mapper.selectProfessorStatsByDepartment(paramMap);

		return rawData.stream().collect(
				Collectors.toMap(map -> (String) map.get("NAME"), map -> ((Number) map.get("COUNT")).intValue()));
	}

	@Override
	public Map<String, Integer> readProfessorStatsByPosition(Map<String, String> paramMap) {
		List<Map<String, Object>> rawData = mapper.selectProfessorStatsByPosition(paramMap);

		return rawData.stream().collect(
				Collectors.toMap(map -> (String) map.get("NAME"), map -> ((Number) map.get("COUNT")).intValue()));
	}

	// 3. 교수 상세 정보 조회 (READ DETAIL)
	@Override
	public ProfessorInfoDTO readStaffProfessorInfo(String professorNo) {
		ProfessorInfoDTO dto = mapper.selectProfessorInfoView(professorNo);
		if (dto == null)
			throw new RuntimeException(professorNo + " 교수 정보 없음");
		return dto;
	}

	// 4. 교수 정보 수정 (UPDATE)
	@Override
	@Transactional
	public void modifyStaffProfessorInfo(ProfessorInfoDTO dto) {
		/*
		 * // DTO → VO 변환 ProfessorVO profVO = new ProfessorVO(); UsersVO userVO = new
		 * UsersVO(); AddressVO addrVO = new AddressVO(); *
		 * BeanUtils.copyProperties(dto, profVO); BeanUtils.copyProperties(dto, userVO);
		 * BeanUtils.copyProperties(dto, addrVO); *
		 * profVO.setUpdateAt(LocalDateTime.now());
		 * userVO.setUpdateAt(LocalDateTime.now());
		 * addrVO.setUpdateAt(LocalDateTime.now()); * // ADDRESS, USERS, PROFESSOR 순차적
		 * 수정 int addrResult = addressMapper.updateAddress(addrVO); if (addrResult != 1)
		 * throw new RuntimeException("주소 정보 수정 실패"); * int userResult =
		 * usersMapper.updateUser(userVO); if (userResult != 1) throw new
		 * RuntimeException("사용자 정보 수정 실패"); * int profResult =
		 * mapper.updateProfessor(profVO); if (profResult != 1) throw new
		 * RuntimeException("교수 정보 수정 실패"); * log.info("✅ 교수 정보 수정 성공. [교번: {}]",
		 * dto.getProfessorNo());
		 */
	}

	@Override
	public List<ProfessorInfoDTO> readStaffProfessorInfoList(Map<String, Object> paramMap) {
		@SuppressWarnings("unchecked")
		PaginationInfo<ProfessorInfoDTO> pagingInfo = (PaginationInfo<ProfessorInfoDTO>) paramMap.get("pagingInfo");

		// 2. 전체 레코드 수 조회
		int totalRecords = mapper.selectProfessorCount(paramMap);

		if (pagingInfo != null) {
			// 총 레코드 수 설정
			pagingInfo.setTotalRecord(totalRecords);

			// 쿼리 파라미터로 사용할 시작/끝 행 번호 설정
			paramMap.put("startRow", pagingInfo.getStartRow());
			paramMap.put("endRow", pagingInfo.getEndRow());
		}

		// 3. 목록 조회 (Map 전체를 전달)
		List<ProfessorInfoDTO> list = mapper.selectStaffProfessorInfoList(paramMap);

		return list;
	}

	@Override
	public List<ProfessorInfoDTO> readProfessorListForStudentMapping(Map<String, Object> paramMap) {
	    // 1. Map에서 searchKeyword를 추출하고 정리 (선택적)
	    //    Map에 담겨진 값은 String 타입이 보장되므로, 널 체크 후 공백을 제거하여 맵에 다시 넣습니다.
	    String searchKeyword = (String) paramMap.get("searchKeyword");
	    if (searchKeyword != null) {
	        String cleanedKeyword = searchKeyword.trim();
	        if (cleanedKeyword.isEmpty()) {
	            paramMap.put("searchKeyword", null);
	        } else {
	            paramMap.put("searchKeyword", cleanedKeyword);
	        }
	    }

	    // 2. Map에서 deptCd도 추출하여 정리 (선택적)
	    String deptCd = (String) paramMap.get("deptCd");
	    if (deptCd != null && deptCd.trim().isEmpty()) {
	        paramMap.put("deptCd", null);
	    }

	    // 3. 맵 전체를 Mapper로 전달하여 교수 목록 조회
	    //    Mapper는 이 Map을 이용해 동적 쿼리(학과 필터링, 검색 키워드)를 실행합니다.
	    return mapper.selectProfessorListForSearch(paramMap);
	}


}