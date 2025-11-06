# AI Dummy Data Generator

LMS 핵심 테이블에 필요한 더미 데이터를 Spring Boot 기반 통합 테스트로 생성하기 위한 작업 메모입니다. 테스트 클래스를 실행하면 `DummyDataMapper`(또는 도메인별 매퍼)가 실제 DB에 `INSERT ALL`을 수행합니다.

## 참고 자료
- 테이블/제약조건 DDL: `SQL_DDL/v0.5/DDL.txt`, `PRIMARY_KEY.txt`, `FOREIGN_KEY.txt`, `COMPOSIT_FOREIGN_KEY.txt`, `MISC_CONSTRAINT.txt`
- 테이블 정의 시트: `SQL_DDL/v0.5/TABLE_DEFINITION_*.csv`
- 시퀀스 규칙: `SQL_DDL/v0.5/SEQUENCE.txt`  
  → 모든 시퀀스 기반 키는 15자리 문자열이며, 접두어 + 1부터 시작하는 숫자를 `LPAD`로 채워 `CHAR(15)`를 만족시킨다.
  → 더미 생성 시 실제 DB 시퀀스 대신 `접두어 + 7 + LPAD된 일련번호` 방식으로 직접 생성한다 (예: `LCTAPLY700000001`, `LECT7000000001`).
- 공통 코드 자료: `src/test/java/kr/or/jsu/aiDummyDataGenerator/COMMON_CODE_SORT.md`, `COMMON_CODE.md`

## 패키지 & 테스트
| 번호 | 테스트 클래스 | 범위 |
| --- | --- | --- |
| 1 | `kr.or.jsu.aiDummyDataGenerator.user.UsersDummyGeneratorTest` | 사용자(학생/교직원/관리자) + 주소, 계좌 |
| 2 | `kr.or.jsu.aiDummyDataGenerator.campus.CampusStructureDummyGeneratorTest` | 대학/단과대/학과(부속 학과 포함), 행정부서 |
| 3 | `kr.or.jsu.aiDummyDataGenerator.campus.CampusPlaceDummyGeneratorTest` | 캠퍼스/건물/시설/강의실 공간 배치 |
| 4 | `kr.or.jsu.aiDummyDataGenerator.staff.StaffDummyGeneratorTest` | 직원 조직 배치(부서 배정, 발령) |
| 5 | `kr.or.jsu.aiDummyDataGenerator.faculty.ProfessorDummyGeneratorTest` | 교수 조직 배치(부서 배정, 임용/겸직) |
| 6 | `kr.or.jsu.aiDummyDataGenerator.student.StudentDummyGeneratorTest` | 학생 기본 정보, 학적 상태, 상담 기본 |
| 7 | `kr.or.jsu.aiDummyDataGenerator.lecture.LectureOpenApplyDummyGeneratorTest` | 강의개설 신청서, 주차/성적비율 초안 |
| 8 | `kr.or.jsu.aiDummyDataGenerator.lecture.LectureDummyGeneratorTest` | 승인된 신청서를 강의/주차/성적비율 테이블로 이관 |
| 9 | `kr.or.jsu.aiDummyDataGenerator.lecture.LectureScheduleDummyGeneratorTest` | 강의실·시간블록 배정 (단과대/시수 기반) |
| 10 | `kr.or.jsu.aiDummyDataGenerator.lecture.StudentEnrollmentDummyGeneratorTest` | 학생-강의 수강 및 성적 더미 |
| 11 이후 | `kr.or.jsu.aiDummyDataGenerator.<도메인>.***GeneratorTest` | Common Code, Lecture 등 추가 도메인 더미 데이터 |

## 업무 가이드
- **상태 보드 유지**: 현재 작업 범위를 명확히 하고 아래 진행 현황 체크박스를 최신 상태로 업데이트한다.
- **테스트 실행**: 생성기 테스트는 `@SpringBootTest`(+ 필요 시 `@Transactional`)로 작성하며, 반복 실행 시 PK 충돌이 나지 않도록 ID 규칙을 준수한다. 동일 테스트를 다시 실행할 때는 시퀀스 시드가 정상 증가하는지 확인할 것.
- **배치 크기**: `DummyDataMapper`는 `INSERT ALL`을 사용하므로 500~1000건 단위로 나눠 처리한다.
- **로그 전략**: `log.info`에 핵심 파라미터(건수, 주요 ID, 날짜 범위 등)만 출력한다.

