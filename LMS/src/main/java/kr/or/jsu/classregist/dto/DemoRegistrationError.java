package kr.or.jsu.classregist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 데모 수강신청 처리 중 실패한 항목과 사유
 */
@Data
@AllArgsConstructor
public class DemoRegistrationError {

    private String studentNo;
    private String lectureId;
    private String message;
}
