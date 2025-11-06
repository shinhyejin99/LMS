import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const STATUS_TABS = [

  { key: 'all', label: '전체' },

  { key: 'ongoing', label: '진행중' },

  { key: 'closed', label: '마감' },

]



const PAGE_SIZE = 10



const initialBuckets = () => ({

  all: [],

  ongoing: [],

  closed: [],

})



const initialPages = () => ({

  all: 1,

  ongoing: 1,

  closed: 1,

})



const pad = (n) => String(n).padStart(2, '0')







const fmtDate = (iso) => {



  if (!iso) return '-'



  const d = new Date(iso)



  if (Number.isNaN(d.getTime())) return '-'



  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`



}







const fmtDateTime = (iso) => {







  if (!iso) return '-'







  const d = new Date(iso)







  if (Number.isNaN(d.getTime())) return '-'







  let hours = d.getHours()







  const ampm = hours >= 12 ? 'PM' : 'AM'







  hours = hours % 12







  hours = hours ? hours : 12 // the hour '0' should be '12'







  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${ampm}${pad(hours)}:${pad(d.getMinutes())}`







}























const classifyStatus = (task) => {







  const now = Date.now()







  const start = task.startAt ? new Date(task.startAt).getTime() : null







  const end = task.endAt ? new Date(task.endAt).getTime() : null







  if (start && start > now) return 'upcoming'







  if (end && end < now) return 'closed'







  return 'ongoing'







}























const normalizeIndiv = (raw) => ({







  type: 'indiv',







  id: raw.indivtaskId,







  name: raw.indivtaskName,







  createAt: raw.createAt,







  startAt: raw.startAt,







  endAt: raw.endAt,







  hasAttachFile: !!raw.hasAttachFile,







  submitted: !!raw.submitted,







})























const normalizeGroup = (raw) => ({







  type: 'group',







  id: raw.grouptaskId,







  name: raw.grouptaskName,







  createAt: raw.createAt,







  startAt: raw.startAt,







  endAt: raw.endAt,







  hasAttachFile: !!raw.hasAttachFile,







  submitted: !!raw.submitted,







  target: !!raw.target,







})























const useTaskBuckets = (lectureId) => {







  const [loading, setLoading] = useState(true)







  const [error, setError] = useState('')







  const [indivBuckets, setIndivBuckets] = useState(initialBuckets)







  const [groupBuckets, setGroupBuckets] = useState(initialBuckets)















  useEffect(() => {







    let alive = true







    const fetchTasks = async () => {







      if (!lectureId) {







        setError('강의 정보를 확인할 수 없습니다.')







        setLoading(false)







        return







      }







      try {







        setLoading(true)







        setError('')







        const options = { headers: { Accept: 'application/json' }, credentials: 'include' }







        const base = `/classroom/api/v1/student/${encodeURIComponent(lectureId)}/task`







        const [indivRes, groupRes] = await Promise.all([







          fetch(`${base}/indiv`, options),







          fetch(`${base}/group`, options),







        ])















        if (!alive) return















        if (!indivRes.ok) throw new Error(`(${indivRes.status}) 개인 과제를 불러오지 못했습니다.`)







        if (!groupRes.ok) throw new Error(`(${groupRes.status}) 조별 과제를 불러오지 못했습니다.`)















        const indivJson = await indivRes.json().catch(() => [])







        const groupJson = await groupRes.json().catch(() => [])















        if (!alive) return















        const normalizeAndBucket = (data, normalizer) => {







          const list = (Array.isArray(data) ? data : []).map(normalizer)







          list.sort((a, b) => {







            const aTime = a.createAt ? new Date(a.createAt).getTime() : 0







            const bTime = b.createAt ? new Date(b.createAt).getTime() : 0







            return bTime - aTime







          })







          const buckets = initialBuckets()







          for (const item of list) {







            const status = classifyStatus(item)







            buckets.all.push(item)







            if (buckets[status]) buckets[status].push(item)







          }







          return buckets







        }















        setIndivBuckets(() => normalizeAndBucket(indivJson, normalizeIndiv))







        setGroupBuckets(() => normalizeAndBucket(groupJson, normalizeGroup))







      } catch (err) {







        if (!alive) return







        setError(err instanceof Error ? err.message : '과제를 불러오는 중 문제가 발생했습니다.')







        setIndivBuckets(initialBuckets)







        setGroupBuckets(initialBuckets)







      } finally {







        if (alive) setLoading(false)







      }







    }















    fetchTasks()















    return () => { alive = false }







  }, [lectureId])















  return { loading, error, indivBuckets, groupBuckets }







}























