package kr.or.jsu.classregist.student.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.dto.DemoRegistrationError;
import kr.or.jsu.classregist.dto.DemoRegistrationItem;
import kr.or.jsu.classregist.dto.DemoRegistrationResult;
import kr.or.jsu.classregist.dto.WishlistCheckDTO;
import kr.or.jsu.classregist.student.controller.LectureWebSocketController;
import kr.or.jsu.mybatis.mapper.ClassRegistWishListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 발표/시연용 수강신청 데이터를 일괄 제어하는 유틸 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassRegistDemoService {

    private static final long TARGET_DURATION_MILLIS = 2_000L;

    private final ClassRegistWishListMapper wishlistMapper;
    private final LectureWebSocketController webSocketController;

    @Transactional
    public DemoRegistrationResult applyForDemo(List<DemoRegistrationItem> items) {
        return runDemoBatch(items, this::forceApply);
    }

    @Transactional
    public DemoRegistrationResult cleanupDemo(List<DemoRegistrationItem> items) {
        return runDemoBatch(items, this::forceDelete);
    }

    private DemoRegistrationResult runDemoBatch(List<DemoRegistrationItem> items,
                                                Consumer<DemoRegistrationItem> operation) {
        int total = items == null ? 0 : items.size();
        if (total == 0) {
            return DemoRegistrationResult.builder()
                    .totalCount(0)
                    .successCount(0)
                    .targetDurationMillis(TARGET_DURATION_MILLIS)
                    .appliedDelayPerItemMillis(0)
                    .errors(List.of())
                    .build();
        }

        long baseDelay = TARGET_DURATION_MILLIS / total;
        long remainder = TARGET_DURATION_MILLIS % total;
        long delayPerItem = baseDelay == 0 ? 0 : baseDelay;

        int success = 0;
        List<DemoRegistrationError> failures = new ArrayList<>();

        for (int idx = 0; idx < total; idx++) {
            DemoRegistrationItem item = items.get(idx);
            try {
                operation.accept(item);
                success++;
            } catch (Exception ex) {
                log.warn("Demo operation failed for studentNo={}, lectureId={}", item.getStudentNo(),
                        item.getLectureId(), ex);
                failures.add(new DemoRegistrationError(
                        item.getStudentNo(),
                        item.getLectureId(),
                        ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()
                ));
            }

            long extra = idx < remainder ? 1 : 0;
            sleepSilently(delayPerItem + extra);
        }

        return DemoRegistrationResult.builder()
                .totalCount(total)
                .successCount(success)
                .targetDurationMillis(TARGET_DURATION_MILLIS)
                .appliedDelayPerItemMillis(delayPerItem)
                .errors(failures)
                .build();
    }

    private void forceApply(DemoRegistrationItem item) {
        String studentNo = item.getStudentNo();
        String lectureId = item.getLectureId();

        if (wishlistMapper.existsAppliedLecture(studentNo, lectureId) > 0) {
            throw new IllegalStateException("이미 수강신청된 강의입니다.");
        }

        WishlistCheckDTO lecture = wishlistMapper.selectLectureForApplyWithLock(lectureId);
        if (lecture == null) {
            throw new IllegalStateException("존재하지 않는 강의입니다.");
        }

        int insert = wishlistMapper.insertApplyLecture(studentNo, lectureId);
        if (insert <= 0) {
            throw new IllegalStateException("수강신청 INSERT가 실패했습니다.");
        }

        wishlistMapper.deleteWishlist(studentNo, lectureId);
        wishlistMapper.insertApplyLog(
                generateLogId(),
                studentNo,
                lectureId,
                'N',
                'Y',
                'I'
        );

        broadcast(lectureId, lecture.getMaxCap());
    }

    private void forceDelete(DemoRegistrationItem item) {
        String studentNo = item.getStudentNo();
        String lectureId = item.getLectureId();

        int deleted = wishlistMapper.deleteApplyLecture(studentNo, lectureId);
        if (deleted <= 0) {
            throw new IllegalStateException("삭제할 수강신청 데이터가 없습니다.");
        }

        wishlistMapper.insertApplyLog(
                generateLogId(),
                studentNo,
                lectureId,
                'N',
                'Y',
                'D'
        );

        WishlistCheckDTO lecture = wishlistMapper.selectLectureForApplyWithLock(lectureId);
        Integer maxCap = lecture != null ? lecture.getMaxCap() : null;
        broadcast(lectureId, maxCap);
    }

    private void broadcast(String lectureId, Integer maxCap) {
        Integer updatedEnroll = wishlistMapper.countCurrentEnroll(lectureId);
        webSocketController.broadcastEnrollUpdate(lectureId, updatedEnroll, maxCap);
    }

    private String generateLogId() {
        Long seq = wishlistMapper.getNextLogSeq();
        long value = seq != null ? seq : 0L;
        return "LCTLOG" + String.format("%09d", value);
    }

    private void sleepSilently(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Demo delay interrupted", e);
        }
    }
}
