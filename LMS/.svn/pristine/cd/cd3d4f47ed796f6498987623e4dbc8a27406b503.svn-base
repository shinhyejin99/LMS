import React, { useEffect, useMemo, useRef, useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import '../../styles/taskGroupForm.css'

const pad = (n) => String(n).padStart(2, '0')
const toLocalDT = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return ''
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}
const normalizeLocalDT = (v) => {
  if (!v) return null
  const s = String(v).trim()
  if (!s) return null
  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(s)) return `${s}:00`
  return s
}
const buildGroup = (index) => ({
  tempId: `GRP${Math.random().toString(36).slice(2, 10)}`,
  groupName: `그룹 ${index + 1}`,
  leaderEnrollId: null,
  members: [],
})

export default function TaskGroupForm() {
  const { lectureId } = useParams()
  const navigate = useNavigate()
  const studentsApiBase = '/classroom/api/v1/professor'
  const profTaskApi = '/classroom/api/v1/professor/task'

  const [name, setName] = useState('')
  const [desc, setDesc] = useState('')
  const [startAt, setStartAt] = useState('')
  const [endAt, setEndAt] = useState('')
  const [attachFileId, setAttachFileId] = useState('')
  const [attachDetails, setAttachDetails] = useState([])
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')

  const [students, setStudents] = useState([])
  const [studentsLoading, setStudentsLoading] = useState(false)
  const [studentError, setStudentError] = useState('')
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState(new Set())
  const [groups, setGroups] = useState([])

  const descRef = useRef(null)
  const editorRef = useRef(null)
  const editorScriptRef = useRef(null)

  const showError = (msg) => {
    setError(msg || '오류가 발생했습니다.')
    setInfo('')
  }
  const showInfo = (msg) => {
    setInfo(msg || '처리되었습니다.')
    setError('')
  }
  const clearFeedback = () => {
    setError('')
    setInfo('')
  }

  useEffect(() => {
    const now = new Date()
    now.setSeconds(0, 0)
    setStartAt(toLocalDT(now.toISOString()))
  }, [])

  const fetchStudents = async () => {
    if (!lectureId) {
      showError('강의 정보가 없습니다.')
      return
    }
    try {
      setStudentsLoading(true)
      setStudentError('')
      clearFeedback()
      const url = `${studentsApiBase}/${encodeURIComponent(lectureId)}/students`
      const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
      if (!res.ok) throw new Error(`학생 목록 조회 실패 (HTTP ${res.status})`)
      const data = await res.json()
      const EXCLUDE = new Set(['ENR_CANCEL', 'ENR_WITHDRAW'])
      const list = (Array.isArray(data) ? data : [])
        .filter(s => !EXCLUDE.has(s.enrollStatusCd))
        .map(s => ({
          enrollId: String(s.enrollId || ''),
          studentNo: String(s.studentNo || ''),
          studentName: `${s.lastName || ''}${s.firstName || ''}`.trim(),
          deptName: s.univDeptName || s.deptName || '',
        }))
        .filter(s => s.enrollId)
      setStudents(list)
      showInfo(`학생 ${list.length}명을 불러왔습니다.`)
    } catch (err) {
      const message = err instanceof Error ? err.message : '학생 정보를 불러오는 중 문제가 발생했습니다.'
      setStudents([])
      setStudentError(message)
      showError(message)
    } finally {
      setStudentsLoading(false)
    }
  }

  useEffect(() => {
    fetchStudents()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lectureId])

  useEffect(() => {
    setSelected(prev => {
      const available = new Set(students.map(s => s.enrollId))
      const assigned = new Set(groups.flatMap(g => g.members))
      const next = new Set()
      prev.forEach(id => {
        if (available.has(id) && !assigned.has(id)) {
          next.add(id)
        }
      })
      return next
    })
  }, [students, groups])

  const assignedEnrolls = useMemo(() => new Set(groups.flatMap(g => g.members)), [groups])

  const filteredStudents = useMemo(() => {
    const q = search.trim().toLowerCase()
    return students.filter(s => {
      if (assignedEnrolls.has(s.enrollId)) return false
      if (!q) return true
      return (
        s.studentNo.toLowerCase().includes(q) ||
        s.studentName.toLowerCase().includes(q) ||
        s.deptName.toLowerCase().includes(q)
      )
    })
  }, [students, search, assignedEnrolls])

  const toggleSelect = (enrollId) => {
    setSelected(prev => {
      const next = new Set(prev)
      if (next.has(enrollId)) next.delete(enrollId)
      else next.add(enrollId)
      return next
    })
  }

  const assignSelectedToGroup = () => {
    if (selected.size === 0) {
      showError('선택한 학생이 없습니다.')
      return
    }

    let added = 0
    let targetName = ''
    setGroups(prev => {
      const cloned = prev.map(g => ({ ...g, members: [...g.members] }))
      if (cloned.length === 0) {
        cloned.push(buildGroup(0))
      }
      const targetIndex = cloned.length - 1
      const target = cloned[targetIndex]
      const memberSet = new Set(target.members)

      selected.forEach(id => {
        if (!memberSet.has(id)) {
          memberSet.add(id)
          added += 1
        }
      })

      if (!target.groupName.trim()) {
        target.groupName = `그룹 ${targetIndex + 1}`
      }

      target.members = Array.from(memberSet)
      targetName = target.groupName
      return cloned
    })

    if (added === 0) {
      showError('선택한 학생이 없습니다.')
      return
    }

    setSelected(new Set())
    showInfo(`${targetName}에 ${added}명 배정했습니다.`)
  }

  const unassignAll = () => {
    const moved = groups.reduce((sum, g) => sum + g.members.length, 0)
    if (moved === 0) {
      showInfo('배정된 학생이 없습니다.')
      return
    }
    setGroups(prev => prev.map(g => ({ ...g, members: [], leaderEnrollId: null })))
    showInfo(`배정을 해제했습니다. (총 ${moved}명)`)
  }

  const addGroup = () => {
    setGroups(prev => {
      const next = prev.map(g => ({ ...g, members: [...g.members] }))
      next.push(buildGroup(next.length))
      return next
    })
  }

  const removeEmptyGroups = () => {
    const before = groups.length
    const next = groups.filter(g => g.members.length > 0)
    setGroups(next)
    const removed = before - next.length
    if (removed > 0) showInfo(`빈 그룹 ${removed}개를 삭제했습니다.`)
    else showInfo('삭제할 빈 그룹이 없습니다.')
  }

  const updateGroupName = (gid, value) => {
    setGroups(prev => prev.map(g => (g.tempId === gid ? { ...g, groupName: value } : g)))
  }

  const clearGroupMembers = (gid) => {
    setGroups(prev => prev.map(g => (g.tempId === gid ? { ...g, members: [], leaderEnrollId: null } : g)))
    showInfo('구성원을 비웠습니다.')
  }

  const deleteGroup = (gid) => {
    setGroups(prev => prev.filter(g => g.tempId !== gid))
    showInfo('그룹을 삭제했습니다.')
  }

  const setLeader = (gid, enrollId) => {
    setGroups(prev => prev.map(g => (g.tempId === gid ? { ...g, leaderEnrollId: enrollId } : g)))
  }

  const removeMember = (gid, enrollId) => {
    setGroups(prev => prev.map(g => {
      if (g.tempId !== gid) return g
      const filtered = g.members.filter(m => m !== enrollId)
      return {
        ...g,
        members: filtered,
        leaderEnrollId: g.leaderEnrollId === enrollId ? null : g.leaderEnrollId,
      }
    }))
  }

  const handleFileChange = async (e) => {
    const files = Array.from(e.target.files || [])
    if (files.length === 0) return
    if (!lectureId) {
      showError('강의 정보가 없습니다.')
      return
    }
    if (files.length > 5) {
      showError('최대 5개까지 업로드할 수 있습니다.')
      return
    }

    try {
      clearFeedback()
      setBusy(true)
      const formData = new FormData()
      files.forEach(file => formData.append('files', file))
      const qs = new URLSearchParams({ type: 'task' })
      const url = `/classroom/api/v1/common/${encodeURIComponent(lectureId)}/upload?${qs}`
      const resp = await fetch(url, { method: 'POST', body: formData, credentials: 'include' })
      if (!resp.ok) throw new Error(`파일 업로드 실패 (${resp.status})`)
      const data = await resp.json()
      const bundleId =
        data.bundleId ||
        data.fileId ||
        (Array.isArray(data.fileIds) && data.fileIds[0]) ||
        (Array.isArray(data.files) && data.files[0]?.fileId) ||
        (Array.isArray(data.details) && data.details[0]?.fileId) ||
        ''
      const details =
        (Array.isArray(data.files) && data.files) ||
        (Array.isArray(data.details) && data.details) ||
        []
      setAttachFileId(bundleId)
      setAttachDetails(details)
      showInfo('파일을 업로드했습니다.')
    } catch (err) {
      showError(err instanceof Error ? err.message : '파일 업로드 중 문제가 발생했습니다.')
    } finally {
      setBusy(false)
      e.target.value = ''
    }
  }

  useEffect(() => {
    let mounted = true
    let editorInstance = null

    const initEditor = async () => {
      if (!mounted || editorRef.current) return
      if (!window.ClassicEditor || !descRef.current) return

      try {
        // Check if element already has CKEditor instance
        const existingInstance = descRef.current.ckeditorInstance
        if (existingInstance) {
          await existingInstance.destroy()
        }

        const editor = await window.ClassicEditor.create(descRef.current, {
          toolbar: ['heading', '|', 'bold', 'italic', 'link', 'bulletedList', 'numberedList', 'blockQuote', '|', 'insertTable', 'undo', 'redo'],
          placeholder: '과제 설명, 평가 기준, 제출 형식을 입력해주세요.',
        })

        if (!mounted) {
          editor.destroy().catch(console.error)
          return
        }

        editorInstance = editor
        editorRef.current = editor
        descRef.current.ckeditorInstance = editor

        editor.model.document.on('change:data', () => {
          setDesc(editor.getData())
        })
      } catch (err) {
        console.error('CKEditor initialization failed:', err)
      }
    }

    const loadEditor = () => {
      if (window.ClassicEditor) {
        initEditor()
      } else {
        const existingScript = document.querySelector('script[src*="ckeditor"]')
        if (existingScript) {
          const onLoad = () => {
            if (mounted) {
              initEditor()
            }
            existingScript.removeEventListener('load', onLoad)
          }
          existingScript.addEventListener('load', onLoad)
        } else {
          const script = document.createElement('script')
          script.src = 'https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js'
          script.async = true
          script.onload = () => {
            if (mounted) {
              initEditor()
            }
          }
          document.head.appendChild(script)
          editorScriptRef.current = script
        }
      }
    }

    // Small delay to avoid double initialization in React StrictMode
    const timeoutId = setTimeout(loadEditor, 100)

    return () => {
      mounted = false
      clearTimeout(timeoutId)

      const editor = editorRef.current || editorInstance
      if (editor && editor.ui && editor.ui.view && editor.ui.view.element) {
        editor.destroy().catch(err => {
          // Ignore errors during cleanup
          console.debug('Editor cleanup:', err)
        })
      }

      editorRef.current = null
      if (descRef.current) {
        descRef.current.ckeditorInstance = null
      }
      editorScriptRef.current = null
    }
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    clearFeedback()
    try {
      if (!lectureId) throw new Error('강의 정보가 없습니다.')
      const finalName = name.trim()
      const finalDesc = editorRef.current ? editorRef.current.getData().trim() : desc.trim()
      const start = normalizeLocalDT(startAt)
      const end = normalizeLocalDT(endAt)

      if (!finalName) throw new Error('과제명을 입력해주세요.')
      if (!finalDesc) throw new Error('과제 설명을 입력해주세요.')
      if (!start) throw new Error('시작 일시를 입력해주세요.')
      if (start && end && start > end) throw new Error('마감 일시는 시작 일시 이후여야 합니다.')

      const payload = {
        grouptaskName: finalName,
        grouptaskDesc: finalDesc,
        startAt: start,
        endAt: end,
        attachFileId: attachFileId || '',
        groups: groups.map(g => ({
          groupName: g.groupName,
          leaderEnrollId: g.leaderEnrollId,
          crews: g.members,
        })),
      }

      setBusy(true)
      const url = `${profTaskApi}/${encodeURIComponent(lectureId)}/group`
      const res = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        body: JSON.stringify(payload),
        credentials: 'include',
      })

      if (!res.ok) {
        let serverMsg = ''
        try {
          const contentType = res.headers.get('Content-Type') || ''
          if (contentType.includes('application/json')) {
            const data = await res.json()
            serverMsg = data?.message || data?.error || JSON.stringify(data)
          } else {
            serverMsg = await res.text()
          }
        } catch {
          serverMsg = ''
        }
        throw new Error(`조별 과제 등록 실패 (HTTP ${res.status})${serverMsg ? ` - ${serverMsg}` : ''}`)
      }

      const responseData = await res.json()
      const grouptaskId = responseData?.grouptaskId || responseData?.id || responseData?.data?.grouptaskId

      alert('조별 과제를 등록했습니다.')

      if (grouptaskId) {
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}`)
      } else {
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/task`)
      }
    } catch (err) {
      showError(err instanceof Error ? err.message : '요청 처리 중 문제가 발생했습니다.')
    } finally {
      setBusy(false)
    }
  }

  const currentAttachBoxClass = `mt-2${attachFileId || attachDetails.length ? '' : ' d-none'}`

  return (
    <section
      id="grouptask-form-root"
      className="container py-4"
      data-lecture-id={lectureId || ''}
      data-prof-base={profTaskApi}
      data-common-base="/classroom/api/v1/common"
    >
      <div className="d-flex align-items-center justify-content-between mb-3">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mb-0">
            <li className="breadcrumb-item">
              <Link to={`/classroom/professor/${encodeURIComponent(lectureId || '')}/task/indiv`}>과제</Link>
            </li>
            <li className="breadcrumb-item active" aria-current="page">조별과제 등록</li>
          </ol>
        </nav>
        <div className="d-flex gap-2">
          <Link className="btn btn-sm btn-outline-secondary" to={`/classroom/professor/${encodeURIComponent(lectureId || '')}/task/group`}>
            조별과제 목록
          </Link>
        </div>
      </div>

      <form id="grouptask-form" className="card shadow-sm" autoComplete="off" onSubmit={handleSubmit}>
        <div className="card-body">
          <div className="row g-3 mb-3">
            <div className="col-12 col-md-6">
              <label htmlFor="grouptask-name" className="form-label required">과제명</label>
              <input
                type="text"
                id="grouptask-name"
                className="form-control"
                maxLength={50}
                required
                placeholder="예) 팀 프로젝트 1차 발표"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
              <div className="form-hint">최대 50자</div>
            </div>
            <div className="col-12 col-md-3">
              <label htmlFor="start-at" className="form-label required">시작 일시</label>
              <input
                type="datetime-local"
                id="start-at"
                className="form-control"
                required
                value={startAt}
                onChange={(e) => setStartAt(e.target.value)}
              />
            </div>
            <div className="col-12 col-md-3">
              <label htmlFor="end-at" className="form-label">마감 일시</label>
              <input
                type="datetime-local"
                id="end-at"
                className="form-control"
                value={endAt}
                onChange={(e) => setEndAt(e.target.value)}
              />
            </div>
          </div>

          <div className="mb-3">
            <label htmlFor="grouptask-desc" className="form-label required">과제 설명</label>
            <textarea
              id="grouptask-desc"
              className="form-control d-none"
              ref={descRef}
              defaultValue={desc}
              placeholder="과제 내용, 평가 기준, 제출 형식을 입력해주세요."
            />
            <div className="form-hint">최대 4000자 (필수)</div>
          </div>

          <div className="mt-4">
            <label className="form-label" htmlFor="file-input">첨부파일</label>
            <input type="file" id="file-input" className="form-control" multiple onChange={handleFileChange} disabled={busy} />
            <div className="form-hint">새 파일을 업로드하면 기존 첨부는 일괄 교체됩니다. (최대 5개)</div>

            <input type="hidden" id="attach-file-id" value={attachFileId} readOnly />
            <div id="current-attach-box" className={currentAttachBoxClass}>
              <div className="alert alert-info py-2 mb-2">
                현재 첨부 묶음 ID: <span id="current-file-id">{attachFileId || '(없음)'}</span>
              </div>
              {attachDetails.length > 0 && (
                <ul id="current-file-list" className="list-group small">
                  {attachDetails.map((file, index) => {
                    const origin = file.originName ?? file.originalName ?? `첨부 ${file.fileOrder ?? file.order ?? index + 1}`
                    const ext = file.extension ? `.${file.extension}` : ''
                    const size = typeof file.fileSize === 'number' ? ` (${file.fileSize.toLocaleString()}B)` : ''
                    return <li key={`${file.fileId || origin}-${index}`} className="list-group-item">{origin}{ext}{size}</li>
                  })}
                </ul>
              )}
            </div>
          </div>

          <hr className="my-4" />

          <h2 className="h6 mb-2">조 구성</h2>
          <div className="form-hint mb-2">수강생을 불러온 뒤 체크박스를 선택해 조 이름/조장을 설정해주세요.</div>

          <div className="group-builder">
            <div>
              <div className="d-flex align-items-center justify-content-between mb-2">
                <strong>학생 목록</strong>
                <div className="d-flex gap-2">
                  <input
                    type="text"
                    id="student-search"
                    className="form-control form-control-sm"
                    placeholder="이름/학번 검색"
                    style={{ width: 180 }}
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                  <button className="btn btn-sm btn-outline-secondary" type="button" id="btn-refresh-students" onClick={fetchStudents}>
                    새로고침
                  </button>
                </div>
              </div>
              <div className="student-list list-scroll" id="student-list">
                {studentsLoading && <div className="p-2 text-muted small text-center">불러오는 중입니다...</div>}
                {!studentsLoading && studentError && <div className="p-2 text-danger small text-center">{studentError}</div>}
                {!studentsLoading && !studentError && filteredStudents.length === 0 && (
                  <div className="p-2 text-muted small text-center">표시할 학생이 없습니다.</div>
                )}
                {!studentsLoading && !studentError && filteredStudents.map(s => (
                  <label key={s.enrollId} className="d-flex align-items-center justify-content-between px-2 py-1 border-bottom">
                    <span className="small text-truncate">
                      <input
                        type="checkbox"
                        className="form-check-input me-2"
                        data-enroll={s.enrollId}
                        checked={selected.has(s.enrollId)}
                        onChange={() => toggleSelect(s.enrollId)}
                      />
                      <b>{s.studentNo}</b> · {s.studentName}
                      {s.deptName ? <small className="text-muted"> / {s.deptName}</small> : null}
                    </span>
                  </label>
                ))}
              </div>
              <div className="form-hint mt-1">체크박스를 선택한 뒤 "선택 학생 배정" 버튼으로 마지막 그룹에 배정됩니다.</div>
            </div>

            <div className="d-flex flex-column align-items-center justify-content-center">
              <button className="btn btn-sm btn-primary mb-2" type="button" id="btn-assign-selected" onClick={assignSelectedToGroup}>
                선택 학생 배정
              </button>
              <button className="btn btn-sm btn-outline-secondary" type="button" id="btn-unassign-all" onClick={unassignAll}>
                전체 배정 해제
              </button>
            </div>

            <div>
              <div className="d-flex align-items-center justify-content-between mb-2">
                <strong>그룹</strong>
                <div className="d-flex gap-2">
                  <button className="btn btn-sm btn-outline-primary" type="button" id="btn-add-group" onClick={addGroup}>
                    그룹 추가
                  </button>
                  <button className="btn btn-sm btn-outline-danger" type="button" id="btn-remove-empty-groups" onClick={removeEmptyGroups}>
                    빈 그룹 삭제
                  </button>
                </div>
              </div>

              <div className="group-list list-scroll p-2" id="group-list">
                {groups.length === 0 && (
                  <div className="p-2 text-muted small text-center">그룹이 없습니다. 그룹 추가를 눌러 구성해주세요.</div>
                )}
                {groups.map(group => (
                  <div key={group.tempId} className="group-card" data-gid={group.tempId}>
                    <div className="group-header mb-2">
                      <div className="d-flex align-items-center gap-2">
                        <input
                          className="form-control form-control-sm group-name-input"
                          data-gid={group.tempId}
                          style={{ width: 220 }}
                          value={group.groupName}
                          onChange={(e) => updateGroupName(group.tempId, e.target.value)}
                          placeholder="그룹 이름"
                        />
                      </div>
                      <div className="d-flex gap-2">
                        <button type="button" className="btn btn-sm btn-outline-secondary btn-clear-members" onClick={() => clearGroupMembers(group.tempId)}>
                          구성원 비우기
                        </button>
                        <button type="button" className="btn btn-sm btn-outline-danger btn-delete-group" onClick={() => deleteGroup(group.tempId)}>
                          그룹 삭제
                        </button>
                      </div>
                    </div>
                    <div className="group-members">
                      {group.members.length === 0 && <div className="text-muted small">배정된 학생이 없습니다.</div>}
                      {group.members.map(enrollId => {
                        const student = students.find(s => s.enrollId === enrollId)
                        if (!student) return null
                        const isLeader = group.leaderEnrollId === enrollId
                        return (
                          <div key={enrollId} className={`member-badge${isLeader ? ' leader' : ''}`} data-enroll={enrollId}>
                            <span><b>{student.studentNo}</b> · {student.studentName}</span>
                            {isLeader && <span className="badge text-bg-primary">조장</span>}
                            <span className="member-actions">
                              {!isLeader && (
                                <button
                                  type="button"
                                  className="btn btn-sm btn-outline-primary btn-set-leader"
                                  onClick={() => setLeader(group.tempId, enrollId)}
                                >
                                  조장 지정
                                </button>
                              )}
                              <button
                                type="button"
                                className="btn btn-sm btn-outline-danger btn-remove-member"
                                onClick={() => removeMember(group.tempId, enrollId)}
                              >
                                제거
                              </button>
                            </span>
                          </div>
                        )
                      })}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="card-footer d-flex justify-content-end gap-2">
          <button type="submit" id="btn-submit" className="btn btn-primary" disabled={busy}>
            {busy ? '등록 중...' : '등록'}
          </button>
        </div>
      </form>

      {error && <div id="error-box" className="alert alert-danger mt-3" role="alert">{error}</div>}
      {info && <div id="info-box" className="alert alert-success mt-3" role="alert">{info}</div>}
    </section>
  )
}
