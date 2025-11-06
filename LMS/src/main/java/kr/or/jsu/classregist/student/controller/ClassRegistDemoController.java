package kr.or.jsu.classregist.student.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.or.jsu.classregist.dto.DemoRegistrationRequest;
import kr.or.jsu.classregist.dto.DemoRegistrationResult;
import kr.or.jsu.classregist.student.service.ClassRegistDemoService;
import lombok.RequiredArgsConstructor;

/**
 * 발표 시연용 수강신청/삭제 엔드포인트
 */
@RestController
@RequestMapping("/classregist/demo")
@RequiredArgsConstructor
public class ClassRegistDemoController {

    private final ClassRegistDemoService demoService;

    /**
     * 시연용 수강신청 일괄 실행
     */
    @PostMapping("/apply")
    public ResponseEntity<DemoRegistrationResult> apply(@Valid @RequestBody DemoRegistrationRequest request) {
        DemoRegistrationResult result = demoService.applyForDemo(request.buildRegistrationItems());
        return ResponseEntity.ok(result);
    }

    /**
     * 시연용 수강신청 데이터 정리
     */
    @PostMapping("/apply/reset")
    public ResponseEntity<DemoRegistrationResult> cleanup(@Valid @RequestBody DemoRegistrationRequest request) {
        DemoRegistrationResult result = demoService.cleanupDemo(request.buildRegistrationItems());
        return ResponseEntity.ok(result);
    }
}
