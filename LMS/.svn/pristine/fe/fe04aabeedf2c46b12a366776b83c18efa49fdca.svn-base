import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import './StudentBoardPost.css'

const PAGE_TEXT = {
  breadcrumbBoard: '\uac8c\uc2dc\ud310',
  breadcrumbCurrent: '\uae00',
  listButton: '\ubaa9\ub85d',
  attachments: '\ucc38\ubd80\ud30c\uc77c',
  noContent: '\ub0b4\uc6a9\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.',
  replySectionTitle: '\ub313\uae00',
  replySortNewest: '\ucd5c\uc2e0\uc21c',
  replySortOldest: '\uc624\ub798\ub41c\uc21c',
  replyPlaceholder: '\ub313\uae00\uc744 \uc785\ub825\ud574\uc8fc\uc138\uc694.',
  replySubmit: '\ub4f1\ub85d',
  replySubmitLoading: '\ub4f1\ub85d \uc911...',
  replyEmpty: '\ub313\uae00\uc774 \uc544\uc9c1 \uc5c6\uc2b5\ub2c8\ub2e4. \uccab \ub313\uae00\uc744 \ub0a8\uaca8\ubcf4\uc138\uc694.',
  replyLoading: '\ub313\uae00\uc744 \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  replyAlertEmpty: '\ub313\uae00 \ub0b4\uc6a9\uc744 \uc785\ub825\ud574\uc8fc\uc138\uc694.',
  replyAlertInvalid: '\uc694\uccad \uc815\ubcf4\uac00 \uc62c\ubc14\ub974\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4.',
  replySubmitSuccess: '',
  replySubmitFail: '\ub313\uae00 \ub4f1\ub85d\uc5d0 \uc2e4\ud328\ud558\uc600\uc2b5\ub2c8\ub2e4.',
  replyDelete: '\uc0ad\uc81c',
  replyDeleteConfirm: '\ub313\uae00\uc744 \uc0ad\uc81c\ud558\uc2dc\uaca0\uc2b5\ub2c8\uae4c?',
  replyDeleteSuccess: '\ub313\uae00\uc774 \uc0ad\uc81c\ub418\uc5c8\uc2b5\ub2c8\ub2e4.',
  replyDeleteFail: '\ub313\uae00 \uc0ad\uc81c\uc5d0 \uc2e4\ud328\ud558\uc600\uc2b5\ub2c8\ub2e4.',
  replyNoContent: '\ub0b4\uc6a9 \uc5c6\uc74c',
  invalidParams: '\uc798\ubabb\ub41c \uc811\uadfc\uc785\ub2c8\ub2e4. (lectureId/postId)',
  postLoadError: '\uac8c\uc2dc\uae00\uc744 \ubd88\ub7ec\uc624\ub294 \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.',
  replyLoadError: '\ub313\uae00\uc744 \ubd88\ub7ec\uc624\ub294 \uc911 \uc624\ub958\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.',
}

const META_LABELS = {
  author: '\uc791\uc131\uc790',
  createdAt: '\uc791\uc131\uc77c',
  revealAt: '\uac8c\uc2dc \uc608\uc815',
  viewCount: '\uc870\ud68c\uc218',
}

const pad = (value) => String(value).padStart(2, '0')

const formatDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const decodeIfEscaped = (html) => {
  if (!html) return ''
  if (!/[&<>"']/.test(html) || /<[^>]+>/.test(html)) return html
  const ta = document.createElement('textarea')
  ta.innerHTML = html
  return ta.value
}

const sanitizeBasic = (html) => {
  if (!html) return ''
  try {
    const parser = new DOMParser()
    const doc = parser.parseFromString(html, 'text/html')
    doc.querySelectorAll("script, style, iframe, object, embed, link[rel='import']").forEach((el) => el.remove())
    doc.querySelectorAll('*').forEach((el) => {
      Array.from(el.attributes).forEach((attr) => {
        if (/^on/i.test(attr.name)) el.removeAttribute(attr.name)
      })
    })
    return doc.body.innerHTML
  } catch (err) {
    console.warn('[StudentBoardPost] sanitize failed', err)
    return html
  }
}

const escapeHtml = (value) =>
  String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')

const formatReplyHtml = (text) => {
  const escaped = escapeHtml(text || '')
  if (!escaped) return ''
  return escaped.replace(/\r?\n/g, '<br />')
}

const formatFileSize = (bytes) => {
  if (!Number.isFinite(bytes) || bytes <= 0) return ''
  const KB = 1024
  const MB = KB * 1024
  if (bytes >= MB) {
    const mb = bytes / MB
    return `${mb >= 10 ? Math.round(mb) : Math.round(mb * 10) / 10} mb`
  }
  const kb = bytes / KB
  return `${kb >= 10 ? Math.round(kb) : Math.round(kb * 10) / 10} kb`
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

const TypeBadge = ({ type }) => {
  if (type === 'NOTICE') {
    return <span className="badge badge-type bg-warning text-dark border border-warning-subtle">{'\uacf5\uc9c0'}</span>
  }
  if (type === 'MATERIAL') {
    return <span className="badge badge-type bg-info text-dark border border-info-subtle">{'\uc790\ub8cc'}</span>
  }
  return <span className="badge badge-type text-bg-secondary">{'\uae30\ud0c0'}</span>
}

const StatusBadge = ({ status }) => {
  switch (status) {
    case 'DRAFT':
      return <span className="badge text-bg-secondary">{'\uc784\uc2dc\uc800\uc7a5'}</span>
    case 'SCHEDULED':
      return <span className="badge text-bg-primary">{'\uc608\uc57d'}</span>
    default:
      return <span className="badge text-bg-success">{'\uac8c\uc2dc'}</span>
  }
}

const replyKey = (reply) => String(reply?.lctReplyId || reply?.replyId || reply?.id || '')

const getParentReplyId = (reply) => {
  if (!reply) return ''
  if (reply.parentReplyId) return reply.parentReplyId
  const parent = reply.parentReply || reply.parent
  if (!parent) return ''
  if (typeof parent === 'string') return parent
  if (typeof parent === 'object') {
    return parent.lctReplyId || parent.replyId || parent.id || parent.parentReplyId || ''
  }
  return ''
}

const replyTimestamp = (reply) => {
  if (reply?.createAt || reply?.createdAt) {
    const ts = Date.parse(reply.createAt || reply.createdAt)
    if (!Number.isNaN(ts)) return ts
  }
  const fallback = Number(String(reply?.lctReplyId || reply?.replyId || reply?.id || '').replace(/\D/g, ''))
  return Number.isNaN(fallback) ? 0 : fallback
}

const compareReplies = (a, b) => {
  const diff = replyTimestamp(a) - replyTimestamp(b)
  if (diff !== 0) return diff
  return replyKey(a).localeCompare(replyKey(b))
}

export default function StudentBoardPost() {
  const { lectureId: lectureParam, postId: postParam } = useParams()
  const lectureId = lectureParam || ''
  const postId = postParam || ''
  const encodedLectureId = encodeURIComponent(lectureId)
  const encodedPostId = encodeURIComponent(postId)

  const [post, setPost] = useState(null)
  const [postLoading, setPostLoading] = useState(true)
  const [postError, setPostError] = useState('')

  const [professorInfo, setProfessorInfo] = useState(null)
  const [studentMap, setStudentMap] = useState(() => new Map())

  const [replies, setReplies] = useState([])
  const [replySort, setReplySort] = useState('desc')
  const [repliesLoading, setRepliesLoading] = useState(false)
  const [replyError, setReplyError] = useState('')

  const [replyContent, setReplyContent] = useState('')
  const [replyAlert, setReplyAlert] = useState({ variant: '', message: '' })
  const [submitting, setSubmitting] = useState(false)
  const [deletingReplyId, setDeletingReplyId] = useState('')

  const boardPath = `/classroom/student/${encodeURIComponent(lectureId || '')}/board`

  const clearReplyAlert = useCallback(() => {
    setReplyAlert({ variant: '', message: '' })
  }, [])

  const loadPost = useCallback(async () => {
    if (!lectureId || !postId) throw new Error(PAGE_TEXT.invalidParams)
    const url = `/classroom/api/v1/student/board/${encodedLectureId}/post/${encodedPostId}`
    const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'same-origin' })
    if (!res.ok) throw new Error(`${PAGE_TEXT.postLoadError} (HTTP ${res.status})`)
    return res.json()
  }, [lectureId, postId, encodedLectureId, encodedPostId])

  const loadProfessor = useCallback(async () => {
    if (!lectureId) return null
    const url = `/classroom/api/v1/common/${encodedLectureId}/professor`
    try {
      const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'same-origin' })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      return res.json()
    } catch (err) {
      console.warn('[StudentBoardPost] professor info load failed', err)
      return null
    }
  }, [lectureId, encodedLectureId])

  const loadStudents = useCallback(async () => {
    const map = new Map()
    if (!lectureId) return map
    const url = `/classroom/api/v1/student/${encodedLectureId}/students`
    try {
      const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'same-origin' })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      if (Array.isArray(data)) {
        data.forEach((student) => {
          if (!student) return
          const userId = String(student.userId || '').trim()
          if (!userId) return
          map.set(userId, student)
        })
      }
    } catch (err) {
      console.warn('[StudentBoardPost] student roster load failed', err)
    }
    return map
  }, [lectureId, encodedLectureId])

  const refreshReplies = useCallback(async () => {
    if (!lectureId || !postId) {
      setReplies([])
      setReplyError(PAGE_TEXT.replyLoadError)
      return []
    }
    const url = `/classroom/api/v1/student/board/${encodedLectureId}/post/${encodedPostId}/reply`
    try {
      setRepliesLoading(true)
      setReplyError('')
      const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'same-origin' })
      if (!res.ok) throw new Error(`HTTP ${res.status}`)
      const data = await res.json()
      const arr = Array.isArray(data) ? data : []
      setReplies(arr)
      return arr
    } catch (err) {
      console.error('[StudentBoardPost] reply load error', err)
      setReplies([])
      setReplyError(PAGE_TEXT.replyLoadError)
      return []
    } finally {
      setRepliesLoading(false)
    }
  }, [lectureId, postId, encodedLectureId, encodedPostId])

  useEffect(() => {
    if (!lectureId || !postId) {
      setPost(null)
      setPostError(PAGE_TEXT.invalidParams)
      setPostLoading(false)
      return
    }
    let ignore = false
    ;(async () => {
      try {
        setPostLoading(true)
        setPostError('')
        const data = await loadPost()
        if (!ignore) setPost(data)
      } catch (err) {
        if (!ignore) {
          setPost(null)
          setPostError(err.message || PAGE_TEXT.postLoadError)
        }
      } finally {
        if (!ignore) setPostLoading(false)
      }
    })()
    return () => {
      ignore = true
    }
  }, [lectureId, postId, loadPost])

  useEffect(() => {
    let ignore = false
    ;(async () => {
      const [prof, roster] = await Promise.all([loadProfessor(), loadStudents()])
      if (ignore) return
      setProfessorInfo(prof)
      setStudentMap(new Map(roster))
    })()
    return () => {
      ignore = true
    }
  }, [loadProfessor, loadStudents])

  useEffect(() => {
    let ignore = false
    ;(async () => {
      await refreshReplies()
      if (ignore) return
    })()
    return () => {
      ignore = true
    }
  }, [refreshReplies])

  const attachments = useMemo(() => {
    if (!post) return []
    const list =
      post.attachFileList ||
      post.attachFiles ||
      post.attachments ||
      post.files ||
      []
    if (!Array.isArray(list)) return []
    return list.map((file, index) => {
      const fallbackName = `\ucc38\ubd80\ud30c\uc77c ${file?.fileOrder || index + 1}`
      const baseName =
        file?.originName ||
        file?.originalFilename ||
        file?.originalFileName ||
        file?.fileName ||
        file?.fileNm ||
        fallbackName
      const extension = String(file?.extension || file?.fileExtension || '').replace(/^\./, '')
      let displayName = typeof baseName === 'string' ? baseName : String(baseName ?? fallbackName)
      if (extension && typeof displayName === 'string' && !displayName.toLowerCase().endsWith(`.${extension.toLowerCase()}`)) {
        displayName = `${displayName}.${extension}`
      }
      const order =
        file?.fileOrder ??
        file?.atchFileSn ??
        file?.fileSn ??
        file?.attachFileOrder ??
        file?.fileId ??
        file?.id ??
        index
      const href =
        (typeof file?.downloadUrl === 'string' && file.downloadUrl) ||
        (typeof file?.url === 'string' && file.url) ||
        (typeof file?.fileUrl === 'string' && file.fileUrl) ||
        `/classroom/api/v1/common/board/${encodedLectureId}/post/${encodedPostId}/attach/${encodeURIComponent(String(order))}`
      const sizeLabel = formatFileSize(Number(file?.fileSize ?? file?.size ?? file?.fileLength ?? 0))
      return {
        key: `${order}-${index}`,
        name: displayName,
        href,
        sizeLabel,
      }
    })
  }, [post, encodedLectureId, encodedPostId])

  const sanitizedContent = useMemo(() => {
    if (!post) return ''
    const raw = post.content || post.postContent || post.contentHtml || ''
    const decoded = decodeIfEscaped(raw)
    const sanitized = sanitizeBasic(decoded)
    return sanitized
  }, [post])

  const replyIndex = useMemo(() => {
    const index = new Map()
    replies.forEach((reply) => {
      const id = replyKey(reply)
      if (id) index.set(id, reply)
    })
    return index
  }, [replies])

  const structuredReplies = useMemo(() => {
    if (!replies.length) {
      return { top: [], children: new Map() }
    }
    const children = new Map()
    const top = []
    replies.forEach((reply) => {
      const id = replyKey(reply)
      const parentId = getParentReplyId(reply)
      if (parentId && parentId !== id && replyIndex.has(parentId)) {
        if (!children.has(parentId)) children.set(parentId, [])
        children.get(parentId).push(reply)
      } else {
        top.push(reply)
      }
    })
    const sortAsc = (a, b) => compareReplies(a, b)
    top.sort(sortAsc)
    children.forEach((list, key) => {
      const sorted = list.slice().sort(sortAsc)
      children.set(key, sorted)
    })
    if (replySort === 'desc') top.reverse()
    return { top, children }
  }, [replies, replyIndex, replySort])

  const formatStudentName = useCallback((student) => {
    if (!student) return ''
    const last = student.lastName || ''
    const first = student.firstName || ''
    const fullName = `${last}${first}`.trim()
    if (fullName) return fullName
    return student.userName || student.userId || ''
  }, [])

  const formatProfessorName = useCallback((professor) => {
    if (!professor) return ''
    const last = professor.lastName || ''
    const first = professor.firstName || ''
    const fullName = `${last}${first}`.trim()
    if (fullName) return fullName
    return professor.userName || professor.professorName || professor.userId || ''
  }, [])

  const formatReplyAuthor = useCallback(
    (reply) => {
      if (!reply) return '\ubb34\uba85'
      const userId = String(reply.userId || '').trim()
      if (
        userId &&
        professorInfo?.userId &&
        professorInfo.userId === userId
      ) {
        const profName = formatProfessorName(professorInfo)
        return profName ? `${profName} \uad50\uc218` : '\uac15\uc0ac \uc120\uc0dd'
      }
      if (userId && studentMap instanceof Map && studentMap.has(userId)) {
        const info = studentMap.get(userId)
        const name = formatStudentName(info) || userId
        const dept = info?.univDeptName || ''
        return dept ? `${name}(${dept})` : name
      }
      return reply.writerName || reply.authorName || reply.studentName || userId || '\ubb34\uba85'
    },
    [professorInfo, studentMap, formatProfessorName, formatStudentName]
  )

  const isProfessorReply = useCallback(
    (reply) => {
      const userId = String(reply?.userId || '').trim()
      return Boolean(userId && professorInfo?.userId && professorInfo.userId === userId)
    },
    [professorInfo]
  )

  const handleReplySubmit = useCallback(
    async (event) => {
      event.preventDefault()
      clearReplyAlert()
      const content = replyContent.trim()
      if (!content) {
        setReplyAlert({ variant: 'warning', message: PAGE_TEXT.replyAlertEmpty })
        return
      }
      if (!lectureId || !postId) {
        setReplyAlert({ variant: 'danger', message: PAGE_TEXT.replyAlertInvalid })
        return
      }
      const url = `/classroom/api/v1/student/board/${encodedLectureId}/post/${encodedPostId}/reply`
      try {
        setSubmitting(true)
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
          credentials: 'same-origin',
          body: JSON.stringify({ replyContent: content }),
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        setReplyContent('')
        setReplyAlert({ variant: 'success', message: PAGE_TEXT.replySubmitSuccess })
        await refreshReplies()
      } catch (err) {
        console.error('[StudentBoardPost] reply submit error', err)
        setReplyAlert({ variant: 'danger', message: PAGE_TEXT.replySubmitFail })
      } finally {
        setSubmitting(false)
      }
    },
    [clearReplyAlert, lectureId, postId, encodedLectureId, encodedPostId, replyContent, refreshReplies]
  )

  const handleReplyDelete = useCallback(
    async (replyId) => {
      if (!replyId) return
      if (!lectureId || !postId) {
        setReplyAlert({ variant: 'danger', message: PAGE_TEXT.replyAlertInvalid })
        return
      }
      const confirmed = window.confirm(PAGE_TEXT.replyDeleteConfirm)
      if (!confirmed) return
      const url = `/classroom/api/v1/student/board/${encodedLectureId}/post/${encodedPostId}/reply/${encodeURIComponent(replyId)}`
      try {
        setDeletingReplyId(replyId)
        const res = await fetch(url, {
          method: 'DELETE',
          headers: { Accept: 'application/json' },
          credentials: 'same-origin',
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        setReplyAlert({ variant: 'info', message: PAGE_TEXT.replyDeleteSuccess })
        await refreshReplies()
      } catch (err) {
        console.error('[StudentBoardPost] reply delete error', err)
        setReplyAlert({ variant: 'danger', message: PAGE_TEXT.replyDeleteFail })
      } finally {
        setDeletingReplyId('')
      }
    },
    [lectureId, postId, encodedLectureId, encodedPostId, refreshReplies]
  )

  const renderReplyNode = useCallback(
    (reply, depth = 0, pathKey = 'root') => {
      const id = replyKey(reply) || `${pathKey}-${depth}`
      const professorReply = isProfessorReply(reply)
      const isChild = depth > 0
      const children = structuredReplies.children.get(replyKey(reply)) || []
      const replyHtml = formatReplyHtml(reply?.replyContent || reply?.content || '')

      return (
        <div
          key={id}
          className={`border rounded p-2 reply-item ${isChild ? 'reply-child' : ''} ${professorReply ? 'reply-professor' : ''}`}
          data-reply-id={id}
          style={professorReply ? { backgroundColor: '#f6f7f9' } : undefined}
        >
          <div className="d-flex align-items-start justify-content-between mb-1">
            <div className="me-2">
              <strong className="me-2">{formatReplyAuthor(reply)}</strong>
              <span className="small text-muted">{formatDateTime(reply?.createAt || reply?.createdAt)}</span>
            </div>
            {!isChild && (
              <div className="btn-group btn-group-sm">
                <button
                  className="btn btn-outline-danger"
                  onClick={() => handleReplyDelete(replyKey(reply))}
                  disabled={Boolean(deletingReplyId) && deletingReplyId === replyKey(reply)}
                >
                  {PAGE_TEXT.replyDelete}
                </button>
              </div>
            )}
          </div>
          <div className="reply-body text-start">
            {replyHtml ? (
              <div dangerouslySetInnerHTML={{ __html: replyHtml }} />
            ) : (
              <div style={{ whiteSpace: 'pre-wrap' }}>{PAGE_TEXT.replyNoContent}</div>
            )}
          </div>
          {children.length > 0 && (
            <div className="mt-2 vstack gap-1">
              {children.map((child, childIndex) => renderReplyNode(child, depth + 1, `${id}-${childIndex}`))}
            </div>
          )}
        </div>
      )
    },
    [structuredReplies.children, formatReplyAuthor, handleReplyDelete, deletingReplyId, isProfessorReply]
  )

  const metaItems = useMemo(() => {
    if (!post) return []
    const items = []
    const author = post.writerName || post.professorName || post.authorName
    if (author) {
      items.push(`${META_LABELS.author}: ${author}`)
    }
    const createdAt = formatDateTime(post.createAt || post.createdAt)
    if (createdAt) {
      items.push(`${META_LABELS.createdAt}: ${createdAt}`)
    }
    const revealAt = formatDateTime(post.revealAt)
    if (revealAt) {
      items.push(`${META_LABELS.revealAt}: ${revealAt}`)
    }
    if (post.viewCount != null) {
      items.push(`${META_LABELS.viewCount}: ${post.viewCount}`)
    }
    return items
  }, [post])

  return (
    <section id="board-post-root" className="container py-4" data-lecture-id={lectureId}>
      <div className="board-layout mx-auto" style={{ marginTop: 0, marginBottom: 0 }}>
        <div className="row g-3">
          <div className="col-lg-7">
            <div className="card shadow-sm">
              <div className="card-body" id="post-body">
                {postError ? (
                  <div className="alert alert-danger" role="alert">
                    {postError}
                  </div>
                ) : (
                  <>
                    <div className="d-flex align-items-start justify-content-between gap-3 mb-3 flex-wrap py-1 px-2" id="post-header">
                      <div className="flex-grow-1">
                        <div className="d-flex align-items-center flex-wrap gap-2 mb-2" id="post-badge-row">
                          {postLoading ? (
                            <>
                              <span className="badge skeleton" style={{ width: '72px', height: '24px' }} />
                              <span className="badge skeleton" style={{ width: '56px', height: '24px' }} />
                            </>
                          ) : (
                            <>
                              <TypeBadge type={post?.postType} />
                              <StatusBadge status={computeStatus(post)} />
                            </>
                          )}
                        </div>
                        <h1 className="h4 mb-2" id="post-title">
                          {postLoading ? (
                            <span className="skeleton" style={{ display: 'block', height: '1.5rem' }}>&nbsp;</span>
                          ) : (
                            (post?.title || post?.postTitle || '').trim() || PAGE_TEXT.noContent
                          )}
                        </h1>
                        <div className="d-flex flex-wrap meta-list mb-0" id="post-meta-row">
                          {postLoading
                            ? [1, 2, 3].map((key) => (
                                <div key={key} className="meta-item skeleton" style={{ width: '180px', height: '1rem' }} />
                              ))
                            : metaItems.map((label, index) => (
                                <div key={index} className="meta-item">
                                  {label}
                                </div>
                              ))}
                        </div>
                      </div>
                    </div>
                    <hr className="my-3" />
                    <article className="post-content mb-4 p-1" id="post-content">
                      {postLoading ? (
                        <div className="skeleton" style={{ height: '12rem' }} />
                      ) : sanitizedContent ? (
                        <div dangerouslySetInnerHTML={{ __html: sanitizedContent }} />
                      ) : (
                        <p className="text-muted mb-0">{PAGE_TEXT.noContent}</p>
                      )}
                    </article>
                    {!postLoading && attachments.length > 0 && (
                      <div id="attach-area" className="mt-4">
                        <h2 className="h6 mb-2">{PAGE_TEXT.attachments}</h2>
                        <ul className="list-group" id="attach-list">
                          {attachments.map((file) => (
                            <li key={file.key} className="list-group-item d-flex justify-content-between align-items-center">
                              <span className="text-truncate me-2" title={file.name}>{file.name}</span>
                              <a className="btn btn-sm btn-outline-secondary py-1 px-2" href={file.href} download>다운로드</a>
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
            {!postError && (
              <section id="reply-section" style={{ marginTop: 0 }}>
                <div className="card shadow-sm" style={{ marginBottom: 0 }}>
                  <div className="card-header d-flex align-items-center justify-content-between gap-2 flex-wrap py-2">
                    <h2 className="h5 mb-0">{PAGE_TEXT.replySectionTitle}</h2>
                    <div className="btn-group btn-group-sm" role="group" aria-label="댓글 정렬">
                      <button
                        type="button"
                        className={`btn py-1 px-2 ${replySort === 'desc' ? 'btn-primary' : 'btn-outline-secondary'}`}
                        id="reply-sort-newest"
                        aria-pressed={replySort === 'desc'}
                        onClick={() => setReplySort('desc')}
                      >
                        {PAGE_TEXT.replySortNewest}
                      </button>
                      <button
                        type="button"
                        className={`btn py-1 px-2 ${replySort === 'asc' ? 'btn-primary' : 'btn-outline-secondary'}`}
                        id="reply-sort-oldest"
                        aria-pressed={replySort === 'asc'}
                        onClick={() => setReplySort('asc')}
                      >
                        {PAGE_TEXT.replySortOldest}
                      </button>
                    </div>
                  </div>
                  <div className="card-body pb-2 p-2">
                    <form id="reply-form" className="vstack gap-2 mb-3" onSubmit={handleReplySubmit}>
                      <div>
                        <label htmlFor="reply-content" className="form-label">댓글 작성</label>
                        <textarea
                          className="form-control reply-textarea"
                          id="reply-content"
                          rows={2}
                          placeholder={PAGE_TEXT.replyPlaceholder}
                          value={replyContent}
                          onChange={(e) => setReplyContent(e.target.value)}
                          style={{ resize: 'vertical', padding: '4px' }}
                          disabled={submitting}
                        />
                      </div>
                      <div className="d-flex justify-content-end">
                        <button type="submit" className="btn btn-primary btn-sm py-1 px-2" id="reply-submit" disabled={submitting}>
                          {submitting ? PAGE_TEXT.replySubmitLoading : PAGE_TEXT.replySubmit}
                        </button>
                      </div>
                    </form>

                    <hr className="my-2" />
                    <div className="vstack gap-1">
                      {replyAlert.message && (
                        <div className={`alert alert-${replyAlert.variant} py-2 small`} role="alert">
                          {replyAlert.message}
                        </div>
                      )}
                      {repliesLoading ? (
                        <div className="text-center py-3 text-muted">{PAGE_TEXT.replyLoading}</div>
                      ) : structuredReplies.top.length === 0 ? (
                        <div className="text-center text-muted py-3 small">{replyError || PAGE_TEXT.replyEmpty}</div>
                      ) : (
                        structuredReplies.top.map((reply) => renderReplyNode(reply))
                      )}
                    </div>
                  </div>
                </div>
              </section>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}
