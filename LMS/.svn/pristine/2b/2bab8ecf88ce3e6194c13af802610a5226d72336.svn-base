import React, { useEffect, useRef, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'

export default function BoardPostEdit() {
  const { lectureId, postId } = useParams()
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
  const [currentFiles, setCurrentFiles] = useState([])

  // Loading state
  const [loading, setLoading] = useState(true)

  // Alert state
  const [error, setError] = useState({ show: false, message: '' })
  const [toast, setToast] = useState({ show: false, message: '' })

  // Date formatting helpers
  const pad2 = (n) => String(n).padStart(2, '0')
  const fmtDate = (d) => `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`
  const fmtTime = (d) => `${pad2(d.getHours())}:${pad2(d.getMinutes())}`

  // Compute status from post data
  const computeStatus = (post) => {
    if (post.tempSaveYn === 'Y') return 'DRAFT'
    if (post.revealAt) {
      const now = new Date()
      const r = new Date(post.revealAt)
      if (r.getTime() > now.getTime()) return 'SCHEDULED'
    }
    return 'PUBLISHED'
  }

  // Initialize CKEditor
  useEffect(() => {
    let cancelled = false

    const initEditor = async () => {
      if (editorInstanceRef.current) {
        return
      }

      if (!editorElementRef.current || !window.ClassicEditor) {
        return
      }

      try {
        const instance = await window.ClassicEditor.create(editorElementRef.current)

        if (cancelled) {
          instance.destroy().catch(console.error)
          return
        }

        editorInstanceRef.current = instance
      } catch (error) {
        console.error('CKEditor initialization failed:', error)
      }
    }

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

  // Load post data
  useEffect(() => {
    let alive = true

    const loadPost = async () => {
      try {
        setLoading(true)
        const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })

        if (!res.ok) throw new Error(`HTTP ${res.status}`)

        const post = await res.json()

        if (!alive) return

        // Set form values
        setTitle(post.title || '')
        setPostType(post.postType || 'NOTICE')
        setFileId(post.attachFileId || '')

        // Set current files
        if (Array.isArray(post.attachFileList)) {
          setCurrentFiles(post.attachFileList)
        }

        // Set status
        const statusValue = computeStatus(post)
        setStatus(statusValue)

        // Set schedule if applicable
        if (statusValue === 'SCHEDULED' && post.revealAt) {
          const d = new Date(post.revealAt)
          if (!Number.isNaN(d.getTime())) {
            setPublishDate(fmtDate(d))
            setPublishTime(fmtTime(d))
          }
        }

        // Set editor content after loading is done
        // This ensures the editor div is rendered
        setLoading(false)

        // Wait for editor to be ready and set content
        const waitForEditor = setInterval(() => {
          if (editorInstanceRef.current) {
            editorInstanceRef.current.setData(post.content || '')
            clearInterval(waitForEditor)
          }
        }, 100)

        // Clear interval after 10 seconds max
        setTimeout(() => clearInterval(waitForEditor), 10000)
      } catch (err) {
        if (!alive) return
        console.error(err)
        setError({ show: true, message: '게시글을 불러오는 중 오류가 발생했습니다.' })
        setLoading(false)
      }
    }

    loadPost()

    return () => { alive = false }
  }, [lectureId, postId])

  // Handle file selection
  const handleFileChange = async (e) => {
    const files = Array.from(e.target.files || [])
    setSelectedFiles(files)

    if (files.length > 0) {
      // TODO: Implement file upload API call
      console.log('Selected files:', files)
    }
  }

  // Build payload
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
      fileId: fileId || null,
      postType,
      tempSaveYn,
      revealAt
    }
  }

  // Form validation
  const validateForm = (payload) => {
    if (!payload.title.trim()) {
      setError({ show: true, message: '제목을 입력하세요.' })
      return false
    }

    if (payload.title.trim().length > 100) {
      setError({ show: true, message: '제목은 최대 100자입니다.' })
      return false
    }

    if (!payload.content || payload.content.replace(/<[^>]*>/g, '').trim().length === 0) {
      setError({ show: true, message: '내용을 입력하세요.' })
      return false
    }

    if (payload.content.length > 1000) {
      setError({ show: true, message: '내용은 최대 1000자입니다.' })
      return false
    }

    if (status === 'SCHEDULED' && !payload.revealAt) {
      setError({ show: true, message: '예약 게시를 선택했으면 날짜/시각을 모두 지정하세요.' })
      return false
    }

    return true
  }

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError({ show: false, message: '' })
    setToast({ show: false, message: '' })

    const payload = buildPayload()

    if (!validateForm(payload)) {
      return
    }

    const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`

    try {
      const res = await fetch(url, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(payload)
      })

      if (res.status === 204) {
        setToast({ show: true, message: '저장되었습니다.' })
        setTimeout(() => {
          navigate(`/classroom/professor/${lectureId}/board/${postId}`)
        }, 300)
        return
      }

      const text = await res.text().catch(() => '')
      throw new Error(`수정 실패 (HTTP ${res.status}) ${text.slice(0, 200)}`)
    } catch (err) {
      console.error(err)
      setError({ show: true, message: err.message || '저장 중 오류가 발생했습니다.' })
    }
  }

  // Handle cancel
  const handleCancel = () => {
    navigate(`/classroom/professor/${lectureId}/board/${postId}`)
  }

  return (
    <section id="post-edit-root" className="container py-4">
      {loading && (
        <div className="alert alert-info">
          <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          게시글을 불러오는 중...
        </div>
      )}
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">게시글 수정</h1>
        <div className="d-flex gap-2">
          <Link
            className="attendance-btn attendance-btn-default btn-sm"
            to={`/classroom/professor/${encodeURIComponent(lectureId)}/board/${encodeURIComponent(postId)}`}
          >
            상세로
          </Link>
          <Link
            className="attendance-btn attendance-btn-default btn-sm"
            to={`/classroom/professor/${encodeURIComponent(lectureId)}/board`}
          >
            목록으로
          </Link>
        </div>
      </div>

      {/* Error/Toast */}
      {error.show && (
        <div id="error-box" className="alert alert-danger" role="alert">
          {error.message}
        </div>
      )}
      {toast.show && (
        <div id="toast-box" className="alert alert-success" role="alert">
          {toast.message}
        </div>
      )}

      <form id="edit-form" className="vstack gap-3" onSubmit={handleSubmit}>
        {/* 1) 타입 + 제목 */}
        <div className="row g-3 align-items-end">
          <div className="col-12 col-md-4 col-lg-2">
            <label htmlFor="postType" className="form-label">
              타입<span className="text-danger">*</span>
            </label>
            <select
              id="postType"
              name="postType"
              className="form-select"
              value={postType}
              onChange={(e) => setPostType(e.target.value)}
              disabled={loading}
            >
              <option value="NOTICE">공지</option>
              <option value="MATERIAL">자료</option>
            </select>
          </div>
          <div className="col-12 col-md-8 col-lg-6">
            <label htmlFor="title" className="form-label">
              제목<span className="text-danger">*</span>
            </label>
            <input
              type="text"
              id="title"
              name="title"
              className="form-control"
              maxLength="100"
              placeholder="제목을 입력하세요"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              disabled={loading}
            />
          </div>
        </div>

        {/* 2) 첨부파일 */}
        <div>
          <label htmlFor="files" className="form-label">
            첨부파일(업로드 시 기존 첨부를 대체합니다)
          </label>
          <input
            type="file"
            id="files"
            name="files"
            className="form-control"
            multiple
            onChange={handleFileChange}
            disabled={loading}
          />
          <div id="file-list" className="form-text">
            {currentFiles.length > 0 ? (
              <>
                현재 첨부:{' '}
                {currentFiles.map((f, i) => {
                  const name = f.originName || `파일 ${f.fileOrder || i + 1}`
                  const ext = f.extension ? `.${f.extension}` : ''
                  return `${name}${ext}`
                }).join(', ')}
              </>
            ) : (
              '현재 첨부 없음'
            )}
          </div>
          {selectedFiles.length > 0 && (
            <div className="form-text">
              새 파일:{' '}
              {selectedFiles.map((f) => `${f.name} (${(f.size / 1024).toFixed(1)} KB)`).join(', ')}
            </div>
          )}
          <div id="file-hint" className="form-text">
            최대 5개. 새로 업로드하면 기존 첨부는 대체됩니다.
          </div>
        </div>

        {/* 3) 내용 */}
        <div>
          <label htmlFor="content" className="form-label">
            내용 <span className="text-danger">*</span>
          </label>
          <div
            ref={editorElementRef}
            id="ck-editor"
          />
          <div className="form-text">서버 제한: 최대 1000자</div>
        </div>

        {/* 4) 상태 라디오 + 저장 버튼 */}
        <div className="d-flex flex-column flex-md-row justify-content-end align-items-start align-items-md-center gap-3">
          <fieldset className="flex-grow-1">
            <legend className="col-form-label pt-0 mb-2">
              게시 상태 <span className="text-danger">*</span>
            </legend>
            <div className="d-flex flex-wrap gap-3">
              <div className="form-check">
                <input
                  className="form-check-input"
                  type="radio"
                  name="status"
                  id="stPublished"
                  value="PUBLISHED"
                  checked={status === 'PUBLISHED'}
                  onChange={(e) => setStatus(e.target.value)}
                />
                <label className="form-check-label" htmlFor="stPublished">
                  즉시 게시
                </label>
              </div>
              <div className="form-check">
                <input
                  className="form-check-input"
                  type="radio"
                  name="status"
                  id="stDraft"
                  value="DRAFT"
                  checked={status === 'DRAFT'}
                  onChange={(e) => setStatus(e.target.value)}
                />
                <label className="form-check-label" htmlFor="stDraft">
                  임시저장
                </label>
              </div>
              <div className="form-check">
                <input
                  className="form-check-input"
                  type="radio"
                  name="status"
                  id="stScheduled"
                  value="SCHEDULED"
                  checked={status === 'SCHEDULED'}
                  onChange={(e) => setStatus(e.target.value)}
                />
                <label className="form-check-label" htmlFor="stScheduled">
                  예약
                </label>
              </div>
            </div>

            {/* 예약 선택 시 노출 */}
            {status === 'SCHEDULED' && (
              <div id="schedule-row" className="row g-2 align-items-end mt-2">
                <div className="col-12 col-sm-6 col-md-4">
                  <label htmlFor="publishDate" className="form-label">게시 예정일</label>
                  <input
                    type="date"
                    id="publishDate"
                    className="form-control"
                    value={publishDate}
                    onChange={(e) => setPublishDate(e.target.value)}
                  />
                </div>
                <div className="col-12 col-sm-6 col-md-3">
                  <label htmlFor="publishTime" className="form-label">게시 예정시각</label>
                  <input
                    type="time"
                    id="publishTime"
                    className="form-control"
                    step="60"
                    value={publishTime}
                    onChange={(e) => setPublishTime(e.target.value)}
                  />
                </div>
              </div>
            )}
          </fieldset>

          {/* 버튼 */}
          <div className="ms-md-auto d-flex gap-2">
            <button type="button" className="attendance-btn attendance-btn-default" onClick={handleCancel} disabled={loading}>
              취소
            </button>
            <button type="submit" className="attendance-btn attendance-btn-save" disabled={loading}>
              저장
            </button>
          </div>
        </div>
      </form>

      <style jsx>{`
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
