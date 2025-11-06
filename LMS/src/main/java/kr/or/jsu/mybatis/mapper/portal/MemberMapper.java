package kr.or.jsu.mybatis.mapper.portal;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.MemberVO;

@Mapper
public interface MemberMapper {
	
	MemberVO selectMember(String memId);
	
}
