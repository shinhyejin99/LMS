package kr.or.jsu.lms.professor.controller.lecture;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.professor.service.ClassroomProfessorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/lms/professor/rest/lecture")
@RequiredArgsConstructor
public class ProfLectureRestController {

	private final ClassroomProfessorService classPrfService;

	/**
	 * 사용자 교수와 관련된 모든 강의 목록을 조회합니다.
	 *
	 * @return 강의목록에 출력할 정보
	 */
	@GetMapping("/list")
	@ResponseBody
	public List<LectureLabelResp> myLectureList() {

		List<LectureLabelResp> list = classPrfService.readMyLectureList();
		// TODO 오류처리
		if(list.size() == 0) {
			throw new RuntimeException("해당 조건에 일치하는 강의가 없습니다.");
		}

		return list;
	}
}
