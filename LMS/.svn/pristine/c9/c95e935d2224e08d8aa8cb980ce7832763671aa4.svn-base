import './App.css'
import { Routes, Route, Navigate } from 'react-router-dom'
import StudentDashboard from './pages/StudentDashboard'
import StudentLectureDashboard from './pages/student/StudentLectureDashboard'
import StudentLectureLayout from './layouts/StudentLectureLayout'
import StuBoard from './components/student/StuBoard'
import StudentAttendance from './pages/student/StudentAttendance'
import StudentExam from './pages/student/StudentExam'
import StudentExamDetail from './pages/student/StudentExamDetail'
import StudentTaskIndiv from './pages/student/StudentTaskIndiv'
import StudentTask from './pages/student/StudentTask'
import StudentTaskGroup from './pages/student/StudentTaskGroup'
import StudentBoardPost from './pages/student/StudentBoardPost'
import StudentReview from './pages/student/StudentReview'
import StudentGradeAppeal from './pages/student/StudentGradeAppeal'
import ProfessorDashboard from './pages/ProfessorDashboard'
import ProfessorLectureDashboard from './pages/ProfessorLectureDashboard'
import ProfessorLectureSummary from './pages/ProfessorLectureSummary'
import ProfessorLectureLayout from './layouts/ProfessorLectureLayout'
import Board from './pages/professor/Board'
import BoardPost from './pages/professor/BoardPost'
import BoardPostForm from './pages/professor/BoardPostForm'
import BoardPostEdit from './pages/professor/BoardPostEdit'
import Attendance from './pages/professor/Attendance'
import Exam from './pages/professor/Exam'
import ExamDetail from './pages/professor/ExamDetail'
import ExamOfflineForm from './pages/professor/ExamOfflineForm'
import ExamEditForm from './pages/professor/ExamEditForm'
import Task from './pages/professor/Task'
import TaskIndiv from './pages/professor/TaskIndiv'
import TaskGroup from './pages/professor/TaskGroup'
import TaskIndivForm from './pages/professor/TaskIndivForm'
import TaskGroupForm from './pages/professor/TaskGroupForm'
import TaskGroupEdit from './pages/professor/TaskGroupEdit'
import StudentManage from './pages/professor/StudentManage'
import StudentManageDetail from './pages/professor/StudentManageDetail'
import ProfessorGradeManagement from './pages/professor/ProfessorGradeManagement'
import ProfessorLectureFinalize from './pages/professor/ProfessorLectureFinalize'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/classroom/student" replace />} />
      <Route path="/classroom/student" element={<StudentDashboard />} />
      <Route path="/classroom/student/:lectureId" element={<StudentLectureLayout />}>
        <Route index element={<StudentLectureDashboard />} />
        <Route path="board" element={<StuBoard />} />
        <Route path="board/:postId" element={<StudentBoardPost />} />
        <Route path="attendance" element={<StudentAttendance />} />
        <Route path="exam" element={<StudentExam />} />
        <Route path="exam/:examId" element={<StudentExamDetail />} />
        <Route path="task" element={<StudentTask />} />
        <Route path="task/indiv/:indivtaskId" element={<StudentTaskIndiv />} />
        <Route path="task/group/:grouptaskId" element={<StudentTaskGroup />} />
        <Route path="review" element={<StudentReview />} />
        <Route path="grade" element={<StudentGradeAppeal />} />
      </Route>
      <Route path="/classroom/professor" element={<ProfessorDashboard />} />
      <Route path="/classroom/professor/:lectureId" element={<ProfessorLectureLayout />}>
        <Route index element={<ProfessorLectureDashboard />} />
        <Route path="summary" element={<ProfessorLectureSummary />} />
        <Route path="board" element={<Board />} />
        <Route path="board/new" element={<BoardPostForm />} />
        <Route path="board/:postId/edit" element={<BoardPostEdit />} />
        <Route path="board/:postId" element={<BoardPost />} />
        <Route path="attendance" element={<Attendance />} />
        <Route path="exam" element={<Exam />} />
        <Route path="exam/offline/new" element={<ExamOfflineForm />} />
        <Route path="exam/:examId" element={<ExamDetail />} />
        <Route path="exam/:examId/edit" element={<ExamEditForm />} />
        <Route path="task" element={<Task />} />
        <Route path="task/indiv/new" element={<TaskIndivForm />} />
        <Route path="task/indiv/:indivtaskId/edit" element={<TaskIndivForm />} />
        <Route path="task/indiv/:indivtaskId" element={<TaskIndiv />} />
        <Route path="task/group/new" element={<TaskGroupForm />} />
        <Route path="task/group/:grouptaskId/edit" element={<TaskGroupEdit />} />
        <Route path="task/group/:grouptaskId" element={<TaskGroup />} />
        <Route path="student" element={<StudentManage />} />
        <Route path="student/:studentNo" element={<StudentManageDetail />} />
        <Route path="grades" element={<ProfessorGradeManagement />} />
        <Route path="finalize" element={<ProfessorLectureFinalize />} />
      </Route>
      <Route path="*" element={<Navigate to="/classroom/student" replace />} />
    </Routes>
  )
}

export default App
