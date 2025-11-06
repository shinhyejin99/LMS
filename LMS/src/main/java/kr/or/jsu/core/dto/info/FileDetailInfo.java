package kr.or.jsu.core.dto.info;

import java.io.File;
import java.io.Serializable;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 비밀번호를 제외한, 정보 조회에 사용할 객체
 * 
 * @author 송태호
 * @since 2025.10.01.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.01.     	송태호	          최초 생성
 *
 * </pre>
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"fileOrder", "fileId"})
public class FileDetailInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer fileOrder;
	private String fileId;
	private String originName;
	private String extension;
	private String saveDir;
	private Long fileSize;
	private String saveName;
	
	public Resource getRealFile() {
		if (saveDir == null || saveName == null)
			throw new RuntimeException("파일 경로 정보가 불충분합니다.");
	
        try {
            File file = new File(saveDir, saveName);
            if (!file.exists())
            	throw new RuntimeException("실제 파일을 찾을 수 없습니다: " + file.getAbsolutePath());
            if (!file.isFile())
            	throw new RuntimeException("파일이 아닙니다: " + file.getAbsolutePath());

            return new FileSystemResource(file);
        } catch (Exception e) {
            throw new RuntimeException("파일 리소스를 불러오는 중 오류가 발생했습니다.", e);
        }
    }
}