package kr.or.jsu.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

/**
 * 고용24 api 목록 응답 - 루트
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
@JacksonXmlRootElement(localName = "dhsOpenEmpInfoList")
public class Work24ListResponseDTO {

	@JacksonXmlProperty(localName = "total")
    private Integer total; // 전체 개수
    
    @JacksonXmlProperty(localName = "startPage")
    private Integer startPage; // 시작 페이지
    
    @JacksonXmlProperty(localName = "display")
    private Integer display; // 한 페이지 표시 개수
    
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "dhsOpenEmpInfo")
    private List<Work24JobDTO> jobList;
}
