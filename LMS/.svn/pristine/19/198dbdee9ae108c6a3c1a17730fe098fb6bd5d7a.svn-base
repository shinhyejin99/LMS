import React, { useEffect, useMemo, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useUser } from '../../context/UserContext'
import { useLecture } from '../../context/LectureContext'
import NotFound from '../../components/NotFound'

const fmtDateTime = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const TypeBadge = ({ t }) => {
  const type = t || ''
  if (type === 'NOTICE') return <span className="badge badge-type bg-warning text-dark border border-warning-subtle">공지</span>
  if (type === 'MATERIAL') return <span className="badge badge-type bg-info text-dark border border-info-subtle">자료</span>
  return <span className="badge badge-type text-bg-secondary">기타</span>
}

const computeStatus = (post) => {
  if (!post) return 'PUBLISHED'
  if (post.tempSaveYn === 'Y') return 'DRAFT'
  if (post.revealAt) {
    const reveal = new Date(post.revealAt)
    if (!Number.isNaN(reveal.getTime()) && reveal.getTime() > Date.now()) return 'SCHEDULED'
  }
  return 'PUBLISHED'
}

const StatusBadge = ({ status }) => {
  switch (status) {
    case 'DRAFT':
      return <span className="badge text-bg-secondary">{'임시저장'}</span>
    case 'SCHEDULED':
      return <span className="badge text-bg-primary">{'예약'}</span>
    default:
      return <span className="badge text-bg-success">{'게시'}</span>
  }
}

const ReplyItemComponent = React.memo(({
  r,
  depth = 0,
  displayNameFor,
  fmtDateTime,
  getUserId,
  inlineReplyForm,
  openInlineReply,
  closeInlineReply,
  updateInlineReplyContent,
  handleDeleteReply,
  submitInlineReply
}) => {
  const isChild = depth > 0
  const name = displayNameFor(r.userId) || r.authorName || r.writerName || r.userName || '-'
  const contentHtml = r.contentHtml
  const content = r.replyContent || r.content
  const id = String(r.lctReplyId || r.replyId || r.id)
  const myId = getUserId && getUserId()
  const isProfessor = myId && String(myId) === String(r.userId || '')
  const showInlineForm = !!inlineReplyForm

  return (
    <>
      <div
        className={`border rounded p-2 reply-item ${isChild ? 'reply-child' : ''} ${isProfessor ? 'reply-professor' : ''}`}
        data-reply-id={id}
        style={isProfessor ? { backgroundColor: '#f6f7f9' } : undefined}
      >
        <div className="d-flex align-items-start justify-content-between mb-1">
          <div className="me-2">
            <strong className="me-2">{name}</strong>
            <span className="small text-muted">{fmtDateTime(r.createAt || r.createdAt)}</span>
          </div>
          <div className="btn-group btn-group-sm">
            {!isChild && (
              <button className="btn btn-outline-secondary" onClick={() => showInlineForm ? closeInlineReply(id) : openInlineReply(id)}>
                {showInlineForm ? '취소' : '답글'}
              </button>
            )}
            <button className="btn btn-outline-danger" onClick={() => handleDeleteReply(id)}>삭제</button>
          </div>
        </div>
        <div className="reply-body text-start">
          {contentHtml ? (
            <div dangerouslySetInnerHTML={{ __html: contentHtml }} />
          ) : (
            <div style={{ whiteSpace: 'pre-wrap' }}>{content || ''}</div>
          )}
        </div>
      </div>
      {showInlineForm && (
        <form className="mt-2 p-2 border rounded bg-light" onSubmit={(e) => submitInlineReply(e, id)}>
          <div className="mb-2">
            <textarea
              className="form-control"
              rows={2}
              placeholder="답글을 입력하세요"
              value={inlineReplyForm.content || ''}
              onChange={(e) => updateInlineReplyContent(id, e.target.value)}
              style={{ resize: 'none', overflowY: 'auto', minHeight: '60px', maxHeight: '60px' }}
            />
          </div>
          <div className="d-flex justify-content-end gap-2">
            <button type="button" className="btn btn-outline-secondary btn-sm py-1 px-2" onClick={() => closeInlineReply(id)}>취소</button>
            <button type="submit" className="btn btn-primary btn-sm py-1 px-2" disabled={inlineReplyForm.submitting}>{inlineReplyForm.submitting ? '등록중…' : '등록'}</button>
          </div>
        </form>
      )}
    </>
  )
})

