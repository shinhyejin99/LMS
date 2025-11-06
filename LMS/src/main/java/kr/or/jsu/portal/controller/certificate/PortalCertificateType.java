package kr.or.jsu.portal.controller.certificate;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 9.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 9.     	정태일	          최초 생성
 *
 * </pre>
 */
public enum PortalCertificateType {
    ENROLLMENT("ENROLL", "재학증명서", "S"),
    GRADUATION("GRAD", "졸업증명서", "S"),
    TRANSCRIPT("TRANS", "성적증명서", "S"),
    EMPLOYMENT("EMP", "재직증명서", "PF"),
    CAREER("CAREER", "경력증명서", "PF");

    private final String code;
    private final String displayName;
    private final String shareScope; // S, P, F, SF, PF, ALL 등

    PortalCertificateType(String code, String displayName, String shareScope) {
        this.code = code;
        this.displayName = displayName;
        this.shareScope = shareScope;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getShareScope() { return shareScope; }

    // 코드로 찾기 위한 헬퍼 메소드
    public static PortalCertificateType fromCode(String code) {
        for (PortalCertificateType type : PortalCertificateType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        // Enum에 정의되지 않은 코드는 예외 처리 또는 null 반환
        return null; // 또는 throw new IllegalArgumentException("알 수 없는 증명서 코드: " + code);
    }
}
