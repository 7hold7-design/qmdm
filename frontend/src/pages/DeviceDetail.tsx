import React from 'react'
import { useParams } from 'react-router-dom'
import { useDevice, useDeviceCommands, useSendCommand } from '../api/devices'
import type { CommandType } from '../types'
import { 
  Smartphone, 
  Battery, 
  Wifi, 
  Bluetooth, 
  MapPin,
  HardDrive,
  Cpu,
  Clock,
  ArrowLeft,
  RefreshCw,
  Power,
  Lock,
  Download
} from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'
import { ru } from 'date-fns/locale'
import toast from 'react-hot-toast'

export default function DeviceDetail() {
  const { id } = useParams<{ id: string }>()
  const { data: device, isLoading, refetch } = useDevice(id!)
  const { data: commands } = useDeviceCommands(id!)
  const sendCommand = useSendCommand()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (!device) {
    return (
      <div className="text-center py-12">
        <h2 className="text-2xl font-semibold text-gray-900">Устройство не найдено</h2>
        <p className="text-gray-600 mt-2">Устройство с ID {id} не существует</p>
      </div>
    )
  }

  const handleCommand = async (type: CommandType) => {
    try {
      await sendCommand.mutateAsync({
        deviceId: device.deviceId,
        command: { type, parameters: {} }
      })
      toast.success('Команда отправлена')
    } catch (error) {
      toast.error('Ошибка при отправке команды')
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center space-x-4">
        <button 
          onClick={() => window.history.back()}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
        >
          <ArrowLeft className="h-5 w-5 text-gray-600" />
        </button>
        <h1 className="text-2xl font-semibold text-gray-900">{device.name || device.deviceId}</h1>
        <button 
          onClick={() => refetch()}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors ml-auto"
        >
          <RefreshCw className="h-5 w-5 text-gray-600" />
        </button>
      </div>

      {/* Device Info Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Smartphone className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Модель</p>
              <p className="font-medium text-gray-900">{device.model}</p>
              <p className="text-xs text-gray-500">{device.manufacturer}</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <Cpu className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Android</p>
              <p className="font-medium text-gray-900">{device.androidVersion}</p>
              <p className="text-xs text-gray-500">API {device.apiLevel}</p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Clock className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Последняя активность</p>
              <p className="font-medium text-gray-900">
                {formatDistanceToNow(new Date(device.lastSeen), { addSuffix: true, locale: ru })}
              </p>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center space-x-3">
            <div className={`p-2 rounded-lg ${
              device.status === 'ACTIVE' ? 'bg-green-100' : 'bg-yellow-100'
            }`}>
              <div className={`h-5 w-5 rounded-full ${
                device.status === 'ACTIVE' ? 'bg-green-500' : 'bg-yellow-500'
              }`} />
            </div>
            <div>
              <p className="text-sm text-gray-600">Статус</p>
              <p className="font-medium text-gray-900">
                {device.status === 'ACTIVE' ? 'Активно' : 
                 device.status === 'PENDING' ? 'Ожидает' :
                 device.status === 'SUSPENDED' ? 'Приостановлено' : 'Списано'}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Быстрые действия</h2>
        <div className="flex flex-wrap gap-3">
          <button 
            onClick={() => handleCommand('LOCK')}
            className="flex items-center px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            <Lock className="h-4 w-4 mr-2" />
            Заблокировать
          </button>
          <button 
            onClick={() => handleCommand('REBOOT')}
            className="flex items-center px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            <Power className="h-4 w-4 mr-2" />
            Перезагрузить
          </button>
          <button 
            onClick={() => handleCommand('INSTALL_APP')}
            className="flex items-center px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            <Download className="h-4 w-4 mr-2" />
            Установить приложение
          </button>
        </div>
      </div>

      {/* Commands History */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">История команд</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Тип</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Статус</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Создана</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {commands?.map((cmd) => (
                <tr key={cmd.id}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{cmd.type}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs rounded-full ${
                      cmd.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                      cmd.status === 'FAILED' ? 'bg-red-100 text-red-700' :
                      'bg-yellow-100 text-yellow-700'
                    }`}>
                      {cmd.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatDistanceToNow(new Date(cmd.createdAt), { addSuffix: true, locale: ru })}
                  </td>
                </tr>
              ))}
              {(!commands || commands.length === 0) && (
                <tr>
                  <td colSpan={3} className="px-6 py-4 text-center text-gray-500">
                    Нет команд для этого устройства
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
