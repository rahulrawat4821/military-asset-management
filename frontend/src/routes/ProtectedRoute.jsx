import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Wraps a set of routes: redirects to /login if not authenticated, and
// optionally restricts to a list of allowed roles (e.g. ['ADMIN']).
// Usage:
//   <Route element={<ProtectedRoute />}>...</Route>
//   <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>...</Route>
export default function ProtectedRoute({ allowedRoles }) {
  const { isAuthenticated, user } = useAuth()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />
  }

  return <Outlet />
}
