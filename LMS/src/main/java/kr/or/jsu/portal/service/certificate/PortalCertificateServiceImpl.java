package kr.or.jsu.portal.service.certificate;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.utils.pdf.PdfService;
import kr.or.jsu.dto.CertificateProfessorDetailDTO;
import kr.or.jsu.dto.CertificateStaffDetailDTO;
import kr.or.jsu.dto.CertificateStudentDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.mybatis.mapper.CertificateDetailMapper;
import kr.or.jsu.mybatis.mapper.CertificateItemMapper;
import kr.or.jsu.mybatis.mapper.CertificateProfessorMapper;
import kr.or.jsu.mybatis.mapper.CertificateStaffMapper;
import kr.or.jsu.mybatis.mapper.CertificateStudentMapper;
import kr.or.jsu.mybatis.mapper.PortalCertificateMapper;
import kr.or.jsu.portal.controller.certificate.PortalCertificateConfig;
import kr.or.jsu.portal.controller.certificate.PortalCertificateType;
import kr.or.jsu.vo.CertificateDetailVO;
import kr.or.jsu.vo.CertificateItemVO;
import kr.or.jsu.vo.CertificateReqVO;
import kr.or.jsu.vo.CertificateVO;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *  2025. 10.23.     	정태일	          증명서 누락 부분 수정
 *  2025. 10.24.     	정태일	          pdf 라이브러리 변경
 *
 *      </pre>
 */
@Service
@RequiredArgsConstructor
public class PortalCertificateServiceImpl implements PortalCertificateService {

	private final PortalCertificateMapper certMapper;
	private final CertificateProfessorMapper certificateProfessorMapper;
	private final CertificateStaffMapper certificateStaffMapper;
	private final CertificateStudentMapper certificateStudentMapper;
	private final CertificateItemMapper certItemMapper;
	private final CertificateDetailMapper certDetailMapper;
	private final PdfService pdfService;
	private final ResourceLoader resourceLoader;
	private final UserNotificationCreateService notificationService;

	/**
	 * 자동 알림 전송 헬퍼 메서드
	 *
	 * @param userId          수신자 ID
	 * @param certificateName 증명서 이름
	 */
	private void sendCertificateIssueNotification(String userId, String certificateName) {

		AutoNotificationRequest alert = AutoNotificationRequest
				.builder()
				.receiverId(userId) // 알림을 받을 사용자 ID
				.title("✅ " + certificateName + " 발급 완료") // 제목에 증명서 이름 포함
				.content(certificateName + " 발급이 완료되었습니다. 지금 확인해 보세요.")
				.senderName("LMS 행정처") // ⭐⭐⭐ 이 부분을 "행정처"로 수정합니다. ⭐⭐⭐
				.pushUrl("/lms/portal/certificate/history") // 알림 클릭 시 이동할 URL
				.build();
		// 통합된 자동 알림 메서드 호출
		notificationService.sendAutoNotification(alert);
	}

	/**
	 * 사용자의 증명서 요청 목록 조회
	 * dday 추가
	 */
	@Override
	public List<CertificateReqVO> getCertificateReqList(String userId) {
		List<CertificateReqVO> certList = certMapper.selectCertificateReqList(userId);
		LocalDateTime now = LocalDateTime.now();

		for (CertificateReqVO cert : certList) {
			if (cert.getExpireAt() != null) {
				long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(now.toLocalDate(), cert.getExpireAt().toLocalDate());

				if (daysBetween == 0) {
					cert.setdDayText("D-Day");
				} else if (daysBetween > 0) {
					cert.setdDayText("D-" + daysBetween);
				} else {
					cert.setdDayText("만료됨");
				}
			} else {
				cert.setdDayText("-");
			}
		}
		return certList;
	}

