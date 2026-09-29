import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useLookups } from '../hooks/useLookups'

export default function AssignmentsPage() {
  const { user } = useAuth()
  const { bases, equipmentTypes } = useLookups()
  const isAdmin = user.role === 'ADMIN'

  const [assignments, setAssignments] = useState([])
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [expendingId, setExpendingId] = useState(null)

  const [form, setForm] = useState({
    baseId: isAdmin ? '' : user.baseId,
    equipmentTypeId: '',
    personnelName: '',
    quantity: '',
    assignmentDate: new Date().toISOString().slice(0, 10),
  })

  function loadAssignments() {
    apiClient
      .get('/api/assignments')
      .then((res) => setAssignments(res.data))
      .catch(() => setError('Could not load assignments'))
  }

  useEffect(loadAssignments, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await apiClient.post('/api/assignments', {
        baseId: Number(form.baseId),
        equipmentTypeId: Number(form.equipmentTypeId),
        personnelName: form.personnelName,
        quantity: Number(form.quantity),
        assignmentDate: form.assignmentDate,
      })
      setForm((f) => ({ ...f, equipmentTypeId: '', personnelName: '', quantity: '' }))
      loadAssignments()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to record assignment')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleExpend(id) {
    setError(null)
    setExpendingId(id)
    try {
      await apiClient.patch(`/api/assignments/${id}/expend`)
      loadAssignments()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to mark as expended')
    } finally {
      setExpendingId(null)
    }
  }

  return (
    <div className="page">
      <h2>Assignments / Expenditures</h2>

      <form className="inline-form" onSubmit={handleSubmit}>
        {isAdmin ? (
          <select
            value={form.baseId}
            onChange={(e) => setForm({ ...form, baseId: e.target.value })}
            required
          >
            <option value="">Base</option>
            {bases.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        ) : (
          <span className="locked-field">Base: {user.baseName}</span>
        )}

        <select
          value={form.equipmentTypeId}
          onChange={(e) => setForm({ ...form, equipmentTypeId: e.target.value })}
          required
        >
          <option value="">Equipment type</option>
          {equipmentTypes.map((eq) => (
            <option key={eq.id} value={eq.id}>
              {eq.name}
            </option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Personnel name"
          value={form.personnelName}
          onChange={(e) => setForm({ ...form, personnelName: e.target.value })}
          required
        />

        <input
          type="number"
          min="1"
          placeholder="Quantity"
          value={form.quantity}
          onChange={(e) => setForm({ ...form, quantity: e.target.value })}
          required
        />

        <input
          type="date"
          value={form.assignmentDate}
          onChange={(e) => setForm({ ...form, assignmentDate: e.target.value })}
          required
        />

        <button type="submit" disabled={submitting}>
          {submitting ? 'Saving...' : 'Assign'}
        </button>
      </form>

      {error && <div className="error-text">{error}</div>}

      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Base</th>
            <th>Equipment</th>
            <th>Personnel</th>
            <th className="num">Quantity</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {assignments.map((a) => (
            <tr key={a.id}>
              <td>{a.assignmentDate}</td>
              <td>{a.baseName}</td>
              <td>{a.equipmentTypeName}</td>
              <td>{a.personnelName}</td>
              <td className="num">{a.quantity}</td>
              <td>
                <span className={`status-badge status-${a.status.toLowerCase()}`}>{a.status}</span>
              </td>
              <td>
                {a.status === 'ASSIGNED' && (
                  <button onClick={() => handleExpend(a.id)} disabled={expendingId === a.id}>
                    {expendingId === a.id ? 'Updating...' : 'Mark expended'}
                  </button>
                )}
              </td>
            </tr>
          ))}
          {assignments.length === 0 && (
            <tr>
              <td colSpan={7}>No assignments yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
