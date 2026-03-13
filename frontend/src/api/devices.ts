import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'
import type { Device, Command, CommandType } from '../types'

export const useDevices = () => {
  return useQuery({
    queryKey: ['devices'],
    queryFn: async () => {
      const { data } = await apiClient.get<Device[]>('/devices')
      return data
    },
    refetchInterval: 30000,
  })
}

export const useDevice = (id: string) => {
  return useQuery({
    queryKey: ['devices', id],
    queryFn: async () => {
      const { data } = await apiClient.get<Device>(`/api/devices/${id}`)
      return data
    },
    enabled: !!id,
  })
}

export const useDeviceCommands = (deviceId: string) => {
  return useQuery({
    queryKey: ['devices', deviceId, 'commands'],
    queryFn: async () => {
      const { data } = await apiClient.get<Command[]>(`/api/devices/${deviceId}/commands`)
      return data
    },
    enabled: !!deviceId,
    refetchInterval: 10000,
  })
}

interface SendCommandParams {
  deviceId: string
  command: {
    type: CommandType
    parameters?: Record<string, any>
  }
}

export const useSendCommand = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: async ({ deviceId, command }: SendCommandParams) => {
      const { data } = await apiClient.post(`/api/devices/${deviceId}/commands`, command)
      return data
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['devices', variables.deviceId, 'commands'] })
    },
  })
}
