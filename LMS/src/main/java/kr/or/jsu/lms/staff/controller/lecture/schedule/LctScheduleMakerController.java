package kr.or.jsu.lms.staff.controller.lecture.schedule;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/lecture/schedulemaker")
@RequiredArgsConstructor
public class LctScheduleMakerController {

    @GetMapping()
    public String dashboard() {
        return "staff/lectureRoom/scheduleMaker";
    }
    
}



// 강의실 배정 흐름
// 
// 1. 배정해야 할 강의가 뭐가있나.. (특정 학기에, 강의실 배정이 없거나, 시수가 부족한 강의들 목록이 출력된다.)
// 
// 2. 단과대학, 학과별로 봐야겠다. (분류 기능으로, 단과대, 학과별 구분.) (이수구분, 학점, 시수 등 다른 분류도 가능)
// 
// 3. 강의 과목과 관련있는 건물에 넣어야 하는데.
// 
// 4. (장소 섹션에서 건물 목록을 보고, 관련된 건물을 선택한다.)
// 
// 5. (건물을 선택하면 강의실 목록이 나오고, 현재 사용률 + 수용 인원수가 표시된다.)
// (사용률이란, 대상 학기에 특정 강의실에 대한 시간블록이 얼마나 차 있는지.)
// 
// 6. A건물의 B강의실이 맞겠다. (사용률이 과하지 않고, 강의실의 수용인원수가 강의의 정원 이상인 강의실을 선택한다.)
// 
// 7. 해당 강의실의 타임블록을 선택하여 시수를 맞춘다. (한 강의실에만 모든 시수를 넣지 않아도 됨)
// 
// 8. 배정 버튼을 누르면 POST로 요청이 가고, 데이터베이스에 기록된다. 프론트도 적절히 리렌더링을 해야하나?