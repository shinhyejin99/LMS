package kr.or.jsu.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

/**
 * 고용24 API 상세 응답
 * @author 송태호
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
@JacksonXmlRootElement(localName = "dhsOpenEmpInfoDetailRoot")
public class Work24DetailDTO {

	@JacksonXmlProperty(localName = "empSeqno")
    private String empSeqno;
    
    @JacksonXmlProperty(localName = "empWantedTitle")
    private String empWantedTitle;
    
    @JacksonXmlProperty(localName = "empBusiNm")
    private String empBusiNm;
    
    @JacksonXmlProperty(localName = "coClcdNm")
    private String coClcdNm;
    
    @JacksonXmlProperty(localName = "empWantedStdt")
    private String empWantedStdt;
    
    @JacksonXmlProperty(localName = "empWantedEndt")
    private String empWantedEndt;
    
    @JacksonXmlProperty(localName = "empWantedHomepg")
    private String empWantedHomepg; // 회사 홈페이지
    
    @JacksonXmlProperty(localName = "empWantedHomepgDetail")
    private String empWantedHomepgDetail; // 채용 상세 페이지
    
    @JacksonXmlProperty(localName = "empWantedMobileUrl")
    private String empWantedMobileUrl;
    
    @JacksonXmlProperty(localName = "regLogImgNm")
    private String regLogImgNm;
    
    @JacksonXmlProperty(localName = "empWantedTypeNm")
    private String empWantedTypeNm;
    
    @JacksonXmlProperty(localName = "recrCommCont")
    private String recrCommCont; // 공통 자격요건
    
    @JacksonXmlProperty(localName = "empSubmitDocCont")
    private String empSubmitDocCont; // 제출 서류
    
    @JacksonXmlProperty(localName = "empRcptMthdCont")
    private String empRcptMthdCont; // 접수 방법
    
    @JacksonXmlProperty(localName = "inqryCont")
    private String inqryCont; // 문의처
    
    @JacksonXmlProperty(localName = "empnEtcCont")
    private String empnEtcCont; // 기타사항
    
    @JacksonXmlProperty(localName = "empnRecrSummaryCont")
    private String empnRecrSummaryCont; // 채용요약
    
    // empRecrList, empSelsList 등은 추후에 필요시 추가
}
