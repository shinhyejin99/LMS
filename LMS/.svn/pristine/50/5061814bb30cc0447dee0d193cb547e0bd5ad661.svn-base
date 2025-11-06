package kr.or.jsu.core.utils.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

@Service
public class PdfService {
	@Autowired
    private TemplateEngine templateEngine;

	@Autowired
    private ResourceLoader resourceLoader;

	/**
     * 범용 PDF 생성 메서드
     */
    public byte[] generatePdf(String templateName, Object data) throws Exception {

        // Thymeleaf로 HTML 생성
    	Context context = new Context();
        context.setVariable("data", data);

        // 이미지를 Base64로 인코딩해서 Context에 추가
        String stampImageBase64 = getImageAsBase64("classpath:static/images/JSU대학교인장.png");
        context.setVariable("stampImage", stampImageBase64);

        String html = templateEngine.process(templateName, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        // 한글 폰트 설정
        try {
            String fontPath = getClass().getResource("/fonts/NanumGothic.ttf").toString();
            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            System.err.println("폰트 로드 실패: " + e.getMessage());
        }

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 이미지를 Base64로 인코딩
     */
    private String getImageAsBase64(String imagePath) throws Exception {
        try {
            Resource resource = resourceLoader.getResource(imagePath);
            InputStream inputStream = resource.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            System.err.println("이미지 로드 실패: " + e.getMessage());
            return ""; // 이미지 없으면 빈 문자열
        }
    }

}
