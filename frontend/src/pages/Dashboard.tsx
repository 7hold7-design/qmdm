import { useDashboardStats } from '../api/stats'
import { 
  Smartphone, 
  Activity, 
  AlertCircle, 
  CheckCircle,
  Clock,
  TrendingUp
} from 'lucide-react'
import { 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts'
import LoadingSpinner from '../components/ui/LoadingSpinner'
import ErrorMessage from '../components/ui/ErrorMessage'

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#6b7280']

export default function Dashboard() {
  const { data: stats, isLoading, error, refetch } = useDashboardStats()

  // Пример данных для графиков
  const activityData = [
    { time: '00:00', devices: 4 },
    { time: '04:00', devices: 3 },
    { time: '08:00', devices: 7 },
    { time: '12:00', devices: 8 },
    { time: '16:00', devices: 9 },
    { time: '20:00', devices: 6 },
  ]

  const pieData = stats ? [
    { name: 'Активные', value: stats.activeDevices },
    { name: 'В ожидании', value: stats.pendingDevices },
    { name: 'Приостановлены', value: stats.suspendedDevices },
    { name: 'Списаны', value: stats.retiredDevices },
  ] : []

  if (isLoading) {
    return <LoadingSpinner size="lg" />
  }

  if (error) {
    return (
      <ErrorMessage 
        message="Ошибка загрузки данных дашборда" 
        onRetry={refetch}
      />
    )
  }

  return (
    <div className="space-y-6">
      {/* Stats cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Всего устройств</p>
              <p className="text-2xl font-bold text-gray-900">{stats?.totalDevices || 0}</p>
            </div>
            <div className="p-3 bg-blue-100 rounded-full">
              <Smartphone className="h-6 w-6 text-blue-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <TrendingUp className="h-4 w-4 text-green-500 mr-1" />
            <span className="text-green-500">+12%</span>
            <span className="text-gray-500 ml-2">за последний месяц</span>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Активные</p>
              <p className="text-2xl font-bold text-gray-900">{stats?.activeDevices || 0}</p>
            </div>
            <div className="p-3 bg-green-100 rounded-full">
              <Activity className="h-6 w-6 text-green-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <CheckCircle className="h-4 w-4 text-green-500 mr-1" />
            <span className="text-green-500">
              {stats?.totalDevices ? Math.round((stats.activeDevices / stats.totalDevices) * 100) : 0}%
            </span>
            <span className="text-gray-500 ml-2">от общего числа</span>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">В ожидании</p>
              <p className="text-2xl font-bold text-gray-900">{stats?.pendingDevices || 0}</p>
            </div>
            <div className="p-3 bg-yellow-100 rounded-full">
              <Clock className="h-6 w-6 text-yellow-600" />
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Проблемные</p>
              <p className="text-2xl font-bold text-gray-900">{stats?.failedCommands || 0}</p>
            </div>
            <div className="p-3 bg-red-100 rounded-full">
              <AlertCircle className="h-6 w-6 text-red-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Активность устройств</h3>
          <div className="h-80" style={{ minHeight: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%" minWidth={300} minHeight={300}>
              <AreaChart data={activityData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="time" />
                <YAxis />
                <Tooltip />
                <Area 
                  type="monotone" 
                  dataKey="devices" 
                  stroke="#3b82f6" 
                  fill="#93c5fd" 
                  fillOpacity={0.3}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Распределение по статусам</h3>
          <div className="h-80" style={{ minHeight: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%" minWidth={300} minHeight={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => 
                    percent ? `${name} ${(percent * 100).toFixed(0)}%` : name
                  }
                  outerRadius={80}
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
      
      {/* Если нет данных, показываем подсказку */}
      {(!stats || stats.totalDevices === 0) && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center">
          <h3 className="text-blue-800 font-medium mb-2">Нет данных для отображения</h3>
          <p className="text-blue-600">
            Добавьте устройства через API или Android клиент, чтобы увидеть статистику.
          </p>
        </div>
      )}
    </div>
  )
}