	/**
	 * 증명서 요청 생성 1. 요청 ID 생성 2. 요청 정보 저장 3. 사용자 정보 기반 상세항목 데이터 생성 후 저장
	 */
	@Override
	@Transactional
	public void createCertificateRequest(CertificateReqVO reqVO) {
		// reqVO.setCertReqId(newCertReqId); // Mapper의 selectKey를 통해 자동 생성

		// requestAt, expireAt, statusCd가 설정되지 않았을 경우 기본값 설정
		if (reqVO.getRequestAt() == null) {
			reqVO.setRequestAt(LocalDateTime.now());
		}
		if (reqVO.getExpireAt() == null) {
			reqVO.setExpireAt(reqVO.getRequestAt().plusMonths(1));
		}
		if (reqVO.getStatusCd() == null || reqVO.getStatusCd().isEmpty()) {
			// 승인기능 구현하면 대기중
//		    reqVO.setStatusCd("PENDING");
			// 승인없이 바로 발급완료
			reqVO.setStatusCd("ISSUED");
		}

		certMapper.insertCertificateReq(reqVO);

		// 상세 항목 데이터 수집 및 저장
		Map<String, Object> detailsMap = new HashMap<>();
		populateDetailData(reqVO.getUserId(), detailsMap);

		List<CertificateItemVO> items = certItemMapper.selectCertificateItemListByCertCd(reqVO.getCertificateCd());
		for (CertificateItemVO item : items) {
			CertificateDetailVO detailVO = new CertificateDetailVO();
			detailVO.setCertReqId(reqVO.getCertReqId());
			detailVO.setCertificateCd(reqVO.getCertificateCd());
			detailVO.setItem(item.getItem());
//            detailVO.setValue(detailsMap.getOrDefault(item.getItem(), "정보 없음"));
			Object valueFromMap = detailsMap.get(item.getItem());
			if (valueFromMap == null || valueFromMap.toString().isEmpty()) {
				detailVO.setValue("정보 없음");
			} else {
				detailVO.setValue(valueFromMap.toString());
			}
			certDetailMapper.insertCertificateDetail(detailVO);
		}
		// 자동알림 처리 코드
		PortalCertificateType certType = PortalCertificateType.fromCode(reqVO.getCertificateCd());
		String certificateName = (certType != null) ? certType.getDisplayName() : "증명서";

		// 알림 전송 (가장 마지막에 실행)
		sendCertificateIssueNotification(reqVO.getUserId(), certificateName);
	}

	    private String getImageAsBase64(String imagePath) throws Exception {

	        try {

	            Resource resource = resourceLoader.getResource(imagePath);
	            InputStream inputStream = resource.getInputStream();
	            byte[] imageBytes = inputStream.readAllBytes();
	            String base64 = Base64.getEncoder().encodeToString(imageBytes);

	            return "data:image/png;base64," + base64;

	        } catch (Exception e) {
	            System.err.println("이미지 로드 실패: " + e.getMessage());
	            return ""; // 이미지 없으면 빈 문자열
	        }
	    }

