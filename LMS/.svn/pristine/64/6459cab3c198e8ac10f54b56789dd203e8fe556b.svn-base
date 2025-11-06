package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.PortalRecruitApiVO;

@Mapper
public interface PortalRecruitApiMapper {

	// 공고 API 등록
    public int insertPortalRecruitApi(PortalRecruitApiVO portalRecruitApi);

    // 공고 API 전체 목록 조회
    public List<PortalRecruitApiVO> selectPortalRecruitApiList();

    // 특정 공고 API 조회 
    public PortalRecruitApiVO selectPortalRecruitApi(String apiRecruitId);

    // 공고 API 수정
    public int updatePortalRecruitApi(PortalRecruitApiVO portalRecruitApi);

    // 공고 API 삭제 
    public int deletePortalRecruitApi(String apiRecruitId);
}
