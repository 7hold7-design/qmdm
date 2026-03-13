import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useDevices } from '../api/devices'
import { formatDistanceToNow } from 'date-fns'
import { ru } from 'date-fns/locale'
import { Smartphone, MoreVertical, CheckCircle, AlertCircle, PauseCircle, XCircle } from 'lucide-react'
import type { Device } from '../types'

const statusConfig: Record<string, { icon: any; color: string; bg: string; label: string }> = {
  ACTIVE: { icon: CheckCircle, color: 'text-green-500', bg: 'bg-green-100', label: 'Активно' },
  PENDING: { icon: AlertCircle, color: 'text-yellow-500', bg: 'bg-yellow-100', label: 'Ожидает' },
  SUSPENDED: { icon: PauseCircle, color: 'text-orange-500', bg: 'bg-orange-100', label: 'Приостановлено' },
  RETIRED: { icon: XCircle, color: 'text-gray-500', bg: 'bg-gray-100', label: 'Списано' },
}

export default function DevicesTable() {
  const navigate = useNavigate()
  const { data: devices, isLoading, error } = useDevices()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Ошибка загрузки устройств</p>
      </div>
    )
  }

  return (
    <div className="bg-white shadow-sm rounded-lg overflow-hidden">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Устройство
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Модель
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Статус
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Последняя активность
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Действия
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {devices?.map((device: Device) => {
            const config = statusConfig[device.status] || statusConfig.PENDING
            const StatusIcon = config.icon
            return (
              <tr 
                key={device.id} 
                className="hover:bg-gray-50 cursor-pointer"
                onClick={() => navigate(`/devices/${device.id}`)}
              >
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="flex items-center">
                    <Smartphone className="h-5 w-5 text-gray-400 mr-3" />
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        {device.name || device.deviceId}
                      </div>
                      <div className="text-sm text-gray-500">
                        {device.deviceId}
                      </div>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="text-sm text-gray-900">{device.model}</div>
                  <div className="text-sm text-gray-500">{device.manufacturer}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${config.bg} ${config.color}`}>
                    <StatusIcon className="h-4 w-4 mr-1" />
                    {config.label}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {formatDistanceToNow(new Date(device.lastSeen), { addSuffix: true, locale: ru })}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  <button className="text-gray-400 hover:text-gray-600">
                    <MoreVertical className="h-5 w-5" />
                  </button>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
