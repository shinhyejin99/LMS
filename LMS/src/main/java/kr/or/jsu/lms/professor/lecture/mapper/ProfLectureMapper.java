package kr.or.jsu.lms.professor.lecture.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProfLectureMapper {

    List<Map<String, Object>> selectProfLectures(Map<String, Object> paramMap);

    Map<String, Object> selectLectureDetails(String lectureId);
}