package kr.or.jsu.ai.dto;

import lombok.Data;

@Data
public class CollegeUnvieDeptInfoDTO { 
    private String entityType;      // 'COLLEGE' 또는 'DEPARTMENT'
    private String univDeptCd;      // 학과 코드
    private String univDeptName;    // 학과명
    private String CollegeCd;       // 단과대 코드
    private String CollegeName;     // 단과대명
    private String location;        // 위치
    private String telNo;           // 전화번호
    private String deanName;        // 대학장 이름 (단과대학용)
    private String headOfDeptName;  // 학과장 이름 (학과용)
}