import React, { useEffect, useRef, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import '../../styles/customButtons.css'

export default function BoardPostForm() {
  const { lectureId } = useParams()
  const navigate = useNavigate()
  const editorElementRef = useRef(null)
  const editorInstanceRef = useRef(null)

  // Form state
  const [postType, setPostType] = useState('NOTICE')
  const [title, setTitle] = useState('')
  const [status, setStatus] = useState('PUBLISHED')
  const [publishDate, setPublishDate] = useState('')
  const [publishTime, setPublishTime] = useState('')
  const [fileId, setFileId] = useState('')
  const [selectedFiles, setSelectedFiles] = useState([])

  // Alert state
  const [alert, setAlert] = useState({ show: false, type: 'danger', message: '' })

  // Initialize CKEditor
  useEffect(() => {
    let cancelled = false

    const initEditor = async () => {
      // 이미 에디터 인스턴스가 있으면 재생성하지 않음
      if (editorInstanceRef.current) {
        return
      }

      if (!editorElementRef.current || !window.ClassicEditor) {
        return
      }

      try {
        const instance = await window.ClassicEditor.create(editorElementRef.current)

        // 비동기 작업 중 언마운트되었으면 즉시 파괴
        if (cancelled) {
          instance.destroy().catch(console.error)
          return
        }

        editorInstanceRef.current = instance
      } catch (error) {
        console.error('CKEditor initialization failed:', error)
      }
    }

    // Load CKEditor script if not loaded
    if (!window.ClassicEditor) {
      // Check if script is already being loaded
      const existingScript = document.querySelector('script[src*="ckeditor"]')
      if (existingScript) {
        // Script is already in DOM, wait for it to load
        existingScript.addEventListener('load', () => {
          if (!cancelled) {
            initEditor()
          }
        })
      } else {
        // Load new script
        const script = document.createElement('script')
        script.src = 'https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js'
        script.onload = () => {
          if (!cancelled) {
            initEditor()
          }
        }
        document.head.appendChild(script)
      }
    } else {
      initEditor()
    }

    return () => {
      cancelled = true

      const editor = editorInstanceRef.current
      if (editor && editor.ui && editor.ui.view && editor.ui.view.element) {
        editor.destroy().catch(err => {
          console.debug('Editor cleanup:', err)
        })
      }

      editorInstanceRef.current = null
    }
  }, [])

  // Handle file selection
  const handleFileChange = async (e) => {
    const files = Array.from(e.target.files || [])
    setSelectedFiles(files)

    if (files.length > 0) {
      // TODO: Implement file upload API call
      // For now, just log the files
      console.log('Selected files:', files)

      // Simulated file upload
      try {
        // const uploadedFileId = await uploadClassroomFiles(lectureId, 'board', files)
        // setFileId(uploadedFileId)
      } catch (error) {
        console.error('File upload failed:', error)
      }
    }
  }

  // Build payload for submission
  const buildPayload = () => {
    const content = editorInstanceRef.current ? editorInstanceRef.current.getData() : ''

    let tempSaveYn = 'N'
    let revealAt = null

    if (status === 'DRAFT') {
      tempSaveYn = 'Y'
    } else if (status === 'SCHEDULED') {
      if (publishDate && publishTime) {
        const [hh, mm] = publishTime.split(':')
        revealAt = `${publishDate}T${hh.padStart(2, '0')}:${mm.padStart(2, '0')}:00`
      }
    }

    return {
      title,
      content,
      fileId,
      postType,
      tempSaveYn,
      revealAt
    }
  }

  // Form validation
  const validateForm = (payload) => {
    if (!payload.title.trim()) {
      setAlert({ show: true, type: 'danger', message: '제목을 입력하세요.' })
      return false
    }

    const contentText = payload.content.replace(/<[^>]*>/g, '').trim()
    if (!contentText) {
      setAlert({ show: true, type: 'danger', message: '내용을 입력하세요.' })
      return false
    }

    if (status === 'SCHEDULED' && !payload.revealAt) {
      setAlert({ show: true, type: 'danger', message: '예약 게시를 선택하셨습니다. 날짜와 시간을 모두 선택하세요.' })
      return false
    }

    return true
  }

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()
    setAlert({ show: false, type: 'danger', message: '' })

    const payload = buildPayload()

    if (!validateForm(payload)) {
      return
    }

    const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post`

    try {
      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(payload)
      })

      if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(`HTTP ${res.status} - ${text || '요청 실패'}`)
      }

      // SweetAlert로 성공 메시지 표시
      await Swal.fire({
        title: '저장 완료',
        text: '게시글이 저장되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })

      // Redirect to board list
      navigate(`/classroom/professor/${lectureId}/board`)
    } catch (error) {
      console.error(error)
      await Swal.fire({
        title: '저장 실패',
        text: `저장 중 오류가 발생했습니다. ${error.message}`,
        icon: 'error',
        confirmButtonText: '확인'
      })
    }
  }

  return (
    <section id="post-root" className="container py-4">
      <form id="post-form" onSubmit={handleSubmit}>

        {alert.show && (
          <div id="alert-box" className={`alert alert-${alert.type} alert-dismissible fade show`} role="alert">
            {alert.message}
            <button type="button" className="btn-close" onClick={() => setAlert({ ...alert, show: false })} aria-label="Close"></button>
          </div>
        )}

        <div className="row g-4">
          {/* Main Content */}
          <div className="col-lg-8">
            <div className="card mb-4">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0" style={{ fontSize: '1.2em' }}>게시글 작성</h5>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => {
                    setPostType('MATERIAL')
                    setTitle('실습 자료 - React Hook 심화 학습')
                    if (editorInstanceRef.current) {
                      editorInstanceRef.current.setData(`
                        <p>안녕하세요, 이번 주 웹 프로그래밍 실습 자료를 공유합니다.</p>
                        <p>이번 자료는 React Hook에 대한 심화 학습 내용을 다루고 있습니다. useState, useEffect, useContext 등 기본 Hook부터 커스텀 Hook 작성 방법까지 포함되어 있습니다.</p>
                        <p>첨부된 PDF 파일에는 실습 예제 코드와 함께 각 Hook의 동작 원리에 대한 상세한 설명이 포함되어 있습니다.</p>
                        <p>실습 과제는 다음 주 월요일까지 제출해주시기 바랍니다. 과제 제출 시 본인이 작성한 커스텀 Hook 예제를 포함해주세요.</p>
                        <p>자료를 학습하시면서 궁금한 점이나 어려운 부분이 있다면 언제든지 질문 게시판이나 이메일로 문의해주시기 바랍니다.</p>
                        <p>모두 열심히 학습하시어 좋은 결과 있으시길 바랍니다.</p>
                      `)
                    }
                  }}
                >
                  시연
                </button>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <div className="d-flex gap-2">
                    <select
                      id="postType"
                      name="postType"
                      className="form-select"
                      style={{ width: '120px' }}
                      value={postType}
                      onChange={(e) => setPostType(e.target.value)}
                    >
                      <option value="NOTICE">공지</option>
                      <option value="MATERIAL">자료</option>
                    </select>
                    <input
                      type="text"
                      id="title"
                      name="title"
                      className="form-control form-control-lg"
                      maxLength="200"
                      placeholder="제목을 입력하세요"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                      style={{ flex: 1 }}
                    />
                  </div>
                </div>
                <div>
                  <label htmlFor="content" className="form-label fs-5">내용</label>
                  <div ref={editorElementRef} id="ck-editor"></div>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="col-lg-4">
            <div className="vstack gap-4">
              {/* Publish Card */}
              <div className="card">
                <div className="card-header">
                  <h5 className="card-title mb-0">게시 설정</h5>
                </div>
                <div className="card-body">
                  <div className="row">
                    <div className="col-6">
                      <fieldset>
                        <legend className="visually-hidden">게시 상태</legend>
                        <div className="form-check">
                          <input className="form-check-input" type="radio" name="status" id="stPublished" value="PUBLISHED" checked={status === 'PUBLISHED'} onChange={(e) => setStatus(e.target.value)} />
                          <label className="form-check-label" htmlFor="stPublished">즉시 게시</label>
                        </div>
                        <div className="form-check">
                          <input className="form-check-input" type="radio" name="status" id="stDraft" value="DRAFT" checked={status === 'DRAFT'} onChange={(e) => setStatus(e.target.value)} />
                          <label className="form-check-label" htmlFor="stDraft">임시저장</label>
                        </div>
                        <div className="form-check">
                          <input className="form-check-input" type="radio" name="status" id="stScheduled" value="SCHEDULED" checked={status === 'SCHEDULED'} onChange={(e) => setStatus(e.target.value)} />
                          <label className="form-check-label" htmlFor="stScheduled">예약 게시</label>
                        </div>
                      </fieldset>
                    </div>
                    <div className="col-6">
                      {status === 'SCHEDULED' && (
                        <div id="schedule-row">
                          <label htmlFor="publishDatetime" className="form-label">게시일시</label>
                          <input
                            type="datetime-local"
                            id="publishDatetime"
                            className="form-control"
                            style={{ padding: '6px', minWidth: '180px' }}
                            value={publishDate && publishTime ? `${publishDate}T${publishTime}` : ''}
                            onChange={(e) => {
                              const value = e.target.value
                              if (value) {
                                const [date, time] = value.split('T')
                                setPublishDate(date)
                                setPublishTime(time)
                              }
                            }}
                          />
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>

              {/* Attachments Card */}
              <div className="card">
                <div className="card-header">
                  <h5 className="card-title mb-0">첨부파일</h5>
                </div>
                <div className="card-body">
                  <input type="file" id="files" name="files" className="form-control" style={{ padding: '6px' }} multiple onChange={handleFileChange} />
                  <div className="form-text mt-2">
                    {selectedFiles.length > 0 ? (
                      <ul className="list-unstyled mb-0">
                        {selectedFiles.map((f, i) => (
                          <li key={i} className="small">• {f.name} ({(f.size / 1024).toFixed(1)} KB)</li>
                        ))}
                      </ul>
                    ) : (
                      '선택된 파일이 없습니다.'
                    )}
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="d-flex justify-content-end gap-2">
                <Link className="custom-btn custom-btn-outline-secondary custom-btn-md" to={`/classroom/professor/${encodeURIComponent(lectureId)}/board`}>취소</Link>
                <button type="submit" className="custom-btn custom-btn-primary custom-btn-md">저장</button>
              </div>
            </div>
          </div>
        </div>
      </form>

      <style>{`
        .ck.ck-editor {
          max-width: 100%;
        }
        .ck-editor__editable {
          min-height: 400px;
        }
      `}</style>
    </section>
  )
}
