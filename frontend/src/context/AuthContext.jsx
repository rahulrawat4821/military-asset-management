import { createContext, useContext, useState, useCallback } from 'react'
import { apiClient } from '../api/client'

const AuthContext = createContext(null)

function loadStoredUser() {
  const raw = localStorage.getItem('mams_user')
  return raw ? JSON.parse(raw) : null
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const login = useCallback(async (username, password) => {
    setLoading(true)
    setError(null)
    try {
      const response = await apiClient.post('/api/auth/login', { username, password })
      const { token, username: uname, role, baseId, baseName } = response.data

      const loggedInUser = { username: uname, role, baseId, baseName }
      localStorage.setItem('mams_token', token)
      localStorage.setItem('mams_user', JSON.stringify(loggedInUser))
      setUser(loggedInUser)
      return loggedInUser
    } catch (err) {
      const message = err.response?.data?.message || 'Invalid username or password'
      setError(message)
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('mams_token')
    localStorage.removeItem('mams_user')
    setUser(null)
  }, [])

  const value = { user, login, logout, loading, error, isAuthenticated: !!user }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return ctx
}
