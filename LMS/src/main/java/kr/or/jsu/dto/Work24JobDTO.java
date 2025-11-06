package kr.or.jsu.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

/**
 * 고용24 api 목록 응답 - 채용공고 개별
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
@JacksonXmlRootElement(localName = "dhsOpenEmpInfo")
public class Work24JobDTO {

	@JacksonXmlProperty(localName = "empSeqno")
    private String empSeqno; // 공고순번
    
    @JacksonXmlProperty(localName = "empWantedTitle")
    private String empWantedTitle; // 채용제목
    
    @JacksonXmlProperty(localName = "empBusiNm")
    private String empBusiNm; // 회사명
    
    @JacksonXmlProperty(localName = "coClcdNm")
    private String coClcdNm; // 기업규모 (대기업, 중소기업 등)
    
    @JacksonXmlProperty(localName = "empWantedStdt")
    private String empWantedStdt; // 접수시작일 (yyyyMMdd)
    
    @JacksonXmlProperty(localName = "empWantedEndt")
    private String empWantedEndt; // 접수종료일 (yyyyMMdd)
    
    @JacksonXmlProperty(localName = "empWantedTypeNm")
    private String empWantedTypeNm; // 채용형태 (정규직, 계약직 등)
    
    @JacksonXmlProperty(localName = "regLogImgNm")
    private String regLogImgNm; // 로고이미지 URL
    
    @JacksonXmlProperty(localName = "empWantedHomepgDetail")
    private String empWantedHomepgDetail; // 상세페이지 URL
    
    @JacksonXmlProperty(localName = "empWantedMobileUrl")
    private String empWantedMobileUrl; // 모바일 URL
}
