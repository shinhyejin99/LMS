package kr.or.jsu.mybatis.mapper.dummy;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.ApprovalVO;
import kr.or.jsu.vo.EnrollGpaVO;
import kr.or.jsu.vo.CollegeVO;
import kr.or.jsu.vo.LctApplyGraderatioVO;
import kr.or.jsu.vo.LctApplyWeekbyVO;
import kr.or.jsu.vo.LctOpenApplyVO;
import kr.or.jsu.vo.LctGraderatioVO;
import kr.or.jsu.vo.LctWeekbyVO;
import kr.or.jsu.vo.LectureVO;
import kr.or.jsu.vo.LctPostVO;
import kr.or.jsu.vo.PlaceVO;
import kr.or.jsu.vo.PrfCareerVO;
import kr.or.jsu.vo.PrfDegreeVO;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.SbjTargetVO;
import kr.or.jsu.vo.StaffDeptVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.StuEnrollLctVO;
import kr.or.jsu.vo.StuGraduationVO;
import kr.or.jsu.vo.StuEntranceVO;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.SubjectVO;
import kr.or.jsu.vo.UnivDeptVO;
import kr.or.jsu.vo.UsersVO;
import kr.or.jsu.vo.LctAttroundVO;
import kr.or.jsu.vo.LctStuAttVO;
import kr.or.jsu.vo.LctIndivtaskVO;
import kr.or.jsu.vo.IndivtaskSubmitVO;
import kr.or.jsu.vo.LctGrouptaskVO;
import kr.or.jsu.vo.GrouptaskGroupVO;
import kr.or.jsu.vo.GrouptaskCrewVO;
import kr.or.jsu.vo.GrouptaskSubmitVO;
import kr.or.jsu.vo.LctExamVO;
import kr.or.jsu.vo.ExamSubmitVO;

@Mapper
public interface DummyDataMapper {

	public int insertOneDummyAddress(AddressVO address);
	public int insertDummyAddress(List<AddressVO> dummyAddress);

	public int insertOneDummyUser(UsersVO user);
	public int insertDummyUser(List<UsersVO> dummyUsers);

	public int insertOneDummyStudent(StudentVO student);
	public int insertOneDummyStuEntrance(StuEntranceVO entranceInfo);
	public int insertDummyStuGraduations(@Param("records") List<StuGraduationVO> records);
	public int insertOneDummyStuGraduation(StuGraduationVO record);

	public int insertOneDummyProfessor(ProfessorVO professor);
	public int insertOneDummyPrfCareer(PrfCareerVO career);
	public int insertOneDummyPrfDegree(PrfDegreeVO degree);

	public int insertOneDummySubject(SubjectVO subject);
	public int insertOneDummySbjTarget(SbjTargetVO sbjTarget);

	public int insertOneDummyLecture(LectureVO lecture);
	public int clearLectureEndAtByYearterm(@Param("yeartermCd") String yeartermCd);

	public int insertOneDummyEnrollment(StuEnrollLctVO enrollment);

	public int insertColleges(List<CollegeVO> colleges);

	public int insertUnivDepartments(List<UnivDeptVO> departments);

	public int insertStaffDepartments(List<StaffDeptVO> staffDepartments);

	public int insertOneDummyStaff(StaffVO staff);

	public int insertDummyApproval(ApprovalVO approval);
	public int insertDummyLctOpenApply(LctOpenApplyVO lctOpenApply);
	public int insertDummyLctApplyWeekby(@Param("weekbyList") List<LctApplyWeekbyVO> weekbyList);
	public int insertDummyLctApplyGraderatio(@Param("graderatioList") List<LctApplyGraderatioVO> graderatioList);

	public int insertDummyLctWeekby(@Param("weekbyList") List<LctWeekbyVO> weekbyList);
	public int insertDummyLctGraderatio(@Param("graderatioList") List<LctGraderatioVO> graderatioList);

	public int insertDummyEnrollGpa(@Param("gpaList") List<EnrollGpaVO> gpaList);
	public int insertOneEnrollGpa(EnrollGpaVO gpa);

	public int insertDummyPlaces(@Param("placeList") List<PlaceVO> placeList);

	public String findAvailableRoom(@Param("timeBlocks") String[] timeBlocks,
	        @Param("maxCap") int maxCap,
	        @Param("roomPrefix") String roomPrefix);
	public int insertSchedule(@Param("lectureId") String lectureId,
            @Param("roomPlaceCd") String roomPlaceCd,
            @Param("timeBlockCd") String timeBlockCd);
	public int isProfessorBusy(@Param("professorNo") String professorNo, @Param("timeBlocks") String[] timeBlocks);

	public int insertDummyLctPosts(@Param("posts") List<LctPostVO> posts);

	public int insertDummyLctAttrounds(@Param("rounds") List<LctAttroundVO> rounds);

	public int insertDummyLctStuAtt(@Param("records") List<LctStuAttVO> records);

	public int insertDummyIndivtasks(@Param("tasks") List<LctIndivtaskVO> tasks);

	public int insertDummyIndivtaskSubmits(@Param("submits") List<IndivtaskSubmitVO> submits);

	public int insertDummyGrouptasks(@Param("tasks") List<LctGrouptaskVO> tasks);

	public int insertDummyGrouptaskGroups(@Param("groups") List<GrouptaskGroupVO> groups);

	public int insertDummyGrouptaskCrews(@Param("crews") List<GrouptaskCrewVO> crews);

	public int insertDummyGrouptaskSubmits(@Param("submits") List<GrouptaskSubmitVO> submits);

	public int insertDummyExams(@Param("exams") List<LctExamVO> exams);

	public int insertDummyExamSubmits(@Param("submits") List<ExamSubmitVO> submits);


}