const Pagination = ({ totalCount, pageSize, currentPage, onChange, label }) => {







  const totalPages = Math.max(1, Math.ceil(totalCount / pageSize))







  if (totalPages <= 1) return null















  const items = []







  const pushItem = (page, text, disabled = false, active = false, key) => {







    items.push(







      <li key={key ?? text} className={`page-item${disabled ? ' disabled' : ''}${active ? ' active' : ''}`}>







        <button







          type="button"







          className="page-link"







          onClick={() => { if (!disabled) onChange(page) }}







        >







          {text}







        </button>







      </li>,







    )







  }















  pushItem(Math.max(1, currentPage - 1), '‹', currentPage === 1, false, 'prev')







  for (let page = 1; page <= totalPages; page += 1) {







    pushItem(page, String(page), false, page === currentPage)







  }







  pushItem(Math.min(totalPages, currentPage + 1), '›', currentPage === totalPages, false, 'next')















  return (







    <nav className="mt-3" aria-label={label}>







      <ul className="pagination pagination-sm mb-0">







        {items}







      </ul>







    </nav>







  )







}























const TaskTable = ({ lectureId, tasks, loading }) => {







  if (loading) {







    return <div className="text-muted p-3 text-center">불러오는 중입니다...</div>







  }







  if (!tasks || tasks.length === 0) {







    return <div className="text-muted p-3 text-center">표시할 과제가 없습니다.</div>







  }















  return (







    <div className="table-responsive">







      <table className="table table-hover table-sm align-middle mb-0">







        <thead>







          <tr className="table-light">







            <th>과제명</th>







            <th className="text-center" style={{ width: '100px' }}>제출 상태</th>







            <th className="text-center d-none d-md-table-cell" style={{ width: '120px' }}>과제 기간</th>







          </tr>







        </thead>







        <tbody>







          {tasks.map((task) => {







            const href = `/classroom/student/${encodeURIComponent(lectureId || '')}/task/${task.type}/${encodeURIComponent(task.id)}`







            return (







              <tr key={`${task.type}-${task.id}`}>







                                                <td className="align-middle" style={{ width: '250px', overflow: 'hidden', textOverflow: 'ellipsis' }}>







                                                  <Link to={href} className="text-decoration-none d-block">







                                                    <div className="fw-bold text-primary">{task.name || '-'}</div>







                                                  </Link>







                                                </td>







                                <td className="align-middle text-center" style={{ width: '100px' }}>







                







                                                                      {task.type === 'group' && task.target === false ? (







                







                                                    







                







                                                                        <span className="badge bg-secondary">면제</span>







                







                                                    







                







                                                                      ) : (







                







                                                    







                







                                                                        <span className={`badge ${task.submitted ? '' : 'bg-secondary'}`} style={task.submitted ? { backgroundColor: '#198754', color: 'white' } : {}}>







                







                                                    







                







                                                                          {task.submitted ? '제출 완료' : '미제출'}







                







                                                    







                







                                                                        </span>







                







                                                    







                







                                                                      )}







                







                                </td>







                







                                <td className="align-middle text-center d-none d-md-table-cell text-nowrap small" style={{ width: '120px' }}>







                







                                  <div>{fmtDate(task.startAt)}</div>
                                  <div>~ {fmtDate(task.endAt)}</div>







                







                                </td>







              </tr>







            )







          })}







        </tbody>







      </table>







    </div>







  )







}















const TaskStatusTabs = ({ scope, activeKey, onChange, counts }) => (















  <ul className="nav nav-pills mb-3" id={`${scope}-status-tabs`} role="tablist">















    {STATUS_TABS.map((tab) => {















      const badgeColor = tab.key === 'all' ? 'bg-primary' : tab.key === 'ongoing' ? 'bg-success' : tab.key === 'closed' ? 'bg-danger' : 'bg-secondary';















      return (















        <li className="nav-item" role="presentation" key={tab.key}>















          <button















            className={`nav-link${activeKey === tab.key ? ' active' : ''}`}















            id={`${scope}-${tab.key}-tab`}















            type="button"















            role="tab"















            aria-selected={activeKey === tab.key}















            onClick={() => onChange(tab.key)}















          >















            {tab.label}















                                    <span className={`badge rounded-pill ${badgeColor} ms-1 ${tab.key === 'ongoing' ? 'text-white' : ''}`} id={`${scope}-count-${tab.key}`} style={{ minWidth: '30px' }}>















                                      {counts[tab.key] ?? 0}















                                    </span>















          </button>















        </li>















      );















    })}















  </ul>















)



