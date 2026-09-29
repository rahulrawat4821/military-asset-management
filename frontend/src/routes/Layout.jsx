import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Shared shell for every authenticated page. Assignments link is hidden for
// LOGISTICS_OFFICER - that role is scoped to purchases/transfers only,
// matching the backend's @PreAuthorize rules (and the route guard in App.jsx
// blocks direct navigation too, not just the link).
export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="sidebar-brand-mark">MAMS</div>
          <div className="sidebar-brand-name">Asset Management</div>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/purchases">Purchases</NavLink>
          <NavLink to="/transfers">Transfers</NavLink>
          {user?.role !== 'LOGISTICS_OFFICER' && (
            <NavLink to="/assignments">Assignments</NavLink>
          )}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <span className="sidebar-user-name">{user?.username}</span>
            <span className="sidebar-user-role">{user?.role}</span>
            {user?.baseName && (
              <span className="sidebar-user-base">{user.baseName}</span>
            )}
          </div>
          <button className="secondary" onClick={logout}>
            Log out
          </button>
        </div>
      </aside>

      <div className="main-area">
        <Outlet />
      </div>
    </div>
  )
}
