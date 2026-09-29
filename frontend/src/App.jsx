import { Routes, Route, Navigate } from 'react-router-dom'
import ProtectedRoute from './routes/ProtectedRoute'
import Layout from './routes/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import PurchasesPage from './pages/PurchasesPage'
import TransfersPage from './pages/TransfersPage'
import AssignmentsPage from './pages/AssignmentsPage'
import UnauthorizedPage from './pages/UnauthorizedPage'
import NotFoundPage from './pages/NotFoundPage'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      {/* Everything below requires a valid session */}
      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/purchases" element={<PurchasesPage />} />
          <Route path="/transfers" element={<TransfersPage />} />

          {/* Logistics Officer is deliberately excluded here, mirroring the
              backend's AssignmentController @PreAuthorize rule */}
          <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'BASE_COMMANDER']} />}>
            <Route path="/assignments" element={<AssignmentsPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
