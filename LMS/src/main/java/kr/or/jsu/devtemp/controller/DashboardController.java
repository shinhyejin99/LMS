package kr.or.jsu.devtemp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.professor.service.info.ProfInfoService;
import kr.or.jsu.lms.staff.service.mypage.StaffMyPageService;
import kr.or.jsu.lms.staff.service.professor.StaffProfessorInfoService;
import kr.or.jsu.lms.staff.service.student.StaffStudentInfoService;
import kr.or.jsu.lms.staff.service.userreserve.StaffUserReserveService;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.portal.service.certificate.PortalCertificateService;
import kr.or.jsu.portal.service.facility.PortalFacilityService;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.portal.service.notice.PortalNoticeService;
import kr.or.jsu.vo.CertificateReqVO;
import kr.or.jsu.vo.PortalNoticeVO;
import kr.or.jsu.vo.PortalRecruitVO;
import lombok.RequiredArgsConstructor;

/**
 * 대시보드 전용 컨트롤러
 *
 * 사용자(학생, 교수, 교직원, 포털 사용자)의 권한별로 맞춤형 대시보드 화면 데이터를 구성하여 반환합니다.
 *
 * @author 정태일
 * @since 2025. 10. 20.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	정태일	          최초 생성
 *  2025. 10. 20.     	정태일	          채용정보 하드코딩 수정
 *  2025. 10. 24.		김수현			학생 대시보드 분리 - 기존에 있던 학생 대시보드 메서드는 삭제
 *  2025. 10. 27 		신혜진			교직원 대시보드 수정
 *
 *      </pre>
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final StuInfoService stuInfoService;
	private final ProfInfoService profInfoService;
	private final StaffMyPageService staffMyPageService;
	private final PortalNoticeService portalNoticeService;
	private final PortalCertificateService portalCertificateService;
	private final PortalJobService portalJobService;
	private final StaffStudentInfoService staffStudentInfoService;
	private final StaffProfessorInfoService staffProfessorInfoService;
	private final StaffUserReserveService userReserveService;
	private final PortalFacilityService portalFacilityService;


	/**
	 * 교수용 대시보드 페이지 구성 - 교수 기본정보 - 지도 학생 현황 (전체 / 휴학 / 자퇴) - 일반공지 및 학사공지 최신 3건 - 최근
	 * 확정된 강의 5건
	 *
	 * @param model     Spring MVC 모델 객체
	 * @param loginUser 로그인 사용자 정보
	 * @return dashboard/professordashboard.jsp
	 */
	@GetMapping("/professor")
	public String profPage(Model model, @AuthenticationPrincipal CustomUserDetails loginUser) {
		// 1. 교수 정보 조회
		String professorNo = loginUser.getUsername();
		Object userInfo = profInfoService.getProfessorInfoView(professorNo);
		model.addAttribute("userInfo", userInfo);

		// 2. 담당 학생 현황
		int totalStudentsCount = profInfoService.getTotalAdvisedStudentsCount(professorNo);
		int leaveOfAbsenceStudentsCount = profInfoService.getLeaveOfAbsenceAdvisedStudentsCount(professorNo);
		int withdrawalStudentsCount = profInfoService.getWithdrawalAdvisedStudentsCount(professorNo);
		model.addAttribute("totalStudentsCount", totalStudentsCount);
		model.addAttribute("leaveOfAbsenceStudentsCount", leaveOfAbsenceStudentsCount);
		model.addAttribute("withdrawalStudentsCount", withdrawalStudentsCount);

		// 3. 일반공지 최신 3건
		PaginationInfo<PortalNoticeVO> generalPaging = new PaginationInfo<>();
		generalPaging.setCurrentPage(1);
		generalPaging.setScreenSize(3);
		generalPaging.setNoticeTypeCd("GENERAL");
		List<PortalNoticeVO> generalNotices = portalNoticeService.readPortalNoticeList(generalPaging);
		model.addAttribute("generalNotices", generalNotices);

		// 4. 학사공지 최신 3건
		PaginationInfo<PortalNoticeVO> academicPaging = new PaginationInfo<>();
		academicPaging.setCurrentPage(1);
		academicPaging.setScreenSize(3); // Added this line
		academicPaging.setNoticeTypeCd("ACADEMIC"); // Added this line
		List<PortalNoticeVO> academicNotices = portalNoticeService.readPortalNoticeList(academicPaging);
		model.addAttribute("academicNotices", academicNotices);

		// 5. 최근 확정 강의 5건
		List<kr.or.jsu.vo.LectureVO> recentLectures = profInfoService.getRecentConfirmedLectures(professorNo);
		model.addAttribute("recentLectures", recentLectures);

		return "dashboard/professordashboard";
	}

	/**
	 * 교직원용 대시보드 페이지 반환
	 *
	 * @return dashboard/staffdashboard.jsp
	 */
	@GetMapping("/staff")
	public String staffPage(Model model) {

		// ******************************************************************
		// 1. 대시보드 통계 데이터 (총 학생수, 교원수, 비율, 업무 건수)
		// ******************************************************************

		// 1-1. 2열 통계 카드 (총 학생 수, 총 교원 수)
		Map<String, Long> statusCounts = staffStudentInfoService.readStudentStatusCounts();
		long totalStudents = statusCounts.values().stream().mapToLong(Long::longValue).sum();
		Map<String, Integer> profStatusCounts = staffProfessorInfoService.readEmploymentStatusCounts();
		int totalFaculty = profStatusCounts.values().stream().mapToInt(Integer::intValue).sum();

		model.addAttribute("totalStudents", totalStudents);
		model.addAttribute("totalFaculty", totalFaculty);

		// 1-2. 3열 통계 카드 (시설 예약률, 등록금 납부율)

		// 시설 예약률
		int totalSlots = userReserveService.readTotalReservableSlots(); // 총 예약 가능 슬롯
		int confirmedSlots = userReserveService.readConfirmedReservationSlots(); // 확정된 예약 슬롯
		int facilityReservationRate = 0;

		if (totalSlots > 0) {
			// 예약률 = (확정된 예약 슬롯 / 총 예약 가능 슬롯) * 100, 반올림하여 정수 처리
			facilityReservationRate = (int) Math.round((double) confirmedSlots / totalSlots * 100);
		}
		// ------------------------------------



		model.addAttribute("facilityReservationRate", facilityReservationRate); // JSP 변수에 값 전달



		// 2-1. 일반공지 최신 8건
		PaginationInfo<PortalNoticeVO> generalPaging = new PaginationInfo<>();
		generalPaging.setCurrentPage(1);
		generalPaging.setScreenSize(5);
		generalPaging.setNoticeTypeCd("GENERAL");
		List<PortalNoticeVO> generalNotices = portalNoticeService.readPortalNoticeList(generalPaging);
		model.addAttribute("generalNotices", generalNotices);

		// 2-2. 학사공지 최신 8건
		PaginationInfo<PortalNoticeVO> academicPaging = new PaginationInfo<>();
		academicPaging.setCurrentPage(1);
		academicPaging.setScreenSize(5);
		academicPaging.setNoticeTypeCd("ACADEMIC");
		List<PortalNoticeVO> academicNotices = portalNoticeService.readPortalNoticeList(academicPaging);
		model.addAttribute("academicNotices", academicNotices);

		// 2-3. 채용정보 최신 5건
		PaginationInfo<PortalRecruitVO> jobPaging = new PaginationInfo<>();
		jobPaging.setCurrentPage(1);
		jobPaging.setScreenSize(7);

		List<PortalRecruitVO> jobNotices = portalJobService.readSchRecruitList(jobPaging);
		model.addAttribute("jobNotices", jobNotices);

		return "dashboard/staffdashboard";
	}


	/**
	 * 포털 공용 대시보드 페이지 구성
	 *
	 * - 사용자 역할별 기본 정보 조회 (학생 / 교수 / 교직원 / 관리자) - 공지사항(일반, 학사) 최신 목록 - 증명서 발급 요청 목록
	 * - 채용정보 목록 (학생은 학과 맞춤형, 그 외는 전체)
	 *
	 * @param model     Spring MVC 모델 객체
	 * @param loginUser 로그인 사용자 정보
	 * @return dashboard/portaldashboard.jsp
	 */
	@GetMapping("/portal")
	public String portalPage(Model model, @AuthenticationPrincipal CustomUserDetails loginUser) {

		String userId = loginUser.getRealUser().getUserId(); // 사용자 ID
		String userNo = loginUser.getUsername(); // 사용자 고유번호
		String role = "";

		// 1. 사용자 권한 추출 (첫 번째 권한만 사용)
		for (GrantedAuthority authority : loginUser.getAuthorities()) {
			role = authority.getAuthority();
			break;
		}
		// --- 사용자 정보 조회 ---
		Object userInfo = null;

		// 2. 역할별 사용자 정보 및 채용 공고 조회
		if ("ROLE_STUDENT".equals(role)) {
			userInfo = stuInfoService.readStuMyInfo(userNo);

		} else if ("ROLE_PROFESSOR".equals(role)) {
			userInfo = profInfoService.getProfessorInfoView(userNo);
		} else if ("ROLE_STAFF".equals(role) || "ROLE_ADMIN".equals(role)) {
			userInfo = staffMyPageService.readStaffDetail(userNo);
		}
		model.addAttribute("userInfo", userInfo);

		if ("ROLE_STUDENT".equals(role)) {
			StudentDetailDTO studentInfo = (StudentDetailDTO) userInfo;
			String univDeptCd = studentInfo.getUnivDeptCd();

			PaginationInfo<PortalRecruitVO> jobPaging = new PaginationInfo<>();
			jobPaging.setCurrentPage(1);
			jobPaging.setScreenSize(5);

			// 학과 코드 기반 맞춤형 채용 목록
			model.addAttribute("jobNotices", portalJobService.readStudentRecruitList(jobPaging, univDeptCd));
		} else {
			// 교수 및 교직원의 경우, 일반 학내 채용 목록을 조회
			PaginationInfo<PortalRecruitVO> jobPaging = new PaginationInfo<>();
			jobPaging.setCurrentPage(1);
			jobPaging.setScreenSize(5);

			// 일반 학내 채용 목록
			model.addAttribute("jobNotices", portalJobService.readSchRecruitList(jobPaging));
		}
		model.addAttribute("userInfo", userInfo);

		// 3. 공지사항(일반 / 학사) 최신 목록
		PaginationInfo<PortalNoticeVO> generalPaging = new PaginationInfo<>();
		generalPaging.setCurrentPage(1);
		generalPaging.setScreenSize(5);
		generalPaging.setNoticeTypeCd("GENERAL");
		List<PortalNoticeVO> generalNotices = portalNoticeService.readPortalNoticeList(generalPaging);
		model.addAttribute("generalNotices", generalNotices);

		PaginationInfo<PortalNoticeVO> academicPaging = new PaginationInfo<>();
		academicPaging.setCurrentPage(1);
		academicPaging.setScreenSize(5);
		academicPaging.setNoticeTypeCd("ACADEMIC");
		List<PortalNoticeVO> academicNotices = portalNoticeService.readPortalNoticeList(academicPaging);
		model.addAttribute("academicNotices", academicNotices);

		// 4. 증명서 발급 요청 목록
		List<CertificateReqVO> certList = portalCertificateService.getCertificateReqList(userId);
		model.addAttribute("certList", certList);

		// 5. 나의 시설 예약 목록
		model.addAttribute("myReservations", portalFacilityService.retrieveMyReservations(userId));
//		// 5. 긴급 공지 목록
//		List<PortalNoticeVO> urgentNotices = portalNoticeService.readUrgentNoticeList();
//		model.addAttribute("urgentNotices", urgentNotices);

		return "dashboard/portaldashboard";
	}
}
