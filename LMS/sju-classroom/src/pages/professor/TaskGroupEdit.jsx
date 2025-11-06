import React, { useEffect, useState, useRef } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Swal from 'sweetalert2'

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

export default function TaskGroupEdit() {
  const { lectureId, grouptaskId } = useParams()
  const navigate = useNavigate()
  const apiBase = '/classroom/api/v1/professor/task'
  const commonBase = '/classroom/api/v1/common/task'

  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [startAt, setStartAt] = useState('')
  const [endAt, setEndAt] = useState('')
  const [attachFileId, setAttachFileId] = useState('')
  const [attachDetails, setAttachDetails] = useState([])
  const [busy, setBusy] = useState(false)
  const [loading, setLoading] = useState(true)
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
      try {
        setLoading(true)
        setError('')
        const url = `${apiBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`조별과제 조회 실패(HTTP ${res.status})`)
        const task = await res.json()

        setName(task.grouptaskName || '')
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
            editorInstanceRef.current.setData(task.grouptaskDesc || '')
            clearInterval(waitForEditor)
          }
        }, 100)

        // Clear interval after 10 seconds max
        setTimeout(() => clearInterval(waitForEditor), 10000)
      } catch (err) {
        setError(err.message || String(err))
        setLoading(false)
      }
    }
    init()
  }, [lectureId, grouptaskId, apiBase])

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
      setInfo('첨부가 업로드되어 현재 첨부로 연결되었습니다.')
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
        grouptaskId,
        grouptaskName: name.trim(),
        grouptaskDesc: descContent.trim(),
        startAt,
        endAt: endAt || null,
        attachFileId: attachFileId || ''
      }

      const url = `${apiBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}`
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
        text: '조별과제가 저장되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })
      navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}`)
    } catch (err) {
      setError(err.message || String(err))
    } finally {
      setBusy(false)
    }
  }

  if (loading) {
    return (
      <section id="task-group-edit-root" className="container py-4">
        <div className="text-muted">불러오는 중...</div>
      </section>
    )
  }

  return (
    <section id="task-group-edit-root" className="container py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mb-0">
            <li>
              <Link className="text-decoration-none" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task`}>과제</Link>
              <span className="mx-2">&gt;</span>
            </li>
            <li>
              <Link className="text-decoration-none" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task`}>조별과제 목록</Link>
              <span className="mx-2">&gt;</span>
            </li>
            <li className="breadcrumb-item active fw-bold" aria-current="page">조별과제 수정</li>
          </ol>
        </nav>
        <div className="d-flex gap-2">
          <Link className="btn btn-sm btn-outline-secondary" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}`}>상세</Link>
          <Link className="btn btn-sm btn-outline-secondary" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task`}>목록</Link>
        </div>
      </div>

      <form id="task-group-form" className="card shadow-sm" onSubmit={onSubmit}>
        <div className="card-body">
          <div className="mb-3">
            <label htmlFor="task-name" className="form-label">과제명</label>
            <input id="task-name" type="text" className="form-control" maxLength={50} required disabled={busy} value={name} onChange={(e) => setName(e.target.value)} placeholder="조별 프로젝트 발표자료 제출" />
            <div className="text-muted small">최대 50자</div>
          </div>

          <div className="mb-3">
            <label htmlFor="task-desc" className="form-label">과제 설명</label>
            <div ref={editorElementRef} id="task-desc" />
            <div className="text-muted small">최대 4000자(권장)</div>
          </div>

          <div className="row g-3">
            <div className="col-12 col-md-6">
              <label htmlFor="start-at" className="form-label">과제 시작일시</label>
              <input id="start-at" type="datetime-local" className="form-control" required disabled={busy} value={startAt} onChange={(e) => setStartAt(e.target.value)} />
            </div>
            <div className="col-12 col-md-6">
              <label htmlFor="end-at" className="form-label">과제 마감일시</label>
              <input id="end-at" type="datetime-local" className="form-control" disabled={busy} value={endAt} onChange={(e) => setEndAt(e.target.value)} />
            </div>
          </div>

          <div className="mt-4">
            <label className="form-label">첨부파일</label>
            <input id="file-input" type="file" className="form-control" multiple disabled={busy} onChange={onFilesChange} />
            <div className="text-muted small">파일을 선택하면 즉시 업로드되고 현재 첨부가 대체됩니다. (최대 5개)</div>
            {attachFileId && (
              <div id="current-attach-box" className="mt-2">
                <div className="alert alert-info py-2 mb-2">현재 첨부 파일ID: <span id="current-file-id">{attachFileId || '(없음)'}</span></div>
                <ul id="current-file-list" className="list-group small">
                  {attachDetails.map((d, i) => {
                    const name = d.originName ?? d.originalName ?? `첨부 ${d.fileOrder ?? d.order ?? i}`
                    const ext = d.extension ? `.${d.extension}` : ''
                    const size = typeof d.fileSize === 'number' ? ` (${d.fileSize.toLocaleString()}B)` : ''
                    const order = d.fileOrder ?? d.order
                    const href = (order == null)
                      ? '#'
                      : `${commonBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/attach/${encodeURIComponent(order)}`
                    return (
                      <li key={i} className="list-group-item d-flex justify-content-between align-items-center">
                        <div className="text-truncate" style={{ maxWidth: '70%' }}>{name}{ext}{size}</div>
                        {order != null && (
                          <a className="btn btn-sm btn-outline-secondary" href={href}>다운로드</a>
                        )}
                      </li>
                    )
                  })}
                </ul>
              </div>
            )}
          </div>
        </div>

        <div className="card-footer bg-white d-flex justify-content-between align-items-center flex-wrap gap-2">
          <div>
            {error && <div className="alert alert-danger py-2 mb-0">{error}</div>}
            {info && <div className="alert alert-success py-2 mb-0">{info}</div>}
          </div>
          <div className="d-flex gap-2">
            <button type="button" className="btn btn-outline-secondary" disabled={busy} onClick={() => navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}`)}>취소</button>
            <button type="submit" className="btn btn-primary" disabled={busy}>{busy ? '저장 중...' : '저장'}</button>
          </div>
        </div>
      </form>

      <div className="alert alert-info mt-3">
        <strong>안내:</strong> 이 페이지에서는 조별과제의 기본 정보(과제명, 설명, 기간, 첨부)만 수정할 수 있습니다. 조 구성 등의 수정은 별도 페이지에서 진행해야 합니다.
      </div>
    </section>
  )
}
