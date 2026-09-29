import { useEffect, useState } from 'react'
import { apiClient } from '../api/client'

// Shared by Purchases/Transfers/Assignments/Dashboard - all of them need
// the same two dropdown sources (bases, equipment types).
export function useLookups() {
  const [bases, setBases] = useState([])
  const [equipmentTypes, setEquipmentTypes] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([apiClient.get('/api/bases'), apiClient.get('/api/equipment-types')])
      .then(([basesRes, equipmentRes]) => {
        setBases(basesRes.data)
        setEquipmentTypes(equipmentRes.data)
      })
      .finally(() => setLoading(false))
  }, [])

  return { bases, equipmentTypes, loading }
}
