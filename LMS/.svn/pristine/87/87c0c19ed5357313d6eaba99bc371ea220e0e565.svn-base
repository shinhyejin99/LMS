package kr.or.jsu.ai.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * VectorStore 서비스 : PDF 문서를 저장하고 검색하는 저장소 관리자 라고 생각
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
@Slf4j
@Service
public class VectorStoreService {
	@Value("${spring.ai.vectorstore.file:./vector-store.json}")
    private String vectorStoreFilePath;

	// PDF 파일들이 있는 폴더 경로 설정
	@Value("${spring.ai.documents.path:./documents}")
	private String documentsPath;

	private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final TextSplitter textSplitter;

    /**
     * 생성자
     * @param vectorStore
     * @param embeddingModel
     */
    public VectorStoreService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel; 			// 주입받은 Vector 빈을 클래스 필드에 할당
        this.textSplitter = TokenTextSplitter.builder() // 텍스트 분할
            .withChunkSize(300) 						// 문서를 최대 500 토큰 크기로 분할
            .withMinChunkSizeChars(100) 				// 최소 100자 이상일 때한 유효한 청크로
            .build();
    }

    /**
     * 서버 시작 시 자동 실행(벡터 저장소를 초기화)
     * @throws IOException
     */
    @PostConstruct // 빈 생성 후 의존 관계 주입이 완료된 후 실행되는 '초기화 콜백'을 적용(딱 한번이겠죠?)
    public void initVectorStore() throws IOException {
        log.info("===> RAG 문서 인덱싱 시작...");

        // vector-store.json 파일이 있으면 로드
        File vectorStoreFile = new File(vectorStoreFilePath);
        if (vectorStoreFile.exists()) {
            ((SimpleVectorStore) vectorStore).load(vectorStoreFile);
            log.info("===> 벡터 저장소 로드 완료");
        // 없으면 documents/ 폴더의 PDF 자동 로드
        } else {
            log.info("===> 벡터 저장소 파일 없음. documents/ 폴더의 PDF 자동 로드함");
            loadDocumentsFromFolder();
        }
    }
    // ===================
    // 문서 로드 및 인덱싱 관련
    // ===================

    /**
     * 폴더에서 PDF 파일 자동 로드 메서드
     */
    private void loadDocumentsFromFolder() {
        File documentsFolder = new File(documentsPath);

        if (!documentsFolder.exists()) {
            log.warn("===> 문서 폴더가 없습니다: {}", documentsPath);
            log.info("===> 폴더 생성 중...");
            documentsFolder.mkdirs();
            return;
        }

        // 폴더에서 pdf 파일을 찾아 배열로 가져옴
        File[] pdfFiles = documentsFolder.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".pdf")
        );

        if (pdfFiles == null || pdfFiles.length == 0) {
            log.info("===> 로드할 PDF 파일이 없습니다.");
            return;
        }

        log.info("===> {}개의 PDF 파일 발견", pdfFiles.length);

        for (File pdfFile : pdfFiles) {
            try {
                log.info("===> 로드 중: {}", pdfFile.getName());

                // File → Resource 변환
                Resource pdfResource = new org.springframework.core.io.FileSystemResource(pdfFile);

                // 메타데이터 설정
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("filename", pdfFile.getName()); // 원본 파일명 저장
                metadata.put("category", extractCategory(pdfFile.getName()));
                metadata.put("loadedAt", System.currentTimeMillis());

                // PDF 인덱싱(문서 읽고 분할 -> 벡터 저장소에 추가)
                indexPdfDocument(pdfResource, metadata);

                log.info("===> 로드 완료: {}", pdfFile.getName());

            } catch (Exception e) {
                log.error("===> PDF 로드 실패: {}", pdfFile.getName(), e);
            }
        }

        log.info("===> 전체 PDF 로드 완료!");
    }

    /**
     * 파일명에서 카테고리 추출 메서드
     */
    private String extractCategory(String filename) {
        // 예: "학칙_2025.pdf" → "학칙"
        if (filename.contains("학칙")) return "학칙";
        if (filename.contains("수강신청")) return "수강신청규정";
        if (filename.contains("전과")) return "전과규정";
        if (filename.contains("졸업")) return "졸업요건";
        return "기타";
    }

    /**
     * PDF 문서 로드 및 인덱싱(: 문서 읽고 분할 -> 벡터 저장소에 추가)
     */
    public void indexPdfDocument(Resource pdfResource, Map<String, Object> metadata) {
        try {
            log.info("===> PDF 문서 로드 시작: {}", pdfResource.getFilename());

            // PDF → Document 변환 (pdf 리더로 파일 내용읽고 변환)
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
            List<Document> documents = pdfReader.get();

            // 텍스트 청크 분할
            List<Document> chunks = textSplitter.split(documents);

            // 메타데이터 추가
            chunks.forEach(chunk -> chunk.getMetadata().putAll(metadata));

            // VectorStore에 저장 (분할된 청크들을 EmbeddingModel로 벡터화한 후 저장하는 것.)
            vectorStore.add(chunks);

            log.info("===> PDF 인덱싱 완료: {} 개 청크", chunks.size());

            // 벡터 저장소를 파일로 저장
            saveVectorStore();

        } catch (Exception e) {
            log.error("===> PDF 로드 실패", e);
        }
    }

    /**
     * 텍스트 문서 인덱싱(pdf 파일 아닐 때 사용)
     */
    public void indexDocument(String content, Map<String, Object> metadata) {
        Document document = new Document(content, metadata);
        List<Document> chunks = textSplitter.split(document);
        vectorStore.add(chunks);

        log.info("===> 문서 인덱싱 완료: {} 개 청크", chunks.size());
        saveVectorStore();
    }


    // ===================
    // 검색 및 저장 관련
    // ===================

    /**
     * 유사 문서 검색 메서드
     */
    public List<Document> searchSimilarDocuments(String query, int topK, double threshold) {
    	// 검색 요청 객체 생성
        SearchRequest searchRequest = SearchRequest.builder()
            .query(query) 						// 질의 텍스트 설정
            .topK(topK) 						// 검색 결과 중 가장 유사한 문서를 몇개 반환할지 설정
            .similarityThreshold(threshold) 	// 유사도 임계값 설정.(이보다 낮으면 제외)
            .build();

        // 검색 실행
        List<Document> results = vectorStore.similaritySearch(searchRequest);

        // ⭐ 디버깅 로그 추가
        log.info("===> 검색어: '{}'", query);
        log.info("===> 검색 결과: {}개", results.size());

        if (results.isEmpty()) {
            log.warn("===> 검색 결과 없음!");
        } else {
            for (int i = 0; i < results.size(); i++) {
                Document doc = results.get(i);
                log.info("===> 결과 {}: {}", i+1, doc.getText().substring(0, Math.min(100, doc.getText().length())));
            }
        }

        // VectorStore에 저장된 벡터들과 유사도 검색 실행 후 결과 반환
        return results;
    }

    /**
     * VectorStore 파일로 저장 메서드 => vector-store.json에 저장(json 형태로)
     * ex)
	 * {
	 *    "text": "제1장 총칙...",
	 *    "vector": [0.2, 0.5, ...],
	 *    "metadata": {"filename": "학칙.pdf"}
	 * }
     */
    private void saveVectorStore() {
        try {
            File file = new File(vectorStoreFilePath);
            ((SimpleVectorStore) vectorStore).save(file);
            log.info("===> 벡터 저장소 저장 완료");
        } catch (Exception e) {
            log.error("===> 벡터 저장소 저장 실패", e);
        }
    }
}
