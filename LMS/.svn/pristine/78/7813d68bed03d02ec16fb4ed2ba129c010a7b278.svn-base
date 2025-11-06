package kr.or.jsu.core.utils.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PrettyPrint {
	private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // LocalDate/LocalDateTime 등 지원
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
    /** 아무 객체나 받아 Jackson으로 예쁘게(들여쓰기) 직렬화한 문자열을 반환 */
    public static String pretty(Object value) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter()
                         .writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON pretty serialization failed", e);
        }
    }
}
