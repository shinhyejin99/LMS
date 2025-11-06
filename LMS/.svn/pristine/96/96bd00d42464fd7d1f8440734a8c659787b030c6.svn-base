package kr.or.jsu.lms.student.service.academicChange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.dto.AffilApplyResponseDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import kr.or.jsu.dto.UnifiedApplyResponseDTO;
import kr.or.jsu.mybatis.mapper.RecordApplyMapper;
import kr.or.jsu.mybatis.mapper.UnivAffilApplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학적변동 신청 현황 조회 서비스 구현체
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 10. 13.		김수현			신청 현황 조회 서비스 구현
 *										파일 처리 추가
 *	2025. 10. 15		김수현			전체 신청 현황 처리 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StuRecordStatusServiceImpl implements StuRecordStatusService {

	private final RecordApplyMapper recordApplyMapper;
	private final UnivAffilApplyMapper affilMapper;
    private final LMSFilesService fileService;

	/**
	 * 학생의 학적변동 신청 목록 조회
	 */
	@Override
	public List<RecordApplyResponseDTO> getApplyList(String studentNo) {
		log.debug("신청 목록 조회 - studentNo: {}", studentNo);
        return recordApplyMapper.selectApplyListByStudent(studentNo);
	}

	/**
	 * 신청 상세 조회
	 */
	@Override
	public RecordApplyResponseDTO getApplyDetail(String applyId) {
		RecordApplyResponseDTO response = recordApplyMapper.selectApplyDetail(applyId);

        if (response == null) {
            throw new RuntimeException("존재하지 않는 신청입니다.");
        }

        // 첨부파일 정보
        String fileId = response.getAttachFileId();
        if (fileId != null) {
            try {
                FilesBundleDTO bundle = fileService.readFileBundle(fileId);

                // 목록 응답 DTO로 변환
                List<FileDetailResp> files = bundle.getFileDetailInfo().stream()
                    .map(f -> {
                         FileDetailResp resp = new FileDetailResp();
                         BeanUtils.copyProperties(f, resp); // DTO 복사
                         return resp;
                    }).toList();

                response.setAttachFiles(files);

            } catch (RuntimeException e) { // readFileBundle 내부의 "존재하지 않는 파일ID" 예외 처리
                log.warn("첨부파일 조회 실패 - fileId: {}", fileId, e);
                response.setAttachFiles(null);
            }
        }

        log.debug("신청 상세 조회 완료 - applyId: {}", applyId);

        return response;
	}

	/**
	 * 학생의 소속변경 신청 목록 조회
	 */
	@Override
	public List<AffilApplyResponseDTO> getAffilList(String studentNo) {
		return affilMapper.selectApplyListByStudent(studentNo);
	}

	/**
	 * 소속변경 신청 상세조회
	 */
	@Override
	public AffilApplyResponseDTO getAffilDetail(String applyId) {
		AffilApplyResponseDTO response = affilMapper.selectApplyDetail(applyId);
		if (response == null) {
            throw new RuntimeException("존재하지 않는 신청입니다.");
        }

        // 첨부파일 정보
        String fileId = response.getAttachFileId();
        if (fileId != null) {
            try {
                FilesBundleDTO bundle = fileService.readFileBundle(fileId);

                // 목록 응답 DTO로 변환
                List<FileDetailResp> files = bundle.getFileDetailInfo().stream()
                    .map(f -> {
                         FileDetailResp resp = new FileDetailResp();
                         BeanUtils.copyProperties(f, resp); // DTO 복사
                         return resp;
                    }).toList();

                response.setAttachFiles(files);

            } catch (RuntimeException e) { // readFileBundle 내부의 "존재하지 않는 파일ID" 예외 처리
                log.warn("첨부파일 조회 실패 - fileId: {}", fileId, e);
                response.setAttachFiles(null);
            }
        }

        log.debug("신청 상세 조회 완료 - applyId: {}", applyId);

        return response;
	}

	/**
	 * 신청 상세보기에서 첨부파일 다운로드 처리
	 */
	@Override
	public FileDetailInfo getAppliedFile(String applyId, int fileOrder, String studentNo) {
		// 1. 권한 확인 (학적변동 신청서의 학생과 요청자가 일치하는지)
		RecordApplyResponseDTO apply = getApplyDetail(applyId);
	    if (!apply.getStudentNo().equals(studentNo)) {
	        log.warn("권한 없는 파일 다운로드 시도 - applyId: {}, requestStudent: {}", applyId, studentNo);
	        throw new RuntimeException("파일 다운로드 권한이 없습니다.");
	    }

	    // 2. 파일 ID 가져오기
	    String fileId = apply.getAttachFileId();
	    if (fileId == null) {
	        throw new RuntimeException("첨부파일이 없습니다.");
	    }

	    // 3. 파일 메타데이터 조회
	    FilesBundleDTO fileMetadata = fileService.readFileBundle(fileId);

	    // 4. 요청된 순서의 파일 상세 정보 획득
	    FileDetailInfo targetFile = fileMetadata.getFileDetailInfo().stream()
	        .filter(f -> f.getFileOrder() == fileOrder)
	        .findFirst()
	        .orElseThrow(() -> new RuntimeException("존재하지 않는 파일입니다."));

	    // 5. DB 정보가 채워진 targetFile을 반환
	    return targetFile;
	}

	/**
	 * 전체 조회(재학변경+소속변경)
	 */
	@Override
	public Map<String, Object> getAllApplications(String studentNo) {
		Map<String, Object> result = new HashMap<>();

	    // 1. 재학상태변경 목록
	    List<RecordApplyResponseDTO> recordList = recordApplyMapper.selectApplyListByStudent(studentNo);

	    // 2. 소속변경 목록
	    List<AffilApplyResponseDTO> affilList = affilMapper.selectApplyListByStudent(studentNo);

	    result.put("recordList", recordList);
	    result.put("affilList", affilList);

	    log.debug("전체 신청 현황 조회 - studentNo: {}, record: {}, affil: {}",
	        studentNo, recordList.size(), affilList.size());

	    return result;
	}

	/**
	 * 전체 현황목록 조회 하기 위함 (재학변경+소속변경)
	 */
	@Override
	public List<UnifiedApplyResponseDTO> getUnifiedApplyList(String studentNo) {
		// 1. 기존의 통합 데이터 조회 메서드를 호출
        Map<String, Object> allApplications = this.getAllApplications(studentNo);

        @SuppressWarnings("unchecked")
        List<RecordApplyResponseDTO> recordList = (List<RecordApplyResponseDTO>) allApplications.get("recordList");
        @SuppressWarnings("unchecked")
        List<AffilApplyResponseDTO> affilList = (List<AffilApplyResponseDTO>) allApplications.get("affilList");

        List<UnifiedApplyResponseDTO> unifiedList = new java.util.ArrayList<>();

        // 2. 재학변동 목록 매핑 및 통합
        for (RecordApplyResponseDTO record : recordList) {
            unifiedList.add(mapRecordToUnified(record));
        }

        // 3. 소속변경 목록 매핑 및 통합
        for (AffilApplyResponseDTO affil : affilList) {
            unifiedList.add(mapAffilToUnified(affil));
        }

        // 4. 최신순 정렬
        unifiedList.sort(java.util.Comparator.comparing(UnifiedApplyResponseDTO::getApplyAt,
                                                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())));

        return unifiedList;
	}

	// --- 매핑 헬퍼 메서드 (Service 내부) ---
    /**
     * RecordApplyResponseDTO -> UnifiedApplyResponseDTO 매핑
     * @param record
     * @return
     */
    private UnifiedApplyResponseDTO mapRecordToUnified(RecordApplyResponseDTO record) {
        UnifiedApplyResponseDTO unified = new UnifiedApplyResponseDTO();
        org.springframework.beans.BeanUtils.copyProperties(record, unified);

        unified.setApplyType("RECORD");
        unified.setApplyTypeName("재학상태변경");
        unified.setUnifiedChangeCd(record.getRecordChangeCd());
        unified.setUnifiedChangeName(record.getRecordChangeName());

        return unified;
    }

    /**
     * AffilApplyResponseDTO -> UnifiedApplyResponseDTO 매핑
     * @param affil
     * @return
     */
    private UnifiedApplyResponseDTO mapAffilToUnified(AffilApplyResponseDTO affil) {
        UnifiedApplyResponseDTO unified = new UnifiedApplyResponseDTO();
        org.springframework.beans.BeanUtils.copyProperties(affil, unified);

        // 1. 원본 이름 획득
        String affilName = affil.getAffilChangeName();

        // 2. MJ_TRF 코드일 경우 이름을 "전과"로 변경
        if ("MJ_TRF".equals(affil.getAffilChangeCd())) {
            affilName = "전과"; // "전과 이후"를 "전과"로 덮어씀
        }

        // 소속변경 고유 코드 및 타입 설정
        unified.setApplyType("AFFIL");
        unified.setApplyTypeName("소속변경");
        unified.setAffilChangeCd(affil.getAffilChangeCd());
        // 3. 변경된 affilName 변수를 사용하도록
        unified.setAffilChangeName(affilName);
        unified.setUnifiedChangeCd(affil.getAffilChangeCd());
        unified.setUnifiedChangeName(affilName);

        // 소속변경 고유 필드 수동 설정
        unified.setTargetDeptCd(affil.getTargetDeptCd());
        unified.setTargetDeptName(affil.getTargetDeptName());
        unified.setTargetCollegeName(affil.getTargetCollegeName());

        return unified;
    }
}