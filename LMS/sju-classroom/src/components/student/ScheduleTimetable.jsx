import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

const DAYS = ['월', '화', '수', '목', '금']
const DAY_CODES = { '월': 'MO', '화': 'TU', '수': 'WE', '목': 'TH', '금': 'FR' }
const TIME_SLOTS = []
for (let hour = 9; hour <= 18; hour++) {
  TIME_SLOTS.push(`${String(hour).padStart(2, '0')}00`)
  TIME_SLOTS.push(`${String(hour).padStart(2, '0')}30`)
}
TIME_SLOTS.push('1900') // Added to ensure addThirtyMinutes always finds an endIndex

const SEMESTER_OPTIONS = [
  { value: 'REG1', label: '1학기' },
  { value: 'REG2', label: '2학기' },
  { value: 'SUB1', label: '여름학기' },
  { value: 'SUB2', label: '겨울학기' }
]

const COLORS = [
  '#FFB3BA', '#FFDFBA', '#FFFFBA', '#BAFFC9', '#BAE1FF',
  '#D4BAFF', '#FFB3E6', '#FFC9BA', '#BAF0FF', '#E6BAFF',
  '#FFBABA', '#FFE5BA', '#F0FFBA', '#BAFFDB', '#BADEFF'
]

export default function ScheduleTimetable() {
  const navigate = useNavigate()
  const [year, setYear] = useState('')
  const [semester, setSemester] = useState('')
  const [scheduleData, setScheduleData] = useState([])
  const [loading, setLoading] = useState(false)
  const [isOpen, setIsOpen] = useState(false)
  const [initialLoad, setInitialLoad] = useState(false)

  const currentYear = new Date().getFullYear()
  const yearOptions = []
  for (let y = currentYear - 3; y <= currentYear + 1; y++) {
    yearOptions.push(y)
  }

  // Initial load when dropdown opens
  useEffect(() => {
    if (isOpen && !initialLoad) {
      fetchSchedule()
      setInitialLoad(true)
    }
  }, [isOpen])

  // Fetch when year or semester changes
  useEffect(() => {
    if (year && semester) {
      fetchSchedule()
    }
  }, [year, semester])

  const fetchSchedule = async () => {
    setLoading(true)
    try {
      let url = '/classroom/api/v1/student/schedule'
      if (year && semester) {
        url += `?yeartermCd=${year}_${semester}`
      }
      const response = await fetch(url)
      if (!response.ok) {
        throw new Error('Failed to fetch schedule')
      }
      const data = await response.json()
      setScheduleData(data || [])
    } catch (error) {
      console.error('Failed to fetch schedule:', error)
      setScheduleData([])
    } finally {
      setLoading(false)
    }
  }

  const handleDropdownToggle = (e) => {
    const dropdown = e.currentTarget.closest('.dropdown')
    const isOpening = !dropdown.classList.contains('show')
    setIsOpen(isOpening)
  }

  const parseTimeblock = (timeblockCd) => {
    // timeblockCd format: "TU1300" -> day: TU, time: 1300
    const dayCode = timeblockCd.substring(0, 2)
    const time = timeblockCd.substring(2)
    return { dayCode, time }
  }

  const groupLecturesByIdAndTime = (data) => {
    const grouped = {}

    data.forEach(item => {
      if (!grouped[item.lectureId]) {
        grouped[item.lectureId] = []
      }
      grouped[item.lectureId].push(item)
    })

    // Sort each group by time
    Object.keys(grouped).forEach(lectureId => {
      grouped[lectureId].sort((a, b) => {
        const timeA = parseTimeblock(a.timeblockCd)
        const timeB = parseTimeblock(b.timeblockCd)
        if (timeA.dayCode !== timeB.dayCode) {
          return timeA.dayCode.localeCompare(timeB.dayCode)
        }
        return timeA.time.localeCompare(timeB.time)
      })
    })

    return grouped
  }

  const findConsecutiveBlocks = (items) => {
    const blocks = []
    let currentBlock = null

    items.forEach(item => {
      const { dayCode, time } = parseTimeblock(item.timeblockCd)

      if (!currentBlock || currentBlock.dayCode !== dayCode) {
        if (currentBlock) blocks.push(currentBlock)
        currentBlock = {
          lectureId: item.lectureId,
          subjectName: item.subjectName,
          placeName: item.placeName,
          dayCode,
          startTime: time,
          endTime: time,
          items: [item]
        }
      } else {
        // Check if consecutive (30 min apart)
        const lastTime = currentBlock.endTime
        const expectedNext = addThirtyMinutes(lastTime)

        if (time === expectedNext) {
          currentBlock.endTime = time
          currentBlock.items.push(item)
        } else {
          blocks.push(currentBlock)
          currentBlock = {
            lectureId: item.lectureId,
            subjectName: item.subjectName,
            placeName: item.placeName,
            dayCode,
            startTime: time,
            endTime: time,
            items: [item]
          }
        }
      }
    })

    if (currentBlock) blocks.push(currentBlock)
    return blocks
  }

  const addThirtyMinutes = (timeStr) => {
    const hour = parseInt(timeStr.substring(0, 2))
    const min = parseInt(timeStr.substring(2))

    if (min === 30) {
      return `${String(hour + 1).padStart(2, '0')}00`
    } else {
      return `${String(hour).padStart(2, '0')}30`
    }
  }

  const renderTimetable = () => {
    const grouped = groupLecturesByIdAndTime(scheduleData)
    const allBlocks = []
    const lectureColorMap = {}
    let colorIndex = 0

    // Get all lecture IDs sorted by first appearance time
    const sortedLectureIds = Object.keys(grouped).sort((a, b) => {
      const firstA = parseTimeblock(grouped[a][0].timeblockCd)
      const firstB = parseTimeblock(grouped[b][0].timeblockCd)

      if (firstA.dayCode !== firstB.dayCode) {
        return firstA.dayCode.localeCompare(firstB.dayCode)
      }
      return firstA.time.localeCompare(firstB.time)
    })

    // Assign colors
    sortedLectureIds.forEach(lectureId => {
      lectureColorMap[lectureId] = COLORS[colorIndex % COLORS.length]
      colorIndex++

      const blocks = findConsecutiveBlocks(grouped[lectureId])
      allBlocks.push(...blocks)
    })

    const timetableGrid = {}
    DAYS.forEach(day => {
      timetableGrid[day] = {}
      TIME_SLOTS.forEach(time => {
        timetableGrid[day][time] = null
      })
    })

    allBlocks.forEach(block => {
      const dayName = Object.keys(DAY_CODES).find(key => DAY_CODES[key] === block.dayCode)
      if (!dayName) return

      const startIndex = TIME_SLOTS.indexOf(block.startTime)
      const endTime = addThirtyMinutes(block.endTime)
      const endIndex = TIME_SLOTS.indexOf(endTime)

      if (startIndex !== -1 && endIndex !== -1) {
        const rowSpan = endIndex - startIndex
        timetableGrid[dayName][block.startTime] = {
          ...block,
          rowSpan,
          color: lectureColorMap[block.lectureId]
        }

        // Mark occupied cells
        for (let i = startIndex + 1; i < endIndex; i++) {
          timetableGrid[dayName][TIME_SLOTS[i]] = 'occupied'
        }
      }
    })

    return (
      <table className="table table-bordered table-sm" style={{ tableLayout: 'fixed' }}>
        <colgroup>
          <col style={{ width: '80px' }} />
          {DAYS.map(day => (
            <col key={day} style={{ width: '100px' }} />
          ))}
        </colgroup>
        <thead>
          <tr>
            <th style={{ width: '80px', minWidth: '80px', textAlign: 'center' }}>시간</th>
            {DAYS.map(day => (
              <th key={day} style={{ width: '100px', textAlign: 'center' }}>{day}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {TIME_SLOTS.slice(0, -1).map((time, idx) => (
            <tr key={time} style={{ height: '30px' }}>
              <td className="text-center align-middle" style={{ fontSize: '0.85rem' }}>
                {time.substring(0, 2)}:{time.substring(2)}~{addThirtyMinutes(time).substring(0, 2)}:{addThirtyMinutes(time).substring(2)}
              </td>
              {DAYS.map(day => {
                const cell = timetableGrid[day][time]

                if (cell === 'occupied') {
                  return null
                }

                if (cell && cell.rowSpan) {
                  return (
                    <td
                      key={day}
                      rowSpan={cell.rowSpan}
                      className="p-2 text-center align-middle position-relative"
                      style={{
                        backgroundColor: cell.color,
                        cursor: 'pointer',
                        verticalAlign: 'middle'
                      }}
                      onClick={() => navigate(`/classroom/student/lecture/${cell.lectureId}`)}
                    >
                      <div style={{ fontSize: '0.9rem', fontWeight: '600' }}>
                        {cell.subjectName}
                      </div>
                      <div style={{ fontSize: '0.75rem', marginTop: '4px' }}>
                        {cell.placeName}
                      </div>
                    </td>
                  )
                }

                return <td key={day}></td>
              })}
            </tr>
          ))}
        </tbody>
      </table>
    )
  }

  return (
    <div className="dropdown">
      <a
        className="nav-link dropdown-toggle"
        href="#"
        role="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
        onClick={handleDropdownToggle}
      >
        시간표
      </a>
      <div className="dropdown-menu dropdown-menu-dark dropdown-menu-end p-3" style={{ minWidth: '700px', maxWidth: '800px' }}>
        <div className="mb-3 d-flex gap-2">
          <select
            className="form-select form-select-sm"
            value={year}
            onChange={(e) => setYear(e.target.value)}
            style={{ width: 'auto' }}
          >
            <option value="">연도</option>
            {yearOptions.map(y => (
              <option key={y} value={y}>{y}년</option>
            ))}
          </select>

          <select
            className="form-select form-select-sm"
            value={semester}
            onChange={(e) => setSemester(e.target.value)}
            style={{ width: 'auto' }}
          >
            <option value="">학기</option>
            {SEMESTER_OPTIONS.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>

        {loading && (
          <div className="text-center py-3">
            <div className="spinner-border spinner-border-sm text-light" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        )}

        {!loading && initialLoad && (
          <div style={{ maxHeight: '700px', overflowY: 'auto', backgroundColor: 'white', borderRadius: '4px', padding: '8px' }}>
            {scheduleData.length > 0 ? renderTimetable() : (
              <div className="text-center py-3 text-muted">
                해당 학기에 시간표가 없습니다.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
