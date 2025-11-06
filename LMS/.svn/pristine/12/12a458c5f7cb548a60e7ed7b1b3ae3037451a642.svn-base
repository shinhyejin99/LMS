import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import App from './App.jsx'
import './styles/classroom.css'
import './styles/customButtons.css'
import { UserProvider } from './context/UserContext'
import { LectureProvider } from './context/LectureContext'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <UserProvider>
        <LectureProvider>
          <App />
        </LectureProvider>
      </UserProvider>
    </BrowserRouter>
  </StrictMode>,
)
