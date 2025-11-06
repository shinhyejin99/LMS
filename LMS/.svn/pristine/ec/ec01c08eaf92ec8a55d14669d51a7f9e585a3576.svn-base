package kr.or.jsu.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "pushId", "userId" }) // 알림 자체의 고유성을 위해 pushId와 userId를 모두 사용
public class PushNoticeDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	// PUSH_NOTICE 컬럼 (알림의 기본 정보)
	private String pushId; // 알림 ID
	private String sender; // 송신자 명칭
	private String pushDetail; // 알림 내용
	private LocalDateTime createAt; // 알림 생성일
	private String pushUrl; // 참조 URL

	// PUSH_NOTICE_TARGET 컬럼 (사용자별 알림 상태)
	private String userId; // 수신자 ID
	private LocalDateTime receiveAt; // 도착 시점
	private LocalDateTime checkAt; // 확인 시점 (NULL이면 미확인)

	private String lastName;
	private String firstName;
	private String senderLastName;
	private String senderFirstName;

	private String isRead;
	private String stfDeptCd;
	private String stfDeptName;
	private String pushTitle;
	private String mobileNo;

	private String recipientName;
	private String senderDeptName;

	private String univDeptCd; // 소속 학과 코드
	private String univDeptName;

	private String collegeCd;
	private String collegeName;
	private String recipientInfo;

}