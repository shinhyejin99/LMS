package kr.or.jsu.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 채용정보 api 관련 설정파일
 * 
 * @author 김수현
 * @since 2025. 9. 29.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	김수현	          최초 생성
 *
 *      </pre>
 */
@Configuration
public class RestTemplateConfig {

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * XmlMapper Bean 등록 XML 응답을 Java 객체로 변환할 때 사용
	 */
	@Bean
	XmlMapper xmlMapper() {
		XmlMapper mapper = new XmlMapper();

		// 알 수 없는 필드가 있어도 무시 (API가 필드를 추가해도 에러 안남)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 빈 문자열을 null로 처리
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

		return mapper;
	}

	/**
	 * ObjectMapper Bean 등록 JSON 직렬화/역직렬화 시 사용 LocalDate, LocalDateTime 등 Java 8
	 * 날짜/시간 타입 처리
	 */
	@Bean
	ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()) // Java 8 날짜/시간 모듈 추가
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로
				.build();
	}
}
