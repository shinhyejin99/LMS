package kr.or.jsu.classregist.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 데모 수강신청 항목 (학생-강의 1:1 매핑)
 */
@Data
public class DemoRegistrationItem {

    @NotBlank
    private String studentNo;

    @NotBlank
    private String lectureId;
}
