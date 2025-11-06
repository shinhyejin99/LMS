# 강의개설 신청 더미데이터 설계 가이드

## 생성 범위
- 대상 테이블: `APPROVAL`, `LCT_OPEN_APPLY`, `LCT_APPLY_WEEKBY`, `LCT_APPLY_GRADERATIO`
- 학기 범위: `2025_REG1`, `2025_REG2`
- 신청 주체: 재직 중(`PRF_STATUS_ACTV`)인 모든 교수. 학과장(`PRF_POSIT_HEAD`)은 승인자 역할.
- 승인 프로세스: 단일 결재선(과목 신청 교수 → 소속 학과장), 모든 건 승인 완료 상태(`approveYnnull='Y'`).
- 시퀀스 ID 규칙: 접두어 + `7` + 고정 자리수(CHAR(15))
  - 승인: `APPR7XXXXXXXXXX` (10자리 숫자)
  - 강의개설 신청: `LCTAPLY7XXXXXXX` (7자리 숫자)

## 데이터 구성 원칙
- **과목 매핑**  
  - `MAJ_CORE`, `MAJ_ELEC`, `GE_BASIC` Career Design 과목은 해당 학과 교수에게 배정.  
  - `MAJ_BASIC`, `GE_CORE`, `GE_ELEC`는 단과대 공통 담당 교수 풀(동일 단과대 교수)에서 순환 배정.  
  - 일반 교양(`DEPT-GENR`) 과목은 교양 학부 교수 우선, 없을 경우 전교 교수 풀에서 보강.
- **정원 산정**  
  - 학생 현황(`StudentMapper.selectStudentList`)을 기반으로 학과/단과대/학년별 재학생 수를 집계.  
  - 과목 Completion 유형별 가중치를 적용해 기본 정원을 계산하고, 정규 학기 평균 15학점 이상을 공급하도록 학기별 총합을 보정.  
  - PASS/FAIL 교과(진로설계 등)는 출석/실습 위주의 배점과 비교적 작은 정원으로 설정.
- **강의계획 요소**  
  - `LECTURE_INDEX`, `LECTURE_GOAL`, `PREREQ_SUBJECT`, `DESIRE_OPTION`은 간략 문장 위주로 구성.  
  - 주차별 계획은 15주를 기본으로 하며, 주차별 목표·설명은 주제 키워드(이론, 사례, 토론 등)를 순환 사용.  
  - 성적비율(`GRADE_CRITEIRA_CD`)은 Completion 유형별 대표 비중(시험/과제/출석/기타)으로 합계 100을 맞춘다. PASS/FAIL은 실습/출석 위주.
- **신청/승인일**  
  - `APPLY_AT`: 학기별 개설 신청 기간을 가정해 2024.11~2025.01(1학기), 2025.05~06(2학기) 사이 랜덤 분포.  
  - `APPROVE_AT`: 신청일 + 3~7일 범위에서 승인 완료 시간으로 설정.

## 생성기 실행 전 체크리스트
1. `SUBJECT`, `SBJ_TARGET`, `PROFESSOR`, `STUDENT`, `UNIV_DEPT`에 더미 데이터가 존재해야 함.  
2. `LCT_OPEN_APPLY`, `LCT_APPLY_WEEKBY`, `LCT_APPLY_GRADERATIO`, `APPROVAL` 기존 레코드를 비운 뒤 시퀀스(`SEQ_LCT_OPEN_APPLY`, `SEQ_APPROVAL`)를 초기화하거나, 앞선 규칙과 중복되지 않도록 관리.  
3. `StudentMapper.selectStudentList()`가 재학생 수를 정상 반환하는지 확인(없으면 기본 최소 정원으로 생성).

## 테스트 클래스 개요
- 파일: `lecture/LectureOpenApplyDummyGeneratorTest.java`
- 주요 절차  
  1. 교수/과목/학생/학과 데이터를 집계하고 매핑 테이블 구성  
  2. 과목·학기 조합별 담당 교수 선정 및 정원 산정  
  3. 승인 레코드 작성 → 강의개설 신청 본문 → 주차 계획/성적비율 일괄 삽입  
  4. 학기별 공급 학점 총합을 로그로 출력하여 15학점 평균 충족 여부 확인

## 실행 후 검증 포인트
- `APPROVAL`: `APPLY_TYPE_CD='LCT_OPEN'`, `APPROVE_YNNULL='Y'`, `APPROVE_AT` 채워짐  
- `LCT_OPEN_APPLY`: 과목·교수·년도학기 매핑, 정원(`EXPECT_CAP`)과 희망사항 텍스트 확인  
- `LCT_APPLY_WEEKBY`: 신청별 15개 주차 레코드 구성  
- `LCT_APPLY_GRADERATIO`: 신청별 배점 합계 100  
- 학기별 공급 학점 로그가 총 재학생 수 × 15학점 이상인지 확인하고, 필요 시 정원 수치 조정.

## 추가 참고
- 승인 결재선이 여러 단계로 확장될 경우 `prevApproveId` 체인을 생성 후 `insertDummyApproval` 호출 순서를 조정하면 된다.  
- 계절학기(`SUB1`, `SUB2`)나 부분 학년 개설을 추가하려면 `REGULAR_TERMS` 집합과 정원 산정 로직을 확장하면 된다.
