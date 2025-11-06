package kr.or.jsu.core.utils.databasecache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.core.dto.info.CollegeInfo;
import kr.or.jsu.core.dto.info.CommonCodeInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.dto.info.UsersInfo;
import kr.or.jsu.dto.info.StaffDeptInfo;
import kr.or.jsu.mybatis.mapper.cache.CacheMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("!test") // 테스트 환경에선 이거 실행 안 함.
public class DatabaseCache {
	
	@Autowired @Lazy
	private final CacheMapper ccMapper;
	private final ConcurrentHashMap<String, String> code2name = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userId2No = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> userNo2Name = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> udCode2Name = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> clgCode2Name = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> sbjCd2Name = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> sdpCd2Name = new ConcurrentHashMap<>();
    
	@Getter
	private String currentYearterm;
	@Getter
	private final List<CollegeInfo> collegeList;
	@Getter
	private final List<UnivDeptInfo> univDeptList;
	@Getter
	private final List<StaffDeptInfo> staffDeptList;
	
	
    public DatabaseCache(CacheMapper ccMapper) {
        this.ccMapper = ccMapper;
        collegeList = ccMapper.selectAllCollege();
        univDeptList = ccMapper.selectAllUnivDept();
        staffDeptList = ccMapper.selectAllStaffDept();
    }
	
	@PostConstruct
	public void init() { reload(); }

	private void reload() {
		List<CommonCodeInfo> ccRows = ccMapper.selectAllCommonCode();
		ConcurrentHashMap<String, String> tmp = new ConcurrentHashMap<>(ccRows.size());
        for (CommonCodeInfo r : ccRows) tmp.put(r.getCommonCd(), r.getCdName());
        
        List<UsersInfo> userRows = ccMapper.selectAllUsers();
        ConcurrentHashMap<String, String> tmp2 = new ConcurrentHashMap<>(userRows.size());
        ConcurrentHashMap<String, String> tmp3 = new ConcurrentHashMap<>(userRows.size());
        for (UsersInfo r : userRows) {
        	tmp2.put(r.getUserId(), r.getUserNo());
        	tmp3.put(r.getUserNo(), r.getUserName());
        }
        
        List<UnivDeptInfo> udRows = ccMapper.selectAllUnivDept();
		ConcurrentHashMap<String, String> tmp4 = new ConcurrentHashMap<>(udRows.size());
        for (UnivDeptInfo r : udRows) tmp4.put(r.getUnivDeptCd(), r.getUnivDeptName());
        
        List<CollegeInfo> clgRows = ccMapper.selectAllCollege();
        ConcurrentHashMap<String, String> tmp5 = new ConcurrentHashMap<>(clgRows.size());
        for (CollegeInfo r : clgRows) tmp5.put(r.getCollegeCd(), r.getCollegeName());
        
        List<SubjectInfo> sbjRows = ccMapper.selectAllSubject();
        ConcurrentHashMap<String, String> tmp6 = new ConcurrentHashMap<>(sbjRows.size());
        for (SubjectInfo r : sbjRows) tmp6.put(r.getSubjectCd(), r.getSubjectName());
        
        List<StaffDeptInfo> sdpRows = ccMapper.selectAllStaffDept();
        ConcurrentHashMap<String, String> tmp7 = new ConcurrentHashMap<>(sdpRows.size());
        for (StaffDeptInfo r : sdpRows) tmp7.put(r.getStfDeptCd(), r.getStfDeptName());
        
        // 현재 학기 조회
        String tmpYearterm = ccMapper.selectCurrentYearterm();
        
        // 현재 학기 업데이트
        this.currentYearterm = tmpYearterm;
        
        code2name.clear(); code2name.putAll(tmp);
        userId2No.clear(); userId2No.putAll(tmp2);
        userNo2Name.clear(); userNo2Name.putAll(tmp3);
        udCode2Name.clear(); udCode2Name.putAll(tmp4);
        clgCode2Name.clear(); clgCode2Name.putAll(tmp5);
    	sbjCd2Name.clear(); sbjCd2Name.putAll(tmp6);
    	sdpCd2Name.clear(); sdpCd2Name.putAll(tmp7);
	}
	
	public String getCodeName(String code) {
        if (code == null) return null;
        return code2name.getOrDefault(code, code);
    }
	
	public String getUserNo(String userId) {
		if (userId == null) return null;
		return userId2No.getOrDefault(userId, userId);
	}
	
	public String getUserName(String userNo) {
		if (userNo == null) return null;
		return userNo2Name.getOrDefault(userNo, userNo);
	}
	
	public String getUnivDeptName(String univDeptCd) {
		if (univDeptCd == null) return null;
		return udCode2Name.getOrDefault(univDeptCd, univDeptCd);
	}
	
	public String getCollegeName(String collegeCd) {
		if (collegeCd == null) return null;
		return clgCode2Name.getOrDefault(collegeCd, collegeCd);
	}
	public String getSubjectName(String subjectCd) {
		if (subjectCd == null) return null;
		return sbjCd2Name.getOrDefault(subjectCd, subjectCd);
	}
	public String getStaffDeptName(String StfDeptCd) {
		if (StfDeptCd == null) return null;
		return sdpCd2Name.getOrDefault(StfDeptCd, StfDeptCd);
	}
	
	public String getYeartermName(String yeartermCd) {
		String termCd = yeartermCd.split("_")[1];
		String termName = getCodeName(termCd);
		String yearTermName = yeartermCd.split("_")[0] + "년 " + termName;
		
		return yearTermName;
	}	
}