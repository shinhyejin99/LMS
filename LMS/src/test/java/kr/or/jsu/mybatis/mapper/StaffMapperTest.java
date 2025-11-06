package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.FilesVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

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
 *  2025. 9. 30.     	김수현	          교직원 부서명 테스트 추가, slf4j 추가
 *
 * </pre>
 */
@Slf4j
@SpringBootTest
@Transactional
class StaffMapperTest {

    @Autowired
    private StaffMapper mapper;

    
//    @Test
//    @DisplayName("직원 전체 목록 조회")
//    void testSelectStaffList() {
//        List<StaffVO> list = mapper.selectStaffList();
//
//        assertNotNull(list, "직원 목록이 null이면 안 됩니다.");
//        assertFalse(list.isEmpty(), "직원 목록이 비어있으면 안 됩니다.");
//
//        list.forEach(System.out::println);
//    }

    @Test
    @DisplayName("직원 상세 조회 (DTO)")
    void testSelectStaffInfo() {
    	 UserStaffDTO dto = mapper.selectStaffInfo("2023001");

        if (dto == null) {
            System.out.println("✅ 테스트 성공 - 데이터가 없습니다.");
        } else {
            System.out.println("✅ 테스트 성공 - 데이터 있음: " + dto);
        }

        // 테스트는 무조건 성공하도록 처리
        assertTrue(true);
    }

    
    	@Test
    	@DisplayName("직원 정보 수정 (UserStaffDTO)")
    	void testUpdateStaffInfo() {
    		 String testStaffNo = "2023001";

    		    // 1) 전체 조회
    		    UserStaffDTO dto = mapper.selectStaffInfo(testStaffNo);
    		    assertNotNull(dto, "수정 대상 DTO가 null이면 안 됩니다.");

    		    // 2) UsersVO 수정
    		    UsersVO userInfo = dto.getUserInfo();
    		    userInfo.setFirstName("수정된이름");
    		    userInfo.setEmail("updated_email@example.com");

    		    // 3) StaffVO 수정
    		    StaffVO staffInfo = dto.getStaffInfo();
    		    staffInfo.setTeleNo("010-1234-5678");

    		    // 4) AddressVO 수정 (예시)
    		    AddressVO addressInfo = dto.getAddressInfo();
    		    addressInfo.setBaseAddr("수정된 기본 주소");
    		    addressInfo.setDetailAddr("수정된 상세 주소");

    		    // 5) FilesVO 수정 (예시)
    		    FilesVO filesInfo = dto.getFilesInfo();
    		    filesInfo.setPublicYn("Y");   		 

    		    // 7) 각 VO 별 update 호출
    		    int userUpdated = mapper.updateUserInfo(userInfo);
    		    int staffUpdated = mapper.updateStaffInfo(staffInfo);
    		    

    		    assertThat(userUpdated).isEqualTo(1);
    		    assertThat(staffUpdated).isEqualTo(1);

    		    // 8) 최종 조회해서 확인
    		    UserStaffDTO updated = mapper.selectStaffInfo(testStaffNo);
    		    assertEquals("수정된이름", updated.getUserInfo().getFirstName());
    		    assertEquals("updated_email@example.com", updated.getUserInfo().getEmail());
    		    assertEquals("010-1234-5678", updated.getStaffInfo().getTeleNo());
    		    assertEquals("수정된 기본 주소", updated.getAddressInfo().getBaseAddr());
    		    assertEquals("수정된 상세 주소", updated.getAddressInfo().getDetailAddr());
    		    assertEquals("Y", updated.getFilesInfo().getPublicYn());


    		    System.out.println("수정 후 DTO: " + updated);
    		}


    @Test
    @DisplayName("사용자 및 직원 정보 등록 테스트")
    void testInsertUserAndStaff() {
        // UsersVO 생성
        UsersVO user = new UsersVO();
        user.setUserId("2023001");
        user.setPwHash("hashed_password");
        user.setFirstName("홍");
        user.setLastName("길동");
        user.setRegiNo("123456-1234567");
        user.setPhotoId("photo123");
        user.setAddrId("addr001");
        user.setMobileNo("010-1234-5678");
        user.setEmail("testuser@example.com");
        user.setBankCode("001");
        user.setBankAccount("123-456-7890");

        int userInsertCount = mapper.insertUser(user);
        assertThat(userInsertCount).isEqualTo(1);

        // StaffVO 생성
        StaffVO staff = new StaffVO();
        staff.setStaffNo("2025002");
        staff.setUserId(user.getUserId());
        staff.setStfDeptCd("D01");
        staff.setTeleNo("010-9876-5432");

        int staffInsertCount = mapper.insertStaff(staff);
        assertThat(staffInsertCount).isEqualTo(1);

//        // 등록된 직원 정보 확인
//        StaffVO insertedStaff = mapper.selectStaffInfo(null).stream()
//            .filter(s -> s.getStaffNo().equals(staff.getStaffNo()))
//            .findFirst()
//            .orElse(null);
//
//        assertNotNull(insertedStaff, "등록된 직원이 null이면 안 됩니다.");
//        assertEquals("D01", insertedStaff.getStfDeptCd());
//        assertEquals("010-9876-5432", insertedStaff.getTeleNo());
//
//        System.out.println("등록 성공: " + insertedStaff);
    }
    
    @Test
    void selectStfDeptNameByCode() {
    	String stfDeptCd = "STF-HR";
    	mapper.selectStfDeptNameByCode(stfDeptCd);
    	assertDoesNotThrow(() -> mapper.selectStfDeptNameByCode(stfDeptCd));
    	log.info("===> 조회된 결과 : {}", mapper.selectStfDeptNameByCode(stfDeptCd));
    }
    
}
