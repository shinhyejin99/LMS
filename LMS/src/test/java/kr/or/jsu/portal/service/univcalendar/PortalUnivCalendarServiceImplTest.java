package kr.or.jsu.portal.service.univcalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.or.jsu.mybatis.mapper.UnivCalendarMapper;
import kr.or.jsu.vo.UnivCalendarVO;

@ExtendWith(MockitoExtension.class)
class PortalUnivCalendarServiceImplTest {

    @Mock
    private UnivCalendarMapper univCalendarMapper;

    @InjectMocks
    private PortalUnivCalendarServiceImpl portalUnivCalendarService;

    private UnivCalendarVO testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new UnivCalendarVO(
            "CAL00000000001",
            "2023001",
            "등록",
            "2025_REG1",
            "테스트 일정",
            "ALL",
            LocalDateTime.of(2025, 3, 1, 9, 0),
            LocalDateTime.of(2025, 3, 1, 18, 0),
            LocalDateTime.now(),
            "N"
        );
    }

    @Test
    @DisplayName("학사일정 목록 조회 테스트")
    void testReadCalendarList() {
        // Given
        when(univCalendarMapper.selectCalendarList(anyString(), anyString()))
            .thenReturn(Arrays.asList(testEvent));

        // When
        List<UnivCalendarVO> result = portalUnivCalendarService.readCalendarList("2025_REG1", "등록");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testEvent.getCalendarId(), result.get(0).getCalendarId());
        verify(univCalendarMapper, times(1)).selectCalendarList("2025_REG1", "등록");
    }

    @Test
    @DisplayName("학사일정 생성 테스트")
    void testCreateCalendarEvent() {
        // Given
        when(univCalendarMapper.insertCalendarEvent(any(UnivCalendarVO.class))).thenReturn(1);

        // When
        int result = portalUnivCalendarService.createCalendarEvent(testEvent);

        // Then
        assertEquals(1, result);
        assertNotNull(testEvent.getStaffNo()); // STAFF_NO가 서비스에서 설정되었는지 확인
        verify(univCalendarMapper, times(1)).insertCalendarEvent(testEvent);
    }
}