package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.vo.PushNoticeTargetVO;

@Mapper
public interface PushNoticeTargetMapper {

	// 알림 대상 테이블(PUSH_NOTICE_TARGET)에 데이터 삽입 (단일)
	public int insertPushNoticeTarget(PushNoticeDetailDTO target);

	// 알림 대상 테이블의 CHECK_AT 컬럼 업데이트 (읽음 처리)
	int updateCheckAt(@Param("pushId") String pushId, @Param("targetUserId") String userId);

	public List<PushNoticeTargetVO> selectPushNoticeTargetList();

	// 부서명 조회 메서드 정의
	public String selectSenderDepartmentName(@Param("senderId") String senderId);

	// 엑셀 일괄 삽입을 위한 다중 대상 삽입 메서드
	public int insertPushNoticeTargets(@Param("list") List<PushNoticeTargetVO> targetList);

    // ⭐️ 수정 제안: List<String>으로 변경 (중복 데이터 문제 해결)
	public List<String> findProfNoByUserId(String dbUserId);

    // ⭐️ 수정 제안: List<String>으로 변경 (중복 데이터 문제 해결)
	public List<String> findStaffNoByUserId(String dbUserId);

    // ⭐️ 오류 발생 메소드: List<String>으로 변경 (중복 데이터 문제 해결)
	public List<String> findStudentNoByUserId(String dbUserId);

	public List<String> selectStudentUserIdsAll();

	public List<String> selectStudentUserIdsByDepartment(@Param("targetCode") String targetCode, @Param("gradeCode") String gradeCode);

	public List<String> selectStudentUserIdsByGrade(String targetCode);
}