import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const PAGE_SIZE = 10

function badgeForType(t) {
  if (t === 'NOTICE') return <span className="badge bg-warning text-dark border border-warning-subtle">공지</span>
  if (t === 'MATERIAL') return <span className="badge bg-info text-dark border border-info-subtle">자료</span>
  return <span className="badge text-bg-secondary">기타</span>
}

const fmtDateTime = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  const pad = (n) => String(n).padStart(2, '0')
  const nowYear = new Date().getFullYear()
  const yyyy = d.getFullYear()
  const mm = pad(d.getMonth() + 1)
  const dd = pad(d.getDate())
  const hh = pad(d.getHours())
  const mi = pad(d.getMinutes())
  const datePart = yyyy === nowYear ? `${mm}-${dd}` : `${yyyy}-${mm}-${dd}`
  return `${datePart} ${hh}:${mi}`
}

export default function StuBoard() {
  const { lectureId } = useParams()
  const [all, setAll] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [tab, setTab] = useState('all') // all | notice | material
  const [typeFilter, setTypeFilter] = useState('') // '' | NOTICE | MATERIAL
  const [q, setQ] = useState('')
  const [page, setPage] = useState(1)

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const url = `/classroom/api/v1/student/board/${encodeURIComponent(lectureId)}/post`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 게시글 목록을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!Array.isArray(data)) throw new Error('서버 응답 형식이 올바르지 않습니다.')
        if (!alive) return
        setAll(data)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId])

  const getType = (p) => p.postType || p.classification || ''
  const counts = useMemo(() => {
    let allCnt = all.length
    let notice = 0, material = 0
    for (const p of all) {
      const t = getType(p)
      if (t === 'NOTICE') notice++
      else if (t === 'MATERIAL') material++
    }
    return { all: allCnt, notice, material }
  }, [all])

  useEffect(() => { // 탭 전환 시 typeFilter 동기화
    if (tab === 'all') setTypeFilter('')
    else if (tab === 'notice') setTypeFilter('NOTICE')
    else if (tab === 'material') setTypeFilter('MATERIAL')
    setPage(1)
  }, [tab])

  const filtered = useMemo(() => {
    let list = all
    if (typeFilter) list = list.filter(p => getType(p) === typeFilter)
    if (q && q.trim().length) {
      const qq = q.trim().toLowerCase()
      list = list.filter(p => String(p.title || '').toLowerCase().includes(qq) || String(p.content || '').toLowerCase().includes(qq))
    }
    return list
  }, [all, typeFilter, q])

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))
  const pageItems = useMemo(() => filtered.slice((page-1)*PAGE_SIZE, page*PAGE_SIZE), [filtered, page])

  useEffect(() => { if (page > totalPages) setPage(1) }, [totalPages, page])

  const renderItem = (p, index) => {
    const postId = p.lctPostId || p.postId || p.id
    const targetId = postId ?? p.id
    if (targetId == null) return null
    const type = getType(p)
    const createdLabel = fmtDateTime(p.createAt || p.createdAt)
    let preview = ''
    for (const candidate of [p.summary, p.content]) {
      if (!candidate) continue
      const normalised = String(candidate).replace(/\s+/g, ' ').trim()
      if (normalised.length) {
        preview = normalised
        break
      }
    }
    const titleText = p.title || '(제목 없음)'
    const href = `/classroom/student/${encodeURIComponent(lectureId)}/board/${encodeURIComponent(String(targetId))}`
    return (
      <Link
        key={String(targetId)}
        to={href}
        className="list-group-item list-group-item-action text-reset text-decoration-none py-2"
      >
        <div className="d-flex align-items-center gap-2 flex-nowrap">
          <div className="d-flex align-items-center gap-1 flex-shrink-0">
            {badgeForType(type)}
          </div>
          <div className="flex-grow-1 text-truncate" style={{ minWidth: 0 }}>
            <span className="fw-semibold">{titleText}</span>
            {preview && <span className="text-muted"> · {preview}</span>}
          </div>
          <span className="text-muted small text-nowrap flex-shrink-0">작성: {createdLabel}</span>
        </div>
      </Link>
    )
  }

  const TabButton = ({ id, label, active, onClick, badge }) => (
    <button className={`nav-link ${active ? 'active' : ''}`} id={`tab-${id}`} type="button" onClick={onClick} role="tab">
      {label} {badge != null && <span className="badge bg-secondary-subtle text-secondary border border-secondary-subtle ms-1">{badge}</span>}
    </button>
  )

  const Pagination = () => (
    <nav className="mt-3" aria-label="게시글 페이지 이동">
      <ul className="pagination pagination-sm mb-0">
        <li className={`page-item ${page<=1 ? 'disabled':''}`}>
          <a className="page-link" href="#" onClick={(e)=>{e.preventDefault(); if(page>1) setPage(page-1)}}>&laquo;</a>
        </li>
        {Array.from({length: totalPages}, (_,i)=>i+1).map(p => (
          <li key={p} className={`page-item ${p===page? 'active':''}`}>
            <a className="page-link" href="#" onClick={(e)=>{e.preventDefault(); setPage(p)}}>{p}</a>
          </li>
        ))}
        <li className={`page-item ${page>=totalPages ? 'disabled':''}`}>
          <a className="page-link" href="#" onClick={(e)=>{e.preventDefault(); if(page<totalPages) setPage(page+1)}}>&raquo;</a>
        </li>
      </ul>
    </nav>
  )

  return (
    <section id="board-root" className="container py-0" data-lecture-id={lectureId}>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">게시글</h1>
        <div className="d-flex align-items-center gap-2">
          <select id="filter-type" className="form-select form-select-sm" style={{minWidth:120}} value={typeFilter} onChange={(e)=>setTypeFilter(e.target.value)}>
            <option value="">전체 항목</option>
            <option value="NOTICE">공지사항</option>
            <option value="MATERIAL">자료</option>
          </select>
          <div className="input-group input-group-sm" style={{minWidth:220}}>
            <input id="search-q" type="text" className="form-control" placeholder="제목/내용 검색" value={q} onChange={(e)=>{setQ(e.target.value); setPage(1)}} />
            <button id="btn-search" className="btn btn-outline-secondary" onClick={()=>setPage(1)}>검색</button>
          </div>
        </div>
      </div>

      <section className="mt-4" aria-labelledby="tablist-h">
        {/* 목록 헤더 텍스트 제거 요청에 따라 생략 */}

        <ul className="nav nav-tabs mt-2" id="post-tabs" role="tablist">
          <li className="nav-item" role="presentation"><TabButton id="all" label="전체" active={tab==='all'} onClick={()=>setTab('all')} badge={counts.all} /></li>
          <li className="nav-item" role="presentation"><TabButton id="notice" label="공지사항" active={tab==='notice'} onClick={()=>setTab('notice')} badge={counts.notice} /></li>
          <li className="nav-item" role="presentation"><TabButton id="material" label="자료" active={tab==='material'} onClick={()=>setTab('material')} badge={counts.material} /></li>
        </ul>
        <div className="tab-content border border-top-0 rounded-bottom p-3">
          {(loading || error) && (
            <div>
              {loading && <div className="text-muted">로딩 중…</div>}
              {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}
            </div>
          )}

          {!loading && !error && (
            <>
              <div id="pane-all" className={`tab-pane fade ${tab==='all'?'show active':''} p-0`} role="tabpanel" aria-labelledby="tab-all">
                <div id="list-all">
                  {typeFilter && tab==='all' ? null : (
                    pageItems.length===0 ? <div className="text-center text-muted py-4">조건에 맞는 게시글이 없습니다.</div> : (
                      <div className="list-group list-group-flush">
                        {pageItems.map((p, idx) => renderItem(p, idx))}
                      </div>
                    )
                  )}
                </div>
                <div className="mt-3 d-flex align-items-center justify-content-between">
                  <Pagination />
                </div>
              </div>

              <div id="pane-notice" className={`tab-pane fade ${tab==='notice'?'show active':''} p-0`} role="tabpanel" aria-labelledby="tab-notice">
                <div id="list-notice">
                  {filtered.filter(p=>getType(p)==='NOTICE').length===0 ? (
                    <div className="text-center text-muted py-4">해당 강의가 없습니다.</div>
                  ) : (
                    <div className="list-group list-group-flush">
                      {filtered.filter(p=>getType(p)==='NOTICE').map((p, idx) => renderItem(p, idx))}
                    </div>
                  )}
                </div>
              </div>

              <div id="pane-material" className={`tab-pane fade ${tab==='material'?'show active':''} p-0`} role="tabpanel" aria-labelledby="tab-material">
                <div id="list-material">
                  {filtered.filter(p=>getType(p)==='MATERIAL').length===0 ? (
                    <div className="text-center text-muted py-4">해당 강의가 없습니다.</div>
                  ) : (
                    <div className="list-group list-group-flush">
                      {filtered.filter(p=>getType(p)==='MATERIAL').map((p, idx) => renderItem(p, idx))}
                    </div>
                  )}
                </div>
              </div>
            </>
          )}
        </div>
        <div id="notfound-box" className="alert alert-danger mt-3 d-none" role="alert"></div>
      </section>
    </section>
  )
}