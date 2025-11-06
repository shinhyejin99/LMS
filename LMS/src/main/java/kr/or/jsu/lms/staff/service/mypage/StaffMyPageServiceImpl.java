package kr.or.jsu.lms.staff.service.mypage;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile; // 파일 처리를 위해 필요

import kr.or.jsu.core.utils.enums.FileUploadDirectory; // 파일 경로 ENUM
import kr.or.jsu.devtemp.service.FilesUploadService; // 파일 서비스 DI
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.mybatis.mapper.AddressMapper;
import kr.or.jsu.mybatis.mapper.StaffMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffMyPageServiceImpl implements StaffMyPageService {

	private final UsersMapper usersMapper;
	private final StaffMapper mapper;
	private final AddressMapper addressMapper;
    private final FilesUploadService filesUploadService; // ⭐️ 파일 서비스 주입

	@Override
	public UserStaffDTO readStaffDetail(String staffNo) throws RuntimeException {
		UserStaffDTO userStaffDTO = mapper.selectStaffInfo(staffNo);

		if (userStaffDTO == null) {
			log.warn("교직원 정보가 존재하지 않음. staffNo={}", staffNo);
			throw new RuntimeException("해당 교직원 정보를 찾을 수 없습니다. staffNo=" + staffNo);
		}

		String regiNoHyphen = userStaffDTO.getUserInfo().getRegiNo();
		if(regiNoHyphen != null && regiNoHyphen.length() >= 7) {
			String regiNo = regiNoHyphen.replace("-", "").trim();

			if(regiNo.length() >= 7) {
				String genderCode = regiNo.substring(6, 7);
				userStaffDTO.setGender(genderCode);
			}
		}
		return userStaffDTO;
	}


	@Override
	@Transactional
	public void modifyMyStaffInfo(UserStaffDTO userStaffDTO, String staffNo) {

		try {
			// 1. 기존 교직원 정보 조회 및 원본 객체 확보
			UserStaffDTO staffInfo = mapper.selectStaffInfo(staffNo);

			if (staffInfo == null) {
	            log.error("교직원 정보 수정 실패: staffNo={} 에 해당하는 정보를 찾을 수 없습니다.", staffNo);
	            throw new RuntimeException("해당 교직원 정보를 찾을 수 없습니다. 수정 작업을 진행할 수 없습니다.");
	        }

            // 폼에서 넘어온 객체 추출
            AddressVO address = userStaffDTO.getAddressInfo();
            UsersVO user = userStaffDTO.getUserInfo();

            // ⭐️⭐️ 오류 수정 지점: DTO의 photoFile 필드에서 MultipartFile을 직접 가져옵니다. ⭐️⭐️
            MultipartFile photoFile = userStaffDTO.getPhotoFile();


			// DB 원본에서 ID 및 기존 PHOTO_ID 추출
			AddressVO originAddress = staffInfo.getAddressInfo();
	        UsersVO originUser = staffInfo.getUserInfo();
	        String oldPhotoId = originUser.getPhotoId(); // 기존 파일 ID


	        // ⭐️⭐️ 폼 객체에 DB 원본 ID를 설정 (MyBatis 업데이트 조건) ⭐️⭐️
	        if (!StringUtils.hasText(address.getAddrId())) {
	            address.setAddrId(originAddress.getAddrId());
	        }
	        if (!StringUtils.hasText(user.getUserId())) {
	            user.setUserId(originUser.getUserId());
	        }

            // ----------------------------------------------------------------------
	        // 2. 파일 업로드 및 PHOTO_ID 업데이트 로직 ⭐️
            if (photoFile != null && !photoFile.isEmpty()) {

                // 기존 파일이 있다면 사용 여부를 'N'으로 변경하여 논리적 삭제 처리
                if (StringUtils.hasText(oldPhotoId)) {
                    filesUploadService.modifyFileUsingYn(oldPhotoId);
                    log.info("DEBUG: 기존 프로필 사진(ID:{}) 사용 종료 처리 완료.", oldPhotoId);
                }

                // ⭐️⭐️ 기존 FilesUploadService 메서드 활용 ⭐️⭐️
                String uploaderUserId = originUser.getUserId();
                String newPhotoId = null;

                // A. 단일 파일을 List로 래핑하여 디스크에 저장하고 메타데이터를 받습니다.
                List<FileDetailVO> fileDetails = filesUploadService.saveAtDirectory(
                    List.of(photoFile),
                    FileUploadDirectory.IDPHOTO // 프로필 사진 경로 지정
                );

                if (!fileDetails.isEmpty()) {
                    // B. 메타데이터를 DB에 저장하고, 새로운 FileId를 받습니다. (프로필 사진은 공개 'Y')
                    newPhotoId = filesUploadService.saveAtDB(
                        fileDetails,
                        uploaderUserId,
                        true // isPublic = true
                    );
                }

                // 새로운 PHOTO_ID를 user 객체에 설정 (DB 업데이트 대상)
                if (StringUtils.hasText(newPhotoId)) {
                    user.setPhotoId(newPhotoId);
                    log.info("DEBUG: 새로운 프로필 사진 업로드 완료, New PhotoId: {}", newPhotoId);
                } else {
                    log.error("프로필 사진 저장 실패: FileId를 확보하지 못했습니다.");
                    // 실패 시 기존 ID 유지 또는 null 처리 (여기서는 기존 ID 유지를 위해 oldPhotoId 재설정)
                    user.setPhotoId(oldPhotoId);
                }
            } else {
                // 새 파일이 없으면 기존 PHOTO_ID 유지
                user.setPhotoId(oldPhotoId);
            }
            // ----------------------------------------------------------------------

			// 3. 사용자 정보 업데이트 (UsersMapper 사용)
			// 🚨 MobileNo에서 모든 공백 제거
			String mobileNo = user.getMobileNo();
	        if (StringUtils.hasText(mobileNo)) {
	            String cleanMobileNo = mobileNo.replaceAll("\\s", "");
	            user.setMobileNo(cleanMobileNo);
	            log.debug("DEBUG: MobileNo 공백 제거 후 - {}", cleanMobileNo);
	        } else {
	            user.setMobileNo(null);
	        }

			int result2 = usersMapper.updateUser(user);
			log.debug("DEBUG: User update result: {}", result2);

			// 4. 주소 정보 업데이트 (AddressMapper 사용)
			int result1 = addressMapper.updateAddress(address);
			log.debug("DEBUG: Address update result: {}", result1);


	        // 5. 결과 확인
	        if (result1 < 0 || result2 < 0) {
	             log.error("교직원 정보 수정 중 오류 발생. Address update result: {}, User update result: {}", result1, result2);
	             throw new RuntimeException("교직원 정보 수정 작업 실패");
	        }

        } catch (Exception e) {
            log.error("정보 수정 중 예상치 못한 오류 발생", e);
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("정보 업데이트 중 예상치 못한 오류 발생", e);
        }
	}
}