## 공통 규칙 메모
- **Address**: 모든 주소는 `서울시 종로구` 기준으로 생성한다. 상세 주소는 동/번지/호수 조합으로만 표현한다.
- **Users**
  - 비밀번호는 `{bcrypt}$2a$10$1vrhrg0sCry19KPz/pyJ5OmwYQZNw805uL9lYF8dFiVMWgCbodQwi`를 고정 사용.
  - 은행 코드는 `COMMON_CODE.md`의 `BANK_*` 항목에서 선택.
  - `USER_OTP_SECRET`는 항상 `NULL`.
  - 학생 계정 ID: `USER1YY########` (YY=입학연도 2025~2019, 총 2000명).
  - 교수 계정 ID: `USER2YY########` (YY=임용연도 2025~2019, 총 500명).
  - 직원 계정 ID: `USER3YY########` (YY=입사연도 2025~2019, 총 100명).
  - 학번/사번/교번 규칙: `연도(4자리)+7+일련번호`
    * 학번: `YYYY7NNNN` (N은 0001부터)
    * 교번: `YYYY7NNN`
    * 사번: `YYYY7NN`
  - 연락처는 ID 기반으로 생성해 중복되지 않도록 한다.

## 진행 현황
- [x] Users (학생/교직원/관리자 기본 정보 & 주소/계좌)
- [x] College / UnivDept / StaffDept
- [x] Campus / Building / Place 공간 구조
- [x] Staff
- [x] Professors
- [x] Students
- [ ] Common Code 세트 (Users 전 선행, 외부 스크립트 실행)
- [ ] Professor / Student / Lecture 기타 엔티티
- [x] Lecture Applications (10.x 강의개설신청 계열)
- [x] Lecture Room Schedule (12.3 강의_공간_시간표)
- [x] Student Enrollment (14.x STU_ENROLL_LCT / ENROLL_GPA)
- [ ] Lectures (12.x 강의 확정 계열) — LectureDummyGeneratorTest 준비됨

## CampusPlaceDummyGeneratorTest 메모
- `CAMPUSMAIN` 중심으로 캠퍼스/건물/독립 시설/강의실 계층을 구성합니다.
- 단과대 건물마다 8~10개의 강의실과 3개 이상의 실습실, 교수연구실·세미나실·회의실을 배치했습니다.
- 중앙도서관 스터디룸(6개), 학생회관 라운지/회의실 등 학습·휴게 공간을 함께 생성합니다.
- 하위 공간 `PLACE_NAME`은 부모 이름을 포함하지 않고 유형+호수 조합(예: `강의실 101`)으로만 구성합니다. 부모 정보가 필요하면 조인으로 조회합니다.

## StudentEnrollmentDummyGeneratorTest 메모
- 2024 학기는 입학년도 2021~2024, 2025 학기는 2022~2025 학번만 재학생으로 간주해 정원의 2/3만 수강 확정합니다.
- 2025_REG2는 진행 중(`ENR_ING`) 상태로 성적을 부여하지 않고, 이전 학기는 수강완료 90% / 낙제 10% 비율로 분배합니다.
- Pass/Fail 과목은 `P`/`NP`로, 상대·절대평가 과목은 A/B/C/D 구간을 30%/30%/40%로 배분하고 `F`를 별도로 처리합니다.
- `ENROLL_GPA`는 완료·낙제 건에만 기록하며, 최종 평점이 자동 평점과 다르면 보정 사유를 자동 기재합니다.

## LectureOpenApplyDummyGeneratorTest 메모
- 생성 연도: `2024_REG1`, `2024_REG2`, `2025_REG1`, `2025_REG2` 4개 학년도/학기로 신청서를 만든다.
- 교수 필터: `professorNo` 앞 4자리 기준 2023년까지(2024년 이전) 임용된 활성 교수만 신청서를 작성한다.
- 교수당 강의 수: 한 명이 최소 2건, 최대 4건의 강의개설신청을 담당하도록 배분한다.

## 강의개설신청 → 강의 더미 데이터 이관
관련 테스트: `LectureDummyGeneratorTest`
10번 계열 테이블에서 생성한 데이터를 기반으로 12번 계열 테이블을 채운다. 1:1 매핑을 보장하기 위해 `LCT_APPLY_ID`와 새로 생성되는 `LECTURE_ID`의 대응표를 Map으로 관리한다.

