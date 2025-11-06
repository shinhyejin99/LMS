package kr.or.jsu.core.utils.enums;

/**
 * 공통코드 분류 코드 모아놓은 ENUM입니다. <br>
 *
 * @author 송태호
 * @since 2025. 9. 28.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 28.     	송태호	          최초 생성
 *      </pre>
 */
public enum CommonCodeSort {
	BANK_CODE("BANK_CODE", "은행코드", "국내 시중/지방/인터넷은행 및 유사기관 코드"),

	// Professor(교수) 테이블 관련
	PRF_STATUS_CD("PRF_STATUS_CD", "교수 재직상태코드", "교원의 현재 상태(재직, 휴직, 파견 등)"),
	PRF_APPNT_CD("PRF_APPNT_CD", "교수 임용코드", "전임/비전임의 임용 구분"),
	PRF_POSIT_CD("PRF_POSIT_CD", "교수 직책코드", "학과 내 보직(학과장/부학과장 등)"),

	// Student(학생) 및 학사 공통
	MILITARY_TYPE_CD("MILITARY_TYPE_CD", "병역유형코드", "군별·대체복무 구분 코드(2025 기준 복무기간 반영)"),
	ENTRANCE_TYPE_CD("ENTRANCE_TYPE_CD", "입학전형코드", "대학 입학전형 유형 공통코드"),
	COMPLETION_CD("COMPLETION_CD", "이수구분", "과목의 이수구분(전공/교양 등) 정의"),
	STU_STATUS_CD("STU_STATUS_CD", "재학상태코드", "학생의 현재 재학 상태"), GRADE_CD("GRADE_CD", "학년코드", "재학 학년 구분 코드"),
	TERM_CD("TERM_CD", "학기코드", "정규/계절 학기 구분 코드"),
	RECORD_CHANGE_CD("RECORD_CHANGE_CD", "학적변동코드", "입학/진급/졸업/휴학/자퇴 등 학적 상태 변동"),
	AFFIL_CHANGE_CD("AFFIL_CHANGE_CD", "소속변동코드", "학생 소속(전공/부전공 등) 변동 유형 코드"),

	// 학생 학적변동, 소속변동의 신청상태(간략 버전)
	APPLY_STATUS_CD("APPLY_STATUS_CD", "신청/접수 상태 코드", "신청, 접수, 처리, 승인/반려 등의 진행 상태를 관리하는 코드 분류입니다."),

	// 등록/장학금
	TUITION_SORT_CD("TUITION_SORT_CD", "등록금유형코드", "등록금 징수 항목에 대한 공통코드"),
	SCHOLARSHIP_CD("SCHOLARSHIP_CD", "장학금종류코드", "장학금 종류 공통코드"),

	// 성적
	GRADE_CRITERIA_CD("GRADE_CRITERIA_CD", "성적산출기준코드", "시험/레포트/출석/기타/과제/실습 등 성적 산출 기준"),

	// 공간(PLACE) 테이블 관련
	PLACE_TYPE_CD("PLACE_TYPE_CD", "공간유형(계층)", "캠퍼스/건물/방/시설 등 계층 역할"),
	PLACE_USAGE_CD("PLACE_USAGE_CD", "공간용도(기능)", "예약·운영에 쓰는 공간 기능 라벨"),

	// 수강 및 강의(클래스룸) 관련
	ENROLL_STATUS_CD("ENROLL_STATUS_CD", "수강상태코드", "수강(확정) 이후에만 사용하는 상태코드(수강중/완료/철회/포기)"),
	ATT_STATUS_CD("ATT_STATUS_CD", "출결상태코드", "출석/결석/지각/조퇴/공결 등 출결 상태를 구분하는 코드"),

	// 공지
	NOTICE_TYPE_CD("NOTICE_TYPE_CD", "공지사항유형코드", "공지사항 유형을 분류하는 공통코드 (일반공지, 학사공지 등)");

	private final String code; // DB 코드값
	private final String name; // 한국어 이름
	private final String description; // 설명

	CommonCodeSort(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/** DB 코드값 반환 (예: "BANK_CODE"). */
	public String getCode() {
		return code;
	}

	/** 한국어 이름 반환. */
	public String getName() {
		return name;
	}

	/** 설명 반환. */
	public String getDescription() {
		return description;
	}
}