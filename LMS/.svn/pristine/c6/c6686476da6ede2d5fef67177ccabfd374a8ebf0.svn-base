import React, { useEffect, useState, useRef } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Swal from 'sweetalert2'
import '../../styles/customButtons.css'

const pad = (n) => String(n).padStart(2, '0')
const fmtLocalDT = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return ''
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const normalizeAttachDetails = (raw) => {
  if (Array.isArray(raw)) return raw
  if (raw?.attachFileList && Array.isArray(raw.attachFileList)) return raw.attachFileList
  return []
}

const resolveBundleId = (bundleId, details) => {
  if (bundleId) return bundleId
  if (!Array.isArray(details)) return ''
  for (const item of details) {
    if (item?.bundleId) return item.bundleId
    if (item?.fileId) return item.fileId
  }
  return ''
}

export default function TaskIndivForm() {
  const { lectureId, indivtaskId } = useParams()
  const navigate = useNavigate()
  const apiBase = '/classroom/api/v1/professor/task'
  const commonBase = '/classroom/api/v1/common/task'

  const isEditMode = !!indivtaskId

  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [startAt, setStartAt] = useState('')
  const [endAt, setEndAt] = useState('')
  const [attachFileId, setAttachFileId] = useState('')
  const [attachDetails, setAttachDetails] = useState([])
  const [busy, setBusy] = useState(false)
  const [loading, setLoading] = useState(isEditMode)
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')

  const editorElementRef = useRef(null)
  const editorInstanceRef = useRef(null)

  // Initialize CKEditor
  useEffect(() => {
    let cancelled = false
    let editorInstance = null

    const initEditor = async () => {
      // Prevent multiple initializations
      if (cancelled || editorInstanceRef.current) {
        return
      }

      if (!editorElementRef.current) {
        return
      }

      if (!window.ClassicEditor) {
        return
      }

      try {
        // Check if element already has CKEditor instance
        const existingInstance = editorElementRef.current.ckeditorInstance
        if (existingInstance) {
          await existingInstance.destroy()
        }

        const instance = await window.ClassicEditor.create(editorElementRef.current)

        if (cancelled) {
          instance.destroy().catch(console.error)
          return
        }

        editorInstance = instance
        editorInstanceRef.current = instance
        editorElementRef.current.ckeditorInstance = instance
      } catch (error) {
        console.error('CKEditor initialization failed:', error)
      }
    }

    const loadEditor = () => {
      if (window.ClassicEditor) {
        initEditor()
      } else {
        // Check if script is already being loaded
        const existingScript = document.querySelector('script[src*="ckeditor"]')
        if (existingScript) {
          // Script is already in DOM, wait for it to load
          const onLoad = () => {
            if (!cancelled) {
              initEditor()
            }
            existingScript.removeEventListener('load', onLoad)
          }
          existingScript.addEventListener('load', onLoad)
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
      }
    }

    // Small delay to avoid double initialization in React StrictMode
    const timeoutId = setTimeout(loadEditor, 100)

    return () => {
      cancelled = true
      clearTimeout(timeoutId)

      const editor = editorInstanceRef.current || editorInstance
      if (editor && editor.ui && editor.ui.view && editor.ui.view.element) {
        editor.destroy().catch(err => {
          // Ignore errors during cleanup
          console.debug('Editor cleanup:', err)
        })
      }

      editorInstanceRef.current = null
      if (editorElementRef.current) {
        editorElementRef.current.ckeditorInstance = null
      }
    }
  }, [])

  useEffect(() => {
    const init = async () => {
      if (isEditMode) {
        // 수정 모드: 기존 데이터 로드
        try {
          setLoading(true)
          setError('')
          const url = `${apiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`
          const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
          if (!res.ok) throw new Error(`과제 조회 실패(HTTP ${res.status})`)
          const task = await res.json()

          setName(task.indivtaskName || '')
          setStartAt(fmtLocalDT(task.startAt))
          setEndAt(fmtLocalDT(task.endAt))

          const details = normalizeAttachDetails(task.attachFileList)
          const bundleId = task.fileId || task.attachFileId || resolveBundleId('', details)
          setAttachFileId(bundleId)
          setAttachDetails(details)

          setLoading(false)

          // Wait for editor to be ready and set content
          const waitForEditor = setInterval(() => {
            if (editorInstanceRef.current) {
              editorInstanceRef.current.setData(task.indivtaskDesc || '')
              clearInterval(waitForEditor)
            }
          }, 100)

          // Clear interval after 10 seconds max
          setTimeout(() => clearInterval(waitForEditor), 10000)
        } catch (err) {
          setError(err.message || String(err))
          setLoading(false)
        }
      } else {
        // 작성 모드: 기본값 설정
        const now = new Date()
        now.setSeconds(0, 0)
        setStartAt(fmtLocalDT(now.toISOString()))
      }
    }
    init()
  }, [lectureId, indivtaskId, isEditMode, apiBase])

  const onFilesChange = async (e) => {
    const files = Array.from(e.target.files || [])
    if (files.length === 0) return
    if (files.length > 5) { setError('최대 5개까지 업로드할 수 있습니다.'); return }
    try {
      setBusy(true); setError(''); setInfo('')
      const fd = new FormData()
      for (const f of files) fd.append('files', f)
      const url = `/classroom/api/v1/common/${encodeURIComponent(lectureId)}/upload?type=task`
      const resp = await fetch(url, { method: 'POST', body: fd, credentials: 'include' })
      if (!resp.ok) throw new Error(`(${resp.status}) 업로드 실패`)
      const data = await resp.json()
      const bundle = data.bundleId || data.fileId || (Array.isArray(data.fileIds) && data.fileIds[0]) || (Array.isArray(data.files) && data.files[0]?.fileId) || (Array.isArray(data.details) && data.details[0]?.fileId) || ''
      const details = (Array.isArray(data.files) && data.files) || (Array.isArray(data.details) && data.details) || []
      setAttachFileId(bundle)
      setAttachDetails(details)
    } catch (err) {
      setError(err.message || String(err))
    } finally {
      setBusy(false)
    }
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    try {
      setBusy(true); setError(''); setInfo('')
      if (!name.trim()) throw new Error('과제명을 입력하세요.')

      const descContent = editorInstanceRef.current ? editorInstanceRef.current.getData() : desc
      if (!descContent.trim()) throw new Error('과제 설명을 입력하세요.')

      if (!startAt) throw new Error('과제 시작일시를 입력하세요.')
      if (startAt && endAt && startAt > endAt) throw new Error('시작 일시는 마감 일시 이전이어야 합니다.')

      const payload = {
        indivtaskName: name.trim(),
        indivtaskDesc: descContent.trim(),
        startAt,
        endAt: endAt || null,
        attachFileId: attachFileId || ''
      }

      if (isEditMode) {
        // 수정 모드
        payload.indivtaskId = indivtaskId
        const url = `${apiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`
        const resp = await fetch(url, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
          credentials: 'include',
          body: JSON.stringify(payload)
        })
        if (!resp.ok) {
          const msg = await resp.text().catch(() => resp.statusText)
          throw new Error(`저장 실패(${resp.status}): ${msg}`)
        }
        await Swal.fire({
          title: '저장 완료',
          text: '개인과제가 저장되었습니다.',
          icon: 'success',
          confirmButtonText: '확인'
        })
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/${encodeURIComponent(indivtaskId)}`)
      } else {
        // 작성 모드
        const url = `${apiBase}/${encodeURIComponent(lectureId)}/indiv`
        const resp = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify(payload)
        })
        if (!resp.ok) {
          const msg = await resp.text().catch(() => resp.statusText)
          throw new Error(`등록 실패(${resp.status}): ${msg}`)
        }
        const loc = resp.headers.get('Location')
        await Swal.fire({
          title: '등록 완료',
          text: '개인과제가 등록되었습니다.',
          icon: 'success',
          confirmButtonText: '확인'
        })
        navigate(loc || `/classroom/professor/${encodeURIComponent(lectureId)}/task`)
      }
    } catch (err) {
      setError(err.message || String(err))
    } finally {
      setBusy(false)
    }
  }

  if (loading) {
    return (
      <section id="task-form-root" className="container py-4">
        <div className="text-muted">불러오는 중...</div>
      </section>
    )
  }

  return (
    <section id="task-form-root" className="container py-4">
      <form id="task-form" onSubmit={onSubmit}>
        {error && (
          <div id="error-box" className="alert alert-danger alert-dismissible fade show" role="alert">
            {error}
            <button type="button" className="btn-close" onClick={() => setError('')} aria-label="Close"></button>
          </div>
        )}
        {info && (
          <div id="info-box" className="alert alert-success alert-dismissible fade show" role="alert">
            {info}
            <button type="button" className="btn-close" onClick={() => setInfo('')} aria-label="Close"></button>
          </div>
        )}

        <div className="row g-4">
          {/* Main Content */}
          <div className="col-lg-8">
            <div className="card mb-4">
              <div className="card-header">
                <h5 className="card-title mb-0" style={{ fontSize: '1.2em' }}>{isEditMode ? '개인과제 수정' : '개인과제 작성'}</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label htmlFor="task-name" className="form-label">과제명</label>
                  <input id="task-name" type="text" className="form-control form-control-lg" maxLength={50} required disabled={busy} value={name} onChange={(e) => setName(e.target.value)} placeholder="주제 5개 정리보고서 제출" style={{ padding: '6px' }} />
                </div>

                <div>
                  <label htmlFor="task-desc" className="form-label fs-5">과제 설명</label>
                  {!loading && <div ref={editorElementRef} id="task-desc" />}
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="col-lg-4">
            <div className="vstack gap-4">
              {/* Schedule Card */}
              <div className="card">
                <div className="card-header">
                  <h5 className="card-title mb-0">일정 설정</h5>
                </div>
                <div className="card-body">
                  <div className="mb-3">
                    <label htmlFor="start-at" className="form-label">과제 시작일시</label>
                    <input id="start-at" type="datetime-local" className="form-control" required disabled={busy} value={startAt} onChange={(e) => setStartAt(e.target.value)} style={{ padding: '6px' }} />
                  </div>
                  <div>
                    <label htmlFor="end-at" className="form-label">과제 마감일시</label>
                    <input id="end-at" type="datetime-local" className="form-control" disabled={busy} value={endAt} onChange={(e) => setEndAt(e.target.value)} style={{ padding: '6px' }} />
                  </div>
                </div>
              </div>

              {/* Attachments Card */}
              <div className="card">
                <div className="card-header">
                  <h5 className="card-title mb-0">첨부파일</h5>
                </div>
                <div className="card-body">
                  <input id="file-input" type="file" className="form-control" multiple disabled={busy} onChange={onFilesChange} style={{ padding: '6px' }} />
                  <div className="form-text mt-2">
                    {attachFileId && attachDetails.length > 0 ? (
                      <ul className="list-unstyled mb-0">
                        {attachDetails.map((d, i) => {
                          const name = d.originName ?? d.originalName ?? `첨부 ${d.fileOrder ?? d.order ?? i}`
                          const ext = d.extension ? `.${d.extension}` : ''
                          const size = typeof d.fileSize === 'number' ? ` (${(d.fileSize / 1024).toFixed(1)} KB)` : ''
                          return (
                            <li key={i} className="small">• {name}{ext}{size}</li>
                          )
                        })}
                      </ul>
                    ) : (
                      '첨부파일은 최대 5개까지 업로드할 수 있습니다.'
                    )}
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="d-flex justify-content-end gap-2">
                <Link to={isEditMode ? `/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/${encodeURIComponent(indivtaskId)}` : `/classroom/professor/${encodeURIComponent(lectureId)}/task`} className="custom-btn custom-btn-outline-secondary custom-btn-md">취소</Link>
                <button id="btn-submit" type="submit" className="custom-btn custom-btn-primary custom-btn-md" disabled={busy}>{busy ? (isEditMode ? '저장중…' : '등록중…') : (isEditMode ? '저장' : '등록')}</button>
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