export default function BoardPost() {
  const { lectureId, postId } = useParams()
  const navigate = useNavigate()
  const [post, setPost] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const { user, getUserId } = useUser()
  const { students } = useLecture()

  // replies
  const [replies, setReplies] = useState([])
  const [replySort, setReplySort] = useState('asc') // 'desc' | 'asc'
  const [replyContent, setReplyContent] = useState('')
  const [replyAlert, setReplyAlert] = useState({ variant: '', message: '' })
  const [submitting, setSubmitting] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [inlineReplyForms, setInlineReplyForms] = useState({}) // { [replyId]: { content: string, submitting: boolean } }

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        setNotFound(false)
        const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (res.status === 404 || res.status === 500) {
          if (!alive) return
          setNotFound(true)
          return
        }
        if (!res.ok) throw new Error(`(${res.status}) 게시글을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        setPost(data)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, postId])

  async function loadReplies() {
    const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}/reply`
    const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
    if (!res.ok) throw new Error(`(${res.status}) 댓글을 불러오지 못했습니다.`)
    const arr = await res.json()
    if (!Array.isArray(arr)) return []
    return arr
  }

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        const arr = await loadReplies()
        if (!alive) return
        setReplies(arr)
      } catch (e) {
        // replies error는 화면 전체를 막지 않음
      }
    })()
    return () => { alive = false }
  }, [lectureId, postId])

  const roots = useMemo(() => {
    const byParent = new Map()
    for (const r of replies) {
      const p = r.parentReply || ''
      if (!byParent.has(p)) byParent.set(p, [])
      byParent.get(p).push(r)
    }
    const sortFn = (a, b) => {
      const tA = new Date(a.createAt || a.createdAt || 0).getTime()
      const tB = new Date(b.createAt || b.createdAt || 0).getTime()
      return replySort === 'desc' ? (tB - tA) : (tA - tB)
    }
    const roots = (byParent.get('') || byParent.get(null) || []).slice().sort(sortFn)
    const childrenMap = new Map()
    for (const [pid, list] of byParent.entries()) {
      if (pid) childrenMap.set(pid, list.slice().sort(sortFn))
    }
    return { roots, childrenMap }
  }, [replies, replySort])

  const displayNameFor = (uid) => {
    const id = String(uid || '')
    if (!id) return '-'
    // 1) 수강생 목록에서 찾기
    const stu = Array.isArray(students) ? students.find(s => String(s.userId || '') === id) : null
    if (stu) {
      const last = stu.lastName || ''
      const first = stu.firstName || ''
      const dept = stu.univDeptName || ''
      const name = `${last}${first}`.trim() || (stu.studentNo || id)
      return dept ? `${name}(${dept})` : name
    }
    // 2) 교수(현재 사용자) 확인
    const myId = getUserId && getUserId()
    if (myId && String(myId) === id) {
      const last = user?.lastName || ''
      const first = user?.firstName || ''
      const name = `${last}${first}`.trim() || '교수'
      return `${name} 교수`
    }
    // 3) 그 외(교수로 간주)
    return '교수'
  }

  const submitReply = async (e) => {
    e.preventDefault()
    if (!replyContent.trim()) {
      setReplyAlert({ variant: 'danger', message: '내용을 입력하세요.' })
      return
    }
    try {
      setSubmitting(true)
      setReplyAlert({ variant: '', message: '' })
      const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}/reply`
      const res = await fetch(url, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({ parentReplyId: null, replyContent }),
      })
      const raw = await res.text()
      let json = null
      try { json = raw ? JSON.parse(raw) : null } catch {}
      if (!res.ok) throw new Error(`댓글 등록 실패 (HTTP ${res.status})`)
      setReplyContent('')
      setReplyAlert({ variant: '', message: '' })
      const arr = await loadReplies()
      setReplies(arr)
    } catch (err) {
      setReplyAlert({ variant: 'danger', message: String(err.message || err) })
    } finally {
      setSubmitting(false)
    }
  }

  const openInlineReply = (replyId) => {
    setInlineReplyForms(prev => ({
      ...prev,
      [replyId]: { content: '', submitting: false }
    }))
  }

  const closeInlineReply = (replyId) => {
    setInlineReplyForms(prev => {
      const next = { ...prev }
      delete next[replyId]
      return next
    })
  }

  const updateInlineReplyContent = (replyId, content) => {
    setInlineReplyForms(prev => ({
      ...prev,
      [replyId]: { ...prev[replyId], content }
    }))
  }

  const submitInlineReply = async (e, parentId) => {
    e.preventDefault()
    const form = inlineReplyForms[parentId]
    if (!form || !form.content.trim()) {
      setReplyAlert({ variant: 'danger', message: '내용을 입력하세요.' })
      return
    }
    try {
      setInlineReplyForms(prev => ({
        ...prev,
        [parentId]: { ...prev[parentId], submitting: true }
      }))
      setReplyAlert({ variant: '', message: '' })
      const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}/reply`
      const res = await fetch(url, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({ parentReplyId: parentId || null, replyContent: form.content }),
      })
      const raw = await res.text()
      let json = null
      try { json = raw ? JSON.parse(raw) : null } catch {}
      if (!res.ok) throw new Error(`댓글 등록 실패 (HTTP ${res.status})`)
      closeInlineReply(parentId)
      setReplyAlert({ variant: '', message: '' })
      const arr = await loadReplies()
      setReplies(arr)
    } catch (err) {
      setReplyAlert({ variant: 'danger', message: String(err.message || err) })
      setInlineReplyForms(prev => ({
        ...prev,
        [parentId]: { ...prev[parentId], submitting: false }
      }))
    }
  }

  const handleDeleteReply = async (rid) => {
    const replyId = String(rid || '')
    if (!replyId) return
    if (!window.confirm('이 댓글을 삭제할까요?\n삭제 후 되돌릴 수 없습니다.')) return
    try {
      const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}/reply/${encodeURIComponent(replyId)}`
      const res = await fetch(url, { method: 'DELETE', credentials: 'include' })
      if (!res.ok) throw new Error(`댓글 삭제 실패 (HTTP ${res.status})`)
      const arr = await loadReplies()
      setReplies(arr)
    } catch (err) {
      setReplyAlert({ variant: 'danger', message: String(err.message || err) })
    }
  }

  const handleDeletePost = async () => {
    if (!window.confirm('정말로 삭제하겠습니까?')) {
      return
    }

    try {
      setDeleting(true)
      const url = `/classroom/api/v1/professor/board/${encodeURIComponent(lectureId)}/post/${encodeURIComponent(postId)}`
      const res = await fetch(url, {
        method: 'DELETE',
        headers: { Accept: 'application/json' },
        credentials: 'include'
      })

      if (res.status === 204) {
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/board`, { replace: true })
        return
      }

      const text = await res.text().catch(() => '')
      throw new Error(`삭제에 실패했습니다. (HTTP ${res.status})`)
    } catch (err) {
      console.error(err)
      setError(err)
      setDeleting(false)
    }
  }

  const ReplyItem = ({ r, depth = 0 }) => {
    const id = String(r.lctReplyId || r.replyId || r.id)
    return (
      <ReplyItemComponent
        r={r}
        depth={depth}
        displayNameFor={displayNameFor}
        fmtDateTime={fmtDateTime}
        getUserId={getUserId}
        inlineReplyForm={inlineReplyForms[id]}
        openInlineReply={openInlineReply}
        closeInlineReply={closeInlineReply}
        updateInlineReplyContent={updateInlineReplyContent}
        handleDeleteReply={handleDeleteReply}
        submitInlineReply={submitInlineReply}
      />
    )
  }

  if (notFound) {
    return <NotFound message="게시글을 찾을 수 없습니다" resourceType="게시글" />
  }

  return (
    <section id="board-post-root" className="container py-4" data-lecture-id={lectureId}>
      <div className="board-layout mx-auto" style={{ marginTop: 0, marginBottom: 0 }}>
        <div className="row g-3">
          <div className="col-lg-7">
            <div className="card shadow-sm">
              <div className="card-body" id="post-body">
                {loading && <div className="skeleton" style={{ height: '8rem' }} />}
                {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}
                {!loading && !error && post && (
                  <>
                    <div className="d-flex align-items-start justify-content-between gap-3 mb-3 flex-wrap py-1 px-2" id="post-header">
                      <div className="flex-grow-1">
                        <div className="d-flex align-items-center flex-wrap gap-2 mb-2" id="post-badge-row">
                          <TypeBadge t={post.postType || post.classification} />
                          <StatusBadge status={computeStatus(post)} />
                        </div>
                        <h1 className="h4 mb-2" id="post-title">{post.title || '(제목 없음)'}</h1>
                        <div className="d-flex flex-wrap meta-list mb-0" id="post-meta-row">
                          <div className="meta-item">작성 {fmtDateTime(post.createAt || post.createdAt)}</div>
                          {post.revealAt && <div className="meta-item">공개 {fmtDateTime(post.revealAt)}</div>}
                          {post.authorName && <div className="meta-item">작성자 {post.authorName}</div>}
                        </div>
                      </div>
                      <div className="d-flex align-items-start gap-2 post-header-actions">
                        <Link
                          className="btn btn-outline-secondary btn-sm py-1 px-2"
                          id="btn-edit-bottom"
                          to={`/classroom/professor/${encodeURIComponent(lectureId)}/board/${encodeURIComponent(postId)}/edit`}
                        >
                          수정
                        </Link>
                        <button
                          className="btn btn-outline-danger btn-sm py-1 px-2"
                          id="btn-delete-bottom"
                          onClick={handleDeletePost}
                          disabled={deleting}
                        >
                          {deleting ? '삭제중...' : '삭제'}
                        </button>
                      </div>
                    </div>
                    <hr className="my-3" />
                    <article className="post-content mb-4 p-1" id="post-content">
                      <div dangerouslySetInnerHTML={{ __html: post.contentHtml || post.content || '' }} />
                    </article>
                    {Array.isArray(post.attachments) && post.attachments.length > 0 && (
                      <div id="attach-area" className="mt-4">
                        <h2 className="h6 mb-2">첨부파일</h2>
                        <ul className="list-group" id="attach-list">
                          {post.attachments.map((f, i) => (
                            <li key={i} className="list-group-item d-flex justify-content-between align-items-center">
                              <span className="text-truncate me-2" title={f.fileName || f.name}>{f.fileName || f.name}</span>
                              {f.url && <a className="btn btn-sm btn-outline-secondary py-1 px-2" href={f.url}>다운로드</a>}
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </>
                )}
              </div>
            </div>
          </div>

          <div className="col-lg-5">
            <section id="reply-section" style={{ marginTop: 0 }}>
              <div className="card shadow-sm" style={{ marginBottom: 0 }}>
                <div className="card-header d-flex align-items-center justify-content-between gap-2 flex-wrap py-2">
                  <h2 className="h5 mb-0">댓글</h2>
                  <div className="btn-group btn-group-sm" role="group" aria-label="댓글 정렬">
                    <button type="button" className={`btn py-1 px-2 ${replySort==='desc'?'btn-primary':'btn-outline-secondary'}`} id="reply-sort-newest" aria-pressed={replySort==='desc'} onClick={()=>setReplySort('desc')}>최신순</button>
                    <button type="button" className={`btn py-1 px-2 ${replySort==='asc'?'btn-primary':'btn-outline-secondary'}`} id="reply-sort-oldest" aria-pressed={replySort==='asc'} onClick={()=>setReplySort('asc')}>오래된순</button>
                  </div>
                </div>
                <div className="card-body pb-2 p-2">
              <form id="reply-form" className="vstack gap-2 mb-3" onSubmit={submitReply}>
                <div>
                  <label htmlFor="reply-content" className="form-label">댓글 내용</label>
                  <textarea className="form-control reply-textarea" id="reply-content" rows={2} placeholder="댓글을 입력하세요" value={replyContent} onChange={(e)=>setReplyContent(e.target.value)} style={{ resize: 'none', overflowY: 'auto', minHeight: '60px', maxHeight: '60px' }} />
                </div>
                <div className="d-flex justify-content-end">
                  <button type="submit" className="btn btn-primary btn-sm py-1 px-2" id="reply-submit" disabled={submitting}>{submitting?'등록중…':'등록'}</button>
                </div>
              </form>

              <hr className="my-2" />
              <div className="vstack gap-1">
                {replies.length === 0 ? (
                  <div id="reply-empty" className="text-muted small text-center py-4">댓글이 아직 없습니다. 첫 댓글을 남겨보세요.</div>
                ) : (
                  <div id="reply-list" className="vstack gap-2">
                    {roots.roots.map((r) => (
                      <div key={r.lctReplyId||r.replyId||r.id} className="vstack gap-2">
                        <ReplyItem r={r} depth={0} />
                        {(roots.childrenMap.get(r.lctReplyId||r.replyId||r.id) || []).map(child => (
                          <div key={(child.lctReplyId||child.replyId||child.id)} className="ms-4">
                            <ReplyItem r={child} depth={1} />
                          </div>
                        ))}
                      </div>
                    ))}
                  </div>
                )}
              </div>

                  {replyAlert.message && (
                    <div id="reply-alert" className={`alert mt-4 alert-${replyAlert.variant||'secondary'}`} role="alert">{replyAlert.message}</div>
                  )}
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </section>
  )
}
