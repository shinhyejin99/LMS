package kr.or.jsu.lms.staff.service.student;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.mybatis.mapper.AddressMapper;
import kr.or.jsu.mybatis.mapper.StuEntranceMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.CommonCodeVO;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.StuEntranceVO;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffStudentInfoServiceImpl implements StaffStudentInfoService {

	private final UsersMapper usersMapper;
	private final StudentMapper mapper;
	private final PasswordEncoder passwordEncoder;
	private final StuEntranceMapper stuEntranceMapper;
	private final AddressMapper addressMapper;
	private final FilesUploadService filesUploadService;

	// ⭐ 필수 의존성 주입: CommonCodeService와 DatabaseCache
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;

	// -------------------------------------------------------------------------
	// CRUD 및 기본 기능
	// -------------------------------------------------------------------------

	/**
	 * 전체 성별 통계를 조회하고, JavaScript 차트 형식에 맞게 변환합니다.
	 *
	 * @return Map<String(성별명), Integer(학생수)>
	 */
	@Override
	public Map<String, Integer> getOverallGenderStatistics() {

		List<Map<String, Object>> rawStats = mapper.selectOverallGenderStatistics();

		Map<String, Integer> formattedStats = new LinkedHashMap<>();

		for (Map<String, Object> item : rawStats) {

			String genderName = (String) item.get("gender");

			Number count = (Number) item.get("studentCount");
			if (genderName != null && count != null) {
				formattedStats.put(genderName, count.intValue());
			}
		}
		return formattedStats;
	}

	/**
	 * 전체 학년별 통계를 조회하고, JavaScript 차트 형식에 맞게 변환합니다.
	 *
	 * @return Map<String(학년명), Integer(학생수)>
	 */
	public Map<String, Integer> getOverallGradeStatistics() {

		List<Map<String, Object>> rawStats = mapper.selectOverallGradeStatistics();

		Map<String, Integer> formattedStats = new LinkedHashMap<>();

		for (Map<String, Object> item : rawStats) {

			String gradeName = (String) item.get("grade");
			Number count = (Number) item.get("studentCount"); // DB에서 COUNT 결과는 Number 타입

			if (gradeName != null && count != null) {

				formattedStats.put(gradeName, count.intValue());
			}
		}
		return formattedStats;
	}

	@Override
	@Transactional
	public void createStaffStudentInfo(StudentDetailDTO studentDto) {
		// (단일 학생 등록 로직 - 제공된 코드를 그대로 유지)
		if (studentDto.getGradExamYn() == null || studentDto.getGradExamYn().trim().isEmpty()) {
			studentDto.setGradExamYn("N");
		}

		String entranceDateStr = studentDto.getEntranceDate();
		String entranceYear;
		LocalDate entranceDate;

		try {
			// YYYY-MM-DD 또는 YYYYMMDD 포맷을 모두 허용하는지 확인 필요. 여기서는 ISO_DATE를 가정합니다.
			// 엑셀에서 YYYYMMDD를 읽어온다면 아래 코드 대신 적절한 DateTimeFormatter가 필요합니다.
			// 현재 코드는 제공된 상태를 유지합니다.
			entranceDate = LocalDate.parse(entranceDateStr, DateTimeFormatter.ISO_DATE);
			entranceYear = String.valueOf(entranceDate.getYear());
		} catch (DateTimeParseException e) {
			try { // YYYYMMDD 포맷 재시도
				entranceDate = LocalDate.parse(entranceDateStr, DateTimeFormatter.BASIC_ISO_DATE);
				entranceYear = String.valueOf(entranceDate.getYear());
			} catch (DateTimeParseException ex) {
				throw new RuntimeException("입학일(entranceDate) 형식 오류", ex);
			}
		}

		int maxSequence = mapper.findMaxSequenceByYear(entranceYear);
		int nextSequence = maxSequence + 1;
		String sequencePart = String.format("%05d", nextSequence);
		String newStudentNo = entranceYear + sequencePart;

		studentDto.setStudentNo(newStudentNo);
		studentDto.setUserNo(newStudentNo);
		studentDto.setEntranceDate(entranceDateStr);

		try {
			int entranceMonth = entranceDate.getMonthValue();
			String entranceTermCd;

			if (entranceMonth >= 3 && entranceMonth <= 8) {
				entranceTermCd = "REG1"; // 1학기
			} else {
				entranceTermCd = "REG2"; // 2학기
			}

			int expectedYear = entranceDate.getYear() + 4; // 4년제 기준
			String expectedYeartermCd = expectedYear + "_" + entranceTermCd;
			studentDto.setExpectedYeartermCd(expectedYeartermCd);

		} catch (Exception e) {
			log.warn("입학일({}) 기반 예상 졸업 학년도/학기 계산 실패", entranceDateStr, e);
			studentDto.setExpectedYeartermCd(null);
		}

		String initialPassword = studentDto.getRegiNo().substring(0, 6);
		String encodedPw = passwordEncoder.encode(initialPassword);
		studentDto.setPwHash(encodedPw);

		String studentName = studentDto.getStudentName();
		if (studentName != null && studentName.trim().length() > 1) {
			String trimmedName = studentName.trim();
			String lastName = trimmedName.substring(0, 1);
			String firstName = trimmedName.substring(1);

			studentDto.setLastName(lastName);
			studentDto.setFirstName(firstName);
		} else {
			studentDto.setLastName(" ");
			studentDto.setFirstName(" ");
		}

		// DB 삽입 로직
		int insertedCount = 0;

		AddressVO addressVO = new AddressVO();
		addressVO.setBaseAddr(studentDto.getBaseAddr());
		addressVO.setDetailAddr(studentDto.getDetailAddr());
		addressVO.setZipCode(studentDto.getZipCode());
		addressVO.setUsingYn("Y");

		int addressInsertResult = addressMapper.insertAddress(addressVO);
		if (addressInsertResult != 1)
			throw new RuntimeException("ADDRESS 등록 실패");
		insertedCount += addressInsertResult;

		String generatedAddrId = addressVO.getAddrId();
		if (generatedAddrId == null) {
			throw new IllegalStateException("주소 등록 후 생성된 ADDR_ID가 null입니다. MyBatis 설정을 확인하세요.");
		}
		studentDto.setAddrId(generatedAddrId);

		UsersVO usersVo = new UsersVO();
		// ... (UsersVO 프로퍼티 복사 및 설정 생략)
		usersVo.setUserType("ROLE_STUDENT");
		usersVo.setCreateAt(LocalDateTime.now());
		usersVo.setUserNo(newStudentNo);
		usersVo.setPwHash(studentDto.getPwHash());
		usersVo.setFirstName(studentDto.getFirstName());
		usersVo.setLastName(studentDto.getLastName());
		usersVo.setRegiNo(studentDto.getRegiNo());
		usersVo.setAddrId(generatedAddrId);
		usersVo.setMobileNo(studentDto.getMobileNo().trim());
		usersVo.setEmail(studentDto.getEmail());
		String bankCode = studentDto.getBankCode();
		usersVo.setBankCode(bankCode != null ? bankCode : "000");
		usersVo.setBankAccount(studentDto.getBankAccount());

		int usersInsertResult = usersMapper.insertUser(usersVo);
		if (usersInsertResult != 1)
			throw new RuntimeException("USERS 등록 실패");
		insertedCount += usersInsertResult;

		String finalUserId = usersVo.getUserId();
		studentDto.setUserId(finalUserId);

		MultipartFile photoFile = studentDto.getPhotoFile();
		String newPhotoId = null;

		if (photoFile != null && !photoFile.isEmpty()) {
			List<FileDetailVO> fileDetails = filesUploadService.saveAtDirectory(List.of(photoFile),
					FileUploadDirectory.IDPHOTO);

			if (!fileDetails.isEmpty()) {
				String savedFileId = filesUploadService.saveAtDB(fileDetails, finalUserId, true);

				if (StringUtils.hasText(savedFileId)) {
					newPhotoId = savedFileId;
					int updateResult = usersMapper.updateUserPhotoId(finalUserId, newPhotoId);

					if (updateResult == 1) {
						usersVo.setPhotoId(newPhotoId);
						studentDto.setPhotoId(newPhotoId);
						log.info("DEBUG: 새로운 프로필 사진 업로드 및 USERS 업데이트 완료, New PhotoId: {}", newPhotoId);
					} else {
						log.error("USERS 테이블 PHOTO_ID 업데이트 실패: UserId={}", finalUserId);
					}

				} else {
					log.error("프로필 사진 저장 실패: FileId를 확보하지 못했습니다. UserId={}", finalUserId);
				}
			}
		}

		if (studentDto.getGradeCd() == null || studentDto.getGradeCd().trim().isEmpty()) {
			studentDto.setGradeCd("1ST");
		}

		if (studentDto.getStuStatusCd() == null || studentDto.getStuStatusCd().trim().isEmpty()) {
			studentDto.setStuStatusCd("ENROLLED");
		}

		int studentInsertResult = mapper.insertStaffStudentInfo(studentDto);
		if (studentInsertResult != 1)
			throw new RuntimeException("STUDENT 등록 실패");
		insertedCount += studentInsertResult;

		StuEntranceVO stuEntranceVo = new StuEntranceVO();
		BeanUtils.copyProperties(studentDto, stuEntranceVo);
		stuEntranceVo.setStudentNo(newStudentNo);

		String entranceTypeCd = studentDto.getEntranceTypeCd();
		if (entranceTypeCd == null || entranceTypeCd.trim().isEmpty()) {
			entranceTypeCd = "SU-GC";
		}
		stuEntranceVo.setEntranceTypeCd(entranceTypeCd);

		int stuEntranceInsertResult = stuEntranceMapper.insertStuEntrance(stuEntranceVo);
		if (stuEntranceInsertResult != 1)
			throw new RuntimeException("STU_ENTRANCE 등록 실패");
		insertedCount += stuEntranceInsertResult;

		if (insertedCount != 4) {
			throw new RuntimeException(
					"학생 등록 중 필수 4개 테이블(ADDRESS, USERS, STUDENT, STU_ENTRANCE)에 대한 데이터 삽입이 완료되지 않았습니다.");
		}

		log.info("학생 등록 완료: ADDRESS={}, USERS={}, STUDENT={}, STU_ENTRANCE={}", addressInsertResult, usersInsertResult,
				studentInsertResult, stuEntranceInsertResult);
	}

	@Override
	public List<Map<String, Object>> readStaffStudentInfoList(Map<String, Object> paramMap) {
		@SuppressWarnings("unchecked")
		PaginationInfo<Map<String, Object>> pagingInfo = (PaginationInfo<Map<String, Object>>) paramMap
				.get("pagingInfo");

		if (pagingInfo == null) {
			log.error("PaginationInfo가 paramMap에 없습니다.");
			return List.of();
		}

		int totalRecords = mapper.selectStudentCount(paramMap);
		pagingInfo.setTotalRecord(totalRecords);

		List<Map<String, Object>> studentList = mapper.selectStaffStudentInfoList(paramMap);

		return studentList;
	}

	@Override
	public StudentDetailDTO readStaffStudentInfo(String studentNo) throws RuntimeException {
		StudentDetailDTO student = mapper.selectStudentDetailInfo(studentNo);
		return student;
	}

	@Override
	@Transactional
	public void modifyStaffStudentInfo(@Valid StudentDetailDTO student) {
		// (학생 정보 수정 로직 - 제공된 코드를 그대로 유지)
		String studentNo = student.getStudentNo();
		if (!StringUtils.hasText(studentNo)) {
			throw new IllegalArgumentException("학생 번호(StudentNo)가 누락되어 정보를 수정할 수 없습니다.");
		}

		StudentDetailDTO existingStudent = mapper.selectStudentDetailInfo(studentNo);
		if (existingStudent == null) {
			throw new RuntimeException("학번 [" + studentNo + "]에 해당하는 학생 정보가 DB에 존재하지 않아 수정할 수 없습니다.");
		}

		String existingAddrId = existingStudent.getAddrId();
		String existingUserId = existingStudent.getUserId();
		String existingPhotoId = existingStudent.getPhotoId();

		String studentName = student.getStudentName();
		if (StringUtils.hasText(studentName) && studentName.trim().length() > 1) {
			String trimmedName = studentName.trim();
			String lastName = trimmedName.substring(0, 1);
			String firstName = trimmedName.substring(1);

			student.setLastName(lastName);
			student.setFirstName(firstName);
		} else {
			student.setLastName(existingStudent.getLastName() != null ? existingStudent.getLastName() : " ");
			student.setFirstName(existingStudent.getFirstName() != null ? existingStudent.getFirstName() : " ");
		}

		if (!StringUtils.hasText(student.getGradeCd()))
			student.setGradeCd(existingStudent.getGradeCd());
		if (!StringUtils.hasText(student.getStuStatusCd()))
			student.setStuStatusCd(existingStudent.getStuStatusCd());
		if (!StringUtils.hasText(student.getGradExamYn()))
			student.setGradExamYn(existingStudent.getGradExamYn() != null ? existingStudent.getGradExamYn() : "N");

		AddressVO addressVO = new AddressVO();
		addressVO.setAddrId(existingAddrId);
		addressVO.setBaseAddr(student.getBaseAddr());
		addressVO.setDetailAddr(student.getDetailAddr());
		addressVO.setZipCode(student.getZipCode());
		addressVO.setUsingYn("Y");

		int addressUpdateResult = addressMapper.updateAddress(addressVO);
		if (addressUpdateResult != 1) {
			log.warn("ADDRESS 업데이트 실패: ADDR_ID={}", existingAddrId);
		}

		MultipartFile photoFile = student.getPhotoFile();
		String newPhotoId = existingPhotoId;

		if (photoFile != null && !photoFile.isEmpty()) {
			List<FileDetailVO> fileDetails = filesUploadService.saveAtDirectory(List.of(photoFile),
					FileUploadDirectory.IDPHOTO);

			if (!fileDetails.isEmpty()) {
				String savedFileId = filesUploadService.saveAtDB(fileDetails, existingUserId, true);

				if (StringUtils.hasText(savedFileId)) {
					newPhotoId = savedFileId;
				} else {
					log.error("새 프로필 사진 저장 실패: FileId를 확보하지 못했습니다.");
				}
			}
		}

		UsersVO usersVo = new UsersVO();
		usersVo.setUserId(existingUserId);
		usersVo.setAddrId(existingAddrId);
		usersVo.setPhotoId(newPhotoId);

		usersVo.setFirstName(student.getFirstName());
		usersVo.setLastName(student.getLastName());
		usersVo.setMobileNo(student.getMobileNo());
		usersVo.setEmail(student.getEmail());
		usersVo.setBankCode(student.getBankCode());
		usersVo.setBankAccount(student.getBankAccount());

		if (StringUtils.hasText(student.getPwHash()) && !student.getPwHash().equals(existingStudent.getPwHash())) {
			usersVo.setPwHash(passwordEncoder.encode(student.getPwHash()));
		} else {
			usersVo.setPwHash(null);
		}

		int usersUpdateResult = usersMapper.updateUser(usersVo);
		if (usersUpdateResult != 1) {
			log.error("USERS 업데이트 실패: UserId={}", existingUserId);
			throw new RuntimeException("사용자 정보 업데이트에 실패했습니다.");
		}

		student.setUserId(existingUserId);

		int studentUpdateResult = mapper.updateStaffStudentInfo(student);

		if (studentUpdateResult != 1) {
			log.error("STUDENT 업데이트 실패: StudentNo={}", studentNo);
			throw new RuntimeException("학생 테이블 정보 업데이트에 실패했습니다.");
		}

		log.info("학생 정보 수정 완료: StudentNo={}, ADDRESS={}, USERS={}, STUDENT={}", studentNo, addressUpdateResult,
				usersUpdateResult, studentUpdateResult);
	}

	// -------------------------------------------------------------------------
	// 엑셀 배치 처리 로직
	// -------------------------------------------------------------------------

	/**
	 * 엑셀 배치 미리보기 (학과별 카운트 계산)
	 */
	@Override
	public Map<String, Integer> previewBatchStudentsByExcel(MultipartFile excelFile,
			Map<String, Map<String, String>> codeMaps) {
		if (excelFile == null || excelFile.isEmpty()) {
			return new HashMap<>();
		}

		List<StudentDetailDTO> studentDtoList = processExcelToStudentDTOs(excelFile, codeMaps);

		// ⭐ 학과별 카운트 계산 로직 ⭐
		Map<String, Integer> previewCountsByDept = new HashMap<>();
		studentDtoList.forEach(studentDto -> {
			// processExcelToStudentDTOs에서 엑셀의 학과명은 'UnivDeptName'에 저장됩니다.
			String deptName = studentDto.getUnivDeptName();

			if (StringUtils.hasText(deptName)) {
				// merge를 사용하여 해당 학과명에 대한 카운트를 1 증가시킵니다.
				previewCountsByDept.merge(deptName, 1, Integer::sum);
			} else {
				// 학과명이 빈 경우, 별도로 카운트할 수 있습니다.
				previewCountsByDept.merge("학과 정보 없음", 1, Integer::sum);
			}
		});

		return previewCountsByDept;
	}

	/**
	 * 엑셀 파일 읽어 학생 정보 일괄 등록 (DB 배치 삽입 및 학과별 카운트 반환)
	 */
	@Transactional
	@Override
	public Map<String, Integer> createBatchStudentsByExcel(MultipartFile excelFile,
			Map<String, Map<String, String>> codeMaps) {

		List<StudentDetailDTO> studentDtoList = processExcelToStudentDTOs(excelFile, codeMaps);
		Map<String, Integer> insertedCountsByDept = new HashMap<>();

		List<AddressVO> addressList = new ArrayList<>();
		List<UsersVO> usersList = new ArrayList<>();
		List<StudentVO> studentList = new ArrayList<>();
		List<StuEntranceVO> stuEntranceList = new ArrayList<>();

		Map<String, Integer> maxSequenceByYear = new HashMap<>();

		if (studentDtoList.isEmpty()) {
			return new HashMap<>();
		}

		try {
			int studentCount = studentDtoList.size();

			// 시퀀스 배치 획득
			List<String> newUserIdList = usersMapper.getNextUserIdSequenceBatch(studentCount);
			List<String> newAddrIdList = addressMapper.getNextAddressIdSequenceBatch(studentCount);

			if (newAddrIdList.size() != studentCount || newUserIdList.size() != studentCount) {
				throw new IllegalStateException("필요한 시퀀스 수(" + studentCount + ")와 획득한 수가 일치하지 않습니다.");
			}

			for (int idx = 0; idx < studentCount; idx++) {
				StudentDetailDTO studentDto = studentDtoList.get(idx);

				// (데이터 정규화/null 체크 로직 생략)

				// 학번 생성
				String entranceYear = studentDto.getGradYear();

				maxSequenceByYear.computeIfAbsent(entranceYear, y -> mapper.findMaxSequenceByYear(y));
				int nextSequence = maxSequenceByYear.get(entranceYear) + 1;
				String newStudentNo = entranceYear + String.format("%05d", nextSequence);

				studentDto.setStudentNo(newStudentNo);
				studentDto.setUserNo(newStudentNo);

				maxSequenceByYear.put(entranceYear, nextSequence);

				// 시퀀스 할당
				String newAddrId = newAddrIdList.get(idx);
				studentDto.setAddrId(newAddrId);

				String newUserId = newUserIdList.get(idx);
				studentDto.setUserId(newUserId);

				// ⭐ 학과별 카운트 누적 ⭐
				String deptName = studentDto.getUnivDeptName();
				if (StringUtils.hasText(deptName)) {
					insertedCountsByDept.merge(deptName, 1, Integer::sum);
				} else {
					insertedCountsByDept.merge("알 수 없는 학과", 1, Integer::sum);
				}

				// VO 객체 생성 및 리스트 추가 (배치 삽입용)
				AddressVO addressVO = new AddressVO();
				addressVO.setAddrId(newAddrId);
				addressVO.setBaseAddr(studentDto.getBaseAddr());
				addressVO.setDetailAddr(studentDto.getDetailAddr());
				addressVO.setZipCode(studentDto.getZipCode());
				addressVO.setUsingYn("Y");
				addressList.add(addressVO);

				UsersVO usersVo = new UsersVO();
				BeanUtils.copyProperties(studentDto, usersVo);
				usersVo.setUserType("ROLE_STUDENT");
				usersVo.setCreateAt(LocalDateTime.now());
				usersVo.setUserId(newUserId);
				usersVo.setAddrId(newAddrId);
				usersList.add(usersVo);

				StudentVO studentVo = new StudentVO();
				BeanUtils.copyProperties(studentDto, studentVo);
				studentVo.setUserId(newUserId);
				studentList.add(studentVo);

				StuEntranceVO stuEntranceVo = new StuEntranceVO();
				BeanUtils.copyProperties(studentDto, stuEntranceVo);
				stuEntranceVo.setStudentNo(newStudentNo);
				stuEntranceList.add(stuEntranceVo);
			}
		} catch (Exception e) {
			log.error("학생 정보 매핑 실패:", e);
			throw new RuntimeException("학생 정보 매핑 실패: " + e.getMessage(), e);
		}

		try {
			// DB 일괄 삽입
			addressMapper.insertBatchAddress(addressList);
			usersMapper.insertBatchUser(usersList);
			mapper.insertBatchStaffStudentInfo(studentList);
			stuEntranceMapper.insertBatchStuEntrance(stuEntranceList);

		} catch (Exception e) {
			log.error("DB 일괄 삽입 실패: ", e);
			throw new RuntimeException("DB 일괄 삽입 실패: " + e.getMessage(), e);
		}

		return insertedCountsByDept;
	}

	/**
	 * 공통 코드 목록을 Map 형태로 조회합니다. (엑셀 배치 처리 등에 사용)
	 */
	@Override
	public Map<String, Map<String, String>> readCommonCodeMaps() {

		Map<String, Map<String, String>> codeMaps = new HashMap<>();

		// 1. 학과 (학과명 -> 학과코드) : DatabaseCache 사용
		Map<String, String> deptMap = new HashMap<>();
		((List<UnivDeptInfo>) databaseCache.getUnivDeptList()).forEach(dept -> {
			deptMap.put(dept.getUnivDeptName(), dept.getUnivDeptCd());
		});
		codeMaps.put("deptMap", deptMap);

		// 2. 학년 (학년명 -> 학년코드) : CommonCodeService 사용
		Map<String, String> gradeMap = new HashMap<>();
		((List<CommonCodeVO>) commonCodeService.readCommonCodeList(CommonCodeSort.GRADE_CD))
				.forEach(code -> gradeMap.put(code.getCdName(), code.getCommonCd()));
		codeMaps.put("gradeMap", gradeMap);

		// 3. 입학구분 (입학구분명 -> 입학구분코드) : CommonCodeService 사용
		Map<String, String> entranceTypeMap = new HashMap<>();
		((List<CommonCodeVO>) commonCodeService.readCommonCodeList(CommonCodeSort.ENTRANCE_TYPE_CD))
				.forEach(code -> entranceTypeMap.put(code.getCdName(), code.getCommonCd()));
		codeMaps.put("entranceTypeMap", entranceTypeMap);

		// 4. 은행 (은행명 -> 은행코드) : CommonCodeService 사용
		Map<String, String> bankMap = new HashMap<>();
		((List<CommonCodeVO>) commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE))
				.forEach(code -> bankMap.put(code.getCdName(), code.getCommonCd()));
		codeMaps.put("bankMap", bankMap);

		log.info("Common Code Maps Loaded: Keys={}, Depts={}", codeMaps.keySet(), deptMap.size());

		return codeMaps;
	}

	/**
	 * 엑셀 파일을 읽고, 유효성 검증을 거쳐 StudentDetailDTO 리스트로 변환합니다.
	 */
	private List<StudentDetailDTO> processExcelToStudentDTOs(MultipartFile excelFile,
			Map<String, Map<String, String>> codeMaps) {
		// (엑셀 파싱 로직 - 제공된 코드를 그대로 유지)
		if (excelFile == null || excelFile.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, String> gradeNameToCdMap = codeMaps.getOrDefault("gradeMap", new HashMap<>());
		Map<String, String> deptNameToCdMap = codeMaps.getOrDefault("deptMap", new HashMap<>());
		Map<String, String> entranceTypeNameToCdMap = codeMaps.getOrDefault("entranceTypeMap", new HashMap<>());
		Map<String, String> bankNameToCdMap = codeMaps.getOrDefault("bankMap", new HashMap<>());

		List<StudentDetailDTO> studentDtoList = new ArrayList<>();

		try (InputStream is = excelFile.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			int startRow = sheet.getFirstRowNum() + 1;
			int lastRowNumber = sheet.getLastRowNum();

			if (startRow > lastRowNumber)
				return new ArrayList<>();

			for (int i = startRow; i <= lastRowNumber; i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				StudentDetailDTO studentDto = new StudentDetailDTO();

				String korNm = readCellValue(row.getCell(1));
				String regiNo = readCellValue(row.getCell(2));
				String entranceDateRaw = readCellValue(row.getCell(8));
				String mobileNo = readCellValue(row.getCell(10));

				if (korNm.isBlank() && regiNo.isBlank() && entranceDateRaw.isBlank() && mobileNo.isBlank()) {
					continue;
				}

				studentDto.setRegiNo(regiNo);
				studentDto.setMobileNo(mobileNo);
				studentDto.setEntranceDate(entranceDateRaw);

				String excelBankName = readCellValue(row.getCell(15));
				String normalizedBankName = excelBankName.trim().replaceAll("\\s+", "");
				String bankNameKey;
				if (normalizedBankName.equals("국민은행")) {
					bankNameKey = "KB국민은행";
				} else if (normalizedBankName.equals("농협")) {
					bankNameKey = "농협은행";
				} else {
					bankNameKey = normalizedBankName;
				}
				String convertedBankCd = bankNameToCdMap.getOrDefault(bankNameKey, null);
				studentDto.setBankCode(convertedBankCd);

				String bankAccount = readCellValue(row.getCell(16));
				if (bankAccount == null || bankAccount.isBlank()) {
					bankAccount = "TEMP_0000000000";
				}
				studentDto.setBankAccount(bankAccount);

				String excelDept = readCellValue(row.getCell(7));
				String excelGrade = readCellValue(row.getCell(5));
				String excelEntranceType = readCellValue(row.getCell(9));
				String excelEmail = readCellValue(row.getCell(11));

				String convertedDeptCd = deptNameToCdMap.getOrDefault(excelDept, "DEP-ENGI-CSE");
				String convertedGradeCd = gradeNameToCdMap.getOrDefault(excelGrade, "1ST");
				String convertedEntranceTypeCd = entranceTypeNameToCdMap.getOrDefault(excelEntranceType, "SU-GC");

				studentDto.setUnivDeptCd(convertedDeptCd);
				studentDto.setGradeCd(convertedGradeCd);
				studentDto.setEntranceTypeCd(convertedEntranceTypeCd);
				studentDto.setEmail(excelEmail);

				studentDto.setEngLname(readCellValue(row.getCell(17)));
				studentDto.setEngFname(readCellValue(row.getCell(18)));

				studentDto.setGuardName(readCellValue(row.getCell(19)));
				studentDto.setGuardPhone(readCellValue(row.getCell(20)));

				studentDto.setTargetDept(readCellValue(row.getCell(21)));

				if (korNm != null && !korNm.isBlank()) {
					if (korNm.length() > 1) {
						studentDto.setLastName(korNm.substring(0, 1));
						studentDto.setFirstName(korNm.substring(1));
					} else {
						studentDto.setLastName(korNm.substring(0, 1));
						studentDto.setFirstName(" ");
					}
				} else {
					studentDto.setLastName(" ");
					studentDto.setFirstName(" ");
				}

				String gradYearRaw = entranceDateRaw;
				if (gradYearRaw != null && gradYearRaw.length() >= 4) {
					studentDto.setGradYear(gradYearRaw.substring(0, 4));
				} else {
					studentDto.setGradYear(String.valueOf(LocalDate.now().getYear()));
				}

				studentDto.setZipCode(readCellValue(row.getCell(12)));
				studentDto.setBaseAddr(readCellValue(row.getCell(13)));
				studentDto.setDetailAddr(readCellValue(row.getCell(14)));

				studentDto.setStuStatusCd("ENROLLED");
				studentDto.setGradExamYn("N");

				if (studentDto.getRegiNo().length() >= 6) {
					studentDto.setPwHash(passwordEncoder.encode(studentDto.getRegiNo().substring(0, 6)));
				} else {
					studentDto.setPwHash(passwordEncoder.encode("123456"));
				}

				// ⭐ 학과명 저장: 카운트 목적으로 사용 ⭐
				studentDto.setUnivDeptName(excelDept);
				studentDtoList.add(studentDto);
			}
		} catch (Exception e) {
			log.error("엑셀 파일 처리 실패:", e);
			throw new RuntimeException("엑셀 파일 처리 실패: " + e.getMessage(), e);
		}

		if (studentDtoList.isEmpty()) {
			throw new RuntimeException("엑셀 파일에서 등록할 유효한 학생 데이터가 발견되지 않았습니다.");
		}

		return studentDtoList;
	}

	/**
	 * 엑셀 셀 값을 문자열로 변환합니다.
	 */
	private String readCellValue(org.apache.poi.ss.usermodel.Cell cell) {
		// (셀 값 읽기 로직 - 제공된 코드를 그대로 유지)
		if (cell == null)
			return "";
		// ... (POI 셀 타입별 처리 로직 생략)
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
			}
			double numericValue = cell.getNumericCellValue();
			if (numericValue == Math.floor(numericValue)) {
				return new java.text.DecimalFormat("#").format(numericValue).trim();
			}
			return String.valueOf(numericValue).trim();

		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			try {
				CellType resultType = cell.getCachedFormulaResultType();
				if (resultType == CellType.STRING) {
					return cell.getStringCellValue().trim();
				} else if (resultType == CellType.NUMERIC) {
					double formulaNumericValue = cell.getNumericCellValue();
					if (DateUtil.isCellDateFormatted(cell)) {
						return new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
					} else if (formulaNumericValue == Math.floor(formulaNumericValue)) {
						return new java.text.DecimalFormat("#").format(formulaNumericValue).trim();
					}
					return String.valueOf(formulaNumericValue).trim();
				}
				return "";
			} catch (IllegalStateException e) {
				return "";
			}
		default:
			return "";
		}
	}

	// -------------------------------------------------------------------------
	// 통계 관련 로직
	// -------------------------------------------------------------------------

	@Override
	public Map<String, Long> readStudentStatusCounts() {
		// (통계 카운트 로직 - 제공된 코드를 그대로 유지)
		List<Map<String, Object>> rawList = mapper.selectStudentStatusCounts();
		Map<String, Long> statusCountsMap = new HashMap<>();

		for (Map<String, Object> rawMap : rawList) {
			String stuStatusName = null;
			Object statusCountObject = null;

			// MyBatis에서 반환되는 키 이름에 따라 적절히 설정해야 함
			if (rawMap.containsKey("stuStatusName")) {
				stuStatusName = (String) rawMap.get("stuStatusName");
				statusCountObject = rawMap.get("STATUSCOUNT");
			} else if (rawMap.containsKey("STUSTATUSNAME")) {
				stuStatusName = (String) rawMap.get("STUSTATUSNAME");
				statusCountObject = rawMap.get("STATUSCOUNT");
			} else if (rawMap.containsKey("stustatusname")) {
				stuStatusName = (String) rawMap.get("stustatusname");
				statusCountObject = rawMap.get("statuscount");
			}

			if (stuStatusName != null && statusCountObject != null) {
				Long statusCount = 0L;

				if (statusCountObject instanceof Number) {
					statusCount = ((Number) statusCountObject).longValue();
				} else {
					try {
						statusCount = Long.parseLong(statusCountObject.toString());
					} catch (NumberFormatException e) {
						log.error("카운트 값 파싱 오류: {}", statusCountObject);
					}
				}
				statusCountsMap.put(stuStatusName, statusCount);
			}
		}
		return statusCountsMap;
	}

	@Override
	public List<Map<String, Object>> readStudentCountsByCollege(String stuStatusName) {
		return mapper.selectStudentCountsByCollege(stuStatusName);
	}

	@Override
	public List<Map<String, Object>> readStudentCountsByDepartment(String stuStatusName, String collegeName) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stuStatusName", stuStatusName);
		paramMap.put("collegeName", collegeName);

		log.info("학과 통계 매퍼 호출 파라미터: {}", paramMap);
		return mapper.selectStudentCountsByDepartment(paramMap);
	}

	@Override
	public List<Map<String, Object>> readStudentCountsByGrade(String stuStatusName, String collegeName,
			String univDeptName) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("stuStatusName", stuStatusName);
		paramMap.put("collegeName", collegeName);
		paramMap.put("univDeptName", univDeptName);

		log.info("학년 통계 매퍼 호출 파라미터: {}", paramMap);
		return mapper.selectStudentCountsByGrade(paramMap);
	}

	// -------------------------------------------------------------------------
	// 엑셀 양식 생성 로직
	// -------------------------------------------------------------------------

	@Override
	public Workbook createStudentExcel(List<StudentDetailDTO> students) {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("학생 등록 양식");
		int rowNum = 0;

		// **********************************************
		// 1. 스타일 정의
		// **********************************************

		XSSFFont headerFont = (XSSFFont) workbook.createFont();
		headerFont.setBold(true);

		XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		XSSFFont instructionFont = (XSSFFont) workbook.createFont();
		instructionFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

		XSSFCellStyle instructionStyle = (XSSFCellStyle) workbook.createCellStyle();
		instructionStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		instructionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		instructionStyle.setFont(instructionFont);
		instructionStyle.setAlignment(HorizontalAlignment.LEFT);
		instructionStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// **********************************************
		// 2. 데이터 정의 (헤더/안내)
		// **********************************************
		String[] headers = { "학번", "이름", "주민번호", "성별", "학적상태", "학년", "단과대학", "학과", "입학일", "입학구분", "휴대전화", "이메일", "우편번호",
				"주소", "상세주소", "은행", "계좌번호", "영문 성", "영문 이름", "보호자 이름", "보호자 연락처", "복수전공" };

		String[] instructions = { "", "홍길동", "900101-1234567", "남자", "재학", "4학년", "인문대학", "국어국문학과", "20230302", "논술전형",
				"010-1234-5678", "example@univ.ac.kr", "12345", "서울특별시 강남구 역삼동", "123-45", "국민은행", "123456-01-123456",
				"HONG", "GILDONG", "홍박사", "010-9876-5432", "경영학과" };

		// 2. 헤더 행 생성 및 스타일 적용
		Row headerRow = sheet.createRow(rowNum++);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		// 2.5. 기입 안내 행 생성 및 스타일 적용
		Row instructionRow = sheet.createRow(rowNum++);
		for (int i = 0; i < instructions.length; i++) {
			Cell cell = instructionRow.createCell(i);
			cell.setCellValue(instructions[i]);
			cell.setCellStyle(instructionStyle);
		}

		// **********************************************
		// 3. 데이터 행 채우기 (기존 데이터)
		// **********************************************
		for (StudentDetailDTO student : students) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;

			// ... (데이터 셀에 쓰기 로직 생략)
			row.createCell(colNum++).setCellValue(student.getStudentNo());
			row.createCell(colNum++).setCellValue(student.getStudentName());
			row.createCell(colNum++).setCellValue(student.getRegiNo());
			row.createCell(colNum++).setCellValue(student.getGender());

			row.createCell(colNum++).setCellValue(student.getStuStatusName());
			row.createCell(colNum++).setCellValue(student.getGradeName());
			row.createCell(colNum++).setCellValue(student.getCollegeName());
			row.createCell(colNum++).setCellValue(student.getUnivDeptName());

			row.createCell(colNum++).setCellValue(student.getEntranceDate());
			row.createCell(colNum++).setCellValue(student.getEntranceTypeName());

			row.createCell(colNum++).setCellValue(student.getMobileNo());
			row.createCell(colNum++).setCellValue(student.getEmail());

			row.createCell(colNum++).setCellValue(student.getZipCode());
			row.createCell(colNum++).setCellValue(student.getBaseAddr());
			row.createCell(colNum++).setCellValue(student.getDetailAddr());

			row.createCell(colNum++).setCellValue(student.getBankName());
			row.createCell(colNum++).setCellValue(student.getBankAccount());

			row.createCell(colNum++).setCellValue(student.getEngLname());
			row.createCell(colNum++).setCellValue(student.getEngFname());
			row.createCell(colNum++).setCellValue(student.getGuardName());
			row.createCell(colNum++).setCellValue(student.getGuardPhone());
			row.createCell(colNum++).setCellValue(student.getTargetDept());
		}

		// **********************************************
		// 4. 컬럼 너비 자동 조정
		// **********************************************
		final int MIN_WIDTH = 256 * 15;
		final int PADDING_WIDTH = 256 * 7;

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
			int currentWidth = sheet.getColumnWidth(i);
			int calculatedWidth = Math.max(currentWidth, MIN_WIDTH);
			int finalWidth = calculatedWidth + PADDING_WIDTH;
			sheet.setColumnWidth(i, finalWidth);
		}

		return workbook;
	}

	/**
	 * 엑셀 배치 미리보기 (학과별 카운트 계산)
	 *
	 * @return totalCount(전체 학생 수), detailCounts(학과별 학생 수)를 포함한 Map
	 */
	@Override
	public Map<String, Object> previewBatchStudentInfo(MultipartFile excelFile) {

		Map<String, Object> resultMap = new HashMap<>();

		if (excelFile == null || excelFile.isEmpty()) {
			resultMap.put("success", false);
			resultMap.put("message", "엑셀 파일을 찾을 수 없습니다.");
			return resultMap;
		}

		try {
			// 1. 공통 코드 및 학과 목록 조회 (validation을 위해 필요)
			Map<String, Map<String, String>> codeMaps = readCommonCodeMaps();

			// 2. 엑셀 파싱 및 학과별 카운트 계산 (요청하신 핵심 로직)
			// processExcelToStudentDTOs는 StudentDetailDTO 리스트를 반환합니다.
			List<StudentDetailDTO> studentDtoList = processExcelToStudentDTOs(excelFile, codeMaps);

			// ⭐ StudentDetailDTO 리스트를 사용하여 학과별 인원수 Map 집계 ⭐
			Map<String, Integer> previewCountsByDept = new HashMap<>();
			studentDtoList.forEach(studentDto -> {
				// processExcelToStudentDTOs에서 엑셀의 학과명은 'UnivDeptName'에 저장됩니다.
				String deptName = studentDto.getUnivDeptName();

				if (StringUtils.hasText(deptName)) {
					// merge를 사용하여 해당 학과명에 대한 카운트를 1 증가시킵니다.
					previewCountsByDept.merge(deptName, 1, Integer::sum);
				} else {
					// 학과명이 빈 경우, 별도로 카운트할 수 있습니다.
					previewCountsByDept.merge("학과 정보 없음", 1, Integer::sum);
				}
			});

			// 3. 응답 Map 구성
			int totalCount = previewCountsByDept.values().stream().mapToInt(Integer::intValue).sum();

			resultMap.put("success", true);
			resultMap.put("totalCount", totalCount);
			// 엑셀에 있는 학과와 인원수만 포함된 Map을 'detailCounts'로 반환
			resultMap.put("detailCounts", previewCountsByDept); // <== 이 데이터가 프론트로 전달됩니다.
			resultMap.put("message", "엑셀 파일 분석 및 미리보기가 완료되었습니다.");

		} catch (RuntimeException e) {
			log.error("엑셀 파일 미리보기 처리 실패: {}", e.getMessage(), e);
			resultMap.put("success", false);
			resultMap.put("message", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
		} catch (Exception e) {
			log.error("예상치 못한 오류 발생:", e);
			resultMap.put("success", false);
			resultMap.put("message", "서버 오류로 파일 처리 실패.");
		}

		return resultMap;
	}

	/**
	 * 엑셀 배치 미리보기 (학과별 카운트 계산) 기존 메서드는 StudentDetailDTO 리스트를 사용하도록 변경되어 더 이상 필요하지
	 * 않습니다. 다만, 기존 인터페이스를 유지하기 위해 StudentDetailDTO 리스트를 반환하도록 내부 구현을 변경합니다.
	 */

}