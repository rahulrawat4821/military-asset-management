import axios from 'axios'

// In dev, VITE_API_BASE_URL is left empty and Vite's proxy (vite.config.js)
// forwards /api/* to the Spring Boot backend. In production, set
// VITE_API_BASE_URL to the deployed backend's URL.
const baseURL = import.meta.env.VITE_API_BASE_URL || ''

export const apiClient = axios.create({
  baseURL,
})

// Attach the JWT (if we have one) to every outgoing request.
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('mams_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// If the backend says the token is invalid/expired, drop it and send the
// user back to login rather than showing a broken page.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem('mams_token')
      localStorage.removeItem('mams_user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)
