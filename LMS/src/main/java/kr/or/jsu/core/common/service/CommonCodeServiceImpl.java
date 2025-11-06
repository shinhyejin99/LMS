package kr.or.jsu.core.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.mybatis.mapper.CommonCodeMapper;
import kr.or.jsu.vo.CommonCodeVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

	private final CommonCodeMapper ccMapper;

	@Override
	public List<CommonCodeVO> readCommonCodeList(CommonCodeSort sortCode) {
		return ccMapper.selectCommonCodeList(sortCode.getCode());
	}

	@Override
	public String getCurrentYearterm() {
		return ccMapper.selectCurrentYearterm();
	}

	@Override
	public Integer getMaxCreditByGrade(String gradeCd, String termCd) {
		return ccMapper.selectMaxCreditByGrade(gradeCd, termCd);
	}
}