import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useLookups } from '../hooks/useLookups'

export default function TransfersPage() {
  const { user } = useAuth()
  const { bases, equipmentTypes } = useLookups()
  const isAdmin = user.role === 'ADMIN'

  const [transfers, setTransfers] = useState([])
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  // Non-admins can only move stock in/out of their own base, so fromBaseId
  // is locked to it - they just pick which other base it's going to.
  const [form, setForm] = useState({
    fromBaseId: isAdmin ? '' : user.baseId,
    toBaseId: '',
    equipmentTypeId: '',
    quantity: '',
    transferDate: new Date().toISOString().slice(0, 10),
  })

  function loadTransfers() {
    apiClient
      .get('/api/transfers')
      .then((res) => setTransfers(res.data))
      .catch(() => setError('Could not load transfers'))
  }

  useEffect(loadTransfers, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)

    if (form.fromBaseId && form.toBaseId && String(form.fromBaseId) === String(form.toBaseId)) {
      setError('From and To base cannot be the same')
      return
    }

    setSubmitting(true)
    try {
      await apiClient.post('/api/transfers', {
        fromBaseId: Number(form.fromBaseId),
        toBaseId: Number(form.toBaseId),
        equipmentTypeId: Number(form.equipmentTypeId),
        quantity: Number(form.quantity),
        transferDate: form.transferDate,
      })
      setForm((f) => ({ ...f, toBaseId: '', equipmentTypeId: '', quantity: '' }))
      loadTransfers()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to record transfer')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <h2>Transfers</h2>

      <form className="inline-form" onSubmit={handleSubmit}>
        {isAdmin ? (
          <select
            value={form.fromBaseId}
            onChange={(e) => setForm({ ...form, fromBaseId: e.target.value })}
            required
          >
            <option value="">From base</option>
            {bases.map((b) => (
              <option key={b.id} value={b.id}>
                {b.name}
              </option>
            ))}
          </select>
        ) : (
          <span className="locked-field">From: {user.baseName}</span>
        )}

        <select
          value={form.toBaseId}
          onChange={(e) => setForm({ ...form, toBaseId: e.target.value })}
          required
        >
          <option value="">To base</option>
          {bases.map((b) => (
            <option key={b.id} value={b.id}>
              {b.name}
            </option>
          ))}
        </select>

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
          type="number"
          min="1"
          placeholder="Quantity"
          value={form.quantity}
          onChange={(e) => setForm({ ...form, quantity: e.target.value })}
          required
        />

        <input
          type="date"
          value={form.transferDate}
          onChange={(e) => setForm({ ...form, transferDate: e.target.value })}
          required
        />

        <button type="submit" disabled={submitting}>
          {submitting ? 'Saving...' : 'Record transfer'}
        </button>
      </form>

      {error && <div className="error-text">{error}</div>}

      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>From</th>
            <th>To</th>
            <th>Equipment</th>
            <th className="num">Quantity</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {transfers.map((t) => (
            <tr key={t.id}>
              <td>{t.transferDate}</td>
              <td>{t.fromBaseName}</td>
              <td>{t.toBaseName}</td>
              <td>{t.equipmentTypeName}</td>
              <td className="num">{t.quantity}</td>
              <td>
                <span className={`status-badge status-${t.status.toLowerCase()}`}>{t.status}</span>
              </td>
            </tr>
          ))}
          {transfers.length === 0 && (
            <tr>
              <td colSpan={6}>No transfers yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
