package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.UsersVO;

/**
 * @author 송태호
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25. 		김수현			학생) 인적정보 조회 메소드 추가
 *  2025. 9. 25.     	송태호	          최초 생성, 기본 CRUD 쿼리 추가
 *	2025.10.10			신혜진			학생 정보 엑셀로 등록 추가
 * </pre>
 */
@Mapper
public interface UsersMapper {

    /**
     * 사용자 한 명을 직접 등록할 때 사용하는 메서드입니다. <br>
     * 셀렉트키를 사용하여, SEQ_USER 시퀀스로 15자리 USER번호를 생성합니다. <br>
     * 성공할 경우, 파라미터로 넣은 UserVO user에 생성된 userId가 들어있습니다. <br>
     * @author 송태호, 09.25
     * @param user 사용자 한 명의 정보가 들어있는 vo
     * @return DB에 입력된 사용자 레코드 수. 성공 시 1 반환.
     */
    int insertUser(UsersVO user);

    /**
     * userId로 사용자 한 명의 기본 정보만을 가져오는 메서드입니다. <br>
     * 해당 사용자의 액터 정보까지 필요하다면 다른 메서드를 사용하세요.
     * @author 송태호, 09.25
     * @param userId
     * @return 사용자 한 명의 기본 정보
     */
    UsersVO selectUser(String userId);

    /**
     * 모든 사용자 목록을 가져오는 메서드입니다. <br/>
     * 필요할 경우에만 사용하시고, 목록 관리가 필요할 경우 페이지네이션이 적용된 메서드를 사용하세요.
     * @author 송태호, 09.25
     * @return 모든 사용자 목록
     */
    List<UsersVO> selectUserList();

    /**
     * UserVO 안의 userId가 일치하는 사용자가 있을 경우, <br>
     * 모든 필드를 UserVO에 든 필드로 변경하는 메서드입니다.
     * @author 송태호, 09.25
     * @param userId
     * @return 수정된 레코드 수. 성공 시 1.
     */
    int updateUser(UsersVO user);

    /**
     * userId가 일치하는 사용자를 DB에서 물리적으로 삭제하는 메서드입니다. <br>
     * 아무런 관계가 생성되지 않는 사용자만 삭제할 수 있습니다. <br>
     * 관계가 생성된 사용자를 삭제할 경우 예외가 발생합니다.
     * @author 송태호, 09.25
     * @param userId
     * @return 삭제된 레코드 수. 성공 시 1.
     */
    int deleteUser(String userId);

    // user항목 엑셀로 등록
    int insertBatchUser(List<UsersVO> userList);
    List<String> getNextUserIdSequenceBatch(@Param("count") int count);

    public int updateUserPhotoId(
    	@Param("finalUserId") String userId
    	, @Param("newPhotoId") String photoId
    );

}