### 1. `LCT_OPEN_APPLY` → `LECTURE`
| 출처 컬럼 | 대상 컬럼 | 비고 |
| --- | --- | --- |
| `LCT_APPLY_ID` | (참조용) | 강의 확정 후에도 원본 신청 ID를 보관하려면 별도 컬럼이나 메타 테이블로 관리 |
| (신규 생성) | `LECTURE_ID` | `'LECT' || LPAD(SEQ_LECTURE.NEXTVAL, 11, '0')` 규칙 적용 |
| `SUBJECT_CD` | `SUBJECT_CD` | 동일 값 유지 |
| `PROFESSOR_NO` | `PROFESSOR_NO` | 승인된 담당 교수 |
| `YEARTERM_CD` | `YEARTERM_CD` | 자료사전 길이 확인: 신청 테이블이 `CHAR(8)`이라면 9자리 요구 시 우측에 `'0'` 패딩 여부 확인 |
| `EXPECT_CAP` | `MAX_CAP` | 신청 시 예상 정원 값을 확정 강의 정원으로 사용 |
| `LECTURE_INDEX` | `LECTURE_INDEX` | 신청서에서 작성한 강의 개요 |
| `LECTURE_GOAL` | `LECTURE_GOAL` | 신청서에서 작성한 강의 목표 |
| `PREREQ_SUBJECT` | `PREREQ_SUBJECT` | 선수학습 과목 |
| (없음) | `CANCEL_YN` | 기본값 `'N'`으로 입력. 폐강 시 후처리에서 `Y`로 업데이트 |
| (없음) | `END_AT` | 종강 전이라면 `NULL`, 종료 시점 확정 후 업데이트 |

추가 메모:
- `APPROVE_ID`, `APPLY_AT`, `DESIRE_OPTION`, `CANCEL_YN` 등 신청 전용 컬럼은 강의 확정 테이블에 존재하지 않으므로, 필요 시 별도 감사 로그나 이관 히스토리에 저장한다.
- 승인 절차에서 하나라도 `CANCEL_YN='Y'`가 된 신청은 강의로 이관하지 않는다.

### 2. `LCT_APPLY_WEEKBY` → `LCT_WEEKBY`
| 출처 컬럼 | 대상 컬럼 | 비고 |
| --- | --- | --- |
| `LCT_APPLY_ID` | `LECTURE_ID` | Step 1에서 만든 Map으로 변환 |
| `LECTURE_WEEK` | `LECTURE_WEEK` | 1~N주차 |
| `WEEK_GOAL` | `WEEK_GOAL` | 동일 값 |
| `WEEK_DESC` | `WEEK_DESC` | 동일 값 |

주차 정보는 신청 당시 기록을 그대로 복제하되, 확정 후 수정 이력을 추적하려면 `updated_at` 등을 별도 관리한다.

### 3. `LCT_APPLY_GRADERATIO` → `LCT_GRADERATIO`
| 출처 컬럼 | 대상 컬럼 | 비고 |
| --- | --- | --- |
| `LCT_APPLY_ID` | `LECTURE_ID` | Map으로 매핑 |
| `GRADE_CRITEIRA_CD` | `GRADE_CRITERIA_CD` | 오탈자 주의: 대상 컬럼은 `CRITERIA` |
| `RATIO` | `RATIO` | 모든 항목의 합이 100이 되도록 검증 로직 추가 |

성적 산출 기준은 공통 코드에서 관리되므로, 신청 단계에서 선택했던 코드만 그대로 복사한다. 더미 데이터 생성 시 합계가 100인지 `assert`로 확인한다.

### 구현 권장 순서
1. `LectureApplicationDummyGenerator` (가칭) 테스트에서 10.x 데이터를 모두 생성한다.
2. 생성 직후 `LCT_APPLY_ID` ↔ `LECTURE_ID` 매핑을 확보하고 로그에 요약을 남긴다.
3. `LectureDummyGenerator`에서 위 매핑을 사용해 12.x 테이블을 채운다.
4. 주차별/성적비율 자식 데이터는 강의 마스터 생성 직후 같은 트랜잭션에서 처리한다.

위 단계가 완료되면 진행 현황에서 `Lectures (12.x 강의 확정 계열)` 항목을 체크하고, 필요 시 검증을 위한 조회 SQL을 README나 테스트 클래스에 주석으로 남긴다.
