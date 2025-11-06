package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.AddressVO;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	송태호	          최초 생성, 셀렉트키 추가
 *	2025.10.10			신혜진			학생 엑셀로 등록 추가 
 * </pre>
 */
@Mapper
public interface AddressMapper {

	/**
	 * 1. DB에 주소를 저장하고 <br>
	 * 2. 만들어진 주소ID를 address.getAddrId()로 꺼내 쓸 수 있다. 
	 * @param addrId, usingYn을 제외한 데이터를 넣은 VO
	 * @return 입력된 DB record 수(1이면 성공, 0이면 실패) + VO address 안에 들어있는 주소ID
	 */
	public int insertAddress(AddressVO address);
	
	public int updateAddress(AddressVO address);
	
	@Deprecated // 주소 리스트 출력할 일 있을때 @Dep 풀고 사용하세요
	public List<AddressVO> selectAddressList();
	
	@Deprecated // 주소 삭제할 일 있을때 @Dep 풀고 사용하세요
	public int deleteAddress(String addrId);

	
	// 엑셀로 주소 입력
	public void insertBatchAddress(List<AddressVO> addressList);

	  List<String> getNextAddressIdSequenceBatch(@Param("count") int count);

	
 
}