export default function StudentTask() {

  const { lectureId } = useParams()

  const { loading, error, indivBuckets, groupBuckets } = useTaskBuckets(lectureId)

  const [indivTab, setIndivTab] = useState('ongoing')

  const [groupTab, setGroupTab] = useState('ongoing')

  const [indivPages, setIndivPages] = useState(initialPages)

  const [groupPages, setGroupPages] = useState(initialPages)



  useEffect(() => {
    setIndivPages(initialPages());
    setGroupPages(initialPages());
    setIndivTab(indivBuckets.ongoing.length > 0 ? 'ongoing' : 'all');
    setGroupTab(groupBuckets.ongoing.length > 0 ? 'ongoing' : 'all');
  }, [lectureId, indivBuckets.ongoing.length, groupBuckets.ongoing.length]);



  const indivCounts = useMemo(() => STATUS_TABS.reduce((acc, tab) => {

    acc[tab.key] = indivBuckets[tab.key]?.length ?? 0

    return acc

  }, {}), [indivBuckets])



  const groupCounts = useMemo(() => STATUS_TABS.reduce((acc, tab) => {

    acc[tab.key] = groupBuckets[tab.key]?.length ?? 0

    return acc

  }, {}), [groupBuckets])



  const indivPageData = useMemo(() => {

    const list = indivBuckets[indivTab] ?? []

    const page = indivPages[indivTab] ?? 1

    const start = (page - 1) * PAGE_SIZE

    return list.slice(start, start + PAGE_SIZE)

  }, [indivBuckets, indivTab, indivPages])



  const groupPageData = useMemo(() => {

    const list = groupBuckets[groupTab] ?? []

    const page = groupPages[groupTab] ?? 1

    const start = (page - 1) * PAGE_SIZE

    return list.slice(start, start + PAGE_SIZE)

  }, [groupBuckets, groupTab, groupPages])



  const handleIndivTabChange = (nextTab) => {

    setIndivTab(nextTab)

    setIndivPages((prev) => ({ ...prev, [nextTab]: 1 }))

  }



  const handleGroupTabChange = (nextTab) => {

    setGroupTab(nextTab)

    setGroupPages((prev) => ({ ...prev, [nextTab]: 1 }))

  }



      return (

        <section

          id="task-root"

          className="container py-4"

          data-lecture-id={lectureId || ''}

          data-api={`/classroom/api/v1/student/${encodeURIComponent(lectureId || '')}/task`}

        >

    

            <div className="d-flex align-items-center justify-content-between mb-3">

              <h1 className="h3 mb-0">과제</h1>

              <p className="text-muted small mb-0">진행 중인 과제 상태를 확인하고 제출 여부를 점검하세요.</p>

            </div>

    

            <div className="row g-4 align-items-start">

              <div className="col-12 col-xl-6">

                <div className="card shadow-sm">

                  <div className="card-header d-flex align-items-center justify-content-between">

                    <h2 className="h5 mb-0">개인 과제</h2>

                  </div>

                  <div className="card-body">

                    <TaskStatusTabs

                      scope="indiv"

                      activeKey={indivTab}

                      onChange={handleIndivTabChange}

                      counts={indivCounts}

                    />

                    <div className="tab-content" id="indiv-status-content">

                      {STATUS_TABS.map((tab) => {

                        const isActive = indivTab === tab.key

                        const tabId = `indiv-${tab.key}`

                        const page = indivPages[tab.key] ?? 1

                        return (

                          <div

                            key={tab.key}

                            id={tabId}

                            role="tabpanel"

                            aria-labelledby={`${tabId}-tab`}

                            className={`tab-pane fade${isActive ? ' show active' : ''}`}

                          >

                            {isActive ? (

                              <>

                                <TaskTable lectureId={lectureId} tasks={indivPageData} loading={loading} />

                                <Pagination

                                  totalCount={indivCounts[tab.key] || 0}

                                  pageSize={PAGE_SIZE}

                                  currentPage={page}

                                  onChange={(next) => setIndivPages((prev) => ({ ...prev, [tab.key]: next }))}

                                  label="개인 과제 목록 페이지 이동"

                                />

                              </>

                            ) : null}

                          </div>

                        )

                      })}

                    </div>

                  </div>

                </div>

              </div>

    

              <div className="col-12 col-xl-6">

                <div className="card shadow-sm">

                  <div className="card-header d-flex align-items-center justify-content-between">

                    <h2 className="h5 mb-0">조별 과제</h2>

                  </div>

                  <div className="card-body">

                    <TaskStatusTabs

                      scope="group"

                      activeKey={groupTab}

                      onChange={handleGroupTabChange}

                      counts={groupCounts}

                    />

                    <div className="tab-content" id="group-status-content">

                      {STATUS_TABS.map((tab) => {

                        const isActive = groupTab === tab.key

                        const tabId = `group-${tab.key}`

                        const page = groupPages[tab.key] ?? 1

                        return (

                          <div

                            key={tab.key}

                            id={tabId}

                            role="tabpanel"

                            aria-labelledby={`${tabId}-tab`}

                            className={`tab-pane fade${isActive ? ' show active' : ''}`}

                          >

                            {isActive ? (

                              <>

                                <TaskTable lectureId={lectureId} tasks={groupPageData} loading={loading} />

                                <Pagination

                                  totalCount={groupCounts[tab.key] || 0}

                                  pageSize={PAGE_SIZE}

                                  currentPage={page}

                                  onChange={(next) => setGroupPages((prev) => ({ ...prev, [tab.key]: next }))}

                                  label="조별 과제 목록 페이지 이동"

                                />

                              </>

                            ) : null}

                          </div>

                        )

                      })}

                    </div>

                  </div>

                </div>

              </div>

            </div>

    

            {error && (

              <div id="task-notice-box" className="alert alert-danger mt-3" role="alert">

                {error}

              </div>

            )}

        </section>

      )

    }
