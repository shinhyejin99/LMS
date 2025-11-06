package kr.or.jsu.classregist.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * 데모 수강신청 요청 payload
 */
@Data
public class DemoRegistrationRequest {

    @Valid
    private List<DemoRegistrationItem> registrations;

    private List<String> studentNos;
    private List<String> lectureIds;

    public List<DemoRegistrationItem> buildRegistrationItems() {
        List<DemoRegistrationItem> result = new ArrayList<>();

        if (registrations != null) {
            result.addAll(registrations);
        }

        if (studentNos != null && lectureIds != null) {
            for (String studentNo : studentNos) {
                if (studentNo == null || studentNo.isBlank()) {
                    continue;
                }
                for (String lectureId : lectureIds) {
                    if (lectureId == null || lectureId.isBlank()) {
                        continue;
                    }
                    DemoRegistrationItem item = new DemoRegistrationItem();
                    item.setStudentNo(studentNo);
                    item.setLectureId(lectureId);
                    result.add(item);
                }
            }
        }

        return result;
    }
}
