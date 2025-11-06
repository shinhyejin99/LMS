package kr.or.jsu.lms.professor.service.advising;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.jsu.mybatis.mapper.StuCounselMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.vo.StudentVO;

@Service
public class ProfAdStudentServiceImpl implements ProfAdStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StuCounselMapper stuCounselMapper;

    @Override
    public List<StudentVO> retrieveAdvisingStudentList(Map<String, Object> paramMap) {
    	
        List<StudentVO> studentList = studentMapper.selectAdvisingStudentList(paramMap);
        System.out.println("Service: Student list size from mapper: " + studentList.size()); // Debugging statement
        return studentList;
    }

    @Override
    public int retrieveAdvisingStudentCount(Map<String, Object> paramMap) {
        return studentMapper.selectAdvisingStudentCount(paramMap);
    }

    @Override
    public List<Map<String, Object>> retrieveCounselingRequestList(String professorNo) {
        List<Map<String, Object>> result = stuCounselMapper.selectCounselingRequestListByProfessorNo(professorNo);
        System.out.println("Counseling requests from mapper: " + result);
        return result;
    }

    @Override
    public List<Map<String, Object>> retrieveCounselingRequestListByStudentNo(String studentNo) {
        return stuCounselMapper.selectCounselingRequestListByStudentNo(studentNo);
    }

    @Override
    public StudentVO getStudentDetailsByStudentNo(String studentNo) {
        return studentMapper.selectStudentDetailsByStudentNo(studentNo);
    }
}
