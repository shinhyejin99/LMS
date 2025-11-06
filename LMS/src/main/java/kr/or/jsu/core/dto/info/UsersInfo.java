package kr.or.jsu.core.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성, 캐싱용
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "userId")
public class UsersInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userId;
	private String pwHash;
	private String firstName;
	private String lastName;
	private String regiNo;
	private String photoId;
	private String addrId;
	private String mobileNo;
	private String email;
	private String bankCode;
	private String bankAccount;
	private LocalDateTime createAt;
	
	// 캐시로 가져올 수 있는 정보들
	private String userNo;
	private String userName;
	
	public String getUserType() {
		switch (userNo.length()) {
			case 7: return "STAFF";
			case 8: return "PROFESSOR";
			case 9: return "STUDENT";
			default: return "UNKNOWN";
		}
	}
}