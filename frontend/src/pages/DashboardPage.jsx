import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useLookups } from '../hooks/useLookups'

const METRIC_LABELS = [
  ['openingBalance', 'Opening balance'],
  ['closingBalance', 'Closing balance'],
  ['netMovement', 'Net movement'],
  ['purchases', 'Purchases'],
  ['transfersIn', 'Transfers in'],
  ['transfersOut', 'Transfers out'],
  ['assigned', 'Assigned'],
  ['expended', 'Expended'],
]

export default function DashboardPage() {
  const { user } = useAuth()
  const { bases, equipmentTypes } = useLookups()
  const isAdmin = user.role === 'ADMIN'

  const [filters, setFilters] = useState({
    baseId: '',
    equipmentTypeId: '',
    fromDate: '',
    toDate: '',
  })
  const [summary, setSummary] = useState(null)
  const [error, setError] = useState(null)
  const [showBreakdown, setShowBreakdown] = useState(false)

  function loadSummary() {
    setError(null)
    const params = {}
    if (isAdmin && filters.baseId) params.baseId = filters.baseId
    if (filters.equipmentTypeId) params.equipmentTypeId = filters.equipmentTypeId
    if (filters.fromDate) params.fromDate = filters.fromDate
    if (filters.toDate) params.toDate = filters.toDate

    apiClient
      .get('/api/dashboard', { params })
      .then((res) => setSummary(res.data))
      .catch(() => setError('Could not load dashboard summary'))
  }

  // reload whenever a filter changes
  useEffect(loadSummary, [filters, isAdmin])

  return (
    <div className="page">
      <h2>Dashboard {!isAdmin && `- ${user.baseName}`}</h2>

      <div className="inline-form">
        {isAdmin && (
          <select
            value={filters.baseId}
            onChange={(e) => setFilters({ ...filters, baseId: e.target.value })}
          >
            <option value="">All bases</option>
            {bases.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        )}

        <select
          value={filters.equipmentTypeId}
          onChange={(e) => setFilters({ ...filters, equipmentTypeId: e.target.value })}
        >
          <option value="">All equipment</option>
          {equipmentTypes.map((eq) => (
            <option key={eq.id} value={eq.id}>
              {eq.name}
            </option>
          ))}
        </select>

        <input
          type="date"
          value={filters.fromDate}
          onChange={(e) => setFilters({ ...filters, fromDate: e.target.value })}
        />
        <input
          type="date"
          value={filters.toDate}
          onChange={(e) => setFilters({ ...filters, toDate: e.target.value })}
        />
      </div>

      {error && <div className="error-text">{error}</div>}

      {summary && (
        <>
          <div className="stat-grid">
            {METRIC_LABELS.map(([key, label]) => (
              <div
                key={key}
                className={`stat-card ${key === 'netMovement' ? 'stat-card-clickable' : ''}`}
                onClick={key === 'netMovement' ? () => setShowBreakdown(true) : undefined}
              >
                <div className="stat-label">{label}</div>
                <div className="stat-value">{summary[key]}</div>
              </div>
            ))}
          </div>

          {showBreakdown && (
            <div className="modal-backdrop" onClick={() => setShowBreakdown(false)}>
              <div className="modal-card" onClick={(e) => e.stopPropagation()}>
                <h3>Net movement breakdown</h3>
                <p>Purchases: +{summary.purchases}</p>
                <p>Transfers in: +{summary.transfersIn}</p>
                <p>Transfers out: -{summary.transfersOut}</p>
                <hr />
                <p>
                  <strong>Net movement: {summary.netMovement}</strong>
                </p>
                <button onClick={() => setShowBreakdown(false)}>Close</button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}
