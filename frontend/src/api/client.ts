import axios from 'axios'

// В production API доступен через тот же домен через прокси nginx
const API_URL = ''

export const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
  withCredentials: false, // Не отправляем куки
})

// Добавляем токен из localStorage
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  console.log(`������ ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`, config.data)
  return config
})

// Обработка ответов
apiClient.interceptors.response.use(
  (response) => {
    console.log(`✅ ${response.status} ${response.config.url}`)
    return response
  },
  (error) => {
    if (error.code === 'ERR_NETWORK') {
      console.error('❌ Network Error - backend недоступен')
    } else if (error.response?.status === 401) {
      console.error('❌ 401 Unauthorized - redirect to login')
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else {
      console.error(`❌ ${error.response?.status || 'Error'} ${error.config?.url}`, error.response?.data)
    }
    return Promise.reject(error)
  }
)
