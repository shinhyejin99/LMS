package kr.or.jsu.core.utils.enums;


/**
 * D:\LMSFileRepository 하위의 파일 저장 경로를 제한하는 ENUM입니다. <br>
 * @author 송태호
 * @since 2025. 9. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 28.     	송태호	          최초 생성
 *
 * </pre>
 */
public enum FileUploadDirectory {
    DEVTEMP("/devtemp", "개발 중 테스트할 때 여기로 저장")
    , IDPHOTO("/idphoto", "사용자들의 증명사진 여기로 저장")
    , CLASSROOM("/classroom", "클래스룸 첨부파일 저장경로")
    ,
    ;

    private final String directory;    // 이 경로에
    private final String fileSortDesc; // 어떤 파일종류를 저장하는지 설명

    FileUploadDirectory(String directory, String fileSortDesc) {
        this.directory = directory;
        this.fileSortDesc = fileSortDesc;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFileSortDesc() {
        return fileSortDesc;
    }
}