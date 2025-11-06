package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.CertificateReqVO;

@Mapper
public interface CertificateReqMapper {
	public int insertCertificateReq(CertificateReqVO certificateReq);
	public List<CertificateReqVO> selectCertificateReqList();
	public int updateCertificateReq(CertificateReqVO certificateReq);
	public int deleteCertificateReq(String certReqId);
}
