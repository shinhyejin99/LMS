package kr.or.jsu.lms.professor.service.lecture;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.or.jsu.lms.professor.lecture.mapper.ProfLectureMapper;

@Service("profLectureService")
public class ProfLectureServiceImpl implements ProfLectureService {


    @Resource(name = "profLectureMapper")
    private ProfLectureMapper profLectureMapper;

    @Override
    public List<Map<String, Object>> selectProfLectures(Map<String, Object> paramMap) {
        return profLectureMapper.selectProfLectures(paramMap);
    }

    @Override
    public Map<String, Object> selectLectureDetails(String lectureId) {
        return profLectureMapper.selectLectureDetails(lectureId);
    }
}