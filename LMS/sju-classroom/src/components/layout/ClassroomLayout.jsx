import React from 'react'
import Header from './Header'
import SidebarStudent from './SidebarStudent'
import SidebarProfessor from './SidebarProfessor'
import Footer from './Footer'
import '../../styles/classroom.css'

export default function ClassroomLayout({ role = 'student', children, lectureId, onLectureInfo }) {
  return (
    <div className="app">
      <Header role={role} lectureId={lectureId} onLectureInfo={onLectureInfo} />
      <main className="app-main">
        <aside className="app-sidebar">
          {role === 'professor' ? <SidebarProfessor /> : <SidebarStudent />}
        </aside>
        <section className="app-content">
          <div className="container p-3">
            {children}
          </div>
        </section>
      </main>
      <Footer />
    </div>
  )
}

