import React, { useEffect, useState } from 'react'

import { NavLink, useParams } from 'react-router-dom'

import { useLecture } from '../../context/LectureContext'



const LABELS = {

  dashboard: '수업홈',

  board: '게시판',

  attendance: '출석',

  exam: '시험',

  task: '과제',

  review: '강의평가',

  grade: '성적조회',

}



export default function StudentLectureNav() {

  const { lectureId } = useParams()

  const { lecture } = useLecture()

  const [reviewStatus, setReviewStatus] = useState({ show: false, enabled: false })



  const [tooltipKey, setTooltipKey] = useState(null)





  useEffect(() => {

    let alive = true

    const checkReview = async () => {

      // 강의가 종강 상태인지 확인

      if (!lecture || !lecture.ended) {

        setReviewStatus({ show: false, enabled: false })

        return

      }



      // 종강이면 탭은 보이게 함

      setReviewStatus({ show: true, enabled: false })



      try {

        // 평가 대상 강의 목록 조회

        const res = await fetch('/classroom/api/v1/student/review', {

          headers: { Accept: 'application/json' },

          credentials: 'include',

        })

        if (!res.ok) {

          return

        }

        const data = await res.json()

        if (!alive) return



        // 현재 강의가 평가 대상에 포함되어 있는지 확인

        const reviewList = Array.isArray(data) ? data : []

        const isEnabled = reviewList.includes(lectureId)

        setReviewStatus({ show: true, enabled: isEnabled })

      } catch {

        // 에러 발생 시에도 탭은 보이되 비활성화

      }

    }



    checkReview()

    return () => { alive = false }

  }, [lecture, lectureId])



  const base = `/classroom/student/${encodeURIComponent(lectureId || '')}`

  const makeItem = (to, label, opts = {}) => (
    <li className="nav-item" key={to}>
      <NavLink
        end={opts.end}
        className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
        to={to}
      >
        {label}
      </NavLink>
    </li>
  )

  const makeDisabledItem = (label, key, tooltip) => {

    const showTooltip = tooltip && tooltipKey === key



    const handleEnter = () => setTooltipKey(key)



    const handleLeave = () => setTooltipKey((prev) => (prev === key ? null : prev))



    return (

      <li

        className="nav-item position-relative"

        key={key}

        onMouseEnter={handleEnter}

        onMouseLeave={handleLeave}

        onFocus={handleEnter}

        onBlur={handleLeave}

      >

        <span

          className="nav-link disabled text-muted"

          style={{ cursor: 'not-allowed', pointerEvents: 'auto' }}

          role="button"

          aria-disabled="true"

          tabIndex={0}

        >

          {label}

        </span>

        {showTooltip && (

          <div

            className="position-absolute text-nowrap"

            style={{

              top: '100%',

              left: '50%',

              transform: 'translate(-50%, 0.5rem)',

              backgroundColor: '#212529',

              color: '#fff',

              padding: '0.35rem 0.6rem',

              borderRadius: '0.375rem',

              fontSize: '0.8rem',

              boxShadow: '0 0.5rem 1rem rgba(0, 0, 0, 0.18)',

              whiteSpace: 'nowrap',

              zIndex: 1050,

              pointerEvents: 'none',

            }}

            role="tooltip"

          >

            {tooltip}

            <div

              style={{

                position: 'absolute',

                top: '-0.4rem',

                left: '50%',

                transform: 'translateX(-50%)',

                width: 0,

                height: 0,

                borderLeft: '0.4rem solid transparent',

                borderRight: '0.4rem solid transparent',

                borderBottom: '0.4rem solid #212529',

              }}

            />

          </div>

        )}

      </li>

    )

  }



  return (

    <ul className="nav nav-pills mb-4">

      {makeItem(base, LABELS.dashboard, { end: true })}

      {makeItem(`${base}/board`, LABELS.board)}

      {makeItem(`${base}/attendance`, LABELS.attendance)}

      {makeItem(`${base}/task`, LABELS.task)}

      {makeItem(`${base}/exam`, LABELS.exam)}

      {reviewStatus.show && (
        <>
          {reviewStatus.enabled
            ? makeItem(`${base}/review`, LABELS.review)
            : makeDisabledItem(
              LABELS.review,
              'review',
              '\uc774\ubbf8 \uac15\uc758\ud3c9\uac00\ub97c \uc791\uc131\ud558\uc168\uc2b5\ub2c8\ub2e4'
            )}
          {reviewStatus.enabled
            ? makeDisabledItem(
              LABELS.grade,
              'grade',
              '\uac15\uc758\ud3c9\uac00\ub97c \uc791\uc131\ud55c \ud6c4 \uc774\uc6a9\ud558\uc2e4 \uc218 \uc788\uc2b5\ub2c8\ub2e4'
            )
            : makeItem(`${base}/grade`, LABELS.grade)}
        </>
      )}
    </ul>

  )

}

