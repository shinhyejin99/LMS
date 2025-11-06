1	COMMON_SORT_CD			VARCHAR2(50)	NOT NULL
2	SORT_NAME			VARCHAR2(100)	NOT NULL
3	SORT_DESC			VARCHAR2(1000)	NOT NULL
4	USING_YN			CHAR(1)	NOT NULL

AFFIL_CHANGE_CD	소속변동코드	학생 소속(전공/부전공 등) 변동 유형 코드	Y
ANSWER_TYPE_CD	답변 타입코드	강의평가나 설문 문항 등의 응답 형태를 구분하기 위한 코드 (객관식, 주관식 등)	Y
APPLY_STATUS_CD	신청/접수 상태 코드	신청, 접수, 처리, 승인/반려 등의 진행 상태를 관리하는 코드 분류입니다.	Y
APPLY_TYPE	신청 유형	결재 신청서의 유형을 분류	Y
APPLY_TYPE_CD	신청분류코드	학적변동/소속변동/강의개설 등 다양한 신청 유형을 구분하기 위한 분류 코드입니다. 각 신청서는 해당 분류와 매핑되어 전자결재/처리 흐름에서 일관되게 관리됩니다.	Y
ATT_STATUS_CD	출결상태코드	출석/결석/지각/조퇴/공결 등 출결 상태를 구분하는 코드	Y
BANK_CODE	은행코드	국내 시중/지방/인터넷은행 및 유사기관 코드	Y
COMPLETION_CD	이수구분	과목의 이수구분(전공/교양 등) 정의	Y
CONDITION_CD	학과별조건코드	학과마다 상이한 조건을 기록하는 테이블입니다. 조건의 종류에는 '2학년_자동진급_필요학점', '졸업_어학점수', '졸업_논문', '학과별_복수전공_졸업필요학점' 등이 있습니다.	Y
CREDIT_LIMIT_CD	수강신청제한학점코드	학년/학기별 최대 수강 학점 기준	Y
ENROLL_STATUS	수강상태	강의 수강 상태 분류	Y
ENROLL_STATUS_CD	수강상태코드	수강(확정) 이후에만 사용하는 상태코드(수강중/완료/철회/포기)	Y
ENTRANCE_TYPE_CD	입학전형코드	대학 입학전형 유형 공통코드	Y
GPA_CD	대학 성적 등급 코드	4.5 만점 기준의 대학 성적 등급(A+, B0 등)을 관리하는 코드 분류입니다.	Y
GRADE_CD	학년코드	재학 학년 구분 코드	Y
GRADE_CRITERIA_CD	성적산출기준코드	시험/레포트/출석/기타/과제/실습 등 성적 산출 기준	Y
MILITARY_TYPE_CD	병역유형코드	군별·대체복무 구분 코드(2025 기준 복무기간 반영)	Y
NOTICE_TYPE_CD	공지사항유형코드	공지사항 유형을 분류하는 공통코드 (일반공지, 학사공지 등)	Y
PLACE_TYPE_CD	공간유형(계층)	캠퍼스/건물/방/시설 등 계층 역할	Y
PLACE_USAGE_CD	공간용도(기능)	예약·운영에 쓰는 공간 기능 라벨	Y
PRF_APPNT_CD	교수 임용코드	전임/비전임의 임용 구분	Y
PRF_POSIT_CD	교수 직책코드	학과 내 보직(학과장/부학과장 등)	Y
PRF_STATUS_CD	교수 재직상태코드	교원의 현재 상태(재직, 휴직, 파견 등)	Y
RECORD_CHANGE_CD	학적변동코드	입학/진급/졸업/휴학/자퇴 등 학적 상태 변동	Y
SCHOLARSHIP_CD	장학금종류코드	장학금 종류 공통코드	Y
STU_STATUS_CD	재학상태코드	학생의 현재 재학 상태	Y
SUBJECT_TYPE_CD	과목 분류코드	과목의 평점 산정 방식을 구분하기 위한 코드 (상대평가, 절대평가, 수료/낙제 등)	Y
TERM_CD	학기코드	정규/계절 학기 구분 코드	Y
TUITION_SORT_CD	등록금유형코드	등록금 징수 항목에 대한 공통코드	Y