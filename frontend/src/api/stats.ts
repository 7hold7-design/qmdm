import { useQuery } from '@tanstack/react-query'
import { apiClient } from './client'
import type { DashboardStats } from '../types'

export const useDashboardStats = () => {
  return useQuery({
    queryKey: ['stats'],
    queryFn: async () => {
      const { data } = await apiClient.get<DashboardStats>('/stats/dashboard')
      return data
    },
    refetchInterval: 60000,
    retry: 2,
  })
}
