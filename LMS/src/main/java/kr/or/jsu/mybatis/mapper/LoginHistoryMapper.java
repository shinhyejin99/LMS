package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.LoginHistoryVO;

@Mapper
public interface LoginHistoryMapper {

	// 로그인 이력 등록
	public int insertLoginHistory(LoginHistoryVO loginHistory);

	// 로그인 이력 전체 목록 조회
	public List<LoginHistoryVO> selectLoginHistoryList();

	// 특정 로그인 이력 조회
	public LoginHistoryVO selectLoginHistory(String loginHistoryId);

	// 로그인 이력 삭제
	public int deleteLoginHistory(String loginHistoryId);
}
