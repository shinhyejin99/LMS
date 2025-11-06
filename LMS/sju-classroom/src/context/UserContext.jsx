import React, { createContext, useContext, useMemo, useState } from 'react'

const UserContext = createContext(null)

export function UserProvider({ children }) {
  const [user, setUser] = useState(null)
  const [role, setRole] = useState(null) // 'student' | 'professor'

  const value = useMemo(() => {
    const getUserId = () => {
      if (!user) return null
      return (
        user.userId || user.id || user.username || user.userName || null
      )
    }
    const isCurrentUser = (id) => {
      if (!id) return false
      const mine = getUserId()
      return mine != null && String(mine) === String(id)
    }
    return { user, role, setUser, setRole, getUserId, isCurrentUser }
  }, [user, role])

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>
}

export function useUser() {
  const ctx = useContext(UserContext)
  if (!ctx) throw new Error('useUser must be used within <UserProvider>')
  return ctx
}

