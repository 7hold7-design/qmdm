import React, { createContext, useContext, useState, useEffect } from 'react'
import { apiClient } from '../api/client'
import toast from 'react-hot-toast'

interface User {
  username: string
  email: string
  roles: string[]
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (username: string, password: string) => Promise<void>
  logout: () => void
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'))
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (token) {
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }
    setIsLoading(false)
  }, [token])

  const login = async (username: string, password: string) => {
    try {
      console.log('Login attempt:', { username })
      
      // ВАЖНО: путь должен быть /api/auth/login, а не /auth/login
      const response = await apiClient.post('/api/auth/login', { username, password })
      
      console.log('Login response:', response.data)
      
      const { token, username: userName, email, roles } = response.data
      
      localStorage.setItem('token', token)
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
      
      setToken(token)
      setUser({ username: userName, email, roles })
      
      toast.success('Вход выполнен успешно')
    } catch (error: any) {
      console.error('Login error:', error.response?.status, error.response?.data)
      toast.error(error.response?.data?.message || 'Ошибка входа')
      throw error
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    delete apiClient.defaults.headers.common['Authorization']
    setToken(null)
    setUser(null)
    toast.success('Выход выполнен')
  }

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  )
}
