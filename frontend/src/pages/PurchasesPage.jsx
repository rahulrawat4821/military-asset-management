import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useLookups } from '../hooks/useLookups'

export default function PurchasesPage() {
  const { user } = useAuth()
  const { bases, equipmentTypes } = useLookups()
  const isAdmin = user.role === 'ADMIN'

  const [purchases, setPurchases] = useState([])
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const [form, setForm] = useState({
    baseId: isAdmin ? '' : user.baseId,
    equipmentTypeId: '',
    quantity: '',
    purchaseDate: new Date().toISOString().slice(0, 10),
  })

  function loadPurchases() {
    apiClient
      .get('/api/purchases')
      .then((res) => setPurchases(res.data))
      .catch(() => setError('Could not load purchases'))
  }

  useEffect(loadPurchases, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await apiClient.post('/api/purchases', {
        baseId: Number(form.baseId),
        equipmentTypeId: Number(form.equipmentTypeId),
        quantity: Number(form.quantity),
        purchaseDate: form.purchaseDate,
      })
      setForm((f) => ({ ...f, equipmentTypeId: '', quantity: '' }))
      loadPurchases()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to record purchase')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <h2>Purchases</h2>

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
          type="number"
          min="1"
          placeholder="Quantity"
          value={form.quantity}
          onChange={(e) => setForm({ ...form, quantity: e.target.value })}
          required
        />

        <input
          type="date"
          value={form.purchaseDate}
          onChange={(e) => setForm({ ...form, purchaseDate: e.target.value })}
          required
        />

        <button type="submit" disabled={submitting}>
          {submitting ? 'Saving...' : 'Record purchase'}
        </button>
      </form>

      {error && <div className="error-text">{error}</div>}

      <table>
        <thead>
          <tr>
            <th>Date</th>
            <th>Base</th>
            <th>Equipment</th>
            <th className="num">Quantity</th>
            <th>Recorded by</th>
          </tr>
        </thead>
        <tbody>
          {purchases.map((p) => (
            <tr key={p.id}>
              <td>{p.purchaseDate}</td>
              <td>{p.baseName}</td>
              <td>{p.equipmentTypeName}</td>
              <td className="num">{p.quantity}</td>
              <td>{p.createdBy || '-'}</td>
            </tr>
          ))}
          {purchases.length === 0 && (
            <tr>
              <td colSpan={5}>No purchases yet.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
