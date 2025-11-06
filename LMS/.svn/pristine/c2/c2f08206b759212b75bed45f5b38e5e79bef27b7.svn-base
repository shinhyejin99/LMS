package kr.or.jsu.portal.controller.certificate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
public class PortalCertificateConfig {

    // Spring Security 역할 문자열을 접근 가능한 shareScope 문자열 집합에 매핑
    // "S", "P", "F", "SF", "PF", "ALL" 등은 CertificateVO의 shareScopeCd 값과 매칭됩니다.
    public static final Map<String, Set<String>> ROLE_STRING_TO_SHARE_SCOPES = Map.ofEntries(
        Map.entry("ROLE_STUDENT", new HashSet<>(Arrays.asList("S", "SF", "ALL"))),
        Map.entry("ROLE_PROFESSOR", new HashSet<>(Arrays.asList("P", "PF", "ALL"))),
        Map.entry("ROLE_STAFF", new HashSet<>(Arrays.asList("F", "SF", "PF", "ALL"))),
        Map.entry("ROLE_ADMIN", new HashSet<>(Arrays.asList("S", "P", "F", "SF", "PF", "ALL"))) // 관리자는 모든 증명서에 접근 가능
    );

    /**
     * 현재 사용자의 Spring Security 역할 문자열과 모든 증명서 목록을 기반으로
     * 사용 가능한 증명서 목록을 필터링하여 반환합니다.
     *
     * @param userRoleString 현재 사용자의 Spring Security 역할 문자열 (예: "ROLE_STUDENT")
     * @param allCertificates 모든 CertificateType 목록
     * @return 필터링된 CertificateType 목록
     */
    public static List<PortalCertificateType> getAvailableCertificates(String userRoleString, List<PortalCertificateType> allCertificates) {
        Set<String> allowedScopes = ROLE_STRING_TO_SHARE_SCOPES.getOrDefault(userRoleString, Collections.emptySet());
        return allCertificates.stream()
                .filter(cert -> allowedScopes.contains(cert.getShareScope()))
                .collect(Collectors.toList());
    }
}