		/**
		 * 증명서 미리보기(PDF) 생성
		 */
		    @Override
		    public byte[] generateCertificatePreview(String userId, String certificateCd) throws Exception {

		        CertificateVO certVO = certMapper.selectCertificateByCd(certificateCd);
		        if (certVO == null) {
		            throw new Exception("증명서 정보를 찾을 수 없습니다.");
		        }
		        String templatePath = certVO.getTemplatePath();
		        if (templatePath.startsWith("templates/")) {
		            templatePath = templatePath.substring("templates/".length());
		        }

				Map<String, Object> data = new HashMap<>();

				data.put("certificateName", certVO.getCertificateName());
				data.put("requestAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
				data.put("bodyClass", "preview-mode"); // 미리보기 클래스 추가
				data.put("logoImage", getImageAsBase64("classpath:static/images/JSU대학교로고.png"));
				data.put("stampImage", getImageAsBase64("classpath:static/images/JSU대학교인장.png"));

		        populateDetailData(userId, data);

		        return pdfService.generatePdf(templatePath, data);
		    }

		    /**
		     * 실제 발급된 증명서 PDF 가져오기
		     */
		    @Override
		    public byte[] getCertificatePdf(String certReqId) throws Exception {
		        CertificateReqVO certReqVO = certMapper.selectCertificateReqById(certReqId);
		        if (certReqVO == null) {
		            throw new Exception("증명서 요청 정보를 찾을 수 없습니다.");
		        }
		        String certificateCd = certReqVO.getCertificateCd();
		        String userId = certReqVO.getUserId();
		        CertificateVO certVO = certMapper.selectCertificateByCd(certificateCd);

		        if (certVO == null) {
		            throw new Exception("증명서 정보를 찾을 수 없습니다.");
		        }
		        String templatePath = certVO.getTemplatePath();

		        if (templatePath.startsWith("templates/")) {
		            templatePath = templatePath.substring("templates/".length());
		        }

				Map<String, Object> data = new HashMap<>();

				data.put("certificateName", certVO.getCertificateName());
				data.put("requestAt", certReqVO.getRequestAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
		        data.put("certReqId", certReqVO.getCertReqId());
				data.put("bodyClass", ""); // 클래스 없음
				data.put("logoImage", getImageAsBase64("classpath:static/images/JSU대학교로고.png"));
				data.put("stampImage", getImageAsBase64("classpath:static/images/JSU대학교인장.png"));

				populateDetailData(userId, data);

		        return pdfService.generatePdf(templatePath, data);

		    }
	/**
	 * 현재 사용자 권한(Role)에 따라 조회 가능한 증명서 목록 반환
	 */
	@Override
	public List<CertificateVO> getAvailableCertificates() {
		String userRoleString = getCurrentUserSpringSecurityRole();
		if (userRoleString == null) {
			return Collections.emptyList();
		}
		List<CertificateVO> allCertificates = certMapper.selectAllCertificates();
		Set<String> allowedScopes = PortalCertificateConfig.ROLE_STRING_TO_SHARE_SCOPES.getOrDefault(userRoleString,
				Collections.emptySet());
		return allCertificates.stream().filter(cert -> allowedScopes.contains(cert.getShareScopeCd()))
				.collect(Collectors.toList());
	}

	/**
	 * 현재 로그인한 사용자의 ROLE 정보를 반환
	 */
	private String getCurrentUserSpringSecurityRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.filter(authority -> authority.startsWith("ROLE_")).findFirst().orElse(null);
	}


	/**
	 * 사용자 ID로 해당하는 학생/교수/직원의 상세 정보 조회 후 Map에 데이터 추가
	 */
	private void populateDetailData(String userId, Map<String, Object> data) {
		String userType = certMapper.selectUserTypeByUserId(userId);
		switch (userType) {
		case "STUDENT":
			CertificateStudentDetailDTO studentInfo = certificateStudentMapper
					.selectStudentCertificateDetailInfo(userId);
			if (studentInfo != null) {
				data.put("lastName", studentInfo.getLastName());
				data.put("firstName", studentInfo.getFirstName());
				data.put("studentNo", studentInfo.getStudentNo());
				data.put("univDeptName", studentInfo.getUnivDeptName());
				data.put("statusName", studentInfo.getStuStatusName());
				if (studentInfo.getCreateAt() != null) {
					data.put("entryDate",
							studentInfo.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
				}
				if (studentInfo.getGraduationDate() != null) {
					data.put("graduationDate",
							studentInfo.getGraduationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
				} else {
					data.put("graduationDate", "");
				}
				if (studentInfo.getRegiNo() != null && studentInfo.getRegiNo().length() >= 7) {
					data.put("formattedRegiNo", studentInfo.getRegiNo().substring(0, 6) + "-"
							+ studentInfo.getRegiNo().substring(6, 7) + "******");
				} else {
					data.put("formattedRegiNo", "");
				}
			}
			break;
		case "PROFESSOR":
			CertificateProfessorDetailDTO professorInfo = certificateProfessorMapper.selectProfessorDetailInfo(userId);
			if (professorInfo != null) {
				data.put("lastName", professorInfo.getLastName());
				data.put("firstName", professorInfo.getFirstName());
				data.put("employeeId", professorInfo.getProfessorNo());
				data.put("departmentName", professorInfo.getUnivDeptName());
//                    data.put("position", professorInfo.getPrfPositName());
				data.put("position", professorInfo.getCareerName());
				data.put("statusName", professorInfo.getPrfStatusName());
				if (professorInfo.getCreateAt() != null) {
					data.put("hireDate",
							professorInfo.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
				}
				if (professorInfo.getRegiNo() != null && professorInfo.getRegiNo().length() >= 8) {
//                        data.put("formattedRegiNo", professorInfo.getRegiNo().substring(0, 8) + "******");
					data.put("formattedRegiNo", professorInfo.getRegiNo().substring(0, 6) + "-"
							+ professorInfo.getRegiNo().substring(6, 7) + "******");
				} else {
					data.put("formattedRegiNo", "");
				}
			}
			break;
		case "STAFF":
			CertificateStaffDetailDTO staffInfo = certificateStaffMapper.selectStaffDetailInfo(userId);
			if (staffInfo != null) {
				data.put("lastName", staffInfo.getLastName());
				data.put("firstName", staffInfo.getFirstName());
				data.put("employeeId", staffInfo.getStaffNo());
				data.put("departmentName", staffInfo.getStfDeptName());
				data.put("position", "교직원");
//                    data.put("position", staffInfo.getStfDeptName());
				data.put("statusName", "재직중");
				if (staffInfo.getCreateAt() != null) {
					data.put("hireDate", staffInfo.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
				}
				if (staffInfo.getRegiNo() != null && staffInfo.getRegiNo().length() >= 8) {
//                        data.put("formattedRegiNo", staffInfo.getRegiNo().substring(0, 8) + "******");
					data.put("formattedRegiNo", staffInfo.getRegiNo().substring(0, 6) + "-"
							+ staffInfo.getRegiNo().substring(6, 7) + "******");
				} else {
					data.put("formattedRegiNo", "");
				}
			}
			break;
		default:
			break;
		}
	}
}
