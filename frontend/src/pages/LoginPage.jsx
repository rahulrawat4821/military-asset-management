import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Convenience only - these map to the seeded demo accounts from DataSeeder.
// Picking a role just fills the fields below; the actual role comes from
// the backend after login, not from this dropdown.
const DEMO_ACCOUNTS = {
  ADMIN: { username: 'admin', password: 'admin123' },
  BASE_COMMANDER: { username: 'commander1', password: 'commander123' },
  LOGISTICS_OFFICER: { username: 'logistics1', password: 'logistics123' },
}

export default function LoginPage() {
  const [role, setRole] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const { login, loading, error } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const from = location.state?.from?.pathname || '/dashboard'

  function handleRoleChange(e) {
    const value = e.target.value
    setRole(value)
    if (DEMO_ACCOUNTS[value]) {
      setUsername(DEMO_ACCOUNTS[value].username)
      setPassword(DEMO_ACCOUNTS[value].password)
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    try {
      await login(username, password)
      navigate(from, { replace: true })
    } catch {
      // error is already surfaced via the auth context's `error` state
    }
  }

  return (
    <div className="login-shell">
      <div className="login-card">
        <div className="login-mark">MAMS</div>
        <h2>Sign in</h2>
        <p className="login-sub">Track assets, transfers and assignments across your bases.</p>

        <form onSubmit={handleSubmit}>
          {error && <div className="error-text">{error}</div>}

          <label className="login-field-label" htmlFor="role">
            Sign in as
          </label>
          <select id="role" value={role} onChange={handleRoleChange}>
            <option value="">Choose a role...</option>
            <option value="ADMIN">Admin</option>
            <option value="BASE_COMMANDER">Base Commander</option>
            <option value="LOGISTICS_OFFICER">Logistics Officer</option>
          </select>

          <label className="login-field-label" htmlFor="username">
            Username
          </label>
          <input
            id="username"
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />

          <label className="login-field-label" htmlFor="password">
            Password
          </label>
          <input
            id="password"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  )
}
