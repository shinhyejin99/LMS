package kr.or.jsu.ai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vector Store 설정 파일 (bean으로 등록)
 * => VectorStore 객체를 사용할 수 있게 함
 * => 따라서 검색 기능을 위한 최소한의 환경 설정이라고 생각하면 됨!
 * @author 김수현
 * @since 2025. 10. 23.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 23.     	김수현	          최초 생성
 *
 * </pre>
 */
@Configuration
public class VectorStoreConfig {

    /**
     * @param embeddingModel : Spring 컨테이너로부터 주입(DI)받을 의존성 : 텍스트를 벡터로 변환하는 모델
     * @return : 벡터 저장소 역할 (메서드가 Spring 컨테이너에 등록할 Bean의 타입)
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {

    	// 실제 VectorStore 객체를 생성하여 반환
    	// SimpleVectorStore : 인메모리 기반의 VectorStore 구현체
    	// VectorStore는 저장 및 검색을 위해 EmbeddingModel이 필요함.
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
