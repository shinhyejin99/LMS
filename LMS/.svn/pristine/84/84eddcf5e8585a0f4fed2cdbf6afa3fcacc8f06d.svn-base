package kr.or.jsu.core.utils.job;

import java.util.List;
import java.util.Map;

public class JobMatchingUtil {

	/**
     * 학과명별 관련 직무 키워드
     */
    private static final Map<String, List<String>> MAJOR_KEYWORDS = Map.of(
        "소프트웨어", List.of("소프트웨어", "SW", "IT", "개발", "프로그래밍", "시스템", "웹", "앱", "데이터", "보안"),
        "컴퓨터공학", List.of("컴퓨터", "소프트웨어", "IT", "개발", "프로그래밍", "시스템", "네트워크", "보안"),
        "전자공학", List.of("전자", "전기", "하드웨어", "회로", "반도체", "제어", "임베디드"),
        "기계공학", List.of("기계", "설계", "CAD", "제조", "생산", "자동화", "설비"),
        "경영학", List.of("경영", "기획", "마케팅", "영업", "인사", "재무", "회계", "전략"),
        "디자인", List.of("디자인", "UI", "UX", "그래픽", "영상", "편집", "3D"),
        "전기공학", List.of("전기", "전자", "전력", "제어", "자동화", "설비")
        // 다른 학과는 추후 추가
    );
    
    /**
     * 학과명으로 관련 키워드 조회
     */
    public static List<String> getKeywordsByMajor(String majorName) {
        if (majorName == null) {
            return List.of();
        }
        
        // 정확히 일치하는 경우
        if (MAJOR_KEYWORDS.containsKey(majorName)) {
            return MAJOR_KEYWORDS.get(majorName);
        }
        
        // 부분 일치 (소프트웨어학과 -> 소프트웨어) 하는 경우
        for (Map.Entry<String, List<String>> entry : MAJOR_KEYWORDS.entrySet()) {
            if (majorName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 매칭 안 되면 학과명 자체 반환
        return List.of(majorName);
    }
    
    /**
     * 직무명이 키워드 리스트와 매칭되는지 확인
     */
    public static boolean isMatchingJob(String jobName, List<String> keywords) {
        if (jobName == null || keywords == null || keywords.isEmpty()) {
            return false;
        }
        
        for (String keyword : keywords) {
            if (jobName.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
}
