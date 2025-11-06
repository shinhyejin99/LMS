package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.PaymentReceiptDTO;
import kr.or.jsu.dto.ScholarshipDistributionDTO;
import kr.or.jsu.dto.ScholarshipSimpleDTO;
import kr.or.jsu.dto.StudentPaymentHistoryDTO;
import kr.or.jsu.dto.TuitionDetailDTO;
import kr.or.jsu.dto.TuitionSimpleDTO;
import kr.or.jsu.vo.TuitionVO;

/**
 * 등록금 & 장학금 매퍼
 * @author 김수현
 * @since 2025. 10. 4.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 4.     	김수현	        교직원이 납부요청하는 프로시저 호출 메소드 추가
 *
 * </pre>
 */
@Mapper
public interface TuitionMapper {

    int insertTuition(TuitionVO tuition);

    TuitionVO selectTuitionById(String tuitionId);

    List<TuitionVO> selectAllTuitions();

    int updateTuition(TuitionVO tuition);

    int deleteTuition(String tuitionId);

    /**
     * 교직원이 등록금 납부요청하는 프로시저 호출 메소드
     */
    public void callTuitionRequestProcedure();

    // ====== 학생 등록금 관련 ====================
    /**
     * 등록금 고지서 메인 정보 조회
     */
    public TuitionDetailDTO selectTuitionNoticeMain(
        String studentNo
        , String yeartermCd
    );

    /**
     * 등록금 상세 내역 조회
     */
    public List<TuitionSimpleDTO> selectTuitionDetails(
        String studentNo
        , String yeartermCd
    );

    /**
     * 특정 학기의 장학금 상세 내역 조회
     */
    public List<ScholarshipSimpleDTO> selectScholarshipDetails(
        String studentNo
        , String yeartermCd
    );

    /**
     * 학생의 전체 납부 내역 조회 (여러 학기)
     */
    public List<StudentPaymentHistoryDTO> selectPaymentHistory(@Param("studentNo") String studentNo);

    /**
     * 특정 학기의 장학금 분포 (도넛 차트)
     */
    public List<ScholarshipDistributionDTO> selectScholarshipDistribution(
        @Param("studentNo") String studentNo,
        @Param("yeartermCd") String yeartermCd
    );


    /**
     * 특정 학기의 장학금 총액
     * @param studentNo 학번
     * @param yeartermCd 학기코드
     * @return
     */
    public Integer selectTotalScholarship(
        @Param("studentNo") String studentNo,
        @Param("yeartermCd") String yeartermCd
    );

    /**
     * 납부확인서 조회
     * @param studentNo 학번
     * @param yeartermCd 학기코드
     * @return
     */
    public PaymentReceiptDTO selectPaymentReceipt(
	    @Param("studentNo") String studentNo,
	    @Param("yeartermCd") String yeartermCd
    );
}
